/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-10
 * Time: 8:45:44
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
package com.luntsys.luntbuild.dao;

import com.luntsys.luntbuild.db.*;
import com.luntsys.luntbuild.facades.lb20.DataCollection;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Data access interface of this application.
 *
 * @author robin shine
 */
public interface Dao {
	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void saveProperties(Map properties);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	Map loadProperties();

	/** Save project
     * @param project
	 * @throws org.springframework.dao.DataAccessException
	 */
	void saveProject(Project project);

    /** Save project - for internal use only to avoid security
     * @param project
     * @throws org.springframework.dao.DataAccessException
     */
    void saveProjectInternal(Project project);

	/**
	 * Load all projects from persistence. Collections of the project may not be loaded.
	 * @return a list of project objects order by name
	 * @throws org.springframework.dao.DataAccessException
 	 */
	List loadProjects();

    /**
     * Load all projects from persistence - for INTERNAL use so it is not authorized.
     * Collections of the project may not be loaded.
     * @return a list of project objects order by name
     * @throws org.springframework.dao.DataAccessException
     */
    List loadProjectsInternal();

	/**
	 * Loads project information based on the project id. Collections of the project should be loaded.
	 * @param projectId the identifier of the project to load
	 * @return Project
	 * @throws org.springframework.dao.DataAccessException
	 */
	Project loadProject(long projectId);

	/**
	 * Load project information based on the project name. Collections of the project should be
	 * loaded
	 * @param projectName name of the project
	 * @return Project
	 * @throws org.springframework.dao.DataAccessException
	 */
	Project loadProject(String projectName);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteProject(Project project);

	/**
     * @param user
	 * @throws org.springframework.dao.DataAccessException
	 */
	void saveUser(User user);

    /** Save user, only for internal use without security checking!
     * @param user
     * @throws org.springframework.dao.DataAccessException
     */
    void saveUserInternal(User user);

    /**
	 * @throws org.springframework.dao.DataAccessException
 	 */
	List loadUsers();

	/**
	 *  for authentication purpose
	 *  we need to search for loginName
	 * @param userName
	 * @return
	 */
	User loadUser(String userName);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	User loadUser(long userId);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteUser(long userId);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void saveSchedule(Schedule schedule);

	/**
	 * Loads all schedules configured in the system. Collections of each schedule may not be loaded, but parent objects
	 * should be loaded
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	List loadSchedules();

	/**
	 * Loads schedule information based on the schedule id. Parent objects and Collections of the schedule should be loaded..
	 * @param scheduleId
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	Schedule loadSchedule(long scheduleId);

	/**
	 * Loads schedule information based on project name and schedule name. Parent objects and Collections of this schedule
	 * will also get loaded
	 * @param projectName name of the belonging project
	 * @param scheduleName name of the schedule to load
	 * @return
	 */
	Schedule loadSchedule(String projectName, String scheduleName);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteSchedule(Schedule schedule);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	VcsLogin loadVcsLogin(long vcsLoginId);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void saveVcsLogin(VcsLogin vcsLogin);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteVcsLogin(VcsLogin vcsLogin);

	/**
	 * Save a execution result of a schedule
	 * @param build execution result of a particular triggered execution of a particular schedule
	 * @throws org.springframework.dao.DataAccessException
	 */
	void saveBuild(Build build);

	/**
	 * Load the build by specifying the build identify. All parent object of this build should be loaded
	 * @param buildId
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	Build loadBuild(long buildId);

	/**
	 * Load build by specifying project name, schedule name and build version. All parent object and
	 * lazy collections of this build will be loaded also
	 * @param projectName
	 * @param scheduleName
	 * @param buildVersion
	 * @return
	 */
	Build loadBuild(String projectName, String scheduleName, String buildVersion);

	/**
	 * Load build by specifying project name, schedule name and build version. All parent object and
	 * lazy collections of this build will be loaded also
	 * @param projectName
	 * @param scheduleName
	 * @param buildVersion
	 * @return
	 */
	Build loadBuildInternal(String projectName, String scheduleName, String buildVersion);

	/**
	 * Delete the build by specifying the buildId
	 * @param build
	 */
	void deleteBuild(Build build);

	/**
	 * get the last build generated by the specified schedule
	 * @param schedule
	 * @return the last build, this value maybe null
	 * @throws org.springframework.dao.DataAccessException
	 */
	Build loadLastBuild(Schedule schedule);

	/**
	 * Get last build for specified schedule name in specified project name
	 * @param projectName
	 * @param scheduleName
	 * @return last build for specified project and specified schedule. Maybe null if does not exist
	 */
	Build loadLastBuild(String projectName, String scheduleName);

	/**
	 * Get last build for specified schedule name in specified project name
	 * @param projectName
	 * @param scheduleName
	 * @return last build for specified project and specified schedule. Maybe null if does not exist
	 */
	Build loadLastBuildInternal(String projectName, String scheduleName);

	/**
	 * get the last successful build generated by the specified schedule
	 * @param schedule
	 * @return the last success build, value maybe null
	 * @throws org.springframework.dao.DataAccessException
	 */
	Build loadLastSuccessBuild(Schedule schedule);

	/**
	 * get last successful build of specified project and schedule name
	 * @param projectName
	 * @param scheduleName
	 * @return last success build. Maybe null if does not exist
	 * @throws org.springframework.dao.DataAccessException
	 */
	Build loadLastSuccessBuild(String projectName, String scheduleName);

	/**
	 * Determines how many builds can be found using specified search criteria
	 * @param searchCriteria
	 * @return
	 */
	int countBuilds(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria);

	/**
	 * Searches builds by specifying search criteria
	 * @param searchCriteria
	 * @param start the start index (0 based) for the retrieved builds
	 * @param count number of builds to retrieve. If this value equals 0, no limits will be set
	 * on the number of retrieved builds.
	 * @return
	 */
	List searchBuilds(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria,  int start, int count);

	/**
	 * This method checks if there is any un-finished builds in the system,
	 * and mark them as failed. This method is called at  start up of the
	 * system in order to clear status of un-finished build.
	 * @throws org.springframework.dao.DataAccessException
	 */
	void processUnfinishedBuilds();

	/**
	 * This method checks if there is any running schedule in the system,
	 * and mark them as failed. This method is called at startup of the system in order
	 * to clear status of un-finished schedule executions
	 */
	void processUnfinishedSchedules();

	/**
	 * Delete all builds matching specified search criteria
	 * @param searchCriteria
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteBuilds(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria);

	/**
	 * Loads current running build for specified schedule
	 * @param schedule
	 * @return maybe null if there is no running build inside the specified schedule
	 * @throws org.springframework.dao.DataAccessException
	 */
	Build loadRunningBuild(Schedule schedule);

	/**
	 * Move builds matching specified criteria to specified schedule
	 * @param searchCriteria
	 * @param scheduleId
	 * @throws org.springframework.dao.DataAccessException
	 */
	void moveBuilds(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria, long scheduleId);

	/**
	 * Move specified build to specified schedule
	 * @param buildId
	 * @param scheduleId
	 * @throws org.springframework.dao.DataAccessException
	 */
	void moveBuild(long buildId, long scheduleId);

    /**
     *
     * @return all predefined security roles from db
     * @throws org.springframework.dao.DataAccessException
     */
    List loadRoles();

	boolean isUserNameUsed(User user);

	boolean isUserExist(String userName);

	void initialize();

	boolean isProjectNameUsed(Project project);

	boolean isScheduleNameUsed(Schedule schedule);

	boolean isVcsLoginNameUsed(VcsLogin vcsLogin);

	void reserveBuildsByCount(Schedule schedule, int reserveCount);

	void removeBuildsBefore(Schedule schedule, Date date);

	DataCollection loadDataCollection20();

	void saveDataCollection20(DataCollection data12);

	void eraseExistingData();
}
