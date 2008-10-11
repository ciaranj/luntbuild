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

package com.luntsys.luntbuild.facades.lb111;

import java.util.Map;

/**
 * User facade.
 *
 * @author robin shine
 */
public class UserFacade{
	private long id;
	private String name;
	private String password;

	/**
	 * A map including various contact methods for current user. Key of the map
	 * is class name of a notifier class, while value is the contact string which will
	 * get interpreted by the notifier class
	 */
	private Map contacts;

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
	 * Gets the contacts of this user.
	 *
	 * @return the contacts of this user
	 */
	public Map getContacts() {
		return contacts;
	}

	/**
	 * Sets the contacts of this user.
	 *
	 * @param contacts the contacts of this user
	 */
	public void setContacts(Map contacts) {
		this.contacts = contacts;
	}
}
