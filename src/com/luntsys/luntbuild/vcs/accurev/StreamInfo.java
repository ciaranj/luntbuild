/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */

package com.luntsys.luntbuild.vcs.accurev;

import org.apache.tools.ant.Project;
import org.dom4j.Document;
import org.dom4j.Element;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.vcs.AccurevAdaptor;
import com.luntsys.luntbuild.ant.Commandline;

/**
 * Stream info object.
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class StreamInfo {
    private String name;
    private String backingStream;
    private String depot;
    private StreamType type;

    /**
     * Creates a new stream info object.
     * 
     * @param name the name of the stream
     * @param backingStream the backing stream
     * @param depot the AccuRev depot
     * @param type the type
     */
    public StreamInfo(String name, String backingStream, String depot, StreamType type) {
        this.name = name;
        this.backingStream = backingStream;
        this.depot = depot;
        this.type = type;
    }

    /**
     * Creates a new stream info object from an element.
     * Reads the name, basis, and type attributes of the element to create a new stream info object.
     * 
     * @param streamElement the element
     * @param depot the AccuRev depot
     * @return the stream info object
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
     * Creates a new stream in AccuRev and returns a stream info object describing it.
     * 
     * @param streamName the name for the new stream
     * @param module the module
     * @param lastTransaction the AccuRev transaction number to build the stream from, may be <code>null</code>
     * @param antProject the ant project used for logging
     * @return the stream info object
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

    /**
     * Gets the name of the stream.
     * 
     * @return the name
     */
    public String getName() {
        return name;
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
     * Gets the AccuRev depot.
     * 
     * @return the depot
     */
    public String getDepot() {
        return depot;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public StreamType getType() {
        return type;
    }

    /**
     * Gets the stream info object for the specified stream by name.
     * 
     * @param depot the AccuRev depot
     * @param stream the name of the stream
     * @param antProject the ant project used for logging
     * @return the stream info object, or <code>null</code> if the stream could not be found
     */
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

    /**
     * Gets the stream info object for the specified stream by number.
     * 
     * @param depot the AccuRev depot
     * @param streamNum the stream number
     * @param antProject the ant project used for logging
     * @return the stream info object, or <code>null</code> if the stream could not be found
     */
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

    /**
     * Locks this stream.
     * 
     * @param comment the comment, may be <code>null</code>
     * @param antProject the ant project used for logging
     */
    private void lockStream(String comment, Project antProject) {
        antProject.log("Locking stream " + name + " with comment \"" + comment + "\"");
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("lock");
        if (!Luntbuild.isEmpty(comment)) {
            cmdLine.createArgument().setLine("-c \"" + comment + "\"");
        }
        cmdLine.createArgument().setValue(name);
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
    }

    /**
     * Updates this stream using (optionally) the given transaction number as the point-in-time to sync to.
     * 
     * @param lastTransaction the AccuRev transaction number to sync to, may be <code>null</code>
     * @param antProject the ant project used for logging
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

    /**
     * Unlocks this stream.
     * 
     * @param antProject the ant project used for logging
     */
    private void unlockStream(Project antProject) {
        antProject.log("Unlocking stream " + name);
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("unlock");
        cmdLine.createArgument().setValue(name);
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
    }
}
