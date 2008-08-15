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
package com.luntsys.luntbuild.facades.lb20;

/**
 * Facade of ucm clearcase adaptor. This adaptor does not support modules definition
 * @author robin shine
 */
public class UCMClearcaseAdaptorFacade extends VcsFacade {
	public static final String BUILD_LATEST = "latest";
	public static final String BUILD_LATEST_BASELINES = "latest baselines";
	public static final String BUILD_RECOMMENDED_BASELINES = "recommended baselines";
	public static final String BUILD_FOUNDATION_BASELINES = "foundation baselines";

	public static final String BASELINE_FOUNDATION = "found_bls";
	public static final String BASELINE_LATEST = "latest_bls";
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

	private String cleartoolDir;

	/**
	 * Get view storage location. It will be used as -stgloc option when creates clearcase views
	 * @return view storage location
	 */
	public String getViewStgLoc() {
		return viewStgLoc;
	}

	/**
	 * Set view storage location.
	 * @param viewStgLoc
	 */
	public void setViewStgLoc(String viewStgLoc) {
		this.viewStgLoc = viewStgLoc;
	}

	/**
	 * Get the UCM project vob. This is a project level property.
	 * @return UCM project vob
	 */
	public String getProjectVob() {
		return projectVob;
	}

	/**
	 * Set UCM project vob
	 * @param projectVob
	 */
	public void setProjectVob(String projectVob) {
		this.projectVob = projectVob;
	}

	/**
	 * Get explicit path for view storage which will be used as the "-vws" option.
	 * This option can override the "-stgloc" option.
	 * @return explicit path for view storage
	 */
	public String getVws() {
		return vws;
	}

	/**
	 * Set explicit path for view storage.
	 * @param vws
	 */
	public void setVws(String vws) {
		this.vws = vws;
	}

	/**
	 * Get UCM stream.
	 * @return UCM stream
	 */
	public String getStream() {
		return stream;
	}

	/**
	 * Set UCM stream.
	 * @param stream
	 */
	public void setStream(String stream) {
		this.stream = stream;
	}

	/**
	 * Get what to build string. Refer to user manual for detailed information about this property.
	 * @return what to build property
	 */
	public String getWhatToBuild() {
		return whatToBuild;
	}

	/**
	 * Set what to build string.
	 * @param whatToBuild
	 */
	public void setWhatToBuild(String whatToBuild) {
		this.whatToBuild = whatToBuild;
	}

	/**
	 * Get modification detection config string. Refer to user manual for detailed information
	 * @return modification detection config string
	 */
	public String getModificationDetectionConfig() {
		return modificationDetectionConfig;
	}

	/**
	 * Set modification detection config string
	 * @param modificationDetectionConfig
	 */
	public void setModificationDetectionConfig(String modificationDetectionConfig) {
		this.modificationDetectionConfig = modificationDetectionConfig;
	}

	/**
	 * Get extra options for creating clearcase views.
	 * @return extra options for creating clearcase views
	 */
	public String getMkviewExtraOpts() {
		return mkviewExtraOpts;
	}

	/**
	 * Set extra options for creating clearcase views
	 * @param mkviewExtraOpts
	 */
	public void setMkviewExtraOpts(String mkviewExtraOpts) {
		this.mkviewExtraOpts = mkviewExtraOpts;
	}

	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.UCMClearcaseAdaptor";
	}

	public String getCleartoolDir() {
		return cleartoolDir;
	}

	public void setCleartoolDir(String cleartoolDir) {
		this.cleartoolDir = cleartoolDir;
	}
}
