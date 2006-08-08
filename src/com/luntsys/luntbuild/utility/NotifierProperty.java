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
 * This class denotes a notifier property
 * @author robin shine
 */
public abstract class NotifierProperty {

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
        return false;
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

	public abstract Class getNotifierClass();

	/**
	 * @return key value for property value
	 */
	public String getKey() {
		return getNotifierClass().getName() + "#" + getDisplayName();
	}

	/**
	 * @param properties should not be null
	 * @return
	 */
	public String getValue(Map properties) {
		return (String) properties.get(getKey());
	}

	/**
	 * @param properties should not be null
	 * @param value
	 */
	public void setValue(Map properties, String value) {
		properties.put(getKey(), value);
	}
}