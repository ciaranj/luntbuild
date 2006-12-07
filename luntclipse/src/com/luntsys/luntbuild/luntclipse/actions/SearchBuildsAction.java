package com.luntsys.luntbuild.luntclipse.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;

import com.luntsys.luntbuild.facades.ILuntbuild;
import com.luntsys.luntbuild.facades.SearchCriteria;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.Build;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;

/**
 * This action searches previous builds.
 * @author 	 Lubos Pochman
 * @version  $Revision: 432 $
 * @since 	 1.3
 */
public class SearchBuildsAction extends Action {

    /**
     */
    public SearchBuildsAction() {
        super();
        setText("Search builds...");
        setToolTipText("Search previous builds for selected last build on Luntbuild server.");
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/searchbuilds.gif")));
        setDisabledImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/searchbuilds-disabled.gif")));
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        LuntbuildViewer viewer = LuntbuildView.currentViewer;
        if (viewer == null) return;

        LuntbuildConnection con = viewer.getConnection();
        Build currentBuild = viewer.getSelectedBuild();
        if (currentBuild == null) return;

        SearchBuildsDialog dlg =
            new SearchBuildsDialog(viewer.getShell(), con, currentBuild);
        int rc = dlg.open();
        if (rc == SWT.OK) {
            SearchCriteria criteria = dlg.getSearchCriteria();
            viewer.getBuildsProvider().setCriteria(criteria);
            viewer.refresh(true);
            viewer.setTabSelection(LuntbuildViewer.BUILDS_INDEX);
        }
    }

}
