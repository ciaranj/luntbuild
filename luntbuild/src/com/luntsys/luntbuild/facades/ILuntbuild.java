package com.luntsys.luntbuild.facades;

import java.util.List;

import com.luntsys.luntbuild.facades.lb12.BuildFacade;
import com.luntsys.luntbuild.facades.lb12.UserFacade;

/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-19
 * Time: 17:37:16
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

/**
 * This interface is to define general services provided by luntbuild system. Other system interact
 * with luntbuild through this interface
 *
 * NOTE: Method with the same name(even parameters are not the same) should not exist in this interface.
 * Otherwise, hessian will complains about a class instantiation error.
 *
 * @author robin shine
 */
public interface ILuntbuild {
    /**
     * Return all projects configured in the system
     * @return list of {@link com.luntsys.luntbuild.facades.lb12.ProjectFacade}
     */
    List getAllProjects();

    /**
     * Return all schedules configured for all projects in the system
     * @return list of {@link com.luntsys.luntbuild.facades.lb12.ScheduleFacade}
     */
    List getAllSchedules();

    /**
     * Return all schedules configured for specified project
     * @param projectName
     * @return list of {@link com.luntsys.luntbuild.facades.lb12.ScheduleFacade}
     */
    List getAllSchedulesOfProject(String projectName);

    /**
     * Get project by project name, if more than one project have the same name, a random project
     * will be returned
     * @param projectName name of the returned project
     * @return one of the project with specified name, or null if no projects have this name
     */
    com.luntsys.luntbuild.facades.lb12.ProjectFacade getProjectByName(String projectName);

    /**
     * Get schedule by project and schedule name.
     * @param projectName project name of the schedule
     * @param scheduleName name of the schedule
     * @return schedule with specified project and name, or null if no schedules within specified project
     * have this specified name
     */
    com.luntsys.luntbuild.facades.lb12.ScheduleFacade getScheduleByName(String projectName, String scheduleName);

    /**
     * Trigger a build within specified project name, schedule name, and with specified build params
     * @param projectName project name to trigger build in
     * @param scheduleName schedule name to trigger build in
     * @param buildParams build parameters
     */
    void triggerBuild(String projectName, String scheduleName, com.luntsys.luntbuild.facades.BuildParams buildParams);

    /**
     * Update properties of a project based on its facade object
     * @param projectFacade
     */
    void saveProject(com.luntsys.luntbuild.facades.lb12.ProjectFacade projectFacade);

    /**
     * Update properties of a schedule based on its facade object
     * @param scheduleFacade
     */
    void saveSchedule(com.luntsys.luntbuild.facades.lb12.ScheduleFacade scheduleFacade);

    /**
     * Get luntbuild system property value by specifying property name.
     * Refer to {@link com.luntsys.luntbuild.facades.Constants} for defined system property names
     * @param propertyName
     * @return luntbuild system property value
     */
    String getSystemProperty(String propertyName);

    /**
     * Set luntbuild system property value. Refer to {@link com.luntsys.luntbuild.facades.Constants} for
     * defined system property names
     * @param propertyName
     * @param propertyValue
     */
    void setSystemProperty(String propertyName, String propertyValue);

    /**
     * Search for builds in the system
     * @param condition the condition matching builds should meet
     * @param start start position of search operation
     * @param count number of builds to retrieve. No limit will be set on number of
     * returned builds if this value equals to 0
     * @return found builds
     */
    List searchBuilds(SearchCriteria condition, int start, int count);

    /**
     * Delete builds matching specified search criteria.
     *
     * @param searchCriteria criteria
     * @since 1.3
     */
    public void deleteBuilds(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria);

    /**
     * Delete specified build.
     *
     * @param buildFacade build facade
     * @since 1.3
     */
    public void deleteBuild(BuildFacade buildFacade);

    /**
     * Move builds matching specified search criteria to specified schedule.
     *
     * @param searchCriteria criteria
     * @param projectName project name
     * @param scheduleName schedule name
     * @since 1.3
     */
    public void moveBuilds(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria,
            String projectName, String scheduleName);

    /** Move build to specified project/schedule.
     * @param buildFacade build
     * @param projectName project name
     * @param scheduleName schedule name
     * @since 1.3
     */
    public void moveBuild(BuildFacade buildFacade, String projectName, String scheduleName);

    /**
     * Get latest build for specified schedule
     * @param scheduleFacade
     * @return maybe null if there are not any builds inside this schedule
     */
    com.luntsys.luntbuild.facades.lb12.BuildFacade getLastBuild(com.luntsys.luntbuild.facades.lb12.ScheduleFacade scheduleFacade);

    /**
     * Get latest build for specified schedule
     * @param projectName
     * @param scheduleName
     * @return maybe null if there are not any builds inside this schedule
     */
    com.luntsys.luntbuild.facades.lb12.BuildFacade getLastBuild(String projectName, String scheduleName);

    /**
     * Get last successful build for specified schedule
     * @param scheduleFacade
     * @return maybe null if there are not any successful builds inside this schedule
     */
    com.luntsys.luntbuild.facades.lb12.BuildFacade getLastSuccessBuild(com.luntsys.luntbuild.facades.lb12.ScheduleFacade scheduleFacade);

    /**
     * Get last successful build for specified schedule
     * @param projectName
     * @param scheduleName
     * @return maybe null if there are not any successful builds inside this schedule
     */
    com.luntsys.luntbuild.facades.lb12.BuildFacade getLastSuccessBuild(String projectName, String scheduleName);

    /**
     * Create project based on its facade object
     * @param projectFacade
     * @since 1.3
     */
    void createProject(com.luntsys.luntbuild.facades.lb12.ProjectFacade projectFacade);

    /**
     *  Delete project by project name
     * @param projectName
     * @since 1.3
     */
    void deleteProject(String projectName);

    /**
     * Create schedule based on its facade object
     * @param scheduleFacade
     * @since 1.3
     */
    void createSchedule(com.luntsys.luntbuild.facades.lb12.ScheduleFacade scheduleFacade);

    /**
     *  Delete schedule by schedule object
     * @param scheduleFacade
     * @since 1.3
     */
    void deleteSchedule(com.luntsys.luntbuild.facades.lb12.ScheduleFacade scheduleFacade);

    /**
     * Delete all schedules configured for specified project
     * @param projectName
     * @since 1.3
     */
    public void deleteAllSchedulesOfProject(String projectName);

    /**
     * Get build log of the specified build
     * @param buildFacade
     * @return build log as array of strings
     * @since 1.3
     */
    String[] getBuildLog(com.luntsys.luntbuild.facades.lb12.BuildFacade buildFacade);

    /**
     * Get build log of the specified build in Html
     * @param buildFacade
     * @return build log as array of strings
     * @since 1.3
     */
    public String[] getBuildLogHtml(BuildFacade buildFacade);

    /**
     * Get revision log of the specified build
     * @param buildFacade
     * @return revision log as array of strings
     * @since 1.3
     */
    String[] getRevisionLog(com.luntsys.luntbuild.facades.lb12.BuildFacade buildFacade);

    /**
     * Get revision log of the specified build in Html
     * @param buildFacade
     * @return revision log as array of strings
     * @since 1.3
     */
    public String[] getRevisionLogHtml(BuildFacade buildFacade);

    /**
     * Get system log
     * @return system log as array of strings
     * @since 1.3
     */
    String[] getSystemLog();

    /**
     * Get system log
     * @return system log as array of strings
     * @since 1.3
     */
    public String[] getSystemLogHtml();

    /** Return true if user exists and can create project.
    *
    * @param username of a user
    * @return true if user exists and can create project
    * @since 1.3
    */
   public boolean canCreateProject(String username);

   /** Returns list of all users.
    * @return list of all users
    * @since 1.3
    */
   public List getUsers();

   /** Returns user facade if user exists.
    * @param username of a user
    * @return user facade if user exists.
    * @since 1.3
    */
   public UserFacade getUser(String username);

}
