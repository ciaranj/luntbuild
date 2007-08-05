/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-12
 * Time: 6:50:17
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
import com.luntsys.luntbuild.vcs.Vcs;
import org.apache.tapestry.IRequestCycle;

/**
 * This component renders the tab page which displays version control systems configured
 * for a project
 *
 * @author robin shine
 */
public abstract class VcsListTab extends TabPageComponent {
	private Vcs.Module module;

	public String getTabName() {
		return "VCS adaptors";
	}

	public void tabSelected() {
		setAction(null);
	}

	public ProjectPage getProjectPage() {
		return (ProjectPage) getPage();
	}

	/**
	 * create a new vcs object
	 *
	 * @param cycle
	 */
	public void createVcs(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		setAction("editVcs");
		VcsEditor vcsEditor = (VcsEditor) getComponent("vcsEditorComponent");
		vcsEditor.assignVcsIndexToEdit(-1);
	}

	public void deleteVcs(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		int vcsIndex = ((Integer) cycle.getServiceParameters()[0]).intValue();
		setVcsIndexToDelete(vcsIndex);
		setAction("deleteVcs");
	}

	public void editVcs(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		int vcsIndex = ((Integer) cycle.getServiceParameters()[0]).intValue();
		VcsEditor vcsEditor = (VcsEditor) getComponent("vcsEditorComponent");
		vcsEditor.assignVcsIndexToEdit(vcsIndex);
		setAction("editVcs");
	}

    public void copyVcs(IRequestCycle cycle) {
        ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
        int vcsIndex = ((Integer) cycle.getServiceParameters()[0]).intValue();
        VcsEditor vcsEditor = (VcsEditor) getComponent("vcsEditorComponent");
        vcsEditor.assignVcsIndexToCopy(vcsIndex);
        setAction("editVcs");
    }

	/**
	 * Actually delete the vcs
	 *
	 * @param cycle
	 */
	public void confirmDeleteVcs(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		if (getVcsIndexToDelete() == -1) { // avoid re-submit
			return;
		}
		getProjectPage().getProject().getVcsList().remove(getVcsIndexToDelete());
		getProjectPage().saveProject();		
		setVcsIndexToDelete(-1);
		Luntbuild.getSchedService().rescheduleBuilds();
		setAction(null);
	}

	/**
	 * Cancel the deletion of the vcs
	 *
	 * @param cycle
	 */
	public void cancelDeleteVcs(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		setVcsIndexToDelete(-1);
		setAction(null);
	}

	public abstract void setAction(String action);

	public abstract String getAction();

	public abstract void setVcsIndexToDelete(int vcsIndexToDelete);

	public abstract int getVcsIndexToDelete();

	public abstract void setModuleIndexToDelete(int moduleIndexToDelete);

	public abstract int getModuleIndexToDelete();

	public int getRefreshInterval() {
		return 0;
	}

	public abstract int getVcsIndex();

	public String getVcsCssClass() {
		if (getVcsIndex() == 0)
			return "firstRowContainingTable";
		else
			return "rowContainingTable";
	}

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyListName propertyListName2";
		else
			return "propertyListName propertyListName1";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyListValue propertyListValue2";
		else
			return "propertyListValue propertyListValue1";
	}

	public abstract int getCssIndex();

	public String getModulesTitleCssClass() {
		if (getCssIndex() % 2 == 0)
			return "modulesTitle modulesTitle2";
		else
			return "modulesTitle modulesTitle1";
	}

	public String getModulePropertyTitleCssClass() {
		if (getCssIndex() % 2 == 0)
			return "modulePropertyTitle modulePropertyTitle2";
		else
			return "modulePropertyTitle modulePropertyTitle1";
	}

	public String getModuleCssClass() {
		if (getCssIndex() % 2 == 0)
			return "dataTableRow dataTableRow2";
		else
			return "dataTableRow dataTableRow1";
	}

	public abstract Vcs getVcs();

	/**
	 * Get the properties for a newly instanciated module for current vcs
	 * @return
	 */
	public Vcs.Module getNewModule() {
		Vcs.Module newModule = getVcs().createNewModule();
		return newModule;
	}

	public Vcs.Module getModule() {
		return module;
	}

	public void setModule(Vcs.Module module) {
		this.module = module;
	}

	public void editModule(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		int vcsIndex = ((Integer) cycle.getServiceParameters()[0]).intValue();
		int moduleIndex = ((Integer) cycle.getServiceParameters()[1]).intValue();
		setAction("editModule");
		VcsModuleEditor moduleEditor = (VcsModuleEditor) getComponent("moduleEditorComponent");
		moduleEditor.assignVcsIndexToEdit(vcsIndex);
		moduleEditor.assignModuleIndexToEdit(moduleIndex);
	}

	public void deleteModule(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		int vcsIndex = ((Integer) cycle.getServiceParameters()[0]).intValue();
		int moduleIndex = ((Integer) cycle.getServiceParameters()[1]).intValue();
		setVcsIndexToDelete(vcsIndex);
		setModuleIndexToDelete(moduleIndex);
		setAction("deleteModule");
	}

	public void createModule(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		int vcsIndex = ((Integer) cycle.getServiceParameters()[0]).intValue();
		setAction("editModule");
		VcsModuleEditor moduleEditor = (VcsModuleEditor) getComponent("moduleEditorComponent");
		moduleEditor.assignVcsIndexToEdit(vcsIndex);
		moduleEditor.assignModuleIndexToEdit(-1);
	}

	/**
	 * Actually delete the module
	 *
	 * @param cycle
	 */
	public void confirmDeleteModule(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		if (getVcsIndexToDelete() == -1 || getModuleIndexToDelete() == -1) { // avoid re-submit
			return;
		}
		Vcs vcs = (Vcs) getProjectPage().getProject().getVcsList().get(getVcsIndexToDelete());
		vcs.getModules().remove(getModuleIndexToDelete());
		getProjectPage().saveProject();
		setVcsIndexToDelete(-1);
		setModuleIndexToDelete(-1);
		Luntbuild.getSchedService().rescheduleBuilds();
		setAction(null);
	}

	/**
	 * Cancel the deletion of the module
	 *
	 * @param cycle
	 */
	public void cancelDeleteModule(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		setVcsIndexToDelete(-1);
		setModuleIndexToDelete(-1);
		setAction(null);
	}
}
