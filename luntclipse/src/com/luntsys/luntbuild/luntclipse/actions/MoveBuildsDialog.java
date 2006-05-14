package com.luntsys.luntbuild.luntclipse.actions;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.wizards.ProjectWizardPage;

/**
 * Move builds dialog, select schedule to move builds
 *
 * @author lubosp
 *
 */
public class MoveBuildsDialog extends TitleAreaDialog implements SelectionListener {

    private String projectName = null;
    private String scheduleName = null;
    private LuntbuildConnection connection = null;
    private List schedulesList = null;
    private Button okButton = null;
    private Image image = null;

    /**
     * @param parentShell
     * @param con
     */
    public MoveBuildsDialog(Shell parentShell, LuntbuildConnection con) {
        super(parentShell);
        this.connection = con;
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
      setTitle("Move Builds");

      // Set the message
      setMessage("Select a schedule to which to move selected builds.",
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

        GridData gdata = new GridData(GridData.FILL_HORIZONTAL);
        gdata.heightHint = 100;
        this.schedulesList = new List(ProjectWizardPage.groupIn(composite, "Move to Schedule:", gdata),
                SWT.BORDER|SWT.SINGLE|SWT.SCROLL_LINE|SWT.V_SCROLL);
        String[] schedules = this.connection.getAllSchedules();
        for (int i = 0; i < schedules.length; i++) {
            String val = schedules[i];
            this.schedulesList.add(val);
        }
        this.schedulesList.addSelectionListener(this);

        return composite;
    }

    /**
     * Creates buttons.
     *
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        this.okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        this.okButton.setEnabled(false);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, true);
    }

    /**
     * Closes the dialog when the Close button is pressed.
     *
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.CANCEL_ID)
            cancelPressed();
        else if (buttonId == IDialogConstants.OK_ID) {
            setReturnCode(SWT.OK);
            close();
        } else
            super.buttonPressed(buttonId);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Move Builds");
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
     * @return project name
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * @return schedule name
     */
    public String getScheduleName() {
        return this.scheduleName;
    }

    public void widgetSelected(SelectionEvent e){
        String[] selection = this.schedulesList.getSelection();
        if (selection == null || selection.length == 0) {
            this.okButton.setEnabled(false);
            return;
        }
        int idx = selection[0].indexOf('/');
        if (idx == -1) {
            this.okButton.setEnabled(false);
            return;
        }
        this.projectName = selection[0].substring(0, idx);
        this.scheduleName = selection[0].substring(idx + 1);
        this.okButton.setEnabled(true);
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }

}
