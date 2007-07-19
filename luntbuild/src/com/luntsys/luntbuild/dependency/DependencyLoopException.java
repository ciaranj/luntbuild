/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-3-4
 * Time: 22:23:10
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

package com.luntsys.luntbuild.dependency;

import java.util.Set;
import java.util.Iterator;

/**
 * Luntbuild dependency specific exception.
 * @author robin shine
 */
public class DependencyLoopException extends Exception {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new dependency loop exception with the specified detail message.
	 * 
	 * @param message the detail message. The detail message is saved for later retrieval by the
	 * {@link #getMessage(Set)} method.
	 */
	public DependencyLoopException(String message){
		super(message);
	}

	/**
	 * Constructs a new dependency loop exception with <code>null</code> as its detail message.
	 */
	public DependencyLoopException(){
		super();
	}

    /**
     * Returns the detail message string of this dependency loop exception.  The set of nodes is required to
     * list the nodes that have a dependency loop.
     * 
     * @param loopNodes the set of nodes to check
     * @return  the detail message string of this dependency loop exception instance
     */
	public static String getMessage(Set loopNodes) {
		Iterator it = loopNodes.iterator();
		StringBuffer message = new StringBuffer();
		message.append("Dependency loop detected in the following nodes: \n");
		while (it.hasNext()) {
			DependentNode node = (DependentNode) it.next();
			message.append(node.toString() + "\n");
		}
		return message.toString();
	}
}
