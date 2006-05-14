/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-12
 * Time: 11:32:13
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

import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.vcs.Vcs;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.spec.IComponentSpecification;

/**
 * This component renders vcs module editing page
 *
 * @author robin shine
 */
public abstract class VcsModuleEditor extends BaseComponent implements PageDetachListener{
	private Vcs.Module moduleToEdit;

	public VcsListTab getVcsListTab(){
		return (VcsListTab) getContainer();
	}

	public ProjectPage getProjectPage(){
		return (ProjectPage)getPage();
	}

	public void finishLoad(IRequestCycle cycle, IPageLoader loader, IComponentSpecification specification) {
		super.finishLoad(cycle, loader, specification);
		moduleToEdit = null;
	}

	public void pageDetached(PageEvent event) {
		moduleToEdit = null;
	}

	public void save(IRequestCycle cycle){
		try {
			getVcsToEdit().validateModules();
			getProjectPage().getProject().getVcsList().set(getVcsIndexToEdit(), getVcsToEdit());
			getProjectPage().saveProject();
			Luntbuild.getSchedService().rescheduleBuilds();
			getVcsListTab().setAction(null);
		} catch (ValidationException e) {
			setErrorMsg(e.getMessage());
			return;
		}
	}

	public void cancel(IRequestCycle cycle){
		getVcsListTab().setAction(null);
	}

	public abstract void setErrorMsg(String errorMsg);

	public abstract int getPropertyIndex();

	public String getPropertyNameCssClass() {
		if (getPropertyIndex() % 2 == 0)
			return "propertyEditorName1";
		else
			return "propertyEditorName2";
	}

	public String getPropertyValueCssClass() {
		if (getPropertyIndex() % 2 == 0)
			return "propertyEditorValue1";
		else
			return "propertyEditorValue2";
	}

	public String getPropertyEditorTailCssClass() {
		if (getModuleToEdit().getProperties().size() % 2 == 0)
			return "propertyEditorTail1";
		else
			return "propertyEditorTail2";
	}

	public abstract void setVcsToEdit(Vcs vcsToEdit);

	public abstract Vcs getVcsToEdit();

	public abstract void setVcsIndexToEdit(int vcsIndexToEdit);

	public abstract int getVcsIndexToEdit();

	public void setModuleToEdit(Vcs.Module moduleToEdit) {
		this.moduleToEdit = moduleToEdit;
		Tapestry.fireObservedChange(this, "moduleToEdit", moduleToEdit);
	}

	public Vcs.Module getModuleToEdit() {
		return moduleToEdit;
	}

	public void assignVcsIndexToEdit(int vcsIndexToEdit) {
		setVcsToEdit((Vcs) getProjectPage().getProject().getVcsList().get(vcsIndexToEdit));
		setVcsIndexToEdit(vcsIndexToEdit);
	}

	public void assignModuleIndexToEdit(int moduleIndexToEdit) {
		if (moduleIndexToEdit == -1) {
			setModuleToEdit(getVcsToEdit().createNewModule());
			getVcsToEdit().getModules().add(getModuleToEdit());
		} else {
			setModuleToEdit((Vcs.Module) getVcsToEdit().getModules().get(moduleIndexToEdit));
		}
	}
}
