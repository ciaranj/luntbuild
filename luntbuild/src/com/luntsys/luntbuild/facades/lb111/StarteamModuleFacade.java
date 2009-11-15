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
 * StarTeam VCS module facade.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.StarteamAdaptor.StarteamModule
 */
public class StarteamModuleFacade extends ModuleFacade {
	private String starteamView;
	private String srcPath;
	private String label;
	private String destPath;

	/**
	 * Gets the StarTeam view.
	 * 
	 * @return the StarTeam view
	 */
	public String getStarteamView() {
		return starteamView;
	}

	/**
	 * Sets the StarTeam view.
	 * 
	 * @param starteamView the StarTeam view
	 */
	public void setStarteamView(String starteamView) {
		this.starteamView = starteamView;
	}

	/**
	 * Gets the source path. This path is relative to the StarTeam view.
	 * 
	 * @return the source path
	 */
	public String getSrcPath() {
		return srcPath;
	}

	/**
	 * Sets the source path.
	 * 
	 * @param srcPath the source path
	 */
	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	/**
	 * Gets the label to use.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label to use.
	 * 
	 * @param label the label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the destination path to check out to.
	 * 
	 * @return the destination path
	 */
	public String getDestPath() {
		return destPath;
	}

	/**
	 * Sets the destination path to check out to.
	 * 
	 * @param destPath the destination path
	 */
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
}
