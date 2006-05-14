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
 * Facade class for Rake builder
 * @author lubosp
 */
public class RakeBuilderFacade extends BuilderFacade {
	/**
	 * The command to run Rake
	 */
	private String command;

	/**
	 * Path to Rake build script
	 */
	private String buildScriptPath;

	/**
	 * Target to build
	 */
	private String buildTargets;

	/**
	 * Build properties transfered into Rake build script
	 */
	private String buildProperties;

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

	public String getBuildTargets() {
		return buildTargets;
	}

	public void setBuildTargets(String buildTargets) {
		this.buildTargets = buildTargets;
	}

	public String getBuildProperties() {
		return buildProperties;
	}

	public void setBuildProperties(String buildProperties) {
		this.buildProperties = buildProperties;
	}

	public String getBuilderClassName() {
		return "com.luntsys.luntbuild.builders.RakeBuilder";
	}
}
