/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-4
 * Time: 22:26:43
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
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.vcs.VcsAdaptorSelectionModel;
import com.luntsys.luntbuild.vcs.Vcs;
import com.luntsys.luntbuild.vcs.VcsAdaptorProperty;
import com.luntsys.luntbuild.LuntBuildEngine;
import com.luntsys.luntbuild.utility.LuntBuild;
import com.luntsys.luntbuild.dao.Dao;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * This component shows basic information about a project. Basic information includes
 * project name, description, version control system information, etc.
 *
 * @author alvin shen
 */
public abstract class ProjectBasicTab extends TabPageComponent {

	public String getTabName() {
		return "basic";
	}

	/**
	 * Triggered when this tab been selected by user. We will do an reload of data
	 * to make the data fresh
	 */
	public void tabSelected(){
		getProjectPage().loadData(); // refresh project data 
		// if the project contained in project page is a new project, switch to edit action automatically
		if (getProjectPage().getProject().getId() == 0)
			setCurrentAction("edit");
	}

	/**
	 * Triggered when user edit the project
	 * @param cycle
	 */
	public void edit(IRequestCycle cycle){
		setCurrentAction("edit");
	}

	public ProjectPage getProjectPage(){
		return (ProjectPage)getPage();
	}

	/**
	 * @param currentAction action taken for the project basic information in this tab. Possible values are listed below:
	 * <i> null, show the information
	 * <i> "edit", edit the information
	 */
	public abstract void setCurrentAction(String currentAction);

	public abstract String getCurrentAction();
}
