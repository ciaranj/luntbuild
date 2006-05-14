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
import com.luntsys.luntbuild.remoting.facade.FileSystemAdaptorFacade;
import com.luntsys.luntbuild.remoting.facade.VcsFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
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
import java.util.Map;

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
		return "file system";
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

	public void checkout(Map properties, Build build, Project antProject) {
		if (!Luntbuild.isEmpty(getSourceDir())) {
			String workingDir = build.getSchedule().getProject().getWorkingDir(properties);
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

	public void label(Map properties, Build build, Project antProject) {
		// not applicable for file system adaptor
	}

	public Vcs.Module createNewModule() {
		return null;
	}

	public Revisions getRevisionsSince(Map properties, Build build, Project antProject) {
		Revisions revisions = new Revisions();
		if (!Luntbuild.isEmpty(getSourceDir()))
			getRevisions(new File(getSourceDir()), build.getStartDate(), revisions);
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

	public List getProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty(){
			public String getDisplayName() {
				return "source directory";
			}

			public String getDescription() {
				return "This is an optional property. If specified, changes can be detected from " +
						"this source directory based on modification time, and modified files under this " +
						"directory will be copied to the project working directory to perform build.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getSourceDir();
			}

			public void setValue(String value) {
				setSourceDir(value);
			}
		});
		return properties;
	}

	public VcsFacade getFacade() {
		FileSystemAdaptorFacade facade = new FileSystemAdaptorFacade();
		facade.setSourceDir(getSourceDir());
		return facade;
	}

	public void setFacade(VcsFacade facade) {
		if (!(facade instanceof FileSystemAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		FileSystemAdaptorFacade fileSystemFacade = (FileSystemAdaptorFacade) facade;
		setSourceDir(fileSystemFacade.getSourceDir());
	}
}
