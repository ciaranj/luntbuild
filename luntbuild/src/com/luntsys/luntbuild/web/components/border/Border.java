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
package com.luntsys.luntbuild.web.components.border;

import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.HierarchyPage;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IRequestCycle;

/**
 * The common border for lunt applications, it uses the RenderBody component to achieve
 * templating-like page. This border will extract special hierarchical informations from pages include it,
 * So every page include this component should extends from the {@link com.luntsys.luntbuild.web.HierarchyPage}
 *
 * @author robin shine
 */
public abstract class Border extends BaseComponent {

	/**
	 * Do some initializations before render.
	 */
	protected void finishLoad() {
		super.finishLoad();
	}

	public String getTitle(){
		return "luntbuild - make your software building managable";
	}

	public String getProductVersion(){
		String version = Luntbuild.buildInfos.getProperty("buildVersion");
		if (version == null || version.trim().equals(""))
			return "luntbuild";
		else
			return version;
	}
	
	public IAsset getStylesheet(){
		return getAsset("stylesheet");
	}

	public String getProductDate(){
		String date = Luntbuild.buildInfos.getProperty("buildDate");
		if (date == null || date.trim().equals(""))
			return "2001~2004";
		else
			return date;
	}

	public abstract boolean isAutoRefresh();

	public abstract void setAutoRefresh(boolean autoRefresh);

	public void switchRefreshStatus(IRequestCycle cycle) {
		setAutoRefresh(!isAutoRefresh());
		cycle.getPage().pageActivated(cycle);
	}

	public int getRefreshInterval() {
		if (isAutoRefresh())
			return  ((HierarchyPage)getPage()).getRefreshInterval();
		else
			return 0;
	}

	public String getRefreshSwitchGif() {
		if (isAutoRefresh())
			return "images/disable_refresh.gif";
		else
			return "images/enable_refresh.gif";
	}

	public String getRefreshSwitchText() {
		if (isAutoRefresh())
			return "turnoff refresh";
		else
			return "turnon refresh";
	}

	public String getRefreshStatusText() {
		if (isAutoRefresh())
			return "REFRESH IS ON";
		else
			return "REFRESH IS OFF";
	}
}
