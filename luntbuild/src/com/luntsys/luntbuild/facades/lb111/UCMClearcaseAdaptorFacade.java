/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 17:59:28
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

package com.luntsys.luntbuild.facades.lb111;

/**
 * UCM Clearcase VCS adaptor. This adaptor does not support modules.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.UCMClearcaseAdaptor
 */
public class UCMClearcaseAdaptorFacade extends VcsFacade {
	/** Baseline to build, latest */
	public static final String BUILD_LATEST = "latest";
	/** Baseline to build, latest baselines */
	public static final String BUILD_LATEST_BASELINES = "latest baselines";
	/** Baseline to build, recommended baselines */
	public static final String BUILD_RECOMMENDED_BASELINES = "recommended baselines";
	/** Baseline to build, foundation baselines */
	public static final String BUILD_FOUNDATION_BASELINES = "foundation baselines";

	/** Baseline, fondation */
	public static final String BASELINE_FOUNDATION = "found_bls";
	/** Baseline, latest */
	public static final String BASELINE_LATEST = "latest_bls";
	/** Baseline, recommended */
	public static final String BASELINE_RECOMMENDED = "rec_bls";

	private String viewStgLoc;

	private String projectVob;

	private String vws;

	private String stream;

	private String whatToBuild = BUILD_LATEST;

	/**
	 * Config to detect modifications. This is used to dertermine whether or not need to
	 * perform next build. This property will not take effect when {@link whatToBuild}
	 * is not of value {@link this#BUILD_LATEST}
	 */
	private String modificationDetectionConfig;

	private String mkviewExtraOpts;

    /**
     * Gets the corresponding VCS adaptor class name.
     *
     * @return the VCS adaptor class name
     */
	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.UCMClearcaseAdaptor";
	}

	/**
	 * Gets the Clearcase server-side view storage location. This is a project level property.
	 * 
	 * @return the Clearcase server-side view storage location
	 */
	public String getViewStgLoc() {
		return viewStgLoc;
	}

	/**
	 * Sets the Clearcase server-side view storage location. This is a project level property.
	 * 
	 * @param viewStgLoc the Clearcase server-side view storage location
	 */
	public void setViewStgLoc(String viewStgLoc) {
		this.viewStgLoc = viewStgLoc;
	}

	/**
	 * Gets the project vob tag. This is a project level property.
	 * 
	 * @return the project vob tag
	 */
	public String getProjectVob() {
		return projectVob;
	}

	/**
	 * Sets the project vob tag. This is a project level property.
	 * 
	 * @param projectVob the project vob tag
	 */
	public void setProjectVob(String projectVob) {
		this.projectVob = projectVob;
	}

	/**
	 * Gets the path for view storage. This is a view level property.
	 * 
	 * @return the path for view storage
	 */
	public String getVws() {
		return vws;
	}

	/**
	 * Sets the path for view storage. This is a view level property.
	 * 
	 * @param vws the path for view storage
	 */
	public void setVws(String vws) {
		this.vws = vws;
	}

	/**
	 * Gets the UCM stream. This is a view level property.
	 * 
	 * @return the UCM stream
	 */
	public String getStream() {
		return stream;
	}

	/**
	 * Sets the UCM stream. This is a view level property.
	 * 
	 * @param stream the UCM stream
	 */
	public void setStream(String stream) {
		this.stream = stream;
	}

	/**
	 * Gets the baselines to build in the stream. This is a view level property.
	 * 
	 * @return the baselines to build
	 */
	public String getWhatToBuild() {
		return whatToBuild;
	}

	/**
	 * Sets the baselines to build in the stream. This is a view level property.
	 * 
	 * @param whatToBuild the baselines to build
	 */
	public void setWhatToBuild(String whatToBuild) {
		this.whatToBuild = whatToBuild;
	}

	/**
	 * Gets the modification detection config. This is a view level property.
	 * 
	 * @return the modification detection config
	 */
	public String getModificationDetectionConfig() {
		return modificationDetectionConfig;
	}

	/**
	 * Sets the modification detection config. This is a view level property.
	 * 
	 * @param modificationDetectionConfig the modification detection config
	 */
	public void setModificationDetectionConfig(String modificationDetectionConfig) {
		this.modificationDetectionConfig = modificationDetectionConfig;
	}

	/**
	 * Gets the extra options when creating snapshot view. This is a view level property.
	 * 
	 * @return the extra options
	 */
	public String getMkviewExtraOpts() {
		return mkviewExtraOpts;
	}

	/**
	 * Sets the extra options when creating snapshot view. This is a view level property.
	 * 
	 * @param mkviewExtraOpts the extra options
	 */
	public void setMkviewExtraOpts(String mkviewExtraOpts) {
		this.mkviewExtraOpts = mkviewExtraOpts;
	}
}
