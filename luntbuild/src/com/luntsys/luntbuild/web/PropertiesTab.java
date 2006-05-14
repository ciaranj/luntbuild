/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-19
 * Time: 10:17:46
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

import com.luntsys.luntbuild.utility.LuntBuild;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;

import java.util.Map;

import org.apache.tapestry.IRequestCycle;

/**
 * This component shows and edits system wide properties
 *
 * @author alvin shen
 */
public abstract class PropertiesTab extends TabPageComponent {
	public String getTabName() {
		return "properties";
	}

	public void tabSelected() {
		setProperties(LuntBuild.getDao().loadProperties());
	}

	public void update(IRequestCycle cycle){
		LuntBuild.getDao().saveProperties(getProperties());
		TabControl tabs = (TabControl) getPage().getComponent("tabs");
		tabs.setSelectedTabId("projects");
		TabPageComponent selectedTab = (TabPageComponent)tabs.getSelectedTab();
		selectedTab.tabSelected(); // notify selected tab
		LuntBuild.getService().rescheduleBuilds();
	}

	public abstract void setProperties(Map properties);

	public abstract Map getProperties();
}
