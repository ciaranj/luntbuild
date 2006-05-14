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

import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.db.VcsLogin;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.web.selectionmodels.UserSelectionModel;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;

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
		getLoginMappingTab().ensureCurrentTab();
		try {
			getVcsLoginToEdit().validate();
			if (getVcsLoginToEdit().getId() == 0 && Luntbuild.getDao().isVcsLoginNameUsed(getVcsLoginToEdit())) {
				setErrorMsg("Specified login already associated with an user!");
				return;
			}
			Luntbuild.getDao().saveVcsLogin(getVcsLoginToEdit());
			getLoginMappingTab().setAction(null);
		} catch (ValidationException e) {
			setErrorMsg(e.getMessage());
		}
	}

	public void cancel(IRequestCycle cycle) {
		getLoginMappingTab().ensureCurrentTab();
		getLoginMappingTab().setAction(null);
	}

	public IPropertySelectionModel getUserSelectionModel() {
		return new UserSelectionModel(Luntbuild.removeCheckinUser(Luntbuild.getDao().loadUsers()));
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

	public abstract void setVcsLoginToEdit(VcsLogin vcsLoginToEdit);

	public abstract VcsLogin getVcsLoginToEdit();

	public abstract void setVcsLoginIdToEdit(long vcsLoginIdToEdit);

	public abstract long getVcsLoginIdToEdit();

	public void assignVcsLoginIdToEdit(long vcsLoginIdToEdit) {
		if (vcsLoginIdToEdit == 0) {
			setVcsLoginToEdit(new VcsLogin());
			getVcsLoginToEdit().setProject(getProjectPage().getProject());
		} else
			setVcsLoginToEdit(Luntbuild.getDao().loadVcsLogin(vcsLoginIdToEdit));
		setVcsLoginIdToEdit(vcsLoginIdToEdit);
	}
}