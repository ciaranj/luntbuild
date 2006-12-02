package com.luntsys.luntbuild.luntclipse.wizards;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.ScheduleProjectData;

/**
 * Project Schedules
 *
 * @author Lubos Pochman
 *
 */
public class ScheduleProjectPage extends ProjectWizardPage implements SelectionListener, ModifyListener {

    private Text nameText = null;
    private Text descText = null;
    private Text nextBuildText = null;
    private Text workDirText = null;
    private Text buildConditionText = null;
    private CCombo buildType = null;
    List buildersList = null;
    List postBuildersList = null;
    private List dependentSchedulesList = null;
    private CCombo postBuildStrategy = null;
    private CCombo labelStrategy = null;
    private CCombo notifyStrategy = null;
    private RadioGroupInfo triggerTypes = null;
    private RadioGroupInfo cleanupStrategy = null;

    org.eclipse.swt.widgets.List schedulesList = null;
    private java.util.List schedulesData = null;

    private ScheduleProjectData selectedData = null;

    /**
     * @param con
     */
    public ScheduleProjectPage(LuntbuildConnection con) {
        super("Project Schedules", true, con);
        setTitle("Project Schedules");
        setDescription(((super.doCreate) ? "Create" : "Edit") + " Project Schedules.");
    }

    /**
     * @param con
     * @param allData
     */
    public ScheduleProjectPage(LuntbuildConnection con, java.util.List allData) {
        super("Project Schedules", false, con);
        this.allData = allData;
        this.schedulesData = (java.util.List)this.allData.get(3);
        setTitle("Project Schedules");
        setDescription(((super.doCreate) ? "Create" : "Edit") + " Project Schedules.");
    }

    /**
     * @param parent
     *
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {

        Composite top = new Composite(parent, SWT.NULL);
        GridLayout topLayout = new GridLayout();
        top.setLayout(topLayout);
        topLayout.numColumns = 2;
        topLayout.verticalSpacing = 2;
        GridData gd;

        // schedules list group, selection sets selectedData
        // List (name) + buttons Delete, Modify, Deselect
        GridLayout selectLayout = new GridLayout();
        selectLayout.numColumns = 1;
        selectLayout.verticalSpacing = 2;
        Group selectGroup = groupIn(top, "Schedules List", selectLayout);
        this.schedulesList = new org.eclipse.swt.widgets.List(selectGroup,
                SWT.BORDER|SWT.SINGLE|SWT.SCROLL_LINE|SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        gd.widthHint = 50;
        this.schedulesList.setLayoutData(gd);
        selectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
        if (this.schedulesData != null) {
            for (Iterator iter = this.schedulesData.iterator(); iter.hasNext();) {
                ScheduleProjectData elem = (ScheduleProjectData) iter.next();
                this.schedulesList.add(elem.getName());
                this.schedulesList.setData(elem.getName(), elem);
            }
        }
        this.schedulesList.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e){
                String[] name = schedulesList.getSelection();
                selectedData = (ScheduleProjectData)schedulesList.getData(name[0]);
                if (selectedData == null) return;
                setDataText();
            }
        });

        // buttons
        Composite buttonsComp = new Composite(selectGroup, SWT.NULL);
        RowLayout buttonLayout = new RowLayout();
        buttonLayout.wrap = false;
        buttonLayout.pack = false;
        buttonLayout.justify = true;
        buttonLayout.type = SWT.HORIZONTAL;
        buttonLayout.marginLeft = 2;
        buttonLayout.marginTop = 2;
        buttonLayout.marginRight = 2;
        buttonLayout.marginBottom = 2;
        buttonLayout.spacing = 2;
        buttonsComp.setLayout(buttonLayout);
        Button button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.CREATE_IMG));
        button.setToolTipText("Add/Create a new schedule.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    addSchedule();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.MODIFY_IMG));
        button.setToolTipText("Modify selected schedule.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    modifySchedule();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.DELETE_IMG));
        button.setToolTipText("Delete selected schedule.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    deleteSchedule();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.DESELECT_IMG));
        button.setToolTipText("Deselect selected schedule.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    schedulesList.deselectAll();
                    clearDataText();
                    selectedData = null;
                    dialogChanged();
                }
            });

        // Selected/new schedule
        GridLayout groupLayout = new GridLayout();
        groupLayout.numColumns = 2;
        groupLayout.verticalSpacing = 2;
        Group topGroup = groupIn(top, "Schedule", groupLayout);
        topGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

        this.nameText = new Text(groupFill(topGroup, "Name:"), SWT.BORDER | SWT.SINGLE);
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.nameText.setText(((ScheduleProjectData)this.schedulesData.get(0)).getName());
        this.nameText.addModifyListener(this);

        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 60;
        this.descText = new Text(groupIn(topGroup, "Description:", gd),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.descText.setText(((ScheduleProjectData)this.schedulesData.get(0)).getDescription());
        this.descText.addModifyListener(this);

        this.nextBuildText = new Text(groupFill(topGroup, "Next build version:"), SWT.BORDER | SWT.SINGLE);
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.nextBuildText.setText(((ScheduleProjectData)this.schedulesData.get(0)).getNextBuildVersion());
        this.nextBuildText.addModifyListener(this);

        this.workDirText = new Text(groupFill(topGroup, "Work directory:"), SWT.BORDER | SWT.SINGLE);
         if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.workDirText.setText(((ScheduleProjectData)this.schedulesData.get(0)).getWorkDirectory());
         this.workDirText.addModifyListener(this);

        this.buildConditionText = new Text(groupFill(topGroup, "Build Condition:"), SWT.BORDER | SWT.SINGLE);
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.buildConditionText.setText(((ScheduleProjectData)this.schedulesData.get(0)).getBuildCondition());
       this.buildConditionText.addModifyListener(this);

        this.buildType = new CCombo(groupFill(topGroup, "Build type:"), SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.buildType.length; i++) {
            String type = LuntclipseConstants.buildType[i];
            this.buildType.add(type);
        }
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.buildType.select(((ScheduleProjectData)this.schedulesData.get(0)).getBuildType());
        else
            this.buildType.select(0);
        this.buildType.addSelectionListener(this);

        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 60;
        this.buildersList = new List(groupIn(topGroup, "Builders:", gd),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        if (this.schedulesData != null && this.schedulesData.size() > 0) {
            this.buildersList.setSelection(((ScheduleProjectData)this.schedulesData.get(0)).getBuilders());
        }
        this.buildersList.addSelectionListener(this);

        // trigger layout
        this.triggerTypes =
            radioGroup(groupIn(topGroup, "Trigger type:", new GridLayout(3, false)),
                    LuntclipseConstants.buildTriggerTypes,
                    new String[] {null, "repeat (min)", "expression"});
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.triggerTypes.buttons[((ScheduleProjectData)this.schedulesData.get(0)).
                                      getTrigerType()].setSelection(true);
        else
            this.triggerTypes.buttons[0].setSelection(true);
        for (int i = 0; i < this.triggerTypes.buttons.length; i++) {
            Button btn = this.triggerTypes.buttons[i];
            btn.addSelectionListener(this);
        }

        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 60;
        this.postBuildersList = new List(groupIn(topGroup, "Post-Builders:", gd),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        if (this.schedulesData != null && this.schedulesData.size() > 0) {
            this.postBuildersList.setSelection(((ScheduleProjectData)this.schedulesData.
                    get(0)).getPostBuilders());
        }
        this.postBuildersList.addSelectionListener(this);

        this.postBuildStrategy = new CCombo(groupIn(topGroup, "Post-Build Strategy:"), SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.postBuildStrategy.length; i++) {
            String type = LuntclipseConstants.postBuildStrategy[i];
            this.postBuildStrategy.add(type);
        }
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.postBuildStrategy.select(((ScheduleProjectData)this.schedulesData.
                    get(0)).getPostBuildStrategy());
        else
            this.postBuildStrategy.select(0);
        this.postBuildStrategy.addSelectionListener(this);

        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 60;
        this.dependentSchedulesList = new List(groupIn(topGroup, "Dependent Schedules:", gd),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        String[] schedules = ((EditProjectWizard)getWizard()).getAllSchedules();
        for (int i = 0; i < schedules.length; i++) {
            String val = schedules[i];
            this.dependentSchedulesList.add(val);
        }
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.dependentSchedulesList.setSelection(
                    ((ScheduleProjectData)this.schedulesData.get(0)).getDependentSchedules());
        this.dependentSchedulesList.addSelectionListener(this);

        // cleanup layout
        this.cleanupStrategy =
            radioGroup(groupIn(topGroup, "Build Cleanup:", new GridLayout(3, false)),
                    LuntclipseConstants.buildCleanupStrategy,
                    new String[] {null, "days", "count"});
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.cleanupStrategy.buttons[((ScheduleProjectData)this.schedulesData.get(0)).
                                         getBuildCleanup()].setSelection(true);
        else
            this.cleanupStrategy.buttons[0].setSelection(true);
        for (int i = 0; i < this.cleanupStrategy.buttons.length; i++) {
            Button btn = this.cleanupStrategy.buttons[i];
            btn.addSelectionListener(this);
        }

        this.labelStrategy = new CCombo(groupFill(topGroup, "Label Strategy:"), SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.labelStrategy.length; i++) {
            String type = LuntclipseConstants.labelStrategy[i];
            this.labelStrategy.add(type);
        }
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.labelStrategy.select(((ScheduleProjectData)this.schedulesData.get(0)).getLabelStrategy());
        else
            this.labelStrategy.select(0);
        this.labelStrategy.addSelectionListener(this);

        this.notifyStrategy = new CCombo(groupFill(topGroup, "Notify Strategy:"), SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.notifyStrategy.length; i++) {
            String type = LuntclipseConstants.notifyStrategy[i];
            this.notifyStrategy.add(type);
        }
        if (this.schedulesData != null && this.schedulesData.size() > 0)
            this.notifyStrategy.select(((ScheduleProjectData)this.schedulesData.get(0)).getNotifyStrategy());
        else
            this.notifyStrategy.select(0);
        this.notifyStrategy.addSelectionListener(this);

        if (this.schedulesData != null && this.schedulesData.size() > 0) {
            this.selectedData = (ScheduleProjectData)this.schedulesData.get(0);
            this.schedulesList.select(0);
        }

        checkStatus();

        setControl(top);
    }

    private void addSchedule() {
        if (getErrorMessage() != null) return;
        this.hasChanged = true;
        ScheduleProjectData data = buildData();
        if (data == null) return;

        clearDataText();
        this.schedulesList.deselectAll();
        this.selectedData = null;

        this.schedulesList.add(data.getName());
        this.schedulesList.setData(data.getName(), data);

        dialogChanged();
        this.setPageComplete(this.schedulesData != null && this.schedulesData.size() > 0);
    }

    private void modifySchedule() {
        if (this.selectedData == null) return;
        this.hasChanged = true;
        buildData();
    }

    private void deleteSchedule() {
        if (this.selectedData == null) return;
        this.hasChanged = true;
        this.schedulesList.remove(this.selectedData.getName());
        this.schedulesData.remove(this.selectedData);
        clearDataText();
        this.schedulesList.deselectAll();
        this.selectedData = null;
        dialogChanged();
        this.setPageComplete(this.schedulesData != null && this.schedulesData.size() > 0);
    }

    void dialogChanged() {
        this.hasChanged = true;
        checkStatus();
    }

    private void checkStatus() {
        // validate data, report status
        String val = this.nameText.getText().trim();
        if (val.length() == 0) {
            updateStatus("Please specify name of the schedule!");
            return;
        }
        val = this.nextBuildText.getText().trim();
        if (val.length() == 0) {
            updateStatus("Please specify next build version!");
            return;
        }
        val = this.buildConditionText.getText().trim();
        if (val.length() == 0) {
            updateStatus("Please specify build necessary condition!");
            return;
        }
        if (this.buildersList.getSelectionCount() == 0) {
            updateStatus("Please specify at least one builder!");
            return;
        }

        updateStatus(null);
    }

    /**
     * @return
     *
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizardPage#canFlipToNextPage()
     */
    public boolean canFlipToNextPage(){
        if (this.schedulesData != null && this.schedulesData.size() > 0) return true;

        if (getErrorMessage() != null) return false;
        String name = this.nameText.getText().trim();
        String next = this.nextBuildText.getText().trim();
        String cond = this.buildConditionText.getText().trim();
        return name.length() != 0 && next.length() != 0 && cond.length() != 0 &&
            this.buildersList.getSelectionCount() > 0;
     }

    /** Create basic project data and return next page
     * @return next page
     *
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizardPage#getNextPage()
     */
    public IWizardPage getNextPage() {

        // Page is complete - set data
        buildData();

        BasicProjectPage nextPage = ((EditProjectWizard)getWizard()).getBasicPage();
        return nextPage;
    }

    private ScheduleProjectData buildData() {
        if (!this.hasChanged) return null;

        String name = this.nameText.getText().trim();
        if (name.length() == 0) return null;
        ScheduleProjectData data = getScheduleData(name);

        if (data == null) data = new ScheduleProjectData();

        data.setName(name);
        data.setDescription(this.descText.getText().trim());
        data.setWorkDirectory(this.workDirText.getText().trim());
        data.setNextBuildVersion(this.nextBuildText.getText().trim());
        data.setBuildCondition(this.buildConditionText.getText().trim());
        data.setBuildType(this.buildType.getSelectionIndex());
        data.setBuilders(this.buildersList.getSelection());
        data.setTrigerType(this.triggerTypes.getSelectionIndex());
        data.setTriggerData(this.triggerTypes.getSelectionData());
        data.setPostBuilders(this.postBuildersList.getSelection());
        data.setPostBuildStrategy(this.postBuildStrategy.getSelectionIndex());
        data.setDependentSchedules(this.dependentSchedulesList.getSelection());
        data.setBuildCleanup(this.cleanupStrategy.getSelectionIndex());
        data.setBuildCleanupData(this.cleanupStrategy.getSelectionData());
        data.setLabelStrategy(this.labelStrategy.getSelectionIndex());
        data.setNotifyStrategy(this.notifyStrategy.getSelectionIndex());

        if (this.schedulesData == null)
            this.schedulesData = new ArrayList();
        this.schedulesData.add(data);

        this.hasChanged = false;

        return data;
    }

    private void setDataText() {
        if (this.selectedData == null) return;

        this.nameText.setText(this.selectedData.getName());
        this.descText.setText(this.selectedData.getDescription());
        this.workDirText.setText(this.selectedData.getWorkDirectory());
        this.nextBuildText.setText(this.selectedData.getNextBuildVersion());
        this.buildConditionText.setText(this.selectedData.getBuildCondition());
        this.buildType.select(this.selectedData.getBuildType());
        this.buildersList.setSelection(this.selectedData.getBuilders());
        this.triggerTypes.select(this.selectedData.getTrigerType());
        this.triggerTypes.setSelectionData(this.selectedData.getTriggerData());
        this.postBuildersList.setSelection(this.selectedData.getPostBuilders());
        this.postBuildStrategy.select(this.selectedData.getPostBuildStrategy());
        this.dependentSchedulesList.setSelection(this.selectedData.getDependentSchedules());
        this.cleanupStrategy.select(this.selectedData.getBuildCleanup());
        this.cleanupStrategy.setSelectionData(this.selectedData.getBuildCleanupData());
        this.labelStrategy.select(this.selectedData.getLabelStrategy());
        this.notifyStrategy.select(this.selectedData.getNotifyStrategy());
    }

    private void clearDataText() {
        this.nameText.setText("");
        this.descText.setText("");
        this.workDirText.setText("");
        this.nextBuildText.setText("");
        this.buildConditionText.setText("");
        this.buildType.deselectAll();
        this.buildersList.deselectAll();
        this.triggerTypes.deselectAll();
        this.triggerTypes.clearSelectionData();
        this.postBuildersList.deselectAll();
        this.postBuildStrategy.deselectAll();
        this.dependentSchedulesList.deselectAll();
        this.cleanupStrategy.deselectAll();
        this.cleanupStrategy.clearSelectionData();
        this.labelStrategy.deselectAll();
        this.notifyStrategy.deselectAll();
    }

    private ScheduleProjectData getScheduleData(String name) {
        if (this.schedulesData == null || name == null || name.length() == 0) return null;
        for (Iterator iter = this.schedulesData.iterator(); iter.hasNext();) {
            ScheduleProjectData data = (ScheduleProjectData) iter.next();
            if (data.getName().equals(name)) return data;
        }
        return null;
    }

    /** Returns schedules project data.
     * @return schedules project data
     */
    public java.util.List getData() {
        buildData();
        return this.schedulesData;
    }

    /** Set builders, post-builders lists
     * @param builders array
     */
    public void setBuilderLists(String[] builders) {
        ScheduleProjectPage.this.buildersList.setItems(builders);
        ScheduleProjectPage.this.postBuildersList.setItems(builders);
        if (this.schedulesData != null && this.schedulesData.size() > 0) {
            this.buildersList.setSelection(((ScheduleProjectData)this.schedulesData.get(0)).getBuilders());
        }
        if (this.schedulesData != null && this.schedulesData.size() > 0) {
            this.postBuildersList.setSelection(((ScheduleProjectData)this.schedulesData.get(0)).getPostBuilders());
        }
        checkStatus();
    }

    public void widgetSelected(SelectionEvent e){
        dialogChanged();
    }

    public void widgetDefaultSelected(SelectionEvent e){
        dialogChanged();
    }

    public void modifyText(ModifyEvent e) {
        dialogChanged();
    }

}
