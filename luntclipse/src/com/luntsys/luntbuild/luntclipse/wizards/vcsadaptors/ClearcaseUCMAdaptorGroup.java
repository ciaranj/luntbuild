package com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.model.CvsModuleData;
import com.luntsys.luntbuild.luntclipse.model.VcsProjectData;
import com.luntsys.luntbuild.luntclipse.wizards.ProjectWizardPage;

/**
 * Clearcase UCM Adaptor
 *
 * @author Lubos Pochman
 *
 */
public class ClearcaseUCMAdaptorGroup extends VcsAdaptorGroup {

    private Text viewStglocText = null;
    private Text vobTagText = null;
    private Text viewStorageText = null;
    private Text streamNameText = null;
    private Text buildTargetText = null;
    private Text modConfigText = null;
    private Text viewOptionsText = null;
    private Text exePathText = null;

    /**
     * @param parent
     *
     */
    public void addControls(Composite parent) {
        boolean hasData = (this.editData != null && this.editData.size() > 0 &&
                LuntclipseConstants.UCM_CLEARCASE_ADAPTOR == ((VcsProjectData)this.editData.get(0)).getType());

        this.viewStglocText = new Text(ProjectWizardPage.groupFill(parent, "Clearcase view stgloc name:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.viewStglocText.setText(((VcsProjectData)this.editData.get(0)).getClearcaseViewStgloc());
        this.viewStglocText.addModifyListener(this);

        this.vobTagText = new Text(ProjectWizardPage.groupFill(parent, "Project vob tag:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.vobTagText.setText(((VcsProjectData)this.editData.get(0)).getClearcaseVobTag());
        this.vobTagText.addModifyListener(this);

        this.viewStorageText = new Text(ProjectWizardPage.groupFill(parent, "Explicit path for view storage:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.viewStorageText.setText(((VcsProjectData)this.editData.get(0)).getClearcaseViewStorage());
        this.viewStorageText.addModifyListener(this);

        this.streamNameText = new Text(ProjectWizardPage.groupFill(parent, "UCM stream name:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.streamNameText.setText(((VcsProjectData)this.editData.get(0)).getClearcaseStreamName());
        this.streamNameText.addModifyListener(this);

        this.buildTargetText = new Text(ProjectWizardPage.groupFill(parent, "What to build:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.buildTargetText.setText(((VcsProjectData)this.editData.get(0)).getClearcaseBuildTarget());
        this.buildTargetText.addModifyListener(this);

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 60;
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 60;
        this.modConfigText = new Text(ProjectWizardPage.groupIn(this.group, "Modification detection config:", gd),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        if (hasData)
            this.modConfigText.setText(((VcsProjectData)this.editData.get(0)).getClearcaseModConfig());
        this.modConfigText.addModifyListener(this);

        this.viewOptionsText = new Text(ProjectWizardPage.groupFill(parent, "Extra options when creating snapshot view:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.viewOptionsText.setText(((VcsProjectData)this.editData.get(0)).getClearcaseViewOptions());
        this.viewOptionsText.addModifyListener(this);

        this.exePathText = new Text(ProjectWizardPage.groupFill(parent, "Path for cleartool executable:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.exePathText.setText(((VcsProjectData)this.editData.get(0)).getExePath());
        this.exePathText.addModifyListener(this);
    }

    /**
     * Dialog data changed
     *
     */
    public void dialogChanged() {
        this.page.setHasChanged(true);
        this.hasChanged = true;
        checkStatus();
    }

    /**
     * Check status of the page
     */
    public void checkStatus() {
        if (this.vobTagText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify Clearcase project Vob tag!");
            return;
        }
        if (this.streamNameText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify Clearcase UCM stream name!");
            return;
        }
        if (this.buildTargetText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify Clearcase build target!");
            return;
        }
        super.page.updateStatus(null);
    }

    /**
     * @return true if can flip to next
     *
     */
    public boolean canFlipToNextPage() {
        String vob = this.vobTagText.getText().trim();
        String stream = this.streamNameText.getText().trim();
        String build = this.buildTargetText.getText().trim();
        return vob.length() != 0 && stream.length() != 0 && build.length() != 0;
    }

    /**
     * @param data VCS project data to set
     *
     */
    public void setData(VcsProjectData data) {
        if (this.hasChanged) {
            data.setClearcaseViewStgloc(this.viewStglocText.getText().trim());
            data.setClearcaseVobTag(this.vobTagText.getText().trim());
            data.setClearcaseStreamName(this.streamNameText.getText().trim());
            data.setClearcaseBuildTarget(this.buildTargetText.getText().trim());
            data.setClearcaseViewStorage(this.viewStorageText.getText().trim());
            data.setClearcaseModConfig(this.modConfigText.getText().trim());
            data.setClearcaseViewOptions(this.viewOptionsText.getText().trim());
            data.setExePath(this.exePathText.getText().trim());
        }
        this.hasChanged = false;
    }

    /**
     * Set controls/text from data
     * @param data adaptor data
     */
    public void setDataText(VcsProjectData data) {
        this.viewStglocText.setText(data.getClearcaseViewStgloc());
        this.vobTagText.setText(data.getClearcaseVobTag());
        this.streamNameText.setText(data.getClearcaseStreamName());
        this.buildTargetText.setText(data.getClearcaseBuildTarget());
        this.viewStorageText.setText(data.getClearcaseViewStorage());
        this.modConfigText.setText(data.getClearcaseModConfig());
        this.viewOptionsText.setText(data.getClearcaseViewOptions());
        this.exePathText.setText(data.getExePath());
    }

    /**
     * Clears controls/text
     */
    public void clearDataText() {
        this.viewStglocText.setText("");
        this.vobTagText.setText("");
        this.streamNameText.setText("");
        this.buildTargetText.setText("");
        this.viewStorageText.setText("");
        this.modConfigText.setText("");
        this.viewOptionsText.setText("");
        this.exePathText.setText("");
    }

    public boolean usesModules() { return false; }

    public void addModuleControl(Composite parent) {
    }

    public void addModuleTableColumns(Table table) {
    }

    public IStructuredContentProvider getModuleViewProvider() {
        return null;
    }

    public ITableLabelProvider getModuleViewLabelProvider() {
        return null;
    }

    public String[] getModuleColumnNames() {
        return null;
    }

    /**
     * @return selected module
     */
    public CvsModuleData getSelectedModule() {
        return null;
    }

    /**
     * Creates a module from module data
     */
    public void createModule() {}

    /**
     * Modifies selected module
     */
    public void modifySelectedModule() {
    }

    /**
     * Deletes selected module
     */
    public void deleteSelectedModule() {
    }

}
