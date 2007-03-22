/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-12-11
 * Time: 11:21
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
package com.luntsys.luntbuild.vcs;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.FileSystemAdaptorFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.utility.IStringProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.ValidationException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * adaptor to build from file systems. It will be serialized by hibernate
 *
 * @author robin shine
 */
public class FileSystemAdaptor extends Vcs {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;

	private String sourceDir;

	public String getDisplayName() {
		return "File system";
	}

	public String getIconName() {
		return "filesystem.gif";
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public void validateProperties() {
		super.validateProperties();
		if (!Luntbuild.isEmpty(getSourceDir()) && !new File(getSourceDir()).isAbsolute())
			throw new ValidationException("\"source directory\" should be an absolute path!");
	}

	public void checkoutActually(Build build, Project antProject) {
		if (!Luntbuild.isEmpty(getSourceDir())) {
			String workingDir = build.getSchedule().getWorkDirRaw();
			Copy copy = new Copy();
			copy.setTodir(new File(workingDir));
			FileSet fileSet = new FileSet();
			fileSet.setProject(antProject);
			fileSet.setDir(new File(getSourceDir()));
			copy.addFileset(fileSet);
			copy.setFailOnError(true);
			copy.setPreserveLastModified(true);
			copy.setOverwrite(false);
			copy.setIncludeEmptyDirs(true);
			copy.setTaskName("copy");
			copy.setTaskType("copy");
			copy.setProject(antProject);
			copy.execute();
		}
	}

	public void label(Build build, Project antProject) {
		// not applicable for file system adaptor
	}

	public Vcs.Module createNewModule() {
		return null;
	}

    public Vcs.Module createNewModule(Vcs.Module module) {
        return null;
    }

	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		Revisions revisions = new Revisions();
		if (!Luntbuild.isEmpty(getSourceDir()))
			getRevisions(new File(getSourceDir()), sinceDate, revisions);
		return revisions;
	}

	/**
	 * Detect changes of specified file based on modified time
	 * @param file the file to detect changes
	 * @param sinceDate the date since which to determine modifications
	 * @param revisions add detected modifiecations as revisions
	 */
	private void getRevisions(File file, Date sinceDate, Revisions revisions) {
		Date lastModified = new Date(file.lastModified());
		if (lastModified.after(sinceDate)) {
			revisions.setFileModified(true);
			if (file.isDirectory())
				revisions.getChangeLogs().add("Directory \"" + file.getAbsolutePath() + "\" modified at " + lastModified.toString());
			else
				revisions.getChangeLogs().add("File \"" + file.getAbsolutePath() + "\" modified at " + lastModified.toString());
		}
		if (file.isDirectory()) {
			File childs[] = file.listFiles();
			for (int i = 0; i < childs.length; i++) {
				File child = childs[i];
				getRevisions(child, sinceDate, revisions);
			}
		}
	}

	public List getVcsSpecificProperties() {
		List properties = getFilesystemProperties();
		return properties;
	}

	public void saveToFacade(VcsFacade facade) {
		com.luntsys.luntbuild.facades.lb12.FileSystemAdaptorFacade fileSystemFacade = (com.luntsys.luntbuild.facades.lb12.FileSystemAdaptorFacade) facade;
		fileSystemFacade.setSourceDir(getSourceDir());
	}

	public void loadFromFacade(VcsFacade facade) {
		if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.FileSystemAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		com.luntsys.luntbuild.facades.lb12.FileSystemAdaptorFacade fileSystemFacade = (FileSystemAdaptorFacade) facade;
		setSourceDir(fileSystemFacade.getSourceDir());
	}

	public VcsFacade constructFacade() {
		return new FileSystemAdaptorFacade();
	}
}
