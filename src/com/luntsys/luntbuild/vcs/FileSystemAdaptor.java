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
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.OgnlHelper;
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
 * File system VCS adaptor implementation.
 *
 * @author robin shine
 */
public class FileSystemAdaptor extends Vcs {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;

	private String sourceDir;

    /**
     * @inheritDoc
     */
	public String getDisplayName() {
		return "File system";
	}

    /**
     * @inheritDoc
     */
	public String getIconName() {
		return "filesystem.gif";
	}

	/**
	 * Gets the source directory.
	 * 
	 * @return the source directory
	 */
	public String getSourceDir() {
		return sourceDir;
	}

	/**
	 * Gets the source directory. This method will parse OGNL variables.
	 * 
	 * @return the source directory
	 */
	public String getActualSourceDir() {
		return OgnlHelper.evaluateScheduleValue(getSourceDir());
	}

	/**
	 * Sets the source directory.
	 * 
	 * @param sourceDir the source directory
	 */
	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

    /**
     * Validates the properties of this VCS.
     *
     * @throws ValidationException if a property has an invalid value
     */
	public void validateProperties() {
		super.validateProperties();
		if (!Luntbuild.isEmpty(getSourceDir()) && !new File(getSourceDir()).isAbsolute())
			throw new ValidationException("\"source directory\" should be an absolute path!");
	}

	/**
     * @inheritDoc
	 */
	public void checkoutActually(Build build, Project antProject) {
		if (!Luntbuild.isEmpty(getSourceDir())) {
			String workingDir = build.getSchedule().getWorkDirRaw();
			Copy copy = new Copy();
			copy.setTodir(new File(workingDir));
			FileSet fileSet = new FileSet();
			fileSet.setProject(antProject);
			fileSet.setDir(new File(getActualSourceDir()));
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

	/**
     * @inheritDoc
	 */
	public void label(Build build, Project antProject) {
		// not applicable for file system adaptor
	}

	/**
     * @inheritDoc
	 */
	public Module createNewModule() {
		return null;
	}

	/**
     * @inheritDoc
	 */
    public Module createNewModule(Module module) {
        return null;
    }

	/**
     * @inheritDoc
	 */
	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		Revisions revisions = new Revisions();
        revisions.addLog(this.getClass().getName(), toString());
        revisions.getChangeLogs().add("*************************************************************");
        revisions.getChangeLogs().add(toString());
        revisions.getChangeLogs().add("");
		if (!Luntbuild.isEmpty(getSourceDir()))
			getRevisions(new File(getActualSourceDir()), sinceDate, revisions);
		return revisions;
	}

	/**
	 * Detects changes of the specified file based on modified time.
	 * 
	 * @param file the file to detect changes
	 * @param sinceDate the date to determine modifications from
	 * @param revisions existing revisions to add detected modifications to
	 */
	private void getRevisions(File file, Date sinceDate, Revisions revisions) {
		Date lastModified = new Date(file.lastModified());
		if (lastModified.after(sinceDate)) {
			revisions.setFileModified(true);
			String message = "";
			String action = "";
			if (file.isDirectory()) {
				message = "Directory \"" + file.getAbsolutePath() + "\" modified at " + lastModified.toString();
				action = "directory";
			} else {
				message = "File \"" + file.getAbsolutePath() + "\" modified at " + lastModified.toString();
				action = "file";
			}
			revisions.addEntryToLastLog("", "", lastModified, message);
			revisions.addPathToLastEntry(file.getAbsolutePath(), action, "");
			revisions.getChangeLogs().add(message);
		}
		if (file.isDirectory()) {
			File childs[] = file.listFiles();
			for (int i = 0; i < childs.length; i++) {
				File child = childs[i];
				getRevisions(child, sinceDate, revisions);
			}
		}
	}

    /**
     * @inheritDoc
     */
	public List getVcsSpecificProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty(){
			public String getDisplayName() {
				return "Source directory";
			}

			public String getDescription() {
				return "This is an optional property. If specified, changes can be detected in " +
						"the source directory based on modification time, and modified files under this " +
						"directory will be copied to the project work directory to perform build.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getSourceDir();
			}

			public String getActualValue() {
				return getActualSourceDir();
			}

			public void setValue(String value) {
				setSourceDir(value);
			}
		});
		return properties;
	}

	/**
     * Creates a link to the specified file.
     * 
	 * @param path the path to the file
	 * @return the link
	 */
	public String createLinkForFile(String path) {
    	if (Luntbuild.isEmpty(path))
    		return "";
    	if (path.startsWith("\\\\"))
    		return "<a href=\"file:" + path + "\">" + path + "</a>";
    	else if (path.startsWith("file:\\\\"))
    		return "<a href=\"" + path + "\">" + path + "</a>";
    	else
    		return path;
	}

    /**
     * @inheritDoc
     * @see FileSystemAdaptorFacade
     */
	public void saveToFacade(VcsFacade facade) {
    	// TODO throw RuntimeException if the facade is not the right class
		FileSystemAdaptorFacade fileSystemFacade = (FileSystemAdaptorFacade) facade;
		fileSystemFacade.setSourceDir(getSourceDir());
	}

    /**
     * @inheritDoc
     * @throws RuntimeException if the facade is not an <code>FileSystemAdaptorFacade</code>
     * @see FileSystemAdaptorFacade
     */
	public void loadFromFacade(VcsFacade facade) {
		if (!(facade instanceof FileSystemAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		FileSystemAdaptorFacade fileSystemFacade = (FileSystemAdaptorFacade) facade;
		setSourceDir(fileSystemFacade.getSourceDir());
	}

    /**
     * @inheritDoc
     * @see FileSystemAdaptorFacade
     */
	public VcsFacade constructFacade() {
		return new FileSystemAdaptorFacade();
	}
}
