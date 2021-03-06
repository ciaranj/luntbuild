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
import org.apache.tapestry.form.IPropertySelectionModel;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;

/**
 * Tapestry selection model for trigger type
 *
 * @author robin shine
 */
public class TriggerTypeSelectionModel implements IPropertySelectionModel{
	public int getOptionCount() {
		return Constants.NUM_TRIGGER_TYPE;
	}

	public Object getOption(int index) {
		if (index == Constants.TRIGGER_TYPE_CRON)
			return CronTrigger.class;
		else if (index == Constants.TRIGGER_TYPE_SIMPLE)
			return SimpleTrigger.class;
		else
			return null;
	}

	public String getLabel(int index) {
		return Constants.getTriggerTypeText(index);
	}

	public String getValue(int index) {
		return String.valueOf(index);
	}

	public Object translateValue(String value) {
		return getOption(new Integer(value).intValue());
	}
}
