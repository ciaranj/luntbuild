/* $Header$
 *
 * Copyright (c) 2004 - 2005 A.S.E.I. s.r.o.
 */
package com.luntsys.luntbuild.luntclipse.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;

/**
 * This repetitive job refreshs luntbuild view.
 *
 * @author 	 Roman Pichlík
 * @version  $Revision: 432 $
 * @since 	 0.0.1
 */
public class ProjectsRefreshJob extends RefreshJob {
    TreeViewer viewer;

    /**
     * @param viewer
     * @param connection
     * @param delay
     */
    public ProjectsRefreshJob(TreeViewer viewer, LuntbuildConnection connection, int delay) {
        super("Luntbuild refresh", connection, delay);
        this.viewer = viewer;
    }

    protected  synchronized IStatus run(IProgressMonitor monitor) {
        // Load the data and refresh it
        Thread th = new Thread(
            new Runnable() {
                public void run(){
                    ProjectsRefreshJob.this.connection.loadBuildData();
                    Display.getDefault().asyncExec(
                            new Runnable() {
                                public void run(){
                                    ProjectsRefreshJob.this.viewer.refresh();
                                    LuntbuildView.mainView.enableActionButtons();
                                }
                            });
                }
             });
        th.setPriority(Thread.MIN_PRIORITY);
        th.start();
        schedule(this.delay);
        return Status.OK_STATUS;
    }

}
