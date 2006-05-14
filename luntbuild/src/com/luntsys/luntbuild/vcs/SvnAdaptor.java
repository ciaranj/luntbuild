/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-7-10
 * Time: 10:34:47
 *
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package com.luntsys.luntbuild.vcs;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.remoting.facade.ModuleFacade;
import com.luntsys.luntbuild.remoting.facade.SvnAdaptorFacade;
import com.luntsys.luntbuild.remoting.facade.SvnModuleFacade;
import com.luntsys.luntbuild.remoting.facade.VcsFacade;
import com.luntsys.luntbuild.utility.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The subversion adaptor
 *
 * @author robin shine
 */
public class SvnAdaptor extends Vcs {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;

	private static final String SVN_COMMAND_INPUT = null;
	private static final SimpleDateFormat INPUT_DATE_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final SimpleDateFormat OUTPUT_DATE_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	static {
		INPUT_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
		OUTPUT_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private String urlBase;
	private String trunk;
	private String branches;
	private String tags;
	private String user;
	private String password;

	public String getDisplayName() {
		return "subversion";
	}

	public String getIconName() {
		return "svn.jpg";
	}

	public List getProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "repository url base";
			}

			public String getDescription() {
				return "The base part of subversion url, for example, you can input " +
						"\"svn://buildmachine.foobar.com/\", or \"file:///c:/svn_repository\", or " +
						"\"svn://buildmachine.foobar.com/myproject/othersubdirectory\", etc." +
						"Other definitions such as tags directory, branches directory, or modules " +
						"are relative to this base url. NOTE: if you are using https:// schema, you " +
						"should make sure svn server certificate has been accepted permermantly " +
						"by your build machine";
			}

			public String getValue() {
				return getUrlBase();
			}

			public void setValue(String value) {
				setUrlBase(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "directory for trunk";
			}

			public String getDescription() {
				return "Directory used to hold trunk for this url base. " +
						"This directory is relative to the url base. Left it blank if you have not " +
						"defined any trunk directory in the above url base.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getTrunk();
			}

			public void setValue(String value) {
				setTrunk(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "directory for branches";
			}

			public String getDescription() {
				return "Directory used to hold branches for this url base. " +
						"This directory is relative to the url base. If left blank, " +
						"\"branches\" will be used as the default value.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getBranches();
			}

			public void setValue(String value) {
				setBranches(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "directory for tags";
			}

			public String getDescription() {
				return "Directory used to hold tags for this url base. " +
						"This directory is relative to the url base. If left blank, " +
						"\"tags\" will be used as the default value";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getTags();
			}

			public void setValue(String value) {
				setTags(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "user name";
			}

			public String getDescription() {
				return "User name to login to subversion";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getUser();
			}

			public void setValue(String value) {
				setUser(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "password";
			}

			public String getDescription() {
				return "Password to login to subversion";
			}

			public boolean isRequired() {
				return false;
			}

			public boolean isSecret() {
				return true;
			}

			public String getValue() {
				return getPassword();
			}

			public void setValue(String value) {
				setPassword(value);
			}
		});
		return properties;
	}

	public void checkout(Map properties, Build build, Project antProject) {
		String workingDir = build.getSchedule().getProject().getWorkingDir(properties);
		// retrieve modules
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			SvnModule module = (SvnModule) Luntbuild.cloneModule(this, (Vcs.Module) it.next());
			if (build.isRebuild() && Luntbuild.isEmpty(module.getLabel()))
				module.setLabel(Luntbuild.getLabelByVersion(build.getVersion()));
			if (build.isRebuild() || build.isCleanBuild())
				retrieveModule(workingDir, module, antProject);
			else
				updateModule(workingDir, module, antProject);
		}
	}

	public void label(Map properties, Build build, Project antProject) {
		String workingDir = build.getSchedule().getProject().getWorkingDir(properties);
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			SvnModule module = (SvnModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel()))
				labelModule(workingDir, module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
		}
	}

	public Vcs.Module createNewModule() {
		return new SvnModule();
	}

	private void retrieveModule(String workingDir, SvnModule module, Project antProject) {
		String destDir;
		if (Luntbuild.isEmpty(module.getDestPath()))
			destDir = Luntbuild.concatPath(workingDir, module.getSrcPath());
		else
			destDir = Luntbuild.concatPath(workingDir, module.getDestPath());
		String url = Luntbuild.concatPath(getUrlBase(), mapPathByBranchLabel(module.getSrcPath(),
				module.getBranch(), module.getLabel()));

		antProject.log("Retrieve url: " + url);

		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("svn");
		try {
			// first try switch command
			cmdLine.clearArgs();
			cmdLine.createArgument().setValue("switch");
			cmdLine.createArgument().setValue(url);
			cmdLine.createArgument().setValue(destDir);
			addSvnSwitches(cmdLine, antProject);
			new MyExecTask("switch", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
					Project.MSG_INFO).execute();
		} catch (BuildException e) {
			// then try checkout command
			cmdLine.clearArgs();
			cmdLine.createArgument().setValue("checkout");
			cmdLine.createArgument().setValue(url);
			cmdLine.createArgument().setValue(destDir);
			addSvnSwitches(cmdLine, antProject);
			new MyExecTask("checkout", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
					Project.MSG_INFO).execute();
		}
	}

	private void labelModule(String workingDir, SvnModule module, String label, Project antProject) {
		String normalizedModule = Luntbuild.concatPath("/", module.getSrcPath());
		String normalizedTagsDir = Luntbuild.concatPath("/", getTagsDir());

		// no need to label this module cause this module is fetched from tags directory
		if (normalizedModule.startsWith(normalizedTagsDir))
			return;

		String mapped = mapPathByLabel(module.getSrcPath(), label);
		String[] fields = mapped.split("/");
		String url = getUrlBase();

		antProject.log("Label url: " + Luntbuild.concatPath(url, mapPathByBranch(module.getSrcPath(), module.getBranch())),
				Project.MSG_INFO);
		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("svn");
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			if (!field.trim().equals("")) {
				url = Luntbuild.concatPath(url, field);
				cmdLine.clearArgs();
				cmdLine.createArgument().setValue("mkdir");
				cmdLine.createArgument().setValue(url);
				cmdLine.createArgument().setLine("-m \"\"");
				addSvnSwitches(cmdLine, antProject);
				try {
					new MyExecTask("mkdir", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
							Project.MSG_INFO).execute();
				} catch (BuildException e) {
					// ignore the exception
				}
			}
		}
		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("delete");
		cmdLine.createArgument().setValue(url);
		cmdLine.createArgument().setLine("-m \"\"");
		addSvnSwitches(cmdLine, antProject);
		new MyExecTask("delete", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
				Project.MSG_INFO).execute();

		String destDir;
		if (Luntbuild.isEmpty(module.getDestPath()))
			destDir = Luntbuild.concatPath(workingDir, module.getSrcPath());
		else
			destDir = Luntbuild.concatPath(workingDir, module.getDestPath());

		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("copy");
		cmdLine.createArgument().setValue(destDir);
		cmdLine.createArgument().setValue(url);
		cmdLine.createArgument().setLine("-m \"\"");
		addSvnSwitches(cmdLine, antProject);
		new MyExecTask("copy", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
				Project.MSG_INFO).execute();
	}

	private void updateModule(String workingDir, SvnModule module, Project antProject) {
		String url = Luntbuild.concatPath(getUrlBase(), mapPathByBranchLabel(module.getSrcPath(),
				module.getBranch(), module.getLabel()));

		antProject.log("Update url: " + url);

		String destDir;
		if (Luntbuild.isEmpty(module.getDestPath()))
			destDir = Luntbuild.concatPath(workingDir, module.getSrcPath());
		else
			destDir = Luntbuild.concatPath(workingDir, module.getDestPath());

		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("svn");
		cmdLine.createArgument().setValue("update");
		cmdLine.createArgument().setValue(destDir);
		addSvnSwitches(cmdLine, antProject);
		new MyExecTask("update", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
				Project.MSG_INFO).execute();
	}

	public String getUrlBase() {
		return urlBase;
	}

	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTrunk() {
		return trunk;
	}

	public void setTrunk(String trunk) {
		this.trunk = trunk;
	}

	/**
	 * add common switches for various svn commands
	 */
	private void addSvnSwitches(Commandline cmdLine, Project antProject) {
		cmdLine.createArgument().setValue("--non-interactive");
		LuntbuildLogger luntBuildLogger = Luntbuild.getLuntBuildLogger(antProject);
		if (luntBuildLogger != null && luntBuildLogger.getMessageOutputLevel() <= Project.MSG_INFO)
			cmdLine.createArgument().setValue("--quiet");
		if (getUser() != null && !getUser().trim().equals("")) {
			cmdLine.createArgument().setValue("--username");
			cmdLine.createArgument().setValue(getUser());
			if (getPassword() != null && !getPassword().equals("")) {
				cmdLine.createArgument().setValue("--password");
				cmdLine.createArgument().setValue(getPassword());
			}
		}
	}

	public void validateModules() {
		super.validateModules();
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			SvnModule module = (SvnModule) it.next();
			if (module.getSrcPath().indexOf('\\') != -1)
				throw new ValidationException("Source path \"" + module.getSrcPath() + "\" should not contain character '\\'");
		}
	}

	public com.luntsys.luntbuild.utility.Revisions getRevisionsSince(Map properties, Build build, Project antProject) {
		String workingDir = build.getSchedule().getProject().getWorkingDir(properties);
		Revisions revisions = new Revisions();
		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("svn");
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			SvnModule module = (SvnModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel())) {
				cmdLine.clearArgs();
				cmdLine.createArgument().setValue("log");
				String url = Luntbuild.concatPath(getUrlBase(), mapPathByBranchLabel(module.getSrcPath(),
						module.getBranch(), module.getLabel()));
				cmdLine.createArgument().setValue(url);
				if (getUser() != null && !getUser().trim().equals("")) {
					cmdLine.createArgument().setValue("--username");
					cmdLine.createArgument().setValue(getUser());
					if (getPassword() != null && !getPassword().equals("")) {
						cmdLine.createArgument().setValue("--password");
						cmdLine.createArgument().setValue(getPassword());
					}
				}
				cmdLine.createArgument().setLine("--non-interactive -v --xml -r");
				cmdLine.createArgument().setValue("{" + INPUT_DATE_FORMAT.format(build.getStartDate()) +
						"}:{" + INPUT_DATE_FORMAT.format(new Date()) + "}");
				final StringBuffer buffer = new StringBuffer();
				new MyExecTask("log", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT, -1) {
					public void handleStdout(String line) {
						buffer.append(line);
						buffer.append("\n");
					}
				}.execute();
				SAXReader reader = new SAXReader();
				try {
					Document doc = reader.read(new StringReader(buffer.toString()));
					Iterator itElement = doc.getRootElement().elementIterator("logentry");
					while (itElement.hasNext()) {
						Element logEntry = (Element) itElement.next();
						String dateString = logEntry.element("date").getText();
						Date revisionDate = OUTPUT_DATE_FORMAT.parse(dateString.substring(0, dateString.indexOf('Z') - 3));
						if (revisionDate.before(build.getStartDate()))
							continue;
						revisions.getChangeLogins().add(logEntry.element("author").getText());
						revisions.getChangeLogs().add("----------------------------------------------------------------------------------------------------------------------");
						revisions.getChangeLogs().add("r" + logEntry.attribute("revision").getText() + " | " +
								logEntry.element("author").getText() + " | " +
								revisionDate.toString());
						revisions.getChangeLogs().add("Changed paths:");
						Iterator itPath = logEntry.element("paths").elementIterator("path");
						while (itPath.hasNext()) {
							Element path = (Element) itPath.next();
							revisions.getChangeLogs().add("    " + path.attribute("action").getText() + " " + path.getText());
							revisions.setFileModified(true);
						}
						revisions.getChangeLogs().add("");
					}
				} catch (DocumentException e) {
					throw new RuntimeException(e);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return revisions;
	}

	/**
	 * Map a subversion path to sub directory of tags or branches based on the branch or label name
	 * Label will take preference over branch
	 *
	 * @param path
	 * @param branch
	 * @param label
	 * @return
	 */
	private String mapPathByBranchLabel(String path, String branch, String label) {
		if (!Luntbuild.isEmpty(label))
			return mapPathByLabel(path, label);
		else
			return mapPathByBranch(path, branch);
	}

	/**
	 * Map a subversion path to sub directory of tags based on the label name
	 *
	 * @param path
	 * @param label should not be empty
	 * @return
	 */
	private String mapPathByLabel(String path, String label) {
		String mapped = Luntbuild.concatPath(getTagsDir(), label);
		return Luntbuild.concatPath(mapped, path);
	}

	/**
	 * Map a subversion path to sub directory of branches based on the branch name,
	 * or to sub directory under trunk if branch name is empty
	 * @param path
	 * @param branch maybe empty
	 * @return
	 */
	private String mapPathByBranch(String path, String branch) {
		String mapped;
		if (!Luntbuild.isEmpty(branch))
			mapped = Luntbuild.concatPath(getBranchesDir(), branch);
		else
			mapped = getTrunkDir();
		return Luntbuild.concatPath(mapped, path);
	}

	public String getBranches() {
		return branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTrunkDir() {
		if (Luntbuild.isEmpty(getTrunk()))
			return "";
		else
			return getTrunk();
	}

	public String getBranchesDir() {
		if (Luntbuild.isEmpty(getBranches()))
			return "branches";
		else
			return getBranches();
	}

	public String getTagsDir() {
		if (Luntbuild.isEmpty(getTags()))
			return "tags";
		else
			return getTags();
	}

	public class SvnModule extends Module {
		/**
		 * Keep tracks of version of this class, used when do serialization-deserialization
		 */
		static final long serialVersionUID = -6422762249045288996L;

		private String srcPath;
		private String branch;
		private String label;
		private String destPath;

		public String getSrcPath() {
			return srcPath;
		}

		public void setSrcPath(String srcPath) {
			this.srcPath = srcPath;
		}

		public String getBranch() {
			return branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getDestPath() {
			return destPath;
		}

		public void setDestPath(String destPath) {
			this.destPath = destPath;
		}

		public List getProperties() {
			List properties = new ArrayList();
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "source path";
				}

				public String getDescription() {
					return "Represents a path into the subversion repository, for example \"testsvn\", " +
							"\"testsvn/web\", or \"/testsvn\". When \"branch\" or \"label\" properties are " +
							"defined, this path will be mapped to another path into the svn repository. " +
							"Please refer to the manual for detail information";
				}

				public String getValue() {
					return getSrcPath();
				}

				public void setValue(String value) {
					setSrcPath(value);
				}
			});
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "branch";
				}

				public String getDescription() {
					return "Specify the branch for above source path. This property is optional. When " +
							"left empty, trunk will be assumed.\n" +
							"PS: Subversion does not internally has the notion of branch. Value specified " +
							"here will be used by luntbuild to do url mapping for the above source path so that " +
							"actual effect is just like branch in cvs. Refer to the manual for detailed information " +
							"about the url mapping for branch.";
				}

				public boolean isRequired() {
					return false;
				}

				public String getValue() {
					return getBranch();
				}

				public void setValue(String value) {
					setBranch(value);
				}
			});
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "label";
				}

				public String getDescription() {
					return "Specify the label for the above source path. This property is optional. If specified, " +
							"it will take preference over branch. When left empty, head version of specified branch " +
							"will be assumed. " +
							"PS: Subversion does not internally has the notion of label. Value specified here " +
							"will be used by luntbuild to do url mapping for the above source path so that actual " +
							"effect is just like tag in cvs. Refer to the manual for detailed information about the " +
							"url mapping for label.";
				}

				public boolean isRequired() {
					return false;
				}

				public String getValue() {
					return getLabel();
				}

				public void setValue(String value) {
					setLabel(value);
				}
			});
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "destination path";
				}

				public String getDescription() {
					return "This property is optional. If specified, contents from subversion repository " +
							"will be retrieved to \"destination path\" relative to project working directory, " +
							"otherwise, contents will be put to \"source path\"(with no regard to " +
							"\"branch\" or \"label\" here) relative to project working directory.";
				}

				public boolean isRequired() {
					return false;
				}

				public String getValue() {
					return getDestPath();
				}

				public void setValue(String value) {
					setDestPath(value);
				}
			});
			return properties;
		}

		public ModuleFacade getFacade() {
			SvnModuleFacade facade = new SvnModuleFacade();
			facade.setBranch(getBranch());
			facade.setDestPath(getDestPath());
			facade.setLabel(getLabel());
			facade.setSrcPath(getSrcPath());
			return facade;
		}

		public void setFacade(ModuleFacade facade) {
			if (facade instanceof SvnModuleFacade) {
				SvnModuleFacade svnModuleFacade = (SvnModuleFacade) facade;
				setBranch(svnModuleFacade.getBranch());
				setLabel(svnModuleFacade.getLabel());
				setSrcPath(svnModuleFacade.getSrcPath());
				setDestPath(svnModuleFacade.getDestPath());
			} else
				throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		}
	}

	public VcsFacade getFacade() {
		SvnAdaptorFacade facade = new SvnAdaptorFacade();
		facade.setTrunk(getTrunk());
		facade.setBranches(getBranches());
		facade.setPassword(getPassword());
		facade.setTags(getTags());
		facade.setUrlBase(getUrlBase());
		facade.setUser(getUser());

		facade.getModules().clear();
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			SvnModule module = (SvnModule) it.next();
			facade.getModules().add(module.getFacade());
		}
		return facade;
	}

	public void setFacade(VcsFacade facade) {
		if (!(facade instanceof SvnAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		SvnAdaptorFacade svnFacade = (SvnAdaptorFacade) facade;
		setTrunk(svnFacade.getTrunk());
		setBranches(svnFacade.getBranches());
		setPassword(svnFacade.getPassword());
		setTags(svnFacade.getTags());
		setUrlBase(svnFacade.getUrlBase());
		setUser(svnFacade.getUser());

		getModules().clear();
		Iterator it = svnFacade.getModules().iterator();
		while (it.hasNext()) {
			SvnModuleFacade moduleFacade = (SvnModuleFacade) it.next();
			SvnModule module = new SvnModule();
			module.setFacade(moduleFacade);
			getModules().add(module);
		}
	}
}
