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

import com.luntsys.luntbuild.web.TabPageComponent;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This component implements a tab controller. It will take the responsibility of
 * switching between its child components in a tabbed page form. Tabs which can be
 * contained in this component should be inheritted from
 * {@link com.luntsys.luntbuild.web.TabPageComponent tab page class}
 *
 * @author alvin shen
 */
public abstract class TabControl extends BaseComponent{

	private Map tabs = new LinkedHashMap();

	/**
	 * The persistent property to record which tab is currently selected.
	 * NOTE: Instead of been defined as "property-specification" way in the specification file, we define this property
	 * to be persisted manually here to avoid some initialization tricks of tapestry, because it will be initialized to the first tab
	 * in the overrided {@link TabControl#addBody(org.apache.tapestry.IRender) addBody} method.  
	 */
	private String selectedTabId;

	/**
	 * Overrides this method to get the contained tab pages of this component
	 * @param element see {@link org.apache.tapestry.AbstractComponent#addBody(org.apache.tapestry.IRender) AbstractComponent.addBody}
	 */
	public void addBody(IRender element) {
		super.addBody(element);
		if (element instanceof TabPageComponent){
			TabPageComponent tab = (TabPageComponent)element;
			if (tabs.size() == 0 && getSelectedTabId() == null)
				setSelectedTabId(tab.getId());
			tabs.put(tab.getId(), tab);
		}
	}

	/**
	 * Get all tab pages inside this component
	 * @return
	 */
	public Map getTabs(){
		return tabs;
	}

	/**
	 * Select a tab page, the tab page is specified through DirectLink parameter
	 * @param cycle
	 */
	public void selectTab(IRequestCycle cycle){
		String tabId = (String) cycle.getServiceParameters()[0];
		setSelectedTabId(tabId);
		getSelectedTab().tabSelected();
	}

	public TabPageComponent getSelectedTab(){
		if (getSelectedTabId() == null)
			return null;
		else
			return (TabPageComponent) tabs.get(getSelectedTabId());
	}

	public String getTabColor(){
		if (getCurrentTab() == getSelectedTab())
			return "#CCCCCC";
		else
			return "#999999";
	}

	public abstract void setCurrentTab(TabPageComponent currentTabPage);

	public abstract TabPageComponent getCurrentTab();

	public String getSelectedTabId() {
		return selectedTabId;
	}

	public void setSelectedTabId(String selectedTabId) {
		this.selectedTabId = selectedTabId;
		Tapestry.fireObservedChange(this, "selectedTabId", selectedTabId);
	}
}
