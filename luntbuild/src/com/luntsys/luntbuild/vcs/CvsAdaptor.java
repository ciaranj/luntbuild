/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-4-26
 * Time: 11:38:40
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

import com.luntsys.luntbuild.db.Snapshot;
import com.luntsys.luntbuild.utility.LuntBuild;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.CVSPass;
import org.apache.tools.ant.taskdefs.Cvs;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

import java.io.*;

/**
 * adaptor to cvs version control system. It will be serialized by hibernate
 *
 * @author alvin shen
 */
public class CvsAdaptor implements Vcs{
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;
	private static final String passwdFileName = ".cvspass";
	private String cvsRoot;
	private String cvsPassword;

    /**
	 * set the cvs root of cvs repository
	 * @param cvsRoot the cvs root string, an example can be ":pserver:anoncvs@cvs.luntsys.com:/home/cvspublic"
	 */
	public void setCvsRoot(String cvsRoot){
		this.cvsRoot = cvsRoot;
	}

	/**
	 *  return the cvs root string
	 */
	public String getCvsRoot(){
		return cvsRoot;
	}

	/**
	 * set the cvs password
	 * @param cvsPassword password for the cvs repository
	 */
	public void setCvsPassword(String cvsPassword){
		this.cvsPassword = cvsPassword;
	}

	/**
	 * return the cvs repository password
	 */
	public String getCvsPassword(){
		return cvsPassword;
	}

	public String getVcsDisplayName() {
		return "cvs";
	}

	public String[] getProperties() {
		return new String[]{"cvsRoot", "cvsPassword"};
	}

	public String getPropertyDisplayName(String propertyName) {
		if (propertyName.equals("cvsRoot"))
			return "cvs root";
		else if (propertyName.equals("cvsPassword"))
			return "cvs password";
		return "";
	}

	public boolean isPropertySecret(String propertyName) {
		if (propertyName.equals("cvsPassword"))
			return true;
		else
			return false;
	}

	public boolean isPropertyOptional(String propertyName) {
		if (propertyName.equals("cvsPassword"))
			return true;
		else
			return false;
	}

	public String getPropertyDescription(String propertyName) {
		if (propertyName.equals("cvsRoot"))
			return "The cvs root for this project, for example, :pserver:administrator@localhost:d:/cvs_repository";
		else if (propertyName.equals("cvsPassword"))
			return "The cvs password for above cvs root";
		return "";
	}                                                   

	public void login(String logFile) {
		// call ant CVSPass task to login into cvs server
		CVSPass cvsPassTask = new CVSPass();
		cvsPassTask.setProject(new org.apache.tools.ant.Project());
		cvsPassTask.getProject().init();
		cvsPassTask.setCvsroot(getCvsRoot());
		cvsPassTask.setPassword(getCvsPassword());
		cvsPassTask.setPassfile(new File(LuntBuild.installDir + "/" + passwdFileName));
		cvsPassTask.execute();

		// cause the ant task CVSPass have a bug in windows system, we should go
		// longer to pick up the encrypted password and call a external program to save
		// it to windows registry
		if (System.getProperty("os.name").startsWith("Windows")){
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(LuntBuild.installDir + "/" + passwdFileName));
				String line;
				while((line = reader.readLine()) != null){
					String[] fields = line.split(" ");
					if (fields.length != 2)
						continue;
					if (fields[0].equals(getCvsRoot())){
						String cvsntPasswdExe = new File(LuntBuild.installDir + "/osdependent/cvsnt_passwd").getCanonicalPath();
						OutputStream logOutputStream = null;
						try {
							Execute executeTask = new Execute();
							logOutputStream = new FileOutputStream(logFile, true);
							executeTask.setStreamHandler(new PumpStreamHandler(logOutputStream));
							executeTask.setCommandline(new String[]{cvsntPasswdExe, getCvsRoot(), fields[1]});
							int exitCode = executeTask.execute();
							if (exitCode != 0)
								throw new BuildException("Execution of cvsnt_passwd command returns code of " + exitCode);
							return;
						} catch (IOException e) {
							throw new BuildException(e.getMessage());
						} finally {
							if (logOutputStream != null)
								try {
									logOutputStream.close();
								} catch (IOException e) {
									throw new BuildException("Failed to close file: " + logFile, e);
								}
						}
					}
				}
			} catch (IOException e) {
				throw new BuildException(e);
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						throw new BuildException("Failed to close password file", e);
					}
			}
			// we should not go this far
			throw new BuildException("Failed to find password for CVSROOT \"" + getCvsRoot() + "\" in password file!");
		}
	}

	public void logout(String logFile){
		// does nothing
	}

	/**
	 * Method may throw BuildException to indicates module acquisition error
	 * @param workingDir
	 * @param module
	 * @param snapshot
	 * @param logFile
	 */
	public void retrieveModule(String workingDir, String module, Snapshot snapshot, String logFile) {
		// call ant cvs task to retrieve module
		Cvs cvsTask = new Cvs();
		cvsTask.setProject(new org.apache.tools.ant.Project());
		cvsTask.getProject().init();
		cvsTask.setCommand("checkout");
		cvsTask.setCvsRoot(getCvsRoot());
		cvsTask.setPackage(module);
		cvsTask.setPassfile(new File(LuntBuild.installDir + "/" + passwdFileName));

		// label takes precedence of branch if both of them are not empty
		if (snapshot.getLabel() != null && !snapshot.getLabel().trim().equals(""))
			cvsTask.setTag(snapshot.getLabel());
		else if (snapshot.getBranch() != null && !snapshot.getBranch().trim().equals(""))
			cvsTask.setTag(snapshot.getBranch());

		cvsTask.setDest(new File(workingDir));
		cvsTask.setQuiet(true);
		cvsTask.setError(new File(logFile));
		cvsTask.setAppend(true);
		cvsTask.setFailOnError(true);
		cvsTask.execute();
	}

	/**
	 * Method may throw a BuildException to indicates a labeling error
	 * @param workingDir
	 * @param module
	 * @param snapshot
	 * @param label
	 * @param logFile
	 */
	public void labelModule(String workingDir, String module, Snapshot snapshot, String label, String logFile) {
		// call ant cvs task to perform code labeling
		Cvs cvsTask = new Cvs();
		cvsTask.setProject(new org.apache.tools.ant.Project());
		cvsTask.getProject().init();
		cvsTask.setCommand("tag " + label);
		cvsTask.setCvsRoot(getCvsRoot());
		cvsTask.setPassfile(new File(LuntBuild.installDir + "/" + passwdFileName));

		cvsTask.setDest(new File(workingDir + "/" + module));
		cvsTask.setQuiet(true);
		cvsTask.setError(new File(logFile));
		cvsTask.setAppend(true);
		cvsTask.setFailOnError(true);
		cvsTask.execute();
	}

	/**
	 * Method may throw a BuildException to indicates a module update exception
	 * @param workingDir
	 * @param module
	 * @param snapshot
	 * @param logFile
	 */
	public void updateModule(String workingDir, String module, Snapshot snapshot, String logFile) {
		// call ant cvs task to update module
		Cvs cvsTask = new Cvs();
		cvsTask.setProject(new org.apache.tools.ant.Project());
		cvsTask.getProject().init();
		cvsTask.setCommand("update");
		cvsTask.setCvsRoot(getCvsRoot());
		cvsTask.setPackage(module);
		cvsTask.setPassfile(new File(LuntBuild.installDir + "/" + passwdFileName));

		cvsTask.setDest(new File(workingDir));
		cvsTask.setQuiet(true);
		cvsTask.setError(new File(logFile));
		cvsTask.setAppend(true);
		cvsTask.setFailOnError(true);
		cvsTask.execute();
	}
}
