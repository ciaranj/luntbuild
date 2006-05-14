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
import org.apache.tapestry.IRequestCycle;

/**
 * This component renders project's builders list page
 *
 * @author robin shine
 */
public abstract class BuilderListTab extends TabPageComponent {
	public String getTabName() {
		return "Builders";
	}

	public void tabSelected() {
		setAction(null);
	}

	public ProjectPage getProjectPage() {
		return (ProjectPage) getPage();
	}

	/**
	 * create a new builders object
	 *
	 * @param cycle
	 */
	public void createBuilder(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		setAction("editBuilder");
		BuilderEditor builderEditor = (BuilderEditor) getComponent("builderEditorComponent");
		builderEditor.assignBuilderIndexToEdit(-1);
	}

	public void deleteBuilder(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		int builderIndex = ((Integer) cycle.getServiceParameters()[0]).intValue();
		setBuilderIndexToDelete(builderIndex);
		setAction("deleteBuilder");
	}

	public void editBuilder(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		int builderIndex = ((Integer) cycle.getServiceParameters()[0]).intValue();
		BuilderEditor builderEditor = (BuilderEditor) getComponent("builderEditorComponent");
		builderEditor.assignBuilderIndexToEdit(builderIndex);
		setAction("editBuilder");
	}

    public void copyBuilder(IRequestCycle cycle) {
        ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
        int builderIndex = ((Integer) cycle.getServiceParameters()[0]).intValue();
        BuilderEditor builderEditor = (BuilderEditor) getComponent("builderEditorComponent");
        builderEditor.assignBuilderIndexToCopy(builderIndex);
        setAction("editBuilder");
    }

	/**
	 * Actually delete the builders
	 *
	 * @param cycle
	 */
	public void confirmDeleteBuilder(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		if (getBuilderIndexToDelete() == -1) { // avoid re-submit
			return;
		}
		getProjectPage().getProject().getBuilderList().remove(getBuilderIndexToDelete());
		getProjectPage().saveProject();
		setBuilderIndexToDelete(-1);
		Luntbuild.getSchedService().rescheduleBuilds();
		setAction(null);
	}

	/**
	 * Cancel the deletion of the builders
	 *
	 * @param cycle
	 */
	public void cancelDeleteBuilder(IRequestCycle cycle) {
		ensureCurrentTab(); // in case user go back to this page through browser's back button and click on this
		setBuilderIndexToDelete(-1);
		setAction(null);
	}

	public abstract void setAction(String action);

	public abstract String getAction();

	public abstract void setBuilderIndexToDelete(int builderIndexToDelete);

	public abstract int getBuilderIndexToDelete();

	public int getRefreshInterval() {
		return 0;
	}

	public abstract int getBuilderIndex();

	public String getBuilderCssClass() {
		if (getBuilderIndex() == 0)
			return "firstRowContainingTable";
		else
			return "rowContainingTable";
	}

	public String getPropertyNameCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyListName2";
		else
			return "propertyListName1";
	}

	public String getPropertyValueCssClass() {
		if (getCssIndex() % 2 == 0)
			return "propertyListValue2";
		else
			return "propertyListValue1";
	}

	public abstract int getCssIndex();
}
