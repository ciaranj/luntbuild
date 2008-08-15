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
 * Facade class for maven builder
 * @author robin shine
 */
public class Maven2BuilderFacade extends com.luntsys.luntbuild.facades.lb12.BuilderFacade {
	/**
	 * The command to run maven
	 */
	private String command;

	/**
	 * Directory to run maven in
	 */
    private String dirToRunMaven;

	/**
	 * Goals to build
	 */
	private String goals;

	/**
	 * Build properties transfered into maven build script
	 */
	private String buildProperties;

	/**
	 * Get command to run maven
	 * @return command to run maven
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Set command to run maven
	 * @param command
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * Get directory to run maven in
	 * @return directory to run maven in
	 */
	public String getDirToRunMaven() {
		return dirToRunMaven;
	}

	/**
	 * Set directory to run maven in
	 * @param dirToRunMaven
	 */
	public void setDirToRunMaven(String dirToRunMaven) {
		this.dirToRunMaven = dirToRunMaven;
	}

	/**
	 * Get goals to build
	 * @return goals to build
	 */
	public String getGoals() {
		return goals;
	}

	/**
	 * Set goals to build
	 * @param goals
	 */
	public void setGoals(String goals) {
		this.goals = goals;
	}

	public String getBuildProperties() {
		return buildProperties;
	}

	public void setBuildProperties(String buildProperties) {
		this.buildProperties = buildProperties;
	}

	public String getBuilderClassName() {
		return "com.luntsys.luntbuild.builders.Maven2Builder";
	}
}
