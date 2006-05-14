package com.luntsys.luntbuild.luntclipse.model;

/**
 * Cvs Module Data
 *
 * @author Lubos Pochman
 *
 */
public class CvsModuleData {

    private String sourcePath = null;
    private String branch = null;
    private String label = null;
    private String destPath = null;
    /**
     * @return Returns the branch.
     */
    public final String getBranch() {
        return (this.branch == null) ? "" : this.branch;
    }
    /**
     * @param branch The branch to set.
     */
    public final void setBranch(String branch) {
        this.branch = branch;
    }
    /**
     * @return Returns the label.
     */
    public final String getLabel() {
        return (this.label == null) ? "" : this.label;
    }
    /**
     * @param label The label to set.
     */
    public final void setLabel(String label) {
        this.label = label;
    }
    /**
     * @return Returns the sourcePath.
     */
    public final String getSourcePath() {
        return (this.sourcePath == null) ? "" : this.sourcePath;
    }
    /**
     * @param sourcePath The sourcePath to set.
     */
    public final void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
    /**
     * @return Returns the destPath.
     */
    public final String getDestPath() {
        return (this.destPath == null) ? "" : this.destPath;
    }
    /**
     * @param destPath The destPath to set.
     */
    public final void setDestPath(String destPath) {
        this.destPath = destPath;
    }

}
