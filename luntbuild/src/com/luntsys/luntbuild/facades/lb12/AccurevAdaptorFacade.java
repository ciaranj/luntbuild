/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.facades.lb12;

/**
 * AccurevAdaptorFacade
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class AccurevAdaptorFacade extends VcsFacade {
    private String port;

	private String accurevDir;

    /**
     * Get the corresponding vcs adaptor class name
     *
     * @return vcs adaptor class name
     */
    public String getVcsClassName() {
        return "com.luntsys.luntbuild.vcs.AccurevAdaptor";
    }

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAccurevDir() {
		return accurevDir;
	}

	public void setAccurevDir(String accurevDir) {
		this.accurevDir = accurevDir;
	}
}
