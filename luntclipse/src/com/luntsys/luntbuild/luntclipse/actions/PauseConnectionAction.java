package com.luntsys.luntbuild.luntclipse.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.preferences.PreferenceHelper;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;

/**
 * @author lubosp
 *
 */
public class PauseConnectionAction extends Action {

    /**
     */
    public PauseConnectionAction(){
    	setIcon(false);
    }

    public void run() {
        LuntbuildViewer viewer = LuntbuildView.currentViewer;
        if (viewer == null) {
        	setIcon(false);
        	return;
        }

        boolean isPaused = viewer.getConnection().getConnectionData().isPaused();
        viewer.getConnection().getConnectionData().setPaused(!isPaused);
        setIcon(viewer.getConnection().getConnectionData().isPaused());
        PreferenceHelper.saveConnection(viewer.getConnection().getConnectionData());
    }

    public void setIcon(boolean isPaused) {
    	if (isPaused) {
            setText("Resume Connection");
            setToolTipText("Resume connection data refresh");
            setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                    getBundle().getEntry("images/resume.gif")));
    	} else {
            setText("Pause Connection");
            setToolTipText("Pause connection data refresh");
            setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                    getBundle().getEntry("images/pause.gif")));
    	}
    }
}
