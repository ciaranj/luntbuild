/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */

package com.luntsys.luntbuild.vcs;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.dom4j.Document;
import org.dom4j.Element;

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.remoting.facade.AccurevAdaptorFacade;
import com.luntsys.luntbuild.remoting.facade.AccurevModuleFacade;
import com.luntsys.luntbuild.facades.lb12.ModuleFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.OgnlHelper;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.SynchronizedDateFormatter;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.vcs.accurev.AccurevHelper;
import com.luntsys.luntbuild.vcs.accurev.AccurevModuleInterface;
import com.luntsys.luntbuild.vcs.accurev.ReferenceTreeInfo;
import com.luntsys.luntbuild.vcs.accurev.StreamInfo;

/**
 * AccuRev VCS adaptor implementation.
 * 
 * <p>This adaptor is NOT safe for remote hosts.</p>
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class AccurevAdaptor extends Vcs {
    /** Date format for AccuRev. */
    private static final String ACCUREV_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 23L;

	private String user;
	private String password;

	/**
	 * Gets the password to login to the AccuRev server.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sets the password to login to the AccuRev server.
	 * 
	 * @param password the password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the user to login to the AccuRev server.
	 * 
	 * @return the user
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * Sets the user to login to the AccuRev server.
	 * 
	 * @param user the user
	 */
	public void setUser(String user) {
		this.user = user;
	}

    /**
     * @inheritDoc
     */
    public String getDisplayName() {
        return "AccuRev";
    }

    /**
     * @inheritDoc
     */
    public String getIconName() {
        return "accurev.jpg";
    }

	/**
     * @inheritDoc
	 * @see AccurevModule
	 */
    public Module createNewModule() {
        return new AccurevModule();
    }

	/**
     * @inheritDoc
	 * @see AccurevModule
	 */
    public Module createNewModule(Module module) {
        return new AccurevModule((AccurevModule)module);
    }

    /**
     * @inheritDoc
     */
    public List getVcsSpecificProperties() {
        List properties = new ArrayList();
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "User";
			}

			public String getDescription() {
				return "User name to login to the AccuRev server.";
			}

			public String getValue() {
				return getUser();
			}

			public void setValue(String value) {
				setUser(value);
			}
		});
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Password";
			}

			public String getDescription() {
				return "Password to login to the AccuRev server.";
			}

			public boolean isSecret() {
				return true;
			}

			public String getValue() {
				return getPassword();
			}

			public void setValue(String value) {
				setPassword(value);
			}
		});
        return properties;
    }

    /**
     * Validates the modules of this VCS.
     *
     * @throws ValidationException if a module is not invalid
     */
    public void validateModules() {
        super.validateModules();
//        List modules = getModules();
//        for (Iterator iterator = modules.iterator(); iterator.hasNext();) {
//            AccurevModule module = (AccurevModule) iterator.next();
//        }
    }

    /**
     * @inheritDoc
     * @see AccurevAdaptorFacade
     */
    public void saveToFacade(VcsFacade facade) {
    	// TODO throw RuntimeException if the facade is not the right class
        AccurevAdaptorFacade accuFacade = (AccurevAdaptorFacade) facade;
        accuFacade.setUser(getUser());
        accuFacade.setPassword(getPassword());
    }

    /**
     * @inheritDoc
     * @throws RuntimeException if the facade is not an <code>AccurevAdaptorFacade</code>
     * @see AccurevAdaptorFacade
     */
    public void loadFromFacade(VcsFacade facade) {
        if (!(facade instanceof AccurevAdaptorFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        AccurevAdaptorFacade accuFacade = (AccurevAdaptorFacade) facade;
        setUser(accuFacade.getUser());
        setPassword(accuFacade.getPassword());
    }

    /**
     * @inheritDoc
     * @see AccurevAdaptorFacade
     */
    public VcsFacade constructFacade() {
        return new AccurevAdaptorFacade();
    }

	/**
     * @inheritDoc
	 */
    public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
        antProject.log("Getting Revisions... ");
        AccurevHelper.setUser(user, password, antProject);
        AccurevHelper.syncTime(antProject);
        final Revisions revisions = new Revisions();
        revisions.addLog(this.getClass().getName(), toString());
        revisions.getChangeLogs().add("*************************************************************");
        revisions.getChangeLogs().add(toString());
        revisions.getChangeLogs().add("");

        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            AccurevModule module = (AccurevModule) it.next();
            antProject.log("Getting Revisions for module: " + module);
            Commandline cmdLine = new Commandline();
            cmdLine.setExecutable("accurev");
            cmdLine.createArgument().setValue("hist");
            cmdLine.createArgument().setValue("-a");
            cmdLine.createArgument().setLine("-p " + module.getActualDepot());
            cmdLine.createArgument().setLine("-s " + module.getActualBackingStream());
            cmdLine.createArgument().setLine("-k promote");
            cmdLine.createArgument().setLine("-t \"now-" +
            		SynchronizedDateFormatter.formatDate(sinceDate, ACCUREV_DATE_FORMAT) + "\"");
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

                long timeVal = Long.parseLong(transaction.attributeValue("time"));
                timeVal = timeVal * 1000; // Accurev returns time in seconds
                Date transDate = new Date(timeVal);
                String dateString = SynchronizedDateFormatter.formatDate(transDate, ACCUREV_DATE_FORMAT);
                final String transactionStr = transaction.attributeValue("id");
                final long transactionNumber = Long.parseLong(transactionStr);

                final String comment = transaction.elementText("comment");

                buffer.append("Transaction ").append(transactionNumber).append(" date ")
                        .append(dateString).append(" by ").append(userName).append("\n\"")
                        .append(comment).append("\"");
                revisions.getChangeLogs().add(buffer.toString());
                revisions.addEntryToLastLog(transactionStr, userName, transDate, comment);

                for (Iterator versionIter = transaction.elementIterator("version"); versionIter.hasNext();) {
                    revisions.setFileModified(true);
                    buffer.setLength(0);
                    buffer.append("\t");
                    Element version = (Element) versionIter.next();
                    buffer.append(version.attributeValue("path").substring(2)).append(" (")
                            .append(version.attributeValue("virtual")).append(")");
                    revisions.getChangeLogs().add(buffer.toString());
                    revisions.addPathToLastEntry(version.attributeValue("path").substring(2), "", "");
                }
            }
        }
        return revisions;
    }

	/**
     * @inheritDoc
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

	/**
	 * Labels the contents of a module.
	 * 
	 * @param module the module
	 * @param antProject the ant project used for logging
	 */
    private void labelModule(AccurevModule module, Project antProject) {
        antProject.log("Labeling module " + module);
        final String label = "" + AccurevHelper.getLastTransactionNumber(module, antProject);
        antProject.log("Got label (max transaction) " + label);
        module.setLabel(label);
    }

	/**
     * @inheritDoc
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

	/**
	 * Checks out the contents from a module.
	 * 
	 * @param workingDir the working directory
	 * @param module the module
	 * @param isClean set <code>true</code> if this is a clean build
	 * @param isRebuild set <code>true</code> if this is a rebuild
	 * @param antProject the ant project used for logging
	 */
    private void retrieveModule(String workingDir, AccurevModule module, boolean isClean, boolean isRebuild, Project antProject) {
        final String srcPath = module.getActualSrcPath();
        if (isClean)
            antProject.log("Clean build of source path: " + srcPath, Project.MSG_INFO);
        if (isRebuild)
            antProject.log("Rebuild of source path: " + srcPath, Project.MSG_INFO);
        if (!(isClean || isRebuild))
            antProject.log("Incremental build of source path: " + srcPath, Project.MSG_INFO);

        final String depot = module.getActualDepot();
        final String buildStreamName = module.getActualBuildStream();
        StreamInfo buildStreamInfo = StreamInfo.findStreamInfo(depot, buildStreamName, antProject);
        if ((buildStreamInfo != null) && (!buildStreamInfo.getBackingStream().equals(module.getActualBackingStream()))) {
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
            buildStreamInfo.updateStream(Long.valueOf(module.getActualLabel()), antProject);
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

	/**
	 * An Accurev module definition.
	 *
	 * @author robin shine
	 */
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

		/**
		 * Constructor, creates a blank AccuRev module.
		 */
        public AccurevModule() {}

		/**
		 * Copy constructor, creates a AccuRev module from another AccuRev module.
		 * 
		 * @param module the module to create from
		 */
        public AccurevModule(AccurevModule module) {
            this.depot = module.depot;
            this.srcPath = module.srcPath;
            this.backingStream = module.backingStream;
            this.buildStream = module.buildStream;
            this.label = module.label;
        }

        /**
         * Gets the AccuRev depot name.
         * 
         * @return the depot name
         */
        public String getDepot() {
            return depot;
        }

        /**
         * Gets the AccuRev depot name. This method will parse OGNL variables.
         * 
         * @return the depot name
         */
        private String getActualDepot() {
			return OgnlHelper.evaluateScheduleValue(getDepot());
        }

        /**
         * Sets the AccuRev depot name.
         * 
         * @param depot the depot name
         */
        public void setDepot(String depot) {
            this.depot = depot;
        }

        /**
         * Gets the source path where this module should be put.
         * 
         * @return the source path
         */
        public String getSrcPath() {
            return srcPath;
        }

        /**
         * Gets the source path where this module should be put. This method will parse OGNL variables.
         * 
         * @return the source path
         */
        private String getActualSrcPath() {
			return OgnlHelper.evaluateScheduleValue(getSrcPath());
        }

        /**
         * Sets the source path where this module should be put.
         * 
         * @param srcPath the source path
         */
        public void setSrcPath(String srcPath) {
            this.srcPath = srcPath;
        }

        /**
         * Gets the backing stream.
         * 
         * @return the backing stream
         */
        public String getBackingStream() {
            return backingStream;
        }

        /**
         * Gets the backing stream. This method will parse OGNL variables.
         * 
         * @return the backing stream
         */
        private String getActualBackingStream() {
			return OgnlHelper.evaluateScheduleValue(getBackingStream());
        }

        /**
         * Sets the backing stream.
         * 
         * @param backingStream the backing stream
         */
        public void setBackingStream(String backingStream) {
            this.backingStream = backingStream;
        }

        /**
         * Gets the backing stream for this build module.
         * 
         * @return the build stream
         */
        public String getBuildStream() {
            return buildStream;
        }

        /**
         * Gets the backing stream for this build module. This method will parse OGNL variables.
         * 
         * @return the build stream
         */
        private String getActualBuildStream() {
			return OgnlHelper.evaluateScheduleValue(getBuildStream());
        }

        /**
         * Sets the backing stream for this build module.
         * 
         * @param buildStream the build stream
         */
        public void setBuildStream(String buildStream) {
            this.buildStream = buildStream;
        }

        /**
         * Gets the transaction number with which to sync.
         * 
         * @return the transaction number
         */
        public String getLabel() {
            return label;
        }

        /**
         * Gets the transaction number with which to sync. This method will parse OGNL variables.
         * 
         * @return the transaction number
         */
        private String getActualLabel() {
			return OgnlHelper.evaluateScheduleValue(getLabel());
        }

        /**
         * Sets the transaction number with which to sync.
         * 
         * @param label the transaction number
         */
        public void setLabel(String label) {
            this.label = label;
        }

        /**
         * Gets the reference tree.
         * 
         * @return the reference tree
         */
        public String getReferenceTree() {
            return getActualBuildStream() + "_reference";
        }

		/**
		 * @inheritDoc
		 */
        public List getProperties() {
            List properties = new ArrayList();
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "module path";
                }

                public String getDescription() {
                    return "The relative path under the project directory where this module should be put";
                }

                public boolean isRequired() {
                    return false;
                }

                public String getValue() {
                    return getSrcPath();
                }

                public String getActualValue() {
                    return getActualSrcPath();
                }

                public void setValue(String value) {
                    setSrcPath(value);
                }
            });
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "label";
                }

                public String getDescription() {
                    return "The label is the transaction number to which to sync. Specify the transaction number you want to build at.";
                }

                public boolean isRequired() {
                    return false;
                }

                public String getValue() {
                    return getLabel();
                }

                public String getActualValue() {
                    return getActualLabel();
                }

                public void setValue(String value) {
                    setLabel(value);
                }
            });
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "depot";
                }

                public String getDescription() {
                    return "The accurev depot to check code out from.";
                }

                public String getValue() {
                    return getDepot();
                }

                public String getActualValue() {
                    return getActualDepot();
                }

                public boolean isRequired() {
                    return true;
                }

                public void setValue(String value) {
                    setDepot(value);
                }
            });
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "backing stream";
                }

                public String getDescription() {
                    return "The backing stream for this build module. The backing stream should be " +
                            "able to have streams created from it by the build user.";
                }

                public String getValue() {
                    return getBackingStream();
                }

                public String getActualValue() {
                    return getActualBackingStream();
                }

                public boolean isRequired() {
                    return true;
                }

                public void setValue(String value) {
                    setBackingStream(value);
                }
            });
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "build stream";
                }

                public String getDescription() {
                    return "The name of the stream to create from the backing stream. If it doesn't exist " +
                            "it will be created. A reference tree will be created from this stream with a " +
                            "'_reference' suffix appended to the build stream name.";
                }

                public String getValue() {
                    return getBuildStream();
                }

                public String getActualValue() {
                    return getActualBuildStream();
                }

                public boolean isRequired() {
                    return true;
                }

                public void setValue(String value) {
                    setBuildStream(value);
                }
            });
            return properties;
        }

	    /**
	     * @inheritDoc
	     * @see AccurevModuleFacade
	     */
        public ModuleFacade getFacade() {
            AccurevModuleFacade facade = new AccurevModuleFacade();
            facade.setDepot(getDepot());
            facade.setBackingStream(getBackingStream());
            facade.setBuildStream(getBuildStream());
            return facade;
        }

	    /**
	     * @inheritDoc
	     * @throws RuntimeException if the facade is not an <code>AccurevModuleFacade</code>
	     * @see AccurevModuleFacade
	     */
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

    	/**
    	 * Converts this module to a string.
    	 * 
    	 * @return the string representation of this module
    	 */
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
