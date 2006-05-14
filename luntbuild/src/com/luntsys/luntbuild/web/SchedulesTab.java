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

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IRequestCycle;
import com.luntsys.luntbuild.utility.LuntBuild;
import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Schedule;

import java.util.List;

/**
 * This component responsible for schedule configuration in the system
 *
 * @author alvin shen
 */
public abstract class SchedulesTab extends TabPageComponent {

	public String getTabName() {
		return "schedules";
	}

	public void tabSelected() {
		// refresh data on select
		loadData();
	}

	/**
	 * Loads schedules information from persistence layer
	 */
	public void loadData(){
		setSchedules(LuntBuild.getDao().loadSchedules());
	}

	/**
	 * create a new schedule
	 * @param cycle
	 */
	public void createSchedule(IRequestCycle cycle){
		SchedulePage page = (SchedulePage)cycle.getPage("SchedulePage");
		page.createSchedule();
		cycle.activate(page);
	}

	/**
	 * Load a particular schedule
 	 * @param cycle
	 */
	public void loadSchedule(IRequestCycle cycle){
		long scheduleId = ((Long)cycle.getServiceParameters()[0]).longValue();
		SchedulePage page = (SchedulePage)cycle.getPage("SchedulePage");
		page.loadSchedule(scheduleId);
		cycle.activate(page);
	}

	public void deleteSchedule(IRequestCycle cycle){
		long scheduleId = ((Long)cycle.getServiceParameters()[0]).longValue();
		setScheduleToBeDelete(LuntBuild.getDao().loadSchedule(scheduleId));
		setCurrentAction("delete");
	}

	/**
	 * Actually delete the schedule
	 * @param cycle
	 */
	public void confirmDelete(IRequestCycle cycle){
		if (getScheduleToBeDelete() != null){ // consider refresh occasion
			LuntBuild.getDao().deleteSchedule(getScheduleToBeDelete().getId());
			setScheduleToBeDelete(null);
			loadData();
			LuntBuild.getService().rescheduleBuilds();
		}
		setCurrentAction(null);
	}

	/**
	 * Cancel the deletion of the schedule
	 * @param cycle
	 */
	public void cancelDelete(IRequestCycle cycle){
		setScheduleToBeDelete(null);
		loadData();
		setCurrentAction(null);
	}

	public abstract void setSchedules(List schedules);

	public abstract List getSchedules();

	public abstract void setCurrentSchedule(Schedule schedule);

	public abstract Schedule getCurrentSchedule();

	public abstract void setCurrentAction(String currentAction);

	public abstract String getCurrentAction();

	public abstract void setScheduleToBeDelete(Schedule schedule);

	public abstract Schedule getScheduleToBeDelete();
}
