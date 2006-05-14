/*
 * Created on 25.01.2005 by n970130
 * 
 * 
 * $Header$
 * 
 * @version $Revision: 61 $ $Date: 2005-01-27 12:37:48 -0700 (Thu, 27 Jan 2005) $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.Project;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.remoting.Constants;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.NotifierProperty;

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
        return "jabber";
    }

    public String getComment()
    {
        return "NOTE: connecting through proxy is not supported currently.";
    }

    /**
     * @see com.luntsys.luntbuild.notifiers.Notifier#sendBuildNotification(java.util.Set,
     *      java.util.Set, com.luntsys.luntbuild.db.Build, java.util.Map,
     *      org.apache.tools.ant.Project)
     */
    public void sendBuildNotification(Set checkinUsers, Set subscribeUsers, Build build, Map properties,
            Project antProject)
    {
        loginToJabberServer(properties, antProject);
        if (connection != null)
        {

            Iterator it = checkinUsers.iterator();
            while (it.hasNext())
            {
                sendMessage(connection, properties, (User) it.next(),
                        constructNotificationMsg(properties, build, true), antProject);
            }
            it = subscribeUsers.iterator();
            while (it.hasNext())
            {
                sendMessage(connection, properties, (User) it.next(),
                        constructNotificationMsg(properties, build, false), antProject);
            }

            logoffFromJabberServer(connection);
        }
    }

    /**
     * @see com.luntsys.luntbuild.notifiers.Notifier#sendScheduleNotification(java.util.Set,
     *      com.luntsys.luntbuild.db.Schedule, java.util.Map,
     *      org.apache.tools.ant.Project)
     */
    public void sendScheduleNotification(Set subscribeUsers, Schedule schedule, Map properties, Project antProject)
    {
        loginToJabberServer(properties, antProject);

        if (connection != null)
        {
            Iterator it = subscribeUsers.iterator();
            while (it.hasNext())
            {
                sendMessage(connection, properties, (User) it.next(), constructNotificationMsg(properties, schedule),
                        antProject);
            }

            logoffFromJabberServer(connection);
        }
    }

    private String constructNotificationMsg(Map properties, Schedule schedule)
    {

        String scheduleDesc = schedule.getProject().getName() + "/" + schedule.getName();

        String msg = "Schedule \"" + scheduleDesc + "\" " + Constants.getScheduleStatusText(schedule.getStatus())
                + " at " + Luntbuild.DATE_DISPLAY_FORMAT.format(schedule.getStatusDate()) + "\r\n" + "system log url: "
                + Luntbuild.getSystemLogUrl(properties);

        return msg;
    }

    private String constructNotificationMsg(Map properties, Build build, boolean checkinUsr)
    {

        String msg = "Build \"" + build.getVersion() + "\" " + Constants.getBuildStatusText(build.getStatus()) + "\r\n"
                + "Start time: " + Luntbuild.DATE_DISPLAY_FORMAT.format(build.getStartDate()) + "\r\n" + "Stop time: "
                + Luntbuild.DATE_DISPLAY_FORMAT.format(build.getEndDate()) + "\r\n" + "Build url: "
                + build.getUrl(properties);

        if (checkinUsr)
        {
            msg += "\r\n\r\n"
                    + "You receive this email because you've made checkins into the source repository recently";
        }

        return msg;
    }

    private void loginToJabberServer(Map properties, Project antProject)
    {
        NotifierProperty property = (NotifierProperty) getSystemLevelProperties().get(JABBER_ACCOUNT);

        String luntJabberAccount = property.getValue(properties);
        if (Luntbuild.isEmpty(luntJabberAccount))
            throw new RuntimeException("Luntbuild jabber account should not be empty in order to send "
                    + "out build notifications through Jabber");

        property = (NotifierProperty) getSystemLevelProperties().get(JABBER_PASSWORD);
        String jabberPasswd = property.getValue(properties);
        if (Luntbuild.isEmpty(jabberPasswd))
            throw new RuntimeException("Password of luntbuild jabber account should not be empty "
                    + "in order to send out build notifications through jabber messenger");

        property = (NotifierProperty) getSystemLevelProperties().get(JABBER_SERVER);
        String jabberServer = property.getValue(properties);
        if (Luntbuild.isEmpty(jabberServer))
        {
            jabberServer ="127.0.0.1";
        }

        property = (NotifierProperty) getSystemLevelProperties().get(JABBER_SERVERPORT);
        String jabberServerPort = property.getValue(properties);
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

    private void sendMessage(XMPPConnection connection, Map properties, User user, String notificationMessage,
            Project antProject)
    {
        String userJabberAccount = getUserLevelProperty().getValue(user.getContacts());

        if (Luntbuild.isEmpty(userJabberAccount))
            antProject.log("Can not send jabber messages to user " + "\"" + user.getName()
                    + "\": jabber account is empty!", Project.MSG_WARN);
        else
         {
            antProject.log("Call jabber account \"" + userJabberAccount + "\"...", Project.MSG_INFO);

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
                return "jabber server";
            }

            public String getDescription()
            {
                return "You can optionally specify the jabber host used by "
                        + "luntbuild to send jabber messages. If this property is not specified, "
                        + "luntbuild will use localhost as the default value.";
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
                return "jabber server port";
            }

            public String getDescription()
            {
                return "The jabber server port to connect do; default would be 5222";
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
                return "jabber user";
            }

            public String getDescription()
            {
                return "The jabber account name to be used for luntbuild to login and send messages";
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
                return "jabber password";
            }

            public String getDescription()
            {
                return "The jabber account password to login.";
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
                return "jabber account";
            }

            public String getDescription()
            {
                return "jabberID of user, for example \"johndoe@jabber.org\"";
            }
        };
    }

}
