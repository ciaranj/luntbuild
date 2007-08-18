/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-20
 * Time: 19:51:17
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

package com.luntsys.luntbuild.facades.lb111;

import java.util.List;
import java.util.Map;

/**
 * View facade.
 * 
 * @author robin shine
 */
public class ViewFacade {
	private long id;
	private String name;
	private String description;
	private String buildNecessaryCondition;
	private String buildFilePath;
	private String buildTarget;
	private String buildSuccessCondition;
	private String postbuildFilePath;
	private String postbuildTarget;

	private String antCommand;
	private String envFilePath;
	private VcsFacade vcs;

	private String nextVersion;
	/**
	 * List of notifier class names to send build notification
	 */
	private List notifiers;

	/**
	 * This map keeps current value of all possible variables used by the nextVersion property
	 */
	private Map versionVariableValues;

	private long projectId;

	/**
	 * Gets the identifer of this view.
	 * 
	 * @return the identifer of this view
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the identifier of this view.
	 *
	 * @param id the identifier of this view
	 */
	public void setId(long id) {
		this.id = id;
	}

    /**
     * Gets the name of this view.
     * 
     * @return the name
     */
	public String getName() {
		return name;
	}

    /**
     * Sets the name of this view.
     * 
     * @param name the name
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Gets the description of this view.
     * 
     * @return the description
     */
	public String getDescription() {
		return description;
	}

    /**
     * Sets the description of this view.
     * 
     * @param description the description
     */
	public void setDescription(String description) {
		this.description = description;
	}

    /**
     * Gets the build necessary condition for this view.
     * 
     * @return the build necessary condition
     */
	public String getBuildNecessaryCondition() {
		return buildNecessaryCondition;
	}

    /**
     * Sets the build necessary condition of this view.
     * 
     * @param buildNecessaryCondition the build necessary condition
     */
	public void setBuildNecessaryCondition(String buildNecessaryCondition) {
		this.buildNecessaryCondition = buildNecessaryCondition;
	}

    /**
     * Gets the build file path for this view.
     * 
     * @return the build file path
     */
	public String getBuildFilePath() {
		return buildFilePath;
	}

    /**
     * Sets the build file path of this view.
     * 
     * @param buildFilePath the build file path
     */
	public void setBuildFilePath(String buildFilePath) {
		this.buildFilePath = buildFilePath;
	}

    /**
     * Gets the build target for this view.
     * 
     * @return the build target
     */
	public String getBuildTarget() {
		return buildTarget;
	}

    /**
     * Sets the build target of this view.
     * 
     * @param buildTarget the build target
     */
	public void setBuildTarget(String buildTarget) {
		this.buildTarget = buildTarget;
	}

    /**
     * Gets the build success condition for this view.
     *
     * @return the build success condition
     */
	public String getBuildSuccessCondition() {
		return buildSuccessCondition;
	}

    /**
     * Sets the build success condition for this view.
     *
     * @param buildSuccessCondition the build success condition
     */
	public void setBuildSuccessCondition(String buildSuccessCondition) {
		this.buildSuccessCondition = buildSuccessCondition;
	}

    /**
     * Gets the post-build file path for this view.
     * 
     * @return the post-build file path
     */
	public String getPostbuildFilePath() {
		return postbuildFilePath;
	}

    /**
     * Sets the post-build file path of this view.
     * 
     * @param postbuildFilePath the post-build file path
     */
	public void setPostbuildFilePath(String postbuildFilePath) {
		this.postbuildFilePath = postbuildFilePath;
	}

    /**
     * Gets the post-build target for this view.
     * 
     * @return the post-build target
     */
	public String getPostbuildTarget() {
		return postbuildTarget;
	}

    /**
     * Sets the post-build target of this view.
     * 
     * @param postbuildTarget the post-build target
     */
	public void setPostbuildTarget(String postbuildTarget) {
		this.postbuildTarget = postbuildTarget;
	}

	/**
	 * Gets the command to run ant.
	 * 
	 * @return the command to run ant
	 */
	public String getAntCommand() {
		return antCommand;
	}

	/**
	 * Sets the command to run ant.
	 * 
	 * @param antCommand the command to run ant
	 */
	public void setAntCommand(String antCommand) {
		this.antCommand = antCommand;
	}

    /**
     * Gets the environment file path for this view.
     * 
     * @return the environment file path
     */
	public String getEnvFilePath() {
		return envFilePath;
	}

    /**
     * Sets the environment file path of this view.
     * 
     * @param envFilePath the environment file path
     */
	public void setEnvFilePath(String envFilePath) {
		this.envFilePath = envFilePath;
	}

	/**
	 * Gets the VCS facade of this view.
	 * 
	 * @return the VCS facade
	 */
	public VcsFacade getVcs() {
		return vcs;
	}

	/**
	 * Sets the VCS facade of this view.
	 * 
	 * @param vcs the VCS facade
	 */
	public void setVcs(VcsFacade vcs) {
		this.vcs = vcs;
	}

    /**
     * Gets the next build version of this view.
     * 
     * @return the next build version
     */
	public String getNextVersion() {
		return nextVersion;
	}

    /**
     * Sets the next build version of this view.
     * 
     * @param nextVersion the next build version
     */
	public void setNextVersion(String nextVersion) {
		this.nextVersion = nextVersion;
	}

	/**
	 * Gets the list of notifiers for this view.
	 * 
	 * @return the notifiers
	 */
	public List getNotifiers() {
		return notifiers;
	}

	/**
	 * Sets the list of notifiers for this view.
	 * 
	 * @param notifiers the list of notifiers
	 */
	public void setNotifiers(List notifiers) {
		this.notifiers = notifiers;
	}

	/**
	 * Gets the map of version variable values for this view.
	 * 
	 * @return the map of version variable values
	 */
	public Map getVersionVariableValues() {
		return versionVariableValues;
	}

	/**
	 * Sets the map of version variable values for this view.
	 * 
	 * @param versionVariableValues the map of version variable values
	 */
	public void setVersionVariableValues(Map versionVariableValues) {
		this.versionVariableValues = versionVariableValues;
	}

    /**
     * Gets the identifier of the project of this view.
     * 
     * @return the identifier of the project
     */
	public long getProjectId() {
		return projectId;
	}

    /**
     * Sets the identifier of the project of this view.
     * 
     * @param projectId the identifier of the project
     */
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
}
