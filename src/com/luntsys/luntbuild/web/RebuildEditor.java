/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-29
 * Time: 21:49:16
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

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.SynchronizedDateFormatter;
import com.luntsys.luntbuild.web.selectionmodels.BuildTimingSelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.NotifyStrategySelectionModel;
import com.luntsys.luntbuild.web.selectionmodels.PostbuildStrategySelectionModel;
import com.luntsys.luntbuild.BuildGenerator;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.engine.PageService;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.spec.IComponentSpecification;
import org.quartz.SimpleTrigger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This component renders rebuild editing page.
 *
 * @author robin shine
 */
public abstract class RebuildEditor extends BaseComponent implements PageDetachListener {

    private int notifyStrategy;

	private int postbuildStrategy;

	public IPropertySelectionModel getNotifyStrategySelectionModel() {
		return new NotifyStrategySelectionModel();
	}

	public IPropertySelectionModel getPostbuildStrategySelectionModel() {
		return new PostbuildStrategySelectionModel();
	}

	public IPropertySelectionModel getBuildTimingSelectionModel(){
		return new BuildTimingSelectionModel();
	}

	/**
	 * Saves this manual build
	 *
	 * @param cycle
	 */
	public void save(IRequestCycle cycle){
		BuildViewer buildViewer = (BuildViewer)getContainer();
		buildViewer.getBuildsTab().ensureCurrentTab();

		SimpleTrigger trigger = new SimpleTrigger();
		trigger.setGroup(BuildGenerator.REBUILD_GROUP);
		// encode current build id, and current time stamp into the trigger name
		trigger.setName(String.valueOf(getBuild().getId()) + Luntbuild.TRIGGER_NAME_SEPERATOR +
			 getNotifyStrategy() + Luntbuild.TRIGGER_NAME_SEPERATOR +
			 getPostbuildStrategy() + Luntbuild.TRIGGER_NAME_SEPERATOR +
				String.valueOf(System.currentTimeMillis()));
		trigger.setRepeatCount(0);
		trigger.setRepeatInterval(0);
		if (getBuildTiming() == Constants.BUILD_TIMING_NOW){
			trigger.setStartTime(new Date(System.currentTimeMillis()));
		} else if (getBuildTiming() == com.luntsys.luntbuild.facades.Constants.BUILD_TIMING_AFTER){
			try{
				long afterMinutes = new Long(getBuildTime()).longValue();
				if (afterMinutes <= 0){
					setErrorMsg("Value of the property \"after\" should be greater than 0!");
					return;
				}
				trigger.setStartTime(new Date(System.currentTimeMillis() + afterMinutes*60000));
			} catch (NumberFormatException e){
				setErrorMsg("Value of the property \"after\" should be of number format!");
				return;
			}
		} else if (getBuildTiming() == com.luntsys.luntbuild.facades.Constants.BUILD_TIMING_AT){
			Date startTime = SynchronizedDateFormatter.parseDate(getBuildTime(), "yyyy-MM-dd HH:mm");
			if (startTime == null) {
				setErrorMsg("Value of the property \"at\" is invalid: " + getBuildTime());
				return;
			}
			trigger.setStartTime(startTime);
		} else {
			try{
				trigger.setStartTime(Luntbuild.getDateByHHMM(getBuildTime()));
			} catch (RuntimeException e){
				setErrorMsg("Value of the property \"later at\" is invalid: " + e.getMessage());
				return;
			}
		}
		Luntbuild.getSchedService().scheduleBuild(buildViewer.getBuild().getSchedule(), trigger);
		buildViewer.setAction(null);
        PageService s = new PageService();
        String redirect = s.getLink(cycle, this, new Object[]{"Home"}).getURL();
        throw new RedirectException(redirect);
	}

	/**
	 * Cancels this rebuild
	 * @param cycle
	 */
	public void cancel(IRequestCycle cycle){
		BuildViewer buildViewer = (BuildViewer)getContainer();
		buildViewer.getBuildsTab().ensureCurrentTab();
		buildViewer.setAction(null);
        PageService s = new PageService();
        String redirect = s.getLink(cycle, this, new Object[]{"Home"}).getURL();
        throw new RedirectException(redirect);
	}

	public int getNotifyStrategy() {
		if (notifyStrategy == -1)
			return getBuild().getSchedule().getNotifyStrategy();
		else
			return notifyStrategy;
	}

	public void setNotifyStrategy(int notifyStrategy) {
		this.notifyStrategy = notifyStrategy;
	}

	public int getPostbuildStrategy() {
		if (postbuildStrategy == -1)
			return getBuild().getPostbuildStrategy();
		else
			return postbuildStrategy;
	}

	public void setPostbuildStrategy(int postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

	public abstract void setBuildTime(String scheduleTime);

	public abstract String getBuildTime();

	public abstract int getBuildTiming();

	public abstract void setBuildTiming(int buildTiming);

	public abstract void setErrorMsg(String errorMsg);

	public void finishLoad(IRequestCycle iRequestCycle, IPageLoader iPageLoader, IComponentSpecification iComponentSpecification) {
		super.finishLoad(iRequestCycle, iPageLoader, iComponentSpecification);
		notifyStrategy = -1;
		postbuildStrategy = -1;
	}

	public void pageDetached(PageEvent event) {
		notifyStrategy = -1;
		postbuildStrategy = -1;
	}

	protected abstract Build getBuild();

	public abstract void setBuild(Build build);

	public abstract int getCssIndex();

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorName propertyEditorName1";
		else
			return "propertyEditorName propertyEditorName2";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorValue propertyEditorValue1";
		else
			return "propertyEditorValue propertyEditorValue2";
	}

	public String getTailCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyEditorTail1";
		else
			return "propertyEditorTail2";
	}
}
