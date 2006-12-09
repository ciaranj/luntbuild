package com.luntsys.luntbuild.luntclipse.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;

/**
 * Copy project to clipboard
 *
 * @author Lubos Pochman
 *
 */
public class CopyProjectAction extends Action {

    /**
     */
    public CopyProjectAction() {
        super();
        this.setText("Copy Project...");
        this.setToolTipText("Copy selected project.");
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().getBundle().
                getEntry("images/copyproject.gif")));
        setDisabledImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/copyproject-disabled.gif")));
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        LuntbuildViewer viewer = LuntbuildView.currentViewer;
        if (viewer == null) return;

        LuntbuildConnection con = viewer.getConnection();
        con.copyProject(viewer.getSelectedProject());
        LuntbuildView.mainView.enableActionButtons();
    }
}
