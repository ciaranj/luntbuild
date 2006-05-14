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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.model.BuildMessenger;
import com.luntsys.luntbuild.luntclipse.model.VcsProjectData;
import com.luntsys.luntbuild.luntclipse.model.VisualSourcesafeModuleData;
import com.luntsys.luntbuild.luntclipse.wizards.ProjectWizardPage;

/**
 * Visual Sourcesafe Adaptor
 *
 * @author Lubos Pochman
 *
 */
public class VisualSourcesafeAdaptorGroup extends VcsAdaptorGroup {

    private Text pathText = null;
    private Text userText = null;
    private Text passwordText = null;
    private Text datetimeText = null;
    private Text exePathText = null;
    private Text moduleSourcePathText = null;
    private Text moduleLabelText = null;
    private Text moduleDestinationPathText = null;

    private VisualSourcesafeModuleData selectedModule = null;

    /**
     * @param parent
     *
     */
    public void addControls(Composite parent) {
        boolean hasData = (this.editData != null && this.editData.size() > 0 &&
                LuntclipseConstants.VSS_ADAPTOR == ((VcsProjectData)this.editData.get(0)).getType());

        this.pathText = new Text(ProjectWizardPage.groupFill(parent, "Sourcesafe path:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.pathText.setText(((VcsProjectData)this.editData.get(0)).getSourcesafePath());
        this.pathText.addModifyListener(this);

        this.userText = new Text(ProjectWizardPage.groupFill(parent, "User:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.userText.setText(((VcsProjectData)this.editData.get(0)).getUser());
        this.userText.addModifyListener(this);

        this.passwordText =
            new Text(ProjectWizardPage.groupFill(parent, "Password:"), SWT.BORDER | SWT.SINGLE);
        this.passwordText.setEchoChar('*');
        if (hasData)
            this.passwordText.setText(((VcsProjectData)this.editData.get(0)).getPassword());
        this.passwordText.addModifyListener(this);

        this.datetimeText = new Text(ProjectWizardPage.groupFill(parent, "Datetime format:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.datetimeText.setText(((VcsProjectData)this.editData.get(0)).getSourcesafeDatetime());
        this.datetimeText.addModifyListener(this);

        this.exePathText = new Text(ProjectWizardPage.groupFill(parent, "Path for ss.exe:"),
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
        if (this.pathText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify Sourcesafe path!");
            return;
        }
        if (this.userText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify User name!");
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
        String path = this.pathText.getText().trim();
        String user = this.userText.getText().trim();
        return path.length() != 0 && user.length() != 0 && this.modulesList.size() > 0;
    }

    /**
     * @param data VCS project data to set
     *
     */
    public void setData(VcsProjectData data) {
        if (this.hasChanged) {
            data.setSourcesafePath(this.pathText.getText().trim());
            data.setUser(this.userText.getText().trim());
            data.setPassword(this.passwordText.getText().trim());
            data.setSourcesafeDatetime(this.datetimeText.getText().trim());
            data.setExePath(this.exePathText.getText().trim());
        }
        this.hasChanged = false;
    }

    /**
     * Set controls/text from data
     * @param data adaptor data
     */
    public void setDataText(VcsProjectData data) {
        this.pathText.setText(data.getSourcesafePath());
        this.userText.setText(data.getUser());
        this.passwordText.setText(data.getPassword());
        this.datetimeText.setText(data.getSourcesafeDatetime());
        this.exePathText.setText(data.getExePath());
        setModuleText();
    }

    /**
     * Clears controls/text
     */
    public void clearDataText() {
        this.pathText.setText("");
        this.userText.setText("");
        this.passwordText.setText("");
        this.datetimeText.setText("");
        this.exePathText.setText("");
        clearModuleData();
    }

    public boolean usesModules() { return true; }

    public void addModuleControl(Composite parent) {
        this.moduleSourcePathText =
            new Text(ProjectWizardPage.groupFill(parent, "Source path:"), SWT.BORDER | SWT.SINGLE);
        this.moduleSourcePathText.addModifyListener(this);
        this.moduleLabelText =
            new Text(ProjectWizardPage.groupFill(parent, "Label:"), SWT.BORDER | SWT.SINGLE);
        this.moduleLabelText.addModifyListener(this);
        this.moduleDestinationPathText =
            new Text(ProjectWizardPage.groupFill(parent, "Destination path:"), SWT.BORDER | SWT.SINGLE);
        this.moduleDestinationPathText.addModifyListener(this);
    }

    public void addModuleTableColumns(Table table) {
        table.addSelectionListener(
                new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e){
                        ISelection selection = VisualSourcesafeAdaptorGroup.this.tableViewer.getSelection();
                        selectedModule =
                            (VisualSourcesafeModuleData)((IStructuredSelection)selection).getFirstElement();
                        setModuleText();
                    }
                }
        );
        TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        column.setText("Source path");
        column.setWidth(100);
        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText("Label");
        column.setWidth(50);
        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText("Destination path");
        column.setWidth(50);
    }

    public IStructuredContentProvider getModuleViewProvider() {
        return new VisualSourcesafeViewProvider();
    }

    public ITableLabelProvider getModuleViewLabelProvider() {
        return new VisualSourcesafeLabelProvider();
    }

    public String[] getModuleColumnNames() {
        String[] colNames = {
                "VSS_MODULE_SOURCE_PATH",
                "VSS_MODULE_LABEL",
                "VSS_MODULE_DESTINATION_PATH"};
        return colNames;
    }

    /**
     * @return selected module
     */
    public VisualSourcesafeModuleData getSelectedModule() {
        return this.selectedModule;
    }

    /**
     * Creates a module from module data
     */
    public void createModule() {
        if (!validateModule()) return;
        this.page.dialogChanged();
        this.selectedModule = setModuleData(new VisualSourcesafeModuleData());
        this.modulesList.add(this.selectedModule);
        this.tableViewer.refresh();
        clearModuleData();
    }

    private void clearModuleData() {
        this.moduleSourcePathText.setText("");
        this.moduleLabelText.setText("");
        this.moduleDestinationPathText.setText("");
    }

    private void setModuleText() {
        this.moduleSourcePathText.setText(this.selectedModule.getSrcPath());
        this.moduleLabelText.setText(this.selectedModule.getLabel());
        this.moduleDestinationPathText.setText(this.selectedModule.getDestPath());
    }

    private boolean validateModule() {
        if (this.moduleSourcePathText == null) return false;
        if (this.moduleSourcePathText.getText().trim().length() == 0) {
            this.page.updateStatus("Visual Sourcesafe Module Source path needs to be specified!");
            return false;
        }

        return true;
    }

    private VisualSourcesafeModuleData setModuleData(VisualSourcesafeModuleData data) {
        data.setSrcPath(this.moduleSourcePathText.getText().trim());
        data.setLabel(this.moduleLabelText.getText().trim());
        data.setDestPath(this.moduleDestinationPathText.getText().trim());
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

    private class VisualSourcesafeViewProvider implements IStructuredContentProvider {
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
            if (VisualSourcesafeAdaptorGroup.this.modulesList == null) return new Object[0];
            Object[] elems = new Object[VisualSourcesafeAdaptorGroup.this.modulesList.size()];
            int i = 0;
            for (Iterator iter = VisualSourcesafeAdaptorGroup.this.modulesList.iterator(); iter.hasNext();) {
                VisualSourcesafeModuleData mod = (VisualSourcesafeModuleData) iter.next();
                elems[i++] = mod;
            }
            return elems;
        }
    }

    private class VisualSourcesafeLabelProvider extends LabelProvider implements ITableLabelProvider {
        /**
         * @param idx index
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object obj, int idx) {
            String result = "";
            if(obj instanceof Exception){
                return (idx == 0) ? "An Exception has occurred! See error log." : "";
            }
            VisualSourcesafeModuleData data = (VisualSourcesafeModuleData)obj;
            switch (idx) {
            case 0: result = data.getSrcPath(); break;
            case 1: result = data.getLabel(); break;
            case 2: result = data.getDestPath(); break;
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
