/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.vcs.accurev;

import org.apache.tools.ant.Project;
import org.dom4j.Document;
import org.dom4j.Element;
import com.luntsys.luntbuild.vcs.AccurevAdaptor;
import com.luntsys.luntbuild.ant.Commandline;


/**
 * StreamInfo
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class StreamInfo {
    private String name;
    private String backingStream;
    private String depot;
    private StreamType type;

    /**
     * Creates a new StreamInfo instance
     * @param name
     * @param backingStream
     * @param depot
     * @param type
     */
    public StreamInfo(String name, String backingStream, String depot, StreamType type) {
        this.name = name;
        this.backingStream = backingStream;
        this.depot = depot;
        this.type = type;
    }

    /**
     * Reads the name, basis, and type attributes of the element to create a new StreamInfo instance
     * @param streamElement
     * @param depot
     * @return
     */
    public static StreamInfo buildStreamInfo(Element streamElement, String depot) {
        if (streamElement == null) {
            return null;
        }
        String name = streamElement.attributeValue("name");
        String basis = streamElement.attributeValue("basis");
        String type = streamElement.attributeValue("type");

        StreamInfo info = new StreamInfo(name,basis,depot,StreamType.fromString(type));
        return info;
    }

    /**
     * Creates a new Stream in Accurev and returns a StreamInfo describing it
     * @param streamName
     * @param module
     * @param lastTransaction
     * @param antProject
     * @return
     */
    public static StreamInfo buildStream(String streamName, AccurevAdaptor.AccurevModule module, Long lastTransaction, Project antProject) {
        String backingStream = module.getBackingStream();
        antProject.log("Building stream " + streamName + " from stream " + backingStream
                + ((lastTransaction != null) ? " last transaction = " + lastTransaction : ""));
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("mkstream");
        cmdLine.createArgument().setLine("-s " + streamName);
        cmdLine.createArgument().setLine("-b " + backingStream);
        if (lastTransaction != null) {
            cmdLine.createArgument().setLine("-t " + lastTransaction);
        }
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
        final StreamInfo streamInfo = new StreamInfo(streamName,backingStream,module.getDepot(),StreamType.normal);
        streamInfo.lockStream("Build stream for backing stream " + backingStream
                +((lastTransaction != null) ? " at transaction " + lastTransaction : ""), antProject);
        return streamInfo;
    }

    public String getName() {
        return name;
    }

    public String getBackingStream() {
        return backingStream;
    }

    public String getDepot() {
        return depot;
    }

    public StreamType getType() {
        return type;
    }

    public static StreamInfo findStreamInfo(String depot, String stream, Project antProject) {
         antProject.log("Getting Stream info for Stream : depot = " + depot + " stream = " + stream);
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("show -fx -p " + depot + " -s " + stream + " streams");
        Document doc = AccurevHelper.buildResponseDocument(cmdLine, antProject);
        if (doc == null) {
            return null;
        }
        Element streamElement = (Element) doc.selectSingleNode("/streams/stream");
        return buildStreamInfo(streamElement, depot);

    }

    public static StreamInfo findStreamInfoByStreamNumber(String depot, long streamNum, Project antProject) {
         antProject.log("Getting Stream info for Stream : depot = " + depot + " stream number = " + streamNum);
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("show -fx -p " + depot + " streams");
        Document doc = AccurevHelper.buildResponseDocument(cmdLine, antProject);
        if (doc == null) {
            return null;
        }
        Element streamElement = (Element) doc.selectSingleNode("/streams/stream[@streamNumber=" + streamNum + "]");
        return buildStreamInfo(streamElement, depot);
    }

    private void lockStream(String comment, Project antProject) {
        antProject.log("Locking stream " + name + " with comment \"" + comment + "\"");
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("lock");
        if (comment != null && comment.trim().length() > 0) {
            cmdLine.createArgument().setLine("-c \"" + comment + "\"");
        }
        cmdLine.createArgument().setValue(name);
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
    }

    /**
     * Updates the Stream represented by the StreamInfo using (optionally) the given
     * transaction number as the point-in-time to sync to
     * @param lastTransaction - optionally the Accurev transaction number to sync to
     * @param antProject
     */
    public void updateStream(Long lastTransaction, Project antProject) {
        antProject.log("Updating stream " + name + " from stream " + backingStream
                + ((lastTransaction != null) ? " at last transaction  " + lastTransaction : ""));
        unlockStream(antProject);
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("chstream");
        cmdLine.createArgument().setLine("-b " + backingStream);
        cmdLine.createArgument().setLine("-s " + name);
        if (lastTransaction != null) {
            cmdLine.createArgument().setLine("-t " + lastTransaction);
        }
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
        lockStream("Build stream for backing stream " + backingStream
                + ((lastTransaction != null) ? " at last transaction  " + lastTransaction : ""), antProject);
    }

    private void unlockStream(Project antProject) {
        antProject.log("Unlocking stream " + name);
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("unlock");
        cmdLine.createArgument().setValue(name);
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
    }
}
