package com.luntsys.luntbuild.luntclipse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.core.NotificationMessage;
import com.luntsys.luntbuild.luntclipse.core.ProjectsRefreshJob;
import com.luntsys.luntbuild.luntclipse.core.RefreshJob;
import com.luntsys.luntbuild.luntclipse.core.RefreshJobRestarter;
import com.luntsys.luntbuild.luntclipse.model.Build;
import com.luntsys.luntbuild.luntclipse.model.ConnectionData;
import com.luntsys.luntbuild.luntclipse.preferences.PreferenceHelper;

/**
 * The main Luntclipse plugin class.
 *
 *
 * @author 	 Roman Pichlík
 * @version  $Revision: 432 $
 * @since 	 0.0.1
 *
 */
public class LuntclipsePlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.luntsys.luntbuild.luntclipse";

	/** Notification Tray handling */
    private Tray tray = null;
    private TrayItem trayItem = null;
    private Menu trayMenu;
    private ToolTip tip = null;

    public Image trayImage = null;
    public Image successImage = null;
    public Image runningImage = null;
    public Image failedImage = null;

    /** Restart preferences */
    private boolean alwaysRunNotifier = false;
    private boolean useNotifyTray = true;

    /** Quickbuild Connections */
    private ArrayList<LuntbuildConnection> connections = new ArrayList<LuntbuildConnection>();
    /** Currently selected connection */
    private LuntbuildConnection selectedConnection = null;

    /** Build Refresh/Monitor job */
    private RefreshJob buildRefreshJob = null;

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

		// preferences
		this.alwaysRunNotifier = PreferenceHelper.alwaysRunNotifierTray();
		this.useNotifyTray = PreferenceHelper.useNotifyTray();

		// initialize connections
        ArrayList conArr = PreferenceHelper.getConnections();
        for (Iterator iter = conArr.iterator(); iter.hasNext();) {
            ConnectionData con = (ConnectionData) iter.next();
            this.connections.add(new LuntbuildConnection(con));
        }

        initImages();
    }

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
        stopRefresh();
		if (this.tray != null) this.tray.dispose();
		this.tray = null;
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
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/tray-icon.jpg"));
    }

    private void initImages() {
        ImageDescriptor id = ImageDescriptor.createFromURL(getBundle().getEntry("images/failed.gif"));
        this.failedImage = id.createImage(getWorkbench().getDisplay());
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/success.gif"));
        this.successImage = id.createImage(getWorkbench().getDisplay());
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/running.gif"));
        this.runningImage = id.createImage(getWorkbench().getDisplay());
        id = ImageDescriptor.createFromURL(getBundle().getEntry("images/tray-icon.jpg"));
        this.trayImage = id.createImage(getWorkbench().getDisplay());
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

    /**
     * Create Notify Tray
     */
    public void createTray() {
    	if (!LuntclipsePlugin.getDefault().useNotifyTray()) {
    		this.tray = null;
        	// Set the tooltip location manually.
    		return;
    	}
    	this.tray = getWorkbench().getDisplay().getSystemTray();
    	if (this.tray != null) {
	    	this.trayItem = new TrayItem(this.tray, SWT.NONE);
	    	this.trayItem.setImage(trayImage);
	    	this.trayItem.addListener (SWT.MenuDetect, new Listener () {
	    		public void handleEvent (Event event) {
	    			trayMenu.setVisible(true);
	    		}
	    	});
    	}
    }

    public void createTrayMenu(Shell shell) {
    	if (this.tray == null) return;
    	String selectedConfig = PreferenceHelper.getNotifyConfiguration();
    	this.trayMenu = new Menu(shell, SWT.MENU);

    	for (LuntbuildConnection con : this.connections) {
	    	MenuItem menuItem = new MenuItem(this.trayMenu, SWT.RADIO);
	    	String configName = con.getConnectionData().getName();
	    	menuItem.setText(configName);
	    	menuItem.setData(con);
	    	menuItem.addListener (SWT.Selection, new Listener () {
	    		public void handleEvent (Event e) {
	    			MenuItem item = (MenuItem)e.widget;
	    			if (item == null) return;
	    			selectedConnection = (LuntbuildConnection)item.getData();
			    	String configName = selectedConnection.getConnectionData().getName();
			    	PreferenceHelper.setNotifyConfiguration(configName);
			    	setTrayIcon(selectedConnection);
	    		}
	    	});
	    	if (selectedConfig.equals(configName)) {
	    		menuItem.setSelection(true);
	    		this.selectedConnection = con;
	    	}
		}
    	MenuItem menuItem = new MenuItem(this.trayMenu, SWT.RADIO);
    	menuItem.setText("None");
    	menuItem.addListener (SWT.Selection, new Listener () {
    		public void handleEvent (Event e) {
    			selectedConnection = null;
		    	PreferenceHelper.setNotifyConfiguration("None");
		    	setTrayIcon(null);
    		}
    	});
    	if (this.connections.size() == 0 ||selectedConfig.equals("None")) menuItem.setSelection(true);
    }

    public void createTip(Shell shell) {
    	if (this.tip != null) return;
		// Create notifier tooltip
        this.tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION | SWT.ON_TOP);
        this.tip.setMessage("Configuration build status.");
        this.tip.setText("Configuration Messages");
        this.tip.setAutoHide(true);
        if (this.tray != null) {
        	this.trayItem.setToolTip(this.tip);
        } else {
        	Rectangle rect = shell.getBounds();
        	this.tip.setLocation(rect.x + rect.width, rect.y + rect.height);
        }
    }

    /**
     * Set notification state
     * @param connection
     * @param messages
     */
    public void setNotificationState(LuntbuildConnection connection, List<NotificationMessage> messages) {
    	if (connection == this.selectedConnection) setTrayIcon(connection);
    	if (messages != null) setTooltipMessages(connection, messages);
    }

    private void setTrayIcon(LuntbuildConnection connection) {
    	if (this.tray != null) {
    		Build build = (connection == null) ? null : connection.getLastBuild();
    		if (build != null) {
    			if (build.getLastBuildStatus() == LuntclipseConstants.BUILD_SUCCESS)
    				this.trayItem.setImage(successImage);
    			else if (build.getLastBuildStatus() == LuntclipseConstants.BUILD_RUNNING)
    				this.trayItem.setImage(runningImage);
    			else if (build.getLastBuildStatus() == LuntclipseConstants.BUILD_FAILED)
    				this.trayItem.setImage(failedImage);
    			String msg = connection.getConnectionData().getName() + "\n";
    			msg += build.getVersion() + "-" + LuntclipseConstants.buildStatus[build.getLastBuildStatus()] + "\n";
		    	this.trayItem.setToolTipText(msg);
    		} else {
    			this.trayItem.setImage(trayImage);
		    	this.trayItem.setToolTipText("None");
    		}
    		this.trayItem.setVisible(true);
    	}
    }

    /**
     * Update notification (used when no views are displayed
     *
     * @param connection
     */
    public void updateNotifier(LuntbuildConnection connection) {
    	setNotificationState(connection, connection.getNewMessages());
    }

    public void setTooltipMessages(LuntbuildConnection connection, List<NotificationMessage> messages) {
    	if (messages.size() == 0) return;
    	// Title
        this.tip.setText(connection.getConnectionData().getName());
        StringBuilder buf = new StringBuilder();
        for (Iterator iter = messages.iterator(); iter.hasNext();) {
			NotificationMessage message = (NotificationMessage) iter.next();
			buf.append(message.getDate());
			buf.append(" - ");
			buf.append(message.getSeverity().toString());
			buf.append(": ");
			buf.append(message.getContent());
			buf.append("\n");
		}
        this.tip.setMessage(buf.toString());

    	// Play sound
    	AudioStream as = null;
    	try {
	    	as = new AudioStream(LuntclipsePlugin.getDefault().
	                getBundle().getEntry("sounds/notify.wav").openStream());
	    	AudioPlayer.player.start(as);
	    	Thread.sleep(1000);
	    	AudioPlayer.player.stop(as);
    	} catch (Exception e) {
			// ignore
		}
        this.tip.setVisible(true);

        showView();
    }

    private void showView() {
        try {
    	getWorkbench().getActiveWorkbenchWindow().
    		getActivePage().showView("com.luntsys.luntbuild.luntclipse.views.LuntbuildView");
        } catch (Exception e) {
			doLog(IStatus.ERROR, IStatus.OK, "Unable to show view", e);
		}
    }

    /**
     * Dispose tray
     */
    private void disposeTray() {
    	if (!this.alwaysRunNotifier() && this.tray != null) {
    		this.tray.dispose();
    		this.tray = null;
    	}
    }

    /**
     * Start build refresh job
     */
    public void startRefreshJob() {
        this.buildRefreshJob = new ProjectsRefreshJob(PreferenceHelper.getRefreshTime());
        this.buildRefreshJob.schedule();
    }

    /**
     * This method restarts job responsible for refreshing
     * Quiclipse view. If job is not running, it will be started.
     */
    public void restartRefreshJob(){
        if (this.buildRefreshJob.getState() == Job.NONE) {
            this.buildRefreshJob.setDelay(PreferenceHelper.getRefreshTime());
            this.buildRefreshJob.schedule();
        } else {
            new RefreshJobRestarter(this.buildRefreshJob, PreferenceHelper.getRefreshTime()).schedule();
        }
    }

    /**
     * Sets refresh job delay
     */
    public void setRefreshJobDelay() {
    	if (this.buildRefreshJob == null) return;
    	this.buildRefreshJob.setDelay(PreferenceHelper.getRefreshTime());
    }

    public void disposeView() {
    	if (!this.alwaysRunNotifier) {
    		stopRefresh();
    		disposeTray();
    	}
    	for (LuntbuildConnection con : this.connections) {
			con.setViewer(null);
		}
    }

    private void stopRefresh() {
    	if (this.buildRefreshJob == null) return;
        int count = 0;
    	while (!this.buildRefreshJob.cancel()) {
    		count++;
    		try {
    			Thread.sleep(1000);
    		} catch (Exception e) {
				// Ignore
			}
    		if (count > 60) {
    			doLog(IStatus.WARNING, IStatus.OK, "Unable to stop the build job!", null);
    		}
    	}
    }

	/**
	 * @return Returns the connections.
	 */
	public final ArrayList<LuntbuildConnection> getConnections() {
		return this.connections;
	}

	/**
	 * @return Returns the alwaysRunNotifier.
	 */
	public final boolean alwaysRunNotifier() {
		return alwaysRunNotifier;
	}

	/**
	 * @return Returns the useNotifyTray.
	 */
	public final boolean useNotifyTray() {
		return useNotifyTray;
	}

	/**
	 * Delete all connections
	 */
	public void deleteAllConnections() {
		this.connections = new ArrayList<LuntbuildConnection>();
	}

	/**
	 * Add new connection
	 * @param con
	 */
	public LuntbuildConnection addConnection(ConnectionData con) {
		LuntbuildConnection connection = new LuntbuildConnection(con);
        this.connections.add(connection);
        return connection;
	}

	/**
	 * Remove given connection
	 * @param con
	 */
	public void removeConnection(LuntbuildConnection con) {
		this.connections.remove(con);
	}
}
