/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-7
 * Time: 21:15:38
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

import com.luntsys.luntbuild.builders.Builder;
import com.luntsys.luntbuild.utility.Luntbuild;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * The selection model for builders classes
 *
 * @author robin shine
 */
public class BuilderClassSelectionModel implements IPropertySelectionModel{
	public int getOptionCount() {
		return Luntbuild.builders.size();
	}

	public Object getOption(int index) {
		return Luntbuild.builders.get(index);
	}

	public String getLabel(int index) {
		Class builderClass = (Class)getOption(index);
		Builder builder;
		try {
			builder = (Builder)(builderClass.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationRuntimeException(e);
		}
		return builder.getDisplayName();
	}

	public String getValue(int index) {
		return String.valueOf(index);
	}

	public Object translateValue(String value) {
		return Luntbuild.builders.get(new Integer(value).intValue());
	}
}