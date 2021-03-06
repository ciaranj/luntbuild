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

import com.luntsys.luntbuild.BuildGenerator;
import com.luntsys.luntbuild.facades.BuildParams;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.maintenance.SystemCare;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.TriggerStartTimeComparator;
import com.luntsys.luntbuild.utility.ValidationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.*;

/**
 * This class provides quartz scheduler access service. It will loaded by
 * spring framework as a singleton
 *
 * @author robin shine
 */
public class QuartzService implements IScheduler {
	private static Log logger = LogFactory.getLog(QuartzService.class);
	private Object schedLock = new Object();
	/**
	 * The quartz {@link org.quartz.Scheduler} object which will be initialized and destroyed respectively
	 * in servlet init and destroy
	 */
	private Scheduler sched = null;

	public static final String DUMMY_TRIGGER_NAME = "dummy";
	public static final String DUMMY_JOB_NAME = "dummy";

	/**
	 * reschedule builds based on build scheduling information existed in the persistent storage
	 */
	public void rescheduleBuilds() {
		validateSched();
		synchronized (schedLock) {
			try {
				String[] names = sched.getTriggerNames(Scheduler.DEFAULT_GROUP);
				for (int i = 0; i < names.length; i++) {
					sched.unscheduleJob(names[i], Scheduler.DEFAULT_GROUP);
				}

				// Jobs could not be deleted due to a bug in quartz-1.4.0 which will cause
				// stateful job blocked permernantly if it is deleted while executing
/*
				groups = sched.getJobGroupNames();
				for (int i = 0; i < groups.length; i++) {
					String[] names = sched.getJobNames(groups[i]);
					for (int j = 0; j < names.length; j++) {
						sched.deleteJob(names[j], groups[i]);
					}
				}
*/
				ListIterator itSchedule = Luntbuild.getDao().loadSchedules().listIterator();
				while (itSchedule.hasNext()) {
					Schedule schedule = (Schedule) itSchedule.next();
					if (schedule.getTrigger() == null)
						continue;
                    String jobName = schedule.getJobName();
					JobDetail jobDetail = sched.getJobDetail(jobName, Scheduler.DEFAULT_GROUP);
					if (jobDetail == null) {
                        jobDetail = new JobDetail();
						jobDetail.setDurability(true);
						jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
						jobDetail.setName(jobName);
						jobDetail.setJobClass(BuildGenerator.class);
						sched.addJob(jobDetail, false);
					}

					// should not save scheduleId here, cause one job instance can associate with
					// more than one schedules, and scheduleId may get messed up if two or more
					// schedules with same job instance calls this method
/*
                    JobDataMap dataMap = new JobDataMap();
                    dataMap.put("scheduleId", String.valueOf(schedule.getId()));
                    jobDetail.setJobDataMap(dataMap);
*/

                    Trigger trigger = (Trigger) schedule.getTrigger().clone();
					trigger.setGroup(Scheduler.DEFAULT_GROUP);
					trigger.setName(String.valueOf(schedule.getId()));
					trigger.setJobGroup(Scheduler.DEFAULT_GROUP);
					trigger.setJobName(jobDetail.getName());
					if (trigger instanceof SimpleTrigger) {
						SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
						simpleTrigger.setStartTime(new Date(System.currentTimeMillis() +
								simpleTrigger.getRepeatInterval()));
					} else
						trigger.setStartTime(new Date(System.currentTimeMillis()));
					sched.scheduleJob(trigger);
				}
			} catch (Exception e) {
				logger.error("Error in rescheduleBuilds: ", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Validates if the scheduler is currently initialized and started
	 */
	private void validateSched() {
		try {
			if (sched == null)
				throw new SchedulerException("Scheduler not initialized!");
			if (sched.isShutdown())
				throw new SchedulerException("Scheduler not started!");
		} catch (Exception e) {
			logger.error("Error in validateSched: ", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Schedule a build for specified schedule using specified trigger
	 *
	 * @param schedule
	 * @param trigger
	 */
	public void scheduleBuild(Schedule schedule, Trigger trigger) {
		synchronized (schedLock) {
			try {
                String jobName = schedule.getJobName();
				JobDetail jobDetail = sched.getJobDetail(jobName, Scheduler.DEFAULT_GROUP);
				if (jobDetail == null) {
					jobDetail = new JobDetail();
					jobDetail.setDurability(true);
					jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
					jobDetail.setName(jobName);
					jobDetail.setJobClass(BuildGenerator.class);
					sched.addJob(jobDetail, false);
				}

				// should not save scheduleId here, cause one job instance can associate with
				// more than one schedules, and scheduleId may get messed up if two or more
				// schedules with same job instance calls this method
/*
                JobDataMap dataMap = new JobDataMap();
                dataMap.put("scheduleId", String.valueOf(schedule.getId()));
                jobDetail.setJobDataMap(dataMap);
*/

				trigger.setJobGroup(jobDetail.getGroup());
				trigger.setJobName(jobDetail.getName());
				sched.scheduleJob(trigger);
			} catch (Exception e) {
				logger.error("Error in scheduleBuilds: ", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Remove specified trigger
	 *
	 * @param triggerName
	 * @param triggerGroup
	 */
	public void unscheduleBuild(String triggerName, String triggerGroup) {
		synchronized (schedLock) {
			try {
				sched.unscheduleJob(triggerName, triggerGroup);
			} catch (Exception e) {
				logger.error("Error in unscheduleBuild: ", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Get list of triggers manually triggered for specified schedule, the list is ordered increasely
	 * by start time of the trigger
	 *
	 * @param schedule
	 * @return
	 */
	public List getWaitingManualBuildTriggers(Schedule schedule) {
		synchronized (schedLock) {
			try {
				List waitingManualBuildTriggers = new ArrayList();
				Trigger manualTriggers[] = sched.getTriggersOfJob(schedule.getJobName(), Scheduler.DEFAULT_GROUP);
				List executingJobs = sched.getCurrentlyExecutingJobs();
				for (int i = 0; i < manualTriggers.length; i++) {
					Trigger manualTrigger = manualTriggers[i];
					if (!manualTrigger.getGroup().equals(BuildGenerator.MANUALBUILD_GROUP))
						continue;
					BuildParams buildParams = Schedule.parseTriggerName(manualTrigger.getName());
					if (buildParams.getScheduleId() != schedule.getId())
						continue;
					Iterator itExecutingJob = executingJobs.iterator();
					boolean executing = false;
					while (itExecutingJob.hasNext()) {
						JobExecutionContext jobExecutionContext = (JobExecutionContext) itExecutingJob.next();
						if (manualTrigger.equals(jobExecutionContext.getTrigger())) {
							executing = true;
							break;
						}
					}
					if (!executing)
						waitingManualBuildTriggers.add(manualTrigger);
				}
				Collections.sort(waitingManualBuildTriggers, new TriggerStartTimeComparator());
				return waitingManualBuildTriggers;
			} catch (Exception e) {
				logger.error("Error in getWaitingManualBuildTriggers: ", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Get rebuilds for specified schedule, rebuilds are a map from trigger to Build
	 * It is ordered by trigger start time
	 *
	 * @param schedule
	 * @return
	 */
	public Map getRebuilds(Schedule schedule) {
		synchronized (schedLock) {
			try {
				Map rebuilds = new HashMap();
				String[] rebuildTriggerNames = sched.getTriggerNames(BuildGenerator.REBUILD_GROUP);
				List executingJobs = sched.getCurrentlyExecutingJobs();
				for (int i = 0; i < rebuildTriggerNames.length; i++) {
					String[] fields = rebuildTriggerNames[i].split("\\" + Luntbuild.TRIGGER_NAME_SEPERATOR);
					long buildId = new Long(fields[0]).longValue();
					Build build = Luntbuild.getDao().loadBuild(buildId);
					if (build.getSchedule().getId() != schedule.getId())
						continue;
					Trigger rebuildTrigger = sched.getTrigger(rebuildTriggerNames[i], BuildGenerator.REBUILD_GROUP);
					Iterator itExecutingJob = executingJobs.iterator();
					boolean executing = false;
					while (itExecutingJob.hasNext()) {
						JobExecutionContext jobExecutionContext = (JobExecutionContext) itExecutingJob.next();
						if (rebuildTrigger.equals(jobExecutionContext.getTrigger())) {
							executing = true;
							break;
						}
					}
					if (!executing)
						rebuilds.put(rebuildTrigger, build);
				}
				List rebuildTriggers = new ArrayList(rebuilds.keySet());
				Collections.sort(rebuildTriggers, new TriggerStartTimeComparator());
				Iterator itRebuildTrigger = rebuildTriggers.iterator();
				Map orderedRebuilds = new LinkedHashMap();
				while (itRebuildTrigger.hasNext()) {
					Trigger trigger = (Trigger) itRebuildTrigger.next();
					orderedRebuilds.put(trigger, rebuilds.get(trigger));
				}
				return orderedRebuilds;
			} catch (Exception e) {
				logger.error("Error in getRebuilds: ", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Initialize and startup the quartz scheduler
	 */
	public void startup() {
		try {
			StdSchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

			Properties props = new Properties();
			props.setProperty("org.quartz.scheduler.instanceName", "DefaultQuartzScheduler");
			props.setProperty("org.quartz.scheduler.rmi.export", "false");
			props.setProperty("org.quartz.scheduler.rmi.proxy", "false");
			props.setProperty("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
			props.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
			props.setProperty("org.quartz.threadPool.threadPriority", "5");
			props.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
			props.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
			props.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
			String buildThreadCountText = (String) Luntbuild.getProperties().get(Constants.BUILD_THREAD_COUNT);
			if (!Luntbuild.isEmpty(buildThreadCountText))
				props.setProperty("org.quartz.threadPool.threadCount", buildThreadCountText);
			else
				props.setProperty("org.quartz.threadPool.threadCount", "10");

			schedFact.initialize(props);
			sched = schedFact.getScheduler();
			sched.start();
		} catch (Exception e) {
			logger.error("Error in startup: ", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Shutdown the quartz scheduler. The scheduler can not be re-started after
	 * a shutdown
	 */
	public void shutdown() {
		if (sched != null) {
			try {
				sched.shutdown(true);
			} catch (Exception e) {
				logger.debug("Error in shutdown: ", e);
			}
		}
	}

	/**
	 * Validates if specified trigger is schedulable
	 *
	 * @param trigger
	 * @throws com.luntsys.luntbuild.utility.ValidationException
	 *
	 */
	public void validateTrigger(Trigger trigger) {
		synchronized (schedLock) {
			try {
				JobDetail jobDetail = sched.getJobDetail(DUMMY_JOB_NAME, Scheduler.DEFAULT_GROUP);
				if (jobDetail == null) {
					jobDetail = new JobDetail();
					jobDetail.setDurability(true);
					jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
					jobDetail.setName(DUMMY_JOB_NAME);
					jobDetail.setJobClass(BuildGenerator.class);
					sched.addJob(jobDetail, false);
				}
				trigger.setGroup(Scheduler.DEFAULT_GROUP);
				trigger.setName(DUMMY_TRIGGER_NAME);
				trigger.setJobGroup(Scheduler.DEFAULT_GROUP);
				trigger.setJobName(DUMMY_JOB_NAME);

				sched.scheduleJob(trigger);
				sched.unscheduleJob(DUMMY_TRIGGER_NAME, Scheduler.DEFAULT_GROUP);
			} catch (Exception e) {
				logger.error("Error in validateTrigger", e);
				if (e.getMessage() != null)
					throw new ValidationException(e.getMessage());
				else
					throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Removes un-necessary manual builds in a schedule. Un-necessary manual
	 * builds are defined as builds that scheduled to occur before time of this function is
	 * called
	 *
	 * @param schedule
	 */
	public void removeUnNecessaryManualTriggers(Schedule schedule) {
		synchronized (schedLock) {
			try {
				Trigger manualTriggers[] = sched.getTriggersOfJob(schedule.getJobName(), Scheduler.DEFAULT_GROUP);
				List executingJobs = sched.getCurrentlyExecutingJobs();
				for (int i = 0; i < manualTriggers.length; i++) {
					Trigger manualTrigger = manualTriggers[i];
					if (!manualTrigger.getGroup().equals(BuildGenerator.MANUALBUILD_GROUP))
						continue;
					BuildParams buildParams = Schedule.parseTriggerName(manualTrigger.getName());
					if (buildParams.getScheduleId() != schedule.getId())
						continue;
					Iterator itExecutingJob = executingJobs.iterator();
					boolean executing = false;
					while (itExecutingJob.hasNext()) {
						JobExecutionContext jobExecutionContext = (JobExecutionContext) itExecutingJob.next();
						if (manualTrigger.equals(jobExecutionContext.getTrigger())) {
							executing = true;
							break;
						}
					}
					if (!executing && manualTrigger.getStartTime().before(new Date()))
						sched.unscheduleJob(manualTrigger.getName(), manualTrigger.getGroup());
				}
			} catch (Exception e) {
				logger.error("Error in removeUnNecessaryManualTriggers: ", e);
				throw new RuntimeException(e);
			}
		}
	}

	public boolean isTriggerAvailable(Trigger trigger) {
		synchronized (schedLock) {
			try {
				if (sched.getTriggerState(trigger.getName(), trigger.getGroup()) != Trigger.STATE_NONE)
					return true;
				else
					return false;
			} catch (SchedulerException e) {
				return false;
			}
		}
	}

	public void scheduleSystemCare() {
		validateSched();
		synchronized (schedLock) {
			try {
				if (sched.getTriggerState(SystemCare.TRIGGER_NAME, SystemCare.TRIGGER_GROUP) != Trigger.STATE_NONE)
					sched.unscheduleJob(SystemCare.TRIGGER_NAME, SystemCare.TRIGGER_GROUP);
				JobDetail jobDetail = sched.getJobDetail(SystemCare.JOB_NAME, SystemCare.JOB_GROUP);
				if (jobDetail == null) {
					jobDetail = new JobDetail();
					jobDetail.setDurability(true);
					jobDetail.setGroup(SystemCare.JOB_GROUP);
					jobDetail.setName(SystemCare.JOB_NAME);
					jobDetail.setJobClass(SystemCare.class);
					sched.addJob(jobDetail, false);
				}
				CronTrigger trigger = new CronTrigger();
				trigger.setGroup(SystemCare.TRIGGER_GROUP);
				trigger.setName(SystemCare.TRIGGER_NAME);
				trigger.setJobGroup(SystemCare.JOB_GROUP);
				trigger.setJobName(SystemCare.JOB_NAME);
				trigger.setCronExpression("0 0 1 * * ?");
				trigger.setStartTime(new Date(System.currentTimeMillis()));
				sched.scheduleJob(trigger);
			} catch (Exception e) {
				logger.error("Error in scheduleSystemCare: ", e);
				throw new RuntimeException(e);
			}
		}
	}
}
