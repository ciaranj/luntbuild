package com.luntsys.luntbuild.luntclipse.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

import com.luntsys.luntbuild.facades.ILuntbuild;
import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.BuildMessenger;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;

/**
 * This action moves selected builds.
 * @author 	 Lubos Pochman
 * @version  $Revision: 432 $
 * @since 	 1.3
 */
public class MoveBuildsAction extends Action {

    /**
     */
    public MoveBuildsAction() {
        super();
        setText("Move builds...");
        setToolTipText("Move selected builds to a specified schedule.");
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/movebuilds.gif")));
        setDisabledImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/movebuilds-disabled.gif")));
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        LuntbuildViewer viewer = LuntbuildView.currentViewer;
        if (viewer == null) return;

        LuntbuildConnection con = viewer.getConnection();
        ILuntbuild luntbuild = con.getLuntbuild();
        if (luntbuild == null) {
            MessageDialog.openWarning(
                    viewer.getShell(),
                    "Luntbuild Connection",
                    "Unable to connect to Luntbuild: " + con.getConnectionData().getUrl());
            return;
        }
        if (con.getVersion() <=
            LuntclipseConstants.getVersion(LuntclipseConstants.LUNTBUILD_VERSION_122)) {
            MessageDialog.openWarning(
                    viewer.getShell(),
                    "Not Supported",
                    "Move build not supported for Luntbuild version less than 1.3!");
            return;
        }

        List selectedBuilds = viewer.getSelectedHistoryBuilds();
        if (selectedBuilds == null) return;

        MoveBuildsDialog dlg =
            new MoveBuildsDialog(viewer.getShell(), con);
        int rc = dlg.open();
        if (rc == SWT.OK) {
            String projectName = dlg.getProjectName();
            String scheduleName = dlg.getScheduleName();
            for (Iterator iter = selectedBuilds.iterator(); iter.hasNext();) {
                BuildMessenger build = (BuildMessenger) iter.next();
                con.moveBuild(build.getFacade(), projectName, scheduleName);
            }
            viewer.refresh();
        }
    }

}
