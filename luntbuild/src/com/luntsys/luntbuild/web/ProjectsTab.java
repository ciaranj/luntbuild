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
package com.luntsys.luntbuild.web;

import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.utility.Luntbuild;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.spec.IComponentSpecification;

import java.util.List;

/**
 * This tab shows information about projects in the system
 *
 * @author robin shine
 */
public abstract class ProjectsTab extends TabPageComponent implements PageDetachListener {
	private List projects;

	public void finishLoad(IRequestCycle iRequestCycle, IPageLoader iPageLoader, IComponentSpecification iComponentSpecification) {
		super.finishLoad(iRequestCycle, iPageLoader, iComponentSpecification);
		projects = null;
	}

	public void pageDetached(PageEvent event) {
		projects = null;
	}

	public String getTabName() {
		return "Projects";
	}

	public void tabSelected() {
		setAction(null);
	}

	/**
	 * create a new project
	 * @param cycle
	 */
	public void createProject(IRequestCycle cycle){
		ProjectPage page = (ProjectPage)cycle.getPage("ProjectPage");
		page.setProjectId(0);
		page.setProjectIdToCopy(0);
		page.gotoBasicTab(cycle);
		cycle.activate(page);
	}

	public void copyProject(IRequestCycle cycle) {
		ProjectPage page = (ProjectPage) cycle.getPage("ProjectPage");
		long projectIdToCopy = ((Long)cycle.getServiceParameters()[0]).longValue();
		page.setProjectId(0);
		page.setProjectIdToCopy(projectIdToCopy);
		page.gotoBasicTab(cycle);
		cycle.activate(page);
	}

	/**
	 * Load the project page to show details of a particular project
 	 * @param cycle
	 */
	public void gotoProject(IRequestCycle cycle){
		long projectId = ((Long)cycle.getServiceParameters()[0]).longValue();
		ProjectPage page = (ProjectPage)cycle.getPage("ProjectPage");
		page.setProjectId(projectId);
		page.gotoBasicTab(cycle);
		cycle.activate(page);
	}

	public void deleteProject(IRequestCycle cycle){
		ensureCurrentTab();
		long projectId = ((Long)cycle.getServiceParameters()[0]).longValue();
		setProjectIdToDelete(projectId);
		setAction("deleteProject");
	}

	/**
	 * Actually delete the project
	 * @param cycle
	 */
	public void confirmDelete(IRequestCycle cycle){
		ensureCurrentTab();
		if (getProjectIdToDelete() == 0) {
			return;
		}
		Luntbuild.getDao().deleteProject(Luntbuild.getDao().loadProject(getProjectIdToDelete()));
		setProjectIdToDelete(0);
		Luntbuild.getSchedService().rescheduleBuilds();
		setAction(null);
	}

	/**
	 * Cancel the deletion of the project
	 * @param cycle
	 */
	public void cancelDelete(IRequestCycle cycle){
		ensureCurrentTab();
		setProjectIdToDelete(0);
		setAction(null);
	}

	public List getProjects() {
		if (projects == null)
			projects = Luntbuild.getDao().loadProjects();
		return projects;
	}

	public abstract void setProject(Project project);

	public abstract Project getProject();

	public abstract void setAction(String action);

	public abstract String getAction();

	public abstract void setProjectIdToDelete(long projectIdToDelete);

	public abstract long getProjectIdToDelete();

	public abstract int getProjectIndex();

	/**
	 * Get css class for current row
	 * @return
	 */
	public String getRowClass() {
		if (getProjectIndex() % 2 == 0)
			return "dataTableRow dataTableRow1";
		else
			return "dataTableRow dataTableRow2";
	}
}
