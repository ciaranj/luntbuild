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
 * Subversion VCS adaptor facade.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.SvnAdaptor
 */
public class SvnAdaptorFacade extends VcsFacade {
	private String urlBase;
    private String layout;
	private String trunk;
	private String branches;
	private String tags;
	private String user;
	private String password;
    /** Subversion web interface to itegrate with */
    private String webInterface;
    /** Subversion web interface URL */
    private String webUrl;

    /**
     * Gets the corresponding VCS adaptor class name.
     *
     * @return the VCS adaptor class name
     */
	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.SvnAdaptor";
	}

	/**
	 * Gets the repository URL base.
	 * 
	 * @return the URL base
	 */
	public String getUrlBase() {
		return urlBase;
	}

	/**
	 * Sets the repository URL base.
	 * 
	 * @param urlBase the URL base
	 */
	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}

    /**
     * Gets the repository layout.
     * 
     * @return the repository layout
     */
    public String getLayout() {
        return layout;
    }

    /**
     * Sets the repository layout.
     * 
     * @param layout the repository layout
     */
    public void setLayout(String layout) {
        this.layout = layout;
    }

	/**
	 * Gets the directory for the trunk.
	 * 
	 * @return the trunk directory
	 */
	public String getTrunk() {
		return trunk;
	}

	/**
	 * Sets the directory for the trunk.
	 * 
	 * @param trunk the trunk directory
	 */
	public void setTrunk(String trunk) {
		this.trunk = trunk;
	}

	/**
	 * Gets the directory for the branches.
	 * 
	 * @return the branches directory
	 */
	public String getBranches() {
		return branches;
	}

	/**
	 * Sets the directory for the branches.
	 * 
	 * @param branches the branches directory
	 */
	public void setBranches(String branches) {
		this.branches = branches;
	}

	/**
	 * Gets the directory for the tags.
	 * 
	 * @return the tags directory
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * Sets the directory for the tags.
	 * 
	 * @param tags the tags directory
	 */
	public void setTags(String tags) {
		this.tags = tags;
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
     * Gets the web interface to integrate with.
     * 
     * @return the web interface to integrate with
     */
    public String getWebInterface() {
        return webInterface;
    }

    /**
     * Sets the web interface to integrate with.
     * 
     * @param webInterface the web interface to integrate with
     */
    public void setWebInterface(String webInterface) {
        this.webInterface = webInterface;
    }

    /**
     * Gets the web interface URL.
     * 
     * @return the web interface URL
     */
    public String getWebUrl() {
        return webUrl;
    }

    /**
     * Sets the web interface URL.
     * 
     * @param webUrl the web interface URL
     */
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
