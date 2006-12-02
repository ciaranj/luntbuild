package com.luntsys.luntbuild.luntclipse.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * This job restarts refresh job, which permanently refreshes Luntclipse projects or build view.
 * @author 	 Roman Pichlík
 * @version  $Revision: 432 $
 * @since 	 0.0.2
 */
public class RefreshJobRestarter extends Job {
    private RefreshJob refreshJob;
    private int newDelay;

    /**
     * Default constructor
     * @param refreshJob refresh job
     * @param newDelay new time (in seconds) delay for refresh job
     */
    public RefreshJobRestarter(RefreshJob refreshJob, int newDelay){
        super("Luntclipse refresh job restarting");
        this.refreshJob = refreshJob;
        this.newDelay = newDelay;
    }

    /**
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IStatus run(IProgressMonitor monitor) {
        this.refreshJob.cancel();
        this.refreshJob.setDelay(this.newDelay);
        this.refreshJob.schedule();
        return Status.OK_STATUS;
    }

}
