/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-11-15
 * Time: 14:54:55
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
 * View composition config facade.
 * 
 * @author robin shine
 */
public class ViewCompositionConfigFacade {
	private long id;
	private long compositeViewId;
	private long basicViewId;

	/**
	 * Gets the identifer of this view composition config.
	 * 
	 * @return the identifer of this view composition config
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the identifier of this view composition config.
	 *
	 * @param id the identifier of this view composition config
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the identifier of the composite view of this view composition config.
	 * 
	 * @return the identifier of the composite view
	 */
	public long getCompositeViewId() {
		return compositeViewId;
	}

	/**
	 * Sets the identifier of the composite view of this view composition config.
	 * 
	 * @param compositeViewId the identifier of the composite view
	 */
	public void setCompositeViewId(long compositeViewId) {
		this.compositeViewId = compositeViewId;
	}

	/**
	 * Gets the identifier of the basic view of this view composition config.
	 * 
	 * @return the identifier of the basic view
	 */
	public long getBasicViewId() {
		return basicViewId;
	}

	/**
	 * Sets the identifier of the basic view of this view composition config.
	 * 
	 * @param basicViewId the identifier of the basic view
	 */
	public void setBasicViewId(long basicViewId) {
		this.basicViewId = basicViewId;
	}
}
