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
 * Rake builder facade.
 * 
 * @author lubosp
 * @see com.luntsys.luntbuild.builders.RakeBuilder
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

    /**
     * Gets the corresponding builder class name.
     *
     * @return the builder class name
     */
	public String getBuilderClassName() {
		return "com.luntsys.luntbuild.builders.RakeBuilder";
	}

	/**
	 * Gets the command to run rake.
	 * 
	 * @return the command to run rake
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Sets the command to run rake.
	 * 
	 * @param command the command to run rake
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * Gets the path to the build script.
	 * 
	 * @return the path to the build script
	 */
	public String getBuildScriptPath() {
		return buildScriptPath;
	}

	/**
	 * Sets the path to the build script.
	 * 
	 * @param buildScriptPath the path to the build script
	 */
	public void setBuildScriptPath(String buildScriptPath) {
		this.buildScriptPath = buildScriptPath;
	}

	/**
	 * Gets the targets to run.
	 * 
	 * @return the targets to run.
	 */
	public String getBuildTargets() {
		return buildTargets;
	}

	/**
	 * Sets the targets to run.
	 * 
	 * @param buildTargets the targets to run.
	 */
	public void setBuildTargets(String buildTargets) {
		this.buildTargets = buildTargets;
	}

	/**
	 * Gets the build properties.
	 * 
	 * @return the build properties
	 */
	public String getBuildProperties() {
		return buildProperties;
	}

	/**
	 * Sets the build properties.
	 * 
	 * @param buildProperties the build properties
	 */
	public void setBuildProperties(String buildProperties) {
		this.buildProperties = buildProperties;
	}
}
