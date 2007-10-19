/*
 * Original Copyright luntsys (c) 2004-2005,
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
import com.luntsys.luntbuild.facades.lb12.ModuleFacade;
import com.luntsys.luntbuild.facades.lb12.PerforceAdaptorFacade;
import com.luntsys.luntbuild.facades.lb12.PerforceModuleFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.utility.*;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Perforce VCS adaptor implementation.
 * 
 * <p>This adaptor is safe for remote hosts.</p>
 *
 * @author robin shine
 */
public class PerforceAdaptor extends Vcs {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1;
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

    /** Perforce web interface to itegrate with */
    private String webInterface;
    /** Perforce web interface URL */
    private String webUrl;

    private int clientChange;
    private int tipChange;

    /**
     * @inheritDoc
     */
    public String getDisplayName() {
        return "Perforce";
    }

    /**
     * @inheritDoc
     */
    public String getIconName() {
        return "perforce.jpg";
    }

	/**
	 * Gets the path to the Perforce executable.
	 * 
	 * @return the path to the Perforce executable
	 */
	public String getP4Dir() {
		return p4Dir;
	}

	/**
	 * Sets the path to the Perforce executable.
	 * 
	 * @param p4Dir the path to the Perforce executable
	 */
	public void setP4Dir(String p4Dir) {
		this.p4Dir = p4Dir;
	}

    /**
     * Gets the web interface to integrate with.
     * 
     * @return the web interface to integrate with
     */
    public String getWebInterface() {
        return webInterface;
    }

    /**
     * Sets the web interface to integrate with.
     * 
     * @param webInterface the web interface to integrate with
     */
    public void setWebInterface(String webInterface) {
        this.webInterface = webInterface;
    }

    /**
     * Gets the web interface URL.
     * 
     * @return the web interface URL
     */
    public String getWebUrl() {
        return webUrl;
    }

    /**
     * Sets the web interface URL.
     * 
     * @param webUrl the web interface URL
     */
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    /**
     * @inheritDoc
     */
    public List getVcsSpecificProperties() {
        List properties = new ArrayList();
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "Perforce port";
            }

            public String getDescription() {
                return "The Perforce port in the format of <port>, or <servername>:<port>, " +
                        "where <servername> and <port> will be replaced by the actual Perforce " +
                        "server name and the port number.";
            }

            public String getValue() {
                return getPort();
            }

            public void setValue(String value) {
                setPort(value);
            }
        });
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "User name";
            }

            public String getDescription() {
                return "User name to access the above Perforce server. This user should have " +
                        "the rights to create and edit client specifications and to checkout and " +
                        "label code.";
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
                return "Password";
            }

            public String getDescription() {
                return "Password for the above user. Can be blank, if your Perforce server does not use password based security.";
            }

            public boolean isSecret() {
                return true;
            }

            public boolean isRequired() {
                return false;
            }

            public String getValue() {
                return getPassword();
            }

            public void setValue(String value) {
                setPassword(value);
            }
        });
        DisplayProperty p = new DisplayProperty() {
            public String getDisplayName() {
                return "Line end";
            }

            public String getDescription() {
                return "Set line ending character(s) for client text files.The following values are possible:\n" +
                        "local: use mode native to the client\n" +
                        "unix: UNIX style\n" +
                        "mac: Macintosh style\n" +
                        "win: Windows style\n" +
                        "share: writes UNIX style but reads UNIX, Mac or Windows style";
            }

            public boolean isRequired() {
                return false;
            }

            public boolean isSelect() {
                return true;
            }

            public String getValue() {
                return getLineEnd();
            }

            public void setValue(String value) {
                setLineEnd(value);
            }
        };
        // Create selection model
        IPropertySelectionModel model = new PerforceLineEndSelectionModel();
        // Set selection model
        p.setSelectionModel(model);
        // Add property to properties list
        properties.add(p);
        
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Path for p4 executable";
			}

			public String getDescription() {
				return "The directory path, where your p4 executable file resides in. " +
						"It should be specified here, if it does not exist in the system path.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getP4Dir();
			}

			public void setValue(String value) {
				setP4Dir(value);
			}
		});
        p = new DisplayProperty() {
            public String getDisplayName() {
                return "Web interface";
            }

            public String getDescription() {
                return "Set the web interface to integrate with.";
            }

            public boolean isRequired() {
                return false;
            }

            public boolean isSelect() {
                return true;
            }

            public String getValue() {
                return getWebInterface();
            }

            public void setValue(String value) {
                setWebInterface(value);
            }
        };
        // Create selection model
        model = new PerforceWebInterfaceSelectionModel();
        // Set selection model
        p.setSelectionModel(model);
        // Add property to properties list
        properties.add(p);
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "URL to web interface";
            }

            public String getDescription() {
                return "The URL to access the repository in your chosen web interface.";
            }

            public boolean isRequired() {
                return false;
            }

            public String getValue() {
                return getWebUrl();
            }

            public void setValue(String value) {
                setWebUrl(value);
            }
        });
		return properties;
	}

	/**
	 * Constructs the executable part of a commandline object.
	 * 
	 * @return the commandline object
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
	 * Selection model used for user interface of <code>PerforceAdaptor</code>.
	 */
    static class PerforceLineEndSelectionModel implements IPropertySelectionModel {
        String[] values = {"local", "unix", "mac", "win", "share"};

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
     * Sets up a perforce client specification based on current build information.
     * 
     * @param schedule the schedule
     * @param antProject the ant project used for logging
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
			String clientPath = perforceModule.getActualClientPath().replaceFirst(clientNamePattern, "//" + getClient(schedule) + "/");
            p4Client.addViewValue(perforceModule.getActualDepotPath() + " " + clientPath);
        }
        if (Luntbuild.isEmpty(getLineEnd()))
            p4Client.setLineEndValue("local");
        else
            p4Client.setLineEndValue(getLineEnd());
        p4Client.setTaskType("P4SpecWrite");
        p4Client.setTaskName("P4SpecWrite");
        p4Client.write();
    }

    /**
     * Validates the properties of this VCS.
     *
     * @throws ValidationException if a property has an invalid value
     */
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
     * Sets up a perforce label based on current build information.
     * 
     * @param schedule the schedule
     * @param label the label to create
     * @param antProject the ant project used for logging
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
                labelView += perforceModule.getActualDepotPath();
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
	 * Retrieves the contents of a module.
	 * 
	 * @param schedule the schedule
     * @param module the module
     * @param antProject the ant project used for logging
     * @param force set <code>true</code> if checkout should be forced
     */
    private void retrieveModule(Schedule schedule, PerforceModule module, Project antProject, boolean force) {
        if (force)
            antProject.log("Retrieve depot path: " + module.getActualDepotPath(), Project.MSG_INFO);
        else
            antProject.log("Update depot path: " + module.getActualDepotPath(), Project.MSG_INFO);

        P4Sync p4Sync = new P4Sync();
		p4Sync.setP4Dir(getP4Dir());
        initP4Cmd(p4Sync, antProject);
        p4Sync.setClient(getClient(schedule));
        if (force)
            p4Sync.setForce("yes");
        p4Sync.setView(module.getActualDepotPath());
        if (module.getLabel() != null && !module.getLabel().trim().equals(""))
            p4Sync.setLabel(module.getActualLabel());
        p4Sync.setTaskType("P4Sync");
        p4Sync.setTaskName("P4Sync");
        p4Sync.execute();
    }

    /**
     * Labels the specified module.
     * 
	 * @param schedule the schedule
     * @param module the module
     * @param label the label to use
     * @param antProject the ant project used for logging
     */
    private void labelModule(Schedule schedule, PerforceModule module, String label, Project antProject) {
        antProject.log("Label depot path: " + module.getActualDepotPath(), Project.MSG_INFO);

        P4Labelsync p4LabelSync = new P4Labelsync();
		p4LabelSync.setP4Dir(getP4Dir());
        initP4Cmd(p4LabelSync, antProject);
        p4LabelSync.setClient(getClient(schedule));
        p4LabelSync.setAdd(true);
        p4LabelSync.setView(module.getActualDepotPath());
        p4LabelSync.setName(label);
        p4LabelSync.setTaskType("P4LabelSync");
        p4LabelSync.setTaskName("P4LabelSync");
        p4LabelSync.execute();
    }

	/**
     * @inheritDoc
	 */
    public void checkoutActually(Build build, Project antProject) {
        setupP4Client(build.getSchedule(), antProject);

        // retrieve modules
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            PerforceModule module = (PerforceModule) Luntbuild.cloneModule(this, (Vcs.Module) it.next());
			if (module.getActualDepotPath().startsWith("-"))
				continue;
            if (build.isRebuild() && Luntbuild.isEmpty(module.getLabel()))
                module.setLabel(Luntbuild.getLabelByVersion(build.getVersion()));
            if (build.isRebuild() || build.isCleanBuild())
                retrieveModule(build.getSchedule(), module, antProject, true);
            else
                retrieveModule(build.getSchedule(), module, antProject, false);
        }
    }

	/**
     * @inheritDoc
	 */
    public void label(Build build, Project antProject) {
        setupP4Label(build.getSchedule(), Luntbuild.getLabelByVersion(build.getVersion()), antProject);
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            PerforceModule module = (PerforceModule) it.next();
			if (module.getActualDepotPath().startsWith("-"))
				continue;
            if (Luntbuild.isEmpty(module.getLabel()))
                labelModule(build.getSchedule(), module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
        }
    }

    /**
     * Retrieves the client name from a p4 client path. For example, if the passed-in client path is
     * "//build/testperforce/...", client name returned should be "build".
     *
     * @param p4ClientPath a perforce client path, for example: //build/testperforce/...
     * @return the client name extracted from the p4 client path
     * @throws ValidationException if the client path is invalid
     */
    private String getP4Client(String p4ClientPath) {
        Pattern pattern = Pattern.compile((clientNamePattern));
        Matcher matcher = pattern.matcher(p4ClientPath);
        if (!matcher.find())
            throw new ValidationException("Property \"client path\" in module definition of the Perforce adaptor is invalid: " + p4ClientPath);
        else
            return matcher.group(1);
    }

	/**
     * @inheritDoc
	 * @see PerforceModule
	 */
    public Module createNewModule() {
        return new PerforceModule();
    }

	/**
     * @inheritDoc
	 * @see PerforceModule
	 */
    public Module createNewModule(Module module) {
        return new PerforceModule((PerforceModule)module);
    }

    /**
     * Creates a P4Web link to the specified changelist.
     * 
     * @param changelist the changelist
     * @return the link
     */
    public String createLinkForChangelist(String changelist) {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl())
                || Luntbuild.isEmpty(changelist))
            return changelist;
        if (getWebInterface().equals("P4DB"))
            return "<a href=\"" + getWebUrl() + "/changeView.cgi?CH=" + changelist + "\">" + changelist + "</a>";
        else if (getWebInterface().equals("P4Web"))
            return "<a href=\"" + getWebUrl() + "/" + changelist + "?ac=10\">" + changelist + "</a>";
        else
            return changelist;
    }

    /**
     * Creates a P4Web link to the specified user.
     * 
     * @param user the user
     * @return the link
     */
    public String createLinkForUser(String user) {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl())
                || Luntbuild.isEmpty(user))
            return user;
        if (getWebInterface().equals("P4DB"))
            return "<a href=\"" + getWebUrl() + "/userView.cgi?USER=" + user + "\">" + user + "</a>";
        else if (getWebInterface().equals("P4Web"))
            return "<a href=\"" + getWebUrl() + "/" + user + "?ac=17\">" + user + "</a>";
        else
            return user;
    }

    /**
     * Creates a P4Web link to the specified workspace.
     * 
     * @param workspace the workspace
     * @return the link
     */
    public String createLinkForWorkspace(String workspace) {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl())
                || Luntbuild.isEmpty(workspace))
            return workspace;
        if (getWebInterface().equals("P4DB"))
            return "<a href=\"" + getWebUrl() + "/clientView.cgi?CLIENT=" + workspace + "\">" + workspace + "</a>";
        else if (getWebInterface().equals("P4Web"))
            return "<a href=\"" + getWebUrl() + "/" + workspace + "?ac=15\">" + workspace + "</a>";
        else
            return workspace;
    }

    /**
     * Creates a P4Web link to the specified job.
     * 
     * @param job the job
     * @return the link
     */
    public String createLinkForJob(String job) {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl())
                || Luntbuild.isEmpty(job))
            return job;
        if (getWebInterface().equals("P4DB"))
            return "<a href=\"" + getWebUrl() + "/jobView.cgi?JOB=" + job + "\">" + job + "</a>";
        else if (getWebInterface().equals("P4Web"))
            return "<a href=\"" + getWebUrl() + "/" + job + "?ac=111\">" + job + "</a>";
        else
            return job;
    }

    /**
     * Creates a P4Web link to the specified file revision.
     * 
     * @param path the depot path for the file
     * @param rev the file revision
     * @return the link
     */
    public String createLinkForFile(String path, String rev) {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl())
                || Luntbuild.isEmpty(path))
            return path + "#" + rev;
        if (getWebInterface().equals("P4DB"))
            return "<a href=\"" + getWebUrl() + "/fileViewer.cgi?FSPC=" + path + "&REV=" + rev + "\">" + path + "#" + rev + "</a>";
        else if (getWebInterface().equals("P4Web"))
            return "<a href=\"" + getWebUrl() + "/" + path + "?ac=64&rev1=" + rev + "\">" + path + "#" + rev + "</a>";
        else
            return path + "#" + rev;
    }

    /**
     * Creates a P4Web link to diff the specified file and revision with the previous revision.
     * 
     * @param path the depot path for the file
     * @param rev the file revision
     * @return the link
     */
    public String createLinkForDiff(String path, String rev) {
        if (Luntbuild.isEmpty(rev))
            return "";
        try {
            int prev_rev = Integer.parseInt(rev) - 1;
            if (prev_rev > 0)
                return createLinkForDiff(path, rev, String.valueOf(prev_rev));
        } catch (Exception e) {
            // ignore
        }
        return "";
    }

    /**
     * Creates a P4Web link to diff the specified file between the specified revisions.
     * 
     * @param path the depot path for the file
     * @param rev the file revision (right hand side)
     * @param prev_rev the previous file revision (left hand side)
     * @return the link
     */
    public String createLinkForDiff(String path, String rev, String prev_rev) {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl())
                || Luntbuild.isEmpty(path) || Luntbuild.isEmpty(rev) || Luntbuild.isEmpty(prev_rev))
            return "";
        if (getWebInterface().equals("P4DB"))
            return "(<a href=\"" + getWebUrl() + "/fileDiffView.cgi?FSPC=" + path + "&ACT=edit&REV=" + prev_rev + "&REV2=" + rev + "\">diff</a>)";
        else if (getWebInterface().equals("P4Web"))
            return "(<a href=\"" + getWebUrl() + "/" + path + "?ac=19&rev1=" + prev_rev + "&rev2=" + rev + "\">diff</a>)";
        else
            return "";
    }

    /**
     * Gets the server name and port to connect to the Perforce server with.
     * 
     * @return the server name and port
     */
    public String getPort() {
        return port;
    }

    /**
     * Sets the server name and port to connect to the Perforce server with.
     * 
     * @param port the server name and port
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Gets the login user to use.
     * 
     * @return the login user
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the login user to use.
     * 
     * @param user the login user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets the login password to use.
     * 
     * @return the login password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the login password to use.
     * 
     * @param password the login password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the line end mode.
     * 
     * @return the line end mode
     */
    public String getLineEnd() {
        return lineEnd;
    }

    /**
     * Sets the line end mode.
     * 
     * @param lineEnd the line end mode
     */
    public void setLineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
    }

    /**
     * Gets the tip changelist. This is also the client changelist if called after {@link #getClientChangelist(Schedule, Project)}.
     * 
     * @return the tip changelist
     */
    public String getChangelist() {
        return Integer.toString(tipChange);
    }

    /**
     * Sets the tip changelist.
     * 
     * @param changelist the tip changelist
     */
    public void setChangelist(String changelist) {
    	try {
    		if (Luntbuild.isEmpty(changelist))
    			changelist = "0";
        	this.tipChange = Integer.parseInt(changelist);
        } catch (NumberFormatException nfe) {
        	this.tipChange = 0;
        }
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

    /**
     * Gets the client (workspace) name being used.
     * 
     * @param schedule the schedule
     * @return the client name
     */
    public String getClient(Schedule schedule) {
        PerforceModule firstModule = (PerforceModule) getModules().get(0);
        if (firstModule != null)
            return getP4Client(firstModule.getActualClientPath()) + "-" + schedule.getJobName();
        else
            return "";
    }

    /**
     * Validates the modules of this VCS.
     *
     * @throws ValidationException if a module is not invalid
     */
    public void validateModules() {
        super.validateModules();
        PerforceModule firstModule = (PerforceModule) getModules().get(0);
        String clientName = getP4Client(firstModule.getActualClientPath());
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            PerforceModule perforceModule = (PerforceModule) it.next();
            if (!getP4Client(perforceModule.getActualClientPath()).equals(clientName))
                throw new ValidationException("P4 Client name not consistent in modules definition!");
        }
    }

    /**
     * Gets the changelist number to which this workspace is synced.  This is
     * used to generate the revisions report, which lists the changelists used
     * in the build.
     * 
     * @param workingSchedule the currently running schedule
     * @param antProject the ant project used for logging
     * @return the client changelist
     */
    public int getClientChangelist(Schedule workingSchedule, Project antProject) {
        /*
            Perforce Tech Note #51 explains that if the latest changelist
            contains purely deletes, the returned changelist does not reflect
            it.  This is problematic, for us, because if we don't get the
            correct "synced" changelist value, builds are unnecessarily
            performed until the latest changelist contains a non-delete
            action.

            To resolve this, we need to add a test, verifying whether we are
            already synced to the tip changelist.  If the workspace is
            current, we ignore the apparent synced changelist and, instead,
            return the tip changelist as our client (synced) changelist.
        */

        // Get the latest changelist in this workspace view:
        Commandline cmdLine = buildP4Executable();
        cmdLine.createArgument().setValue("-s");
        addCommonOpts(cmdLine);
        cmdLine.createArgument().setLine("-c " + getClient(workingSchedule));
        cmdLine.createArgument().setLine("changes -s submitted -m1 //" + getClient(workingSchedule) + "/...");
        new MyExecTask("tipChange", antProject, workingSchedule.getWorkingDir(), cmdLine, null, null, -1) {
            public void handleStdout(String line) {
                if (line.startsWith("error:"))
                    throw new BuildException(line);
                else if (line.startsWith("exit: 1"))
                    throw new BuildException(line);
                else if (line.startsWith("info:")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    st.nextToken();
                    final String strChange = st.nextToken();
                    try {
                        tipChange = Integer.parseInt(strChange);
                    } catch (NumberFormatException e) { }
                }
            }
        }.execute();

        // Get the (candidate) synced changelist:
        cmdLine.clearArgs();
        cmdLine = buildP4Executable();
        cmdLine.createArgument().setValue("-s");
        addCommonOpts(cmdLine);
        cmdLine.createArgument().setLine("-c " + getClient(workingSchedule));
        cmdLine.createArgument().setLine("changes -m1 @" + getClient(workingSchedule));
        new MyExecTask("clientChange", antProject, workingSchedule.getWorkingDir(), cmdLine, null, null, -1) {
            public void handleStdout(String line) {
                if (line.startsWith("error:"))
                    throw new BuildException(line);
                else if (line.startsWith("exit: 1"))
                    throw new BuildException(line);
                else if (line.startsWith("info:")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    st.nextToken();
                    final String strChange = st.nextToken();
                    try {
                        clientChange = Integer.parseInt(strChange);
                    } catch (NumberFormatException e) { }
                }
            }
        }.execute();

        /*
            If we're already synced to the tip changelist, use that as our
            client changelist.
        */
        if (clientChange != tipChange) {
            cmdLine.clearArgs();
            cmdLine = buildP4Executable();
            cmdLine.createArgument().setValue("-s");
            addCommonOpts(cmdLine);
            cmdLine.createArgument().setLine("-c " + getClient(workingSchedule));
            cmdLine.createArgument().setLine("sync -n @" + tipChange);
            new MyExecTask("previewSync", antProject, workingSchedule.getWorkingDir(), cmdLine, null, null, -1) {
                public void handleStdout(String line) {
                    if (line.startsWith("exit: 1"))
                        throw new BuildException(line);
                    else if (line.equals("error: @" + tipChange + " - file(s) up-to-date.")) {
                        /*
                            Latest changelist(s) contain only deleted files.  We
                            are actually synced to tipChange, so record that.
                        */
                        clientChange = tipChange;
                    } else if (line.startsWith("error:")) {
                        throw new BuildException(line);
                    }
                }
            }.execute();
        }

        antProject.log("Client changelist:" + clientChange, Project.MSG_INFO);

        return clientChange;
    }

    /**
     * @inheritDoc
     */
    public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String workingDir = workingSchedule.getWorkDirRaw();
        final Revisions revisions = new Revisions();
        revisions.addLog(this.getClass().getName(), toString());
        revisions.getChangeLogs().add("*************************************************************");
        revisions.getChangeLogs().add(toString());
        revisions.getChangeLogs().add("");
        setupP4Client(workingSchedule, antProject);
        // Keep this
        final int nextChange = getClientChangelist(workingSchedule, antProject) + 1;
        Commandline cmdLine = buildP4Executable();
        cmdLine.createArgument().setValue("-s");
        addCommonOpts(cmdLine);

        cmdLine.createArgument().setLine("-c " + getClient(workingSchedule));
        cmdLine.createArgument().setLine("changes -s submitted");
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            PerforceModule module = (PerforceModule) it.next();
			if (module.getActualDepotPath().startsWith("-"))
				continue;
            if (Luntbuild.isEmpty(module.getLabel())) {
                cmdLine.createArgument().setValue(module.getActualClientPath().replaceFirst(clientNamePattern,
						"//" + getClient(workingSchedule) + "/") + "@" + format.format(sinceDate) +
						"," + format.format(new Date()));
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
        final Pattern changelistPattern = Pattern.compile("^Change (.*) by (.*)@.* on (.*)");
        final Pattern endChangelistPattern = Pattern.compile("^(Jobs fixed \\.\\.\\.)|(Affected files \\.\\.\\.)$");
        final Pattern jobPattern = Pattern.compile("^(.*) on .* by (.*) (.*)$");
        final Pattern jobDescPattern = Pattern.compile("^\\t(.*)");
        final Pattern pathPattern = Pattern.compile("^\\.\\.\\. (.*)#(.*) (.*)$");
        new MyExecTask("describe", antProject, workingDir, cmdLine, null, null, Project.MSG_VERBOSE) {
            private boolean revisionReady = false;
            private String changelist = "";
            private String author = "";
            private Date date = null;
            private String msg = "";
            private boolean captureNextLineForJob = false;
            private String job_name = "";
            private String job_user = "";
            private String job_status = "";
            public void handleStdout(String line) {
                revisions.getChangeLogs().add(line);
                Matcher matcher = changelistPattern.matcher(line);
                Matcher endmatcher = endChangelistPattern.matcher(line);
                Matcher jobmatcher = jobPattern.matcher(line);
                Matcher jobdescmatcher = jobDescPattern.matcher(line);
                Matcher pathmatcher = pathPattern.matcher(line);
                if (matcher.find()) {
                    revisionReady = true;
                    changelist = matcher.group(1).trim();
                    author = matcher.group(2).trim();
                    revisions.getChangeLogins().add(author);
                    try {
                        date = format.parse(matcher.group(3).trim());
                    } catch (Exception e) {
                        logger.error("Failed to parse date from Perforce log", e);
                        date = null;
                    }
                } else if (endmatcher.find() && revisionReady) {
                    revisionReady = false;
                    revisions.addEntryToLastLog(changelist, author, date, msg);
                    msg = "";
                } else if (jobmatcher.find()) {
                    captureNextLineForJob = true;
                    job_name = jobmatcher.group(1).trim();
                    job_user = jobmatcher.group(2).trim();
                    job_status = jobmatcher.group(3).trim();
                } else if (jobdescmatcher.find()) {
                    if (captureNextLineForJob) {
                        captureNextLineForJob = false;
                        revisions.addTaskToLastEntry(job_name, job_user, job_status, jobdescmatcher.group(1).trim());
                    } else {
                        msg += jobdescmatcher.group(1).trim() + "\r\n";
                    }
                } else if (pathmatcher.find()) {
                    String action = pathmatcher.group(3).trim();
                    revisions.addPathToLastEntry(pathmatcher.group(1).trim(), action, pathmatcher.group(2).trim());
                }
            }
        }.execute();

        return revisions;
    }

    /**
     * Adds common options for various P4 commands.
     * 
     * @param cmdLine the commandline object to add options to
     */
    private void addCommonOpts(Commandline cmdLine) {
        cmdLine.createArgument().setLine("-p " + getPort() + " -u " + getUser());
        if (!Luntbuild.isEmpty(getPassword())) {
			Commandline.Argument arg = cmdLine.createArgument();
            arg.setLine("-P " + getPassword());
			arg.setDescriptiveLine("-P ******");
		}
    }

    /**
     * Selection model used for user interface of <code>PerforceAdaptor</code>.
     */
    static class PerforceWebInterfaceSelectionModel implements IPropertySelectionModel {
        String[] values = {"", "P4DB", "P4Web"};
        String[] display_values = {"", "P4DB", "P4Web"};

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
            return this.display_values[index];
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
	 * An Perforce module definition.
	 *
	 * @author robin shine
	 */
    public class PerforceModule extends Module {
        /**
         * Keep tracks of version of this class, used when do serialization-deserialization
         */
        static final long serialVersionUID = 1;
        private String depotPath;
        private String label;
        private String clientPath;

		/**
		 * Constructor, creates a blank Perforce module.
		 */
        public PerforceModule() {}

		/**
		 * Copy constructor, creates a Perforce module from another Perforce module.
		 * 
		 * @param module the module to create from
		 */
        public PerforceModule(PerforceModule module) {
            this.depotPath = module.depotPath;
            this.label = module.label;
            this.clientPath = module.clientPath;
        }

		/**
		 * @inheritDoc
		 */
        public List getProperties() {
            List properties = new ArrayList();
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "Depot path";
                }

                public String getDescription() {
                    return "Specify the Perforce depot side path, such as \"//depot/testperforce/...\".";
                }

                public String getValue() {
                    return getDepotPath();
                }

                public String getActualValue() {
                    return getActualDepotPath();
                }

                public void setValue(String value) {
                    setDepotPath(value);
                }
            });
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "Label";
                }

                public String getDescription() {
                    return "Specify the label for the above depot path. This property is " +
                            "optional. When empty, the latest version (head) of the above depot path will " +
                            "be retrieved.";
                }

                public String getValue() {
                    return getLabel();
                }

                public String getActualValue() {
                    return getActualLabel();
                }

                public boolean isRequired() {
                    return false;
                }

                public void setValue(String value) {
                    setLabel(value);
                }
            });
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "Client path";
                }

                public String getDescription() {
                    return "Specify the client side path, such as \"//myclient/testperforce/...\".";
                }

                public String getValue() {
                    return getClientPath();
                }

                public String getActualValue() {
                    return getActualClientPath();
                }

                public void setValue(String value) {
                    setClientPath(value);
                }
            });
            return properties;
        }

        /**
         * Gets the depot path.
         * 
         * @return the depot path
         */
        public String getDepotPath() {
            return depotPath;
        }

        /**
         * Gets the depot path. This method will parse OGNL variables.
         * 
         * @return the depot path
         */
        private String getActualDepotPath() {
        	return OgnlHelper.evaluateScheduleValue(getDepotPath());
        }

        /**
         * Sets the depot path.
         * 
         * @param depotPath the depot path
         */
        public void setDepotPath(String depotPath) {
            this.depotPath = depotPath;
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
        public void setLabel(String label) {
            this.label = label;
        }

        /**
         * Gets the client path.
         * 
         * @return the client path
         */
        public String getClientPath() {
            return clientPath;
        }

        /**
         * Gets the client path. This method will parse OGNL variables.
         * 
         * @return the client path
         */
        private String getActualClientPath() {
        	return OgnlHelper.evaluateScheduleValue(getClientPath());
        }

        /**
         * Sets the client path.
         * 
         * @param clientPath the client path
         */
        public void setClientPath(String clientPath) {
            this.clientPath = clientPath;
        }

	    /**
	     * @inheritDoc
	     * @see PerforceModuleFacade
	     */
        public ModuleFacade getFacade() {
            PerforceModuleFacade facade = new PerforceModuleFacade();
            facade.setClientPath(getClientPath());
            facade.setDepotPath(getDepotPath());
            facade.setLabel(getLabel());
            return facade;
        }

	    /**
	     * @inheritDoc
	     * @throws RuntimeException if the facade is not an <code>PerforceModuleFacade</code>
	     * @see PerforceModuleFacade
	     */
        public void setFacade(ModuleFacade facade) {
            if (facade instanceof PerforceModuleFacade) {
                PerforceModuleFacade perforceModuleFacade = (PerforceModuleFacade) facade;
                setClientPath(perforceModuleFacade.getClientPath());
                setDepotPath(perforceModuleFacade.getDepotPath());
                setLabel(perforceModuleFacade.getLabel());
            } else
                throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        }
    }

    /**
     * @inheritDoc
     * @see PerforceAdaptorFacade
     */
    public void saveToFacade(VcsFacade facade) {
    	// TODO throw RuntimeException if the facade is not the right class
        PerforceAdaptorFacade perforceFacade = (PerforceAdaptorFacade) facade;
        perforceFacade.setLineEnd(getLineEnd());
        perforceFacade.setPassword(getPassword());
        perforceFacade.setPort(getPort());
        perforceFacade.setUser(getUser());
		perforceFacade.setP4Dir(getP4Dir());
        perforceFacade.setWebInterface(getWebInterface());
        perforceFacade.setWebUrl(getWebUrl());
		perforceFacade.setChangelist(getChangelist());
    }

    /**
     * @inheritDoc
     * @throws RuntimeException if the facade is not an <code>PerforceAdaptorFacade</code>
     * @see PerforceAdaptorFacade
     */
    public void loadFromFacade(VcsFacade facade) {
        if (!(facade instanceof PerforceAdaptorFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        PerforceAdaptorFacade perforceFacade = (PerforceAdaptorFacade) facade;
        setLineEnd(perforceFacade.getLineEnd());
        setPassword(perforceFacade.getPassword());
        setPort(perforceFacade.getPort());
        setUser(perforceFacade.getUser());
		setP4Dir(perforceFacade.getP4Dir());
        setWebInterface(perforceFacade.getWebInterface());
        setWebUrl(perforceFacade.getWebUrl());
		setChangelist(perforceFacade.getChangelist());
    }

    /**
     * @inheritDoc
     * @see PerforceAdaptorFacade
     */
	public VcsFacade constructFacade() {
		return new PerforceAdaptorFacade();
	}
}
