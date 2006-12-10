/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.remoting.facade;

import com.luntsys.luntbuild.vcs.AccurevAdaptor;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;

/**
 * AccurevAdaptorFacade
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class AccurevAdaptorFacade  extends VcsFacade {
    /**
     * Get the corresponding vcs adaptor class name
     *
     * @return vcs adaptor class name
     */
    public String getVcsClassName() {
        return AccurevAdaptor.class.getName();
    }
}
