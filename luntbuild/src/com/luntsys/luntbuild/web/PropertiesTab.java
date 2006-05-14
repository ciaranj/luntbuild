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

import com.luntsys.luntbuild.notifiers.Notifier;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;
import org.apache.tapestry.IRequestCycle;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This component shows and edits system wide properties
 *
 * @author robin shine
 */
public abstract class PropertiesTab extends TabPageComponent {
	public String getTabName() {
		return "properties";
	}

	public void tabSelected() {
		setProperties(Luntbuild.getDao().loadProperties());
	}

	public void update(IRequestCycle cycle){
		Iterator it = getProperties().keySet().iterator();
		while (it.hasNext()) {
			String property = (String) it.next();
			String value = (String) getProperties().get(property);
			if (value != null)
				getProperties().put(property, value.trim());
		}
		Luntbuild.getDao().saveProperties(getProperties());
		TabControl tabs = (TabControl) getPage().getComponent("tabs");
		tabs.setSelectedTabId("builds");
		TabPageComponent selectedTab = (TabPageComponent)tabs.getSelectedTab();
		selectedTab.tabSelected(); // notify selected tab
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public abstract void setProperties(Map properties);

	public abstract Map getProperties();

	public abstract Notifier getCurrentNotifier();

	public String getNotifierTitle() {
		String title = "Set " + getCurrentNotifier().getDisplayName() +
				" related properties here if you want to notify user through " +
				getCurrentNotifier().getDisplayName() + ".";
		if (Luntbuild.isEmpty(getCurrentNotifier().getComment()))
			return title;
		else
			return title + " " + getCurrentNotifier().getComment();
	}

	public abstract int getCssIndex();

	public String getNotifierTitleCssClass() {
		if (getCssIndex() % 2 == 0)
			return "notifierTitle1";
		else
    		return "notifierTitle2";
	}

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorName1";
		else
			return "propertyEditorName2";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorValue1";
		else
			return "propertyEditorValue2";
	}

	public String getTailCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorTail1";
		else
			return "propertyEditorTail2";
	}

	public List getNotifierInstances() {
		return Luntbuild.getNotifierInstances(Luntbuild.notifiers);
	}
}