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
 * StarTeam VCS adaptor facade.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.StarteamAdaptor
 */
public class StarteamAdaptorFacade extends VcsFacade {
	private String projectLocation;
	private String user;
	private String password;
	private String convertEOL;

    /**
     * Gets the corresponding VCS adaptor class name.
     *
     * @return the VCS adaptor class name
     */
	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.StarteamAdaptor";
	}

	/**
	 * Gets the project location.
	 * <p>Location of a StarTeam project is defined as: <servername>:<portnum>/<projectname>,
	 * where <servername> is the host where the StarTeam server runs, <portnum> is the port
	 * number the StarTeam server uses, default value is 49201. <projectname> is a StarTeam
	 * project under this StarTeam server.</p>
	 * 
	 * @return the project location
	 */
	public String getProjectLocation() {
		return projectLocation;
	}

	/**
	 * Sets the project location.
	 * 
	 * @param projectLocation the project location
	 */
	public void setProjectLocation(String projectLocation) {
		this.projectLocation = projectLocation;
	}

	/**
	 * Gets the login user.
	 * 
	 * @return the login user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the login user.
	 * 
	 * @param user the login user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets the login password.
	 * 
	 * @return the login password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the login password.
	 * 
	 * @param password the login password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the convert EOL property ("yes"/"no").
	 * 
	 * @return the convert EOL property
	 */
	public String getConvertEOL() {
		return convertEOL;
	}

	/**
	 * Sets the convert EOL property ("yes"/"no").
	 * 
	 * @param convertEOL the convert EOL property
	 */
	public void setConvertEOL(String convertEOL) {
		this.convertEOL = convertEOL;
	}
}
