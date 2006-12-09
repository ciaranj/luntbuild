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

import com.luntsys.luntbuild.facades.lb111.VcsFacade;

import java.util.List;
import java.util.Map;

/**
 * Facade of a luntbuild view. It is mainly used by web service client to operate on
 * view specific properties
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
	private com.luntsys.luntbuild.facades.lb111.VcsFacade vcs;

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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBuildNecessaryCondition() {
		return buildNecessaryCondition;
	}

	public void setBuildNecessaryCondition(String buildNecessaryCondition) {
		this.buildNecessaryCondition = buildNecessaryCondition;
	}

	public String getBuildFilePath() {
		return buildFilePath;
	}

	public void setBuildFilePath(String buildFilePath) {
		this.buildFilePath = buildFilePath;
	}

	public String getBuildTarget() {
		return buildTarget;
	}

	public void setBuildTarget(String buildTarget) {
		this.buildTarget = buildTarget;
	}

	public String getBuildSuccessCondition() {
		return buildSuccessCondition;
	}

	public void setBuildSuccessCondition(String buildSuccessCondition) {
		this.buildSuccessCondition = buildSuccessCondition;
	}

	public String getPostbuildFilePath() {
		return postbuildFilePath;
	}

	public void setPostbuildFilePath(String postbuildFilePath) {
		this.postbuildFilePath = postbuildFilePath;
	}

	public String getPostbuildTarget() {
		return postbuildTarget;
	}

	public void setPostbuildTarget(String postbuildTarget) {
		this.postbuildTarget = postbuildTarget;
	}

	public String getAntCommand() {
		return antCommand;
	}

	public void setAntCommand(String antCommand) {
		this.antCommand = antCommand;
	}

	public String getEnvFilePath() {
		return envFilePath;
	}

	public void setEnvFilePath(String envFilePath) {
		this.envFilePath = envFilePath;
	}

	public VcsFacade getVcs() {
		return vcs;
	}

	public void setVcs(VcsFacade vcs) {
		this.vcs = vcs;
	}

	public String getNextVersion() {
		return nextVersion;
	}

	public void setNextVersion(String nextVersion) {
		this.nextVersion = nextVersion;
	}

	public List getNotifiers() {
		return notifiers;
	}

	public void setNotifiers(List notifiers) {
		this.notifiers = notifiers;
	}

	public Map getVersionVariableValues() {
		return versionVariableValues;
	}

	public void setVersionVariableValues(Map versionVariableValues) {
		this.versionVariableValues = versionVariableValues;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
}
