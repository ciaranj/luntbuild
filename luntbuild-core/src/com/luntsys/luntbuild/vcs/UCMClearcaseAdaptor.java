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

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.utility.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The clearcase adaptor for UCM operations, this adaptor will utilize the base clearcase
 * adaptor to achieve UCM-oriented build
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
	 * perform next build. This property will not take effect when {@link whatToBuild}
	 * is not of value {@link com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade#BUILD_LATEST}
	 */
	private String modificationDetectionConfig;

	private String mkviewExtraOpts;

	public String getDisplayName() {
		return "Clearcase UCM";
	}

	public String getIconName() {
		return "ucmclearcase.jpg";
	}

	public List getVcsSpecificProperties() {
		List properties = getUCMClearcaseProperties();
		return properties;
	}

	/**
	 * Build the executable part of a commandline object
	 *
	 * @return
	 */
	protected Commandline buildCleartoolExecutable() {
		Commandline cmdLine = new Commandline();
		if (Luntbuild.isEmpty(getCleartoolDir()))
			cmdLine.setExecutable("cleartool");
		else
			cmdLine.setExecutable(Luntbuild.concatPath(getCleartoolDir(), "cleartool"));
		return cmdLine;
	}

	public String getCleartoolDir() {
		return cleartoolDir;
	}

	public void setCleartoolDir(String cleartoolDir) {
		this.cleartoolDir = cleartoolDir;
	}

	public String getModificationDetectionConfig() {
		return modificationDetectionConfig;
	}

	public void setModificationDetectionConfig(String modificationDetectionConfig) {
		this.modificationDetectionConfig = modificationDetectionConfig;
	}

	public Vcs deriveBuildTimeVcs(Project antProject) {
		BaseClearcaseAdaptor baseClearcaseAdaptor = new BaseClearcaseAdaptor();
		baseClearcaseAdaptor.setViewStgLoc(getViewStgLoc());
		baseClearcaseAdaptor.setVws(getVws());
		baseClearcaseAdaptor.setViewCfgSpec(constructCfgSpec(antProject));
		baseClearcaseAdaptor.setModificationDetectionConfig(getModificationDetectionConfig());
		baseClearcaseAdaptor.setMkviewExtraOpts(getMkviewExtraOpts());
		baseClearcaseAdaptor.setUcmStream(getStream() + "@" + getProjectVob());
		baseClearcaseAdaptor.setCleartoolDir(getCleartoolDir());
		return baseClearcaseAdaptor;
	}

	public void checkoutActually(Build build, Project antProject) {
		throw new BuildException("Checkout operation not applicable for UCM Clearcase adaptor!");
	}

	public void cleanupCheckout(Build build, Project antProject) {
		throw new BuildException("Cleanup checkout operation not applicable for UCM Clearcase adaptor!");
	}

	public void label(Build build, Project antProject) {
		throw new BuildException("Label operation not applicable for UCM Clearcase adaptor!");
	}

	public String getProjectVob() {
		return projectVob;
	}

	public void setProjectVob(String projectVob) {
		this.projectVob = projectVob;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public String getWhatToBuild() {
		return whatToBuild;
	}

	public void setWhatToBuild(String whatToBuild) {
		this.whatToBuild = whatToBuild;
	}

	public String getMkviewExtraOpts() {
		return mkviewExtraOpts;
	}

	public void setMkviewExtraOpts(String mkviewExtraOpts) {
		this.mkviewExtraOpts = mkviewExtraOpts;
	}

	public void validateProperties() {
		super.validateProperties();
		Pattern reservedPattern = Pattern.compile("^<(.*)>$");
		Matcher matcher = reservedPattern.matcher(getWhatToBuild());
		if (matcher.find()) {
			String reserved = matcher.group(1).trim();
			if (!reserved.equalsIgnoreCase(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BUILD_FOUNDATION_BASELINES) &&
					!reserved.equalsIgnoreCase(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BUILD_LATEST) &&
					!reserved.equalsIgnoreCase(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BUILD_LATEST_BASELINES) &&
					!reserved.equalsIgnoreCase(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BUILD_RECOMMENDED_BASELINES))
				throw new ValidationException("Invalid reserved values for \"what to build\" property!");
		}
		if ((Luntbuild.isEmpty(viewStgLoc)) && Luntbuild.isEmpty(vws))
			throw new ValidationException("Both \"Clearcase view storage name\" and \"Explicit path for view storage\" " +
					"are empty. You should specify at least one of them to store the view information");
		if (!Luntbuild.isEmpty(getModificationDetectionConfig())) {
			BufferedReader reader = new BufferedReader(new StringReader(getModificationDetectionConfig().replace(';', '\n')));
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					String fields[] = line.split(":");
					if (fields.length != 1 && fields.length != 2)
						throw new ValidationException("Invalid entry of property \"modification detection config\": " + line);
				}
			} catch (IOException e) {
				// ignores
			}
		}
	}

	/**
	 * Construct the config spec for current stream based on whatToBuild property
	 *
	 * @param antProject
	 * @return
	 */
	private String constructCfgSpec(Project antProject) {
		antProject.log("Construct config spec...", Project.MSG_INFO);
		String cfgSpec = "element * CHECKEDOUT\n";
		List baselines;
		Pattern reservedPattern = Pattern.compile("^<(.*)>$");
		Matcher matcher = reservedPattern.matcher(getWhatToBuild());
		if (matcher.find()) {
			String reserved = matcher.group(1).trim();
			if (reserved.equalsIgnoreCase(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BUILD_LATEST)) {
				baselines = getStreamBaselines(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BASELINE_LATEST, antProject);
				cfgSpec += "element * .../" + getStream() + "/LATEST\n";
			} else if (reserved.equalsIgnoreCase(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BUILD_FOUNDATION_BASELINES)) {
				baselines = getStreamBaselines(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BASELINE_FOUNDATION, antProject);
			} else if (reserved.equalsIgnoreCase(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BUILD_LATEST_BASELINES)) {
				baselines = getStreamBaselines(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BASELINE_LATEST, antProject);
			} else if (reserved.equalsIgnoreCase(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BUILD_RECOMMENDED_BASELINES)) {
				baselines = getStreamBaselines(com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade.BASELINE_RECOMMENDED, antProject);
			} else
				throw new BuildException("Invalid reserved value for what to build property: " +
						getWhatToBuild());
		} else {
			String fields[] = getWhatToBuild().trim().split("\\s");
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

	/**
	 * Retrieve recommended  baselines, foundation baselines or
	 * latest baselines of current stream
	 *
	 * @param baselineType specifies what type of baselines you want to retrieve,
	 *                     possible values are {@link UCMClearcaseAdaptorFacade#BASELINE_FOUNDATION},
	 *                     {@link com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade#BASELINE_LATEST},
	 *                     and {@link com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade#BASELINE_RECOMMENDED}
	 * @param antProject   the ant antProject
	 * @return
	 * @throws BuildException
	 */
	public List getStreamBaselines(String baselineType, Project antProject) {
		final List baselines = new ArrayList();

		Commandline cmdLine = buildCleartoolExecutable();
		cmdLine.createArgument().setLine("lsstream -fmt");
		cmdLine.createArgument().setValue("\"%[" + baselineType + "]p\"");
		cmdLine.createArgument().setValue(getStream() + "@" + getProjectVob());

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
	 * Get component for specified baseline name
	 *
	 * @param baseline
	 * @param antProject
	 * @return component name of specified baseline, will never be null
	 * @throws BuildException
	 */
	private String getBaselineComponent(String baseline, Project antProject) {
		final String[] component = new String[]{null};

		Commandline cmdLine = buildCleartoolExecutable();
		cmdLine.createArgument().setLine("lsbl -fmt");
		cmdLine.createArgument().setValue("\"%[component]p\"");
		cmdLine.createArgument().setValue(baseline + "@" + getProjectVob());

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
	 * Get dependent baselines of specified baseline
	 *
	 * @param baseline
	 * @param antProject
	 * @return
	 * @throws BuildException
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

		cmdLine.createArgument().setValue(baseline + "@" + getProjectVob());

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
	 * Get root directory of specified component
	 *
	 * @param component
	 * @param antProject
	 * @return root directory for specified component, a null value will be
	 *         returned if there is no root directory associated
	 * @throws BuildException
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

		cmdLine.createArgument().setValue(component + "@" + getProjectVob());

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
	 * Get the label type corresponding to this baseline name
	 *
	 * @param baseline
	 * @param antProject
	 * @return label type for specified baseline name, null value will be returned if
	 *         there is no label type associated with this baseline
	 */
	private String getBaselineLabeltype(String baseline, Project antProject) {
		final String[] labeltype = new String[]{null};
		final Pattern pattern = Pattern.compile("^\\s*BaselineLbtype\\s*->\\s*lbtype:(.*)@.*");

		Commandline cmdLine = buildCleartoolExecutable();
		cmdLine.createArgument().setValue("describe");
		if (baseline.startsWith("\"") && baseline.endsWith("\"")) {
			baseline = baseline.substring(1, baseline.length() - 1);
		}
		cmdLine.createArgument().setValue("baseline:" + baseline + "@" + getProjectVob());

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
	 * Given a set of foundation baselines, recommended baselines, or latest baselines,
	 * this function will resolves dependency of each baseline, take overriden into account
	 * and return a set of in-effect baselines indexed by component name
	 *
	 * @param baselines
	 * @param antProject
	 * @return
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

	public Module createNewModule() {
		return null; // module definition not applicable for this vcs
	}

    public Module createNewModule(Module module) {
        return null; // module definition not applicable for this vcs
    }

/*
	public boolean isConfigModifiedComparedTo(Vcs vcs, Project antProject) {
		return deriveBaseClearcaseAdaptor(antProject).isConfigModifiedComparedTo(vcs, antProject);
	}
*/

	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		throw new BuildException("Get revisions operation not applicable for UCM Clearcase adaptor!");
	}

	public void saveToFacade(VcsFacade facade) {
		UCMClearcaseAdaptorFacade ucmClearcaseFacade = (com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade) facade;
		ucmClearcaseFacade.setMkviewExtraOpts(getMkviewExtraOpts());
		ucmClearcaseFacade.setModificationDetectionConfig(getModificationDetectionConfig());
		ucmClearcaseFacade.setProjectVob(getProjectVob());
		ucmClearcaseFacade.setStream(getStream());
		ucmClearcaseFacade.setViewStgLoc(getViewStgLoc());
		ucmClearcaseFacade.setVws(getVws());
		ucmClearcaseFacade.setWhatToBuild(getWhatToBuild());
		ucmClearcaseFacade.setCleartoolDir(getCleartoolDir());
	}

	public void loadFromFacade(com.luntsys.luntbuild.facades.lb12.VcsFacade facade) {
		if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		UCMClearcaseAdaptorFacade ucmClearcaseFacade = (com.luntsys.luntbuild.facades.lb12.UCMClearcaseAdaptorFacade) facade;
		setMkviewExtraOpts(ucmClearcaseFacade.getMkviewExtraOpts());
		setModificationDetectionConfig(ucmClearcaseFacade.getModificationDetectionConfig());
		setProjectVob(ucmClearcaseFacade.getProjectVob());
		setStream(ucmClearcaseFacade.getStream());
		setViewStgLoc(ucmClearcaseFacade.getViewStgLoc());
		setVws(ucmClearcaseFacade.getVws());
		setWhatToBuild(ucmClearcaseFacade.getWhatToBuild());
		setCleartoolDir(ucmClearcaseFacade.getCleartoolDir());
	}

	public com.luntsys.luntbuild.facades.lb12.VcsFacade constructFacade() {
		return new UCMClearcaseAdaptorFacade();
	}
}
