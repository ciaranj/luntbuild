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
 * Perforce VCS adaptor facade.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.PerforceAdaptor
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
	 * Line end mode
	 */
	private String lineEnd;

	private String p4Dir;

    /** Perforce web interface to itegrate with */
    private String webInterface;
    /** Perforce web interface URL */
    private String webUrl;

	private String changelist;

    /**
     * Gets the corresponding VCS adaptor class name.
     *
     * @return the VCS adaptor class name
     */
	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.PerforceAdaptor";
	}

    /**
     * Gets the server name and port to connect to the Perforce server with.
     * 
     * @return the server name and port
     */
	public String getPort() {
		return port;
	}

    /**
     * Sets the server name and port to connect to the Perforce server with.
     * 
     * @param port the server name and port
     */
	public void setPort(String port) {
		this.port = port;
	}

    /**
     * Gets the login user to use.
     * 
     * @return the login user
     */
	public String getUser() {
		return user;
	}

    /**
     * Sets the login user to use.
     * 
     * @param user the login user
     */
	public void setUser(String user) {
		this.user = user;
	}

    /**
     * Gets the login password to use.
     * 
     * @return the login password
     */
	public String getPassword() {
		return password;
	}

    /**
     * Sets the login password to use.
     * 
     * @param password the login password
     */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the line end mode for checked-out text files.
	 * 
	 * @return the line end mode
	 */
	public String getLineEnd() {
		return lineEnd;
	}

	/**
	 * Sets the line end mode for checked-out text files.
	 * 
	 * @param lineEnd the line end mode
	 */
	public void setLineEnd(String lineEnd) {
		this.lineEnd = lineEnd;
	}

	/**
	 * Gets the path to the Perforce executable.
	 * 
	 * @return the path to the Perforce executable
	 */
	public String getP4Dir() {
		return p4Dir;
	}

	/**
	 * Sets the path to the Perforce executable.
	 * 
	 * @param p4Dir the path to the Perforce executable
	 */
	public void setP4Dir(String p4Dir) {
		this.p4Dir = p4Dir;
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

    /**
     * Gets the tip changelist.
     * 
     * @return the tip changelist
     */
	public String getChangelist() {
		return changelist;
	}

    /**
     * Sets the tip changelist.
     * 
     * @param changelist the tip changelist
     */
	public void setChangelist(String changelist) {
		this.changelist = changelist;
	}
}
