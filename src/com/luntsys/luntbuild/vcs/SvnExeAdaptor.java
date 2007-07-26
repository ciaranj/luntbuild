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

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.ModuleFacade;
import com.luntsys.luntbuild.facades.lb12.SvnExeAdaptorFacade;
import com.luntsys.luntbuild.facades.lb12.SvnExeModuleFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.utility.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.tmatesoft.svn.util.SVNDebugLog;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Subversion Executable VCS adaptor implementation.
 *
 * @author robin shine
 */
public class SvnExeAdaptor extends Vcs {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1;

    private static final String SVN_COMMAND_INPUT = null;

    private String urlBase;
    private String trunk;
    private String branches;
    private String tags;
    private String user;
    private String password;
    private String svnDir;
	/** Subversion web interface to itegrate with */
	private String webInterface;
	/** Subversion web interface URL */
	private String webUrl;
    private boolean cygwinSvn;

    /**
     * @inheritDoc
     */
    public String getDisplayName() {
        return "SubversionExe";
    }

    /**
     * @inheritDoc
     */
    public String getIconName() {
        return "svn.jpg";
    }

	/**
	 * Gets the path to the SVN executable.
	 * 
	 * @return the path to the SVN executable
	 */
    public String getSvnDir() {
        return this.svnDir;
    }

	/**
	 * Sets the path to the SVN executable.
	 * 
	 * @param svnDir the path to the SVN executable
	 */
    public void setSvnDir(String svnDir) {
        this.svnDir = svnDir;
    }

	/**
	 * Constructs the executable part of a commandline object.
	 *
	 * @return the commandline object
	 */
    protected Commandline buildSvnExecutable() {
        Commandline cmdLine = new Commandline();
        if (Luntbuild.isEmpty(getSvnDir()))
            cmdLine.setExecutable("svn");
        else
            cmdLine.setExecutable(Luntbuild.concatPath(getSvnDir(), "svn"));
        return cmdLine;
    }

    /**
     * @inheritDoc
     */
    public List getVcsSpecificProperties() {
        List properties = new ArrayList();
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "Repository url base";
            }

            public String getDescription() {
                return "The base part of Subversion url, for example, you can input " +
                        "\"svn://buildmachine.foobar.com/\", or \"file:///c:/svn_repository\", or " +
                        "\"svn://buildmachine.foobar.com/myproject/othersubdirectory\", etc. " +
                        "Other definitions such as tags directory, branches directory, or modules " +
                        "are relative to this base url. NOTE. If you are using https:// schema, you " +
                        "should make sure that svn server certificate has been accepted permermantly " +
                        "by your build machine.";
            }

            public String getValue() {
                return getUrlBase();
            }

            public String getActualValue() {
                return getActualUrlBase();
            }

            public void setValue(String value) {
                setUrlBase(value);
            }
        });
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "Directory for trunk";
            }

            public String getDescription() {
                return "Directory used to hold trunk for this url base. " +
                        "This directory is relative to the url base. Leave it blank, if you didn't " +
                        "define any trunk directory in the above url base.";
            }

            public boolean isRequired() {
                return false;
            }

            public String getValue() {
                return getTrunk();
            }

            public String getActualValue() {
                return getActualTrunk();
            }

            public void setValue(String value) {
                setTrunk(value);
            }
        });
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "Directory for branches";
            }

            public String getDescription() {
                return "Directory used to hold branches for this url base. " +
                        "This directory is relative to the url base. If left blank, " +
                        "\"branches\" will be used as the default value.";
            }

            public boolean isRequired() {
                return false;
            }

            public String getValue() {
                return getBranches();
            }

            public String getActualValue() {
                return getActualBranches();
            }

            public void setValue(String value) {
                setBranches(value);
            }
        });
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "Directory for tags";
            }

            public String getDescription() {
                return "Directory used to hold tags for this url base. " +
                        "This directory is relative to the url base. If left blank, " +
                        "\"tags\" will be used as the default value.";
            }

            public boolean isRequired() {
                return false;
            }

            public String getValue() {
                return getTags();
            }

            public String getActualValue() {
                return getActualTags();
            }

            public void setValue(String value) {
                setTags(value);
            }
        });
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "Username";
            }

            public String getDescription() {
                return "User name to use to login to Subversion.";
            }

            public boolean isRequired() {
                return false;
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
                return "Password to use to login to Subversion.";
            }

            public boolean isRequired() {
                return false;
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
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "Path for svn executable";
            }

            public String getDescription() {
                return "The directory path, where your svn executable file resides in. " +
                        "It should be specified here, if it does not exist in the system path.";
            }

            public boolean isRequired() {
                return false;
            }

            public String getValue() {
                return getSvnDir();
            }

            public void setValue(String value) {
                setSvnDir(value);
            }
        });
        DisplayProperty p = new DisplayProperty() {
            public String getDisplayName() {
                return "Web interface";
            }

            public String getDescription() {
                return "Set the web interface to integrate with.";
            }

            public boolean isRequired() {
                return false;
            }

            public boolean isSelect() {
                return true;
            }

            public String getValue() {
                return getWebInterface();
            }

            public void setValue(String value) {
                setWebInterface(value);
            }
        };
        // Create selection model
        IPropertySelectionModel model = new SvnWebInterfaceSelectionModel();
        // Set selection model
        p.setSelectionModel(model);
        // Add property to properties list
        properties.add(p);
        properties.add(new DisplayProperty() {
            public String getDisplayName() {
                return "URL to web interface";
            }

            public String getDescription() {
                return "The URL to access the repository in your chosen web interface. " +
                "NOTE: If using \"JavaForge\" enter the ID of your poject only.";
            }

            public boolean isRequired() {
                return false;
            }

            public String getValue() {
                return getWebUrl();
            }

            public void setValue(String value) {
                setWebUrl(value);
            }
        });
        return properties;
    }

	/**
     * @inheritDoc
	 */
    public void checkoutActually(Build build, Project antProject) {
        String workingDir = build.getSchedule().getWorkDirRaw();
        // retrieve modules
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            SvnExeModule module = (SvnExeModule) Luntbuild.cloneModule(this, (Vcs.Module) it.next());
            if (build.isRebuild() && Luntbuild.isEmpty(module.getLabel()))
                module.setLabel(Luntbuild.getLabelByVersion(build.getVersion()));
            if (build.isRebuild() || build.isCleanBuild())
                retrieveModule(workingDir, module, antProject);
            else
                updateModule(workingDir, module, antProject);
        }
    }

	/**
     * @inheritDoc
	 */
    public void label(Build build, Project antProject) {
        String workingDir = build.getSchedule().getWorkDirRaw();
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            SvnExeModule module = (SvnExeModule) it.next();
            if (Luntbuild.isEmpty(module.getLabel()))
                labelModule(workingDir, module, Luntbuild.getLabelByVersion(build.getVersion()), antProject);
        }
    }

	/**
     * @inheritDoc
	 * @see SvnExeModule
	 */
    public Module createNewModule() {
        return new SvnExeModule();
    }

	/**
     * @inheritDoc
	 * @see SvnExeModule
	 */
    public Module createNewModule(Module module) {
        return new SvnExeModule((SvnExeModule)module);
    }

	/**
	 * Checks out the contents from a module.
	 * 
	 * @param workingDir the working directory
	 * @param module the module
	 * @param antProject the ant project used for logging
	 */
    private void retrieveModule(String workingDir, SvnExeModule module, Project antProject) {
        String destDir;
        if (Luntbuild.isEmpty(module.getDestPath()))
            destDir = Luntbuild.concatPath(workingDir, module.getActualSrcPath());
        else
            destDir = Luntbuild.concatPath(workingDir, module.getActualDestPath());
        String url = Luntbuild.concatPath(getActualUrlBase(), mapPathByBranchLabel(module.getActualSrcPath(),
                module.getActualBranch(), module.getActualLabel()));

        antProject.log("Retrieve url: " + url);

        Commandline cmdLine = buildSvnExecutable();
        try {
            // first try switch command
            cmdLine.clearArgs();
            cmdLine.createArgument().setValue("switch");
            cmdLine.createArgument().setValue(url);
            cmdLine.createArgument().setValue(destDir);
            addSvnSwitches(cmdLine, antProject, false);
            new MyExecTask("switch", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
                    Project.MSG_INFO).execute();
        } catch (BuildException e) {
            // then try checkout command
            cmdLine.clearArgs();
            cmdLine.createArgument().setValue("checkout");
            cmdLine.createArgument().setValue(url);
            cmdLine.createArgument().setValue(destDir);
            addSvnSwitches(cmdLine, antProject, false);
            new MyExecTask("checkout", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
                    Project.MSG_INFO).execute();
        }
    }

	/**
	 * Labels the contents of a module.
	 * 
	 * @param workingDir the working directory
	 * @param module the module
	 * @param label the label
	 * @param antProject the ant project used for logging
	 */
    private void labelModule(String workingDir, SvnExeModule module, String label, Project antProject) {
        String normalizedModule = Luntbuild.concatPath("/", module.getActualSrcPath());
        String normalizedTagsDir = Luntbuild.concatPath("/", getTagsDir());

        // no need to label this module cause this module is fetched from tags directory
        if (normalizedModule.startsWith(normalizedTagsDir))
            return;

        antProject.log("Label url: " + Luntbuild.concatPath(getActualUrlBase(), mapPathByBranch(module.getActualSrcPath(), module.getActualBranch())),
                Project.MSG_INFO);

        String mapped = mapPathByLabel(module.getActualSrcPath(), label);
        Commandline cmdLine = buildSvnExecutable();

        // Is this mapped path already exists?
        cmdLine.createArgument().setValue("list");
        String url = Luntbuild.concatPath(getActualUrlBase(), mapped);
        cmdLine.createArgument().setValue(url);
        addSvnSwitches(cmdLine, antProject, true);
        try {
            new MyExecTask("list", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT, -1).execute();
            throw new BuildException("Failed to create label, url \"" + url + "\" already exists.");
        } catch (BuildException e) {
            // ignore the exception
        }

        // make sure parent of mapped path exists
        String mappedParent = StringUtils.substringBeforeLast(StringUtils.stripEnd(mapped, "/"), "/");
        String[] fields = mappedParent.split("/");
        url = getActualUrlBase();
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            if (!field.trim().equals("")) {
                url = Luntbuild.concatPath(url, field);
                // Is this path already exists?
                cmdLine.clearArgs();
                cmdLine.createArgument().setValue("list");
                cmdLine.createArgument().setValue(url);
                addSvnSwitches(cmdLine, antProject, true);
                try {
                    new MyExecTask("list", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
                            -1).execute();
                } catch (BuildException e) {
                    antProject.log("Seems that url \"" + url + "\" does not exist, creating...", Project.MSG_INFO);
                    cmdLine.clearArgs();
                    cmdLine.createArgument().setValue("mkdir");
                    cmdLine.createArgument().setValue(url);
                    cmdLine.createArgument().setLine("-m \"\"");
                    addSvnSwitches(cmdLine, antProject, false);
                    try {
                        new MyExecTask("mkdir", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
                                Project.MSG_INFO).execute();
                    } catch (BuildException e2) {
                        // ignore the exception
                    }
                }
            }
        }

        String destDir;
        if (Luntbuild.isEmpty(module.getDestPath()))
            destDir = Luntbuild.concatPath(workingDir, module.getActualSrcPath());
        else
            destDir = Luntbuild.concatPath(workingDir, module.getActualDestPath());

        cmdLine.clearArgs();
        cmdLine.createArgument().setValue("copy");
        cmdLine.createArgument().setValue(destDir);
        cmdLine.createArgument().setValue(Luntbuild.concatPath(getActualUrlBase(), mapped));
        cmdLine.createArgument().setLine("-m \"\"");
        addSvnSwitches(cmdLine, antProject, false);
        new MyExecTask("copy", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
                Project.MSG_INFO).execute();
    }

	/**
	 * Updates the contents of a module.
	 * 
	 * @param workingDir the working directory
	 * @param module the module
	 * @param antProject the ant project used for logging
	 */
    private void updateModule(String workingDir, SvnExeModule module, Project antProject) {
        String url = Luntbuild.concatPath(getActualUrlBase(), mapPathByBranchLabel(module.getActualSrcPath(),
                module.getActualBranch(), module.getActualLabel()));

        antProject.log("Update url: " + url);

        String destDir;
        if (Luntbuild.isEmpty(module.getDestPath()))
            destDir = Luntbuild.concatPath(workingDir, module.getActualSrcPath());
        else
            destDir = Luntbuild.concatPath(workingDir, module.getActualDestPath());

        Commandline cmdLine = buildSvnExecutable();
        cmdLine.createArgument().setValue("update");
        cmdLine.createArgument().setValue(destDir);
        addSvnSwitches(cmdLine, antProject, false);
        new MyExecTask("update", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT,
                Project.MSG_INFO).execute();
    }

	/**
	 * Gets the repository URL base.
	 * 
	 * @return the URL base
	 */
    public String getUrlBase() {
        return this.urlBase;
    }

	/**
	 * Gets the repository URL base. This method will parse OGNL variables.
	 * 
	 * @return the URL base
	 */
    private String getActualUrlBase() {
        return OgnlHelper.evaluateScheduleValue(getUrlBase());
    }

	/**
	 * Sets the repository URL base.
	 * 
	 * @param urlBase the URL base
	 */
    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

	/**
	 * Gets the login user.
	 * 
	 * @return the login user
	 */
    public String getUser() {
        return this.user;
    }

	/**
	 * Sets the login user.
	 * 
	 * @param user the login user
	 */
    public void setUser(String user) {
        this.user = user;
    }

	/**
	 * Gets the login password.
	 * 
	 * @return the login password
	 */
    public String getPassword() {
        return this.password;
    }

	/**
	 * Sets the login password.
	 * 
	 * @param password the login password
	 */
    public void setPassword(String password) {
        this.password = password;
    }

	/**
	 * Gets the web interface to integrate with.
	 * 
	 * @return the web interface to integrate with
	 */
	public String getWebInterface() {
		return webInterface;
	}

	/**
	 * Sets the web interface to integrate with.
	 * 
	 * @param webInterface the web interface to integrate with
	 */
	public void setWebInterface(String webInterface) {
		this.webInterface = webInterface;
	}

	/**
	 * Gets the web interface URL.
	 * 
	 * @return the web interface URL
	 */
	public String getWebUrl() {
		return webUrl;
	}

	/**
	 * Sets the web interface URL.
	 * 
	 * @param webUrl the web interface URL
	 */
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	/**
	 * Gets the directory for the trunk.
	 * 
	 * @return the trunk directory
	 */
    public String getTrunk() {
        return this.trunk;
    }

	/**
	 * Gets the directory for the trunk. This method will parse OGNL variables.
	 * 
	 * @return the trunk directory
	 */
    private String getActualTrunk() {
        return OgnlHelper.evaluateScheduleValue(getTrunk());
    }

	/**
	 * Sets the directory for the trunk.
	 * 
	 * @param trunk the trunk directory
	 */
    public void setTrunk(String trunk) {
        this.trunk = trunk;
    }

	/**
	 * Adds common switches for various SVN commands.
	 * 
     * @param cmdLine the commandline object to add switches to
     * @param antProject the ant project used for logging
     * @param ignoreLogLevel set <code>true</code> to ignore the log level setting in the ant project
	 */
    private void addSvnSwitches(Commandline cmdLine, Project antProject, boolean ignoreLogLevel) {
        cmdLine.createArgument().setValue("--non-interactive");
        if (!ignoreLogLevel) {
            LuntbuildLogger luntBuildLogger = Luntbuild.getLuntBuildLogger(antProject);
            if (luntBuildLogger != null && luntBuildLogger.getMessageOutputLevel() <= Project.MSG_INFO)
                cmdLine.createArgument().setValue("--quiet");
        }
        if (getUser() != null && !getUser().trim().equals("")) {
            cmdLine.createArgument().setValue("--username");
            cmdLine.createArgument().setValue(getUser());
            if (getPassword() != null && !getPassword().equals("")) {
                cmdLine.createArgument().setValue("--password");
                Commandline.Argument arg = cmdLine.createArgument();
                arg.setValue(getPassword());
                arg.setDescriptiveValue("******");
            }
        }
    }

    /**
     * Validates the modules of this VCS.
     *
     * @throws ValidationException if a module is not invalid
     */
    public void validateModules() {
        super.validateModules();
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            SvnExeModule module = (SvnExeModule) it.next();
            if (module.getActualSrcPath().indexOf('\\') != -1)
                throw new ValidationException("Source path \"" + module.getActualSrcPath() + "\" should not contain character '\\'");
        }
    }

    /**
     * @inheritDoc
     * @throws RuntimeException if an exception occurs while reading the SVN log
     */
    public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
        final SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        inFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        outFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        String workingDir = workingSchedule.getWorkDirRaw();
        SvnRevisions revisions = new SvnRevisions();
        revisions.addLog(this.getClass().getName(), toString());
        revisions.getChangeLogs().add("*************************************************************");
        revisions.getChangeLogs().add(toString());
        revisions.getChangeLogs().add("");
        Commandline cmdLine = buildSvnExecutable();
        Iterator it = getModules().iterator();
        while (it.hasNext()) {
            SvnExeModule module = (SvnExeModule) it.next();
            if (Luntbuild.isEmpty(module.getLabel())) {
                cmdLine.clearArgs();
                cmdLine.createArgument().setValue("log");
                String url = Luntbuild.concatPath(getActualUrlBase(), mapPathByBranchLabel(module.getActualSrcPath(),
                        module.getActualBranch(), module.getActualLabel()));
                cmdLine.createArgument().setValue(url);
                if (getUser() != null && !getUser().trim().equals("")) {
                    cmdLine.createArgument().setValue("--username");
                    cmdLine.createArgument().setValue(getUser());
                    if (getPassword() != null && !getPassword().equals("")) {
                        cmdLine.createArgument().setValue("--password");
                        Commandline.Argument arg = cmdLine.createArgument();
                        arg.setValue(getPassword());
                        arg.setDescriptiveValue("******");
                    }
                }
                cmdLine.createArgument().setLine("--non-interactive -v --xml -r");
                cmdLine.createArgument().setValue("{" + inFormat.format(sinceDate) +
						"}:{" + inFormat.format(new Date()) + "}");
                final StringBuffer buffer = new StringBuffer();
                new MyExecTask("log", antProject, workingDir, cmdLine, null, SVN_COMMAND_INPUT, -1) {
                    public void handleStdout(String line) {
                        buffer.append(line);
                        buffer.append("\n");
                    }
                }.execute();
                SAXReader reader = new SAXReader();
                try {
                    Document doc = reader.read(new StringReader(buffer.toString()));
                    Iterator itElement = doc.getRootElement().elementIterator("logentry");
                    while (itElement.hasNext()) {
                        Element logEntry = (Element) itElement.next();
                        String dateString = logEntry.element("date").getText();
                        Date revisionDate = outFormat.parse(dateString.substring(0, dateString.indexOf('Z') - 3));
                        if (revisionDate.before(sinceDate))
                            continue;
                        Element authorElement = logEntry.element("author");
                        if (authorElement != null)
                            revisions.getChangeLogins().add(authorElement.getText());
                        //
                        // 2005.06.21 - ghenry@lswe.com --> check for commit msg
                        //
                        Element msgElement = logEntry.element("msg");
                        String message = "";
                        if (msgElement != null && msgElement.getText().trim().length() != 0)
                            message = msgElement.getText();
                        revisions.getChangeLogs().add("----------------------------------------------------------------------------------------------------------------------");
                        if (authorElement != null) {
                            revisions.addEntryToLastLog(logEntry.attribute("revision").getText(), authorElement.getText(),
                                    revisionDate, message);
                            revisions.getChangeLogs().add("r" + logEntry.attribute("revision").getText() + " | " +
                                authorElement.getText() + " | " + revisionDate.toString());
                        } else {
                            revisions.addEntryToLastLog(logEntry.attribute("revision").getText(), "anonymous",
                                    revisionDate, message);
                            revisions.getChangeLogs().add("r" + logEntry.attribute("revision").getText() + " | " +
                                "anonymous" + " | " + revisionDate.toString());
                        }

                        //
                        // 2005.06.21 - ghenry@lswe.com --> check for commit msg
                        //
                        if (message.length() != 0) {
                            revisions.getChangeLogs().add("----------------------------------------------------------------------------------------------------------------------");
                            revisions.getChangeLogs().add(message);
                            revisions.getChangeLogs().add("----------------------------------------------------------------------------------------------------------------------");
                        }
                        //

                        revisions.getChangeLogs().add("Changed paths:");
                        Iterator itPath = logEntry.element("paths").elementIterator("path");
                        while (itPath.hasNext()) {
                            Element path = (Element) itPath.next();
                            revisions.addPathToLastEntry(path.getText(), path.attribute("action").getText(), logEntry.attribute("revision").getText());
                            revisions.getChangeLogs().add("    " + path.attribute("action").getText() + " " + path.getText());
                            if (path.attribute("copyfrom-path") != null)
                                revisions.addCopyToLastPath(path.attribute("copyfrom-path").getText(), path.attribute("copyfrom-rev").getText());
                            revisions.setFileModified(true);
                        }
                        revisions.getChangeLogs().add("");
                    }
                } catch (DocumentException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return revisions;
    }

	/**
	 * Maps a Subversion path to a sub directory of tags or branches based on a branch or label name.
	 * Label will take precedence over branch.
	 *
	 * @param path the Subversion path
	 * @param branch the branch name
	 * @param label the label name
	 * @return the sub directory
	 */
    private String mapPathByBranchLabel(String path, String branch, String label) {
        if (!Luntbuild.isEmpty(label))
            return mapPathByLabel(path, label);
        else
            return mapPathByBranch(path, branch);
    }

	/**
	 * Maps a Subversion path to a sub directory of tags based on a label name.
	 *
	 * @param path the Subversion path
	 * @param label the label name, should not be empty
	 * @return the sub directory
	 */
    private String mapPathByLabel(String path, String label) {
        String mapped = Luntbuild.concatPath(getTagsDir(), label);
        return Luntbuild.concatPath(mapped, path);
    }

	/**
	 * Maps a subversion path to a sub directory of branches based on a branch name,
	 * or to a sub directory under trunk if branch name is empty.
	 * 
	 * @param path the Subversion path
	 * @param branch the branch name, may be empty
	 * @return the sub directory
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
	 * Gets the directory for the branches.
	 * 
	 * @return the branches directory
	 */
    public String getBranches() {
        return this.branches;
    }

	/**
	 * Gets the directory for the branches. This method will parse OGNL variables.
	 * 
	 * @return the branches directory
	 */
    private String getActualBranches() {
	    return OgnlHelper.evaluateScheduleValue(getBranches());
    }

	/**
	 * Sets the directory for the branches.
	 * 
	 * @param branches the branches directory
	 */
    public void setBranches(String branches) {
        this.branches = branches;
    }

	/**
	 * Gets the directory for the tags.
	 * 
	 * @return the tags directory
	 */
    public String getTags() {
        return this.tags;
    }

	/**
	 * Gets the directory for the tags. This method will parse OGNL variables.
	 * 
	 * @return the tags directory
	 */
    private String getActualTags() {
        return OgnlHelper.evaluateScheduleValue(getTags());
    }

	/**
	 * Sets the directory for the tags.
	 * 
	 * @param tags the tags directory
	 */
    public void setTags(String tags) {
        this.tags = tags;
    }

	/**
	 * Gets the trunk directory.
	 * 
	 * @return the trunk directory
	 */
    public String getTrunkDir() {
        if (Luntbuild.isEmpty(getTrunk()))
            return "";
        else
            return getActualTrunk();
    }

	/**
	 * Gets the branches directory.
	 * 
	 * @return the branches directory
	 */
    public String getBranchesDir() {
        if (Luntbuild.isEmpty(getBranches()))
            return "branches";
        else
            return getActualBranches();
    }

	/**
	 * Gets the tags directory.
	 * 
	 * @return the tags directory
	 */
    public String getTagsDir() {
        if (Luntbuild.isEmpty(getTags()))
            return "tags";
        else
            return getActualTags();
    }

    /**
     * Creates a link to browse the repository.
     * 
     * @return the link
     */
    public String createLinkForRepository() {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl()))
            return "&nbsp;";
        if (getWebInterface().equals("JavaForge"))
            return "<a href=\"http://www.javaforge.com/proj/sources/sccBrowse.do?proj_id=" + getWebUrl() + "\">browse</a>";
        else
            return "<a href=\"" + getWebUrl() + "\">browse</a>";
    }

    /**
     * Creates a link to the specified revision.
     * 
     * @param revision the revision
     * @return the link
     */
    public String createLinkForRevision(String revision) {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl()) ||
                Luntbuild.isEmpty(revision))
            return revision;
        if (getWebInterface().equals("Chora"))
            return revision;
        else if (getWebInterface().equals("Insurrection"))
            return "<a href=\"" + getWebUrl() + "?Insurrection=log&r1=" + revision + "&r2=" + revision + "\">" + revision + "</a>";
        else if (getWebInterface().equals("JavaForge"))
            return revision;
        else if (getWebInterface().equals("perl_svn"))
            return "<a href=\"" + getWebUrl() + "&a=lr&r=" + revision + "\">" + revision + "</a>";
        else if (getWebInterface().equals("SVN::Web"))
            return "<a href=\"" + getWebUrl() + "/revision?rev=" + revision + "\">" + revision + "</a>";
        else if (getWebInterface().equals("Trac"))
            return "<a href=\"" + getWebUrl() + "/changeset/" + revision + "\">" + revision + "</a>";
        else if (getWebInterface().equals("ViewVC"))
            return "<a href=\"" + getWebUrl() + "?view=rev&revision=" + revision + "\">" + revision + "</a>";
        else if (getWebInterface().equals("WebSVN"))
            return "<a href=\"" + getWebUrl() + "/listing.php?rev=" + revision + "&sc=1\">" + revision + "</a>";
        else
            return revision;
    }

    /**
     * Creates a link to the specified file revision.
     * 
     * @param path the path to the file
     * @param revision the revision
     * @return the link
     */
    public String createLinkForFile(String path, String revision) {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl()) ||
                Luntbuild.isEmpty(path) || Luntbuild.isEmpty(revision))
            return path;
        String clean_path = Luntbuild.removeLeadingSlash(path);
        if (getWebInterface().equals("Chora"))
            return path;
        else if (getWebInterface().equals("Insurrection"))
            return "<a href=\"" + getWebUrl() + "/" + clean_path + "?r=" + revision + "\">" + path + "</a>";
        else if (getWebInterface().equals("JavaForge"))
            return "<a href=\"http://www.javaforge.com/sccShowFileRevision/?proj_id=" + getWebUrl() + "&date=&tag=" + revision + "&filename=" + clean_path + "\">" + path + "</a>";
        else if (getWebInterface().equals("perl_svn"))
            return "<a href=\"" + getWebUrl() + "/" + clean_path + "&a=d&r=" + revision + "\">" + path + "</a>";
        else if (getWebInterface().equals("SVN::Web"))
            return "<a href=\"" + getWebUrl() + "/view/" + clean_path + "?rev=" + revision + "\">" + path + "</a>";
        else if (getWebInterface().equals("Trac"))
            return "<a href=\"" + getWebUrl() + "/browser/" + clean_path + "?rev=" + revision + "\">" + path + "</a>";
        else if (getWebInterface().equals("ViewVC"))
            return "<a href=\"" + getWebUrl() + "/" + clean_path + "?pathrev=" + revision + "&view=markup\">" + path + "</a>";
        else if (getWebInterface().equals("WebSVN"))
            return "<a href=\"" + getWebUrl() + "/filedetails.php?path=" + clean_path + "&rev=" + revision + "\">" + path + "</a>";
        else
            return path;
    }

    /**
     * Creates a link to diff the specified file and revision with the previous revision.
     * 
     * @param path the path to the file
     * @param revision the revision
     * @return the link
     */
    public String createLinkForDiff(String path, String revision) {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl()) ||
                Luntbuild.isEmpty(path) || Luntbuild.isEmpty(revision))
            return "";
        String clean_path = Luntbuild.removeLeadingSlash(path);
        if (getWebInterface().equals("Chora"))
            return "";
        else if (getWebInterface().equals("Insurrection"))
            return "";
        else if (getWebInterface().equals("JavaForge"))
            return "";
        else if (getWebInterface().equals("perl_svn"))
            return "(<a href=\"" + getWebUrl() + "/" + clean_path + "&a=c&r=" + revision + "\">diff</a>)";
        else if (getWebInterface().equals("SVN::Web"))
            return "(<a href=\"" + getWebUrl() + "/revision?rev=" + revision + "#diff__" + clean_path.replaceAll("/","_") + "\">diff</a>)";
        else if (getWebInterface().equals("Trac"))
            return "";
        else if (getWebInterface().equals("ViewVC"))
            return "";
        else if (getWebInterface().equals("WebSVN"))
            return "(<a href=\"" + getWebUrl() + "/diff.php?path=" + clean_path + "&rev=" + revision + "\">diff</a>)";
        else
            return "";
    }

    /**
     * Creates a link to diff the specified file between the specified revisions.
     * 
     * @param path the path to the file
     * @param revision the revision (right hand side)
     * @param prev_rev the previous revision (left hand side)
     * @return the link
     */
    public String createLinkForDiff(String path, String revision, String prev_rev) {
        if (Luntbuild.isEmpty(getWebInterface()) || Luntbuild.isEmpty(getWebUrl()) ||
                Luntbuild.isEmpty(path) || Luntbuild.isEmpty(revision) || Luntbuild.isEmpty(prev_rev))
            return "";
        String clean_path = Luntbuild.removeLeadingSlash(path);
        if (getWebInterface().equals("Chora"))
            return "";
        else if (getWebInterface().equals("Insurrection"))
            return "(<a href=\"" + getWebUrl() + "/" + clean_path + "?Insurrection=diff&r1=" + prev_rev + "&r2=" + revision + "\">diff</a>)";
        else if (getWebInterface().equals("JavaForge"))
            return "(<a href=\"http://www.javaforge.com/sccFileDiff?proj_id=" + getWebUrl() + "date=&revision1=" + revision + "&revision2=" + prev_rev + "&filename=" + clean_path + "\">diff</a>)";
        else if (getWebInterface().equals("perl_svn"))
            return "(<a href=\"" + getWebUrl() + "/" + clean_path + "&a=c&r=" + revision + "\">diff</a>)";
        else if (getWebInterface().equals("SVN::Web"))
            return "(<a href=\"" + getWebUrl() + "/revision?rev=" + revision + "#diff__" + clean_path.replaceAll("/","_") + "\">diff</a>)";
        else if (getWebInterface().equals("Trac"))
            return "(<a href=\"" + getWebUrl() + "/changeset/?new=" + clean_path + "@" + revision + "&old=" + clean_path + "@" + prev_rev + "\">diff</a>)";
        else if (getWebInterface().equals("ViewVC"))
            return "(<a href=\"" + getWebUrl() + "/" + clean_path + "?r1=" + prev_rev + "&r2=" + revision + "&view=markup\">diff</a>)";
        else if (getWebInterface().equals("WebSVN"))
            return "(<a href=\"" + getWebUrl() + "/comp.php?compare[]=" + clean_path + "@" + prev_rev + "&compare[]=" + clean_path + "@" + revision + "\">diff</a>)";
        else
            return "";
    }

    private void initLogger(Project antProject) {
        SvnCustomLogger svnLogger = new SvnCustomLogger(antProject);
        SVNDebugLog.setDefaultLog(svnLogger);
    }

    /**
     * Selection model used for user interface of <code>SvnAdaptor</code>.
     */
    class SvnWebInterfaceSelectionModel implements IPropertySelectionModel {
        String[] values = {"", "Chora", "Insurrection", "JavaForge", "perl_svn", "SVN::Web", "Trac", "ViewVC", "WebSVN"};
        String[] display_values = {"", "Chora", "Insurrection", "JavaForge", "perl_svn", "SVN::Web", "Trac", "ViewVC", "WebSVN"};

        /**
         * Gets the number of options.
         * 
         * @return the number of options
         */
        public int getOptionCount() {
            return this.values.length;
        }

        /**
         * Gets an option.
         * 
         * @param index the index of the opiton
         * @return the option
         */
        public Object getOption(int index) {
            return this.values[index];
        }

        /**
         * Gets the display label of an option.
         * 
         * @param index the index of the opiton
         * @return the label
         */
        public String getLabel(int index) {
            return this.display_values[index];
        }

        /**
         * Gets the value of an option.
         * 
         * @param index the index of the opiton
         * @return the value
         */
        public String getValue(int index) {
            return this.values[index];
        }

        /**
         * Gets the option that corresponds to a value.
         * 
         * @param value the value
         * @return the option
         */
        public Object translateValue(String value) {
            return value;
        }
    }

    /**
     * Revision (or change log) manager for Subversion Executable.
     */
    public class SvnRevisions extends Revisions {
        /**
         * Adds a copyfrom path to the last path of the last entry added.
         * 
         * @param path the path
         * @param revision the revision or version of the path
         */
        public void addCopyToLastPath(String path, String revision) {
            if (lastEntry == null)
               throw new BuildException("No entry exists to add path to.");
            org.w3c.dom.NodeList paths = ((org.w3c.dom.Element) lastEntry.getElementsByTagName("paths").item(0)).getChildNodes();
            org.w3c.dom.Element pathElement = (org.w3c.dom.Element) paths.item(paths.getLength() - 1);
            pathElement.setAttribute("z_copyfrom-path", path);
            pathElement.setAttribute("z_copyfrom-rev", revision);
        }
    }

	/**
	 * A Subversion Executable module definition.
	 *
	 * @author robin shine
	 */
    public class SvnExeModule extends Module {
        /**
         * Keep tracks of version of this class, used when do serialization-deserialization
         */
        static final long serialVersionUID = 1L;

        private String srcPath;
        private String branch;
        private String label;
        private String destPath;

        /**
         * Constructor, creates a blank Subversion Executable module.
         */
        public SvnExeModule() {}

        /**
         * Copy constructor, creates a Subversion Executable module from another Subversion Executable module.
         * 
         * @param module the module to create from
         */
        public SvnExeModule(SvnExeModule module) {
            this.srcPath = module.srcPath;
            this.branch = module.branch;
            this.label = module.label;
            this.destPath = module.destPath;
        }

		/**
		 * Gets the source path.
		 * 
		 * @return the source path
		 */
        public String getSrcPath() {
            return this.srcPath;
        }

		/**
		 * Gets the source path. This method will parse OGNL variables.
		 * 
		 * @return the source path
		 */
        private String getActualSrcPath() {
			return OgnlHelper.evaluateScheduleValue(getSrcPath());
        }

		/**
		 * Sets the source path.
		 * 
		 * @param srcPath the source path
		 */
        public void setSrcPath(String srcPath) {
            this.srcPath = srcPath;
        }

		/**
		 * Gets the branch.
		 * 
		 * @return the branch
		 */
        public String getBranch() {
            return this.branch;
        }

		/**
		 * Gets the branch. This method will parse OGNL variables.
		 * 
		 * @return the branch
		 */
        private String getActualBranch() {
			return OgnlHelper.evaluateScheduleValue(getBranch());
		}

		/**
		 * Sets the branch.
		 * 
		 * @param branch the branch
		 */
        public void setBranch(String branch) {
            this.branch = branch;
        }

		/**
		 * Gets the label.
		 * 
		 * @return the label
		 */
        public String getLabel() {
            return this.label;
        }

		/**
		 * Gets the label. This method will parse OGNL variables.
		 * 
		 * @return the label
		 */
        private String getActualLabel() {
			return OgnlHelper.evaluateScheduleValue(getLabel());
		}

		/**
		 * Sets the label.
		 * 
		 * @param label the label
		 */
        public void setLabel(String label) {
            this.label = label;
        }

		/**
		 * Gets the destination path.
		 * 
		 * @return the destination path
		 */
        public String getDestPath() {
            return this.destPath;
        }

		/**
		 * Gets the destination path. This method will parse OGNL variables.
		 * 
		 * @return the destination path
		 */
        private String getActualDestPath() {
			return OgnlHelper.evaluateScheduleValue(getDestPath());
        }

		/**
		 * Sets the destination path.
		 * 
		 * @param destPath the destination path
		 */
        public void setDestPath(String destPath) {
            this.destPath = destPath;
        }

		/**
		 * @inheritDoc
		 */
        public List getProperties() {
            List properties = new ArrayList();
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "Source path";
                }

                public String getDescription() {
                    return "Represents a path in the Subversion repository, for example \"testsvn\", " +
                            "\"testsvn/web\", or \"/testsvn\". When \"branch\" or \"label\" properties are " +
                            "defined, this path will be mapped to another path in the svn repository. " +
                            "Please refer to the User's Guide for details.";
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
                    return "Branch";
                }

                public String getDescription() {
                    return "Specify the branch for above source path. This property is optional. When " +
                            "left empty, trunk is assumed.\n" +
                            "NOTE. Subversion does not internally has the notion of branch. Value specified " +
                            "here will be used by Luntbuild to do url mapping for the above source path so that " +
                            "actual effect is just like a branch in Cvs. Refer to the User's Guide for details " +
                            "about the url mapping for a branch.";
                }

                public boolean isRequired() {
                    return false;
                }

                public String getValue() {
                    return getBranch();
                }

                public String getActualValue() {
                    return getActualBranch();
                }

                public void setValue(String value) {
                    setBranch(value);
                }
            });
            properties.add(new DisplayProperty() {
                public String getDisplayName() {
                    return "Label";
                }

                public String getDescription() {
                    return "Specify the label for the above source path. This property is optional. If specified, " +
                            "it will take preference over branch. When left empty, head version of the specified branch " +
                            "is assumed. " +
                            "NOTE. Subversion does not internally has the notion of label. Value specified here " +
                            "will be used by Luntbuild to do url mapping for the above source path so that actual " +
                            "effect is just like a tag in Cvs. Refer to the User's Guide for details about the " +
                            "url mapping for a label.";
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
                    return "Destination path";
                }

                public String getDescription() {
                    return "This property is optional. If specified, the contents from Subversion repository " +
                            "will be retrieved to the \"destination path\" relative to the project work directory. " +
                            "Otherwise the contents will be retrieved to \"source path\" (with no regard to " +
                            "\"branch\" or \"label\") relative to the project work directory.";
                }

                public boolean isRequired() {
                    return false;
                }

                public String getValue() {
                    return getDestPath();
                }

                public String getActualValue() {
                    return getActualDestPath();
                }

                public void setValue(String value) {
                    setDestPath(value);
                }
            });
            return properties;
        }

	    /**
	     * @inheritDoc
	     * @see SvnExeModuleFacade
	     */
        public ModuleFacade getFacade() {
            SvnExeModuleFacade facade = new SvnExeModuleFacade();
            facade.setBranch(getBranch());
            facade.setDestPath(getDestPath());
            facade.setLabel(getLabel());
            facade.setSrcPath(getSrcPath());
            return facade;
        }

	    /**
	     * @inheritDoc
	     * @throws RuntimeException if the facade is not an <code>SvnModuleFacade</code>
	     * @see SvnExeModuleFacade
	     */
        public void setFacade(ModuleFacade facade) {
            if (facade instanceof SvnExeModuleFacade) {
                SvnExeModuleFacade svnModuleFacade = (SvnExeModuleFacade) facade;
                setBranch(svnModuleFacade.getBranch());
                setLabel(svnModuleFacade.getLabel());
                setSrcPath(svnModuleFacade.getSrcPath());
                setDestPath(svnModuleFacade.getDestPath());
            } else
                throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        }
    }

    /**
     * @inheritDoc
     * @see SvnExeAdaptorFacade
     */
    public void saveToFacade(VcsFacade facade) {
    	// TODO throw RuntimeException if the facade is not the right class
        SvnExeAdaptorFacade svnFacade = (SvnExeAdaptorFacade) facade;
        svnFacade.setTrunk(getTrunk());
        svnFacade.setBranches(getBranches());
        svnFacade.setPassword(getPassword());
        svnFacade.setTags(getTags());
        svnFacade.setUrlBase(getUrlBase());
        svnFacade.setUser(getUser());
        svnFacade.setSvnDir(getSvnDir());
        svnFacade.setWebInterface(getWebInterface());
        svnFacade.setWebUrl(getWebUrl());
    }

    /**
     * @inheritDoc
     * @throws RuntimeException if the facade is not an <code>SvnAdaptorFacade</code>
     * @see SvnExeAdaptorFacade
     */
    public void loadFromFacade(VcsFacade facade) {
        if (!(facade instanceof SvnExeAdaptorFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        SvnExeAdaptorFacade svnFacade = (SvnExeAdaptorFacade) facade;
        setTrunk(svnFacade.getTrunk());
        setBranches(svnFacade.getBranches());
        setPassword(svnFacade.getPassword());
        setTags(svnFacade.getTags());
        setUrlBase(svnFacade.getUrlBase());
        setUser(svnFacade.getUser());
        setSvnDir(svnFacade.getSvnDir());
		setWebInterface(svnFacade.getWebInterface());
		setWebUrl(svnFacade.getWebUrl());
    }

    /**
     * @inheritDoc
     * @see SvnExeAdaptorFacade
     */
    public VcsFacade constructFacade() {
        return new SvnExeAdaptorFacade();
    }
}
