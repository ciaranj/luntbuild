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

import java.util.List;

import org.apache.tapestry.IRequestCycle;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.BuildSchedule;
import com.luntsys.luntbuild.remoting.BuildFacade;
import com.luntsys.luntbuild.remoting.BuildScheduleFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;

/**
 * This tab shows overral builds information in this system
 *
 * @author robin shine
 */
public abstract class BuildsTab extends TabPageComponent {
	BuildSchedule currentBuildSchedule;

	public String getTabName() {
		return "builds";
	}

	public void tabSelected() {
		// refresh data on select
		loadData();
		setCurrentAction(null);
	}

	/**
	 * Loads builds information from persistence layer
	 */
	public void loadData(){
		setBuildSchedules(Luntbuild.getDao().loadBuildSchedules());
	}

	public abstract void setBuildSchedules(List buildSchedules);

	public abstract List getBuildSchedules();

	/**
	 * List all builds for a particular build schedule
	 * @param cycle
	 */
	public void listBuilds(IRequestCycle cycle){
		long buildScheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		com.luntsys.luntbuild.remoting.SearchCriteria searchCriteria = new com.luntsys.luntbuild.remoting.SearchCriteria();
		searchCriteria.setBuildScheduleId(buildScheduleId);
		ListBuilds listBuilds = (ListBuilds) getComponent("listBuildsComp");
		listBuilds.setSearchCriteria(searchCriteria);
		listBuilds.initPage(cycle);
		setCurrentAction("listBuilds");
	}

	/**
	 * Trigger a build for a particular build schedule
	 * @param cycle
	 */
	public void buildManually(IRequestCycle cycle) {
		long buildScheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		BuildSchedule buildSchedule = Luntbuild.getDao().loadBuildSchedule(buildScheduleId);
		ViewPage page = (ViewPage)cycle.getPage("ViewPage");
		page.loadView(buildSchedule.getView().getId());
		cycle.activate(page);
		TabControl tabs = (TabControl)page.getComponent("tabs");
		tabs.setSelectedTabId("schedules");
		ViewSchedulesTab viewSchedulesTab = (ViewSchedulesTab) tabs.getSelectedTab();
		viewSchedulesTab.setCurrentAction("manualBuild");
		ManualBuild manualBuild = (ManualBuild) viewSchedulesTab.getComponent("manualBuildComp");
		manualBuild.setBuildSchedule(buildSchedule);
		manualBuild.setLastPosition("builds");
	}

	/**
	 * Show detail information about a build
	 * @param cycle
	 */
	public void showBuild(IRequestCycle cycle){
		long buildId = ((Long)cycle.getServiceParameters()[0]).longValue();
		ShowBuild showBuild = (ShowBuild) getComponent("showBuildComp");
		showBuild.setCurrentAction(null);
		showBuild.setBuildId(buildId);
		showBuild.setRelativePath("/artifacts");
		setCurrentAction("showBuild");
	}

	public void setCurrentBuildSchedule(BuildSchedule currentBuildSchedule){
		this.currentBuildSchedule = currentBuildSchedule;
		setLastBuild(Luntbuild.getDao().getLastBuild(currentBuildSchedule));
	}

	public BuildSchedule getCurrentBuildSchedule(){
		return currentBuildSchedule;
	}

	public abstract void setLastBuild(Build lastBuild);

	public abstract Build getLastBuild();

	public String getLastBuildStatusGif(){
		Build lastBuild = getLastBuild();
		if (lastBuild != null){
			if (lastBuild.getStatus() == BuildFacade.SUCCESS)
				return "images/success.gif";
			else if (lastBuild.getStatus() == BuildFacade.FAILED)
				return "images/failed.gif";
			else
				return "images/running.gif";
		} else
			return "images/null.gif";
	}
	public int getRefreshInterval() {
		if (getCurrentAction() == null || getCurrentAction().equals("listBuilds") ||
				getCurrentAction().equals("showBuild"))
			return Luntbuild.PAGE_REFRESH_INTERVAL;
		else
			return 0;
	}

	public String getCurrentBuildScheduleStatusGif() {
		int status = getCurrentBuildSchedule().getStatus();
		if (status == BuildScheduleFacade.SUCCESS)
			return "images/success.gif";
		else if (status == BuildScheduleFacade.FAILED)
			return "images/failed.gif";
		else if (status == BuildScheduleFacade.RUNNING)
			return "images/running.gif";
		else
			return "images/null.gif";
	}

	public abstract String getCurrentAction();

	public abstract void setCurrentAction(String currentAction);

	public void searchBuild(IRequestCycle cycle) {
		SearchBuild searchBuild = (SearchBuild) getComponent("searchBuildComp");
		searchBuild.setLastAction(getCurrentAction());
		setCurrentAction("searchBuild");
	}

	public void deleteBuilds(IRequestCycle cycle) {
		setCurrentAction("deleteBuilds");
	}

	public void deleteBuild(IRequestCycle cycle) {
		setCurrentAction("deleteBuild");
	}

	public void moveBuilds(IRequestCycle cycle) {
		MoveBuilds moveBuilds = (MoveBuilds) getComponent("moveBuildsComp");
		moveBuilds.setLastAction("listBuilds");
		setCurrentAction("moveBuilds");
	}

	public void moveBuild(IRequestCycle cycle) {
		MoveBuilds moveBuilds = (MoveBuilds) getComponent("moveBuildsComp");
		moveBuilds.setLastAction("showBuild");
		setCurrentAction("moveBuilds");
	}

	/**
	 * Actually delete the builds
	 * @param cycle
	 */
	public void confirmDeleteBuilds(IRequestCycle cycle) {
		ListBuilds listBuilds = (ListBuilds) getComponent("listBuildsComp");
		if (listBuilds.getSearchCriteria() == null)
			return;
		Luntbuild.getDao().deleteBuilds(listBuilds.getSearchCriteria());
		listBuilds.setSearchCriteria(null);
		tabSelected();
	}

	/**
	 * Cancel the deletion of build
	 * @param cycle
	 */
	public void cancelDeleteBuilds(IRequestCycle cycle) {
		setCurrentAction("listBuilds");
	}

	/**
	 * Actually delete the build
	 * @param cycle
	 */
	public void confirmDeleteBuild(IRequestCycle cycle) {
		ShowBuild showBuild = (ShowBuild) getComponent("showBuildComp");
		if (showBuild.getBuildId() == 0)
			return;
		Luntbuild.getDao().deleteBuild(showBuild.getBuildId());
		showBuild.setBuildId(0);
		tabSelected();
	}

	/**
	 * Cancel the deletion of build
	 * @param cycle
	 */
	public void cancelDeleteBuild(IRequestCycle cycle) {
		setCurrentAction("showBuild");
	}

	public abstract int getCurrentBuildScheduleIndex();

	/**
	 * Get css class for current row
	 * @return
	 */
	public String getRowClass() {
		if (getCurrentBuildScheduleIndex() % 2 == 0)
			return "dataTableRow1";
		else
			return "dataTableRow2";
	}

	public String getLastBuildEndDate() {
		Build lastBuild = getLastBuild();
		if (lastBuild == null || lastBuild.getEndDate() == null)
			return "";
		else
			return Luntbuild.DATE_DISPLAY_FORMAT.format(lastBuild.getEndDate());
	}

	public void gotoBuildSchedule(IRequestCycle cycle) {
		long buildScheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		BuildSchedule buildSchedule = Luntbuild.getDao().loadBuildSchedule(buildScheduleId);
		ViewPage page = (ViewPage)cycle.getPage("ViewPage");
		page.loadView(buildSchedule.getView().getId());
		cycle.activate(page);
		TabControl tabs = (TabControl)page.getComponent("tabs");
		tabs.setSelectedTabId("schedules");
		ViewSchedulesTab viewSchedulesTab = (ViewSchedulesTab) tabs.getSelectedTab();
		viewSchedulesTab.setCurrentAction(null);
	}

	public void gotoProject(IRequestCycle cycle) {
		long projectId = ((Long) cycle.getServiceParameters()[0]).longValue();
		ProjectPage projectPage = (ProjectPage) cycle.getPage("ProjectPage");
		projectPage.loadProject(projectId);
		TabControl tabs = (TabControl) projectPage.getComponent("tabs");
		tabs.setSelectedTabId("basic");
		cycle.activate(projectPage);
	}

	public void gotoView(IRequestCycle cycle) {
		long viewId = ((Long) cycle.getServiceParameters()[0]).longValue();
		ViewPage viewPage = (ViewPage) cycle.getPage("ViewPage");
		viewPage.loadView(viewId);
		TabControl tabs = (TabControl) viewPage.getComponent("tabs");
		tabs.setSelectedTabId("basic");
		cycle.activate(viewPage);
	}

	public void gotoSchedule(IRequestCycle cycle) {
		long scheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		SchedulePage schedulePage = (SchedulePage) cycle.getPage("SchedulePage");
		schedulePage.loadSchedule(scheduleId);
		TabControl tabs = (TabControl) schedulePage.getComponent("tabs");
		tabs.setSelectedTabId("basic");
		cycle.activate(schedulePage);
	}
}
