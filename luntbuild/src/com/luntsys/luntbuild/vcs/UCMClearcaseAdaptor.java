/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-7-25
 * Time: 11:02
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
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.MyExecTask;
import com.luntsys.luntbuild.utility.OgnlHelper;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.ValidationException;

/**
 * UCM Clearcase VCS adaptor implementation. This adaptor will utilize the base Clearcase
 * adaptor to achieve UCM-oriented build.
 * 
 * <p>This adaptor is NOT safe for remote hosts.</p>
 *
 * @author robin shine
 * @see BaseClearcaseAdaptor
 */
public class UCMClearcaseAdaptor extends Vcs {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;

	//private static Log logger = LogFactory.getLog(UCMClearcaseAdaptor.class);

	private String viewStgLoc;

	private String projectVob;

	private String cleartoolDir;

	private String vws;

	private String stream;

	private String whatToBuild = "<" + UCMClearcaseAdaptorFacade.BUILD_LATEST + ">";
	/**
	 * Config to detect modifications. This is used to dertermine whether or not need to
	 * perform next build. This property will not take effect when <code>whatToBuild</code>
	 * is not of value {@link UCMClearcaseAdaptorFacade#BUILD_LATEST}
	 */
	private String modificationDetectionConfig;

	private String mkviewExtraOpts;

    /**
     * @inheritDoc
     */
	public String getDisplayName() {
		return "Clearcase UCM";
	}

    /**
     * @inheritDoc
     */
	public String getIconName() {
		return "ucmclearcase.jpg";
	}

    /**
     * @inheritDoc
     */
	public List getVcsSpecificProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Clearcase view stgloc name";
			}

			public String getDescription() {
				return "Name of the Clearcase view storage location, which will be used as " +
						"-stgloc option when creating Clearcase view for this project.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getViewStgLoc();
			}

			public String getActualValue() {
				return getActualViewStgLoc();
			}

			public void setValue(String value) {
				setViewStgLoc(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Project vob tag";
			}

			public String getDescription() {
				return "Tag for the project vob, for example: \\pvob1.";
			}

			public String getValue() {
				return getProjectVob();
			}

			public String getActualValue() {
				return getActualProjectVob();
			}

			public void setValue(String value) {
				setProjectVob(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Explicit path for view storage";
			}

			public String getDescription() {
				return "This property is required only when the \"Clearcase view stgloc name\" property is empty. " +
						"If specified, it will be used as -vws option instead of -stgloc option when creating Clearcase view.\n" +
						" NOTE. This value should be a writable UNC path on Windows platform.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getVws();
			}

			public String getActualValue() {
				return getActualVws();
			}

			public void setValue(String value) {
				setVws(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "UCM stream name";
			}

			public String getDescription() {
				return "Name of the UCM stream.";
			}

			public String getValue() {
				return getStream();
			}

			public String getActualValue() {
				return getActualStream();
			}

			public void setValue(String value) {
				setStream(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "What to build";
			}

			public String getDescription() {
				return "Specifies baselines you want to build inside the stream. Multiple baselines are " +
						"separated by space. The following values have particular meaning:\n" +
						"<latest>:  means build with all the latest code from every component\n" +
						"<latest baselines>:  means build with all the latest baselines from every component\n" +
						"<recommended baselines>:  means build with all the recommended baselines\n" +
						"<foundation baselines>:  means build with all the foundation baselines\n";
			}

			public String getValue() {
				return getWhatToBuild();
			}

			public String getActualValue() {
				return getActualWhatToBuild();
			}

			public void setValue(String value) {
				setWhatToBuild(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Modification detection config";
			}

			public String getDescription() {
				return "This property will only take effect when the \"What to build\" property equals to \"latest\". " +
						"It is used by Luntbuild to lookup if there are any changes in the repository since the last build. " +
						"This property comprises of multiple entries with each entry in the format " +
						"\"<path>[:<branch>]\". <path> is a path inside a vob which should be visible " +
						"using the above config spec. Luntbuild will lookup any changes at any branch " +
						"inside this path recursively, or it will lookup changes in the specified branch, if <branch> is " +
						"specified. Multiple entries are separated by \";\" or line terminator. Refer to the User's Guide for " +
						"details.";
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

			public String getActualValue() {
				return getActualModificationDetectionConfig();
			}

			public void setValue(String modificationDetectionConfig) {
				setModificationDetectionConfig(modificationDetectionConfig);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Extra options when creating snapshot view";
			}

			public String getDescription() {
				return "You may optionally specify extra options for the cleartool mkview " +
						"sub command used by Luntbuild to create related clearcase snapshot " +
						"view for the current project. Options that can be specified here are restricted to -tmode, " +
						"-ptime, and -cachesize. For example you can specify \"-tmode insert_cr\" " +
						"to use Windows end of line text mode.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getMkviewExtraOpts();
			}

			public String getActualValue() {
				return getActualMkviewExtraOpts();
			}

			public void setValue(String value) {
				setMkviewExtraOpts(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Path for cleartool executable";
			}

			public String getDescription() {
				return "The directory path, where your cleartool executable file resides in. " +
						"It should be specified here, if it does not exist in the system path.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getCleartoolDir();
			}

			public void setValue(String value) {
				setCleartoolDir(value);
			}
		});
		return properties;
	}

	/**
	 * Constructs the executable part of a commandline object.
	 *
	 * @return the commandline object
	 */
	protected Commandline buildCleartoolExecutable() {
		Commandline cmdLine = new Commandline();
		if (Luntbuild.isEmpty(getCleartoolDir()))
			cmdLine.setExecutable("cleartool");
		else
			cmdLine.setExecutable(Luntbuild.concatPath(getCleartoolDir(), "cleartool"));
		return cmdLine;
	}

	/**
	 * Gets the path to the cleartool executable.
	 * 
	 * @return the path to the cleartool executable
	 */
	public String getCleartoolDir() {
		return cleartoolDir;
	}

	/**
	 * Sets the path to the cleartool executable.
	 * 
	 * @param cleartoolDir the path to the cleartool executable
	 */
	public void setCleartoolDir(String cleartoolDir) {
		this.cleartoolDir = cleartoolDir;
	}

	/**
	 * Gets the modification detection config.
	 * 
	 * @return the modification detection config
	 */
	public String getModificationDetectionConfig() {
		return modificationDetectionConfig;
	}

	/**
	 * Gets the modification detection config. This method will parse OGNL variables.
	 * 
	 * @return the modification detection config
	 */
	public String getActualModificationDetectionConfig() {
		return OgnlHelper.evaluateScheduleValue(getModificationDetectionConfig());
	}

	/**
	 * Sets the modification detection config.
	 * 
	 * @param modificationDetectionConfig the modification detection config
	 */
	public void setModificationDetectionConfig(String modificationDetectionConfig) {
		this.modificationDetectionConfig = modificationDetectionConfig;
	}

	/**
     * @inheritDoc
	 * @see BaseClearcaseAdaptor
	 */
	public Vcs deriveBuildTimeVcs(Project antProject) {
		BaseClearcaseAdaptor baseClearcaseAdaptor = new BaseClearcaseAdaptor();
		baseClearcaseAdaptor.setViewStgLoc(getActualViewStgLoc());
		baseClearcaseAdaptor.setVws(getActualVws());
		baseClearcaseAdaptor.setViewCfgSpec(constructCfgSpec(antProject));
		baseClearcaseAdaptor.setModificationDetectionConfig(getActualModificationDetectionConfig());
		baseClearcaseAdaptor.setMkviewExtraOpts(getActualMkviewExtraOpts());
		baseClearcaseAdaptor.setUcmStream(getActualStream() + "@" + getActualProjectVob());
		baseClearcaseAdaptor.setCleartoolDir(getCleartoolDir());
		return baseClearcaseAdaptor;
	}

	/**
     * @inheritDoc
	 * @throws BuildException because this method does not apply
	 */
	public void checkoutActually(Build build, Project antProject) {
		throw new BuildException("Checkout operation not applicable for UCM Clearcase adaptor!");
	}

	/**
     * @inheritDoc
	 * @throws BuildException because this method does not apply
	 */
	public void cleanupCheckout(Build build, Project antProject) {
		throw new BuildException("Cleanup checkout operation not applicable for UCM Clearcase adaptor!");
	}

	/**
     * @inheritDoc
	 * @throws BuildException because this method does not apply
	 */
	public void label(Build build, Project antProject) {
		throw new BuildException("Label operation not applicable for UCM Clearcase adaptor!");
	}

	/**
	 * Gets the project vob tag.
	 * 
	 * @return the project vob tag
	 */
	public String getProjectVob() {
		return projectVob;
	}

	/**
	 * Gets the project vob tag. This method will parse OGNL variables.
	 * 
	 * @return the project vob tag
	 */
	public String getActualProjectVob() {
		return OgnlHelper.evaluateScheduleValue(getProjectVob());
	}

	/**
	 * Sets the project vob tag.
	 * 
	 * @param projectVob the project vob tag
	 */
	public void setProjectVob(String projectVob) {
		this.projectVob = projectVob;
	}

	/**
	 * Gets the UCM stream.
	 * 
	 * @return the UCM stream
	 */
	public String getStream() {
		return stream;
	}

	/**
	 * Gets the UCM stream. This method will parse OGNL variables.
	 * 
	 * @return the UCM stream
	 */
	public String getActualStream() {
		return OgnlHelper.evaluateScheduleValue(getStream());
	}

	/**
	 * Sets the UCM stream.
	 * 
	 * @param stream the UCM stream
	 */
	public void setStream(String stream) {
		this.stream = stream;
	}

	/**
	 * Gets the baselines to build in the stream.
	 * 
	 * @return the baselines to build
	 */
	public String getWhatToBuild() {
		return whatToBuild;
	}

	/**
	 * Gets the baselines to build in the stream. This method will parse OGNL variables.
	 * 
	 * @return the baselines to build
	 */
	public String getActualWhatToBuild() {
		return OgnlHelper.evaluateScheduleValue(getWhatToBuild());
	}

	/**
	 * Sets the baselines to build in the stream.
	 * 
	 * @param whatToBuild the baselines to build
	 */
	public void setWhatToBuild(String whatToBuild) {
		this.whatToBuild = whatToBuild;
	}

	/**
	 * Gets the extra options when creating snapshot view.
	 * 
	 * @return the extra options
	 */
	public String getMkviewExtraOpts() {
		return mkviewExtraOpts;
	}

	/**
	 * Gets the extra options when creating snapshot view. This method will parse OGNL variables.
	 * 
	 * @return the extra options
	 */
	public String getActualMkviewExtraOpts() {
		return OgnlHelper.evaluateScheduleValue(getMkviewExtraOpts());
	}

	/**
	 * Sets the extra options when creating snapshot view.
	 * 
	 * @param mkviewExtraOpts the extra options
	 */
	public void setMkviewExtraOpts(String mkviewExtraOpts) {
		this.mkviewExtraOpts = mkviewExtraOpts;
	}

    /**
     * Validates the properties of this VCS.
     *
     * @throws ValidationException if a property has an invalid value
     */
	public void validateProperties() {
		super.validateProperties();
		Pattern reservedPattern = Pattern.compile("^<(.*)>$");
		Matcher matcher = reservedPattern.matcher(getActualWhatToBuild());
		if (matcher.find()) {
			String reserved = matcher.group(1).trim();
			if (!reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_FOUNDATION_BASELINES) &&
					!reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_LATEST) &&
					!reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_LATEST_BASELINES) &&
					!reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_RECOMMENDED_BASELINES))
				throw new ValidationException("Invalid reserved values for \"what to build\" property!");
		}
		if ((Luntbuild.isEmpty(viewStgLoc)) && Luntbuild.isEmpty(vws))
			throw new ValidationException("Both \"Clearcase view storage name\" and \"Explicit path for view storage\" " +
					"are empty. You should specify at least one of them to store the view information");
		if (!Luntbuild.isEmpty(getModificationDetectionConfig())) {
			BufferedReader reader = new BufferedReader(new StringReader(getActualModificationDetectionConfig().replace(';', '\n')));
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					String fields[] = line.split(":");
					if (fields.length != 1 && fields.length != 2)
						throw new ValidationException("Invalid entry of property \"modification detection config\": " + line);
				}
			} catch (IOException e) {
				// ignores
			} finally {
				if (reader != null) try {reader.close();} catch (Exception e) {}
			}
		}
	}

	/**
	 * Constructs the config spec for current stream based on the <code>whatToBuild</code> property.
	 *
	 * @param antProject the ant project used for logging
	 * @return the config spec
	 */
	private String constructCfgSpec(Project antProject) {
		antProject.log("Construct config spec...", Project.MSG_INFO);
		String cfgSpec = "element * CHECKEDOUT\n";
		List baselines;
		Pattern reservedPattern = Pattern.compile("^<(.*)>$");
		Matcher matcher = reservedPattern.matcher(getActualWhatToBuild());
		if (matcher.find()) {
			String reserved = matcher.group(1).trim();
			if (reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_LATEST)) {
				baselines = getStreamBaselines(UCMClearcaseAdaptorFacade.BASELINE_LATEST, antProject);
				cfgSpec += "element * .../" + getActualStream() + "/LATEST\n";
			} else if (reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_FOUNDATION_BASELINES)) {
				baselines = getStreamBaselines(UCMClearcaseAdaptorFacade.BASELINE_FOUNDATION, antProject);
			} else if (reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_LATEST_BASELINES)) {
				baselines = getStreamBaselines(UCMClearcaseAdaptorFacade.BASELINE_LATEST, antProject);
			} else if (reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_RECOMMENDED_BASELINES)) {
				baselines = getStreamBaselines(UCMClearcaseAdaptorFacade.BASELINE_RECOMMENDED, antProject);
			} else
				throw new BuildException("Invalid reserved value for what to build property: " +
						getActualWhatToBuild());
		} else {
			String fields[] = getActualWhatToBuild().trim().split("\\s");
			baselines = new ArrayList();
			for (int i = 0; i < fields.length; i++) {
				baselines.add(fields[i]);
			}
		}

		Map effectBaselines = getEffectBaselines(baselines, antProject);
		Iterator it = effectBaselines.keySet().iterator();
		while (it.hasNext()) {
			String component = (String) it.next();
			String baseline = (String) effectBaselines.get(component);
			String rootDir = getComponentRootDir(component, antProject);
			String label = getBaselineLabeltype(baseline, antProject);
			if (rootDir == null || label == null)
				continue;
			cfgSpec += "load " + rootDir + "\n";
			cfgSpec += "element " + rootDir + "/... " + label + "\n";
		}
		cfgSpec += "element * /main/0\n";

		antProject.log("Config spec:\n" + cfgSpec);

		return cfgSpec;
	}

	/**
	 * Gets the Clearcase server-side view storage location.
	 * 
	 * @return the Clearcase server-side view storage location
	 */
	public String getViewStgLoc() {
		return viewStgLoc;
	}

	/**
	 * Gets the Clearcase server-side view storage location. This method will parse OGNL variables.
	 * 
	 * @return the Clearcase server-side view storage location
	 */
	public String getActualViewStgLoc() {
		return OgnlHelper.evaluateScheduleValue(getViewStgLoc());
	}

	/**
	 * Sets the Clearcase server-side view storage location.
	 * 
	 * @param viewStgLoc the Clearcase server-side view storage location
	 */
	public void setViewStgLoc(String viewStgLoc) {
		this.viewStgLoc = viewStgLoc;
	}

	/**
	 * Gets the path for view storage.
	 * 
	 * @return the path for view storage
	 */
	public String getVws() {
		return vws;
	}

	/**
	 * Gets the path for view storage. This method will parse OGNL variables.
	 * 
	 * @return the path for view storage
	 */
	public String getActualVws() {
		return OgnlHelper.evaluateScheduleValue(getVws());
	}

	/**
	 * Sets the path for view storage.
	 * 
	 * @param vws the path for view storage
	 */
	public void setVws(String vws) {
		this.vws = vws;
	}

	/**
	 * Retrieves the recommended baselines, foundation baselines or latest baselines of current stream.
	 *
	 * @param baselineType specifies what type of baselines you want to retrieve,
	 *                     possible values are {@link UCMClearcaseAdaptorFacade#BASELINE_FOUNDATION},
	 *                     {@link UCMClearcaseAdaptorFacade#BASELINE_LATEST},
	 *                     or {@link UCMClearcaseAdaptorFacade#BASELINE_RECOMMENDED}
	 * @param antProject   the ant project used for logging
	 * @return the baselines
	 */
	public List getStreamBaselines(String baselineType, Project antProject) {
		final List baselines = new ArrayList();

		Commandline cmdLine = buildCleartoolExecutable();
		cmdLine.createArgument().setLine("lsstream -fmt");
		cmdLine.createArgument().setValue("\"%[" + baselineType + "]p\"");
		cmdLine.createArgument().setValue(getActualStream() + "@" + getActualProjectVob());

		new MyExecTask("lsstream", antProject, cmdLine, Project.MSG_VERBOSE) {
			public void handleStdout(String line) {
				String[] fields = line.split("\\s");
				for (int i = 0; i < fields.length; i++) {
					String field = fields[i];
					if (!field.trim().equals(""))
						baselines.add(field);
				}
			}
		}.execute();
		return baselines;
	}

	/**
	 * Gets the component for the specified baseline name.
	 * 
	 * @param baseline the baseline name
	 * @param antProject the ant project used for logging
	 * @return the component name of specified baseline, will never be <code>null</code>
	 * @throws BuildException if the component could not be found
	 */
	private String getBaselineComponent(String baseline, Project antProject) {
		final String[] component = new String[]{null};

		Commandline cmdLine = buildCleartoolExecutable();
		cmdLine.createArgument().setLine("lsbl -fmt");
		cmdLine.createArgument().setValue("\"%[component]p\"");
		cmdLine.createArgument().setValue(baseline + "@" + getActualProjectVob());

		new MyExecTask("lsbl", antProject, cmdLine, Project.MSG_VERBOSE) {
			public void handleStdout(String line) {
				String[] fields = line.split("\\s");
				for (int i = 0; i < fields.length; i++) {
					String field = fields[i];
					if (!field.trim().equals("")) {
						component[0] = field;
						break;
					}
				}
			}
		}.execute();
		if (component[0] == null || component[0].equals(""))
			throw new BuildException("ERROR: Failed to find associated component for baseline \"" + baseline + "\"");
		return component[0];
	}

	/**
	 * Gets the dependent baselines of the specified baseline.
	 *
	 * @param baseline the baseline name
	 * @param antProject the ant project used for logging
	 * @return the dependent baselines
	 */
	private List getBaselineDepends(String baseline, Project antProject) {
		final List dependsOn = new ArrayList();

		Commandline cmdLine = buildCleartoolExecutable();
		cmdLine.createArgument().setLine("lsbl -fmt");
		cmdLine.createArgument().setValue("\"%[depends_on]p\"");
		if (baseline.startsWith("\"")) {
			baseline = baseline.substring(1);
		}
		if (baseline.endsWith("\"")) {
			baseline = baseline.substring(0, baseline.length() - 1);
		}

		cmdLine.createArgument().setValue(baseline + "@" + getActualProjectVob());

		new MyExecTask("lsbl", antProject, cmdLine, Project.MSG_VERBOSE) {
			public void handleStdout(String line) {
				String[] fields = line.split("\\s");
				for (int i = 0; i < fields.length; i++) {
					String field = fields[i];
					if (field.trim().equals(""))
						continue;
					String parts[] = field.split("@");
					if (parts.length == 2)
						dependsOn.add(parts[0].trim());
				}
			}
		}.execute();

		return dependsOn;
	}

	/**
	 * Gets the root directory of the specified component.
	 *
	 * @param component the component
	 * @param antProject the ant project used for logging
	 * @return the root directory, or <code>null</code> if there is no root
	 * directory associated with the component
	 */
	private String getComponentRootDir(String component, Project antProject) {
		final String[] rootDir = new String[]{null};

		Commandline cmdLine = buildCleartoolExecutable();
		cmdLine.createArgument().setLine("lscomp -fmt");
		cmdLine.createArgument().setValue("\"%[root_dir]p\"");
		if (component.startsWith("\"")) {
			component = component.substring(1);
		}
		if (component.endsWith("\"")) {
			component = component.substring(0, component.length() - 1);
		}

		cmdLine.createArgument().setValue(component + "@" + getActualProjectVob());

		new MyExecTask("lscomp", antProject, cmdLine, Project.MSG_VERBOSE) {
			public void handleStdout(String line) {
				rootDir[0] = line.trim();
			}
		}.execute();
		if (rootDir[0] != null && rootDir[0].trim().equals(""))
			rootDir[0] = null;

		if (rootDir[0] != null) {
			if (rootDir[0].startsWith("\""))
				rootDir[0] = rootDir[0].substring(1);
			if (rootDir[0].endsWith("\""))
				rootDir[0] = rootDir[0].substring(0, rootDir[0].length() - 1);
		}
		return rootDir[0];
	}

	/**
	 * Gets the label type corresponding to the specified baseline name.
	 *
	 * @param baseline the baseline name
	 * @param antProject the ant project used for logging
	 * @return the label type, or <code>null</code> if there is no label type
	 * associated with the baseline
	 * @throws BuildException if the baseline has no associated label
	 */
	private String getBaselineLabeltype(String baseline, Project antProject) {
		final String[] labeltype = new String[]{null};
		final Pattern pattern = Pattern.compile("^\\s*BaselineLbtype\\s*->\\s*lbtype:(.*)@.*");

		Commandline cmdLine = buildCleartoolExecutable();
		cmdLine.createArgument().setValue("describe");
		if (baseline.startsWith("\"") && baseline.endsWith("\"")) {
			baseline = baseline.substring(1, baseline.length() - 1);
		}
		cmdLine.createArgument().setValue("baseline:" + baseline + "@" + getActualProjectVob());

		new MyExecTask("describe", antProject, cmdLine, Project.MSG_VERBOSE) {
			public void handleStdout(String line) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find())
					labeltype[0] = matcher.group(1).trim();
			}
		}.execute();

		if (labeltype[0] != null && labeltype[0].trim().equals(""))
			labeltype[0] = null;
		if (labeltype[0] == null && baseline.startsWith("deliverbl")) {
			throw new BuildException("ERROR: Baseline \"" + baseline + "\" was created automatically during deliver process, " +
					"and currently has no associated label. Please consider to upgrade this baseline before proceed!");
		}
		return labeltype[0];
	}

	/**
	 * Resolves dependencies among baselines.
	 * 
	 * <p>Given a set of foundation baselines, recommended baselines, or latest baselines,
	 * this function will resolves dependency of each baseline, take overriden into account
	 * and return a set of in-effect baselines indexed by component name.</p>
	 *
	 * @param baselines the set of all baselines
	 * @param antProject the ant project used for logging
	 * @return the map of in-effect baselines indexed by component name
	 */
	private Map getEffectBaselines(List baselines, Project antProject) {
		String message = "Resolved baseline dependency and overriden...";
		antProject.log(message, Project.MSG_INFO);
		Map effectBaselines = new HashMap();
		Stack baselineStack = new Stack();

		// initialize the baseline stack
		Iterator it = baselines.iterator();
		while (it.hasNext()) baselineStack.push(it.next());

		// traverse the baseline stack to find effect baselines
		while (!baselineStack.empty()) {
			String baseline = (String) baselineStack.pop();
			if (baseline.startsWith("\"")) {
				baseline = baseline.substring(1);
			}
			if (baseline.endsWith("\"")) {
				baseline = baseline.substring(0, baseline.length() - 1);
			}

			String component = getBaselineComponent(baseline, antProject);
			if (effectBaselines.get(component) == null) {
				effectBaselines.put(component, baseline);
				it = getBaselineDepends(baseline, antProject).iterator();
				while (it.hasNext()) baselineStack.push(it.next());
			}
		}

		return effectBaselines;
	}

	/**
     * @inheritDoc
	 */
	public Module createNewModule() {
		return null; // module definition not applicable for this vcs
	}

	/**
     * @inheritDoc
	 */
    public Module createNewModule(Module module) {
        return null; // module definition not applicable for this vcs
    }

/*
	public boolean isConfigModifiedComparedTo(Vcs vcs, Project antProject) {
		return deriveBaseClearcaseAdaptor(antProject).isConfigModifiedComparedTo(vcs, antProject);
	}
*/

	/**
     * @inheritDoc
	 * @throws BuildException because this method does not apply
	 */
	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		throw new BuildException("Get revisions operation not applicable for UCM Clearcase adaptor!");
	}

    /**
     * @inheritDoc
     * @see UCMClearcaseAdaptorFacade
     */
	public void saveToFacade(VcsFacade facade) {
    	// TODO throw RuntimeException if the facade is not the right class
		UCMClearcaseAdaptorFacade ucmClearcaseFacade = (UCMClearcaseAdaptorFacade) facade;
		ucmClearcaseFacade.setMkviewExtraOpts(getMkviewExtraOpts());
		ucmClearcaseFacade.setModificationDetectionConfig(getModificationDetectionConfig());
		ucmClearcaseFacade.setProjectVob(getProjectVob());
		ucmClearcaseFacade.setStream(getStream());
		ucmClearcaseFacade.setViewStgLoc(getViewStgLoc());
		ucmClearcaseFacade.setVws(getVws());
		ucmClearcaseFacade.setWhatToBuild(getWhatToBuild());
		ucmClearcaseFacade.setCleartoolDir(getCleartoolDir());
	}

    /**
     * @inheritDoc
     * @throws RuntimeException if the facade is not an <code>UCMClearcaseAdaptorFacade</code>
     * @see UCMClearcaseAdaptorFacade
     */
	public void loadFromFacade(VcsFacade facade) {
		if (!(facade instanceof UCMClearcaseAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		UCMClearcaseAdaptorFacade ucmClearcaseFacade = (UCMClearcaseAdaptorFacade) facade;
		setMkviewExtraOpts(ucmClearcaseFacade.getMkviewExtraOpts());
		setModificationDetectionConfig(ucmClearcaseFacade.getModificationDetectionConfig());
		setProjectVob(ucmClearcaseFacade.getProjectVob());
		setStream(ucmClearcaseFacade.getStream());
		setViewStgLoc(ucmClearcaseFacade.getViewStgLoc());
		setVws(ucmClearcaseFacade.getVws());
		setWhatToBuild(ucmClearcaseFacade.getWhatToBuild());
		setCleartoolDir(ucmClearcaseFacade.getCleartoolDir());
	}

    /**
     * @inheritDoc
     * @see UCMClearcaseAdaptorFacade
     */
	public VcsFacade constructFacade() {
		return new UCMClearcaseAdaptorFacade();
	}
}
