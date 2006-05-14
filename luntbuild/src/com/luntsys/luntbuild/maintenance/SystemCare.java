/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-3-19
 * Time: 20:23:25
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
package com.luntsys.luntbuild.maintenance;

import org.quartz.StatefulJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.Constants;

import java.util.List;
import java.util.Iterator;
import java.util.Date;

/**
 * System maintenance class runs every mid-night
 * @author robin shine
 */
public class SystemCare implements StatefulJob {
	public static final String JOB_NAME = "system care";
	public static final String JOB_GROUP = "system care";
	public static final String TRIGGER_NAME = "system care";
	public static final String TRIGGER_GROUP = "system care";

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		List schedules = Luntbuild.getDao().loadSchedules();
		Iterator it = schedules.iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			if (schedule.getBuildCleanupStrategy() == com.luntsys.luntbuild.facades.Constants.BUILD_KEEP_BY_DAYS) {
				int reserveDays = new Integer(schedule.getBuildCleanupStrategyData()).intValue();
				Luntbuild.getDao().removeBuildsBefore(schedule, new Date(System.currentTimeMillis() - reserveDays*24*3600*1000));
			}
		}
	}
}
