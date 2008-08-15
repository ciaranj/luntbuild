/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.facades.lb20;

/**
 * AccurevAdaptorFacade
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class AccurevAdaptorFacade extends VcsFacade {
    private String user;
	private String password;

    /**
     * Get the corresponding vcs adaptor class name
     *
     * @return vcs adaptor class name
     */
    public String getVcsClassName() {
        return "com.luntsys.luntbuild.vcs.AccurevAdaptor";
    }

    /**
     * @return Returns the password.
     */
    public final String getPassword() {
        return this.password;
    }

    /**
     * @param password The password to set.
     */
    public final void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the user.
     */
    public final String getUser() {
        return this.user;
    }

    /**
     * @param user The user to set.
     */
    public final void setUser(String user) {
        this.user = user;
    }

}
