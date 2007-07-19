/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-4-3
 * Time: 22:37:44
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

/**
 * Roles mapping facade.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.db.RolesMapping
 */
public class RolesMappingFacade {
	private long id;
	private long projectId;
	private long userId;
	private long roleId;

	/**
	 * Gets the identifer of this roles mapping.
	 * 
	 * @return the identifer of this roles mapping
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the identifier of this roles mapping.
	 *
	 * @param id the identifier of this roles mapping
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the identifier of the project of this roles mapping.
	 * 
	 * @return the identifier of the project
	 */
	public long getProjectId() {
		return projectId;
	}

	/**
	 * Sets the identifier of the project of this roles mapping.
	 * 
	 * @param projectId the identifier of the project
	 */
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	/**
	 * Gets the identifier of the user of this roles mapping.
	 * 
	 * @return the identifier of the user
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * Sets the identifier of the user of this roles mapping.
	 * 
	 * @param userId the identifier of the user
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * Gets the identifier of the role of this roles mapping.
	 * 
	 * @return the identifier of the role
	 */
	public long getRoleId() {
		return roleId;
	}

	/**
	 * Sets the identifier of the role of this roles mapping.
	 * 
	 * @param roleId the identifier of the role
	 */
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
}
