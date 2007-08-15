/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-11-15
 * Time: 14:53:22
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

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import com.luntsys.luntbuild.facades.lb12.NotifyMappingFacade;

/**
 * Represents the <code>Project</code> to <code>User</code> notification mapping.
 * 
 * <p>This is a hibernate mapping class.</p>
 * 
 * @author robin shine
 * @see Project
 * @see User
 */
public class NotifyMapping {
	private long id;
	private Project project;
	private User user;

	/**
	 * Creates a blank notify mapping.
	 */
	public NotifyMapping(){};

	/**
	 * Creates a notify mapping.
	 * 
	 * @param project the project
	 * @param user the user
	 */
	public NotifyMapping(Project project, User user) {
		this.project = project;
		this.user = user;
	}

	/**
	 * Gets the identifer of this notify mapping.
	 * 
	 * @return the identifer of this notify mapping
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the identifier of this notify mapping, will be called by hibernate.
	 *
	 * @param id the identifier of this notify mapping
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the project of this notify mapping.
	 * 
	 * @return the project of this notify mapping
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Sets the project of this notify mapping.
	 * 
	 * @param project the project of this notify mapping
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * Gets the user of this notify mapping.
	 * 
	 * @return the user of this notify mapping
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user of this notify mapping.
	 * 
	 * @param user the user of this notify mapping
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return a hash code value for this object
	 * @see #equals(Object)
	 */
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getProject().getId())
			.append(getUser().getId())
			.hashCode();
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise
	 */
	public boolean equals(Object obj) {
		if ( !(obj instanceof NotifyMapping) ) return false;
		NotifyMapping castOther = (NotifyMapping) obj;
		return new EqualsBuilder()
			.append(getProject().getId(), castOther.getProject().getId())
			.append(getUser().getId(), castOther.getUser().getId())
			.isEquals();
	}

	/**
	 * Gets the facade of this notify mapping.
	 * 
	 * @return the facade of this notify mapping
	 */
	public NotifyMappingFacade getFacade() {
		NotifyMappingFacade facade = new NotifyMappingFacade();
		facade.setId(getId());
		facade.setProjectId(getProject().getId());
		facade.setUserId(getUser().getId());
		return facade;
	}
}
