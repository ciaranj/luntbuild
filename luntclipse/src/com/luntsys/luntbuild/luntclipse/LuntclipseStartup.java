/*
 * Created on Dec 2, 2006
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.luntsys.luntbuild.luntclipse;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


public class LuntclipseStartup implements IStartup {

	public void earlyStartup() {
        if (!LuntclipsePlugin.getDefault().alwaysRunNotifier()) return;
        // Create tray notifier, start refresh job
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
          public void run() {
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            if (window != null) {
            	LuntclipsePlugin.getDefault().createTray();
            	LuntclipsePlugin.getDefault().startRefreshJob();
            	LuntclipsePlugin.getDefault().createTip(window.getShell());
            	LuntclipsePlugin.getDefault().createTrayMenu(window.getShell());
            }
          }
        });
	}

}
