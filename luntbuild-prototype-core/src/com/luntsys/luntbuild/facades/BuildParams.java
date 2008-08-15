package com.luntsys.luntbuild.facades;


/**
 * Parameters control a build
 * @author alvin shen
 */
public class BuildParams {
	private long scheduleId;
	private int buildType = -1;
	private String buildVersion = null;
	private int labelStrategy = -1;
	private int notifyStrategy = -1;
	private int postbuildStrategy = -1;
	private String buildNecessaryCondition = null;
	private int triggerDependencyStrategy = -1;

	public int getBuildType() {
		return buildType;
	}

	public void setBuildType(int buildType) {
		this.buildType = buildType;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public int getLabelStrategy() {
		return labelStrategy;
	}

	public void setLabelStrategy(int labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

	public int getNotifyStrategy() {
		return notifyStrategy;
	}

	public void setNotifyStrategy(int notifyStrategy) {
		this.notifyStrategy = notifyStrategy;
	}

	public int getPostbuildStrategy() {
		return postbuildStrategy;
	}

	public void setPostbuildStrategy(int postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

	public String getBuildNecessaryCondition() {
		return buildNecessaryCondition;
	}

	public void setBuildNecessaryCondition(String buildNecessaryCondition) {
		this.buildNecessaryCondition = buildNecessaryCondition;
	}

	public int getTriggerDependencyStrategy() {
		return triggerDependencyStrategy;
	}

	public void setTriggerDependencyStrategy(int triggerDependencyStrategy) {
		this.triggerDependencyStrategy = triggerDependencyStrategy;
	}

	public long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}
}
