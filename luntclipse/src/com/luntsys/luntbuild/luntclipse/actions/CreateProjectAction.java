package com.luntsys.luntbuild.luntclipse.actions;

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
public class CreateProjectAction extends Action {

    /**
     */
    public CreateProjectAction() {
        super();
        this.setText("New Project...");
        this.setToolTipText("Create new project.");
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().getBundle().
                getEntry("images/createproject.gif")));
        setDisabledImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/createproject-disabled.gif")));
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
                    "Create Project",
                    "Luntbuild version less than " + LuntclipseConstants.LUNTBUILD_VERSION_13 +
                    " cannot create project remotely!");
            return;
        }
        if (!con.canCreateProject()) {
            MessageDialog.openWarning(
                    viewer.getShell(),
                    "Create Project",
                    "User " + con.getConnectionData().getUser() + " cannot create project!");
            return;
        }
        EditProjectWizard wizard = new EditProjectWizard(con);
        WizardDialog dlg = new WizardDialog(viewer.getShell(), wizard);
        dlg.open();
        viewer.refresh();
    }

}
