/*
 * Created on 25.01.2005 by n970130
 *
 *
 * $Header$
 *
 * @version $Revision: 343 $ $Date: 2005-11-21 18:24:21 -0700 (Mon, 21 Nov 2005) $
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
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.NotifierProperty;

import org.apache.tapestry.form.StringPropertySelectionModel;
import org.apache.tools.ant.Project;
import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.GoogleTalkConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jabber messenger implementation.
 * 
 * @author johannes plachy
 */
public class JabberNotifier  extends TemplatedNotifier
{
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1L;

    private static final int JABBER_SERVER = 0;

    private static final int JABBER_SERVERTYPE = 1;

    private static final int JABBER_SERVERPORT = 2;

    private static final int JABBER_ACCOUNT = 3;

    private static final int JABBER_PASSWORD = 4;

    private static final int USER_JABBER_ACCOUNT = 0;

    private XMPPConnection connection = null;

    /**
     * Creates a Jabber notifier.
     */
    public JabberNotifier() {
        super(JabberNotifier.class, "jabber");
    }

    /**
     * @inheritDoc
     */
    public String getDisplayName()
    {
        return "Jabber";
    }

	/**
	 * @inheritDoc
	 */
    public String getComment()
    {
        return "NOTE. Connecting using proxy is not yet supported.";
    }

	/**
	 * @inheritDoc
	 */
    public void sendBuildNotification(Set checkinUsers, Set subscribeUsers, Build build, Project antProject)
    {
        loginToJabberServer(antProject);
        if (this.connection != null)
        {
            Iterator it = checkinUsers.iterator();
            while (it.hasNext())
            {
                sendMessage(this.connection, (User) it.next(),
                        constructNotificationBody4CheckinUsers(build), antProject);
            }
            it = subscribeUsers.iterator();
            while (it.hasNext())
            {
                sendMessage(this.connection, (User) it.next(),
                        constructNotificationBody(build), antProject);
            }

            logoffFromJabberServer(this.connection, antProject);
        }
    }

	/**
	 * @inheritDoc
	 */
    public void sendScheduleNotification(Set subscribeUsers, Schedule schedule, Project antProject)
    {
        loginToJabberServer(antProject);

        if (this.connection != null)
        {
            Iterator it = subscribeUsers.iterator();
            while (it.hasNext())
            {
                sendMessage(this.connection, (User) it.next(), constructNotificationBody(schedule), antProject);
            }

            logoffFromJabberServer(this.connection,antProject);
        }
    }

    /**
     * Logs in to the Jabber server specified by the system level properties.
     * 
     * @param antProject the ant project used for logging purposes
     */
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
        if (Luntbuild.isEmpty(jabberServerPort))
        {
            jabberServerPort = "5222";
        }

        try
        {
            property = (NotifierProperty) getSystemLevelProperties().get(JABBER_SERVERTYPE);
            int jabberServerType = Constants.getJabberServerTypeValue(property.getValue(Luntbuild.getProperties()));

            if ( jabberServerType == Constants.JABBER_SERVER_TYPE_SSL) {
                this.connection = new SSLXMPPConnection(jabberServer,Integer.parseInt(jabberServerPort));
                this.connection.login(luntJabberAccount, jabberPasswd,
                        "LuntBuild-" + this.connection.getConnectionID());
            } else if(jabberServerType == Constants.JABBER_SERVER_TYPE_GOOGLE) {
                this.connection = new GoogleTalkConnection();
                this.connection.login(luntJabberAccount, jabberPasswd,
                        "LuntBuild-" + this.connection.getConnectionID());
            } else {
                this.connection = new XMPPConnection(jabberServer,Integer.parseInt(jabberServerPort));
                this.connection.login(luntJabberAccount, jabberPasswd,
                        "LuntBuild-" + this.connection.getConnectionID());
            }
            if ( this.connection.isConnected() ){
                antProject.log("Login to jabberserver [" + jabberServer + "] using [" + luntJabberAccount + "] succeeded",
                        Project.MSG_INFO);
            }
        }
        catch (XMPPException e)
        {
            antProject.log("Login to jabberserver [" + jabberServer + "] using [" + luntJabberAccount + "] failed",
                    Project.MSG_WARN);
        }
    }

    /**
     * Logs out of a Jabber server connection.
     * 
     * @param connection the Jabber server connection
     * @param antProject the ant project used for logging purposes
     */
    private void logoffFromJabberServer(XMPPConnection connection, Project antProject)
    {
        if (connection != null)
        {
            try { Thread.sleep(250);} catch (InterruptedException ignored){}
            // wait 1/4 a second to make sure our message(s) are all sent
            connection.close();
            connection = null;
        }
    }

    /**
     * Sends a message with the specified Jabber server connection to the specified user's Jabber account.
     * 
     * @param connection the Jabber server connection
     * @param user the user with a Jabber account to send to
     * @param notificationMessage the message to send
     * @param antProject the ant project used for logging purposes
     */
    private void sendMessage(XMPPConnection connection, User user, String notificationMessage,
            Project antProject)
    {
        String userJabberAccount =
            ((NotifierProperty)getUserLevelProperties().get(USER_JABBER_ACCOUNT)).getValue(user.getContacts());

        if (Luntbuild.isEmpty(userJabberAccount))
            antProject.log("Can not send Jabber messages to user " + "\"" + user.getName()
                    + "\": Jabber account is empty!", Project.MSG_WARN);
        else
        {
            antProject.log("Call Jabber account \"" + userJabberAccount + "\"...", Project.MSG_INFO);

            sendNotification(connection, userJabberAccount, notificationMessage, antProject);
        }
    }

    /**
     * Sends a message with the specified Jabber server connection to the specified Jabber account.
     * 
     * @param connection the Jabber server connection
     * @param receiver the Jabber account to send to
     * @param message the message to send
     * @param antProject the ant project used for logging purposes
     */
    private void sendNotification(XMPPConnection connection, String receiver, String message,
            Project antProject)
    {
        try
        {
            connection.createChat(receiver).sendMessage(message);
        }
        catch (XMPPException e)
        {
            // e.printStackTrace();
            antProject.log("Error sending Message: " + e.getMessage());
        }
    }

	/**
	 * @inheritDoc
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

        NotifierProperty p = new NotifierProperty() {
            public Class getNotifierClass()
            {
                return JabberNotifier.class;
            }

            public String getDisplayName() {
                return "Jabber server type";
            }

            public String getDescription() {
                return "You can optionally specify the type of Jabber server "
                + "Luntbuild will communicate with. Valid entries are "
                + "NORMAL, SSL, and GOOGLE.";
            }

            public boolean isSelect() {
                return true;
            }

            public String getValue(Map properties) {
                String value = (String) properties.get(getKey());
                if (Luntbuild.isEmpty(value))
                    value = "0";
                return Constants.getJabberServerType(Integer.parseInt(value));
            }

            public void setValue(Map properties, String value) {
                properties.put(getKey(), Integer.toString(Constants.getJabberServerTypeValue(value)));
            }
        };
        // Set selection model
        p.setSelectionModel(new StringPropertySelectionModel(
                Constants.getJabberServerTypes()));
        // Add property to properties list
        properties.add(p);

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
	 * @inheritDoc
	 */
    public List getUserLevelProperties()
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
                return "Jabber account";
            }

            public String getDescription()
            {
                return "JabberID for this user, for example \"johndoe@jabber.org\".";
            }
        });
        return properties;
    }
}
