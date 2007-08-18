package com.luntsys.luntbuild.notifiers;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import org.apache.tools.ant.Project;

import java.util.List;
import java.util.Set;

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
 * Base class for all build notifiers.
 * @author robin shine
 */
public abstract class Notifier {

    /**
     * Gets the display name for this notifier.
     *
     * @return the display name for this notifier
     */
	public abstract String getDisplayName();

	/**
	 * Gets the display comment for this notifier.
	 * 
	 * @return comment the display comment
	 */
	public String getComment() {
		return "";
	}

	/**
	 * Notifies the specified list of users of the result of a build.
	 * 
	 * @param checkinUsers the list of users who have checked in code for this build
	 * @param subscribeUsers the list of users who are subscribed to receive notifications for this build
	 * @param build the build to notify about
	 * @param antProject the ant project used for logging purpose
	 * @see com.luntsys.luntbuild.db.User
	 */
	public abstract void sendBuildNotification(Set checkinUsers, Set subscribeUsers, Build build, Project antProject);

	/**
	 * Notifies the specified list of users of the status of the a schedule.
	 * 
	 * @param subscribeUsers the list of users who are subscribed to receive notifications for this schedule
	 * @param schedule the schedule to notify about
	 * @param antProject the ant project used for logging purpose
	 * @see com.luntsys.luntbuild.db.User
	 */
	public abstract void sendScheduleNotification(Set subscribeUsers, Schedule schedule, Project antProject);

	/**
	 * Gets the system level properties of this notifier.
	 * 
	 * @return the list of properties
	 * @see com.luntsys.luntbuild.utility.NotifierProperty
	 */
	public abstract List getSystemLevelProperties();

	/**
	 * Gets the user level properties of this notifier.
	 * 
	 * @return the list of properties
	 * @see com.luntsys.luntbuild.utility.NotifierProperty
	 */
	public abstract List getUserLevelProperties();
}
