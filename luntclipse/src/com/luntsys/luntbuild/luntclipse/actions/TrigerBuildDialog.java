package com.luntsys.luntbuild.luntclipse.actions;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.facades.BuildParams;
import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;
import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.model.Build;

/**
 * TrigerBuild Dialog
 *
 * @author Lubos Pochman
 *
 */
public class TrigerBuildDialog extends TitleAreaDialog {

    private BuildParams buildParams;
    private Build selectedMessenger;

    private Text buildVersion = null;
    private CCombo buildType = null;
    private CCombo postBuild = null;
    private CCombo labelStrategy = null;
    private CCombo notifyStrategy = null;
    private CCombo dependencyStrategy = null;

    private Image image;


    /**
     * @param parentShell
     * @param buildParams
     * @param selectedMessenger
     */
    public TrigerBuildDialog(Shell parentShell, BuildParams buildParams, Build selectedMessenger) {
        super(parentShell);
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX
                | SWT.APPLICATION_MODAL);

        this.buildParams = buildParams;
        this.selectedMessenger = selectedMessenger;
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
      setTitle("Trigger build");

      // Set the message
      setMessage("Trigger build for project \"" +
              this.selectedMessenger.getProjectName() + "\" and schedule \"" +
              this.selectedMessenger.getScheduleName() + "\".",
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
        label.setText("Build as Version:");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.buildVersion = new Text(gridComp, SWT.BORDER);
        this.buildVersion.setText(this.buildParams.getBuildVersion());
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.buildVersion.setLayoutData(gdata);

        label = new Label(gridComp, SWT.NONE);
        label.setText("Build Type:");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.buildType = new CCombo(gridComp, SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.buildType.length; i++) {
            String type = LuntclipseConstants.buildType[i];
            this.buildType.add(type);
        }
        this.buildType.select(this.buildParams.getBuildType());
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.buildType.setLayoutData(gdata);

        label = new Label(gridComp, SWT.NONE);
        label.setText("Post-build Strategy:");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.postBuild = new CCombo(gridComp, SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.postBuildStrategy.length; i++) {
            String val = LuntclipseConstants.postBuildStrategy[i];
            this.postBuild.add(val);
        }
        this.postBuild.select(this.buildParams.getPostbuildStrategy());
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.postBuild.setLayoutData(gdata);

        label = new Label(gridComp, SWT.NONE);
        label.setText("Label Strategy:");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.labelStrategy = new CCombo(gridComp, SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.labelStrategy.length; i++) {
            String val = LuntclipseConstants.labelStrategy[i];
            this.labelStrategy.add(val);
        }
        this.labelStrategy.select(this.buildParams.getLabelStrategy());
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.labelStrategy.setLayoutData(gdata);

        label = new Label(gridComp, SWT.NONE);
        label.setText("Notify Strategy:");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.notifyStrategy = new CCombo(gridComp, SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.notifyStrategy.length; i++) {
            String val = LuntclipseConstants.notifyStrategy[i];
            this.notifyStrategy.add(val);
        }
        this.notifyStrategy.select(this.buildParams.getNotifyStrategy());
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.notifyStrategy.setLayoutData(gdata);

        label = new Label(gridComp, SWT.NONE);
        label.setText("Dependency Strategy:");
        gdata = new GridData();
        gdata.widthHint = 120;
        label.setLayoutData(gdata);
        this.dependencyStrategy = new CCombo(gridComp, SWT.BORDER);
        for (int i = 0; i < LuntclipseConstants.dependencyStrategy.length; i++) {
            String val = LuntclipseConstants.dependencyStrategy[i];
            this.dependencyStrategy.add(val);
        }
        this.dependencyStrategy.select(this.buildParams.getTriggerDependencyStrategy());
        gdata = new GridData();
        gdata.widthHint = 220;
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        this.dependencyStrategy.setLayoutData(gdata);

        return composite;
    }

    /**
     * Creates a single Close button.
     *
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
                "Build", true);
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
            this.buildParams.setBuildNecessaryCondition("true");
            this.buildParams.setBuildType(this.buildType.getSelectionIndex());
            this.buildParams.setBuildVersion(this.buildVersion.getText());
            this.buildParams.setLabelStrategy(this.labelStrategy.getSelectionIndex());
            this.buildParams.setNotifyStrategy(this.notifyStrategy.getSelectionIndex());
            this.buildParams.setPostbuildStrategy(this.postBuild.getSelectionIndex());
            this.buildParams.setTriggerDependencyStrategy(this.dependencyStrategy.getSelectionIndex());
            setReturnCode(SWT.OK);
            close();
        } else
            super.buttonPressed(buttonId);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Trigger Build");
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
     * @return Returns the buildParams.
     */
    public final BuildParams getBuildParams() {
        return this.buildParams;
    }

}
