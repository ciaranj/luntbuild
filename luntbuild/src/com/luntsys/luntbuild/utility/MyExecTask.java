/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-7-30
 * Time: 8:49:31
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
package com.luntsys.luntbuild.utility;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import com.luntsys.luntbuild.ant.Execute;
import org.apache.tools.ant.taskdefs.optional.perforce.P4Handler;
import org.apache.tools.ant.taskdefs.optional.perforce.P4HandlerAdapter;
import com.luntsys.luntbuild.ant.Commandline;
import org.apache.tools.ant.types.Environment;

import java.io.IOException;
import java.io.File;

/**
 * This class supplies an altenate way to run external command with
 * the abibility to supply input to external command and process the
 * output of external command
 *
 * @author robin shine
 */
public class MyExecTask extends Task {
	private Commandline cmdLine;
	private String input;
	Environment env;
	String workingDir;
	int outputPriority = Project.MSG_INFO;

	/**
	 * The constructor supplies various parameter
	 *
	 * @param taskName       name of this task, will be used when log
	 * @param project        the ant project for this task, will be used to log
	 * @param workingDir     working directory to execute command line, maybe null
	 * @param cmdLine        command line object for this task
	 * @param env            environments to execute command line, maybe null
	 * @param input          input to the command line, maybe null
	 * @param outputPriority specify the log priority for command output. The log
	 *                       priority is constants defined in {@link Project} such as {@link Project.MSG_INFO}, etc.
	 *                       if this value is set to -1, then output will never be logged
	 */
	public MyExecTask(String taskName, Project project, String workingDir, Commandline cmdLine,
					  Environment env, String input, int outputPriority) {
		if (taskName != null) {
			setTaskType(taskName);
			setTaskName(taskName);
		}
		if (project != null)
			setProject(project);
		else {
			setProject(new Project());
			getProject().init();
		}
		this.cmdLine = cmdLine;
		this.input = input;
		this.workingDir = workingDir;
		this.env = env;
		this.outputPriority = outputPriority;
	}

	public MyExecTask(String taskName, Project project, Commandline cmdLine, int outputPriority) {
		this(taskName, project, null, cmdLine, null, null, outputPriority);
	}

	public MyExecTask(String taskName, Project project, Commandline cmdLine, String input, int outputPriority) {
		this(taskName, project, null, cmdLine, null, input, outputPriority);
	}

	public MyExecTask(Commandline cmdLine, int outputPriority) {
		this(null, null, null, cmdLine, null, null, outputPriority);
	}

	public MyExecTask(String workingDir, Commandline cmdLine, int outputPriority) {
		this(null, null, workingDir, cmdLine, null, null, outputPriority);
	}

	/**
	 * Execute the task
	 *
	 * @throws BuildException
	 */
	public final void execute() throws BuildException {
		int result = executeAndGetResult();
		if (result != 0)
			throw new BuildException("ERROR: Fail to command: " +
					cmdLine.describeCommand() + ", returned code: " + result);
	}

	public int executeAndGetResult() throws BuildException {
		P4Handler handler = new P4HandlerAdapter() {
			public void processStdout(String line) {
				if (outputPriority != -1)
					log(line, outputPriority);
				handleStdout(line);
			}

			public void processStderr(String line) {
				log(line, Project.MSG_WARN);
				handleStderr(line);
			}
		};
		if (input != null)
			handler.setOutput(input);
		Execute exec = new Execute();
		exec.setCommandline(cmdLine.getCommandline());
		if (env != null)
			exec.setEnvironment(env.getVariables());
		if (workingDir != null)
			exec.setWorkingDirectory(new File(workingDir));
		exec.setAntRun(getProject());
		exec.setStreamHandler(handler);
		try {
			getProject().log("Execute command: " + Commandline.describeCommand(cmdLine),
					Project.MSG_VERBOSE);
			return exec.execute();
		} catch (IOException e) {
			throw new BuildException("ERROR: " + e.getMessage());
		}
	}

	/**
	 * Sub class may override this method to process one line of output
	 *
	 * @param line
	 */
	public void handleStdout(String line) {
		// does nothing
	}

	/**
	 * Sub class may override this method to process one line of error
	 *
	 * @param line
	 */
	public void handleStderr(String line) {
		// does nothing
	}
}
