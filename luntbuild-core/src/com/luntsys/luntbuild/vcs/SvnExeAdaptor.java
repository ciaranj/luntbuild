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

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.SvnExeAdaptorFacade;
import com.luntsys.luntbuild.facades.lb12.SvnExeModuleFacade;
import com.luntsys.luntbuild.utility.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.commons.lang.StringUtils;
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
public class SvnExeAdaptor extends Vcs {
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
	private String svnDir;
	private boolean cygwinSvn;

	public String getDisplayName() {
		return "SubversionExe";
	}

	public String getIconName() {
		return "svn.jpg";
	}

	public String getSvnDir() {
		return this.svnDir;
	}

	public void setSvnDir(String svnDir) {
		this.svnDir = svnDir;
	}

	/**
	 * Build the executable part of a commandline object
	 *
	 * @return
	 */
	protected Commandline buildSvnExecutable() {
		Commandline cmdLine = new Commandline();
		if (Luntbuild.isEmpty(getSvnDir()))
			cmdLine.setExecutable("svn");
		else
			cmdLine.setExecutable(Luntbuild.concatPath(getSvnDir(), "svn"));
		return cmdLine;
	}

	public List getVcsSpecificProperties() {
		List properties = getSvnExeProperties();
		return properties;
	}

	public void checkoutActually(Build build, Project antProject) {
		String workingDir = build.getSchedule().getWorkDirRaw();
		// retrieve modules
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			SvnExeModule module = (SvnExeModule) Luntbuild.cloneModule(this, (Vcs.Module) it.next());
			if (build.isRebuild() && Luntbuild.isEmpty(module.getLabel()))
				module.setLabel(Luntbuild.getLabelByVersion(build.getVersion()));
			if (build.isRebuild() || build.isCleanBuild())
				retrieveModule(workingDir, module, antProject);
			else
				updateModule(workingDir, module, antProject);
		}
	}

	public void label(Build build, Project antProject) {
		String workingDir = build.getSchedule().getWorkDirRaw();
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
            SvnExeModule module = (SvnExeModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel()))
				labelModule(workingDir, module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
		}
	}

	public Vcs.Module createNewModule() {
		return new SvnExeModule();
	}

    public Vcs.Module createNewModule(Vcs.Module module) {
        return new SvnExeModule((SvnExeModule)module);
    }

	private void retrieveModule(String workingDir, SvnExeModule module, Project antProject) {
		String destDir;
		if (Luntbuild.isEmpty(module.getDestPath()))
			destDir = Luntbuild.concatPath(workingDir, module.getSrcPath());
		else
			destDir = Luntbuild.concatPath(workingDir, module.getDestPath());
		String url = Luntbuild.concatPath(getUrlBase(), mapPathByBranchLabel(module.getSrcPath(),
				module.getBranch(), module.getLabel()));

		antProject.log("Retrieve url: " + url);

		Commandline cmdLine = buildSvnExecutable();
		try {
			// first try switch command
			cmdLine.clearArgs();
			cmdLine.createArgument().setValue("switch");
			cmdLine.createArgument().setValue(url);
			cmdLine.createArgument().setValue(destDir);
			addSvnSwitches(cmdLine, antProject, false);
			new MyExecTask("switch", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
					Project.MSG_INFO).execute();
		} catch (BuildException e) {
			// then try checkout command
			cmdLine.clearArgs();
			cmdLine.createArgument().setValue("checkout");
			cmdLine.createArgument().setValue(url);
			cmdLine.createArgument().setValue(destDir);
			addSvnSwitches(cmdLine, antProject, false);
			new MyExecTask("checkout", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
					Project.MSG_INFO).execute();
		}
	}

	private void labelModule(String workingDir, SvnExeModule module, String label, Project antProject) {
		String normalizedModule = Luntbuild.concatPath("/", module.getSrcPath());
		String normalizedTagsDir = Luntbuild.concatPath("/", getTagsDir());

		// no need to label this module cause this module is fetched from tags directory
		if (normalizedModule.startsWith(normalizedTagsDir))
			return;

		antProject.log("Label url: " + Luntbuild.concatPath(getUrlBase(), mapPathByBranch(module.getSrcPath(), module.getBranch())),
				Project.MSG_INFO);

		String mapped = mapPathByLabel(module.getSrcPath(), label);
		Commandline cmdLine = buildSvnExecutable();

		// Is this mapped path already exists?
		cmdLine.createArgument().setValue("list");
		String url = Luntbuild.concatPath(getUrlBase(), mapped);
		cmdLine.createArgument().setValue(url);
		addSvnSwitches(cmdLine, antProject, true);
		try {
			new MyExecTask("list", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT, -1).execute();
			throw new BuildException("Failed to create label, url \"" + url + "\" already exists.");
		} catch (BuildException e) {
			// ignore the exception
		}

		// make sure parent of mapped path exists
		String mappedParent = StringUtils.substringBeforeLast(StringUtils.stripEnd(mapped, "/"), "/");
		String[] fields = mappedParent.split("/");
		url = getUrlBase();
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			if (!field.trim().equals("")) {
				url = Luntbuild.concatPath(url, field);
				// Is this path already exists?
				cmdLine.clearArgs();
				cmdLine.createArgument().setValue("list");
				cmdLine.createArgument().setValue(url);
				addSvnSwitches(cmdLine, antProject, true);
				try {
					new MyExecTask("list", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
							-1).execute();
				} catch (BuildException e) {
					antProject.log("Seems that url \"" + url + "\" does not exist, creating...", Project.MSG_INFO);
					cmdLine.clearArgs();
					cmdLine.createArgument().setValue("mkdir");
					cmdLine.createArgument().setValue(url);
					cmdLine.createArgument().setLine("-m \"\"");
					addSvnSwitches(cmdLine, antProject, false);
					try {
						new MyExecTask("mkdir", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
								Project.MSG_INFO).execute();
					} catch (BuildException e2) {
						// ignore the exception
					}
				}
			}
		}

		String destDir;
		if (Luntbuild.isEmpty(module.getDestPath()))
			destDir = Luntbuild.concatPath(workingDir, module.getSrcPath());
		else
			destDir = Luntbuild.concatPath(workingDir, module.getDestPath());

		cmdLine.clearArgs();
		cmdLine.createArgument().setValue("copy");
		cmdLine.createArgument().setValue(destDir);
		cmdLine.createArgument().setValue(Luntbuild.concatPath(getUrlBase(), mapped));
		cmdLine.createArgument().setLine("-m \"\"");
		addSvnSwitches(cmdLine, antProject, false);
		new MyExecTask("copy", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
				Project.MSG_INFO).execute();
	}

	private void updateModule(String workingDir, SvnExeModule module, Project antProject) {
		String url = Luntbuild.concatPath(getUrlBase(), mapPathByBranchLabel(module.getSrcPath(),
				module.getBranch(), module.getLabel()));

		antProject.log("Update url: " + url);

		String destDir;
		if (Luntbuild.isEmpty(module.getDestPath()))
			destDir = Luntbuild.concatPath(workingDir, module.getSrcPath());
		else
			destDir = Luntbuild.concatPath(workingDir, module.getDestPath());

		Commandline cmdLine = buildSvnExecutable();
		cmdLine.createArgument().setValue("update");
		cmdLine.createArgument().setValue(destDir);
		addSvnSwitches(cmdLine, antProject, false);
		new MyExecTask("update", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
				Project.MSG_INFO).execute();
	}

	public String getUrlBase() {
		return this.urlBase;
	}

	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTrunk() {
		return this.trunk;
	}

	public void setTrunk(String trunk) {
		this.trunk = trunk;
	}

	/**
	 * add common switches for various svn commands
	 */
	private void addSvnSwitches(Commandline cmdLine, Project antProject, boolean ignoreLogLevel) {
		cmdLine.createArgument().setValue("--non-interactive");
		if (!ignoreLogLevel) {
			LuntbuildLogger luntBuildLogger = Luntbuild.getLuntBuildLogger(antProject);
			if (luntBuildLogger != null && luntBuildLogger.getMessageOutputLevel() <= Project.MSG_INFO)
				cmdLine.createArgument().setValue("--quiet");
		}
		if (getUser() != null && !getUser().trim().equals("")) {
			cmdLine.createArgument().setValue("--username");
			cmdLine.createArgument().setValue(getUser());
			if (getPassword() != null && !getPassword().equals("")) {
				cmdLine.createArgument().setValue("--password");
				Commandline.Argument arg = cmdLine.createArgument();
				arg.setValue(getPassword());
				arg.setDescriptiveValue("******");
			}
		}
	}

	public void validateModules() {
		super.validateModules();
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
            SvnExeModule module = (SvnExeModule) it.next();
			if (module.getSrcPath().indexOf('\\') != -1)
				throw new ValidationException("Source path \"" + module.getSrcPath() + "\" should not contain character '\\'");
		}
	}

	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		String workingDir = workingSchedule.getWorkDirRaw();
		Revisions revisions = new Revisions();
		Commandline cmdLine = buildSvnExecutable();
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
            SvnExeModule module = (SvnExeModule) it.next();
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
						Commandline.Argument arg = cmdLine.createArgument();
						arg.setValue(getPassword());
						arg.setDescriptiveValue("******");
					}
				}
				cmdLine.createArgument().setLine("--non-interactive -v --xml -r");
				cmdLine.createArgument().setValue("{" + INPUT_DATE_FORMAT.format(sinceDate) +
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
						if (revisionDate.before(sinceDate))
							continue;
						Element authorElement = logEntry.element("author");
						if (authorElement != null)
							revisions.getChangeLogins().add(authorElement.getText());
						revisions.getChangeLogs().add("----------------------------------------------------------------------------------------------------------------------");
						if (authorElement != null)
							revisions.getChangeLogs().add("r" + logEntry.attribute("revision").getText() + " | " +
								authorElement.getText() + " | " + revisionDate.toString());
						else
							revisions.getChangeLogs().add("r" + logEntry.attribute("revision").getText() + " | " +
								"anonymous" + " | " + revisionDate.toString());

								//
						// 2005.06.21 - ghenry@lswe.com --> check for commit msg
						//
						Element msgElement = logEntry.element("msg");
						if (msgElement != null && msgElement.getText().trim().length() != 0) {
							revisions.getChangeLogs().add("----------------------------------------------------------------------------------------------------------------------");
							revisions.getChangeLogs().add(msgElement.getText());
							revisions.getChangeLogs().add("----------------------------------------------------------------------------------------------------------------------");
						}
						//

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
		return this.branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	public String getTags() {
		return this.tags;
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

	public class SvnExeModule extends Module {
		/**
		 * Keep tracks of version of this class, used when do serialization-deserialization
		 */
		static final long serialVersionUID = 1L;

		private String srcPath;
		private String branch;
		private String label;
		private String destPath;

        /**
         * Constructor
         */
        public SvnExeModule() {}

        /**
         * Copy Constructor
         */
        public SvnExeModule(SvnExeModule module) {
            this.srcPath = module.srcPath;
            this.branch = module.branch;
            this.label = module.label;
            this.destPath = module.destPath;
        }

		public String getSrcPath() {
			return this.srcPath;
		}

		public void setSrcPath(String srcPath) {
			this.srcPath = srcPath;
		}

		public String getBranch() {
			return this.branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
		}

		public String getLabel() {
			return this.label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getDestPath() {
			return this.destPath;
		}

		public void setDestPath(String destPath) {
			this.destPath = destPath;
		}

		public List getProperties() {
			List properties = getSvnExeModuleProperties();
			return properties;
		}

		public com.luntsys.luntbuild.facades.lb12.ModuleFacade getFacade() {
			SvnExeModuleFacade facade = new com.luntsys.luntbuild.facades.lb12.SvnExeModuleFacade();
			facade.setBranch(getBranch());
			facade.setDestPath(getDestPath());
			facade.setLabel(getLabel());
			facade.setSrcPath(getSrcPath());
			return facade;
		}

		public void setFacade(com.luntsys.luntbuild.facades.lb12.ModuleFacade facade) {
			if (facade instanceof com.luntsys.luntbuild.facades.lb12.SvnExeModuleFacade) {
				SvnExeModuleFacade svnModuleFacade = (com.luntsys.luntbuild.facades.lb12.SvnExeModuleFacade) facade;
				setBranch(svnModuleFacade.getBranch());
				setLabel(svnModuleFacade.getLabel());
				setSrcPath(svnModuleFacade.getSrcPath());
				setDestPath(svnModuleFacade.getDestPath());
			} else
				throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		}
	}

	public void saveToFacade(com.luntsys.luntbuild.facades.lb12.VcsFacade facade) {
		SvnExeAdaptorFacade svnFacade = (SvnExeAdaptorFacade) facade;
		svnFacade.setTrunk(getTrunk());
		svnFacade.setBranches(getBranches());
		svnFacade.setPassword(getPassword());
		svnFacade.setTags(getTags());
		svnFacade.setUrlBase(getUrlBase());
		svnFacade.setUser(getUser());
		svnFacade.setSvnDir(getSvnDir());
	}

	public void loadFromFacade(com.luntsys.luntbuild.facades.lb12.VcsFacade facade) {
		if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.SvnExeAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		com.luntsys.luntbuild.facades.lb12.SvnExeAdaptorFacade svnFacade =
            (com.luntsys.luntbuild.facades.lb12.SvnExeAdaptorFacade) facade;
		setTrunk(svnFacade.getTrunk());
		setBranches(svnFacade.getBranches());
		setPassword(svnFacade.getPassword());
		setTags(svnFacade.getTags());
		setUrlBase(svnFacade.getUrlBase());
		setUser(svnFacade.getUser());
		setSvnDir(svnFacade.getSvnDir());
	}

	public com.luntsys.luntbuild.facades.lb12.VcsFacade constructFacade() {
		return new SvnExeAdaptorFacade();
	}
}
