/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-20
 * Time: 19:31:43
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

package com.luntsys.luntbuild.facades.lb12;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * User facade.
 *
 * @author robin shine
 */
public class UserFacade {
	private long id;
	private String name;
	private String fullname;
	private String password;
	private boolean canCreateProject;
	private Map contacts = new LinkedHashMap();

	/**
	 * Gets the unique identifier of this user.
	 *
	 * @return the identifier of this user
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of this user.
	 *
	 * @param id the identifier of this user
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the name of this user.
	 *
	 * @return the name of this user
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this user.
	 *
	 * @param name the name of this user
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the full name of the user.
	 * 
	 * @return the full name of the user
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * Sets the full name of the user.
	 * 
	 * @param fullname the full name of the user
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	/**
	 * Gets the password of this user.
	 *
	 * @return the password of this user
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password of this user.
	 *
	 * @param password the password of this user
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Checks if this user is allowed to create a new project.
	 * 
	 * @return <code>true</code> if this user can create a new project
	 */
	public boolean isCanCreateProject() {
		return canCreateProject;
	}

	/**
	 * Sets the permission for this user to create a new project.
	 * 
	 * @param canCreateProject if <code>true</code>, this user can create a new project
	 */
	public void setCanCreateProject(boolean canCreateProject) {
		this.canCreateProject = canCreateProject;
	}

	/**
	 * Gets the contacts of this user.
	 *
	 * @return the contacts of this user
	 * @see com.luntsys.luntbuild.db.NotifyMapping
	 */
	public Map getContacts() {
		return contacts;
	}

	/**
	 * Sets the contacts of this user.
	 *
	 * @param contacts the contacts of this user
	 * @see com.luntsys.luntbuild.db.NotifyMapping
	 */
	public void setContacts(Map contacts) {
		this.contacts = contacts;
	}
}
