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
 * AccurevHelper
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
 public class AccurevHelper {
    public static void syncTime(Project antProject) {
        antProject.log("Syncing time...");
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("synctime");
        executeVoidCommand(cmdLine, antProject);
    }

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

    public static void forceWorkingDirRefresh(String workingDir, AccurevAdaptor.AccurevModule module, Project antProject) {
        antProject.log("Temporarily removing reference tree " + module.getReferenceTree());
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("remove ref " + module.getReferenceTree());
        executeVoidCommand(cmdLine,antProject);
        final String moduleDir = workingDir + "/" + module.getSrcPath();
        antProject.log("Forcing refresh of module dir: " + moduleDir);
        cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("pop");
        cmdLine.createArgument().setValue("-O");
        cmdLine.createArgument().setValue("-R");
        cmdLine.createArgument().setLine("-v " + module.getBuildStream());
        cmdLine.createArgument().setLine("-L " + moduleDir);
        cmdLine.createArgument().setValue(".");
        executeVoidCommand(cmdLine,antProject);
        antProject.log("Reactivating reference tree " + module.getReferenceTree());
        cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("reactivate ref " + module.getReferenceTree());
        executeVoidCommand(cmdLine,antProject);
    }

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
