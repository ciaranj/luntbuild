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

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.utility.IStringProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import org.apache.tools.ant.Project;

import rath.msnm.MSNMessenger;
import rath.msnm.SwitchboardSession;
import rath.msnm.UserStatus;
import rath.msnm.entity.MsnFriend;
import rath.msnm.event.MsnAdapter;
import rath.msnm.msg.MimeMessage;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * The msn messenger notifier
 */
public class MsnNotifier  extends TemplatedNotifier {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1L;
	/**
	 * Constants denotes system level property index
	 */
    private static final int MSN_ACCOUNT = 0;
    private static final int MSN_PASSWORD = 1;

    private static final int USER_MSN_ACCOUNT = 0;

	private static final int WAIT_INTERVAL = 10000;

	private Object loginLock = new Object();
	private String errorMsg;

    /**
     * Constructor
     */
    public MsnNotifier() {
        super(MsnNotifier.class, "msn");
    }

	public String getDisplayName() {
		return "MSN Messenger";
	}

	public String getComment() {
		return "NOTE. Connecting using proxy is not yet supported.";
	}

	private MSNMessenger createMessenger() {
		IStringProperty property = (IStringProperty) getSystemLevelProperties().get(MSN_ACCOUNT);
		String msnAccount = property.getValue(Luntbuild.getProperties());
		if (Luntbuild.isEmpty(msnAccount))
			throw new RuntimeException("Luntbuild MSN account should not be empty in order to send " +
					"out build notifications to MSN Messenger");
		property = (IStringProperty) getSystemLevelProperties().get(MSN_PASSWORD);
		String msnPasswd = property.getValue(Luntbuild.getProperties());
		if (Luntbuild.isEmpty(msnPasswd))
			throw new RuntimeException("Password of Luntbuild MSN account should not be empty " +
					"in order to send out build notifications to MSN Messenger");
		MSNMessenger messenger = new MSNMessenger(msnAccount, msnPasswd);
		messenger.setInitialStatus(UserStatus.ONLINE);
		messenger.addMsnListener(new MsnListener());
		return messenger;
	}

	private void login(MSNMessenger messenger, Project antProject) {
		antProject.log("Login to MSN Messenger...", Project.MSG_INFO);
		errorMsg = null;
		messenger.login();
		try {
			synchronized (loginLock) {
				loginLock.wait();
			}
			if (!messenger.isLoggedIn()) {
				if (errorMsg != null)
					throw new RuntimeException("MSN Messenger login failed with error: " + errorMsg);
				else
					throw new RuntimeException("MSN Messenger login failed!");
			}
			Thread.sleep(WAIT_INTERVAL);
		} catch (InterruptedException e) {
			// ignores
		}
	}

	private void logout(MSNMessenger messenger, Project antProject) {
		antProject.log("Logout from MSN Messenger...", Project.MSG_INFO);
		messenger.logout();
	}

	private void sendMessage(MSNMessenger messenger, User user, MimeMessage mimeMsg, Project antProject) {
		String msnAccount =
            ((IStringProperty)getUserLevelProperties().get(USER_MSN_ACCOUNT)).getValue(user.getContacts());
		if (Luntbuild.isEmpty(msnAccount))
			antProject.log("Can not send MSN messages to user " +
					"\"" + user.getName() + "\": MSN account is empty!", Project.MSG_WARN);
		else {
			try {
				antProject.log("Call MSN account \"" + msnAccount + "\"...", Project.MSG_INFO);
				SwitchboardSession ss = messenger.doCallWait(msnAccount);
				if (ss == null) {
					antProject.log("Failed to call MSN account \"" + msnAccount +
							"\": this account might offline, or the account has not " +
							"allowed you to send a message", Project.MSG_ERR);
				} else {
					antProject.log("Send instant message to \"" + msnAccount + "\"...", Project.MSG_INFO);
					ss.sendInstantMessage(mimeMsg);
				}
			} catch (IOException e) {
				antProject.log("Exception while sending MSN messages: " + e.getMessage(),
						Project.MSG_ERR);
			} catch (InterruptedException e) {
				// igmores
			}
		}
	}

	private MimeMessage constructNotificationMsg(Schedule schedule) {
		MimeMessage mimeMsg = new MimeMessage();
		if (schedule.getStatus() == com.luntsys.luntbuild.facades.Constants.SCHEDULE_STATUS_SUCCESS) {
			mimeMsg.setFontColor(new Color(0, 170, 0));
		} else if (schedule.getStatus() == com.luntsys.luntbuild.facades.Constants.SCHEDULE_STATUS_FAILED) {
			mimeMsg.setFontColor(new Color(170, 0, 0));
		} else if (schedule.getStatus() == com.luntsys.luntbuild.facades.Constants.SCHEDULE_STATUS_RUNNING) {
			mimeMsg.setFontColor(new Color(170, 170, 0));
		} else {
			mimeMsg.setFontColor(new Color(0, 0, 0));
		}

		mimeMsg.setMessage(constructNotificationBody(schedule));
		return mimeMsg;
	}

	private MimeMessage constructNotificationMsg4CheckinUsers(Build build) {
		MimeMessage mimeMsg = new MimeMessage();
		if (build.getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_SUCCESS) {
			mimeMsg.setFontColor(new Color(0, 170, 0));
		} else if (build.getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_FAILED) {
			mimeMsg.setFontColor(new Color(170, 0, 0));
		} else {
			mimeMsg.setFontColor(new Color(170, 170, 0));
		}

		mimeMsg.setMessage(constructNotificationBody4CheckinUsers(build));
		return mimeMsg;
	}

	private MimeMessage constructNotificationMsg(Build build) {
		MimeMessage mimeMsg = new MimeMessage();
		if (build.getStatus() == Constants.BUILD_STATUS_SUCCESS) {
			mimeMsg.setFontColor(new Color(0, 170, 0));
		} else if (build.getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_FAILED) {
			mimeMsg.setFontColor(new Color(170, 0, 0));
		} else {
			mimeMsg.setFontColor(new Color(170, 170, 0));
		}

		mimeMsg.setMessage(constructNotificationBody(build));
		return mimeMsg;
	}

	public void sendBuildNotification(Set checkinUsers, Set subscribeUsers, Build build, Project antProject) {
		MSNMessenger messenger = createMessenger();
		login(messenger, antProject);
		try {
			Iterator it = checkinUsers.iterator();
			while (it.hasNext()) {
				sendMessage(messenger, (User) it.next(), constructNotificationMsg4CheckinUsers(build),
						antProject);
			}
			it = subscribeUsers.iterator();
			while (it.hasNext()) {
				sendMessage(messenger, (User) it.next(), constructNotificationMsg(build), antProject);
			}
		} finally {
			logout(messenger, antProject);
		}
	}

	public void sendScheduleNotification(Set subscribeUsers, Schedule schedule, Project antProject) {
		MSNMessenger messenger = createMessenger();
		login(messenger, antProject);
		try {
			Iterator it = subscribeUsers.iterator();
			while (it.hasNext()) {
				sendMessage(messenger, (User) it.next(), constructNotificationMsg(schedule),
						antProject);
			}
		} finally {
			logout(messenger, antProject);
		}
	}

	public List getSystemLevelProperties() {
		List properties = getMsnNotifierProperties();
		return properties;
	}

	public List getUserLevelProperties() {
        List properties = getMsnNotifierProperties();
        return properties;
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
