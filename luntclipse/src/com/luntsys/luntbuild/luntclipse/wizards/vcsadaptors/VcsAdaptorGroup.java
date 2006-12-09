package com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.model.VcsProjectData;
import com.luntsys.luntbuild.luntclipse.wizards.ProjectWizardPage;
import com.luntsys.luntbuild.luntclipse.wizards.VcsProjectPage;

/**
 * VcsAdaptor base class
 *
 * @author Lubos Pochman
 *
 */
public abstract class VcsAdaptorGroup implements SelectionListener, ModifyListener {

    int index = -1;
    VcsProjectPage page = null;
    Composite top = null;
    Group group = null;
    Group moduleGroup = null;
    Table moduleTable = null;
    protected TableViewer tableViewer = null;
    Text quietPeriod = null;

    protected boolean hasChanged = false;

    protected List editData = null;
    protected List modulesList = new ArrayList();


    /**
     * @param thepage
     * @param parent
     * @param idx
     * @param vcsName
     * @param theEditData
     */
    public void createControls(VcsProjectPage thepage, Composite parent, int idx,
            String vcsName, List theEditData) {
        this.page = thepage;
        this.index = idx;
        this.editData = theEditData;

        this.top = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 2;
        this.top.setLayout(layout);

        boolean hasData = (this.editData != null && this.editData.size() > 0 &&
                idx == ((VcsProjectData)this.editData.get(0)).getType());
        // Adaptor controls
        this.group = ProjectWizardPage.groupIn(this.top, vcsName, new GridLayout(1, true));
        this.group.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        addControls(this.group);
        this.quietPeriod = new Text(ProjectWizardPage.groupIn(this.group, "Quiet period:"),
                SWT.BORDER | SWT.SINGLE);
        if (hasData)
            this.quietPeriod.setText(((VcsProjectData)this.editData.get(0)).getQuietPeriod());
        this.quietPeriod.addModifyListener(this);
        if (!usesModules()) return;

        // Module controls
        this.moduleGroup = ProjectWizardPage.groupIn(this.top, vcsName + " Modules", new GridLayout(1, true));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        this.moduleGroup.setLayoutData(gd);

        if (hasData)
            this.modulesList = ((VcsProjectData)this.editData.get(0)).getModules();
        this.moduleTable =
            new Table(this.moduleGroup, SWT.SINGLE| SWT.FULL_SELECTION| SWT.H_SCROLL | SWT.V_SCROLL);
        this.moduleTable.setHeaderVisible(true);
        addModuleTableColumns(this.moduleTable);
        this.tableViewer = new TableViewer(this.moduleTable);
        this.tableViewer.setUseHashlookup(true);
        this.tableViewer.setContentProvider(getModuleViewProvider());
        this.tableViewer.setLabelProvider(getModuleViewLabelProvider());
        this.tableViewer.setSorter(null);
        this.tableViewer.setInput(parent);
        this.tableViewer.setColumnProperties(getModuleColumnNames());
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 60;
        gd.widthHint = 200;
        this.moduleTable.setLayoutData(gd);

        Composite buttonsComp = new Composite(this.moduleGroup, SWT.NULL);
        RowLayout buttonLayout = new RowLayout(SWT.HORIZONTAL);
        buttonsComp.setLayout(buttonLayout);
        Button button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.CREATE_IMG));
        button.setToolTipText("Add/Create a new Vcs module.");
        button.addSelectionListener(
            new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    getPage().createModule();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.MODIFY_IMG));
        button.setToolTipText("Modify selected Vcs module.");
        button.addSelectionListener(
            new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e) {
                    getPage().modifySelectedModule();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.DELETE_IMG));
        button.setToolTipText("Delete selected Vcs module.");
        button.addSelectionListener(
            new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    getPage().deleteSelectedModule();
                }
            });

        addModuleControl(this.moduleGroup);

        if (hasData) {
            List modules = ((VcsProjectData)this.editData.get(0)).getModules();
            if (modules != null && modules.size() > 0)
                this.moduleTable.select(0);
        }
    }

    VcsProjectPage getPage() {
        return this.page;
    }

    /** Add Adaptor controls
     * @param parent widget
     */
    public abstract void addControls(Composite parent);

    /**
     * @return tru if adaptor uses modules
     */
    public abstract boolean usesModules();

    /** Add Module controls
     * @param parent widget
     */
    public abstract void addModuleControl(Composite parent);

    /** Add Module table columns
     * @param table
     */
    public abstract void addModuleTableColumns(Table table);

    /**
     * @return module view data provider
     */
    public abstract IStructuredContentProvider getModuleViewProvider();

    /**
     * @return module view label provider
     */
    public abstract ITableLabelProvider getModuleViewLabelProvider();

    /**
     * @return module column names
     */
    public abstract String[] getModuleColumnNames();

    /**
     * Adaptor dialog changed
     */
    public abstract void dialogChanged();

    /**
     * Check status of the page
     */
    public abstract void checkStatus();

    /**
     * @return true if Adaptor can flip to next page
     */
    public abstract boolean canFlipToNextPage();

    /**
     * @param data adaptor data
     */
    public abstract void setData(VcsProjectData data);

    /**
     * @return list of modules
     */
    public List getModules() {
        return this.modulesList;
    }

    /**
     * Set controls/text from data
     * @param data adaptor data
     */
    public abstract void setDataText(VcsProjectData data);

    /**
     * Clears controls/text
     */
    public abstract void clearDataText();

    /**
     * Creates a module from module data
     */
    public abstract void createModule();

    /**
     * Modifies selected module
     */
    public abstract void modifySelectedModule();

    /**
     * Deletes selected module
     */
    public abstract void deleteSelectedModule();

    /**
     * @return Returns the top composite.
     */
    public final Composite getTop() {
        return this.top;
    }

    /**
     * @return quiet period
     */
    public final String getQuietPeriod() {
        return this.quietPeriod.getText().trim();
    }

    /**
     * @param period
     */
    public final void setQuietPeriod(String period) {
        this.quietPeriod.setText(period);
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
