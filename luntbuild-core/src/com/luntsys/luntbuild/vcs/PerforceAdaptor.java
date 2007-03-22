/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-7-16
 * Time: 7:02:00
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
import com.luntsys.luntbuild.ant.perforce.P4Label;
import com.luntsys.luntbuild.ant.perforce.P4Sync;
import com.luntsys.luntbuild.ant.perforce.P4Labelsync;
import com.luntsys.luntbuild.ant.perforce.P4Base;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.PerforceModuleFacade;
import com.luntsys.luntbuild.utility.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Perforce adaptor
 *
 * @author robin shine
 */
public class PerforceAdaptor extends Vcs {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1;
    private static final SimpleDateFormat P4_DATE_FORMAT =
            new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss");
	private static final String clientNamePattern = "^//([^\\s/]+)/";

    /**
     * Perforce port, such as 1666 or <server>:1666, etc.
     */
    private String port;

    /**
     * Perforce user
     */
    private String user;
    /**
     * Perforce password
     */
    private String password;

    private String lineEnd;

	private String p4Dir;

    public String getDisplayName() {
        return "Perforce";
    }

    public String getIconName() {
        return "perforce.jpg";
    }

	public String getP4Dir() {
		return p4Dir;
	}

	public void setP4Dir(String p4Dir) {
		this.p4Dir = p4Dir;
	}

    public List getVcsSpecificProperties() {
        List properties = getPerforceProperties();
		return properties;
	}

	/**
	 * Build the executable part of a commandline object
	 *
	 * @return
	 */
	protected Commandline buildP4Executable() {
		Commandline cmdLine = new Commandline();
		if (Luntbuild.isEmpty(getP4Dir()))
			cmdLine.setExecutable("p4");
		else
			cmdLine.setExecutable(Luntbuild.concatPath(getP4Dir(), "p4"));
		return cmdLine;
	}

    /**
     * Setup perforce client specification based on current build information
     *
     * @param antProject
     */
    private void setupP4Client(Schedule schedule, Project antProject) {
        antProject.log("Setup Perforce client specification...", Project.MSG_INFO);

		String workingDir = schedule.getWorkDirRaw();

        // edit p4 client specification to reflect current working dirctory and view mapping
        P4ClientSpec p4Client = new P4ClientSpec();
		p4Client.setP4Dir(getP4Dir());
        initP4Cmd(p4Client, antProject);

        // Get full view if it exist
        p4Client.setHostValue("");
        p4Client.setClientValue(getClient(schedule));
        p4Client.setOwnerValue(getUser());
        p4Client.setRootValue(workingDir);
        // concatenate the view string of the perforce client
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            PerforceModule perforceModule = (PerforceModule) it.next();
			String clientPath = perforceModule.getClientPath().replaceFirst(clientNamePattern, "//" + getClient(schedule) + "/");
            p4Client.addViewValue(perforceModule.getDepotPath() + " " + clientPath);
        }
        if (Luntbuild.isEmpty(getLineEnd()))
            p4Client.setLineEndValue("local");
        else
            p4Client.setLineEndValue(getLineEnd());
        p4Client.setTaskType("P4SpecWrite");
        p4Client.setTaskName("P4SpecWrite");
        p4Client.write();
    }

    public void validateProperties() {
        super.validateProperties();
        if (!Luntbuild.isEmpty(getLineEnd())) {
            String lineEnd = getLineEnd().trim();
            if (!lineEnd.equalsIgnoreCase("local") && !lineEnd.equalsIgnoreCase("unix") &&
                    !lineEnd.equalsIgnoreCase("mac") && !lineEnd.equalsIgnoreCase("win") &&
                    !lineEnd.equalsIgnoreCase("share"))
                throw new ValidationException("Invalid value for convert EOL: should be " +
                        "one of \"local\", \"unix\", \"mac\", \"win\", or \"share\"");
            setLineEnd(lineEnd);
        }
    }

    /**
     * Setup perforce label based on current build
     *
     * @param label
     * @param antProject
     */
    private void setupP4Label(Schedule schedule, String label, Project antProject) {
        antProject.log("Setup label specification...", Project.MSG_INFO);
        // concatenates the label view string
        String labelView = "";
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            PerforceModule perforceModule = (PerforceModule) it.next();
            if (Luntbuild.isEmpty(perforceModule.getLabel())) {
                if (!labelView.equals(""))
                    labelView += ":";
                labelView += perforceModule.getDepotPath();
            }
        }

        // create a new label
        if (!labelView.equals("")) {
            P4Label p4Label = new P4Label();
			p4Label.setP4Dir(getP4Dir());
            initP4Cmd(p4Label, antProject);
            p4Label.setClient(getClient(schedule));
            p4Label.setName(label);
            p4Label.setDesc("a luntbuild label");
            p4Label.setView(labelView);
            p4Label.setTaskType("P4Label");
            p4Label.setTaskName("P4Label");
            p4Label.execute();
        }
    }

    /**
     * Retrieve specified module
     *
     * @param module
     * @param antProject
     * @param force
     */
    private void retrieveModule(Schedule schedule, PerforceModule module, Project antProject, boolean force) {
        if (force)
            antProject.log("Retrieve depot path: " + module.getDepotPath(), Project.MSG_INFO);
        else
            antProject.log("Update depot path: " + module.getDepotPath(), Project.MSG_INFO);

        P4Sync p4Sync = new P4Sync();
		p4Sync.setP4Dir(getP4Dir());
        initP4Cmd(p4Sync, antProject);
        p4Sync.setClient(getClient(schedule));
        if (force)
            p4Sync.setForce("yes");
        p4Sync.setView(module.getDepotPath());
        if (module.getLabel() != null && !module.getLabel().trim().equals(""))
            p4Sync.setLabel(module.getLabel());
        p4Sync.setTaskType("P4Sync");
        p4Sync.setTaskName("P4Sync");
        p4Sync.execute();
    }

    /**
     * Label specified module
     *
     * @param module
     * @param label
     * @param antProject
     */
    private void labelModule(Schedule schedule, PerforceModule module, String label, Project antProject) {
        antProject.log("Label depot path: " + module.getDepotPath(), Project.MSG_INFO);

        P4Labelsync p4LabelSync = new P4Labelsync();
		p4LabelSync.setP4Dir(getP4Dir());
        initP4Cmd(p4LabelSync, antProject);
        p4LabelSync.setClient(getClient(schedule));
        p4LabelSync.setAdd(true);
        p4LabelSync.setView(module.getDepotPath());
        p4LabelSync.setName(label);
        p4LabelSync.setTaskType("P4LabelSync");
        p4LabelSync.setTaskName("P4LabelSync");
        p4LabelSync.execute();
    }

    public void checkoutActually(Build build, Project antProject) {
        setupP4Client(build.getSchedule(), antProject);

        // retrieve modules
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            PerforceModule module = (PerforceModule) Luntbuild.cloneModule(this, (Vcs.Module) it.next());
			if (module.getDepotPath().startsWith("-"))
				continue;
            if (build.isRebuild() && Luntbuild.isEmpty(module.getLabel()))
                module.setLabel(Luntbuild.getLabelByVersion(build.getVersion()));
            if (build.isRebuild() || build.isCleanBuild())
                retrieveModule(build.getSchedule(), module, antProject, true);
            else
                retrieveModule(build.getSchedule(), module, antProject, false);
        }
    }

    public void label(Build build, Project antProject) {
        setupP4Label(build.getSchedule(), Luntbuild.getLabelByVersion(build.getVersion()), antProject);
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            PerforceModule module = (PerforceModule) it.next();
			if (module.getDepotPath().startsWith("-"))
				continue;
            if (Luntbuild.isEmpty(module.getLabel()))
                labelModule(build.getSchedule(), module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
        }
    }

    /**
     * Retrieves client name from a p4 client path, For example, if passed-in client path is
     * "//build/testperforce/...", client name returned should be build
     *
     * @param p4ClientPath specifies a perforce client path, for example: //build/testperforce/...
     * @return client name extracted from the p4 client path
     * @throws ValidationException
     */
    private String getP4Client(String p4ClientPath) {
        Pattern pattern = Pattern.compile((clientNamePattern));
        Matcher matcher = pattern.matcher(p4ClientPath);
        if (!matcher.find())
            throw new ValidationException("Property \"client path\" in module definition of the Perforce adaptor is invalid: " + p4ClientPath);
        else
            return matcher.group(1);
    }

    public String getPort() {
        return port;
    }

    public Module createNewModule() {
        return new PerforceModule();
    }

    public Module createNewModule(Module module) {
        return new PerforceModule((PerforceModule)module);
    }

    public void setPort(String port) {
        this.port = port;
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

    public String getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
    }

    private void initP4Cmd(P4Base p4Cmd, Project antProject) {
        p4Cmd.setProject(antProject);
        p4Cmd.init();
        p4Cmd.setPort(getPort());
        p4Cmd.setUser(getUser());
        if (getPassword() != null && !getPassword().trim().equals("")) {
            p4Cmd.setGlobalopts("-P " + getPassword());
			p4Cmd.setDescriptiveGlobalopts("-P *****");
		}
        p4Cmd.setDetectErrorByRetCode(true);
        p4Cmd.setFailonerror(true);
    }

    private String getClient(Schedule schedule) {
		PerforceModule firstModule = (PerforceModule) getModules().get(0);
		return getP4Client(firstModule.getClientPath()) + "-" + schedule.getJobName();
    }

    public void validateModules() {
        super.validateModules();
        PerforceModule firstModule = (PerforceModule) getModules().get(0);
        String clientName = getP4Client(firstModule.getClientPath());
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            PerforceModule perforceModule = (PerforceModule) it.next();
            if (!getP4Client(perforceModule.getClientPath()).equals(clientName))
                throw new ValidationException("P4 Client name not consistent in modules definition!");
        }
    }

    public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
        String workingDir = workingSchedule.getWorkDirRaw();
        final Revisions revisions = new Revisions();
        setupP4Client(workingSchedule, antProject);
        Commandline cmdLine = buildP4Executable();
        cmdLine.createArgument().setValue("-s");
        addCommonOpts(cmdLine);
        cmdLine.createArgument().setLine("-c " + getClient(workingSchedule));
        cmdLine.createArgument().setLine("changes -s submitted");
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            PerforceModule module = (PerforceModule) it.next();
			if (module.getDepotPath().startsWith("-"))
				continue;
            if (Luntbuild.isEmpty(module.getLabel())) {
                cmdLine.createArgument().setValue(module.getClientPath().replaceFirst(clientNamePattern,
						"//" + getClient(workingSchedule) + "/") + "@" + P4_DATE_FORMAT.format(sinceDate) +
						"," + P4_DATE_FORMAT.format(new Date()));
            }
        }

        // get list of change numbers
        final List changeNumbers = new ArrayList();
        new MyExecTask("changes", antProject, workingDir, cmdLine, null, null, -1) {
            public void handleStdout(String line) {
                if (line.startsWith("error:"))
                    throw new BuildException(line);
                else if (line.startsWith("exit: 1"))
                    throw new BuildException(line);
                else if (line.startsWith("info:")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    st.nextToken();
                    changeNumbers.add(st.nextToken());
                }
            }
        }.execute();

        if (changeNumbers.size() == 0)
            return revisions;
        else
            revisions.setFileModified(true);

        // describe above change number list to get affected files
        cmdLine.clearArgs();
        addCommonOpts(cmdLine);
        cmdLine.createArgument().setLine("-c " + getClient(workingSchedule));
        cmdLine.createArgument().setLine("describe -s");
        it = changeNumbers.iterator();
        while (it.hasNext()) {
            String changeNumber = (String) it.next();
            cmdLine.createArgument().setValue(changeNumber);
        }
        final Pattern authorPattern = Pattern.compile("^Change.*by(.*)@.*");
        new MyExecTask("describe", antProject, workingDir, cmdLine, null, null, Project.MSG_VERBOSE) {
            public void handleStdout(String line) {
                revisions.getChangeLogs().add(line);
                Matcher matcher = authorPattern.matcher(line);
                if (matcher.find())
                    revisions.getChangeLogins().add(matcher.group(1).trim());
            }
        }.execute();

        return revisions;
    }

    /**
     * Add common options for various p4 command
     *
     * @param cmdLine
     */
    private void addCommonOpts(Commandline cmdLine) {
        cmdLine.createArgument().setLine("-p " + getPort() + " -u " + getUser());
        if (!Luntbuild.isEmpty(getPassword())) {
			Commandline.Argument arg = cmdLine.createArgument();
            arg.setLine("-P " + getPassword());
			arg.setDescriptiveLine("-P ******");
		}
    }

    public class PerforceModule extends Module {
        /**
         * Keep tracks of version of this class, used when do serialization-deserialization
         */
        static final long serialVersionUID = 1;
        private String depotPath;
        private String label;
        private String clientPath;

        public PerforceModule() {}

        public PerforceModule(PerforceModule module) {
            this.depotPath = module.depotPath;
            this.label = module.label;
            this.clientPath = module.clientPath;
        }

        public List getProperties() {
            List properties = getPerforceModuleProperties();
            return properties;
        }

        public String getDepotPath() {
            return depotPath;
        }

        public void setDepotPath(String depotPath) {
            this.depotPath = depotPath;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getClientPath() {
            return clientPath;
        }

        public void setClientPath(String clientPath) {
            this.clientPath = clientPath;
        }

        public com.luntsys.luntbuild.facades.lb12.ModuleFacade getFacade() {
            PerforceModuleFacade facade = new com.luntsys.luntbuild.facades.lb12.PerforceModuleFacade();
            facade.setClientPath(getClientPath());
            facade.setDepotPath(getDepotPath());
            facade.setLabel(getLabel());
            return facade;
        }

        public void setFacade(com.luntsys.luntbuild.facades.lb12.ModuleFacade facade) {
            if (facade instanceof com.luntsys.luntbuild.facades.lb12.PerforceModuleFacade) {
                PerforceModuleFacade perforceModuleFacade = (PerforceModuleFacade) facade;
                setClientPath(perforceModuleFacade.getClientPath());
                setDepotPath(perforceModuleFacade.getDepotPath());
                setLabel(perforceModuleFacade.getLabel());
            } else
                throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        }
    }

    public void saveToFacade(com.luntsys.luntbuild.facades.lb12.VcsFacade facade) {
        com.luntsys.luntbuild.facades.lb12.PerforceAdaptorFacade perforceFacade = (com.luntsys.luntbuild.facades.lb12.PerforceAdaptorFacade) facade;
        perforceFacade.setLineEnd(getLineEnd());
        perforceFacade.setPassword(getPassword());
        perforceFacade.setPort(getPort());
        perforceFacade.setUser(getUser());
		perforceFacade.setP4Dir(getP4Dir());
    }

    public void loadFromFacade(com.luntsys.luntbuild.facades.lb12.VcsFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.PerforceAdaptorFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb12.PerforceAdaptorFacade perforceFacade = (com.luntsys.luntbuild.facades.lb12.PerforceAdaptorFacade) facade;
        setLineEnd(perforceFacade.getLineEnd());
        setPassword(perforceFacade.getPassword());
        setPort(perforceFacade.getPort());
        setUser(perforceFacade.getUser());
		setP4Dir(perforceFacade.getP4Dir());
    }

	public com.luntsys.luntbuild.facades.lb12.VcsFacade constructFacade() {
		return new com.luntsys.luntbuild.facades.lb12.PerforceAdaptorFacade();
	}
}
