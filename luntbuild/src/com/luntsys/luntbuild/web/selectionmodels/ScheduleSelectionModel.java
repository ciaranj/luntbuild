/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-17
 * Time: 16:11:45
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
package com.luntsys.luntbuild.web.selectionmodels;

import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.form.IPropertySelectionModel;

import java.util.Iterator;
import java.util.List;

import com.luntsys.luntbuild.db.Schedule;

/**
 * The selection model for schedules configured in the system, 
 *
 * @author robin shine
 */
public class ScheduleSelectionModel implements IPropertySelectionModel{
	private List schedules;

	public ScheduleSelectionModel(List schedules){
		this.schedules = schedules;
	}

	public int getOptionCount() {
		return schedules.size();
	}

	public Object getOption(int index) {
		return schedules.get(index);
	}

	public String getLabel(int index) {
		return ((Schedule)getOption(index)).getName();
	}

	public String getValue(int index) {
		return String.valueOf(((Schedule)getOption(index)).getId());
	}

	public Object translateValue(String value) {
		long scheduleId = new Integer(value).longValue();
		Iterator itSchedule = schedules.iterator();
	 	while (itSchedule.hasNext()) {
			 Schedule schedule = (Schedule) itSchedule.next();
			 if (schedule.getId() == scheduleId){
				 return schedule;
			 }
		 }
		throw new ApplicationRuntimeException("Selected schedule has been removed already, " +
				"please select another schedule for try!");
	}
}
