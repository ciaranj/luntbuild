/*
 * Copyright  2001-2004 The Apache Software Foundation
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
package com.luntsys.luntbuild.ant.starteam;

import com.starbase.starteam.Folder;
import com.starbase.starteam.Item;
import com.starbase.starteam.Status;
import com.starbase.starteam.View;
import com.starbase.starteam.ViewConfiguration;

import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;


/**
 * Checks out files from a StarTeam project.
 * It also creates all working directories on the
 * local directory if appropriate. Ant Usage:
 * <pre>
 * &lt;taskdef name="starteamcheckout"
 * classname="org.apache.tools.ant.taskdefs.StarTeamCheckout"/&gt;
 * &lt;starteamcheckout username="BuildMaster" password="ant" starteamFolder="Source"
 * starteamurl="servername:portnum/project/view"
 * createworkingdirectories="true"/&gt;
 * </pre>
 *
 * @author Christopher Charlier, ThoughtWorks, Inc. 2001
 * @author <a href="mailto:jcyip@thoughtworks.com">Jason Yip</a>
 * @author Jason Pettiss
 * @author <a href="mailto:stevec@ignitesports.com">Steve Cohen</a>
 * @version 1.1
 * @see <A HREF="http://www.starbase.com/">StarBase Web Site</A>
 *
 */
public class StarTeamCheckout extends TreeBasedTask {

    /**
     * holder for the createDirs attribute
     */
    private boolean createDirs = true;

    /**
     * holder for the deleteUncontrolled attribute.  If true,
     * all local files not in StarTeam will be deleted.
     */
    private boolean deleteUncontrolled = true;

    /**
     * holder for the deleteUncontrolled attribute.  If true,
     * (default) local non-binary files will be checked out using the local
     * platform's EOL convention.  If false, checkouts will preserve the
     * server's EOL convention.
     */
    private boolean convertEOL = true;


    /**
     * flag (defaults to true) to create all directories
     * that are in the Starteam repository even if they are empty.
     *
     * @param value  the value to set the attribute to.
     */
    public void setCreateWorkingDirs(boolean value) {
        this.createDirs = value;
    }

    /**
     * Whether or not all local files <i>not<i> in StarTeam should be deleted.
     * Optional, defaults to <code>true</code>.
     * @param value  the value to set the attribute to.
     */
    public void setDeleteUncontrolled(boolean value) {
        this.deleteUncontrolled = value;
    }

    /**
     * Set whether or not files should be checked out using the
     * local machine's EOL convention.
     * Optional, defaults to <code>true</code>.
     * @param value  the value to set the attribute to.
     */
    public void setConvertEOL(boolean value) {
        this.convertEOL = value;
    }

    /**
     * Sets the label StarTeam is to use for checkout; defaults to the most recent file.
     * The label must exist in starteam or an exception will be thrown.
     * @param label the label to be used
     */
    public void setLabel(String label) {
        _setLabel(label);
    }
    
    /**
     * Sets the promotion state StarTeam is to use for checkout;  defaults to the most
     * recent file.  The promotion state must exist in starteam or an exception will 
     * be thrown.
     * @param promotionState the promotion state to be used
     */
    public void setPromotionState(String promotionState) {
        _setPromotionState(promotionState);
    }

    /**
     * This attribute tells whether to do a locked checkout, an unlocked
     * checkout or to leave the checkout status alone (default).  A locked
     * checkout locks all other users out from making changes.  An unlocked
     * checkout reverts all local files to their previous repository status
     * and removes the lock.
     * @see #setLocked(boolean)
     * @see #setUnlocked(boolean)
     */
    private int lockStatus = Item.LockType.UNCHANGED;

    /**
     * Set to do a locked checkout; optional default is false.
     * @param v  True to do a locked checkout, false to checkout without
     *           changing status/.
     * @exception BuildException if both locked and unlocked are set true
     */
    public void setLocked(boolean v) throws BuildException {
        setLockStatus(v, Item.LockType.EXCLUSIVE);
    }


    /**
     * Set to do an unlocked checkout. Default is false;
     * @param v  True to do an unlocked checkout, false to checkout without
     *           changing status.
     * @exception BuildException if both locked and unlocked are set true
     */
    public void setUnlocked(boolean v) throws BuildException {
        setLockStatus(v, Item.LockType.UNLOCKED);
    }

    private void setLockStatus(boolean v, int newStatus)
            throws BuildException {
        if (v) {
            if (this.lockStatus == Item.LockType.UNCHANGED) {
                this.lockStatus = newStatus;
            } else if (this.lockStatus != newStatus) {
                throw new BuildException(
                        "Error: cannot set locked and unlocked both true.");
            }
        }
    }

    /**
     * should checked out files get the timestamp from the repository
     * or the time they are checked out.  True means use the repository
     * timestamp.
     */
    private boolean useRepositoryTimeStamp = false;

    /**
     * sets the useRepositoryTimestmp member.
     *
     * @param useRepositoryTimeStamp
     *               true means checked out files will get the repository timestamp.
     *               false means the checked out files will be timestamped at the time
     *               of checkout.
     */
    public void setUseRepositoryTimeStamp(boolean useRepositoryTimeStamp) {
        this.useRepositoryTimeStamp = useRepositoryTimeStamp;
    }

    /**
     * returns the value of the useRepositoryTimestamp member
     *
     * @return the value of the useRepositoryTimestamp member
     */
    public boolean getUseRepositoryTimeStamp() {
        return this.useRepositoryTimeStamp;
    }

    /**
     * Override of base-class abstract function creates an
     * appropriately configured view for checkouts - either
     * the current view or a view from this.label or the raw
     * view itself in the case of a revision label.
     *
     * @param raw    the unconfigured <code>View</code>
     *
     * @return the snapshot <code>View</code> appropriately configured.
     * @exception BuildException
     */
    protected View createSnapshotView(View raw) throws BuildException {

        int promotionStateID = getPromotionStateID(raw);        
      
        // if a promotion state has been supplied, use it to configure the view
        if (this.isUsingPromotionState()) {
           return new View(raw, ViewConfiguration.createFromPromotionState(promotionStateID));
        }
        
        int labelID = getLabelID(raw);

        // if a label has been supplied and it is a view label, use it
        // to configure the view
        if (this.isUsingViewLabel()) {
            return new View(raw, ViewConfiguration.createFromLabel(labelID));
        }
        // if a label has been supplied and it is a revision label, use the raw
        // the view as the snapshot
        else if (this.isUsingRevisionLabel()) {
            return raw;
        }
        // if a date has been supplied use a view configured to the date.
        View view = getViewConfiguredByDate(raw);
        if (view != null) {
            return view;
        }
        // otherwise, use this view configured as the tip.
        else {
            return new View(raw, ViewConfiguration.createTip());
        }
    }


    /**
     * Implements base-class abstract function to define tests for
     * any preconditons required by the task.
     *
     * @exception BuildException thrown if both rootLocalFolder
     * and viewRootLocalFolder are defined
     */
    protected void testPreconditions() throws BuildException {
        if (this.isUsingRevisionLabel() && this.createDirs) {
            log("Ignoring createworkingdirs while using a revision label."
                + "  Folders will be created only as needed.",
                Project.MSG_WARN);
            this.createDirs = false;
        }
        if (lockStatus != Item.LockType.UNCHANGED) {
            boolean lockStatusBad = false;
            if (null != getLabel()) {
                log("Neither locked nor unlocked may be true"
                    + " when checking out a labeled version.",
                    Project.MSG_ERR);
                lockStatusBad = true;
            } else if (null != getAsOfDate()) {
                log("Neither locked nor unlocked may be true"
                    + " when checking out by date.",
                    Project.MSG_ERR);
                lockStatusBad = true;
            }
            if (lockStatusBad) {
                throw new BuildException(
                    "Lock status may not be changed"
                    + " when checking out a non-current version.");
            }
        }
        if (null != getLabel() && null != getAsOfDate()) {
            throw new BuildException(
                "Both label and asOfDate specified.  "
                + "Unable to process request.");
        }

    }

    /**
     * extenders should emit to the log an entry describing the parameters
     * that will be used by this operation.
     *
     * @param starteamrootFolder
     *               root folder in StarTeam for the operation
     * @param targetrootFolder
     *               root local folder for the operation (whether specified
     * by the user or not.
     */

    protected void logOperationDescription(
        Folder starteamrootFolder, java.io.File targetrootFolder) {
        log((this.isRecursive() ? "Recursive" : "Non-recursive")
            + " Checkout from: " + starteamrootFolder.getFolderHierarchy());

        log("  Checking out to"
            + (null == getRootLocalFolder() ? "(default): " : ": ")
            + targetrootFolder.getAbsolutePath());


        logLabel();
        logAsOfDate();
        logIncludes();
        logExcludes();

        if (this.lockStatus == Item.LockType.EXCLUSIVE) {
            log("  Items will be checked out with Exclusive locks.");
        } else if (this.lockStatus == Item.LockType.UNLOCKED) {
            log("  Items will be checked out unlocked "
                 + "(even if presently locked).");
        } else {
            log("  Items will be checked out with no change in lock status.");
        }
        log("  Items will be checked out with "
            + (this.useRepositoryTimeStamp ? "repository timestamps."
                                        : "the current timestamp."));
        log("  Items will be checked out "
            + (this.isForced() ? "regardless of" : "in accordance with")
            + " repository status.");
        if (this.deleteUncontrolled) {
            log("  Local items not found in the repository will be deleted.");
        }
        log("  Items will be checked out "
            + (this.convertEOL ? "using the local machine's EOL convention"
             : "without changing the EOL convention used on the server"));
        log("  Directories will be created"
            + (this.createDirs ? " wherever they exist in the repository, even if empty."
             : " only where needed to check out files."));

    }


    /**
     * Implements base-class abstract function to perform the checkout
     * operation on the files in each folder of the tree.
     *
     * @param starteamFolder the StarTeam folder from which files to be
     *                       checked out
     * @param targetFolder the local mapping of rootStarteamFolder
     * @exception BuildException if any error occurs
     */
    protected void visit(Folder starteamFolder, java.io.File targetFolder)
            throws BuildException {
        try {


            if (null != getRootLocalFolder()) {
                starteamFolder.setAlternatePathFragment(
                    targetFolder.getAbsolutePath());
            }

            if (!targetFolder.exists()) {
                if (!this.isUsingRevisionLabel()) {
                    if (this.createDirs) {
                        if (targetFolder.mkdirs()) {
                            log("Creating folder: " + targetFolder, Project.MSG_VERBOSE);
                        } else {
                            throw new BuildException(
                                "Failed to create local folder " + targetFolder);
                        }
                    }
                }
            }


            Folder[] foldersList = starteamFolder.getSubFolders();
            Item[] filesList = starteamFolder.getItems(getTypeNames().FILE);

            if (this.isUsingRevisionLabel()) {

                // prune away any files not belonging to the revision label
                // this is one ugly API from Starteam SDK

                Hashtable labelItems = new Hashtable(filesList.length);
                int s = filesList.length;
                int[] ids = new int[s];
                for (int i = 0; i < s; i++) {
                    ids[i] = filesList[i].getItemID();
                    labelItems.put(new Integer(ids[i]), new Integer(i));
                }
                int[] foundIds = getLabelInUse().getLabeledItemIDs(ids);
                s = foundIds.length;
                Item[] labeledFiles = new Item[s];
                for (int i = 0; i < s; i++) {
                    Integer id = new Integer(foundIds[i]);
                    labeledFiles[i] =
                        filesList[((Integer) labelItems.get(id)).intValue()];
                }
                filesList = labeledFiles;
            }


            // note, it's important to scan the items BEFORE we make the
            // Unmatched file map because that creates a bunch of NEW
            // folders and files (unattached to repository) and we
            // don't want to include those in our traversal.

            UnmatchedFileMap ufm =
                new CheckoutMap().
                    init(targetFolder.getAbsoluteFile(), starteamFolder);



            for (int i = 0; i < foldersList.length; i++) {
                Folder stFolder = foldersList[i];

                java.io.File subfolder =
                     new java.io.File(targetFolder, stFolder.getName());

                 ufm.removeControlledItem(subfolder);

                 if (isRecursive()) {
                         visit(stFolder, subfolder);
                     }
                 }

            for (int i = 0; i < filesList.length; i++) {
                com.starbase.starteam.File stFile =
                    (com.starbase.starteam.File) filesList[i];
                processFile(stFile, targetFolder);

                ufm.removeControlledItem(
                    new java.io.File(targetFolder, stFile.getName()));
            }
            if (this.deleteUncontrolled) {
                ufm.processUncontrolledItems();
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }


    /**
     * provides a string showing from and to full paths for logging
     *
     * @param remotefile the Star Team file being processed.
     *
     * @return a string showing from and to full paths
     */
    private String describeCheckout(com.starbase.starteam.File remotefile,
                                    java.io.File localFile) {
        StringBuffer sb = new StringBuffer();
        sb.append(getFullRepositoryPath(remotefile))
          .append(" --> ");
        if (null == localFile) {
            sb.append(remotefile.getFullName());
        } else {
            sb.append(localFile);
        }
        return sb.toString();
    }
    private String describeCheckout(com.starbase.starteam.File remotefile) {
        return describeCheckout(remotefile, null);
    }
    /**
     * Processes (checks out) <code>stFiles</code>files from StarTeam folder.
     *
     * @param eachFile repository file to process
     * @param targetFolder a java.io.File (Folder) to work
     * @throws IOException when StarTeam API fails to work with files
     */
    private void processFile(com.starbase.starteam.File eachFile,
                             File targetFolder)
    throws IOException {
        String filename = eachFile.getName();

        java.io.File localFile = new java.io.File(targetFolder, filename);

        // If the file doesn't pass the include/exclude tests, skip it.
        if (!shouldProcess(filename)) {
            log("Excluding " + getFullRepositoryPath(eachFile),
                Project.MSG_INFO);
                return;
        }

        if (this.isUsingRevisionLabel()) {
            if (!targetFolder.exists()) {
                if (targetFolder.mkdirs()) {
                    log("Creating folder: " + targetFolder, Project.MSG_VERBOSE);
                } else {
                    throw new BuildException(
                        "Failed to create local folder " + targetFolder);
                }
            }
            boolean success = eachFile.checkoutByLabelID(
                localFile,
                getIDofLabelInUse(),
                this.lockStatus,
                !this.useRepositoryTimeStamp,
                true,
                false);
            if (success) {
                log("Checked out " + describeCheckout(eachFile, localFile), Project.MSG_VERBOSE);
            }
        } else {
            boolean checkout = true;

            // Just a note: StarTeam has a status for NEW which implies
            // that there is an item  on your local machine that is not
            // in the repository.  These are the items that show up as
            // NOT IN VIEW in the Starteam GUI.
            // One would think that we would want to perhaps checkin the
            // NEW items (not in all cases! - Steve Cohen 15 Dec 2001)
            // Unfortunately, the sdk doesn't really work, and we can't
            // actually see  anything with a status of NEW. That is why
            // we can just check out  everything here without worrying
            // about losing anything.

            int fileStatus = (eachFile.getStatus());

            // We try to update the status once to give StarTeam
            // another chance.

            if (fileStatus == Status.MERGE
                || fileStatus == Status.UNKNOWN) {
                eachFile.updateStatus(true, true);
                fileStatus = (eachFile.getStatus());
            }

            log(eachFile.toString() + " has status of "
                + Status.name(fileStatus), Project.MSG_DEBUG);


            switch (fileStatus) {
            case Status.OUTOFDATE:
            case Status.MISSING:
                log("Checking out: " + describeCheckout(eachFile), Project.MSG_VERBOSE);
                break;
            default:
                if (isForced()) {
                    log("Forced checkout of "
                        + describeCheckout(eachFile)
                        + " over status " + Status.name(fileStatus), Project.MSG_VERBOSE);
                } else {
                    log("Skipping: " + getFullRepositoryPath(eachFile)
                        + " - status: " + Status.name(fileStatus), Project.MSG_VERBOSE);
                    checkout = false;
                }
            }

            if (checkout) {
                if (!targetFolder.exists()) {
                    if (targetFolder.mkdirs()) {
                        log("Creating folder: " + targetFolder, Project.MSG_VERBOSE);
                    } else {
                        throw new BuildException(
                            "Failed to create local folder " + targetFolder);
                    }
                }
                eachFile.checkout(this.lockStatus,
                                 !this.useRepositoryTimeStamp, this.convertEOL, true);
            }
        }
    }
    /**
     * handles the deletion of uncontrolled items
     */
    private class CheckoutMap extends UnmatchedFileMap {
        protected boolean isActive() {
            return StarTeamCheckout.this.deleteUncontrolled;
        }

        /**
         * override of the base class init.  It can be much simpler, since
         * the action to be taken is simply to delete the local files.  No
         * further interaction with the repository is necessary.
         *
         * @param localFolder
         *        the local folder from which the mappings will be made.
         * @param remoteFolder
         *        not used in this implementation
         */
        UnmatchedFileMap init(java.io.File localFolder, Folder remoteFolder) {
            if (!localFolder.exists()) {
                return this;
            }

            String[] localFiles = localFolder.list();

            for (int i = 0; i < localFiles.length; i++) {
                java.io.File localFile =
                    new java.io.File(localFolder, localFiles[i]).getAbsoluteFile();

                log("adding " + localFile + " to UnmatchedFileMap",
                    Project.MSG_DEBUG);

                if (localFile.isDirectory()) {
                    this.put(localFile, "");
                } else {
                    this.put(localFile, "");
                }
            }
            return this;
        }



        /**
         * deletes uncontrolled items from the local tree.  It is assumed
         * that this method will not be called until all the items in the
         * corresponding folder have been processed, and that the internal map
         * will contain only uncontrolled items.
         */
        void processUncontrolledItems() throws BuildException {
            if (this.isActive()) {
                Enumeration e = this.keys();
                while (e.hasMoreElements()) {
                    java.io.File local = (java.io.File) e.nextElement();
                    delete(local);
                }
            }
        }

        /**
         * deletes all files and if the file is a folder recursively deletes
         * everything in it.
         *
         * @param local  The local file or folder to be deleted.
         */
        void delete(java.io.File local) {
            // once we find a folder that isn't in the repository,
            // anything below it can be deleted.
            if (local.isDirectory() && isRecursive()) {
                String[] contents = local.list();
                for (int i = 0; i < contents.length; i++) {
                    java.io.File file = new java.io.File(local, contents[i]);
                    delete(file);
                }
            }
            local.delete();
            log("Deleted uncontrolled item " + local.getAbsolutePath());
        }
    }
}
