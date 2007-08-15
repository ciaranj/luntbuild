/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */

package com.luntsys.luntbuild.vcs.accurev;

/**
 * Accurev module interface.
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public interface AccurevModuleInterface {

    /**
     * Gets the depot name.
     * 
     * @return the depot name
     */
    String getDepot();

    /**
     * Sets the depot name.
     * 
     * @param depot the depot name
     */
    void setDepot(String depot);

    /**
     * Gets the backing stream.
     * 
     * @return the backing stream
     */
    String getBackingStream();

    /**
     * Sets the backing stream.
     * 
     * @param backingStream the backing stream
     */
    void setBackingStream(String backingStream);

    /**
     * Gets the backing stream for this build module.
     * 
     * @return the build stream
     */
    String getBuildStream();

    /**
     * Sets the backing stream for this build module.
     * 
     * @param buildStream the build stream
     */
    void setBuildStream(String buildStream);

    /**
     * Gets the transaction number with which to sync.
     * 
     * @return the transaction number
     */
    String getLabel();

    /**
     * Sets the transaction number with which to sync.
     * 
     * @param label the transaction number
     */
    void setLabel(String label);

    /**
     * Gets the source path where this module should be put.
     * 
     * @return the source path
     */
    String getSrcPath();

    /**
     * Sets the source path where this module should be put.
     * 
     * @param srcPath the source path
     */
    void setSrcPath(String srcPath);
}
