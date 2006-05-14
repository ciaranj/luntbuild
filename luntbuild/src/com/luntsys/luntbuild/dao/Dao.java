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

import java.util.List;
import java.util.Map;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.BuildSchedule;
import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.db.VcsLogin;
import com.luntsys.luntbuild.db.View;
import com.luntsys.luntbuild.remoting.SearchCriteria;

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

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void saveProject(Project project);

	/**
	 * Load all projects from persistence. Collections of the project may not be loaded.
	 * @return a list of project objects order by name
	 * @throws org.springframework.dao.DataAccessException
 	 */
	List loadProjects();

	/**
	 * Loads project information based on the project id. Collections of the project should be loaded..
	 * @param projectId the identifier of the project to load
	 * @return Project
	 * @throws org.springframework.dao.DataAccessException
	 */
	Project loadProject(long projectId);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteProject(long projectId);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void saveUser(User user);

	/**
	 * @throws org.springframework.dao.DataAccessException
 	 */
	List loadUsers();

    /**
     * 
     * @return list of all physical users
     */
    List loadUsersWithoutVirtual();
    
	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	User loadUser(long userId);

    /**
     *  for authentication purpose 
     *  we need to search for loginName
     * @param userLoginName
     * @return
     */
    User loadUser(String userLoginName);
    
	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteUser(long userId);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void saveView(View view);

	/**
	 * Loads view information based on the view id. Parent objects and Collections of the view should be loaded.
	 * @param viewId
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	View loadView(long viewId);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteView(long viewId);

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
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteSchedule(long scheduleId);

	/**
	 * Load all build schedules in the system, collection of each build schedule may not be initialized. But parent objects should
	 * be loaded
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	List loadBuildSchedules();

	/**
	 * Load all views in the system.
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	List loadViews();

	/**
	 * Load specified build schedule, modules of the view of each build schedule should also be initialized. Also parent objects should be
	 * loaded, But the builds collection may not be initialized
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	BuildSchedule loadBuildSchedule(long buildScheduleId);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void saveBuildSchedule(BuildSchedule buildSchedule);

	/**
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteBuildSchedule(long buildScheduleId);

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
	void deleteVcsLogin(long vcsLoginId);

	/**
	 * Save a execution result of a build schedule
	 * @param build execution result of a particular triggered execution of a particular build schedule
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
	 * Delete the build by specifying the buildId
	 * @param buildId
	 */
	void deleteBuild(long buildId);

	/**
	 * get the last build generated by the specified build schedule
	 * @param buildSchedule
	 * @return the last build, this value maybe null
	 * @throws org.springframework.dao.DataAccessException
	 */
	Build getLastBuild(BuildSchedule buildSchedule);

	/**
	 * get the last successful build generated by the specified build schedule
	 * @param buildSchedule
	 * @return the last success build, value maybe null
	 * @throws org.springframework.dao.DataAccessException
	 */
	Build getLastSuccessBuild(BuildSchedule buildSchedule);

	/**
	 * Determines how many builds are currently belongs to specified build schedule
	 * @param buildSchedule
	 * @return
	 * @throws org.springframework.dao.DataAccessException
	 */
	int getTotalBuilds(BuildSchedule buildSchedule);

	/**
	 * Determines how many builds can be found using specified search criteria
	 * @param searchCriteria
	 * @return
	 */
	int getTotalBuilds(SearchCriteria searchCriteria);

	/**
	 * Searches builds by specifying search criteria
	 * @param searchCriteria
	 * @param start the start index (0 based) for the retrieved builds
	 * @param count number of builds to retrieve. If this value equals 0, no limits will be set
	 * on the number of retrieved builds.
	 * @return
	 */
	List searchBuilds(SearchCriteria searchCriteria,  int start, int count);

	/**
	 * This method checks if there is any un-finished builds in the system,
	 * and mark them as failed. This method is called at  start up of the
	 * system in order to clear status of un-finished build.
	 * @throws org.springframework.dao.DataAccessException
	 */
	void failUnfinishedBuilds();

	/**
	 * This method checks if there is any running build schedule in the system,
	 * and mark them as failed. This method is called at startup of the system in order
	 * to clear status of un-finished schedule executions
	 */
	void failUnfinishedSchedules();

	/**
	 * Delete all builds matching specified search criteria
	 * @param searchCriteria
	 * @throws org.springframework.dao.DataAccessException
	 */
	void deleteBuilds(SearchCriteria searchCriteria);

	/**
	 * Loads current running build for specified build schedule
	 * @param buildSchedule
	 * @return maybe null if there is no running build inside the specified build schedule
	 * @throws org.springframework.dao.DataAccessException
	 */
	Build getRunningBuild(BuildSchedule buildSchedule);

	/**
	 * Move builds matching specified criteria to specified build schedule
	 * @param searchCriteria
	 * @param buildScheduleId
	 * @throws org.springframework.dao.DataAccessException
	 */
	void moveBuilds(com.luntsys.luntbuild.remoting.SearchCriteria searchCriteria, long buildScheduleId);

	/**
	 * Create the virtual user checkin
	 * @see com.luntsys.luntbuild.db.User
	 * @throws org.springframework.dao.DataAccessException
	 */
	void createUserCheckin();
    
    /**
     *  
     * @return all predefined security roles from db 
     * @throws org.springframework.dao.DataAccessException
     */
    List loadRoles();
    
    void deleteRoleMapping(long roleId);
}
