/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-8
 * Time: 20:56
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
package com.luntsys.luntbuild.web.selectionmodels;

import com.luntsys.luntbuild.notifiers.Notifier;
import com.luntsys.luntbuild.utility.Luntbuild;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * The selection model for notifiers configured in the system,
 *
 * @author robin shine
 */
public class NotifierSelectionModel implements IPropertySelectionModel{
	public int getOptionCount() {
		return Luntbuild.notifiers.size();
	}

	public Object getOption(int index) {
		return Luntbuild.notifiers.get(index);
	}

	public String getLabel(int index) {
		Class notifierClass = (Class) getOption(index);
		try {
			Notifier notifier = (Notifier) notifierClass.newInstance();
			return notifier.getDisplayName();
		} catch (Exception e) {
			throw new ApplicationRuntimeException(e);
		}
	}

	public String getValue(int index) {
		return String.valueOf(index);
	}

	public Object translateValue(String value) {
		return Luntbuild.notifiers.get(new Integer(value).intValue());
	}
}
