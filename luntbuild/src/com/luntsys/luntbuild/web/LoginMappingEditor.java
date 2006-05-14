/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-8
 * Time: 20:32
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

import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.db.VcsLogin;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.UserSelectionModel;

/**
 * This component edits vcs login information for a particular user
 */
public abstract class LoginMappingEditor extends BaseComponent {
	public LoginMappingTab getLoginMappingTab() {
		return (LoginMappingTab) getContainer();
	}

	public abstract String getLogin();

	public abstract void setLogin(String login);

	public abstract User getUser();

	public ProjectPage getProjectPage() {
		return (ProjectPage) getPage();
	}

	public void save(IRequestCycle cycle) {
		if (getLoginMappingTab().getCurrentAction() == null) // avoid re-submit
			return;

		if (getLoginMappingTab().getLoginToBeEdit() == null) { // creating new login
			if (Luntbuild.isEmpty(getLogin())) {
				setErrorMsg("Vcs login should not be empty!");
				return;
			}
			setLogin(getLogin().trim());
			if (getUser() == null) {
				setErrorMsg("No user selected!");
				return;
			}
			if (VcsLogin.findVcsLogin(getProjectPage().getProject().getVcsLogins(), getLogin()) != null) {
				setErrorMsg("Vcs login \"" + getLogin() + "\"already be configured!");
				return;
			}
			VcsLogin vcsLogin = new VcsLogin();
			vcsLogin.setLogin(getLogin());
			vcsLogin.setUser(getUser());
			vcsLogin.setProject(getProjectPage().getProject());
			Luntbuild.getDao().saveVcsLogin(vcsLogin);
			getProjectPage().getProject().getVcsLogins().add(vcsLogin);
		} else {
			if (getUser() == null) {
				setErrorMsg("No user selected!");
				return;
			}
			VcsLogin vcsLogin = VcsLogin.findVcsLogin(getProjectPage().getProject().getVcsLogins(),
					getLoginMappingTab().getLoginToBeEdit());
			vcsLogin.setUser(getUser());
			Luntbuild.getDao().saveVcsLogin(vcsLogin);
		}
		getLoginMappingTab().setCurrentAction(null);
	}

	public void cancel(IRequestCycle cycle) {
		getLoginMappingTab().setLoginToBeEdit(null);
		getLoginMappingTab().setCurrentAction(null);
	}

	public IPropertySelectionModel getUserSelectionModel() {
		List users = Luntbuild.getDao().loadUsers();
		User userCheckin = new User();
		userCheckin.setId(User.USER_CHECKIN_ID);
		users.remove(userCheckin);
		return new UserSelectionModel(users);
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

	public String getPropertyTailCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorTail1";
		else
			return "propertyEditorTail2";
	}
}