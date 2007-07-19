/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-19
 * Time: 9:44:09
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
 * Property facade.
 * 
 * @see com.luntsys.luntbuild.db.Property
 */
public class PropertyFacade {
	private String name;
	private String value;

	/**
	 * Sets the name of this property.
	 * 
	 * @param name the name of this property
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * Gets the name of this property.
	 * 
	 * @return the name of this property
	 */
	public String getName(){
		return name;
	}

	/**
	 * Sets the value of this property.
	 * 
	 * @param value the value of this property
	 */
	public void setValue(String value){
		this.value = value;
	}

	/**
	 * Gets the value of this property.
	 * 
	 * @return the value of this property
	 */
	public String getValue(){
		return value;
	}
}
