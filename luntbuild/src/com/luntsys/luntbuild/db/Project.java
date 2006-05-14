/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-20
 * Time: 15:18
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

import com.luntsys.luntbuild.remoting.ProjectFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.vcs.Vcs;

import java.util.Iterator;
import java.util.Set;
import java.util.List;

/**
 * As the name indicates, this class represents a project in a source repository. From the project, several views can be configured in order to support
 * build based on different branches. This class is a hibernate mapping class
 *
 * @author robin shine
 * @see View
 */
public class Project {
	private long id;
	private String name;
	private String description;
	private Vcs vcs;
	private Set views;
	private Set vcsLogins;

	/**
	 * set the unique identity of this project, will be called by hibernate
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	/**
	 * set the name of this project
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * set the description of this project
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * set the version control system adaptor for this project. The null value will indicate
	 * current project is a composite project.
	 * @param vcs
	 */
	public void setVcs(Vcs vcs){
		this.vcs = vcs;
	}

	public Vcs getVcs(){
		return vcs;
	}

	/**
	 * configure a collection of views for this project
	 * @param views
	 * @see View
	 */
	public void setViews(Set views) {
		this.views = views;
	}

	public Set getViews() {
		return views;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Project){
			if (getId() == ((Project)obj).getId())
				return true;
		}
		return false;
	}

	public int hashCode() {
		return (int) getId();
	}

	public Set getVcsLogins() {
		return vcsLogins;
	}

	public void setVcsLogins(Set vcsLogins) {
		this.vcsLogins = vcsLogins;
	}

	public void validate() {
		if (Luntbuild.isEmpty(getName())){
			throw new ValidationException("Name should not be empty!");
		}
		setName(getName().trim());
		getVcs().validateProjectLevelProperties();
	}

	public ProjectFacade getFacade() {
		ProjectFacade facade = new ProjectFacade();
		facade.setId(getId());
		facade.setName(getName());
		facade.setDescription(getDescription());
		facade.setVcs(vcs.getFacade());
		return facade;
	}

	public void setFacade(ProjectFacade facade) {
		setName(facade.getName());
		setDescription(facade.getDescription());
		try {
			Vcs newVcs = (Vcs) Class.forName(facade.getVcs().getVcsClassName()).newInstance();
			newVcs.setFacade(facade.getVcs());
			setVcs(newVcs);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		validate();
	}

	public View getView(String viewName) {
		// reload project to make sure views are initialized
		Iterator it = getViews().iterator();
		while (it.hasNext()) {
			View view = (View) it.next();
			if (view.getName().equals(viewName))
				return Luntbuild.getDao().loadView(view.getId());
		}
		return null;
	}

	public void setView(String viewName, View view) {
		// empty method, only want to conform to ognl indexed property
	}

	/**
	 * This function determines the luntbuild user by the vcs login string
	 *
	 * @param login version control system login name
	 * @param users luntbuild users list
	 * @return maybe null if no user found to match this vcs login
	 */
	public User getUserByVcsLogin(String login, List users) {
		VcsLogin vcsLogin = VcsLogin.findVcsLogin(getVcsLogins(), login);
		if (vcsLogin != null)
			return vcsLogin.getUser();

		// continue to find based on global users
		Iterator it = users.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			if (user.getId() == User.USER_CHECKIN_ID)
				continue;
			if (login.equalsIgnoreCase(user.getName()))
				return user;
		}
		return null;
	}
}