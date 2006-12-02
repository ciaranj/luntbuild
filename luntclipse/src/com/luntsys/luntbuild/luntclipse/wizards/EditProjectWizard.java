package com.luntsys.luntbuild.luntclipse.wizards;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.luntsys.luntbuild.luntclipse.core.LuntbuildConnection;
import com.luntsys.luntbuild.luntclipse.model.BasicProjectData;

/**
 * Edit/Create Project wizard
 *
 * @author Lubos Pochman
 *
 */
public class EditProjectWizard extends Wizard {

    private boolean doCreate = true;

    private BasicProjectPage basicPage;
    private VcsProjectPage vcsPage;
    private BuilderProjectPage builderPage;
    private ScheduleProjectPage schedulePage;

    private LuntbuildConnection connection;
    private List allData = null;


    /**
     * @param connection
     */
    public EditProjectWizard(LuntbuildConnection connection) {
        super();
        setNeedsProgressMonitor(true);
        this.doCreate = true;
        this.connection = connection;
    }

    /**
     * @param connection
     * @param allData
     */
    public EditProjectWizard(LuntbuildConnection connection, List allData) {
        super();
        setNeedsProgressMonitor(true);
        this.doCreate = false;
        this.connection = connection;
        this.allData = allData;
    }

    /**
     *
     *
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        if (this.doCreate)
            this.basicPage = new BasicProjectPage(this.connection);
        else
            this.basicPage = new BasicProjectPage(this.connection, this.allData);
        if (this.doCreate)
            this.vcsPage = new VcsProjectPage(this.connection);
        else
            this.vcsPage = new VcsProjectPage(this.connection, this.allData);
        if (this.doCreate)
            this.builderPage = new BuilderProjectPage(this.connection);
        else
            this.builderPage = new BuilderProjectPage(this.connection, this.allData);
        if (this.doCreate)
            this.schedulePage = new ScheduleProjectPage(this.connection);
        else
            this.schedulePage = new ScheduleProjectPage(this.connection, this.allData);
        addPage(this.basicPage);
        addPage(this.vcsPage);
        addPage(this.builderPage);
        addPage(this.schedulePage);
    }

    public boolean canFinish() {
        if (this.doCreate) {
            return this.basicPage.getData() != null;
        } else
            return true;
    }

    /**
     * @return
     *
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
        BasicProjectData basicData = this.basicPage.getData();
        if (basicData == null) return false;
        List vcsData = this.vcsPage.getData();
        List builderData = this.builderPage.getData();
        List scheduleData = this.schedulePage.getData();

        // process data
        if (this.doCreate)
            this.connection.createProject(basicData, vcsData, builderData, scheduleData);
        else
            this.connection.editProject(basicData, vcsData, builderData, scheduleData);

        return true;
    }

    /**
     * @return all project/schedules
     */
    public String[] getAllSchedules() {
        return this.connection.getAllSchedules();
    }

    /**
     * @return Returns the schedulePage.
     */
    public final ScheduleProjectPage getSchedulePage() {
        return this.schedulePage;
    }

    /**
     * @return Returns the basicPage.
     */
    public final BasicProjectPage getBasicPage() {
        return this.basicPage;
    }

    /**
     * @return Returns the builderPage.
     */
    public final BuilderProjectPage getBuilderPage() {
        return this.builderPage;
    }

    /**
     * @return Returns the connection.
     */
    public final LuntbuildConnection getConnection() {
        return this.connection;
    }

    /**
     * @return Returns the doCreate.
     */
    public final boolean isDoCreate() {
        return this.doCreate;
    }

    /**
     * @return Returns the vcsPage.
     */
    public final VcsProjectPage getVcsPage() {
        return this.vcsPage;
    }
}
