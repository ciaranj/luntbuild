/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-19
 * Time: 10:17:46
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
package com.luntsys.luntbuild.web;

import com.luntsys.luntbuild.notifiers.Notifier;
import com.luntsys.luntbuild.security.SecurityHelper;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.facades.Constants;
import org.apache.tapestry.IRequestCycle;
import org.quartz.CronTrigger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.text.ParseException;

/**
 * This component shows and edits system wide properties
 *
 * @author robin shine
 */
public abstract class PropertiesTab extends TabPageComponent {
	public String getTabName() {
		return "Properties";
	}

	public void tabSelected() {
		setProperties(Luntbuild.getDao().loadProperties());
	}

	public void update(IRequestCycle cycle) {
		ensureCurrentTab();
		String publishDir = (String) getProperties().get(Constants.PUBLISH_DIR);
		if (!Luntbuild.isEmpty(publishDir) && !new File(publishDir).isAbsolute()) {
			setErrorMsg("Publish directory should be an absolute path!");
			return;
		}
		String workingDir = (String) getProperties().get(Constants.WORKING_DIR);
		if (!Luntbuild.isEmpty(workingDir) && !new File(workingDir).isAbsolute()) {
			setErrorMsg("Work directory should be an absolute path!");
			return;
		}
		String buildThreadCountText = (String) getProperties().get(Constants.BUILD_THREAD_COUNT);
		if (!Luntbuild.isEmpty(buildThreadCountText)) {
			try {
				int buildThreadCount = new Integer(buildThreadCountText).intValue();
				if (buildThreadCount <= 0) {
					setErrorMsg("Number of build threads should be a positive integer value!");
					return;
				}
			} catch (NumberFormatException e) {
				setErrorMsg("Number of build threads should be a positive integer value!");
				return;
			}
		}
		String backupCronExpression = (String) getProperties().get(Constants.BACKUP_CRON_EXPRESSION);
		if (!Luntbuild.isEmpty(backupCronExpression)) {
			CronTrigger cronTrigger = new CronTrigger();
			try {
				cronTrigger.setCronExpression(backupCronExpression);
			} catch (ParseException e) {
				setErrorMsg("Invalid database backup cron expression: " + backupCronExpression);
				return;
			}
		}

		Iterator it = getProperties().keySet().iterator();
		while (it.hasNext()) {
			String property = (String) it.next();
			String value = (String) getProperties().get(property);
			if (value != null)
				getProperties().put(property, value.trim());
		}
		Luntbuild.getDao().saveProperties(getProperties());
		Luntbuild.setProperties(getProperties());
		Luntbuild.getSchedService().rescheduleBuilds();
		Luntbuild.getSchedService().scheduleSystemBackup();
		setSuccessMsg("Properties updated successfully!");
	}

	public abstract void setProperties(Map properties);

	public abstract Map getProperties();

	public abstract Notifier getNotifier();

	public String getNotifierTitle() {
		String title = "Set the " + getNotifier().getDisplayName() +
				" related properties here if you want to notify user by " +
				getNotifier().getDisplayName() + ".";
		if (Luntbuild.isEmpty(getNotifier().getComment()))
			return title;
		else
			return title + " " + getNotifier().getComment();
	}

	public abstract int getCssIndex();

	public String getNotifierTitleCssClass() {
		if (getCssIndex() % 2 == 0)
			return "notifierTitle notifierTitle2";
		else
			return "notifierTitle notifierTitle1";
	}

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorName propertyEditorName2";
		else
			return "propertyEditorName propertyEditorName1";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorValue propertyEditorValue2";
		else
			return "propertyEditorValue propertyEditorValue1";
	}

	public String getTailCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorTail propertyEditorTail2";
		else
			return "propertyEditorTail propertyEditorTail1";
	}

	public List getNotifierInstances() {
		return Luntbuild.getNotifierInstances(Luntbuild.notifiers);
	}

	public String getPrincipal() {
        return SecurityHelper.getPrincipalAsString();
	}

	public abstract void setSuccessMsg(String successMsg);

	public abstract void setErrorMsg(String errorMsg);
}