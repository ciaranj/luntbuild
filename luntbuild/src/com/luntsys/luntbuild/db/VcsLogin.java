/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-8
 * Time: 18:48
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

package com.luntsys.luntbuild.db;

import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.facades.lb12.VcsLoginFacade;

import java.util.Iterator;
import java.util.Set;

/**
 * Represents a version control system login of a particular <code>User</code> for a particular <code>Project</code>.
 * 
 * <p>This is a hibernate mapping class.</p>
 *
 * @author robin shine
 * @see User
 * @see Project
 */
public class VcsLogin {
	private long id;
	private Project project;
	private User user;
	private String login;

	/**
	 * Creates a blank VCS login.
	 */
	public VcsLogin() {}

	/**
	 * Creates a VCS login.
	 * 
	 * @param project the project
	 * @param user the user
	 * @param login the VCS login name/id
	 */
	public VcsLogin(Project project, User user, String login) {
		this.project = project;
		this.user = user;
		this.login = login;
	}

	/**
	 * Gets the unique identifier of this VCS login.
	 *
	 * @return the identifier of this VCS login
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of this VCS login, will be called by hibernate.
	 *
	 * @param id the identifier of this VCS login
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the project of this VCS login.
	 *
	 * @return the project of this VCS login
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Sets the project of this VCS login.
	 *
	 * @param project the project of this VCS login
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * Gets the user of this VCS login.
	 *
	 * @return the user of this VCS login
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user of this VCS login.
	 *
	 * @param user the user of this VCS login
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Gets the VCS login name/id of this VCS login.
	 *
	 * @return the VCS login name/id of this VCS login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Sets the VCS login name/id of this VCS login.
	 *
	 * @param login the VCS login name/id of this VCS login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Validates properties of this VCS login.
	 * 
	 * @throws ValidationException if a property has an invalid value
	 */
	public void validate() {
		if (Luntbuild.isEmpty(getLogin()))
			throw new ValidationException("Login name should not be empty!");
		setLogin(getLogin().trim());
		if (getProject() == null)
			throw new ValidationException("No project selected!");
		if (getUser() == null)
			throw new ValidationException("No user selected!");
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise
	 */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof VcsLogin){
			if (getId() == ((VcsLogin)obj).getId())
				return true;
		}
		return false;
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return a hash code value for this object
	 * @see #equals(Object)
	 */
	public int hashCode() {
		return (int) getId();
	}

	/**
	 * Finds the VCS login from a list that has the specified VCS login name/id.
	 * 
	 * @param vcsLogins the list of <code>VcsLogin</code>s
	 * @param login the VCS login name/id to check for
	 * @return the VCS login or <code>null</code>
	 */
	public static VcsLogin findVcsLogin(Set vcsLogins, String login) {
		Iterator it = vcsLogins.iterator();
		while (it.hasNext()) {
			VcsLogin vcsLogin = (VcsLogin) it.next();
			if (vcsLogin.getLogin().equalsIgnoreCase(login))
				return vcsLogin;
		}
		return null;
	}

	/**
	 * Gets the facade of this VCS login.
	 * 
	 * @return the facade of this VCS login
	 */
	public VcsLoginFacade getFacade() {
		VcsLoginFacade facade = new VcsLoginFacade();
		facade.setId(getId());
		facade.setLogin(getLogin());
		facade.setProjectId(getProject().getId());
		facade.setUserId(getUser().getId());
		return facade;
	}
}
