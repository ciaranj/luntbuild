/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-18
 * Time: 16:31:17
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
package com.luntsys.luntbuild.services;

import com.luntsys.luntbuild.db.Schedule;
import org.quartz.Trigger;

import java.util.List;
import java.util.Map;

/**
 * The scheduler interface for luntbuild system
 *
 * @author robin shine
 */
public interface IScheduler {
	/**
	 * reschedule builds based on build scheduling information existed in the persistent storage
	 */
	void rescheduleBuilds();

	/**
	 * Schedule a build for specified schedule using specified trigger
	 *
	 * @param schedule
	 * @param trigger
	 */
	void scheduleBuild(Schedule schedule, Trigger trigger);

	/**
	 * Remove specified trigger
	 *
	 * @param triggerName
	 * @param triggerGroup
	 */
	void unscheduleBuild(String triggerName, String triggerGroup);

	/**
	 * Get list of triggers manually triggered for specified schedule, the list is ordered increasely
	 * by start time of the trigger
	 *
	 * @param schedule
	 * @return
	 */
	List getWaitingManualBuildTriggers(Schedule schedule);

	/**
	 * Get rebuilds for specified schedule, rebuilds are a map from trigger to Build
	 * It is ordered by trigger start time
	 *
	 * @param schedule
	 * @return
	 */
	Map getRebuilds(Schedule schedule);

	/**
	 * Initialize and startup the scheduler
	 */
	void startup();

	/**
	 * Shutdown the scheduler.
	 */
	void shutdown();

	void validateTrigger(Trigger trigger);

	/**
	 * Removes un-necessary manual builds in a schedule. Un-necessary manual
	 * builds are defined as builds that scheduled to occur before time of this function is
	 * called
	 * @param schedule
	 */
	void removeUnNecessaryManualTriggers(Schedule schedule);

	boolean isTriggerAvailable(Trigger trigger);

	void scheduleSystemBackup();

	void scheduleSystemCare();
}
