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

package com.luntsys.luntbuild.facades.lb12;

/**
 * Base Clearcase VCS adaptor facade.  This adaptor does not support modules.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.BaseClearcaseAdaptor
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

    /**
     * @inheritDoc
	 */
	public String getVcsClassName() {
		return "com.luntsys.luntbuild.vcs.BaseClearcaseAdaptor";
	}

    /**
     * Gets the format params for the cleartool lshistory command.
     * Please see the clearcase man pages on fmt_ccase for more information.
     * 
     * @return the format params
     */
	public String getFormatParams() {
        return formatParams;
    }

    /**
     * Sets the format params for the cleartool lshistory command.
     * Please see the clearcase man pages on fmt_ccase for more information.
     * 
     * @param formatParams the format params
     */
    public void setFormatParams(String formatParams) {
        this.formatParams = formatParams;
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
	 * Sets the path for view storage.
	 * 
	 * @param vws the path for view storage
	 */
	public void setVws(String vws) {
		this.vws = vws;
	}

	/**
	 * Gets the snapshot view config spec.
	 * 
	 * @return the snapshot view config spec
	 */
	public String getViewCfgSpec() {
		return viewCfgSpec;
	}

	/**
	 * Sets the snapshot view config spec.
	 * 
	 * @param viewCfgSpec the snapshot view config spec
	 */
	public void setViewCfgSpec(String viewCfgSpec) {
		this.viewCfgSpec = viewCfgSpec;
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
	 * Sets the modification detection config.
	 * 
	 * @param modificationDetectionConfig the modification detection config
	 */
	public void setModificationDetectionConfig(String modificationDetectionConfig) {
		this.modificationDetectionConfig = modificationDetectionConfig;
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
	 * Sets the extra options when creating snapshot view.
	 * 
	 * @param mkviewExtraOpts the extra options
	 */
	public void setMkviewExtraOpts(String mkviewExtraOpts) {
		this.mkviewExtraOpts = mkviewExtraOpts;
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
     * Gets the view tag.
     * 
     * @return the view tag
     */
    public String getViewTag() {
        return viewTag;
    }

    /**
     * Sets the view tag.
     * 
     * @param viewTag the view tag
     */
    public void setViewTag(String viewTag) {
        this.viewTag = viewTag;
    }
}
