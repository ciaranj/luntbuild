/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-10-7
 * Time: 19:48
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
import com.luntsys.luntbuild.security.SecurityHelper;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.spec.IComponentSpecification;

import java.util.List;

/**
 * This tab shows information about users in the system
 *
 * @author robin shine
 */
public abstract class UsersTab extends TabPageComponent implements PageDetachListener {
	private List users;

	public void finishLoad(IRequestCycle iRequestCycle, IPageLoader iPageLoader, IComponentSpecification iComponentSpecification) {
		super.finishLoad(iRequestCycle, iPageLoader, iComponentSpecification);
		users = null;
	}

	public void pageDetached(PageEvent event) {
		users = null;
	}

	public String getTabName() {
		return "Users";
	}

	public void tabSelected() {
		setAction(null);
	}

	/**
	 * create a new user
	 * @param cycle
	 */
	public void createUser(IRequestCycle cycle){
		ensureCurrentTab();
		UserEditor userEditor = (UserEditor) getComponent("userEditorComponent");
		userEditor.setUserId(0);
		setAction("editUser");
	}

	public void viewUser(IRequestCycle cycle){
		ensureCurrentTab();
		long userId = ((Long)cycle.getServiceParameters()[0]).longValue();
		UserViewer userViewer = (UserViewer) getComponent("userViewerComponent");
		userViewer.setUserId(userId);
		setAction("viewUser");
	}

	public void deleteUser(IRequestCycle cycle){
		ensureCurrentTab();
		long userId = ((Long)cycle.getServiceParameters()[0]).longValue();
		setUserIdToDelete(userId);
		setAction("deleteUser");
	}

	/**
	 * Actually delete the user
	 * @param cycle
	 */
	public void confirmDelete(IRequestCycle cycle){
		ensureCurrentTab();
		if (getUserIdToDelete() == 0) {
			return;
		}
		Luntbuild.getDao().deleteUser(getUserIdToDelete());
		setUserIdToDelete(0);
		setAction(null);
	}

	/**
	 * Cancel the deletion of the user
	 * @param cycle
	 */
	public void cancelDelete(IRequestCycle cycle){
		ensureCurrentTab();
		setUserIdToDelete(0);
		setAction(null);
	}

	public List getUsers() {
		if (users == null) {
			users = Luntbuild.getDao().loadUsers();
			Luntbuild.removeCheckinUser(users);
		}
		return users;
	}

	public abstract void setUser(User user);

	public abstract User getUser();

	public abstract void setAction(String action);

	public abstract String getAction();

	public abstract void setUserIdToDelete(long userIdToDelete);

	public abstract long getUserIdToDelete();

	public abstract int getUserIndex();

	/**
	 * Get css class for current row
	 * @return
	 */
	public String getRowClass() {
		if (getUserIndex() % 2 == 0)
			return "dataTableRow dataTableRow1";
		else
			return "dataTableRow dataTableRow2";
	}

	public List getNotifierInstances() {
		return Luntbuild.getNotifierInstances(Luntbuild.notifiers);
	}

	public String getPrincipal() {
        return SecurityHelper.getPrincipalAsString();		
	}
}
