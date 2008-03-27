/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-9-2
 * Time: 15:13
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

import com.luntsys.luntbuild.facades.SearchCriteria;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.db.Schedule;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;

import java.util.List;
import java.util.Iterator;

/**
 * This class stands for a tapestry component. It responsible for
 * search builds in the system
 *
 * @author robin shine
 */
public abstract class SearchBuilds extends BaseComponent implements PageDetachListener {
	private List schedules;

	public abstract void setErrorMsg(String errorMsg);

	public abstract String getFromError();

	public abstract void setFromError(String fromError);

	public abstract String getToError();

	public abstract void setToError(String toError);

	public abstract void setSearchCriteria(SearchCriteria searchCriteria);

	public abstract SearchCriteria getSearchCriteria();

	public List getSchedules() {
		if (schedules == null) {
			schedules = Luntbuild.getDao().loadSchedules();
			// set all schedules to be selected initially
			Iterator it = schedules.iterator();
			while (it.hasNext()) {
				Schedule schedule = (Schedule) it.next();
				schedule.setSelected(true);
			}
		}
		return schedules;
	}

	public void finishLoad(IRequestCycle iRequestCycle, IPageLoader iPageLoader, IComponentSpecification iComponentSpecification) {
		super.finishLoad(iRequestCycle, iPageLoader, iComponentSpecification);
		schedules = null;
	}

	public void pageDetached(PageEvent event) {
		schedules = null;
	}

	public IPropertySelectionModel getBuildStatusModel() {
		return new com.luntsys.luntbuild.web.selectionmodels.BuildStatusSelectionModel();
	}

	public Integer getBuildStatus() {
		return new Integer(getSearchCriteria().getStatus());
	}

	public void setBuildStatus(Integer buildStatus) {
		getSearchCriteria().setStatus(buildStatus.intValue());
	}
	
	public void search(IRequestCycle cycle) {
		getBuildsTab().ensureCurrentTab();
		if (getFromError() != null) {
			setErrorMsg("Error in the property \"from\":" + getFromError());
			return;
		}
		if (getToError() != null) {
			setErrorMsg("Error in the property \"to\":" + getToError());
			return;
		}
		if (getSearchCriteria().getVersion() != null)
			getSearchCriteria().setVersion(getSearchCriteria().getVersion().trim());
		Iterator it = getSchedules().iterator();
		int numSelectedSchedules = 0;
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			if (schedule.isSelected())
				numSelectedSchedules ++;
		}
		getSearchCriteria().setScheduleIds(new long[numSelectedSchedules]);
		it = getSchedules().iterator();
		int index = 0;
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			if (schedule.isSelected())
				getSearchCriteria().getScheduleIds()[index++]=schedule.getId();
		}
		BuildList buildList = (BuildList) getContainer().getComponent("buildListComponent");
		buildList.setSearchCriteria(getSearchCriteria());
		buildList.initPage(cycle);
		BuildsTab buildsTab = (BuildsTab) getContainer();
		buildsTab.setAction("listBuilds");
	}

	public BuildsTab getBuildsTab() {
		return (BuildsTab) getContainer();
	}

	public void cancel(IRequestCycle cycle) {
		getBuildsTab().ensureCurrentTab();
		getBuildsTab().setAction(getLastAction());
	}

	public abstract void setLastAction(String lastAction);

	public abstract String getLastAction();
}
