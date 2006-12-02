package com.luntsys.luntbuild.luntclipse.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewSite;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.preferences.ManageConnectionDialog;

/**
 * Display ManageConnection dialog
 *
 * @author Lubos Pochman
 *
 */
public class ManageConnectionAction extends Action {
    private IViewSite site = null;

    /**
     * @param site site
     */
    public ManageConnectionAction(IViewSite site) {
        super();
        this.site = site;
        setText("Connections...");
        setToolTipText("Add or remove Luntbuild connection.");
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/connect.jpg")));
        setDisabledImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/connect-disabled.gif")));
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        ManageConnectionDialog dlg = new ManageConnectionDialog(this.site.getShell(), this.site);
        dlg.open();
    }
}
