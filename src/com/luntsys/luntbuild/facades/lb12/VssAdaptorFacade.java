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

/**
 * Microsoft Visual Source Safe VCS adaptor facade.
 * 
 * @see com.luntsys.luntbuild.vcs.VssAdaptor
 */
public class VssAdaptorFacade extends VcsFacade {
	private String vssPath; // path to srcsafe.ini file
	private String vssUser; // user name
	private String vssPassword; // password
	private String dateTimeFormat; // datetime format for ss HISTORY command
	private String ssDir; // path to ss.exe file, if blank, ss.exe is expected to be in the PATH environment variable

    /**
     * Gets the corresponding VCS adaptor class name.
     *
     * @return the VCS adaptor class name
     */
	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.VssAdaptor";
	}

	/**
	 * Gets the SourceSafe path (SSDIR), location should contain srcsafe.ini.
	 * 
	 * @return the SourceSafe path
	 */
	public String getVssPath() {
		return vssPath;
	}

	/**
	 * Sets the SourceSafe path (SSDIR), location should contain srcsafe.ini.
	 * 
	 * @param vssPath the SourceSafe path
	 */
	public void setVssPath(String vssPath) {
		this.vssPath = vssPath;
	}

	/**
	 * Gets the login user.
	 * 
	 * @return the login user
	 */
	public String getVssUser() {
		return vssUser;
	}

	/**
	 * Sets the login user.
	 * 
	 * @param vssUser the login user
	 */
	public void setVssUser(String vssUser) {
		this.vssUser = vssUser;
	}

	/**
	 * Gets the login password.
	 * 
	 * @return the login password
	 */
	public String getVssPassword() {
		return vssPassword;
	}

	/**
	 * Sets the login password.
	 * 
	 * @param vssPassword the login password
	 */
	public void setVssPassword(String vssPassword) {
		this.vssPassword = vssPassword;
	}

	/**
	 * Gets the datetime format to use for the <code>ss history</code> command.
	 * 
	 * @return the datetime format
	 */
	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	/**
	 * Sets the datetime format to use for the <code>ss history</code> command.
	 * 
	 * @param dateTimeFormat the datetime format
	 */
	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	/**
	 * Gets the path to the SourceSafe executable.
	 * 
	 * @return the path to the SourceSafe executable
	 */
	public String getSsDir() {
		return ssDir;
	}

	/**
	 * Sets the path to the SourceSafe executable.
	 * 
	 * @param ssDir the path to the SourceSafe executable
	 */
	public void setSsDir(String ssDir) {
		this.ssDir = ssDir;
	}
}
