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
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb20.StarteamAdaptorFacade;
import com.luntsys.luntbuild.facades.lb20.StarteamModuleFacade;
import com.luntsys.luntbuild.facades.lb20.VcsFacade;
import com.luntsys.luntbuild.utility.*;
import com.luntsys.luntbuild.ant.starteam.StarTeamCheckout;
import com.luntsys.luntbuild.ant.starteam.StarTeamLabel;
import com.luntsys.luntbuild.ant.Commandline;
import com.starbase.starteam.*;
import com.starbase.util.OLEDate;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Environment;

import java.util.*;

/**
 * Borland starteam adaptor
 *
 * @author robin shine
 */
public class StarteamAdaptor extends Vcs {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;

	private String projectLocation;
	private String user;
	private String password;
	private String convertEOL;

	private transient OLEDate checkoutDate;

	public String getDisplayName() {
		return "StarTeam";
	}

	public String getIconName() {
		return "starteam.jpg";
	}

	public void checkoutActually(Build build, Project antProject) {
		// record current time (minus 10 seconds to tolerate time difference between starteam server and build server
		checkoutDate = new OLEDate(System.currentTimeMillis() - 10000);
		String workingDir = build.getSchedule().getWorkDirRaw();
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

	public void label(Build build, Project antProject) {
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			StarteamModule module = (StarteamModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel()))
				labelModule(module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
		}
	}

	public List getVcsSpecificProperties() {
		List properties = getStarteamProperties();
        return properties;
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
			antProject.log("Retrieve source path \"" + module.getSrcPath() + "\" of StarTeam view \"" + module.getStarteamView() + "\"");
		else
			antProject.log("Update source path \"" + module.getSrcPath() + "\" of StarTeam view \"" + module.getStarteamView() + "\"");
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

    if (!Luntbuild.isEmpty(module.getStarteamPromotionState()))
      starteamTask.setPromotionState(module.getStarteamPromotionState());
    else if (!Luntbuild.isEmpty(module.getLabel()))
			starteamTask.setLabel(module.getLabel());
		else
			starteamTask.setAsOfDate(checkoutDate);
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
		antProject.log("Label source path \"" + module.getSrcPath() + "\" of StarTeam view \"" + module.getStarteamView() + "\"");

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

		starteamTask.setLastBuild(checkoutDate);
		starteamTask.setLabel(label);
		starteamTask.setDescription("luntbuild labels");
		starteamTask.setTaskType("StarTeamLabel");
		starteamTask.setTaskName("StarTeamLabel");
		starteamTask.execute();
	}

	public Vcs.Module createNewModule() {
		return new StarteamModule();
	}

    public Vcs.Module createNewModule(Vcs.Module module) {
        return new StarteamModule((StarteamModule)module);
    }

	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		Revisions revisions = new Revisions();

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
						throw new BuildException("StarTeam view \"" + viewName + "\" not found!");

					server = view.getServer();

					// shift back 10 seconds to tolerate time difference between starteam server and build server
					OLEDate currentTime = new OLEDate(System.currentTimeMillis() - 10000);
					OLEDate buildTime = new OLEDate(sinceDate);

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
								"does not exist in the current configuration of StarTeam view \"" + viewName + "\"");
					Folder buildRoot = StarTeamFinder.findFolder(buildConfiguration.getRootFolder(),
							Luntbuild.concatPath("/", module.getSrcPath()));
					if (buildRoot == null)
						throw new BuildException("Source path \"" + module.getSrcPath() + "\" " +
								"does not exist in the last build's configuration of StarTeam view \"" + viewName + "\"");

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

	private String getStarteamProject() {
		String[] fields = getProjectLocation().split("/");
		if (fields.length != 2 || fields[1].trim().equals(""))
			throw new ValidationException("Invalid value for project location: " + getProjectLocation());
		return fields[1].trim();
	}

	public void validateProperties() {
		super.validateProperties();
		String[] fields = getProjectLocation().split("/");
		if (fields.length != 2 || fields[1].trim().equals(""))
			throw new ValidationException("Invalid value for project location: " + getProjectLocation());
		if (!Luntbuild.isEmpty(getConvertEOL())) {
			if (!getConvertEOL().trim().equalsIgnoreCase("yes") &&
					!getConvertEOL().trim().equalsIgnoreCase("no"))
				throw new ValidationException("Invalid value for convert EOL: should be " +
						"yes or no");
			setConvertEOL(getConvertEOL().trim());
		}
	}

	public class StarteamModule extends Vcs.Module {
		/**
		 * Keep tracks of version of this class, used when do serialization-deserialization
		 */
		static final long serialVersionUID = 1;

		private String starteamView;
    private String starteamPromotionState;
		private String srcPath;
		private String label;
		private String destPath;

        /**
         * Constructor
         */
        public StarteamModule() {}

        /**
         * Copy Constructor
         */
        public StarteamModule(StarteamModule module) {
            this.starteamView = module.starteamView;
            this.srcPath = module.srcPath;
            this.label = module.label;
            this.destPath = module.destPath;
        }

		public List getProperties() {
			List properties = getStarteamModuleProperties();
			return properties;
		}

		public String getStarteamView() {
			return starteamView;
		}

		public void setStarteamView(String starteamView) {
			this.starteamView = starteamView;
		}

    public String getStarteamPromotionState() {
      return starteamPromotionState;
    }

    public void setStarteamPromotionState(String starteamPromotionState) {
      this.starteamPromotionState = starteamPromotionState;
    }

		public String getSrcPath() {
			return srcPath;
		}

		public void setSrcPath(String srcPath) {
			this.srcPath = srcPath;
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

		public com.luntsys.luntbuild.facades.lb20.ModuleFacade getFacade() {
			StarteamModuleFacade facade = new StarteamModuleFacade();
			facade.setStarteamView(getStarteamView());
			facade.setDestPath(getDestPath());
			facade.setLabel(getLabel());
      facade.setStarteamPromotionState(getStarteamPromotionState());
			facade.setSrcPath(getSrcPath());
			return facade;
		}

		public void setFacade(com.luntsys.luntbuild.facades.lb20.ModuleFacade facade) {
			if (facade instanceof StarteamModuleFacade) {
				StarteamModuleFacade starteamModuleFacade = (StarteamModuleFacade) facade;
				setStarteamView(starteamModuleFacade.getStarteamView());
				setLabel(starteamModuleFacade.getLabel());
        setStarteamPromotionState(starteamModuleFacade.getStarteamPromotionState());
				setSrcPath(starteamModuleFacade.getSrcPath());
				setDestPath(starteamModuleFacade.getDestPath());
			} else
				throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		}
	}

	public void saveToFacade(com.luntsys.luntbuild.facades.lb20.VcsFacade facade) {
		StarteamAdaptorFacade starteamFacade = (StarteamAdaptorFacade) facade;
		starteamFacade.setConvertEOL(getConvertEOL());
		starteamFacade.setPassword(getPassword());
		starteamFacade.setProjectLocation(getProjectLocation());
		starteamFacade.setUser(getUser());
	}

	public void loadFromFacade(com.luntsys.luntbuild.facades.lb20.VcsFacade facade) {
		if (!(facade instanceof StarteamAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		StarteamAdaptorFacade starteamFacade = (StarteamAdaptorFacade) facade;
		setConvertEOL(starteamFacade.getConvertEOL());
		setPassword(starteamFacade.getPassword());
		setProjectLocation(starteamFacade.getProjectLocation());
		setUser(starteamFacade.getUser());
	}

	public VcsFacade constructFacade() {
		return new StarteamAdaptorFacade();
	}
}
