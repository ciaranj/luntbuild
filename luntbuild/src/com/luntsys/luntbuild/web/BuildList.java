/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-9-2
 * Time: 15:12
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

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.SearchCriteria;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.components.pagenavigator.PageNavigator;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;

import java.util.ArrayList;
import java.util.List;

/**
 * This component renders a list of builds
 *
 * @author robin shine
 */
public abstract class BuildList extends BaseComponent {
	public static final int PAGE_SIZE = 50;

	public String getBuildStatusGif(){
		if (getBuild().getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_SUCCESS)
			return "images/success.gif";
		else if (getBuild().getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_FAILED)
			return "images/failed.gif";
		else
			return "images/running.gif";
	}

	public String getBuildCostTime(){
		if (getBuild().getStartDate() == null)
			return "";
		if (getBuild().getEndDate() == null)
			return String.valueOf((System.currentTimeMillis() - getBuild().getStartDate().getTime()) / 60000);
		return String.valueOf((getBuild().getEndDate().getTime() - getBuild().getStartDate().getTime()) / 60000);
	}

	/**
	 * Called by page navigator component to show another page of builds
	 * @param cycle
	 */
	public void changePage(IRequestCycle cycle){
		getBuildsTab().ensureCurrentTab();
	}

	public BuildsTab getBuildsTab() {
		return (BuildsTab) getContainer();
	}

	/**
	 * Initialize the page
	 */
	public void initPage(IRequestCycle cycle){
		getBuildsTab().ensureCurrentTab();
		PageNavigator pageNavigator = (PageNavigator) getComponent("pageNavigator");
		setNumTotalBuilds(Luntbuild.getDao().countBuilds(getSearchCriteria()));
		if (getNumTotalBuilds() % PAGE_SIZE == 0)
			pageNavigator.setTotalPages(getNumTotalBuilds() / PAGE_SIZE);
		else
			pageNavigator.setTotalPages(getNumTotalBuilds() / PAGE_SIZE + 1);
		pageNavigator.setCurrentPage(1);
		changePage(cycle);
	}

	public List getBuilds(){
		PageNavigator pageNavigator = (PageNavigator) getComponent("pageNavigator");
		int totalPages = pageNavigator.getTotalPages();
		int currentPage = pageNavigator.getCurrentPage();
		if (currentPage <= 0 || currentPage > totalPages)
			return new ArrayList();
		else
			return Luntbuild.getDao().searchBuilds(getSearchCriteria(),
					PAGE_SIZE * (currentPage - 1), PAGE_SIZE);
	}

	public abstract Build getBuild();

	public abstract void setBuild(Build build);

	public abstract int getNumTotalBuilds();

	public abstract void setNumTotalBuilds(int numTotalBuilds);

	public abstract void setSearchCriteria(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria);

	public abstract com.luntsys.luntbuild.facades.SearchCriteria getSearchCriteria();

	public String getBuildEndDate() {
		if (getBuild().getEndDate() == null)
			return "";
		else
			return Luntbuild.DATE_DISPLAY_FORMAT.format(getBuild().getEndDate());
	}

	public abstract int getBuildIndex();

	public String getRowCssClass() {
		if (getBuildIndex() % 2 == 0)
			return "dataTableRow dataTableRow1";
		else
			return "dataTableRow dataTableRow2";
	}
}