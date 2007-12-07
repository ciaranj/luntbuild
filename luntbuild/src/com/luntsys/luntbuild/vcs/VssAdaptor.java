/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-9
 * Time: 10:44:16
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
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

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Environment;

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.ModuleFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.facades.lb12.VssAdaptorFacade;
import com.luntsys.luntbuild.facades.lb12.VssModuleFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.LuntbuildLogger;
import com.luntsys.luntbuild.utility.MyExecTask;
import com.luntsys.luntbuild.utility.OgnlHelper;
import com.luntsys.luntbuild.utility.RevisionBlock;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.SynchronizedDateFormatter;
import com.luntsys.luntbuild.utility.ValidationException;

/**
 * Microsoft Visual Source Safe VCS adaptor implementation.
 * 
 * <p>This adaptor is safe for remote hosts.</p>
 *
 * @author robin shine
 */
public class VssAdaptor extends Vcs {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;
	/** Default date format for the <code>ss history</code> command */
	public static final String DEFAULT_DATETIME_FORMAT = "M/dd/yy;h:mm:ssa";
	/** The input feed to any vss commands to avoid command hang */
	public static final String COMMAND_INPUT = "\n\n\n\n\n\n\n\n";
	private String vssPath; // path to srcsafe.ini file
	private String vssUser; // user name
	private String vssPassword; // password
	private String dateTimeFormat; // datetime format for ss HISTORY command
	private String ssDir; // path to ss.exe file, if blank, ss.exe is expected to be in the PATH environment variable

	/**
	 * Gets the SourceSafe path (SSDIR), location should contain srcsafe.ini.
	 * 
	 * @return the SourceSafe path
	 */
	public String getVssPath() {
		return vssPath;
	}

	/**
	 * Gets the SourceSafe path (SSDIR), location should contain srcsafe.ini. This method will parse OGNL variables.
	 * 
	 * @return the SourceSafe path
	 */
    private String getActualVssPath() {
		return OgnlHelper.evaluateScheduleValue(getVssPath());
	}

	/**
	 * Sets the SourceSafe path (SSDIR), location should contain srcsafe.ini.
	 * 
	 * @param vssPath the SourceSafe path
	 */
	public void setVssPath(String vssPath) {
		this.vssPath = vssPath;
	}

	/**
	 * Gets the login user.
	 * 
	 * @return the login user
	 */
	public String getVssUser() {
		return vssUser;
	}

	/**
	 * Sets the login user.
	 * 
	 * @param vssUser the login user
	 */
	public void setVssUser(String vssUser) {
		this.vssUser = vssUser;
	}

	/**
	 * Gets the login password.
	 * 
	 * @return the login password
	 */
	public String getVssPassword() {
		return vssPassword;
	}

	/**
	 * Sets the login password.
	 * 
	 * @param vssPassword the login password
	 */
	public void setVssPassword(String vssPassword) {
		this.vssPassword = vssPassword;
	}

	/**
	 * Gets the path to the SourceSafe executable.
	 * 
	 * @return the path to the SourceSafe executable
	 */
	public String getSsDir() {
		return ssDir;
	}

	/**
	 * Sets the path to the SourceSafe executable.
	 * 
	 * @param ssDir the path to the SourceSafe executable
	 */
	public void setSsDir(String ssDir) {
		this.ssDir = ssDir;
	}

	/**
	 * Gets the datetime format to use for the <code>ss history</code> command.
	 * 
	 * @return the datetime format
	 */
	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	/**
	 * Gets the datetime format to use for the <code>ss history</code> command. This method will parse OGNL variables.
	 * 
	 * @return the datetime format
	 */
    private String getActualDateTimeFormat() {
		return OgnlHelper.evaluateScheduleValue(getDateTimeFormat());
	}

	/**
	 * Sets the datetime format to use for the <code>ss history</code> command.
	 * 
	 * @param dateTimeFormat the datetime format
	 */
	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

    /**
     * @inheritDoc
     */
	public String getDisplayName() {
		return "Visual Sourcesafe";
	}

    /**
     * @inheritDoc
     */
	public String getIconName() {
		return "vss.jpg";
	}

	/**
	 * Retrieves the contents of a module.
	 * 
	 * @param workingDir the working directory
     * @param module the module
	 * @param isClean set <code>true</code> if this is a clean build
     * @param antProject the ant project used for logging
     */
	private void retrieveModule(String workingDir, VssModule module, boolean isClean, Project antProject) {
		if (isClean)
			antProject.log("Retrieve source path: " + module.getActualSrcPath(), Project.MSG_INFO);
		else
			antProject.log("Update source path: " + module.getActualSrcPath(), Project.MSG_INFO);
		String destDir;
		if (Luntbuild.isEmpty(module.getDestPath()))
			destDir = Luntbuild.concatPath(workingDir, module.getActualSrcPath());
		else
			destDir = Luntbuild.concatPath(workingDir, module.getActualDestPath());

		if (isClean)
			Luntbuild.deleteDir(destDir);
		Luntbuild.createDir(destDir);

		Commandline cmdLine = buildVssExecutable();
		cmdLine.createArgument().setValue("Get");
		cmdLine.createArgument().setValue(Luntbuild.concatPath("$", module.getActualSrcPath()));
		cmdLine.createArgument().setLine("-I- -R");
		if (!Luntbuild.isEmpty(module.getLabel()))
			cmdLine.createArgument().setValue("-VL" + module.getActualLabel());
		cmdLine.createArgument().setValue("-W");
		LuntbuildLogger luntBuildLogger = Luntbuild.getLuntBuildLogger(antProject);
		if (luntBuildLogger == null || luntBuildLogger.getMessageOutputLevel() <= Project.MSG_INFO)
			cmdLine.createArgument().setValue("-O-");

		appendLoginInfo(cmdLine);
		Environment env = buildVssEnvironment();

		new MyExecTask("get", antProject, destDir, cmdLine, env, COMMAND_INPUT, Project.MSG_INFO).execute();
	}

	/**
	 * Labels the contents of a module.
	 * 
	 * @param workingDir the working directory
	 * @param module the module
	 * @param label the label
	 * @param antProject the ant project used for logging
	 */
	private void labelModule(String workingDir, VssModule module, String label, Project antProject) {
		antProject.log("Label source path: " + module.getActualSrcPath(), Project.MSG_INFO);
		Commandline cmdLine = buildVssExecutable();
		cmdLine.createArgument().setValue("Label");
		cmdLine.createArgument().setValue(Luntbuild.concatPath("$", module.getActualSrcPath()));
		cmdLine.createArgument().setLine("-C- -I- -L" + label);
		appendLoginInfo(cmdLine);
		Environment env = buildVssEnvironment();

		new MyExecTask("label", antProject, workingDir, cmdLine, env, COMMAND_INPUT,
				Project.MSG_INFO).execute();
	}

	/**
	 * Deletes the specified label from the specified module.
	 * 
	 * @param workingDir the working directory
	 * @param module the module
	 * @param label the label
	 * @param antProject the ant project used for logging
	 */
	private void unlabelModule(String workingDir, VssModule module, String label, Project antProject) {
		antProject.log("Unlabel source path: " + module.getActualSrcPath(), Project.MSG_INFO);
		Commandline cmdLine = buildVssExecutable();
		cmdLine.createArgument().setValue("Label");
		cmdLine.createArgument().setValue(Luntbuild.concatPath("$", module.getActualSrcPath()));
		cmdLine.createArgument().setValue("-VL" + label);
		cmdLine.createArgument().setLine("-C- -I-Y -L");
		appendLoginInfo(cmdLine);
		Environment env = buildVssEnvironment();

		new MyExecTask("label", antProject, workingDir, cmdLine, env, COMMAND_INPUT, Project.MSG_INFO).execute();
	}

	/**
     * @inheritDoc
	 */
	public void checkoutActually(Build build, Project antProject) {
		String workingDir = build.getSchedule().getWorkDirRaw();
		// Because sourcesafe can not label based on current working
		// directory. In order to keep labeled code exactly the same as retrieved code, we
		// should label first
		Iterator it;
		if (!build.isRebuild() && build.getLabelStrategy() != com.luntsys.luntbuild.facades.Constants.LABEL_NONE) {
			it = getModules().iterator();
			while (it.hasNext()) {
				VssModule module = (VssModule) it.next();
				if (Luntbuild.isEmpty(module.getLabel())) {
					labelModule(workingDir, module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
				}
			}
		}

		// retrieve modules
		it = getModules().iterator();
		while (it.hasNext()) {
			VssModule module = (VssModule) Luntbuild.cloneModule(this, (Vcs.Module) it.next());
			if (Luntbuild.isEmpty(module.getLabel()) && build.getLabelStrategy() != com.luntsys.luntbuild.facades.Constants.LABEL_NONE)
				module.setLabel(Luntbuild.getLabelByVersion(build.getVersion()));
			if (build.isRebuild() || build.isCleanBuild())
				retrieveModule(workingDir, module, true, antProject);
			else
				retrieveModule(workingDir, module, false, antProject);
		}
	}

	/**
     * @inheritDoc
	 */
	public void label(Build build, Project antProject) {
		// does nothing
	}

	/**
     * @inheritDoc
	 */
	public void unlabel(Build build, Project antProject) {
		if (build.getLabelStrategy() != com.luntsys.luntbuild.facades.Constants.LABEL_NONE) {
			String workingDir = build.getSchedule().getWorkDirRaw();
			Iterator it = getModules().iterator();
			while (it.hasNext()) {
				VssModule module = (VssModule) it.next();
				if (Luntbuild.isEmpty(module.getLabel())) {
					unlabelModule(workingDir, module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
				}
			}
		}
	}

	/**
     * @inheritDoc
	 * @see VssModule
	 */
	public Module createNewModule() {
		return new VssModule();
	}

	/**
     * @inheritDoc
	 * @see VssModule
	 */
    public Module createNewModule(Module module) {
        return new VssModule((VssModule)module);
    }

    /**
     * @inheritDoc
     */
	public List getVcsSpecificProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Sourcesafe path";
			}

			public String getDescription() {
				return "The directory where your srcsafe.ini resides in. For example: \\\\machine1\\directory1. " +
						"You should use expicit hostname eg. \"machine1\", " +
						"not the ip address of the \"machine1\", or you should specify ip address in the Sourcesafe path.";
			}

			public String getValue() {
				return getVssPath();
			}

			public String getActualValue() {
				return getActualVssPath();
			}

			public void setValue(String value) {
				setVssPath(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Username";
			}

			public String getDescription() {
				return "User name to use to login the above sourcesafe database.";
			}

			public String getValue() {
				return getVssUser();
			}

			public void setValue(String value) {
				setVssUser(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Password";
			}

			public String getDescription() {
				return "Password for the above user name.";
			}

			public boolean isRequired() {
				return false;
			}

			public boolean isSecret() {
				return true;
			}

			public String getValue() {
				return getVssPassword();
			}

			public void setValue(String value) {
				setVssPassword(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Datetime format";
			}

			public String getDescription() {
				return "Specify the date/time format used for the Sourcesafe history command. " +
						"This property is optional. If left empty, Luntbuild will use \"M/dd/yy;h:mm:ssa\" as " +
						"the default value. The default value is suitable for English language operating systems that use US locale. Please refer to " +
						"the User's Guide for details how to specify this value if Luntbuild is running on a version of OS with different locale. ";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getDateTimeFormat();
			}

			public String getActualValue() {
				return getActualDateTimeFormat();
			}

			public void setValue(String value) {
				setDateTimeFormat(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Path for ss.exe";
			}

			public String getDescription() {
				return "The directory path, where your ss.exe file resides in. " +
						"It should be specified here, if it does not exist in the system path.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getSsDir();
			}

			public void setValue(String value) {
				setSsDir(value);
			}
		});
		return properties;
	}

    /**
     * Validates the modules of this VCS.
     *
     * @throws ValidationException if a module is not invalid
     */
	public void validateModules() {
		super.validateModules();
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			VssModule vssModule = (VssModule) it.next();
			if (vssModule.getActualSrcPath().startsWith("$"))
				throw new ValidationException("Property \"source path\" in module definition should not start with $");
		}
	}

	/**
	 * A SourceSafe module definition.
	 *
	 * @author robin shine
	 */
	public class VssModule extends Module {
		/**
		 * Keep tracks of version of this class, used when do serialization-deserialization
		 */
		static final long serialVersionUID = 1L;

		private String srcPath;
		private String label;
		private String destPath;

		/**
		 * Constructor, creates a blank SourceSafe module.
		 */
        public VssModule() {}

		/**
		 * Copy constructor, creates a SourceSafe module from another SourceSafe module.
		 * 
		 * @param module the module to create from
		 */
        public VssModule(VssModule module) {
            this.srcPath = module.srcPath;
            this.label = module.label;
            this.destPath = module.destPath;
        }

		/**
		 * Gets the source path.
		 * 
		 * @return the source path
		 */
		public String getSrcPath() {
			return srcPath;
		}

		/**
		 * Gets the source path. This method will parse OGNL variables.
		 * 
		 * @return the source path
		 */
        private String getActualSrcPath() {
			return OgnlHelper.evaluateScheduleValue(getSrcPath());
		}

		/**
		 * Sets the source path.
		 * 
		 * @param srcPath the source path
		 */
		public void setSrcPath(String srcPath) {
			this.srcPath = srcPath;
		}

		/**
		 * Gets the label to use.
		 * 
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * Gets the label to use. This method will parse OGNL variables.
		 * 
		 * @return the label
		 */
        private String getActualLabel() {
			return OgnlHelper.evaluateScheduleValue(getLabel());
		}

		/**
		 * Sets the label to use.
		 * 
		 * @param label the label
		 */
        private void setLabel(String label) {
			this.label = label;
		}

		/**
		 * Gets the destination path.
		 * 
		 * @return the destination path
		 */
		public String getDestPath() {
			return destPath;
		}

		/**
		 * Gets the destination path. This method will parse OGNL variables.
		 * 
		 * @return the destination path
		 */
        private String getActualDestPath() {
			return OgnlHelper.evaluateScheduleValue(getDestPath());
		}

		/**
		 * Sets the destination path.
		 * 
		 * @param destPath the destination path
		 */
		public void setDestPath(String destPath) {
			this.destPath = destPath;
		}

		/**
		 * @inheritDoc
		 */
		public List getProperties() {
			List properties = new ArrayList();
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "Source path";
				}

				public String getDescription() {
					return "Specify the path in the VSS repository, for example: \"testvss\", or \"/testvss\".\n" +
							"NOTE. You should not add $ in front of this path, in order to specify the whole repository, " +
							"you should just enter \"/\".";
				}

				public String getValue() {
					return getSrcPath();
				}

				public String getActualValue() {
					return getActualSrcPath();
				}

				public void setValue(String value) {
					setSrcPath(value);
				}
			});
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "Label";
				}

				public String getDescription() {
					return "Specify the label for the above source path. This property " +
							"is optional. If left empty, latest version " +
							"is assumed.";
				}

				public boolean isRequired() {
					return false;
				}

				public String getValue() {
					return getLabel();
				}

				public String getActualValue() {
					return getActualLabel();
				}

				public void setValue(String value) {
					setLabel(value);
				}
			});
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "Destination path";
				}

				public String getDescription() {
					return "Specify the destination directory relative to the project work directory, where " +
							" the contents under the above source path should be retrieved to. This property " +
							"is optional. If left empty, retrieved code will be put into directory defined by " +
							"the source path, relative to the project work directory.";
				}

				public boolean isRequired() {
					return false;
				}

				public String getValue() {
					return getDestPath();
				}

				public String getActualValue() {
					return getActualDestPath();
				}

				public void setValue(String value) {
					setDestPath(value);
				}
			});
			return properties;
		}

	    /**
	     * @inheritDoc
	     * @see VssModuleFacade
	     */
		public ModuleFacade getFacade() {
			VssModuleFacade facade = new VssModuleFacade();
			facade.setDestPath(getDestPath());
			facade.setLabel(getLabel());
			facade.setSrcPath(getSrcPath());
			return facade;
		}

	    /**
	     * @inheritDoc
	     * @throws RuntimeException if the facade is not an <code>VssModuleFacade</code>
	     * @see VssModuleFacade
	     */
		public void setFacade(ModuleFacade facade) {
			if (facade instanceof VssModuleFacade) {
				VssModuleFacade vssModuleFacade = (VssModuleFacade) facade;
				setLabel(vssModuleFacade.getLabel());
				setSrcPath(vssModuleFacade.getSrcPath());
				setDestPath(vssModuleFacade.getDestPath());
			} else
				throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		}
	}

	/**
     * @inheritDoc
	 */
	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		final Revisions revisions = new Revisions();
        revisions.addLog(this.getClass().getName(), toString());
        revisions.getChangeLogs().add("*************************************************************");
        revisions.getChangeLogs().add(toString());
        revisions.getChangeLogs().add("");

		Commandline cmdLine = buildVssExecutable();
		Environment env = buildVssEnvironment();
		final RevisionBlock block = new RevisionBlock();
        final Pattern folderPattern = Pattern.compile("^\\*\\*\\*\\*\\* (.*) \\*\\*\\*\\*$");
        final Pattern rootPattern = Pattern.compile("^\\*.*Version (.*) \\*.*$");
        final Pattern versionPattern = Pattern.compile("^Version (.*)$");
		final Pattern authorDatePattern = Pattern.compile("^User:(.*)Date:(.*)Time:(.*)");
        final Pattern commentPattern = Pattern.compile("^Comment:(.*)$");
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			final VssModule module = (VssModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel())) { // detect changes in head versions
				cmdLine.clearArgs();
				cmdLine.createArgument().setLine("HISTORY -I- -R -#" + Revisions.MAX_ENTRIES);
				appendLoginInfo(cmdLine);
				cmdLine.createArgument().setValue("-V~d" + formatDateForVss(sinceDate));
				cmdLine.createArgument().setValue(Luntbuild.concatPath("$", module.getActualSrcPath()));

				// provide some inputs to avoid command halts in case provided passwords is not correct
				new MyExecTask("history", antProject, null, cmdLine, env, COMMAND_INPUT, -1) {
					public void handleStdout(String line) {
						if (!revisions.isFileModified()) {
							if (line.endsWith("added"))
								revisions.setFileModified(true);
							else if (line.endsWith("Created"))
								revisions.setFileModified(true);
							else if (line.endsWith("deleted"))
								revisions.setFileModified(true);
							else if (line.endsWith("destroyed"))
								revisions.setFileModified(true);
							else if (line.startsWith("Checked in"))
								revisions.setFileModified(true);
							else if (line.endsWith("shared"))
								revisions.setFileModified(true);
							else if (line.endsWith("recovered"))
								revisions.setFileModified(true);
							else if (line.indexOf(" renamed to ") != -1)
								revisions.setFileModified(true);
						}
						if (line.matches("^\\*\\*\\*\\*\\*.*\\*\\*\\*\\*$")) {
							if (block.isValid()) {
								String version = "";
								String file_version = "";
								String author = "";
								Date date = null;
								String comment = "";
								String path = "";
								String action = "";
								boolean parseNextLine = false;
								Iterator itBlockLine = block.getLines().iterator();
								while (itBlockLine.hasNext()) {
									String blockLine = (String) itBlockLine.next();
									revisions.getChangeLogs().add(blockLine);
									if (blockLine.matches("^\\*\\*\\*\\*\\*.*\\*\\*\\*\\*$")) {
										Matcher foldermatcher = folderPattern.matcher(blockLine);
										Matcher rootmatcher = rootPattern.matcher(blockLine);
										if (foldermatcher.find()) {
											path = foldermatcher.group(1).trim();
										} else if (rootmatcher.find()) {
											path = Luntbuild.concatPath("$", module.getActualSrcPath());
											version = rootmatcher.group(1).trim();
										}
									} else if (parseNextLine) {
										parseNextLine = false;
										if (blockLine.endsWith("added")) {
											action = "A";
											path = Luntbuild.concatPath(path,
													blockLine.substring(0, blockLine.length() - 5).trim());
											file_version = "";
										}
										else if (blockLine.endsWith("Created")) {
											action = "C";
											revisions.setFileModified(true);
											file_version = "";
										}
										else if (blockLine.endsWith("deleted")) {
											action = "D";
											path = Luntbuild.concatPath(path,
													blockLine.substring(0, blockLine.length() - 7).trim());
											file_version = "";
										}
										else if (blockLine.endsWith("destroyed")) {
											action = "P";  // or Purged
											path = Luntbuild.concatPath(path,
													blockLine.substring(0, blockLine.length() - 9).trim());
											file_version = "";
										}
										else if (blockLine.startsWith("Checked in")) {
											action = "E";  // or Editted
											path = Luntbuild.concatPath(
													blockLine.substring(10, blockLine.length()).trim(), path);
											file_version = version;
										}
										else if (blockLine.endsWith("shared")) {
											action = "S";
											path = blockLine.substring(0, blockLine.length() - 6).trim();
											file_version = "";
										}
										else if (blockLine.endsWith("recovered")) {
											action = "O";  // or recOvered
											path = Luntbuild.concatPath(path,
													blockLine.substring(0, blockLine.length() - 9).trim());
											file_version = "";
										}
										else if (blockLine.indexOf(" renamed to ") != -1) {
											action = "R";
											revisions.setFileModified(true);
											file_version = "";
										}
									} else {
										Matcher versionmatcher = versionPattern.matcher(blockLine);
										Matcher authormatcher = authorDatePattern.matcher(blockLine);
										Matcher commentmatcher = commentPattern.matcher(blockLine);
										if (versionmatcher.find()) {
											version = versionmatcher.group(1).trim();
										} else if (authormatcher.find()) {
											author = authormatcher.group(1).trim();
											revisions.getChangeLogins().add(author);
											String date_text = authormatcher.group(2).trim() + ";" + authormatcher.group(3).trim();
						                	date = SynchronizedDateFormatter.parseDate(date_text, DEFAULT_DATETIME_FORMAT);
						                	if (date == null)
							                	throw new BuildException("Failed to parse date from VSS history: " + date_text);
											parseNextLine = true;
										} else if (commentmatcher.find()) {
											comment = commentmatcher.group(1).trim();
										} else {
											comment += blockLine.trim() + "\r\n";
										}
									}
								}
								revisions.addEntryToLastLog(version, author, date, comment);
								revisions.addPathToLastEntry(path, action, file_version);
							}
							block.setReady(true);
							block.setValid(true);
							block.getLines().clear();
							block.getLines().add(line);
						} else {
							if (block.isReady()) {
								block.getLines().add(line);
								if (line.startsWith("Labeled") || line.matches(".*pinned to.*"))
									block.setValid(false);
							}
						}
					}
				}.execute();
				if (block.isValid()) {
					String version = "";
					String file_version = "";
					String author = "";
					Date date = null;
					String comment = "";
					String path = "";
					String action = "";
					boolean parseNextLine = false;
					Iterator itBlockLine = block.getLines().iterator();
					while (itBlockLine.hasNext()) {
						String blockLine = (String) itBlockLine.next();
						revisions.getChangeLogs().add(blockLine);
						if (blockLine.matches("^\\*\\*\\*\\*\\*.*\\*\\*\\*\\*$")) {
							Matcher foldermatcher = folderPattern.matcher(blockLine);
							Matcher rootmatcher = rootPattern.matcher(blockLine);
							if (foldermatcher.find()) {
								path = foldermatcher.group(1).trim();
							} else if (rootmatcher.find()) {
								path = Luntbuild.concatPath("$", module.getActualSrcPath());
								version = rootmatcher.group(1).trim();
							}
						} else if (parseNextLine) {
							parseNextLine = false;
							if (blockLine.endsWith("added")) {
								action = "A";
								path = Luntbuild.concatPath(path,
										blockLine.substring(0, blockLine.length() - 5).trim());
								file_version = "";
							}
							else if (blockLine.endsWith("Created")) {
								action = "C";
								revisions.setFileModified(true);
								file_version = "";
							}
							else if (blockLine.endsWith("deleted")) {
								action = "D";
								path = Luntbuild.concatPath(path,
										blockLine.substring(0, blockLine.length() - 7).trim());
								file_version = "";
							}
							else if (blockLine.endsWith("destroyed")) {
								action = "P";  // or Purged
								path = Luntbuild.concatPath(path,
										blockLine.substring(0, blockLine.length() - 9).trim());
								file_version = "";
							}
							else if (blockLine.startsWith("Checked in")) {
								action = "E";  // or Editted
								path = Luntbuild.concatPath(
										blockLine.substring(10, blockLine.length()).trim(), path);
								file_version = version;
							}
							else if (blockLine.endsWith("shared")) {
								action = "S";
								path = blockLine.substring(0, blockLine.length() - 6).trim();
								file_version = "";
							}
							else if (blockLine.endsWith("recovered")) {
								action = "O";  // or recOvered
								path = Luntbuild.concatPath(path,
										blockLine.substring(0, blockLine.length() - 9).trim());
								file_version = "";
							}
							else if (blockLine.indexOf(" renamed to ") != -1) {
								action = "R";
								revisions.setFileModified(true);
								file_version = "";
							}
						} else {
							Matcher versionmatcher = versionPattern.matcher(blockLine);
							Matcher authormatcher = authorDatePattern.matcher(blockLine);
							Matcher commentmatcher = commentPattern.matcher(blockLine);
							if (versionmatcher.find()) {
								version = versionmatcher.group(1).trim();
							} else if (authormatcher.find()) {
								author = authormatcher.group(1).trim();
								revisions.getChangeLogins().add(author);
								String date_text = authormatcher.group(2).trim() + ";" + authormatcher.group(3).trim();
			                	date = SynchronizedDateFormatter.parseDate(date_text, DEFAULT_DATETIME_FORMAT);
			                	if (date == null)
				                	throw new BuildException("Failed to parse date from VSS history: " + date_text);
								parseNextLine = true;
							} else if (commentmatcher.find()) {
								comment = commentmatcher.group(1).trim();
							} else {
								comment += blockLine.trim() + "\r\n";
							}
						}
					}
					revisions.addEntryToLastLog(version, author, date, comment);
					revisions.addPathToLastEntry(path, action, file_version);
				}
				block.setReady(false);
				block.setValid(true);
				block.getLines().clear();
			}
		}
		return revisions;
	}

	/**
	 * Constructs the executable part of a commandline object.
	 * 
	 * @return the commandline object
	 */
	protected Commandline buildVssExecutable() {
		Commandline cmdLine = new Commandline();
		if (Luntbuild.isEmpty(getSsDir()))
			cmdLine.setExecutable("ss");
		else
			cmdLine.setExecutable(Luntbuild.concatPath(getSsDir(), "ss"));
		return cmdLine;
	}

	/**
	 * Appends login information to the specified commandline object.
	 *
	 * @param cmdLine the commandline object to add to
	 */
	protected void appendLoginInfo(Commandline cmdLine) {
		if (Luntbuild.isEmpty(getVssPassword()))
			cmdLine.createArgument().setValue("-Y" + getVssUser());
		else {
			Commandline.Argument arg = cmdLine.createArgument();
			arg.setValue("-Y" + getVssUser() + "," + getVssPassword());
			arg.setDescriptiveValue("-Y" + getVssUser() + ",******");
		}
	}

	/**
	 * Builds the necessary environment variables needed to run ss.exe, this typically includes
	 * "SSDIR".
	 *
	 * @return the necessary environment variables
	 */
	protected Environment buildVssEnvironment() {
		Environment env = new Environment();
		Environment.Variable var = new Environment.Variable();
		var.setKey("SSDIR");
		var.setValue(getActualVssPath());
		env.addVariable(var);
		return env;
	}

	/**
	 * Formats the specified date to use in the SourceSafe command line.
	 *
	 * @param date the date to format
	 * @return the formatted date
	 */
	protected synchronized String formatDateForVss(Date date) {
		String pattern = DEFAULT_DATETIME_FORMAT;
		if (!Luntbuild.isEmpty(getDateTimeFormat()))
			pattern = getActualDateTimeFormat();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		DateFormatSymbols dfs = new DateFormatSymbols();
		dfs.setAmPmStrings(new String[]{"a", "p"});
		sdf.setDateFormatSymbols(dfs);
		return sdf.format(date);
	}

    /**
     * @inheritDoc
     * @see VssAdaptorFacade
     */
	public void saveToFacade(VcsFacade facade) {
    	// TODO throw RuntimeException if the facade is not the right class
		VssAdaptorFacade vssFacade = (VssAdaptorFacade) facade;
		vssFacade.setDateTimeFormat(getDateTimeFormat());
		vssFacade.setSsDir(getSsDir());
		vssFacade.setVssPassword(getVssPassword());
		vssFacade.setVssPath(getVssPath());
		vssFacade.setVssUser(getVssUser());
	}

    /**
     * @inheritDoc
     * @throws RuntimeException if the facade is not an <code>VssAdaptorFacade</code>
     * @see VssAdaptorFacade
     */
	public void loadFromFacade(VcsFacade facade) {
		if (!(facade instanceof VssAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		VssAdaptorFacade vssFacade = (VssAdaptorFacade) facade;
		setDateTimeFormat(vssFacade.getDateTimeFormat());
		setSsDir(vssFacade.getSsDir());
		setVssPassword(vssFacade.getVssPassword());
		setVssPath(vssFacade.getVssPath());
		setVssUser(vssFacade.getVssUser());
	}

    /**
     * @inheritDoc
     * @see VssAdaptorFacade
     */
	public VcsFacade constructFacade() {
		return new VssAdaptorFacade();
	}
}
