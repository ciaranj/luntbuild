/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-30
 * Time: 8:36:29
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

import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;
import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.vcs.VcsAdaptorSelectionModel;
import com.luntsys.luntbuild.vcs.Vcs;
import com.luntsys.luntbuild.vcs.VcsAdaptorProperty;
import com.luntsys.luntbuild.LuntBuildEngine;
import com.luntsys.luntbuild.utility.LuntBuild;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.Tapestry;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * this page is used to show aspects of a specific project. informations be shown at this page
 * includes: project settings, configured views, etc.
 *
 * @author alvin shen
 */
public abstract class ProjectPage extends HierarchyPage{
	public String getPageDataDescription() {
		if (getProject().getId() == 0)
			return "project-creating...";
		else {
			return "project(" + getProject().getName() + ")";
		}
	}

	public PageInfo[] getHierarchyPageInfos() {
		PageInfo[] pageInfos = new PageInfo[1];
		pageInfos[0] = new PageInfo();
		pageInfos[0].setPageName("Home");

		// data description and id are non-sense for home page, we are just make some
		// initialization of them
		pageInfos[0].setPageDataDesciption("Home");
		pageInfos[0].setPageDataId(0);

		return pageInfos;
	}

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		long projectId = ((Long)parameters[0]).longValue();
		loadProject(projectId);
	}

	/**
	 * Load a project by the project id
	 * @param projectId
	 */
	public void loadProject(long projectId){
		setProject(LuntBuild.getDao().loadProject(projectId));
		ProjectBasicTab basicTab = (ProjectBasicTab)getComponent("basic");
		basicTab.setCurrentAction(null); // set a null action to show basic information about the project
	}

	/**
	 * Create a new project
	 */
	public void createProject(){
		setProject(new Project());
		Map global = (Map)getGlobal();
		VcsAdaptorSelectionModel vcsSelectionModel = (VcsAdaptorSelectionModel) global.get(LuntBuildEngine.VCS_ADAPTORS_KEY);
		Class vcsAdaptor = (Class)vcsSelectionModel.getOption(0);
		try{
			Vcs vcs = (Vcs)vcsAdaptor.newInstance();
			getProject().setVcs(vcs);
		}catch(Exception e){
			throw new ApplicationRuntimeException(e);
		}
		TabControl tabs = (TabControl)getComponent("tabs");
		tabs.setSelectedTabId("basic"); // first switch to basic information tab
		ProjectBasicTab basicTab = (ProjectBasicTab)getComponent("basic");
		basicTab.setCurrentAction("edit");  // activate edit action to edit basic information of current project
	}

	public List getVcsAdaptorProperties(){
		Vcs vcs = getProject().getVcs();
		String[] properties = vcs.getProperties();
		List vcsAdaptorProperties = new ArrayList();
		for (int i = 0; i < properties.length; i++) {
			vcsAdaptorProperties.add(new VcsAdaptorProperty(vcs, properties[i]));
		}
		return vcsAdaptorProperties;
	}

	/**
	 * Saves currently modified project into external storage
	 */
	public void saveData(){
		LuntBuild.getDao().saveProject(getProject());
	}

	/**
	 * Reloads project object from external storage
	 */
	public void loadData(){
		if (getProject().getId() != 0)
			setProject(LuntBuild.getDao().loadProject(getProject().getId()));
	}

	public abstract void setProject(Project project);

	public abstract Project getProject();

	public int getRefreshInterval(){
		return 0;
	}
}
