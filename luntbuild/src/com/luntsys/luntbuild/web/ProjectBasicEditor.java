/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-6
 * Time: 15:58:02
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
import com.luntsys.luntbuild.utility.LuntBuild;
import com.luntsys.luntbuild.vcs.Vcs;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;

/**
 * This component responsible for editing basic information about a project, such as
 * project name, description, version control system informaiton, etc.
 *
 * @author alvin shen
 */
public abstract class ProjectBasicEditor extends BaseComponent {
	private Class vcsAdaptor;

	/**
	 * save the edit results
	 * @param cycle
	 */
	public void save(IRequestCycle cycle){
		// verifies user input
		Project project = getProjectPage().getProject();
		if (project.getName() == null || project.getName().trim().equals("")){
			setErrorMsg("Field \"Name\" should not be empty!");
			return;
		}
		Vcs vcs = project.getVcs();
		String[] properties = vcs.getProperties();
		for (int i = 0; i < properties.length; i++) {
			String property = properties[i];
			try {
				String value = BeanUtils.getProperty(vcs, property);
				if (!vcs.isPropertyOptional(property) && (value == null || value.trim().equals(""))){
					setErrorMsg("Field \"" + vcs.getPropertyDisplayName(property)+ "\" should not be empty!");
					return;
				}
			} catch (Exception e) {
				throw new ApplicationRuntimeException(e);
			}
		}
		getProjectPage().saveData();
		LuntBuild.getService().rescheduleBuilds();
		getBasicTab().setCurrentAction(null); // set the null action to display the project information
	}

	/**
	 * Cancels the editing action. If the project is new creating, the top level home page
	 * will be activated, otherwise, basic information about current project is displayed.
	 * @param cycle
	 */
	public void cancel(IRequestCycle cycle){
		if (getProjectPage().getProject().getId() == 0){ // creating new project
			Home home = (Home)cycle.getPage("Home");
			home.activateExternalPage(cycle.getServiceParameters(), cycle);
			cycle.activate(home);
		} else{
			getProjectPage().loadData(); // reload data to discard changes
			getBasicTab().setCurrentAction(null);
		}
	}

	public ProjectPage getProjectPage(){
		return (ProjectPage)getPage();
	}

	public ProjectBasicTab getBasicTab(){
		return (ProjectBasicTab)getContainer();
	}

	public void setVcsAdaptor(Class vcsAdaptor){
		this.vcsAdaptor  = vcsAdaptor; // temporarily save the class for later use
	}

	public Class getVcsAdaptor(){
		return getProjectPage().getProject().getVcs().getClass();
	}

	/**
	 * Called when user select a different vcs adaptor type. It will changes vcs class of the
	 * underlying project object
	 * @param cycle
	 */
	public void changeVcsAdaptor(IRequestCycle cycle){
		Project project = getProjectPage().getProject();
		Class oldVcsAdaptor = project.getVcs().getClass();
		if (oldVcsAdaptor == vcsAdaptor)
			return;
		try{
			Vcs vcs = (Vcs)vcsAdaptor.newInstance();
			project.setVcs(vcs);
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public abstract void setErrorMsg(String errorMsg);
}
