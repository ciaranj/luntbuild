package com.luntsys.luntbuild.luntclipse.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.luntsys.luntbuild.facades.lb12.UserFacade;
import com.luntsys.luntbuild.luntclipse.LuntclipseConstants;


/**
 * Basic Project data
 *
 * @author Lubos Pochman
 *
 */
public class BasicProjectData {

    private String name = null;
    private String description = null;
    private String[] admins = null;
    private String[] builders = null;
    private String[] viewers = null;
    private String[] notifyWith = null;
    private String[] notifyWho = null;
    private String variables = null;
    private int logLevel = 1;


    /**
     * @return Returns the description.
     */
    public final String getDescription() {
        return (this.description == null) ? "" : this.description;
    }

    /**
     * @param description The description to set.
     */
    public final void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the logLevel.
     */
    public final int getLogLevel() {
        return this.logLevel;
    }

    /**
     * @param logLevel The logLevel to set.
     */
    public final void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
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
     * @return Returns the variables.
     */
    public final String getVariables() {
        return (this.variables == null) ? "" : this.variables;
    }

    /**
     * @param variables The variables to set.
     */
    public final void setVariables(String variables) {
        this.variables = variables;
    }

    /**
     * @return Returns the admins.
     */
    public final String[] getAdmins() {
        if (this.admins == null) return new String[0];
        return this.admins;
    }

    /** Add admin users
     * @param names
     */
    public void setAdmins(String [] names) {
        this.admins = names;
    }

    /**
     * @return Returns the builders.
     */
    public final String[] getBuilders() {
        if (this.builders == null) return new String[0];
        return this.builders;
    }

    /** Add builder user
     * @param names
     */
    public void setBuilders(String [] names) {
        this.builders = names;
    }

    /**
     * @return Returns the notifyWho.
     */
    public final String[] getNotifyWho() {
        if (this.notifyWho == null) return new String[0];
        return this.notifyWho;
    }

    /** Add notify user
     * @param names
     */
    public void setNotifyWho(String[] names) {
        this.notifyWho = names;
    }

    /**
     * @return Returns the notifyWith.
     */
    public final String[] getNotifyWith() {
        if (this.notifyWith == null) return new String[0];
        return this.notifyWith;
    }

    /**
     * @return list of notifiers
     */
    public final List getNotifyWithList() {
        ArrayList list = new ArrayList();
        if (this.notifyWith == null) return list;

        for (int i = 0; i < this.notifyWith.length; i++) {
            String n = LuntclipseConstants.getNotifierClassName(this.notifyWith[i]);
            list.add(n);
        }
        return list;
    }

    /** Set notifiers array
     * @param notifyWith
     */
    public final void setNotifyWithList(List notifyWith) {
        ArrayList list = new ArrayList();
        if (notifyWith == null) {
            this.notifyWith = null;
            return;
        }
        for (Iterator iter = notifyWith.iterator(); iter.hasNext();) {
            String className = (String) iter.next();
            list.add(LuntclipseConstants.getNotifierName(className));
        }
        this.notifyWith = (String[])list.toArray(new String[list.size()]);
    }

    /** Add notify methods
     * @param methods
     */
    public void setNotifyWith(String[] methods) {
        this.notifyWith = methods;
    }

    /**
     * @return Returns the viewers.
     */
    public final String[] getViewers() {
        if (this.viewers == null) return new String[0];
        return this.viewers;
    }

    /** Add viewer user
     * @param names
     */
    public void setViewers(String[] names) {
        this.viewers = names;
    }

}
