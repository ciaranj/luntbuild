package com.luntsys.luntbuild.notifiers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.Project;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.BuildSchedule;
import com.luntsys.luntbuild.utility.NotifierProperty;

/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-26
 * Time: 12:56:19
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

/**
 * Build notifier abstract class.
 * @author robin shine
 */
public abstract class Notifier {
	public abstract String getDisplayName();

	public String getComment() {
		return "";
	}

	/**
	 * Notify specified list of users of status of current build
	 * @param checkinUsers list of {@link com.luntsys.luntbuild.db.User},
	 * denotes who are checked in for this build
	 * @param subscribeUsers list of {@link com.luntsys.luntbuild.db.User},
	 * denotes who are subscribed to receive notifications of this build
	 * @param build build to notify about
	 * @param properties luntbuild system level properties
	 * @param antProject ant project used for logging purpose
	 */
	public abstract void sendBuildNotification(Set checkinUsers, Set subscribeUsers, Build build,
							   Map properties, Project antProject);

	/**
	 * Notify specified list of users of status of current build schedule
	 * @param subscribeUsers list of {@link com.luntsys.luntbuild.db.User}, denotes
	 * who are subscribed to receive notifications of this build schedule
	 * @param buildSchedule build schedule to notify about
	 * @param properties luntbuild system level properties
	 * @param antProject ant project used for logging purpose
	 */
	public abstract void sendBuildScheduleNotification(Set subscribeUsers, BuildSchedule buildSchedule,
									   Map properties, Project antProject);

	/**
	 * Get system level properties of current notifier
	 * @return list of {@link com.luntsys.luntbuild.utility.DisplayProperty}
	 */
	public abstract List getSystemLevelProperties();

	/**
	 * Get user level properties of current notifier
	 * @return
	 */
	public abstract NotifierProperty getUserLevelProperty();
}