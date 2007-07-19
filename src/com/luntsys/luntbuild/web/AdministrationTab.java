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

import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.migration.MigrationException;
import com.luntsys.luntbuild.migration.MigrationManager;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.selectionmodels.ProjectSelectionModel;
import com.thoughtworks.xstream.XStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;

import java.io.*;

/**
 * This component shows and edits system wide properties
 *
 * @author robin shine
 */
public abstract class AdministrationTab extends TabPageComponent {
    private static Log logger = LogFactory.getLog(AdministrationTab.class);

	public String getTabName() {
		return "Administration";
	}

	public void tabSelected() {
	}

	public abstract String getFilePathToExport();

	public abstract String getFilePathToImport();

	public abstract String getProjectPathToExport();

	public abstract String getProjectPathToImport();

	public abstract Project getProjectToExport();

	public abstract void setFilePathToImport(String filePathToImport);

	public abstract void setFilePathToExport(String filePathToExport);

	public abstract void setProjectPathToImport(String projectPathToImport);

	public abstract void setProjectPathToExport(String projectPathToExport);

	public abstract void setProjectToExport(Project projectToExport);

	public abstract void setErrorMsg(String errorMsg);

	public abstract void setSuccessMsg(String successMsg);

	public IPropertySelectionModel getProjectSelectionModel() {
		return new ProjectSelectionModel(Luntbuild.getDao().loadProjects());
	}

	public void exportData(IRequestCycle cycle) {
		ensureCurrentTab();
        String fileName = getFilePathToExport().trim();
		if (Luntbuild.isEmpty(fileName)) {
			setErrorMsg("You should specify the file to export to!");
			return;
		}
		com.luntsys.luntbuild.facades.lb12.DataCollection dataCollection = Luntbuild.getDao().loadDataCollection12();
		OutputStream os = null;
		PrintWriter pw = null;
        if (!(new File(fileName).isAbsolute()))
            fileName = Luntbuild.installDir + File.separator + fileName;
		try {
			os = new FileOutputStream(fileName);
			pw = new PrintWriter(os);
			XStream xstream = new XStream();
			xstream.alias("DataCollection", com.luntsys.luntbuild.facades.lb12.DataCollection.class);
			pw.print(xstream.toXML(dataCollection));
			pw.close();
			pw = null;
			os.close();
			os = null;
			setSuccessMsg("Data has been exported successfully!");
			setFilePathToExport(null);
		} catch (IOException e) {
			setErrorMsg("ERROR: " + e.getMessage());
		} finally {
			if (pw != null)
				pw.close();
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					// ignores
				}
		}
	}

	public void importData(IRequestCycle cycle) {
		ensureCurrentTab();
        String fileName = getFilePathToImport().trim();
		if (Luntbuild.isEmpty(fileName)) {
			setErrorMsg("You should specify the file to import from!");
			return;
		}
        if (!(new File(fileName).isAbsolute()))
            fileName = Luntbuild.installDir + File.separator + fileName;
		if (!(new File(fileName).exists())) {
			setErrorMsg("Specified importing file does not exist!");
			return;
		}
		setFilePathToImport(fileName);
		setAction("import");
	}

	public void confirmImport(IRequestCycle cycle) {
		ensureCurrentTab();
		if (getFilePathToImport() == null)
			return;
		try {
			com.luntsys.luntbuild.facades.lb12.DataCollection dataCollection =
					MigrationManager.importAsDataCollection12(new File(getFilePathToImport()));
	        try {
	            Luntbuild.getDao().eraseExistingData();
	        } catch (Exception e) {
	            logger.error("Unable to delete existing data, import might fail ", e);
	        }
			Luntbuild.getDao().saveDataCollection12(dataCollection);
			Luntbuild.setProperties(Luntbuild.getDao().loadProperties());
			Luntbuild.getSchedService().rescheduleBuilds();
			setSuccessMsg("Data has been imported successfully!");
		} catch (MigrationException me) {
			setErrorMsg(me.getMessage());
		}
		setFilePathToImport(null);
	}

	public void cancelImport(IRequestCycle cycle) {
		ensureCurrentTab();
	}

	public void exportProjectData(IRequestCycle cycle) {
		ensureCurrentTab();
        String fileName = getProjectPathToExport().trim();
		Project project = getProjectToExport();
		if (Luntbuild.isEmpty(fileName)) {
			setErrorMsg("You should specify the file to export to!");
			return;
		}
		if (project == null) {
			setErrorMsg("You should specify a project to export!");
			return;
		}
		com.luntsys.luntbuild.facades.lb12.ProjectCollection dataCollection = Luntbuild.getDao().loadProjectCollection12(project.getId());
		OutputStream os = null;
		PrintWriter pw = null;
        if (!(new File(fileName).isAbsolute()))
            fileName = Luntbuild.installDir + File.separator + fileName;
		try {
			os = new FileOutputStream(fileName);
			pw = new PrintWriter(os);
			XStream xstream = new XStream();
			xstream.alias("ProjectCollection", com.luntsys.luntbuild.facades.lb12.ProjectCollection.class);
			pw.print(xstream.toXML(dataCollection));
			pw.close();
			pw = null;
			os.close();
			os = null;
			setSuccessMsg("Project has been exported successfully!");
			setProjectPathToExport(null);
		} catch (IOException e) {
			setErrorMsg("ERROR: " + e.getMessage());
		} finally {
			if (pw != null)
				pw.close();
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					// ignores
				}
		}
	}

	public void importProjectData(IRequestCycle cycle) {
		ensureCurrentTab();
        String fileName = getProjectPathToImport().trim();
		if (Luntbuild.isEmpty(fileName)) {
			setErrorMsg("You should specify the file to import from!");
			return;
		}
        if (!(new File(fileName).isAbsolute()))
            fileName = Luntbuild.installDir + File.separator + fileName;
		if (!(new File(fileName).exists())) {
			setErrorMsg("Specified importing file does not exist!");
			return;
		}
		try {
			com.luntsys.luntbuild.facades.lb12.ProjectCollection projectCollection =
					MigrationManager.importAsProjectCollection12(new File(fileName));
			Luntbuild.getDao().saveProjectCollection12(projectCollection);
			Luntbuild.getSchedService().rescheduleBuilds();
			setSuccessMsg("Project has been imported successfully!");
		} catch (MigrationException me) {
			setErrorMsg(me.getMessage());
		}
		setProjectPathToImport(null);
	}

	public abstract String getAction();

	public abstract void setAction(String action);
}
