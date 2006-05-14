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

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IRequestCycle;

import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;

/**
 * This tab shows information about users in the system
 *
 * @author robin shine
 */
public abstract class UsersTab extends TabPageComponent {

	public String getTabName() {
		return "users";
	}

	public void tabSelected() {
		// refresh data on select
		loadData();
	}

	/**
	 * Loads users information from persistence layer
	 */
	public void loadData(){
		User userCheckin = new User();
		userCheckin.setId(User.USER_CHECKIN_ID);
		userCheckin.setName(User.USER_CHECKIN_NAME);
		List users = Luntbuild.getDao().loadUsers();
		users.remove(userCheckin);
		setUsers(users);
	}

	public List getActualUsers() {
		User userCheckin = new User();
		userCheckin.setId(User.USER_CHECKIN_ID);
		userCheckin.setName(User.USER_CHECKIN_NAME);
		List actualUsers = new ArrayList(getUsers());
		actualUsers.remove(userCheckin);
		return actualUsers;
	}

	/**
	 * create a new user
	 * @param cycle
	 */
	public void createUser(IRequestCycle cycle){
		UserPage page = (UserPage)cycle.getPage("UserPage");
		page.createUser();
		cycle.activate(page);
	}

	/**
	 * Load the user page to show details of a particular user
 	 * @param cycle
	 */
	public void loadUser(IRequestCycle cycle){
		long userId = ((Long)cycle.getServiceParameters()[0]).longValue();
		UserPage page = (UserPage)cycle.getPage("UserPage");
		page.loadUser(userId);
		TabControl tabs = (TabControl) page.getComponent("tabs");
		tabs.setSelectedTabId("basic");		
		cycle.activate(page);
	}

	public void deleteUser(IRequestCycle cycle){
		long userId = ((Long)cycle.getServiceParameters()[0]).longValue();
		setUserToBeDelete(Luntbuild.getDao().loadUser(userId));
		setCurrentAction("delete");
	}

	/**
	 * Actually delete the user
	 * @param cycle
	 */
	public void confirmDelete(IRequestCycle cycle){
		Luntbuild.getDao().deleteUser(getUserToBeDelete().getId());
		setUserToBeDelete(null);
		loadData();
		setCurrentAction(null);
	}

	/**
	 * Cancel the deletion of the user
	 * @param cycle
	 */
	public void cancelDelete(IRequestCycle cycle){
		setUserToBeDelete(null);
		loadData();
		setCurrentAction(null);
	}

	public abstract void setUsers(List users);

	public abstract List getUsers();

	public abstract void setCurrentUser(User user);

	public abstract User getCurrentUser();

	public abstract void setCurrentAction(String currentAction);

	public abstract String getCurrentAction();

	public abstract void setUserToBeDelete(User user);

	public abstract User getUserToBeDelete();

	public abstract int getCurrentUserIndex();

	/**
	 * Get css class for current row
	 * @return
	 */
	public String getRowClass() {
		if (getCurrentUserIndex() % 2 == 0)
			return "dataTableRow1";
		else
			return "dataTableRow2";
	}

	public List getNotifierInstances() {
		return Luntbuild.getNotifierInstances(Luntbuild.notifiers);
	}
}
