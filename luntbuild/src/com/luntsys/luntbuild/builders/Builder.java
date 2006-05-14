/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-12-16
 * Time: 21:16:37
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
package com.luntsys.luntbuild.builders;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.remoting.facade.BuilderFacade;
import com.luntsys.luntbuild.utility.*;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The base class for all builders.
 *
 * @author robin shine
 */
public abstract class Builder implements Serializable {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1L;

	public static final String BUILDER_LOG = "builder_log.txt";
	public static final String ARTIFACTS_DIR = "artifacts";

	private String buildSuccessCondition;
	private String environments;

	/**
	 * Get display name for current builders
	 *
	 * @return display name for current builders
	 */
	public abstract String getDisplayName();

	/**
	 * @return name of the icon for this version control system. Icon should be put into
	 * the images directory of the web application.
	 */
	public abstract String getIconName();

	/**
	 * Get properties of this builders. These properites will be shown to user and expect
	 * input from user.
	 *
	 * @return list of properties can be configured by user
	 */
	public List getProperties() {
		List properties = getBuilderSpecificProperties();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "environment variables";
			}

			public String getDescription() {
				return "Environment variables to set before running this builder. For example:\n" +
						"buildVersion=${build.version}\n" +
						"scheduleName=${build.schedule.name}\n" +
						"You should set one variable per line, and ognl expression can be inserted to form the value provided they are " +
						"wrapped inside ${...}. For valid ognl expressions can be used here, please refer to user manual.";
			}

			public boolean isRequired() {
				return false;
			}

			public boolean isMultiLine() {
				return true;
			}

			public String getValue() {
				return getEnvironments();
			}

			public void setValue(String value) {
				setEnvironments(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "build success condition";
			}

			public String getDescription() {
				return "The build success condition is an ognl expression used to determine if build of current project is successful. " +
						"If left empty, the \"result==0\" value will be assumed. Refer to user manual for detailed information.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getBuildSuccessCondition();
			}

			public void setValue(String value) {
				setBuildSuccessCondition(value);
			}
		});
		return properties;
	}

	public abstract List getBuilderSpecificProperties();

	/**
	 * Validates properties of this builders
	 *
	 * @throws com.luntsys.luntbuild.utility.ValidationException
	 *
	 */
	public void validate() {
		Iterator it = getProperties().iterator();
		while (it.hasNext()) {
			DisplayProperty property = (DisplayProperty) it.next();
			if (property.isRequired() && (Luntbuild.isEmpty(property.getValue())))
				throw new ValidationException("Property \"" + property.getDisplayName() + "\" can not be empty!");
			if (!property.isMultiLine() && !property.isSecret() && property.getValue() != null)
				property.setValue(property.getValue().trim());
		}
		if (!Luntbuild.isEmpty(getEnvironments())) {
			BufferedReader reader = new BufferedReader(new StringReader(getEnvironments()));
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					String fields[] = line.split("=");
					if (fields.length != 2 || fields[0].trim().equals(""))
						throw new ValidationException("Invalid entry of property \"environment variables\": " + line);
				}
			} catch (IOException e) {
				// ignores
			}
		}
		if (!Luntbuild.isEmpty(getBuildSuccessCondition())) {
			try {
				Ognl.parseExpression(getBuildSuccessCondition());
			} catch (OgnlException e) {
				throw new ValidationException("Invalid build success condition: " + e.getMessage());
			}
		}
	}

	/**
	 * Get facade object of this builders
	 *
	 * @return facade object of this builders
	 */
	public BuilderFacade getFacade() {
		BuilderFacade facade = constructFacade();
		facade.setEnvironments(getEnvironments());
		facade.setBuildSuccessCondition(getBuildSuccessCondition());
		saveToFacade(facade);
		return facade;
	}

	/**
	 * Construct builders facade object
	 * @return builders facade object
	 */ 
	public abstract BuilderFacade constructFacade();

	/**
	 * Load value from builders facade
	 * @param facade
	 */
	public abstract void loadFromFacade(BuilderFacade facade);

	/**
	 * Save value to builders facade
	 * @param facade
	 */
	public abstract void saveToFacade(BuilderFacade facade);

	/**
	 * Set facade object of this builders
	 *
	 * @param facade
	 */
	public void setFacade(BuilderFacade facade) {
		setEnvironments(facade.getEnvironments());
		setBuildSuccessCondition(facade.getBuildSuccessCondition());
		loadFromFacade(facade);
	}

	/**
	 * Perform build for specified build object
	 *
	 * @throws Throwable
	 *
	 */
	public void build(Map properties, Build build) throws Throwable {
		String publishDirPath = build.getPublishDir(properties);
		String logPath = publishDirPath + File.separator + BUILDER_LOG;

		// create a ant project to receive log
		Project antProject = new Project();
		antProject.init();
		LuntbuildLogger buildLogger = new LuntbuildLogger();
		// log will be written without any filter or decoration
		buildLogger.setDirectMode(true);
		antProject.addBuildListener(buildLogger);
		PrintStream logStream = null;
		try {
			logStream = new PrintStream(new FileOutputStream(logPath));
			buildLogger.setOutputPrintStream(logStream);
			buildLogger.setErrorPrintStream(logStream);

			String buildCmd = constructBuildCmd(properties, build);

			buildCmd = Luntbuild.evaluateExpression(new BuildCommandRoot(build), buildCmd);
			Commandline cmdLine = Luntbuild.parseCmdLine(buildCmd);
			antProject.log("Run command: " + buildCmd, Project.MSG_INFO);

			Environment env = new Environment();
			if (!Luntbuild.isEmpty(getEnvironments())) {
				String environments = Luntbuild.evaluateExpression(new BuildCommandRoot(build), getEnvironments());
				BufferedReader reader = new BufferedReader(new StringReader(environments));
				try {
					String line;
					while ((line = reader.readLine()) != null) {
						String fields[] = line.split("=");
						if (fields.length == 2) {
							String name = fields[0].trim();
							String value = fields[1].trim();
							if (!name.equals("")) {
								Environment.Variable var = new Environment.Variable();
								var.setKey(name);
								var.setValue(value);
								env.addVariable(var);
							}
						}
					}
				} catch (IOException e) {
					// ignores
				}
			}

			MyExecTask exec = new MyExecTask(getDisplayName(), antProject, constructBuildCmdDir(properties, build), cmdLine, env,
					null, Project.MSG_INFO);

			int result;
			result = exec.executeAndGetResult();

			if (!isBuildSuccess(result, new File(logPath)))
				throw new BuildException(getDisplayName() + " failed: build success condition not met!");
		} finally {
			if (logStream != null) {
				logStream.close();
				buildLogger.setOutputPrintStream(null);
				buildLogger.setErrorPrintStream(null);
			}
		}
	}

	/**
	 * Constructs the command to run build
	 *
	 * @return the command to run build, should not be null
	 */
	public abstract String constructBuildCmd(Map properties, Build build) throws IOException;

	/**
	 * Constructs the directory to run build command in
	 *
	 * @return the directory to run build command in. Null if do not care where to run build command
	 */
	public abstract String constructBuildCmdDir(Map properties, Build build);

	/**
	 * Get build success condition for this builders
	 *
	 * @return build success condition for this builders, Null if not exist
	 */
	public String getBuildSuccessCondition() {
		return buildSuccessCondition;
	}

	/**
	 * Set build success condition for this builders
	 *
	 * @param buildSuccessCondition
	 */
	public void setBuildSuccessCondition(String buildSuccessCondition) {
		this.buildSuccessCondition = buildSuccessCondition;
	}

	public String getEnvironments() {
		return environments;
	}

	public void setEnvironments(String environments) {
		this.environments = environments;
	}

	private boolean isBuildSuccess(int result, File logFile) {
		BuildSuccessConditionRoot.setResult(result);
		BuildSuccessConditionRoot.setLogFile(logFile);

		BuildSuccessConditionRoot ognlRoot = new BuildSuccessConditionRoot();
		String buildSuccessCondition;
		if (!Luntbuild.isEmpty(getBuildSuccessCondition()))
			buildSuccessCondition = getBuildSuccessCondition();
		else
			buildSuccessCondition = "result == 0";
		try {
			Boolean buildSuccessValue = (Boolean) Ognl.getValue(Ognl.parseExpression(buildSuccessCondition),
					Ognl.createDefaultContext(ognlRoot), ognlRoot, Boolean.class);
			if (buildSuccessValue == null)
				return false;
			else
				return buildSuccessValue.booleanValue();
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		} catch (OgnlException e) {
			throw new RuntimeException(e);
		}
	}
}
