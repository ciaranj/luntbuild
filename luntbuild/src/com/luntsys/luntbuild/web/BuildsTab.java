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

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.remoting.Constants;
import com.luntsys.luntbuild.remoting.SearchCriteria;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;
import org.apache.tapestry.IRequestCycle;

import java.util.List;

/**
 * This tab shows overral builds information in this system
 *
 * @author robin shine
 */
public abstract class BuildsTab extends TabPageComponent {
	private Schedule schedule;

	public String getTabName() {
		return "builds";
	}

	public void tabSelected() {
		// refresh data on select
		loadData();
		setAction(null);
	}

	/**
	 * Loads builds information from persistence layer
	 */
	public void loadData(){
		setSchedules(Luntbuild.getDao().loadSchedules());
	}

	public abstract void setSchedules(List schedules);

	public abstract List getSchedules();

	/**
	 * List all builds for a particular schedule
	 * @param cycle
	 */
	public void listBuilds(IRequestCycle cycle){
		long scheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setScheduleId(scheduleId);
		BuildList buildList = (BuildList) getComponent("buildListComponent");
		buildList.setSearchCriteria(searchCriteria);
		buildList.initPage(cycle);
		setAction("listBuilds");
	}

	/**
	 * Trigger a build for a particular schedule
	 * @param cycle
	 */
	public void build(IRequestCycle cycle) {
		long scheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		ProjectPage projectPage = (ProjectPage)cycle.getPage("ProjectPage");
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleId);
		projectPage.setProject(schedule.getProject());
		TabControl tabs = (TabControl)projectPage.getComponent("tabs");
		tabs.setSelectedTabId("schedules");
		SchedulesTab schedulesTab = (SchedulesTab) tabs.getSelectedTab();
		schedulesTab.setAction("buildManually");
		ManualBuildEditor manualBuildEditor = (ManualBuildEditor) schedulesTab.getComponent("manualBuildEditorComponent");
		manualBuildEditor.setSchedule(schedule);
		manualBuildEditor.setWhereFrom(ManualBuildEditor.BUILD_FROM_BUILDSTAB);
		cycle.activate(projectPage);
	}

	/**
	 * View detail information about a build
	 * @param cycle
	 */
	public void viewBuild(IRequestCycle cycle){
		long buildId = ((Long)cycle.getServiceParameters()[0]).longValue();
		BuildViewer buildViewer = (BuildViewer) getComponent("buildViewerComponent");
		buildViewer.setAction(null);
		buildViewer.setBuildId(buildId);
		buildViewer.setRelativePath("/artifacts");
		setAction("viewBuild");
	}

	public void setSchedule(Schedule schedule){
		this.schedule = schedule;
		setLastBuild(Luntbuild.getDao().loadLastBuild(schedule));
	}

	public Schedule getSchedule(){
		return schedule;
	}

	public abstract void setLastBuild(Build lastBuild);

	public abstract Build getLastBuild();

	public String getLastBuildStatusGif(){
		Build lastBuild = getLastBuild();
		if (lastBuild != null){
			if (lastBuild.getStatus() == Constants.BUILD_STATUS_SUCCESS)
				return "images/success.gif";
			else if (lastBuild.getStatus() == Constants.BUILD_STATUS_FAILED)
				return "images/failed.gif";
			else
				return "images/running.gif";
		} else
			return "images/null.gif";
	}
	public int getRefreshInterval() {
		if (getAction() == null || getAction().equals("listBuilds") ||
				getAction().equals("viewBuild"))
			return Luntbuild.PAGE_REFRESH_INTERVAL;
		else
			return 0;
	}

	public String getScheduleStatusGif() {
		int status = getSchedule().getStatus();
		if (status == Constants.SCHEDULE_STATUS_SUCCESS)
			return "images/success.gif";
		else if (status == Constants.SCHEDULE_STATUS_FAILED)
			return "images/failed.gif";
		else if (status == Constants.SCHEDULE_STATUS_RUNNING)
			return "images/running.gif";
		else
			return "images/null.gif";
	}

	public abstract String getAction();

	public abstract void setAction(String action);

	public void searchBuild(IRequestCycle cycle) {
		SearchBuilds searchBuilds = (SearchBuilds) getComponent("searchBuildsComponent");
		searchBuilds.setLastAction(getAction());
		setAction("searchBuilds");
	}

	public void deleteBuilds(IRequestCycle cycle) {
		setAction("deleteBuilds");
	}

	public void deleteBuild(IRequestCycle cycle) {
		setAction("deleteBuild");
	}

	public void moveBuilds(IRequestCycle cycle) {
		MoveBuilds moveBuilds = (MoveBuilds) getComponent("moveBuildsComponent");
		moveBuilds.setLastAction("listBuilds");
		setAction("moveBuilds");
	}

	public void moveBuild(IRequestCycle cycle) {
		MoveBuilds moveBuilds = (MoveBuilds) getComponent("moveBuildsComponent");
		moveBuilds.setLastAction("viewBuild");
		setAction("moveBuilds");
	}

	/**
	 * Actually delete builds
	 * @param cycle
	 */
	public void confirmDeleteBuilds(IRequestCycle cycle) {
		BuildList buildList = (BuildList) getComponent("buildListComponent");
		if (buildList.getSearchCriteria() == null)
			return;
		Luntbuild.getDao().deleteBuilds(buildList.getSearchCriteria());
		buildList.setSearchCriteria(null);
		tabSelected();
	}

	/**
	 * Cancel the deletion of build
	 * @param cycle
	 */
	public void cancelDeleteBuilds(IRequestCycle cycle) {
		setAction("listBuilds");
	}

	/**
	 * Actually delete build
	 * @param cycle
	 */
	public void confirmDeleteBuild(IRequestCycle cycle) {
		BuildViewer buildViewer = (BuildViewer) getComponent("buildViewerComponent");
		if (buildViewer.getBuildId() == 0)
			return;
		Luntbuild.getDao().deleteBuild(buildViewer.getBuildId());
		buildViewer.setBuildId(0);
		tabSelected();
	}

	/**
	 * Cancel the deletion of build
	 * @param cycle
	 */
	public void cancelDeleteBuild(IRequestCycle cycle) {
		setAction("viewBuild");
	}

	public abstract int getScheduleIndex();

	/**
	 * Get css class for current row
	 * @return
	 */
	public String getRowClass() {
		if (getScheduleIndex() % 2 == 0)
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

	public void gotoSchedule(IRequestCycle cycle) {
		long projectId = ((Long) cycle.getServiceParameters()[0]).longValue();
		ProjectPage projectPage = (ProjectPage)cycle.getPage("ProjectPage");
		projectPage.assignProjectId(projectId);
		TabControl tabs = (TabControl)projectPage.getComponent("tabs");
		tabs.setSelectedTabId("schedules");
		SchedulesTab schedulesTab = (SchedulesTab) tabs.getSelectedTab();
		schedulesTab.setAction(null);
		cycle.activate(projectPage);
	}

	public void gotoProject(IRequestCycle cycle) {
		long projectId = ((Long) cycle.getServiceParameters()[0]).longValue();
		ProjectPage projectPage = (ProjectPage) cycle.getPage("ProjectPage");
		projectPage.assignProjectId(projectId);
		cycle.activate(projectPage);
	}
}
