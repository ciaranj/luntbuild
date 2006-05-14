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

import com.luntsys.luntbuild.remoting.UCMClearcaseAdaptorFacade;
import com.luntsys.luntbuild.remoting.VcsFacade;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.utility.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;

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
public class UCMClearcaseAdaptor extends VcsAdaptor {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;

	/**
	 * The base clearcase adaptor used by the UCM adaptor
	 */
	private static ThreadLocal baseClearcaseAdaptor = new ThreadLocal();

	//private static Log logger = LogFactory.getLog(UCMClearcaseAdaptor.class);

	private String viewStgLoc;

	private String projectVob;

	private String vws;

	private String stream;

	private String whatToBuild = UCMClearcaseAdaptorFacade.BUILD_LATEST;
	/**
	 * Config to detect modifications. This is used to dertermine whether or not need to
	 * perform next build. This property will not take effect when {@link whatToBuild}
	 * is not of value {@link UCMClearcaseAdaptorFacade#BUILD_LATEST}
	 */
	private String modificationDetectionConfig;

	private String mkviewExtraOpts;

	public String getVcsDisplayName() {
		return "Clearcase UCM";
	}

	public List getProjectLevelProperties() {
		List properties = new ArrayList();
		properties.add(new DisplayProperty(){
			public String getDisplayName() {
				return "clearcase view stgloc name";
			}
			public String getDescription() {
				return "Name of the clearcase view storage location which will be used as" +
						"-stgloc option when creating all clearcase views under this project.";
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
		properties.add(new DisplayProperty(){
			public String getDisplayName() {
				return "project vob tag";
			}
			public String getDescription() {
				return "Tag for the project vob, for example: \\pvob1";
			}
			public String getValue() {
				return getProjectVob();
			}
			public void setValue(String value) {
				setProjectVob(value);
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
		properties.add(new DisplayProperty(){
			public String getDisplayName() {
				return "ucm stream name";
			}
			public String getDescription() {
				return "Name of the ucm stream";
			}
			public String getValue() {
				return getStream();
			}
			public void setValue(String value) {
				setStream(value);
			}
		});
		properties.add(new DisplayProperty(){
			public String getDisplayName() {
				return "what to build";
			}
			public String getDescription() {
				return "Specifies baselines you want to build inside the stream. Multiple baselines are " +
						"seperated by space. The following values are reserved for particular meaning:\n" +
						"<latest>:  means build with all latest codes from every component.\n" +
						"<latest baselines>:  means build with all latest baselines from every component.\n" +
						"<recommended baselines>:  means build with all recommended baselines.\n" +
						"<foundation baselines>:  means build with all foundation baselines.\n";
			}
			public String getValue() {
				return getWhatToBuild();
			}
			public void setValue(String value) {
				setWhatToBuild(value);
			}
		});
		properties.add(new DisplayProperty(){
			public String getDisplayName(){
				return "modification detection config";
			}
			public String getDescription() {
				return "This property will only take effect when whatToBuild property equals \"latest\". " +
						"It is used by luntbuild to lookup if there are any changes in the repository since last build. " +
						"This property is comprised of multiple entries with each entry be the format of " +
						"\"<path>[:<branch>]\". <path> stands for a path inside a vob which should be visible " +
						"using the above config spec. Luntbuild will lookup any changes at any branch " +
						"inside this path recursively, or will lookup changes at specified branch if <branch> is " +
						"specified. Multiple entries are seperated by \";\" or line terminator. Refer to user manual for " +
						"detailed information.";
			}
			public boolean isMultiLine(){
				return true;
			}
			public boolean isRequired() {
				return false;
			}
			public String getValue() {
				return getModificationDetectionConfig();
			}
			public void setValue(String modificationDetectionConfig) {
				setModificationDetectionConfig(modificationDetectionConfig);
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

	/**
	 * Derive the base clearcase adaptor object based on current setting
	 * @return
	 * @throws BuildException
	 */
	private BaseClearcaseAdaptor deriveBaseClearcaseAdaptor(Project antProject){
		BaseClearcaseAdaptor baseClearcaseAdaptor = new BaseClearcaseAdaptor();
		baseClearcaseAdaptor.setViewStgLoc(getViewStgLoc());
		baseClearcaseAdaptor.setVws(getVws());
		baseClearcaseAdaptor.setViewCfgSpec(constructCfgSpec(antProject));
		baseClearcaseAdaptor.setModificationDetectionConfig(getModificationDetectionConfig());
		baseClearcaseAdaptor.setMkviewExtraOpts(getMkviewExtraOpts());
		baseClearcaseAdaptor.setUcmStream(getStream() + "@" + getProjectVob());
		return baseClearcaseAdaptor;
	}

	public void checkout(Map properties, Build build, Project antProject) {
		// utilize base clearcase adaptor to finish our work
		setBaseClearcaseAdaptor(deriveBaseClearcaseAdaptor(antProject));
		getBaseClearcaseAdaptor().checkout(properties, build, antProject);

		// replace current build's vcs object in order to achieve rebuildable.
		build.setVcs(getBaseClearcaseAdaptor());
	}

	public void label(Map properties, Build build, Project antProject) {
		// this method will never get called because the vcs object was replaced with
		// base clearcase adaptor during checkout()
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

	
	/* (non-Javadoc)
	 * @see com.luntsys.luntbuild.vcs.Vcs#validateViewLevelProperties()
	 */
	public void validateViewLevelProperties() {
		super.validateViewLevelProperties();
		Pattern reservedPattern = Pattern.compile("^<(.*)>$");
		Matcher matcher = reservedPattern.matcher(getWhatToBuild());
		if (matcher.find()) {
			String reserved = matcher.group(1).trim();
			if (!reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_FOUNDATION_BASELINES) && 
					!reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_LATEST) && 
					!reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_LATEST_BASELINES) && 
					!reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_RECOMMENDED_BASELINES))
					throw new ValidationException("Invalid reserved values for \"what to build\" property!");
		}
	}
	
	public void validateAll() {
		super.validateAll();
		if ((viewStgLoc == null || viewStgLoc.trim().equals("")) &&
				(vws == null || vws.trim().equals("")))
			throw new ValidationException("Both \"Clearcase view storage name\" and  \"Explicit path for view storage\" " +
					"are empty. You should specify at least one of them to store the view information");
	}

	/**
	 * Construct the config spec for current stream based on whatToBuild property
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
			if (reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_LATEST)){
				baselines = getStreamBaselines(UCMClearcaseAdaptorFacade.BASELINE_LATEST, antProject);
				cfgSpec += "element * .../" + getStream() + "/LATEST\n";
			} else if (reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_FOUNDATION_BASELINES)) {
				baselines = getStreamBaselines(UCMClearcaseAdaptorFacade.BASELINE_FOUNDATION, antProject);
			} else if (reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_LATEST_BASELINES)){
				baselines = getStreamBaselines(UCMClearcaseAdaptorFacade.BASELINE_LATEST, antProject);
			} else if (reserved.equalsIgnoreCase(UCMClearcaseAdaptorFacade.BUILD_RECOMMENDED_BASELINES)){
				baselines = getStreamBaselines(UCMClearcaseAdaptorFacade.BASELINE_RECOMMENDED, antProject);
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
			cfgSpec += "element " + rootDir+ "/... " + label + "\n";
		}
		cfgSpec += "element * /main/0\n";

		antProject.log("Config spec:\n" + cfgSpec);

		return cfgSpec;
	}

	private static BaseClearcaseAdaptor getBaseClearcaseAdaptor() {
		return (BaseClearcaseAdaptor) baseClearcaseAdaptor.get();
	}

	private static void setBaseClearcaseAdaptor(BaseClearcaseAdaptor baseClearcaseAdaptor) {
		UCMClearcaseAdaptor.baseClearcaseAdaptor.set(baseClearcaseAdaptor);
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
	 * @param baselineType specifies what type of baselines you want to retrieve,
	 * possible values are {@link UCMClearcaseAdaptorFacade#BASELINE_FOUNDATION},
	 * {@link UCMClearcaseAdaptorFacade#BASELINE_LATEST},
	 * and {@link UCMClearcaseAdaptorFacade#BASELINE_RECOMMENDED}
	 * @param antProject the ant antProject
	 * @return
	 * @throws BuildException
	 */
	public List getStreamBaselines(String baselineType, Project antProject) {
		final List baselines = new ArrayList();

		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("cleartool");
		cmdLine.createArgument().setLine("lsstream -fmt");
		cmdLine.createArgument().setValue("\"%[" + baselineType + "]p\"");
		cmdLine.createArgument().setValue(getStream() + "@" + getProjectVob());

		new MyExecTask("lsstream", antProject, cmdLine, Project.MSG_VERBOSE){
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
	 * @param baseline
	 * @param antProject
	 * @return component name of specified baseline, will never be null
	 * @throws BuildException
	 */
	private String getBaselineComponent(String baseline, Project antProject) {
		final String[] component = new String[] { null };

		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("cleartool");
		cmdLine.createArgument().setLine("lsbl -fmt");
		cmdLine.createArgument().setValue("\"%[component]p\"");
		cmdLine.createArgument().setValue(baseline + "@" + getProjectVob());

		new MyExecTask("lsbl", antProject, cmdLine, Project.MSG_VERBOSE) {
			public void handleStdout(String line) {
				component[0] = line.trim();
			}
		}.execute();
		if (component[0] == null || component[0].equals(""))
			throw new BuildException("ERROR: Failed to find associated component for baseline \"" + baseline + "\"");
		return component[0];
	}

	/**
	 * Get dependent baselines of specified baseline
	 * @param baseline
	 * @param antProject
	 * @return
	 * @throws BuildException
	 */
	private List getBaselineDepends(String baseline, Project antProject) {
		final List dependsOn = new ArrayList();

		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("cleartool");
		cmdLine.createArgument().setLine("lsbl -fmt");
		cmdLine.createArgument().setValue("\"%[depends_on]p\"");
		cmdLine.createArgument().setValue(baseline + "@" + getProjectVob());

		new MyExecTask("lsbl", antProject, cmdLine, Project.MSG_VERBOSE){
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
	 * @param component
	 * @param antProject
	 * @return root directory for specified component, a null value will be
	 * returned if there is no root directory associated
	 * @throws BuildException
	 */
	private String getComponentRootDir(String component, Project antProject){
		final String[] rootDir = new String[] { null };

		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("cleartool");
		cmdLine.createArgument().setLine("lscomp -fmt");
		cmdLine.createArgument().setValue("\"%[root_dir]p\"");
		cmdLine.createArgument().setValue(component + "@" + getProjectVob());

		new MyExecTask("lscomp", antProject, cmdLine, Project.MSG_VERBOSE){
			public void handleStdout(String line) {
				rootDir[0] = line.trim();
			}
		}.execute();
		if (rootDir[0] != null && rootDir[0].trim().equals(""))
			rootDir[0] = null;
		return rootDir[0];
	}

	/**
	 * Get the label type corresponding to this baseline name
	 * @param baseline
	 * @param antProject
	 * @return label type for specified baseline name, null value will be returned if
	 * there is no label type associated with this baseline
	 */
	private String getBaselineLabeltype(String baseline, Project antProject){
		final String[] labeltype = new String[] { null };
		final Pattern pattern = Pattern.compile("^\\s*BaselineLbtype\\s*->\\s*lbtype:(.*)@.*");

		Commandline cmdLine = new Commandline();
		cmdLine.setExecutable("cleartool");
		cmdLine.createArgument().setValue("describe");
		cmdLine.createArgument().setValue("baseline:"+ baseline + "@" + getProjectVob());

		new MyExecTask("describe", antProject, cmdLine, Project.MSG_VERBOSE){
			public void handleStdout(String line) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find())
					labeltype[0] = matcher.group(1).trim();
			}
		}.execute();

		if (labeltype[0] != null && labeltype[0].trim().equals(""))
			labeltype[0] = null;
		return labeltype[0];
	}

	/**
	 * Given a set of foundation baselines, recommended baselines, or latest baselines,
	 * this function will resolves dependency of each baseline, take overriden into account
	 *  and return a set of in-effect baselines indexed by component name
	 * @param baselines
	 * @param antProject
	 * @return
	 */
	private Map getEffectBaselines(List baselines, Project antProject){
		String message = "Resolve baseline dependency and overriden...";
		antProject.log(message, Project.MSG_INFO);
		Map effectBaselines = new HashMap();
		Stack baselineStack = new Stack();

		// initialize the baseline stack
		Iterator it = baselines.iterator();
		while (it.hasNext()) baselineStack.push(it.next());

		// traverse the baseline stack to find effect baselines
		while (!baselineStack.empty()){
			String baseline = (String) baselineStack.pop();
			String component = getBaselineComponent(baseline, antProject);
			if (effectBaselines.get(component) == null){
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

	public boolean isConfigModifiedComparedTo(Vcs vcs, Project antProject) {
		return deriveBaseClearcaseAdaptor(antProject).isConfigModifiedComparedTo(vcs, antProject);
	}

	public com.luntsys.luntbuild.utility.Revisions getRevisionsSince(Map properties, Build build, Project antProject) {
		return deriveBaseClearcaseAdaptor(antProject).getRevisionsSince(properties, build, antProject);
	}

	public VcsFacade getFacade() {
		UCMClearcaseAdaptorFacade facade = new UCMClearcaseAdaptorFacade();
		facade.setMkviewExtraOpts(getMkviewExtraOpts());
		facade.setModificationDetectionConfig(getModificationDetectionConfig());
		facade.setProjectVob(getProjectVob());
		facade.setStream(getStream());
		facade.setViewStgLoc(getViewStgLoc());
		facade.setVws(getVws());
		facade.setWhatToBuild(getWhatToBuild());
		return facade;
	}

	public void setFacade(VcsFacade facade) {
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
	}
}
