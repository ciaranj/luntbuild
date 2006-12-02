package com.luntsys.luntbuild.luntclipse.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

/**
 * RefreshJob basic class
 *
 * @author lubosp
 *
 */
public abstract class RefreshJob extends Job {
    protected int delay = 0;
    protected LuntbuildConnection connection = null;

    /**
     * @param name
     * @param connection
     * @param delay
     */
    public RefreshJob(String name, LuntbuildConnection connection, int delay) {
        super(name);
        this.delay = delay;
        this.connection = connection;
    }

    /**
     * @param monitor
     * @return
     *
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    abstract protected IStatus run(IProgressMonitor monitor);

    /**
     * @param delay
     */
    public synchronized void setDelay(int delay) {
        this.delay = delay;
    }

}
