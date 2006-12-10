package com.luntsys.luntbuild.luntclipse.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
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
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.BuilderProjectData;

/**
 * Project Builders
 *
 * @author Lubos Pochman
 *
 */
public class BuilderProjectPage extends ProjectWizardPage implements SelectionListener, ModifyListener {

    CCombo builderType = null;
    Composite stack = null;
    StackLayout builderLayout = null;
    org.eclipse.swt.widgets.List buildersList = null;
    BuilderGroup[] builders = null;

    private List buildersData = null;

    private BuilderProjectData selectedData = null;

    /**
     * @param con
     */
    public BuilderProjectPage(LuntbuildConnection con) {
        super("Project Builders", true, con);
        setTitle("Project Builders");
        setDescription(((super.doCreate) ? "Create" : "Edit") + " Project Builders.");

        this.builders = new BuilderGroup[LuntclipseConstants.builderType.length];
    }

    /**
     * @param con
     * @param allData
     */
    public BuilderProjectPage(LuntbuildConnection con, java.util.List allData) {
        super("Project Builders", false, con);
        this.allData = allData;
        this.buildersData = (List)this.allData.get(2);
        setTitle("Project Builders");
        setDescription(((super.doCreate) ? "Create" : "Edit") + " Project Builders.");

        this.builders = new BuilderGroup[LuntclipseConstants.builderType.length];
    }

    /**
     * @param parent
     *
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite top = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 2;
        top.setLayout(layout);

        // Builders list group, selection sets selectedData
        // List (name) + buttons Delete, Modify, Deselect
        GridLayout selectLayout = new GridLayout();
        selectLayout.numColumns = 1;
        selectLayout.verticalSpacing = 2;
        Group selectGroup = groupIn(top, "Builders:", selectLayout);
        this.buildersList = new org.eclipse.swt.widgets.List(selectGroup,
                SWT.BORDER|SWT.SINGLE|SWT.SCROLL_LINE|SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        gd.widthHint = 50;
        this.buildersList.setLayoutData(gd);
        selectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
        if (this.buildersData != null) {
            for (Iterator iter = this.buildersData.iterator(); iter.hasNext();) {
                BuilderProjectData elem = (BuilderProjectData) iter.next();
                this.buildersList.add(elem.getName());
                this.buildersList.setData(elem.getName(), elem);
            }
        }
        this.buildersList.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e){
                String[] name = buildersList.getSelection();
                selectedData = (BuilderProjectData)buildersList.getData(name[0]);
                if (selectedData == null) return;
                int type = selectedData.getType();
                BuilderProjectPage.this.builderLayout.topControl =
                    BuilderProjectPage.this.builders[type].group;
                BuilderProjectPage.this.stack.layout();
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
        button.setToolTipText("Add/Create a new builder.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    addBuilder();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.MODIFY_IMG));
        button.setToolTipText("Modify selected builder.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    modifyBuilder();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.DELETE_IMG));
        button.setToolTipText("Delete selected builder.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    deleteBuilder();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.DESELECT_IMG));
        button.setToolTipText("Deselect selected builder.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    buildersList.deselectAll();
                    clearDataText();
                    selectedData = null;
                    dialogChanged();
                }
            });

        layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 2;
        Group builderGroup = groupIn(top, "Builder:", layout);
        builderGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

        this.builderType = new CCombo(groupIn(builderGroup, "Builder type:"), SWT.BORDER);

        createLine(builderGroup, layout.numColumns);

        this.stack = new Composite(builderGroup, SWT.NULL);
        this.builderLayout = new StackLayout();
        this.stack.setLayout(this.builderLayout);
        this.stack.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

        for (int i = 0; i < LuntclipseConstants.builderType.length; i++) {
            String type = LuntclipseConstants.builderType[i];
            this.builderType.add(type);
            this.builders[i] = new BuilderGroup(this.stack, i, type);
            if (i == LuntclipseConstants.COMMAND_BUILDER) {
                this.builders[i].waitToFinish = groupIn(this.builders[i].group, "Wait to Finish:",
                        new RowLayout(SWT.HORIZONTAL));
                button = new Button(this.builders[i].waitToFinish, SWT.RADIO);
                button.setText("Yes");
                button.addSelectionListener(this);
                button = new Button(this.builders[i].waitToFinish, SWT.RADIO);
                button.setText("No");
                button.addSelectionListener(this);
            }
        }
        int type = 0;
        if (this.buildersData != null && this.buildersData.size() > 0) {
            type = ((BuilderProjectData)this.buildersData.get(0)).getType();
            this.selectedData = (BuilderProjectData)this.buildersData.get(0);
            this.buildersList.select(0);
        }

        this.builderType.select(type);
        this.builderLayout.topControl = this.builders[type].group;
        this.builderType.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent e) {
                    int i = BuilderProjectPage.this.builderType.getSelectionIndex();
                    BuilderProjectPage.this.builderLayout.topControl =
                        BuilderProjectPage.this.builders[i].group;
                    BuilderProjectPage.this.stack.layout();
                    BuilderProjectPage.this.hasChanged = true;
                }
                public void widgetDefaultSelected(SelectionEvent e) {
                    int i = BuilderProjectPage.this.builderType.getSelectionIndex();
                    BuilderProjectPage.this.builderLayout.topControl =
                        BuilderProjectPage.this.builders[i].group;
                    BuilderProjectPage.this.stack.layout();
                    BuilderProjectPage.this.hasChanged = true;
                }
            });

        checkStatus();

        setControl(top);
    }

    private void addBuilder() {
        if (getErrorMessage() != null) return;
        int i = this.builderType.getSelectionIndex();
        if (i < 0) return;
        this.hasChanged = true;
        BuilderProjectData data = buildData(i);
        if (data == null) return;

        clearDataText();
        this.buildersList.deselectAll();
        this.selectedData = null;

        this.buildersList.add(data.getName());
        this.buildersList.setData(data.getName(), data);

        dialogChanged();
        this.setPageComplete(this.buildersData != null && this.buildersData.size() > 0);
    }

    private void modifyBuilder() {
        if (this.selectedData == null) return;
        this.hasChanged = true;
        buildData(this.selectedData.getType());
    }

    private void deleteBuilder() {
        if (this.selectedData == null) return;
        this.hasChanged = true;
        this.buildersList.remove(this.selectedData.getName());
        this.buildersData.remove(this.selectedData);
        clearDataText();
        this.buildersList.deselectAll();
        this.selectedData = null;
        dialogChanged();
        this.setPageComplete(this.buildersData != null && this.buildersData.size() > 0);
    }

    void dialogChanged() {
        this.hasChanged = true;
        checkStatus();
    }

    private void checkStatus() {
        int i = this.builderType.getSelectionIndex();
        if (i < 0) {
            updateStatus("Please specify name of the builder!");
            return;
        }
        if (this.builders[i].nameText.getText().trim().length() == 0) {
            updateStatus("Please specify name of the builder!");
            return;
        }
        if (this.builders[i].commandText.getText().trim().length() == 0) {
            updateStatus("Please specify command of the builder!");
            return;
        }
        if (this.builders[i].pathText.getText().trim().length() == 0) {
            updateStatus("Please specify path or directory of the builder!");
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
        if (this.buildersData != null && this.buildersData.size() > 0) return true;

        if (getErrorMessage() != null) return false;

        int i = this.builderType.getSelectionIndex();
        if (i < 0) return false;
        String name = this.builders[i].nameText.getText().trim();
        String command = this.builders[i].commandText.getText().trim();
        String path = this.builders[i].pathText.getText().trim();
        return name.length() != 0 && command.length() != 0 && path.length() != 0;
     }

    /** Create builders project data and return next page
     * @return next page
     *
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizardPage#getNextPage()
     */
    public IWizardPage getNextPage() {

        // BuildersProjectData is complete - set data
        int i = this.builderType.getSelectionIndex();
        if (i >= 0) buildData(i);
        if (this.buildersData == null || this.buildersData.size() == 0) return null;

        ScheduleProjectPage nextPage = ((EditProjectWizard)getWizard()).getSchedulePage();
        nextPage.setBuilderLists(getBuilderArray());

        return nextPage;
    }

    /**
     * @return builder names array
     */
    public String[] getBuilderArray() {
        String[] nameArr = new String[this.buildersData.size()];
        int j = 0;
        for (Iterator iter = this.buildersData.iterator(); iter.hasNext();) {
            BuilderProjectData bdata = (BuilderProjectData) iter.next();
            nameArr[j++] = bdata.getName();
        }
        return nameArr;
    }

    private BuilderProjectData buildData(int type) {
        if (!this.hasChanged) return null;

        String name = this.builders[type].nameText.getText().trim();
        if (name.length() == 0) return null;
        BuilderProjectData data = getBuilderData(name);
        if (data == null) data = new BuilderProjectData();

        data.setType(type);
        data.setName(name);
        data.setCommand(this.builders[type].commandText.getText().trim());
        data.setScriptPath(this.builders[type].pathText.getText().trim());
        if (this.builders[type].targetsText != null)
            data.setTragets(this.builders[type].targetsText.getText().trim());
        if (this.builders[type].propertiesText != null)
            data.setProperties(this.builders[type].propertiesText.getText().trim());
        data.setEnvVars(this.builders[type].envVarsText.getText().trim());
        data.setCondition(this.builders[type].successConditionText.getText().trim());
        if (this.builders[type].waitToFinish != null) {
            Button[] button = (Button[])this.builders[type].waitToFinish.getChildren();
            data.setWaitForFinish(button[0].getSelection());
        }

        if (this.buildersData == null)
            this.buildersData = new ArrayList();
        this.buildersData.add(data);

        this.hasChanged = false;

        return data;
    }

    private void setDataText() {
        if (this.selectedData == null) return;
        int type = this.selectedData.getType();
        this.builderType.select(type);
        this.builders[type].nameText.setText(this.selectedData.getName());
        this.builders[type].commandText.setText(this.selectedData.getCommand());
        this.builders[type].pathText.setText(this.selectedData.getScriptPath());

        if (this.builders[type].targetsText != null)
            this.builders[type].targetsText.setText(this.selectedData.getTragets());
        if (this.builders[type].propertiesText != null)
            this.builders[type].propertiesText.setText(this.selectedData.getProperties());
        this.builders[type].envVarsText.setText(this.selectedData.getEnvVars());
        this.builders[type].successConditionText.setText(this.selectedData.getCondition());
        if (this.builders[type].waitToFinish != null) {
            Button[] button = (Button[])this.builders[type].waitToFinish.getChildren();
            if (this.selectedData.getWaitForFinish()) {
                button[0].setSelection(true);
                button[1].setSelection(false);
            } else {
                button[1].setSelection(true);
                button[0].setSelection(false);
            }
        }
    }

    private void clearDataText() {
        int type = 0;
        this.builderType.deselectAll();
        this.builders[type].nameText.setText("");
        this.builders[type].commandText.setText("");
        this.builders[type].pathText.setText("");

        if (this.builders[type].targetsText != null)
            this.builders[type].targetsText.setText("");
        if (this.builders[type].propertiesText != null)
            this.builders[type].propertiesText.setText("");
        this.builders[type].envVarsText.setText("");
        this.builders[type].successConditionText.setText("");
        if (this.builders[type].waitToFinish != null) {
            Button[] button = (Button[])this.builders[type].waitToFinish.getChildren();
            button[0].setSelection(true);
            button[1].setSelection(false);
        }
    }

    private BuilderProjectData getBuilderData(String name) {
        if (this.buildersData == null || name == null || name.length() == 0) return null;
        for (Iterator iter = this.buildersData.iterator(); iter.hasNext();) {
            BuilderProjectData data = (BuilderProjectData) iter.next();
            if (data.getName().equals(name)) return data;
        }
        return null;
    }

    /** Returns builders project data.
     * @return builders project data BuilderProjectData
     */
    public List getData() {
        int i = this.builderType.getSelectionIndex();
        if (i >= 0)  buildData(i);
        return this.buildersData;
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

    private class BuilderGroup {
        int index = -1;
        Group group = null;
        Text nameText = null;
        Text commandText = null;
        Text pathText = null;
        Text targetsText = null;
        Text propertiesText = null;
        Text envVarsText = null;
        Text successConditionText = null;
        Group waitToFinish = null;

        BuilderGroup(Composite parent, int idx, String builderName) {
            this.index = idx;

            GridLayout layout = new GridLayout(1, true);
            this.group = groupIn(parent, builderName, layout);
            this.group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
            this.nameText = new Text(groupFill(this.group, "Name:"), SWT.BORDER | SWT.SINGLE);
            if (buildersData != null && buildersData.size() > 0)
                this.nameText.setText(((BuilderProjectData)buildersData.get(0)).getName());
            this.nameText.addModifyListener(BuilderProjectPage.this);

            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.heightHint = 60;
            this.commandText = new Text(groupIn(this.group, "Command:", gd),
                    SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
            this.commandText.setText(LuntclipseConstants.builderCommands[idx]);
            if (buildersData != null && buildersData.size() > 0)
                this.commandText.setText(((BuilderProjectData)buildersData.get(0)).getCommand());
            this.commandText.addModifyListener(BuilderProjectPage.this);

            this.pathText = new Text(groupFill(this.group, LuntclipseConstants.builderPathLabels[idx]),
                    SWT.BORDER | SWT.SINGLE);
            if (buildersData != null && buildersData.size() > 0)
                this.pathText.setText(((BuilderProjectData)buildersData.get(0)).getScriptPath());
            this.pathText.addModifyListener(BuilderProjectPage.this);

            if (LuntclipseConstants.builderTargetLabels[idx] != null) {
                this.targetsText = new Text(groupFill(this.group,
                        LuntclipseConstants.builderTargetLabels[idx]),
                        SWT.BORDER | SWT.SINGLE);
                if (buildersData != null && buildersData.size() > 0)
                    this.targetsText.setText(((BuilderProjectData)buildersData.get(0)).getTragets());
                this.targetsText.addModifyListener(BuilderProjectPage.this);
            }

            String[] props = (String[])LuntclipseConstants.builderProperties.get(idx);
            if (props != null) {
                gd = new GridData(GridData.FILL_HORIZONTAL);
                gd.heightHint = 60;
                this.propertiesText = new Text(groupIn(this.group, "Properties:", gd),
                        SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
                for (int i = 0; i < props.length; i++) {
                    String val = props[i];
                    this.propertiesText.append(val + "\n");
                }
                if (buildersData != null && buildersData.size() > 0)
                    this.propertiesText.setText(((BuilderProjectData)buildersData.get(0)).getProperties());
                this.propertiesText.addModifyListener(BuilderProjectPage.this);
            }

            gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.heightHint = 60;
            this.envVarsText = new Text(groupIn(this.group, "Environment Variables:", gd),
                    SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
            if (buildersData != null && buildersData.size() > 0)
                this.envVarsText.setText(((BuilderProjectData)buildersData.get(0)).getEnvVars());
            this.envVarsText.addModifyListener(BuilderProjectPage.this);

            this.successConditionText = new Text(groupFill(this.group, "Success Condition"),
                    SWT.BORDER | SWT.SINGLE);
            this.successConditionText.setText(LuntclipseConstants.builderSuccessConditions[idx]);
            if (buildersData != null && buildersData.size() > 0)
                this.successConditionText.setText(((BuilderProjectData)buildersData.get(0)).getCondition());
            this.successConditionText.addModifyListener(BuilderProjectPage.this);
        }
    }
}
