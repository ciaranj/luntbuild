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

import org.apache.tapestry.IRequestCycle;

import java.util.List;

import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;

/**
 * This tab shows information about projects in the system
 *
 * @author robin shine
 */
public abstract class ProjectsTab extends TabPageComponent {

	public String getTabName() {
		return "projects";
	}

	public void tabSelected() {
		// refresh data on select
		loadData();
	}

	/**
	 * Loads projects information from persistence layer
	 */
	public void loadData(){
		setProjects(Luntbuild.getDao().loadProjects());
	}

	/**
	 * create a new project
	 * @param cycle
	 */
	public void createProject(IRequestCycle cycle){
		ProjectPage page = (ProjectPage)cycle.getPage("ProjectPage");
		page.createProject();
		cycle.activate(page);
	}

	/**
	 * Load the project page to show details of a particular project
 	 * @param cycle
	 */
	public void loadProject(IRequestCycle cycle){
		long projectId = ((Long)cycle.getServiceParameters()[0]).longValue();
		ProjectPage page = (ProjectPage)cycle.getPage("ProjectPage");
		page.loadProject(projectId);
		TabControl tabs = (TabControl) page.getComponent("tabs");
		tabs.setSelectedTabId("basic");		
		cycle.activate(page);
	}

	public void deleteProject(IRequestCycle cycle){
		long projectId = ((Long)cycle.getServiceParameters()[0]).longValue();
		setProjectToBeDelete(Luntbuild.getDao().loadProject(projectId));
		setCurrentAction("delete");
	}

	/**
	 * Actually delete the project
	 * @param cycle
	 */
	public void confirmDelete(IRequestCycle cycle){
		Luntbuild.getDao().deleteProject(getProjectToBeDelete().getId());
		setProjectToBeDelete(null);
		loadData();
		Luntbuild.getSchedService().rescheduleBuilds();
		setCurrentAction(null);
	}

	/**
	 * Cancel the deletion of the project
	 * @param cycle
	 */
	public void cancelDelete(IRequestCycle cycle){
		setProjectToBeDelete(null);
		loadData();
		setCurrentAction(null);
	}

	public abstract void setProjects(List projects);

	public abstract List getProjects();

	public abstract void setCurrentProject(Project project);

	public abstract Project getCurrentProject();

	public abstract void setCurrentAction(String currentAction);

	public abstract String getCurrentAction();

	public abstract void setProjectToBeDelete(Project project);

	public abstract Project getProjectToBeDelete();

	public abstract int getCurrentProjectIndex();

	/**
	 * Get css class for current row
	 * @return
	 */
	public String getRowClass() {
		if (getCurrentProjectIndex() % 2 == 0)
			return "dataTableRow1";
		else
			return "dataTableRow2";
	}

}
