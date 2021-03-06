/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 16:03:58
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

package com.luntsys.luntbuild.security;

import org.acegisecurity.AuthenticationException;

/**
 * Authentication specific exception, integrates into the acegi framework provided
 * Exception sheme.
 * 
 * <p>An instance of this exception is thrown in case authorization fails.</p>
 * 
 * @author johannes plachy
 */
public class ApplicationAuthenticationException extends AuthenticationException
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3546357336206947890L;

    /**
     * Constructs a new authentication exception with the specified detail message.
     * 
     * @param text the detail message. The detail message is saved for later retrieval by the
	 * {@link java.lang.Throwable#getMessage()} method.
     */
    public ApplicationAuthenticationException(String text)
    {
        super(text);
    }

    /**
     * Constructs a new authentication exception with the specified detail message and cause. 
     * 
     * @param text the detail message. The detail message is saved for later retrieval by the
	 * {@link java.lang.Throwable#getMessage()} method.
     * @param throwable the cause. The cause is saved for later retrieval by the 
     * {@link java.lang.Throwable#getCause()} method.
     * (A <code>null</code> value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ApplicationAuthenticationException(String text,Throwable throwable)
    {
        super(text, throwable);
    }
}
