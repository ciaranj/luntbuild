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

import com.luntsys.luntbuild.facades.lb12.UserFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

import java.util.*;

/**
 * A Luntbuild user.
 * 
 * <p>This is a hibernate mapping class.</p>
 *
 * @author robin shine
 */
public class User {
    /** Name of the user object representing users who recently check in code */
    public static final String CHECKIN_USER_NAME_RECENT = "<users who checked in code recently>";
    /** Name of the user object representing users who checked in code since the last successful build */
    public static final String CHECKIN_USER_NAME_ALL = "<users who checked in since last successful build>";

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
	 * Sets the unique identifier of this user, will be called by hibernate.
	 *
	 * @param id the identifier of this user
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the unique identifier of this user.
	 *
	 * @return the identifier of this user
	 */
	public long getId() {
		return id;
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
	 * Gets the name of this user.
	 *
	 * @return the name of this user
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the password of this user.
	 *
	 * @return the password of this user
	 */
	public String getPassword() {
		return this.password;
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
	 * Gets the validation copy of the password of this user.
	 *
	 * @return the validation password of this user
	 */
    public String getValidatedPassword() {
        return this.validatedPassword;
    }

	/**
	 * Sets the validation copy of the password of this user.
	 *
	 * @param password the validation password of this user
	 */
    public void setValidatedPassword(String password) {
        this.validatedPassword = password;
    }

	/**
	 * Returns the decrypted password.
	 *
	 * @return the decrypted password
	 */
	public String getDecryptedPassword() {
		return Luntbuild.decryptPassword(this.password);
	}

	/**
	 * Sets the password in the decryped form, the password will first be encryped and then saved.
	 *
	 * @param decrypedPassword the decrypted password
	 */
	public void setDecryptedPassword(String decrypedPassword) {
		this.password = Luntbuild.encryptPassword(decrypedPassword);
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise
	 */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof User) {
			if (getId() == ((User) obj).getId())
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
	 * Gets the VCS logins of this user.
	 *
	 * @return the VCS logins of this user
	 * @see VcsLogin
	 */
	public Set getVcsLogins() {
		if (vcsLogins == null)
			vcsLogins = new HashSet();
		return vcsLogins;
	}

	/**
	 * Sets the VCS logins of this user.
	 *
	 * @param vcsLogins the VCS logins of this user
	 * @see VcsLogin
	 */
	public void setVcsLogins(Set vcsLogins) {
		this.vcsLogins = vcsLogins;
	}

	/**
	 * Gets the notify mappings of this user.
	 *
	 * @return the notify mappings of this user
	 * @see NotifyMapping
	 */
	public Set getNotifyMappings() {
		if (notifyMappings == null)
			notifyMappings = new HashSet();
		return notifyMappings;
	}

	/**
	 * Sets the notify mappings of this user.
	 *
	 * @param notifyMappings the notify mappings of this user
	 * @see NotifyMapping
	 */
	public void setNotifyMappings(Set notifyMappings) {
		this.notifyMappings = notifyMappings;
	}

	/**
	 * Gets the contacts of this user.
	 *
	 * @return the contacts of this user
	 * @see NotifyMapping
	 */
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

	/**
	 * Sets the contacts of this user.
	 *
	 * @param contacts the contacts of this user
	 * @see NotifyMapping
	 */
	public void setContacts(Map contacts) {
		this.contacts = contacts;
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
	 * @param loginname the full name of the user
	 */
	public void setFullname(String loginname) {
		this.fullname = loginname;
	}

	/**
	 * Gets the roles mappings of this user.
	 *
	 * @return the roles mappings of this user
	 * @see RolesMapping
	 */
	public Set getRolesMappings() {
		if (rolesMappings == null)
			rolesMappings = new HashSet();
		return rolesMappings;
	}

	/**
	 * Sets the roles mappings of this user.
	 *
	 * @param rolesMappings the roles mappings of this user
	 * @see RolesMapping
	 */
	public void setRolesMappings(Set rolesMappings) {
		this.rolesMappings = rolesMappings;
	}

	/**
	 * Validates properties of this user.
	 * 
	 * @throws ValidationException if a property has an invalid value
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

	/**
	 * Returns a string representation of this object.
	 * 
	 * @return a string representation of this object
	 */
	public String toString() {
		return getName();
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
	 * Gets the facade of this user.
	 * 
	 * @return the facade of this user
	 */
	public UserFacade getFacade() {
		UserFacade facade = new UserFacade();
		facade.setCanCreateProject(isCanCreateProject());
		facade.setFullname(getFullname());
		facade.setId(getId());
		facade.setName(getName());
		facade.setPassword(getDecryptedPassword());
		facade.setContacts(getContacts());
		return facade;
	}

	/**
	 * Sets the facade of this user.
	 * 
	 * @param facade the user facade
	 */
	public void setFacade(UserFacade facade) {
		setCanCreateProject(facade.isCanCreateProject());
		setFullname(facade.getFullname());
		setDecryptedPassword(facade.getPassword());
		setContacts(facade.getContacts());
	}
}
