package com.luntsys.luntbuild.luntclipse.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;

import com.luntsys.luntbuild.facades.ILuntbuild;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.BasicProjectData;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;

/**
 * Paste project
 *
 * @author Lubos Pochman
 *
 */
public class PasteProjectAction extends Action {

    /**
     */
    public PasteProjectAction() {
        super();
        this.setText("Paste Project...");
        this.setToolTipText("Paste a previously copied project.");
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().getBundle().
                getEntry("images/pasteproject.gif")));
        setDisabledImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/pasteproject-disabled.gif")));
    }

    public void run() {
        LuntbuildViewer viewer = LuntbuildView.currentViewer;
        if (viewer == null) return;

        LuntbuildConnection con = viewer.getConnection();
        List project = LuntclipsePlugin.getProjectClipboard();
        if (project == null) {
            MessageDialog.openWarning(
                    viewer.getShell(),
                    "Paste Project",
                    "Project copy not available, copy project first!");
            return;
        }
        BasicProjectData basicData = (BasicProjectData)project.get(0);
        if (con.projectExist(basicData.getName())) {
            MessageDialog.openWarning(
                    viewer.getShell(),
                    "Paste Project",
                    "Project \"" + basicData.getName() + "\" already exist!");
            return;
        }
        con.createProject(project);
        viewer.refresh(true);
    }
}
