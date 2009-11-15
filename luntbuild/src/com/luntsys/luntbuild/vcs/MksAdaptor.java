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
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tools.ant.Project;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.MksAdaptorFacade;
import com.luntsys.luntbuild.facades.lb12.MksModuleFacade;
import com.luntsys.luntbuild.facades.lb12.ModuleFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.OgnlHelper;
import com.luntsys.luntbuild.utility.Revisions;

/**
 * MKS VCS adaptor implementation.
 * 
 * <p>This adaptor is NOT safe for remote hosts.</p>
 * 
 * @author Stefan Baramov (TRX Inc.)
 */
public class MksAdaptor extends Vcs {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	private static final long serialVersionUID = 3265795051270917502L;

	// Sanbox Search Status Definitions
	/** Sanbox Search Status, none found */
	public static final int NO_SANDBOX_FOUND = 0;
	/** Sanbox Search Status, found */
	public static final int MATCHING_SANDBOX_FOUND = 1;
	/** Sanbox Search Status, devpath differ */
	public static final int SANDBOX_DEVPATH_DIFFER = 2;
	/** Sanbox Search Status, version differ */
	public static final int SANDBOX_VERSION_DIFFER = 3;

	/**
	 * Internal logger.
	 */
	private static final Log logger = LogFactory.getLog(MksServiceProvider.class);

	/**
	 * Selection model used for user interface of <code>MksAdaptor</code>.
	 */
	private static class MksYesNoSelectionModel implements IPropertySelectionModel {
		String[] values = {"no", "yes"};

		/**
		 * Gets the number of options.
		 * 
		 * @return the number of options
		 */
		public int getOptionCount() {
			return this.values.length;
		}

		/**
		 * Gets an option.
		 * 
		 * @param index the index of the opiton
		 * @return the option
		 */
		public Object getOption(int index) {
			return this.values[index];
		}

		/**
		 * Gets the display label of an option.
		 * 
		 * @param index the index of the opiton
		 * @return the label
		 */
		public String getLabel(int index) {
			return this.values[index];
		}

		/**
		 * Gets the value of an option.
		 * 
		 * @param index the index of the opiton
		 * @return the value
		 */
		public String getValue(int index) {
			return this.values[index];
		}

		/**
		 * Gets the option that corresponds to a value.
		 * 
		 * @param value the value
		 * @return the option
		 */
		public Object translateValue(String value) {
			return value;
		}
	}

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
		 * Constructor, creates a blank MKS module.
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
		 * Copy constructor, creates a MKS module from another MKS module.
		 * 
		 * @param module the module to create from
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
		 */
		public List getProperties() {
			List properties = new ArrayList();

			// subproject
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "Subproject";
				}

				public String getDescription() {
					return "The subproject is the name of the subproject related to the root project defined " +
							"in the MKS adaptor. The project.pj file should not be included. This property is required.";
				}

				public String getValue() {
					return getSubproject();
				}

				public String getActualValue() {
					return getActualSubproject();
				}

				public void setValue(String value) {
					setSubproject(value);
				}
			});

			// version
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "Version";
				}

				public String getDescription() {
					return "The subproject version or checkpoint to be retrieved. If left empty then the latest " +
							"version of the project will be retrieved. If specified the the corresponding " +
							"version or checkpoint will be retrieved. Version and Development path are mutually exclusive. "+
							"Version will overide the Development Path. Enter project version like '1.6' or Chekpoint "+
							"Label like 'Dev Build 1.2' as it appears in MKS repository";
				}

				public boolean isRequired() {
					return false;
				}

				public String getValue() {
					return getVersion();
				}

				public String getActualValue() {
					return getActualVersion();
				}

				public void setValue(String value) {
					setVersion(value);
				}
			});


			// project file name
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "Project File Name";
				}

				public String getDescription() {
					return "The project file name should be project.pj. Change it if it is something else.";
				}

				public String getValue() {
					return getProjectFileName();
				}

				public String getActualValue() {
					return getActualProjectFileName();
				}

				public void setValue(String value) {
					setProjectFileName(value);
				}
			});

			// project development path
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "Development Path";
				}

				public String getDescription() {
					return "The development path as labeled in the MKS repository. Leave it empty if the trunk should be used.";
				}

				public String getValue() {
					return getDevelopmentPath();
				}

				public String getActualValue() {
					return getActualDevelopmentPath();
				}

				public void setValue(String value) {
					setDevelopmentPath(value);
				}

				public boolean isRequired() {
					return false;
				}
			});

			// external
			DisplayProperty p = new DisplayProperty() {
				public String getDisplayName() {
					return "Is external module";
				}

				public String getDescription() {
					return "This property indicates whether or not this module is an external for the main module. " +
							"External modules are usually independant projects or libraries. An external module " +
							"may have a different version then the main module. The external module will " +
							"not get labeled (checkpointed) when the main module is labeled. " +
							"For example: if the main module is called myApp and it depends on FooLib and BarLib " +
							"and FooLib and BarLib have their own development cycle, then the configuration " +
							"should have three modules: one for the myApp, and two external modules for " +
							"FooLib and BarLib.";
				}

				public boolean isRequired() {
					return false;
				}

				public boolean isSelect() {
					return true;
				}

				public String getValue() {
					return isExternal() ? "yes" : "no";
				}

				public String getActualValue() {
					return isExternal() ? "yes" : "no";
				}

				public void setValue(String value) {
					if (Luntbuild.isEmpty(value)) {
						throw new RuntimeException("Incorrect input value!");
					}

					if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true")) {
						setExternal(true);
					}
					else {
						setExternal(false);
					}
				}
			};
			p.setSelectionModel(new MksYesNoSelectionModel());
			properties.add(p);

			return properties;
		}

		/**
		 * @inheritDoc
		 * @see ModuleFacade
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
	     * @throws RuntimeException if the facade is not an <code>ModuleFacade</code>
		 * @see ModuleFacade
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
		 * Gets the full project name as it should be stored in MKS repository.
		 *
		 * @return the full project name
		 */
		public String getProject() {
			// NOTE: The separator should be the server file system separator.
			// However MKS API does not provide way to get the this information.
			// So assume the server is Unix based.
			return getActualRootProject() + "/" + getActualSubproject() + "/" + getActualProjectFileName();
		}

		/**
		 * Gets the complete name of the sandbox for this module based in the given working
		 * directory.
		 *
		 * @param workingDir the working directory
		 * @return the sandbox path, ending with the project (e.g. file project.pj)
		 */
		public String getSandbox(String workingDir) {
			return getSandboxBaseDir(workingDir) + File.separator + getActualProjectFileName();
		}

		/**
		 * Gets the base directory for this module's sandbox, based on the given working directory.
		 *
		 * @param workingDir the working directory
		 * @return the base directory
		 */
		public String getSandboxBaseDir(String workingDir) {

			return workingDir + File.separator + getSubprojectOsDependant();
		}

		/**
		 * Gets the subproject version or checkpoint to be retrieved.
		 * 
		 * @return the subproject version
		 */
		public String getVersion() {
			return version;
		}

		/**
		 * Gets the subproject version or checkpoint to be retrieved. This method will parse OGNL variables.
		 * 
		 * @return the subproject version
		 */
		public String getActualVersion() {
			return OgnlHelper.evaluateScheduleValue(getVersion());
		}

		/**
		 * Sets the subproject version or checkpoint to be retrieved.
		 * 
		 * @param label the subproject version
		 */
		public void setVersion(String label) {
			this.version = label;
		}

		/**
		 * Gets the subproject name.
		 * 
		 * @return the subproject name
		 */
		public String getSubproject() {
			return subproject;
		}

		/**
		 * Gets the subproject name. This method will parse OGNL variables.
		 * 
		 * @return the subproject name
		 */
		public String getActualSubproject() {
			return OgnlHelper.evaluateScheduleValue(getSubproject());
		}

		/**
		 * Gets the subproject name for the operating system.
		 * 
		 * @return the subproject name
		 */
		public String getSubprojectOsDependant() {
			// replace all \ and / with the OS depedant direcotry separator.
			return getActualSubproject().replaceAll("[/\\\\]", "\\" + File.separator);
		}

		/**
		 * Sets the subproject name.
		 * 
		 * @param srcPath the subproject name
		 */
		public void setSubproject(String srcPath) {
			this.subproject = srcPath;
		}

		/**
		 * Gets the project file name.
		 * 
		 * @return the project file name
		 */
		public String getProjectFileName() {
			return projectFileName;
		}

		/**
		 * Gets the project file name. This method will parse OGNL variables.
		 * 
		 * @return the project file name
		 */
		public String getActualProjectFileName() {
			return OgnlHelper.evaluateScheduleValue(getProjectFileName());
		}

		/**
		 * Sets the project file name.
		 * 
		 * @param subprojectName the project file name
		 */
		public void setProjectFileName(String subprojectName) {
			this.projectFileName = subprojectName;
		}

		/**
		 * Checks if this is an external module.
		 * 
		 * @return <code>true</code> if this is an external module
		 */
		public boolean isExternal() {
			return external;
		}

		/**
		 * Sets if this is an external module.
		 * 
		 * @param external set <code>true</code> if this is an external module
		 */
		public void setExternal(boolean external) {
			this.external = external;
		}

		/**
		 * Gets the development path.
		 * 
		 * @return the development path
		 */
		public String getDevelopmentPath() {
			return developmentPath;
		}

		/**
		 * Gets the development path. This method will parse OGNL variables.
		 * 
		 * @return the development path
		 */
		public String getActualDevelopmentPath() {
			return OgnlHelper.evaluateScheduleValue(getDevelopmentPath());
		}

		/**
		 * Sets the development path.
		 * 
		 * @param developmentPath the development path
		 */
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
     * Constructs a MKS adaptor with default settings.
     */
	public MksAdaptor() {
		super();
	}

	/**
	 * @inheritDoc
	 */
	public String getDisplayName() {

		return "MKS";
	}

	/**
	 * @inheritDoc
	 */
	public String getIconName() {

		return "mks.gif";
	}

	/**
	 * @inheritDoc
	 */
	public List getVcsSpecificProperties() {

		List properties = new ArrayList();

		// default hostname
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Hostname";
			}

			public String getDescription() {
				return "The hostname used to connect to the MKS server. The default value stored in the client will be used, if left empty.";
			}

			public String getValue() {
				return getDefaultHostname();
			}

			public void setValue(String value) {
				setDefaultHostname(value);
			}

			public boolean isRequired() {
				return false;
			}
		});

		// default port
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Port";
			}

			public String getDescription() {
				return "The port used to connect to the MKS server. The default value stored in the client will be used, if left empty.";
			}

			public String getValue() {
				return Integer.toString(getDefaultPort());
			}

			public void setValue(String value) {
				try {
					setDefaultPort(Integer.parseInt(value));
				}
				catch (NumberFormatException ex) {
					setDefaultPort(7001);
				}
			}

			public boolean isRequired() {
				return false;
			}
		});

		// default username
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Username";
			}

			public String getDescription() {
				return "The username used to connect to the MKS client.";
			}

			public String getValue() {
				return getDefaultUsername();
			}

			public void setValue(String value) {
				setDefaultUsername(value);
			}
		});

		// default password
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Password";
			}

			public String getDescription() {
				return "The password used to connect to the MKS client.";
			}

			public boolean isSecret() {
				return true;
			}

			public String getValue() {
				return getDefaultPassword();
			}

			public void setValue(String value) {
				setDefaultPassword(value);
			}
		});

		// project root
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Project Root";
			}

			public String getDescription() {
				return "The root project for all modules used in this adaptor. " +
						"It should be an absolute path resolvable on the MKS server file system and should " +
						"point to a MKS project part of the MKS repository. It should not have the project.pj file. " +
						"For example /mks/Projects/myprojects";
			}

			public String getValue() {
				return getRootProject();
			}

			public String getActualValue() {
				return getActualRootProject();
			}

			public void setValue(String value) {
				setRootProject(value);
			}
		});

		return properties;
	}

	/**
	 * @inheritDoc
	 */
	public void cleanupCheckout(Schedule workingSchedule, Project antProject) {
		String workingDir = workingSchedule.getWorkDirRaw();

		final MksServiceProvider mks = new MksServiceProvider(defaultHostname, defaultPort, defaultUsername, defaultPassword);
		try {
			if (mks.isPartOfSandbox(workingDir) ) {
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
	 */
	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		Revisions revisions = new Revisions();
        revisions.addLog(this.getClass().getName(), toString());
        revisions.getChangeLogs().add("*************************************************************");
        revisions.getChangeLogs().add(toString());
        revisions.getChangeLogs().add("");

		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			MksAdaptor.MksModule module = (MksAdaptor.MksModule) Luntbuild.cloneModule(this,
					(Vcs.Module) it.next());

			if (module.isExternal()) {
				// not interested
				continue;
			} else {
				retrieveModuleRevisions(revisions, sinceDate, module, antProject);
			}
		}

		return revisions;
	}

	/**
	 * @inheritDoc
	 * @see MksModule
	 */
	public Module createNewModule() {
		return new MksModule();
	}

    /**
     * @inheritDoc
	 * @see MksModule
     */
    public Module createNewModule(Module module) {
        return new MksModule((MksModule)module);
    }

	/**
	 * @inheritDoc
	 * @see MksAdaptorFacade
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
     * @throws RuntimeException if the facade is not an <code>MksAdaptorFacade</code>
	 * @see MksAdaptorFacade
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
	 */
	public VcsFacade constructFacade() {
		return new MksAdaptorFacade();
	}

	/**
	 * Gets the default password.
	 * 
	 * @return the default password
	 */
	public String getDefaultPassword() {
		return defaultPassword;
	}

	/**
	 * Sets the default password.
	 * 
	 * @param defaultPassword the default password
	 */
	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	/**
	 * Gets the default user name.
	 * 
	 * @return the default user name
	 */
	public String getDefaultUsername() {
		return defaultUsername;
	}

	/**
	 * Sets the default user name.
	 * 
	 * @param defaultUsername the default user name
	 */
	public void setDefaultUsername(String defaultUsername) {
		this.defaultUsername = defaultUsername;
	}

	/**
	 * Gets the root project.
	 * 
	 * @return the root project
	 */
	public String getRootProject() {
		return rootProject;
	}

	/**
	 * Gets the root project. This method will parse OGNL variables.
	 * 
	 * @return the root project
	 */
	public String getActualRootProject() {
		return OgnlHelper.evaluateScheduleValue(getRootProject());
	}

	/**
	 * Sets the root project.
	 * 
	 * @param rootProject the root project
	 */
	public void setRootProject(String rootProject) {
		this.rootProject = rootProject;
	}

	/**
	 * Gets the default host name.
	 * 
	 * @return the default host name
	 */
	public String getDefaultHostname() {
		return defaultHostname;
	}

	/**
	 * Sets the default host name.
	 * 
	 * @param defaultHostname the default host name
	 */
	public void setDefaultHostname(String defaultHostname) {
		this.defaultHostname = defaultHostname;
	}

	/**
	 * Gets the default port.
	 * 
	 * @return the default port
	 */
	public int getDefaultPort() {
		return defaultPort;
	}

	/**
	 * Sets the default port.
	 * 
	 * @param defaultPort the default port
	 */
	public void setDefaultPort(int defaultPort) {
		this.defaultPort = defaultPort;
	}

	/**
	 * Checks out the contents from a module by creating a sandbox, populating the sandbox and droping the
	 * sandbox. All project.pj files will be cleaned up.
	 * 
	 * @param workingDir the working directory
	 * @param module the module
	 * @param antProject the ant project used for logging
	 * @param isClean set <code>true</code> if this is a clean build
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
				mks.createSandbox(project, sandboxDir, module.getActualVersion(), module.getActualDevelopmentPath());
			}
			else if( sandboxStatus == MksAdaptor.SANDBOX_DEVPATH_DIFFER || sandboxStatus == MksAdaptor.SANDBOX_VERSION_DIFFER )
			{
				logDual(antProject, "Droping sandbox: " + sandbox, Project.MSG_INFO);
				mks.dropSandbox(sandbox);
				logDual(antProject, "Creating sandbox:" + sandboxDir, Project.MSG_INFO);
				mks.createSandbox(project, sandboxDir, module.getActualVersion(), module.getActualDevelopmentPath());
			}
			else if( sandboxStatus == MksAdaptor.MATCHING_SANDBOX_FOUND )
			{
				if( cleanBuild )
				{
					// No matter that we found a sandbox, drop and recreate it....
					logDual(antProject, "Droping sandbox: " + sandbox, Project.MSG_INFO);
					mks.dropSandbox(sandbox);
					logDual(antProject, "Creating sandbox:" + sandboxDir, Project.MSG_INFO);
					mks.createSandbox(project, sandboxDir, module.getActualVersion(), module.getActualDevelopmentPath());
				}
				else
				{
					// Just resynch it
					logDual(antProject, "Resynchronizing the existing sandbox:" + sandbox, Project.MSG_INFO);
					mks.resyncSandbox(sandbox, true);
				}
			}
		} finally {
			mks.release();
		}
	}

	/**
	 * Gets a list of change logs from the specified module since the specified date.
	 * 
	 * @param sinceDate the date to check from
	 * @param workingSchedule the currently running schedule
	 * @param module the module
	 * @param antProject the ant project used for logging
	 */
	private void retrieveModuleRevisions(Revisions revisions, Date sinceDate, MksModule module, Project antProject) {
		// --- log info ---
		antProject.log("Retrieving project recent revisions : " + module.getProject(), Project.MSG_INFO);

		final String project = module.getProject();

		final MksServiceProvider mks = new MksServiceProvider(defaultHostname, defaultPort, defaultUsername, defaultPassword);
		try {
			mks.getRevisionsSince(revisions, sinceDate, project, module.getActualDevelopmentPath());
		} finally {
			mks.release();
		}
	}

	/**
	 * Labels the contents of a module.
	 * 
	 * @param workingDir the working directory
	 * @param module the module
	 * @param label the label
	 * @param antProject the ant project used for logging
	 * @param host the host to execute on
	 */
	private void labelModule(MksModule module, String checkpointDescription, Project antProject) {

		final String project = module.getProject();

		// --- log info ---
		antProject.log("Creating a checkpoint for project: " + project, Project.MSG_INFO);

		// checkpoint
		final MksServiceProvider mks = new MksServiceProvider(defaultHostname, defaultPort, defaultUsername, defaultPassword);
		try {
			mks.checkpointProject(project, checkpointDescription, module.getActualDevelopmentPath());
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
