/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.vcs;

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.IStringProperty;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb20.AccurevAdaptorFacade;
import com.luntsys.luntbuild.facades.lb20.AccurevModuleFacade;
import com.luntsys.luntbuild.facades.lb20.ModuleFacade;
import com.luntsys.luntbuild.facades.lb20.VcsFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.vcs.accurev.AccurevHelper;
import com.luntsys.luntbuild.vcs.accurev.AccurevModuleInterface;
import com.luntsys.luntbuild.vcs.accurev.ReferenceTreeInfo;
import com.luntsys.luntbuild.vcs.accurev.StreamInfo;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Adapter to an Accurev VCS
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class AccurevAdaptor extends Vcs {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 23L;
    public static final SimpleDateFormat ACCUREV_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


	private String user;
	private String password;

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	/**
     * @return a string value describes type of the version control system
     */
    public String getDisplayName() {
        return "AccuRev";
    }

    public String getIconName() {
        return "accurev.jpg";
    }

    public Module createNewModule() {
        return new AccurevModule();
    }

    public Module createNewModule(Module module) {
        return new AccurevModule((AccurevModule)module);
    }

    public List getVcsSpecificProperties() {
        List properties = getAccurevProperties();
        return properties;
    }

    public void validateModules() {
        super.validateModules();
    }

    public VcsFacade getFacade() {
        AccurevAdaptorFacade facade = new AccurevAdaptorFacade();
        List modules = getModules();
        for (Iterator iterator = modules.iterator(); iterator.hasNext();) {
            Module module = (Module) iterator.next();
            facade.getModules().add(module.getFacade());
        }
        return facade;
    }

    public void setFacade(VcsFacade facade) {
        AccurevAdaptorFacade accurevAdaptorFacade = (AccurevAdaptorFacade) facade;
        getModules().clear();
        Iterator it = accurevAdaptorFacade.getModules().iterator();
        while (it.hasNext()) {
            AccurevModuleFacade moduleFacade = (AccurevModuleFacade) it.next();
            AccurevModule module = new AccurevModule();
            module.setFacade(moduleFacade);
            getModules().add(module);
        }
    }

    public void saveToFacade(VcsFacade facade) {
        com.luntsys.luntbuild.facades.lb20.AccurevAdaptorFacade accuFacade =
            (com.luntsys.luntbuild.facades.lb20.AccurevAdaptorFacade) facade;
        accuFacade.setUser(getUser());
        accuFacade.setPassword(getPassword());
    }

    public void loadFromFacade(VcsFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb20.AccurevAdaptorFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb20.AccurevAdaptorFacade accuFacade =
            (com.luntsys.luntbuild.facades.lb20.AccurevAdaptorFacade) facade;
        setUser(accuFacade.getUser());
        setPassword(accuFacade.getPassword());
    }

    public VcsFacade constructFacade() {
        return new AccurevAdaptorFacade();
    }

    /**
     * Get a list of change logs covered by current vcs config since
     * the specified date.
     *
     * @param sinceDate       the oldest revision date to include
     * @param workingSchedule
     * @param antProject
     * @return list of file modification descriptions
     */
    public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
        antProject.log("Getting Revisions... ");
        AccurevHelper.setUser(user, password, antProject);
        AccurevHelper.syncTime(antProject);
        final Revisions revisions = new Revisions();
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            AccurevModule module = (AccurevModule) it.next();
            antProject.log("Getting Revisions for module: " + module);
            Commandline cmdLine = new Commandline();
            cmdLine.setExecutable("accurev");
            cmdLine.createArgument().setValue("hist");
            cmdLine.createArgument().setValue("-a");
            cmdLine.createArgument().setLine("-p " + module. getDepot());
            cmdLine.createArgument().setLine("-s " + module.getBackingStream());
            cmdLine.createArgument().setLine("-k promote");
            cmdLine.createArgument().setLine("-t \"now-" + ACCUREV_DATE_FORMAT.format(sinceDate) + "\"");
            cmdLine.createArgument().setValue("-fx");
            Document doc = AccurevHelper.buildResponseDocument(cmdLine, antProject);
            if (doc == null) {
                return null;
            }

            Element root = doc.getRootElement();

            StringBuffer buffer = new StringBuffer();
            // iterate through child elements of root with element name "transaction"
            for (Iterator transactionIter = root.elementIterator("transaction"); transactionIter.hasNext();) {
                buffer.setLength(0);
                Element transaction = (Element) transactionIter.next();
                final String userName = transaction.attributeValue("user");
                revisions.getChangeLogins().add(userName);
                final String timeAttr = transaction.attributeValue("time");
                long timeVal = Long.parseLong(timeAttr);
                timeVal = timeVal * 1000; // Accurev returns time in seconds
                Date transDate = new Date(timeVal);
                String dateString = ACCUREV_DATE_FORMAT.format(transDate);
                final String transactionStr = transaction.attributeValue("id");
                final long transactionNumber = Long.parseLong(transactionStr);
                final String comment = transaction.elementText("comment");
                buffer.append("Transaction ").append(transactionNumber).append(" date ")
                        .append(dateString).append(" by ").append(userName).append("\n\"")
                        .append(comment).append("\"");
                revisions.getChangeLogs().add(buffer.toString());
                for (Iterator versionIter = transaction.elementIterator("version"); versionIter.hasNext();) {
                    revisions.setFileModified(true);
                    buffer.setLength(0);
                    buffer.append("\t");
                    Element version = (Element) versionIter.next();
                    buffer.append(version.attributeValue("path").substring(2)).append(" (")
                            .append(version.attributeValue("virtual")).append(")");
                    revisions.getChangeLogs().add(buffer.toString());
                }
            }
        }
        return revisions;
    }

    /**
     * Label contents in version control system
     *
     * @param build
     * @param antProject
     */
    public void label(Build build, Project antProject) {
        antProject.log("Labeling...");
//        AccurevHelper.syncTime(antProject);
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            AccurevModule module = (AccurevModule) it.next();
            labelModule(module, antProject);
        }
    }

    private void labelModule(AccurevModule module, Project antProject) {
        antProject.log("Labeling module " + module);
        final String label = "" + AccurevHelper.getLastTransactionNumber(module, antProject);
        antProject.log("Got label (max transaction) " + label);
        module.setLabel(label);
    }

    /**
     * Checkout contents from version control system
     *
     * @param build
     * @param antProject
     */
    public void checkoutActually(Build build, Project antProject) {
        antProject.log("Checking out...");
        AccurevHelper.setUser(user, password, antProject);
        AccurevHelper.syncTime(antProject);
        String workingDir = build.getSchedule().getWorkDirRaw();
        // retrieve modules
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            AccurevModule module = (AccurevModule) Luntbuild.cloneModule(this, (Module) it.next());
            antProject.log("Checking out module " + module);
            if (build.isRebuild() && Luntbuild.isEmpty(module.getLabel()))
                throw new BuildException("The label cannot be empty for rebuilding this accurev project. The label is the transaction number to sync to.");
            retrieveModule(workingDir, module, build.isCleanBuild(), build.isRebuild(), antProject);
        }
    }

    private void retrieveModule(String workingDir, AccurevModule module, boolean isClean, boolean isRebuild, Project antProject) {
        final String srcPath = module.getSrcPath();
        if (isClean)
            antProject.log("Clean build of source path: " + srcPath, Project.MSG_INFO);
        if (isRebuild)
            antProject.log("Rebuild of source path: " + srcPath, Project.MSG_INFO);
        if (!(isClean || isRebuild))
            antProject.log("Incremental build of source path: " + srcPath, Project.MSG_INFO);

        final String depot = module.getDepot();
        final String buildStreamName = module.getBuildStream();
        StreamInfo buildStreamInfo = StreamInfo.findStreamInfo(depot, buildStreamName, antProject);
        if ((buildStreamInfo != null) && (!buildStreamInfo.getBackingStream().equals(module.getBackingStream()))) {
            antProject.log("Found build stream is not based on backing stream, updating.");
            buildStreamInfo.updateStream(null, antProject);
        }
        ReferenceTreeInfo referenceTreeInfo = ReferenceTreeInfo.findReferenceTreeInfo(depot, module.getReferenceTree(), antProject);
        if (referenceTreeInfo != null) {
            if (!referenceTreeInfo.getBasisStream().getName().equals(buildStreamName)) {
                throw new BuildException("Existing reference tree '" + module.getReferenceTree() +
                        "' already exists and is based on stream '" + referenceTreeInfo.getBasisStream().getName() +
                        "' not build stream '" + buildStreamName + "'");
            }
            if (referenceTreeInfo.isHidden()) {
                antProject.log("Reference tree " + referenceTreeInfo.getName() + " was hidden, reactivating");
                referenceTreeInfo.reactivate(antProject);
            }
            // get the canonical form for the directories
            File storageDir = new File(referenceTreeInfo.getStorage());
            File workingDirFile = new File(workingDir);
            if (!storageDir.equals(workingDirFile)) {
                antProject.log("Moving reference tree " + referenceTreeInfo.getName() + " from " + referenceTreeInfo.getStorage() + " to " + workingDir);
                referenceTreeInfo.move(workingDir, antProject);
            }
        }

        if (isRebuild) {
            if ((buildStreamInfo == null) || (referenceTreeInfo == null)) {
                throw new BuildException("Cannot rebuild a module which hasn't been built before. Do a clean build first.");
            }
            buildStreamInfo.updateStream(Long.valueOf(module.getLabel()), antProject);
            AccurevHelper.forceWorkingDirRefresh(workingDir, module, antProject);
        } else {
            if (buildStreamInfo == null) {
                if (referenceTreeInfo != null) {
                    throw new BuildException("There is an existing reference tree named " + module.getReferenceTree() + ". Reference trees cannot be re-homed.");
                }
                StreamInfo.buildStream(buildStreamName, module, null, antProject);
                ReferenceTreeInfo.buildReferenceTree(workingDir, module, antProject);
            }
            if (isClean) {
                if (referenceTreeInfo == null) {
                    ReferenceTreeInfo.buildReferenceTree(workingDir, module, antProject);
                    ReferenceTreeInfo.updateReferenceTree(module, antProject);
                } else {
                    ReferenceTreeInfo.updateReferenceTree(module, antProject); // set us to the right version
                    AccurevHelper.forceWorkingDirRefresh(workingDir, module, antProject);
                }
            } else {
                ReferenceTreeInfo.updateReferenceTree(module, antProject);
            }
        }
    }

    public class AccurevModule extends Module implements AccurevModuleInterface {
        /**
         * Keep tracks of version of this class, used when do serialization-deserialization
         */
        static final long serialVersionUID = 1;

        private String depot;
        private String srcPath;
        private String backingStream;
        private String buildStream;
        private String label;

        public AccurevModule() {}

        public AccurevModule(AccurevModule module) {
            this.depot = module.depot;
            this.srcPath = module.srcPath;
            this.backingStream = module.backingStream;
            this.buildStream = module.buildStream;
            this.label = module.label;
        }

        public String getDepot() {
            return depot;
        }

        public void setDepot(String depot) {
            this.depot = depot;
        }

        public String getSrcPath() {
            return srcPath;
        }

        public void setSrcPath(String srcPath) {
            this.srcPath = srcPath;
        }

        public String getBackingStream() {
            return backingStream;
        }

        public void setBackingStream(String backingStream) {
            this.backingStream = backingStream;
        }

        public String getBuildStream() {
            return buildStream;
        }

        public void setBuildStream(String buildStream) {
            this.buildStream = buildStream;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getReferenceTree() {
            return buildStream + "_reference";
        }

        public List getProperties() {
            List properties = getAccurevModuleProperties();
            return properties;
        }

        public ModuleFacade getFacade() {
            AccurevModuleFacade facade = new AccurevModuleFacade();
            facade.setDepot(getDepot());
            facade.setBackingStream(getBackingStream());
            facade.setBuildStream(getBuildStream());
            return facade;
        }

        public void setFacade(ModuleFacade facade) {
            if (facade instanceof AccurevModuleFacade) {
                AccurevModuleFacade moduleFacade = (AccurevModuleFacade) facade;
                setLabel(moduleFacade.getLabel());
                setDepot(moduleFacade.getDepot());
                setBackingStream(moduleFacade.getBackingStream());
                setBuildStream(moduleFacade.getBuildStream());
            } else
                throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        }

        public String toString() {
            return new ToStringBuilder(this)
                    .append("depot", getDepot())
                    .append("backing stream", getBackingStream())
                    .append("build stream", getBuildStream())
//            .append("destination directory", getDestPath())
                    .toString();
        }

    }

}
