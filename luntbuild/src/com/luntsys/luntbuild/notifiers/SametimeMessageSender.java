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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.lotus.sametime.commui.CommUI;
import com.lotus.sametime.commui.CommUIEvent;
import com.lotus.sametime.commui.CommUIListener;
import com.lotus.sametime.community.CommunityService;
import com.lotus.sametime.community.LoginEvent;
import com.lotus.sametime.community.LoginListener;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.constants.EncLevel;
import com.lotus.sametime.core.constants.ImTypes;
import com.lotus.sametime.core.types.STUser;
import com.lotus.sametime.im.Im;
import com.lotus.sametime.im.ImEvent;
import com.lotus.sametime.im.ImListener;
import com.lotus.sametime.im.InstantMessagingService;

/**
 * Sametime message sender and listener.
 *
 * @author inger
 */
public class SametimeMessageSender implements LoginListener, CommUIListener, ImListener {

    private static final long LOGIN_TIMEOUT = 10000;

    private STSession session;
    private CommunityService commService;
    private InstantMessagingService imService;
    private CommUI commUI;

    private String host;
    private String login;
    private String password;
    private Map messageMap;
    private Map imMap;
    private int count = 0;
    private boolean loggedIn = false;
    private RuntimeException runtimeException;

    /**
     * A Sametime message.
     */
    public static final class Message {
        private String partner;
        private String message;

        /**
         * Creates a Sametime message.
         * 
         * @param partner the partner
         * @param message the message
         */
        public Message(String partner, String message) {
            this.partner = partner;
            this.message = message;
        }

        /**
         * Gets the partner to send the message to.
         * 
         * @return the partner
         */
        public String getPartner() {
            return partner;
        }

        /**
         * Gets the message.
         * 
         * @return the message
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * Creates a Samtime message sender with a partner and a message.
     * 
     * @param host the Sametime host
     * @param login the login name/id
     * @param password the login password
     * @param partner the partner
     * @param message the message
     */
    public SametimeMessageSender(String host,
                                 String login,
                                 String password,
                                 String partner,
                                 String message) {
        this(host, login, password, new Message(partner, message));
    }

    /**
     * Creates a Samtime message sender with a Sametime message.
     * 
     * @param host the Sametime host
     * @param login the login name/id
     * @param password the login password
     * @param message the Sametime message
     */
    public SametimeMessageSender(String host,
                                 String login,
                                 String password,
                                 Message message) {
        this(host, login, password, new Message[]{message});
    }

    /**
     * Creates a Samtime message sender with a list of Sametime messages.
     * 
     * @param host the Sametime host
     * @param login the login name/id
     * @param password the login password
     * @param messages the list of Sametime messages
     */
    public SametimeMessageSender(String host,
                                 String login,
                                 String password,
                                 Message messages[]) {
        this.host = host;
        this.login = login;
        this.password = password;

        this.messageMap = new HashMap();
        for (int i = 0; i < messages.length; i++) {
            List m = (List) messageMap.get(messages[i].getPartner());
            if (m == null) {
                m = new ArrayList();
                messageMap.put(messages[i].getPartner(), m);
            }
            m.add(messages[i]);
        }

        this.imMap = new HashMap();
    }

    /**
     * Sends all Sametime messages stored in this Sametime message sender.
     * 
     * @throws RuntimeException if an error occurs
     */
    public void send() {
        try {
            session = new STSession("Luntbuild" + this);
            session.loadAllComponents();
            session.start();
            commService = (CommunityService)
                    session.getCompApi(CommunityService.COMP_NAME);
            commService.addLoginListener(this);

            imService = (InstantMessagingService)
                    session.getCompApi(InstantMessagingService.COMP_NAME);
            imService.registerImType(ImTypes.IM_TYPE_CHAT);

            commUI = (CommUI)
                    session.getCompApi(CommUI.COMP_NAME);
            commUI.addCommUIListener(this);

            commService.loginByPassword(host,
                                        login,
                                        password);

            long start = System.currentTimeMillis();
            while (count != messageMap.size()) {
                try {
                    Thread.sleep(200);
                }
                catch (InterruptedException e) {

                }
                if (runtimeException != null) {
                    throw runtimeException;
                }
                if (!loggedIn) {
                    long current = System.currentTimeMillis();
                    long elapsed = current - start;
                    if (elapsed >= LOGIN_TIMEOUT) {
                        throw new RuntimeException("Login to Sametime exceeded the timeout threshold");
                    }
                }
            }

            imService.unregisterImType(ImTypes.IM_TYPE_CHAT);
            commService.logout();
            session.stop();
            session.unloadSession();
            imService = null;
            commService = null;
            session = null;
        }
        catch (DuplicateObjectException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Executes after succesfully logging in.
     * 
     * @param event the login event
     */
    public void loggedIn(LoginEvent event) {
        loggedIn = true;
        Iterator it = messageMap.keySet().iterator();
        while (it.hasNext()) {
            String partner = (String) it.next();
            commUI.resolve(partner, true, false);
        }
    }

    /**
     * Executes after logging out.
     * 
     * @param event the login event
     */
    public void loggedOut(LoginEvent event) {
    }

    /**
     * Executes when a failed name resolution event occurs.
     * 
     * @param event the event
     */
    public void resolveFailed(CommUIEvent event) {
        runtimeException = new RuntimeException("failed to resolve name: " + event.getName());
    }

    /**
     * Executes when a successful name resolution event occurs.
     * 
     * @param event the event
     */
    public void resolved(CommUIEvent event) {
        STUser stUser = event.getUser();
        Im im = imService.createIm(stUser,
                                   EncLevel.ENC_LEVEL_NONE,
                                   ImTypes.IM_TYPE_CHAT);
        imMap.put(im, messageMap.get(event.getName()));
        im.addImListener(this);
        im.open();
    }

    /**
     * Executes when a data received event occurs.
     * 
     * @param event the event
     */
    public void dataReceived(ImEvent event) {
    }

    /**
     * Executes when a text received event occurs.
     * 
     * @param event the event
     */
    public void textReceived(ImEvent event) {
    }

    /**
     * Executes when an IM closed event occurs.
     * 
     * @param event the event
     */
    public void imClosed(ImEvent event) {
        imMap.remove(event.getIm());
    }

    /**
     * Executes when a successful IM opened event occurs.
     * 
     * @param event the event
     */
    public void imOpened(ImEvent event) {
        List messages = (List) imMap.get(event.getIm());

        Iterator it = messages.iterator();
        while (it.hasNext()) {
            Message m = (Message) it.next();
            event.getIm().sendText(false, m.getMessage());
        }
        event.getIm().close(0);
        count++;
    }

    /**
     * Executes when a failed IM opened event occurs.
     * 
     * @param event the event
     */
    public void openImFailed(ImEvent event) {
        runtimeException = new RuntimeException("failed to open IM connection");
    }
}
