package com.luntsys.luntbuild.luntclipse.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Vcs Project Data
 *
 * @author Lubos Pochman
 *
 */
public class VcsProjectData {

    private int type = -1;
    private String user = null;
    private String password = null;
    private String quietPeriod = null;
    private String clearcaseViewStgloc = null;
    private String clearcaseViewStorage = null;
    private String clearcaseConfig = null;
    private String clearcaseModConfig = null;
    private String clearcaseViewOptions = null;
    private String exePath = null;
    private String clearcaseVobTag = null;
    private String clearcaseStreamName = null;
    private String clearcaseBuildTarget = null;
    private String cvsRoot = null;
    private int cvsCygwin = -1;
    private int cvsLogCommand = -1;
    private int cvsHistroy = -1;
    private String filesystemSource = null;
    private String perforcePort = null;
    private int perforceLineEnd = -1;
    private String starTeamLocation = null;
    private int starTeamConvertEol = -1;
    private String svnUrl = null;
    private String svnTrunkDir = null;
    private String svnBranchesDir = null;
    private String svnTagsDir = null;
    private String sourcesafePath = null;
    private String sourcesafeDatetime = null;
    private List modules = null;

    /**
     * @return Returns the password.
     */
    public final String getPassword() {
        return (this.password == null) ? "" : this.password;
    }
    /**
     * @param password The password to set.
     */
    public final void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return Returns the type.
     */
    public final int getType() {
        return this.type;
    }
    /**
     * @param type The type to set.
     */
    public final void setType(int type) {
        this.type = type;
    }
    /**
     * @return Returns the user.
     */
    public final String getUser() {
        return (this.user == null) ? "" : this.user;
    }
    /**
     * @param user The user to set.
     */
    public final void setUser(String user) {
        this.user = user;
    }
    /**
     * @return Returns the clearcaseConfig.
     */
    public final String getClearcaseConfig() {
        return (this.clearcaseConfig == null) ? "" : this.clearcaseConfig;
    }
    /**
     * @param clearcaseConfig The clearcaseConfig to set.
     */
    public final void setClearcaseConfig(String clearcaseConfig) {
        this.clearcaseConfig = clearcaseConfig;
    }
    /**
     * @return Returns the clearcaseExePath.
     */
    public final String getExePath() {
        return (this.exePath == null) ? "" : this.exePath;
    }
    /**
     * @param clearcaseExePath The clearcaseExePath to set.
     */
    public final void setExePath(String exePath) {
        this.exePath = exePath;
    }
    /**
     * @return Returns the clearcaseModConfig.
     */
    public final String getClearcaseModConfig() {
        return (this.clearcaseModConfig == null) ? "" : this.clearcaseModConfig;
    }
    /**
     * @param clearcaseModConfig The clearcaseModConfig to set.
     */
    public final void setClearcaseModConfig(String clearcaseModConfig) {
        this.clearcaseModConfig = clearcaseModConfig;
    }
    /**
     * @return Returns the clearcaseViewOptions.
     */
    public final String getClearcaseViewOptions() {
        return (this.clearcaseViewOptions == null) ? "" : this.clearcaseViewOptions;
    }
    /**
     * @param clearcaseViewOptions The clearcaseViewOptions to set.
     */
    public final void setClearcaseViewOptions(String clearcaseViewOptions) {
        this.clearcaseViewOptions = clearcaseViewOptions;
    }
    /**
     * @return Returns the clearcaseViewStgloc.
     */
    public final String getClearcaseViewStgloc() {
        return (this.clearcaseViewStgloc == null) ? "" : this.clearcaseViewStgloc;
    }
    /**
     * @param clearcaseViewStgloc The clearcaseViewStgloc to set.
     */
    public final void setClearcaseViewStgloc(String clearcaseViewStgloc) {
        this.clearcaseViewStgloc = clearcaseViewStgloc;
    }
    /**
     * @return Returns the clearcaseViewStorage.
     */
    public final String getClearcaseViewStorage() {
        return (this.clearcaseViewStorage == null) ? "" : this.clearcaseViewStorage;
    }
    /**
     * @param clearcaseViewStorage The clearcaseViewStorage to set.
     */
    public final void setClearcaseViewStorage(String clearcaseViewStorage) {
        this.clearcaseViewStorage = clearcaseViewStorage;
    }
    /**
     * @return Returns the quietPeriod.
     */
    public final String getQuietPeriod() {
        return (this.quietPeriod == null) ? "" : this.quietPeriod;
    }
    /**
     * @param quietPeriod The quietPeriod to set.
     */
    public final void setQuietPeriod(String quietPeriod) {
        this.quietPeriod = quietPeriod;
    }
    /**
     * @return Returns the clearcaseBuildTarget.
     */
    public final String getClearcaseBuildTarget() {
        return (this.clearcaseBuildTarget == null) ? "" : this.clearcaseBuildTarget;
    }
    /**
     * @param clearcaseBuildTarget The clearcaseBuildTarget to set.
     */
    public final void setClearcaseBuildTarget(String clearcaseBuildTarget) {
        this.clearcaseBuildTarget = clearcaseBuildTarget;
    }
    /**
     * @return Returns the clearcaseStreamName.
     */
    public final String getClearcaseStreamName() {
        return (this.clearcaseStreamName == null) ? "" : this.clearcaseStreamName;
    }
    /**
     * @param clearcaseStreamName The clearcaseStreamName to set.
     */
    public final void setClearcaseStreamName(String clearcaseStreamName) {
        this.clearcaseStreamName = clearcaseStreamName;
    }
    /**
     * @return Returns the clearcaseVobTag.
     */
    public final String getClearcaseVobTag() {
        return (this.clearcaseVobTag == null) ? "" : this.clearcaseVobTag;
    }
    /**
     * @param clearcaseVobTag The clearcaseVobTag to set.
     */
    public final void setClearcaseVobTag(String clearcaseVobTag) {
        this.clearcaseVobTag = clearcaseVobTag;
    }
    /**
     * @return Returns the cvsRoot.
     */
    public final String getCvsRoot() {
        return (this.cvsRoot == null) ? "" : this.cvsRoot;
    }
    /**
     * @param cvsRoot The cvsRoot to set.
     */
    public final void setCvsRoot(String cvsRoot) {
        this.cvsRoot = cvsRoot;
    }
    /**
     * @return Returns the cvsCygwin.
     */
    public final int getCvsCygwin() {
        return this.cvsCygwin;
    }
    /**
     * @param cvsCygwin The cvsCygwin to set.
     */
    public final void setCvsCygwin(int cvsCygwin) {
        this.cvsCygwin = cvsCygwin;
    }
    /**
     * @return Returns the cvsHistroy.
     */
    public final int getCvsHistroy() {
        return this.cvsHistroy;
    }
    /**
     * @param cvsHistroy The cvsHistroy to set.
     */
    public final void setCvsHistroy(int cvsHistroy) {
        this.cvsHistroy = cvsHistroy;
    }
    /**
     * @return Returns the cvsLogCommand.
     */
    public final int getCvsLogCommand() {
        return this.cvsLogCommand;
    }
    /**
     * @param cvsLogCommand The cvsLogCommand to set.
     */
    public final void setCvsLogCommand(int cvsLogCommand) {
        this.cvsLogCommand = cvsLogCommand;
    }
    /**
     * @return Returns the filesystemSource.
     */
    public final String getFilesystemSource() {
        return (this.filesystemSource == null) ? "" : this.filesystemSource;
    }
    /**
     * @param filesystemSource The filesystemSource to set.
     */
    public final void setFilesystemSource(String filesystemSource) {
        this.filesystemSource = filesystemSource;
    }
    /**
     * @return Returns the perforceLineEnd.
     */
    public final int getPerforceLineEnd() {
        return this.perforceLineEnd;
    }
    /**
     * @param perforceLineEnd The perforceLineEnd to set.
     */
    public final void setPerforceLineEnd(int perforceLineEnd) {
        this.perforceLineEnd = perforceLineEnd;
    }
    /**
     * @return Returns the perforcePort.
     */
    public final String getPerforcePort() {
        return (this.perforcePort == null) ? "" : this.perforcePort;
    }
    /**
     * @param perforcePort The perforcePort to set.
     */
    public final void setPerforcePort(String perforcePort) {
        this.perforcePort = perforcePort;
    }
    /**
     * @return Returns the starTeamConvertEol.
     */
    public final int getStarTeamConvertEol() {
        return this.starTeamConvertEol;
    }
    /**
     * @param starTeamConvertEol The starTeamConvertEol to set.
     */
    public final void setStarTeamConvertEol(int starTeamConvertEol) {
        this.starTeamConvertEol = starTeamConvertEol;
    }
    /**
     * @return Returns the starTeamLocation.
     */
    public final String getStarTeamLocation() {
        return (this.starTeamLocation == null) ? "" : this.starTeamLocation;
    }
    /**
     * @param starTeamLocation The starTeamLocation to set.
     */
    public final void setStarTeamLocation(String starTeamLocation) {
        this.starTeamLocation = starTeamLocation;
    }
    /**
     * @return Returns the svnBranchesDir.
     */
    public final String getSvnBranchesDir() {
        return (this.svnBranchesDir == null) ? "" : this.svnBranchesDir;
    }
    /**
     * @param svnBranchesDir The svnBranchesDir to set.
     */
    public final void setSvnBranchesDir(String svnBranchesDir) {
        this.svnBranchesDir = svnBranchesDir;
    }
    /**
     * @return Returns the svnTagsDir.
     */
    public final String getSvnTagsDir() {
        return (this.svnTagsDir == null) ? "" : this.svnTagsDir;
    }
    /**
     * @param svnTagsDir The svnTagsDir to set.
     */
    public final void setSvnTagsDir(String svnTagsDir) {
        this.svnTagsDir = svnTagsDir;
    }
    /**
     * @return Returns the svnTrunkDir.
     */
    public final String getSvnTrunkDir() {
        return (this.svnTrunkDir == null) ? "" : this.svnTrunkDir;
    }
    /**
     * @param svnTrunkDir The svnTrunkDir to set.
     */
    public final void setSvnTrunkDir(String svnTrunkDir) {
        this.svnTrunkDir = svnTrunkDir;
    }
    /**
     * @return Returns the svnUrl.
     */
    public final String getSvnUrl() {
        return (this.svnUrl == null) ? "" : this.svnUrl;
    }
    /**
     * @param svnUrl The svnUrl to set.
     */
    public final void setSvnUrl(String svnUrl) {
        this.svnUrl = svnUrl;
    }
    /**
     * @return Returns the sourcesafeDatetime.
     */
    public final String getSourcesafeDatetime() {
        return (this.sourcesafeDatetime == null) ? "" : this.sourcesafeDatetime;
    }
    /**
     * @param sourcesafeDatetime The sourcesafeDatetime to set.
     */
    public final void setSourcesafeDatetime(String sourcesafeDatetime) {
        this.sourcesafeDatetime = sourcesafeDatetime;
    }
    /**
     * @return Returns the sourcesafePath.
     */
    public final String getSourcesafePath() {
        return (this.sourcesafePath == null) ? "" : this.sourcesafePath;
    }
    /**
     * @param sourcesafePath The sourcesafePath to set.
     */
    public final void setSourcesafePath(String sourcesafePath) {
        this.sourcesafePath = sourcesafePath;
    }
    /**
     * @return Returns the modules.
     */
    public final List getModules() {
        if (this.modules == null) return new ArrayList();
        return this.modules;
    }
    /**
     * @param modules The modules to set.
     */
    public final void setModules(List modules) {
        this.modules = modules;
    }

}
