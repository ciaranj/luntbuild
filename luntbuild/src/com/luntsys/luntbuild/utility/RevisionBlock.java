/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-11
 * Time: 21:28:53
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

package com.luntsys.luntbuild.utility;

import java.util.List;
import java.util.ArrayList;

/**
 * A single revision comprised of multiple lines. This is used
 * to help extract information of a multi-line revision.
 *
 * @author robin shine
 */
public class RevisionBlock {
	private List lines = new ArrayList();
	private boolean ready = false;
	private boolean valid = true;

	/**
	 * Gets the lines of this revision.
	 * 
	 * @return the lines
	 */
	public List getLines() {
		return lines;
	}

	/**
	 * Checks if this revision is ready.
	 * 
	 * @return <code>true</code> if this revision is ready
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * Sets the ready status of this revision.
	 * 
	 * @param ready set <code>true</code> if this revision is ready
	 */
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	/**
	 * Checks if this revision is valid.
	 * 
	 * @return <code>true</code> if this revision is valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Sets the valid status of this revision.
	 * 
	 * @param valid set <code>true</code> if this revision is valid
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
