/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-9-16
 * Time: 8:41
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

import com.luntsys.luntbuild.utility.Luntbuild;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * This class stands for a tapestry component. It responsible for
 * move builds page in the system
 *
 * @author robin shine
 */
public abstract class MoveBuilds extends BaseComponent {
	public abstract void setErrorMsg(String errorMsg);

	public IPropertySelectionModel getSchedulesModel() {
		return new com.luntsys.luntbuild.web.selectionmodels.ScheduleSelectionModel(Luntbuild.getDao().loadSchedules());
	}

	public BuildsTab getBuildsTab() {
		return (BuildsTab) getContainer();
	}

	public void move(IRequestCycle cycle) {
		getBuildsTab().ensureCurrentTab();
		if (getScheduleId() == 0) {
			setErrorMsg("No destination schedule selected");
			return;
		}
		if (getLastAction().equals("listBuilds")) {
			BuildList buildList = (BuildList) getContainer().getComponent("buildListComponent");
			Luntbuild.getDao().moveBuilds(buildList.getSearchCriteria(), getScheduleId());
		} else {
			BuildViewer buildViewer = (BuildViewer) getContainer().getComponent("buildViewerComponent");
			Luntbuild.getDao().moveBuild(buildViewer.getBuildId(), getScheduleId());
		}
		BuildsTab buildsTab = (BuildsTab) getContainer();
		buildsTab.tabSelected();
	}

	public void cancel(IRequestCycle cycle) {
		getBuildsTab().ensureCurrentTab();
		BuildsTab buildsTab = (BuildsTab) getContainer();
		if (getLastAction() == null)
			buildsTab.tabSelected();
		else
			buildsTab.setAction(getLastAction());
	}

	public abstract void setLastAction(String lastAction);

	public abstract String getLastAction();

	public abstract long getScheduleId();

	public abstract void setScheduleId(long scheduleId);
}
