package com.luntsys.luntbuild.luntclipse.preferences;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.model.ConnectionData;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;

/**
 * Create Connection Dialog
 *
 * @author Lubos Pochman
 *
 */
public class CreateConnectionDialog extends TitleAreaDialog {

    private ConnectionData connectionData = null;

    private Text nameText;
    private Text urlText;
    private Text userText;
    private Text passwordText;
    private Text refreshText;
    private ArrayList versionButtons = new ArrayList();

    private Image image;


    /**
     * @param parentShell
     */
    public CreateConnectionDialog(Shell parentShell) {
        super(parentShell);
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
      setTitle("Create connection");

      // Set the message
      setMessage("Create connection to an existing Luntbuild instance.",
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

        // Grid Layout panel
        Composite gridComp = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridComp.setLayout(gridLayout);
        GridData gdata = new GridData();
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        gridComp.setLayoutData(gdata);

        Label label = new Label(gridComp, SWT.NONE);
        label.setText("Connection Name:");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.nameText = new Text(gridComp, SWT.BORDER);
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.nameText.setLayoutData(gdata);

        label = new Label(gridComp, SWT.NONE);
        label.setText("Luntbuild Url:");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.urlText = new Text(gridComp, SWT.BORDER);
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.urlText.setLayoutData(gdata);

        label = new Label(gridComp, SWT.NONE);
        label.setText("Luntbuild User:");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.userText = new Text(gridComp, SWT.BORDER);
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.userText.setLayoutData(gdata);

        label = new Label(gridComp, SWT.NONE);
        label.setText("Luntbuild Password:");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.passwordText = new Text(gridComp, SWT.BORDER);
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.passwordText.setLayoutData(gdata);

        label = new Label(gridComp, SWT.NONE);
        label.setText("Luntbuild refresh time (s):");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.refreshText = new Text(gridComp, SWT.BORDER);
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.refreshText.setLayoutData(gdata);

        // Create the version group
        label = new Label(gridComp, SWT.NONE);
        label.setText("Version:");
        gdata = new GridData();
        gdata.widthHint = 120;
        Group group = new Group(gridComp, SWT.SHADOW_IN);
        group.setText("Luntbuild version");
        group.setLayout(new RowLayout(SWT.HORIZONTAL));
        Button button = new Button(group, SWT.RADIO);
        button.setText("1.2");
        this.versionButtons.add(button);
        button = new Button(group, SWT.RADIO);
        button.setText("1.2.1");
        this.versionButtons.add(button);
        button = new Button(group, SWT.RADIO);
        button.setText("1.2.2");
        this.versionButtons.add(button);
        button = new Button(group, SWT.RADIO);
        button.setText("1.3");
        button.setSelection(true);
        this.versionButtons.add(button);

        return composite;
    }

    /**
     * Creates a single Close button.
     *
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
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
            validateConnectionData();
            if (this.connectionData == null) {
                setReturnCode(SWT.CANCEL);
                close();
                return;
            }
            if (LuntbuildView.mainView.nameExists(this.connectionData.getName())) {
                MessageDialog.openWarning(
                        getShell(),
                        "Luntbuild Connection Exists",
                        "Luntbuild connection " + this.connectionData.getName() + " already exists!\n" +
                        "Please choose different name!");
                return;
            } else if (this.connectionData == null) {
                return;
            }
            setReturnCode(SWT.OK);
            close();
        } else
            super.buttonPressed(buttonId);
    }

    private void validateConnectionData() {
        ConnectionData data = new ConnectionData();

        String name = this.nameText.getText().trim();
        if (name.length() == 0) {
            MessageDialog.openError(getShell(), "Luntbuild Connection Invalid",
                    "Luntbuild connection name is missing!");
            this.connectionData = null;
            return;
        }
        data.setName(name);

        String url = this.urlText.getText().trim();
        if (url.length() == 0) {
            MessageDialog.openError(getShell(), "Luntbuild Connection Invalid",
                    "Luntbuild connection url is missing!");
            this.connectionData = null;
            return;
        }
        data.setUrl(url);

        String user = this.userText.getText().trim();
        if (user.length() == 0) {
            MessageDialog.openError(getShell(), "Luntbuild Connection Invalid",
                    "Luntbuild connection user is missing!");
            this.connectionData = null;
            return;
        }
        data.setUser(user);

        String password = this.passwordText.getText().trim();
        if (password.length() == 0 && !user.equals("anonymous")) {
            MessageDialog.openError(getShell(), "Luntbuild Connection Invalid",
                    "Luntbuild connection password is missing!");
            this.connectionData = null;
            return;
        }
        data.setPassword(password);

        String refresh = this.refreshText.getText().trim();
        if (refresh.length() == 0) {
            MessageDialog.openWarning(getShell(), "Luntbuild Connection Invalid",
                    "Luntbuild connection refresh time invalid using default (60s)!");
            data.setRefreshTime("60");
        } else {
            try{
                 new Integer(refresh).intValue();
                 data.setRefreshTime(refresh);
            }catch (NumberFormatException e) {
                MessageDialog.openWarning(getShell(), "Luntbuild Connection Invalid",
                "Luntbuild connection refresh time invalid using default (60s)!");
                data.setRefreshTime("60");
            }
        }

        for (Iterator iter = this.versionButtons.iterator(); iter.hasNext();) {
            Button bt = (Button) iter.next();
            if (bt.getSelection()) {
                data.setVersion(bt.getText());
                break;
            }
        }

        this.connectionData = data;
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Create Luntbuild Connection");
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
     * @return Returns the connectionData.
     */
    public final ConnectionData getConnectionData() {
        return this.connectionData;
    }


}
