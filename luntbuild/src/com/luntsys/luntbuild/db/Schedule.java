/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-27
 * Time: 6:24:27
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
package com.luntsys.luntbuild.db;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.luntsys.luntbuild.remoting.ScheduleFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

/**
 * Schedule utilize a quartz(http://www.opensymphony.com/quartz/) trigger to
 * implement its functionality for schedules. By utilizing this trigger, it can be
 * able to achieve periodical schedules or cron-like schedules. This is a hibernate
 * mapping class
 *
 * @author robin shine
 */
public class Schedule {	
	private long id;
	private String name;
	private String description;
	private Trigger trigger;
	private Map buildSchedules;

	/**
	 * set a unique identify for this schedule, called automatically by hibernate
	 * @param id
	 */
	public void setId(long id){
		this.id = id;
	}

	public long getId(){
		return id;
	}

	/**
	 * set a name for this schedule
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	/**
	 * set trigger of this schedule
	 * @param trigger
	 * @see org.quartz.Trigger
	 */
	public void setTrigger(Trigger trigger){
		this.trigger = trigger;
	}

	public Trigger getTrigger(){
		return trigger;
	}

	/**
	 * set build schedules configured for this particular schedule
	 * @param buildSchedules map from {@link View} to {@link BuildSchedule}
	 */
	public void setBuildSchedules(Map buildSchedules){
		this.buildSchedules = buildSchedules;
	}

	public Map getBuildSchedules(){
		return buildSchedules;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Schedule){
			if (getId() == ((Schedule)obj).getId())
				return true;
		}
		return false;
	}

	public int hashCode() {
		return (int) getId();
	}

	/**
	 * Validates fields of this class
	 * @throws ValidationException
	 */
	public void validate() {
		if (Luntbuild.isEmpty(getName()))
			throw new ValidationException("Name should not be empty!");
		setName(getName().trim());

		if (getTrigger() != null) {
			try {
				Trigger cloneTrigger =  (Trigger) getTrigger().clone();
				if (cloneTrigger instanceof SimpleTrigger) {
					SimpleTrigger simpleTrigger = (SimpleTrigger) cloneTrigger;
					simpleTrigger.setStartTime(new Date(System.currentTimeMillis() + simpleTrigger.getRepeatInterval()));
				} else
					cloneTrigger.setStartTime(new Date(System.currentTimeMillis()));
				Luntbuild.getSchedService().validateTrigger(cloneTrigger);
			} catch (Exception e){
				throw new ValidationException(e.getMessage());
			}
		}
	}

	public ScheduleFacade getFacade() {
		ScheduleFacade facade = new ScheduleFacade();
		facade.setId(getId());
		facade.setName(getName());
		facade.setDescription(getDescription());
		if (getTrigger() == null)
			facade.setTriggerType(ScheduleFacade.TRIGGER_MANUAL);
		else if (getTrigger().getClass() == SimpleTrigger.class) {
			facade.setTriggerType(ScheduleFacade.TRIGGER_SIMPLE);
			SimpleTrigger simpleTrigger = (SimpleTrigger) getTrigger();
			facade.setRepeatInterval(simpleTrigger.getRepeatInterval() / 60000);
		} else {
			facade.setTriggerType(ScheduleFacade.TRIGGER_CRON);
			CronTrigger cronTrigger = (CronTrigger) getTrigger();
			facade.setCronExpression(cronTrigger.getCronExpression());
		}
		return facade;
	}

	public void setFacade(ScheduleFacade facade) {
		setName(facade.getName());
		setDescription(facade.getDescription());
		if (facade.getTriggerType() == ScheduleFacade.TRIGGER_MANUAL)
			setTrigger(null);
		else if (facade.getTriggerType() == ScheduleFacade.TRIGGER_SIMPLE) {
			SimpleTrigger simpleTrigger = new SimpleTrigger();
			simpleTrigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			simpleTrigger.setRepeatInterval(facade.getRepeatInterval() * 60000);
			setTrigger(simpleTrigger);
		} else {
			CronTrigger cronTrigger = new CronTrigger();
			try {
				cronTrigger.setCronExpression(facade.getCronExpression());
			} catch (ParseException e) {
				throw new RuntimeException("Invalid cron expression: " + facade.getCronExpression());
			}
			setTrigger(cronTrigger);
		}
		validate();
	}
}
