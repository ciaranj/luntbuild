/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */

package com.luntsys.luntbuild.facades.lb12;

/**
 * AccuRev VCS adaptor facade.
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 * @see com.luntsys.luntbuild.vcs.AccurevAdaptor
 */
public class AccurevAdaptorFacade extends VcsFacade {
    private String user;
	private String password;

    /**
     * Gets the corresponding VCS adaptor class name.
     *
     * @return the VCS adaptor class name
     */
    public String getVcsClassName() {
        return "com.luntsys.luntbuild.vcs.AccurevAdaptor";
    }

	/**
	 * Gets the password to login to the AccuRev server.
	 * 
	 * @return the password
	 */
    public final String getPassword() {
        return this.password;
    }

	/**
	 * Sets the password to login to the AccuRev server.
	 * 
	 * @param password the password
	 */
    public final void setPassword(String password) {
        this.password = password;
    }

	/**
	 * Gets the user to login to the AccuRev server.
	 * 
	 * @return the user
	 */
    public final String getUser() {
        return this.user;
    }

	/**
	 * Sets the user to login to the AccuRev server.
	 * 
	 * @param user the user
	 */
    public final void setUser(String user) {
        this.user = user;
    }
}
