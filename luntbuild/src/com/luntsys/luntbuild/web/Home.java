/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-3-15
 * Time: 19:58:11
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

import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IRequestCycle;

/**
 * The start page for a tapestry web application
 *
 * @author robin shine
 */
public class Home extends HierarchyPage {
	/**
	 * Constants to identify meaning of the first service param if that
	 * value is negative.
	 */
	public static final long SERVICE_PARAMETER_HOME = 0;
	public static final long SERVICE_PARAMETER_BUILD = 1;

	public PageInfo[] getHierarchyPageInfos() {
		return new PageInfo[0];
	}

	public String getPageDataDescription() {
		return "Home";
	}

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		TabControl tabs = (TabControl) getComponent("tabs");
		if (parameters == null) { // activated by first enter home page, or by restart session, or page refresh
			TabPageComponent selectedTab = (TabPageComponent) tabs.getSelectedTab();
			if (!(selectedTab instanceof BuildsTab))
				selectedTab.tabSelected(); // notify selected tab
		} else {
			long actionCode = ((Long) parameters[0]).longValue();
			if (actionCode == SERVICE_PARAMETER_HOME) { // activated by clicking the Home link in the navigation area
				TabPageComponent selectedTab = (TabPageComponent) tabs.getSelectedTab();
				selectedTab.tabSelected();
			} else if (actionCode == SERVICE_PARAMETER_BUILD) { // try to show a specific build
				tabs.setSelectedTabId("builds");
				BuildsTab buildsTab = (BuildsTab) tabs.getSelectedTab();
				cycle.setServiceParameters(new Long[]{(Long) parameters[1]});
				buildsTab.viewBuild(cycle);
			} else {
				throw new ApplicationRuntimeException("Invalid action code for Home page: " + actionCode);
			}
		}
	}

	public void pageActivated(IRequestCycle cycle) {
		activateExternalPage(cycle.getServiceParameters(), cycle);
	}

	public int getRefreshInterval() {
		TabControl tabs = (TabControl) getComponent("tabs");
		if (tabs.getSelectedTabId().equals("builds")) {
			return ((BuildsTab) tabs.getSelectedTab()).getRefreshInterval();
		} else
			return 0;
	}
}