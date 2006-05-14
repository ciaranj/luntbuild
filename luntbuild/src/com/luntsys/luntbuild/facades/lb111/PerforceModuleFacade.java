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
package com.luntsys.luntbuild.facades.lb111;

public class PerforceModuleFacade extends com.luntsys.luntbuild.facades.lb111.ModuleFacade {
	private String depotPath;
	private String label;
	private String clientPath;

	/**
	 * Get depot path of this module
	 * @return depot path of this module
	 */
	public String getDepotPath() {
		return depotPath;
	}

	/**
	 * Set depot path of this module
	 * @param depotPath
	 */
	public void setDepotPath(String depotPath) {
		this.depotPath = depotPath;
	}

	/**
	 * Get label of the depot path
	 * @return label of the depot path
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set label of the depot path
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Get client path of this module
	 * @return client path of this module
	 */
	public String getClientPath() {
		return clientPath;
	}

	/**
	 * Set client path of this module
	 * @param clientPath
	 */
	public void setClientPath(String clientPath) {
		this.clientPath = clientPath;
	}
}
