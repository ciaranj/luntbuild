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

import com.luntsys.luntbuild.remoting.*;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.utility.*;
import com.luntsys.luntbuild.utility.Revisions;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.perforce.*;
import org.apache.tools.ant.types.Commandline;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Perforce adaptor
 *
 * @author robin shine
 */
public class PerforceAdaptor extends VcsAdaptor {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;
	/**
	 * The p4 client for a particular round of build execution
	 */
	private static ThreadLocal client = new ThreadLocal();
	private static final SimpleDateFormat P4_DATE_FORMAT =
			new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss");

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

	public String getVcsDisplayName() {
		return "Perforce";
	}

	public List getProjectLevelProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "perforce port";
			}

			public String getDescription() {
				return "The perforce port in the format of <port>, or <servername>:<port>, " +
						"where <servername> and <port> will  be replaced by the actual perforce" +
						"server name and port number.";
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
				return "user name";
			}

			public String getDescription() {
				return "User name to access the above perforce server, this user should have " +
						"the right to create or edit client specifications besides checkout code and " +
						"label code";
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
				return "Password for the above use, maybe blank";
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
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "line end";
			}

			public String getDescription() {
				return "Set line ending character(s) for client text files. The following values are possible: <br>" +
						"local: Use mode native to the client<br>" +
						"unix: UNIX style<br>" +
						"mac: Macintosh style<br>" +
						"win: Windows style<br>" +
						"share: writes UNIX style but reads UNIX, Mac or Windows style. <br><br>" +
						"This property is optional. If not specified, the value will default to \"local\"";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getLineEnd();
			}

			public void setValue(String value) {
				setLineEnd(value);
			}
		});
		return properties;
	}

	public List getViewLevelProperties() {
		return new ArrayList();
	}

	/**
	 * Setup perforce client specification based on current build information
	 *
	 * @param workingDir current working directory for this build
	 * @param antProject
	 */
	private void setupP4Client(String workingDir, Project antProject) {
		antProject.log("Setup perforce client specification...", Project.MSG_INFO);

		// concatenate the view string of the perforce client
		String viewValue = "";
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			PerforceModule perforceModule = (PerforceModule) it.next();
			viewValue += perforceModule.getDepotPath() + " " + perforceModule.getClientPath() + "\n";
		}

		// edit p4 client specification to reflect current working dirctory and view mapping
		P4Client p4Client = new P4Client();
		initP4Cmd(p4Client, antProject);
		p4Client.setHostValue("");
		p4Client.setClientValue(getClient());
		p4Client.setOwnerValue(getUser());
		p4Client.setRootValue(workingDir);
		p4Client.setViewValue(viewValue);
		if (Luntbuild.isEmpty(getLineEnd()))
			p4Client.setLineEndValue("local");
		else
			p4Client.setLineEndValue(getLineEnd());
		p4Client.setTaskType("P4Client");
		p4Client.setTaskName("P4Client");
		p4Client.execute();
	}

	public void validateProjectLevelProperties() {
		super.validateProjectLevelProperties();
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
	private void setupP4Label(String label, Project antProject) {
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
			initP4Cmd(p4Label, antProject);
			p4Label.setClient(getClient());
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
	private void retrieveModule(PerforceModule module, Project antProject, boolean force) {
		if (force)
			antProject.log("Retrieve depot path: " + module.getDepotPath(), Project.MSG_INFO);
		else
			antProject.log("Update depot path: " + module.getDepotPath(), Project.MSG_INFO);

		P4Sync p4Sync = new P4Sync();
		initP4Cmd(p4Sync, antProject);
		p4Sync.setClient(getClient());
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
	private void labelModule(PerforceModule module, String label, Project antProject) {
		antProject.log("Label depot path: " + module.getDepotPath(), Project.MSG_INFO);

		P4Labelsync p4LabelSync = new P4Labelsync();
		initP4Cmd(p4LabelSync, antProject);
		p4LabelSync.setClient(getClient());
		p4LabelSync.setAdd(true);
		p4LabelSync.setView(module.getDepotPath());
		p4LabelSync.setName(label);
		p4LabelSync.setTaskType("P4LabelSync");
		p4LabelSync.setTaskName("P4LabelSync");
		p4LabelSync.execute();
	}

	public void checkout(Map properties, Build build, Project antProject) {
		String workingDir = build.getBuildSchedule().getView().getWorkingDir(properties);
		setupP4Client(workingDir, antProject);

		// retrieve modules
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			PerforceModule module = (PerforceModule) Luntbuild.cloneModule(this, (Vcs.Module) it.next());
			if (build.isRebuild() && Luntbuild.isEmpty(module.getLabel()))
				module.setLabel(Luntbuild.getLabelByVersion(build.getVersion()));
			if (build.isRebuild() || build.isCleanBuild())
				retrieveModule(module, antProject, true);
			else
				retrieveModule(module, antProject, false);
		}
	}

	public void label(Map properties, Build build, Project antProject) {
		setupP4Label(Luntbuild.getLabelByVersion(build.getVersion()), antProject);
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			PerforceModule module = (PerforceModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel()))
				labelModule(module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
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
		Pattern pattern = Pattern.compile("^//(\\w+)/");
		Matcher matcher = pattern.matcher(p4ClientPath);
		if (!matcher.find())
			throw new ValidationException("Property \"client path\" in module definition of perforce adaptor is invalid: " + p4ClientPath);
		else
			return matcher.group(1);
	}

	public String getPort() {
		return port;
	}

	public Module createNewModule() {
		return new PerforceModule();
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
		if (getPassword() != null && !getPassword().trim().equals(""))
			p4Cmd.setGlobalopts("-P " + getPassword());
		p4Cmd.setDetectErrorByRetCode(true);
		p4Cmd.setFailonerror(true);
	}

	private String getClient() {
		return (String) client.get();
	}

	private void setClient(String client) {
		PerforceAdaptor.client.set(client);
	}

	public void validateModules() {
		super.validateModules();
		PerforceModule firstModule = (PerforceModule) getModules().get(0);
		setClient(getP4Client(firstModule.getClientPath()));
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			PerforceModule perforceModule = (PerforceModule) it.next();
			if (!getP4Client(perforceModule.getClientPath()).equals(getClient()))
				throw new ValidationException("P4 Client name not consistent in modules definition!");
		}
	}

	public com.luntsys.luntbuild.utility.Revisions getRevisionsSince(Map properties, Build build, Project antProject) {
		String workingDir = build.getBuildSchedule().getView().getWorkingDir(properties);
		final Revisions revisions = new Revisions();
		setupP4Client(workingDir, antProject);
		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("p4");
		cmdLine.createArgument().setValue("-s");
		addCommonOpts(cmdLine);
		cmdLine.createArgument().setLine("-c " + getClient());
		cmdLine.createArgument().setLine("changes -s submitted");
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			PerforceModule module = (PerforceModule) it.next();
			if (Luntbuild.isEmpty(module.getLabel())) {
				cmdLine.createArgument().setValue(module.getClientPath() + "@" +
						P4_DATE_FORMAT.format(build.getStartDate()) + "," +
						P4_DATE_FORMAT.format(new Date()));
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
		cmdLine.createArgument().setLine("-c " + getClient());
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
		if (!Luntbuild.isEmpty(getPassword()))
			cmdLine.createArgument().setLine("-P " + getPassword());
	}

	public class PerforceModule extends Module {
		/**
		 * Keep tracks of version of this class, used when do serialization-deserialization
		 */
		static final long serialVersionUID = 1;
		private String depotPath;
		private String label;
		private String clientPath;

		public List getProperties() {
			List properties = new ArrayList();
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "Depot path";
				}

				public String getDescription() {
					return "Specify the perforce depot side path, such as \"//depot/testperforce/...\"";
				}

				public String getValue() {
					return getDepotPath();
				}

				public void setValue(String value) {
					setDepotPath(value);
				}
			});
			properties.add(new DisplayProperty() {
				public String getDisplayName() {
					return "label";
				}

				public String getDescription() {
					return "Specify the label for the above depot path. This property is" +
							"optional. When empty, latest version of the above depot path will" +
							"be retrieved.";
				}

				public String getValue() {
					return getLabel();
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
					return "Specify the client side path, such as \"//myclient/testperforce/...\"";
				}

				public String getValue() {
					return getClientPath();
				}

				public void setValue(String value) {
					setClientPath(value);
				}
			});
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

		public ModuleFacade getFacade() {
			PerforceModuleFacade facade = new PerforceModuleFacade();
			facade.setClientPath(getClientPath());
			facade.setDepotPath(getDepotPath());
			facade.setLabel(getLabel());
			return facade;
		}

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

	public VcsFacade getFacade() {
		PerforceAdaptorFacade facade = new PerforceAdaptorFacade();
		facade.setLineEnd(getLineEnd());
		facade.setPassword(getPassword());
		facade.setPort(getPort());
		facade.setUser(getUser());

		facade.getModules().clear();
		Iterator it = getModules().iterator();
		while (it.hasNext()) {
			PerforceModule module = (PerforceModule) it.next();
			facade.getModules().add(module.getFacade());
		}
		return facade;
	}

	public void setFacade(VcsFacade facade) {
		if (!(facade instanceof PerforceAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		PerforceAdaptorFacade perforceFacade = (PerforceAdaptorFacade) facade;
		setLineEnd(perforceFacade.getLineEnd());
		setPassword(perforceFacade.getPassword());
		setPort(perforceFacade.getPort());
		setUser(perforceFacade.getUser());

		getModules().clear();
		Iterator it = perforceFacade.getModules().iterator();
		while (it.hasNext()) {
			PerforceModuleFacade moduleFacade = (PerforceModuleFacade) it.next();
			PerforceModule module = new PerforceModule();
			module.setFacade(moduleFacade);
			getModules().add(module);
		}
	}
}
