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

import com.luntsys.luntbuild.BuildGenerator;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.SearchCriteria;
import com.luntsys.luntbuild.facades.lb12.ProjectFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import org.quartz.SimpleTrigger;

import java.util.*;

/**
 * Implementation of interface {@link com.luntsys.luntbuild.facades.ILuntbuild}
 * @author robin shine
 */
public class LuntbuildService implements com.luntsys.luntbuild.facades.ILuntbuild {
	public List getAllProjects() {
		List facades = new ArrayList();
		Iterator it = Luntbuild.getDao().loadProjects().iterator();
		while (it.hasNext()) {
			Project project = (Project) it.next();
			facades.add(project.getFacade());
		}
		return facades;
	}

	public List getAllSchedules() {
		List facades = new ArrayList();
		Iterator it = Luntbuild.getDao().loadSchedules().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			facades.add(schedule.getFacade());
		}
		return facades;
	}

	public List getAllSchedulesOfProject(String projectName) {
		List facades = new ArrayList();
		Iterator it = Luntbuild.getDao().loadProject(projectName).getSchedules().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			facades.add(schedule.getFacade());
		}
		return facades;
	}

	public ProjectFacade getProjectByName(String projectName) {
		return Luntbuild.getDao().loadProject(projectName).getFacade();
	}

	public com.luntsys.luntbuild.facades.lb12.ScheduleFacade getScheduleByName(String projectName, String schedulelName) {
		return Luntbuild.getDao().loadSchedule(projectName, schedulelName).getFacade();
	}

	public void triggerBuild(String projectName, String scheduleName, com.luntsys.luntbuild.facades.BuildParams buildParams) {
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

	public void saveProject(com.luntsys.luntbuild.facades.lb12.ProjectFacade projectFacade) {
		Project project = Luntbuild.getDao().loadProject(projectFacade.getId());
		project.setFacade(projectFacade);
		project.validate();
		Luntbuild.getDao().saveProject(project);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public void saveSchedule(com.luntsys.luntbuild.facades.lb12.ScheduleFacade scheduleFacade) {
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());
		schedule.setFacade(scheduleFacade);
		schedule.validate();
		Luntbuild.getDao().saveSchedule(schedule);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public String getSystemProperty(String propertyName) {
		return (String) Luntbuild.getProperties().get(propertyName);
	}

	public void setSystemProperty(String propertyName, String propertyValue) {
		if (propertyValue != null) {
			Luntbuild.getProperties().put(propertyName, propertyValue.trim());
		} else {
			Luntbuild.getProperties().remove(propertyName);
		}
		Luntbuild.getDao().saveProperties(Luntbuild.getProperties());
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public List searchBuilds(SearchCriteria condition, int start,  int count) {
		List buildFacades = new ArrayList();
		Iterator it = Luntbuild.getDao().searchBuilds(condition, start, count).listIterator();
		while (it.hasNext()) {
			Build build = (Build) it.next();
			buildFacades.add(build.getFacade());
		}
		return buildFacades;
	}

	public com.luntsys.luntbuild.facades.lb12.BuildFacade getLastBuild(com.luntsys.luntbuild.facades.lb12.ScheduleFacade scheduleFacade) {
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());
		Build build = Luntbuild.getDao().loadLastBuild(schedule);
		if (build != null) {
			return build.getFacade();
		} else
			return null;
	}

	public com.luntsys.luntbuild.facades.lb12.BuildFacade getLastSuccessBuild(com.luntsys.luntbuild.facades.lb12.ScheduleFacade scheduleFacade) {
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());
		Build build = Luntbuild.getDao().loadLastSuccessBuild(schedule);
		if (build != null) {
			return build.getFacade();
		} else
			return null;
	}
}
