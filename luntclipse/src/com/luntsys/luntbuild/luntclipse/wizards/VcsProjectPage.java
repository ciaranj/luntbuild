package com.luntsys.luntbuild.luntclipse.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.VcsProjectData;
import com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors.VcsAdaptorGroup;

/**
 * Version Control System Page
 *
 * @author Lubos Pochman
 *
 */
public class VcsProjectPage extends ProjectWizardPage implements SelectionListener {

    CCombo vcsType = null;
    Composite stack = null;
    StackLayout vcsLayout = null;
    VcsAdaptorGroup[] vcsadaptors;

    org.eclipse.swt.widgets.List vcsList = null;

    private List vcsData = null;

    private VcsProjectData selectedData = null;

    /**
     * @param con
     */
    public VcsProjectPage(LuntbuildConnection con) {
        super("Version Control System", true, con);
        setTitle("Version Control System");
        setDescription(((super.doCreate) ? "Create" : "Edit") + " Version Control System.");

        this.vcsadaptors = new VcsAdaptorGroup[LuntclipseConstants.vcsAdaptorType.length];
    }

    /**
     * @param con
     * @param allData
     */
    public VcsProjectPage(LuntbuildConnection con, java.util.List allData) {
        super("Version Control System", false, con);
        this.allData = allData;
        this.vcsData = (List)this.allData.get(1);
        setTitle("Version Control System");
        setDescription(((super.doCreate) ? "Create" : "Edit") + " Version Control System.");

        this.vcsadaptors = new VcsAdaptorGroup[LuntclipseConstants.vcsAdaptorType.length];
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
        GridData gd;

        // Vcs adaptors list, selection sets selectedData
        // List (type - this.vcsadaptors[type].getDesc()) + buttons Delete, Modify, Deselect
        GridLayout selectLayout = new GridLayout();
        selectLayout.numColumns = 1;
        selectLayout.verticalSpacing = 2;
        Group selectGroup = groupIn(top, "Vcs Adaptors List", selectLayout);
        this.vcsList = new org.eclipse.swt.widgets.List(selectGroup,
                SWT.BORDER|SWT.SINGLE|SWT.SCROLL_LINE|SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        gd.widthHint = 50;
        this.vcsList.setLayoutData(gd);
        selectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
        if (this.vcsData != null) {
            int i = 0;
            for (Iterator iter = this.vcsData.iterator(); iter.hasNext();) {
                VcsProjectData elem = (VcsProjectData) iter.next();
                this.vcsList.add("Vcs Adaptor " + i++);
                this.vcsList.setData("Vcs Adaptor " + i++, elem);
            }
        }
        this.vcsList.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e){
                String[] name = vcsList.getSelection();
                selectedData = (VcsProjectData)vcsList.getData(name[0]);
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
        button.setToolTipText("Add/Create a new Vcs adaptor.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    addVcs();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.MODIFY_IMG));
        button.setToolTipText("Modify selected Vcs adaptor.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    modifyVcs();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.DELETE_IMG));
        button.setToolTipText("Delete selected Vcs adaptor.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    deleteVcs();
                }
            });
        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setImage(LuntclipsePlugin.getDefault().getImageRegistry().get(LuntclipseConstants.DESELECT_IMG));
        button.setToolTipText("Deselect selected Vcs adaptor.");
        button.addSelectionListener(new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    vcsList.deselectAll();
                    clearDataText();
                    selectedData = null;
                    dialogChanged();
                }
            });


        // Selected/new vcs adaptor
        GridLayout groupLayout = new GridLayout();
        groupLayout.numColumns = 2;
        groupLayout.verticalSpacing = 2;
        Group topGroup = groupIn(top, "VCS Adaptor", groupLayout);
        topGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

        this.vcsType = new CCombo(groupIn(topGroup, "Version Control System:"), SWT.BORDER);

        createLine(topGroup, layout.numColumns);

        this.stack = new Composite(topGroup, SWT.NULL);
        this.vcsLayout = new StackLayout();
        this.stack.setLayout(this.vcsLayout);
        this.stack.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

        for (int i = 0; i < LuntclipseConstants.vcsAdaptorType.length; i++) {
            String type = LuntclipseConstants.vcsAdaptorType[i];
            this.vcsType.add(type);
            try {
                this.vcsadaptors[i] = (VcsAdaptorGroup)LuntclipseConstants.vcsAdaptorClass[i].newInstance();
                this.vcsadaptors[i].createControls(this, this.stack, i, type, this.vcsData);
            } catch (Exception e) {
                LuntclipsePlugin.doLog(IStatus.ERROR, IStatus.OK,
                        "Cannot instantiate VCS adaptor " +
                        LuntclipseConstants.vcsAdaptorClass[i].getName(), e);
            }
        }
        int type = 0;
        if (this.vcsData != null &&
                this.vcsData.size() > 0) type = ((VcsProjectData)this.vcsData.get(0)).getType();
        this.vcsType.select(type);

        this.vcsLayout.topControl = this.vcsadaptors[type].getTop();
        this.vcsType.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent e) {
                    int i = VcsProjectPage.this.vcsType.getSelectionIndex();
                    VcsProjectPage.this.vcsLayout.topControl =
                        VcsProjectPage.this.vcsadaptors[i].getTop();
                    VcsProjectPage.this.stack.layout();
                    dialogChanged();
                    hasChanged = true;
                }
                public void widgetDefaultSelected(SelectionEvent e) {
                    int i = VcsProjectPage.this.vcsType.getSelectionIndex();
                    VcsProjectPage.this.vcsLayout.topControl =
                        VcsProjectPage.this.vcsadaptors[i].getTop();
                    VcsProjectPage.this.stack.layout();
                    dialogChanged();
                    hasChanged = true;
                }
            });

        if (this.vcsData != null && this.vcsData.size() > 0) {
            this.selectedData = (VcsProjectData)this.vcsData.get(0);
            this.vcsList.select(0);
        }
        this.vcsadaptors[type].checkStatus();

        setControl(top);
    }

    private void addVcs() {
        if (getErrorMessage() != null) return;
        int i = this.vcsType.getSelectionIndex();
        if (i < 0) return;
        this.hasChanged = true;
        VcsProjectData data = buildData(i);
        if (data == null) return;

        clearDataText(i);
        this.vcsList.deselectAll();
        this.selectedData = null;

        String name = "Vcs Adaptor " + this.vcsData.size();
        this.vcsList.add(name);
        this.vcsList.setData(name, data);

        dialogChanged();
        this.setPageComplete(this.vcsData != null && this.vcsData.size() > 0);
    }

    private void modifyVcs() {
        if (this.selectedData == null) return;
        this.hasChanged = true;
        buildData(this.selectedData.getType());
    }

    private void deleteVcs() {
        if (this.selectedData == null) return;
        int i = this.vcsData.indexOf(this.selectedData);
        if (i < 0) return;
        this.hasChanged = true;
        this.vcsList.remove("Vcs Adaptor " + i);
        this.vcsData.remove(this.selectedData);
        clearDataText(i);
        this.vcsList.deselectAll();
        this.selectedData = null;
        dialogChanged();
        this.setPageComplete(this.vcsData != null && this.vcsData.size() > 0);
    }

    /**
     *
     */
    public void dialogChanged() {
        int i = this.vcsType.getSelectionIndex();

        this.vcsadaptors[i].dialogChanged();
        if (getErrorMessage() != null) return;

        // Page is done
        updateStatus(null);
    }

    /**
     * @return
     *
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizardPage#canFlipToNextPage()
     */
    public boolean canFlipToNextPage() {
        if (this.vcsData != null && this.vcsData.size() > 0) return true;

        if (getErrorMessage() != null) return false;

        int i = this.vcsType.getSelectionIndex();
        if (i < 0) return false;

        return this.vcsadaptors[i].canFlipToNextPage();
    }

    /** Create builders project data and return next page
     * @return next page
     *
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizardPage#getNextPage()
     */
    public IWizardPage getNextPage() {

        // BuildersProjectData is complete - set data
        int i = this.vcsType.getSelectionIndex();
        if (i >= 0) buildData(i);
        if (this.vcsData == null || this.vcsData.size() == 0) return null;

        return super.getNextPage();
    }

    private VcsProjectData buildData(int type) {
        if (!this.hasChanged) return null;

        // is this selected vcs adaptor or new?
        VcsProjectData data = (this.selectedData != null) ? this.selectedData : new VcsProjectData();
        data.setType(type);
        this.vcsadaptors[type].setData(data);
        data.setQuietPeriod(this.vcsadaptors[type].getQuietPeriod());
        data.setModules(this.vcsadaptors[type].getModules());

        if (this.selectedData == null) {
            if (this.vcsData == null)
                this.vcsData = new ArrayList();

            this.vcsData.add(data);
        }
        this.hasChanged = false;

        return data;
    }

    private void setDataText() {
        if (this.selectedData == null) return;

           int type = this.selectedData.getType();
           this.vcsadaptors[type].setDataText(this.selectedData);
           this.vcsadaptors[type].setQuietPeriod(this.selectedData.getQuietPeriod());
    }

    private void clearDataText(int type) {
        this.vcsadaptors[type].clearDataText();
    }

    private void clearDataText() {
        for (int i = 0; i < this.vcsadaptors.length; i++) {
            VcsAdaptorGroup vcs = this.vcsadaptors[i];
            vcs.clearDataText();
        }
    }

    /** Returns vcs project data list.
     * @return vcs project data
     */
    public List getData() {
        int i = this.vcsType.getSelectionIndex();
        if (i >= 0)  buildData(i);

        return this.vcsData;
    }

    /** Returns selected vcs project data.
     * @return vcs project data
     */
    public VcsProjectData getSelectedData() {
        return this.selectedData;
    }

    /**
     * Create a module from data
     */
    public void createModule() {
        int i = this.vcsType.getSelectionIndex();
        this.vcsadaptors[i].createModule();
    }

    /**
     *
     */
    public void modifySelectedModule() {
        int i = this.vcsType.getSelectionIndex();
        this.vcsadaptors[i].modifySelectedModule();
    }

    /**
     *
     */
    public void deleteSelectedModule() {
        int i = this.vcsType.getSelectionIndex();
        this.vcsadaptors[i].deleteSelectedModule();
    }

    public void widgetSelected(SelectionEvent e){
        dialogChanged();
    }

    public void widgetDefaultSelected(SelectionEvent e){
        dialogChanged();
    }

}
