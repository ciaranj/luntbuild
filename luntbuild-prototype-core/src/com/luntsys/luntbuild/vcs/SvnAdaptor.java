/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-7-10
 * Time: 10:34:47
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.luntsys.luntbuild.vcs;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.IStringProperty;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb20.SvnAdaptorFacade;
import com.luntsys.luntbuild.facades.lb20.SvnModuleFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.ValidationException;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.*;
import org.tmatesoft.svn.util.SVNDebugLog;

import java.io.File;
import java.util.*;

/**
 * The subversion adaptor
 *
 * @author robin shine
 */
public class SvnAdaptor extends Vcs {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1;

    static {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
    }

    private String urlBase;
    private String trunk;
    private String branches;
    private String tags;
    private String user;
    private String password;

    public String getDisplayName() {
        return "Subversion";
    }

    public String getIconName() {
        return "svn.jpg";
    }

    private SVNClientManager getClientManager() {
        return SVNClientManager.newInstance(SVNWCUtil.createDefaultOptions(true), this.user, this.password);
    }

    public List getVcsSpecificProperties() {
        List properties = getSvnProperties();
        return properties;
    }

    public void checkoutActually(Build build, Project antProject) {
        String workingDir = build.getSchedule().getWorkDirRaw();
        // retrieve modules
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            SvnModule module = (SvnModule) Luntbuild.cloneModule(this, (Vcs.Module) it.next());
            if (build.isRebuild() && Luntbuild.isEmpty(module.getLabel()))
                module.setLabel(Luntbuild.getLabelByVersion(build.getVersion()));
            if (build.isRebuild() || build.isCleanBuild())
                retrieveModule(workingDir, module, antProject);
            else
                updateModule(workingDir, module, antProject);
        }
    }

    public void label(Build build, Project antProject) {
        String workingDir = build.getSchedule().getWorkDirRaw();
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            SvnModule module = (SvnModule) it.next();
            if (Luntbuild.isEmpty(module.getLabel()))
                labelModule(workingDir, module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
        }
    }

    public Vcs.Module createNewModule() {
        return new SvnModule();
    }

    public Vcs.Module createNewModule(Vcs.Module module) {
        return new SvnModule((SvnModule)module);
    }

    /** Retrieve module
     * @param workingDir
     * @param module
     * @param antProject
     */
    public void retrieveModule(String workingDir, SvnModule module, Project antProject) {
        File destDir = getModuleDestDir(module, workingDir);
        SVNURL url = getModuleUrl(module);

		antProject.log("Retrieve url: " + url, Project.MSG_INFO);

        initLogger(antProject);

        SVNUpdateClient updateClient = getClientManager().getUpdateClient();
        try {
            updateClient.doSwitch(destDir, url, SVNRevision.HEAD, true);
        } catch (SVNException e) {
            try {
                updateClient.doCheckout(url, destDir, SVNRevision.HEAD, SVNRevision.HEAD, true);
            } catch (SVNException e1) {
                throw new RuntimeException("Error executing checkout svn command", e1);
            }
        }
    }

    private SVNURL getModuleUrl(SvnModule module) {
        String urlString = Luntbuild.concatPath(getUrlBase(), mapPathByBranchLabel(module.getSrcPath(),
                module.getBranch(), module.getLabel()));
        try {
            return SVNURL.parseURIEncoded(urlString);
        } catch (SVNException e) {
            throw new RuntimeException("Error parsing url: " + urlString, e);
        }
    }

    /** Label module
     * @param workingDir
     * @param module
     * @param label
     * @param antProject
     */
    public synchronized void labelModule(String workingDir, SvnModule module, String label, Project antProject) {
        // no need to label this module cause this module is fetched from tags directory
        File dir = new File("/", module.getSrcPath());
        File tagsDir = new File("/", getTagsDir());

        boolean isParent = false;
        while (dir != null && !(isParent = dir.equals(tagsDir))) {
            dir = dir.getParentFile();
        }

        if (isParent) {
            return;
        }

        SVNURL srcUrl = getModuleUrl(module);

        antProject.log("Label url: " + srcUrl, Project.MSG_INFO);

        initLogger(antProject);

        String mapped = mapPathByLabel(module.getSrcPath(), label);
        String urlString = Luntbuild.concatPath(getUrlBase(), mapped);

        SVNURL url = parseUrl(urlString);
        SVNClientManager clientManager = getClientManager();

        try {
            SVNRepository repository = clientManager.createRepository(url, true);
            SVNNodeKind nodeKind = repository.checkPath("", -1);
            if (nodeKind != SVNNodeKind.NONE) {
                throw new BuildException("Failed to create label, url \"" + urlString + "\" already exists.");
            }
        } catch (SVNException e) {
            throw new RuntimeException("Error checking that url doesn't exits: " + urlString, e);
        }

        createLabelParentDir(mapped, clientManager, antProject);

        try {
        	String path = workingDir;
        	if (module.getSrcPath() != null && module.getSrcPath().trim().length() > 0)
        		path += File.separatorChar + module.getSrcPath().trim();
            clientManager.getCopyClient().doCopy(new File(path), SVNRevision.WORKING, url, false, "Labeled: " + label);
        } catch (SVNException e) {
            throw new RuntimeException("Error executing copy svn command", e);
        }
    }

    private SVNURL parseUrl(String urlString) {
        SVNURL url;
        try {
            url = SVNURL.parseURIEncoded(urlString);
        } catch (SVNException e) {
            throw new RuntimeException("Error parsing url: " + urlString, e);
        }
        return url;
    }

    private void createLabelParentDir(String mapped, SVNClientManager clientManager, Project antProject) {
        String mappedParent = StringUtils.substringBeforeLast(StringUtils.stripEnd(mapped, "/"), "/");
        String[] fields = mappedParent.split("/");
        SVNURL baseUrl = parseUrl(getUrlBase());
        try {
            SVNRepository repository = clientManager.createRepository(baseUrl, true);

            String path = null;

            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];
                if (!field.trim().equals("")) {
                    path = Luntbuild.concatPath(path, field);

                    SVNNodeKind nodeKind = repository.checkPath(path, -1);
                    if (nodeKind == SVNNodeKind.NONE) {
                        SVNURL url = baseUrl.appendPath(path, true);
                        antProject.log("Seems that baseUrl \"" + url + "\" does not exist, creating...", Project.MSG_INFO);
                        clientManager.getCommitClient().doMkDir(new SVNURL[]{url}, "Creating parent directory for a label");
                    }
                }
            }
        } catch (SVNException e) {
            throw new RuntimeException("Error creating label: baseUrl = " + baseUrl + ", mappedParent = " + mappedParent, e);
        }
    }

    /** Update module
     * @param workingDir
     * @param module
     * @param antProject
     */
    public void updateModule(String workingDir, SvnModule module, Project antProject) {
		antProject.log("Update url: " + getModuleUrl(module));

        File destDir = getModuleDestDir(module, workingDir);

        try {
            getClientManager().getUpdateClient().doUpdate(destDir, SVNRevision.HEAD, true);
        } catch (SVNException e) {
            retrieveModule(workingDir, module, antProject);
        }
    }

    private File getModuleDestDir(SvnModule module, String workingDir) {
        File destDir;
        if (Luntbuild.isEmpty(module.getDestPath()))
            destDir = new File(workingDir, module.getSrcPath());
        else
            destDir = new File(workingDir, module.getDestPath());
        return destDir;
    }

    /**
     * @return url base
     */
    public String getUrlBase() {
        return this.urlBase;
    }

    /**
     * @param urlBase url base
     */
    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

    /**
     * @return user
     */
    public String getUser() {
        return this.user;
    }

    /**
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return trunk
     */
    public String getTrunk() {
        return this.trunk;
    }

    /**
     * @param trunk
     */
    public void setTrunk(String trunk) {
        this.trunk = trunk;
    }

    public void validateModules() {
        super.validateModules();
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            SvnModule module = (SvnModule) it.next();
            if (module.getSrcPath().indexOf('\\') != -1)
                throw new ValidationException("Source path \"" + module.getSrcPath() + "\" should not contain character '\\'");
        }
    }

    public Revisions getRevisionsSince(final Date sinceDate, Schedule workingSchedule, Project antProject) {
        SVNLogClient logClient = getClientManager().getLogClient();
        final Revisions revisions = new Revisions();

        initLogger(antProject);

        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            SvnModule module = (SvnModule) it.next();
            if (Luntbuild.isEmpty(module.getLabel())) {
                SVNURL url = getModuleUrl(module);

                antProject.log("Getting revisions for url: " + url, Project.MSG_INFO);

                ISVNLogEntryHandler handler = new ISVNLogEntryHandler() {
                    public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
                        Date revisionDate = logEntry.getDate();
                        if (!revisionDate.before(sinceDate)) {
                            String author = logEntry.getAuthor();
                            revisions.getChangeLogins().add(author);
                            List logs = revisions.getChangeLogs();
                            logs.add("----------------------------------------------------------------------------------------------------------------------");
                            logs.add("r" + logEntry.getRevision() + " | " + author + " | " + revisionDate.toString());
                            logs.add("Changed paths:");
                            Collection pathEntries = logEntry.getChangedPaths().values();
                            for (Iterator iterator = pathEntries.iterator(); iterator.hasNext();) {
                                SVNLogEntryPath logEntryPath = (SVNLogEntryPath) iterator.next();
                                logs.add("    " + logEntryPath.getType() + " " + logEntryPath.getPath());
                                revisions.setFileModified(true);
                            }

                            logs.add(logEntry.getMessage());
                        }
                    }
                };
                try {
                    logClient.doLog(url, null, SVNRevision.HEAD, SVNRevision.create(sinceDate), SVNRevision.HEAD,
                            false, true, 0, handler);
                } catch (SVNException e) {
                    throw new RuntimeException("Error executing log svn command: " + url, e);
                }
            }
        }
        return revisions;
    }

    /**
     * Map a subversion path to sub directory of tags or branches based on the branch or label name
     * Label will take preference over branch
     *
     * @param path
     * @param branch
     * @param label
     * @return calculated path
     */
    private String mapPathByBranchLabel(String path, String branch, String label) {
        if (!Luntbuild.isEmpty(label))
            return mapPathByLabel(path, label);
        else
            return mapPathByBranch(path, branch);
    }

    /**
     * Map a subversion path to sub directory of tags based on the label name
     *
     * @param path
     * @param label should not be empty
     * @return calculated path
     */
    private String mapPathByLabel(String path, String label) {
        String mapped = Luntbuild.concatPath(getTagsDir(), label);
        return Luntbuild.concatPath(mapped, path);
    }

    /**
     * Map a subversion path to sub directory of branches based on the branch name,
     * or to sub directory under trunk if branch name is empty
     *
     * @param path
     * @param branch maybe empty
     * @return calculated path
     */
    private String mapPathByBranch(String path, String branch) {
        String mapped;
        if (!Luntbuild.isEmpty(branch))
            mapped = Luntbuild.concatPath(getBranchesDir(), branch);
        else
            mapped = getTrunkDir();
        return Luntbuild.concatPath(mapped, path);
    }

    /**
     * @return branches
     */
    public String getBranches() {
        return this.branches;
    }

    /**
     * @param branches
     */
    public void setBranches(String branches) {
        this.branches = branches;
    }

    /**
     * @return tags
     */
    public String getTags() {
        return this.tags;
    }

    /**
     * @param tags
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * @return trunk dir
     */
    public String getTrunkDir() {
        if (Luntbuild.isEmpty(getTrunk()))
            return "";
        else
            return getTrunk();
    }

    /**
     * @return branch dir
     */
    public String getBranchesDir() {
        if (Luntbuild.isEmpty(getBranches()))
            return "branches";
        else
            return getBranches();
    }

    /**
     * @return tags dir
     */
    public String getTagsDir() {
        if (Luntbuild.isEmpty(getTags()))
            return "tags";
        else
            return getTags();
    }

    private void initLogger(Project antProject) {
        SvnCustomLogger svnLogger = new SvnCustomLogger(antProject);
        SVNDebugLog.setDefaultLog(svnLogger);
    }

    /**
     * Svn Module
     *
     */
    public class SvnModule extends Module {
        /**
         * Keep tracks of version of this class, used when do serialization-deserialization
         */
        static final long serialVersionUID = 1L;

        private String srcPath;
        private String branch;
        private String label;
        private String destPath;

        /**
         * Constructor
         */
        public SvnModule() {}

        /**
         * Copy Constructor
         * @param module
         */
        public SvnModule(SvnModule module) {
            this.srcPath = module.srcPath;
            this.branch = module.branch;
            this.label = module.label;
            this.destPath = module.destPath;
        }

        /**
         * @return source path
         */
        public String getSrcPath() {
            return this.srcPath;
        }

        /**
         * @param srcPath
         */
        public void setSrcPath(String srcPath) {
            this.srcPath = srcPath;
        }

        /**
         * @return branch
         */
        public String getBranch() {
            return this.branch;
        }

        /**
         * @param branch
         */
        public void setBranch(String branch) {
            this.branch = branch;
        }

        /**
         * @return label
         */
        public String getLabel() {
            return this.label;
        }

        /**
         * @param label
         */
        public void setLabel(String label) {
            this.label = label;
        }

        /**
         * @return dest path
         */
        public String getDestPath() {
            return this.destPath;
        }

        /**
         * @param destPath
         */
        public void setDestPath(String destPath) {
            this.destPath = destPath;
        }

        public List getProperties() {
            List properties = getSvnModuleProperties();
            return properties;
        }

        public com.luntsys.luntbuild.facades.lb20.ModuleFacade getFacade() {
            SvnModuleFacade facade = new com.luntsys.luntbuild.facades.lb20.SvnModuleFacade();
            facade.setBranch(getBranch());
            facade.setDestPath(getDestPath());
            facade.setLabel(getLabel());
            facade.setSrcPath(getSrcPath());
            return facade;
        }

        public void setFacade(com.luntsys.luntbuild.facades.lb20.ModuleFacade facade) {
            if (facade instanceof com.luntsys.luntbuild.facades.lb20.SvnModuleFacade) {
                SvnModuleFacade svnModuleFacade = (com.luntsys.luntbuild.facades.lb20.SvnModuleFacade) facade;
                setBranch(svnModuleFacade.getBranch());
                setLabel(svnModuleFacade.getLabel());
                setSrcPath(svnModuleFacade.getSrcPath());
                setDestPath(svnModuleFacade.getDestPath());
            } else
                throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        }
    }

    public void saveToFacade(com.luntsys.luntbuild.facades.lb20.VcsFacade facade) {
        SvnAdaptorFacade svnFacade = (SvnAdaptorFacade) facade;
        svnFacade.setTrunk(getTrunk());
        svnFacade.setBranches(getBranches());
        svnFacade.setPassword(getPassword());
        svnFacade.setTags(getTags());
        svnFacade.setUrlBase(getUrlBase());
        svnFacade.setUser(getUser());
    }

    public void loadFromFacade(com.luntsys.luntbuild.facades.lb20.VcsFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb20.SvnAdaptorFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb20.SvnAdaptorFacade svnFacade = (com.luntsys.luntbuild.facades.lb20.SvnAdaptorFacade) facade;
        setTrunk(svnFacade.getTrunk());
        setBranches(svnFacade.getBranches());
        setPassword(svnFacade.getPassword());
        setTags(svnFacade.getTags());
        setUrlBase(svnFacade.getUrlBase());
        setUser(svnFacade.getUser());
    }

    public com.luntsys.luntbuild.facades.lb20.VcsFacade constructFacade() {
        return new SvnAdaptorFacade();
    }

}

