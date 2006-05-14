package com.luntsys.luntbuild.luntclipse.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.model.BuildMessenger;

/**
 * Refreshes running build
 *
 * @author lubosp
 *
 */
public class BuildLogRefreshJob extends RefreshJob {
    Browser logHtmlViewer = null;
    TextViewer logTextViewer = null;
    BuildMessenger build = null;

    /**
     * @param build
     * @param viewer
     * @param connection
     * @param delay
     */
    public BuildLogRefreshJob(BuildMessenger build,
            TextViewer viewer, LuntbuildConnection connection, int delay) {
        super("Build log refresh", connection, delay);
        this.logTextViewer = viewer;
        this.build = build;
    }

    /**
     * @param build
     * @param viewer
     * @param connection
     * @param delay
     */
    public BuildLogRefreshJob(BuildMessenger build, Browser viewer,
            LuntbuildConnection connection, int delay) {
        super("Build log refresh", connection, delay);
        this.logHtmlViewer = viewer;
        this.build = build;
    }

    /**
     * @param monitor
     * @return status
     *
     */
    protected IStatus run(IProgressMonitor monitor) {
        // Load the data and refresh it
        if (!BuildLogRefreshJob.this.connection.isBuildRunning(BuildLogRefreshJob.this.build))
            return Status.OK_STATUS;

        String url = BuildLogRefreshJob.this.build.getBuildLogUrl();
        final String log;
        try {
            log = BuildLogRefreshJob.this.connection.openURL(url);
        } catch (Exception e) {
            LuntclipsePlugin.doLog(IStatus.ERROR, IStatus.OK, "Cannot connect to Lutbuild!", e);
            if (BuildLogRefreshJob.this.logHtmlViewer != null) {
                BuildLogRefreshJob.this.logHtmlViewer.
                setText("<html><body>Cannot connect to Lutbuild!</body></html>");
            } else if (BuildLogRefreshJob.this.logTextViewer != null) {
                BuildLogRefreshJob.this.logTextViewer.getTextWidget().
                setText("Cannot connect to Lutbuild!");
            }
            return Status.OK_STATUS;
        }
        final int numLines = Integer.MAX_VALUE;
        Display.getDefault().asyncExec(
                new Runnable() {
                    public void run(){
                        if (BuildLogRefreshJob.this.logHtmlViewer != null) {
                            BuildLogRefreshJob.this.logHtmlViewer.setText(log);
                            ScrollBar bar = BuildLogRefreshJob.this.logHtmlViewer.getParent().getVerticalBar();
                            if (bar != null) bar.setSelection(numLines);
                        } else if (BuildLogRefreshJob.this.logTextViewer != null) {
                            BuildLogRefreshJob.this.logTextViewer.getTextWidget().setText("");
                            BuildLogRefreshJob.this.logTextViewer.getTextWidget().append(log);
                            BuildLogRefreshJob.this.logTextViewer.setTopIndex(numLines);

                        }
                    }
                });
        if (!BuildLogRefreshJob.this.connection.isBuildRunning(BuildLogRefreshJob.this.build)) {
            cancel();
        }
        schedule(this.delay);
        return Status.OK_STATUS;
    }

}
