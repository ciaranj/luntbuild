/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-10-7
 * Time: 21:19
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
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;

import java.util.List;

import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.components.SecuritySupportComponent;
import com.luntsys.luntbuild.db.User;

/**
 * This component shows basic information about a user. Basic information includes
 * user name, etc.
 *
 * @author robin shine
 */
public abstract class UserViewer extends SecuritySupportComponent implements PageDetachListener {
	private User user;

	/**
	 * Triggered when user edit the user
	 * @param cycle
	 */
	public void edit(IRequestCycle cycle){
		UsersTab usersTab = (UsersTab) getContainer();
		usersTab.ensureCurrentTab();
		UserEditor userEditor = (UserEditor) getContainer().getComponent("userEditorComponent");
		userEditor.setUserId(getUserId());
		usersTab.setAction("editUser");
	}

	public void pageDetached(PageEvent event) {
		user = null;
	}

	public void finishLoad(IRequestCycle iRequestCycle, IPageLoader iPageLoader, IComponentSpecification iComponentSpecification) {
		super.finishLoad(iRequestCycle, iPageLoader, iComponentSpecification);
		user = null;
	}

	public List getNotifierInstances() {
		return Luntbuild.getNotifierInstances(Luntbuild.notifiers);
	}

	public abstract int getCssIndex();

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyListName2";
		else
			return "propertyListName1";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyListValue2";
		else
			return "propertyListValue1";
	}

	public abstract long getUserId();

	public abstract void setUserId(long userId);

	public User getUser() {
		if (user == null)
			user = Luntbuild.getDao().loadUser(getUserId());
		return user;
	}
}
