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

import com.luntsys.luntbuild.db.*;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.web.selectionmodels.BaseUserSelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.LogLevelSelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.NotifierSelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.UserSelectionModel;
import com.luntsys.luntbuild.security.InternalRoles;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

/**
 * This component responsible for editing basic information of the
 * project
 *
 * @author robin shine
 */
public abstract class ProjectBasicEditor extends BaseComponent {
	private static Log logger = LogFactory.getLog(ProjectBasicEditor.class);

	/**
	 * save the edit results
	 *
	 * @param cycle
	 */
	public void save(IRequestCycle cycle) {
		setSaveOrCancelTriggered(true);
		try {
			getProjectPage().getProject().validateBasic();
			getProjectPage().saveProject();
			Luntbuild.getSchedService().rescheduleBuilds();
			getBasicTab().setAction(null); // set the action to be null to return to the basic information showing page
		} catch (ValidationException e) {
			setErrorMsg(e.getMessage());
		} catch (DataIntegrityViolationException e) {
			setErrorMsg("Specified project already exists!");
		}
	}

	/**
	 * Cancels the editing action. If the project is new creating, the top level home page
	 * will be activated, otherwise, basic information about current project is displayed.
	 *
	 * @param cycle
	 */
	public void cancel(IRequestCycle cycle) {
		setSaveOrCancelTriggered(true);

		if (getProjectPage().getProjectId() == 0){ // creating new project
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
			return "propertyEditorName1";
		else
			return "propertyEditorName2";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorValue1";
		else
			return "propertyEditorValue2";
	}

	public String getPropertyEditorTailCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorTail1";
		else
			return "propertyEditorTail2";
	}

	public List getNotificationUsers() {
		if (getProjectPage().getProject().getNotificationConfigs() == null)
			return new ArrayList();
		return new ArrayList(getProjectPage().getProject().getNotificationConfigs().keySet());
	}

	public void setNotificationUsers(List notificationUsers) {
		if (getProjectPage().getProject().getNotificationConfigs() == null)
			getProjectPage().getProject().setNotificationConfigs(new HashMap());
		getProjectPage().getProject().getNotificationConfigs().clear();
		if (notificationUsers != null) {
			Iterator it = notificationUsers.iterator();
			while (it.hasNext()) {
				User user = (User) it.next();
				NotificationConfig notificationConfig = new NotificationConfig();
				notificationConfig.setProject(getProjectPage().getProject());
				notificationConfig.setUser(user);
				getProjectPage().getProject().getNotificationConfigs().put(user, notificationConfig);
			}
		}
	}

	public IPropertySelectionModel getUserSelectionModel() {
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

	public IPropertySelectionModel getAdminUserSelectionModel() {

		return new BaseUserSelectionModel(getAvailableUsers());
	}

	public IPropertySelectionModel getBuildUserSelectionModel() {

		return new BaseUserSelectionModel(getAvailableUsers());
	}

	public IPropertySelectionModel getViewUserSelectionModel() {

		return new BaseUserSelectionModel(getAvailableUsers());
	}

	public List getAdminUsers() {

		return getProjectPage().getProject().getMappedRolesUserList(InternalRoles.ROLE_PRJ_ADMIN);
	}

	public void setAdminUsers(List adminUsers) {
		getProjectPage().getProject().putMappedRolesUserList(adminUsers, InternalRoles.ROLE_PRJ_ADMIN);
	}

	public List getBuildUsers() {
		return getProjectPage().getProject().getMappedRolesUserList(InternalRoles.ROLE_BUILD_ADMIN);
	}

	public void setBuildUsers(List buildUsers) {
		getProjectPage().getProject().putMappedRolesUserList(buildUsers, InternalRoles.ROLE_BUILD_ADMIN);
	}

	public List getViewUsers() {
		return getProjectPage().getProject().getMappedRolesUserList(InternalRoles.ROLE_VIEWER);
	}

	public void setViewUsers(List viewUsers) {
		getProjectPage().getProject().putMappedRolesUserList(viewUsers, InternalRoles.ROLE_VIEWER);
	}

	private List getAvailableUsers() {

		List users = Luntbuild.getDao().loadUsersWithoutVirtual();
		return users;
	}
}