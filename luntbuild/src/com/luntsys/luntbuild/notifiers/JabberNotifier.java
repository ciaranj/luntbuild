/*
 * Created on 25.01.2005 by n970130
 * 
 * 
 * $Header$
 * 
 * @version $Revision: 195 $ $Date: 2005-04-14 04:18:06 -0600 (Thu, 14 Apr 2005) $
 * 
 * ===========================================================================
 * 
 * Copyright (c) 2004++ Johannes Plachy. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE APACHE
 * SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * 
 * ===========================================================================
 */

package com.luntsys.luntbuild.notifiers;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.NotifierProperty;
import org.apache.tools.ant.Project;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 
 *  notifier for jabber messanger
 *  to be used by luntbuild for internal notifications
 * 
 * @author johannes plachy
 * 
 */

public class JabberNotifier extends Notifier
{
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1L;

    public static final int JABBER_SERVER = 0;
    
    public static final int JABBER_SERVERPORT = 1;

    public static final int JABBER_ACCOUNT = 2;

    public static final int JABBER_PASSWORD = 3;

    private XMPPConnection connection = null;

    /**
     * @see com.luntsys.luntbuild.notifiers.Notifier#getDisplayName()
     */
    public String getDisplayName()
    {
        return "Jabber";
    }

    public String getComment()
    {
        return "NOTE. Connecting using proxy is not yet supported.";
    }

    /**
     * @see com.luntsys.luntbuild.notifiers.Notifier#sendBuildNotification(java.util.Set,
     *      java.util.Set, com.luntsys.luntbuild.db.Build, org.apache.tools.ant.Project)
     */
    public void sendBuildNotification(Set checkinUsers, Set subscribeUsers, Build build, Project antProject)
    {
        loginToJabberServer(antProject);
        if (connection != null)
        {

            Iterator it = checkinUsers.iterator();
            while (it.hasNext())
            {
                sendMessage(connection, (User) it.next(),
                        constructNotificationMsg(build, true), antProject);
            }
            it = subscribeUsers.iterator();
            while (it.hasNext())
            {
                sendMessage(connection, (User) it.next(),
                        constructNotificationMsg(build, false), antProject);
            }

            logoffFromJabberServer(connection);
        }
    }

    /**
     * @see com.luntsys.luntbuild.notifiers.Notifier#sendScheduleNotification(java.util.Set,
     *      com.luntsys.luntbuild.db.Schedule, org.apache.tools.ant.Project)
     */
    public void sendScheduleNotification(Set subscribeUsers, Schedule schedule, Project antProject)
    {
        loginToJabberServer(antProject);

        if (connection != null)
        {
            Iterator it = subscribeUsers.iterator();
            while (it.hasNext())
            {
                sendMessage(connection, (User) it.next(), constructNotificationMsg(schedule), antProject);
            }

            logoffFromJabberServer(connection);
        }
    }

    private String constructNotificationMsg(Schedule schedule)
    {

        String scheduleDesc = schedule.getProject().getName() + "/" + schedule.getName();

        String msg = "Schedule \"" + scheduleDesc + "\" " + com.luntsys.luntbuild.facades.Constants.getScheduleStatusText(schedule.getStatus())
                + " at " + Luntbuild.DATE_DISPLAY_FORMAT.format(schedule.getStatusDate()) + "\r\n" + "system log url: "
                + Luntbuild.getSystemLogUrl();

        return msg;
    }

    private String constructNotificationMsg(Build build, boolean checkinUsr)
    {
		String buildDesc = build.getSchedule().getProject().getName() + "/" + build.getSchedule().getName() + "/" + build.getVersion();
        String msg = "Build \"" + buildDesc + "\" " + com.luntsys.luntbuild.facades.Constants.getBuildStatusText(build.getStatus()) + "\r\n"
                + "Start time: " + Luntbuild.DATE_DISPLAY_FORMAT.format(build.getStartDate()) + "\r\n" + "End time: "
                + Luntbuild.DATE_DISPLAY_FORMAT.format(build.getEndDate()) + "\r\n" + "Build url: "
                + build.getUrl();

        if (checkinUsr)
        {
            msg += "\r\n\r\n"
                    + "You have received this email because you've made checkins in the source repository recently";
        }

        return msg;
    }

    private void loginToJabberServer(Project antProject)
    {
        NotifierProperty property = (NotifierProperty) getSystemLevelProperties().get(JABBER_ACCOUNT);

        String luntJabberAccount = property.getValue(Luntbuild.getProperties());
        if (Luntbuild.isEmpty(luntJabberAccount))
            throw new RuntimeException("Luntbuild Jabber account should not be empty in order to send "
                    + "out build notifications to Jabber Messenger");

        property = (NotifierProperty) getSystemLevelProperties().get(JABBER_PASSWORD);
        String jabberPasswd = property.getValue(Luntbuild.getProperties());
        if (Luntbuild.isEmpty(jabberPasswd))
            throw new RuntimeException("Password of Luntbuild jabber account should not be empty "
                    + "in order to send out build notifications to Jabber Messenger");

        property = (NotifierProperty) getSystemLevelProperties().get(JABBER_SERVER);
        String jabberServer = property.getValue(Luntbuild.getProperties());
        if (Luntbuild.isEmpty(jabberServer))
        {
            jabberServer ="127.0.0.1";
        }

        property = (NotifierProperty) getSystemLevelProperties().get(JABBER_SERVERPORT);
        String jabberServerPort = property.getValue(Luntbuild.getProperties());
        if (Luntbuild.isEmpty(jabberServer))
        {
            jabberServerPort = "5222";
        }
        
        try
        {
            connection = new XMPPConnection(jabberServer,Integer.parseInt(jabberServerPort));            
            connection.login(luntJabberAccount, jabberPasswd);
        }
        catch (XMPPException e)
        {
            antProject.log("Login to jabberserver [" + jabberServer + "] using [" + luntJabberAccount + "] failed",
                    Project.MSG_WARN);

            // e.printStackTrace();
        }
    }

    private void logoffFromJabberServer(XMPPConnection connection)
    {
        if (connection != null)
        {
            connection.close();
        }
    }

    private void sendMessage(XMPPConnection connection, User user, String notificationMessage,
            Project antProject)
    {
        String userJabberAccount = getUserLevelProperty().getValue(user.getContacts());

        if (Luntbuild.isEmpty(userJabberAccount))
            antProject.log("Can not send Jabber messages to user " + "\"" + user.getName()
                    + "\": Jabber account is empty!", Project.MSG_WARN);
        else
         {
            antProject.log("Call Jabber account \"" + userJabberAccount + "\"...", Project.MSG_INFO);

            sendNotification(connection, userJabberAccount, notificationMessage);
        }
    }

    private void sendNotification(XMPPConnection connection, String receiver, String message)
    {
        try
        {
            connection.createChat(receiver).sendMessage(message);
        }
        catch (XMPPException e)
        {
            // e.printStackTrace();
        }
    }

    /**
     * @see com.luntsys.luntbuild.notifiers.Notifier#getSystemLevelProperties()
     */
    public List getSystemLevelProperties()
    {
        List properties = new ArrayList();

        properties.add(new NotifierProperty()
        {

            public Class getNotifierClass()
            {
                return JabberNotifier.class;
            }

            public String getDisplayName()
            {
                return "Jabber server";
            }

            public String getDescription()
            {
                return "You can optionally specify the Jabber host used by "
                        + "Luntbuild to send Jabber messages. If this property is not specified, "
                        + "Luntbuild will use localhost as the default value.";
            }
        });
        properties.add(new NotifierProperty()
        {

            public Class getNotifierClass()
            {
                return JabberNotifier.class;
            }

            public String getDisplayName()
            {
                return "Jabber server port";
            }

            public String getDescription()
            {
                return "The Jabber server port to connect to; default is 5222.";
            }
        });        
        properties.add(new NotifierProperty()
        {

            public Class getNotifierClass()
            {
                return JabberNotifier.class;
            }

            public String getDisplayName()
            {
                return "Jabber user";
            }

            public String getDescription()
            {
                return "The Jabber account name to be used for Luntbuild to login and send messages.";
            }
        });
        properties.add(new NotifierProperty()
        {

            public Class getNotifierClass()
            {
                return JabberNotifier.class;
            }

            public boolean isSecret()
            {
                return true;
            }

            public String getDisplayName()
            {
                return "Jabber password";
            }

            public String getDescription()
            {
                return "The Jabber account password to login.";
            }
        });

        return properties;
    }

    /**
     * @see com.luntsys.luntbuild.notifiers.Notifier#getUserLevelProperty()
     */
    public NotifierProperty getUserLevelProperty()
    {
        return new NotifierProperty()
        {

            public Class getNotifierClass()
            {
                return JabberNotifier.class;
            }

            public String getDisplayName()
            {
                return "Jabber account";
            }

            public String getDescription()
            {
                return "JabberID for this user, for example \"johndoe@jabber.org\".";
            }
        };
    }

}
