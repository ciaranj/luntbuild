/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-4-26
 * Time: 11:38:40
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
import com.luntsys.luntbuild.ant.cvstask.CVSPass;
import com.luntsys.luntbuild.ant.cvstask.Cvs;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.lb12.ModuleFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.utility.*;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.FixCRLF;
import org.apache.tools.ant.types.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * adaptor to cvs version control system. It will be serialized by hibernate
 *
 * @author robin shine
 */
public class CvsAdaptor extends Vcs {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;
	private static final String passwdFileName = ".cvspass";
	private static final SimpleDateFormat CMD_DATE_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String cvsRoot;
	private String cvsPassword;
	/**
	 * Determines if the cvs executable being used is a cygwin one?
	 */
	private String cygwinCvs;

	/**
	 * Determines if the -S option for log command should be disabled
	 */
	private String disableSuppressOption;

	/**
	 * Determines if the history command should be disabled when check revisions
	 */
	private String disableHistoryCmd;

	private String cvsDir;

	public CvsAdaptor() {
		setQuietPeriod("60");
	}

	/**
	 * set the cvs root of cvs repository
	 *
	 * @param cvsRoot the cvs root string, an example can be ":pserver:anoncvs@cvs.pmease.com:/home/cvspublic"
	 */
	public void setCvsRoot(String cvsRoot) {
		this.cvsRoot = cvsRoot;
	}

	/**
	 * return the cvs root string
	 */
	public String getCvsRoot() {
		return cvsRoot;
	}

	public String getCvsDir() {
		return cvsDir;
	}

	public void setCvsDir(String cvsDir) {
		this.cvsDir = cvsDir;
	}

	/**
	 * set the cvs password
	 *
	 * @param cvsPassword password for the cvs repository
	 */
	public void setCvsPassword(String cvsPassword) {
		this.cvsPassword = cvsPassword;
	}

	/**
	 * return the cvs repository password
	 */
	public String getCvsPassword() {
		return cvsPassword;
	}

	public String getCygwinCvs() {
		return cygwinCvs;
	}

	public boolean isCygwinCvs() {
		return !Luntbuild.isEmpty(getCygwinCvs()) && getCygwinCvs().equalsIgnoreCase("yes");
	}

	public void setCygwinCvs(String cygwinCvs) {
		this.cygwinCvs = cygwinCvs;
	}

	/**
	 * Get a string indicates whether or not to disable -S option for log command
	 *
	 * @return "yes", or "no" or empty. An empty value will implies the value "no"
	 */
	public String getDisableSuppressOption() {
		return disableSuppressOption;
	}

	public void setDisableSuppressOption(String disableSuppressOption) {
		this.disableSuppressOption = disableSuppressOption;
	}

	public boolean isDisableSuppressOption() {
		return !Luntbuild.isEmpty(getDisableSuppressOption()) && getDisableSuppressOption().equalsIgnoreCase("yes");
	}

	/**
	 * Get a string indicates whether or not to disable history command when check
	 * revisions
	 *
	 * @return "yes" or "no" or empty. An empty value will implies the value "no"
	 */
	public String getDisableHistoryCmd() {
		return disableHistoryCmd;
	}

	public void setDisableHistoryCmd(String disableHistoryCmd) {
		this.disableHistoryCmd = disableHistoryCmd;
	}

	public boolean isDisableHistoryCmd() {
		return !Luntbuild.isEmpty(getDisableHistoryCmd()) && getDisableHistoryCmd().equalsIgnoreCase("yes");
	}

	public String getDisplayName() {
		return "Cvs";
	}

	public String getIconName() {
		return "cvs.jpg";
	}

	/**
	 * login to the cvs server
	 *
	 * @param antProject
	 * @throws BuildException
	 */
	private void login(Project antProject) {
		// call ant CVSPass task to login into cvs server
		antProject.log("Login to cvs...", Project.MSG_INFO);

		com.luntsys.luntbuild.ant.cvstask.CVSPass cvsPassTask = new CVSPass();
		cvsPassTask.setProject(antProject);
		cvsPassTask.setCvsroot(getCvsRoot());
		cvsPassTask.setPassword(getCvsPassword());
		cvsPassTask.setPassfile(new File(Luntbuild.installDir + "/" + passwdFileName));
		cvsPassTask.setTaskType("CVSPass");
		cvsPassTask.setTaskName("CVSPass");
		cvsPassTask.execute();

		// adjust eol of cvs pass file
		if (isCygwinCvs()) {
			FixCRLF fixCRLF = new FixCRLF();
			fixCRLF.setProject(antProject);
			fixCRLF.setTaskName("fixCRLF");
			fixCRLF.setTaskType("fixCRLF");
			fixCRLF.setSrcdir(new File(Luntbuild.installDir));
			fixCRLF.setIncludes(".cvspass");
			FixCRLF.CrLf crLf = new FixCRLF.CrLf();
			crLf.setValue("unix");
			fixCRLF.setEol(crLf);
			FixCRLF.AddAsisRemove addAsisRemove = new FixCRLF.AddAsisRemove();
			addAsisRemove.setValue("remove");
			fixCRLF.setEof(addAsisRemove);
			fixCRLF.execute();
		}

		// cause the ant task CVSPass have a bug in windows system, we should go
		// longer to pick up the encrypted password and call a external program to save
		// it to windows registry
		if (System.getProperty("os.name").startsWith("Windows")) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(Luntbuild.installDir + "/" + passwdFileName));
				String line;
				while ((line = reader.readLine()) != null) {
					int index = line.indexOf(' ');
					String currentCvsRoot;
					String currentEncryptedPasswd;
					if (index == -1) {
						currentCvsRoot = line;
						currentEncryptedPasswd = "";
					} else {
						currentCvsRoot = line.substring(0, index);
						currentEncryptedPasswd = line.substring(index + 1);
					}
					if (currentCvsRoot.equals(getCvsRoot())) {
						String cvsntPasswdExe = new File(Luntbuild.installDir + "/osdependent/cvsnt_passwd").getCanonicalPath();
						Commandline cmdLine = new Commandline();
						cmdLine.setExecutable(cvsntPasswdExe);
						cmdLine.createArgument().setValue(getCvsRoot());
						Commandline.Argument arg = cmdLine.createArgument();
						arg.setValue(currentEncryptedPasswd.replaceAll("\"", "\\\\\""));
						arg.setDescriptiveValue("******");
						new MyExecTask("cvsnt_passwd", antProject, cmdLine, Project.MSG_INFO).execute();
						return;
					}
				}
			} catch (IOException e) {
				throw new BuildException("ERROR: " + e.getMessage());
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						throw new BuildException("ERROR: Failed to close password file: " + e.getMessage());
					}
			}
			// we should not go this far
			antProject.log("ERROR: Failed to find password for CVSROOT \"" +
					getCvsRoot() + "\" in password file!", Project.MSG_ERR);
		}
	}

	/**
	 * Build the executable part of a commandline object
	 *
	 * @return
	 */
	protected Commandline buildCvsExecutable() {
		Commandline cmdLine = new Commandline();
		if (Luntbuild.isEmpty(getCvsDir()))
			cmdLine.setExecutable("cvs");
		else
			cmdLine.setExecutable(Luntbuild.concatPath(getCvsDir(), "cvs"));
		return cmdLine;
	}

	public void validateProperties() {
		super.validateProperties();
		if (!Luntbuild.isEmpty(getCygwinCvs())) {
			if (!getCygwinCvs().equalsIgnoreCase("yes") && !getCygwinCvs().equalsIgnoreCase("no"))
				throw new ValidationException("Only \"yes\" or \"no\" value can be specified " +
						"for \"is cygwin cvs\" property!");
		}
		if (!Luntbuild.isEmpty(getDisableSuppressOption())) {
			if (!getDisableSuppressOption().equalsIgnoreCase("yes") && !getDisableSuppressOption().equalsIgnoreCase("no"))
				throw new ValidationException("Only \"yes\" or \"no\" value can be specified " +
						"for \"disable -S option for log command\" property!");
		}
		if (!Luntbuild.isEmpty(getDisableHistoryCmd())) {
			if (!getDisableHistoryCmd().equalsIgnoreCase("yes") && !getDisableHistoryCmd().equalsIgnoreCase("no"))
				throw new ValidationException("Only \"yes\" or \"no\" value can be specified " +
						"for \"disable history command\" property!");
		}
	}

	private String normalizeModulePath(String modulePath) {
		return Luntbuild.removeTrailingSlash(Luntbuild.removeLeadingSlash(modulePath.replace('\\', '/')));
	}

	/**
	 * Method may throw BuildException to indicates module acquisition error
	 *
	 * @param workingDir
	 * @param module
	 * @param antProject
	 */
	private void retrieveModule(String workingDir, CvsModule module, boolean isClean, Project antProject) {
		if (isClean)
			antProject.log("Retrieve source path: " + module.getSrcPath(), Project.MSG_INFO);
		else
			antProject.log("Update source path: " + module.getSrcPath(), Project.MSG_INFO);

		if (isClean)
			Luntbuild.deleteDir(workingDir + "/" + module.getSrcPath());

		// call ant cvs task to retrieve module
		Cvs cvsTask = new Cvs();
		cvsTask.setProject(antProject);
		cvsTask.setCommand("checkout -P");
		cvsTask.setCvsRoot(getCvsRoot());
		cvsTask.setPackage(normalizeModulePath(module.getSrcPath()));
		cvsTask.setPassfile(new File(Luntbuild.installDir + "/" + passwdFileName));
		cvsTask.setCvsDir(getCvsDir());

		// label takes precedence of branch if both of them are not empty
		if (!Luntbuild.isEmpty(module.getLabel()))
			cvsTask.setTag(module.getLabel());
		else if (!Luntbuild.isEmpty(module.getBranch()))
			cvsTask.setTag(module.getBranch());

		cvsTask.setDest(new File(workingDir));
		LuntbuildLogger luntBuildLogger = Luntbuild.getLuntBuildLogger(antProject);
		if (luntBuildLogger == null || luntBuildLogger.getMessageOutputLevel() <= Project.MSG_INFO)
			cvsTask.setReallyquiet(true);
		cvsTask.setFailOnError(true);
		cvsTask.setTaskType("Cvs");
		cvsTask.setTaskName("Cvs");
		cvsTask.execute();
	}

	/**
	 * Method may throw a BuildException to indicates a labeling error
	 *
	 * @param workingDir
	 * @param module
	 * @param label
	 * @param antProject
	 */
	private void labelModule(String workingDir, CvsModule module, String label, Project antProject) {
		// call ant cvs task to perform code labeling
		antProject.log("Label source path: " + module.getSrcPath(), Project.MSG_INFO);
		Cvs cvsTask = new Cvs();
		cvsTask.setProject(antProject);
		cvsTask.setCommand("tag " + label);
		cvsTask.setCvsRoot(getCvsRoot());
		cvsTask.setPassfile(new File(Luntbuild.installDir + "/" + passwdFileName));
		cvsTask.setCvsDir(getCvsDir());

		cvsTask.setDest(new File(Luntbuild.concatPath(workingDir, module.getSrcPath())));
		LuntbuildLogger luntBuildLogger = Luntbuild.getLuntBuildLogger(antProject);
		if (luntBuildLogger == null || luntBuildLogger.getMessageOutputLevel() <= Project.MSG_INFO)
			cvsTask.setReallyquiet(true);
		cvsTask.setFailOnError(true);
		cvsTask.setTaskType("Cvs");
		cvsTask.setTaskName("Cvs");
		cvsTask.execute();
	}

	public void checkoutActually(Build build, Project antProject) {
		String workingDir = build.getSchedule().getWorkDirRaw();
		if (getCvsRoot().startsWith(":pserver:"))
			login(antProject);

		// retrieve modules
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			CvsAdaptor.CvsModule module = (CvsAdaptor.CvsModule) Luntbuild.cloneModule(this, (Vcs.Module) it.next());
			if (build.isRebuild() && Luntbuild.isEmpty(module.getLabel()))
				module.setLabel(Luntbuild.getLabelByVersion(build.getVersion()));
			if (build.isRebuild() || build.isCleanBuild())
				retrieveModule(workingDir, module, true, antProject);
			else
				retrieveModule(workingDir, module, false, antProject);
		}
	}

	public void label(Build build, Project antProject) {
		String workingDir = build.getSchedule().getWorkDirRaw();
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			CvsModule module = (CvsModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel()))
				labelModule(workingDir, module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
		}
	}

	public Vcs.Module createNewModule() {
		return new CvsModule();
	}

    public Vcs.Module createNewModule(Vcs.Module module) {
        return new CvsModule((CvsModule)module);
    }

	/**
	 * Override default implementation in order to speed up quiet detection for CVS adaptor
	 *
	 * @param sinceDate
	 * @param workingSchedule
	 * @param antProject
	 * @return
	 */
	public boolean isVcsQuietSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		String workingDir = workingSchedule.getWorkDirRaw();
		if (getCvsRoot().startsWith(":pserver:"))
			login(antProject);
		Environment envs = new Environment();
		Environment.Variable var = new Environment.Variable();
		var.setKey("CVS_PASSFILE");
		try {
			var.setValue(new File(Luntbuild.installDir + "/" + passwdFileName).getCanonicalPath());
		} catch (IOException e) {
			throw new BuildException("Failed to get canonical path for cvs pass file!", e);
		}
		envs.addVariable(var);
		Commandline cmdLine = buildCvsExecutable();

		if (!isDisableHistoryCmd()) {
			final boolean commitsHappened[] = new boolean[1];
			commitsHappened[0] = true;
			cmdLine.clearArgs();
			cmdLine.createArgument().setValue("-d" + getCvsRoot());
			cmdLine.createArgument().setLine("-q history -c -a");
			cmdLine.createArgument().setValue("-D" + CMD_DATE_FORMAT.format(sinceDate));
			new MyExecTask("history", antProject, null, cmdLine, envs, null, -1) {
				public void handleStdout(String line) {
					if (line.equals("No records selected."))
						commitsHappened[0] = false;
				}
			}.execute();
			if (!commitsHappened[0])
				return true;
			else
				return false;
		}

		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			CvsModule module = (CvsModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel())) { // necessary to check revisions
				// prepare working directory to run log command
				try {
					// first try update working directory
					retrieveModule(workingDir, module, false, antProject);
				} catch (BuildException e) {
					retrieveModule(workingDir, module, true, antProject);
				}

				cmdLine.clearArgs();
				cmdLine.createArgument().setValue("-d" + getCvsRoot());
				if (isDisableSuppressOption())
					cmdLine.createArgument().setLine("-q log -N");
				else
					cmdLine.createArgument().setLine("-q log -S -N");
				cmdLine.createArgument().setValue("-d>" + CMD_DATE_FORMAT.format(sinceDate));
				if (Luntbuild.isEmpty(module.getBranch()))
					cmdLine.createArgument().setValue("-b");
				else
					cmdLine.createArgument().setValue("-r" + module.getBranch());

				final Revisions revisions = new Revisions();
				// initialize blocks to help parse output when -S option is disabled for log command
				final RevisionBlock block = new RevisionBlock();
				block.setValid(false);
				block.setReady(false);
				new MyExecTask("log", antProject, Luntbuild.concatPath(workingDir, module.getSrcPath()),
						cmdLine, envs, null, -1) {
					public void handleStdout(String line) {
						if (isDisableSuppressOption()) {
							if (line.startsWith("RCS file:")) {
								if (block.isValid())
									revisions.setFileModified(true);
								block.setReady(true);
								block.setValid(false);
							} else {
								if (block.isReady()) {
									if (line.startsWith("revision "))
										block.setValid(true);
								}
							}
						} else {
							if (!revisions.isFileModified() && line.toLowerCase().startsWith("revision"))
								revisions.setFileModified(true);
						}
					}
				}.execute();
				if (isDisableSuppressOption()) {
					if (block.isValid())
						revisions.setFileModified(true);
				}
				if (revisions.isFileModified())
					return false;
			}
		}
		return true;
	}

	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		String workingDir = workingSchedule.getWorkDirRaw();
		if (getCvsRoot().startsWith(":pserver:"))
			login(antProject);
		final Revisions revisions = new Revisions();
		Environment envs = new Environment();
		Environment.Variable var = new Environment.Variable();
		var.setKey("CVS_PASSFILE");
		try {
			var.setValue(new File(Luntbuild.installDir + "/" + passwdFileName).getCanonicalPath());
		} catch (IOException e) {
			throw new BuildException("Failed to get canonical path for cvs pass file!", e);
		}
		envs.addVariable(var);
		Commandline cmdLine = buildCvsExecutable();
		if (!isDisableHistoryCmd()) {
			antProject.log("History command enabled.");
			final boolean commitsHappened[] = new boolean[1];
			commitsHappened[0] = true;
			cmdLine.createArgument().setValue("-d" + getCvsRoot());
			cmdLine.createArgument().setLine("-q history -c -a");
			cmdLine.createArgument().setValue("-D" + CMD_DATE_FORMAT.format(sinceDate));
			new MyExecTask("history", antProject, null, cmdLine, envs, null, -1) {
				public void handleStdout(String line) {
					if (line.equals("No records selected."))
						commitsHappened[0] = false;
				}
			}.execute();
			if (!commitsHappened[0]) {
				antProject.log("Commits not found in repository, no need to run log command.");
				return revisions;
			}
			antProject.log("Commits found in module history, further check with log command...");
		}

		final Pattern authorPattern = Pattern.compile(".*author:(.*);.*state:.*");

		// initialize blocks to help parse output when -S option is disabled for log command
		final RevisionBlock block = new RevisionBlock();
		block.setValid(false);
		block.setReady(false);
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			CvsModule module = (CvsModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel())) { // necessary to check revisions
				antProject.log("Getting revisions for module \"" + module.getSrcPath() + "\"...");

				// prepare working directory to run log command
				antProject.log("Prepares the work directory for running of the log command...");
               if (workingSchedule.getBuildType() == Constants.BUILD_TYPE_CLEAN) {
                    retrieveModule(workingDir, module, true, antProject);
                } else {
                    try {
                        // first try update working directory
                        retrieveModule(workingDir, module, false, antProject);
                    } catch (BuildException e) {
                        retrieveModule(workingDir, module, true, antProject);
                    }
                }

				cmdLine.clearArgs();
				cmdLine.createArgument().setValue("-d" + getCvsRoot());
				if (isDisableSuppressOption())
					cmdLine.createArgument().setLine("-q log -N");
				else
					cmdLine.createArgument().setLine("-q log -S -N");
				cmdLine.createArgument().setValue("-d>" + CMD_DATE_FORMAT.format(sinceDate));
				if (Luntbuild.isEmpty(module.getBranch()))
					cmdLine.createArgument().setValue("-b");
				else
					cmdLine.createArgument().setValue("-r" + module.getBranch());
				antProject.log("Run log command to get revisions for the current module...");
				new MyExecTask("log", antProject, Luntbuild.concatPath(workingDir, module.getSrcPath()),
						cmdLine, envs, null, -1) {
					public void handleStdout(String line) {
						if (isDisableSuppressOption()) {
							if (line.startsWith("RCS file:")) {
								if (block.isValid()) {
									revisions.setFileModified(true);
									Iterator itBlockLine = block.getLines().iterator();
									while (itBlockLine.hasNext()) {
										String blockLine = (String) itBlockLine.next();
										revisions.getChangeLogs().add(blockLine);
										Matcher matcher = authorPattern.matcher(blockLine);
										if (matcher.find())
											revisions.getChangeLogins().add(matcher.group(1).trim());
									}
								}
								block.setReady(true);
								block.setValid(false);
								block.getLines().clear();
								block.getLines().add(line);
							} else {
								if (block.isReady()) {
									block.getLines().add(line);
									if (line.startsWith("revision "))
										block.setValid(true);
								}
							}
						} else {
							if (!line.startsWith("? ")) {
								revisions.getChangeLogs().add(line);
								Matcher matcher = authorPattern.matcher(line);
								if (matcher.find())
									revisions.getChangeLogins().add(matcher.group(1).trim());
							}
							if (!revisions.isFileModified() && line.toLowerCase().startsWith("revision"))
								revisions.setFileModified(true);
						}
					}
				}.execute();
				if (isDisableSuppressOption()) {
					if (block.isValid()) {
						revisions.setFileModified(true);
						Iterator itBlockLine = block.getLines().iterator();
						while (itBlockLine.hasNext()) {
							String blockLine = (String) itBlockLine.next();
							revisions.getChangeLogs().add(blockLine);
							Matcher matcher = authorPattern.matcher(blockLine);
							if (matcher.find())
								revisions.getChangeLogins().add(matcher.group(1).trim());
						}
					}
					block.setReady(false);
					block.setValid(false);
					block.getLines().clear();
				}
			}
		}
		return revisions;
	}

	public List getVcsSpecificProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Cvs root";
			}

			public String getDescription() {
				return "The Cvs root for this project, for example, :pserver:administrator@localhost:d:/cvs_repository. " +
						"If you are using ssh, the :ext: protocol will need to be specified, " +
						"and proper environment need to be setup outside of Luntbuild system. " +
						"Please refer to your Cvs User's Guide for details.";
			}

			public String getValue() {
				return getCvsRoot();
			}

			public void setValue(String value) {
				setCvsRoot(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Cvs password";
			}

			public String getDescription() {
				return "The Cvs password for above Cvs root if connecting using pserver protocol.";
			}

			public boolean isRequired() {
				return false;
			}

			public boolean isSecret() {
				return true;
			}

			public String getValue() {
				return getCvsPassword();
			}

			public void setValue(String value) {
				setCvsPassword(value);
			}
		});
		DisplayProperty p = new DisplayProperty() {
			public String getDisplayName() {
				return "Is cygwin cvs?";
			}

			public String getDescription() {
				return "This property indicates whether or not the cvs executable being used is a cygwin one.";
			}

			public boolean isRequired() {
				return false;
			}

			public boolean isSelect() {
				return true;
			}

			public String getValue() {
				return getCygwinCvs();
			}

			public void setValue(String value) {
				setCygwinCvs(value);
			}
		};
		// Create selection model
		IPropertySelectionModel model = new CvsYesNoSelectionModel();
		// Set selection model
		p.setSelectionModel(model);
		// Add property to properties list
		properties.add(p);
		p = new DisplayProperty() {
			public String getDisplayName() {
				return "Disable \"-S\" option for log command?";
			}

			public String getDescription() {
				return "This property indicates whether or not the \"-S\" option for the log command should be disabled." +
						"The -S option used in the log command can speed up modification detection, however " +
						"some earlier versions of Cvs do not support this option. In this case you should disable it. ";
			}

			public boolean isRequired() {
				return false;
			}

			public boolean isSelect() {
				return true;
			}

			public String getValue() {
				return getDisableSuppressOption();
			}

			public void setValue(String value) {
				setDisableSuppressOption(value);
			}
		};
		// Create selection model
		model = new CvsYesNoSelectionModel();
		// Set selection model
		p.setSelectionModel(model);
		// Add property to properties list
		properties.add(p);

		p = new DisplayProperty() {
			public String getDisplayName() {
				return "Disable history command?";
			}

			public String getDescription() {
				return "This property indicates whether or not to disable the history command when performing modification detection. " +
						"Using the history command in conjunction with the log command can speed up modification detection, " +
						"however some Cvs repositories may not hold history information of commits. In this case you should disable it. ";
			}

			public boolean isRequired() {
				return false;
			}

			public boolean isSelect() {
				return true;
			}

			public String getValue() {
				return getDisableHistoryCmd();
			}

			public void setValue(String value) {
				setDisableHistoryCmd(value);
			}
		};
		// Create selection model
		model = new CvsYesNoSelectionModel();
		// Set selection model
		p.setSelectionModel(model);
		// Add property to properties list
		properties.add(p);
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Path for cvs executable";
			}

			public String getDescription() {
				return "The directory path, where your cvs executable file resides in. " +
						"It should be specified here, if it does not exist in the system path.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getCvsDir();
			}

			public void setValue(String value) {
				setCvsDir(value);
			}
		});
		return properties;
	}

	class CvsYesNoSelectionModel implements IPropertySelectionModel {
		String[] values = {"no", "yes"};

		public int getOptionCount() {
			return this.values.length;
		}

		public Object getOption(int index) {
			return this.values[index];
		}

		public String getLabel(int index) {
			return this.values[index];
		}

		public String getValue(int index) {
			return this.values[index];
		}

		public Object translateValue(String value) {
			return value;
		}
	}

	public class CvsModule extends Module {
		/**
		 * Keep tracks of version of this class, used when do serialization-deserialization
		 */
		static final long serialVersionUID = 1;

		private String srcPath;
		private String branch;
		private String label;

        public CvsModule() {}

        public CvsModule(CvsModule module) {
            this.srcPath = module.srcPath;
            this.branch = module.branch;
            this.label = module.label;
        }

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

		public List getProperties() {
			List properties = new ArrayList();
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "Source path";
				}

				public String getDescription() {
					return "Specify a path to retrieve from the Cvs repository, for example: testcvs/src.";
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
					return "Branch";
				}

				public String getDescription() {
					return "Specify the branch for the above source path. This property " +
							"is optional. When left empty, main branch is assumed.";
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
					return "Label";
				}

				public String getDescription() {
					return "Specify the label for the above source path. This property is optional. " +
							"If specified, it will take preference over branch. When left empty, latest " +
							"version of the specified branch will be retrieved.";
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
			return properties;
		}

		public ModuleFacade getFacade() {
			com.luntsys.luntbuild.facades.lb12.CvsModuleFacade facade = new com.luntsys.luntbuild.facades.lb12.CvsModuleFacade();
			facade.setBranch(getBranch());
			facade.setLabel(getLabel());
			facade.setSrcPath(getSrcPath());
			return facade;
		}

		public void setFacade(ModuleFacade facade) {
			if (facade instanceof com.luntsys.luntbuild.facades.lb12.CvsModuleFacade) {
				com.luntsys.luntbuild.facades.lb12.CvsModuleFacade cvsModuleFacade = (com.luntsys.luntbuild.facades.lb12.CvsModuleFacade) facade;
				setBranch(cvsModuleFacade.getBranch());
				setLabel(cvsModuleFacade.getLabel());
				setSrcPath(cvsModuleFacade.getSrcPath());
			} else
				throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		}
	}

	public void saveToFacade(VcsFacade facade) {
		com.luntsys.luntbuild.facades.lb12.CvsAdaptorFacade cvsFacade = (com.luntsys.luntbuild.facades.lb12.CvsAdaptorFacade) facade;
		cvsFacade.setCvsRoot(getCvsRoot());
		cvsFacade.setCvsPassword(getCvsPassword());
		cvsFacade.setCygwinCvs(getCygwinCvs());
		cvsFacade.setDisableHistoryCmd(getDisableHistoryCmd());
		cvsFacade.setDisableSuppressOption(getDisableSuppressOption());
		cvsFacade.setCvsDir(getCvsDir());
	}

	public void loadFromFacade(com.luntsys.luntbuild.facades.lb12.VcsFacade facade) {
		if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.CvsAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		com.luntsys.luntbuild.facades.lb12.CvsAdaptorFacade cvsFacade = (com.luntsys.luntbuild.facades.lb12.CvsAdaptorFacade) facade;
		setCvsRoot(cvsFacade.getCvsRoot());
		setCvsPassword(cvsFacade.getCvsPassword());
		setCygwinCvs(cvsFacade.getCygwinCvs());
		setDisableHistoryCmd(cvsFacade.getDisableHistoryCmd());
		setDisableSuppressOption(cvsFacade.getDisableSuppressOption());
		setCvsDir(cvsFacade.getCvsDir());
	}

	public com.luntsys.luntbuild.facades.lb12.VcsFacade constructFacade() {
		return new com.luntsys.luntbuild.facades.lb12.CvsAdaptorFacade();
	}
}
