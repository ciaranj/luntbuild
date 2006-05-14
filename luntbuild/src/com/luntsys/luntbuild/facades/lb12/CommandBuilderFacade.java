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
 * Facade class for command builder
 * @author robin shine
 */
public class CommandBuilderFacade extends com.luntsys.luntbuild.facades.lb12.BuilderFacade {
	/**
	 * The command to run build
	 */
	private String command;

	/**
	 * Directory to run build command in
	 */
	private String dirToRunCmd;

	/**
	 * Get command to run build
	 * @return command to run build
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Set command to run build
	 * @param command
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * Get directory to run build command in
	 * @return directory to run build command in
	 */
	public String getDirToRunCmd() {
		return dirToRunCmd;
	}

	/**
	 * Set directory to run build command in
	 * @param dirToRunCmd
	 */
	public void setDirToRunCmd(String dirToRunCmd) {
		this.dirToRunCmd = dirToRunCmd;
	}

	public String getBuilderClassName() {
		return "com.luntsys.luntbuild.builders.CommandBuilder";
	}
}
