package com.luntsys.luntbuild.luntclipse.wizards;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.BasicProjectData;
import com.luntsys.luntbuild.facades.lb12.UserFacade;

/**
 * Basic Project Info
 *
 * @author Lubos Pochman
 *
 */
public class BasicProjectPage extends ProjectWizardPage implements SelectionListener, ModifyListener {

    private Text nameText = null;
    private Text descText = null;
    private Text varText = null;
    private List adminsList = null;
    private List buildersList = null;
    private List viewersList = null;
    private List notifyWithList = null;
    private List notifyWhoList = null;
    private CCombo logLevel = null;

    private BasicProjectData basicData = null;

    /**
     * @param con
     */
    public BasicProjectPage(LuntbuildConnection con) {
        super("Basic Project Info", true, con);
        setTitle("Basic Project Info");
        setDescription(((super.doCreate) ? "Create" : "Edit") + " Basic Project Info.");
    }

    /**
     * @param con
     * @param allData
     */
    public BasicProjectPage(LuntbuildConnection con, java.util.List allData) {
        super("Basic Project Info", false, con);
        this.allData = allData;
        this.basicData = (BasicProjectData)this.allData.get(0);
        setTitle("Basic Project Info");
        setDescription(((super.doCreate) ? "Create" : "Edit") + " Basic Project Info.");
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
        layout.numColumns = 1;
        layout.verticalSpacing = 5;
        top.setLayout(layout);
        GridData gd;

        this.nameText = new Text(groupIn(top, "Name:", 120), SWT.BORDER|SWT.SINGLE);
        if (this.basicData != null) this.nameText.setText(this.basicData.getName());
        this.nameText.addModifyListener(this);

        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 60;
        this.descText = new Text(groupIn(top, "Description:", gd),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        if (this.basicData != null) this.descText.setText(this.basicData.getDescription());
        this.descText.addModifyListener(this);

        Composite fillComp = new Composite(top, SWT.NULL);
        FillLayout fillLay = new FillLayout(SWT.HORIZONTAL);
        fillLay.marginHeight = 5;
        fillLay.marginWidth = 5;
        fillLay.spacing  = 5;
        fillComp.setLayout(fillLay);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 80;
        fillComp.setLayoutData(gd);

        this.notifyWithList = new List(groupIn(fillComp, "Notify with:"),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        for (int i = 0; i < LuntclipseConstants.notifyWith.length; i++) {
            String val = LuntclipseConstants.notifyWith[i];
            this.notifyWithList.add(val);
        }
        if (this.basicData != null) this.notifyWithList.setSelection(this.basicData.getNotifyWith());
        this.notifyWithList.addSelectionListener(this);

        this.notifyWhoList = new List(groupIn(fillComp, "Notify Users:"),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        ArrayList users = (ArrayList)super.connection.getUsers();
        for (Iterator iter = users.iterator(); iter.hasNext();) {
            UserFacade user = (UserFacade) iter.next();
            this.notifyWhoList.add(user.getName());
        }
        if (this.basicData != null) this.notifyWhoList.setSelection(this.basicData.getNotifyWho());
        this.notifyWhoList.addSelectionListener(this);

        fillComp = new Composite(top, SWT.NULL);
        fillLay = new FillLayout(SWT.HORIZONTAL);
        fillLay.marginHeight = 5;
        fillLay.marginWidth = 5;
        fillLay.spacing  = 5;
        fillComp.setLayout(fillLay);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 80;
        fillComp.setLayoutData(gd);

        this.adminsList = new List(groupIn(fillComp, "Project Admins:"),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        for (Iterator iter = users.iterator(); iter.hasNext();) {
            UserFacade user = (UserFacade) iter.next();
            if (!user.getName().equals(LuntclipseConstants.usersCheckedRecently))
                this.adminsList.add(user.getName());
        }
        if (this.basicData != null) this.adminsList.setSelection(this.basicData.getAdmins());
        this.adminsList.addSelectionListener(this);

        this.buildersList = new List(groupIn(fillComp, "Project Builders:"),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        for (Iterator iter = users.iterator(); iter.hasNext();) {
            UserFacade user = (UserFacade) iter.next();
            if (!user.getName().equals(LuntclipseConstants.usersCheckedRecently))
                this.buildersList.add(user.getName());
        }
        if (this.basicData != null) this.buildersList.setSelection(this.basicData.getBuilders());
        this.buildersList.addSelectionListener(this);

        this.viewersList = new List(groupIn(fillComp, "Project Viewers:"),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        for (Iterator iter = users.iterator(); iter.hasNext();) {
            UserFacade user = (UserFacade) iter.next();
            if (!user.getName().equals(LuntclipseConstants.usersCheckedRecently))
                this.viewersList.add(user.getName());
        }
        if (this.basicData != null) this.viewersList.setSelection(this.basicData.getViewers());
        this.viewersList.addSelectionListener(this);

        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 60;
        this.varText = new Text(groupIn(top, "Variables:", gd),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        this.varText.setText("versionIterator=1");
        if (this.basicData != null) this.varText.setText(this.basicData.getVariables());
        this.varText.addModifyListener(this);

        this.logLevel = new CCombo(groupIn(top, "Log Level:"), SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.logLevel.length; i++) {
            String val = LuntclipseConstants.logLevel[i];
            this.logLevel.add(val);
        }
        if (this.basicData != null)
            this.logLevel.select(this.basicData.getLogLevel());
        else
            this.logLevel.select(1);
        this.logLevel.addSelectionListener(this);

        if (super.doCreate) updateStatus("Please specify name of the project!");

        setControl(top);
    }

    void dialogChanged() {

        this.hasChanged = true;

        // validate data, report status
        String name = this.nameText.getText().trim();
        if (name.length() == 0) {
            updateStatus("Please specify name of the project!");
            return;
        }
        if (this.doCreate && super.connection.projectExist(name)) {
            updateStatus("Project \"" + name + "\" already exist!");
            return;
        }

        updateStatus(null);
    }

    public boolean canFlipToNextPage(){
        if (getErrorMessage() != null) return false;
        String name = this.nameText.getText().trim();
         return name.length() != 0;
     }

    /** Create basic project data and return next page
     * @return next page
     *
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizardPage#getNextPage()
     */
    public IWizardPage getNextPage() {

        // Page is complete - set data
        buildData();
        return super.getNextPage();
    }

    private void buildData() {
        if (!this.hasChanged) return;

        this.basicData = new BasicProjectData();
        this.basicData.setName(this.nameText.getText().trim());
        this.basicData.setDescription(this.descText.getText().trim());
        this.basicData.setAdmins(this.adminsList.getSelection());
        this.basicData.setBuilders(this.buildersList.getSelection());
        this.basicData.setViewers(this.viewersList.getSelection());
        this.basicData.setNotifyWith(this.notifyWithList.getSelection());
        this.basicData.setNotifyWho(this.notifyWhoList.getSelection());
        this.basicData.setVariables(this.varText.getText().trim());
        this.basicData.setLogLevel(this.logLevel.getSelectionIndex());

        this.hasChanged = false;
    }

    /** Returns basic project data.
     * @return basic project data
     */
    public BasicProjectData getData() {
        buildData();
        return this.basicData;
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
