/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-29
 * Time: 6:46:18
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
package com.luntsys.luntbuild.web;

import com.luntsys.luntbuild.db.BuildSchedule;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.utility.LuntBuild;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.ApplicationRuntimeException;

import java.util.List;

/**
 * This tab shows overral builds information in this system
 *
 * @author alvin shen
 */
public abstract class BuildsTab extends TabPageComponent {
	BuildSchedule currentBuildSchedule;

	public String getTabName() {
		return "builds";
	}

	public void tabSelected() {
		// refresh data on select
		loadData();
	}

	/**
	 * Loads builds information from persistence layer
	 */
	public void loadData(){
		setBuildSchedules(LuntBuild.getDao().loadBuildSchedules());
	}

	public abstract void setBuildSchedules(List buildSchedules);

	public abstract List getBuildSchedules();

	/**
	 * Load the project page to show details of a particular project
 	 * @param cycle
	 */
	public void loadProject(IRequestCycle cycle){
		long projectId = ((Long)cycle.getServiceParameters()[0]).longValue();
		ProjectPage page = (ProjectPage)cycle.getPage("ProjectPage");
		page.loadProject(projectId);
		cycle.activate(page);
	}

	/**
	 * Load the view page to show details of a particular view
	 * @param cycle
	 */
	public void loadView(IRequestCycle cycle){
		long viewId = ((Long)cycle.getServiceParameters()[0]).longValue();
		ViewPage page = (ViewPage)cycle.getPage("ViewPage");
		page.loadView(viewId);
		cycle.activate(page);
	}

	/**
	 * Load the schedule page to show details of a particular schedule
	 * @param cycle
	 */
	public void loadSchedule(IRequestCycle cycle){
		long scheduleId = ((Long)cycle.getServiceParameters()[0]).longValue();
		SchedulePage page = (SchedulePage)cycle.getPage("SchedulePage");
		page.loadSchedule(scheduleId);
		cycle.activate(page);
	}

	/**
	 * Show detail information about build
	 * @param cycle
	 */
	public void showBuild(IRequestCycle cycle){
		long buildId = ((Long)cycle.getServiceParameters()[0]).longValue();
		Build build = LuntBuild.getDao().loadBuild(buildId);
		ViewPage page = (ViewPage)cycle.getPage("ViewPage");
		page.loadView(build.getBuildSchedule().getView().getId());
		cycle.activate(page);
		TabControl tabs = (TabControl)page.getComponent("tabs");
		tabs.setSelectedTabId("schedules");
		ViewSchedulesTab viewSchedulesTab = (ViewSchedulesTab) tabs.getSelectedTab();
		viewSchedulesTab.setCurrentAction("showBuild");
		ShowBuild showBuild = (ShowBuild) viewSchedulesTab.getComponent("showBuildComp");
		showBuild.setBuild(build);
		showBuild.setRelativePath("");
		showBuild.listFiles();
	}

	/**
	 * List all builds for a particular view and schedule
	 * @param cycle
	 */
	public void listBuilds(IRequestCycle cycle){
		long buildScheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		BuildSchedule buildSchedule = LuntBuild.getDao().loadBuildSchedule(buildScheduleId);
		ViewPage page = (ViewPage)cycle.getPage("ViewPage");
		page.loadView(buildSchedule.getView().getId());
		cycle.activate(page);
		TabControl tabs = (TabControl)page.getComponent("tabs");
		tabs.setSelectedTabId("schedules");
		ViewSchedulesTab viewSchedulesTab = (ViewSchedulesTab) tabs.getSelectedTab();
		viewSchedulesTab.setCurrentAction("listBuildScheduleBuilds");
		ListBuildScheduleBuilds buildList = (ListBuildScheduleBuilds) viewSchedulesTab.getComponent("listBuildScheduleBuildsComp");
		buildList.setBuildSchedule(buildSchedule);
		buildList.loadBuilds(cycle);
	}

	public void setCurrentBuildSchedule(BuildSchedule currentBuildSchedule){
		this.currentBuildSchedule = currentBuildSchedule;
		setLastBuild(LuntBuild.getDao().getLastBuild(currentBuildSchedule));
	}

	public BuildSchedule getCurrentBuildSchedule(){
		return currentBuildSchedule;
	}

	public abstract void setLastBuild(Build lastBuild);

	public abstract Build getLastBuild();

	public String getLastBuildStatusColor(){
		Build lastBuild = getLastBuild();
		if (lastBuild != null){
			if (lastBuild.getStatus() == Build.SUCCESS)
				return "#00FF00";
			else if (lastBuild.getStatus() == Build.FAILED)
				return "#FF0000";
			else
				return "#FFFF00";
		} else
			return "#FFFFFF";
	}
	public int getRefreshInterval() {
		return LuntBuild.PAGE_REFRESH_INTERVAL;
	}
}
