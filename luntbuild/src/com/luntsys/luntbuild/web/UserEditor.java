/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-10-7
 * Time: 21:18
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
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.web.components.SecuritySupportComponent;
import com.luntsys.luntbuild.security.SecurityHelper;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.spec.IComponentSpecification;

import java.util.List;

/**
 * This component responsible for editing basic information about a user, such as
 * user name, etc..
 *
 * @author robin shine
 */
public abstract class UserEditor extends SecuritySupportComponent implements PageDetachListener {
	private User user;

	/**
	 * save the edit results
	 * @param cycle
	 */
	public void save(IRequestCycle cycle){
		getUsersTab().ensureCurrentTab();
		try {
			getUser().validate();
			if (getUser().getId() == 0 && Luntbuild.getDao().isUserNameUsed(getUser())) {
				setErrorMsg("User with this name already exists!");
				return;
			}
			if (!isSiteAdmin()) {
				getUser().setCanCreateProject(Luntbuild.getDao().loadUser(getUserId()).isCanCreateProject());
			}
			Luntbuild.getDao().saveUser(getUser());
			setUserId(getUser().getId());
			UsersTab usersTab = (UsersTab) getContainer();
			UserViewer userViewer = (UserViewer) getContainer().getComponent("userViewerComponent");
			userViewer.setUserId(getUser().getId());
			usersTab.setAction("viewUser");
			SecurityHelper.refreshUserCache();
		} catch (ValidationException e) {
			setErrorMsg(e.getMessage());
		}
	}

	public UsersTab getUsersTab() {
		return (UsersTab) getContainer();
	}

	public void cancel(IRequestCycle cycle){
		getUsersTab().ensureCurrentTab();
		UsersTab usersTab = (UsersTab) getContainer();
		if (getUserId() == 0)  // creating new user
			usersTab.setAction(null);
		else
			usersTab.setAction("viewUser");
	}

	public void edit(IRequestCycle cycle) {
		getUsersTab().ensureCurrentTab();
		// does nothing
	}

	public abstract void setErrorMsg(String errorMsg);

	public abstract int getCssIndex();

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorName2";
		else
			return "propertyEditorName1";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorValue2";
		else
			return "propertyEditorValue1";
	}

	public String getTailCssClass() {
		if (getCssIndex() % 2 ==0)
			return "propertyEditorTail2";
		else
			return "propertyEditorTail1";
	}

	public List getNotifierInstances() {
		return Luntbuild.getNotifierInstances(Luntbuild.notifiers);
	}

	public abstract long getUserId();

	public abstract void setUserId(long userId);

	public void pageDetached(PageEvent event) {
		user = null;
	}

	public void finishLoad(IRequestCycle iRequestCycle, IPageLoader iPageLoader, IComponentSpecification iComponentSpecification) {
		super.finishLoad(iRequestCycle, iPageLoader, iComponentSpecification);
		user = null;
	}

	public User getUser() {
		if (user == null) {
			if (getUserId() == 0) // creating new user
				user = new User();
			else
				user = Luntbuild.getDao().loadUser(getUserId());
		}
		return user;
	}

}
