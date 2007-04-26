/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 16:03:58
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
 * Facade of base clearcase adaptor. This adaptor does not support modules definition
 * @author robin shine
 */
public class BaseClearcaseAdaptorFacade extends VcsFacade {
	private String viewStgLoc;

	private String vws;

	private String viewCfgSpec;

	private String modificationDetectionConfig;

	private String mkviewExtraOpts;

	private String cleartoolDir;
    
    private String formatParams;

    private String viewTag;
    
	public String getFormatParams() {
        return formatParams;
    }

    public void setFormatParams(String formatParams) {
        this.formatParams = formatParams;
    }

    /**
	 * Get view storage location.This is a project level vcs property. It will be used as -stgloc option
	 * when creating clearcase views.
	 * @return view storage location
	 */
	public String getViewStgLoc() {
		return viewStgLoc;
	}

	/**
	 * Set the view storage location. This is a project level vcs property.
	 * @param viewStgLoc
	 */
	public void setViewStgLoc(String viewStgLoc) {
		this.viewStgLoc = viewStgLoc;
	}

	/**
	 * Get explicit path for view storage. This is a view level vcs property. It will be used as
	 * -vws option when creating clearcase views. It can override the -stgloc option
	 * @see BaseClearcaseAdaptorFacade#getViewStgLoc()
	 * @return explicit path for view storage
	 */
	public String getVws() {
		return vws;
	}

	/**
	 * Set the explicit path for view storage.
	 * @param vws
	 */
	public void setVws(String vws) {
		this.vws = vws;
	}

	/**
	 * Get the config spec.
	 * @return view config spec
	 */
	public String getViewCfgSpec() {
		return viewCfgSpec;
	}

	/**
	 * Set the config spec.
	 * @param viewCfgSpec
	 */
	public void setViewCfgSpec(String viewCfgSpec) {
		this.viewCfgSpec = viewCfgSpec;
	}

	/**
	 * Get modification detection config..
	 * Refer to luntbuild user manual for detailed explanation of this property
	 * @return modification detection config
	 */
	public String getModificationDetectionConfig() {
		return modificationDetectionConfig;
	}

	/**
	 * Set the modification detection config.
	 * @param modificationDetectionConfig
	 */
	public void setModificationDetectionConfig(String modificationDetectionConfig) {
		this.modificationDetectionConfig = modificationDetectionConfig;
	}

	/**
	 * Get extra options when call clearcase mkview sub-command.
	 * Refer to luntbuild user manual for detailed explanation of this property
	 * @return mkview extra options
	 */
	public String getMkviewExtraOpts() {
		return mkviewExtraOpts;
	}

	/**
	 * Set extra options when call clearcase mkview sub-command.
	 * @param mkviewExtraOpts
	 */
	public void setMkviewExtraOpts(String mkviewExtraOpts) {
		this.mkviewExtraOpts = mkviewExtraOpts;
	}

	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.BaseClearcaseAdaptor";
	}

	public String getCleartoolDir() {
		return cleartoolDir;
	}

	public void setCleartoolDir(String cleartoolDir) {
		this.cleartoolDir = cleartoolDir;
	}

    public String getViewTag() {
        return viewTag;
    }

    public void setViewTag(String viewTag) {
        this.viewTag = viewTag;
    }
}
