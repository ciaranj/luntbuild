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
 * Facade of a luntbuild project, this is mainly used by web service client to operate
 * project specific properties
 *
 * @author robin shine
 */
public class ProjectFacade {
	private long id;
	private String name;
	private String description;
	private VcsFacade vcs;

	/**
	 * Get name of this project
	 * @return name of this project
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get id of this project
	 * @return id of this project
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set id of this project
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Set name of this project
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get description of this project
	 * @return description of this project
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set description of this project
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get project level vcs object of this project
	 * @return project level vcs object
	 */
	public VcsFacade getVcs() {
		return vcs;
	}

	/**
	 * Set project level vcs object of this project
	 * @param vcs
	 */
	public void setVcs(VcsFacade vcs) {
		this.vcs = vcs;
	}
}
