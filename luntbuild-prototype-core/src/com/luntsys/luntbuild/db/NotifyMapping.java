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
import com.luntsys.luntbuild.facades.lb20.NotifyMappingFacade;

/**
 * This class is a hibernate entity represents the project-user notification mapping
 * @author robin shine
 */
public class NotifyMapping {
	private long id;
	private Project project;
	private User user;

	public NotifyMapping(){};

	public NotifyMapping(Project project, User user) {
		this.project = project;
		this.user = user;
	}

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

	public int hashCode() {
		return new HashCodeBuilder()
			.append(getProject().getId())
			.append(getUser().getId())
			.hashCode();
	}

	public boolean equals(Object other) {
		if ( !(other instanceof NotifyMapping) ) return false;
		NotifyMapping castOther = (NotifyMapping) other;
		return new EqualsBuilder()
			.append(getProject().getId(), castOther.getProject().getId())
			.append(getUser().getId(), castOther.getUser().getId())
			.isEquals();
	}

	public com.luntsys.luntbuild.facades.lb20.NotifyMappingFacade getFacade() {
		com.luntsys.luntbuild.facades.lb20.NotifyMappingFacade facade = new com.luntsys.luntbuild.facades.lb20.NotifyMappingFacade();
		facade.setId(getId());
		facade.setProjectId(getProject().getId());
		facade.setUserId(getUser().getId());
		return facade;
	}
}
