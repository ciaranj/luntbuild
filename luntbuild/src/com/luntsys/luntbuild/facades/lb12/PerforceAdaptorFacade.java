/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 18:25:26
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
 * Facade of perforce adaptor
 * @author robin shine
 */
public class PerforceAdaptorFacade extends VcsFacade {
	/**
	 * Perforce port, such as 1666 or <server>:1666, etc.
	 */
	private String port;

	/**
	 * Perforce user
	 */
	private String user;
	/**
	 * Perforce password
	 */
	private String password;

	/**
	 * line end mode, possible values are "local", "unix", "mac", "win", "share". Refer
	 * to user manual for detail information
	 */
	private String lineEnd;

	private String p4Dir;

	/**
	 * Get the perforce port. This is a project level property
	 * @return perforce port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Set perforce port
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * Get user name to access perforce. This is a project level property
	 * @return user name to access perforce
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set user name to access perforce
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get password to access perforce. This is a project level property
	 * @return password to access perforce
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set password to access perforce
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get line end mode for checkouted text files. This is a project level property
	 * @return one value of "local", "unix", "mac", "win", "share". Refer to
	 * user manual for detail information
	 */
	public String getLineEnd() {
		return lineEnd;
	}

	/**
	 * Set line end mode for checkouted text files
	 * @param lineEnd one value of "local", "unix", "mac", "win", "share".
	 */
	public void setLineEnd(String lineEnd) {
		this.lineEnd = lineEnd;
	}

	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.PerforceAdaptor";
	}

	public String getP4Dir() {
		return p4Dir;
	}

	public void setP4Dir(String p4Dir) {
		this.p4Dir = p4Dir;
	}
}
