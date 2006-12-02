package com.luntsys.luntbuild.luntclipse.model;

/**
 * Perforce Module Data
 *
 * @author Lubos Pochman
 *
 */
public class PerforceModuleData {
    private String depotPath = null;
    private String label = null;
    private String clientPath = null;
    /**
     * @return Returns the clientPath.
     */
    public final String getClientPath() {
        return (this.clientPath == null) ? "" : this.clientPath;
    }
    /**
     * @param clientPath The clientPath to set.
     */
    public final void setClientPath(String clientPath) {
        this.clientPath = clientPath;
    }
    /**
     * @return Returns the depotPath.
     */
    public final String getDepotPath() {
        return (this.depotPath == null) ? "" : this.depotPath;
    }
    /**
     * @param depotPath The depotPath to set.
     */
    public final void setDepotPath(String depotPath) {
        this.depotPath = depotPath;
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
}
