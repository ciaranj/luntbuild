package com.luntsys.luntbuild.luntclipse.model;

/**
 * Luntbuild connection data
 *
 * @author Lubos Pochman
 *
 */
public class ConnectionData {
    private String name = null;
    private String url = null;
    private String user = null;
    private String password = null;
    private String version = null;
    private String refreshTime = null;

    /**
     * @return Returns the name.
     */
    public final String getName() {
        return this.name;
    }
    /**
     * @param name The name to set.
     */
    public final void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the password.
     */
    public final String getPassword() {
        return this.password;
    }
    /**
     * @param password The password to set.
     */
    public final void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return Returns the url.
     */
    public final String getUrl() {
        return this.url;
    }
    /**
     * @param url The url to set.
     */
    public final void setUrl(String url) {
        this.url = url;
    }
    /**
     * @return Returns the user.
     */
    public final String getUser() {
        return this.user;
    }
    /**
     * @param user The user to set.
     */
    public final void setUser(String user) {
        this.user = user;
    }
    /**
     * @return Returns the version.
     */
    public final String getVersion() {
        return this.version;
    }
    /**
     * @param version The version to set.
     */
    public final void setVersion(String version) {
        this.version = version;
    }
    /**
     * @return Returns the refreshTime.
     */
    public final String getRefreshTime() {
        return this.refreshTime;
    }
    /**
     * @param refreshTime The refreshTime to set.
     */
    public final void setRefreshTime(String refreshTime) {
        this.refreshTime = refreshTime;
    }


}
