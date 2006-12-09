package com.luntsys.luntbuild.luntclipse.model;

/**
 * StarTeam Module Data
 *
 * @author Lubos Pochman
 *
 */
public class StarTeamModuleData {
    private String starteamView = null;
    private String srcPath = null;
    private String label = null;
    private String destPath = null;
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
    /**
     * @return Returns the starteamView.
     */
    public final String getStarteamView() {
        return (this.starteamView == null) ? "" : this.starteamView;
    }
    /**
     * @param starteamView The starteamView to set.
     */
    public final void setStarteamView(String starteamView) {
        this.starteamView = starteamView;
    }
}
