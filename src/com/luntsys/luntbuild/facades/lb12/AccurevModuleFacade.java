/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-4-3
 * Time: 21:38:45
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
 * AccuRev VCS module facade.
 * 
 * @author Jason Carreira <jcarreira@eplus.com>
 * @see com.luntsys.luntbuild.vcs.AccurevAdaptor.AccurevModule
 */
public class AccurevModuleFacade extends ModuleFacade {
	private String depot;
	private String srcPath;
	private String backingStream;
	private String buildStream;
	private String label;

	/**
	 * Gets the AccuRev depot name.
	 * 
	 * @return the depot name
	 */
	public String getDepot() {
		return depot;
	}

	/**
	 * Sets the AccuRev depot name.
	 * 
	 * @param depot the depot name
	 */
	public void setDepot(String depot) {
		this.depot = depot;
	}

	/**
	 * Gets the source path where this module should be put.
	 * 
	 * @return the source path
	 */
	public String getSrcPath() {
		return srcPath;
	}

	/**
	 * Sets the source path where this module should be put.
	 * 
	 * @param srcPath the source path
	 */
	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	/**
	 * Gets the backing stream.
	 * 
	 * @return the backing stream
	 */
	public String getBackingStream() {
		return backingStream;
	}

	/**
	 * Sets the backing stream.
	 * 
	 * @param backingStream the backing stream
	 */
	public void setBackingStream(String backingStream) {
		this.backingStream = backingStream;
	}

	/**
	 * Gets the backing stream for this build module.
	 * 
	 * @return the build stream
	 */
	public String getBuildStream() {
		return buildStream;
	}

	/**
	 * Sets the backing stream for this build module.
	 * 
	 * @param buildStream the build stream
	 */
	public void setBuildStream(String buildStream) {
		this.buildStream = buildStream;
	}

	/**
	 * Gets the transaction number with which to sync.
	 * 
	 * @return the transaction number
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the transaction number with which to sync.
	 * 
	 * @param label the transaction number
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}
