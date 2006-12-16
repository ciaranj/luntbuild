package com.luntsys.luntbuild.luntclipse.views;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.actions.BuildLogAction;
import com.luntsys.luntbuild.luntclipse.actions.CleanLogAction;
import com.luntsys.luntbuild.luntclipse.actions.CopyProjectAction;
import com.luntsys.luntbuild.luntclipse.actions.CreateProjectAction;
import com.luntsys.luntbuild.luntclipse.actions.DeleteBuildsAction;
import com.luntsys.luntbuild.luntclipse.actions.DeleteProjectAction;
import com.luntsys.luntbuild.luntclipse.actions.EditProjectAction;
import com.luntsys.luntbuild.luntclipse.actions.ManageConnectionAction;
import com.luntsys.luntbuild.luntclipse.actions.MoveBuildsAction;
import com.luntsys.luntbuild.luntclipse.actions.PasteProjectAction;
import com.luntsys.luntbuild.luntclipse.actions.PauseConnectionAction;
import com.luntsys.luntbuild.luntclipse.actions.RefreshAction;
import com.luntsys.luntbuild.luntclipse.actions.RevisionLogAction;
import com.luntsys.luntbuild.luntclipse.actions.SearchBuildsAction;
import com.luntsys.luntbuild.luntclipse.actions.SystemLogAction;
import com.luntsys.luntbuild.luntclipse.actions.TriggerBuildAction;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.ConnectionData;
import com.luntsys.luntbuild.luntclipse.preferences.PreferenceHelper;

/**
 * The main luntbuild view shows data obtained from remote invocation of
 * Luntbuild.
 *
 * @author 	 Roman Pichlík, Lubos Pochman
 * @since 	 0.0.1
 *
 */
public class LuntbuildView extends ViewPart implements SelectionListener {

    /** This view  */
	public Composite parent = null;
    public static LuntbuildView mainView = null;
    private static ArrayList<LuntbuildViewer> viewers = null;
    private CTabFolder m_folder = null;
    private Composite focusFolder = null;

    //actions
    private Action refreshAction = null;
    private BuildLogAction buildLogAction = null;
    private RevisionLogAction revisionLogAction = null;
    private SystemLogAction systemLogAction = null;
    private CleanLogAction cleanLogAction = null;
    private TriggerBuildAction triggerBuildAction = null;
    private SearchBuildsAction searchBuildsAction = null;
    private MoveBuildsAction moveBuildsAction = null;
    private DeleteBuildsAction deleteBuildsAction = null;
    private CreateProjectAction createProjectAction = null;
    private EditProjectAction editProjectAction = null;
    private DeleteProjectAction deleteProjectAction = null;
    private CopyProjectAction copyProjectAction = null;
    private PasteProjectAction pasteProjectAction = null;
    private ManageConnectionAction connectionAction = null;
    private PauseConnectionAction pauseConnectionAction = null;

    /** Current build viewer */
    public static LuntbuildViewer currentViewer = null;

	/**
	 * The constructor.
	 */
	public LuntbuildView() {
        viewers = new ArrayList<LuntbuildViewer>();
        mainView = this;
	}

	/**
	 * This is a callback that will allow us
	 * to create the buildViewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
    	this.parent = parent;
        this.m_folder = new CTabFolder(parent, SWT.NONE);
        this.focusFolder = m_folder;
        this.m_folder.addSelectionListener(this);

        ArrayList<LuntbuildConnection> connections = LuntclipsePlugin.getDefault().getConnections();
        for (LuntbuildConnection con : connections) {
            LuntbuildViewer viewer = createViewer(con);
            if (viewer != null) viewers.add(viewer);
        }

        // Set actions
        this.refreshAction = new RefreshAction();
        this.triggerBuildAction = new TriggerBuildAction();
        this.cleanLogAction = new CleanLogAction();
        this.searchBuildsAction = new SearchBuildsAction();
        this.moveBuildsAction = new MoveBuildsAction();
        this.deleteBuildsAction = new DeleteBuildsAction();
        this.createProjectAction = new CreateProjectAction();
        this.editProjectAction = new EditProjectAction();
        this.deleteProjectAction = new DeleteProjectAction();
        this.copyProjectAction = new CopyProjectAction();
        this.pasteProjectAction = new PasteProjectAction();
        this.connectionAction = new ManageConnectionAction(getViewSite());
        this.buildLogAction = new BuildLogAction();
        this.revisionLogAction = new RevisionLogAction();
        this.systemLogAction = new SystemLogAction();
        this.pauseConnectionAction = new PauseConnectionAction();

        contributeToActionBars();

        if (!LuntclipsePlugin.getDefault().alwaysRunNotifier()) {
        	LuntclipsePlugin.getDefault().createTray();
        	LuntclipsePlugin.getDefault().createTip(parent.getShell());
        	LuntclipsePlugin.getDefault().startRefreshJob();
        	LuntclipsePlugin.getDefault().createTrayMenu(parent.getShell());
        }
	}

    /**
     * Sent when selection occurs in the control.
     *
     * @param e an event containing information about the selection
     */
    public void widgetSelected(SelectionEvent e) {
        CTabItem item = (CTabItem)e.item;
        if (item == null) return;
        currentViewer = (LuntbuildViewer)item.getData();
        if (currentViewer == null) return;
        ((CTabFolder)currentViewer.getBuildViewer().
                getControl().getParent()).setSelection(LuntbuildViewer.PROJECTS_INDEX);
        enableActionButtons();
    }

    /**
     * Sent when default selection occurs in the control.
     *
     * @param e an event containing information about the default selection
     */
    public void widgetDefaultSelected(SelectionEvent e) {
    }

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * @param manager
	 */
	public void fillLocalPullDown(IMenuManager manager) {
        enableActionButtons();
        manager.add(this.connectionAction);
        manager.add(this.pauseConnectionAction);
        manager.add(this.refreshAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(this.triggerBuildAction);
        manager.add(this.searchBuildsAction);
        manager.add(this.moveBuildsAction);
        manager.add(this.deleteBuildsAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(this.buildLogAction);
        manager.add(this.revisionLogAction);
        manager.add(this.systemLogAction);
        manager.add(this.cleanLogAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(this.createProjectAction);
        manager.add(this.editProjectAction);
        manager.add(this.deleteProjectAction);
        manager.add(this.copyProjectAction);
        manager.add(this.pasteProjectAction);
	}

    /**
     * @param manager
     */
    public void fillBuildsPullDown(IMenuManager manager) {
        manager.add(this.moveBuildsAction);
        manager.add(this.deleteBuildsAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(this.buildLogAction);
        manager.add(this.revisionLogAction);
        manager.add(this.systemLogAction);
        manager.add(this.cleanLogAction);
    }

	private void fillLocalToolBar(IToolBarManager manager) {
        enableActionButtons();
        manager.add(this.connectionAction);
        manager.add(this.pauseConnectionAction);
        manager.add(this.refreshAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(this.triggerBuildAction);
        manager.add(this.searchBuildsAction);
        manager.add(this.moveBuildsAction);
        manager.add(this.deleteBuildsAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(this.buildLogAction);
        manager.add(this.revisionLogAction);
        manager.add(this.systemLogAction);
        manager.add(this.cleanLogAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(this.createProjectAction);
        manager.add(this.editProjectAction);
        manager.add(this.deleteProjectAction);
        manager.add(this.copyProjectAction);
        manager.add(this.pasteProjectAction);
	}

    /**
     * Enable/disable toolbar buttons
     */
    public void enableActionButtons() {
    	boolean enable = currentViewer != null &&
    		currentViewer.getSelectedBuild() != null &&
    		!currentViewer.getConnection().getConnectionData().isPaused();
    	this.pauseConnectionAction.setIcon(
    			(currentViewer != null)?currentViewer.getConnection().getConnectionData().isPaused():false);
    	this.refreshAction.setEnabled(
                currentViewer != null &&
                !currentViewer.getConnection().getConnectionData().isPaused() &&
    			LuntclipsePlugin.getDefault().getConnections().size() > 0);
        this.triggerBuildAction.setEnabled(enable);
        this.buildLogAction.setEnabled(enable);
        this.revisionLogAction.setEnabled(enable);
        this.systemLogAction.setEnabled(enable);
        this.cleanLogAction.setEnabled(enable);
        this.createProjectAction.setEnabled(
                currentViewer != null &&
                !currentViewer.getConnection().getConnectionData().isPaused() &&
        		LuntclipsePlugin.getDefault().getConnections().size() > 0);
        this.editProjectAction.setEnabled(enable);
        this.deleteProjectAction.setEnabled(enable);
        this.copyProjectAction.setEnabled(enable);
        this.pasteProjectAction.setEnabled(
                currentViewer != null &&
                !currentViewer.getConnection().getConnectionData().isPaused() &&
        		LuntclipsePlugin.getProjectClipboard() != null);
        this.searchBuildsAction.setEnabled(enable);
        this.moveBuildsAction.setEnabled(
                currentViewer != null &&
                !currentViewer.getConnection().getConnectionData().isPaused() &&
                currentViewer.getSelectedHistoryBuilds() != null &&
                currentViewer.getSelectedHistoryBuilds().size() > 0);
        this.deleteBuildsAction.setEnabled(
                currentViewer != null &&
                !currentViewer.getConnection().getConnectionData().isPaused() &&
                currentViewer.getSelectedHistoryBuilds() != null &&
                currentViewer.getSelectedHistoryBuilds().size() > 0);
    }

	/**
	 * Passing the focus request to the buildViewer's control.
	 */
	public void setFocus() {
    	this.parent.setFocus();
        this.focusFolder.setFocus();
	}

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    public void dispose() {
        try{
        	LuntclipsePlugin.getDefault().disposeView();
        }finally{
            super.dispose();
        }
    }

    /** Delete connection.
     * @param data connection data
     */
    public void deleteConnection(LuntbuildConnection data) {
        LuntbuildViewer view = findViewer(data.getConnectionData());
        if (view == null) return;
        PreferenceHelper.removeConnection(data.getConnectionData());
        view.remove();
        this.m_folder.redraw();
        viewers.remove(view);
        if (currentViewer == view) currentViewer = null;
        LuntclipsePlugin.getDefault().removeConnection(data);
        LuntclipsePlugin.getDefault().createTrayMenu(this.parent.getShell());
    }

    private LuntbuildViewer findViewer(ConnectionData data) {
        for (Iterator iter = viewers.iterator(); iter.hasNext();) {
        	LuntbuildViewer viewer = (LuntbuildViewer) iter.next();
            LuntbuildConnection con = viewer.getConnection();
			if (con.getConnectionData() == data) return viewer;
        }
        return null;
    }

    /**
     * Delete all connections
     */
    public void deleteAllConnections() {
        for (Iterator iter = viewers.iterator(); iter.hasNext();) {
            LuntbuildViewer viewer = (LuntbuildViewer) iter.next();
            viewer.remove();
        }
        PreferenceHelper.removeAllConnections();
        viewers = new ArrayList<LuntbuildViewer>();
        currentViewer = null;
        this.m_folder.redraw();
        LuntclipsePlugin.getDefault().deleteAllConnections();
        LuntclipsePlugin.getDefault().createTrayMenu(this.parent.getShell());
    }

    /** Add connection
     * @param data connection data
     */
    public void addConnection(ConnectionData data) {
        PreferenceHelper.addConnection(data);
        LuntbuildConnection con = LuntclipsePlugin.getDefault().addConnection(data);
        LuntbuildViewer viewer = createViewer(con);
        if (viewer != null) viewers.add(viewer);
        this.m_folder.redraw();
    }

    private LuntbuildViewer createViewer(LuntbuildConnection con) {
    	LuntbuildViewer viewer = new LuntbuildViewer(con, this);
        viewer.create(this.m_folder, viewers.size());
        return viewer;
    }

    /** Return true if connection exists
     * @param data connection data
     * @return true if connection exists
     */
    public boolean connectionExists(ConnectionData data) {
    	return findViewer(data) != null;
    }

    /**
     * @return Returns the viewers.
     */
    public final ArrayList getViewers() {
        return viewers;
    }

}