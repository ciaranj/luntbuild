/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.vcs;

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.facades.lb12.AccurevAdaptorFacade;
import com.luntsys.luntbuild.facades.lb12.ModuleFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.facades.lb12.AccurevModuleFacade;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.vcs.accurev.AccurevHelper;
import com.luntsys.luntbuild.vcs.accurev.ReferenceTreeInfo;
import com.luntsys.luntbuild.vcs.accurev.StreamInfo;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.dom4j.Document;
import org.dom4j.Element;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Adapter to an Accurev VCS
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class AccurevAdaptor extends Vcs {
    /**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1L;
    public static final SimpleDateFormat ACCUREV_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * Accurev port, such as 1666 or <server>:1666, etc.
     */
    private String port;

	private String accurevDir;

    /**
     * @return a string value describes type of the version control system
     */
    public String getDisplayName() {
        return "AccuRev";
    }

    public String getIconName() {
        return "accurev.jpg";
    }

	public String getAccurevDir() {
		return accurevDir;
	}

	public void setAccurevDir(String accurevDir) {
		this.accurevDir = accurevDir;
	}

    public Module createNewModule() {
        return new AccurevModule();
    }

    /**
     * The project level properties will be shown at project basic information page
     *
     * @return list of project level display properties of the version control system.
     *         Should not be empty
     */
    public List getVcsSpecificProperties() {
       List properties = new ArrayList();
       properties.add(new DisplayProperty() {
           public String getDisplayName() {
               return "AccuRev port";
           }

           public String getDescription() {
               return "The AccuRev port in the format of <servername>:<port>, " +
                       "where <servername> and <port> will be replaced by the actual AccuRev " +
                       "server name and the port number. This property is optional and overrides " +
                       "acclient.cnf values.";
           }

           public String getValue() {
               return getPort();
           }

           public void setValue(String value) {
               setPort(value);
           }

           public boolean isRequired() {
               return false;
           }
       });
		properties.add(new DisplayProperty() {
			public String getDisplayName() {
				return "Path for accurev executable";
			}

			public String getDescription() {
				return "The directory path, where your accurev executable file resides in. " +
						"It should be specified here, if it does not exist in the system path.";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getAccurevDir();
			}

			public void setValue(String value) {
				setAccurevDir(value);
			}
		});
       return properties;
    }

    /**
     * @return port
     */
    public String getPort() {
        return this.port;
    }

    /** Sets port
     * @param port
     */
    public void setPort(String port) {
        this.port = port;
    }

	/**
	 * Build the executable part of a commandline object
	 *
	 * @return
	 */
	public Commandline buildAccurevExecutable() {
		Commandline cmdLine = new Commandline();
		if (Luntbuild.isEmpty(getAccurevDir()))
			cmdLine.setExecutable("accurev");
		else
			cmdLine.setExecutable(Luntbuild.concatPath(getAccurevDir(), "accurev"));
		return cmdLine;
	}

	public com.luntsys.luntbuild.facades.lb12.VcsFacade constructFacade() {
		return new AccurevAdaptorFacade();
	}

    public void saveToFacade(com.luntsys.luntbuild.facades.lb12.VcsFacade facade) {
        AccurevAdaptorFacade accurevFacade = (AccurevAdaptorFacade) facade;
		accurevFacade.setPort(getPort());
		accurevFacade.setAccurevDir(getAccurevDir());
    }

    public void loadFromFacade(VcsFacade facade) {
        AccurevAdaptorFacade accurevFacade = (com.luntsys.luntbuild.facades.lb12.AccurevAdaptorFacade) facade;
		setPort(accurevFacade.getPort());
		setAccurevDir(accurevFacade.getAccurevDir());
    }

    /**
     * Get a list of change logs covered by current vcs config since
     * the specified date.
     *
     */
    public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
        antProject.log("Getting Revisions... ");
        AccurevHelper.syncTime(antProject, this);
        final Revisions revisions = new Revisions();
        Iterator it = getModules().iterator();
		while (it.hasNext()) {
            AccurevModule module = (AccurevModule) it.next();
            antProject.log("Getting Revisions for module: " + module);
            Commandline cmdLine = buildAccurevExecutable();
            cmdLine.createArgument().setValue("hist");
            if (this.port != null)
                cmdLine.createArgument().setLine("-H " + this.port);
            cmdLine.createArgument().setValue("-a");
            cmdLine.createArgument().setLine("-p " +module.getDepot());
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
            for ( Iterator transactionIter = root.elementIterator( "transaction" ); transactionIter.hasNext(); ) {
                buffer.setLength(0);
                Element transaction = (Element) transactionIter.next();
                final String userName = transaction.attributeValue("user");
                revisions.getChangeLogins().add(userName);
                revisions.setFileModified(true);				
                final String timeAttr = transaction.attributeValue("time");
                long timeVal = Long.parseLong(timeAttr);
                Date transDate = new Date(timeVal);
                String dateString = ACCUREV_DATE_FORMAT.format(transDate);
                final String transactionStr = transaction.attributeValue("id");
                final long transactionNumber = Long.parseLong(transactionStr);
                final String comment = transaction.elementText("comment");
                buffer.append("Transaction ").append( transactionNumber).append(" date ")
                        .append(dateString).append(" by ").append( userName).append("\n\"")
                        .append(comment).append("\"");
                revisions.getChangeLogs().add(buffer.toString());
                for (Iterator versionIter = transaction.elementIterator("version"); versionIter.hasNext();) {
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
        AccurevHelper.syncTime(antProject, this);
        Iterator it = getModules().iterator();
		while (it.hasNext()) {
			AccurevModule module = (AccurevModule) it.next();
            labelModule(module, antProject);
		}
    }

    private void labelModule(AccurevModule module, Project antProject) {
        antProject.log("Labeling module " + module);
        final String label = "" + AccurevHelper.getLastTransactionNumber(module,antProject, this);
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
        AccurevHelper.syncTime(antProject, this);
        String workingDir = build.getSchedule().getWorkingDir();
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
        StreamInfo buildStreamInfo =
            AccurevHelper.findStreamInfo(depot, buildStreamName, antProject, this);
        ReferenceTreeInfo referenceTreeInfo =
            AccurevHelper.getReferenceTreeInfo(depot,module.getReferenceTree(),antProject, this);

		if (isRebuild) {
            AccurevHelper.updateStream(buildStreamName,module.getBackingStream(),
                    Long.valueOf(module.getLabel()),antProject, this);
            AccurevHelper.forceWorkingDirRefresh(workingDir, module, antProject, this);
        } else if (isClean) {
            if (buildStreamInfo == null) {
                if (referenceTreeInfo != null) {
                    throw new BuildException("There is an existing reference tree named " + module.getReferenceTree() + ". Reference trees cannot be re-homed.");
                }
                AccurevHelper.buildStream(buildStreamName,module.getBackingStream(),
                        null, antProject, this);
                AccurevHelper.buildReferenceTree(workingDir,module,antProject, this);
            } else {
                if (referenceTreeInfo == null) {
                    AccurevHelper.buildReferenceTree(workingDir,module,antProject, this);
                    AccurevHelper.updateReferenceTree(module,antProject, this);
                } else {
                    AccurevHelper.updateReferenceTree(module,antProject, this); // set us to the right version
                    AccurevHelper.forceWorkingDirRefresh(workingDir, module, antProject, this);
                }
            }
        } else {
            AccurevHelper.updateReferenceTree(module, antProject, this);
        }

    }

    public class AccurevModule extends Module {
        /**
		 * Keep tracks of version of this class, used when do serialization-deserialization
		 */
		static final long serialVersionUID = 1;

        private String depot;
        private String srcPath;
        private String backingStream;
        private String buildStream;
        private String label;

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
            List properties = new ArrayList();
            for (int i = 0; i < properties.size(); i++) {
                DisplayProperty property = (DisplayProperty) properties.get(i);
                if (("label".equals(property.getDisplayName())) || ("branch".equals(property.getDisplayName()))) {
                    properties.remove(i);
                    break;
                }
            }
            properties.add(new DisplayProperty(){
				public String getDisplayName() {
					return "Label";
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
				public void setValue(String value) {
					setLabel(value);
				}
			});
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "Depot";
                }

                public String getDescription() {
                    return "The AccuRev depot to check the code out of.";
                }

                public String getValue() {
                    return getDepot();
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
                    return "Backing stream";
                }

                public String getDescription() {
                    return "The backing stream for this build module. The backing stream should be " +
                            "able to have streams created from it by the build user.";
                }

                public String getValue() {
                    return getBackingStream();
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
                    return "Build stream";
                }

                public String getDescription() {
                    return "The name of the stream to create from the backing stream. If it doesn't exist " +
                            "it will be created. A reference tree will be created from this stream with a " +
                            "'_reference' suffix appended to the build stream name.";
                }

                public String getValue() {
                    return getBuildStream();
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

        public com.luntsys.luntbuild.facades.lb12.ModuleFacade getFacade() {
            AccurevModuleFacade facade = new com.luntsys.luntbuild.facades.lb12.AccurevModuleFacade();
			facade.setBackingStream(getBackingStream());
			facade.setBuildStream(getBuildStream());
			facade.setDepot(getDepot());
			facade.setLabel(getLabel());
			facade.setSrcPath(getSrcPath());
			return facade;
        }

        public void setFacade(ModuleFacade facade) {
            if (facade instanceof AccurevModuleFacade) {
				AccurevModuleFacade moduleFacade = (AccurevModuleFacade) facade;
				setBackingStream(moduleFacade.getBackingStream());
				setBuildStream(moduleFacade.getBuildStream());
				setDepot(moduleFacade.getDepot());
				setLabel(moduleFacade.getLabel());
				setSrcPath(moduleFacade.getSrcPath());
			} else
				throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        }

        public String toString() {
            return new ToStringBuilder(this)
            .append("depot", getDepot())
            .append("backing stream", getBackingStream())
            .append("build stream", getBuildStream())
			.append("label", getLabel())
			.append("srcPath", getSrcPath())
            .toString();
        }
    }
}
