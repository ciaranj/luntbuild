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
package com.luntsys.luntbuild.facades.lb20;

/**
 * Facade of cvs adaptor
 * @author robin shine
 */
public class CvsAdaptorFacade extends VcsFacade {
	private String cvsRoot;
	private String cvsPassword;
	private String cygwinCvs;
	private String disableHistoryCmd;
	private String disableSuppressOption;
	private String cvsDir;

	/**
	 * Get cvs root. This is a project level property. Refer to user manual for detailed information
	 * @return cvs root
	 */
	public String getCvsRoot() {
		return cvsRoot;
	}

	/**
	 * Set cvs root
	 * @param cvsRoot
	 */
	public void setCvsRoot(String cvsRoot) {
		this.cvsRoot = cvsRoot;
	}

	public final String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.CvsAdaptor";
	}

	/**
	 * Get cvs password. This is a project level property.
	 * @return the cvs password
	 */
	public String getCvsPassword() {
		return cvsPassword;
	}

	/**
	 * Set cvs password
	 * @param cvsPassword
	 */
	public void setCvsPassword(String cvsPassword) {
		this.cvsPassword = cvsPassword;
	}

	/**
	 * Get cygwin cvs indication string. This is a project level property
	 * @return "yes" if used cvs is a cygwin cvs, or "no" otherwise
	 */
	public String getCygwinCvs() {
		return cygwinCvs;
	}

	/**
	 * Set cygwin cvs indication string
	 * @param cygwinCvs "yes", or "no" to indicate wheter or not used cvs is a cygwin
	 * one
	 */
	public void setCygwinCvs(String cygwinCvs) {
		this.cygwinCvs = cygwinCvs;
	}

	public String getDisableHistoryCmd() {
		return disableHistoryCmd;
	}

	public void setDisableHistoryCmd(String disableHistoryCmd) {
		this.disableHistoryCmd = disableHistoryCmd;
	}

	public String getDisableSuppressOption() {
		return disableSuppressOption;
	}

	public void setDisableSuppressOption(String disableSuppressOption) {
		this.disableSuppressOption = disableSuppressOption;
	}

	public String getCvsDir() {
		return cvsDir;
	}

	public void setCvsDir(String cvsDir) {
		this.cvsDir = cvsDir;
	}
}
