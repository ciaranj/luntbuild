/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-20
 * Time: 19:57:43
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
 * CVS VCS adaptor facade.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.CvsAdaptor
 */
public class CvsAdaptorFacade extends VcsFacade {
	private String cvsRoot;
	private String cvsPassword;
	private String cygwinCvs;
	private String disableHistoryCmd;
	private String disableSuppressOption;
	private String cvsDir;
    private String webInterface;
    private String webUrl;

    /**
     * Gets the corresponding VCS adaptor class name.
     *
     * @return the VCS adaptor class name
     */
	public final String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.CvsAdaptor";
	}

	/**
	 * Gets the CVS root of the repository.
	 * 
	 * @return the CVS root
	 */
	public String getCvsRoot() {
		return cvsRoot;
	}

	/**
	 * Sets the CVS root of the repository.
	 *
	 * @param cvsRoot the CVS root, an example can be ":pserver:anoncvs@cvs.pmease.com:/home/cvspublic"
	 */
	public void setCvsRoot(String cvsRoot) {
		this.cvsRoot = cvsRoot;
	}

	/**
	 * Gets the CVS password for the repository.
	 *
	 * @return the password
	 */
	public String getCvsPassword() {
		return cvsPassword;
	}

	/**
	 * Sets the CVS password for the repository.
	 *
	 * @param cvsPassword the password
	 */
	public void setCvsPassword(String cvsPassword) {
		this.cvsPassword = cvsPassword;
	}

	/**
	 * Gets the is cygwin CVS property.
	 * This property indicates whether or not ("yes" or "no") the CVS executable to use is the cygwin one.
	 * 
	 * @return the is cygwin CVS property
	 */
	public String getCygwinCvs() {
		return cygwinCvs;
	}

	/**
	 * Sets the is cygwin CVS property.
	 * This property indicates whether or not ("yes" or "no") the CVS executable to use is the cygwin one.
	 * 
	 * @param cygwinCvs the is cygwin CVS property
	 */
	public void setCygwinCvs(String cygwinCvs) {
		this.cygwinCvs = cygwinCvs;
	}

	/**
	 * Gets the disable history command property.
	 * This property indicates whether or not ("yes" or "no") to disable history command when checking revisions.
	 *
	 * @return the disable history command property
	 */
	public String getDisableHistoryCmd() {
		return disableHistoryCmd;
	}

	/**
	 * Sets the disable history command property.
	 * This property indicates whether or not ("yes" or "no") to disable history command when checking revisions.
	 *
	 * @param disableHistoryCmd the disable history command property
	 */
	public void setDisableHistoryCmd(String disableHistoryCmd) {
		this.disableHistoryCmd = disableHistoryCmd;
	}

	/**
	 * Gets the disable suppress property.
	 * This property indicates whether or not ("yes" or "no") to disable -S option for log command.
	 *
	 * @return the disable suppress property
	 */
	public String getDisableSuppressOption() {
		return disableSuppressOption;
	}

	/**
	 * Sets the disable suppress property.
	 * This property indicates whether or not ("yes" or "no") to disable -S option for log command.
	 *
	 * @param disableSuppressOption the disable suppress property
	 */
	public void setDisableSuppressOption(String disableSuppressOption) {
		this.disableSuppressOption = disableSuppressOption;
	}

	/**
	 * Gets the path to the CVS executable.
	 * 
	 * @return the path to the CVS executable
	 */
	public String getCvsDir() {
		return cvsDir;
	}

	/**
	 * Sets the path to the CVS executable.
	 * 
	 * @param cvsDir the path to the CVS executable
	 */
	public void setCvsDir(String cvsDir) {
		this.cvsDir = cvsDir;
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
