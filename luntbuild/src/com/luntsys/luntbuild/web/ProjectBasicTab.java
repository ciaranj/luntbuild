/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-11
 * Time: 7:00:25
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
import com.luntsys.luntbuild.db.Role;
import org.apache.tapestry.IRequestCycle;

import java.util.ArrayList;
import java.util.List;

/**
 * displays basic information of a project, such as name and description
 *
 * @author robin shine
 */
public abstract class ProjectBasicTab extends TabPageComponent {

	public String getTabName() {
		return "Basic";
	}

	public void tabSelected() {
		if (getProjectPage().getProject().getId() != 0)
			setAction(null);
		else
			setAction("editProject");
	}

	/**
	 * Triggered when user edit the project
	 *
	 * @param cycle
	 */
	public void edit(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		setAction("editProject");
	}

	public void gotoUser(IRequestCycle cycle) {
		PageUtils.gotoUser(cycle);
	}

	/**
	 * @param action action taken for the project basic information in this tab. Possible values are listed below:
	 *                      <i> null, show the information
	 *                      <i> "edit", edit the information
	 */
	public abstract void setAction(String action);

	public abstract String getAction();

	public ProjectPage getProjectPage() {
		return (ProjectPage) getPage();
	}

	public abstract int getCssIndex();

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyListName propertyListName2";
		else
			return "propertyListName propertyListName1";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyListValue propertyListValue2";
		else
			return "propertyListValue propertyListValue1";
	}

	public List getUsersToNotify() {
		return getProjectPage().getProject().getUsersToNotify();
	}

	public List getNotifierInstances() {
		if (getProjectPage().getProject().getNotifiers() == null)
			return new ArrayList();
		else
			return Luntbuild.getNotifierInstances(Luntbuild.getNotifierClasses(getProjectPage().getProject().getNotifiers()));
	}

	public List getAdminUsers() {

		return getProjectPage().getProject().getMappedRolesUserList(Role.LUNTBUILD_PRJ_ADMIN);
	}

	public List getBuildUsers() {
		return getProjectPage().getProject().getMappedRolesUserList(Role.LUNTBUILD_PRJ_BUILDER);
	}

	public List getViewUsers() {
		return getProjectPage().getProject().getMappedRolesUserList(Role.LUNTBUILD_PRJ_VIEWER);
	}
}