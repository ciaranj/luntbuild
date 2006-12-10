package com.luntsys.luntbuild.luntclipse.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewSite;

import com.luntsys.luntbuild.luntclipse.LuntclipsePlugin;
import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.Build;
import com.luntsys.luntbuild.luntclipse.model.ConnectionData;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildView;
import com.luntsys.luntbuild.luntclipse.views.LuntbuildViewer;

/**
 * Manages Luntbuild Connections
 *
 * @author Lubos Pochman
 *
 */
public class ManageConnectionDialog extends TitleAreaDialog {

    private IViewSite site = null;

    private TableViewer connViewer = null;
    private Button buttonEdit = null;
    private Button buttonDelete = null;
    private Button buttonTest = null;

    private LuntbuildConnection selectedData = null;

    private Image image;

    // Set column names
    private static String[] columnNames = new String[] {
            "CONNECTION_NAME",
            "CONNECTION_URL"
            };

    /**
     * @param parentShell parentShell
     * @param site site
     */
    public ManageConnectionDialog(Shell parentShell, IViewSite site) {
        super(parentShell);
        this.site = site;
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
      setTitle("Manage Connections");

      // Set the message
      setMessage("Manage connections to multiple Luntbuild instances.",
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

        Table table = createTable(composite);
        this.connViewer = new TableViewer(table);
        this.connViewer.setUseHashlookup(true);
        this.connViewer.setContentProvider( new ConnectionViewProvider());
        this.connViewer.setLabelProvider(new ConnectionViewLabelProvider());
        this.connViewer.setSorter(null);
        this.connViewer.setInput(this.site);
        this.connViewer.setColumnProperties(columnNames);
        GridData gdata = new GridData();
        gdata.horizontalAlignment = GridData.FILL;
        gdata.grabExcessHorizontalSpace = true;
        gdata.widthHint = 400;
        gdata.heightHint = 200;
        this.connViewer.getControl().setLayoutData(gdata);

        Composite buttonsComp = new Composite(composite, SWT.NONE);
        RowLayout buttons = new RowLayout();
        buttons.wrap = false;
        buttons.pack = false;
        buttons.justify = true;
        buttons.type = SWT.HORIZONTAL;
        buttons.marginLeft = 5;
        buttons.marginTop = 5;
        buttons.marginRight = 5;
        buttons.marginBottom = 5;
        buttons.spacing = 5;
        buttonsComp.setLayout(buttons);

        Button button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setText("New...");
        button.addSelectionListener(
            new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    CreateConnectionDialog dlg =
                        new CreateConnectionDialog(getShell());
                    int rc = dlg.open();
                    if (rc == SWT.OK) {
                        final ConnectionData con = dlg.getConnectionData();
                        Display.getDefault().asyncExec(
                                new Runnable() {
                                    public void run(){
                                    	LuntbuildView.mainView.addConnection(con);
                                        connViewer.refresh();
                                    }
                                });
                    }

                }
            });

        this.buttonEdit = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        this.buttonEdit.setText("Edit...");
        this.buttonEdit.addSelectionListener(
            new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    if (selectedData == null) return;
                    CreateConnectionDialog dlg =
                        new CreateConnectionDialog(getShell(), selectedData);
                    int rc = dlg.open();
                    if (rc == SWT.OK) {
                        connViewer.refresh();
                        PreferenceHelper.saveConnection(selectedData.getConnectionData());
                        if (!selectedData.isConnected())
                           MessageDialog.openWarning(
                                    getShell(),
                                    "Quickbuild Connection",
                                    "Unable to connect to Quickbuild: " + selectedData.getConnectionData().getUrl());
                    }
                }
            });
        this.buttonEdit.setEnabled(false);

        this.buttonDelete = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        this.buttonDelete.setText("Delete");
        this.buttonDelete.addSelectionListener(
                new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e){
                        if (selectedData == null) return;
                        // Confirm
                        MessageBox mb =
                            new MessageBox(getShell(),
                                    SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
                        mb.setText("Confirm Delete");
                        mb.setMessage("Are you sure you want to delete " + selectedData.getConnectionData().getName() + "?");
                        int rc = mb.open();
                        if (rc == SWT.OK) {
                            LuntbuildView.mainView.deleteConnection(selectedData);
                            connViewer.refresh();
                        }
                    }
                });
        this.buttonDelete.setEnabled(false);

        button = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        button.setText("Delete All");
        button.addSelectionListener(
                new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e){
                        // Confirm
                        MessageBox mb =
                            new MessageBox(getShell(),
                                    SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
                        mb.setText("Confirm Delete");
                        mb.setMessage("Are you sure you want to delete all connections?");
                        int rc = mb.open();
                        if (rc == SWT.OK) {
                            LuntbuildView.mainView.deleteAllConnections();
                            connViewer.refresh();
                          	 buttonEdit.setEnabled(false);
                        	 buttonDelete.setEnabled(false);
                        	 buttonTest.setEnabled(false);
                        }
                    }
                });

        this.buttonTest = new Button(buttonsComp, SWT.PUSH|SWT.CENTER);
        this.buttonTest.setText("Test");
        this.buttonTest.addSelectionListener(
                new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e){
                        if (selectedData == null) return;
                        if (selectedData.isConnected())
                        MessageDialog.openInformation(
                                getShell(),
                                "Luntbuild Connection",
                                "Connected to Luntbuild: " + selectedData.getConnectionData().getUrl());
                        else
                        MessageDialog.openError(
                                getShell(),
                                "Luntbuild Connection",
                                "Unable to connect to Luntbuild: " + selectedData.getConnectionData().getUrl());
                    }
                });
        this.buttonTest.setEnabled(false);

        return composite;
    }

    private Table createTable(Composite parent) {
        Table table = new Table(parent, SWT.SINGLE| SWT.FULL_SELECTION| SWT.H_SCROLL | SWT.V_SCROLL );

        table.setHeaderVisible(true);
        table.addSelectionListener(
                new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e){
                        ISelection selection = connViewer.getSelection();
                         selectedData =
                            (LuntbuildConnection)((IStructuredSelection)selection).getFirstElement();
                         boolean doEnable = (selectedData != null);
                    	 buttonEdit.setEnabled(doEnable);
                    	 buttonDelete.setEnabled(doEnable);
                    	 buttonTest.setEnabled(doEnable);
                    }
                }
        );

        TableColumn column = new TableColumn(table, SWT.LEFT, 0);
        column.setText("Name");
        column.setWidth(100);

        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText("Url");
        column.setWidth(300);

        return table;
    }

    /**
     * Creates a single Close button.
     *
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CLOSE_ID,
                IDialogConstants.CLOSE_LABEL, true);
    }

    /**
     * Closes the dialog when the Close button is pressed.
     *
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.CLOSE_ID)
            cancelPressed();
        else
            super.buttonPressed(buttonId);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Manage Luntbuild Connections");
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
     * The content provider class is responsible for
     * providing objects representing connection info to the view.
     *
     * @author   Lubos Pochman
     * @version  $Revision: 432 $
     * @since    0.0.3
     */
    private class ConnectionViewProvider implements IStructuredContentProvider {

        /**
         * Default constructor
         */
        public ConnectionViewProvider() {
        }

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
         * @return array of {@link Build}
         *
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements(Object parent) {
            List dataList = new ArrayList();

            ArrayList viewers = LuntbuildView.mainView.getViewers();
            for (Iterator iter = viewers.iterator(); iter.hasNext();) {
                LuntbuildViewer viewer = (LuntbuildViewer) iter.next();
                dataList.add(viewer.getConnection());
            }
            return  dataList.toArray();
        }
    }

    /**
     * Connection View Label Provider
     *
     * @author Lubos Pochman
     *
     */
    private class ConnectionViewLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         *
         */
        public ConnectionViewLabelProvider() {
            super();
        }

        /**
         * @param obj
         * @param index
         * @return column text
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object obj, int index) {
            String result = "";
            if(obj instanceof Exception){
                return (index == 0)? "An Exception has occurred! See error log." : "";
            }
            ConnectionData data = ((LuntbuildConnection)obj).getConnectionData();
            switch (index) {
            case 0:
                result = data.getName();
                break;
            case 1:
                result = data.getUrl();
                break;
            default:
                break;
            }
            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage(Object obj, int index) {
            if(obj instanceof Exception){
                return null;
            }
            Image image = null;
            return image;
        }
    }

}
