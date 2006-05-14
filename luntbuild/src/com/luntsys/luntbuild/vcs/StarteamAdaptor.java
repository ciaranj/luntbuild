/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-9
 * Time: 10:44:16
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
import com.luntsys.luntbuild.utility.*;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.remoting.*;
import com.starbase.starteam.*;
import com.starbase.util.OLEDate;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.starteam.StarTeamCheckout;
import org.apache.tools.ant.taskdefs.optional.starteam.StarTeamLabel;

import java.util.*;

/**
 * Borland starteam adaptor
 *
 * @author robin shine
 */
public class StarteamAdaptor extends VcsAdaptor {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;

	private static ThreadLocal starteamProject = new ThreadLocal();

	private String projectLocation;
	private String user;
	private String password;
	private String convertEOL;

	public String getVcsDisplayName() {
		return "starteam";
	}

	public void checkout(Map properties, Build build, Project antProject) {
		String workingDir = build.getBuildSchedule().getView().getWorkingDir(properties);
		// retrieve modules
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			StarteamModule module = (StarteamModule) Luntbuild.cloneModule(this, (Vcs.Module) it.next());
			if (build.isRebuild() && Luntbuild.isEmpty(module.getLabel()))
				module.setLabel(Luntbuild.getLabelByVersion(build.getVersion()));
			if (build.isRebuild() || build.isCleanBuild())
				retrieveModule(workingDir, module, true, antProject);
			else
				retrieveModule(workingDir, module, false, antProject);
		}
	}

	public void label(Map properties, Build build, Project antProject) {
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			StarteamModule module = (StarteamModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel()))
				labelModule(module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
		}
	}

	public List getProjectLevelProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "project location";
			}

			public String getDescription() {
				return "Location of a starteam project is defined as: " +
						"<servername>:<portnum>/<projectname>, where <servername>" +
						"stands for the host where starteam server runs on, <portnum> stands " +
						"for the port number the starteam server is listening on, normally this value " +
						"will be 49201. <projectname> stands for a starteam project under this starteam server.";
			}

			public String getValue() {
				return getProjectLocation();
			}

			public void setValue(String value) {
				setProjectLocation(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "user";
			}

			public String getDescription() {
				return "User name to login to the starteam server";
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
				return "Password to login to the starteam server";
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
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "convert EOL?";
			}

			public String getDescription() {
				return "The following values are possible<br>" +
						"yes: all ascii files will have their end-of-line characters adjusted to that " +
						"of the local machine on checkout. <br>" +
						"no:  the files will be checked out with whatever end-of-line " +
						"characters are used on the server. <br><br>" +
						"This property is optional. If not specified, it will be default to \"yes\"";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getConvertEOL();
			}

			public void setValue(String value) {
				setConvertEOL(value);
			}
		});
		return properties;
	}

	public List getViewLevelProperties() {
		return new ArrayList();
	}

	/**
	 * Method may throw a BuildException to indicates a module acquisition exception
	 *
	 * @param workingDir
	 * @param module
	 * @param antProject
	 */
	private void retrieveModule(String workingDir, StarteamModule module, boolean isClean, Project antProject) {
		if (isClean)
			antProject.log("Retrieve source path \"" + module.getSrcPath() + "\" of starteam view \"" + module.getStarteamView() + "\"");
		else
			antProject.log("Update source path \"" + module.getSrcPath() + "\" of starteam view \"" + module.getStarteamView() + "\"");
		String destDir;
		if (Luntbuild.isEmpty(module.getDestPath()))
			destDir = Luntbuild.concatPath(workingDir, module.getSrcPath());
		else
			destDir = Luntbuild.concatPath(workingDir, module.getDestPath());

		// for a clean retrieve, we first delete the module directory, we need to do this
		// because we do not want retrieve of this module be disturbed by pre-retrieved
		// codes of other modules.
		if (isClean)
			Luntbuild.deleteDir(destDir);
		Luntbuild.createDir(destDir);

		// call ant starteam task to acquire module
		StarTeamCheckout starteamTask = new StarTeamCheckout();
		starteamTask.setProject(antProject);
		starteamTask.setUserName(getUser());
		if (Luntbuild.isEmpty(getPassword()))
			starteamTask.setPassword("");
		else
			starteamTask.setPassword(getPassword());

		if (Luntbuild.isEmpty(module.getStarteamView()))
			starteamTask.setURL(getProjectLocation() + "/" + getStarteamProject());
		else
			starteamTask.setURL(getProjectLocation() + "/" + module.getStarteamView());

		if (!Luntbuild.isEmpty(module.getLabel()))
			starteamTask.setLabel(module.getLabel());
		starteamTask.setRootStarteamFolder(Luntbuild.concatPath("/", module.getSrcPath()));
		starteamTask.setRootLocalFolder(destDir);
		if (Luntbuild.isEmpty(getConvertEOL()) || getConvertEOL().equalsIgnoreCase("yes"))
			starteamTask.setConvertEOL(true);
		else
			starteamTask.setConvertEOL(false);
		starteamTask.setDeleteUncontrolled(false);
		starteamTask.setTaskType("StarTeamCheckout");
		starteamTask.setTaskName("StarTeamCheckout");
		starteamTask.execute();
	}

	/**
	 * Method may throw a BuildException to indicates errors while labeling
	 *
	 * @param module
	 * @param label
	 * @param antProject
	 */
	private void labelModule(StarteamModule module, String label, Project antProject) {
		antProject.log("Label source path \"" + module.getSrcPath() + "\" of starteam view \"" + module.getStarteamView() + "\"");

		// call ant starteam task to label module
		StarTeamLabel starteamTask = new StarTeamLabel();
		starteamTask.setProject(antProject);
		starteamTask.setUserName(getUser());
		if (Luntbuild.isEmpty(getPassword()))
			starteamTask.setPassword("");
		else
			starteamTask.setPassword(getPassword());

		if (Luntbuild.isEmpty(module.getStarteamView()))
			starteamTask.setURL(getProjectLocation() + "/" + getStarteamProject());
		else
			starteamTask.setURL(getProjectLocation() + "/" + module.getStarteamView());

		starteamTask.setLabel(label);
		starteamTask.setDescription("luntbuild labels");
		starteamTask.setTaskType("StarTeamLabel");
		starteamTask.setTaskName("StarTeamLabel");
		starteamTask.execute();
	}

	public Vcs.Module createNewModule() {
		return new StarteamModule();
	}

	public Revisions getRevisionsSince(Map properties, Build build, Project antProject) {
		Revisions revisions = new com.luntsys.luntbuild.utility.Revisions();

		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			StarteamModule module = (StarteamModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel())) {
				Server server = null;
				try {
					String viewName;
					if (Luntbuild.isEmpty(module.getStarteamView()))
						viewName = getStarteamProject();
					else
						viewName = module.getStarteamView();
					String viewUrl = getProjectLocation() + "/" + viewName;

					// get the specified floating view
					View view;
					if (!Luntbuild.isEmpty(getPassword()))
						view = StarTeamFinder.openView(getUser() + ":" + getPassword() + "@" + viewUrl);
					else
						view = StarTeamFinder.openView(getUser() + "@" + viewUrl);

					if (view == null)
						throw new BuildException("Starteam view \"" + viewName + "\" not found!");

					server = view.getServer();

					OLEDate currentTime = new OLEDate(System.currentTimeMillis());
					OLEDate buildTime = new OLEDate(build.getStartDate());

					// get the time based configuration at now and at last build's start time
					View currentConfiguration = new View(view, ViewConfiguration.createFromTime(currentTime));
					View buildConfiguration = new View(view, ViewConfiguration.createFromTime(buildTime));

					// use map to hold all files for current configuration and last build's configuration respectively
					Map currentFiles = new HashMap();
					Map buildFiles = new HashMap();

					Folder currentRoot = StarTeamFinder.findFolder(currentConfiguration.getRootFolder(),
							Luntbuild.concatPath("/", module.getSrcPath()));
					if (currentRoot == null)
						throw new BuildException("Source path \"" + module.getSrcPath() + "\" " +
								"does not exist in current configuration of starteam view \"" + viewName + "\"");
					Folder buildRoot = StarTeamFinder.findFolder(buildConfiguration.getRootFolder(),
							Luntbuild.concatPath("/", module.getSrcPath()));
					if (buildRoot == null)
						throw new BuildException("Source path \"" + module.getSrcPath() + "\" " +
								"does not exist in last build's configuration of starteam view \"" + viewName + "\"");

					// fill file maps with files recursively
					getFolderFiles(currentRoot, currentFiles);
					getFolderFiles(buildRoot, buildFiles);

					// iterate through fetched files of both configurations to identify
					// change logs
					Iterator itFile = currentFiles.keySet().iterator();
					while (itFile.hasNext()) {
						Integer itemId = (Integer) itFile.next();
						File currentFile = (File) currentFiles.get(itemId);
						if (buildFiles.containsKey(itemId)) {
							File buildFile = (File) buildFiles.get(itemId);
							if (currentFile.getContentVersion() != buildFile.getContentVersion())
								addChangeLogs(currentFile, "File modified", revisions);
							else if (!currentFile.getParentFolderHierarchy().equals(buildFile.getParentFolderHierarchy()))
								addChangeLogs(currentFile, "File moved", revisions);
							buildFiles.remove(itemId);
						} else {
							addChangeLogs(currentFile, "File added", revisions);
						}
					}
					// find files already been deleted
					itFile = buildFiles.values().iterator();
					while (itFile.hasNext()) {
						File file = (File) itFile.next();
						addChangeLogs((File) file.getFromHistoryByDate(currentTime), "File deleted", revisions);
					}

					// release resources
					currentConfiguration.getRootFolder().discardItems(server.getTypeNames().FILE, -1);
					buildConfiguration.getRootFolder().discardItems(server.getTypeNames().FILE, -1);
				} finally {
					if (server != null)
						server.disconnect();
				}
			}
		}
		if (revisions.getChangeLogs().size() != 0)
			revisions.setFileModified(true);
		return revisions;
	}

	private void getFolderFiles(Folder folder, Map files) {
		Item[] items = folder.getItems("File");
		for (int i = 0; i < items.length; i++) {
			File file = (File) items[i];
			files.put(new Integer(file.getItemID()), file);
		}

		Folder[] subFolders = folder.getSubFolders();
		for (int i = 0; i < subFolders.length; i++) {
			getFolderFiles(subFolders[i], files);
		}
	}

	private void addChangeLogs(File file, String action, Revisions revisions) {
		User user = file.getServer().getUser(file.getModifiedBy());
		String userName = "<unknown user>";
		if (user != null) {
			revisions.getChangeLogins().add(user.getName());
			userName = user.getName();
		}
		revisions.getChangeLogs().add(action + ": " + userName + " | " + file.getModifiedTime().createDate().toString() +
				" | " + file.getParentFolderHierarchy() + file.getName());
		revisions.getChangeLogs().add("Comment: " + file.getComment());
		revisions.getChangeLogs().add("");
	}

	public String getProjectLocation() {
		return projectLocation;
	}

	public void setProjectLocation(String projectLocation) {
		this.projectLocation = projectLocation;
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

	public String getConvertEOL() {
		return convertEOL;
	}

	public void setConvertEOL(String convertEOL) {
		this.convertEOL = convertEOL;
	}

	public String getStarteamProject() {
		return (String) starteamProject.get();
	}

	public void setStarteamProject(String starteamProject) {
		StarteamAdaptor.starteamProject.set(starteamProject);
	}

	public void validateProjectLevelProperties() {
		super.validateProjectLevelProperties();
		String[] fields = getProjectLocation().split("/");
		if (fields.length != 2 || fields[1].trim().equals(""))
			throw new ValidationException("Invalid value for project location: " + getProjectLocation());
		setStarteamProject(fields[1].trim());
		if (!Luntbuild.isEmpty(getConvertEOL())) {
			if (!getConvertEOL().trim().equalsIgnoreCase("yes") &&
					!getConvertEOL().trim().equalsIgnoreCase("no"))
				throw new ValidationException("Invalid value for convert EOL: should be " +
						"yes or no");
			setConvertEOL(getConvertEOL().trim());
		}
	}

	public class StarteamModule extends CommonModule {
		/**
		 * Keep tracks of version of this class, used when do serialization-deserialization
		 */
		static final long serialVersionUID = 1;

		private String starteamView;

		public List getProperties() {
			List properties = new ArrayList();
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "starteam view";
				}

				public String getDescription() {
					return "Specify a starteam view. This property is optional. If it is left empty, the " +
							"root view of current starteam project will be used";
				}

				public boolean isRequired() {
					return false;
				}

				public String getValue() {
					return getStarteamView();
				}

				public void setValue(String value) {
					setStarteamView(value);
				}
			});
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "source path";
				}

				public String getDescription() {
					return "Specify a path relative to the root of above starteam view. Input \"/\" to " +
							"specify the root";
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
					return "label";
				}

				public String getDescription() {
					return "Specify the label for above starteam view. This property " +
							"is optional. When left empty, latest version of specified view will" +
							"be assumed";
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
					return "Specify the destination directory relative to the view working directory where " +
							"contents under the above source path should be retrieved to. This property " +
							"is optional. When left empty, retrieved code will be put into directory denoted by" +
							"source path relative to the view working directory";
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

		public String getStarteamView() {
			return starteamView;
		}

		public void setStarteamView(String starteamView) {
			this.starteamView = starteamView;
		}

		public ModuleFacade getFacade() {
			StarteamModuleFacade facade = new StarteamModuleFacade();
			facade.setStarteamView(getStarteamView());
			facade.setDestPath(getDestPath());
			facade.setLabel(getLabel());
			facade.setSrcPath(getSrcPath());
			return facade;
		}

		public void setFacade(ModuleFacade facade) {
			if (facade instanceof StarteamModuleFacade) {
				StarteamModuleFacade starteamModuleFacade = (StarteamModuleFacade) facade;
				setStarteamView(starteamModuleFacade.getStarteamView());
				setLabel(starteamModuleFacade.getLabel());
				setSrcPath(starteamModuleFacade.getSrcPath());
				setDestPath(starteamModuleFacade.getDestPath());
			} else
				throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		}
	}

	public VcsFacade getFacade() {
		StarteamAdaptorFacade facade = new StarteamAdaptorFacade();
		facade.setConvertEOL(getConvertEOL());
		facade.setPassword(getPassword());
		facade.setProjectLocation(getProjectLocation());
		facade.setUser(getUser());

		facade.getModules().clear();
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			StarteamModule module = (StarteamModule) it.next();
			facade.getModules().add(module.getFacade());
		}
		return facade;
	}

	public void setFacade(VcsFacade facade) {
		if (!(facade instanceof StarteamAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		StarteamAdaptorFacade starteamFacade = (StarteamAdaptorFacade) facade;
		setConvertEOL(starteamFacade.getConvertEOL());
		setPassword(starteamFacade.getPassword());
		setProjectLocation(starteamFacade.getProjectLocation());
		setUser(starteamFacade.getUser());

		getModules().clear();
		Iterator it = starteamFacade.getModules().iterator();
		while (it.hasNext()) {
			StarteamModuleFacade moduleFacade = (StarteamModuleFacade) it.next();
			StarteamModule module = new StarteamModule();
			module.setFacade(moduleFacade);
			getModules().add(module);
		}
	}
}
