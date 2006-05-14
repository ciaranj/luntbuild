/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-29
 * Time: 6:46:18
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
package com.luntsys.luntbuild.web.components.tabcontrol;

import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.TabPageComponent;
import org.apache.tapestry.*;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This component implements a tab controller. It will take the responsibility of
 * switching between its child components in a tabbed page form. Tabs which can be
 * contained in this component should be inheritted from
 * {@link com.luntsys.luntbuild.web.TabPageComponent tab page class}
 *
 * @author robin shine
 */
public abstract class TabControl extends BaseComponent implements PageDetachListener {
	private List tabs = new ArrayList();
	private String selectedTabId;

	/**
	 * Overrides this method to get the contained tab pages of this component
	 *
	 * @param element see {@link org.apache.tapestry.AbstractComponent#addBody(org.apache.tapestry.IRender) AbstractComponent.addBody}
	 */
	public void addBody(IRender element) {
		super.addBody(element);
		if (element instanceof TabPageComponent) {
			TabPageComponent tab = (TabPageComponent) element;
			tabs.add(tab);
		}
	}

	/**
	 * Select a tab page, the tab page is specified through DirectLink parameter
	 *
	 * @param cycle
	 */
	public void selectTab(IRequestCycle cycle) {
		String tabId = (String) cycle.getServiceParameters()[0];
		setSelectedTabId(tabId);
		getSelectedTab().tabSelected();
	}

	public TabPageComponent getSelectedTab() {
		if (getSelectedTabId() == null) {
			if (tabs.size() == 0)
				throw new ApplicationRuntimeException("No tabs in current tab control!");
			return (TabPageComponent) tabs.get(0);
		}
		Iterator it = tabs.iterator();
		while (it.hasNext()) {
			TabPageComponent tab = (TabPageComponent) it.next();
			if (tab.getId().equals(getSelectedTabId()))
				return tab;
		}
		throw new ApplicationRuntimeException("Tab \" " + getSelectedTabId() + "\" not found in current tab control!");
	}

	public String getTabClass() {
		if (getCurrentTab() == getSelectedTab())
			return "selectedTab";
		else
			return "unselectedTab";
	}

	public abstract void setCurrentTab(TabPageComponent currentTabPage);

	public abstract TabPageComponent getCurrentTab();

	public String getSelectedTabId() {
		if (selectedTabId == null) {
			if (tabs.size() == 0)
				throw new ApplicationRuntimeException("No tabs in current tab control!");
			return ((TabPageComponent) tabs.get(0)).getId();
		} else
			return selectedTabId;
	}

	public void setSelectedTabId(String selectedTabId) {
		this.selectedTabId = selectedTabId;
		Tapestry.fireObservedChange(this, "selectedTabId", selectedTabId);
	}

	public List getTabs() {
		return tabs;
	}

	public void showSystemLog(IRequestCycle cycle) {
		Luntbuild.sendFile(cycle, Luntbuild.installDir + "/logs/" + Luntbuild.log4jFileName);
	}

	public void finishLoad(IRequestCycle cycle, IPageLoader loader, IComponentSpecification specification) {
		super.finishLoad(cycle, loader, specification);
		selectedTabId = null;
	}

	public void pageDetached(PageEvent event) {
		selectedTabId = null;
	}
}
