package com.luntsys.luntbuild.luntclipse;

import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;

/**
 * The main Luntclipseplugin class.
 *
 *
 * @author 	 Roman Pichlík
 * @version  $Revision: 432 $
 * @since 	 0.0.1
 *
 */
public class LuntclipsePlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.luntsys.luntbuild.luntclipse";

	//The shared instance.
	private static LuntclipsePlugin plugin;

    private static List projectClipboard = null;

	/**
	 * The constructor.
	 */
	public LuntclipsePlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
    }

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
        LuntbuildView.mainView.stopRefreshJob();
		plugin = null;
	}

	/**
	 * Returns the shared instance.
     * @return the shared instance.
	 */
	public static LuntclipsePlugin getDefault() {
		return plugin;
	}

    /** Log error
     * @param severity severity
     * @param status status
     * @param message message
     * @param exception exception
     */
    public static void doLog(int severity, int status, String message, Throwable exception){
        ILog pluginLog = getDefault().getLog();
        IStatus s = new Status(severity, PLUGIN_ID, status, message, exception);
        pluginLog.log(s);
    }

    /**
     * Initialize image registry with high-use images which need to be shared.
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        ImageDescriptor id = ImageDescriptor.createFromURL(getBundle().getEntry("images/build.gif"));
        reg.put(LuntclipseConstants.BUILD_IMG, id);
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/failed.gif"));
        reg.put(LuntclipseConstants.FAILED_IMG, id);
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/success.gif"));
        reg.put(LuntclipseConstants.SUCCESS_IMG, id);
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/running.gif"));
        reg.put(LuntclipseConstants.RUNNING_IMG, id);
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/createproject.gif"));
        reg.put(LuntclipseConstants.PROJECT_IMG, id);
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/create.gif"));
        reg.put(LuntclipseConstants.CREATE_IMG, id);
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/modify.gif"));
        reg.put(LuntclipseConstants.MODIFY_IMG, id);
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/delete.gif"));
        reg.put(LuntclipseConstants.DELETE_IMG, id);
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/deselect.gif"));
        reg.put(LuntclipseConstants.DESELECT_IMG, id);
    }

    /**
     * @return Returns the project clipboard.
     */
    public static final List getProjectClipboard() {
        return projectClipboard;
    }

    /**
     * @param projectData The project data to set.
     */
    public static final void setBuildClipboard(List projectData) {
        LuntclipsePlugin.projectClipboard = projectData;
    }
}
