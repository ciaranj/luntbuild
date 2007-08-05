/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-26
 * Time: 15:44:57
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

import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This comparator compares objects by their name property.
 *
 * @author robin shine
 */
public class NameComparator implements Comparator, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2820195295791480935L;

	/**
	 * Compares two objects by their name property.
	 * 
	 * @param o1 the first object
	 * @param o2 the second object
	 * @return <code>0</code> if both objects have the same name property,
	 *         <code>1</code> if the first object's name property is larger, or
	 *         <code>-1</code> if the second object's name property is larger
	 */
	public int compare(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return 0;
		if (o1 != null && o2 == null)
			return 1;
		if (o1 == null && o2 != null)
			return -1;
		try{
			String name1 = BeanUtils.getProperty(o1, "name");
			String name2 = BeanUtils.getProperty(o2, "name");
			if (name1 == null && name2 == null)
				return 0;
			if (name1 != null && name2 == null)
				return 1;
			if (name1 == null && name2 != null)
				return -1;
			return name1.compareTo(name2);
		}catch(Exception e){
			return 0;
		}
	}
}
