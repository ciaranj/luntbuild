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

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.facades.lb12.VssAdaptorFacade;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.utility.*;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Environment;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Microsoft visual source safe adaptor
 *
 * @author robin shine
 */
public class VssAdaptor extends Vcs {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;
	public static final String DEFAULT_DATETIME_FORMAT = "M/dd/yy;h:mm:ssa";
	// the input feed to any vss commands to avoid command hang
	public static final String COMMAND_INPUT = "\n\n\n\n\n\n\n\n";
	private String vssPath; // path to srcsafe.ini file
	private String vssUser; // user name
	private String vssPassword; // password
	private String dateTimeFormat; // datetime format for ss HISTORY command
	private String ssDir; // path to ss.exe file, if blank, ss.exe is expected to be in the PATH environment variable

	public String getVssPath() {
		return vssPath;
	}

	public void setVssPath(String vssPath) {
		this.vssPath = vssPath;
	}

	public String getVssUser() {
		return vssUser;
	}

	public void setVssUser(String vssUser) {
		this.vssUser = vssUser;
	}

	public String getVssPassword() {
		return vssPassword;
	}

	public void setVssPassword(String vssPassword) {
		this.vssPassword = vssPassword;
	}

	public String getSsDir() {
		return ssDir;
	}

	public void setSsDir(String ssDir) {
		this.ssDir = ssDir;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	public String getDisplayName() {
		return "Visual Sourcesafe";
	}

	public String getIconName() {
		return "vss.jpg";
	}

	/**
	 * Method may throw a BuildException to indicates a module retrieve exception
	 *
	 * @param workingDir
	 * @param module
	 * @param isClean
	 * @param antProject
	 */
	private void retrieveModule(String workingDir, VssModule module, boolean isClean, Project antProject) {
		if (isClean)
			antProject.log("Retrieve source path: " + module.getSrcPath(), Project.MSG_INFO);
		else
			antProject.log("Update source path: " + module.getSrcPath(), Project.MSG_INFO);
		String destDir;
		if (Luntbuild.isEmpty(module.getDestPath()))
			destDir = Luntbuild.concatPath(workingDir, module.getSrcPath());
		else
			destDir = Luntbuild.concatPath(workingDir, module.getDestPath());

		if (isClean)
			Luntbuild.deleteDir(destDir);
		Luntbuild.createDir(destDir);

		Commandline cmdLine = buildVssExecutable();
		cmdLine.createArgument().setValue("Get");
		cmdLine.createArgument().setValue(Luntbuild.concatPath("$", module.getSrcPath()));
		cmdLine.createArgument().setLine("-I- -R");
		if (!Luntbuild.isEmpty(module.getLabel()))
			cmdLine.createArgument().setValue("-VL" + module.getLabel());
		cmdLine.createArgument().setValue("-W");
		LuntbuildLogger luntBuildLogger = Luntbuild.getLuntBuildLogger(antProject);
		if (luntBuildLogger == null || luntBuildLogger.getMessageOutputLevel() <= Project.MSG_INFO)
			cmdLine.createArgument().setValue("-O-");

		appendLoginInfo(cmdLine);
		Environment env = buildVssEnvironment();

		new MyExecTask("get", antProject, destDir, cmdLine, env, COMMAND_INPUT, Project.MSG_INFO).execute();
	}

	/**
	 * Method may throw a BuildException to indicates errors while labeling
	 *
	 * @param workingDir
	 * @param module
	 * @param label
	 * @param antProject
	 */
	private void labelModule(String workingDir, VssModule module, String label, Project antProject) {
		antProject.log("Label source path: " + module.getSrcPath(), Project.MSG_INFO);
		Commandline cmdLine = buildVssExecutable();
		cmdLine.createArgument().setValue("Label");
		cmdLine.createArgument().setValue(Luntbuild.concatPath("$", module.getSrcPath()));
		cmdLine.createArgument().setLine("-C- -I- -L" + label);
		appendLoginInfo(cmdLine);
		Environment env = buildVssEnvironment();

		new MyExecTask("label", antProject, workingDir, cmdLine, env, COMMAND_INPUT,
				Project.MSG_INFO).execute();
	}

	/**
	 * Delete specified label from specified module
	 *
	 * @param workingDir
	 * @param module
	 * @param label
	 * @param antProject
	 */
	private void unlabelModule(String workingDir, VssModule module, String label, Project antProject) {
		antProject.log("Unlabel source path: " + module.getSrcPath(), Project.MSG_INFO);
		Commandline cmdLine = buildVssExecutable();
		cmdLine.createArgument().setValue("Label");
		cmdLine.createArgument().setValue(Luntbuild.concatPath("$", module.getSrcPath()));
		cmdLine.createArgument().setValue("-VL" + label);
		cmdLine.createArgument().setLine("-C- -I-Y -L");
		appendLoginInfo(cmdLine);
		Environment env = buildVssEnvironment();

		new MyExecTask("label", antProject, workingDir, cmdLine, env, COMMAND_INPUT, Project.MSG_INFO).execute();
	}

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

	public void label(Build build, Project antProject) {
		// does nothing
	}

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

	public Vcs.Module createNewModule() {
		return new VssModule();
	}

    public Vcs.Module createNewModule(Vcs.Module module) {
        return new VssModule((VssModule)module);
    }

	public List getVcsSpecificProperties() {
		List properties = getVssProperties();
		return properties;
	}

	public void validateModules() {
		super.validateModules();
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			VssModule vssModule = (VssModule) it.next();
			if (vssModule.getSrcPath().startsWith("$"))
				throw new ValidationException("Property \"source path\" in module definition should not start with $");
		}
	}

	public class VssModule extends Module {
		/**
		 * Keep tracks of version of this class, used when do serialization-deserialization
		 */
		static final long serialVersionUID = 1L;

		private String srcPath;
		private String label;
		private String destPath;

        public VssModule() {}

        public VssModule(VssModule module) {
            this.srcPath = module.srcPath;
            this.label = module.label;
            this.destPath = module.destPath;
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

		public List getProperties() {
			List properties = getVssModuleProperties();
			return properties;
		}

		public com.luntsys.luntbuild.facades.lb12.ModuleFacade getFacade() {
			com.luntsys.luntbuild.facades.lb12.VssModuleFacade facade = new com.luntsys.luntbuild.facades.lb12.VssModuleFacade();
			facade.setDestPath(getDestPath());
			facade.setLabel(getLabel());
			facade.setSrcPath(getSrcPath());
			return facade;
		}

		public void setFacade(com.luntsys.luntbuild.facades.lb12.ModuleFacade facade) {
			if (facade instanceof com.luntsys.luntbuild.facades.lb12.VssModuleFacade) {
				com.luntsys.luntbuild.facades.lb12.VssModuleFacade vssModuleFacade = (com.luntsys.luntbuild.facades.lb12.VssModuleFacade) facade;
				setLabel(vssModuleFacade.getLabel());
				setSrcPath(vssModuleFacade.getSrcPath());
				setDestPath(vssModuleFacade.getDestPath());
			} else
				throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		}
	}

	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		final Revisions revisions = new Revisions();

		Commandline cmdLine = buildVssExecutable();
		Environment env = buildVssEnvironment();
		final RevisionBlock block = new RevisionBlock();
		final Pattern authorPattern = Pattern.compile("^User:(.*)Date:.*");
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			VssModule module = (VssModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel())) { // detect changes in head versions
				cmdLine.clearArgs();
				cmdLine.createArgument().setLine("HISTORY -I- -R -#" + Revisions.MAX_ENTRIES);
				appendLoginInfo(cmdLine);
				cmdLine.createArgument().setValue("-V~d" + formatDateForVss(sinceDate));
				cmdLine.createArgument().setValue(Luntbuild.concatPath("$", module.getSrcPath()));

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
				block.setValid(true);
				block.getLines().clear();
			}
		}
		return revisions;
	}

	/**
	 * Build the executable part of a commandline object
	 *
	 * @return
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
	 * Append login information to specified commandline object
	 *
	 * @param cmdLine
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
	 * Build necessary environment variable needed to run ss.exe, this typically includes
	 * SSDIR
	 *
	 * @return
	 */
	protected Environment buildVssEnvironment() {
		Environment env = new Environment();
		Environment.Variable var = new Environment.Variable();
		var.setKey("SSDIR");
		var.setValue(getVssPath());
		env.addVariable(var);
		return env;
	}

	/**
	 * Format specified date to use in vss command line
	 *
	 * @param date
	 * @return
	 */
	protected String formatDateForVss(Date date) {
		String pattern = DEFAULT_DATETIME_FORMAT;
		if (!Luntbuild.isEmpty(getDateTimeFormat()))
			pattern = getDateTimeFormat();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		DateFormatSymbols dfs = new DateFormatSymbols();
		dfs.setAmPmStrings(new String[]{"a", "p"});
		sdf.setDateFormatSymbols(dfs);
		return sdf.format(date);
	}

	public void saveToFacade(VcsFacade facade) {
		com.luntsys.luntbuild.facades.lb12.VssAdaptorFacade vssFacade = (VssAdaptorFacade) facade;
		vssFacade.setDateTimeFormat(getDateTimeFormat());
		vssFacade.setSsDir(getSsDir());
		vssFacade.setVssPassword(getVssPassword());
		vssFacade.setVssPath(getVssPath());
		vssFacade.setVssUser(getVssUser());
	}

	public void loadFromFacade(VcsFacade facade) {
		if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.VssAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		com.luntsys.luntbuild.facades.lb12.VssAdaptorFacade vssFacade = (com.luntsys.luntbuild.facades.lb12.VssAdaptorFacade) facade;
		setDateTimeFormat(vssFacade.getDateTimeFormat());
		setSsDir(vssFacade.getSsDir());
		setVssPassword(vssFacade.getVssPassword());
		setVssPath(vssFacade.getVssPath());
		setVssUser(vssFacade.getVssUser());
	}

	public VcsFacade constructFacade() {
		return new VssAdaptorFacade();
	}
}