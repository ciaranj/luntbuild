/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-17
 * Time: 16:02:20
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

import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.web.selectionmodels.*;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.dao.DataIntegrityViolationException;

import java.text.ParseException;

/**
 * This component renders the schedule editing page
 *
 * @author robin shine
 */
public abstract class ScheduleEditor extends BaseComponent {
	private Class triggerType;
	private String repeatInterval;
	private String cronExpression;

	public SchedulesTab getSchedulesTab() {
		return (SchedulesTab) getContainer();
	}

	public ProjectPage getProjectPage() {
		return (ProjectPage) getPage();
	}

	public void save(IRequestCycle cycle) {
		setSaveOrCancelTriggered(true);
		try {
			if (getScheduleToEdit().getTrigger() != null) {
				if (getScheduleToEdit().getTrigger().getClass() == SimpleTrigger.class) {
					SimpleTrigger simpleTrigger = (SimpleTrigger) getScheduleToEdit().getTrigger();
					simpleTrigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
					try {
						int iRepeatInterval = new Integer(repeatInterval).intValue() * 60000;
						if (iRepeatInterval == 0)
							throw new ValidationException("Property \"repeat interval\" should not be 0!");
						simpleTrigger.setRepeatInterval(iRepeatInterval);
					} catch (NumberFormatException e) {
						throw new ValidationException("Property \"repeat interval\" invalid: " + e.getMessage());
					} catch (IllegalArgumentException e) {
						throw new ValidationException("Property \"repeat interval\" invalid: " + e.getMessage());
					}
				} else {
					try {
						((CronTrigger) getScheduleToEdit().getTrigger()).setCronExpression(cronExpression);
					} catch (ParseException e) {
						throw new ValidationException("Property \"cron expression\" invalid: " + e.getMessage());
					}
				}
			}
			getScheduleToEdit().validate();
			Luntbuild.getDao().saveSchedule(getScheduleToEdit());
			getSchedulesTab().setAction(null);
			Luntbuild.getSchedService().rescheduleBuilds();
		} catch (ValidationException e) {
			setErrorMsg(e.getMessage());
			return;
		} catch (DataIntegrityViolationException e) {
			getScheduleToEdit().setId(getScheduleIdToEdit());
			setErrorMsg("Specified schedule already exists!");
			return;
		}
	}

	public void changeTriggerType(IRequestCycle cycle) {
		if (isSaveOrCancelTriggered())
			return;
		if (getTriggerType() == triggerType)
			return;
		try {
			if (triggerType == null)
				getScheduleToEdit().setTrigger(null);
			else
				getScheduleToEdit().setTrigger((Trigger) triggerType.newInstance());
		} catch (Exception e) {
			throw new ApplicationRuntimeException(e);
		}
	}

	public void cancel(IRequestCycle cycle) {
		setSaveOrCancelTriggered(true);
		getSchedulesTab().setAction(null);
	}

	public abstract void setErrorMsg(String errorMsg);

	public abstract String getErrorMsg();

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorName1";
		else
			return "propertyEditorName2";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorValue1";
		else
			return "propertyEditorValue2";
	}

	public String getPropertyTailCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorTail1";
		else
			return "propertyEditorTail2";
	}

	public abstract int getCssIndex();

	public abstract boolean isSaveOrCancelTriggered();

	public abstract void setSaveOrCancelTriggered(boolean saveOrCancelTriggered);

	public abstract void setScheduleToEdit(Schedule scheduleToEdit);

	public abstract Schedule getScheduleToEdit();

	public abstract void setScheduleIdToEdit(long scheduleIdToEdit);

	public abstract long getScheduleIdToEdit();

	public void assignScheduleIdToEdit(long scheduleIdToEdit) {
		if (scheduleIdToEdit == 0) {
			setScheduleToEdit(new Schedule());
			getScheduleToEdit().setProject(getProjectPage().getProject());
		} else
			setScheduleToEdit(Luntbuild.getDao().loadSchedule(scheduleIdToEdit));
		setScheduleIdToEdit(scheduleIdToEdit);
	}

	public IPropertySelectionModel getBuildStrategySelectionModel() {
		return new BuildStrategySelectionModel();
	}

	public IPropertySelectionModel getBuildTypeSelectionModel() {
		return new BuildTypeSelectionModel();
	}

	public IPropertySelectionModel getPostbuildStrategySelectionModel() {
		return new PostbuildStrategySelectionModel();
	}

	public IPropertySelectionModel getNotifyStrategySelectionModel() {
		return new NotifyStrategySelectionModel();
	}

	public IPropertySelectionModel getLabelStrategySelectionModel() {
		return new LabelStrategySelectionModel();
	}

	public IPropertySelectionModel getTriggerTypeSelectionModel() {
		return new TriggerTypeSelectionModel();
	}

	public Class getTriggerType() {
		if (getScheduleToEdit().getTrigger() != null)
			return getScheduleToEdit().getTrigger().getClass();
		else
			return null;
	}

	public void setTriggerType(Class triggerType) {
		this.triggerType = triggerType;
	}

	public void setRepeatInterval(String repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public String getRepeatInterval() {
		if (getErrorMsg() != null)
			return repeatInterval;
		SimpleTrigger simpleTrigger = (SimpleTrigger) getScheduleToEdit().getTrigger();
		if (simpleTrigger.getRepeatInterval() == 0)
			return "";
		else
			return String.valueOf(simpleTrigger.getRepeatInterval() / 60000);
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getCronExpression() {
		if (getErrorMsg() != null)
			return cronExpression;
		CronTrigger cronTrigger = (CronTrigger) getScheduleToEdit().getTrigger();
		return cronTrigger.getCronExpression();
	}
}