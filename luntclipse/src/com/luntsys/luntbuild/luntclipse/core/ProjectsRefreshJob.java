package com.luntsys.luntbuild.luntclipse.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;

/**
 * This repetitive job refreshs luntbuild view.
 *
 * @author 	 Roman Pichlík
 * @version  $Revision: 432 $
 * @since 	 0.0.1
 */
public class ProjectsRefreshJob extends RefreshJob {

    /**
     * @param delay
     */
    public ProjectsRefreshJob(int delay) {
        super("Luntbuild refresh", delay);
    }

    protected  synchronized IStatus run(IProgressMonitor monitor) {
        // Load the data and refresh it
        Thread th = new Thread(
            new Runnable() {
                public void run(){
                	for (LuntbuildConnection connection : LuntclipsePlugin.getDefault().getConnections()) {
                		connection.loadBuildData();
                		Display.getDefault().asyncExec(new UpdateView(connection));
                	}
                }
             });
        th.setPriority(Thread.MIN_PRIORITY);
        th.start();
        schedule(this.delay);
        return Status.OK_STATUS;
    }

    class UpdateView implements Runnable {
    	private final LuntbuildConnection connection;

    	public UpdateView(LuntbuildConnection connection) {
    		this.connection = connection;
    	}

		public void run() {
			LuntbuildViewer viewer = this.connection.getViewer();
			if (viewer != null) {
				viewer.refresh(false);
			} else
				LuntclipsePlugin.getDefault().updateNotifier(this.connection);
			if (LuntbuildView.mainView != null) LuntbuildView.mainView.enableActionButtons();
		}
    }
}
