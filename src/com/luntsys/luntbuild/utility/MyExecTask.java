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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import com.luntsys.luntbuild.ant.Execute;
import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.ant.perforce.P4HandlerAdapter;
import com.luntsys.luntbuild.ant.perforce.P4Handler;
import org.apache.tools.ant.types.Environment;

import java.io.IOException;
import java.io.File;

/**
 * Executes an external command.
 * 
 * <p>This class supplies an altenate way to run external commands with
 * the ability to supply input to the external command and process the
 * output of the external command.</p>
 * 
 * <p>This class is safe for remote hosts.</p>
 *
 * @author robin shine
 */
public class MyExecTask extends Task {
    protected static Log logger = LogFactory.getLog(MyExecTask.class);

	private Commandline cmdLine;
	private String input;
	Environment env;
	String workingDir;
	int outputPriority = Project.MSG_INFO;

	/**
	 * Creates a new execution task.
	 * 
	 * @param taskName       the name of this task, will be used for logging
	 * @param project        the ant project for this task, will be used for logging
	 * @param workingDir     the working directory to execute this task in, may be <code>null</code>
	 * @param cmdLine        the commandline object for this task
	 * @param env            the environments for this task, may be <code>null</code>
	 * @param input          the input to the task, may be <code>null</code>
	 * @param outputPriority the log level for the task output. The log
	 *                       priority constants are defined in {@link Project}.
	 *                       If this value is set to <code>-1</code>, then output will never be logged.
     * @see Project#MSG_ERR
     * @see Project#MSG_WARN
     * @see Project#MSG_INFO
     * @see Project#MSG_VERBOSE
     * @see Project#MSG_DEBUG
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

	/**
	 * Creates a new execution task.
	 * 
	 * @param taskName       the name of this task, will be used for logging
	 * @param project        the ant project for this task, will be used for logging
	 * @param cmdLine        the commandline object for this task
	 * @param outputPriority the log level for the task output. The log
	 *                       priority constants are defined in {@link Project}.
	 *                       If this value is set to <code>-1</code>, then output will never be logged.
     * @see Project#MSG_ERR
     * @see Project#MSG_WARN
     * @see Project#MSG_INFO
     * @see Project#MSG_VERBOSE
     * @see Project#MSG_DEBUG
	 */
	public MyExecTask(String taskName, Project project, Commandline cmdLine, int outputPriority) {
		this(taskName, project, null, cmdLine, null, null, outputPriority);
	}

	/**
	 * Creates a new execution task.
	 * 
	 * @param taskName       the name of this task, will be used for logging
	 * @param project        the ant project for this task, will be used for logging
	 * @param cmdLine        the commandline object for this task
	 * @param input          the input to the task, may be <code>null</code>
	 * @param outputPriority the log level for the task output. The log
	 *                       priority constants are defined in {@link Project}.
	 *                       If this value is set to <code>-1</code>, then output will never be logged.
     * @see Project#MSG_ERR
     * @see Project#MSG_WARN
     * @see Project#MSG_INFO
     * @see Project#MSG_VERBOSE
     * @see Project#MSG_DEBUG
	 */
	public MyExecTask(String taskName, Project project, Commandline cmdLine, String input, int outputPriority) {
		this(taskName, project, null, cmdLine, null, input, outputPriority);
	}

	/**
	 * Creates a new execution task.
	 * 
	 * @param cmdLine        the commandline object for this task
	 * @param outputPriority the log level for the task output. The log
	 *                       priority constants are defined in {@link Project}.
	 *                       If this value is set to <code>-1</code>, then output will never be logged.
     * @see Project#MSG_ERR
     * @see Project#MSG_WARN
     * @see Project#MSG_INFO
     * @see Project#MSG_VERBOSE
     * @see Project#MSG_DEBUG
	 */
	public MyExecTask(Commandline cmdLine, int outputPriority) {
		this(null, null, null, cmdLine, null, null, outputPriority);
	}

	/**
	 * Creates a new execution task.
	 * 
	 * @param workingDir     the working directory to execute this task in, may be <code>null</code>
	 * @param cmdLine        the commandline object for this task
	 * @param outputPriority the log level for the task output. The log
	 *                       priority constants are defined in {@link Project}.
	 *                       If this value is set to <code>-1</code>, then output will never be logged.
     * @see Project#MSG_ERR
     * @see Project#MSG_WARN
     * @see Project#MSG_INFO
     * @see Project#MSG_VERBOSE
     * @see Project#MSG_DEBUG
	 */
	public MyExecTask(String workingDir, Commandline cmdLine, int outputPriority) {
		this(null, null, workingDir, cmdLine, null, null, outputPriority);
	}

	/**
	 * Executes this task.
	 * 
	 * @throws BuildException if the task failed
	 */
	public final void execute() throws BuildException {
		int result = executeAndGetResult();
		if (result != 0)
			throw new BuildException("ERROR: Failed to run command: " +
					cmdLine.describeCommand() + ", returned code: " + result);
	}

	/**
	 * Executes this task and returns the result.
	 * 
	 * @return the task result
	 * @throws BuildException if the task failed
	 */
	public int executeAndGetResult() throws BuildException {
		return executeAndGetResult(true);
	}

	/**
	 * Executes this task and returns the result, with or without waiting for the task to complete.
	 * 
	 * @param wait set <code>true</code> to wait for the task to finish, or <code>false</code> to not wait
	 * @return the task result
	 * @throws BuildException if the task failed
	 */
	public int executeAndGetResult(boolean wait) throws BuildException {
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
            getProject().log("Execution directory: " + workingDir,
                    Project.MSG_VERBOSE);
            if( env != null ) {
                getProject().log("Execution environment: " + env.getVariables(),
                        Project.MSG_VERBOSE);
            } 
			if (wait) {
				return exec.execute();
			}
			else {
				exec.spawn();
				return 0;
			}
		} catch (IOException e) {
			throw new BuildException("ERROR: " + e.getMessage());
		}
	}

	/**
	 * Processes one line of output.
	 * 
	 * <p>Sub-classes may override this method to process one line of output.</p>
	 *
	 * @param line the line of output
	 */
	public void handleStdout(String line) {
		// does nothing
	}

	/**
	 * Processes one line of error.
	 * 
	 * <p>Sub-classes may override this method to process one line of error.</p>
	 *
	 * @param line the line of error
	 */
	public void handleStderr(String line) {
		// does nothing
	}
}
