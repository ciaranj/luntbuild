/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-9-3
 * Time: 14:20
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

import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.Constants;
import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * Tapestry selection model for dependent triggering strategy
 * @author robin shine
 */
public class TriggerDependencyStrategySelectionModel implements IPropertySelectionModel{
	public int getOptionCount() {
		return com.luntsys.luntbuild.facades.Constants.NUM_TRIGGER_DEPENDENCY_STRATEGY;
	}

	public Object getOption(int index) {
		return new Integer(index);
	}

	public String getLabel(int index) {
		return com.luntsys.luntbuild.facades.Constants.getTriggerDependencyStrategyText(index);
	}

	public String getValue(int index) {
		return getOption(index).toString();
	}

	public Object translateValue(String value) {
		return new Integer(value);
	}
}
