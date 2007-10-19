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

import com.luntsys.luntbuild.BuildGenerator;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.web.components.tabcontrol.TabControl;
import com.luntsys.luntbuild.web.selectionmodels.*;
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
 * This component renders a manual build editing page
 *
 * @author robin shine
 */
public abstract class ManualBuildEditor extends BaseComponent implements PageDetachListener {

    /**
	 * Which page does the user trigger manual build from
	 */
	public static final int BUILD_FROM_BUILDSTAB = 1;
	public static final int BUILD_FROM_SCHEDULESTAB = 2;

	private String buildNecessaryCondition;
	private int buildType;
	private int labelStrategy;
	private int notifyStrategy;
	private int postbuildStrategy;
	private int triggerDependencyStrategy;

	public IPropertySelectionModel getBuildTypeSelectionModel(){
		return new BuildTypeSelectionModel();
	}

	public IPropertySelectionModel getLabelStrategySelectionModel() {
		return new LabelStrategySelectionModel();
	}

	public IPropertySelectionModel getNotifyStrategySelectionModel() {
		return new NotifyStrategySelectionModel();
	}

	public IPropertySelectionModel getPostbuildStrategySelectionModel() {
		return new PostbuildStrategySelectionModel();
	}

	public IPropertySelectionModel getTriggerDependencyStrategySelectionModel() {
		return new TriggerDependencyStrategySelectionModel();
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
		getSchedulesTab().ensureCurrentTab();
		SimpleTrigger trigger = new SimpleTrigger();
		trigger.setGroup(BuildGenerator.MANUALBUILD_GROUP);
		String buildNecessaryCondition = getBuildNecessaryCondition();
		Schedule.validateBuildNecessaryCondition(buildNecessaryCondition);
		buildNecessaryCondition = buildNecessaryCondition.trim();
		String buildAsVersion = getBuildAsVersion();
		if (buildAsVersion == null)
			buildAsVersion = "";
		else
			buildAsVersion = buildAsVersion.trim();
		if (!buildAsVersion.equals("")) {
			try {
				getSchedule().validateBuildVersion(buildAsVersion);
			} catch (ValidationException e) {
				setErrorMsg(e.getMessage());
				return;
			}
		}
		com.luntsys.luntbuild.facades.BuildParams buildParams = new com.luntsys.luntbuild.facades.BuildParams();
		buildParams.setBuildNecessaryCondition(buildNecessaryCondition);
		buildParams.setBuildType(getBuildType());
		buildParams.setBuildVersion(buildAsVersion);
		buildParams.setLabelStrategy(getLabelStrategy());
		buildParams.setNotifyStrategy(getNotifyStrategy());
		buildParams.setPostbuildStrategy(getPostbuildStrategy());
		buildParams.setTriggerDependencyStrategy(getTriggerDependencyStrategy());
		buildParams.setScheduleId(getSchedule().getId());
		trigger.setName(Schedule.constructTriggerName(buildParams));
		trigger.setRepeatCount(0);
		trigger.setRepeatInterval(0);
		if (getBuildTiming() == com.luntsys.luntbuild.facades.Constants.BUILD_TIMING_NOW){
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
			try{
					Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(getBuildTime());
				trigger.setStartTime(startTime);
			} catch (ParseException e){
				setErrorMsg("Value of the property \"at\" is invalid: " + e.getMessage());
				return;
			}
		} else {
			try{
				trigger.setStartTime(Luntbuild.getDateByHHMM(getBuildTime()));
			} catch (RuntimeException e){
				setErrorMsg("Value of the field \"later at\" is invalid: " + e.getMessage());
				return;
			}
		}
		Luntbuild.getSchedService().scheduleBuild(getSchedule(), trigger);
		if (getWhereFrom() == BUILD_FROM_SCHEDULESTAB) {
        	getSchedulesTab().setAction(null);
            PageService s = new PageService();
            String redirect = s.getLink(cycle, this, new Object[]{"ProjectPage"}).getURL();
            throw new RedirectException(redirect);
		} else {
            Home homePage = (Home) cycle.getPage("Home");
            cycle.activate(homePage);
            TabControl tabs = (TabControl) homePage.getComponent("tabs");
            tabs.setSelectedTabId("builds");
            PageService s = new PageService();
            String redirect = s.getLink(cycle, this, new Object[]{"Home"}).getURL();
            throw new RedirectException(redirect);
		}
	}

	public void cancel(IRequestCycle cycle){
		getSchedulesTab().ensureCurrentTab();
		if (getWhereFrom() == BUILD_FROM_SCHEDULESTAB) {
        	getSchedulesTab().setAction(null);
            PageService s = new PageService();
            String redirect = s.getLink(cycle, this, new Object[]{"ProjectPage"}).getURL();
            throw new RedirectException(redirect);
        }else {
            Home homePage = (Home) cycle.getPage("Home");
            cycle.activate(homePage);
            TabControl tabs = (TabControl) homePage.getComponent("tabs");
            tabs.setSelectedTabId("builds");
            PageService s = new PageService();
            String redirect = s.getLink(cycle, this, new Object[]{"Home"}).getURL();
            throw new RedirectException(redirect);
		}
	}

	public ProjectPage getProjectPage(){
		return (ProjectPage) getPage();
	}

	public SchedulesTab getSchedulesTab(){
		return (SchedulesTab)getContainer();
	}

	public abstract void setBuildTime(String buildTime);

	public abstract String getBuildTime();

	public abstract int getBuildTiming();

	public abstract void setBuildTiming(int buildTiming);

	public abstract void setErrorMsg(String errorMsg);

	public abstract String getBuildAsVersion();

	public abstract void setSchedule(Schedule schedule);

	public abstract Schedule getSchedule();

	public int getBuildType() {
		if (buildType == -1)
			return getSchedule().getBuildType();
		else
			return buildType;
	}

	public void setBuildType(int buildType) {
		this.buildType = buildType;
	}

	public int getLabelStrategy() {
		if (labelStrategy == -1)
			return getSchedule().getLabelStrategy();
		else
			return labelStrategy;
	}

	public void setLabelStrategy(int labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

	public int getNotifyStrategy() {
		if (notifyStrategy == -1)
			return getSchedule().getNotifyStrategy();
		else
			return notifyStrategy;
	}

	public void setNotifyStrategy(int notifyStrategy) {
		this.notifyStrategy = notifyStrategy;
	}

	public int getPostbuildStrategy() {
		if (postbuildStrategy == -1)
			return getSchedule().getPostbuildStrategy();
		else
			return postbuildStrategy;
	}

	public void setPostbuildStrategy(int postbuildStrategy) {
		this.postbuildStrategy = postbuildStrategy;
	}

	public int getTriggerDependencyStrategy() {
		if (triggerDependencyStrategy == -1)
			return getSchedule().getTriggerDependencyStrategy();
		else
			return triggerDependencyStrategy;
	}

	public void setTriggerDependencyStrategy(int triggerDependencyStrategy) {
		this.triggerDependencyStrategy = triggerDependencyStrategy;
	}

	public String getBuildNecessaryCondition() {
		if (buildNecessaryCondition == null)
			return getSchedule().getBuildNecessaryCondition();
		else
			return buildNecessaryCondition;
	}

	public void setBuildNecessaryCondition(String buildNecessaryCondition) {
		this.buildNecessaryCondition = buildNecessaryCondition;
	}

	public void finishLoad(IRequestCycle iRequestCycle, IPageLoader iPageLoader, IComponentSpecification iComponentSpecification) {
		super.finishLoad(iRequestCycle, iPageLoader, iComponentSpecification);
		buildType = -1;
		labelStrategy = -1;
		notifyStrategy = -1;
		postbuildStrategy = -1;
		buildNecessaryCondition = null;
		triggerDependencyStrategy = -1;
	}

	public void pageDetached(PageEvent event) {
		buildType = -1;
		labelStrategy = -1;
		notifyStrategy = -1;
		postbuildStrategy = -1;
		buildNecessaryCondition = null;
		triggerDependencyStrategy = -1;
	}

	public abstract void setWhereFrom(int whereFrom);

	protected abstract int getWhereFrom();

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
