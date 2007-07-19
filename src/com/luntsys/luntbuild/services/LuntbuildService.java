/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-19
 * Time: 17:39:18
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

package com.luntsys.luntbuild.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.luntsys.luntbuild.BuildGenerator;
import com.luntsys.luntbuild.security.SecurityHelper;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.NotifyMapping;
import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Role;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.db.VcsLogin;
import com.luntsys.luntbuild.facades.BuildParams;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.ILuntbuild;
import com.luntsys.luntbuild.facades.SearchCriteria;
import com.luntsys.luntbuild.facades.lb12.BuildFacade;
import com.luntsys.luntbuild.facades.lb12.ProjectFacade;
import com.luntsys.luntbuild.facades.lb12.ScheduleFacade;
import com.luntsys.luntbuild.facades.lb12.UserFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SimpleTrigger;


/**
 * Luntbuild service implementation.
 * 
 * @author robin shine
 */
public class LuntbuildService implements ILuntbuild {

    private static Log logger = LogFactory.getLog(LuntbuildService.class);

    /**
     * @inheritDoc
     */
    public List getAllProjects() {
        List facades = new ArrayList();
        Iterator it = Luntbuild.getDao().loadProjects().iterator();
        while (it.hasNext()) {
            Project project = (Project) it.next();
            facades.add(project.getFacade());
        }
        return facades;
    }

    /**
     * @inheritDoc
     */
    public List getAllSchedules() {
        List facades = new ArrayList();
        Iterator it = Luntbuild.getDao().loadSchedules().iterator();
        while (it.hasNext()) {
            Schedule schedule = (Schedule) it.next();
            facades.add(schedule.getFacade());
        }
        return facades;
    }

    /**
     * @inheritDoc
     */
    public List getAllSchedulesOfProject(String projectName) {
        List facades = new ArrayList();
        Iterator it = Luntbuild.getDao().loadProject(projectName).getSchedules().iterator();
        while (it.hasNext()) {
            Schedule schedule = (Schedule) it.next();
            facades.add(schedule.getFacade());
        }
        return facades;
    }

    /**
     * @inheritDoc
     */
    public ProjectFacade getProjectByName(String projectName) {
        Project project = null;
        try {
            project = Luntbuild.getDao().loadProject(projectName);
        } catch (Exception e) {
            return null;
        }
        if (project == null) return null;

        ProjectFacade projectFacade = project.getFacade();

        projectFacade.setNotifyUsers(getNotifyMappings(project));

        String[] userArr = getNamesFromUsers(project.getMappedRolesUserList(Role.LUNTBUILD_PRJ_ADMIN));
        projectFacade.setProjectAdmins(userArr);
        userArr = getNamesFromUsers(project.getMappedRolesUserList(Role.LUNTBUILD_PRJ_BUILDER));
        projectFacade.setProjectBuilders(userArr);
        userArr = getNamesFromUsers(project.getMappedRolesUserList(Role.LUNTBUILD_PRJ_VIEWER));
        projectFacade.setProjectViewers(userArr);

        return projectFacade;
    }

    /**
     * @inheritDoc
     */
    public ScheduleFacade getScheduleByName(String projectName, String scheduleName) {
        Schedule schedule = null;
        try {
            schedule = Luntbuild.getDao().loadSchedule(projectName, scheduleName);
        } catch (Exception e) {
            return null;
        }
        if (schedule == null) return null;
        return schedule.getFacade();
    }

    /**
     * @inheritDoc
     */
    public void triggerBuild(String projectName, String scheduleName, BuildParams buildParams) {
        Schedule schedule = Luntbuild.getDao().loadSchedule(projectName, scheduleName);
        buildParams.setScheduleId(schedule.getId());

        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setGroup(BuildGenerator.MANUALBUILD_GROUP);

        String buildNecessaryCondition = buildParams.getBuildNecessaryCondition();
        if (Luntbuild.isEmpty(buildNecessaryCondition))
            buildNecessaryCondition = schedule.getBuildNecessaryCondition();
        else {
            Schedule.validateBuildNecessaryCondition(buildNecessaryCondition);
            buildNecessaryCondition = buildNecessaryCondition.trim();
        }
        buildParams.setBuildNecessaryCondition(buildNecessaryCondition);

        String buildAsVersion = buildParams.getBuildVersion();
        if (buildAsVersion == null)
            buildAsVersion = "";
        else
            buildAsVersion = buildAsVersion.trim();
        if (!buildAsVersion.equals("")) {
            schedule.validateBuildVersion(buildAsVersion);
        }
        buildParams.setBuildVersion(buildAsVersion);

        int buildType = buildParams.getBuildType();
        if (buildType == -1) {
            buildType = schedule.getBuildType();
        } else
            Schedule.validateBuildType(buildType);
        buildParams.setBuildType(buildType);

        int postbuildStrategy = buildParams.getPostbuildStrategy();
        if (postbuildStrategy == -1)
            postbuildStrategy = schedule.getPostbuildStrategy();
        else
            Schedule.validatePostbuildStrategy(postbuildStrategy);
        buildParams.setPostbuildStrategy(postbuildStrategy);

        int labelStrategy = buildParams.getLabelStrategy();
        if (labelStrategy == -1)
            labelStrategy = schedule.getLabelStrategy();
        else
            Schedule.validateLabelStrategy(labelStrategy);
        buildParams.setLabelStrategy(labelStrategy);

        int notifyStrategy = buildParams.getNotifyStrategy();
        if (notifyStrategy == -1)
            notifyStrategy = schedule.getNotifyStrategy();
        else
            Schedule.validateNotifyStrategy(notifyStrategy);
        buildParams.setNotifyStrategy(notifyStrategy);

        int triggerDependencyStrategy = buildParams.getTriggerDependencyStrategy();
        if (triggerDependencyStrategy == -1)
            triggerDependencyStrategy = schedule.getTriggerDependencyStrategy();
        else
            Schedule.validateTriggerDependencyStrategy(triggerDependencyStrategy);
        buildParams.setTriggerDependencyStrategy(triggerDependencyStrategy);

        trigger.setName(Schedule.constructTriggerName(buildParams));
        trigger.setRepeatCount(0);
        trigger.setRepeatInterval(0);
        trigger.setStartTime(new Date(System.currentTimeMillis()));
        Luntbuild.getSchedService().scheduleBuild(schedule, trigger);
    }

    /**
     * @inheritDoc
     */
    public void saveProject(ProjectFacade projectFacade) {
        Project project = Luntbuild.getDao().loadProject(projectFacade.getId());
        project.setFacade(projectFacade);
        project.validate();
        Luntbuild.getDao().saveProject(project);
        Luntbuild.getSchedService().rescheduleBuilds();
    }

    /**
     * @inheritDoc
     */
    public void saveSchedule(ScheduleFacade scheduleFacade) {
        Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());
        schedule.setFacade(scheduleFacade);
        schedule.validate();
        Luntbuild.getDao().saveSchedule(schedule);
        Luntbuild.getSchedService().rescheduleBuilds();
    }

    /**
     * @inheritDoc
     */
    public String getSystemProperty(String propertyName) {
        return (String) Luntbuild.getProperties().get(propertyName);
    }

    /**
     * @inheritDoc
     */
    public void setSystemProperty(String propertyName, String propertyValue) {
        if (propertyValue != null) {
            Luntbuild.getProperties().put(propertyName, propertyValue.trim());
        } else {
            Luntbuild.getProperties().remove(propertyName);
        }
        Luntbuild.getDao().saveProperties(Luntbuild.getProperties());
        Luntbuild.getSchedService().rescheduleBuilds();
    }

    /**
     * @inheritDoc
     */
    public List searchBuilds(SearchCriteria condition, int start,  int count) {
        List buildFacades = new ArrayList();
        Iterator it = Luntbuild.getDao().searchBuilds(condition, start, count).listIterator();
        while (it.hasNext()) {
            Build build = (Build) it.next();
            buildFacades.add(build.getFacade());
        }
        return buildFacades;
    }

    /**
     * @inheritDoc
     */
    public void deleteBuilds(SearchCriteria searchCriteria) {
        Luntbuild.getDao().deleteBuilds(searchCriteria);
    }

    /**
     * @inheritDoc
     */
    public void deleteBuild(BuildFacade buildFacade) {
        Build build = new Build();
        build.setFacade(buildFacade);
        build.setSchedule(Luntbuild.getDao().loadSchedule(buildFacade.getScheduleId()));
        build.setId(buildFacade.getId());
        Luntbuild.getDao().deleteBuild(build);
    }

    /**
     * @inheritDoc
     */
    public void moveBuilds(SearchCriteria searchCriteria, String projectName, String scheduleName) {
        Schedule schedule = null;
        try {
            schedule = Luntbuild.getDao().loadSchedule(projectName, scheduleName);
        } catch (Exception e) {
            return;
        }
        if (schedule == null) return;
        Luntbuild.getDao().moveBuilds(searchCriteria, schedule.getId());
    }

    /**
     * @inheritDoc
     */
    public void moveBuild(BuildFacade buildFacade, String projectName, String scheduleName) {
        Schedule schedule = null;
        try {
            schedule = Luntbuild.getDao().loadSchedule(projectName, scheduleName);
        } catch (Exception e) {
            return;
        }
        if (schedule == null) return;
        Luntbuild.getDao().moveBuild(buildFacade.getId(), schedule.getId());
    }

    /**
     * @inheritDoc
     */
    public BuildFacade getLastBuild(ScheduleFacade scheduleFacade) {
        Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());
        Build build = Luntbuild.getDao().loadLastBuild(schedule);
        if (build != null) {
            return build.getFacade();
        } else
            return null;
    }

    /**
     * @inheritDoc
     */
    public BuildFacade getLastSuccessBuild(ScheduleFacade scheduleFacade) {
        Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());
        Build build = Luntbuild.getDao().loadLastSuccessBuild(schedule);
        if (build != null) {
            return build.getFacade();
        } else
            return null;
    }

    /**
     * @inheritDoc
     */
    public BuildFacade getLastBuild(String projectName, String scheduleName) {
        return getLastBuild(getScheduleByName(projectName, scheduleName));
    }

    /**
     * @inheritDoc
     */
    public BuildFacade getLastSuccessBuild(String projectName, String scheduleName) {
        return getLastSuccessBuild(getScheduleByName(projectName, scheduleName));
    }

    /**
     * @inheritDoc
     */
    public void createProject(ProjectFacade projectFacade) {
        Project project = new Project();
        if (!SecurityHelper.isSiteAdmin()) {
            User loginUser = Luntbuild.getDao().loadUser(SecurityHelper.getPrincipalAsString());
            List adminUsers = project.getMappedRolesUserList(Role.LUNTBUILD_PRJ_ADMIN);
            if (!adminUsers.contains(loginUser))
                adminUsers.add(loginUser);
            project.putMappedRolesUserList(adminUsers, Role.LUNTBUILD_PRJ_ADMIN);
        }
        project.setId(0L);
        project.setName(projectFacade.getName());
        project.setFacade(projectFacade);

        setNotifyMappings(project, projectFacade.getNotifyUsers());

        List users = getUsersFromNames(projectFacade.getProjectAdmins());
        project.putMappedRolesUserList(users, Role.LUNTBUILD_PRJ_ADMIN);
        users = getUsersFromNames(projectFacade.getProjectBuilders());
        project.putMappedRolesUserList(users, Role.LUNTBUILD_PRJ_BUILDER);
        users = getUsersFromNames(projectFacade.getProjectViewers());
        project.putMappedRolesUserList(users, Role.LUNTBUILD_PRJ_VIEWER);

        try {
            project.validateBasic();
            if (Luntbuild.getDao().isProjectNameUsed(project)) {
                logger.warn("Project with this name already exists!");
                return;
            }
            Luntbuild.getDao().saveProject(project);
            SecurityHelper.refreshUserCache();
            Iterator it = project.getVcsLogins().iterator();
            while (it.hasNext()) {
                VcsLogin vcsLogin = (VcsLogin) it.next();
                vcsLogin.setId(0);
                Luntbuild.getDao().saveVcsLogin(vcsLogin);
            }

        } catch (ValidationException e) {
            logger.error("Unable to validate project " + project.getName());
            return;
        }

        // Set schedules
        List schedules = projectFacade.getScheduleList();
        if (schedules != null) {
            for (Iterator iter = schedules.iterator(); iter.hasNext();) {
                ScheduleFacade sch = (ScheduleFacade) iter.next();
                sch.setProjectId(project.getId());
                createSchedule(sch);
            }
        }

        Luntbuild.getSchedService().rescheduleBuilds();
    }

    private String[] getNotifyMappings(Project project) {
        ArrayList users = new ArrayList();
        for (Iterator iter = project.getNotifyMappings().iterator(); iter.hasNext();) {
            NotifyMapping notifyMapping = (NotifyMapping) iter.next();
            users.add(notifyMapping.getUser().getName());
        }
        return (String[])users.toArray(new String[users.size()]);
    }

    private void setNotifyMappings(Project project, String[] names) {
        if (names == null) return;
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            User user = null;
            try {
                user = Luntbuild.getDao().loadUser(name);
            } catch (Exception e) {
                logger.warn("Unable to find user " + name);
                continue;
            }
            NotifyMapping notifyMapping = new NotifyMapping();
            notifyMapping.setProject(project);
            notifyMapping.setUser(user);
            project.getNotifyMappings().add(notifyMapping);
        }
    }

    private List getUsersFromNames(String[] names) {
        ArrayList users = new ArrayList();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            User user = null;
            try {
                user = Luntbuild.getDao().loadUser(name);
                users.add(user);
            } catch (Exception e) {
                logger.warn("Unable to find user " + name);
            }
        }
        return users;
    }

    private String[] getNamesFromUsers(List users) {
        ArrayList list = new ArrayList();
        for (Iterator iter = users.iterator(); iter.hasNext();) {
            User user = (User) iter.next();
            list.add(user.getName());
        }
        return (String[])list.toArray(new String[list.size()]);
    }

    /**
     * @inheritDoc
     */
    public void deleteProject(String projectName) {
        Luntbuild.getDao().deleteProject(Luntbuild.getDao().loadProject(projectName));
        Luntbuild.getSchedService().rescheduleBuilds();
    }

    /**
     * @inheritDoc
     */
    public void createSchedule(ScheduleFacade scheduleFacade) {
        try {
            Schedule schedule = new Schedule();
            schedule.setFacade(scheduleFacade);
            schedule.setId(0L);
            schedule.setName(scheduleFacade.getName());
            Project project = Luntbuild.getDao().loadProject(scheduleFacade.getProjectId());
            schedule.setProject(project);
            schedule.setStatus(0);
            schedule.setStatusDate(Calendar.getInstance().getTime());
            schedule.validate();
            if (Luntbuild.getDao().isScheduleNameUsed(schedule)) {
                logger.warn("Schedule with this name already exists!");
                return;
            }
            Luntbuild.getDao().saveSchedule(schedule);
            Luntbuild.getSchedService().rescheduleBuilds();
        } catch(ValidationException e) {
            logger.warn("Unable to validate schedule " + scheduleFacade.getName());
            return;
        }
    }

    /**
     * @inheritDoc
     */
    public void deleteSchedule(ScheduleFacade scheduleFacade) {
        Luntbuild.getDao().deleteSchedule(Luntbuild.getDao().loadSchedule(scheduleFacade.getId()));
        Luntbuild.getSchedService().rescheduleBuilds();
    }

    /**
     * @inheritDoc
     */
    public void deleteAllSchedulesOfProject(String projectName) {
        Iterator it = Luntbuild.getDao().loadProject(projectName).getSchedules().iterator();
        while (it.hasNext()) {
            Schedule schedule = (Schedule) it.next();
            Luntbuild.getDao().deleteSchedule(
                    Luntbuild.getDao().loadSchedule(schedule.getFacade().getId()));
        }
        Luntbuild.getSchedService().rescheduleBuilds();
    }

    /**
     * @inheritDoc
     */
    public String[] getBuildLog(BuildFacade buildFacade) {
        Schedule schedule = Luntbuild.getDao().loadSchedule(buildFacade.getScheduleId());
        String version = buildFacade.getVersion();
        String publishDir = getSystemProperty(Constants.PUBLISH_DIR);
        if (Luntbuild.isEmpty(publishDir))
            publishDir = Luntbuild.installDir + "/publish";
        File logFile = new File(publishDir + "/" + schedule.getProject().getName() +
                "/" + schedule.getName() + "/" + version + "/" + BuildGenerator.BUILD_LOG);

        return getFileAsStringArray(logFile);
    }

    /**
     * @inheritDoc
     */
    public String[] getBuildLogHtml(BuildFacade buildFacade) {
        Schedule schedule = Luntbuild.getDao().loadSchedule(buildFacade.getScheduleId());
        String version = buildFacade.getVersion();
        String publishDir = getSystemProperty(Constants.PUBLISH_DIR);
        if (Luntbuild.isEmpty(publishDir))
            publishDir = Luntbuild.installDir + "/publish";
        File logFile = new File(publishDir + "/" + schedule.getProject().getName() +
                "/" + schedule.getName() + "/" + version + "/" + BuildGenerator.BUILD_HTML_LOG);

        return getFileAsStringArray(logFile);
    }

    /**
     * @inheritDoc
     */
    public String[] getRevisionLog(BuildFacade buildFacade) {
        Schedule schedule = Luntbuild.getDao().loadSchedule(buildFacade.getScheduleId());
        String version = buildFacade.getVersion();
        String publishDir = getSystemProperty(Constants.PUBLISH_DIR);
        if (Luntbuild.isEmpty(publishDir))
            publishDir = Luntbuild.installDir + "/publish";
        File logFile = new File(publishDir + "/" + schedule.getProject().getName() +
                "/" + schedule.getName() + "/" + version + "/" + BuildGenerator.REVISION_LOG);

        return getFileAsStringArray(logFile);
    }

    /**
     * @inheritDoc
     */
    public String[] getRevisionLogHtml(BuildFacade buildFacade) {
        Schedule schedule = Luntbuild.getDao().loadSchedule(buildFacade.getScheduleId());
        String version = buildFacade.getVersion();
        String publishDir = getSystemProperty(Constants.PUBLISH_DIR);
        if (Luntbuild.isEmpty(publishDir))
            publishDir = Luntbuild.installDir + "/publish";
        File logFile = new File(publishDir + "/" + schedule.getProject().getName() +
                "/" + schedule.getName() + "/" + version + "/" + BuildGenerator.REVISION_HTML_LOG);

        return getFileAsStringArray(logFile);
    }

    /**
     * @inheritDoc
     */
    public String[] getSystemLog() {
        String servletUrl = Luntbuild.getServletUrl();
        if (!servletUrl.endsWith("app.do"))
            throw new RuntimeException("Invalid servlet url: " + servletUrl);
        File logFile = new File(servletUrl.substring(0, servletUrl.length() - 6) + "logs/" +
            Luntbuild.log4jFileNameTxt);

        return getFileAsStringArray(logFile);
    }

    /**
     * @inheritDoc
     */
    public String[] getSystemLogHtml() {
        String servletUrl = Luntbuild.getServletUrl();
        if (!servletUrl.endsWith("app.do"))
            throw new RuntimeException("Invalid servlet url: " + servletUrl);
        File logFile = new File(servletUrl.substring(0, servletUrl.length() - 6) + "logs/" +
            Luntbuild.log4jFileName);

        return getFileAsStringArray(logFile);
    }

    private String[] getFileAsStringArray(File logFile) {
        List logArr = new ArrayList();
        FileReader reader = null;
        try {
            reader = new FileReader(logFile);
            BufferedReader fr = new BufferedReader(reader);
            while(fr.ready()) {
                logArr.add(fr.readLine());
            }
        } catch(IOException e) {
            logger.warn("Unable to read log file " + logFile.getAbsolutePath());
            return null;
        } catch(ValidationException e) {
            logger.warn("Unable to get log file " + logFile.getAbsolutePath());
        } finally {
            if (reader != null)
                try { reader.close(); } catch (Exception e) {/* ignore */}
        }

        String[] log = (String[]) logArr.toArray(new String[]{});
        return log;
    }

    /**
     * @inheritDoc
     */
    public boolean canCreateProject(String username) {
        if (!Luntbuild.getDao().isUserExist(username)) {
            return SecurityHelper.isSiteAdmin();
        }
        // try to retrieve user credentials from local db
        User luntUser = Luntbuild.getDao().loadUser(username);
        if (luntUser == null) return false;
        return luntUser.isCanCreateProject();
    }

    /**
     * @inheritDoc
     */
    public List getUsers() {
        List users = Luntbuild.getDao().loadUsers();
        ArrayList facades = new ArrayList();
        for (Iterator iter = users.iterator(); iter.hasNext();) {
            User user = (User) iter.next();
            facades.add(user.getFacade());
        }
        return facades;
    }

    /**
     * @inheritDoc
     */
    public UserFacade getUser(String username) {
        User user = Luntbuild.getDao().loadUser(username);
        return (user == null) ? null : user.getFacade();
    }
}
