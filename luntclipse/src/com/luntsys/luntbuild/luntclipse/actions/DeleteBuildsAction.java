package com.luntsys.luntbuild.luntclipse.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.Build;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;

/**
 * This action deletes selected builds.
 * @author 	 Lubos Pochman
 * @version  $Revision: 432 $
 * @since 	 1.3
 */
public class DeleteBuildsAction extends Action {

    /**
     */
    public DeleteBuildsAction() {
        super();
        setText("Delete builds...");
        setToolTipText("Delete selected builds.");
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/deletebuilds.gif")));
        setDisabledImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/deletebuilds-disabled.gif")));
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        LuntbuildViewer viewer = LuntbuildView.currentViewer;
        if (viewer == null) return;

        LuntbuildConnection con = viewer.getConnection();
        if (con.getVersion() <=
            LuntclipseConstants.getVersion(LuntclipseConstants.LUNTBUILD_VERSION_122)) {
            MessageDialog.openWarning(
                    viewer.getShell(),
                    "Not Supported",
                    "Delete build(s) not supported for Luntbuild version less than 1.3!");
            return;
        }

        List selectedBuilds = viewer.getSelectedHistoryBuilds();
        if (selectedBuilds == null) return;

        // Confirm
        MessageBox mb =
            new MessageBox(viewer.getShell(),
                    SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
        mb.setText("Confirm Delete");
        mb.setMessage("Are you sure you want to delete selected builds?");
        int rc = mb.open();
        if (rc == SWT.OK) {
              for (Iterator iter = selectedBuilds.iterator(); iter.hasNext();) {
                  Build build = (Build) iter.next();
                  con.deleteBuild(build.getFacade());
              }
              viewer.refresh(true);
        }
    }

}
