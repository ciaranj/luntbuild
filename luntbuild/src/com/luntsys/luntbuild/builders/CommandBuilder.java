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
import com.luntsys.luntbuild.remoting.facade.BuilderFacade;
import com.luntsys.luntbuild.remoting.facade.CommandBuilderFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implements for a command line builder
 */
public class CommandBuilder extends Builder {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1L;

	/**
	 * The command to run ant
	 */
	private String command = "\"${build.schedule.project.workingDir}\\build\\build.bat\" \"${build.version}\" \"${build.artifactsDir}\" \"${build.startDate}\"";

	/**
	 * The directory to run command, defaults to be current project's working directory if left empty
	 */
	private String dirToRunCmd;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getDirToRunCmd() {
		return dirToRunCmd;
	}

	public void setDirToRunCmd(String dirToRunCmd) {
		this.dirToRunCmd = dirToRunCmd;
	}

	public String getDisplayName() {
		return "command builder";
	}

	public String getIconName() {
		return "command.gif";
	}

	public List getBuilderSpecificProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "build command";
			}

			public String getDescription() {
				return "Specify build command here. For example: " +
						"/path/to/command.bat \"${build.version}\" \"${build.artifactsDir}\". String wrapped inside ${...} will be interpreted " +
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
				return "run command in directory";
			}

			public String getDescription() {
				return "The directory path to run the build command in. It will be assumed to be relative to the project working directory " +
						"if this path is not an absolute path.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getDirToRunCmd();
			}

			public void setValue(String value) {
				setDirToRunCmd(value);
			}
		});;
		return properties;
	}

	public void validate() {
		super.validate();
		try {
			Luntbuild.validateExpression(getCommand());
		} catch (ValidationException e) {
			throw new ValidationException("Invalid command to run ant: " + e.getMessage());
		}
	}

	/**
	 * Construct command to run ant
	 *
	 * @return
	 */
	public String constructBuildCmd(Map properties, Build build) throws IOException {
		String buildCmd = getCommand();
		buildCmd = buildCmd.replace('\n', ' ');
		buildCmd = buildCmd.replace('\r', ' ');

		return buildCmd;
	}

	public String constructBuildCmdDir(Map properties, Build build) {
		if (Luntbuild.isEmpty(getDirToRunCmd()))
			return build.getSchedule().getProject().getWorkingDir(properties);
		else
			return build.getSchedule().getProject().resolveAbsolutePath(properties, getDirToRunCmd());
	}

	public BuilderFacade constructFacade() {
		return new CommandBuilderFacade();
	}

	public void loadFromFacade(BuilderFacade facade) {
		if (!(facade instanceof CommandBuilderFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		CommandBuilderFacade cmdBuilderFacade = (CommandBuilderFacade) facade;
		setCommand(cmdBuilderFacade.getCommand());
		setDirToRunCmd(cmdBuilderFacade.getDirToRunCmd());
	}

	public void saveToFacade(BuilderFacade facade) {
		if (!(facade instanceof CommandBuilderFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		CommandBuilderFacade cmdBuilderFacade = (CommandBuilderFacade) facade;
		cmdBuilderFacade.setCommand(getCommand());
		cmdBuilderFacade.setDirToRunCmd(getDirToRunCmd());
	}
}