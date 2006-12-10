package com.luntsys.luntbuild.luntclipse.wizards;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;


/**
 * ProjectWizardPage base class
 *
 * @author Lubos Pochman
 *
 */
public abstract class ProjectWizardPage extends WizardPage {

    protected boolean doCreate;
    protected LuntbuildConnection connection;
    protected List allData = null;
    protected boolean hasChanged = false;


    /**
     * @param name
     * @param doCreate
     * @param con
     */
    public ProjectWizardPage(String name, boolean doCreate, LuntbuildConnection con) {
        super(name);
        this.doCreate = doCreate;
        this.connection = con;
        setImageDescriptor(ImageDescriptor.createFromURL(LuntclipsePlugin.getDefault().
                getBundle().getEntry("images/guide.gif")));
    }

    /**
     * @param message
     */
    public void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    /**
     * @return true if data changed
     */
    public boolean hasChanged() {
        return this.hasChanged;
    }

    public void setHasChanged(boolean state) {
        this.hasChanged = state;
    }

    /**
     * @param parent
     * @param ncol
     */
    public static void createLine(Composite parent, int ncol) {
        Label line = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL |
                SWT.BOLD);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = ncol;
        line.setLayoutData(gridData);
    }

    /**
     * @param parent
     * @param name
     * @return group
     */
    public static Group groupIn(Composite parent, String name) {
        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(name);
        group.setLayout(new FillLayout(SWT.HORIZONTAL));

        return group;
    }

    /**
     * @param parent
     * @param name
     * @return group
     */
    public static Group groupFill(Composite parent, String name) {
        Group group = groupIn(parent, name);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return group;
    }

    /**
     * @param parent
     * @param name
     * @param width
     * @return group
     */
    public static Group groupIn(Composite parent, String name, int width) {
        Group group = groupIn(parent, name);
        GridData gd = new GridData();
        gd.widthHint = width;
        group.setLayoutData(gd);

        return group;
    }

    /**
     * @param parent
     * @param name
     * @param width
     * @param height
     * @return group
     */
    public static Group groupIn(Composite parent, String name, int width, int height) {
        Group group = groupIn(parent, name);
        GridData gd = new GridData();
        gd.widthHint = width;
        gd.heightHint = height;
        group.setLayoutData(gd);

        return group;
    }

    /**
     * @param parent
     * @param name
     * @param gd
     * @return group
     */
    public static Group groupIn(Composite parent, String name, GridData gd) {
        Group group = groupIn(parent, name);
        group.setLayoutData(gd);

        return group;
    }

    /**
     * @param parent
     * @param name
     * @param layout
     * @return group
     */
    public static Group groupIn(Composite parent, String name, Layout layout) {
        Group group = new Group(parent, SWT.SHADOW_IN);
        group.setText(name);
        group.setLayout(layout);

        return group;
    }

    /**
     * @param parent
     * @param buttonLabels
     * @param fieldLabels
     * @return radio group info
     */
    public static RadioGroupInfo radioGroup(Composite parent, String[] buttonLabels, String[] fieldLabels) {
        RadioGroupInfo info =new RadioGroupInfo(buttonLabels.length);

        for (int i = 0; i < buttonLabels.length; i++) {
            info.buttons[i] = new Button(parent, SWT.RADIO);
            info.buttons[i].setText(buttonLabels[i]);

            if (fieldLabels != null) {
                if (fieldLabels[i] != null) {
                    info.fields[i] = new Text(parent, SWT.BORDER | SWT.SINGLE);
                    Label l = new Label(parent, SWT.NULL);
                    l.setText(fieldLabels[i]);
                } else {
                    Label l = new Label(parent, SWT.NULL);
                    l.setText("");
                    l = new Label(parent, SWT.NULL);
                    l.setText("");

                }
            }
        }

        return info;
    }
}
