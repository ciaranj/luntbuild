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

import java.text.ParseException;
import java.util.List;

import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.spec.IComponentSpecification;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.web.selectionmodels.BuildCleanupStrategySelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.BuildTypeSelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.BuilderSelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.LabelStrategySelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.NotifyStrategySelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.PostbuildStrategySelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.ScheduleSelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.TriggerDependencyStrategySelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.TriggerTypeSelectionModel;

/**
 * This component renders the schedule editing page
 *
 * @author robin shine
 */
public abstract class ScheduleEditor extends BaseComponent implements PageDetachListener {
	private Schedule scheduleToEdit;
	private Class triggerType;
	private int buildCleanupStrategy;

	public SchedulesTab getSchedulesTab() {
		return (SchedulesTab) getContainer();
	}

	public ProjectPage getProjectPage() {
		return (ProjectPage) getPage();
	}

	public void pageDetached(PageEvent event) {
		scheduleToEdit = null;
	}

	public void finishLoad(IRequestCycle iRequestCycle, IPageLoader iPageLoader, IComponentSpecification iComponentSpecification) {
		super.finishLoad(iRequestCycle, iPageLoader, iComponentSpecification);
		scheduleToEdit = null;
	}

	public void save(IRequestCycle cycle) {
		getSchedulesTab().ensureCurrentTab();
		setSaveOrCancelTriggered(true);
		try {
			if (getScheduleToEdit().getTrigger() != null) {
				if (getScheduleToEdit().getTrigger().getClass() == SimpleTrigger.class) {
					SimpleTrigger simpleTrigger = (SimpleTrigger) getScheduleToEdit().getTrigger();
					simpleTrigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
					try {
						long repeatIntervalValue = new Long(getRepeatInterval()).longValue() * 60000;
						if (repeatIntervalValue == 0)
							throw new ValidationException("Property \"repeat interval\" should not be 0!");
						simpleTrigger.setRepeatInterval(repeatIntervalValue);
					} catch (NumberFormatException e) {
						throw new ValidationException("Property \"repeat interval\" invalid: " + e.getMessage());
					} catch (IllegalArgumentException e) {
						throw new ValidationException("Property \"repeat interval\" invalid: " + e.getMessage());
					}
				} else {
					try {
						((CronTrigger) getScheduleToEdit().getTrigger()).setCronExpression(getCronExpression());
					} catch (ParseException e) {
						throw new ValidationException("Property \"cron expression\" invalid: " + e.getMessage());
					}
				}
			}
			getScheduleToEdit().validate();
			if (Luntbuild.getDao().isScheduleNameUsed(getScheduleToEdit())) {
				setErrorMsg("Schedule with this name already exists!");
				return;
			}
			if (getScheduleToEdit().getId() != 0) {
				Schedule scheduleUpToDate = Luntbuild.getDao().loadSchedule(getScheduleToEdit().getId());
				getScheduleToEdit().setStatus(scheduleUpToDate.getStatus());
				getScheduleToEdit().setStatusDate(scheduleUpToDate.getStatusDate());
			}
			Luntbuild.getDao().saveSchedule(getScheduleToEdit());
			getSchedulesTab().setAction(null);
			Luntbuild.getSchedService().rescheduleBuilds();
		} catch (ValidationException e) {
			setErrorMsg(e.getMessage());
			return;
		}
	}

	public void submitForm(IRequestCycle cycle) {
		getSchedulesTab().ensureCurrentTab();
		if (isSaveOrCancelTriggered())
			return;
		if (getTriggerType() != triggerType) {
			try {
				if (triggerType == null)
					getScheduleToEdit().setTrigger(null);
				else
					getScheduleToEdit().setTrigger((Trigger) triggerType.newInstance());
			} catch (Exception e) {
				throw new ApplicationRuntimeException(e);
			}			
		}
		if (buildCleanupStrategy != getScheduleToEdit().getBuildCleanupStrategy()) {
			getScheduleToEdit().setBuildCleanupStrategy(buildCleanupStrategy);
			getScheduleToEdit().setBuildCleanupStrategyData("");
		}
	}

	public void cancel(IRequestCycle cycle) {
		getSchedulesTab().ensureCurrentTab();
		setSaveOrCancelTriggered(true);
		getSchedulesTab().setAction(null);
	}

	public abstract void setErrorMsg(String errorMsg);

	public abstract String getErrorMsg();

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorName propertyEditorName1";
		else
			return "propertyEditorName propertyEditorName2";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorValue propertyEditorValue1";
		else
			return "propertyEditorValue propertyEditorValue2";
	}

	public String getPropertyTailCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorTail propertyEditorTail1";
		else
			return "propertyEditorTail propertyEditorTail2";
	}

	public abstract int getCssIndex();

	public abstract boolean isSaveOrCancelTriggered();

	public abstract void setSaveOrCancelTriggered(boolean saveOrCancelTriggered);

	public Schedule getScheduleToEdit() {
		return scheduleToEdit;
	}

	public void setScheduleToEdit(Schedule scheduleToEdit) {
		this.scheduleToEdit = scheduleToEdit;
		Trigger trigger = getScheduleToEdit().getTrigger();
		if (trigger == null) {
			setRepeatInterval(null);
			setCronExpression(null);
		} else if (trigger instanceof SimpleTrigger) {
			SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
			setRepeatInterval(String.valueOf(simpleTrigger.getRepeatInterval()/60000));
			setCronExpression(null);
		} else {
			CronTrigger cronTrigger = (CronTrigger) trigger;
			setCronExpression(cronTrigger.getCronExpression());
			setRepeatInterval(null);
		}
		Tapestry.fireObservedChange(this, "scheduleToEdit", this.scheduleToEdit);
	}

    public void setScheduleToCopy(Schedule scheduleToCopy) {
        Schedule newSchedule = new Schedule(scheduleToCopy);
        newSchedule.setName("");
        setScheduleToEdit(newSchedule);
    }

	public IPropertySelectionModel getBuildTypeSelectionModel() {
		return new BuildTypeSelectionModel();
	}

	public IPropertySelectionModel getBuildCleanupStrategySelectionModel() {
		return new BuildCleanupStrategySelectionModel();
	}

	public IPropertySelectionModel getTriggerDependencyStrategySelectionModel() {
		return new TriggerDependencyStrategySelectionModel();
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

	public int getBuildCleanupStrategy() {
		return getScheduleToEdit().getBuildCleanupStrategy();
	}

	public void setBuildCleanupStrategy(int buildCleanupStrategy) {
		this.buildCleanupStrategy = buildCleanupStrategy;
	}

	public abstract void setRepeatInterval(String repeatInerval);

	public abstract String getRepeatInterval();

	public abstract void setCronExpression(String cronExpression);

	public abstract String getCronExpression();

	public String getPaletteCssClass() {
		if (getCssIndex() % 2 == 0)
			return "palette1";
		else
			return "palette2";
	}

	public IPropertySelectionModel getDependentSchedulesModel() {
		List schedules = Luntbuild.getDao().loadSchedules();
		schedules.remove(getScheduleToEdit());
		return new ScheduleSelectionModel(schedules);
	}

	public IPropertySelectionModel getBuilderSelectionModel() {
		return new BuilderSelectionModel(getScheduleToEdit().getProject().getBuilderList());
	}
}