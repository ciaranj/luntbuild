/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-27
 * Time: 6:22:18
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
package com.luntsys.luntbuild;

import com.luntsys.luntbuild.builders.Builder;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.listeners.Listener;
import com.luntsys.luntbuild.notifiers.Notifier;
import com.luntsys.luntbuild.remoting.Constants;
import com.luntsys.luntbuild.services.SchedService;
import com.luntsys.luntbuild.utility.*;
import com.luntsys.luntbuild.vcs.Vcs;
import com.luntsys.luntbuild.web.ManualBuildEditor;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.StatefulJob;

import java.io.*;
import java.util.*;

/**
 * This class performs the actual build process and will generate
 * new build or rebuild existing builds upon the trigger of a schedule
 *
 * @author robin shine
 */
public class BuildGenerator implements StatefulJob {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;
	public static final String REVISION_LOG = "revision_log.txt";
	public static final String BUILD_LOG = "build_log.txt";
	private static Log logger = LogFactory.getLog(BuildGenerator.class);

	/**
	 * Triggered by Quartz to execute actual building process
	 *
	 * @param context
	 * @throws JobExecutionException
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (context.getJobDetail().getName().equals(SchedService.DUMMY_JOB_NAME) ||
				context.getTrigger().getName().equals(SchedService.DUMMY_TRIGGER_NAME))
			return; // if this is only DUMMY job or trigger used to help other operations

		// create a ant antProject used to receive ant task logs
		org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
		antProject.init();
		Log4jBuildListener log4jBuildListener = new Log4jBuildListener();

		// re-direct ant logs to log4j logger
		antProject.addBuildListener(log4jBuildListener);
		Schedule schedule = null;
		Map properties = (Map) Luntbuild.getDao().loadProperties();
		int notifyStrategy = Constants.NOTIFY_IF_FAILED;
		Revisions revisions = new Revisions();
		try {
			String triggerGroup = context.getTrigger().getGroup();
			Build currentBuild = null;
			if (triggerGroup.equals(Scheduler.DEFAULT_GROUP)) { // build request triggered by schedules
				schedule = Luntbuild.getDao().loadSchedule(new Long(context.getTrigger().getName()).longValue());
				// Reload project to initialize lazy collection members
				schedule.setProject(Luntbuild.getDao().loadProject(schedule.getProject().getId()));

				if (schedule.getBuildStrategy() == Constants.BUILD_NONE) // schedule been paused
					return;

				schedule.validateAtBuildTime();
				notifyStrategy = schedule.getNotifyStrategy();
				// update status of this schedule
				schedule.setStatus(Constants.SCHEDULE_STATUS_RUNNING);
				schedule.setStatusDate(new Date());
				Luntbuild.getDao().saveSchedule(schedule);

				// determines if current build is necessary
				if (isBuildNecessary(schedule, antProject)) {
					revisions = getRevisions();
					currentBuild = new Build();
					currentBuild.setSchedule(schedule);
					currentBuild.setStartDate(new Date());

					// refresh project to keep project data as new as possible before save
					// in order to reduce the possibility of overriding already-changed project data
					// to the minimum
					com.luntsys.luntbuild.db.Project project = Luntbuild.getDao().loadProject(schedule.getProject().getId());
					if (project.getVersionVariableValues() == null)
						project.setVersionVariableValues(new HashMap());
					VersionHelper.updateVariableValues(project.getVersionVariableValues());
					VersionHelper versionHelper = new VersionHelper(project.getVersionVariableValues());
					currentBuild.setVersion(versionHelper.getVersionValue(project.getNextVersion()));
					project.setNextVersion(Luntbuild.increaseBuildVersion(project.getNextVersion()));
					Luntbuild.getDao().saveProject(project);

					currentBuild.setStatus(Constants.BUILD_STATUS_RUNNING);
					currentBuild.setRebuild(false);
					currentBuild.setPostbuildStrategy(schedule.getPostbuildStrategy());
					currentBuild.setLabelStrategy(schedule.getLabelStrategy());
					currentBuild.setVcsList(deriveBuildVcsList(schedule.getProject().getVcsList(), antProject));
					String workingDir = schedule.getProject().getWorkingDir(properties);
					if (!new File(workingDir).exists()) {
						currentBuild.setBuildType(Constants.BUILD_TYPE_CLEAN);
						Luntbuild.createDir(workingDir);
					} else
						currentBuild.setBuildType(schedule.getBuildType());
					logger.info("Perform a scheduled build...");
				} else
					logger.info("Build necessary condition not met, build not performed!");
			} else if (triggerGroup.equals(ManualBuildEditor.MANUALBUILD_GROUP)) { // triggered manually
				String triggerName = context.getTrigger().getName();
				String[] fields = triggerName.split("\\" + Luntbuild.TRIGGER_NAME_SEPERATOR);
				long scheduleId = new Long(fields[0]).longValue();
				schedule = Luntbuild.getDao().loadSchedule(scheduleId);
				// Reload project to initialize project lazy collection members
				schedule.setProject(Luntbuild.getDao().loadProject(schedule.getProject().getId()));

				schedule.validateAtBuildTime();
				notifyStrategy = new Integer(fields[4]).intValue();
				// update status of this schedule
				schedule.setStatus(Constants.SCHEDULE_STATUS_RUNNING);
				schedule.setStatusDate(new Date());
				Luntbuild.getDao().saveSchedule(schedule);

				Luntbuild.getSchedService().removeUnNecessaryManualBuilds(schedule);

				Build lastBuild = Luntbuild.getDao().loadLastBuild(schedule);
				BuildNecessaryConditionRoot.setInitiateProject(schedule.getProject());
				BuildNecessaryConditionRoot.setRevisions(null);
				BuildNecessaryConditionRoot.setBaseBuild(lastBuild);
				BuildNecessaryConditionRoot.setAntProject(antProject);

				revisions = getRevisions();
				currentBuild = new Build();
				currentBuild.setSchedule(schedule);
				currentBuild.setStartDate(new Date());
				currentBuild.setStatus(Constants.BUILD_STATUS_RUNNING);
				currentBuild.setRebuild(false);
				String workingDir = schedule.getProject().getWorkingDir(properties);
				if (!new File(workingDir).exists()) {
					currentBuild.setBuildType(Constants.BUILD_TYPE_CLEAN);
					Luntbuild.createDir(workingDir);
				} else
					currentBuild.setBuildType(new Integer(fields[1]).intValue());

				// reload project to keep project data as new as possible before save
				// in order to reduce the possibility of overriding already-changed project data
				// to the minimum
				com.luntsys.luntbuild.db.Project project = Luntbuild.getDao().loadProject(schedule.getProject().getId());
				if (project.getVersionVariableValues() == null)
					project.setVersionVariableValues(new HashMap());
				VersionHelper.updateVariableValues(project.getVersionVariableValues());
				VersionHelper versionHelper = new VersionHelper(project.getVersionVariableValues());
				if (!fields[2].equals(""))
					currentBuild.setVersion(versionHelper.getVersionValue(fields[2]));
				else {
					currentBuild.setVersion(versionHelper.getVersionValue(project.getNextVersion()));
					project.setNextVersion(Luntbuild.increaseBuildVersion(project.getNextVersion()));
				}
				Luntbuild.getDao().saveProject(project);

				currentBuild.setLabelStrategy(new Integer(fields[3]).intValue());
				currentBuild.setPostbuildStrategy(new Integer(fields[5]).intValue());
				currentBuild.setVcsList(deriveBuildVcsList(schedule.getProject().getVcsList(), antProject));
				logger.info("Perform manual build...");
			} else { // triggered by rebuild
				revisions.getChangeLogs().add("========== Change log ignored: rebuild performed ==========");
				String triggerName = context.getTrigger().getName();
				String[] fields = triggerName.split("\\" + Luntbuild.TRIGGER_NAME_SEPERATOR);
				long buildId = new Long(fields[0]).longValue();
				currentBuild = Luntbuild.getDao().loadBuild(buildId);
				schedule = currentBuild.getSchedule();
				// reload project to initialize project lazy members
				schedule.setProject(Luntbuild.getDao().loadProject(schedule.getProject().getId()));
				schedule.validateAtBuildTime();
				notifyStrategy = new Integer(fields[1]).intValue();
				currentBuild.setPostbuildStrategy(new Integer(fields[2]).intValue());
				// update status of this schedule
				schedule.setStatus(Constants.SCHEDULE_STATUS_RUNNING);
				schedule.setStatusDate(new Date());
				Luntbuild.getDao().saveSchedule(schedule);

				String workingDir = schedule.getProject().getWorkingDir(properties);
				// create working directory if it does not exist
				Luntbuild.createDir(workingDir);

				if (!currentBuild.isHaveLabelOnHead())
					throw new IllegalStateException("ERROR: this build should not be " +
							"rebuildable because no label is created for this build when it is initially built");
				currentBuild.setStatus(Constants.BUILD_STATUS_RUNNING);
				currentBuild.setRebuild(true);
				currentBuild.setStartDate(new Date());
				currentBuild.setEndDate(null);
				logger.info("Perform rebuild...");
			}

			if (currentBuild != null) {
				Build lastBuild = Luntbuild.getDao().loadLastBuild(schedule);
				// save build to database
				Luntbuild.getDao().saveBuild(currentBuild);
				writeRevisionLog(properties, currentBuild, revisions);
				List listenerInstances = Luntbuild.getListenerInstances(Luntbuild.listeners);
				Iterator it = listenerInstances.iterator();
				while (it.hasNext()) {
					Listener listener = (Listener) it.next();
					try {
						listener.buildStarted(currentBuild);
					} catch (Throwable throwable) {
						logger.error("Error calling build listener", throwable);
					}
				}

				checkoutAndBuild(properties, currentBuild);

				Luntbuild.getDao().saveBuild(currentBuild);
				it = listenerInstances.iterator();
				while (it.hasNext()) {
					Listener listener = (Listener) it.next();
					try {
						listener.buildFinished(currentBuild);
					} catch (Throwable throwable) {
						logger.error("Error calling build listener", throwable);
					}
				}

				// send notifications about current build
				if (notifyStrategy == Constants.NOTIFY_ALWAYS) {
					sendBuildNotification(properties, currentBuild, revisions.getChangeLogins(), antProject);
				} else if (notifyStrategy == Constants.NOTIFY_IF_SUCCESS) {
					if (currentBuild.getStatus() == Constants.BUILD_STATUS_SUCCESS)
						sendBuildNotification(properties, currentBuild, revisions.getChangeLogins(), antProject);
				} else if (notifyStrategy == Constants.NOTIFY_IF_FAILED) {
					if (currentBuild.getStatus() == Constants.BUILD_STATUS_FAILED)
						sendBuildNotification(properties, currentBuild, revisions.getChangeLogins(), antProject);
				} else if (notifyStrategy == Constants.NOTIFY_WHEN_STATUS_CHANGED) {
					if (lastBuild == null || currentBuild.getStatus() != lastBuild.getStatus()) // notify when build status changes
						sendBuildNotification(properties, currentBuild, revisions.getChangeLogins(), antProject);
				}
			}

			// reload schedule to keep schedule data as new as possible before save
			schedule = Luntbuild.getDao().loadSchedule(schedule.getId());
			schedule.setStatus(Constants.SCHEDULE_STATUS_SUCCESS);
			schedule.setStatusDate(new Date());
			Luntbuild.getDao().saveSchedule(schedule);
		} catch (Throwable throwable) {
			logger.error("Exception catched during job execution", throwable);
			if (schedule != null) {
				// reload schedule to keep schedule data as new as possible before save
				schedule = Luntbuild.getDao().loadSchedule(schedule.getId());
				schedule.setProject(Luntbuild.getDao().loadProject(schedule.getProject().getId()));
				schedule.setStatus(Constants.SCHEDULE_STATUS_FAILED);
				schedule.setStatusDate(new Date());
				Luntbuild.getDao().saveSchedule(schedule);
				if (notifyStrategy != Constants.NOTIFY_NONE)
					sendScheduleNotification(properties, schedule, antProject);
			}
		}
	}

	/**
	 * Write revision log for specified build
	 *
	 * @param properties
	 * @param build
	 * @param revisions
	 */
	private void writeRevisionLog(Map properties, Build build, Revisions revisions) {
		String publishDir = build.getPublishDir(properties);
		String revisionLogPath = publishDir + File.separator + BuildGenerator.REVISION_LOG;
		PrintStream revisionLogStream = null;
		try {
			Luntbuild.createDir(publishDir);
			revisionLogStream = new PrintStream(new FileOutputStream(revisionLogPath));
			Iterator it = revisions.getChangeLogs().iterator();
			while (it.hasNext()) {
				String line = (String) it.next();
				revisionLogStream.println(line);
			}
			revisionLogStream.close();
			revisionLogStream = null;
		} catch (IOException e) {
			logger.error("Error while writing revision log!", e);
		} finally {
			if (revisionLogStream != null)
				revisionLogStream.close();
		}
	}

	/**
	 * Derive the build vcs list from specified vcs list
	 *
	 * @param vcsList
	 * @param antProject
	 * @return
	 */
	private List deriveBuildVcsList(List vcsList, Project antProject) {
		List buildVcsList = new ArrayList();
		Iterator it = vcsList.iterator();
		while (it.hasNext()) {
			Vcs vcs = (Vcs) it.next();
			buildVcsList.add(vcs.deriveBuildVcs(antProject));
		}
		return buildVcsList;
	}

	private Revisions getRevisions() {
		Revisions revisions = BuildNecessaryConditionRoot.getRevisions();
		if (revisions != null)
			return revisions;
		// call this method to get revisions for current project
		new BuildNecessaryConditionRoot().isModified();
		revisions = BuildNecessaryConditionRoot.getRevisions();
		if (revisions == null)
			throw new IllegalStateException("Revisions should not be null at this point!");
		return revisions;
	}

	private boolean isBuildNecessary(Schedule schedule, Project antProject) {
		Build lastBuild = Luntbuild.getDao().loadLastBuild(schedule);
		BuildNecessaryConditionRoot.setRevisions(null);
		BuildNecessaryConditionRoot.setBaseBuild(lastBuild);
		BuildNecessaryConditionRoot.setInitiateProject(schedule.getProject());
		BuildNecessaryConditionRoot.setAntProject(antProject);

		if (schedule.getBuildStrategy() == Constants.BUILD_ALWAYS)
			return true;
		if (schedule.getBuildStrategy() == Constants.BUILD_ALWAYS_IF_FAILED &&
				(lastBuild == null || lastBuild.getStatus() == Constants.BUILD_STATUS_FAILED))
			return true;

		BuildNecessaryConditionRoot ognlRoot = new BuildNecessaryConditionRoot();
		String buildNecessaryCondition;
		if (!Luntbuild.isEmpty(schedule.getProject().getBuildNecessaryCondition()))
			buildNecessaryCondition = schedule.getProject().getBuildNecessaryCondition();
		else
			buildNecessaryCondition = "modified";
		try {
			Boolean buildNecessaryValue = (Boolean) Ognl.getValue(Ognl.parseExpression(buildNecessaryCondition),
					Ognl.createDefaultContext(ognlRoot), ognlRoot, Boolean.class);
			if (buildNecessaryValue == null)
				return false;
			else
				return buildNecessaryValue.booleanValue();
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		} catch (OgnlException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Send notification about specified schedule
	 *
	 * @param properties luntbuild system level properties
	 * @param schedule   schedule to notify about
	 * @param antProject ant project used for logging purpose
	 */
	private void sendScheduleNotification(Map properties, Schedule schedule, Project antProject) {
		Set subscribeUsers = new HashSet();
		if (schedule.getProject().getNotificationConfigs() != null) {
			Iterator it = schedule.getProject().getNotificationConfigs().keySet().iterator();
			while (it.hasNext()) {
				User user = (User) it.next();
				if (user.getId() != User.USER_CHECKIN_ID)
					subscribeUsers.add(user);
			}
		}

		if (schedule.getProject().getNotifiers() != null) {
			Iterator it = Luntbuild.getNotifierInstances(Luntbuild.getNotifierClasses(schedule.getProject().
					getNotifiers())).iterator();
			while (it.hasNext()) {
				Notifier notifier = (Notifier) it.next();
				notifier.sendScheduleNotification(subscribeUsers, schedule, properties, antProject);
			}
		}
	}

	/**
	 * Send notification about specified build
	 *
	 * @param properties    luntbuild system level properties
	 * @param build         build to notify about
	 * @param checkinLogins logins checkin recently
	 * @param antProject    ant project used for logging purpose
	 */
	private void sendBuildNotification(Map properties, Build build, Set checkinLogins,
									   Project antProject) {
		com.luntsys.luntbuild.db.Project project = build.getSchedule().getProject();
		User virtualCheckinUser = new User();
		virtualCheckinUser.setId(User.USER_CHECKIN_ID);

		Set checkinUsers = new HashSet();
		if (project.getNotificationConfigs() != null && project.getNotificationConfigs().keySet().
				contains(virtualCheckinUser)) {
			List allUsers = Luntbuild.getDao().loadUsers();
			Iterator it = checkinLogins.iterator();
			while (it.hasNext()) {
				String checkinLogin = (String) it.next();
				User checkinUser = project.getUserByVcsLogin(checkinLogin, allUsers);
				if (checkinUser == null)
					throw new BuildException("ERROR: Failed to find luntbuild user for vcs login \"" +
							checkinLogin + "\" of project \"" + project.getName() + "\"!");
				checkinUsers.add(checkinUser);
			}
		}

		Set subscribeUsers = new HashSet();
		if (project.getNotificationConfigs() != null) {
			Iterator it = project.getNotificationConfigs().keySet().iterator();
			while (it.hasNext()) {
				User user = (User) it.next();
				if (user.getId() != User.USER_CHECKIN_ID && !checkinUsers.contains(user))
					subscribeUsers.add(user);
			}
		}

		if (project.getNotifiers() != null) {
			Iterator it = Luntbuild.getNotifierInstances(Luntbuild.getNotifierClasses(project.getNotifiers())).iterator();
			while (it.hasNext()) {
				Notifier notifier = (Notifier) it.next();
				notifier.sendBuildNotification(checkinUsers, subscribeUsers, build, properties,
						antProject);
			}
		}
	}

	/**
	 * checkout and build based on specified build object
	 *
	 * @param properties
	 * @param build
	 */
	private void checkoutAndBuild(Map properties, Build build) {
		String publishDirPath = build.getPublishDir(properties);
		String buildLogPath = publishDirPath + File.separator + BuildGenerator.BUILD_LOG;

		// create a ant antProject used to receive ant task logs
		Project antProject = new org.apache.tools.ant.Project();
		antProject.init();
		LuntbuildLogger buildLogger = new LuntbuildLogger();
		buildLogger.setDirectMode(false);
		buildLogger.setEmacsMode(false);
		buildLogger.setMessageOutputLevel(Luntbuild.convertToAntLogLevel(build.getSchedule().getProject().getLogLevel()));
		antProject.addBuildListener(buildLogger);
		PrintStream logStream = null;
		boolean isCheckoutSuccess = false;
		long currentTime = System.currentTimeMillis();
		try {
			logStream = new PrintStream(new FileOutputStream(buildLogPath));
			buildLogger.setOutputPrintStream(logStream);
			buildLogger.setErrorPrintStream(logStream);
			if (build.isCleanBuild() || build.isRebuild()) {
				antProject.log("Cleanup project working directory...", Project.MSG_INFO);
				logger.info("Cleanup project working directory...");
				Iterator it = build.getVcsList().iterator();
				while (it.hasNext()) {
					Vcs vcs = (Vcs) it.next();
					vcs.cleanupCheckout(properties, build, antProject);
				}
			}

			logger.info("Check out codes from defined version control systems...");
			Iterator it = build.getVcsList().iterator();
			while (it.hasNext()) {
				Vcs vcs = (Vcs) it.next();
				antProject.log("Perform checkout operation on vcs setting: ", Project.MSG_INFO);
				antProject.log(vcs.summarize(), Project.MSG_INFO);
				vcs.checkout(properties, build, antProject);
			}
			isCheckoutSuccess = true;
			antProject.log("Time cost for checkout operation: " + (System.currentTimeMillis() - currentTime) / 60000 + " minutes",
					org.apache.tools.ant.Project.MSG_INFO);
			currentTime = System.currentTimeMillis();

			Luntbuild.createDir(publishDirPath + File.separator + Builder.ARTIFACTS_DIR);
			logger.info("Build with defined builders...");
			it = build.getSchedule().getProject().getBuilderList().iterator();
			while (it.hasNext()) {
				Builder builder = (Builder) it.next();
				try {
					builder.build(properties, build);
				} finally {
					appendBuilderLog(logStream, new File(publishDirPath + File.separator + Builder.BUILDER_LOG));
				}
			}

			antProject.log("Time cost for builders execution: " + (System.currentTimeMillis() - currentTime) / 60000 + " minutes",
					org.apache.tools.ant.Project.MSG_INFO);
			build.setStatus(Constants.BUILD_STATUS_SUCCESS);
		} catch (Throwable e) {
			build.setStatus(Constants.BUILD_STATUS_FAILED);
			if (e instanceof BuildException) {
				antProject.log(e.getMessage(), Project.MSG_ERR);
				logger.error("Build failed: " + e.getMessage());
			} else {
				if (logStream != null)
					e.printStackTrace(logStream);
				logger.error("Build failed: ", e);
			}
		}

		if (build.getPostbuildStrategy() == Constants.POSTBUILD_ALWAYS ||
				build.getPostbuildStrategy() == Constants.POSTBUILD_IF_SUCCESS && build.getStatus() == Constants.BUILD_STATUS_SUCCESS ||
				build.getPostbuildStrategy() == Constants.POSTBUILD_IF_FAILED && build.getStatus() == Constants.BUILD_STATUS_FAILED) {
			try {
				antProject.log("");
				if (build.getSchedule().getProject().getPostbuilderList().size() == 0)
					throw new BuildException("ERROR: No post-builders defined for project \"" + build.getSchedule().getProject().getName() + "\"");

				logger.info("Post-build with defined post-builders...");
				Iterator it = build.getSchedule().getProject().getPostbuilderList().iterator();
				while (it.hasNext()) {
					Builder builder = (Builder) it.next();
					try {
						builder.build(properties, build);
					} finally {
						appendBuilderLog(logStream, new File(publishDirPath + File.separator + Builder.BUILDER_LOG));
					}
				}

				antProject.log("Time cost for post-builders execution: " + (System.currentTimeMillis() - currentTime) / 60000 + " minutes",
						Project.MSG_INFO);
				logger.info("Run of ant post-build script succeed!");
			} catch (Throwable throwable) {
				build.setStatus(Constants.BUILD_STATUS_FAILED);
				if (throwable instanceof BuildException) {
					antProject.log(throwable.getMessage(), org.apache.tools.ant.Project.MSG_ERR);
					logger.error("Post-build failed: " + throwable.getMessage());
				} else {
					if (logStream != null)
						throwable.printStackTrace(logStream);
					logger.error("Post-build failed: ", throwable);
				}
			}
		}

		try {
			if (isCheckoutSuccess) {
				// insert a blank line into the  log
				antProject.log("\n", org.apache.tools.ant.Project.MSG_INFO);
				if (!build.isRebuild()) {
					if (build.getLabelStrategy() == Constants.LABEL_ALWAYS ||
							build.getLabelStrategy() == Constants.LABEL_IF_SUCCESS &&
							build.getStatus() == Constants.BUILD_STATUS_SUCCESS) {
						build.setHaveLabelOnHead(true);
						Iterator it = build.getVcsList().iterator();
						while (it.hasNext()) {
							Vcs vcs = (Vcs) it.next();
							vcs.label(properties, build, antProject);
						}
					} else {
						Iterator it = build.getVcsList().iterator();
						while (it.hasNext()) {
							Vcs vcs = (Vcs) it.next();
							vcs.unlabel(properties, build, antProject);
						}
					}
				} else {
					antProject.log("Cleanup project working directory after rebuild...", Project.MSG_INFO);
					Iterator it = build.getVcsList().iterator();
					while (it.hasNext()) {
						Vcs vcs = (Vcs) it.next();
						vcs.cleanupCheckout(properties, build, antProject);
					}
					// delete the working directory to force a clean build next time
					Luntbuild.deleteDir(build.getSchedule().getProject().getWorkingDir(properties));
				}
			}
		} catch (Throwable e) {
			build.setStatus(Constants.BUILD_STATUS_FAILED);
			if (e instanceof BuildException) {
				antProject.log(e.getMessage(), org.apache.tools.ant.Project.MSG_ERR);
				logger.error("Build failed: " + e.getMessage());
			} else {
				if (logStream != null)
					e.printStackTrace(logStream);
				logger.error("Build failed: ", e);
			}
		} finally {
			if (logStream != null) {
				logStream.close();
				buildLogger.setOutputPrintStream(null);
				buildLogger.setErrorPrintStream(null);
			}
			build.setEndDate(new Date());
		}
	}

	/**
	 * Append contents of specified builderLogFile to logStream
	 *
	 * @param logStream
	 * @param builderLogFile
	 * @throws IOException
	 */
	private void appendBuilderLog(PrintStream logStream, File builderLogFile) throws IOException {
		// append build log with log generated by current builders
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(builderLogFile));
			String line;
			while ((line = reader.readLine()) != null) {
				logStream.println(line);
			}
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	/**
	 * This class receives ant logs and re-directs to log4j loggers
	 */
	private class Log4jBuildListener extends EmptyBuildListenerImpl {
		public void messageLogged(BuildEvent event) {
			String prefix = "";
			if (event.getTask() != null)
				prefix += "        [" + event.getTask().getTaskName() + "]";
			if (event.getPriority() == Project.MSG_ERR)
				logger.error(prefix + event.getMessage());
			else if (event.getPriority() == Project.MSG_WARN)
				logger.warn(prefix + event.getMessage());
			else if (event.getPriority() == Project.MSG_INFO)
				logger.info(prefix + event.getMessage());
			else
				logger.debug(prefix + event.getMessage());
		}
	}
}
