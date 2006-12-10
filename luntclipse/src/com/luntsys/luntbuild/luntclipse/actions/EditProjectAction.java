package com.luntsys.luntbuild.luntclipse.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;
import com.luntsys.luntbuild.luntclipse.wizards.EditProjectWizard;


/**
 * Create a Project
 *
 * @author Lubos Pochman
 *
 */
public class EditProjectAction extends Action {

    /**
     */
    public EditProjectAction() {
        super();
        this.setText("Edit Project...");
        this.setToolTipText("Edit selected project.");
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().getBundle().
                getEntry("images/editproject.gif")));
        setDisabledImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/editproject-disabled.gif")));
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        LuntbuildViewer viewer = LuntbuildView.currentViewer;
        if (viewer == null) return;

        LuntbuildConnection con = viewer.getConnection();
        if (con.getVersion() <
                LuntclipseConstants.getVersion(LuntclipseConstants.LUNTBUILD_VERSION_13)) {
            MessageDialog.openWarning(
                    viewer.getShell(),
                    "Edit Project",
                    "Luntbuild version less than " + LuntclipseConstants.LUNTBUILD_VERSION_13 +
                    " cannot create project remotely!");
            return;
        }
        String projectName = viewer.getSelectedProject();
        if (projectName == null || projectName.trim().length() == 0) return;

        //load data for project
        List allData = con.getProjectData(projectName);
        EditProjectWizard wizard = new EditProjectWizard(con, allData);
        WizardDialog dlg = new WizardDialog(viewer.getShell(), wizard);
        dlg.open();
        viewer.refresh(true);
    }

}
