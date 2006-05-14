/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-8
 * Time: 20:56
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

import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.form.IPropertySelectionModel;

import java.util.Iterator;
import java.util.List;

import com.luntsys.luntbuild.db.Project;

/**
 * The selection model for projects configured in the system,
 *
 * @author robin shine
 */
public class ProjectSelectionModel implements IPropertySelectionModel{
	private List projects;

	public ProjectSelectionModel(List projects){
		this.projects = projects;
	}

	public int getOptionCount() {
		return projects.size();
	}

	public Object getOption(int index) {
		return projects.get(index);
	}

	public String getLabel(int index) {
		return ((Project)getOption(index)).getName();
	}

	public String getValue(int index) {
		return String.valueOf(((Project)getOption(index)).getId());
	}

	public Object translateValue(String value) {
		long projectId = new Integer(value).longValue();
		Iterator itProject = projects.iterator();
	 	while (itProject.hasNext()) {
			 Project project = (Project) itProject.next();
			 if (project.getId() == projectId){
				 return project;
			 }
		 }
		throw new ApplicationRuntimeException("Selected project has been removed already, please select another project for try!");
	}
}
