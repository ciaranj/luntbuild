/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-3-17
 * Time: 20:45:00
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
package com.luntsys.luntbuild.web;

import org.apache.tapestry.IRequestCycle;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;

/**
 * Utility class for web pages
 * @author robin shine
 */
public class PageUtils {
	public static void gotoUser(IRequestCycle cycle) {
		long userId = ((Long) cycle.getServiceParameters()[0]).longValue();
		Home homePage = (Home) cycle.getPage("Home");
		TabControl tabs = (TabControl) homePage.getComponent("tabs");
		tabs.setSelectedTabId("users");
		UsersTab usersTab = (UsersTab) homePage.getComponent("users");
		usersTab.setAction("viewUser");
		UserViewer userViewer = (UserViewer) usersTab.getComponent("userViewerComponent");
		userViewer.setUserId(userId);
		cycle.activate(homePage);		
	}
}
