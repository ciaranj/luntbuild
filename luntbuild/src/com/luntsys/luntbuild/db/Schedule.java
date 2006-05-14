/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-27
 * Time: 6:24:27
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
package com.luntsys.luntbuild.db;

import com.luntsys.luntbuild.remoting.Constants;
import com.luntsys.luntbuild.remoting.facade.ScheduleFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.web.ProjectPage;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.io.File;

/**
 * Schedule utilize a quartz(http://www.opensymphony.com/quartz/) trigger to
 * implement its functionality for schedules. By utilizing this trigger, it can be
 * able to achieve periodical schedules or cron-like schedules. This is a hibernate
 * mapping class
 *
 * @author robin shine
 */
public class Schedule {	
	private long id;
	private String name;
	private String description;

	/**
	 * Trigger of this schedule. Null value indicates current schedule can only be
	 * triggered manually
	 */
	private Trigger trigger;

	/**
	 * Project this schedule belongs to
	 */
	private Project project;

	private int buildType = Constants.BUILD_TYPE_CLEAN;
	private int buildStrategy = Constants.BUILD_WHEN_NECESSARY;
	private int labelStrategy = Constants.LABEL_IF_SUCCESS;
	private int notifyStrategy = Constants.NOTIFY_WHEN_STATUS_CHANGED;
	private int postbuildStrategy = Constants.POSTBUILD_NONE;

	/**
	 * Status of this schedule
	 */
	private int status = Constants.SCHEDULE_STATUS_CREATED;

	/**
	 * Date when this schedule get current status
	 */
	private Date statusDate = new Date();

	private Set builds = new HashSet();

	/**
	 * set a unique identify for this schedule, called automatically by hibernate
	 * @param id
	 */
	public void setId(long id){
		this.id = id;
	}

	public long getId(){
		return id;
	}

	/**
	 * set a name for this schedule
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	/**
	 * set trigger of this schedule
	 * @param trigger
	 * @see org.quartz.Trigger
	 */
	public void setTrigger(Trigger trigger){
		this.trigger = trigger;
	}

	public Trigger getTrigger(){
		return trigger;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public int getBuildType() {
		return buildType;
	}

	public void setBuildType(int buildType) {
		this.buildType = buildType;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Schedule){
			if (getId() == ((Schedule)obj).getId())
				return true;
		}
		return false;
	}

	public int hashCode() {
		return (int) getId();
	}

	/**
	 * Validates this schedule at build time, this is different from the validate() method
	 * in the way that it enforces a project build time validation
	 */
	public void validateAtBuildTime() {
		validate();
		getProject().validateAtBuildTime();
	}

	/**
	 * Validates properties of this schedule
	 * @throws ValidationException
	 */
	public void validate() {
		try {
			Luntbuild.validatePathElement(getName());
		} catch (ValidationException e) {
			throw new ValidationException("Invalid name: " + e.getMessage());
		}
		setName(getName().trim());

		if (getTrigger() != null) {
			try {
				Trigger cloneTrigger =  (Trigger) getTrigger().clone();
				if (cloneTrigger instanceof SimpleTrigger) {
					SimpleTrigger simpleTrigger = (SimpleTrigger) cloneTrigger;
					simpleTrigger.setStartTime(new Date(System.currentTimeMillis() + simpleTrigger.getRepeatInterval()));
				} else
					cloneTrigger.setStartTime(new Date(System.currentTimeMillis()));
				Luntbuild.getSchedService().validateTrigger(cloneTrigger);
			} catch (Exception e){
				if (getTrigger() instanceof SimpleTrigger)
					throw new ValidationException("Invalid repeat interval: " + e.getMessage());
				else
					throw new ValidationException("Invalid cron expression: " + e.getMessage());
			}
		}
		validateBuildStrategy(getBuildStrategy());
		validateBuildType(getBuildType());
		validateLabelStrategy(getLabelStrategy());
		validateNotifyStrategy(getNotifyStrategy());
		validatePostbuildStrategy(getPostbuildStrategy());
	}

	public int getBuildStrategy() {
		return buildStrategy;
	}

	public void setBuildStrategy(int buildStrategy) {
		this.buildStrategy = buildStrategy;
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

	public ScheduleFacade getFacade() {
		ScheduleFacade facade = new ScheduleFacade();
		facade.setId(getId());
		facade.setName(getName());
		facade.setDescription(getDescription());
		if (getTrigger() == null)
			facade.setTriggerType(Constants.TRIGGER_TYPE_MANUAL);
		else if (getTrigger().getClass() == SimpleTrigger.class) {
			facade.setTriggerType(Constants.TRIGGER_TYPE_SIMPLE);
			SimpleTrigger simpleTrigger = (SimpleTrigger) getTrigger();
			facade.setRepeatInterval(simpleTrigger.getRepeatInterval() / 60000);
		} else {
			facade.setTriggerType(Constants.TRIGGER_TYPE_CRON);
			CronTrigger cronTrigger = (CronTrigger) getTrigger();
			facade.setCronExpression(cronTrigger.getCronExpression());
		}
		facade.setBuildStrategy(getBuildStrategy());
		facade.setPostbuildStrategy(getPostbuildStrategy());
		facade.setBuildType(getBuildType());
		facade.setLabelStrategy(getLabelStrategy());
		facade.setNotifyStrategy(getNotifyStrategy());
		facade.setStatus(getStatus());
		facade.setStatusDate(getStatusDate());
		facade.setUrl(getUrl(Luntbuild.getDao().loadProperties()));
		return facade;
	}

	public void setFacade(ScheduleFacade facade) {
		setName(facade.getName());
		setDescription(facade.getDescription());
		if (facade.getTriggerType() == Constants.TRIGGER_TYPE_MANUAL)
			setTrigger(null);
		else if (facade.getTriggerType() == Constants.TRIGGER_TYPE_SIMPLE) {
			SimpleTrigger simpleTrigger = new SimpleTrigger();
			simpleTrigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			simpleTrigger.setRepeatInterval(facade.getRepeatInterval() * 60000);
			setTrigger(simpleTrigger);
		} else {
			CronTrigger cronTrigger = new CronTrigger();
			try {
				cronTrigger.setCronExpression(facade.getCronExpression());
			} catch (ParseException e) {
				throw new RuntimeException("Invalid cron expression: " + facade.getCronExpression());
			}
			setTrigger(cronTrigger);
		}

		setBuildStrategy(facade.getBuildStrategy());
		setBuildType(facade.getBuildType());
		setLabelStrategy(facade.getLabelStrategy());
		setNotifyStrategy(facade.getNotifyStrategy());
		setPostbuildStrategy(facade.getPostbuildStrategy());
		validate();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Set getBuilds() {
		return builds;
	}

	public void setBuilds(Set builds) {
		this.builds = builds;
	}

	/**
	 * Validates build strategy
	 * @param buildStrategy
	 * @throws ValidationException
	 */
	public static void validateBuildStrategy(int buildStrategy) {
		if (buildStrategy != Constants.BUILD_ALWAYS && buildStrategy != Constants.BUILD_ALWAYS_IF_FAILED &&
				buildStrategy != Constants.BUILD_NONE && buildStrategy != Constants.BUILD_WHEN_NECESSARY)
			throw new ValidationException("Invalid build strategy");
	}

	/**
	 * Validates post-build strategy
	 * @param postbuildStrategy
	 * @throws ValidationException
	 */
	public static void validatePostbuildStrategy(int postbuildStrategy) {
		if (postbuildStrategy != Constants.POSTBUILD_ALWAYS && postbuildStrategy != Constants.POSTBUILD_IF_FAILED &&
				postbuildStrategy != Constants.POSTBUILD_IF_SUCCESS && postbuildStrategy != Constants.POSTBUILD_NONE)
			throw new ValidationException("Invalid post-build strategy");
	}

	/**
	 * Validate build type
	 * @param buildType
	 * @throws ValidationException
	 */
	public static void validateBuildType(int buildType) {
		if (buildType != Constants.BUILD_TYPE_CLEAN && buildType != Constants.BUILD_TYPE_INCREMENT)
			throw new ValidationException("Invalid build type");
	}

	/**
	 * Validate the label strategy
	 * @param labelStrategy
	 * @throws ValidationException
	 */
	public static void validateLabelStrategy(int labelStrategy) {
		if (labelStrategy != Constants.LABEL_ALWAYS && labelStrategy != Constants.LABEL_IF_SUCCESS &&
				labelStrategy != Constants.LABEL_NONE)
			throw new ValidationException("Invalid label strategy");
	}

	/**
	 * Validate the notify strategy
	 * @param notifyStrategy
	 * @throws ValidationException
	 */
	public static void validateNotifyStrategy(int notifyStrategy) {
		if (notifyStrategy != Constants.NOTIFY_WHEN_STATUS_CHANGED && notifyStrategy != Constants.NOTIFY_ALWAYS &&
				notifyStrategy != Constants.NOTIFY_IF_FAILED && notifyStrategy != Constants.NOTIFY_IF_SUCCESS &&
				notifyStrategy != Constants.NOTIFY_NONE)
			throw new ValidationException("Invalid notify strategy");
	}

	public String getUrl(Map properties) {
		return Luntbuild.getServletUrl(properties)+ "?service=external/ProjectPage&sp=l" +
				getProject().getId() + "&sp=l" + ProjectPage.SERVICE_PARAMETER_SCHEDULES;
	}

	/**
	 * Get publish directory of this schedule
	 * @param properties
	 * @return
	 */
	public String getPublishDir(Map properties) {
		String publishDir = (String) properties.get("publishDir");
		if (Luntbuild.isEmpty(publishDir))
			publishDir = new File(Luntbuild.installDir + "/publish").getAbsolutePath();
		return publishDir + File.separator + getProject().getName() + File.separator + getName();		
	}
}
