/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.vcs.accurev;

import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.MyExecTask;
import com.luntsys.luntbuild.vcs.AccurevAdaptor;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import com.luntsys.luntbuild.ant.Commandline;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

/**
 * AccurevHelper
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
 public class AccurevHelper {
    public static void syncTime(Project antProject, AccurevAdaptor adaptor) {
        antProject.log("Syncing time...");
		Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("synctime");
        if (!Luntbuild.isEmpty(adaptor.getPort()))
			cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        executeVoidCommand(cmdLine, antProject);
    }

    public static Long getLastTransactionNumber(AccurevAdaptor.AccurevModule module,
            Project antProject, AccurevAdaptor adaptor) {
        String backingStream = module.getBackingStream();
        antProject.log("Getting last transaction number for backing stream " + backingStream);
		Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("hist");
        if (!Luntbuild.isEmpty(adaptor.getPort()))
			cmdLine.createArgument().setLine("-H " + adaptor.getPort());
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

    public static void buildReferenceTree(String workingDir, AccurevAdaptor.AccurevModule module,
            Project antProject, AccurevAdaptor adaptor) {
        String referenceTreeName = module.getReferenceTree();
        antProject.log("Creating reference tree " + referenceTreeName);
		Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("mkref");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setLine("-r " + referenceTreeName);
        cmdLine.createArgument().setLine("-b " + module.getBuildStream());
        cmdLine.createArgument().setLine("-l " + workingDir + "/" + module.getSrcPath());
        executeVoidCommand(cmdLine,antProject);
    }

    public static void updateReferenceTree(AccurevAdaptor.AccurevModule module,
            Project antProject, AccurevAdaptor adaptor) {
		Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("update");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setLine("-r " + module.getReferenceTree());
        executeVoidCommand(cmdLine, antProject);
    }

    public static void forceWorkingDirRefresh(String workingDir, AccurevAdaptor.AccurevModule module,
            Project antProject, AccurevAdaptor adaptor) {
        antProject.log("Temporarily removing reference tree " + module.getReferenceTree());
		Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("remove");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setLine("ref " + module.getReferenceTree());
        executeVoidCommand(cmdLine,antProject);
        final String moduleDir = workingDir + "/" + module.getSrcPath();
        antProject.log("Forcing refresh of module dir: " + moduleDir);
        cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("pop");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setValue("-O");
        cmdLine.createArgument().setValue("-R");
        cmdLine.createArgument().setLine("-v " + module.getBuildStream());
        cmdLine.createArgument().setLine("-L " + moduleDir);
        cmdLine.createArgument().setValue(".");
        executeVoidCommand(cmdLine,antProject);
        antProject.log("Reactivating reference tree " + module.getReferenceTree());
        cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("reactivate");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setLine("ref " + module.getReferenceTree());
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

    public static ReferenceTreeInfo getReferenceTreeInfo(String depot, String referenceTreeName,
            Project antProject, AccurevAdaptor adaptor) {
        antProject.log("Getting existing reference tree info for reference tree = " + referenceTreeName);
        if (referenceTreeName == null) {
            throw new IllegalArgumentException("Reference tree name cannot be null.");
        }
        Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("show");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setLine("-fix refs");
        Document doc = buildResponseDocument(cmdLine, antProject);
        if (doc == null) {
            return null;
        }
        List refTreesElements = doc.selectNodes( "/AcResponse/Element" );
        for (Iterator iterator = refTreesElements.iterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            String name = element.attributeValue("Name");
            if (referenceTreeName.equals(name)) {
                String hiddenAttr = element.attributeValue("hidden");
                boolean hidden = (hiddenAttr == null) ? false : Boolean.valueOf(hiddenAttr).booleanValue();
                long streamNum = Long.parseLong(element.attributeValue("Stream"));
                StreamInfo streamInfo = findStreamInfoByStreamNumber(depot, streamNum,antProject, adaptor);
                long transaction = Long.parseLong(element.attributeValue("Trans"));
                ReferenceTreeInfo info = new ReferenceTreeInfo(name,streamInfo,transaction, hidden);
                return info;
            }
        }
        return null;
    }

    public static StreamInfo findStreamInfo(String depot, String stream, Project antProject, AccurevAdaptor adaptor) {
         antProject.log("Getting Stream info for Stream : depot = " + depot + " stream = " + stream);
        Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("show");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setLine("-fx -p " + depot + " -s " + stream + " streams");
        Document doc = buildResponseDocument(cmdLine, antProject);
        if (doc == null) {
            return null;
        }
        Element streamElement = (Element) doc.selectSingleNode("/streams/stream");
        return buildStreamInfo(streamElement, depot);

    }

    public static StreamInfo findStreamInfoByStreamNumber(String depot, long streamNum,
														  Project antProject, AccurevAdaptor adaptor) {
         antProject.log("Getting Stream info for Stream : depot = " + depot + " stream number = " + streamNum);
        Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("show");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setLine("-fx -p " + depot + " streams");
        Document doc = buildResponseDocument(cmdLine, antProject);
        if (doc == null) {
            return null;
        }
        Element streamElement = (Element) doc.selectSingleNode("/streams/stream[@streamNumber=" + streamNum + "]");
        return buildStreamInfo(streamElement, depot);
    }

    private static StreamInfo buildStreamInfo(Element streamElement, String depot) {
        if (streamElement == null) {
            return null;
        }
        String name = streamElement.attributeValue("name");
        String basis = streamElement.attributeValue("basis");
        String type = streamElement.attributeValue("type");

        StreamInfo info = new StreamInfo(name,basis,depot,StreamType.fromString(type));
        return info;
    }

    public static void buildStream(String streamName, String backingStream, Long lastTransaction,
            Project antProject, AccurevAdaptor adaptor) {
        antProject.log("Building stream " + streamName + " from stream " + backingStream
                + ((lastTransaction != null) ? " last transaction = " + lastTransaction : ""));
        Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("mkstream");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setLine("-s " + streamName);
        cmdLine.createArgument().setLine("-b " + backingStream);
        if (lastTransaction != null) {
            cmdLine.createArgument().setLine("-t " + lastTransaction);
        }
        executeVoidCommand(cmdLine,antProject);
        lockStream(streamName, "Build stream for backing stream " + backingStream
                +((lastTransaction != null) ? " at transaction " + lastTransaction : ""), antProject, adaptor);
    }

    private static void lockStream(String streamName, String comment, Project antProject, AccurevAdaptor adaptor) {
        antProject.log("Locking stream " + streamName + " with comment \"" + comment + "\"");
        Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("lock");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        if (!Luntbuild.isEmpty(comment)) {
            cmdLine.createArgument().setLine("-c \"" + comment + "\"");
        }
        cmdLine.createArgument().setValue(streamName);
        executeVoidCommand(cmdLine,antProject);
    }

    public static void updateStream(String streamName, String backingStream, Long lastTransaction,
            Project antProject, AccurevAdaptor adaptor) {
        antProject.log("Updating stream " + streamName + " from stream " + backingStream
                + ((lastTransaction != null) ? " at last transaction  " + lastTransaction : ""));
        if (lastTransaction == null) {
            throw new IllegalArgumentException("Last transaction number cannot be null for label update");
        }
        unlockStream(streamName, antProject, adaptor);
        Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("chstream");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setLine("-b " + backingStream);
        cmdLine.createArgument().setLine("-s " + streamName);
        cmdLine.createArgument().setLine("-t " + lastTransaction);
        executeVoidCommand(cmdLine,antProject);
        lockStream(streamName, "Build stream for backing stream " + backingStream
                + " at transaction " + lastTransaction, antProject, adaptor);
    }

    private static void unlockStream(String streamName, Project antProject, AccurevAdaptor adaptor) {
        antProject.log("Unlocking stream " + streamName);
        Commandline cmdLine = adaptor.buildAccurevExecutable();
        cmdLine.createArgument().setValue("unlock");
        if (!Luntbuild.isEmpty(adaptor.getPort())) cmdLine.createArgument().setLine("-H " + adaptor.getPort());
        cmdLine.createArgument().setValue(streamName);
        executeVoidCommand(cmdLine,antProject);
    }
}
