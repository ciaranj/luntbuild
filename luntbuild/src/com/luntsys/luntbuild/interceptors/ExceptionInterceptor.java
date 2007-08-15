/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 13:16:05
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

package com.luntsys.luntbuild.interceptors;

import com.luntsys.luntbuild.facades.LuntbuildException;
import com.luntsys.luntbuild.utility.Luntbuild;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A spring exception interceptor. It is used to intercept exceptions and convert
 * them into {@link LuntbuildException}.
 *
 * @author robin shine
 */
public class ExceptionInterceptor implements MethodInterceptor {
	private static Log logger = LogFactory.getLog(Luntbuild.class);

	/**
	 * Invokes this interceptor for a method invocation.
	 * 
	 * @param methodInvocation the method invocation
	 * @return the result of the method being invoked
	 * @throws Throwable from the method, wrapped in a {@link LuntbuildException}
	 */
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		try {
			return methodInvocation.proceed();
		} catch (Throwable throwable) {
			logger.error("Exception catched in ExceptionInterceptor.invoke()", throwable);			
			throw new LuntbuildException(Luntbuild.getExceptionMessage(throwable));
		}
	}
}
