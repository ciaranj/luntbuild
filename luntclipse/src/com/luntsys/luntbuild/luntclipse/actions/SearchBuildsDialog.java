package com.luntsys.luntbuild.luntclipse.actions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.luntsys.luntbuild.facades.SearchCriteria;
import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.Build;
import com.luntsys.luntbuild.luntclipse.wizards.ProjectWizardPage;

/**
 * Search builds dialog
 *
 * @author lubosp
 *
 */
public class SearchBuildsDialog extends TitleAreaDialog {

    private LuntbuildConnection connection = null;
    private Build currentBuild = null;
    private Text version = null;
    private Button exactVersion = null;
    private CCombo status = null;
    private Text fromDate = null;
    private Text toDate = null;
    private List schedulesList = null;
    private Image image = null;

    private SearchCriteria currentCriteria = null;

    private static final String[] dateFormats = {
        "EEE, d MMM yyyy HH:mm:ss Z",
        "d MMM yyyy HH:mm:ss Z",
        "EEE, d MMM yyyy HH:mm:ss",
        "d MMM yyyy HH:mm:ss",
        "EEE, d MMM yyyy HH:mm Z",
        "d MMM yyyy HH:mm Z",
        "EEE, d MMM yyyy HH:mm",
        "d MMM yyyy HH:mm",
    };

    /**
     * @param parentShell
     * @param con
     * @param currentBuild
     */
    public SearchBuildsDialog(Shell parentShell, LuntbuildConnection con, Build currentBuild) {
        super(parentShell);
        this.connection = con;
        this.currentBuild = currentBuild;
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX
                | SWT.APPLICATION_MODAL);

    }

    /**
     * Creates the dialog's contents
     *
     * @param parent the parent composite
     * @return Control
     */
    protected Control createContents(Composite parent) {
      Control contents = super.createContents(parent);

      // Set the title
      setTitle("Search Builds");

      // Set the message
      setMessage("Set search criteria to search previous builds. Empty criteria will return all previous builds.",
              IMessageProvider.INFORMATION);

      this.image = ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
              getBundle().getEntry("images/guide.gif")).createImage();
      if (this.image != null) setTitleImage(this.image);

      return contents;
    }

    /**
     * Creates a two column grid of property labels and their corresponding
     * values. Some properties span both columns.
     *
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        Group verGroup = ProjectWizardPage.groupIn(composite, "Version:",  gridLayout);
        this.version = new Text(verGroup, SWT.BORDER | SWT.SINGLE);
        this.exactVersion = new Button(verGroup, SWT.CHECK);
        this.exactVersion.setText("exact match");

        this.status = new CCombo(ProjectWizardPage.groupIn(composite, "Build Status:"), SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.buildStatus.length; i++) {
            String val = LuntclipseConstants.buildStatus[i];
            this.status.add(val);
        }
        this.fromDate = new Text(ProjectWizardPage.groupIn(composite, "FromDate:"), SWT.BORDER);
        this.toDate = new Text(ProjectWizardPage.groupIn(composite, "To Date:"), SWT.BORDER);

        GridData gdata = new GridData(GridData.FILL_HORIZONTAL);
        gdata.heightHint = 100;
        this.schedulesList = new List(ProjectWizardPage.groupIn(composite, "Move to Schedule:", gdata),
                SWT.BORDER|SWT.MULTI|SWT.SCROLL_LINE|SWT.V_SCROLL);
        String[] schedules = this.connection.getAllSchedules();
        for (int i = 0; i < schedules.length; i++) {
            String val = schedules[i];
            this.schedulesList.add(val);
        }

        return composite;
    }

    /**
     * Creates buttons.
     *
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, true);
    }

    /**
     * Closes the dialog when the Close button is pressed.
     *
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed(int buttonId) {
        this.currentCriteria = null;
        if (buttonId == IDialogConstants.CANCEL_ID)
            cancelPressed();
        else if (buttonId == IDialogConstants.OK_ID) {
            this.currentCriteria = setSearchCriteria();
            setReturnCode(SWT.OK);
            close();
        } else
            super.buttonPressed(buttonId);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Search Builds");
     }

    /**
     * Disposes the icon, if one was created, and calls super to close this
     * dialog.
     *
     * @see org.eclipse.jface.window.Window#close()
     */
    public boolean close() {
        if (this.image != null) this.image.dispose();
        return super.close();
    }

    /**
     * @return search criteria
     */
    public SearchCriteria getSearchCriteria() {
        return this.currentCriteria;
    }

    private SearchCriteria setSearchCriteria() {
        SearchCriteria criteria = new SearchCriteria();

        if (this.currentBuild != null) {
            long id = this.connection.getScheduleId(this.currentBuild.getProjectName(),
                    this.currentBuild.getScheduleName());
            if (id >= 0) {
                long[] idArr = new long[1];
                idArr[0] = id;
                criteria.setScheduleIds(idArr);
            }
        }
        String strVal = this.version.getText().trim();
        if (strVal.length() > 0) criteria.setVersion(strVal);
        criteria.setExactMatch(this.exactVersion.getSelection());
        if (this.status.getSelectionIndex() >= 0)
            criteria.setStatus(this.status.getSelectionIndex());
        strVal = this.fromDate.getText().trim();
        Date date = getDate(strVal);
        if (date != null) criteria.setFrom(date);
        strVal = this.toDate.getText().trim();
        date = getDate(strVal);
        if (date != null) criteria.setTo(date);
        String[] selected = this.schedulesList.getSelection();
        if (selected == null || selected.length == 0) return criteria;

        ArrayList ids = new ArrayList();
        for (int i = 0; i < selected.length; i++) {
            String val = selected[i];
            int idx = val.indexOf('/');
            if (idx == -1) continue;
            String projectName = val.substring(0, idx);
            String scheduleName = val.substring(idx + 1);
            long id = this.connection.getScheduleId(projectName, scheduleName);
            if (id == -1) continue;
            ids.add(new Long(id));
        }
        if (ids.size() == 0) return criteria;

        long[] idArr = new long[ids.size()];
        int i = 0;
        for (Iterator iter = ids.iterator(); iter.hasNext();) {
            Long val = (Long) iter.next();
            idArr[i++] = val.longValue();
        }
        criteria.setScheduleIds(idArr);

        return criteria;
    }

    private Date getDate(String strVal) {
        Date d = null;
        for (int j = 0; j < dateFormats.length; j++) {
            SimpleDateFormat df = new SimpleDateFormat(dateFormats[j]);
            try {
                d = df.parse(strVal);
            } catch (ParseException ex) {
                // try again with another format
            }
            if (d != null) {
                return d;
            }
        }
        return null;
    }
}
