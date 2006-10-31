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
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.NotifierProperty;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SendEmail;


import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Email Notifier
 *
 * @author Robin Shine
 * @author Dustin Hunter
 *
 */
public class EmailNotifier extends TemplatedNotifier {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1L;
	/**
	 * Constants denotes system level property index
	 */
    private static final int SMTP_HOST = 0;
    private static final int SMTP_USER = 1;
    private static final int SMTP_PASSWD = 2;
    private static final int SENDER_EMAIL  = 3;

    private static final int USER_EMAIL = 0;

    /**
     * Constructor
     */
    public EmailNotifier() {
        super(EmailNotifier.class, "email");
    }
	public String getDisplayName() {
		return "Email";
	}

	/** Send e-mail
	 * @param email email
	 * @param antProject project
	 * @param subject subject
	 * @param body body
	 */
	public void sendMail(String email, Project antProject, String subject, String body) {
		SendEmail mail = new SendEmail();
		mail.setProject(antProject);
		mail.setTaskName("mail");
		mail.setTaskType("mail");
		NotifierProperty property = (NotifierProperty) getSystemLevelProperties().get(SENDER_EMAIL);
		String senderEmail = property.getValue(Luntbuild.getProperties());
		if (Luntbuild.isEmpty(senderEmail))
			senderEmail = "luntbuild@" + Luntbuild.getHostName();
		mail.setFrom(senderEmail);
		mail.setReplyTo(senderEmail);
		mail.setToList(email);

		property = (NotifierProperty) getSystemLevelProperties().get(SMTP_HOST);
		String smtpHost = property.getValue(Luntbuild.getProperties());
		if (Luntbuild.isEmpty(smtpHost))
			smtpHost = "localhost";
		mail.setMailhost(smtpHost);

		property = (NotifierProperty) getSystemLevelProperties().get(SMTP_USER);
		String smtpUser = property.getValue(Luntbuild.getProperties());
		if (!Luntbuild.isEmpty(smtpUser)) {
			mail.setUser(smtpUser);
			property = (NotifierProperty) getSystemLevelProperties().get(SMTP_PASSWD);
			String smtpPassword = property.getValue(Luntbuild.getProperties());
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

	public void sendBuildNotification(Set checkinUsers, Set subscribeUsers, Build build, Project antProject) {
		Iterator it = checkinUsers.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			String email =
                ((NotifierProperty)getUserLevelProperties().get(USER_EMAIL)).getValue(user.getContacts());
			if (Luntbuild.isEmpty(email))
				antProject.log("Cannot send email to user \"" +
						user.getName() + "\": email is empty!", Project.MSG_WARN);
			else
				sendMail(email, antProject, constructNotificationTitle(build),
						constructNotificationBody4CheckinUsers(build));
		}
		it = subscribeUsers.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
            String email =
                ((NotifierProperty)getUserLevelProperties().get(USER_EMAIL)).getValue(user.getContacts());
			if (Luntbuild.isEmpty(email))
				antProject.log("Cannot send email to user \"" +
						user.getName() + "\": email is empty!", Project.MSG_WARN);
			else
				sendMail(email, antProject, constructNotificationTitle(build),
						constructNotificationBody(build));
		}
	}

	public void sendScheduleNotification(Set subscribeUsers, Schedule schedule, Project antProject) {
		Iterator it = subscribeUsers.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
            String email =
                ((NotifierProperty)getUserLevelProperties().get(USER_EMAIL)).getValue(user.getContacts());
			if (Luntbuild.isEmpty(email))
				antProject.log("Cannot send email to user \"" +
						user.getName() + "\": email is empty!", Project.MSG_WARN);
			else
				sendMail(email, antProject, constructNotificationTitle(schedule),
						constructNotificationBody(schedule));
		}
	}

    /**
     * @return key for contacts map
     */
    public String getKey() {
        return ((NotifierProperty)getUserLevelProperties().get(USER_EMAIL)).getKey();
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
						"Luntbuild to send email. If this property is not specified, " +
						"Luntbuild will use localhost as the default value.";
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
				return "Email address of the sender";
			}

			public String getDescription() {
				return "This property is optional. If specified, Luntbuild will use this " +
						"email as the sender address when sending out email notifications. " +
						"Otherwise, the sender address will be \"luntbuild@<hostname>\", where " +
						"<hostname> is the host name of the build machine.";
			}
		});

		return properties;
	}

	public List getUserLevelProperties() {
        List properties = new ArrayList();
        properties.add(new NotifierProperty() {
			public Class getNotifierClass() {
				return EmailNotifier.class;
			}

			public String getDisplayName() {
				return "Email";
			}

			public String getDescription() {
				return "Email address for this user.";
			}
		});
        return properties;
	}
}
