package com.luntsys.luntbuild.luntclipse.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.luntsys.luntbuild.facades.ILuntbuild;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;

/**
 * Delete project
 *
 * @author Lubos Pochman
 *
 */
public class DeleteProjectAction extends Action {

    /**
     */
    public DeleteProjectAction() {
        super();
        this.setText("Delete Project...");
        this.setToolTipText("Delete selected project.");
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().getBundle().
                getEntry("images/deleteproject.gif")));
        setDisabledImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/deleteproject-disabled.gif")));
    }

    public void run() {
        LuntbuildViewer viewer = LuntbuildView.currentViewer;
        if (viewer == null) return;

        LuntbuildConnection con = viewer.getConnection();
        ILuntbuild luntbuild = con.getLuntbuild();
        if (luntbuild == null) {
            MessageDialog.openError(
                    viewer.getShell(),
                    "Luntbuild Connection",
                    "Unable to connect to Luntbuild: " + con.getConnectionData().getUrl());
            return;
        }
        // Confirm
        MessageBox mb =
            new MessageBox(viewer.getShell(),
                    SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
        mb.setText("Confirm Delete");
        mb.setMessage("Are you sure you want to delete project " + viewer.getSelectedProject() + "?");
        int rc = mb.open();
        if (rc == SWT.OK) {
            con.deleteProject(viewer.getSelectedProject());
            viewer.refresh(true);
        }
    }
}
