/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.vcs.accurev;

import com.luntsys.luntbuild.vcs.AccurevAdaptor;
import com.luntsys.luntbuild.ant.Commandline;
import org.apache.tools.ant.Project;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;
import java.util.Iterator;

/**
 * ReferenceTreeInfo
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class ReferenceTreeInfo {
    private String name;
    private StreamInfo basisStream;
    private long transaction;
    private boolean hidden;
    private String storage;

    private ReferenceTreeInfo(String name, StreamInfo basisStream, long transaction, boolean hidden, String storage) {
        if (basisStream == null) {
            throw new IllegalArgumentException("Backing stream for reference tree cannot be null.");
        }
        this.name = name;
        this.basisStream = basisStream;
        this.transaction = transaction;
        this.hidden = hidden;
        this.storage = storage;
    }

    /**
     * Create a new ReferenceTree in Accurev
     * @param workingDir
     * @param module
     * @param antProject
     */
    public static void buildReferenceTree(String workingDir, AccurevAdaptor.AccurevModule module, Project antProject) {
        String referenceTreeName = module.getReferenceTree();
        antProject.log("Creating reference tree " + referenceTreeName);
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("mkref");
        cmdLine.createArgument().setLine("-r " + referenceTreeName);
        cmdLine.createArgument().setLine("-b " + module.getBuildStream());
        cmdLine.createArgument().setLine("-l \"" + workingDir + "/" + module.getSrcPath()+"\"");
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
    }

    public String getName() {
        return name;
    }

    public StreamInfo getBasisStream() {
        return basisStream;
    }

    public long getTransaction() {
        return transaction;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getStorage() {
        return storage;
    }

    public static void updateReferenceTree(AccurevAdaptor.AccurevModule module, Project antProject) {
        Commandline cmdLine;
        cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("update");
        cmdLine.createArgument().setLine("-r " + module.getReferenceTree());
        AccurevHelper.executeVoidCommand(cmdLine, antProject);
    }

    public static ReferenceTreeInfo findReferenceTreeInfo(String depot, String referenceTreeName, Project antProject) {
        antProject.log("Getting existing reference tree info for reference tree = " + referenceTreeName);
        if (referenceTreeName == null) {
            throw new IllegalArgumentException("Reference tree name cannot be null.");
        }
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("show -fix refs");
        Document doc = AccurevHelper.buildResponseDocument(cmdLine, antProject);
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
                StreamInfo streamInfo = StreamInfo.findStreamInfoByStreamNumber(depot, streamNum,antProject);
                long transaction = Long.parseLong(element.attributeValue("Trans"));
                String storage = element.attributeValue("Storage");
                ReferenceTreeInfo info = new ReferenceTreeInfo(name,streamInfo,transaction, hidden, storage);
                return info;
            }
        }
        return null;
    }

    public void remove(Project antProject) {
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("remove ref " + name);
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
        hidden = true;
    }

    public void reactivate(Project antProject) {
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("reactivate ref " + name);
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
        hidden = false;
    }

    public void move(String workingDir, Project antProject) {
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("chref -r " + name);
        cmdLine.createArgument().setLine("-l \"" + workingDir +"\"");
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
    }
}
