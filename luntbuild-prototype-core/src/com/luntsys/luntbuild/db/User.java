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

import com.luntsys.luntbuild.facades.lb20.UserFacade;
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
	public static final String CHECKIN_USER_NAME = "<users who checked in code recently>";

	private long id;
	private String name;
	private String fullname;
	private String password;
	private Set vcsLogins;
	private Set notifyMappings;
	private boolean canCreateProject;
    private transient String validatedPassword;

	/**
	 * mapped project specific security roles
	 */
	private Set rolesMappings;

	/**
	 * The mapping various contact methods for current user.
	 */
	private Map contacts;

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
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public String getValidatedPassword() {
        return this.validatedPassword;
    }

    public void setValidatedPassword(String password) {
        this.validatedPassword = password;
    }

	/**
	 * Returns decrypted password.
	 *
	 * @return decrypted password
	 */
	public String getDecryptedPassword() {
		return Luntbuild.decryptPassword(this.password);
	}

	/**
	 * Set password in the decryped form, the password will be first be encryped and then saved.
	 *
	 * @param decrypedPassword
	 */
	public void setDecryptedPassword(String decrypedPassword) {
		this.password = Luntbuild.encryptPassword(decrypedPassword);
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
		if (vcsLogins == null)
			vcsLogins = new HashSet();
		return vcsLogins;
	}

	public void setVcsLogins(Set vcsLogins) {
		this.vcsLogins = vcsLogins;
	}

	public Set getNotifyMappings() {
		if (notifyMappings == null)
			notifyMappings = new HashSet();
		return notifyMappings;
	}

	public void setNotifyMappings(Set notifyMappings) {
		this.notifyMappings = notifyMappings;
	}

	public Map getContacts() {
		if (contacts == null) {
			/*Do not use HashMap here, cause entry order of HashMap is not determined. This
			 may cause serialized form of HashMap different from times to times even for the same
			 set of data. And this will cause hibernate thinks data has been changed, and re-saves
			 data automatically. This may also true for HashSet type...
			 */
			contacts = new LinkedHashMap();
		}
		return contacts;
	}

	public void setContacts(Map contacts) {
		this.contacts = contacts;
	}

	public String getFullname() {
		return fullname;
	}

	/**
	 * @param loginname The loginname to set.
	 */
	public void setFullname(String loginname) {
		this.fullname = loginname;
	}

	public Set getRolesMappings() {
		if (rolesMappings == null)
			rolesMappings = new HashSet();
		return rolesMappings;
	}

	public void setRolesMappings(Set rolesMappings) {
		this.rolesMappings = rolesMappings;
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
		String decryptedPassword = getDecryptedPassword();
		if (Luntbuild.isEmpty(decryptedPassword))
			throw new ValidationException("Password should not be empty!");
        String validPassword = getValidatedPassword();
        if (Luntbuild.isEmpty(validPassword) ||
                !validPassword.trim().equals(decryptedPassword.trim()))
            throw new ValidationException("Verified Password does not match!");
        setDecryptedPassword(decryptedPassword.trim());

		Iterator it = getContacts().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String value = (String) entry.getValue();
			if (value != null)
				entry.setValue(value.trim());
		}
	}

	public String toString() {
		return getName();
	}

	public boolean isCanCreateProject() {
		return canCreateProject;
	}

	public void setCanCreateProject(boolean canCreateProject) {
		this.canCreateProject = canCreateProject;
	}

	public UserFacade getFacade() {
		com.luntsys.luntbuild.facades.lb20.UserFacade facade = new com.luntsys.luntbuild.facades.lb20.UserFacade();
		facade.setCanCreateProject(isCanCreateProject());
		facade.setFullname(getFullname());
		facade.setId(getId());
		facade.setName(getName());
		facade.setPassword(getDecryptedPassword());
		facade.setContacts(getContacts());
		return facade;
	}

	public void setFacade(UserFacade facade) {
		setCanCreateProject(facade.isCanCreateProject());
		setFullname(facade.getFullname());
		setDecryptedPassword(facade.getPassword());
		setContacts(facade.getContacts());
	}
}
