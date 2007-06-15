/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-11
 * Time: 7:08:09
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

import com.luntsys.luntbuild.db.Role;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.web.components.SecuritySupportComponent;
import com.luntsys.luntbuild.web.selectionmodels.LogLevelSelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.NotifierSelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.UserSelectionModel;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This component responsible for editing basic information of the
 * project
 *
 * @author robin shine
 */
public abstract class ProjectBasicEditor extends SecuritySupportComponent {
	/**
	 * save the edit results
	 *
	 * @param cycle
	 */
	public void save(IRequestCycle cycle) {
		getBasicTab().ensureCurrentTab();
		setSaveOrCancelTriggered(true);
		try {
			getProjectPage().getProject().validateBasic();
			if (Luntbuild.getDao().isProjectNameUsed(getProjectPage().getProject())) {
				setErrorMsg("Project with this name already exists!");
				return;
			}
			getProjectPage().saveProject();
			Luntbuild.getSchedService().rescheduleBuilds();
			getBasicTab().setAction(null); // set the action to be null to return to the basic information showing page
		} catch (ValidationException e) {
			setErrorMsg(e.getMessage());
		}
	}

	/**
	 * Cancels the editing action. If the project is new creating, the top level home page
	 * will be activated, otherwise, basic information about current project is displayed.
	 *
	 * @param cycle
	 */
	public void cancel(IRequestCycle cycle) {
		getBasicTab().ensureCurrentTab();
		setSaveOrCancelTriggered(true);

		if (getProjectPage().getProject().getId() == 0){ // creating new project
			Home home = (Home)cycle.getPage("Home");
			home.activateExternalPage(null, cycle);
			cycle.activate(home);
		} else{
			getBasicTab().setAction(null);
		}
	}

	public ProjectBasicTab getBasicTab() {
		return (ProjectBasicTab) getContainer();
	}

	public ProjectPage getProjectPage() {
		return (ProjectPage) getPage();
	}

	public abstract void setErrorMsg(String errorMsg);

	public abstract int getCssIndex();

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorName propertyEditorName1";
		else
			return "propertyEditorName propertyEditorName2";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorValue propertyEditorValue1";
		else
			return "propertyEditorValue propertyEditorValue2";
	}

	public String getPropertyEditorTailCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorTail propertyEditorTail1";
		else
			return "propertyEditorTail propertyEditorTail2";
	}

	public List getUsersToNotify() {
		return getProjectPage().getProject().getUsersToNotify();
	}

	public void setUsersToNotify(List usersToNotify) {
		getProjectPage().getProject().putUsersToNotify(usersToNotify);
	}

	public IPropertySelectionModel getNotificationUserSelectionModel() {
		return new UserSelectionModel(Luntbuild.getDao().loadUsers());
	}

	public IPropertySelectionModel getNotifierSelectionModel() {
		return new NotifierSelectionModel();
	}

	public List getNotifierClasses() {
		if (getProjectPage().getProject().getNotifiers() == null)
			return new ArrayList();
		else
			return Luntbuild.getNotifierClasses(getProjectPage().getProject().getNotifiers());
	}

	public void setNotifierClasses(List notifierClasses) {
		List notifierClassNames = new ArrayList();
		if (notifierClasses != null) {
			Iterator it = notifierClasses.iterator();
			while (it.hasNext()) {
				Class notifierClass = (Class) it.next();
				notifierClassNames.add(notifierClass.getName());
			}
		}
		getProjectPage().getProject().setNotifiers(notifierClassNames);
	}

	public String getPaletteCssClass() {
		if (getCssIndex() % 2 == 0)
			return "palette1";
		else
			return "palette2";
	}

	public abstract boolean isSaveOrCancelTriggered();

	public abstract void setSaveOrCancelTriggered(boolean saveOrCancelTriggered);

	public IPropertySelectionModel getLogLevelSelectionModel() {
		return new LogLevelSelectionModel();
	}

	// prj specific user role mapping support

	public IPropertySelectionModel getAuthorizationUserSelectionModel() {
		return new UserSelectionModel(Luntbuild.removeCheckinUser(Luntbuild.getDao().loadUsers()));
	}

	public List getAdminUsers() {
		return getProjectPage().getProject().getMappedRolesUserList(Role.LUNTBUILD_PRJ_ADMIN);
	}

	public void setAdminUsers(List adminUsers) {
		getProjectPage().getProject().putMappedRolesUserList(adminUsers, Role.LUNTBUILD_PRJ_ADMIN);
	}

	public List getBuildUsers() {
		return getProjectPage().getProject().getMappedRolesUserList(Role.LUNTBUILD_PRJ_BUILDER);
	}

	public void setBuildUsers(List buildUsers) {
		getProjectPage().getProject().putMappedRolesUserList(buildUsers, Role.LUNTBUILD_PRJ_BUILDER);
	}

	public List getViewUsers() {
		return getProjectPage().getProject().getMappedRolesUserList(Role.LUNTBUILD_PRJ_VIEWER);
	}

	public void setViewUsers(List viewUsers) {
		getProjectPage().getProject().putMappedRolesUserList(viewUsers, Role.LUNTBUILD_PRJ_VIEWER);
	}
}