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

package com.luntsys.luntbuild.facades.lb12;

/**
 * MKS VCS module facade.
 * 
 * @author Stefan Baramov (TRX Inc.)
 * @see com.luntsys.luntbuild.vcs.MksAdaptor.MksModule
 */
public class MksModuleFacade extends ModuleFacade {

	private String version;

	private String subproject;

	private String projectFileName;

	private String developmentPath;

	private boolean external;

	/**
	 * Constructor, creates a new MKS VCS module facade.
	 */
	public MksModuleFacade() {
		super();
	}

	/**
	 * Checks if this is an external module.
	 * 
	 * @return <code>true</code> if this is an external module
	 */
	public boolean isExternal() {
		return external;
	}

	/**
	 * Sets if this is an external module.
	 * 
	 * @param external set <code>true</code> if this is an external module
	 */
	public void setExternal(boolean external) {
		this.external = external;
	}

	/**
	 * Gets the project file name.
	 * 
	 * @return the project file name
	 */
	public String getProjectFileName() {
		return projectFileName;
	}

	/**
	 * Sets the project file name.
	 * 
	 * @param projectFileName the project file name
	 */
	public void setProjectFileName(String projectFileName) {
		this.projectFileName = projectFileName;
	}

	/**
	 * Gets the subproject name.
	 * 
	 * @return the subproject name
	 */
	public String getSubproject() {
		return subproject;
	}

	/**
	 * Sets the subproject name.
	 * 
	 * @param subproject the subproject name
	 */
	public void setSubproject(String subproject) {
		this.subproject = subproject;
	}

	/**
	 * Gets the subproject version or checkpoint to be retrieved.
	 * 
	 * @return the subproject version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the subproject version or checkpoint to be retrieved.
	 * 
	 * @param version the subproject version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the development path.
	 * 
	 * @return the development path
	 */
	public String getDevelopmentPath() {
		return developmentPath;
	}

	/**
	 * Sets the development path.
	 * 
	 * @param developmentPath the development path
	 */
	public void setDevelopmentPath(String developmentPath) {
		this.developmentPath = developmentPath;
	}
}
