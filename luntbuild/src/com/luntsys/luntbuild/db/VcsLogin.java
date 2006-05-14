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

import java.util.Iterator;
import java.util.Set;

/**
 * This class represents a version control system login of a particular user for
 * a particular project. This is a hibernate mapping class.
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof VcsLogin){
			if (getId() == ((VcsLogin)obj).getId())
				return true;
		}
		return false;
	}

	public int hashCode() {
		return (int) getId();
	}

	public static VcsLogin findVcsLogin(Set vcsLogins, String login) {
		Iterator it = vcsLogins.iterator();
		while (it.hasNext()) {
			VcsLogin vcsLogin = (VcsLogin) it.next();
			if (vcsLogin.getLogin().equalsIgnoreCase(login))
				return vcsLogin;
		}
		return null;
	}
}
