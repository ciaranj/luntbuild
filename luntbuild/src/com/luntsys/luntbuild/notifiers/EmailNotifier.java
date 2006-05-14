/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-27
 * Time: 11:15:25
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
package com.luntsys.luntbuild.notifiers;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.BuildSchedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.remoting.BuildFacade;
import com.luntsys.luntbuild.remoting.BuildScheduleFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.NotifierProperty;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SendEmail;

import java.util.*;

public class EmailNotifier extends Notifier {
	/**
	 * Constants denotes system level property index
	 */
	public static final int SMTP_HOST = 0;
	public static final int SMTP_USER = 1;
	public static final int SMTP_PASSWD = 2;
	public static final int SENDER_EMAIL  = 3;

	public String getDisplayName() {
		return "email";
	}

	private String constructNotificationTitle(BuildSchedule buildSchedule) {
		String buildScheduleDesc = buildSchedule.getView().getProject().getName() +
				"/" + buildSchedule.getView().getName() + "/" + buildSchedule.getSchedule().getName();
		String status;
		if (buildSchedule.getStatus() == BuildScheduleFacade.SUCCESS)
			status = "succeed";
		else if (buildSchedule.getStatus() == BuildScheduleFacade.FAILED)
			status = "failed";
		else if (buildSchedule.getStatus() == BuildScheduleFacade.RUNNING)
			status = "running";
		else
			status = "created";

		return "[luntbuild] Build schedule \"" + buildScheduleDesc + "\" " + status;
	}

	private String constructNotificationBody(Map properties, BuildSchedule buildSchedule) {
		String buildScheduleDesc = buildSchedule.getView().getProject().getName() +
				"/" + buildSchedule.getView().getName() + "/" + buildSchedule.getSchedule().getName();
		String status;
		String color;
		if (buildSchedule.getStatus() == BuildScheduleFacade.SUCCESS) {
			status = "succeed";
			color = "#00AA00";
		} else if (buildSchedule.getStatus() == BuildScheduleFacade.FAILED) {
			status = "failed";
			color = "#AA0000";
		} else if (buildSchedule.getStatus() == BuildScheduleFacade.RUNNING) {
			status = "running";
			color = "#AAAA00";
		} else {
			status = "created";
			color = "#000000";
		}

		String msg = "Build schedule <a href=\"" + buildSchedule.getUrl(properties) + "\">" + buildScheduleDesc + "</a> " +
				status + " at " + Luntbuild.DATE_DISPLAY_FORMAT.format(buildSchedule.getStatusDate()) + ".<br><br>" +
				"Access the <a href=\"" + Luntbuild.getSystemLogUrl(properties) + "\">system log</a> to get detail information.<br><br><br>" +
				"-- luntbuild";
		return "<span style=\"color:" + color + "\">" + msg + "</span>";
	}

	private String constructNotificationTitle(Build build) {
		String status;
		if (build.getStatus() == BuildFacade.SUCCESS)
			status = "succeed";
		else if (build.getStatus() == BuildFacade.FAILED)
			status = "failed";
		else
			status = "running";

		return "[luntbuild] Build of \"" + build.getVersion() + "\" " + status;
	}

	private String constructNotificationBody4CheckinUsers(Map properties, Build build) {
		String status;
		String color;
		if (build.getStatus() == BuildFacade.SUCCESS) {
			status = "succeed";
			color = "#00AA00";
		} else if (build.getStatus() == BuildFacade.FAILED) {
			status = "failed";
			color = "#AA0000";
		} else {
			status = "running";
			color = "AAAA00";
		}

		String msg = "Build of <a href=\"" + build.getUrl(properties) + "\">" + build.getVersion() + "</a> " + status + ".<br><br>" +
				"This build is started at " + Luntbuild.DATE_DISPLAY_FORMAT.format(build.getStartDate()) + ", and finishes at " +
				Luntbuild.DATE_DISPLAY_FORMAT.format(build.getEndDate()) + ".<br><br>" +
				"You receive this email because you've made checkins into the source repository recently. <br><br>" +
				"Access the <a href=\"" + build.getBuildLogUrl(properties) + "\">build log</a> and " +
				"<a href=\"" + build.getRevisionLogUrl(properties) + "\">revision log</a> to get detail information.<br><br><br>" +
				"-- luntbuild";
		return "<span style=\"color:" + color + "\">" + msg + "</span>";
	}

	private String constructNotificationBody(Map properties, Build build) {
		String status;
		String color;
		if (build.getStatus() == BuildFacade.SUCCESS) {
			status = "succeed";
			color = "#00AA00";
		} else if (build.getStatus() == BuildFacade.FAILED) {
			status = "failed";
			color = "AA0000";
		} else {
			status = "running";
			color = "AAAA00";
		}

		String msg = "Build of <a href=\"" + build.getUrl(properties) + "\">" + build.getVersion() + "</a> " + status + ".<br><br>" +
				"This build is started at " + Luntbuild.DATE_DISPLAY_FORMAT.format(build.getStartDate()) + ", and finishes at " +
				Luntbuild.DATE_DISPLAY_FORMAT.format(build.getEndDate()) + ".<br><br>" +
				"You may access <a href=\"" + build.getBuildLogUrl(properties) + "\">build log</a> and " +
				"<a href=\"" + build.getRevisionLogUrl(properties) + "\">revision log</a> of this build here to get detail information.<br><br><br>" +
				"-- luntbuild";
		return "<span style=\"color:" + color + "\">" + msg + "</span>";
	}

	public void sendMail(Map properties, String email,
						 Project antProject, String subject, String body) {
		SendEmail mail = new SendEmail();
		mail.setProject(antProject);
		mail.setTaskName("mail");
		mail.setTaskType("mail");
		NotifierProperty property = (NotifierProperty) getSystemLevelProperties().get(SENDER_EMAIL);
		String senderEmail = property.getValue(properties);
		if (Luntbuild.isEmpty(senderEmail))
			senderEmail = "luntbuild@" + Luntbuild.getHostName();
		mail.setFrom(senderEmail);
		mail.setReplyTo(senderEmail);
		mail.setToList(email);

		property = (NotifierProperty) getSystemLevelProperties().get(SMTP_HOST);
		String smtpHost = property.getValue(properties);
		if (Luntbuild.isEmpty(smtpHost))
			smtpHost = "localhost";
		mail.setMailhost(smtpHost);

		property = (NotifierProperty) getSystemLevelProperties().get(SMTP_USER);
		String smtpUser = property.getValue(properties);
		if (!Luntbuild.isEmpty(smtpUser)) {
			mail.setUser(smtpUser);
			property = (NotifierProperty) getSystemLevelProperties().get(SMTP_PASSWD);
			String smtpPassword = property.getValue(properties);
			if (!Luntbuild.isEmpty(smtpPassword))
				mail.setPassword(smtpPassword);
		}
		mail.setMessageMimeType("text/html");
		mail.setCharset("utf-8");
		mail.setFailOnError(true);
		mail.setSubject(subject);
		mail.setMessage(body);

		antProject.log("Send build notification via email to: " + email, Project.MSG_INFO);
		try {
			mail.execute();
		} catch (Exception e) {
			antProject.log(Luntbuild.getExceptionMessage(e), Project.MSG_ERR);
		}
	}

	public void sendBuildNotification(Set checkinUsers, Set subscribeUsers, Build build,
									  Map properties, Project antProject) {
		Iterator it = checkinUsers.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			String email = getUserLevelProperty().getValue(user.getContacts());
			if (Luntbuild.isEmpty(email))
				antProject.log("Can not send email to user \"" +
						user.getName() + "\": email is empty!", Project.MSG_WARN);
			else
				sendMail(properties, email, antProject, constructNotificationTitle(build),
						constructNotificationBody4CheckinUsers(properties, build));
		}
		it = subscribeUsers.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			String email = getUserLevelProperty().getValue(user.getContacts());
			if (Luntbuild.isEmpty(email))
				antProject.log("Can not send email to user \"" +
						user.getName() + "\": email is empty!", Project.MSG_WARN);
			else
				sendMail(properties, email, antProject, constructNotificationTitle(build),
						constructNotificationBody(properties, build));
		}
	}

	public void sendBuildScheduleNotification(Set subscribeUsers, BuildSchedule buildSchedule,
											  Map properties, Project antProject) {
		Iterator it = subscribeUsers.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			String email = getUserLevelProperty().getValue(user.getContacts());
			if (Luntbuild.isEmpty(email))
				antProject.log("Can not send email to user \"" +
						user.getName() + "\": email is empty!", Project.MSG_WARN);
			else
				sendMail(properties, email, antProject, constructNotificationTitle(buildSchedule),
						constructNotificationBody(properties, buildSchedule));
		}
	}

	public List getSystemLevelProperties() {
		List properties = new ArrayList();
		properties.add(new NotifierProperty() {
			public Class getNotifierClass() {
				return EmailNotifier.class;
			}

			public String getDisplayName() {
				return "SMTP host";
			}

			public String getDescription() {
				return "You can optionally specify the SMTP mail host used by " +
						"luntbuild to send email. If this property is not specified, " +
						"luntbuild will use localhost as the default value.";
			}
		});
		properties.add(new NotifierProperty() {
			public Class getNotifierClass() {
				return EmailNotifier.class;
			}

			public String getDisplayName() {
				return "SMTP user";
			}

			public String getDescription() {
				return "This property is optional. If the SMTP host needs authentication, " +
						"you should provide the user name here.";
			}
		});
		properties.add(new NotifierProperty() {
			public Class getNotifierClass() {
				return EmailNotifier.class;
			}

			public boolean isSecret() {
				return true;
			}

			public String getDisplayName() {
				return "SMTP password";
			}

			public String getDescription() {
				return "This property is optional. If the SMTP host needs authentication, " +
						"you should provide the password here.";
			}
		});
		properties.add(new NotifierProperty() {
			public Class getNotifierClass() {
				return EmailNotifier.class;
			}

			public String getDisplayName() {
				return "Email address of sender";
			}

			public String getDescription() {
				return "This property is optional. If specified, luntbuild will use this " +
						"email as the sender address when sending out email notifications. " +
						"Otherwise, the sender address will be \"luntbuild@<hostname>\" where " +
						"<hostname> is host name of the build machine.";
			}
		});

		return properties;
	}

	public NotifierProperty getUserLevelProperty() {
		return new NotifierProperty() {
			public Class getNotifierClass() {
				return EmailNotifier.class;
			}

			public String getDisplayName() {
				return "email";
			}

			public String getDescription() {
				return "Email address of this user";
			}
		};
	}
}
