/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-9
 * Time: 10:44:16
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

import org.apache.tools.ant.taskdefs.optional.vss.MSVSSGET;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSLABEL;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.util.Map;
import java.util.Iterator;

import com.luntsys.luntbuild.utility.LuntBuild;
import com.luntsys.luntbuild.db.Snapshot;

/**
 * Microsoft visual source safe adaptor
 *
 * @author alvin shen
 */
public class VssAdaptor implements Vcs {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;
	private String vssPath; // path to srcsafe.ini file
	private String vssUser; // user name
	private String vssPassword; // password
	private String ssDir; // path to ss.exe file, if blank, ss.exe is expected to be in the PATH environment variable

	public String getVssPath() {
		return vssPath;
	}

	public void setVssPath(String vssPath) {
		this.vssPath = vssPath;
	}

	public String getVssUser() {
		return vssUser;
	}

	public void setVssUser(String vssUser) {
		this.vssUser = vssUser;
	}

	public String getVssPassword() {
		return vssPassword;
	}

	public void setVssPassword(String vssPassword) {
		this.vssPassword = vssPassword;
	}

	public String getSsDir() {
		return ssDir;
	}

	public void setSsDir(String ssDir) {
		this.ssDir = ssDir;
	}

	public String getVcsDisplayName() {
		return "Visual sourcesafe";
	}

	public String[] getProperties() {
		return new String[]{"vssPath", "vssUser", "vssPassword", "ssDir"};
	}

	public String getPropertyDisplayName(String propertyName) {
		if (propertyName.equals("vssPath"))
			return "Sourcesafe path";
		else if (propertyName.equals("vssUser"))
			return "User";
		else if (propertyName.equals("vssPassword"))
			return "Password";
		else if (propertyName.equals("ssDir"))
			return "Directory for ss.exe";
		else
			return "";
	}

	public boolean isPropertySecret(String propertyName) {
		if (propertyName.equals("vssPassword"))
			return true;
		else
			return false;
	}

	public boolean isPropertyOptional(String propertyName) {
		if (propertyName.equals("ssDir"))
			return true;
		else
			return false;
	}

	public String getPropertyDescription(String propertyName) {
		if (propertyName.equals("vssPath"))
			return "The directory where your srcsafe.ini resides in. \nFor example: \\\\machine1\\directory1. \n" +
					"NOTE, you should first login to exactly \"machine1\",\n" +
					"Not the ip address of machine1, or you should specify ip address in the sourcesafe path";
		else if (propertyName.equals("vssUser"))
			return "User name to login the above sourcesafe database";
		else if (propertyName.equals("vssPassword"))
			return "";
		else if (propertyName.equals("ssDir"))
			return "The directory where your ss.exe file resides in. \n" +
					"Should be specified if it does not exist in the system path";
		else
			return "";
	}

	public void login(String logFile) {
    	// does nothing
	}

	public void logout(String logFile){
		// does nothing
	}

	/**
	 * Method may throw a BuildException to indicates a module acquisition exception
	 *
	 * @param workingDir
	 * @param module
	 * @param snapshot
	 * @param logFile
	 */
	public void retrieveModule(String workingDir, String module, Snapshot snapshot, String logFile) {
		if (!new File(workingDir + File.separator + module).exists())
			LuntBuild.createDir(workingDir + File.separator + module);
		// call ant cvs task to acquire module
		MSVSSGET vssGetTask = new MSVSSGET();
		vssGetTask.setProject(new org.apache.tools.ant.Project());
		vssGetTask.getProject().init();
		vssGetTask.getProject().setBasedir(workingDir + File.separator + module);
		vssGetTask.setServerpath(getVssPath());
		vssGetTask.setVsspath("$/" + module);
		if (getSsDir() != null && !getSsDir().trim().equals(""))
			vssGetTask.setSsdir(getSsDir());
		if (snapshot.getLabel()!= null && !snapshot.getLabel().trim().equals(""))
			vssGetTask.setLabel(snapshot.getLabel());
		vssGetTask.setLogin(getVssUser() + "," + getVssPassword());
		vssGetTask.setRecursive(true);
		vssGetTask.setWritable(true);
		vssGetTask.setQuiet(false);
		vssGetTask.execute();
	}

	/**
	 * Method may throw a BuildException to indicates errors while labeling
	 *
	 * @param workingDir
	 * @param module
	 * @param snapshot
	 * @param label
	 * @param logFile
	 */
	public void labelModule(String workingDir, String module, Snapshot snapshot, String label, String logFile){
		// sourcesafe can not apply multiple labels to versions other than current
		if (snapshot.getLabel() != null && !snapshot.getLabel().trim().equals(""))
			return;

    	MSVSSLABEL vssLabelTask = new MSVSSLABEL();
		vssLabelTask.setProject(new org.apache.tools.ant.Project());
		vssLabelTask.getProject().init();
		vssLabelTask.getProject().setBasedir(workingDir);
		vssLabelTask.setServerpath(getVssPath());
		vssLabelTask.setLogin(getVssUser() + "," + getVssPassword());
		vssLabelTask.setLabel(label);
		vssLabelTask.setVsspath("$/" + module);
		if (getSsDir() != null && !getSsDir().trim().equals(""))
			vssLabelTask.setSsdir(getSsDir());
		vssLabelTask.execute();
	}

	/**
	 * Method may throw a BuildException to indicates a module update exception
	 * @param workingDir
	 * @param module
	 * @param snapshot
	 * @param logFile
	 */
	public void updateModule(String workingDir, String module, Snapshot snapshot, String logFile) {
		retrieveModule(workingDir, module, snapshot, logFile);
	}
}
