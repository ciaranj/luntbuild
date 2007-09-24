/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-11-15
 * Time: 14:53:22
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
 * Notification config facade.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.db.NotifyMapping
 */
public class NotificationConfigFacade {
	private long id;
	private long viewId;
	private long userId;

	/**
	 * Gets the identifer of this notification config.
	 * 
	 * @return the identifer of this notification config
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the identifier of this notification config.
	 *
	 * @param id the identifier of this notification config
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the identifier of the view of this notification config.
	 * 
	 * @return the identifier of the view
	 */
	public long getViewId() {
		return viewId;
	}

	/**
	 * Sets the identifier of the view of this notification config.
	 * 
	 * @param viewId the identifier of the view
	 */
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}

	/**
	 * Gets the identifier of the user of this notification config.
	 * 
	 * @return the identifier of the user
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * Sets the identifier of the user of this notification config.
	 * 
	 * @param userId the identifier of the user
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}
}
