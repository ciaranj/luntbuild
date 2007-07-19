/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-11-4
 * Time: 15:43:28
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

/**
 * The variable class.
 * 
 * @author robin shine
 */
public class Variable {
	private VariableHolder holder;
	private String name;
	private String value;

	/**
	 * Creates a new variable.
	 * 
	 * @param holder the variable counter holder
	 * @param name the variable name
	 * @param value the variable value
	 */
	public Variable(VariableHolder holder, String name, String value) {
		this.holder = holder;
		this.name = name;
		this.value = value;
	}

	/**
	 * Increases this variable as an integer.
	 * 
	 * @return the integer value of this variable before the increase
	 */
	public int increaseAsInt() {
		int intValue;
		if (Luntbuild.isEmpty(value))
			intValue = 0;
		else
			intValue = new Integer(value).intValue();
		int oldIntValue = intValue;
		intValue ++;
		value = String.valueOf(intValue);
		holder.setVar(name, this);
		return oldIntValue;
	}

	/**
	 * Increments this variable.
	 * 
	 * @return the variable
	 */
    public Variable increment() {
        int intValue;
        if (Luntbuild.isEmpty(value))
            intValue = 0;
        else
            intValue = new Integer(value).intValue();
        intValue ++;
        value = String.valueOf(intValue);
        holder.setVar(name, this);
        return this;
    }

	/**
	 * Decreases this variable as an integer.
	 * 
	 * @return the integer value of this variable before the decrease
	 */
	public int decreaseAsInt() {
		int intValue;
		if (Luntbuild.isEmpty(value))
			intValue = 0;
		else
			intValue = new Integer(value).intValue();
		int oldIntValue = intValue;
		intValue --;
		value = String.valueOf(intValue);
		holder.setVar(name, this);
		return oldIntValue;
	}

	/**
	 * Decrements this variable.
	 * 
	 * @return the variable
	 */
    public Variable decrement() {
        int intValue;
        if (Luntbuild.isEmpty(value))
            intValue = 0;
        else
            intValue = new Integer(value).intValue();
        intValue --;
        value = String.valueOf(intValue);
        holder.setVar(name, this);
        return this;
    }

	/**
	 * Gets the string value of this variable.
	 * 
	 * @return the string value of this variable
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the string value of this variable.
	 * 
	 * @param value the value
	 * @return the string value of this variable
	 */
	public String setValue(String value) {
		String oldValue = this.value;
		this.value = value;
		holder.setVar(name, this);
		return oldValue;
	}

	/**
	 * Gets the integer value of this variable.
	 * If the value can not be converted to an integer, <code>0</code> will be returned.
	 * 
	 * @return the integer value of this variable
	 */
	public int getIntValue() {
		int intValue;
		if (Luntbuild.isEmpty(value))
			intValue = 0;
		else
			intValue = new Integer(value).intValue();
		return intValue;
	}

	/**
	 * Sets the value of this variable as an integer.
	 * 
	 * @param intValue the integer value
	 * @return the integer value of this variable
	 */
	public int setIntValue(int intValue) {
		int oldIntValue;
		if (Luntbuild.isEmpty(value))
			oldIntValue = 0;
		else
			oldIntValue = new Integer(value).intValue();
		value = String.valueOf(intValue);
		holder.setVar(name, this);
		return oldIntValue;
	}

	/**
	 * Gets the string representation of this variable.
	 * 
	 * @return the string representation of this variable
	 */
	public String toString() {
		return value;
	}
}
