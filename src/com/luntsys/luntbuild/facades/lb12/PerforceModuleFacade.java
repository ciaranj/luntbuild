/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 18:29:45
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
 * Perforce VCS module facade.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.PerforceAdaptor.PerforceModule
 */
public class PerforceModuleFacade extends ModuleFacade {
	private String depotPath;
	private String label;
	private String clientPath;

    /**
     * Gets the depot path.
     * 
     * @return the depot path
     */
	public String getDepotPath() {
		return depotPath;
	}

    /**
     * Sets the depot path.
     * 
     * @param depotPath the depot path
     */
	public void setDepotPath(String depotPath) {
		this.depotPath = depotPath;
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
     * Gets the client path.
     * 
     * @return the client path
     */
	public String getClientPath() {
		return clientPath;
	}

    /**
     * Sets the client path.
     * 
     * @param clientPath the client path
     */
	public void setClientPath(String clientPath) {
		this.clientPath = clientPath;
	}
}
