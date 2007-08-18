/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */

package com.luntsys.luntbuild.remoting.facade;

import com.luntsys.luntbuild.vcs.AccurevAdaptor;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;

/**
 * AccuRev VCS adaptor facade.
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class AccurevAdaptorFacade extends VcsFacade {
    /**
     * Gets the corresponding VCS adaptor class name.
     *
     * @return the VCS adaptor class name
     */
    public String getVcsClassName() {
        return AccurevAdaptor.class.getName();
    }
}
