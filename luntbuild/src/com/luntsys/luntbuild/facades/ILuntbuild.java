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

package com.luntsys.luntbuild.facades;

import java.util.List;

import com.luntsys.luntbuild.facades.lb12.BuildFacade;
import com.luntsys.luntbuild.facades.lb12.ProjectFacade;
import com.luntsys.luntbuild.facades.lb12.ScheduleFacade;
import com.luntsys.luntbuild.facades.lb12.UserFacade;

/**
 * Luntbuild service interface.
 * 
 * <p>This interface is to define general services provided by luntbuild system. Other system interact
 * with luntbuild through this interface.</p>
 *
 * <p>NOTE: Methods with the same name (even parameters are not the same) should not exist in this interface.
 * Otherwise, hessian will complains about a class instantiation error.</p>
 *
 * @author robin shine
 */
public interface ILuntbuild {

    /**
     * Returns all projects configured in the system.
     * 
     * @return a list of project facades
     * @see ProjectFacade
     */
    public List getAllProjects();

    /**
     * Returns all schedules configured for all projects in the system.
     * 
     * @return a list of schedule facades
     * @see ScheduleFacade
     */
    public List getAllSchedules();

    /**
     * Returns all schedules configured for specified project.
     * 
     * @param projectName the project name
     * @return a list of schedule facades
     * @see ScheduleFacade
     */
    public List getAllSchedulesOfProject(String projectName);

    /**
     * Gets a project by name, if more than one project have the same name a random project
     * will be returned.
     * 
     * @param projectName the name of the project
     * @return the project with specified name, or <code>null</code> if no projects have this name
     */
    public ProjectFacade getProjectByName(String projectName);

    /**
     * Gets a schedule by project and schedule name.
     * 
     * @param projectName the name of the project
     * @param scheduleName the name of the schedule
     * @return the schedule, or <code>null</code> if no schedules within specified project
     * have this specified name
     */
    public ScheduleFacade getScheduleByName(String projectName, String scheduleName);

    /**
     * Triggers a build within the specified schedule name and with the specified build parameters.
     * 
     * @param projectName the name of the project name
     * @param scheduleName the name of the schedule to trigger build in
     * @param buildParams the build parameters
     */
    public void triggerBuild(String projectName, String scheduleName, BuildParams buildParams);

    /**
     * Updates the properties of a project based on a facade object.
     * 
     * @param projectFacade the facade containing the updated properties
     */
    public void saveProject(ProjectFacade projectFacade);

    /**
     * Updates the properties of a schedule based on a facade object.
     * 
     * @param scheduleFacade the facade containing the updated properties
     */
    public void saveSchedule(ScheduleFacade scheduleFacade);

    /**
     * Gets a Luntbuild system property value by specifying property name.
     * <p>Refer to {@link Constants} for defined system property names.</p>
     * 
     * @param propertyName the name of the property
     * @return the property value
     */
    public String getSystemProperty(String propertyName);

    /**
     * Sets a Luntbuild system property value.
     * <p>Refer to {@link Constants} for defined system property names.</p>
     * 
     * @param propertyName the name of the property
     * @param propertyValue the new value for the property
     */
    public void setSystemProperty(String propertyName, String propertyValue);

    /**
     * Searches for builds in the system.
     * 
     * @param condition the search criteria
     * @param start the start position of search operation
     * @param count the maximum number of builds to retrieve. Use <code>0</code> for no limit.
     * @return the builds that were found
     */
    public List searchBuilds(SearchCriteria condition, int start, int count);

    /**
     * Deletes builds matching a specified search criteria.
     *
     * @param searchCriteria the search criteria
     * @since 1.3
     */
    public void deleteBuilds(SearchCriteria searchCriteria);

    /**
     * Deletes a specified build.
     *
     * @param buildFacade the build facade
     * @since 1.3
     */
    public void deleteBuild(BuildFacade buildFacade);

    /**
     * Moves builds matching the specified search criteria to the specified schedule.
     *
     * @param searchCriteria the search criteria
     * @param projectName the destination project name
     * @param scheduleName the destination schedule name
     * @since 1.3
     */
    public void moveBuilds(SearchCriteria searchCriteria, String projectName, String scheduleName);

    /**
     * Moves a build to the specified schedule.
     * 
     * @param buildFacade the build facade
     * @param projectName the destination project name
     * @param scheduleName the destination schedule name
     * @since 1.3
     */
    public void moveBuild(BuildFacade buildFacade, String projectName, String scheduleName);

    /**
     * Get latest build for specified schedule
     * @param scheduleFacade
     * @return the last build, or <code>null</code> if there are no builds inside the schedule
     */
    public BuildFacade getLastBuild(ScheduleFacade scheduleFacade);

    /**
     * Gets the latest build for the specified schedule.
     * 
     * @param projectName the project name
     * @param scheduleName the schedule name
     * @return the last build, or <code>null</code> if there are no builds inside the schedule
     */
    public BuildFacade getLastBuild(String projectName, String scheduleName);

    /**
     * Gets the last successful build for the specified schedule.
     * 
     * @param scheduleFacade the schedule facade
     * @return the last successful build, or <code>null</code> if there are no successful builds inside the schedule
     */
    public BuildFacade getLastSuccessBuild(ScheduleFacade scheduleFacade);

    /**
     * Gets the last successful build for the specified schedule.
     * 
     * @param projectName the project name
     * @param scheduleName the schedule name
     * @return the last successful build, or <code>null</code> if there are no successful builds inside the schedule
     */
    public BuildFacade getLastSuccessBuild(String projectName, String scheduleName);

    /**
     * Creates a project based on a facade object.
     * 
     * @param projectFacade the project facade
     * @since 1.3
     */
    public void createProject(ProjectFacade projectFacade);

    /**
     * Deletes the specified project by name.
     * 
     * @param projectName the project name
     * @since 1.3
     */
    public void deleteProject(String projectName);

    /**
     * Creates a schedule based on a facade object.
     * 
     * @param scheduleFacade the schedule facade
     * @since 1.3
     */
    public void createSchedule(ScheduleFacade scheduleFacade);

    /**
     * Deletes the specified schedule.
     * 
     * @param scheduleFacade the schedule facade
     * @since 1.3
     */
    public void deleteSchedule(ScheduleFacade scheduleFacade);

    /**
     * Deletes all schedules configured for the specified project.
     * 
     * @param projectName the project name
     * @since 1.3
     */
    public void deleteAllSchedulesOfProject(String projectName);

    /**
     * Gets the build log of the specified build.
     * 
     * @param buildFacade the build facade
     * @return the build log
     * @since 1.3
     */
    public String[] getBuildLog(BuildFacade buildFacade);

    /**
     * Gets the build log of the specified build in HTML.
     * 
     * @param buildFacade the build facade
     * @return the build log
     * @since 1.3
     */
    public String[] getBuildLogHtml(BuildFacade buildFacade);

    /**
     * Gets the revision log of the specified build.
     * 
     * @param buildFacade the build facade
     * @return the revision log
     * @since 1.3
     */
    public String[] getRevisionLog(BuildFacade buildFacade);

    /**
     * Gets the revision log of the specified build in HTML.
     * 
     * @param buildFacade the build facade
     * @return the revision log
     * @since 1.3
     */
    public String[] getRevisionLogHtml(BuildFacade buildFacade);

    /**
     * Gets the system log.
     * 
     * @return the system log
     * @since 1.3
     */
    public String[] getSystemLog();

    /**
     * Gets the system log.
     * 
     * @return the system log
     * @since 1.3
     */
   public String[] getSystemLogHtml();

   /**
    * Checks if a user exists and can create project.
    * 
    * @param username the name of the user
    * @return <code>true</code> if the user exists and can create project
    * @since 1.3
    */
   public boolean canCreateProject(String username);

   /**
    * Gets a list of all users.
    * 
    * @return a list of all users
    * @since 1.3
    */
   public List getUsers();

   /**
    * Gets a user by name.
    * 
    * @param username the user name
    * @return the user, or <code>null</code> if the user does not exist
    * @since 1.3
    */
   public UserFacade getUser(String username);
}
