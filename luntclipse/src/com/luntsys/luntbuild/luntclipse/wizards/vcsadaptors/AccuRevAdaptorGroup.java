package com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors;

import java.util.Iterator;
import java.util.List;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.model.AccuRevModuleData;
import com.luntsys.luntbuild.luntclipse.model.BuildMessenger;
import com.luntsys.luntbuild.luntclipse.model.VcsProjectData;
import com.luntsys.luntbuild.luntclipse.wizards.ProjectWizardPage;

/**
 * AccuRev Adaptor
 *
 * @author Lubos Pochman
 *
 */
public class AccuRevAdaptorGroup extends VcsAdaptorGroup {

    private Text userText = null;
    private Text passwordText = null;
    private Text modulePathText = null;
    private Text moduleLabelText = null;
    private Text moduleDepotText = null;
    private Text moduleBackingStreamText = null;
    private Text moduleBuildStreamText = null;

    AccuRevModuleData selectedModule = null;

    /**
     * @param parent
     *
     */
    public void addControls(Composite parent) {

        boolean hasData = (this.editData != null && this.editData.size() > 0 &&
                LuntclipseConstants.ACCUREV_ADAPTOR == ((VcsProjectData)this.editData.get(0)).getType());

        Group g;
        this.userText = new Text(g = ProjectWizardPage.groupFill(parent, "User:"), SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.userText.setText(((VcsProjectData)this.editData.get(0)).getUser());
        this.userText.addModifyListener(this);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 200;
        g.setLayoutData(gd);

        this.passwordText =
            new Text(g = ProjectWizardPage.groupFill(parent, "Password:"), SWT.BORDER | SWT.SINGLE);
        this.passwordText.setEchoChar('*');
        if (hasData)
            this.passwordText.setText(((VcsProjectData)this.editData.get(0)).getPassword());
        this.passwordText.addModifyListener(this);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 200;
        g.setLayoutData(gd);
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
        if (this.userText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify user name of the VCS adapter!");
            return;
        }
        if (this.passwordText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify password of the VCS adapter!");
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
        String name = this.userText.getText().trim();
        String password = this.passwordText.getText().trim();
        return name.length() != 0 && password.length() != 0 && this.modulesList.size() > 0;
    }

    /**
     * @param data VCS project data to set
     *
     */
    public void setData(VcsProjectData data) {
        if (this.hasChanged) {
            data.setUser(this.userText.getText().trim());
            data.setPassword(this.passwordText.getText().trim());
        }
        this.hasChanged = false;
    }

    /**
     * Set controls/text from data
     * @param data adaptor data
     */
    public void setDataText(VcsProjectData data) {
        this.userText.setText(data.getUser());
        this.passwordText.setText(data.getPassword());
        List modules = data.getModules();
        if (modules != null && modules.size() > 0)
            this.selectedModule = (AccuRevModuleData)modules.get(0);
        else
            this.selectedModule = null;
        setModuleText();
    }

    /**
     * Clears controls/text
     */
    public void clearDataText() {
        this.userText.setText("");
        this.passwordText.setText("");
        clearModuleData();
    }

    public boolean usesModules() { return true; }

    public void addModuleControl(Composite parent) {
        this.modulePathText =
            new Text(ProjectWizardPage.groupFill(parent, "Module path:"), SWT.BORDER | SWT.SINGLE);
        this.modulePathText.addModifyListener(this);
        this.moduleLabelText =
            new Text(ProjectWizardPage.groupFill(parent, "Label:"), SWT.BORDER | SWT.SINGLE);
        this.moduleLabelText.addModifyListener(this);
        this.moduleDepotText =
            new Text(ProjectWizardPage.groupFill(parent, "Depot:"), SWT.BORDER | SWT.SINGLE);
        this.moduleDepotText.addModifyListener(this);
        this.moduleBackingStreamText =
            new Text(ProjectWizardPage.groupFill(parent, "Backing stream:"), SWT.BORDER | SWT.SINGLE);
        this.moduleBackingStreamText.addModifyListener(this);
        this.moduleBuildStreamText =
            new Text(ProjectWizardPage.groupFill(parent, "Build stream:"), SWT.BORDER | SWT.SINGLE);
        this.moduleBuildStreamText.addModifyListener(this);
    }

    public void addModuleTableColumns(Table table) {
        table.addSelectionListener(
                new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e){
                        ISelection selection = AccuRevAdaptorGroup.this.tableViewer.getSelection();
                        selectedModule =
                            (AccuRevModuleData)((IStructuredSelection)selection).getFirstElement();
                        setModuleText();
                    }
                }
        );
        TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        column.setText("Module path");
        column.setWidth(100);
        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText("Label");
        column.setWidth(50);
        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText("Depot");
        column.setWidth(50);
        column = new TableColumn(table, SWT.LEFT, 3);
        column.setText("Backing stream");
        column.setWidth(50);
        column = new TableColumn(table, SWT.LEFT, 4);
        column.setText("Build stream");
        column.setWidth(50);
    }

    public IStructuredContentProvider getModuleViewProvider() {
        return new AccuRevViewProvider();
    }

    public ITableLabelProvider getModuleViewLabelProvider() {
        return new AccuRevLabelProvider();
    }

    public String[] getModuleColumnNames() {
        String[] colNames = {
                "ACCUREV_MODULE_PATH",
                "ACCUREV_MODULE_LABEL",
                "ACCUREV_MODULE_DEPOT",
                "ACCUREV_MODULE_BACKING_STREAM",
                "ACCUREV_MODULE_BUILD_STREAM"};
        return colNames;
    }

    /**
     * @return selected module
     */
    public AccuRevModuleData getSelectedModule() {
        return this.selectedModule;
    }

    /**
     * Creates a module from module data
     */
    public void createModule() {
        if (!validateModule()) return;
        this.page.dialogChanged();

        this.selectedModule = setModuleData(new AccuRevModuleData());
        this.modulesList.add(this.selectedModule);
        this.tableViewer.refresh();
        clearModuleData();
    }

    private void clearModuleData() {
        this.modulePathText.setText("");
        this.moduleLabelText.setText("");
        this.moduleDepotText.setText("");
        this.moduleBackingStreamText.setText("");
        this.moduleBuildStreamText.setText("");
    }

    private void setModuleText() {
        if (this.selectedModule == null) return;
        this.modulePathText.setText(this.selectedModule.getSrcPath());
        this.moduleLabelText.setText(this.selectedModule.getLabel());
        this.moduleDepotText.setText(this.selectedModule.getDepot());
        this.moduleBackingStreamText.setText(this.selectedModule.getBackingStream());
        this.moduleBuildStreamText.setText(this.selectedModule.getBuildStream());
    }

    private boolean validateModule() {
        if (this.moduleDepotText == null) return false;
        if (this.moduleDepotText.getText().trim().length() == 0) {
            this.page.updateStatus("AccuRev Module Depot needs to be specified!");
            return false;
        }
        if (this.moduleBackingStreamText.getText().trim().length() == 0) {
            this.page.updateStatus("AccuRev Module Backing Stream needs to be specified!");
            return false;
        }
        if (this.moduleBuildStreamText.getText().trim().length() == 0) {
            this.page.updateStatus("AccuRev Module Build Stream needs to be specified!");
            return false;
        }

        return true;
    }

    private AccuRevModuleData setModuleData(AccuRevModuleData data) {
        data.setSrcPath(this.modulePathText.getText().trim());
        data.setLabel(this.moduleLabelText.getText().trim());
        data.setDepot(this.moduleDepotText.getText().trim());
        data.setBackingStream(this.moduleBackingStreamText.getText().trim());
        data.setBuildStream(this.moduleBuildStreamText.getText().trim());
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

    private class AccuRevViewProvider implements IStructuredContentProvider {
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
            if (AccuRevAdaptorGroup.this.modulesList == null) return new Object[0];
            Object[] elems = new Object[AccuRevAdaptorGroup.this.modulesList.size()];
            int i = 0;
            for (Iterator iter = AccuRevAdaptorGroup.this.modulesList.iterator(); iter.hasNext();) {
                AccuRevModuleData mod = (AccuRevModuleData) iter.next();
                elems[i++] = mod;
            }
            return elems;
        }
    }

    private class AccuRevLabelProvider extends LabelProvider implements ITableLabelProvider {
        /**
         * @param idx index
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object obj, int idx) {
            String result = "";
            if(obj instanceof Exception){
                return (idx == 0) ? "An Exception has occurred! See error log." : "";
            }
            AccuRevModuleData data = (AccuRevModuleData)obj;
            switch (idx) {
            case 0: result = data.getSrcPath(); break;
            case 1: result = data.getLabel(); break;
            case 2: result = data.getDepot(); break;
            case 3: result = data.getBackingStream(); break;
            case 4: result = data.getBuildStream(); break;
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
