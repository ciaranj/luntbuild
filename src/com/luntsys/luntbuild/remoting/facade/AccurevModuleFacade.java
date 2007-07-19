/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */

package com.luntsys.luntbuild.remoting.facade;

import com.luntsys.luntbuild.vcs.accurev.AccurevModuleInterface;
import com.luntsys.luntbuild.facades.lb12.ModuleFacade;

/**
 * AccuRev VCS module facade.
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class AccurevModuleFacade extends ModuleFacade implements AccurevModuleInterface {
    private String depot;
    private String srcPath;
    private String backingStream;
    private String buildStream;
    private String label;

    /**
     * Gets the AccuRev depot name.
     * 
     * @return the depot name
     */
    public String getDepot() {
        return depot;
    }

    /**
     * Sets the AccuRev depot name.
     * 
     * @param depot the depot name
     */
    public void setDepot(String depot) {
        this.depot = depot;
    }

    /**
     * Gets the source path where this module should be put.
     * 
     * @return the source path
     */
    public String getSrcPath() {
        return srcPath;
    }

    /**
     * Sets the source path where this module should be put.
     * 
     * @param srcPath the source path
     */
    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    /**
     * Gets the backing stream.
     * 
     * @return the backing stream
     */
    public String getBackingStream() {
        return backingStream;
    }

    /**
     * Sets the backing stream.
     * 
     * @param backingStream the backing stream
     */
    public void setBackingStream(String backingStream) {
        this.backingStream = backingStream;
    }

    /**
     * Gets the backing stream for this build module.
     * 
     * @return the build stream
     */
    public String getBuildStream() {
        return buildStream;
    }

    /**
     * Sets the backing stream for this build module.
     * 
     * @param buildStream the build stream
     */
    public void setBuildStream(String buildStream) {
        this.buildStream = buildStream;
    }

    /**
     * Gets the transaction number with which to sync.
     * 
     * @return the transaction number
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the transaction number with which to sync.
     * 
     * @param label the transaction number
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
