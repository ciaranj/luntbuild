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
import com.luntsys.luntbuild.facades.lb12.BuilderFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

import org.apache.tapestry.form.IPropertySelectionModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private String command = " \"${build.version}\" \"${build.artifactsDir}\" \"${build.startDate}\"";

	/**
	 * The directory to run command, defaults to be current project's work directory if left empty
	 */
	private String dirToRunCmd;

	/**
	 * A Yes/No value determining whether to wait for the command to complete or to fork and continue
	 */
	private String waitForFinish;

    public CommandBuilder() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            this.command = "\"${build.schedule.workingDir}\\build\\build.bat\"" + this.command;
        } else {
            this.command = "\"${build.schedule.workingDir}/build/build\"" + this.command;
        }
    }

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

	public String getWaitForFinish() {
		return waitForFinish;
	}

	public void setWaitForFinish(String waitForFinish) {
		this.waitForFinish = waitForFinish;
	}

	public String getDisplayName() {
		return "Command builder";
	}

	public String getIconName() {
		return "command.gif";
	}

	public List getBuilderSpecificProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Build command";
			}

			public String getDescription() {
				return "Specify the build command. For example: " +
						"/path/to/command.bat \"${build.version}\" \"${build.artifactsDir}\". String enclosed by ${...} will be interpreted " +
						"as OGNL expression, and it will be evaluated before execution. For valid OGNL expressions in this context, please refer to " +
						"the User's Guide. NOTE. A single argument containing spaces should be quoted in order not be interpreted as multiple " +
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
				return "Run command in directory";
			}

			public String getDescription() {
				return "The directory path to run the build command in. If this path is not an absolute path, " +
                        "it is assumed to be relative to the project work directory.";
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
		});
		DisplayProperty p = new DisplayProperty() {
			public String getDisplayName() {
				return "Wait for process to finish before continuing?";
			}

			public String getDescription() {
				return "This property determines whether the build will wait for the command " +
                    "execution to complete before continuing.";
			}

			public boolean isRequired() {
				return false;
			}

			public boolean isSelect() {
				return true;
			}

			public String getValue() {
				return getWaitForFinish();
			}

			public void setValue(String value) {
				setWaitForFinish(value);
			}
		};
		IPropertySelectionModel model = new WaitYesNoSelectionModel();
		p.setSelectionModel(model);
		properties.add(p);

		return properties;
	}

	public void validate() {
		super.validate();
		try {
			Luntbuild.validateExpression(getCommand());
		} catch (ValidationException e) {
			throw new ValidationException("Invalid command to run: " + e.getMessage());
		}
	}

	/**
	 * Construct command to run cmd
	 *
	 * @return command to run cmd
	 */
	public String constructBuildCmd(Build build) throws IOException {
		String buildCmd = getCommand();
		buildCmd = buildCmd.replace('\n', ' ');
		buildCmd = buildCmd.replace('\r', ' ');

		return buildCmd;
	}

	public String constructBuildCmdDir(Build build) {
		if (Luntbuild.isEmpty(getDirToRunCmd()))
			return build.getSchedule().getWorkDirRaw();
		else
			return build.getSchedule().resolveAbsolutePath(getDirToRunCmd());
	}

	public BuilderFacade constructFacade() {
		return new com.luntsys.luntbuild.facades.lb12.CommandBuilderFacade();
	}

	public void loadFromFacade(BuilderFacade facade) {
		if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.CommandBuilderFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		com.luntsys.luntbuild.facades.lb12.CommandBuilderFacade cmdBuilderFacade = (com.luntsys.luntbuild.facades.lb12.CommandBuilderFacade) facade;
		setCommand(cmdBuilderFacade.getCommand());
		setDirToRunCmd(cmdBuilderFacade.getDirToRunCmd());
		setWaitForFinish(cmdBuilderFacade.getWaitForFinish());
	}

	public void saveToFacade(BuilderFacade facade) {
		if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.CommandBuilderFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		com.luntsys.luntbuild.facades.lb12.CommandBuilderFacade cmdBuilderFacade = (com.luntsys.luntbuild.facades.lb12.CommandBuilderFacade) facade;
		cmdBuilderFacade.setCommand(getCommand());
		cmdBuilderFacade.setDirToRunCmd(getDirToRunCmd());
		cmdBuilderFacade.setWaitForFinish(getWaitForFinish());
	}

	class WaitYesNoSelectionModel implements IPropertySelectionModel {
		String[] values = {"Yes", "No"};

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
}
