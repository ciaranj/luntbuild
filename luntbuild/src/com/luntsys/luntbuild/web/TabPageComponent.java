/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-1
 * Time: 12:12:55
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

import com.luntsys.luntbuild.web.components.SecuritySupportComponent;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;

/**
 * base class for all tabs, such as ${@link ProjectsTab}
 *
 * @author robin shine
 */
public abstract class TabPageComponent extends SecuritySupportComponent {
	/**
	 * @return a string value represents name of this tab, which will be shown as
	 * the tab header in {@link com.luntsys.luntbuild.web.components.tabcontrol.TabControl}
	 */
	public abstract String getTabName();

	/**
	 * This method will get called after the tabcontrol selects a tab, sub-class can implement this method
	 * to do some extra actions after it has been selected.
	 */
	public void tabSelected(){};

	protected void ensureCurrentTab() {
		TabControl tabs = (TabControl) getPage().getComponent("tabs");
		tabs.setSelectedTabId(getId());
	}

	/**
	 * @return true if this tab should be visible
	 */
	public boolean isVisible() {
		return true;
	}
}
