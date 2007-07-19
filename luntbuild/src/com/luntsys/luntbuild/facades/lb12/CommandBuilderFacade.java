/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-12-17
 * Time: 12:41:40
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

package com.luntsys.luntbuild.facades.lb12;

/**
 * Command builder facade.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.builders.CommandBuilder
 */
public class CommandBuilderFacade extends BuilderFacade {
	/**
	 * The command to run build
	 */
	private String command;

	/**
	 * Directory to run build command in
	 */
	private String dirToRunCmd;

	/**
	 * Yes/No to determine whether to wait or fork the process
	 */
	private String waitForFinish;

    /**
     * Gets the corresponding builder class name.
     *
     * @return the builder class name
     */
	public String getBuilderClassName() {
		return "com.luntsys.luntbuild.builders.CommandBuilder";
	}

	/**
	 * Gets the command to run.
	 * 
	 * @return the command to run
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Sets the command to run.
	 * 
	 * @param command the command to run
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * Gets the directory to run command in.
	 * 
	 * @return the directory to run command in
	 */
	public String getDirToRunCmd() {
		return dirToRunCmd;
	}

	/**
	 * Sets the directory to run command in.
	 * 
	 * @param dirToRunCmd the directory to run command in
	 */
	public void setDirToRunCmd(String dirToRunCmd) {
		this.dirToRunCmd = dirToRunCmd;
	}

    /**
     * Gets the wait for builder to finish setting (yes/no).
     *
     * @return the wait for builder setting
     */
	public String getWaitForFinish() {
		return waitForFinish;
	}

    /**
     * Sets the wait for builder to finish setting (yes/no).
     *
     * @param waitForFinish the wait for builder setting
     */
	public void setWaitForFinish(String waitForFinish) {
		this.waitForFinish = waitForFinish;
	}
}
