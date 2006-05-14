package com.luntsys.luntbuild.luntclipse.model;

/**
 * Visual Sourcesafe Module Data
 *
 * @author Lubos Pochman
 *
 */
public class VisualSourcesafeModuleData {
    private String srcPath = null;
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
     * @return Returns the srcPath.
     */
    public final String getSrcPath() {
        return (this.srcPath == null) ? "" : this.srcPath;
    }
    /**
     * @param srcPath The srcPath to set.
     */
    public final void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }
}
