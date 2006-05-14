/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-7-23
 * Time: 10:06
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.remoting.BaseClearcaseAdaptorFacade;
import com.luntsys.luntbuild.remoting.VcsFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.MyExecTask;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.ValidationException;

/**
 * The clearcase base adaptor
 *
 * @author robin shine
 */
public class BaseClearcaseAdaptor extends VcsAdaptor {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;

	private static final SimpleDateFormat CMD_DATE_FORMAT =
			new SimpleDateFormat("dd-MMMM-yyyy.HH:mm:ss", new DateFormatSymbols(Locale.ENGLISH));

	/**
	 * A project level property, it stands for the server storage location name for all clearcase
	 * views configured under that project
	 */
	private String viewStgLoc;

	private String vws;

	private static ThreadLocal viewName = new ThreadLocal();

	/**
	 * A view level property, it stands for the config spec specified for this view
	 */
	private String viewCfgSpec;

	/**
	 * Config to detect modifications. This is used to dertermine whether or not need to
	 * perform next build
	 */
	private String modificationDetectionConfig;

	private String mkviewExtraOpts;

	/**
	 * The ucm stream attach to
	 */
	private String ucmStream;

	public String getVcsDisplayName() {
		return "base clearcase";
	}

	public List getProjectLevelProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "clearcase view stgloc name";
			}

			public String getDescription() {
				return "Name of the clearcase server-side view storage location which will be used as" +
						"-stgloc option when creating all clearcase views under this project. This property is optional " +
						"if you specifies \"explicit path for view storage\" when define each view under this project";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getViewStgLoc();
			}

			public void setValue(String value) {
				setViewStgLoc(value);
			}
		});
		return properties;
	}

	public List getViewLevelProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "explicit path for view storage";
			}

			public String getDescription() {
				return "This property is required only when the \"Clearcase view stgloc name\" property at project level is empty. " +
						"If sepcified, it will be used as -vws option when creating this clearcase view instead of using the -stgloc option." +
						" This value should be a writable UNC path on windows platform";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getVws();
			}

			public void setValue(String value) {
				setVws(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "config spec";
			}

			public String getDescription() {
				return "Config spec for current view";
			}

			public boolean isMultiLine() {
				return true;
			}

			public String getValue() {
				return getViewCfgSpec();
			}

			public void setValue(String value) {
				setViewCfgSpec(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "modification detection config";
			}

			public String getDescription() {
				return "This property will take effect if there are some LATEST versions from some branch be " +
						"specified to fetch in the above config spec. It is used by luntbuild to determine if there " +
						"are any changes in the repository since last build. " +
						"This property is comprised of multiple entries with each entry be the format of " +
						"\"<path>[:<branch>]\". <path> stands for a path inside a vob which should be visible " +
						"using the above config spec. Luntbuild will lookup any changes at any branch " +
						"inside this path recursively, or will lookup changes at specified branch if <branch> is " +
						"specified. Multiple entries are seperated by \";\" or line terminator. Refer to " +
						"user manual for detailed information.";
			}

			public boolean isMultiLine() {
				return true;
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getModificationDetectionConfig();
			}

			public void setValue(String value) {
				setModificationDetectionConfig(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "extra options when create snapshot view";
			}

			public String getDescription() {
				return "You may optionally specify extra options for the cleartool mkview " +
						"sub command used by luntbuild to create related clearcase snapshot " +
						"view for current view. Options can be specified here is restrict to -tmode, " +
						"-ptime, and -cachesize. For example you can specify \"-tmode insert_cr\" " +
						"to use windows end of line text mode";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getMkviewExtraOpts();
			}

			public void setValue(String value) {
				setMkviewExtraOpts(value);
			}
		});
		return properties;
	}

	public String getModificationDetectionConfig() {
		return modificationDetectionConfig;
	}

	public void setModificationDetectionConfig(String modificationDetectionConfig) {
		this.modificationDetectionConfig = modificationDetectionConfig;
	}

	public void validateAll() {
		super.validateAll();
		if ((viewStgLoc == null || viewStgLoc.trim().equals("")) &&
				(vws == null || vws.trim().equals("")))
			throw new ValidationException("Both \"Clearcase view storage name\" and  \"Explicit path for view storage\" " +
					"are empty. You should specify at least one of them to store the view information");
	}

	public void checkout(Map properties, Build build, Project antProject) {
		String workingDir = build.getBuildSchedule().getView().getWorkingDir(properties);
		setupViewName(build);
		List loadElements = getLoadElements();
		if (loadElements.size() == 0)
			throw new BuildException("ERROR: No elements configured for load in the view config spec!");
		if (build.isCleanBuild() || build.isRebuild()) {
			// re-create the clearcase view to ensure a safe-cleanup of view working directory
			if (ccViewExists(antProject))
				deleteCcView(antProject);
			Luntbuild.deleteDir(workingDir);			
			createCcView(workingDir, antProject);
			antProject.log("Retrieving source codes from clearcase...", Project.MSG_INFO);
		} else
			antProject.log("Updating source codes from clearcase...", Project.MSG_INFO);

		// when set the config spec, clearcase will automatically update the view working directory
		// with latest codes
		if (!build.isRebuild() || !containLatestVersion())
			setCcViewCfgSpec(workingDir, viewCfgSpec, Project.MSG_VERBOSE, antProject);
		else {
			String rebuildCfgSpec = "element * CHECKEDOUT\n";
			rebuildCfgSpec += "element * " + Luntbuild.getLabelByVersion(build.getVersion()) + "\n";
			Iterator itLoadElement = loadElements.iterator();
			while (itLoadElement.hasNext()) {
				String loadElement = (String) itLoadElement.next();
				rebuildCfgSpec += "load " + loadElement + "\n";
			}
			setCcViewCfgSpec(workingDir, rebuildCfgSpec, Project.MSG_VERBOSE, antProject);
		}
	}

	public void label(Map properties, Build build, Project antProject) {
		if (containLatestVersion()) {
			List loadElements = getLoadElements();
			antProject.log("Labeling current retrieved codes...", Project.MSG_INFO);
			String workingDir = build.getBuildSchedule().getView().getWorkingDir(properties);
			Iterator itLoadElement = loadElements.iterator();
			while (itLoadElement.hasNext()) {
				String loadElement = (String) itLoadElement.next();
				createCcLabelType(workingDir, loadElement, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
			}
			itLoadElement = loadElements.iterator();
			while (itLoadElement.hasNext()) {
				String loadElement = (String) itLoadElement.next();
				createCcLabel(workingDir, loadElement, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
			}
		}
	}

	public String getViewStgLoc() {
		return viewStgLoc;
	}

	public void setViewStgLoc(String viewStgLoc) {
		this.viewStgLoc = viewStgLoc;
	}

	public String getVws() {
		return vws;
	}

	public void setVws(String vws) {
		this.vws = vws;
	}

	public String getViewName() {
		return (String) viewName.get();
	}

	public void setupViewName(Build build) {
		// choose a view name used by current build
		BaseClearcaseAdaptor.viewName.set(Luntbuild.getHostName() +
				"-luntbuild-" + build.getBuildSchedule().getView().getId());
	}

	public String getViewCfgSpec() {
		return viewCfgSpec;
	}

	public void setViewCfgSpec(String viewCfgSpec) {
		this.viewCfgSpec = viewCfgSpec;
	}

	public String getMkviewExtraOpts() {
		return mkviewExtraOpts;
	}

	public void setMkviewExtraOpts(String mkviewExtraOpts) {
		this.mkviewExtraOpts = mkviewExtraOpts;
	}

	public String getUcmStream() {
		return ucmStream;
	}

	public void setUcmStream(String ucmStream) {
		this.ucmStream = ucmStream;
	}

	/**
	 * Does the clearcase view represented by this vcs object exists
	 *
	 * @param antProject
	 * @return
	 */
	private boolean ccViewExists(Project antProject) {
		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("cleartool");
		cmdLine.createArgument().setLine("lsview " + getViewName());
		try {
			new MyExecTask("lsview", antProject, cmdLine, Project.MSG_INFO).execute();
			return true;
		} catch (BuildException e) {
			return false;
		}
	}

	/**
	 * Delete the the clearcase view represented by this vcs object
	 *
	 * @param antProject
	 */
	private void deleteCcView(Project antProject) {
		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("cleartool");
		cmdLine.createArgument().setLine("rmview -force -tag " + getViewName());
		new MyExecTask("rmview", antProject, cmdLine, Project.MSG_INFO).execute();
	}

	/**
	 * Create a clearcase view represented by this vcs object
	 *
	 * @param workingDir
	 * @param antProject
	 */
	private void createCcView(String workingDir, Project antProject) {
		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("cleartool");
		String options = "";
		if (!Luntbuild.isEmpty(getMkviewExtraOpts()))
			options += getMkviewExtraOpts();
		if (!Luntbuild.isEmpty(getUcmStream()))
			options += " -stream " + getUcmStream();
		if (!Luntbuild.isEmpty(vws))
			options += " -vws " + vws;
		else
			options += " -stgloc " + viewStgLoc;
		cmdLine.createArgument().setLine("mkview -snapshot -tag " + getViewName() +
				" " + options);

		cmdLine.createArgument().setValue(workingDir);
		new MyExecTask("mkview", antProject, cmdLine, Project.MSG_INFO).execute();
	}

	/**
	 * Set the config spec for specified clearcase view
	 *
	 * @param workingDir
	 * @param antProject
	 */
	public void setCcViewCfgSpec(String workingDir, String viewCfgSpec, int outputLogPriority, Project antProject) {
		File cfgSpecFile = null;
		PrintStream cfgSpecStream = null;
		try {
			cfgSpecFile = File.createTempFile(getViewName(), "cfgspec",
					new File(Luntbuild.installDir + "/tmp"));
			cfgSpecStream = new PrintStream(new FileOutputStream(cfgSpecFile));
			BufferedReader reader = new BufferedReader(new StringReader(viewCfgSpec.replace(';', '\n')));
			String line;
			while ((line = reader.readLine()) != null) {
				cfgSpecStream.println(line);
			}
			cfgSpecStream.close();
			cfgSpecStream = null;

			Commandline cmdLine = new Commandline();
			cmdLine.setExecutable("cleartool");
			cmdLine.createArgument().setLine("setcs -tag " + getViewName());
			cmdLine.createArgument().setValue(cfgSpecFile.getAbsolutePath());

			new MyExecTask("setcs", antProject, workingDir, cmdLine, null, "yes\n",
					outputLogPriority).execute();
		} catch (FileNotFoundException e) {
			throw new BuildException(e.getMessage());
		} catch (IOException e) {
			throw new BuildException(e.getMessage());
		} finally {
			if (cfgSpecStream != null)
				cfgSpecStream.close();
			if (cfgSpecFile != null)
				cfgSpecFile.delete();
			antProject.log("Delete clearcase update logs...", Project.MSG_INFO);
			Delete deleteTask = new Delete();
			deleteTask.setProject(antProject);
			FileSet fileSet = new FileSet();
			fileSet.setDir(new File(workingDir));
			fileSet.setIncludes("*.updt");
			deleteTask.addFileset(fileSet);
			deleteTask.setTaskType("delete");
			deleteTask.setTaskName("delete");
			deleteTask.execute();
		}
	}

	/**
	 * Create the clearcase label type
	 *
	 * @param workingDir
	 * @param loadElement
	 * @param ccLabelType
	 * @param antProject
	 */
	private void createCcLabelType(String workingDir, String loadElement, String ccLabelType, Project antProject) {
		String vobPath = Luntbuild.concatPath(workingDir, loadElement);

		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("cleartool");
		cmdLine.createArgument().setLine("mklbtype -c \"build_label\" " + ccLabelType);
		try {
			new MyExecTask("mklbtype", antProject, vobPath, cmdLine, null, null, Project.MSG_INFO).execute();
		} catch (BuildException e) {
			// then re-try with -replace option
			cmdLine.clearArgs();
			cmdLine.createArgument().setLine("mklbtype -replace -c \"build_label\" " + ccLabelType);
			new MyExecTask("mklbtype", antProject, vobPath, cmdLine, null, null, Project.MSG_INFO).execute();
		}
	}

	/**
	 * Create the clearcase label
	 *
	 * @param workingDir
	 * @param loadElement
	 * @param ccLabelType
	 * @param antProject
	 */
	private void createCcLabel(String workingDir, String loadElement, String ccLabelType, Project antProject) {
		String vobPath = Luntbuild.concatPath(workingDir, loadElement);

		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("cleartool");
		cmdLine.createArgument().setLine("mklabel -recurse -c \"build_label\" " + ccLabelType);
		cmdLine.createArgument().setValue(vobPath);
		try {
			new MyExecTask("mklabel", antProject, cmdLine, Project.MSG_VERBOSE).execute();
		} catch (BuildException e) {
			// then re-try with -replace option
			cmdLine.clearArgs();
			cmdLine.createArgument().setLine("mklabel -replace -recurse -c \"build_label\" " + ccLabelType);
			cmdLine.createArgument().setValue(vobPath);
			new MyExecTask("mklabel", antProject, cmdLine, Project.MSG_VERBOSE).execute();
		}
	}

	/**
	 * Get the elements configured for load in the view config spec
	 *
	 * @return
	 */
	private List getLoadElements() {
		BufferedReader reader = new BufferedReader(new StringReader(viewCfgSpec.replace(';', '\n')));
		List loadElements = new ArrayList();
		try {
			String line;
			Pattern pattern = Pattern.compile("^\\s*load(.*)", Pattern.CASE_INSENSITIVE);
			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find())
					loadElements.add(matcher.group(1).trim());
			}
		} catch (IOException e) {
		}
		return loadElements;
	}

	/**
	 * Determines if a given config spec denotes some LATEST version
	 *
	 * @return
	 */
	private boolean containLatestVersion() {
		BufferedReader reader = new BufferedReader(new StringReader(viewCfgSpec.replace(';', '\n')));
		try {
			String line;
			Pattern pattern = Pattern.compile("^\\s*element(.*)", Pattern.CASE_INSENSITIVE);
			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					if (matcher.group(1).matches(".*LATEST.*"))
						return true;
				}
			}
		} catch (IOException e) {
		}
		return false;
	}

	public Module createNewModule() {
		return null; // module definition not applicable for current vcs
	}

	public boolean isConfigModifiedComparedTo(Vcs vcs, Project antProject) {
		if (vcs.getClass() == UCMClearcaseAdaptor.class)
			return vcs.isConfigModifiedComparedTo(this, antProject);
		else
			return super.isConfigModifiedComparedTo(vcs, antProject);
	}

	public Revisions getRevisionsSince(Map properties, Build build, Project antProject) {
		String workingDir = build.getBuildSchedule().getView().getWorkingDir(properties);
		final Revisions revisions = new Revisions();

		if (!containLatestVersion())
			return revisions;    // ignore modifications detection configs if there is no LATEST version configured for load

		// prepare view working directory to run cleartool history command
		setupViewName(build);
		try {
			setCcViewCfgSpec(workingDir, viewCfgSpec, -1, antProject);
		} catch (BuildException e) {
			antProject.log("Failed to update working directory with current config spec, " +
					"try to re-create working directory...", Project.MSG_INFO);
			if (ccViewExists(antProject))
				deleteCcView(antProject);
			Luntbuild.deleteDir(workingDir);
			createCcView(workingDir, antProject);
			setCcViewCfgSpec(workingDir, viewCfgSpec, -1, antProject);
		}

		BufferedReader reader = new BufferedReader(new StringReader(modificationDetectionConfig.replace(';', '\n')));
		try {
			String line;
			String path, branch;
			while ((line = reader.readLine()) != null) {
				String fields[] = line.split(":");
				if (fields.length == 2) {
					path = Luntbuild.concatPath(workingDir, fields[0].trim());
					branch = fields[1].trim();
				} else if (fields.length == 1) {
					path = Luntbuild.concatPath(workingDir, fields[0].trim());
					branch = null;
				} else
					throw new BuildException("Invalid entry of property \"modification " +
							"detection config\": " + line);
				if (!new File(path).exists())
					throw new BuildException("Invalid entry of property \"modification " +
							"detection config\": " + line + ", path not found using specified config spec");
				Commandline cmdLine = new Commandline();
				cmdLine.setExecutable("cleartool");
				cmdLine.createArgument().setLine("lshistory -fmt \"date:%d user:%u action:%e %n\n\" -nco -r");
				if (branch != null)
					cmdLine.createArgument().setLine("-branch " + branch);
				cmdLine.createArgument().setLine("-since " + CMD_DATE_FORMAT.format(build.getStartDate()));
				cmdLine.createArgument().setValue(path);
				final Pattern authorPattern = Pattern.compile("date:.*user:(.*)action:.*");
				new MyExecTask("history", antProject, workingDir, cmdLine, null, null, -1) {
					public void handleStdout(String line) {
						revisions.getChangeLogs().add(line);
						revisions.setFileModified(true);
						Matcher matcher = authorPattern.matcher(line);
						if (matcher.find())
							revisions.getChangeLogins().add(matcher.group(1).trim());
					}
				}.execute();
			}
		} catch (IOException e) {
			// ignores
		}

		return revisions;
	}

	public VcsFacade getFacade() {
		BaseClearcaseAdaptorFacade facade = new BaseClearcaseAdaptorFacade();
		facade.setMkviewExtraOpts(getMkviewExtraOpts());
		facade.setModificationDetectionConfig(getModificationDetectionConfig());
		facade.setViewCfgSpec(getViewCfgSpec());
		facade.setViewStgLoc(getViewStgLoc());
		facade.setVws(getVws());
		return facade;
	}

	public void setFacade(VcsFacade facade) {
		if (!(facade instanceof BaseClearcaseAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		BaseClearcaseAdaptorFacade baseClearcaseFacade = (BaseClearcaseAdaptorFacade) facade;
		setMkviewExtraOpts(baseClearcaseFacade.getMkviewExtraOpts());
		setModificationDetectionConfig(baseClearcaseFacade.getModificationDetectionConfig());
		setViewCfgSpec(baseClearcaseFacade.getViewCfgSpec());
		setViewStgLoc(baseClearcaseFacade.getViewStgLoc());
		setVws(baseClearcaseFacade.getVws());
	}
}
