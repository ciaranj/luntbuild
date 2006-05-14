/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-7
 * Time: 20:34
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

import java.util.*;

/**
 * As the name indicates, this class represents an user in the system.
 * This class is a hibernate mapping class
 *
 * @author robin shine
 */
public class User {
	/**
	 * Defines the checkin virtual user, it will be resolved to actual users who checkin codes in the
	 * period of last build and current build at runtime
	 */
	public static final long USER_CHECKIN_ID = 1;
	public static final String USER_CHECKIN_NAME = "<users who check in codes recently>";

	private long id;
	private String name;
	private String fullname;
	private String password;
	private Set vcsLogins = new HashSet();
	private Map notificationConfigs = new HashMap();

	/**
	 * mapped project specific security roles
	 */
	private Set rolesMappings;

	/**
	 * global role site-admin
	 */
	private boolean siteadmin = false;


	/**
	 * A map including various contact methods for current user. Key of the map
	 * is class name of a notifier class, while value is the contact string which will
	 * get interpreted by the notifier class
	 */
	private Map contacts = new HashMap();

	/**
	 * set the unique identity of this user, will be called by hibernate
	 *
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	/**
	 * set the name of this user
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof User) {
			if (getId() == ((User) obj).getId())
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

	public Map getNotificationConfigs() {
		return notificationConfigs;
	}

	public void setNotificationConfigs(Map notificationConfigs) {
		this.notificationConfigs = notificationConfigs;
	}

	public Map getContacts() {
		return contacts;
	}

	public void setContacts(Map contacts) {
		this.contacts = contacts;
	}

	/**
	 * @return Returns the loginname.
	 * @hibernate.property column="LOGINNAME"
	 * length="45"
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * @param loginname The loginname to set.
	 */
	public void setFullname(String loginname) {
		this.fullname = loginname;
	}

	/**
	 * @hibernate.set lazy="true"
	 * inverse="true"
	 * cascade="delete"
	 * @hibernate.collection-key column="FK_USER_ID"
	 * @hibernate.collection-one-to-many class="com.luntsys.luntbuild.db.RolesMapping"
	 */
	public Set getRolesMappings() {
		return this.rolesMappings;
	}

	public void setRolesMappings(Set rolesMappings) {
		this.rolesMappings = rolesMappings;
	}

	/**
	 * @return Returns the siteadmin.
	 */
	public boolean isSiteadmin() {
		return siteadmin;
	}

	/**
	 * @param siteadmin The siteadmin to set.
	 */
	public void setSiteadmin(boolean siteadmin) {
		this.siteadmin = siteadmin;
	}

	/**
	 * Validates properties of this user
	 *
	 * @throws ValidationException
	 */
	public void validate() {
		if (Luntbuild.isEmpty(getName()))
			throw new ValidationException("Name should not be empty!");
		setName(getName().trim());
		Iterator it = getContacts().keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) getContacts().get(key);
			if (value != null)
				getContacts().put(key, value.trim());
		}
	}
}
