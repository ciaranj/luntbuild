/*
 * Copyright TRX Inc(c) 2006,
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.IStringProperty;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb20.MksAdaptorFacade;
import com.luntsys.luntbuild.facades.lb20.MksModuleFacade;
import com.luntsys.luntbuild.facades.lb20.ModuleFacade;
import com.luntsys.luntbuild.facades.lb20.VcsFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.Revisions;

/**
 * Adaptor to MKS version control system. It will be serialized by hibernate
 *
 * @author Stefan Baramov (TRX Inc.)
 *
 */
public class MksAdaptor extends Vcs {

	private static final long serialVersionUID = 3265795051270917502L;

	/**
	 * Sanbox Search Status Definitions
	 */
	public static final int NO_SANDBOX_FOUND = 0;
	public static final int MATCHING_SANDBOX_FOUND = 1;
	public static final int SANDBOX_DEVPATH_DIFFER = 2;
	public static final int SANDBOX_VERSION_DIFFER = 3;

	/**
	 * Internal logger.
	 */
	private static final Log logger = LogFactory.getLog(MksServiceProvider.class);

	/**
	 * MKS modules corresponds to MKS subproject.
	 */
	public class MksModule extends Module {

		private static final long serialVersionUID = 5068536921994975280L;

		/**
		 * Project/module version number. If not specified the current version will be obtained.
		 */
		private String version;

		/**
		 * The subproject folder relative from the root defined in the adaptor.
		 */
		private String subproject;

		/**
		 * This is the project file name. The default value is project.pj. However this could be
		 * changed. The project file name is used in variety of operations, including checkout, label
		 * and clean up.
		 */
		private String projectFileName;

		/**
		 * The development path to be used for this module.
		 */
		private String developmentPath;

		/**
		 * True if this is external to the main project and should not be checkpointed when the main
		 * project is checkpointed.
		 */
		private boolean external;

		/**
		 * Initialize a new object with its default settings.
		 */
		public MksModule() {

			// default values
			projectFileName = "project.pj";
			external = false;

			// required values, must be populated by the user.
			version = "";
			subproject = "";
		}

        /**
         * Initialize a new object with its default settings.
         */
        public MksModule(MksModule module) {
            this.version = module.version;
            this.subproject = module.subproject;
            this.projectFileName = module.projectFileName;
            this.developmentPath = module.developmentPath;
            this.external = module.external;
        }

		/**
		 * @inheritDoc
		 *
		 * @see com.luntsys.luntbuild.vcs.Vcs.Module#getProperties()
		 */
		public List getProperties() {
			List properties = getMksProperties();
			return properties;
		}

		/**
		 * @inheritDoc
		 *
		 * @see com.luntsys.luntbuild.vcs.Vcs.Module#getFacade()
		 */
		public ModuleFacade getFacade() {

			MksModuleFacade facade = new MksModuleFacade();

			facade.setExternal(isExternal());
			facade.setVersion(getVersion());
			facade.setSubproject(getSubproject());
			facade.setProjectFileName(getProjectFileName());
			facade.setDevelopmentPath(getDevelopmentPath());

			return facade;
		}

		/**
		 * @inheritDoc
		 *
		 * @see com.luntsys.luntbuild.vcs.Vcs.Module#setFacade(com.luntsys.luntbuild.facades.lb20.ModuleFacade)
		 */
		public void setFacade(ModuleFacade facade) {

			if (facade instanceof MksModuleFacade) {
				MksModuleFacade mksFacade = (MksModuleFacade) facade;

				setVersion(mksFacade.getVersion());
				setSubproject(mksFacade.getSubproject());
				setProjectFileName(mksFacade.getProjectFileName());
				setExternal(mksFacade.isExternal());
				setDevelopmentPath(mksFacade.getDevelopmentPath());
			}
			else {
				throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
			}
		}

		/**
		 * Returns the full project name as it should be stored in MKS repository.
		 *
		 * @return the project name based on the rootProject field of the enclosing class.
		 */
		public String getProject() {

			// NOTE: The separator should be the server file system separator.
			// However MKS API does not provide way to get the this information.
			// So assume the server is Unix based.

			return rootProject + "/" + getSubproject() + "/" + getProjectFileName();
		}

		/**
		 * Returns the complete name of the sandbox for this module based in the given working
		 * directory.
		 *
		 * @param workingDir
		 *        the working directory, the base for the sandbox.
		 * @return path relative to the given working directory and ending with the project file e.g.
		 *         project.pj.
		 */
		public String getSandbox(String workingDir) {

			return getSandboxBaseDir(workingDir) + File.separator + getProjectFileName();
		}

		/**
		 * Returns the base directory for this moduel sandbox, based on the given working directory.
		 *
		 * @param workingDir
		 *        the working directory becomes the base for the sandbox.
		 * @return path based on the working directory.
		 */
		public String getSandboxBaseDir(String workingDir) {

			return workingDir + File.separator + getSubprojectOsDependant();
		}

		public String getVersion() {

			return version;
		}

		public void setVersion(String label) {

			this.version = label;
		}

		public String getSubproject() {

			return subproject;
		}

		public String getSubprojectOsDependant() {
			// replace all \ and / with the OS depedant direcotry separator.
			return getSubproject().replaceAll("[/\\\\]", "\\" + File.separator);
		}

		public void setSubproject(String srcPath) {

			this.subproject = srcPath;
		}

		public String getProjectFileName() {

			return projectFileName;
		}

		public void setProjectFileName(String subprojectName) {

			this.projectFileName = subprojectName;
		}

		public boolean isExternal() {

			return external;
		}

		public void setExternal(boolean external) {

			this.external = external;
		}

		public String getDevelopmentPath() {

			return developmentPath;
		}

		public void setDevelopmentPath(String developmentPath) {

			this.developmentPath = developmentPath;
		}

	}

	/**
	 * Default hostname of the MKS server.
	 */
	private String defaultHostname;

	/**
	 * Default port number to be used to connect to the MKS server.
	 */
	private int defaultPort;

	/**
	 * Default user name used to create MKS client session.
	 */
	private String defaultUsername;

	/**
	 * Default password for the MKS client session.
	 */
	private String defaultPassword;

	/**
	 * The root MKS project.
	 */
	private String rootProject;

	/**
	 * Initialize a new object with its default settings.
	 */
	public MksAdaptor() {

		super();
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#getDisplayName()
	 */
	public String getDisplayName() {

		return "MKS";
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#getIconName()
	 */
	public String getIconName() {

		return "mks.gif";
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#getVcsSpecificProperties()
	 */
	public List getVcsSpecificProperties() {
		List properties = getMksModuleProperties();
		return properties;
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#cleanupCheckout(com.luntsys.luntbuild.db.Schedule, org.apache.tools.ant.Project)
	 */
	public void cleanupCheckout(Schedule workingSchedule, Project antProject) {

		String workingDir = workingSchedule.getWorkDirRaw();

		final MksServiceProvider mks = new MksServiceProvider(defaultHostname, defaultPort, defaultUsername, defaultPassword);
		try {
			if (mks.isPartOfSandbox(workingDir) )	 {
				// do nothing the checkout process will clear it up.
			}
			else {
				super.cleanupCheckout(workingSchedule, antProject);
			}
		}
		finally {
			mks.release();
		}
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#checkoutActually(com.luntsys.luntbuild.db.Build,
	 *      org.apache.tools.ant.Project)
	 */
	public void checkoutActually(Build build, Project project) {

		String workingDir = build.getSchedule().getWorkDirRaw();

		// checkout all modules
		// retrieve modules
		Iterator it = getModules().iterator();
		while (it.hasNext()) {

			// clone the module since the checkout opration is long and the module can
			// get changed in the middle.
			MksAdaptor.MksModule module = (MksAdaptor.MksModule) Luntbuild.cloneModule(this,
					(Vcs.Module) it.next());

			// propage the version of the build to the all modules.
			if ( Luntbuild.isEmpty(build.getVersion()) && ! module.isExternal() ) {
				module.setVersion(build.getVersion());
			}

			// retrieve the module.
			retrieveModule(workingDir, module, project, build.isCleanBuild());

		}
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#label(com.luntsys.luntbuild.db.Build,
	 *      org.apache.tools.ant.Project)
	 */
	public void label(Build build, Project antProject) {

		 Iterator it = getModules().iterator();
		 while (it.hasNext()) {

			// clone the module since the checkout opration is long and the module can
			// get changed in the middle.
			MksAdaptor.MksModule module = (MksAdaptor.MksModule) Luntbuild.cloneModule(this,
					(Vcs.Module) it.next());

			if (!module.isExternal()) {
				// label the subproject
				labelModule(module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
			}
		}

	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#getRevisionsSince(java.util.Date,
	 *      com.luntsys.luntbuild.db.Schedule, org.apache.tools.ant.Project)
	 */
	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {

		Revisions revisions = new Revisions();

		Iterator it = getModules().iterator();
		while (it.hasNext()) {

			MksAdaptor.MksModule module = (MksAdaptor.MksModule) Luntbuild.cloneModule(this,
					(Vcs.Module) it.next());


			if (module.isExternal()) {
				// not interested
				continue;
			}
			else {
				retrieveModuleRevisions(revisions, sinceDate, module, antProject);
			}
		}

		return revisions;
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#createNewModule()
	 */
	public Module createNewModule() {

		return new MksModule();
	}

    /**
     * @inheritDoc
     *
     * @see com.luntsys.luntbuild.vcs.Vcs#createNewModule()
     */
    public Module createNewModule(Module module) {

        return new MksModule((MksModule)module);
    }

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#saveToFacade(com.luntsys.luntbuild.facades.lb20.VcsFacade)
	 */
	public void saveToFacade(VcsFacade facade) {

		MksAdaptorFacade mksFacade = (MksAdaptorFacade) facade;

		mksFacade.setRootProject(getRootProject());
		mksFacade.setDefaultUsername(getDefaultUsername());
		mksFacade.setDefaultPassword(getDefaultPassword());
		mksFacade.setDefaultHostname(getDefaultHostname());
		mksFacade.setDefaultPort(getDefaultPort());
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#loadFromFacade(com.luntsys.luntbuild.facades.lb20.VcsFacade)
	 */
	public void loadFromFacade(VcsFacade facade) {

		if (!(facade instanceof MksAdaptorFacade)) {
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		}

		MksAdaptorFacade mksFacade = (MksAdaptorFacade) facade;

		setRootProject(mksFacade.getRootProject());
		setDefaultUsername(mksFacade.getDefaultUsername());
		setDefaultPassword(mksFacade.getDefaultPassword());
		setDefaultHostname(mksFacade.getDefaultHostname());
		setDefaultPort(mksFacade.getDefaultPort());
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.vcs.Vcs#constructFacade()
	 */
	public VcsFacade constructFacade() {

		return new MksAdaptorFacade();
	}


	public String getDefaultPassword() {

		return defaultPassword;
	}

	public void setDefaultPassword(String defaultPassword) {

		this.defaultPassword = defaultPassword;
	}

	public String getDefaultUsername() {

		return defaultUsername;
	}

	public void setDefaultUsername(String defaultUsername) {

		this.defaultUsername = defaultUsername;
	}

	public String getRootProject() {

		return rootProject;
	}

	public void setRootProject(String rootProject) {

		this.rootProject = rootProject;
	}

	public String getDefaultHostname() {

		return defaultHostname;
	}

	public void setDefaultHostname(String defaultHostname) {

		this.defaultHostname = defaultHostname;
	}

	public int getDefaultPort() {

		return defaultPort;
	}

	public void setDefaultPort(int defaultPort) {

		this.defaultPort = defaultPort;
	}

	/**
	 * Retrieves a particular module by creating a sandbox, populating the sandbox and droping the
	 * sandbox. All project.pj files will be cleaned up.
	 *
	 * @param workingDir
	 *        the working direcotry.
	 * @param module
	 *        the current MKS module.
	 * @param antProject
	 *        the current project.
	 * @param cleanBuild true if this is a clean build
	 */
	private void retrieveModule(String workingDir, MksModule module, Project antProject, boolean cleanBuild) {

		// project name, eg /mks/Projects/FooBarApp/project.pj
		final String project = module.getProject();

		// sandbox target dir, eg d:\projects\FooBarApp
		final String sandboxDir = module.getSandboxBaseDir(workingDir);

		// sandbox name, eg d:\proejcts\FooBarApp\project.pj
		final String sandbox = module.getSandbox(workingDir);

		// log info
		antProject.log("Retrieving project: " + module.getProject(), Project.MSG_INFO);

		MksServiceProvider mks = new MksServiceProvider(defaultHostname, defaultPort, defaultUsername, defaultPassword);
		try {

			final int sandboxStatus = mks.isSandboxExist(sandbox, module);

			if( sandboxStatus == MksAdaptor.NO_SANDBOX_FOUND )
			{
				logDual(antProject, "Creating sandbox:" + sandboxDir, Project.MSG_INFO);
				mks.createSandbox(project, sandboxDir, module.getVersion(), module.getDevelopmentPath());
			}
			else if( sandboxStatus == MksAdaptor.SANDBOX_DEVPATH_DIFFER || sandboxStatus == MksAdaptor.SANDBOX_VERSION_DIFFER )
			{
				logDual(antProject, "Droping sandbox: " + sandbox, Project.MSG_INFO);
				mks.dropSandbox(sandbox);
				logDual(antProject, "Creating sandbox:" + sandboxDir, Project.MSG_INFO);
				mks.createSandbox(project, sandboxDir, module.getVersion(), module.getDevelopmentPath());
			}
			else if( sandboxStatus == MksAdaptor.MATCHING_SANDBOX_FOUND )
			{
				if( cleanBuild )
				{
					// No matter that we found a sandbox, drop and recreate it....
					logDual(antProject, "Droping sandbox: " + sandbox, Project.MSG_INFO);
					mks.dropSandbox(sandbox);
					logDual(antProject, "Creating sandbox:" + sandboxDir, Project.MSG_INFO);
					mks.createSandbox(project, sandboxDir, module.getVersion(), module.getDevelopmentPath());
				}
				else
				{
					// Just resynch it
					logDual(antProject, "Resynchronizing the existing sandbox:" + sandbox, Project.MSG_INFO);
					mks.resyncSandbox(sandbox, true);
				}
			}

		}
		finally {
			mks.release();
		}

	}

	/**
	 * Loads the revisions for the given module.
	 *
	 * @param revisions
	 *        the revisions data holder.
	 * @param sinceDate
	 *        the start date.
	 * @param module
	 *        the module to inspect.
	 * @param antProject
	 *        the current project.
	 */
	private void retrieveModuleRevisions(Revisions revisions, Date sinceDate, MksModule module, Project antProject) {

		// --- log info ---
		antProject.log("Retrieving project recent revisions : " + module.getProject(), Project.MSG_INFO);

		final String project = module.getProject();

		final MksServiceProvider mks = new MksServiceProvider(defaultHostname, defaultPort, defaultUsername, defaultPassword);
		try {
			mks.getRevisionsSince(revisions, sinceDate, project, module.getDevelopmentPath());
		}
		finally {
			mks.release();
		}
	}


	/**
	 * Checkpoints the project described with the given module.
	 *
	 * @param module
	 *        the module.
	 * @param checkpointDescription
	 *        the checkpoint description.
	 * @param antProject
	 *        the ant project.
	 */
	private void labelModule(MksModule module, String checkpointDescription, Project antProject) {

		final String project = module.getProject();

		// --- log info ---
		antProject.log("Creating a checkpoint for project: " + project, Project.MSG_INFO);

		// checkpoint
		final MksServiceProvider mks = new MksServiceProvider(defaultHostname, defaultPort, defaultUsername, defaultPassword);
		try {
			mks.checkpointProject(project, checkpointDescription, module.getDevelopmentPath());
		}
		finally {
			mks.release();
		}

	}

	private void logDual(Project antProject, String msg, int level) {

		// create a log in the ant log file
		antProject.log(msg, level);

		// create a log record in the system log file.
		if (level == Project.MSG_INFO) {
			logger.info(msg);
		}
		else if (level == Project.MSG_DEBUG) {
			logger.debug(msg);
		}
		else if (level == Project.MSG_ERR) {
			logger.error(msg);
		}
		else if (level == Project.MSG_WARN) {
			logger.warn(msg);
		}

	}

}
