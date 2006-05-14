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

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.remoting.Constants;
import com.luntsys.luntbuild.remoting.facade.AntBuilderFacade;
import com.luntsys.luntbuild.remoting.facade.BuilderFacade;
import com.luntsys.luntbuild.remoting.facade.MavenBuilderFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Maven builder implementation
 * @author robin shine
 */
public class MavenBuilder extends Builder {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1L;

	/**
	 * The command to run maven
	 */
	private String command = "\"C:\\Program Files\\Apache Software Foundation\\Maven 1.0.2\\bin\\maven.bat\" " +
			"-DbuildVersion=\"${build.version}\" -DartifactsDir=\"${build.artifactsDir}\"";

	/**
	 * Directory to run maven in
	 */
	private String dirToRunMaven;

	/**
	 * Goals to build
	 */
	private String goals = "${build.schedule.name}";

	public MavenBuilder() {
		setBuildSuccessCondition("result==0 and logContainsLine(\"BUILD SUCCESSFUL\")");	
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getDirToRunMaven() {
		return dirToRunMaven;
	}

	public void setDirToRunMaven(String dirToRunMaven) {
		this.dirToRunMaven = dirToRunMaven;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public String getDisplayName() {
		return "maven builder";
	}

	public String getIconName() {
		return "maven.png";
	}

	public List getBuilderSpecificProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "command to run maven";
			}

			public String getDescription() {
				return "Specify command to run maven(normally path to maven.bat or maven shell script) here. For example: " +
						"/path/to/maven -DbuildVersion=\"${build.version}\" -DartifactsDir=\"${build.artifactsDir}\". String wrapped inside ${...} will be interpreted " +
						"as ognl expression, and be evaluated before executing. Refer to user manual for valid ognl expressions can being used here, as well as how to " +
						"instruct maven to use luntbuild provided version number to build. NOTE: Single argument with spaces should be quoted in order not be interpreted " +
						"as multiple arguments.";
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
				return "directory to run maven in";
			}

			public String getDescription() {
				return "Specify the directory to run maven in. It will be assumed to be relative " +
						"to the project working directory if this path is not an absolute path. And project working " +
						"directory itself will be assumed if this property leaves empty.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getDirToRunMaven();
			}

			public void setValue(String value) {
				setDirToRunMaven(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "goals to build";
			}

			public String getDescription() {
				return "Specify goals to build. Use space to seperate different goals(goal name " +
						"with spaces should be quoted in order not to be interpreted as multiple goals). " +
						"Also you can use ${...} to pass variables to the goal name. For example you " +
						"can use ${build.schedule.name} to archieve different goals for different schedules. " +
						"For valid ognl expressions here, please refer to user manual.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getGoals();
			}

			public void setValue(String value) {
				setGoals(value);
			}
		});
		return properties;
	}

	public void validate() {
		super.validate();
		try {
			Luntbuild.validateExpression(getCommand());
		} catch (ValidationException e) {
			throw new ValidationException("Invalid command to run maven: " + e.getMessage());
		}
		if (!Luntbuild.isEmpty(getGoals())) {
			try {
				Luntbuild.validateExpression(getGoals());
			} catch (ValidationException e) {
				throw new ValidationException("Invalid goals: " + e.getMessage());
			}
		}
	}

	/**
	 * Construct command to run ant
	 *
	 * @return
	 */
	public String constructBuildCmd(Map properties, Build build) throws IOException {
		String mavenCmd = getCommand();
		mavenCmd = mavenCmd.replace('\n', ' ');
		mavenCmd = mavenCmd.replace('\r', ' ');

		// set maven log level based on project's log level if log level does not been explicitely specified in maven command
		if (!mavenCmd.matches(".*\\s(-X|--debug)($|\\s.*)") && !mavenCmd.matches(".*\\s(-q|--quiet)($|\\s.*)")) {
			if (build.getSchedule().getProject().getLogLevel() == Constants.LOG_LEVEL_BRIEF)
				mavenCmd += " -q";
			else if (build.getSchedule().getProject().getLogLevel() == Constants.LOG_LEVEL_VERBOSE)
				mavenCmd += " -X";
		}

		mavenCmd += " -d \"" + build.getSchedule().getProject().resolveAbsolutePath(properties, getDirToRunMaven()) + "\"";
		if (!Luntbuild.isEmpty(getGoals()))
			mavenCmd += " " + getGoals();

		return mavenCmd;
	}

	public String constructBuildCmdDir(Map properties, Build build) {
		return build.getSchedule().getProject().resolveAbsolutePath(properties, getDirToRunMaven());
	}

	public BuilderFacade constructFacade() {
		return new AntBuilderFacade();
	}

	public void loadFromFacade(BuilderFacade facade) {
		if (!(facade instanceof MavenBuilderFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		MavenBuilderFacade mavenBuilderFacade = (MavenBuilderFacade) facade;
		setCommand(mavenBuilderFacade.getCommand());
		setDirToRunMaven(mavenBuilderFacade.getDirToRunMaven());
		setGoals(mavenBuilderFacade.getGoals());
	}

	public void saveToFacade(BuilderFacade facade) {
		if (!(facade instanceof MavenBuilderFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		MavenBuilderFacade mavenBuilderFacade = (MavenBuilderFacade) facade;
		mavenBuilderFacade.setCommand(getCommand());
		mavenBuilderFacade.setDirToRunMaven(getDirToRunMaven());
		mavenBuilderFacade.setGoals(getGoals());
	}
}