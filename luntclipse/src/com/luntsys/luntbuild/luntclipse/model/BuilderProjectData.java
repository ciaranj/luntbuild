package com.luntsys.luntbuild.luntclipse.model;

/**
 * Builder project data
 *
 * @author Lubos Pochman
 *
 */
public class BuilderProjectData {

    private String name = null;
    private int type = -1;
    private String command = null;
    private String scriptPath = null;
    private String tragets = null;
    private String properties = null;
    private String envVars = null;
    private String condition = null;
    private boolean waitForFinish = false;


    /**
     * @return Returns the command.
     */
    public final String getCommand() {
        return (this.command == null) ? "" : this.command;
    }
    /**
     * @param command The command to set.
     */
    public final void setCommand(String command) {
        this.command = command;
    }
    /**
     * @return Returns the condition.
     */
    public final String getCondition() {
        return (this.condition == null) ? "" : this.condition;
    }
    /**
     * @param condition The condition to set.
     */
    public final void setCondition(String condition) {
        this.condition = condition;
    }
    /**
     * @return Returns the envVars.
     */
    public final String getEnvVars() {
        return (this.envVars == null) ? "" : this.envVars;
    }
    /**
     * @param envVars The envVars to set.
     */
    public final void setEnvVars(String envVars) {
        this.envVars = envVars;
    }
    /**
     * @return Returns the name.
     */
    public final String getName() {
        return (this.name == null) ? "" : this.name;
    }
    /**
     * @param name The name to set.
     */
    public final void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the properties.
     */
    public final String getProperties() {
        return (this.properties == null) ? "" : this.properties;
    }
    /**
     * @param properties The properties to set.
     */
    public final void setProperties(String properties) {
        this.properties = properties;
    }
    /**
     * @return Returns the scriptPath.
     */
    public final String getScriptPath() {
        return (this.scriptPath == null) ? "" :  this.scriptPath;
    }
    /**
     * @param scriptPath The scriptPath to set.
     */
    public final void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }
    /**
     * @return Returns the tragets.
     */
    public final String getTragets() {
        return (this.tragets == null) ? "" : this.tragets;
    }
    /**
     * @param tragets The tragets to set.
     */
    public final void setTragets(String tragets) {
        this.tragets = tragets;
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
     * @return Returns the waitForFinish.
     */
    public final boolean getWaitForFinish() {
        return this.waitForFinish;
    }
    /**
     * @param waitForFinish The waitForFinish to set.
     */
    public final void setWaitForFinish(boolean waitForFinish) {
        this.waitForFinish = waitForFinish;
    }


}
