/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-20
 * Time: 20:30:16
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

import java.util.List;
import java.util.ArrayList;

/**
 * Base class for all VCS adaptor facades.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.Vcs
 */
public abstract class VcsFacade {
	private List modules = new ArrayList();

    /**
     * Gets the corresponding VCS adaptor class name.
     *
     * @return the VCS adaptor class name
     */
	public abstract String getVcsClassName();

	/**
	 * Gets the modules of this VCS.
	 *
	 * @return the modules of this VCS
	 */
	public List getModules() {
		return modules;
	}

	/**
	 * Sets the modules of this VCS.
	 *
	 * @param modules the modules of this VCS
	 */
	public void setModules(List modules) {
		this.modules = modules;
	}
}
