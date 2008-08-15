/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 18:10:31
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

public class VssAdaptorFacade extends com.luntsys.luntbuild.facades.lb12.VcsFacade {
	private String vssPath; // path to srcsafe.ini file
	private String vssUser; // user name
	private String vssPassword; // password
	private String dateTimeFormat; // datetime format for ss HISTORY command
	private String ssDir; // path to ss.exe file, if blank, ss.exe is expected to be in the PATH environment variable

	/**
	 * Get vss database path. This is a project level property.
	 * @return vss database path
	 */
	public String getVssPath() {
		return vssPath;
	}

	/**
	 * Set vss database path.
	 * @param vssPath
	 */
	public void setVssPath(String vssPath) {
		this.vssPath = vssPath;
	}

	/**
	 * Get user name to access vss database
	 * @return user name to access vss database
	 */
	public String getVssUser() {
		return vssUser;
	}

	/**
	 * Set user name to access vss database
	 * @param vssUser
	 */
	public void setVssUser(String vssUser) {
		this.vssUser = vssUser;
	}

	/**
	 * Get password to access vss database
	 * @return password to access vss database
	 */
	public String getVssPassword() {
		return vssPassword;
	}

	/**
	 * Set password to access vss database
	 * @param vssPassword
	 */
	public void setVssPassword(String vssPassword) {
		this.vssPassword = vssPassword;
	}

	/**
	 * Get date time format for vss client in build machine. This is a project level property
	 * @return data time format for vss client in build machine
	 */
	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	/**
	 * Set date time format for vss client in build machine
	 * @param dateTimeFormat
	 */
	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	/**
	 * Get ss.exe path. This is a project level property.
	 * @return ss.exe path
	 */
	public String getSsDir() {
		return ssDir;
	}

	/**
	 * Set ss.exe path
	 * @param ssDir
	 */
	public void setSsDir(String ssDir) {
		this.ssDir = ssDir;
	}

	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.VssAdaptor";
	}
}
