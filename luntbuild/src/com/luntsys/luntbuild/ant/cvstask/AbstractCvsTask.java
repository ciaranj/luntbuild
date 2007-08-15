/*
 * Copyright  2002-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.luntsys.luntbuild.ant.cvstask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.util.StringUtils;
import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.ant.Execute;

/**
 * Performs operations on a CVS repository.
 * 
 * <p>original Cvs.java 1.20</p>
 *
 * <p>NOTE: This implementation has been moved here from <code>Cvs</code> with
 * the addition of some accessors for extensibility.  Another task
 * can extend this with some customized output processing.</p>
 *
 * @author costin@dnt.ro
 * @author stefano@apache.org
 * @author Wolfgang Werner
 *         <a href="mailto:wwerner@picturesafe.de">wwerner@picturesafe.de</a>
 * @author Kevin Ross
 *         <a href="mailto:kevin.ross@bredex.com">kevin.ross@bredex.com</a>
 *
 * @since Ant 1.5
 */
public abstract class AbstractCvsTask extends Task {
    /** Default compression level to use, if compression is enabled via <code>setCompression(true)</code>. */
    public static final int DEFAULT_COMPRESSION_LEVEL = 3;
    private static final int MAXIMUM_COMRESSION_LEVEL = 9;

    private Commandline cmd = new Commandline();

    /** list of Commandline children */
    private Vector vecCommandlines = new Vector();

    /**
     * the CVSROOT variable.
     */
    private String cvsRoot;

    /**
     * the CVS_RSH variable.
     */
    private String cvsRsh;

    /**
     * the package/module to check out.
     */
    private String cvsPackage;

	private String cvsDir;

    /**
     * the tag
     */
    private String tag;
    /**
     * the default command.
     */
    private static final String DEFAULT_COMMAND = "checkout";
    /**
     * the CVS command to execute.
     */
    private String command = null;

    /**
     * suppress information messages.
     */
    private boolean quiet = false;

    /**
     * suppress all messages.
     */
    private boolean reallyquiet = false;

    /**
     * compression level to use.
     */
    private int compression = 0;

    /**
     * report only, don't change any files.
     */
    private boolean noexec = false;

    /**
     * CVS port
     */
    private int port = 0;

    /**
     * CVS password file
     */
    private File passFile = null;

    /**
     * the directory where the checked out files should be placed.
     */
    private File dest;

    /** whether or not to append stdout/stderr to existing files */
    private boolean append = false;

    /**
     * the file to direct standard output from the command.
     */
    private File output;

    /**
     * the file to direct standard error from the command.
     */
    private File error;

    /**
     * If true it will stop the build if cvs exits with error.
     * Default is false. (Iulian)
     */
    private boolean failOnError = false;

    /**
     * Create accessors for the following, to allow different handling of
     * the output.
     */
    private ExecuteStreamHandler executeStreamHandler;
    private OutputStream outputStream;
    private OutputStream errorStream;

    /**
     * Creates a new cvs task with no arguments.
     */
    public AbstractCvsTask() {
        super();
    }

    /**
     * Sets the handler.
     * 
     * @param handler a handler able of processing the output and error streams from the cvs exe
     */
    public void setExecuteStreamHandler(ExecuteStreamHandler handler) {
        this.executeStreamHandler = handler;
    }

    /**
     * Finds the handler or instantiates it if it does not exist yet.
     * 
     * @return the handler for output and error streams
     */
    protected ExecuteStreamHandler getExecuteStreamHandler() {

        if (this.executeStreamHandler == null) {
            setExecuteStreamHandler(new PumpStreamHandler(getOutputStream(),
                                                          getErrorStream()));
        }

        return this.executeStreamHandler;
    }

    /**
     * Sets a stream to which the output from the cvs executable should be sent.
     * 
     * @param outputStream the stream to which the stdout from cvs should go
     */
    protected void setOutputStream(OutputStream outputStream) {

        this.outputStream = outputStream;
    }

    /**
     * Accesses the stream to which the stdout from cvs should go.
     * If this stream has already been set, it will be returned.
     * If the stream has not yet been set and the attribute output
     * has been set, the output stream will go to the output file.
     * Otherwise, the output will go to ant's logging system.
     * 
     * @return the output stream to which cvs' stdout should go to
     */
    protected OutputStream getOutputStream() {

        if (this.outputStream == null) {

            if (output != null) {
                try {
                    setOutputStream(new PrintStream(
                                        new BufferedOutputStream(
                                            new FileOutputStream(output
                                                                 .getPath(),
                                                                 append))));
                } catch (IOException e) {
                    throw new BuildException(e, getLocation());
                }
            } else {
                setOutputStream(new LogOutputStream(this, Project.MSG_INFO));
            }
        }

        return this.outputStream;
    }

    /**
     * Sets a stream to which the stderr from the cvs exe should go.
     * 
     * @param errorStream an output stream willing to process stderr
     */
    protected void setErrorStream(OutputStream errorStream) {

        this.errorStream = errorStream;
    }

    /**
     * Accesses the stream to which the stderr from cvs should go.
     * If this stream has already been set, it will be returned.
     * If the stream has not yet been set and the attribute error
     * has been set, the output stream will go to the file denoted by the error attribute.
     * Otherwise, the stderr output will go to ant's logging system.
     * 
     * @return the output stream to which cvs' stderr should go to
     */
    protected OutputStream getErrorStream() {

        if (this.errorStream == null) {

            if (error != null) {

                try {
                    setErrorStream(new PrintStream(
                                       new BufferedOutputStream(
                                           new FileOutputStream(error.getPath(),
                                                                append))));
                } catch (IOException e) {
                    throw new BuildException(e, getLocation());
                }
            } else {
                setErrorStream(new LogOutputStream(this, Project.MSG_WARN));
            }
        }

        return this.errorStream;
    }

    /**
     * Sets up the environment for <code>toExecute</code> and then runs it.
     * 
     * @param toExecute the command line to execute
     * @throws BuildException if <code>failonError</code> is set to <code>true</code> and the cvs command fails
     */
    protected void runCommand(Commandline toExecute) throws BuildException {
        // XXX: we should use JCVS (www.ice.com/JCVS) instead of
        // command line execution so that we don't rely on having
        // native CVS stuff around (SM)

        // We can't do it ourselves as jCVS is GPLed, a third party task
        // outside of jakarta repositories would be possible though (SB).

        Environment env = new Environment();

        if (port > 0) {
            Environment.Variable var = new Environment.Variable();
            var.setKey("CVS_CLIENT_PORT");
            var.setValue(String.valueOf(port));
            env.addVariable(var);
        }

        /**
         * Need a better cross platform integration with <cvspass>, so
         * use the same filename.
         */
        if (passFile == null) {

            File defaultPassFile = new File(
                System.getProperty("cygwin.user.home",
                    System.getProperty("user.home"))
                + File.separatorChar + ".cvspass");

            if (defaultPassFile.exists()) {
                this.setPassfile(defaultPassFile);
            }
        }

        if (passFile != null) {
            if (passFile.isFile() && passFile.canRead()) {
                Environment.Variable var = new Environment.Variable();
                var.setKey("CVS_PASSFILE");
                var.setValue(String.valueOf(passFile));
                env.addVariable(var);
                log("Using cvs passfile: " + String.valueOf(passFile),
                    Project.MSG_INFO);
            } else if (!passFile.canRead()) {
                log("cvs passfile: " + String.valueOf(passFile)
                    + " ignored as it is not readable",
                    Project.MSG_WARN);
            } else {
                log("cvs passfile: " + String.valueOf(passFile)
                    + " ignored as it is not a file",
                    Project.MSG_WARN);
            }
        }

        if (cvsRsh != null) {
            Environment.Variable var = new Environment.Variable();
            var.setKey("CVS_RSH");
            var.setValue(String.valueOf(cvsRsh));
            env.addVariable(var);
        }

        //
        // Just call the getExecuteStreamHandler() and let it handle
        //     the semantics of instantiation or retrieval.
        //
        Execute exe = new Execute(getExecuteStreamHandler(), null);

        exe.setAntRun(getProject());
        if (dest == null) {
            dest = getProject().getBaseDir();
        }

        if (!com.luntsys.luntbuild.utility.Luntbuild.existsDir(dest.getPath())) {
            com.luntsys.luntbuild.utility.Luntbuild.createDir(dest.getPath());
        }

        exe.setWorkingDirectory(dest);
        exe.setCommandline(toExecute.getCommandline());
        exe.setEnvironment(env.getVariables());

        try {
            String actualCommandLine = executeToString(exe);
            log(actualCommandLine, Project.MSG_VERBOSE);
            int retCode = exe.execute();
            log("retCode=" + retCode, Project.MSG_DEBUG);
            /*Throw an exception if cvs exited with error. (Iulian)*/
            if (failOnError && Execute.isFailure(retCode)) {
                throw new BuildException("cvs exited with error code "
                                         + retCode
                                         + StringUtils.LINE_SEP
                                         + "Command line was ["
                                         + actualCommandLine + "]", getLocation());
            }
        } catch (IOException e) {
            if (failOnError) {
                throw new BuildException(e, getLocation());
            } else {
                log("Caught exception: " + e.getMessage(), Project.MSG_WARN);
            }
        } catch (BuildException e) {
            if (failOnError) {
                throw(e);
            } else {
                Throwable t = e.getException();
                if (t == null) {
                    t = e;
                }
                log("Caught exception: " + t.getMessage(), Project.MSG_WARN);
            }
        } catch (Exception e) {
            if (failOnError) {
                throw new BuildException(e, getLocation());
            } else {
                log("Caught exception: " + e.getMessage(), Project.MSG_WARN);
            }
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
            if (errorStream != null) {
                try {
                    errorStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }

    /**
     * Executes the cvs task.
     * 
     * @throws BuildException if <code>failOnError</code> is set to <code>true</code> and the cvs command fails
     */
    public void execute() throws BuildException {

        String savedCommand = getCommand();

        if (this.getCommand() == null && vecCommandlines.size() == 0) {
            // re-implement legacy behaviour:
            this.setCommand(AbstractCvsTask.DEFAULT_COMMAND);
        }

        String c = this.getCommand();
        Commandline cloned = null;
        if (c != null) {
            cloned = (Commandline) cmd.clone();
            cloned.createArgument(true).setLine(c);
            this.addConfiguredCommandline(cloned, true);
        }

        try {
            for (int i = 0; i < vecCommandlines.size(); i++) {
                this.runCommand((Commandline) vecCommandlines.elementAt(i));
            }
        } finally {
            if (cloned != null) {
                removeCommandline(cloned);
            }
            setCommand(savedCommand);
        }
    }

    /**
     * Converts an <code>Execute</code> object into a string.  The string will not be an executable command.
     * 
     * @param execute the execute to convert
     * @return a string representation of the execute object
     */
    private String executeToString(Execute execute) {

        StringBuffer stringBuffer =
            new StringBuffer(Commandline.describeCommand(execute
                                                         .getCommandline()));

        String newLine = StringUtils.LINE_SEP;
        String[] variableArray = execute.getEnvironment();

        if (variableArray != null) {
            stringBuffer.append(newLine);
            stringBuffer.append(newLine);
            stringBuffer.append("environment:");
            stringBuffer.append(newLine);
            for (int z = 0; z < variableArray.length; z++) {
                stringBuffer.append(newLine);
                stringBuffer.append("\t");
                stringBuffer.append(variableArray[z]);
            }
        }

        return stringBuffer.toString();
    }

    /**
     * Sets the CVSROOT variable.
     *
     * @param root the CVSROOT variable
     */
    public void setCvsRoot(String root) {

        // Check if not real cvsroot => set it to null
        if (root != null) {
            if (root.trim().equals("")) {
                root = null;
            }
        }

        this.cvsRoot = root;
    }

    /**
     * Gets the CVSROOT variable.
     * 
     * @return the CVSROOT variable
     */
    public String getCvsRoot() {

        return this.cvsRoot;
    }

    /**
     * Sets the CVS_RSH variable.
     *
     * @param rsh the CVS_RSH variable
     */
    public void setCvsRsh(String rsh) {
        // Check if not real cvsrsh => set it to null
        if (rsh != null) {
            if (rsh.trim().equals("")) {
                rsh = null;
            }
        }

        this.cvsRsh = rsh;
    }

    /**
     * Gets the CVS_RSH variable.
     * 
     * @return the CVS_RSH variable
     */
    public String getCvsRsh() {

        return this.cvsRsh;
    }

    /**
     * Sets the port used by CVS to communicate with the server.
     *
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets the port used by CVS to communicate with the server.
     * 
     * @return the port
     */
    public int getPort() {

        return this.port;
    }

    /**
     * Sets the file to read passwords from.
     *
     * @param passFile the password file
     */
    public void setPassfile(File passFile) {
        this.passFile = passFile;
    }

    /**
     * Gets the file to read passwords from.
     * 
     * @return the password file
     */
    public File getPassFile() {

        return this.passFile;
    }

    /**
     * Sets the directory where the checked out files should be placed.
     *
     * <p>Note that this is different from CVS's -d command line
     * switch as Ant will never shorten pathnames to avoid empty
     * directories.</p>
     *
     * @param dest the destination directory
     */
    public void setDest(File dest) {
        this.dest = dest;
    }

    /**
     * Gets the directory where the checked out files should be placed
     *
     * @return the destination directory
     */
    public File getDest() {

        return this.dest;
    }

    /**
     * Sets the package or module to operate upon.
     *
     * @param p the package or module
     */
    public void setPackage(String p) {
        this.cvsPackage = p;
    }

    /**
     * Gets the package or module to operate upon.
     *
     * @return the package or module
     */
    public String getPackage() {

        return this.cvsPackage;
    }
    /**
     * Gets the tag or branch.
     * 
     * @return the tag or branch
     * @since ant 1.6.1
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the tag of the package or module to operate upon.
     * @param p the tag or branch
     */
    public void setTag(String p) {
        // Check if not real tag => set it to null
        if (p != null && p.trim().length() > 0) {
            tag = p;
            addCommandArgument("-r" + p);
        }
    }

    /**
     * Adds an argument to the command line.
     * This needs to be public to allow configuration of commands externally.
     * 
     * @param arg the command argument
     */
    public void addCommandArgument(String arg) {
        this.addCommandArgument(cmd, arg);
    }

    /**
     * Adds a command line argument to an external command.
     *
     * <p>I do not understand what this method does in this class ???<br/>
     * particularly not why it is public ????<br/>
     * AntoineLL July 23d 2003</p>
     *
     * @param c the command line to which one argument should be added
     * @param arg the argument to add
     */
    public void addCommandArgument(Commandline c, String arg) {
        c.createArgument().setValue(arg);
    }

    /**
     * Sets the <code>-D</code> flag to use the most recent revision no later than the given date.
     * 
     * @param p a date as string in a format that the CVS executable can understand
     * @see "man cvs"
     */
    public void setDate(String p) {
        if (p != null && p.trim().length() > 0) {
            addCommandArgument("-D");
            addCommandArgument(p);
        }
    }

    /**
     * The CVS command to execute.
     * 
     * <p>This should be deprecated, it is better to use the Commandline class ?<br/>
     * AntoineLL July 23d 2003</p>
     * 
     * @param c a command as string
     */
    public void setCommand(String c) {
        this.command = c;
    }
    /**
     * Gets the command line as a string.
     *
     * <p>This should be deprecated<br/>
     * AntoineLL July 23d 2003</p>
     *
     * @return the command line as a string
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * Sets the quiet setting. If <code>true</code>, suppress informational messages.
     * 
     * @param q if <code>true</code>, suppress informational messages
     */
    public void setQuiet(boolean q) {
        quiet = q;
    }

    /**
     * Sets the really quiet setting. If <code>true</code>, suppress all messages.
     * 
     * @param q if <code>true</code>, suppress all messages
     * @since Ant 1.6
     */
    public void setReallyquiet(boolean q) {
        reallyquiet = q;
    }


    /**
     * Sets the no exec setting. If <code>true</code>, report only and don't change any files.
     *
     * @param ne if <code>true</code>, report only and do not change any files
     */
    public void setNoexec(boolean ne) {
        noexec = ne;
    }

    /**
     * Sets the file to direct standard output from the command.
     * 
     * @param output a file to which stdout should go
     */
    public void setOutput(File output) {
        this.output = output;
    }

    /**
     * Sets the file to direct standard error from the command.
     *
     * @param error a file to which stderr should go
     */
    public void setError(File error) {
        this.error = error;
    }

    /**
     * Sets the file append setting. If <code>true</code>, the output/error will be appended when redirecting to a file.
     * @param value <code>true</code> indicates you want to append
     */
    public void setAppend(boolean value) {
        this.append = value;
    }

    /**
     * Stops the build process if the command exits with
     * a return code other than <code>0</code>.
     * Defaults to <code>false</code>.
     * 
     * @param failOnError if <code>true</code>, stops the build process if the command exits with
     * a return code other than <code>0</code>
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    /**
     * Configures a commandline element for things like cvsRoot, quiet, etc.
     * <p>If the commandline is initially <code>null</code>, the function is a noop
     * otherwise the function append to the commandline arguments concerning
     * <ul>
     * <li>
     * cvs package
     * </li>
     * <li>
     * compression
     * </li>
     * <li>
     * quiet or reallyquiet
     * </li>
     * <li>cvsroot</li>
     * <li>noexec</li>
     * </ul></p>
     * 
     * @param c the command line which will be configured
     */
    protected void configureCommandline(Commandline c) {
        if (c == null) {
            return;
        }
		if (getCvsDir() == null || getCvsDir().trim().length()==0)
			c.setExecutable("cvs");
		else
			c.setExecutable(getCvsDir().trim() + File.separator + "cvs");

        if (cvsPackage != null) {
            c.createArgument().setValue(cvsPackage);
        }
        if (this.compression > 0 && this.compression <= MAXIMUM_COMRESSION_LEVEL) {
            c.createArgument(true).setValue("-z" + this.compression);
        }
        if (quiet && !reallyquiet) {
            c.createArgument(true).setValue("-q");
        }
        if (reallyquiet) {
            c.createArgument(true).setValue("-Q");
        }
        if (noexec) {
            c.createArgument(true).setValue("-n");
        }
        if (cvsRoot != null) {
            c.createArgument(true).setLine("-d" + cvsRoot);
        }
    }

    /**
     * Removes a particular command from a vector of command lines.
     * 
     * @param c the command line which should be removed
     */
    protected void removeCommandline(Commandline c) {
        vecCommandlines.removeElement(c);
    }

    /**
     * Adds a direct command-line to execute.
     * 
     * @param c the command line to execute
     */
    public void addConfiguredCommandline(Commandline c) {
        this.addConfiguredCommandline(c, false);
    }

    /**
     * Configures and adds the given Commandline.
     * 
     * @param c the commandline to insert
     * @param insertAtStart if <code>true</code>, <code>c</code> is
     * inserted at the beginning of the vector of command lines
    */
    public void addConfiguredCommandline(Commandline c, boolean insertAtStart) {
        if (c == null) {
            return;
        }
        this.configureCommandline(c);
        if (insertAtStart) {
            vecCommandlines.insertElementAt(c, 0);
        } else {
            vecCommandlines.addElement(c);
        }
    }

    /**
    * Sets the compression level. If set to a value 1-9 it adds <code>-zN</code> to the cvs command line, else
    * it disables compression.
    * 
     * @param level the compression level (1 to 9)
    */
    public void setCompressionLevel(int level) {
        this.compression = level;
    }

    /**
     * Sets compression. If <code>true</code>, this is the same as compression level "3".
     *
     * @param usecomp if <code>true</code>, turns on compression using default
     * level
     * @see AbstractCvsTask#DEFAULT_COMPRESSION_LEVEL
     */
    public void setCompression(boolean usecomp) {
        setCompressionLevel(usecomp
            ? AbstractCvsTask.DEFAULT_COMPRESSION_LEVEL : 0);
    }

    /**
     * Gets the CVSDIR variable.
     * 
     * @return the CVSDIR variable
     */
	public String getCvsDir() {
		return cvsDir;
	}

	/**
	 * Sets the CVSDIR variable.
	 * 
	 * @param cvsDir the CVSDIR variable
	 */
	public void setCvsDir(String cvsDir) {
		this.cvsDir = cvsDir;
	}
}
