/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-26
 * Time: 17:47:47
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

import java.util.Map;

import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * Notifier property is an user editable property displayed at the user interface.
 *
 * @author robin shine
 */
public abstract class NotifierProperty {

    IPropertySelectionModel model = null;

	/**
	 * Gets the display name of this property.
	 * 
	 * @return the display name
	 */
	public abstract String getDisplayName();

	/**
	 * Gets the description of this property.
	 * 
	 * @return the description
	 */
	public abstract String getDescription();

	/**
	 * Checks if this property is required.  Required properties must not be empty.
	 * 
	 * @return <code>true</code> if this property is required
	 */
    public boolean isRequired(){
        return false;
    }

	/**
	 * Checks if this property needs multi-line input.
	 * 
	 * @return <code>true</code> if this property is multi-line
	 */
    public boolean isMultiLine(){
        return false;
    }

	/**
	 * Checks if this property is secret.  Secret properties will show "*****" instead of their value.
	 * 
	 * @return <code>true</code> if this property is secret
	 */
	public boolean isSecret(){
		return false;
	}

    /**
     * Checks if this property is a selection or droplist.
     * 
     * @return <code>true</code> if this property is a selection or droplist
     */
    public boolean isSelect(){
        return false;
    }

    /**
     * Gets the selection model for this property.
     * 
     * @return the selection model
     */
    public IPropertySelectionModel getSelectionModel() {
        return this.model;
    }

    /**
     * Sets the selection model for this property.
     * 
     * @param model the selection model
     */
    public void setSelectionModel(IPropertySelectionModel model) {
        this.model = model;
    }

	/**
	 * Gets the notifier class that this property is associated with.
	 * 
	 * @return the notifier class
	 */
	public abstract Class getNotifierClass();

	/**
	 * Gets the key value for this property.
	 * 
	 * @return the key value
	 */
	public String getKey() {
		return getNotifierClass().getName() + "#" + getDisplayName();
	}

	/**
	 * Gets the value of this property.
	 * 
	 * @param properties the map of all notifier properties
	 * @return the value
	 */
	public String getValue(Map properties) {
		return (String) properties.get(getKey());
	}

	/**
	 * Sets the value of this property.
	 * 
	 * @param properties the map of all notifier properties
	 * @param value the value
	 */
	public void setValue(Map properties, String value) {
		properties.put(getKey(), value);
	}
}
