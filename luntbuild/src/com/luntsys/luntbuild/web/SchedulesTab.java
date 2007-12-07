/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-12
 * Time: 6:50:17
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acegisecurity.AccessDeniedException;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.spec.IComponentSpecification;
import org.quartz.Trigger;

import com.luntsys.luntbuild.BuildGenerator;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.security.SecurityHelper;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.NameComparator;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;

/**
 * This component renders a project's schedule list page
 *
 * @author robin shine
 */
public abstract class SchedulesTab extends TabPageComponent implements PageDetachListener {
	private Schedule schedule;

	public String getTabName() {
		return "Schedules";
	}

	public void tabSelected() {
		setAction(null);
	}

	public ProjectPage getProjectPage() {
		return (ProjectPage) getPage();
	}

	public void finishLoad(IRequestCycle cycle, IPageLoader loader, IComponentSpecification specification) {
		super.finishLoad(cycle, loader, specification);
		schedule = null;
	}

	/**
	 * Create a new schedule
	 * @param cycle
	 */
	public void createSchedule(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		ScheduleEditor scheduleEditor = (ScheduleEditor) getComponent("scheduleEditorComponent");
		Schedule schedule = new Schedule();
		schedule.setProject(getProjectPage().getProject());
		scheduleEditor.setScheduleToEdit(schedule);
		setAction("editSchedule");
	}

	public List getSchedules() {
		List schedules = new ArrayList(getProjectPage().getProject().getSchedules());
		Collections.sort(schedules, new NameComparator());
		return schedules;
	}

	public void deleteSchedule(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		long scheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		setScheduleIdToDelete(scheduleId);
		setAction("deleteSchedule");
	}

	public void editSchedule(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		long scheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		ScheduleEditor scheduleEditor = (ScheduleEditor) getComponent("scheduleEditorComponent");
		scheduleEditor.setScheduleToEdit(Luntbuild.getDao().loadSchedule(scheduleId));
		setAction("editSchedule");
	}

    public void copySchedule(IRequestCycle cycle) {
        ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
        long scheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
        ScheduleEditor scheduleEditor = (ScheduleEditor) getComponent("scheduleEditorComponent");
        scheduleEditor.setScheduleToCopy(Luntbuild.getDao().loadSchedule(scheduleId));
        setAction("editSchedule");
    }

	/**
	 * Actually delete the schedule
	 *
	 * @param cycle
	 */
	public void confirmDeleteSchedule(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		if (getScheduleIdToDelete() == 0) { // avoid re-submit
			return;
		}
		Luntbuild.getDao().deleteSchedule(Luntbuild.getDao().loadSchedule(getScheduleIdToDelete()));
		setScheduleIdToDelete(0);
		Luntbuild.getSchedService().rescheduleBuilds();
		setAction(null);
	}

	public void pageDetached(PageEvent event) {
		schedule = null;
	}

	/**
	 * Cancel deletion of schedule
	 *
	 * @param cycle
	 */
	public void cancelDeleteSchedule(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		setScheduleIdToDelete(0);
		setAction(null);
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
		setLastBuild(Luntbuild.getDao().loadLastBuild(schedule));
		setLastSuccessBuild(null);
		setRunningBuild(null);
		if (getLastBuild() != null) {
			if (getLastBuild().getStatus() != com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_SUCCESS)
				setLastSuccessBuild(Luntbuild.getDao().loadLastSuccessBuild(schedule));
			Build runningBuild = Luntbuild.getDao().loadRunningBuild(schedule);
			if (runningBuild != null && runningBuild.getId() != getLastBuild().getId())
				setRunningBuild(runningBuild);
		}
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public String getScheduleStatusGif() {
		if (schedule.getStatus() == com.luntsys.luntbuild.facades.Constants.SCHEDULE_STATUS_SUCCESS)
			return "images/success.gif";
		else if (schedule.getStatus() == com.luntsys.luntbuild.facades.Constants.SCHEDULE_STATUS_FAILED)
			return "images/failed.gif";
		else if (schedule.getStatus() == com.luntsys.luntbuild.facades.Constants.SCHEDULE_STATUS_RUNNING)
			return "images/running.gif";
		else
			return "images/null.gif";
	}

	public String getScheduleStatusDate() {
		return Luntbuild.DATE_DISPLAY_FORMAT.format(schedule.getStatusDate());
	}

	public String getLastSuccessBuildEndDate() {
		return Luntbuild.DATE_DISPLAY_FORMAT.format(getLastSuccessBuild().getEndDate());
	}

	public String getRunningBuildStartDate() {
		return Luntbuild.DATE_DISPLAY_FORMAT.format(getRunningBuild().getStartDate());
	}

	public String getLastBuildStatusDescription() {
		if (getLastBuild().getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_RUNNING)
			return "building at " + Luntbuild.DATE_DISPLAY_FORMAT.format(getLastBuild().getStartDate());
		else if (getLastBuild().getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_SUCCESS)
			return "success at " + Luntbuild.DATE_DISPLAY_FORMAT.format(getLastBuild().getEndDate());
		else
			return "failed at " + Luntbuild.DATE_DISPLAY_FORMAT.format(getLastBuild().getEndDate());
	}

	public String getLastBuildStatusGif() {
		if (getLastBuild().getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_RUNNING)
			return "images/running.gif";
		else if (getLastBuild().getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_SUCCESS)
			return "images/success.gif";
		else
			return "images/failed.gif";
	}

	/**
	 * List all builds belongs to a specific schedule
	 *
	 * @param cycle
	 */
	public void listBuilds(IRequestCycle cycle) {
		Home homePage = (Home) cycle.getPage("Home");
		cycle.activate(homePage);
		TabControl tabs = (TabControl) homePage.getComponent("tabs");
		tabs.setSelectedTabId("builds");
		BuildsTab buildsTab = (BuildsTab) tabs.getSelectedTab();
		buildsTab.listBuilds(cycle);
	}

	/**
	 * Enable all schedules
	 * @param cycle
	 */
	public void enableAllBuilds(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		List schedules = getSchedules();
		for (int i = 0; i < schedules.size(); i++)
		{
			Schedule schedule = (Schedule) schedules.get(i);
			schedule.setScheduleDisabled(false);
			Luntbuild.getDao().saveSchedule(schedule);
		}
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	/**
	 * Disable all schedules
	 * @param cycle
	 */
	public void disableAllBuilds(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		List schedules = getSchedules();
		for (int i = 0; i < schedules.size(); i++)
		{
			Schedule schedule = (Schedule) schedules.get(i);
			schedule.setScheduleDisabled(true);
        	Luntbuild.getDao().saveSchedule(schedule);
    	}
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	/**
	 * Enable a particular schedule
	 * @param cycle
	 */
	public void enableBuild(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		long scheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleId);
		schedule.setScheduleDisabled(false);
        Luntbuild.getDao().saveSchedule(schedule);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	/**
	 * Disable a particular schedule
	 * @param cycle
	 */
	public void disableBuild(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		long scheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleId);
		schedule.setScheduleDisabled(true);
        Luntbuild.getDao().saveSchedule(schedule);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	/**
	 * Manually trigger a build
	 *
	 * @param cycle
	 */
	public void build(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		ManualBuildEditor manualBuildEditor = (ManualBuildEditor) getComponent("manualBuildEditorComponent");
		long scheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleId);
		if (schedule.isDisabled())
			return;
		manualBuildEditor.setSchedule(schedule);
		manualBuildEditor.setWhereFrom(ManualBuildEditor.BUILD_FROM_SCHEDULESTAB);
		setAction("buildManually");
	}

	public void killRunningBuild(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		long scheduleId = ((Long) cycle.getServiceParameters()[0]).longValue();
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleId);
		// check permissions
		if (!SecurityHelper.isPrjBuildable(schedule.getProject().getId()))
			throw new AccessDeniedException("Access denied!");

		Thread currentThread = schedule.getCurrentThread();
		if (currentThread != null)
			currentThread.interrupt();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	public void viewBuild(IRequestCycle cycle) {
		Home homePage = (Home) cycle.getPage("Home");
		cycle.activate(homePage);
		TabControl tabs = (TabControl) homePage.getComponent("tabs");
		tabs.setSelectedTabId("builds");
		BuildsTab buildsTab = (BuildsTab) tabs.getSelectedTab();
		buildsTab.viewBuild(cycle);
	}

	public abstract Build getRunningBuild();

	public abstract void setRunningBuild(Build runningBuild);

	public abstract Build getLastBuild();

	public abstract void setLastBuild(Build lastBuild);

	public abstract Build getLastSuccessBuild();

	public abstract void setLastSuccessBuild(Build lastSuccessBuild);

	public abstract void setAction(String action);

	public abstract String getAction();

	public abstract void setScheduleIdToDelete(long scheduleIdToDelete);

	public abstract long getScheduleIdToDelete();

	public boolean loadManualBuildTriggers() {
		setManualBuildTriggers(Luntbuild.getSchedService().getWaitingManualBuildTriggers(getSchedule()));
		if (getManualBuildTriggers().size() == 0)
			return false;
		else
			return true;
	}

	public abstract List getManualBuildTriggers();

	public abstract void setManualBuildTriggers(List manualBuildTriggers);

	/**
	 * Load currently scheduled rebuilds for current schedule
	 *
	 * @return true if there is any scheduled rebuilds, or false if there is not any
	 */
	public boolean loadRebuilds() {
		Map rebuilds = Luntbuild.getSchedService().getRebuilds(getSchedule());
		setRebuilds(rebuilds);
		if (rebuilds.size() == 0)
			return false;
		else
			return true;
	}

	public abstract void setManualBuildTrigger(Trigger manualBuildTrigger);

	public abstract Trigger getManualBuildTrigger();

	public abstract Trigger getRebuildTrigger();

	public String getManualBuildType() {
		com.luntsys.luntbuild.facades.BuildParams buildParams = Schedule.parseTriggerName(getManualBuildTrigger().getName());
		return com.luntsys.luntbuild.facades.Constants.getBuildTypeText(buildParams.getBuildType());
	}

	public String getManualBuildPostbuildStrategy() {
		com.luntsys.luntbuild.facades.BuildParams buildParams = Schedule.parseTriggerName(getManualBuildTrigger().getName());
		return com.luntsys.luntbuild.facades.Constants.getPostbuildStrategyText(buildParams.getPostbuildStrategy());
	}

	public String getManualBuildLabelStrategy() {
		com.luntsys.luntbuild.facades.BuildParams buildParams = Schedule.parseTriggerName(getManualBuildTrigger().getName());
		return com.luntsys.luntbuild.facades.Constants.getLabelStrategyText(buildParams.getLabelStrategy());
	}

	public String getManualBuildTriggerStartTime() {
		return Luntbuild.DATE_DISPLAY_FORMAT.format(getManualBuildTrigger().getStartTime());
	}

	public String getRebuildTriggerStartTime() {
		return Luntbuild.DATE_DISPLAY_FORMAT.format(getRebuildTrigger().getStartTime());
	}

	public String getManualBuildNotifyStrategy() {
		com.luntsys.luntbuild.facades.BuildParams buildParams = Schedule.parseTriggerName(getManualBuildTrigger().getName());
		return com.luntsys.luntbuild.facades.Constants.getNotifyStrategyText(buildParams.getNotifyStrategy());
	}

	public String getRebuildNotifyStrategy() {
		String[] fields = getRebuildTrigger().getName().split("\\" + Luntbuild.TRIGGER_NAME_SEPERATOR);
		return com.luntsys.luntbuild.facades.Constants.getNotifyStrategyText(new Integer(fields[1]).intValue());
	}

	public String getRebuildPostbuildStrategy() {
		String[] fields = getRebuildTrigger().getName().split("\\" + Luntbuild.TRIGGER_NAME_SEPERATOR);
		return Constants.getPostbuildStrategyText(new Integer(fields[2]).intValue());
	}

	public void deleteManualBuildTrigger(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		String manualBuildTriggerName = (String) cycle.getServiceParameters()[0];
		int requestTriggerDeleteCount = ((Integer) cycle.getServiceParameters()[1]).intValue();
		if (requestTriggerDeleteCount != getTriggerDeleteCount()) {
			return;
		}
		setTriggerDeleteCount(getTriggerDeleteCount() + 1);
		Luntbuild.getSchedService().unscheduleBuild(manualBuildTriggerName, BuildGenerator.MANUALBUILD_GROUP);
	}

	public void deleteRebuildTrigger(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		String rebuildTriggerName = (String) cycle.getServiceParameters()[0];
		int requestTriggerDeleteCount = ((Integer) cycle.getServiceParameters()[1]).intValue();
		if (requestTriggerDeleteCount != getTriggerDeleteCount()) {
			return;
		}
		setTriggerDeleteCount(getTriggerDeleteCount() + 1);
		Luntbuild.getSchedService().unscheduleBuild(rebuildTriggerName, BuildGenerator.REBUILD_GROUP);
	}

	/**
	 * This property is used to prevent method {@link this#deleteManualBuildTrigger(org.apache.tapestry.IRequestCycle)} from taking effect
	 * multiple times when user refreshs the page
	 *
	 * @param triggerDeleteCount
	 */
	public abstract void setTriggerDeleteCount(int triggerDeleteCount);

	public abstract int getTriggerDeleteCount();

	public abstract void setRebuilds(Map rebuilds);

	public abstract Map getRebuilds();

	public int getRefreshInterval() {
		if (getAction() == null)
			return Luntbuild.getPageRefreshInterval();
		else
			return 0;
	}

	public abstract int getScheduleIndex();

	public String getScheduleCssClass() {
		if (getScheduleIndex() == 0)
			return "firstRowContainingTable";
		else
			return "rowContainingTable";
	}

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyListName propertyListName2";
		else
			return "propertyListName propertyListName1";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyListValue propertyListValue2";
		else
			return "propertyListValue propertyListValue1";
	}

	public abstract int getCssIndex();

	public void gotoSchedule(IRequestCycle cycle) {
		long projectId = ((Long) cycle.getServiceParameters()[0]).longValue();
		ProjectPage projectPage = (ProjectPage)cycle.getPage("ProjectPage");
		projectPage.setProjectId(projectId);
		projectPage.gotoBasicTab(cycle);
		TabControl tabs = (TabControl)projectPage.getComponent("tabs");
		tabs.setSelectedTabId("schedules");
		SchedulesTab schedulesTab = (SchedulesTab) tabs.getSelectedTab();
		schedulesTab.setAction(null);
		cycle.activate(projectPage);
	}

	public Set getDependentSchedules() {
		return getSchedule().getDependsOn(Luntbuild.getDao().loadSchedules());
	}

	public void gotoBuilder(IRequestCycle cycle) {
		TabControl tabs = (TabControl)getProjectPage().getComponent("tabs");
		tabs.setSelectedTabId("builderList");
		BuilderListTab buildersTab = (BuilderListTab) tabs.getSelectedTab();
		buildersTab.setAction(null);
	}
}
