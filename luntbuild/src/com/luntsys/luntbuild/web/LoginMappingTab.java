/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-10-8
 * Time: 17:39
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

import com.luntsys.luntbuild.db.VcsLogin;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;
import org.apache.tapestry.IRequestCycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This shows logins configured for a particular user
 *
 * @author robin shine
 */
public abstract class LoginMappingTab extends TabPageComponent {
	public String getTabName() {
		return "login mapping";
	}

	public void tabSelected() {
		setAction(null);
	}

	public ProjectPage getProjectPage() {
		return (ProjectPage) getPage();
	}

	/**
	 * create a new login
	 *
	 * @param cycle
	 */
	public void createLogin(IRequestCycle cycle) {
		setAction("editLoginMapping");
		LoginMappingEditor loginMappingEditor = (LoginMappingEditor) getComponent("loginMappingEditorComponent");
		loginMappingEditor.assignVcsLoginIdToEdit(0);
	}

	public void deleteLogin(IRequestCycle cycle) {
		long vcsLoginId = ((Long) cycle.getServiceParameters()[0]).longValue();
		setVcsLoginIdToDelete(vcsLoginId);
		setAction("deleteLoginMapping");
	}

	public void editLogin(IRequestCycle cycle) {
		setAction("editLoginMapping");
		long vcsLoginId = ((Long) cycle.getServiceParameters()[0]).longValue();
		LoginMappingEditor loginMappingEditor = (LoginMappingEditor) getComponent("loginMappingEditorComponent");
		loginMappingEditor.assignVcsLoginIdToEdit(vcsLoginId);
	}

	/**
	 * Actually delete the login
	 *
	 * @param cycle
	 */
	public void confirmDelete(IRequestCycle cycle) {
		if (getVcsLoginIdToDelete() == 0) { // avoid re-submit
			return;
		}
		Luntbuild.getDao().deleteVcsLogin(getVcsLoginIdToDelete());
		setVcsLoginIdToDelete(0);
		setAction(null);
	}

	/**
	 * Cancel the deletion of the login
	 *
	 * @param cycle
	 */
	public void cancelDelete(IRequestCycle cycle) {
		setVcsLoginIdToDelete(0);
		setAction(null);
	}

	/**
	 * Goto the project basic information tab, this action will triggered when
	 * {@link ProjectPage#getProject()#getId()}  returns 0 which means we are creating a
	 * new project
	 *
	 * @param cycle
	 */
	public void gotoBasicTab(IRequestCycle cycle) {
		TabControl tabs = (TabControl) getProjectPage().getComponent("tabs");
		tabs.setSelectedTabId("basic");
	}

	public List getVcsLogins() {
		List vcsLogins = new ArrayList(getProjectPage().getProject().getVcsLogins());
		Collections.sort(vcsLogins, new Comparator() {
			public int compare(Object o1, Object o2) {
				VcsLogin vcsLogin1 = (VcsLogin) o1;
				VcsLogin vcsLogin2 = (VcsLogin) o2;
				return vcsLogin1.getLogin().compareTo(vcsLogin2.getLogin());
			}
		});
		return vcsLogins;
	}

	public abstract void setAction(String action);

	public abstract String getAction();

	public abstract void setVcsLoginIdToDelete(long vcsLoginIdToDelete);

	public abstract long getVcsLoginIdToDelete();

	public abstract int getVcsLoginIndex();

	public String getLoginCssClass() {
		if (getVcsLoginIndex() % 2 == 0)
			return "dataTableRow1";
		else
			return "dataTableRow2";
	}

	public void gotoUser(IRequestCycle cycle) {
		long userId = ((Long) cycle.getServiceParameters()[0]).longValue();
		UserPage userPage = (UserPage) cycle.getPage("UserPage");
		userPage.assignUserId(userId);
		cycle.activate(userPage);
	}
}
