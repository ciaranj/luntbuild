/* $Header$
 *
 * Copyright (c) 2004 - 2005 A.S.E.I. s.r.o.
 */
package com.luntsys.luntbuild.luntclipse.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;

/**
 * Implementation of action for display detail of build log.
 *
 * @author 	 Roman Pichlík
 * @version  $Revision: 432 $
 * @since 	 1.0
 */
public class BuildLogAction extends Action {

    /**
     */
    public BuildLogAction() {
        super();
        setText("Build log");
        setToolTipText("Show build log for selected build.");
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/buildlog.gif")));
        setDisabledImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/buildlog-disabled.gif")));
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        LuntbuildViewer viewer = LuntbuildView.currentViewer;
        if (viewer == null) return;

        viewer.displayBuildLog();
    }
}
