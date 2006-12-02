package com.luntsys.luntbuild.luntclipse.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Schedule Project Data
 *
 * @author Lubos Pochman
 *
 */
public class ScheduleProjectData {

    private String name = null;
    private String description = null;
    private String nextBuildVersion = null;
    private String workDirectory = null;
    private String buildCondition = null;
    private int buildType = -1;
    private String[] builders = null;
    private int trigerType = -1;
    private String triggerData = null;
    private String[] postBuilders = null;
    private int postBuildStrategy = -1;
    private String[] dependentSchedules = null;
    private int buildCleanup = -1;
    private String buildCleanupData = null;
    private int labelStrategy = -1;
    private int notifyStrategy = -1;
    private int scheduleStatus = -1;
    private String statusDate = null;
    private List dependentScheduleIds = null;

    /**
     * @return Returns the buildCleanup.
     */
    public final int getBuildCleanup() {
        return this.buildCleanup;
    }
    /**
     * @param buildCleanup The buildCleanup to set.
     */
    public final void setBuildCleanup(int buildCleanup) {
        this.buildCleanup = buildCleanup;
    }
    /**
     * @return Returns the buildCleanupData.
     */
    public final String getBuildCleanupData() {
        return (this.buildCleanupData == null) ? "" : this.buildCleanupData;
    }
    /**
     * @param buildCleanupData The buildCleanupData to set.
     */
    public final void setBuildCleanupData(String buildCleanupData) {
        this.buildCleanupData = buildCleanupData;
    }
    /**
     * @return Returns the buildCondition.
     */
    public final String getBuildCondition() {
        return (this.buildCondition == null) ? "" : this.buildCondition;
    }
    /**
     * @param buildCondition The buildCondition to set.
     */
    public final void setBuildCondition(String buildCondition) {
        this.buildCondition = buildCondition;
    }
    /**
     * @return Returns the builders.
     */
    public final String[] getBuilders() {
        if (this.builders == null) return new String[0];
        return this.builders;
    }
    /**
     * @param builders The builders to set.
     */
    public final void setBuilders(String[] builders) {
        this.builders = builders;
    }
    /**
     * @return Returns the buildType.
     */
    public final int getBuildType() {
        return this.buildType;
    }
    /**
     * @param buildType The buildType to set.
     */
    public final void setBuildType(int buildType) {
        this.buildType = buildType;
    }
    /**
     * @return Returns the dependentSchedules.
     */
    public final String[] getDependentSchedules() {
        if (this.dependentSchedules == null) return new String[0];
        return this.dependentSchedules;
    }
    /**
     * @param dependentSchedules The dependentSchedules to set.
     */
    public final void setDependentSchedules(String[] dependentSchedules) {
        this.dependentSchedules = dependentSchedules;
    }
    /**
     * @return Returns the description.
     */
    public final String getDescription() {
        return (this.description == null) ? "" : this.description;
    }
    /**
     * @param description The description to set.
     */
    public final void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return Returns the labelStrategy.
     */
    public final int getLabelStrategy() {
        return this.labelStrategy;
    }
    /**
     * @param labelStrategy The labelStrategy to set.
     */
    public final void setLabelStrategy(int labelStrategy) {
        this.labelStrategy = labelStrategy;
    }
    /**
     * @return Returns the name.
     */
    public final String getName() {
        return (this.name == null) ? "" : this.name;
    }
    /**
     * @param name The name to set.
     */
    public final void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the nextBuildVersion.
     */
    public final String getNextBuildVersion() {
        return (this.nextBuildVersion == null) ? "" : this.nextBuildVersion;
    }
    /**
     * @param nextBuildVersion The nextBuildVersion to set.
     */
    public final void setNextBuildVersion(String nextBuildVersion) {
        this.nextBuildVersion = nextBuildVersion;
    }
    /**
     * @return Returns the notifyStrategy.
     */
    public final int getNotifyStrategy() {
        return this.notifyStrategy;
    }
    /**
     * @param notifyStrategy The notifyStrategy to set.
     */
    public final void setNotifyStrategy(int notifyStrategy) {
        this.notifyStrategy = notifyStrategy;
    }
    /**
     * @return Returns the postBuilders.
     */
    public final String[] getPostBuilders() {
        if (this.postBuilders == null) return new String[0];
        return this.postBuilders;
    }
    /**
     * @param postBuilders The postBuilders to set.
     */
    public final void setPostBuilders(String[] postBuilders) {
        this.postBuilders = postBuilders;
    }
    /**
     * @return Returns the postBuildStrategy.
     */
    public final int getPostBuildStrategy() {
        return this.postBuildStrategy;
    }
    /**
     * @param postBuildStrategy The postBuildStrategy to set.
     */
    public final void setPostBuildStrategy(int postBuildStrategy) {
        this.postBuildStrategy = postBuildStrategy;
    }
    /**
     * @return Returns the trigerType.
     */
    public final int getTrigerType() {
        return this.trigerType;
    }
    /**
     * @param trigerType The trigerType to set.
     */
    public final void setTrigerType(int trigerType) {
        this.trigerType = trigerType;
    }
    /**
     * @return Returns the triggerData.
     */
    public final String getTriggerData() {
        return (this.triggerData == null) ? "" : this.triggerData;
    }
    /**
     * @param triggerData The triggerData to set.
     */
    public final void setTriggerData(String triggerData) {
        this.triggerData = triggerData;
    }
    /**
     * @return Returns the workDirectory.
     */
    public final String getWorkDirectory() {
        return (this.workDirectory == null) ? "" : this.workDirectory;
    }
    /**
     * @param workDirectory The workDirectory to set.
     */
    public final void setWorkDirectory(String workDirectory) {
        this.workDirectory = workDirectory;
    }
    /**
     * @return Returns the scheduleStatus.
     */
    public final int getScheduleStatus() {
        return this.scheduleStatus;
    }
    /**
     * @param scheduleStatus The scheduleStatus to set.
     */
    public final void setScheduleStatus(int scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
    /**
     * @return Returns the statusDate.
     */
    public final String getStatusDate() {
        return (this.statusDate == null) ? "" : this.statusDate;
    }
    /**
     * @param statusDate The statusDate to set.
     */
    public final void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }
    /**
     * @return Returns the dependentScheduleIds.
     */
    public final List getDependentScheduleIds() {
        if (this.dependentScheduleIds == null) return new ArrayList();
        return this.dependentScheduleIds;
    }
    /**
     * @param dependentScheduleIds The dependentScheduleIds to set.
     */
    public final void setDependentScheduleIds(List dependentScheduleIds) {
        this.dependentScheduleIds = dependentScheduleIds;
    }



}
