package com.luntsys.luntbuild.luntclipse.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.luntsys.luntbuild.facades.lb12.BuildFacade;

/**
 * Build infromation wrapper class
 * @author 	 Roman Pichlík, Lubos Pochman
 * @version  $Revision: 432 $
 * @since 	 0.0.1
 */
public class BuildMessenger{
    private String projectName = "";
    private String scheduleName = "";
    private int scheduleStatus = 0;
    private int buildStatus = 0;
    private int triggerType = 0;
    private int lastBuildStatus = 0;
    private String statusDate = "";
    private String endDate = "";
    private String duration = null;
    private String version = "no builds yet";
    private String buildLogUrl = null;
    private String revisionLogUrl = null;
    private String systemLogUrl = null;
    private String detailUrl = null;
    private BuildFacade facade = null;

    /**
     * Default constructor
     */
    public BuildMessenger() {
    }

    private BuildMessenger(BuildMessenger toCopy) {
        if (toCopy.projectName != null) this.projectName = new String(toCopy.projectName);
        if (toCopy.scheduleName != null) this.scheduleName = new String(toCopy.scheduleName);
        this.scheduleStatus = toCopy.scheduleStatus;
        this.buildStatus = toCopy.buildStatus;
        this.triggerType = toCopy.triggerType;
        this.lastBuildStatus = toCopy.lastBuildStatus;
        if (toCopy.statusDate != null) this.statusDate = new String(toCopy.statusDate);
        if (toCopy.endDate != null) this.endDate = new String(toCopy.endDate);
        if (toCopy.duration != null) this.duration = new String(toCopy.duration);
        if (toCopy.version != null) this.version = new String(toCopy.version);
        if (toCopy.buildLogUrl != null) this.buildLogUrl = new String(toCopy.buildLogUrl);
        if (toCopy.revisionLogUrl != null) this.revisionLogUrl = new String(toCopy.revisionLogUrl);
        if (toCopy.systemLogUrl != null) this.systemLogUrl = new String(toCopy.systemLogUrl);
        if (toCopy.detailUrl != null) this.detailUrl = new String(toCopy.detailUrl);
        this.facade = toCopy.facade;
    }

    /**
     * @param facades
     * @param lastBuild
     * @return list of BuildMessenger
     */
    public static List toMessanger(List facades, BuildMessenger lastBuild) {
        ArrayList msgList = new ArrayList();

        for (Iterator iter = facades.iterator(); iter.hasNext();) {
            BuildFacade bf = (BuildFacade) iter.next();
            BuildMessenger messenger = new BuildMessenger(lastBuild);

            if(bf != null){
                messenger.setLastBuildStatus(bf.getStatus());
                messenger.setBuildStatus(bf.getStatus());
                messenger.setVersion(bf.getVersion());
                messenger.setBuildLogUrl(bf.getBuildLogUrl());
                messenger.setRevisionLogUrl(bf.getRevisionLogUrl());
                messenger.setDetailUrl(bf.getUrl());
                try {
                    messenger.setSystemLogUrl(bf.getSystemLogUrl());
                } catch (Exception e) {
                    // ignore for earlier versions
                }
            }

            Date endDate = bf.getEndDate();
            if(endDate != null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                messenger.setEndDate(sdf.format(endDate));
            }

            // duration
            Date startDate = bf.getStartDate();
            if (startDate != null) {
                long diffMin = (endDate.getTime()-startDate.getTime())/60000;
                messenger.setDuration("" + diffMin);
            }

            messenger.setFacade(bf);

            msgList.add(messenger);
        }
        return msgList;
    }
    /**
     * @return Returns the buildLogUrl.
     */
    public final String getBuildLogUrl() {
        return (this.buildLogUrl == null) ? "" : this.buildLogUrl;
    }
    /**
     * @param buildLogUrl The buildLogUrl to set.
     */
    public final void setBuildLogUrl(String buildLogUrl) {
        this.buildLogUrl = buildLogUrl;
    }
    /**
     * @return Returns the buildStatus.
     */
    public final int getBuildStatus() {
        return this.buildStatus;
    }
    /**
     * @param buildStatus The buildStatus to set.
     */
    public final void setBuildStatus(int buildStatus) {
        this.buildStatus = buildStatus;
    }
    /**
     * @return Returns the detailUrl.
     */
    public final String getDetailUrl() {
        return (this.detailUrl == null) ? "" : this.detailUrl;
    }
    /**
     * @param detailUrl The detailUrl to set.
     */
    public final void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
    /**
     * @return Returns the duration.
     */
    public final String getDuration() {
        return (this.duration == null) ? "" : this.duration;
    }
    /**
     * @param duration The duration to set.
     */
    public final void setDuration(String duration) {
        this.duration = duration;
    }
    /**
     * @return Returns the endDate.
     */
    public final String getEndDate() {
        return (this.endDate == null) ? "" : this.endDate;
    }
    /**
     * @param endDate The endDate to set.
     */
    public final void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    /**
     * @return Returns the facade.
     */
    public final BuildFacade getFacade() {
        return this.facade;
    }
    /**
     * @param facade The facade to set.
     */
    public final void setFacade(BuildFacade facade) {
        this.facade = facade;
    }
    /**
     * @return Returns the lastBuildStatus.
     */
    public final int getLastBuildStatus() {
        return this.lastBuildStatus;
    }
    /**
     * @param lastBuildStatus The lastBuildStatus to set.
     */
    public final void setLastBuildStatus(int lastBuildStatus) {
        this.lastBuildStatus = lastBuildStatus;
    }
    /**
     * @return Returns the projectName.
     */
    public final String getProjectName() {
        return this.projectName;
    }
    /**
     * @param projectName The projectName to set.
     */
    public final void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    /**
     * @return Returns the revisionLogUrl.
     */
    public final String getRevisionLogUrl() {
        return (this.revisionLogUrl == null) ? "" : this.revisionLogUrl;
    }
    /**
     * @param revisionLogUrl The revisionLogUrl to set.
     */
    public final void setRevisionLogUrl(String revisionLogUrl) {
        this.revisionLogUrl = revisionLogUrl;
    }
    /**
     * @return Returns the scheduleName.
     */
    public final String getScheduleName() {
        return this.scheduleName;
    }
    /**
     * @param scheduleName The scheduleName to set.
     */
    public final void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
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
     * @return Returns the systemLogUrl.
     */
    public final String getSystemLogUrl() {
        return (this.systemLogUrl == null) ? "" : this.systemLogUrl;
    }
    /**
     * @param systemLogUrl The systemLogUrl to set.
     */
    public final void setSystemLogUrl(String systemLogUrl) {
        this.systemLogUrl = systemLogUrl;
    }
    /**
     * @return Returns the triggerType.
     */
    public final int getTriggerType() {
        return this.triggerType;
    }
    /**
     * @param triggerType The triggerType to set.
     */
    public final void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }
    /**
     * @return Returns the version.
     */
    public final String getVersion() {
        return (this.version == null) ? "" : this.version;
    }
    /**
     * @param version The version to set.
     */
    public final void setVersion(String version) {
        this.version = version;
    }
}
