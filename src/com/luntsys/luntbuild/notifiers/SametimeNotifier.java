/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-10
 * Time: 9:54:34
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Notifier class which uses the Sametime protocol.
 *
 * @author		inger
 */

public class SametimeNotifier extends TemplatedNotifier {

    private static final int USER_SAMETIME_NAME = 0;

    /**
     * Constructor
     */
    public SametimeNotifier() {
        super(SametimeNotifier.class, "sametime");
    }

    public String getDisplayName() {
        return "Sametime";
    }

    public String getComment() {
        return super.getComment();
    }

    private void sendMessages(Project antProject, List messages) {
        NotifierProperty property = (NotifierProperty) getSystemLevelProperties().get(0);
        String host = property.getValue(Luntbuild.getProperties());

        property = (NotifierProperty) getSystemLevelProperties().get(1);
        String login = property.getValue(Luntbuild.getProperties());

        property = (NotifierProperty) getSystemLevelProperties().get(2);
        String password = property.getValue(Luntbuild.getProperties());

        SametimeMessageSender.Message messageArray[] =
                (SametimeMessageSender.Message[])
                messages.toArray(new SametimeMessageSender.Message[messages.size()]);

        SametimeMessageSender sender = new SametimeMessageSender(host,
                                                                 login,
                                                                 password,
                                                                 messageArray);
        sender.send();
    }

    private String getSametimeName(User user) {
        String partner =
            ((NotifierProperty)getUserLevelProperties().get(USER_SAMETIME_NAME)).getValue(user.getContacts());
        return partner;
    }

    public void sendBuildNotification(Set checkinUsers,
                                      Set subscribeUsers,
                                      Build build,
                                      Project project) {
        List messages = new ArrayList();

        Iterator it = checkinUsers.iterator();
        String msg = constructNotificationBody4CheckinUsers(build);
        while (it.hasNext()) {
            User user = (User) it.next();
            String sametimeName = getSametimeName(user);
            if (Luntbuild.isEmpty(sametimeName))
                project.log("Can not send Sametime messages to user " + "\"" + user.getName()
                            + "\": Sametime account is empty!", Project.MSG_WARN);
            else {
                project.log("Call Samtime account \"" + sametimeName + "\"...", Project.MSG_INFO);
                messages.add(new SametimeMessageSender.Message(sametimeName,
                                                               msg));
            }
        }

        it = subscribeUsers.iterator();
        msg = constructNotificationBody(build);
        while (it.hasNext()) {
            User user = (User) it.next();
            String sametimeName = getSametimeName(user);
            if (Luntbuild.isEmpty(sametimeName))
                project.log("Can not send Sametime messages to user " + "\"" + user.getName()
                            + "\": Sametime account is empty!", Project.MSG_WARN);
            else {
                project.log("Call Samtime account \"" + sametimeName + "\"...", Project.MSG_INFO);
                messages.add(new SametimeMessageSender.Message(sametimeName,
                                                               msg));
            }
        }

        sendMessages(project, messages);
    }

    public void sendScheduleNotification(Set subscribeUsers,
                                         Schedule schedule,
                                         Project project) {

        List messages = new ArrayList();

        Iterator it = subscribeUsers.iterator();
        String msg = constructNotificationBody(schedule);
        while (it.hasNext()) {
            User user = (User) it.next();
            String sametimeName = getSametimeName(user);
            if (Luntbuild.isEmpty(sametimeName))
                project.log("Can not send Sametime messages to user " + "\"" + user.getName()
                            + "\": Sametime account is empty!", Project.MSG_WARN);
            else {
                project.log("Call Samtime account \"" + sametimeName + "\"...", Project.MSG_INFO);
                messages.add(new SametimeMessageSender.Message(sametimeName,
                                                               msg));
            }
        }

        sendMessages(project, messages);
    }

    public List getSystemLevelProperties() {
        List properties = new ArrayList();

        properties.add(new NotifierProperty() {

            public Class getNotifierClass() {
                return SametimeNotifier.class;
            }

            public String getDisplayName() {
                return "Sametime Server";
            }

            public String getDescription() {
                return "You can optionally specify the Sametime host used by "
                       + "Luntbuild to send Sametime messages. If this property is not specified, "
                       + "Luntbuild will use localhost as the default value.";
            }
        });
        properties.add(new NotifierProperty() {

            public Class getNotifierClass() {
                return SametimeNotifier.class;
            }

            public String getDisplayName() {
                return "Sametime User";
            }

            public String getDescription() {
                return "The Sametime account name to be used for Luntbuild to login and send messages.";
            }
        });
        properties.add(new NotifierProperty() {

            public Class getNotifierClass() {
                return SametimeNotifier.class;
            }

            public boolean isSecret() {
                return true;
            }

            public String getDisplayName() {
                return "Sametime Password";
            }

            public String getDescription() {
                return "The Sametime account password to login.";
            }
        });

        return properties;
    }

    public List getUserLevelProperties() {
        List properties = new ArrayList();
        properties.add(new NotifierProperty() {

            public Class getNotifierClass() {
                return SametimeNotifier.class;
            }

            public String getDisplayName() {
                return "Sametime account";
            }

            public String getDescription() {
                return "Sametime id for this user, for example \"johndoe\".";
            }
        });
        return properties;
    }
}
