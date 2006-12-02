package com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.model.BuildMessenger;
import com.luntsys.luntbuild.luntclipse.model.PerforceModuleData;
import com.luntsys.luntbuild.luntclipse.model.VcsProjectData;
import com.luntsys.luntbuild.luntclipse.wizards.ProjectWizardPage;

/**
 * Perforce Adaptor
 *
 * @author Lubos Pochman
 *
 */
public class PerforceAdaptorGroup extends VcsAdaptorGroup {

    private Text portText = null;
    private Text userText = null;
    private Text passwordText = null;
    private CCombo lineEnd = null;
    private Text exePathText = null;
    private Text moduleDepotPathText = null;
    private Text moduleLabelText = null;
    private Text moduleClientPathText = null;

    PerforceModuleData selectedModule = null;
    /**
     * @param parent
     *
     */
    public void addControls(Composite parent) {

        boolean hasData = (this.editData != null && this.editData.size() > 0 &&
                LuntclipseConstants.PERFORCE_ADAPTOR == ((VcsProjectData)this.editData.get(0)).getType());

        this.portText = new Text(ProjectWizardPage.groupFill(parent, "Perforce port:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.portText.setText(((VcsProjectData)this.editData.get(0)).getPerforcePort());
        this.portText.addModifyListener(this);

        this.userText = new Text(ProjectWizardPage.groupFill(parent, "User name:"), SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.userText.setText(((VcsProjectData)this.editData.get(0)).getUser());
        this.userText.addModifyListener(this);

        this.passwordText =
            new Text(ProjectWizardPage.groupFill(parent, "Password:"), SWT.BORDER | SWT.SINGLE);
        this.passwordText.setEchoChar('*');
        if (hasData)
            this.passwordText.setText(((VcsProjectData)this.editData.get(0)).getPassword());
        this.passwordText.addModifyListener(this);

        this.lineEnd = new CCombo(ProjectWizardPage.groupFill(parent, "Line end:"), SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.perforceLineEnd.length; i++) {
            String type = LuntclipseConstants.perforceLineEnd[i];
            this.lineEnd.add(type);
        }
        if (hasData)
            this.lineEnd.select(((VcsProjectData)this.editData.get(0)).getPerforceLineEnd());
        else
            this.lineEnd.select(0);
        this.lineEnd.addSelectionListener(this);

        this.exePathText = new Text(ProjectWizardPage.groupFill(parent, "Path for p4 executable:"),
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
        if (this.portText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify Cvs root!");
            return;
        }
        if (this.userText.getText().trim().length() == 0) {
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
        String port = this.portText.getText().trim();
        String user = this.userText.getText().trim();
        return port.length() != 0 && user.length() != 0 && this.modulesList.size() > 0;
    }

    /**
     * @param data VCS project data to set
     *
     */
    public void setData(VcsProjectData data) {
        if (this.hasChanged) {
            data.setPerforcePort(this.portText.getText().trim());
            data.setUser(this.userText.getText().trim());
            data.setPassword(this.passwordText.getText().trim());
            data.setPerforceLineEnd(this.lineEnd.getSelectionIndex());
            data.setExePath(this.exePathText.getText().trim());
        }
        this.hasChanged = false;
    }

    /**
     * Set controls/text from data
     * @param data adaptor data
     */
    public void setDataText(VcsProjectData data) {
        this.portText.setText(data.getPerforcePort());
        this.userText.setText(data.getUser());
        this.passwordText.setText(data.getPassword());
        this.lineEnd.select(data.getPerforceLineEnd());
        this.exePathText.setText(data.getExePath());
        setModuleText();
    }

    /**
     * Clears controls/text
     */
    public void clearDataText() {
        this.portText.setText("");
        this.userText.setText("");
        this.passwordText.setText("");
        this.lineEnd.deselectAll();
        this.exePathText.setText("");
        clearModuleData();
    }

    public boolean usesModules() { return true; }

    public void addModuleControl(Composite parent) {
        this.moduleDepotPathText =
            new Text(ProjectWizardPage.groupFill(parent, "Depot path:"), SWT.BORDER | SWT.SINGLE);
        this.moduleDepotPathText.addModifyListener(this);
        this.moduleLabelText =
            new Text(ProjectWizardPage.groupFill(parent, "Label:"), SWT.BORDER | SWT.SINGLE);
        this.moduleLabelText.addModifyListener(this);
        this.moduleClientPathText =
            new Text(ProjectWizardPage.groupFill(parent, "Client path:"), SWT.BORDER | SWT.SINGLE);
        this.moduleClientPathText.addModifyListener(this);
    }

    public void addModuleTableColumns(Table table) {
        table.addSelectionListener(
                new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e){
                        ISelection selection = PerforceAdaptorGroup.this.tableViewer.getSelection();
                        selectedModule =
                            (PerforceModuleData)((IStructuredSelection)selection).getFirstElement();
                        setModuleText();
                    }
                }
        );
        TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        column.setText("Depot path");
        column.setWidth(100);
        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText("Label");
        column.setWidth(50);
        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText("Client path");
        column.setWidth(50);
    }

    public IStructuredContentProvider getModuleViewProvider() {
        return new PerforceViewProvider();
    }

    public ITableLabelProvider getModuleViewLabelProvider() {
        return new PerforceLabelProvider();
    }

    public String[] getModuleColumnNames() {
        String[] colNames = {
                "PERFORCE_MODULE_DEPOT_PATH",
                "PERFORCE_MODULE_LABEL",
                "PERFORCE_MODULE_CLIENT_PATH"};
        return colNames;
    }

    /**
     * @return selected module
     */
    public PerforceModuleData getSelectedModule() {
        return this.selectedModule;
    }

    /**
     * Creates a module from module data
     */
    public void createModule() {
        if (!validateModule()) return;
        this.page.dialogChanged();
        this.selectedModule = setModuleData(new PerforceModuleData());
        this.modulesList.add(this.selectedModule);
        this.tableViewer.refresh();
        clearModuleData();
    }

    private void clearModuleData() {
        this.moduleDepotPathText.setText("");
        this.moduleLabelText.setText("");
        this.moduleClientPathText.setText("");
    }

    private void setModuleText() {
        this.moduleDepotPathText.setText(this.selectedModule.getDepotPath());
        this.moduleLabelText.setText(this.selectedModule.getLabel());
        this.moduleClientPathText.setText(this.selectedModule.getClientPath());
    }

    private boolean validateModule() {
        if (this.moduleDepotPathText == null) return false;
        if (this.moduleDepotPathText.getText().trim().length() == 0) {
            this.page.updateStatus("Perforce Module Depot path needs to be specified!");
            return false;
        }
        if (this.moduleClientPathText != null &&
                this.moduleClientPathText.getText().trim().length() == 0) {
            this.page.updateStatus("Perforce Module Client path needs to be specified!");
            return false;
        }

        return true;
    }

    private PerforceModuleData setModuleData(PerforceModuleData data) {
        data.setDepotPath(this.moduleDepotPathText.getText().trim());
        data.setLabel(this.moduleLabelText.getText().trim());
        data.setClientPath(this.moduleClientPathText.getText().trim());
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

    private class PerforceViewProvider implements IStructuredContentProvider {
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
            if (PerforceAdaptorGroup.this.modulesList == null) return new Object[0];
            Object[] elems = new Object[PerforceAdaptorGroup.this.modulesList.size()];
            int i = 0;
            for (Iterator iter = PerforceAdaptorGroup.this.modulesList.iterator(); iter.hasNext();) {
                PerforceModuleData mod = (PerforceModuleData) iter.next();
                elems[i++] = mod;
            }
            return elems;
        }
    }

    private class PerforceLabelProvider extends LabelProvider implements ITableLabelProvider {
        /**
         * @param idx index
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object obj, int idx) {
            String result = "";
            if(obj instanceof Exception){
                return (idx == 0) ? "An Exception has occurred! See error log." : "";
            }
            PerforceModuleData data = (PerforceModuleData)obj;
            switch (idx) {
            case 0: result = data.getDepotPath(); break;
            case 1: result = data.getLabel(); break;
            case 2: result = data.getClientPath(); break;
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
