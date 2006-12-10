/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 19:24:20
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
 * Facade of svn adaptor
 * @author robin shine
 */
public class SvnAdaptorFacade extends VcsFacade {
	private String urlBase;
	private String trunk;
	private String branches;
	private String tags;
	private String user;
	private String password;
	private String svnDir;

	/**
	 * Get url base of the svn adaptor. This is a project level property.
	 * @return url base of the svn adaptor
	 */
	public String getUrlBase() {
		return urlBase;
	}

	/**
	 * Set url base of this svn adaptor
	 * @param urlBase
	 */
	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}

	/**
	 * Get trunk directory of this svn repository. The trunk directory is relative to the
	 * url base.
	 * @return directory to holding trunks
	 */
	public String getTrunk() {
		return trunk;
	}

	/**
	 * Set the trunk directory
	 * @param trunk
	 */
	public void setTrunk(String trunk) {
		this.trunk = trunk;
	}

	/**
	 * Get directory name to put branches. This is a project level property.
	 * Refer to user manual for detailed explanation.
	 * @return directory name to put branches
	 */
	public String getBranches() {
		return branches;
	}

	/**
	 * Set directory name to put branches.
	 * @param branches
	 */
	public void setBranches(String branches) {
		this.branches = branches;
	}

	/**
	 * Get directory name to put labels. This is a project level property.
	 * Refer to user manual for detailed information.
	 * @return directory to put labels
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * Set directory name to put labels.
	 * @param tags
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * Get user name to access svn repository
	 * @return user name to access svn repository
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set user name to access svn repository
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get password to access svn repository
	 * @return password to access svn repository
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set password to access svn repository
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.SvnAdaptor";
	}

	public String getSvnDir() {
		return svnDir;
	}

	public void setSvnDir(String svnDir) {
		this.svnDir = svnDir;
	}
}
