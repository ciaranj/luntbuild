/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-28
 * Time: 13:37:26
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

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.Project;

import rath.msnm.MSNMessenger;
import rath.msnm.SwitchboardSession;
import rath.msnm.UserStatus;
import rath.msnm.entity.MsnFriend;
import rath.msnm.event.MsnAdapter;
import rath.msnm.msg.MimeMessage;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.BuildSchedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.remoting.BuildFacade;
import com.luntsys.luntbuild.remoting.BuildScheduleFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.NotifierProperty;

/**
 * The msn messenger notifier
 */
public class MsnNotifier extends Notifier {
	/**
	 * Constants denotes system level property index
	 */
	public static final int MSN_ACCOUNT = 0;
	public static final int MSN_PASSWORD = 1;

	private static final int WAIT_INTERVAL = 10000;

	private Object loginLock = new Object();
	private String errorMsg;

	public String getDisplayName() {
		return "msn messenger";
	}

	public String getComment() {
		return "NOTE: connecting through proxy is not supported currently.";  
	}

	private MSNMessenger createMessenger(Map properties) {
		NotifierProperty property = (NotifierProperty) getSystemLevelProperties().get(MSN_ACCOUNT);
		String msnAccount = property.getValue(properties);
		if (Luntbuild.isEmpty(msnAccount))
			throw new RuntimeException("Luntbuild msn account should not be empty in order to send " +
					"out build notifications through msn messenger");
		property = (NotifierProperty) getSystemLevelProperties().get(MSN_PASSWORD);
		String msnPasswd = property.getValue(properties);
		if (Luntbuild.isEmpty(msnPasswd))
			throw new RuntimeException("Password of luntbuild msn account should not be empty " +
					"in order to send out build notifications through msn messenger");
		MSNMessenger messenger = new MSNMessenger(msnAccount, msnPasswd);
		messenger.setInitialStatus(UserStatus.ONLINE);
		messenger.addMsnListener(new MsnListener());
		return messenger;
	}

	private void login(MSNMessenger messenger, Project antProject) {
		antProject.log("Login to MSN messenger...", Project.MSG_INFO);
		errorMsg = null;
		messenger.login();
		try {
			synchronized (loginLock) {
				loginLock.wait();
			}
			if (!messenger.isLoggedIn()) {
				if (errorMsg != null)
					throw new RuntimeException("MSN login failed with error: " + errorMsg);
				else
					throw new RuntimeException("MSN login failed!");
			}
			Thread.sleep(WAIT_INTERVAL);
		} catch (InterruptedException e) {
			// ignores
		}
	}

	private void logout(MSNMessenger messenger, Project antProject) {
		antProject.log("Logout from MSN messenger...", Project.MSG_INFO);
		messenger.logout();
	}

	private void sendMessage(MSNMessenger messenger, User user, MimeMessage mimeMsg, Project antProject) {
		String msnAccount = getUserLevelProperty().getValue(user.getContacts());
		if (Luntbuild.isEmpty(msnAccount))
			antProject.log("Can not send msn messages to user " +
					"\"" + user.getName() + "\": msn account is empty!", Project.MSG_WARN);
		else {
			try {
				antProject.log("Call msn account \"" + msnAccount + "\"...", Project.MSG_INFO);
				SwitchboardSession ss = messenger.doCallWait(msnAccount);
				if (ss == null) {
					antProject.log("Fail to call msn account \"" + msnAccount +
							"\": the reason maybe this account is offline, or has not " +
							"allowed you to send message", Project.MSG_ERR);
				} else {
					antProject.log("Send instant message to \"" + msnAccount + "\"...", Project.MSG_INFO);
					ss.sendInstantMessage(mimeMsg);
				}
			} catch (IOException e) {
				antProject.log("Exception while sending msn messages: " + e.getMessage(),
						Project.MSG_ERR);
			} catch (InterruptedException e) {
				// igmores
			}
		}
	}

	private MimeMessage constructNotificationMsg(Map properties, BuildSchedule buildSchedule) {
		MimeMessage mimeMsg = new MimeMessage();
		String buildScheduleDesc = buildSchedule.getView().getProject().getName() +
				"/" + buildSchedule.getView().getName() + "/" + buildSchedule.getSchedule().getName();
		String status;
		if (buildSchedule.getStatus() == BuildScheduleFacade.SUCCESS) {
			mimeMsg.setFontColor(new Color(0, 170, 0));
			status = "succeed";
		} else if (buildSchedule.getStatus() == BuildScheduleFacade.FAILED) {
			mimeMsg.setFontColor(new Color(170, 0, 0));
			status = "failed";
		} else if (buildSchedule.getStatus() == BuildScheduleFacade.RUNNING) {
			mimeMsg.setFontColor(new Color(170, 170, 0));
			status = "running";
		} else {
			mimeMsg.setFontColor(new Color(0, 0, 0));
			status = "created";
		}

		String msg = "build schedule \"" + buildScheduleDesc + "\" " + status + " at " +
				Luntbuild.DATE_DISPLAY_FORMAT.format(buildSchedule.getStatusDate()) + "\r\n" +
				"system log url: " + Luntbuild.getSystemLogUrl(properties);
		mimeMsg.setMessage(msg);
		return mimeMsg;
	}

	private MimeMessage constructNotificationMsg4CheckinUsers(Map properties, Build build) {
		MimeMessage mimeMsg = new MimeMessage();
		String status;
		if (build.getStatus() == BuildFacade.SUCCESS) {
			mimeMsg.setFontColor(new Color(0, 170, 0));
			status = "succeed";
		} else if (build.getStatus() == BuildFacade.FAILED) {
			mimeMsg.setFontColor(new Color(170, 0, 0));
			status = "failed";
		} else {
			mimeMsg.setFontColor(new Color(170, 170, 0));
			status = "running";
		}

		String msg = "build \"" +  build.getVersion() + "\" " + status + "\r\n" +
				"start time: " + Luntbuild.DATE_DISPLAY_FORMAT.format(build.getStartDate()) + "\r\n" +
				"stop time: " + Luntbuild.DATE_DISPLAY_FORMAT.format(build.getEndDate()) + "\r\n" +
				"build url: " + build.getUrl(properties) + "\r\n\r\n" +
				"You receive this email because you've made checkins into the source repository recently";
		mimeMsg.setMessage(msg);
		return mimeMsg;
	}

	private MimeMessage constructNotificationMsg(Map properties, Build build) {
		MimeMessage mimeMsg = new MimeMessage();
		String status;
		if (build.getStatus() == BuildFacade.SUCCESS) {
			mimeMsg.setFontColor(new Color(0, 170, 0));
			status = "succeed";
		} else if (build.getStatus() == BuildFacade.FAILED) {
			mimeMsg.setFontColor(new Color(170, 0, 0));
			status = "failed";
		} else {
			mimeMsg.setFontColor(new Color(170, 170, 0));
			status = "running";
		}

		String msg = "build \"" +  build.getVersion() + "\" " + status + "\r\n" +
				"start time: " + Luntbuild.DATE_DISPLAY_FORMAT.format(build.getStartDate()) + "\r\n" +
				"stop time: " + Luntbuild.DATE_DISPLAY_FORMAT.format(build.getEndDate()) + "\r\n" +
				"build url: " + build.getUrl(properties);
		mimeMsg.setMessage(msg);
		return mimeMsg;
	}

	public void sendBuildNotification(Set checkinUsers, Set subscribeUsers, Build build,
									  Map properties, Project antProject) {
		MSNMessenger messenger = createMessenger(properties);
		login(messenger, antProject);
		try {
			Iterator it = checkinUsers.iterator();
			while (it.hasNext()) {
				sendMessage(messenger, (User) it.next(), constructNotificationMsg4CheckinUsers(properties, build),
						antProject);
			}
			it = subscribeUsers.iterator();
			while (it.hasNext()) {
				sendMessage(messenger, (User) it.next(), constructNotificationMsg(properties, build), antProject);
			}
		} finally {
			logout(messenger, antProject);
		}
	}

	public void sendBuildScheduleNotification(Set subscribeUsers, BuildSchedule buildSchedule,
											  Map properties, Project antProject) {
		MSNMessenger messenger = createMessenger(properties);
		login(messenger, antProject);
		try {
			Iterator it = subscribeUsers.iterator();
			while (it.hasNext()) {
				sendMessage(messenger, (User) it.next(), constructNotificationMsg(properties, buildSchedule), antProject);
			}
		} finally {
			logout(messenger, antProject);
		}
	}

	public List getSystemLevelProperties() {
		List properties = new ArrayList();
		properties.add(new NotifierProperty() {
			public String getDisplayName() {
				return "luntbuild msn account";
			}

			public String getDescription() {
				return "Luntbuild need a MSN messenger account in order to " +
						"send out build notification messages.For example \"luntbuild@hotmail.com\"";
			}

			public Class getNotifierClass() {
				return MsnNotifier.class;
			}
		});
		properties.add(new NotifierProperty() {
			public String getDisplayName() {
				return "luntbuild msn password";
			}

			public String getDescription() {
				return "Password for the above msn account.";
			}

			public boolean isSecret() {
				return true;
			}

			public Class getNotifierClass() {
				return MsnNotifier.class;
			}
		});

		return properties;
	}

	public NotifierProperty getUserLevelProperty() {
		return new NotifierProperty() {
			public String getDisplayName() {
				return "msn account";
			}

			public String getDescription() {
				return "The MSN messenger account for this user, for example \"foobar@hotmail.com\"";
			}

			public Class getNotifierClass() {
				return MsnNotifier.class;
			}
		};
	}

	class MsnListener extends MsnAdapter {
		public void loginComplete(MsnFriend msnFriend) {
			synchronized (loginLock) {
				loginLock.notify();
			}
		}

		public void loginError(String s) {
			synchronized (loginLock) {
				loginLock.notify();
				errorMsg = s;
			}
		}

		public void logoutNotify() {
			synchronized(loginLock) {
				loginLock.notify();
			}
		}
	}
}
