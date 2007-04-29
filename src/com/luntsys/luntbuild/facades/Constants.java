/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-12-17
 * Time: 16:25:02
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
package com.luntsys.luntbuild.facades;

/**
 * Commonly used constant values
 *
 * @author robin shine
 */
public class Constants {
	/**
	 * Trigger type constants
	 */
	public static final int NUM_TRIGGER_TYPE = 3;

	public static final int TRIGGER_TYPE_MANUAL = 0;
	public static final int TRIGGER_TYPE_SIMPLE = 1;
	public static final int TRIGGER_TYPE_CRON = 2;

	/**
	 * Schedule status constants
	 */
	public static final int NUM_SCHEDULE_STATUS = 4;

	public static final int SCHEDULE_STATUS_CREATED = 0;
	public static final int SCHEDULE_STATUS_SUCCESS = 1;
	public static final int SCHEDULE_STATUS_FAILED = 2;
	public static final int SCHEDULE_STATUS_RUNNING = 3;

	/**
	 * Build status constants
	 */
	public static final int NUM_BUILD_STATUS = 4;

	public static final int BUILD_STATUS_ALL = 0;
	public static final int BUILD_STATUS_SUCCESS = 1;
	public static final int BUILD_STATUS_FAILED = 2;
	public static final int BUILD_STATUS_RUNNING = 3;

	/**
	 * Build type constants
	 */
	public static final int NUM_BUILD_TYPE = 2;

	public static final int BUILD_TYPE_CLEAN = 0;
	public static final int BUILD_TYPE_INCREMENT = 1;

	/**
	 * Build strategy constants
	 */
	public static final int NUM_BUILD_STRATEGY = 4;

	/**
	 * Label strategy constants
	 */
	public static final int NUM_LABEL_STRATEGY = 3;

	public static final int LABEL_IF_SUCCESS = 0;
	public static final int LABEL_ALWAYS = 1;
	public static final int LABEL_NONE = 2;

	/**
	 * Notify strategy constants
	 */
	public static final int NUM_NOTIFY_STRATEGY = 6;

	public static final int NOTIFY_WHEN_STATUS_CHANGED = 0;
	public static final int NOTIFY_IF_SUCCESS = 1;
	public static final int NOTIFY_IF_FAILED = 2;
	public static final int NOTIFY_ALWAYS = 3;
	public static final int NOTIFY_NONE = 4;
	public static final int NOTIFY_IF_FAILED_OR_CHANGED = 5;

	/**
	 * Trigger dependents strategy
	 */
	public static final int NUM_TRIGGER_DEPENDENCY_STRATEGY = 4;

	public static final int TRIGGER_SCHEDULES_THIS_DEPENDS_ON = 0;
	public static final int TRIGGER_SCHEDULES_DEPENDS_ON_THIS = 1;
	public static final int TRIGGER_ALL_DEPENDENT_SCHEDULES = 2;
	public static final int TRIGGER_NONE_DEPENDENT_SCHEDULES = 3;

	/**
	 * Builds cleanup strategy
	 */
	public static final int NUM_BUILD_CLEANUP_STRATEGY = 3;

	public static final int BUILD_CLEANUP_NONE = 0;
	public static final int BUILD_KEEP_BY_DAYS = 1;
	public static final int BUILD_KEEP_BY_COUNT = 2;

	/**
	 * Postbuild strategy constants
	 */
	public static final int NUM_POSTBUILD_STRATEGY = 4;

	public static final int POSTBUILD_NONE = 0;
	public static final int POSTBUILD_IF_SUCCESS = 1;
	public static final int POSTBUILD_IF_FAILED = 2;
	public static final int POSTBUILD_ALWAYS = 3;

	/**
	 * Build timing constants
	 */
	public static final int NUM_BUILD_TIMING = 3;

	public static final int BUILD_TIMING_NOW = 0;
	public static final int BUILD_TIMING_AFTER = 1;
	public static final int BUILD_TIMING_AT = 2;

	/**
	 * Log level constants
	 */
	public static final int NUM_LOG_LEVEL = 3;

	public static final int LOG_LEVEL_BRIEF = 0;
	public static final int LOG_LEVEL_NORMAL = 1;
	public static final int LOG_LEVEL_VERBOSE = 2;

    /**
     * Jabber Server Type
     */
    public static final int NUM_JABBER_SERVER_TYPE = 3;

    public static final int JABBER_SERVER_TYPE_NORMAL = 0;
    public static final int JABBER_SERVER_TYPE_SSL = 1;
    public static final int JABBER_SERVER_TYPE_GOOGLE = 2;

	/**
	 * System level property name constants
	 */
	public static final String SERVLET_URL = "servletUrl";
	public static final String WORKING_DIR = "workingDir";
	public static final String PUBLISH_DIR = "publishDir";
	public static final String PAGE_REFRESH_INTERVAL = "pageRefreshInterval";
	public static final String BUILD_THREAD_COUNT = "buildThreadCount";
	public static final String BACKUP_FILE_NAME = "backupFileName";
	public static final String BACKUP_CRON_EXPRESSION = "backupCronExpression";

	/**
	 * Special build version constants
	 */
	public static final String LAST_BUILD = "last";
	public static final String LAST_SUCCESS_BUILD = "last_success";

	public static String getBuildTypeText(int buildType) {
		if (buildType == BUILD_TYPE_CLEAN)
			return "clean";
		else if (buildType == BUILD_TYPE_INCREMENT)
			return "increment";
		else
			return "";
	}

	public static String getPostbuildStrategyText(int postbuildStrategy) {
		if (postbuildStrategy == POSTBUILD_ALWAYS)
			return "post-build always";
		else if (postbuildStrategy == POSTBUILD_NONE)
			return "do not post-build";
		else if (postbuildStrategy == POSTBUILD_IF_FAILED)
			return "post-build if failed";
		else if (postbuildStrategy == POSTBUILD_IF_SUCCESS)
			return "post-build if success";
		else
			return "";
	}

	public static String getLabelStrategyText(int labelStrategy) {
		if (labelStrategy == LABEL_ALWAYS)
			return "label always";
		else if (labelStrategy == LABEL_NONE)
			return "do not label";
		else if (labelStrategy == LABEL_IF_SUCCESS)
			return "label if success";
		else
			return "";
	}

	public static String getNotifyStrategyText(int notifyStrategy) {
		if (notifyStrategy == NOTIFY_ALWAYS)
			return "notify always";
		else if (notifyStrategy == NOTIFY_IF_FAILED)
			return "notify when failed";
		else if (notifyStrategy == NOTIFY_IF_SUCCESS)
			return "notify when success";
		else if (notifyStrategy == NOTIFY_NONE)
			return "do not notify";
		else if (notifyStrategy == NOTIFY_WHEN_STATUS_CHANGED)
			return "notify when status changed";
		else if (notifyStrategy == NOTIFY_IF_FAILED_OR_CHANGED)
			return "notify when failed or status changed";
		else
			return "";
	}

	public static String getTriggerDependencyStrategyText(int triggerDependencyStrategy) {
		if (triggerDependencyStrategy == TRIGGER_ALL_DEPENDENT_SCHEDULES)
			return "trigger all dependent schedules";
		else if (triggerDependencyStrategy == TRIGGER_NONE_DEPENDENT_SCHEDULES)
			return "do not trigger any dependent schedules";
		else if (triggerDependencyStrategy == TRIGGER_SCHEDULES_DEPENDS_ON_THIS)
			return "trigger schedules that depends on this schedule";
		else if (triggerDependencyStrategy == TRIGGER_SCHEDULES_THIS_DEPENDS_ON)
			return "trigger schedules this schedule depends on";
		else
			return "";
	}

	public static String getTriggerTypeText(int triggerType) {
		if (triggerType == TRIGGER_TYPE_CRON)
			return "cron";
		else if (triggerType == TRIGGER_TYPE_MANUAL)
			return "manual";
		else if (triggerType == TRIGGER_TYPE_SIMPLE)
			return "simple";
		else
			return "";
	}

	public static String getBuildStatusText(int buildStatus) {
		if (buildStatus == BUILD_STATUS_FAILED)
			return "failed";
		else if (buildStatus == BUILD_STATUS_RUNNING)
			return "running";
		else if (buildStatus == BUILD_STATUS_SUCCESS)
			return "success";
		else if (buildStatus == BUILD_STATUS_ALL)
			return "all status";
		else
			return "";
	}

	public static String getScheduleStatusText(int scheduleStatus) {
		if (scheduleStatus == SCHEDULE_STATUS_FAILED)
			return "failed";
		else if (scheduleStatus == SCHEDULE_STATUS_RUNNING)
			return "running";
		else if (scheduleStatus == SCHEDULE_STATUS_SUCCESS)
			return "success";
		else if (scheduleStatus == SCHEDULE_STATUS_CREATED)
			return "created";
		else
			return "";
	}

	public static String getBuildTimingText(int buildTiming) {
		if (buildTiming == BUILD_TIMING_AFTER)
			return "after";
		else if (buildTiming == BUILD_TIMING_AT)
			return "at";
		else if (buildTiming == BUILD_TIMING_NOW)
			return "now";
		else
			return "";
	}

	public static String getLogLevelText(int logLevel) {
		if (logLevel == LOG_LEVEL_BRIEF)
			return "brief";
		else if (logLevel == LOG_LEVEL_NORMAL)
			return "normal";
		else if (logLevel == LOG_LEVEL_VERBOSE)
			return "verbose";
		else
			return "";
	}

	public static String getBuildCleanupStrategyText(int buildCleanupStrategy) {
		if (buildCleanupStrategy == BUILD_CLEANUP_NONE)
			return "do not cleanup builds automatically";
		else if (buildCleanupStrategy == BUILD_KEEP_BY_DAYS)
			return "keep builds by days";
		else if (buildCleanupStrategy == BUILD_KEEP_BY_COUNT)
			return "keep builds by count";
		else
			return "";
	}

    /** Return Jabber server type string.
     * @param type server type
     * @return Jabber server type string
     */
    public static String getJabberServerType(int type) {
        if (type == JABBER_SERVER_TYPE_NORMAL)
            return "normal";
        else if (type == JABBER_SERVER_TYPE_SSL)
            return "ssl";
        else if (type == JABBER_SERVER_TYPE_GOOGLE)
            return "google";
        else
            return "";
    }

    /** Return Jabber server type int.
     * @param type server type
     * @return Jabber server type int
     */
    public static int getJabberServerTypeValue(String type) {
        if (type.equalsIgnoreCase("normal"))
            return JABBER_SERVER_TYPE_NORMAL;
        else if (type.equalsIgnoreCase("ssl"))
            return JABBER_SERVER_TYPE_SSL;
        else if (type.equalsIgnoreCase("google"))
            return JABBER_SERVER_TYPE_GOOGLE;
        else
            return JABBER_SERVER_TYPE_NORMAL;
    }

    /**
     * @return array of jabber server types
     */
    public static String[] getJabberServerTypes() {
        return new String[] {"normal", "ssl", "google"};
    }

}