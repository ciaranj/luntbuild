/*
 * Copyright TRX Inc(c) 2006,
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
 * MksAdaptor facade class.
 */
public class MksAdaptorFacade extends VcsFacade {

	private String defaultHostname;

	private int defaultPort;

	private String defaultUsername;

	private String defaultPassword;

	private String rootProject;

	/**
	 * Initialize a new object with its default settings.
	 */
	public MksAdaptorFacade() {

		super();
	}

	/**
	 * @inheritDoc
	 *
	 * @see com.luntsys.luntbuild.facades.lb20.VcsFacade#getVcsClassName()
	 */
	public String getVcsClassName() {

		return "com.luntsys.luntbuild.vcs.MksAdaptor";
	}

	public String getDefaultPassword() {

		return defaultPassword;
	}

	public void setDefaultPassword(String defaultPassword) {

		this.defaultPassword = defaultPassword;
	}

	public String getDefaultUsername() {

		return defaultUsername;
	}

	public void setDefaultUsername(String defaultUsername) {

		this.defaultUsername = defaultUsername;
	}

	public String getRootProject() {

		return rootProject;
	}

	public void setRootProject(String rootProject) {

		this.rootProject = rootProject;
	}

	public String getDefaultHostname() {

		return defaultHostname;
	}

	public void setDefaultHostname(String defaultHostname) {

		this.defaultHostname = defaultHostname;
	}

	public int getDefaultPort() {

		return defaultPort;
	}

	public void setDefaultPort(int defaultPort) {

		this.defaultPort = defaultPort;
	}

}
