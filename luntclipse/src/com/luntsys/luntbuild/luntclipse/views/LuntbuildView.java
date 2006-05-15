/* $Header: /cvsroot/luntbuild/luntclipse/src/com/luntsys/luntbuild/luntclipse/views/LuntbuildView.java,v 1.12 2006/01/20 15:59:36 lubosp Exp $
 *
 * Copyright (c) 2004 - 2005 A.S.E.I. s.r.o.
 */
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
import com.luntsys.luntbuild.luntclipse.actions.RefreshAction;
import com.luntsys.luntbuild.luntclipse.actions.RevisionLogAction;
import com.luntsys.luntbuild.luntclipse.actions.SearchBuildsAction;
import com.luntsys.luntbuild.luntclipse.actions.SystemLogAction;
import com.luntsys.luntbuild.luntclipse.actions.TriggerBuildAction;
import com.luntsys.luntbuild.luntclipse.model.ConnectionData;
import com.luntsys.luntbuild.luntclipse.preferences.PreferenceHelper;

/**
 * The main luntbuild view shows data obtained from remote invocation of
 * Luntbuild.
 *
 * @author 	 Roman Pichlík
 * @version  $Revision: 1.12 $
 * @since 	 0.0.1
 *
 */
public class LuntbuildView extends ViewPart implements SelectionListener {

    /** This view  */
    public static LuntbuildView mainView = null;
    private CTabFolder m_folder = null;
    private Composite focusFolder = null;

    //actions
    private Action refreshAction;
    private BuildLogAction buildLogAction;
    private RevisionLogAction revisionLogAction;
    private SystemLogAction systemLogAction;
    private CleanLogAction cleanLogAction;
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

    private static ArrayList viewers = null;

    /** Current build viewer */
    public static LuntbuildViewer currentViewer = null;

	/**
	 * The constructor.
	 */
	public LuntbuildView() {
        viewers = new ArrayList();
        mainView = this;
	}

	/**
	 * This is a callback that will allow us
	 * to create the buildViewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
        this.m_folder = new CTabFolder(parent, SWT.NONE);
        this.focusFolder = m_folder;
        this.m_folder.addSelectionListener(this);

        int i = 0;
        ArrayList conArr = PreferenceHelper.getConnections();
        for (Iterator iter = conArr.iterator(); iter.hasNext();) {
            ConnectionData con = (ConnectionData) iter.next();

            LuntbuildViewer viewer = new LuntbuildViewer(con, this);
            viewer.create(this.m_folder, i++);
            viewers.add(viewer);
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

        contributeToActionBars();
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
        this.triggerBuildAction.setEnabled(currentViewer != null && currentViewer.getSelectedBuild() != null);
        this.refreshAction.setEnabled(currentViewer != null && currentViewer.getConnection().isConnected());
        this.buildLogAction.setEnabled(currentViewer != null && currentViewer.getSelectedBuild() != null);
        this.revisionLogAction.setEnabled(currentViewer != null && currentViewer.getSelectedBuild() != null);
        this.systemLogAction.setEnabled(currentViewer != null && currentViewer.getSelectedBuild() != null);
        this.cleanLogAction.setEnabled(currentViewer != null && currentViewer.getSelectedBuild() != null);
        this.editProjectAction.setEnabled(currentViewer != null && currentViewer.getSelectedProject() != null);
        this.deleteProjectAction.setEnabled(currentViewer != null && currentViewer.getSelectedProject() != null);
        this.copyProjectAction.setEnabled(currentViewer != null && currentViewer.getSelectedProject() != null);
        this.pasteProjectAction.setEnabled(LuntclipsePlugin.getProjectClipboard() != null);
        this.searchBuildsAction.setEnabled(currentViewer != null && currentViewer.getSelectedBuild() != null);
        this.moveBuildsAction.setEnabled(
                currentViewer != null && currentViewer.getSelectedHistoryBuilds() != null &&
                currentViewer.getSelectedHistoryBuilds().size() > 0);
        this.deleteBuildsAction.setEnabled(
                currentViewer != null && currentViewer.getSelectedHistoryBuilds() != null &&
                currentViewer.getSelectedHistoryBuilds().size() > 0);

    }

	/**
	 * Passing the focus request to the buildViewer's control.
	 */
	public void setFocus() {
        this.focusFolder.setFocus();
	}

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    public void dispose() {
        try{
            stopRefreshJob();
        }finally{
            super.dispose();
        }
    }

    /**
     * Stop refresh job
     */
    public void stopRefreshJob() {
        for (Iterator iter = viewers.iterator(); iter.hasNext();) {
            LuntbuildViewer view = (LuntbuildViewer) iter.next();
            view.stopRefreshJob();
            view.setRefreshJob(null);
        }
    }

    /**
     * Restarts refresh job
     */
    public void restartRefreshJob() {
        for (Iterator iter = viewers.iterator(); iter.hasNext();) {
            LuntbuildViewer view = (LuntbuildViewer) iter.next();
            view.restartRefreshJob();
        }
    }

    /** Delete connection.
     * @param data connection data
     */
    public void deleteConnection(ConnectionData data) {
        LuntbuildViewer view = null;
        for (Iterator iter = viewers.iterator(); iter.hasNext();) {
            LuntbuildViewer viewer = (LuntbuildViewer) iter.next();
            if (viewer.getConnection().getConnectionData().getName().equals(data.getName())) {
                view = viewer;
                break;
            }
        }

        if (view == null) return;
        PreferenceHelper.removeConnection(data);
        view.stopRefreshJob();
        view.setRefreshJob(null);
        view.remove();
        this.m_folder.redraw();
        viewers.remove(view);
    }

    /**
     * Delete all connections
     */
    public void deleteAllConnections() {
        for (Iterator iter = viewers.iterator(); iter.hasNext();) {
            LuntbuildViewer viewer = (LuntbuildViewer) iter.next();
            viewer.stopRefreshJob();
            viewer.setRefreshJob(null);
            viewer.remove();
        }
        PreferenceHelper.removeAllConnections();
        viewers = new ArrayList();
        this.m_folder.redraw();
    }

    /** Add connection
     * @param data connection data
     */
    public void addConnection(ConnectionData data) {
        PreferenceHelper.addConnection(data);
        LuntbuildViewer viewer = new LuntbuildViewer(data, this);
        viewer.create(this.m_folder, viewers.size());
        viewers.add(viewer);
        this.m_folder.redraw();
    }

    /** Return true if connection exists
     * @param name connection name
     * @return true if connection exists
     */
    public boolean nameExists(String name) {
        for (Iterator iter = viewers.iterator(); iter.hasNext();) {
            LuntbuildViewer viewer = (LuntbuildViewer) iter.next();
            if (viewer.getConnection().getConnectionData().getName().equals(name)) return true;
        }
        return false;
    }

    /**
     * @return Returns the viewers.
     */
    public final ArrayList getViewers() {
        return viewers;
    }

}