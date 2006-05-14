/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 19:44:16
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
package com.luntsys.luntbuild.facades.lb111;

/**
 * Facade of a starteam module
 * @author robin shine
 */
public class StarteamModuleFacade extends com.luntsys.luntbuild.facades.lb111.ModuleFacade {
	private String starteamView;
	private String srcPath;
	private String label;
	private String destPath;

	/**
	 * Get starteam view
	 * @return starteam view
	 */
	public String getStarteamView() {
		return starteamView;
	}

	/**
	 * Set starteam view of this module
	 * @param starteamView
	 */
	public void setStarteamView(String starteamView) {
		this.starteamView = starteamView;
	}

	/**
	 * Get source path of this module
	 * @return source path of this module
	 */
	public String getSrcPath() {
		return srcPath;
	}

	/**
	 * Set source path of this module
	 * @param srcPath
	 */
	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	/**
	 * Get label of this module
	 * @return label of this module
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set label of this module
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Get destination path of this module
	 * @return destination path of this module
	 */
	public String getDestPath() {
		return destPath;
	}

	/**
	 * Set destination path of this module
	 * @param destPath
	 */
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
}
