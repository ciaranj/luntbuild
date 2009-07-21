/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-20
 * Time: 19:31:43
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

/**
 * Project facade.
 *
 * @author robin shine
 * @see com.luntsys.luntbuild.db.Project
 */
public class ProjectFacade {
	private long id;
	private String name;
	private String description;
	private VcsFacade vcs;

	/**
	 * Gets the identifer of this project.
	 * 
	 * @return the identifer of this project
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Sets the identifier of this project.
	 *
	 * @param id the identifier of this project
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the name of this project.
	 * 
	 * @return the name of this project
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this project.
	 * 
	 * @param name the name of this project
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description of this project.
	 * 
	 * @return the description of this project
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description of this project.
	 * 
	 * @param description the description of this project
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the project level VCS facade of this project.
	 * 
	 * @return the VCS facade
	 */
	public VcsFacade getVcs() {
		return vcs;
	}

	/**
	 * Sets the project level VCS facade of this project.
	 * 
	 * @param vcs the VCS facade
	 */
	public void setVcs(VcsFacade vcs) {
		this.vcs = vcs;
	}
}
