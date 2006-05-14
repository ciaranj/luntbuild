/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.remoting.facade;

import com.luntsys.luntbuild.vcs.accurev.AccurevModuleInterface;
import com.luntsys.luntbuild.facades.lb12.ModuleFacade;

/**
 * AccurevModuleFacade
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class AccurevModuleFacade  extends ModuleFacade implements AccurevModuleInterface {
    private String depot;
    private String srcPath;
    private String backingStream;
    private String buildStream;
    private String label;

    public String getDepot() {
        return depot;
    }

    public void setDepot(String depot) {
        this.depot = depot;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getBackingStream() {
        return backingStream;
    }

    public void setBackingStream(String backingStream) {
        this.backingStream = backingStream;
    }

    public String getBuildStream() {
        return buildStream;
    }

    public void setBuildStream(String buildStream) {
        this.buildStream = buildStream;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
