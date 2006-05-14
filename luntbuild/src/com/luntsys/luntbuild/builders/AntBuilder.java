/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-12-16
 * Time: 21:44:26
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

import com.luntsys.luntbuild.remoting.facade.AntBuilderFacade;
import com.luntsys.luntbuild.remoting.facade.BuilderFacade;
import com.luntsys.luntbuild.remoting.Constants;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.db.Build;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Ant builder implementation
 * @author robin shine
 */
public class AntBuilder extends Builder {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1L;

	/**
	 * The command to run ant
	 */
	private String command = "C:\\apache-ant-1.6.2\\bin\\ant.bat " +
			"-DbuildVersion=\"${build.version}\" -DartifactsDir=\"${build.artifactsDir}\" " +
			"-DbuildDate=\"${build.startDate}\"";

	/**
	 * Path to ant build script
	 */
	private String buildScriptPath;

	/**
	 * Targets to build
	 */
	private String targets;

	public AntBuilder() {
		setBuildSuccessCondition("result==0 and logContainsLine(\"BUILD SUCCESSFUL\")");	
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getBuildScriptPath() {
		return buildScriptPath;
	}

	public void setBuildScriptPath(String buildScriptPath) {
		this.buildScriptPath = buildScriptPath;
	}

	public String getTargets() {
		return targets;
	}

	public void setTargets(String targets) {
		this.targets = targets;
	}

	public String getDisplayName() {
		return "ant builder";
	}

	public String getIconName() {
		return "ant.gif";
	}

	public List getBuilderSpecificProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "command to run ant";
			}

			public String getDescription() {
				return "Specify command to run ant(normally path to ant.bat or ant shell script) here. For example: " +
						"/path/to/ant -DbuildVersion=\"${build.version}\" -DartifactsDir=\"${build.artifactsDir}\". String wrapped inside ${...} will be interpreted " +
						"as ognl expression, and be evaluated before executing. For valid ognl expressions here, please refer to " +
						"user manual. NOTE: Single argument with spaces should be quoted in order not be interpreted as multiple " +
						"arguments.";
			}

			public boolean isMultiLine() {
				return true;
			}

			public String getValue() {
				return getCommand();
			}

			public void setValue(String value) {
				setCommand(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "build script path";
			}

			public String getDescription() {
				return "The path for the ant build script. It will be assumed to be relative to the project working directory " +
						"if this path is not an absolute path. Refer to <a href=\"manual/index.html#chapter8\">user manual</a> " +
						"for how to writing  new ant build file or wrapping your existing build script.";
			}

			public String getValue() {
				return getBuildScriptPath();
			}

			public void setValue(String value) {
				setBuildScriptPath(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "build targets";
			}

			public String getDescription() {
				return "Specify targets to build. Use space to seperate different targets(target name " +
						"with spaces should be quoted in order not to be interpreted as multiple targets). " +
						"Also you can use ${...} to pass variables to the target name. For example you " +
						"can use ${build.schedule.name} to archieve different targets for different schedules. " +
						"For valid ognl expressions here, please refer to user manual.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getTargets();
			}

			public void setValue(String value) {
				setTargets(value);
			}
		});
		return properties;
	}

	public void validate() {
		super.validate();
		try {
			Luntbuild.validateExpression(getCommand());
		} catch (ValidationException e) {
			throw new ValidationException("Invalid command to run ant: " + e.getMessage());
		}
		if (!Luntbuild.isEmpty(getTargets())) {
			try {
				Luntbuild.validateExpression(getTargets());
			} catch (ValidationException e) {
				throw new ValidationException("Invalid targets: " + e.getMessage());
			}
		}
	}

	/**
	 * Construct command to run ant
	 *
	 * @return
	 */
	public String constructBuildCmd(Map properties, Build build) throws IOException {
		String antCmd = getCommand();
		antCmd = antCmd.replace('\n', ' ');
		antCmd = antCmd.replace('\r', ' ');

		// set ant log level based on project's log level if log level does not been explicitely specified in ant command
		if (!antCmd.matches(".*\\s(-q|-quiet)($|\\s.*)") && !antCmd.matches(".*\\s(-v|-verbose)($|\\s.*)") &&
				!antCmd.matches(".*\\s(-d|-debug)($|\\s.*)")) {
			if (build.getSchedule().getProject().getLogLevel() == Constants.LOG_LEVEL_BRIEF)
				antCmd += " -q";
			else if (build.getSchedule().getProject().getLogLevel() == Constants.LOG_LEVEL_VERBOSE)
				antCmd += " -v";
		}

		String buildScriptAbsolutePath = build.getSchedule().getProject().resolveAbsolutePath(properties, getBuildScriptPath());
		antCmd += "  -buildfile \"" + buildScriptAbsolutePath + "\"";
		if (!Luntbuild.isEmpty(getTargets()))
			antCmd += " " + getTargets();

		return antCmd;
	}

	public String constructBuildCmdDir(Map properties, Build build) {
		String buildScriptAbsolutePath = build.getSchedule().getProject().resolveAbsolutePath(properties, getBuildScriptPath());
		return new File(buildScriptAbsolutePath).getParent();
	}

	public BuilderFacade constructFacade() {
		return new AntBuilderFacade();
	}

	public void loadFromFacade(BuilderFacade facade) {
		if (!(facade instanceof AntBuilderFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		AntBuilderFacade antBuilderFacade = (AntBuilderFacade) facade;
		setCommand(antBuilderFacade.getCommand());
		setBuildScriptPath(antBuilderFacade.getBuildScriptPath());
		setTargets(antBuilderFacade.getBuildTarget());
	}

	public void saveToFacade(BuilderFacade facade) {
		if (!(facade instanceof AntBuilderFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		AntBuilderFacade antBuilderFacade = (AntBuilderFacade) facade;
		antBuilderFacade.setCommand(getCommand());
		antBuilderFacade.setBuildScriptPath(getBuildScriptPath());
		antBuilderFacade.setBuildTarget(getTargets());
	}
}