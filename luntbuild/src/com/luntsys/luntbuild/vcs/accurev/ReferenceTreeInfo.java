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
 * Reference Tree info object.
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class ReferenceTreeInfo {
    private String name;
    private StreamInfo basisStream;
    private long transaction;
    private boolean hidden;
    private String storage;

    /**
     * Creates a new reference tree info object.
     * 
     * @param name the name of the reference tree
     * @param basisStream the backing stream
     * @param transaction the transaction number
     * @param hidden set <code>true</code> if this reference tree should be hidden
     * @param storage the storage directory
     * @throws IllegalArgumentException if <code>basisStream</code> is <code>null</code>
     */
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
     * Creates a new reference tree in AccuRev for the specified module.
     * 
     * @param workingDir the working directory
     * @param module the module
     * @param antProject the ant project used for logging
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

    /**
     * Gets the name of this reference tree.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the backing stream for this reference tree.
     * 
     * @return the backing stream
     */
    public StreamInfo getBasisStream() {
        return basisStream;
    }

    /**
     * Gets the AccuRev transaction number of this reference tree
     * 
     * @return the transaction number
     */
    public long getTransaction() {
        return transaction;
    }

    /**
     * Checks if this reference tree is hidden.
     * 
     * @return <code>true</code> if this reference tree is hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets this reference tree's hidden status.
     * 
     * @param hidden set <code>true</code> if this reference tree should be hidden
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Gets the storage directory of this reference tree.
     * 
     * @return the storage directory
     */
    public String getStorage() {
        return storage;
    }

    /**
     * Updates the reference tree of the specified module.
     * 
     * @param module the module
     * @param antProject the ant project used for logging
     */
    public static void updateReferenceTree(AccurevAdaptor.AccurevModule module, Project antProject) {
        Commandline cmdLine;
        cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setValue("update");
        cmdLine.createArgument().setLine("-r " + module.getReferenceTree());
        AccurevHelper.executeVoidCommand(cmdLine, antProject);
    }

    /**
     * Gets a reference tree info object for the specified reference tree.
     * 
     * @param depot the AccuRev depot
     * @param referenceTreeName the name of the reference tree
     * @param antProject the ant project used for logging
     * @return the reference tree info object, or <code>null</code> if the tree could not be found
     * @throws IllegalArgumentException if <code>referenceTreeName</code> is <code>null</code>
     */
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

    /**
     * Removes this reference tree.
     * 
     * @param antProject the ant project used for logging
     */
    public void remove(Project antProject) {
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("remove ref " + name);
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
        hidden = true;
    }

    /**
     * Reactivates this reference tree.
     * 
     * @param antProject the ant project used for logging
     */
    public void reactivate(Project antProject) {
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("reactivate ref " + name);
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
        hidden = false;
    }

    /**
     * Moves this reference tree to a new working directory.
     * 
     * @param workingDir the new working directory
     * @param antProject the ant project used for logging
     */
    public void move(String workingDir, Project antProject) {
        Commandline cmdLine = new Commandline();
        cmdLine.setExecutable("accurev");
        cmdLine.createArgument().setLine("chref -r " + name);
        cmdLine.createArgument().setLine("-l \"" + workingDir +"\"");
        AccurevHelper.executeVoidCommand(cmdLine,antProject);
    }
}
