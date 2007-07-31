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

import com.luntsys.luntbuild.BuildGenerator;
import com.luntsys.luntbuild.facades.BuildParams;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.lb12.ScheduleFacade;
import com.luntsys.luntbuild.builders.Builder;
import com.luntsys.luntbuild.dependency.DependentNode;
import com.luntsys.luntbuild.security.SecurityHelper;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.OgnlHelper;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.utility.Variable;
import com.luntsys.luntbuild.utility.VariableHolder;
import com.luntsys.luntbuild.web.ProjectPage;
import ognl.Ognl;
import ognl.OgnlException;

import org.acegisecurity.AccessDeniedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.*;

/**
 * A schedule for executing a <code>Project</code>.
 * 
 * <p><code>Schedule</code> utilizes a quartz(http://www.opensymphony.com/quartz/) trigger to
 * implement its functionality. By utilizing this trigger, it is
 * able to achieve periodical schedules or cron-like schedules. This is a hibernate
 * mapping class.</p>
 *
 * @author robin shine
 * @see Project
 */
public class Schedule implements DependentNode, VariableHolder {
    private static Log logger = LogFactory.getLog(Schedule.class);

    private static Map currentThreads = Collections.synchronizedMap(new HashMap());

    private long id;

    private boolean scheduleDisabled;

    private String name;

    private String description;

    private String nextVersion;

    private String variables = "";

    private List associatedBuilderNames;

    private List associatedPostbuilderNames;

    /**
     * Whether or not this schedule is marked as selected.
     */
    private transient boolean selected;

    /**
     * Trigger of this schedule. Null value indicates current schedule can only be
     * triggered manually
     */
    private Trigger trigger;

    private String buildNecessaryCondition = "vcsModified or dependencyNewer";

    /**
     * Project this schedule belongs to
     */
    private Project project;

    private int buildType = Constants.BUILD_TYPE_CLEAN;
    private int labelStrategy = Constants.LABEL_NONE;
    private int notifyStrategy = Constants.NOTIFY_WHEN_STATUS_CHANGED;
    private int postbuildStrategy = Constants.POSTBUILD_NONE;

    private int triggerDependencyStrategy = Constants.TRIGGER_SCHEDULES_THIS_DEPENDS_ON;

    private int buildCleanupStrategy = Constants.BUILD_CLEANUP_NONE;
    private String buildCleanupStrategyData;

    /**
     * Status of this schedule
     */
    private int status = Constants.SCHEDULE_STATUS_CREATED;

    /**
     * Date when this schedule get current status
     */
    private Date statusDate = new Date();

    private Set builds;

    /**
     * Schedule ids this schedule depends on
     */
    private List dependentScheduleIds;

    private String workingPath;

    /**
     * Used for migration purpose
     */
    private List dependentSchedules;

    /**
     * Create a <code>Schedule</code>.
     */
    public Schedule() {}

    /**
     * Create a <code>Schedule</code> from an existing <code>Schedule</code>.
     * 
     * @param schedule used for creation
     */
    public Schedule(Schedule schedule) {
        setName(getName());
        setScheduleDisabled(schedule.isScheduleDisabled());
        setProject(schedule.getProject());
        setDescription(schedule.getDescription());
        setNextVersion(schedule.getNextVersion());
        setVariables(schedule.getVariables());
        if (schedule.trigger != null)
            setTrigger((Trigger)schedule.getTrigger().clone());
        setBuildNecessaryCondition(schedule.getBuildNecessaryCondition());
        if (schedule.getAssociatedBuilderNames() != null)
            setAssociatedBuilderNames((ArrayList)((ArrayList)schedule.getAssociatedBuilderNames()).clone());
        if (schedule.getAssociatedPostbuilderNames() != null)
        setAssociatedPostbuilderNames((ArrayList)((ArrayList)schedule.getAssociatedPostbuilderNames()).clone());
        setBuildType(schedule.getBuildType());
        setLabelStrategy(schedule.getLabelStrategy());
        setNotifyStrategy(schedule.getNotifyStrategy());
        setPostbuildStrategy(schedule.getPostbuildStrategy());
        setTriggerDependencyStrategy(schedule.getTriggerDependencyStrategy());
        setBuildCleanupStrategy(schedule.getBuildCleanupStrategy());
        setBuildCleanupStrategyData(schedule.getBuildCleanupStrategyData());
        if (schedule.getDependentScheduleIds() != null)
            setDependentScheduleIds((ArrayList)((ArrayList)schedule.getDependentScheduleIds()).clone());
        if (schedule.getDependentSchedules() != null)
            setDependentSchedules((ArrayList)((ArrayList)schedule.getDependentSchedules()).clone());
        setStatus(schedule.getStatus());
        if (schedule.getStatusDate() != null)
            setStatusDate((Date)schedule.getStatusDate().clone());
        setWorkingPath(schedule.getWorkingPath());
    }

    /**
     * Sets the indentifier of this schedule, called automatically by hibernate.
     * 
     * @param id the indentifier of this schedule
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the indentifier of this schedule.
     * 
     * @return the indentifier of this schedule
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the name of this schedule.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this schedule.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the description of this schedule.
     * 
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the description of this schedule.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the trigger of this schedule.
     * 
     * @param trigger the trigger
     */
    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    /**
     * Gets the trigger of this schedule.
     * 
     * @return the trigger
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Checks if this schedule is disabled.
     * 
     * @return <code>true</code> if disabled
     */
    public boolean isDisabled() {
        return scheduleDisabled;
    }

    /**
     * Sets the disabled state of this schedule.
     * 
     * @param scheduleDisabled set <code>true</code> if schedule is disabled
     */
    public void setScheduleDisabled(boolean scheduleDisabled) {
    	this.scheduleDisabled = scheduleDisabled;
    }

    /**
     * Checks if this schedule is disabled.
     * 
     * @return <code>true</code> if disabled
     */
    public boolean isScheduleDisabled() {
        return scheduleDisabled;
    }

    /**
     * Gets the project of this schedule.
     * 
     * @return the project
     */
    public Project getProject() {
        return project;
    }

	/**
	 * Gets the project with the specified name. The project does not have to be the
	 * project of this schedule.
	 * 
	 * @param name the name of the project
	 * @return the project with the specified name
	 */
    public Project getProject(String name) {
        List projects = Luntbuild.getDao().loadProjects();
        for (Iterator iter = projects.iterator(); iter.hasNext();) {
            Project p = (Project) iter.next();
            if (p.getName().equals(name)) return p;
        }
        return null;
    }

    /**
     * Sets the project of this schedule.
     * 
     * @param project the project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Gets the build type of this schedule.
     * 
     * @return the build type
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_CLEAN
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_INCREMENT
     */
    public int getBuildType() {
        return buildType;
    }

    /**
     * Sets the build type of this schedule.
     * 
     * @param buildType the build type
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_CLEAN
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_TYPE_INCREMENT
     */
    public void setBuildType(int buildType) {
        this.buildType = buildType;
    }

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise
	 */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Schedule) {
            if (getId() == ((Schedule) obj).getId())
                return true;
        }
        return false;
    }

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return a hash code value for this object
	 * @see #equals(Object)
	 */
    public int hashCode() {
        return (int) getId();
    }

    /**
     * Validates this schedule at build time, this is different from the <code>validate()</code> method
     * in the way that it enforces a project build time validation.
     * 
	 * @throws ValidationException if a property has an invalid value
     */
    public void validateAtBuildTime() throws ValidationException {
        getProject().validateAtBuildTime();
        validate();
    }

    /**
     * Validates properties of this schedule.
     * 
	 * @throws ValidationException if a property has an invalid value
     */
    public void validate() throws ValidationException {
        try {
            Luntbuild.validatePathElement(getName());
        } catch (ValidationException e) {
            throw new ValidationException("Invalid name: " + e.getMessage());
        }
        setName(getName().trim());

        if (getWorkingPath() != null) {
            setWorkingPath(getWorkingPath().trim());
        }

        validateBuildVersion(getNextVersion());
        setNextVersion(getNextVersion().trim());

        if (getTrigger() != null) {
            try {
                Trigger cloneTrigger = (Trigger) getTrigger().clone();
                if (cloneTrigger instanceof SimpleTrigger) {
                    SimpleTrigger simpleTrigger = (SimpleTrigger) cloneTrigger;
                    simpleTrigger.setStartTime(new Date(System.currentTimeMillis() + simpleTrigger.getRepeatInterval()));
                } else
                    cloneTrigger.setStartTime(new Date(System.currentTimeMillis()));
                Luntbuild.getSchedService().validateTrigger(cloneTrigger);
            } catch (Exception e) {
                if (getTrigger() instanceof SimpleTrigger)
                    throw new ValidationException("Invalid repeat interval: " + e.getMessage());
                else
                    throw new ValidationException("Invalid cron expression: " + e.getMessage());
            }
        }
        validateBuildNecessaryCondition(getBuildNecessaryCondition());
        setBuildNecessaryCondition(getBuildNecessaryCondition().trim());
        validateBuildType(getBuildType());
        validateLabelStrategy(getLabelStrategy());
        validateNotifyStrategy(getNotifyStrategy());
        validatePostbuildStrategy(getPostbuildStrategy());
        validateBuildCleanupStrategy(getBuildCleanupStrategy(), getBuildCleanupStrategyData());

        if (getAssociatedBuilderNames().size() == 0)
            throw new ValidationException("No builders defined for schedule: " + getProject().getName() + "/" + getName());
        if (getAssociatedPostbuilderNames().size() == 0 && getPostbuildStrategy() != Constants.POSTBUILD_NONE)
            throw new ValidationException("No post-builders defined for schedule: " + getProject().getName() + "/" + getName());
    }

    /**
     * Validates a build cleanup strategy.
     * 
     * @param buildCleanupStrategy
     * @param buildCleanupStrategyData
	 * @throws ValidationException if the build cleanup strategy is invalid
     */
    private static void validateBuildCleanupStrategy(int buildCleanupStrategy, String buildCleanupStrategyData)
    		throws ValidationException {
        if (buildCleanupStrategy != Constants.BUILD_CLEANUP_NONE &&
                buildCleanupStrategy != Constants.BUILD_KEEP_BY_COUNT &&
                buildCleanupStrategy != Constants.BUILD_KEEP_BY_DAYS)
            throw new ValidationException("Invalid build cleanup strategy!");
        if (buildCleanupStrategy != com.luntsys.luntbuild.facades.Constants.BUILD_CLEANUP_NONE) {
            try {
                int intValue = new Integer(buildCleanupStrategyData).intValue();
                if (intValue <= 0)
                    throw new ValidationException("Invalid data setting for build cleanup strategy!");
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid data setting for build cleanup strategy!");
            }
        }
    }

    /**
     * Gets the label strategy of this schedule.
     * 
     * @return the label strategy
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_NONE
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_IF_SUCCESS
     */
    public int getLabelStrategy() {
        return labelStrategy;
    }

    /**
     * Sets the label strategy of this schedule.
     * 
     * @param labelStrategy the label strategy
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_NONE
     * @see com.luntsys.luntbuild.facades.Constants#LABEL_IF_SUCCESS
     */
    public void setLabelStrategy(int labelStrategy) {
        this.labelStrategy = labelStrategy;
    }

    /**
     * Gets the notify strategy of this schedule.
     * 
     * @return the notify strategy
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_NONE
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_SUCCESS
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_WHEN_STATUS_CHANGED
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_FAILED_OR_CHANGED
     */
    public int getNotifyStrategy() {
        return notifyStrategy;
    }

    /**
     * Sets the notify strategy of this schedule.
     * 
     * @param notifyStrategy the notify strategy
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_NONE
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_SUCCESS
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_WHEN_STATUS_CHANGED
     * @see com.luntsys.luntbuild.facades.Constants#NOTIFY_IF_FAILED_OR_CHANGED
     */
    public void setNotifyStrategy(int notifyStrategy) {
        this.notifyStrategy = notifyStrategy;
    }

    /**
     * Gets the post-build strategy of this schedule.
     * 
     * @return the post-build strategy
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_NONE
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_SUCCESS
     */
    public int getPostbuildStrategy() {
        return postbuildStrategy;
    }

    /**
     * Sets the post-build strategy of this schedule.
     * 
     * @param postbuildStrategy the post-build strategy
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_ALWAYS
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_NONE
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#POSTBUILD_IF_SUCCESS
     */
    public void setPostbuildStrategy(int postbuildStrategy) {
        this.postbuildStrategy = postbuildStrategy;
    }

    /**
     * Gets the work directory of this schedule 'workdir'/'project'/'schedule'.
     * 
     * @return the work directory
     */
    public String getWorkDirRaw() {
        String workingDir;
        String globalWorkingDir =
            (String) Luntbuild.getProperties().get(com.luntsys.luntbuild.facades.Constants.WORKING_DIR);
        if (Luntbuild.isEmpty(globalWorkingDir))
            globalWorkingDir = new File(Luntbuild.installDir + File.separator + "work").getAbsolutePath();
        if (Luntbuild.isEmpty(getWorkingPath()))
            workingDir = globalWorkingDir + File.separator + getProject().getName();
        else {
            if (new File(getWorkingPath()).isAbsolute())
                workingDir = getWorkingPath();
            else
                workingDir = globalWorkingDir + File.separator + getWorkingPath();
        }
        try {
            workingDir = new File(workingDir).getCanonicalPath();
            return workingDir;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the work directory of this schedule 'workdir'/'project'/'schedule'.
     * <p><strong>
     * Note: this method should be used in OGNL expressions only because of beckslash handling!
     * </strong></p>
     *
     * @return the work directory
     */
    public String getWorkingDir() {
        return getWorkDirRaw().replaceAll("\\\\", "\\\\\\\\"); // in order to keep back slash for ognl expression evaluation
    }

    /**
     * Resolves the absolute path for specified file path, if this file path already denotes a
     * absolute file path, it will just return this path. Otherwise it will prefix the file path with
     * this schedule's working directory and return that.
     *
     * @param filePath the relative or absolute path to the file
     * @return the absolute path
     */
    public String resolveAbsolutePath(String filePath) {
        if (Luntbuild.isEmpty(filePath))
            return getWorkDirRaw();
        File file = new File(filePath);
        if (file.isAbsolute())
            return filePath;
        else
            return getWorkDirRaw() + File.separator + filePath;
    }

	/**
	 * Gets the facade of this schedule.
	 * 
	 * @return the facade of this schedule
	 */
    public ScheduleFacade getFacade() {
        ScheduleFacade facade = new ScheduleFacade();
        facade.setId(getId());
        facade.setName(getName());
        facade.setScheduleDisabled(isScheduleDisabled());
        facade.setDescription(getDescription());
        facade.setNextVersion(getNextVersion());
        facade.setVariables(getVariables());

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
        facade.setBuildNecessaryCondition(getBuildNecessaryCondition());
        facade.setAssociatedBuilderNames(getAssociatedBuilderNames());
        facade.setAssociatedPostbuilderNames(getAssociatedPostbuilderNames());
        facade.setPostbuildStrategy(getPostbuildStrategy());
        facade.setBuildType(getBuildType());
        facade.setLabelStrategy(getLabelStrategy());
        facade.setNotifyStrategy(getNotifyStrategy());
        facade.setTriggerDependencyStrategy(getTriggerDependencyStrategy());
        facade.setBuildCleanupStrategy(getBuildCleanupStrategy());
        facade.setBuildCleanupStrategyData(getBuildCleanupStrategyData());
        facade.setStatus(getStatus());
        facade.setStatusDate(getStatusDate());
        facade.setUrl(getUrl());
        facade.setProjectId(getProject().getId());
        facade.setDependentScheduleIds(getDependentScheduleIds());
        facade.setWorkingPath(getWorkingPath());
        return facade;
    }

	/**
	 * Sets the facade of this schedule.
	 * 
	 * @param facade the schedule facade
	 * @throws RuntimeException if the cron expression  for a cron trigger is invalid
	 */
    public void setFacade(ScheduleFacade facade) throws RuntimeException {
        setScheduleDisabled(facade.isScheduleDisabled());
        setDescription(facade.getDescription());
        setNextVersion(facade.getNextVersion());
        setVariables(facade.getVariables());
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

        setBuildNecessaryCondition(facade.getBuildNecessaryCondition());
        setAssociatedBuilderNames(facade.getAssociatedBuilderNames());
        setAssociatedPostbuilderNames(facade.getAssociatedPostbuilderNames());
        setBuildType(facade.getBuildType());
        setLabelStrategy(facade.getLabelStrategy());
        setNotifyStrategy(facade.getNotifyStrategy());
        setPostbuildStrategy(facade.getPostbuildStrategy());
        setTriggerDependencyStrategy(facade.getTriggerDependencyStrategy());
        setBuildCleanupStrategy(facade.getBuildCleanupStrategy());
        setBuildCleanupStrategyData(facade.getBuildCleanupStrategyData());
        setDependentScheduleIds(facade.getDependentScheduleIds());
        setStatus(facade.getStatus());
        setStatusDate(facade.getStatusDate());
        setWorkingPath(facade.getWorkingPath());
    }

    /**
     * Gets the status of this schedule.
     * 
     * @return the status
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_RUNNING
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_SUCCESS
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status of this schedule.
     * 
     * @param status the status
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_FAILED
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_RUNNING
     * @see com.luntsys.luntbuild.facades.Constants#SCHEDULE_STATUS_SUCCESS
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the date when the status of this schedule was last updated.
     * 
     * @return the date
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * Sets the date when the status of this schedule was last updated.
     * 
     * @param statusDate the date
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Gets the list of builds belonging to this schedule.
     * 
     * @return the list
     */
    public Set getBuilds() {
        if (builds == null)
            builds = new HashSet();
        return builds;
    }

    /**
     * Sets the list of builds belonging to this schedule.
     * 
     * @param builds the list
     */
    public void setBuilds(Set builds) {
        this.builds = builds;
    }

    /**
     * Validates a post-build strategy.
     * 
     * @param postbuildStrategy the post-build strategy
     * @throws ValidationException if the post-build strategy is invalid
     */
    public static void validatePostbuildStrategy(int postbuildStrategy) throws ValidationException {
        if (postbuildStrategy != Constants.POSTBUILD_ALWAYS && postbuildStrategy != Constants.POSTBUILD_IF_FAILED &&
                postbuildStrategy != Constants.POSTBUILD_IF_SUCCESS && postbuildStrategy != com.luntsys.luntbuild.facades.Constants.POSTBUILD_NONE)
            throw new ValidationException("Invalid post-build strategy");
    }

    /**
     * Validates a build type.
     * 
     * @param buildType the build type
     * @throws ValidationException if the build type is invalid
     */
    public static void validateBuildType(int buildType) throws ValidationException {
        if (buildType != com.luntsys.luntbuild.facades.Constants.BUILD_TYPE_CLEAN && buildType != Constants.BUILD_TYPE_INCREMENT)
            throw new ValidationException("Invalid build type");
    }

    /**
     * Validates a dependent triggering strategy.
     * 
     * @param triggerDependencyStrategy the dependent triggering strategy
     * @throws ValidationException if the dependent triggering strategy is invalid
     */
    public static void validateTriggerDependencyStrategy(int triggerDependencyStrategy) throws ValidationException {
        if (triggerDependencyStrategy != Constants.TRIGGER_ALL_DEPENDENT_SCHEDULES &&
                triggerDependencyStrategy != com.luntsys.luntbuild.facades.Constants.TRIGGER_NONE_DEPENDENT_SCHEDULES &&
                triggerDependencyStrategy != Constants.TRIGGER_SCHEDULES_DEPENDS_ON_THIS &&
                triggerDependencyStrategy != Constants.TRIGGER_SCHEDULES_THIS_DEPENDS_ON)
            throw new ValidationException("Invalid dependent triggering strategy");
    }

    /**
     * Validates a build necessary condition.
     * 
     * @param buildNecessaryCondition the build necessary condition
     * @throws ValidationException if the build necessary condition is invalid
     */
    public static void validateBuildNecessaryCondition(String buildNecessaryCondition) throws ValidationException {
        if (Luntbuild.isEmpty(buildNecessaryCondition))
            throw new ValidationException("Invalid build necessary condition: should not be empty!");
        if (buildNecessaryCondition.indexOf(Luntbuild.TRIGGER_NAME_SEPERATOR) != -1)
            throw new ValidationException("Invalid build necessary condition: should not contain sequence \"" +
                    Luntbuild.TRIGGER_NAME_SEPERATOR + "\"");
        try {
            Ognl.parseExpression(buildNecessaryCondition);
        } catch (OgnlException e) {
            throw new ValidationException("Invalid build necessary condition: " + buildNecessaryCondition +
                    ", reason: " + e.getMessage());
        }
    }

    /**
     * Validates a label strategy.
     * 
     * @param labelStrategy the label strategy
     * @throws ValidationException if the label strategy is invalid
     */
    public static void validateLabelStrategy(int labelStrategy) throws ValidationException {
        if (labelStrategy != Constants.LABEL_ALWAYS && labelStrategy != Constants.LABEL_IF_SUCCESS &&
                labelStrategy != Constants.LABEL_NONE)
            throw new ValidationException("Invalid label strategy");
    }

    /**
     * Validates a notify strategy.
     * 
     * @param notifyStrategy the notify strategy
     * @throws ValidationException if the notify strategy is invalid
     */
    public static void validateNotifyStrategy(int notifyStrategy) throws ValidationException {
        if (notifyStrategy != Constants.NOTIFY_WHEN_STATUS_CHANGED && notifyStrategy != Constants.NOTIFY_ALWAYS &&
                notifyStrategy != Constants.NOTIFY_IF_FAILED && notifyStrategy != Constants.NOTIFY_IF_SUCCESS &&
                notifyStrategy != Constants.NOTIFY_NONE && notifyStrategy != Constants.NOTIFY_IF_FAILED_OR_CHANGED)
            throw new ValidationException("Invalid notify strategy");
    }

    /**
     * Gets the URL to this schedule.
     * 
     * @return the URL
     */
    public String getUrl() {
        return Luntbuild.getServletUrl() + "?service=external/ProjectPage&sp=l" +
                getProject().getId() + "&sp=l" + ProjectPage.SERVICE_PARAMETER_SCHEDULES;
    }

    /**
     * Gets the publishing directory of this schedule, builds generated in this schedule will be put under this
     * directory.
     * 
     * @return the publishing
     */
    public String getPublishDir() {
        String publishDir = getProject().getPublishDir() + File.separator + getName();
        try {
            publishDir = new File(publishDir).getCanonicalPath();
            return publishDir.replaceAll("\\\\", "\\\\\\\\"); // in order to keep back slash for ognl expression evaluation
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * Checks if this schedule is selected.
	 * 
	 * @return <code>true</code> if selected
	 */
    public boolean isSelected() {
        return selected;
    }

	/**
	 * Sets if this schedule is selected.
	 * 
	 * @param selected if <code>true</code>, schedule is selected
	 */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Gets the list of schedule ids this schedule depends on.
     * 
     * @return the list of schedule ids
     */
    public List getDependentScheduleIds() {
        if (dependentScheduleIds == null) {
            dependentScheduleIds = new ArrayList();
        }
        return dependentScheduleIds;
    }

    /**
     * Sets the list of schedule ids this schedule depends on.
     * 
     * @param dependentScheduleIds the list of schedule ids
     */
    public void setDependentScheduleIds(List dependentScheduleIds) {
        this.dependentScheduleIds = dependentScheduleIds;
    }

	/**
	 * Gets the sub set of schedules that this schedule depends from a list of schedules.
	 * 
	 * @param userData the list of schedules
	 * @return the sub set of schedules from <code>userData</code> that this schedule depends on
	 */
    public Set getDependsOn(Object userData) {
        List schedules = (List) userData;
        Set dependsOn = new HashSet();
        Iterator itId = getDependentScheduleIds().iterator();
        while (itId.hasNext()) {
            Schedule schedule = new Schedule();
            schedule.setId(((Long) itId.next()).longValue());
            int index = schedules.indexOf(schedule);
            if (index != -1)
                dependsOn.add(schedules.get(index));
        }
        return dependsOn;
    }

	/**
	 * Schedules a trigger for this schedule as part of dependency execution.
	 * 
	 * @param userData the list of build params
	 * @see BuildParams
	 */
    public void visit(Object userData) {
        if (isDisabled()) {
            logger.debug("Schedule \"" + getProject().getName() + "/" + getName() + "\" disabled, not visiting");
        } else {
            logger.debug("Visit schedule \"" + getProject().getName() + "/" + getName() + "\"");
            try {
                SecurityHelper.runAsSiteAdmin();
                BuildParams buildParams;
                if (userData == null)
                    buildParams = getBuildParams();
                else
                    buildParams = (BuildParams) userData;
    
                SimpleTrigger trigger = new SimpleTrigger();
                trigger.setGroup(BuildGenerator.DEPENDENT_GROUP);
                trigger.setName(constructTriggerName(buildParams));
                trigger.setRepeatCount(0);
                trigger.setRepeatInterval(0);
                trigger.setStartTime(new Date(System.currentTimeMillis()));
                Luntbuild.getSchedService().scheduleBuild(this, trigger);
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    if (!Luntbuild.getSchedService().isTriggerAvailable(trigger))
                        break;
                }
            } catch (Throwable throwable) {
                logger.error("Visit of schedule \"" + getProject().getName() + "/" + getName() + "\" failed: " + throwable.getMessage());
            }
        }
    }

    /**
     * Gets the build necessary condition for this schedule.
     * 
     * @return the build necessary condition
     */
    public String getBuildNecessaryCondition() {
        return buildNecessaryCondition;
    }

    /**
     * Sets the build necessary condition of this schedule.
     * 
     * @param buildNecessaryCondition the build necessary condition
     */
    public void setBuildNecessaryCondition(String buildNecessaryCondition) {
        this.buildNecessaryCondition = buildNecessaryCondition;
    }

    /**
     * Determines if the VCS contents have changed for the project of this schedule since last build of this schedule.
     * This function will use the following thread local variables in {@link OgnlHelper}:
	 * <p><code>workingSchedule</code>, this variable denotes the project which this method is initiated by</p>
	 * <p><code>antProject</code>, this variable denotes the logging ant project this method should use</p>
	 * <p><code>revisions</code>, this variable denotes the revisions for the working schedule</p>
     * 
	 * @return <code>true</code> if the VCS contents of the project has changed
     */
    public boolean isVcsModified() {
        Schedule workingSchedule = OgnlHelper.getWorkingSchedule();
        Revisions revisions = new Revisions();
        Build build = Luntbuild.getDao().loadLastBuild(this);
        if (build == null) {
            if (OgnlHelper.getRevisions() == null && workingSchedule.getProject() == getProject()) {
                revisions.getChangeLogs().add("========== Change log ignored: base build does not exist ==========");
                OgnlHelper.setRevisions(revisions);
            }
            return true;
        }
        return getProject().isVcsModifiedSince(build.getStartDate());
    }

    /**
     * Whether or not there are new builds generated in schedules which this schedule depends on.
     * 
     * @return <code>true</code> if there are new builds
     */
    public boolean isDependencyNewer() {
        Build lastBuild = Luntbuild.getDao().loadLastBuild(this);
        if (lastBuild == null)
            return true;
        Set dependents = getDependsOn(Luntbuild.getDao().loadSchedules());
        boolean isDependentNewer = false;
        Iterator it = dependents.iterator();
        while (it.hasNext()) {
            Schedule dependentSchedule = (Schedule) it.next();
            Build lastBuildOfDependentSchedule = Luntbuild.getDao().loadLastBuild(dependentSchedule);
            if (lastBuildOfDependentSchedule != null && lastBuildOfDependentSchedule.getId() > lastBuild.getId()) {
                isDependentNewer = true;
                break;
            }
        }
        return isDependentNewer;
    }

    /**
     * Whether or not all schedules which this schedule depends on were successful.
     * 
     * @return <code>true</code> if all dependencies were successful
     */
    public boolean isDependencySuccessful() {
        return getNotSatisfiedDependency() == null;
    }

	/**
	 * Gets the first dependent schedule found that has not be satisfied (successful build).
	 * 
	 * @return the unsatisfied dependency or <code>null</code>
	 */
    public Schedule getNotSatisfiedDependency() {
        Set dependents = getDependsOn(Luntbuild.getDao().loadSchedules());
        Iterator it = dependents.iterator();
        while (it.hasNext()) {
            Schedule dependentSchedule = (Schedule) it.next();
            Build lastBuildOfDependentSchedule = Luntbuild.getDao().loadLastBuild(dependentSchedule);
            if (lastBuildOfDependentSchedule == null || lastBuildOfDependentSchedule.getStatus() != Constants.BUILD_STATUS_SUCCESS)
                return dependentSchedule;
        }
        return null;
    }

    /**
     * Whether or not this schedule should always run a build when triggered.
     * 
     * @return <code>true</code> if builds should always run
     */
    public boolean isAlways() {
        return true;
    }

    /**
     * Whether or not this schedule should never run a build when triggered.
     * 
     * @return <code>true</code> if builds should never run
     */
    public boolean isNever() {
        return false;
    }

    /**
     * Whether or not the last build of this schedule failed.
     * 
     * @return <code>true</code> if last build failed
     */
    public boolean isAlwaysIfFailed() {
        Build lastBuild = getLastBuild();
        if (lastBuild == null || lastBuild.getStatus() == Constants.BUILD_STATUS_FAILED)
            return true;
        else
            return isVcsModified() || isDependencyNewer();
    }

    /**
     * Gets the system object. Mainly used for ognl evaluation.
     * 
     * @return the system object
     */
    public OgnlHelper getSystem() {
        return new OgnlHelper();
    }

    /**
     * Gets the dependent triggering strategy.
     * 
     * @return the dependent triggering strategy
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_ALL_DEPENDENT_SCHEDULES
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_NONE_DEPENDENT_SCHEDULES
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_DEPENDS_ON_THIS
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_THIS_DEPENDS_ON
     */
    public int getTriggerDependencyStrategy() {
        return triggerDependencyStrategy;
    }

    /**
     * Sets the dependent triggering strategy.
     * 
     * @param triggerDependencyStrategy the dependent triggering strategy
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_ALL_DEPENDENT_SCHEDULES
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_NONE_DEPENDENT_SCHEDULES
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_DEPENDS_ON_THIS
     * @see com.luntsys.luntbuild.facades.Constants#TRIGGER_SCHEDULES_THIS_DEPENDS_ON
     */
    public void setTriggerDependencyStrategy(int triggerDependencyStrategy) {
        this.triggerDependencyStrategy = triggerDependencyStrategy;
    }

	/**
	 * Constructs a unique tigger name from build parameters.
	 * 
	 * @param buildParams the build parameters
	 * @return the trigger name
	 * @see #parseTriggerName(String)
	 */
    public static String constructTriggerName(BuildParams buildParams) {
        String triggerName = buildParams.getBuildNecessaryCondition() +
                Luntbuild.TRIGGER_NAME_SEPERATOR + buildParams.getBuildType() +
                Luntbuild.TRIGGER_NAME_SEPERATOR + buildParams.getBuildVersion() +
                Luntbuild.TRIGGER_NAME_SEPERATOR + buildParams.getLabelStrategy() +
                Luntbuild.TRIGGER_NAME_SEPERATOR + buildParams.getNotifyStrategy() +
                Luntbuild.TRIGGER_NAME_SEPERATOR + buildParams.getPostbuildStrategy() +
                Luntbuild.TRIGGER_NAME_SEPERATOR + buildParams.getTriggerDependencyStrategy() +
                Luntbuild.TRIGGER_NAME_SEPERATOR + buildParams.getScheduleId() +
                Luntbuild.TRIGGER_NAME_SEPERATOR + System.currentTimeMillis() + Thread.currentThread().hashCode();
        return triggerName;
    }

	/**
	 * Gets build parameters from a trigger name.
	 * 
	 * @param triggerName the trigger name
	 * @return the build params
	 * @see #constructTriggerName(BuildParams)
	 */
    public static BuildParams parseTriggerName(String triggerName) {
        BuildParams buildParams = new BuildParams();
        String fields[] = triggerName.split("\\" + Luntbuild.TRIGGER_NAME_SEPERATOR);
        buildParams.setBuildNecessaryCondition(fields[0]);
        buildParams.setBuildType(new Integer(fields[1]).intValue());
        buildParams.setBuildVersion(fields[2]);
        buildParams.setLabelStrategy(new Integer(fields[3]).intValue());
        buildParams.setNotifyStrategy(new Integer(fields[4]).intValue());
        buildParams.setPostbuildStrategy(new Integer(fields[5]).intValue());
        buildParams.setTriggerDependencyStrategy(new Integer(fields[6]).intValue());
        buildParams.setScheduleId(new Integer(fields[7]).intValue());
        return buildParams;
    }

	/**
	 * Gets the build params for this schedule.
	 * 
	 * @return the build params
	 */
    public BuildParams getBuildParams() {
        BuildParams buildParams = new BuildParams();
        buildParams.setBuildNecessaryCondition(getBuildNecessaryCondition());
        buildParams.setBuildType(getBuildType());
        buildParams.setBuildVersion("");
        buildParams.setLabelStrategy(getLabelStrategy());
        buildParams.setNotifyStrategy(getNotifyStrategy());
        buildParams.setPostbuildStrategy(getPostbuildStrategy());
        buildParams.setTriggerDependencyStrategy(getTriggerDependencyStrategy());
        buildParams.setScheduleId(getId());
        return buildParams;
    }

	/**
	 * Returns a string representation of this object.
	 * 
	 * @return a string representation of this object
	 */
    public String toString() {
        return getProject().getName() + "/" + getName();
    }

    /**
     * Gets the latest build of this schedule. <code>null</code> if there are no builds in this schedule.
     * 
     * @return the latest build
     */
    public Build getLastBuild() {
        return Luntbuild.getDao().loadLastBuild(this);
    }

    /**
     * Gets the last success build for this schedule. <code>null</code> if there are no successful
     * builds in this schedule.
     * 
     * @return the last success build
     */
    public Build getLastSuccessBuild() {
        return Luntbuild.getDao().loadLastSuccessBuild(this);
    }

    /**
     * Gets the running build of this schedule. <code>null</code> if there are no running builds in
     * this schedule when this method is called.
     * 
     * @return the running build
     */
    public Build getRunningBuild() {
        return Luntbuild.getDao().loadRunningBuild(this);
    }

    /**
     * Gets the build with specified version.
     * 
     * @param version the build version
     * @return the build with specified version
     */
    public Build getBuild(String version) {
        return Luntbuild.getDao().loadBuild(getProject().getName(), getName(), version);
    }

    /**
     * Empty method, only available to conform with Ognl indexed property requirement
     * @param version
     * @param build
     */
    public void setBuild(String version, Build build) {
        // empty method, just used to conform with ognl index property
    }

	/**
	 * Sets the current execution thread for this schedule.
	 * 
	 * @param currentThread the new current thread
	 */
    public void setCurrentThread(Thread currentThread) {
        currentThreads.put(new Long(getId()), currentThread);
    }

	/**
	 * Gets the current execution thread for this schedule.
	 * 
	 * @return the current thread
	 */
    public Thread getCurrentThread() {
        return (Thread) currentThreads.get(new Long(getId()));
    }

    /**
     * Gets the build cleanup strategy.
     * 
     * @return the build cleanup strategy.
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_CLEANUP_NONE
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_KEEP_BY_COUNT
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_KEEP_BY_DAYS
     */
    public int getBuildCleanupStrategy() {
        return buildCleanupStrategy;
    }

    /**
     * Sets the build cleanup strategy.
     * 
     * @param buildCleanupStrategy the build cleanup strategy.
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_CLEANUP_NONE
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_KEEP_BY_COUNT
     * @see com.luntsys.luntbuild.facades.Constants#BUILD_KEEP_BY_DAYS
     */
    public void setBuildCleanupStrategy(int buildCleanupStrategy) {
        this.buildCleanupStrategy = buildCleanupStrategy;
    }

    /**
     * Gets the build cleanup data.
     * 
     * @return the build cleanup data
     */
    public String getBuildCleanupStrategyData() {
        return buildCleanupStrategyData;
    }

    /**
     * Sets the build cleanup data.
     * 
     * @param buildCleanupStrategyData the build cleanup data
     */
    public void setBuildCleanupStrategyData(String buildCleanupStrategyData) {
        this.buildCleanupStrategyData = buildCleanupStrategyData;
    }

    /**
     * Sets the build version number for the next build. This property is updated automatically
     * by the building process and can also be adjusted manually from user interface.
     * 
     * @param nextVersion the next build version
     */
    public void setNextVersion(String nextVersion) {
        this.nextVersion = nextVersion;
    }

    /**
     * Gets the next build version of this schedule.
     * 
     * @return the next build version
     */
    public String getNextVersion() {
        return nextVersion;
    }

    /**
     * Validates a build version.
     * 
     * @param buildVersion the build version
     * @throws ValidationException if the build version is invalid
     */
    public void validateBuildVersion(String buildVersion) throws ValidationException {
        if (Luntbuild.isEmpty(buildVersion))
            throw new ValidationException("version string can not be empty!");
        try {
            OgnlHelper.setTestMode(true);
            OgnlHelper.setAntProject(Luntbuild.createAntProject());
            String versionValue = Luntbuild.evaluateExpression(this, buildVersion);
            if (versionValue.matches(".*[/\\\\:*?\"<>|$,;@#].*"))
                throw new ValidationException("version string should not contain characters: /\\:*?\"<>|$,;@#");
            if (!versionValue.matches(".*[\\d].*"))
                throw new ValidationException("version string should contain digit");
        } catch (Throwable e) {
            throw new ValidationException("Error parsing version string: " + buildVersion +
                    ", reason: "+ Luntbuild.getExceptionMessage(e));
        }
    }

    /**
     * Gets all variables encoded in a string.
     * 
     * @return all variables encoded in a string
     */
    public String getVariables() {
    	return variables;
    }

    /**
     * Sets all variables encoded in a string.
     * 
     * @param variables all variables encoded in a string
     */
    public void setVariables(String variables) {
    	this.variables = variables;
    }

    /**
     * Gets the variable with specified variable name.
     * 
     * @param name the variable name
     * @return the variable, will not be <code>null</code>
     */
    public Variable getVar(String name) {
    	if (!Luntbuild.isEmpty(getVariables())) {
    		BufferedReader reader = new BufferedReader(new StringReader(getVariables()));
    		try {
    			String line;
    			while ((line = reader.readLine()) != null) {
    				if (line.trim().equals(""))
    					continue;
    				String varName = Luntbuild.getAssignmentName(line);
    				String varValue = Luntbuild.getAssignmentValue(line);
    				if (name.trim().equals(varName)) {
    					return new Variable(this, name.trim(), varValue);
    				}
    			}
    		} catch (IOException e) {
    			// ignores
    		}
    	}
    	return new Variable(this, name.trim(), "");
    }

    /**
     * Sets the value of specified variable.
     * 
     * @param name the name of the variable to set
     * @param var the value to set
	 * @throws AccessDeniedException if the currently logged in user is not a project admin for this project
     */
    public void setVar(String name, Variable var) {
    	if (!SecurityHelper.isPrjAdministrable(getId()))
    		throw new AccessDeniedException("Permission denied!");
    	if (OgnlHelper.isTestMode())
    		return;
    	String newVariables = "";
    	boolean varFound = false;
    	if (!Luntbuild.isEmpty(getVariables())) {
    		BufferedReader reader = new BufferedReader(new StringReader(getVariables()));
    		try {
    			String line;
    			while ((line = reader.readLine()) != null) {
    				if (line.trim().equals(""))
    					continue;
    				String varName = Luntbuild.getAssignmentName(line);
    				if (name.trim().equals(varName)) {
    					newVariables += name.trim() + "=" + var.getValue() + "\n";
    					varFound = true;
    				} else
    					newVariables += line + "\n";
    			}
    		} catch (IOException e) {
    			// ignores
    		}
    	}
    	if (!varFound)
    		newVariables += name.trim() + "=" + var.getValue() + "\n";
    	setVariables(newVariables);
    	Luntbuild.getDao().saveSchedule(this);
    }

    /**
     * Gets the list of associated builder names.
     * 
     * @return the list of associated builder names
     * @see #getAssociatedBuilders()
     */
    public List getAssociatedBuilderNames() {
        if (associatedBuilderNames == null)
            associatedBuilderNames = new ArrayList();
        Iterator it = associatedBuilderNames.iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            if (getProject().getBuilderByName(name) == null)
                it.remove();
        }
        return associatedBuilderNames;
    }

    /**
     * Gets the list associated builders.
     * 
     * @return the list associated builders
     * @see Builder
     */
    public List getAssociatedBuilders() {
        List associatedBuilders = new ArrayList();
        Iterator it = associatedBuilderNames.iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            Builder builder = getProject().getBuilderByName(name);
            if (builder != null)
                associatedBuilders.add(builder);
        }
        return associatedBuilders;
    }

    /**
     * Sets the list of associated builder names for this schedule.
     * 
     * @param associatedBuilderNames the list of associated builder names
     */
    public void setAssociatedBuilderNames(List associatedBuilderNames) {
        this.associatedBuilderNames = associatedBuilderNames;
    }

    /**
     * Gets the list of associated post-builder names.
     * 
     * @return the list of associated post-builder names
     * @see #getAssociatedPostbuilders()
     */
    public List getAssociatedPostbuilderNames() {
        if (associatedPostbuilderNames == null)
            associatedPostbuilderNames = new ArrayList();
        Iterator it = associatedPostbuilderNames.iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            if (getProject().getBuilderByName(name) == null)
                it.remove();
        }
        return associatedPostbuilderNames;
    }

    /**
     * Gets the list of associated post-builders.
     * 
     * @return the list of associated post-builders
     * @see Builder
     */
    public List getAssociatedPostbuilders() {
        List associatedPostbuilders = new ArrayList();
        Iterator it = associatedPostbuilderNames.iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            Builder builder = getProject().getBuilderByName(name);
            if (builder != null)
                associatedPostbuilders.add(builder);
        }
        return associatedPostbuilders;
    }

    /**
     * Sets the list of associated post-builder names for this schedule.
     * 
     * @param associatedPostbuilderNames the list of associated post-builder names
     */
    public void setAssociatedPostbuilderNames(List associatedPostbuilderNames) {
        this.associatedPostbuilderNames = associatedPostbuilderNames;
    }

    /**
     * Gets the list of schedules that depend on this schedule.
     * 
     * @return the list of schedules
     * @see Schedule
     */
    public List getDependentSchedules() {
        if (dependentSchedules == null)
            dependentSchedules = new ArrayList();
        return dependentSchedules;
    }

    /**
     * Sets the list of schedules that depend on this schedule.
     * @param dependentSchedules the list of schedules
     * @see Schedule
     */
    public void setDependentSchedules(List dependentSchedules) {
        this.dependentSchedules = dependentSchedules;
    }

    /**
     * Gets the quartz job name for this schedule.
     * 
     * @return the quartz job name
     */
    public String getJobName() {
        String workingDir = getWorkDirRaw();
        try {
            workingDir = new File(workingDir).getCanonicalPath();
            return workingDir.replaceAll("[:\\s/\\\\]", "-");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the working path setting of this schedule.
     * <p>NOTE: Do not call this method to get working directory of this schedule.
     * Instead, you should call {@link Schedule#getWorkDirRaw()}.</p>
     * 
     * @return the working path
     */
    public String getWorkingPath() {
        return workingPath;
    }

    /**
     * Sets the working path of this schedule.
     * 
     * @param workingPath the working path
     */
    public void setWorkingPath(String workingPath) {
        this.workingPath = workingPath;
    }
}
