/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-6
 * Time: 15:58:02
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

import com.luntsys.luntbuild.builders.AntBuilder;
import com.luntsys.luntbuild.builders.Builder;
import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.web.selectionmodels.BuilderClassSelectionModel;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;

/**
 * This component renders builders editing page.
 *
 * @author robin shine
 */
public abstract class BuilderEditor extends BaseComponent {
	private Class builderClass;

	/**
	 * Called when user select a different builder type.
	 *
	 * @param cycle
	 */
	public void changeBuilderAdaptor(IRequestCycle cycle) {
		if (isSaveOrCancelTriggered())
			return;
		if (getBuilderClass() == builderClass)
			return;
		try {
			setBuilderToEdit((Builder) builderClass.newInstance());
		} catch (Exception e) {
			throw new ApplicationRuntimeException(e);
		}
	}

	/**
	 * save the edit results
	 *
	 * @param cycle
	 */
	public void save(IRequestCycle cycle) {
		setSaveOrCancelTriggered(true);
		try {
			getBuilderToEdit().validate();
			Project project = getProjectPage().getProject();
			if (getBuilderIndexToEdit() == -1)
				project.getBuilderList().add(getBuilderToEdit());
			else
				project.getBuilderList().set(getBuilderIndexToEdit(), getBuilderToEdit());
			getProjectPage().saveProject();
			Luntbuild.getSchedService().rescheduleBuilds();
			getBuilderListTab().setAction(null);
		} catch (ValidationException e) {
			setErrorMsg(e.getMessage());
		}
	}

	public void cancel(IRequestCycle cycle) {
		setSaveOrCancelTriggered(true);
		getBuilderListTab().setAction(null);
	}

	public abstract void setBuilderToEdit(Builder builderToEdit);

	public abstract Builder getBuilderToEdit();

	public ProjectPage getProjectPage() {
		return (ProjectPage) getPage();
	}

	public void setBuilderClass(Class builderClass) {
		this.builderClass = builderClass;
	}

	public Class getBuilderClass() {
		return getBuilderToEdit().getClass();
	}

	public BuilderListTab getBuilderListTab() {
		return (BuilderListTab) getContainer();
	}

	public abstract void setErrorMsg(String errorMsg);

	public abstract int getPropertyIndex();

	public abstract boolean isSaveOrCancelTriggered();

	public abstract void setSaveOrCancelTriggered(boolean saveOrCancelTriggered);

	public String getPropertyNameCssClass() {
		if (getPropertyIndex() % 2 == 0)
			return "propertyEditorName2";
		else
			return "propertyEditorName1";
	}

	public String getPropertyValueCssClass() {
		if (getPropertyIndex() % 2 == 0)
			return "propertyEditorValue2";
		else
			return "propertyEditorValue1";
	}

	public String getPropertyEditorTailCssClass() {
		if (getBuilderToEdit().getProperties().size() % 2 == 0)
			return "propertyEditorTail2";
		else
			return "propertyEditorTail1";
	}

	public IPropertySelectionModel getBuilderClassSelectionModel() {
		return new BuilderClassSelectionModel();
	}

	public void assignBuilderIndexToEdit(int builderIndexToEdit) {
		if (builderIndexToEdit == -1)
			setBuilderToEdit(new AntBuilder());
		else
			setBuilderToEdit((Builder) getProjectPage().getProject().getBuilderList().get(builderIndexToEdit));
		setBuilderIndexToEdit(builderIndexToEdit);
	}

	public abstract void setBuilderIndexToEdit(int builderIndexToEdit);

	public abstract int getBuilderIndexToEdit();
}
