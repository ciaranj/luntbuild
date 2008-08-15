/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 19:43:37
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
 * Facade of starteam adaptor
 * @author robin shine
 */
public class StarteamAdaptorFacade extends VcsFacade {
	private String projectLocation;
	private String user;
	private String password;
	private String convertEOL;

	/**
	 * Get project location of the starteam adaptor. This is a project level property.
	 * Refer to user manual for detail information.
	 * @return project location of the starteam adaptor
	 */
	public String getProjectLocation() {
		return projectLocation;
	}

	/**
	 * Set project location of the starteam adaptor
	 * @param projectLocation
	 */
	public void setProjectLocation(String projectLocation) {
		this.projectLocation = projectLocation;
	}

	/**
	 * Get user name to access starteam server. This is a project level property
	 * @return user name to access starteam server
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set user name to access starteam server
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get password to access starteam server. This is a project level property
	 * @return password to access starteam server
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set password to access starteam server
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get convert eol indication string. This is a project level property
	 * @return "yes" if convert eol, other wise "no"
	 */
	public String getConvertEOL() {
		return convertEOL;
	}

	/**
	 * Set convert eol indication string. Should be "yes" or "no".
	 * @param convertEOL
	 */
	public void setConvertEOL(String convertEOL) {
		this.convertEOL = convertEOL;
	}

	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.StarteamAdaptor";
	}
}
