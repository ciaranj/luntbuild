/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.vcs.accurev;

/**
 * AccurevModuleInterface
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public interface AccurevModuleInterface {
    String getDepot();

    void setDepot(String depot);

    String getBackingStream();

    void setBackingStream(String backingStream);

    String getBuildStream();

    void setBuildStream(String referenceTree);

    String getLabel();

    void setLabel(String label);

    String getSrcPath();

    void setSrcPath(String srcPath);
}
