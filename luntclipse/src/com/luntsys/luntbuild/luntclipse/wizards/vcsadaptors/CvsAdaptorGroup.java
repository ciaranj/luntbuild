package com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.model.BuildMessenger;
import com.luntsys.luntbuild.luntclipse.model.CvsModuleData;
import com.luntsys.luntbuild.luntclipse.model.VcsProjectData;
import com.luntsys.luntbuild.luntclipse.wizards.ProjectWizardPage;
import com.luntsys.luntbuild.luntclipse.wizards.RadioGroupInfo;

/**
 * Cvs Adaptor
 *
 * @author Lubos Pochman
 *
 */
public class CvsAdaptorGroup extends VcsAdaptorGroup {

    private Text cvsRootText = null;
    private Text passwordText = null;
    private RadioGroupInfo cygwinCvsGroup = null;
    private RadioGroupInfo logCmdGroup = null;
    private RadioGroupInfo historyGroup = null;
    private Text exePathText = null;
    private Text moduleSourcePathText = null;
    private Text moduleBranchText = null;
    private Text moduleLabelText = null;

    private CvsModuleData selectedModule = null;

    /**
     * @param parent
     *
     */
    public void addControls(Composite parent) {
        boolean hasData = (this.editData != null && this.editData.size() > 0 &&
                LuntclipseConstants.CVS_ADAPTOR == ((VcsProjectData)this.editData.get(0)).getType());

        this.cvsRootText = new Text(ProjectWizardPage.groupFill(parent, "Cvs root:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.cvsRootText.setText(((VcsProjectData)this.editData.get(0)).getCvsRoot());
        this.cvsRootText.addModifyListener(this);

        this.passwordText =
            new Text(ProjectWizardPage.groupFill(parent, "Cvs Password:"), SWT.BORDER | SWT.SINGLE);
        this.passwordText.setEchoChar('*');
        if (hasData)
            this.passwordText.setText(((VcsProjectData)this.editData.get(0)).getPassword());
        this.passwordText.addModifyListener(this);

        this.cygwinCvsGroup =
            ProjectWizardPage.radioGroup(ProjectWizardPage.groupIn(parent,
                    "Is cygwin cvs?", new GridLayout(2, false)), new String[] {"Yes", "No"}, null);
        if (hasData)
            this.cygwinCvsGroup.buttons[((VcsProjectData)this.editData.get(0)).
                                        getCvsCygwin()].setSelection(true);
        else
            this.cygwinCvsGroup.buttons[1].setSelection(true);
        for (int i = 0; i < this.cygwinCvsGroup.buttons.length; i++) {
            Button btn = this.cygwinCvsGroup.buttons[i];
            btn.addSelectionListener(this);
        }

        this.logCmdGroup =
            ProjectWizardPage.radioGroup(ProjectWizardPage.groupIn(parent,
                    "Disable \"-S\" option for log command?", new GridLayout(2, false)),
                    new String[] {"Yes", "No"}, null);
        if (hasData)
            this.logCmdGroup.buttons[((VcsProjectData)this.editData.get(0)).
                                     getCvsLogCommand()].setSelection(true);
        else
            this.logCmdGroup.buttons[1].setSelection(true);
        for (int i = 0; i < this.logCmdGroup.buttons.length; i++) {
            Button btn = this.logCmdGroup.buttons[i];
            btn.addSelectionListener(this);
        }

        this.historyGroup =
            ProjectWizardPage.radioGroup(ProjectWizardPage.groupIn(parent,
                    "Disable history command?", new GridLayout(2, false)), new String[] {"Yes", "No"}, null);
        if (hasData)
            this.historyGroup.buttons[((VcsProjectData)this.editData.get(0)).
                                      getCvsHistroy()].setSelection(true);
        else
            this.historyGroup.buttons[1].setSelection(true);
        for (int i = 0; i < this.historyGroup.buttons.length; i++) {
            Button btn = this.historyGroup.buttons[i];
            btn.addSelectionListener(this);
        }

        this.exePathText = new Text(ProjectWizardPage.groupFill(parent, "Path for cvs executable:"),
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
        if (this.cvsRootText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify Cvs root!");
            return;
        }
        if (this.modulesList != null && this.modulesList.size() > 0)
            super.page.updateStatus(null);
        else if (!validateModule())
            return;
        super.page.updateStatus(null);
    }

    /**
     * @return true if can flip to next
     *
     */
    public boolean canFlipToNextPage() {
        String root = this.cvsRootText.getText().trim();
        return root.length() != 0 && this.modulesList.size() > 0;
    }

    /**
     * @param data VCS project data to set
     *
     */
    public void setData(VcsProjectData data) {
        if (this.hasChanged) {
            data.setCvsRoot(this.cvsRootText.getText().trim());
            data.setPassword(this.passwordText.getText().trim());
            data.setCvsCygwin(this.cygwinCvsGroup.getSelectionIndex());
            data.setCvsLogCommand(this.logCmdGroup.getSelectionIndex());
            data.setCvsHistroy(this.historyGroup.getSelectionIndex());
            data.setExePath(this.exePathText.getText().trim());
        }
        this.hasChanged = false;
    }

    /**
     * Set controls/text from data
     * @param data adaptor data
     */
    public void setDataText(VcsProjectData data) {
        this.cvsRootText.setText(data.getCvsRoot());
        this.passwordText.setText(data.getPassword());
        this.cygwinCvsGroup.select(data.getCvsCygwin());
        this.logCmdGroup.select(data.getCvsLogCommand());
        this.historyGroup.select(data.getCvsHistroy());
        this.exePathText.setText(data.getExePath());
        setModuleText();
    }

    /**
     * Clears controls/text
     */
    public void clearDataText() {
        this.cvsRootText.setText("");
        this.passwordText.setText("");
        this.cygwinCvsGroup.deselectAll();
        this.logCmdGroup.deselectAll();
        this.historyGroup.deselectAll();
        this.exePathText.setText("");
        clearModuleData();
    }

    public boolean usesModules() { return true; }

    public void addModuleControl(Composite parent) {
        this.moduleSourcePathText =
            new Text(ProjectWizardPage.groupFill(parent, "Source path:"), SWT.BORDER | SWT.SINGLE);
        this.moduleSourcePathText.addModifyListener(this);
        this.moduleBranchText =
            new Text(ProjectWizardPage.groupFill(parent, "Branch:"), SWT.BORDER | SWT.SINGLE);
        this.moduleBranchText.addModifyListener(this);
        this.moduleLabelText =
            new Text(ProjectWizardPage.groupFill(parent, "Label:"), SWT.BORDER | SWT.SINGLE);
        this.moduleLabelText.addModifyListener(this);
    }

    public void addModuleTableColumns(Table table) {
        table.addSelectionListener(
                new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e){
                        ISelection selection = CvsAdaptorGroup.this.tableViewer.getSelection();
                        selectedModule =
                            (CvsModuleData)((IStructuredSelection)selection).getFirstElement();
                        setModuleText();
                    }
                }
        );
        TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        column.setText("Source path");
        column.setWidth(100);
        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText("Branch");
        column.setWidth(50);
        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText("Label");
        column.setWidth(50);
    }

    public IStructuredContentProvider getModuleViewProvider() {
        return new CvsViewProvider();
    }

    public ITableLabelProvider getModuleViewLabelProvider() {
        return new CvsLabelProvider();
    }

    public String[] getModuleColumnNames() {
        String[] colNames = {
                "CVS_MODULE_PATH",
                "CVS_MODULE_BRANCH",
                "CVS_MODULE_LABEL"};
        return colNames;
    }

    /**
     * @return selected module
     */
    public CvsModuleData getSelectedModule() {
        return this.selectedModule;
    }

    /**
     * Creates a module from module data
     */
    public void createModule() {
        if (!validateModule()) return;
        this.page.dialogChanged();
        this.selectedModule = setModuleData(new CvsModuleData());
        this.modulesList.add(this.selectedModule);
        this.tableViewer.refresh();
        clearModuleData();
    }

    private void clearModuleData() {
        this.moduleSourcePathText.setText("");
        this.moduleBranchText.setText("");
        this.moduleLabelText.setText("");
    }

    private void setModuleText() {
        this.moduleSourcePathText.setText(this.selectedModule.getSourcePath());
        this.moduleBranchText.setText(this.selectedModule.getBranch());
        this.moduleLabelText.setText(this.selectedModule.getLabel());
    }

    private boolean validateModule() {
        if (this.moduleSourcePathText == null) return false;
        if (this.moduleSourcePathText.getText().trim().length() == 0) {
            this.page.updateStatus("Cvs Module Source path needs to be specified!");
            return false;
        }

        return true;
    }

    private CvsModuleData setModuleData(CvsModuleData data) {
        data.setSourcePath(this.moduleSourcePathText.getText().trim());
        data.setBranch(this.moduleBranchText.getText().trim());
        data.setLabel(this.moduleLabelText.getText().trim());
        return data;
    }

    /**
     * Modifies selected module
     */
    public void modifySelectedModule() {
        this.selectedModule = setModuleData(this.selectedModule);
        this.tableViewer.refresh();
        clearModuleData();
    }

    /**
     * Deletes selected module
     */
    public void deleteSelectedModule() {
        if (this.selectedModule == null) return;
        this.modulesList.remove(this.selectedModule);
        this.tableViewer.refresh();
        this.selectedModule = null;
    }

    private class CvsViewProvider implements IStructuredContentProvider {
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
            if (CvsAdaptorGroup.this.modulesList == null) return new Object[0];
            Object[] elems = new Object[CvsAdaptorGroup.this.modulesList.size()];
            int i = 0;
            for (Iterator iter = CvsAdaptorGroup.this.modulesList.iterator(); iter.hasNext();) {
                CvsModuleData mod = (CvsModuleData) iter.next();
                elems[i++] = mod;
            }
            return elems;
        }
    }

    private class CvsLabelProvider extends LabelProvider implements ITableLabelProvider {
        /**
         * @param idx index
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object obj, int idx) {
            String result = "";
            if(obj instanceof Exception){
                return (idx == 0) ? "An Exception has occurred! See error log." : "";
            }
            CvsModuleData data = (CvsModuleData)obj;
            switch (idx) {
            case 0: result = data.getSourcePath(); break;
            case 1: result = data.getBranch(); break;
            case 2: result = data.getLabel(); break;
            default: break;
            }
            return result;
        }
        /**
         * @param idx index
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage(Object obj, int idx) {
            return null;
        }
    }

}
