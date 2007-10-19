/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */

package com.luntsys.luntbuild.vcs.accurev;

import com.luntsys.luntbuild.utility.MyExecTask;
import com.luntsys.luntbuild.vcs.AccurevAdaptor;
import com.luntsys.luntbuild.ant.Commandline;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * AccuRev helper.
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
 public class AccurevHelper {
    /**
     * Syncs time with AccuRev.
     * 
     * @param antProject the ant project used for logging
     */
    public static void syncTime(Project antProject) {
        antProject.log("Syncing time...");
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("synctime");
        executeVoidCommand(cmdLine, antProject);
    }
    
    /**
     * Sets the AccuRev user.
     * 
     * @param username the user name
     * @param password the user's password
     * @param antProject the ant project used for logging
     */
    public static void setUser(String username, String password, Project antProject) {
    	antProject.log("Setting User: "+ username) ;
    	
    	Commandline cmd = new Commandline() ;
    	cmd.setExecutable("accurev") ;
    	cmd.createArgument().setValue("setpref");
    	cmd.createArgument().setLine("AC_PRINCIPAL " + username) ;
    	executeVoidCommand(cmd, antProject) ;
    	
    	cmd = new Commandline() ;
    	cmd.setExecutable("accurev") ;
    	cmd.createArgument().setLine("setlocalpasswd " + password) ;
    	executeVoidCommand(cmd, antProject) ;
    }

    /**
     * Gets the last transaction number for the specified module.
     * 
     * @param module the module
     * @param antProject the ant project used for logging
     * @return the last transaction number, or <code>null</code> if none exists
     */
    public static Long getLastTransactionNumber(AccurevModuleInterface module, Project antProject) {
        String backingStream = module.getBackingStream();
        antProject.log("Getting last transaction number for backing stream " + backingStream);
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("hist");
        cmdLine.createArgument().setValue("-a");
        cmdLine.createArgument().setLine("-p " +module. getDepot());
        cmdLine.createArgument().setLine("-s " + backingStream);
        cmdLine.createArgument().setLine("-k promote");
        cmdLine.createArgument().setLine("-t now.1");
        cmdLine.createArgument().setValue("-fx");
        Document doc = buildResponseDocument(cmdLine, antProject);
        if (doc == null) {
            return null;
        }
        Attribute idAttr = (Attribute) doc.selectSingleNode("/AcResponse/transaction/@id");
        if (idAttr != null) {
            return Long.valueOf(idAttr.getValue());
        }
        return null;
    }

    /**
     * Forces a refresh of the working directory.
     * 
     * @param workingDir the working directory
     * @param module the module
     * @param antProject the ant project used for logging
     */
    public static void forceWorkingDirRefresh(String workingDir, AccurevAdaptor.AccurevModule module, Project antProject) {
        antProject.log("Temporarily removing reference tree " + module.getReferenceTree());
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("remove ref " + module.getReferenceTree());
        executeVoidCommand(cmdLine,antProject);
        final String srcPath = module.getSrcPath() ;
        final String moduleDir = workingDir + ((srcPath!=null)?"/" + module.getSrcPath():"");
        antProject.log("Forcing refresh of module dir: " + moduleDir);
        cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("pop");
        cmdLine.createArgument().setValue("-O");
        cmdLine.createArgument().setValue("-R");
        cmdLine.createArgument().setLine("-v " + module.getBuildStream());
        cmdLine.createArgument().setLine("-L \"" + moduleDir+"\"");
        cmdLine.createArgument().setValue(".");
        executeVoidCommand(cmdLine,antProject);
        antProject.log("Reactivating reference tree " + module.getReferenceTree());
        cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("reactivate ref " + module.getReferenceTree());
        executeVoidCommand(cmdLine,antProject);
    }

    /**
     * Executes a commandline object and ignores the return value.
     * 
     * @param cmdLine the commandline object to execute
     * @param antProject the ant project used for logging
     * @throws BuildException from {@link Execute#execute()}
     */
    public static void executeVoidCommand(Commandline cmdLine, Project antProject) {
        antProject.log("Executing command line with no return: " + cmdLine.toString(), Project.MSG_INFO);
        Execute exec = new Execute();
        exec.setCommandline(cmdLine.getCommandline());
        exec.setAntRun(antProject);
        exec.setStreamHandler(new LogStreamHandler(
                                                            antProject.createTask("exec"),
                                                            Project.MSG_INFO,
                                                            Project.MSG_WARN));
        try {
            exec.execute();
        } catch (IOException e) {
            throw new BuildException("ERROR: " + e.getMessage(), e);
        }
    }

    /**
     * Constructs a response document.
     * 
     * @param cmdLine the commandline object to execute
     * @param antProject the ant project used for logging
     * @return the response document, or <code>null</code> if the stream does not exist
     * @throws BuildException if unable to create the document
     */
    public static Document buildResponseDocument(Commandline cmdLine, final Project antProject) {
        antProject.log("Executing command line to parse output: " + cmdLine.toString(), Project.MSG_INFO);

        Document doc;
        try {
            SAXReader saxReader = new SAXReader(false);
            final StringBuffer buffer = new StringBuffer();
            try {
                new MyExecTask("exec", antProject, cmdLine, Project.MSG_INFO) {
                            public void handleStdout(String line) {
                                buffer.append(line);
                            }
                        }.execute();
            } catch (BuildException e) {
                // If e.g. the stream doesn't exist, the command will return 1 and it will throw an exception
                antProject.log("Caught BuildException while executing command " + cmdLine
                        + ". This can mean that the operation returned a non-zero return code.");
                return null;
            }
            doc = saxReader.read(new StringReader(buffer.toString()));
		} catch (DocumentException e) {
            throw new BuildException("ERROR: " + e.getMessage(), e);
        }
        return doc;
    }
}
