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
import com.luntsys.luntbuild.luntclipse.model.StarTeamModuleData;
import com.luntsys.luntbuild.luntclipse.model.VcsProjectData;
import com.luntsys.luntbuild.luntclipse.wizards.ProjectWizardPage;
import com.luntsys.luntbuild.luntclipse.wizards.RadioGroupInfo;

/**
 * StarTeam Adaptor
 *
 * @author Lubos Pochman
 *
 */
public class StarTeamAdaptorGroup extends VcsAdaptorGroup {

    private Text locationText = null;
    private Text userText = null;
    private Text passwordText = null;
    private RadioGroupInfo convertEolGroup = null;
    private Text moduleViewText = null;
    private Text moduleSourcePathText = null;
    private Text moduleLabelText = null;
    private Text moduleDestinationPathText = null;

    private StarTeamModuleData selectedModule = null;

    /**
     * @param parent
     *
     */
    public void addControls(Composite parent) {
        boolean hasData = (this.editData != null && this.editData.size() > 0 &&
                LuntclipseConstants.STARTEAM_ADAPTOR == ((VcsProjectData)this.editData.get(0)).getType());

        this.locationText = new Text(ProjectWizardPage.groupFill(parent, "Project location:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.locationText.setText(((VcsProjectData)this.editData.get(0)).getStarTeamLocation());
        this.locationText.addModifyListener(this);

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

        this.convertEolGroup =
            ProjectWizardPage.radioGroup(ProjectWizardPage.groupIn(parent,
                    "Convert EOL?", new GridLayout(2, false)), new String[] {"Yes", "No"}, null);
        if (hasData)
            this.convertEolGroup.buttons[((VcsProjectData)this.editData.get(0)).
                                         getStarTeamConvertEol()].setSelection(true);
        else
            this.convertEolGroup.buttons[0].setSelection(true);
        for (int i = 0; i < this.convertEolGroup.buttons.length; i++) {
            Button btn = this.convertEolGroup.buttons[i];
            btn.addSelectionListener(this);
        }
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
        if (this.locationText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify Project location!");
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
        String location = this.locationText.getText().trim();
        String user = this.userText.getText().trim();
        return location.length() != 0 && user.length() != 0 && this.modulesList.size() > 0;
    }

    /**
     * @param data VCS project data to set
     *
     */
    public void setData(VcsProjectData data) {
        if (this.hasChanged) {
            data.setStarTeamLocation(this.locationText.getText().trim());
            data.setUser(this.userText.getText().trim());
            data.setPassword(this.passwordText.getText().trim());
            data.setStarTeamConvertEol(this.convertEolGroup.getSelectionIndex());
        }
        this.hasChanged = false;
    }

    /**
     * Set controls/text from data
     * @param data adaptor data
     */
    public void setDataText(VcsProjectData data) {
        this.locationText.setText(data.getStarTeamLocation());
        this.userText.setText(data.getUser());
        this.passwordText.setText(data.getPassword());
        this.convertEolGroup.select(data.getStarTeamConvertEol());
        setModuleText();
    }

    /**
     * Clears controls/text
     */
    public void clearDataText() {
        this.locationText.setText("");
        this.userText.setText("");
        this.passwordText.setText("");
        this.convertEolGroup.deselectAll();
        clearModuleData();
    }

    public boolean usesModules() { return true; }

    public void addModuleControl(Composite parent) {
        this.moduleViewText =
            new Text(ProjectWizardPage.groupFill(parent, "StarTeam view:"), SWT.BORDER | SWT.SINGLE);
        this.moduleViewText.addModifyListener(this);
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
                        ISelection selection = StarTeamAdaptorGroup.this.tableViewer.getSelection();
                        selectedModule =
                            (StarTeamModuleData)((IStructuredSelection)selection).getFirstElement();
                        setModuleText();
                    }
                }
        );
        TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        column.setText("StarTeam view");
        column.setWidth(100);
        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText("Depot path");
        column.setWidth(50);
        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText("Label");
        column.setWidth(50);
        column = new TableColumn(table, SWT.LEFT, 3);
        column.setText("Destination path");
        column.setWidth(50);
    }

    public IStructuredContentProvider getModuleViewProvider() {
        return new StarTeamViewProvider();
    }

    public ITableLabelProvider getModuleViewLabelProvider() {
        return new StarTeamLabelProvider();
    }

    public String[] getModuleColumnNames() {
        String[] colNames = {
                "STARTEAM_MODULE_VIEW",
                "STARTEAM_MODULE_DEPOT_PATH",
                "STARTEAM_MODULE_LABEL",
                "STARTEAM_MODULE_DESTINATION_PATH"};
        return colNames;
    }

    /**
     * @return selected module
     */
    public StarTeamModuleData getSelectedModule() {
        return this.selectedModule;
    }

    /**
     * Creates a module from module data
     */
    public void createModule() {
        if (!validateModule()) return;
        this.page.dialogChanged();
        this.selectedModule = setModuleData(new StarTeamModuleData());
        this.modulesList.add(this.selectedModule);
        this.tableViewer.refresh();
        clearModuleData();
    }

    private void clearModuleData() {
        this.moduleViewText.setText("");
        this.moduleSourcePathText.setText("");
        this.moduleLabelText.setText("");
        this.moduleDestinationPathText.setText("");
    }

    private void setModuleText() {
        this.moduleViewText.setText(this.selectedModule.getStarteamView());
        this.moduleSourcePathText.setText(this.selectedModule.getSrcPath());
        this.moduleLabelText.setText(this.selectedModule.getLabel());
        this.moduleDestinationPathText.setText(this.selectedModule.getDestPath());
    }

    private boolean validateModule() {
        if (this.moduleSourcePathText == null) return false;
        if (this.moduleSourcePathText.getText().trim().length() == 0) {
            this.page.updateStatus("StarTeam Module Source path needs to be specified!");
            return false;
        }

        return true;
    }

    private StarTeamModuleData setModuleData(StarTeamModuleData data) {
        data.setStarteamView(this.moduleViewText.getText().trim());
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

    private class StarTeamViewProvider implements IStructuredContentProvider {
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
            if (StarTeamAdaptorGroup.this.modulesList == null) return new Object[0];
            Object[] elems = new Object[StarTeamAdaptorGroup.this.modulesList.size()];
            int i = 0;
            for (Iterator iter = StarTeamAdaptorGroup.this.modulesList.iterator(); iter.hasNext();) {
                StarTeamModuleData mod = (StarTeamModuleData) iter.next();
                elems[i++] = mod;
            }
            return elems;
        }
    }

    private class StarTeamLabelProvider extends LabelProvider implements ITableLabelProvider {
        /**
         * @param idx index
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object obj, int idx) {
            String result = "";
            if(obj instanceof Exception){
                return (idx == 0) ? "An Exception has occurred! See error log." : "";
            }
            StarTeamModuleData data = (StarTeamModuleData)obj;
            switch (idx) {
            case 0: result = data.getStarteamView(); break;
            case 1: result = data.getSrcPath(); break;
            case 2: result = data.getLabel(); break;
            case 3: result = data.getDestPath(); break;
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
