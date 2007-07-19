/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-20
 * Time: 21:27:07
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

package com.luntsys.luntbuild.facades.lb111;

/**
 * Schedule facade.
 *
 * @author robin shine
 * @see com.luntsys.luntbuild.db.Schedule
 */
public class ScheduleFacade {
	/** Trigger type, manual */
	public final static int TRIGGER_MANUAL = 0;
	/** Trigger type, simple */
	public final static int TRIGGER_SIMPLE = 1;
	/** Trigger type, cron */
	public final static int TRIGGER_CRON = 2;

	private long id;
	private String name;
	private String description;
	/**
	 * Should be one of the values of TRIGGER_MANUAL, TRIGGER_SIMPLE, and
	 * TRIGGER_CRON
	 */
	private int triggerType;
	/**
	 * minutes interval for a simple type trigger. Will take effect when triggerType
	 * equals TRIGGER_SIMPLE
	 */
	private long repeatInterval;
	/**
	 * cron expression for a cron type trigger. Will take effect when triggerType equals
	 * TRIGGER_CRON
	 */
	private String cronExpression;

    /**
     * Gets the indentifier of this schedule.
     * 
     * @return the indentifier of this schedule
     */
	public long getId() {
		return id;
	}

    /**
     * Sets the indentifier of this schedule.
     * 
     * @param id the indentifier of this schedule
     */
	public void setId(long id) {
		this.id = id;
	}

    /**
     * Gets the name of this schedule.
     * 
     * @return the name
     */
	public String getName() {
		return name;
	}

    /**
     * Sets the name of this schedule.
     *
     * @param name the name
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Gets the description of this schedule.
     * 
     * @return the description
     */
	public String getDescription() {
		return description;
	}

    /**
     * Sets the description of this schedule.
     * 
     * @param description the description
     */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the trigger type of this schedule.
	 * 
	 * @return the trigger type
	 * @see ScheduleFacade#TRIGGER_CRON
	 * @see ScheduleFacade#TRIGGER_MANUAL
	 * @see ScheduleFacade#TRIGGER_SIMPLE
	 */
	public int getTriggerType() {
		return triggerType;
	}

	/**
	 * Sets the trigger type of this schedule.
	 * 
	 * @param triggerType the trigger type
	 * @see ScheduleFacade#TRIGGER_CRON
	 * @see ScheduleFacade#TRIGGER_MANUAL
	 * @see ScheduleFacade#TRIGGER_SIMPLE
	 */
	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}
	/**
	 * Gets the repeat interval of this schedule.
	 * Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link ScheduleFacade#TRIGGER_SIMPLE}.
	 * 
	 * @return the repeat interval of this schedule
	 */
	public long getRepeatInterval() {
		return repeatInterval;
	}

	/**
	 * Sets the repeat interval of this schedule.
	 * Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link ScheduleFacade#TRIGGER_SIMPLE}.
	 * 
	 * @param repeatInterval the repeat interval of this schedule
	 */
	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	/**
	 * Gets the cron expression of this schedule.
	 * Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link ScheduleFacade#TRIGGER_CRON}.
	 * 
	 * @return the cron expression of this schedule
	 */
	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * Sets the cron expression of this schedule.
	 * Only meaningful when result of {@link ScheduleFacade#getTriggerType()}
	 * is {@link ScheduleFacade#TRIGGER_CRON}.
	 * 
	 * @param cronExpression the cron expression of this schedule
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
}
