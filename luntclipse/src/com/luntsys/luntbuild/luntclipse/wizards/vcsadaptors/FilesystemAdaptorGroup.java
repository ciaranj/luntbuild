package com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.model.CvsModuleData;
import com.luntsys.luntbuild.luntclipse.model.VcsProjectData;
import com.luntsys.luntbuild.luntclipse.wizards.ProjectWizardPage;

/**
 * Filesystem Adaptor
 *
 * @author Lubos Pochman
 *
 */
public class FilesystemAdaptorGroup extends VcsAdaptorGroup {

    private Text sourceDirText = null;

    /**
     * @param parent
     */
    public void addControls(Composite parent) {
        boolean hasData = (this.editData != null && this.editData.size() > 0 &&
                LuntclipseConstants.FILESYSTEM_ADAPTOR == ((VcsProjectData)this.editData.get(0)).getType());

        this.sourceDirText = new Text(ProjectWizardPage.groupIn(parent, "Source directory:",
                new GridLayout(1, true)), SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.sourceDirText.setText(((VcsProjectData)this.editData.get(0)).getFilesystemSource());
        this.sourceDirText.addModifyListener(this);
        GridData gd = new GridData();
        gd.widthHint = 250;
        this.sourceDirText.setLayoutData(gd);
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
        if (this.sourceDirText.getText().trim().length() == 0) {
            super.page.updateStatus("Please specify source directory!");
            return;
        }
        super.page.updateStatus(null);
    }

    /**
     * @return true if can flip to next
     *
     */
    public boolean canFlipToNextPage() {
        String source = this.sourceDirText.getText().trim();
        return source.length() != 0;
    }

    /**
     * @param data VCS project data to set
     *
     */
    public void setData(VcsProjectData data) {
        if (this.hasChanged)
            data.setFilesystemSource(this.sourceDirText.getText().trim());
        this.hasChanged = false;
    }

    /**
     * Set controls/text from data
     * @param data adaptor data
     */
    public void setDataText(VcsProjectData data) {
        this.sourceDirText.setText(data.getFilesystemSource());
    }

    /**
     * Clears controls/text
     */
    public void clearDataText() {
        this.sourceDirText.setText("");
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
