package com.luntsys.luntbuild.luntclipse.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.luntsys.luntbuild.facades.BuildParams;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.ILuntbuild;
import com.luntsys.luntbuild.facades.SearchCriteria;
import com.luntsys.luntbuild.facades.lb12.ScheduleFacade;
import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.actions.TrigerBuildDialog;
import com.luntsys.luntbuild.luntclipse.core.BuildLogRefreshJob;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.core.ProjectsRefreshJob;
import com.luntsys.luntbuild.luntclipse.core.RefreshJob;
import com.luntsys.luntbuild.luntclipse.core.RefreshJobRestarter;
import com.luntsys.luntbuild.luntclipse.model.BuildMessenger;
import com.luntsys.luntbuild.luntclipse.model.ConnectionData;

/**
 * Viewer for one Luntbuild connection.
 *
 * @author Lubos Pochman
 *
 */
public class LuntbuildViewer {

    /** Projects/Builds list tab */
    public static final int PROJECTS_INDEX = 0;
    /** Builds list tab */
    public static final int BUILDS_INDEX = 1;
    /** Log tab */
    public static final int LOG_INDEX = 2;
    /** Browser log tab */
    public static final int BROWSER_INDEX = 3;

    private LuntbuildConnection connection = null;
    private CTabItem connTab = null;
    private CTabFolder topFolder = null;
    private TreeViewer buildViewer = null;
    private TableViewer histBuildsViewer = null;
    private RefreshJob projectsRefreshJob = null;
    private RefreshJob logRefreshJob = null;
    private CTabItem logsTab = null;
    private Browser logHtmlViewer = null;
    private TextViewer logTextViewer = null;

    List selectedHistoryBuilds = null;
    BuildsViewContentProvider buildsProvider = null;
    BuildMessenger selectedBuild = null;
    String selectedProject = null;

    // Set column names
    private static String[] columnNames = new String[] {
            "PROJECT_NAME",
            "SCHEDULE_STATUS",
            "SCHEDULE_NAME",
            "TRIGGER_TYPE",
            "LAST_BUILD_STATUS",
            "STATUS_DATE"
            };

    private static String[] buildsColumnNames = new String[] {
        "PROJECT_NAME",
        "SCHEDULE_NAME",
        "BUILD_VERSION",
        "BUILD_STATUS",
        "BUILD_FINISHED",
        "BUILD_BURATION"
    };

    LuntbuildView parentView = null;

    /**
     * @param conData
     * @param view
     */
    public LuntbuildViewer(ConnectionData conData, LuntbuildView view) {
        this.connection = new LuntbuildConnection(conData);
        this.parentView = view;
    }

    /**
     * @param parent parent
     * @param index tab index
     */
    public void create(CTabFolder parent, int index) {

        this.connTab = new CTabItem(parent, 0 , index);
        this.topFolder = new CTabFolder(parent, SWT.NONE);
        this.connTab.setToolTipText("Luntbuild " + this.connection.getConnectionData().getName());
        this.connTab.setText("Luntbuild " + this.connection.getConnectionData().getName());
        this.connTab.setControl(this.topFolder);

        Tree tree = createTree(this.topFolder);
        this.buildViewer = new TreeViewer(tree);
        this.buildViewer.setUseHashlookup(true);
        this.buildViewer.setContentProvider( new ProjectsViewContentProvider(this.connection));
        this.buildViewer.setLabelProvider(new ProjectsViewLabelProvider(this.connection));
        this.buildViewer.setSorter(new ProjectsViewSorter(ProjectsViewSorter.NAME));
        this.buildViewer.setInput(parent);
        this.buildViewer.setColumnProperties(columnNames);
        this.connTab.setData(this);
        this.buildViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = buildViewer.getSelection();
                if (selection == null) {
                    selectedProject = null;
                    selectedBuild = null;
                    LuntbuildView.mainView.enableActionButtons();
                    return;
                }
                Object elem = ((IStructuredSelection)selection).getFirstElement();
                if (elem instanceof BuildMessenger) {
                    selectedBuild = (BuildMessenger) elem;
                    selectedProject = selectedBuild.getProjectName();
                    if (selectedProject.equals(LuntclipseConstants.gettingData)) {
                        selectedProject = null;
                        selectedBuild = null;
                    }
                    if (selectedBuild != null) triggerBuild();
                } else if (elem instanceof String){
                    selectedProject = (String) elem;
                    if (selectedProject.equals(LuntclipseConstants.gettingData))
                        selectedProject = null;
                    selectedBuild = null;
                    if (selectedProject != null) {
                        if (buildViewer.getExpandedState(elem))
                            buildViewer.collapseToLevel(elem, AbstractTreeViewer.ALL_LEVELS);
                        else
                            buildViewer.expandToLevel(elem, AbstractTreeViewer.ALL_LEVELS);
                    }
                }
                LuntbuildView.mainView.enableActionButtons();
            }
        });

        // Add Builds tab
        CTabItem newItem = new CTabItem(this.topFolder, 0 , PROJECTS_INDEX);
        newItem.setToolTipText("This tab shows all projects and their build information.\n" +
                "Status of a schedule and build is denoted using icon:\n" +
                "GREEN icon means success, the animation gear icon means building, and RED icon means build failed.\n" +
                "Schedule status is different from build status, and it means whether or not the schedule\n" +
                "has been successfully triggered.");
        newItem.setText("Projects");
        newItem.setControl(this.buildViewer.getControl());

        Table table = createTable(this.topFolder);
        this.histBuildsViewer = new TableViewer(table);
        this.histBuildsViewer.setUseHashlookup(true);
        this.buildsProvider = new BuildsViewContentProvider(this.connection);
        this.histBuildsViewer.setContentProvider(this.buildsProvider);
        this.histBuildsViewer.setLabelProvider(new BuildsViewLabelProvider(this.connection));
        this.histBuildsViewer.setSorter(new BuildsViewSorter(ProjectsViewSorter.NAME));
        this.histBuildsViewer.setInput(parent);
        this.histBuildsViewer.setColumnProperties(buildsColumnNames);
        this.histBuildsViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = histBuildsViewer.getSelection();
                if (selection == null) {
                    return;
                }
                selectedHistoryBuilds = new ArrayList();
                for (Iterator it = ((IStructuredSelection)selection).iterator(); it .hasNext();) {
                    BuildMessenger elem = (BuildMessenger) it.next();
                    selectedHistoryBuilds.add(elem);
                }
                LuntbuildView.mainView.enableActionButtons();
                displayBuildLog();
            }
        });

        newItem = new CTabItem(this.topFolder, 0, BUILDS_INDEX);
        newItem.setText("Builds");
        newItem.setToolTipText("This tab shows list of selected history builds.");
        newItem.setControl(this.histBuildsViewer.getControl());

        this.logsTab = new CTabItem(this.topFolder, 0, LOG_INDEX);
        this.logsTab.setText("Logs");
        setLogViewer(parent, this.topFolder, this.logsTab, this.connection);

        newItem = new CTabItem(this.topFolder, 0, BROWSER_INDEX);
        newItem.setText("Browser");
        newItem.setToolTipText("This tab shows Luntbuild web browser.");
        setBrowser(parent, this.topFolder, newItem, this.connection);

        //run refesh worker
        int delay = 10;
        try{
            String refreshTime = this.connection.getConnectionData().getRefreshTime();
            if(refreshTime != null){
                delay = new Integer(refreshTime).intValue();
            }
        }catch (NumberFormatException e) {
            //ignore it
        }

        this.projectsRefreshJob = new ProjectsRefreshJob(this.buildViewer, this.connection, delay * 1000);

        hookContextMenu();

        try{
            this.connection.connect();
            this.projectsRefreshJob.schedule();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Trigger build
     */
    public void triggerBuild() {
        ILuntbuild luntbuild = this.connection.getLuntbuild();
        if (luntbuild == null) {
            MessageDialog.openError(
                    getShell(),
                    "Luntbuild Connection",
                    "Unable to connect to Luntbuild: " + this.connection.getConnectionData().getUrl());
            return;
        }
        if (this.selectedBuild == null) return;
        BuildParams buildParams = createBuildParams(this.connection,
                    this.selectedBuild.getProjectName(), this.selectedBuild.getScheduleName());
        TrigerBuildDialog dlg =
            new TrigerBuildDialog(getShell(), buildParams, this.selectedBuild);
        int rc = dlg.open();
        if (rc == SWT.OK) {
            buildParams = dlg.getBuildParams();
            this.connection.getLuntbuild().triggerBuild(
                    this.selectedBuild.getProjectName(),
                    this.selectedBuild.getScheduleName(),
                    buildParams);
            refresh();
        }
    }

    private BuildParams createBuildParams(LuntbuildConnection con, String projectName, String scheduleName) {
        BuildParams buildParams = new BuildParams();

        ScheduleFacade schedule = con.getScheduleFacade(projectName, scheduleName);
        buildParams.setBuildType(schedule.getBuildType());
        buildParams.setBuildVersion("");
        buildParams.setLabelStrategy(schedule.getLabelStrategy());
        buildParams.setNotifyStrategy(schedule.getNotifyStrategy());
        buildParams.setPostbuildStrategy(schedule.getPostbuildStrategy());
        buildParams.setTriggerDependencyStrategy(schedule.getTriggerDependencyStrategy());

        return buildParams;
    }

    private Tree createTree(Composite parent) {
        Tree tree = new Tree(parent, SWT.BORDER | SWT.SINGLE| SWT.FULL_SELECTION| SWT.H_SCROLL | SWT.V_SCROLL);
        tree.setHeaderVisible(true);
        tree.addSelectionListener(
                new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e){
                        ISelection selection = buildViewer.getSelection();
                        if (selection == null) {
                            selectedProject = null;
                            selectedBuild = null;
                            LuntbuildView.mainView.enableActionButtons();
                            return;
                        }
                        Object elem = ((IStructuredSelection)selection).getFirstElement();
                        if (elem instanceof BuildMessenger) {
                            selectedBuild = (BuildMessenger) elem;
                            selectedProject = selectedBuild.getProjectName();
                            if (selectedProject.equals(LuntclipseConstants.gettingData)) {
                                selectedProject = null;
                                selectedBuild = null;
                            }
                        } else if (elem instanceof String){
                            selectedProject = (String) elem;
                            if (selectedProject.equals(LuntclipseConstants.gettingData))
                                selectedProject = null;
                            selectedBuild = null;
                        }
                        LuntbuildView.mainView.enableActionButtons();
                    }
                }
        );

        TreeColumn column = new TreeColumn(tree, SWT.LEFT, 0);
        column.setText("Project");
        column.setWidth(180);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                buildViewer.setSorter(new ProjectsViewSorter(ProjectsViewSorter.NAME));
            }
        });

        column = new TreeColumn(tree, SWT.CENTER, 1);
        column.setText("");
        column.setWidth(20);

        column = new TreeColumn(tree, SWT.LEFT, 2);
        column.setText("Schedule");
        column.setWidth(100);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                buildViewer.setSorter(new ProjectsViewSorter(ProjectsViewSorter.SCHEDULE_NAME));
            }
        });

        column = new TreeColumn(tree, SWT.LEFT, 3);
        column.setText("When to trigger");
        column.setWidth(100);

        column = new TreeColumn(tree, SWT.LEFT, 4);
        column.setText("Latest build");
        column.setWidth(150);

        column = new TreeColumn(tree, SWT.LEFT, 5);
        column.setText("Latest build date");
        column.setWidth(120);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                buildViewer.setSorter(new ProjectsViewSorter(ProjectsViewSorter.STATUS_DATE));
            }
        });

        return tree;
    }

    private Table createTable(Composite parent) {
        Table table = new Table(parent, SWT.BORDER | SWT.MULTI| SWT.FULL_SELECTION| SWT.H_SCROLL | SWT.V_SCROLL);
        table.setHeaderVisible(true);
        table.addSelectionListener(
                new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e){
                        ISelection selection = histBuildsViewer.getSelection();
                        if (selection == null) {
                            return;
                        }
                        selectedHistoryBuilds = new ArrayList();
                        for (Iterator it = ((IStructuredSelection)selection).iterator(); it .hasNext();) {
                            BuildMessenger elem = (BuildMessenger) it.next();
                            selectedHistoryBuilds.add(elem);
                        }
                        LuntbuildView.mainView.enableActionButtons();
                    }
                }
        );

        TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        column.setText("Project");
        column.setWidth(100);

        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText("Schedule");
        column.setWidth(100);

        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText("Version");
        column.setWidth(100);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                histBuildsViewer.setSorter(new BuildsViewSorter(BuildsViewSorter.BUILD_VERSION));
            }
        });

        column = new TableColumn(table, SWT.CENTER, 3);
        column.setText("");
        column.setWidth(20);

        column = new TableColumn(table, SWT.LEFT, 4);
        column.setText("Status");
        column.setWidth(60);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                histBuildsViewer.setSorter(new BuildsViewSorter(BuildsViewSorter.BUILD_STATUS));
            }
        });

        column = new TableColumn(table, SWT.LEFT, 5);
        column.setText("Finshed at");
        column.setWidth(120);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                histBuildsViewer.setSorter(new BuildsViewSorter(BuildsViewSorter.BUILD_FINISHED));
            }
        });

        column = new TableColumn(table, SWT.LEFT, 6);
        column.setText("Duration (min)");
        column.setWidth(70);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                histBuildsViewer.setSorter(new BuildsViewSorter(BuildsViewSorter.BUILD_DURATION));
            }
        });

        return table;
    }

    /**
     * This method restarts job responsible for refreshing
     * Luntclipse view. If job is not running, it will be start.
     */
    public void restartRefreshJob(){
        int delay = 10;
        try {
            String refreshTime = this.connection.getConnectionData().getRefreshTime();
            if(refreshTime != null){
                delay = new Integer(refreshTime).intValue();
            }
        } catch (NumberFormatException e) {
            //ignore it
        }

        if (this.projectsRefreshJob.getState() == Job.NONE) {
            this.projectsRefreshJob.setDelay(delay * 1000);
            this.projectsRefreshJob.schedule();
        } else {
            new RefreshJobRestarter(this.projectsRefreshJob, delay * 1000).schedule();
        }
    }

    /**
     * Stop refresh job
     */
    public void stopRefreshJob(){
        if (this.projectsRefreshJob != null) this.projectsRefreshJob.cancel();
        if (this.logRefreshJob != null) this.logRefreshJob.cancel();
    }

    /** Set refresh job
     * @param job job
     */
    public void setRefreshJob(RefreshJob job) {
        this.projectsRefreshJob = job;
        this.logRefreshJob = job;
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                LuntbuildViewer.this.parentView.fillLocalPullDown(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(this.buildViewer.getControl());
        this.buildViewer.getControl().setMenu(menu);
        this.parentView.getSite().registerContextMenu(menuMgr, this.buildViewer);

        menuMgr = new MenuManager("#BuildsPopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                LuntbuildViewer.this.parentView.fillBuildsPullDown(manager);
            }
        });
        menu = menuMgr.createContextMenu(this.histBuildsViewer.getControl());
        this.histBuildsViewer.getControl().setMenu(menu);
        this.parentView.getSite().registerContextMenu(menuMgr, this.histBuildsViewer);
    }

    private Object setLogViewer(Composite parent, CTabFolder folder,
            CTabItem tab, LuntbuildConnection con) {

        Document newDoc = new Document();
        if (this.connection.getVersion() <=
            LuntclipseConstants.getVersion(LuntclipseConstants.LUNTBUILD_VERSION_122)) {
            this.logTextViewer = new TextViewer(folder, SWT.H_SCROLL | SWT.V_SCROLL);
            this.logTextViewer.setDocument(newDoc);
            this.logTextViewer.setEditable(false);
            this.logTextViewer.getTextWidget().setFont(
                    new Font(parent.getDisplay(), new FontData("Courier", 10, SWT.NORMAL)));
            //set tab colors
            ((CTabFolder)this.logTextViewer.getControl().getParent()).
                setBackground(getColor(SWT.COLOR_WIDGET_BACKGROUND));
            ((CTabFolder)this.logTextViewer.getControl().getParent()).
                setSelectionBackground(getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

            this.logTextViewer.getTextWidget().addLineStyleListener(new LogLineHighlighter());

            tab.setControl(this.logTextViewer.getControl());
            return this.logTextViewer;
        } else {
            this.logHtmlViewer = new Browser(folder, SWT.NONE);
            this.logHtmlViewer.setFont(new Font(parent.getDisplay(), new FontData("Courier", 10, SWT.NORMAL)));
            this.logHtmlViewer.setBackground(getColor(SWT.COLOR_WIDGET_BACKGROUND));

            tab.setControl(this.logHtmlViewer);
            return this.logHtmlViewer;
        }
    }

    private Object setBrowser(Composite parent, CTabFolder folder,
            CTabItem newItem, LuntbuildConnection con) {

        Browser browser = new Browser(folder, SWT.NONE);
        browser.setFont(new Font(parent.getDisplay(), new FontData("Courier", 10, SWT.NORMAL)));
        browser.setBackground(getColor(SWT.COLOR_WIDGET_BACKGROUND));

        try {
            browser.setUrl(con.getConnectionData().getUrl());
        } catch (Exception e) {
            LuntclipsePlugin.doLog(IStatus.ERROR, IStatus.OK, "Cannot connect to Lutbuild!", e);
            browser.setText("<html><body>Cannot connect to Lutbuild!</body></html>");
        }

        newItem.setControl(browser);
        return browser;
    }

    private Color getColor(int swtStyle) {
        return this.topFolder.getDisplay().getSystemColor(swtStyle);
    }

    /**
     * Remove the view.
     */
    public void remove() {
        this.connTab.dispose();
    }

    /**
     * @return Returns the connection.
     */
    public final LuntbuildConnection getConnection() {
        return this.connection;
    }

    /**
     * @return Returns the buildViewer.
     */
    public final TreeViewer getBuildViewer() {
        return this.buildViewer;
    }

    /**
     * @return name of selected project
     */
    public final String getSelectedProject() {
        if (this.selectedBuild != null) return this.selectedBuild.getProjectName();
        return this.selectedProject;
    }

    /**
     * @return selected history builds
     */
    public final List getSelectedHistoryBuilds() {
        return this.selectedHistoryBuilds;
    }

    /**
     * @return builds data provider
     */
    public final BuildsViewContentProvider getBuildsProvider() {
        return this.buildsProvider;
    }

    /**
     * Refresh viewer
     */
    public final void refresh() {
        this.connection.loadBuildData();
        this.buildViewer.refresh();
        this.histBuildsViewer.refresh();
        LuntbuildView.mainView.enableActionButtons();
    }

    /**
     * Set the selection to the tab at the specified index.
     * @param index index the index of the tab item to be selected
     */
    public void setTabSelection(int index){
        ((CTabFolder)this.buildViewer.getControl().getParent()).setSelection(index);
    }

    /**
     * @return current/selected build info
     */
    public BuildMessenger getSelectedBuild() {
        return this.selectedBuild;
    }

    private BuildMessenger getBuildToDisplay() {
        BuildMessenger build = null;
        if (this.topFolder.getSelectionIndex() == BUILDS_INDEX) {
            if (this.selectedHistoryBuilds != null && this.selectedHistoryBuilds.size() > 0)
                build = (BuildMessenger)this.selectedHistoryBuilds.get(0);
        }
        if (this.topFolder.getSelectionIndex() == PROJECTS_INDEX || build == null)
            build = getSelectedBuild();
        return build;
    }

    /**
     * Display build log
     */
    public void displayBuildLog() {
        BuildMessenger build = getBuildToDisplay();
        if (build == null) return;
        String url = build.getBuildLogUrl();
        if(url == null){
            MessageDialog.openInformation(
                    this.buildViewer.getControl().getShell(),
                    "Luntbuild view",
                    "Sorry, no builds yet!");
            return;
        }

        int delay = 10;
        try{
            String refreshTime = this.connection.getConnectionData().getRefreshTime();
            if(refreshTime != null){
                delay = new Integer(refreshTime).intValue() / 2;
            }
        }catch (NumberFormatException e) {
            //ignore it
        }
        if (delay < 10) delay = 10;

        this.logsTab.setText("Build Log");
        this.logsTab.setToolTipText("Build Log for build " +
                build.getProjectName() + "/" + build.getScheduleName());
        if (this.logHtmlViewer != null) {
            try {
                String result = this.connection.openURL(url);
                this.logHtmlViewer.setText(result);
                this.logRefreshJob =
                    new BuildLogRefreshJob(build, this.logHtmlViewer, this.connection, delay * 1000);
                this.logRefreshJob.schedule(delay * 1000);
            } catch (Exception e) {
                LuntclipsePlugin.doLog(IStatus.ERROR, IStatus.OK, "Cannot connect to Lutbuild!", e);
                this.logHtmlViewer.setText("<html><body>Cannot connect to Lutbuild!</body></html>");
            }
        } else if (this.logTextViewer != null) {
            try {
                String result = this.connection.openURL(url);
                this.logTextViewer.getTextWidget().setText("");
                this.logTextViewer.getTextWidget().append(result);
                this.logRefreshJob =
                    new BuildLogRefreshJob(build, this.logTextViewer, this.connection, delay * 1000);
                this.logRefreshJob.schedule(delay * 1000);
            } catch (Exception e) {
                LuntclipsePlugin.doLog(IStatus.ERROR, IStatus.OK, "Cannot connect to Lutbuild!", e);
                this.logTextViewer.getTextWidget().setText("Cannot connect to Lutbuild!");
            }
        }
        setTabSelection(LuntbuildViewer.LOG_INDEX);
    }

    /**
     * Clear logs
     */
    public void clearLogs() {
        this.logsTab.setText("Logs");
        this.logsTab.setToolTipText("");
        if (this.logTextViewer != null) {
            this.logTextViewer.getTextWidget().setText("");
       } if (this.logHtmlViewer != null) {
            this.logHtmlViewer.setText("<html><body></body></html>");
        }
    }

    /**
     * Display revision log
     */
    public void displayRevisionLog() {
        BuildMessenger build = getBuildToDisplay();
        if (build == null) return;
        String url = build.getRevisionLogUrl();
        if(url == null){
            MessageDialog.openInformation(
                    buildViewer.getControl().getShell(),
                    "Luntbuild view",
                    "Sorry, no builds yet!");
            return;
        }
        this.logsTab.setText("Revision Log");
        this.logsTab.setToolTipText("Revision Log for build " +
                build.getProjectName() + "/" + build.getScheduleName());
        if (this.logHtmlViewer != null) {
            try {
                String result = this.connection.openURL(url);
                this.logHtmlViewer.setText(result);
            } catch (Exception e) {
                LuntclipsePlugin.doLog(IStatus.ERROR, IStatus.OK, "Cannot connect to Lutbuild!", e);
                this.logHtmlViewer.setText("<html><body>Cannot connect to Lutbuild!</body></html>");
            }
        } else if (this.logTextViewer != null) {
            try {
                String result = this.connection.openURL(url);
                this.logTextViewer.getTextWidget().setText("");
                this.logTextViewer.getTextWidget().append(result);
            } catch (Exception e) {
                LuntclipsePlugin.doLog(IStatus.ERROR, IStatus.OK, "Cannot connect to Lutbuild!", e);
                this.logTextViewer.getTextWidget().setText("Cannot connect to Lutbuild!");
            }
        }
        setTabSelection(LuntbuildViewer.LOG_INDEX);
    }

    /**
     * Display system log
     */
    public void displaySystemLog() {
        BuildMessenger build = getSelectedBuild();
        if (build == null) return;
        setTabSelection(LuntbuildViewer.LOG_INDEX);
        this.logsTab.setText("System Log");
        this.logsTab.setToolTipText("System Log for Luntbuild connection " +
                this.connection.getConnectionData().getName());

        String url = build.getSystemLogUrl();
        if(url == null){
            this.logHtmlViewer.setText("<html><body>System log not yet available!</body></html>");
            return;
        }
        if (this.logHtmlViewer != null) {
            try {
                String result = this.connection.openURL(url);
                this.logHtmlViewer.setText(result);
            } catch (Exception e) {
                LuntclipsePlugin.doLog(IStatus.ERROR, IStatus.OK, "Cannot connect to Lutbuild!", e);
                this.logHtmlViewer.setText("<html><body>Cannot connect to Lutbuild!</body></html>");
            }
        } else if (this.logTextViewer != null) {
            try {
                String result = this.connection.openURL(url);
                this.logTextViewer.getTextWidget().setText("");
                this.logTextViewer.getTextWidget().append(result);
            } catch (Exception e) {
                LuntclipsePlugin.doLog(IStatus.ERROR, IStatus.OK, "Cannot connect to Lutbuild!", e);
                this.logTextViewer.getTextWidget().setText("Cannot connect to Lutbuild!");
            }
        }
    }

    /**
     * @return shell
     */
    public Shell getShell() {
        return this.buildViewer.getControl().getShell();
    }

    /**
     * ViewSorter is used by a structured viewer to
     * reorder the elements provided by its content provider.
     *
     * @author   Roman Pichlík
     * @version  $Revision: 1.14 $
     * @since    0.0.1
     */
    private class ProjectsViewSorter extends ViewerSorter {
        /** column index */
        public final static int NAME = 0;
        public final static int SCHEDULE_NAME = 2;
        public final static int STATUS_DATE = 5;

        private int criteria;

        /**
         * @param criteria
         */
        ProjectsViewSorter(int criteria) {
            super();
            this.criteria = criteria;
        }

        /**
         * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public int compare(Viewer viewer, Object o1, Object o2) {
            if (o1 instanceof String && o2 instanceof String) {
                String projectName1 = (String) o1;
                String projectName2 = (String) o2;
                switch (this.criteria) {
                case NAME :
                    return this.collator.compare(projectName1, projectName2);
                default:
                    return 0;
                }

            } else if (o1 instanceof BuildMessenger && o2 instanceof BuildMessenger) {
                BuildMessenger m1 = (BuildMessenger) o1;
                BuildMessenger m2 = (BuildMessenger) o2;

                switch (this.criteria) {
                    case NAME :
                        return this.collator.compare(m1.getProjectName(), m2.getProjectName());
                    case SCHEDULE_NAME :
                        return this.collator.compare(m1.getScheduleName(), m2.getScheduleName());
                    case STATUS_DATE :
                        return this.collator.compare(m1.getStatusDate(), m2.getStatusDate());
                    default:
                        return 0;
                }
            } else
                return 0;
        }

        /**
         * Returns the sort criteria of this this sorter.
         *
         * @return the sort criterion
         */
        public int getCriteria() {
            return this.criteria;
        }
    }

    /**
     * The content provider class is responsible for
     * providing objects representing build info to the view.
     *
     * @author   Lubos Pochman
     * @version  $Revision: 1.14 $
     * @since    0.0.1
     */
    private class ProjectsViewContentProvider implements ITreeContentProvider {

        private LuntbuildConnection luntbuild;

        /**
         * Default constructor
         * @param con Luntbuild connection
         */
        public ProjectsViewContentProvider(LuntbuildConnection con) {
            this.luntbuild = con;
        }

          /**
           * Gets the children of the specified object
           *
           * @param arg0
           *            the parent object
           * @return Object[]
           */
          public Object[] getChildren(Object arg0) {
              if (!(arg0 instanceof String)) return null;
              // Return the builds
              Map ldata = this.luntbuild.getLuntbuildData();
              if (ldata == null || ldata.isEmpty()) {
                  BuildMessenger[] ret = {new BuildMessenger()};
                  ret[0].setProjectName(LuntclipseConstants.gettingData);
                  return ret;
              }
              List list = (List)ldata.get(arg0);
              if (list == null) {
                  return null;
              } else {
                  return list.toArray();
              }
          }

          /**
           * Gets the parent of the specified object
           *
           * @param arg0
           *            the object
           * @return Object
           */
          public Object getParent(Object arg0) {
              // Return this build's project
              if (arg0 instanceof BuildMessenger) {
                  return ((BuildMessenger)arg0).getProjectName();
              } else {
                  return null;
              }
          }

          /**
           * Returns whether the passed object has children
           *
           * @param arg0
           *            the parent object
           * @return boolean
           */
          public boolean hasChildren(Object arg0) {
              // Get the children
              if (arg0 instanceof BuildMessenger) {
                  return false;
              } else {
                  Map ldata = this.luntbuild.getLuntbuildData();
                  if (ldata == null || ldata.isEmpty()) return false;
                  List list = (List)this.luntbuild.getLuntbuildData().get(arg0);
                  return list != null && list.size() > 0;
              }
          }

          /**
           * Gets the root element(s) of the tree
           *
           * @param arg0
           *            the input data
           * @return Object[]
           */
          public Object[] getElements(Object arg0) {
              // These are the root elements of the tree
              Map ldata = this.luntbuild.getLuntbuildData();
              if (ldata == null || ldata.isEmpty()) {
                  BuildMessenger[] ret = {new BuildMessenger()};
                  if (this.luntbuild.isConnected())
                      ret[0].setProjectName("");
                  else
                      ret[0].setProjectName(LuntclipseConstants.gettingData);
                  return ret;
              }
              Set set = this.luntbuild.getLuntbuildData().keySet();
              return set.toArray(new Object[set.size()]);
          }

          /**
           * Disposes any created resources
           */
          public void dispose() {
            // Nothing to dispose
          }

          /**
           * Called when the input changes
           *
           * @param arg0
           *            the viewer
           * @param arg1
           *            the old input
           * @param arg2
           *            the new input
           */
          public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
            // Nothing to change
          }
    }

    /**
     * This class provides labels for luntbuild view.
     * @author   Roman Pichlík
     * @version  $Revision: 1.14 $
     * @since    0.0.1
     */
    private class ProjectsViewLabelProvider extends LabelProvider implements ITableLabelProvider {

        private LuntbuildConnection luntbuild;

        /**
         * Default constructor
         * @param con
         */
        public ProjectsViewLabelProvider(LuntbuildConnection con) {
            super();
            this.luntbuild = con;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object obj, int index) {
            String result = "";
            if(obj instanceof Exception){
                return (index == 0) ? "An Exception has occurred! See error log." : "";
            }
            if(obj instanceof String){
                return (index == 0) ? (String)obj : "";
            }
            if(obj instanceof BuildMessenger){
                BuildMessenger messenger = (BuildMessenger)obj;
                switch (index) {
                case 0:
                    result = messenger.getProjectName();
                    break;
                case 2:
                    result = messenger.getScheduleName();
                    break;
                case 3:
                    if (!this.luntbuild.isDataAvailable()) break;
                    switch(messenger.getTriggerType()){
                    case Constants.TRIGGER_TYPE_CRON:
                        result = "cron";
                        break;
                    case Constants.TRIGGER_TYPE_SIMPLE:
                        result = "simple";
                        break;
                    case Constants.TRIGGER_TYPE_MANUAL:
                        result = "manual";
                        break;
                    }
                    break;
                case 4:
                    if (!this.luntbuild.isDataAvailable()) break;
                    result = messenger.getVersion();
                    break;
                case 5:
                    if (!this.luntbuild.isDataAvailable() ||
                            messenger.getVersion().equalsIgnoreCase(LuntclipseConstants.noBuildsYet)) break;
                    result = messenger.getStatusDate();
                    break;
                default:
                    break;
                }
            }
            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage(Object obj, int index) {
            Image image = null;
            if(obj instanceof Exception) {
                return null;
            }
            ImageRegistry ir = LuntclipsePlugin.getDefault().getImageRegistry();
            if (obj instanceof String) {
                return (index == 0) ? ir.get(LuntclipseConstants.PROJECT_IMG) : null;
            }
            if (obj instanceof BuildMessenger) {
                BuildMessenger messenger = (BuildMessenger)obj;
                if(index == 1 ){
                    if (!this.luntbuild.isDataAvailable()) {
                        if (!this.luntbuild.isConnected())
                            image =  ir.get(LuntclipseConstants.FAILED_IMG);
                    } else {
                        switch(messenger.getScheduleStatus()){
                        case Constants.SCHEDULE_STATUS_SUCCESS:
                            image =  ir.get(LuntclipseConstants.SUCCESS_IMG);
                            break;
                        case Constants.SCHEDULE_STATUS_FAILED:
                             image =  ir.get(LuntclipseConstants.FAILED_IMG);
                            break;
                        case Constants.SCHEDULE_STATUS_CREATED:
                            image =  ir.get(LuntclipseConstants.BUILD_IMG);
                            break;
                        case Constants.SCHEDULE_STATUS_RUNNING:
                            image =  ir.get(LuntclipseConstants.RUNNING_IMG);
                            break;
                        }
                    }
                }else if(index == 4){
                    if (!this.luntbuild.isDataAvailable()) {
                        image =  null;
                    } else {
                        switch(messenger.getLastBuildStatus()){
                        case Constants.BUILD_STATUS_SUCCESS:
                            image =  ir.get(LuntclipseConstants.SUCCESS_IMG);
                            break;
                        case Constants.BUILD_STATUS_FAILED:
                             image =  ir.get(LuntclipseConstants.FAILED_IMG);
                            break;
                        case Constants.BUILD_STATUS_RUNNING:
                            image =  ir.get(LuntclipseConstants.RUNNING_IMG);
                            break;
                        case Constants.BUILD_STATUS_ALL:
                            image =  ir.get(LuntclipseConstants.BUILD_IMG);
                            break;
                        }
                    }
                }
            }
            return image;
        }

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
         */
        public Image getImage(Object obj) {
            return PlatformUI.getWorkbench().
                    getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }

    /**
     * ViewSorter is used by a structured viewer to
     * reorder the elements provided by its content provider.
     *
     * @author   Lubos Pochman
     */
    private class BuildsViewSorter extends ViewerSorter {
        /** column index */
        public final static int BUILD_VERSION = 2;
        public final static int BUILD_STATUS = 4;
        public final static int BUILD_FINISHED = 5;
        public final static int BUILD_DURATION = 6;

        private int criteria;

        /**
         * @param criteria
         */
        BuildsViewSorter(int criteria) {
            super();
            this.criteria = criteria;
        }

        /**
         * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public int compare(Viewer viewer, Object o1, Object o2) {
            if (o1 instanceof BuildMessenger && o2 instanceof BuildMessenger) {
                BuildMessenger m1 = (BuildMessenger) o1;
                BuildMessenger m2 = (BuildMessenger) o2;

                switch (this.criteria) {
                    case BUILD_VERSION :
                        return this.collator.compare(m1.getVersion(), m2.getVersion());
                    case BUILD_STATUS :
                        return this.collator.compare("" + m1.getBuildStatus(), "" + m2.getBuildStatus());
                    case BUILD_FINISHED :
                        return this.collator.compare(m1.getEndDate(), m2.getEndDate());
                    case BUILD_DURATION :
                        return this.collator.compare(m1.getDuration(), m2.getDuration());
                    default:
                        return 0;
                }
            } else
                return 0;
        }

        /**
         * Returns the sort criteria of this this sorter.
         *
         * @return the sort criterion
         */
        public int getCriteria() {
            return this.criteria;
        }
    }

    /**
     * The content provider class is responsible for
     * providing objects representing build info to the view.
     *
     * @author   Lubos Pochman
     * @version  $Revision: 1.14 $
     * @since    0.0.1
     */
    public class BuildsViewContentProvider implements IStructuredContentProvider {

        private LuntbuildConnection luntbuild;
        private SearchCriteria criteria = null;

        /**
         * Default constructor
         * @param con Luntbuild connection
         */
        public BuildsViewContentProvider(LuntbuildConnection con) {
            this.luntbuild = con;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /**
         * Return model data
         * @return array of {@link BuildMessenger}
         *
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements(Object parent) {
            if (this.luntbuild == null || this.luntbuild.getLuntbuild() == null || this.criteria == null) {
                BuildMessenger[] ret = {new BuildMessenger()};
                ret[0].setProjectName(LuntclipseConstants.gettingData);
                return ret;

            }
            List dataList = this.luntbuild.getLuntbuild().searchBuilds(this.criteria, 0, 0);

            List buildsList = BuildMessenger.toMessanger(dataList, getSelectedBuild());

            return  buildsList.toArray();
        }

        /**
         * @param criteria
         */
        public void setCriteria(SearchCriteria criteria) {
            this.criteria = criteria;
        }
    }

    /**
     * This class provides labels for luntbuild view.
     * @author   Roman Pichlík
     * @version  $Revision: 1.14 $
     * @since    0.0.1
     */
    private class BuildsViewLabelProvider extends LabelProvider implements ITableLabelProvider {

        private LuntbuildConnection luntbuild;

        /**
         * Default constructor
         * @param con
         */
        public BuildsViewLabelProvider(LuntbuildConnection con) {
            super();
            this.luntbuild = con;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object obj, int index) {
            String result = "";
            if(obj instanceof Exception){
                return (index == 0) ? "An Exception has occurred! See error log." : "";
            }
            if(obj instanceof String){
                return (index == 0) ? (String)obj : "";
            }
            if (!this.luntbuild.isDataAvailable())
                return result;
            if(obj instanceof BuildMessenger){
                BuildMessenger messenger = (BuildMessenger)obj;
                if (messenger.getScheduleName().trim().length() == 0) return result;
                switch (index) {
                case 0:
                    result = messenger.getProjectName();
                    break;
                case 1:
                    result = messenger.getScheduleName();
                    break;
                case 2:
                    result = messenger.getVersion();
                    break;
                case 4:
                    result = LuntclipseConstants.buildStatus[messenger.getBuildStatus()];
                    break;
                case 5:
                    result = messenger.getEndDate();
                    break;
                case 6:
                    result = messenger.getDuration();
                    break;
                default:
                    break;
                }
            }
            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage(Object obj, int index) {
            Image image = null;
            if(obj instanceof Exception) {
                return null;
            }
            ImageRegistry ir = LuntclipsePlugin.getDefault().getImageRegistry();
            if (obj instanceof String) {
                return (index == 0) ? ir.get(LuntclipseConstants.PROJECT_IMG) : null;
            }
            if (obj instanceof BuildMessenger) {
                BuildMessenger messenger = (BuildMessenger)obj;
                if (messenger.getScheduleName().trim().length() == 0) return image;
                if(index == 3 && this.luntbuild.isDataAvailable()){
                    switch(messenger.getBuildStatus()){
                    case Constants.BUILD_STATUS_FAILED:
                        image =  ir.get(LuntclipseConstants.FAILED_IMG);
                        break;
                    default:
                        image =  ir.get(LuntclipseConstants.SUCCESS_IMG);
                        break;
                    }
                }
            }
            return image;
        }

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
         */
        public Image getImage(Object obj) {
            return PlatformUI.getWorkbench().
                    getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }

}
