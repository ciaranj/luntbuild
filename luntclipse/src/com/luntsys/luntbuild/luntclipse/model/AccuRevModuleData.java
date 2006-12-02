package com.luntsys.luntbuild.luntclipse.model;

/**
 * AccuRev Module Data
 *
 * @author Lubos Pochman
 *
 */
public class AccuRevModuleData {
    private String depot = null;
    private String srcPath = null;
    private String backingStream = null;
    private String buildStream = null;
    private String label = null;
    /**
     * @return Returns the backingStream.
     */
    public final String getBackingStream() {
        return (this.backingStream == null) ? "" :  this.backingStream;
    }
    /**
     * @param backingStream The backingStream to set.
     */
    public final void setBackingStream(String backingStream) {
        this.backingStream = backingStream;
    }
    /**
     * @return Returns the buildStream.
     */
    public final String getBuildStream() {
        return (this.buildStream == null) ? "" : this.buildStream;
    }
    /**
     * @param buildStream The buildStream to set.
     */
    public final void setBuildStream(String buildStream) {
        this.buildStream = buildStream;
    }
    /**
     * @return Returns the depot.
     */
    public final String getDepot() {
        return (this.depot == null) ? "" : this.depot;
    }
    /**
     * @param depot The depot to set.
     */
    public final void setDepot(String depot) {
        this.depot = depot;
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
