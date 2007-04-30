/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-7-28
 * Time: 18:19:36
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

import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * Display property is an user editable property displaying at
 * the user interface
 *
 * @author robin shine
 */
public abstract class DisplayProperty {

    IPropertySelectionModel model = null;

	/**
	 * @return the display name of this property
	 */
	public abstract String getDisplayName();
	/**
	 * @return the description of this property
	 */
	public abstract String getDescription();
	/**
	 * @return is this property required? Required property will suffer to none-empty
	 * checking
	 */
	public boolean isRequired(){
		return true;
	}
	/**
	 * @return is property needs multi-line input?
	 */
	public boolean isMultiLine(){
		return false;
	}
	/**
	 * @return is this property secret? secret property will use ***** for values display
	 */
	public boolean isSecret(){
		return false;
	}

    /** Returns is this property select/droplist?
     * @return is this property select/droplist?
     */
    public boolean isSelect(){
        return false;
    }

	/**
	 * @return current value for this property
	 */
	public abstract String getValue();

	/** Returns current evaluated value for this property if applicable. By default returns plain value.
	 * @return current evaluated value for this property
	 */
	public String getActualValue() {
		return getValue();
	}

	/**
	 * Set the current value for this proprety
	 * @param value
	 */
	public abstract void setValue(String value);

    /** Returns selection model.
     * @return selection model
     */
    public IPropertySelectionModel getSelectionModel() {
        return this.model;
    }

    /** Sets selection model.
     * @param model model
     */
    public void setSelectionModel(IPropertySelectionModel model) {
        this.model = model;
    }

	/** Checks if two objects equal.
	 * @param obj object
	 * @return true if equal
	 *
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DisplayProperty){
			DisplayProperty displayProperty = (DisplayProperty) obj;
			return (getValue() == null? displayProperty.getValue() == null:
				getValue().equals(displayProperty.getValue()));
		}
		return false;
	}
}