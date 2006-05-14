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
 * Facade of a luntbuild user, this is mainly used by web service client to operate
 * user specific properties
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
	 * Get name of this user
	 * @return name of this user
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get id of this user
	 * @return id of this user
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set id of this user
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Set name of this user
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isCanCreateProject() {
		return canCreateProject;
	}

	public void setCanCreateProject(boolean canCreateProject) {
		this.canCreateProject = canCreateProject;
	}

	public Map getContacts() {
		return contacts;
	}

	public void setContacts(Map contacts) {
		this.contacts = contacts;
	}
}
