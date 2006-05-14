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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.quartz.SimpleTrigger;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.BuildSchedule;
import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.View;
import com.luntsys.luntbuild.remoting.BuildFacade;
import com.luntsys.luntbuild.remoting.BuildScheduleFacade;
import com.luntsys.luntbuild.remoting.ILuntbuild;
import com.luntsys.luntbuild.remoting.ProjectFacade;
import com.luntsys.luntbuild.remoting.ScheduleFacade;
import com.luntsys.luntbuild.remoting.SearchCriteria;
import com.luntsys.luntbuild.remoting.ViewFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.ManualBuild;

/**
 * Implementation of interface {@link com.luntsys.luntbuild.remoting.ILuntbuild}
 * @author robin shine
 */
public class LuntbuildService implements ILuntbuild {
	public ProjectFacade getProjectByName(String projectName) {
		Iterator it = Luntbuild.getDao().loadProjects().iterator();
		while (it.hasNext()) {
			Project project = (Project) it.next();
			if (project.getName().equals(projectName)) {
				return project.getFacade();
			}
		}
		return null;
	}

	public ViewFacade getViewByName(ProjectFacade projectFacade, String viewName) {
		Project project = Luntbuild.getDao().loadProject(projectFacade.getId());
		Iterator it = project.getViews().iterator();
		while (it.hasNext()) {
			View view = (View) it.next();
			if (view.getName().equals(viewName))
				return view.getFacade();
		}
		return null;
	}

	public BuildScheduleFacade getBuildScheduleByName(ViewFacade viewFacade, String scheduleName) {
		View view  = Luntbuild.getDao().loadView(viewFacade.getId());
		Iterator it = view.getBuildSchedules().keySet().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			if (schedule.getName().equals(scheduleName))
				return ((BuildSchedule) view.getBuildSchedules().get(schedule)).getFacade();
		}
		return null;
	}

	public ScheduleFacade getScheduleByName(String scheduleName) {
		Iterator it = Luntbuild.getDao().loadSchedules().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			if (schedule.getName().equals(scheduleName))
				return schedule.getFacade();
		}
		return null;
	}

	/**
	 * @param buildScheduleFacade
	 * @param buildAsVersion
	 * @param buildType
	 * @param postbuildStrategy
	 * @param labelStrategy
	 * @param notifyStrategy
	 */
	public void triggerBuild(BuildScheduleFacade buildScheduleFacade, String buildAsVersion, String buildType,
							 String postbuildStrategy, String labelStrategy, String notifyStrategy) {
		BuildSchedule buildSchedule = Luntbuild.getDao().loadBuildSchedule(buildScheduleFacade.getId());

		SimpleTrigger trigger = new SimpleTrigger();
		trigger.setGroup(ManualBuild.MANUALBUILD_GROUP);
		if (buildAsVersion == null)
			buildAsVersion = "";
		else
			View.validateBuildVersion(buildAsVersion);

		if (buildType == null) {
			if (buildSchedule.isCleanBuild())
				buildType = BuildScheduleFacade.BUILD_TYPE_CLEAN;
			else
				buildType = BuildScheduleFacade.BUILD_TYPE_INCREMENT;
		} else
			BuildSchedule.validateBuildType(buildType);

		if (postbuildStrategy == null)
			postbuildStrategy = buildSchedule.getPostbuildStrategy();
		else
			BuildSchedule.validatePostbuildStrategy(postbuildStrategy);

		if (labelStrategy == null)
			labelStrategy = buildSchedule.getLabelStrategy();
		else
			BuildSchedule.validateLabelStrategy(labelStrategy);

		if (notifyStrategy == null)
			notifyStrategy = buildSchedule.getNotifyStrategy();
		else
			BuildSchedule.validateNotifyStrategy(notifyStrategy);

		trigger.setName(String.valueOf(buildSchedule.getId()) + Luntbuild.TRIGGER_NAME_SEPERATOR +
				buildType + Luntbuild.TRIGGER_NAME_SEPERATOR + buildAsVersion +
				Luntbuild.TRIGGER_NAME_SEPERATOR + labelStrategy +
				Luntbuild.TRIGGER_NAME_SEPERATOR + notifyStrategy +
				Luntbuild.TRIGGER_NAME_SEPERATOR + postbuildStrategy +
				Luntbuild.TRIGGER_NAME_SEPERATOR + String.valueOf(System.currentTimeMillis()));
		trigger.setRepeatCount(0);
		trigger.setRepeatInterval(0);
		trigger.setStartTime(new Date(System.currentTimeMillis()));
		Luntbuild.getSchedService().scheduleBuild(buildSchedule.getView(), trigger);
	}

	public void saveProject(ProjectFacade projectFacade) {
		Project project = Luntbuild.getDao().loadProject(projectFacade.getId());
		project.setFacade(projectFacade);
		Luntbuild.getDao().saveProject(project);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public void saveView(ViewFacade viewFacade) {
    	View view = Luntbuild.getDao().loadView(viewFacade.getId());
		view.setFacade(viewFacade);
		Luntbuild.getDao().saveView(view);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public void saveSchedule(ScheduleFacade scheduleFacade) {
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());
		schedule.setFacade(scheduleFacade);
		Luntbuild.getDao().saveSchedule(schedule);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public void saveBuildSchedule(BuildScheduleFacade buildScheduleFacade) {
		BuildSchedule buildSchedule = Luntbuild.getDao().loadBuildSchedule(buildScheduleFacade.getId());
		buildSchedule.setFacade(buildScheduleFacade);
		Luntbuild.getDao().saveBuildSchedule(buildSchedule);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public String getSystemProperty(String propertyName) {
		Map properties = Luntbuild.getDao().loadProperties();
		return (String) properties.get(propertyName);
	}

	public void setSystemProperty(String propertyName, String propertyValue) {
		Map properties = Luntbuild.getDao().loadProperties();
		if (propertyValue != null)
			properties.put(propertyName, propertyValue.trim());
		Luntbuild.getDao().saveProperties(properties);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public List searchBuilds(SearchCriteria condition, int start,  int count) {
		List buildFacades = new ArrayList();
		Map properties = Luntbuild.getDao().loadProperties();
		Iterator it = Luntbuild.getDao().searchBuilds(condition, start, count).listIterator();
		while (it.hasNext()) {
			Build build = (Build) it.next();
			buildFacades.add(build.getFacade(properties));
		}
		return buildFacades;
	}

	public BuildFacade getLastBuild(BuildScheduleFacade buildScheduleFacade) {
		BuildSchedule buildSchedule = Luntbuild.getDao().loadBuildSchedule(buildScheduleFacade.getId());
		Build build = Luntbuild.getDao().getLastBuild(buildSchedule);
		if (build != null) {
			Map properties = Luntbuild.getDao().loadProperties();
			return build.getFacade(properties);
		} else
			return null;
	}

	public BuildFacade getLastSuccessBuild(BuildScheduleFacade buildScheduleFacade) {
		BuildSchedule buildSchedule = Luntbuild.getDao().loadBuildSchedule(buildScheduleFacade.getId());
		Build build = Luntbuild.getDao().getLastSuccessBuild(buildSchedule);
		if (build != null) {
			Map properties = Luntbuild.getDao().loadProperties();
			return build.getFacade(properties);
		} else
			return null;
	}
}
