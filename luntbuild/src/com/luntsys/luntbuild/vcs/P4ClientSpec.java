/*
 * Copyright luntsys (c) 2004-2005,
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.luntsys.luntbuild.vcs;

import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.perforce.P4Base;
import org.apache.tools.ant.taskdefs.optional.perforce.P4Handler;
import org.apache.tools.ant.taskdefs.optional.perforce.P4HandlerAdapter;


/**
 * P4ClientSpec manages <a href="http://www.perforce.com">Perforce</a> client specification.
 * It supports both client spec read and write. It is loosely based on original P4Client class
 * from Apache Ant distribution, which doesn't seem to be supported any more (in Ant 1.6.2).
 * It uses Ant optional perforce classes. 
 *
 * @author <a href="mailto:pochmans@gmail.com">Lubos Pochman</a><br>
 *
 * Referenced classes of package org.apache.tools.ant.taskdefs.optional.perforce:
 *            P4Base, P4Handler, P4HandlerAdapter
 */
public class P4ClientSpec extends P4Base
{

    // P4 Client Spec data
    protected String clientValue = null;
    protected String ownerValue = null;
    protected String hostValue = null;
    protected String rootValue = null;
    protected String optionsValue = null;
    protected String lineEndValue = null;
    protected ArrayList viewValue = new ArrayList();
    protected ArrayList descValue = new ArrayList();

    /** Returns client spec name.
     * @return client spec name
     */
    public String getClientValue() {
        return this.clientValue;
    }

    /** Sets client spec name.
     * @param clientValue client spec name
     */
    public void setClientValue(String clientValue) {
        this.clientValue = clientValue;
    }

    /** Returns client spec owner.
     * @return client spec owner
     */
    public String getOwnerValue() {
        return this.ownerValue;
    }

    /** Sets client spec owner.
     * @param ownerValue client spec owner
     */
    public void setOwnerValue(String ownerValue) {
        this.ownerValue = ownerValue;
    }

    /** Returns client spec host.
     * @return client spec host
     */
    public String getHostValue() {
        return this.hostValue;
    }

    /** Sets client spec host.
     * @param hostValue client spec host
     */
    public void setHostValue(String hostValue) {
        this.hostValue = hostValue;
    }

    /** Returns client spec description is String form (lines separated by \n\t)
     * @return client spec description
     */
    public String getDescValue() {
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < this.descValue.size(); i++)
            buf.append("\t" + (String)this.descValue.get(i) + "\n");
        return buf.toString();
    }

    /** Adds client spec description line
     * @param desc client spec description line
     */
    public void addDescValue(String desc) {
        String val = desc.trim();
        for(int i = 0; i < this.descValue.size(); i++)
            if (((String)this.descValue.get(i)).equals(val)) return;
        this.descValue.add(val);
    }

    /** Sets client spec description.
     * @param desc client spec description
     */
    public void setDescValue(ArrayList desc) {
        this.descValue = desc;
    }
    
    /** Returns client spec root directory.
     * @return client spec root directory
     */
    public String getRootValue() {
        return this.rootValue;
    }

    /** Sets client spec root directory.
     * @param rootValue client spec root directory
     */
    public void setRootValue(String rootValue) {
        this.rootValue = rootValue;
    }

    /** Returns client spec options.
     * @return client spec options
     */
    public String getOptionsValue() {
        return this.optionsValue;
    }

    /** Sets client spec options.
     * @param optionsValue client spec options
     */
    public void setOptionsValue(String optionsValue) {
        this.optionsValue = optionsValue;
    }

    /** Returns client spec LineEnd.
     * @return client spec LineEnd
     */
    public String getLineEndValue() {
        return this.lineEndValue;
    }

    /** Sets client spec LineEnd.
     * @param lineEndValue client spec LineEnd
     */
    public void setLineEndValue(String lineEndValue) {
        this.lineEndValue = lineEndValue;
    }

    /** Returns client spec view value as a String (lines separated by \t\n)
     * @return client spec view value
     */
    public String getViewValue() {
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < this.viewValue.size(); i++)
            buf.append("\t" + (String)this.viewValue.get(i) + "\n");
        return buf.toString();
    }

    /** Adds a line to the client spec view value.
     * @param view line to be added to the client spec view value
     */
    public void addViewValue(String view) {
        String val = view.trim();
        for(int i = 0; i < this.viewValue.size(); i++)
            if (((String)this.viewValue.get(i)).equals(val)) return;
        this.viewValue.add(val);
    }

    /** Sets client spec view value.
     * @param viewValue client spec view value
     */
    public void setViewValue(ArrayList viewValue) {
        this.viewValue = viewValue;
    }
    
    /**
     * Reads perforce client specification defined by set clientValue and populates object data.
     * 
     * @throws BuildException
     *
     * (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void read()
        throws BuildException {
        
        if(this.clientValue == null || this.clientValue.trim().length() == 0) {
            log("Need to specify the client spec to read!", Project.MSG_ERR);
            return;
        }
        
        log("P4Client read:", Project.MSG_INFO);
        P4Handler handler = new P4HandlerAdapter() {

            private boolean inDescBlock = false;
            private boolean inViewBlock = false;
            
            public void processStdout(String line) {
                
                log(line, Project.MSG_INFO);
                boolean endBlock = !util.match("/^\\s/", line);
                if(this.inDescBlock && endBlock) {
                    this.inDescBlock = false;
                }
                if(this.inViewBlock && endBlock) {
                    inViewBlock = false;
                }
                if(this.inDescBlock) {
                    addDescValue(line.trim());
                } else if(this.inViewBlock) {
                    addViewValue(line.trim());
                } else if(util.match("/^Owner:/", line)) {
                    ownerValue = line.substring(6).trim();
                } else if(util.match("/^Host:/", line)) {
                    hostValue = line.substring(5).trim();
                } else if(util.match("/^Root:/", line)) {
                    rootValue = line.substring(5).trim();
                } else if(util.match("/^Options:/", line)) {
                    optionsValue = line.substring(8).trim();
                } else if(util.match("/^LineEnd:/", line)) {
                    lineEndValue = line.substring(8).trim();
                } else if(util.match("/^Description:/", line)) {
                    this.inDescBlock = true;
                    descValue = new ArrayList();
                } else if(util.match("/^View:/", line)) {
                    this.inViewBlock = true;
                    viewValue = new ArrayList();
                }
                
            }

            public void processStderr(String line)
            {
                log(line, Project.MSG_ERR);
            }

        };
        log("Read client spec output from perforce", Project.MSG_INFO);
        execP4Command("client -o " + this.clientValue, handler);
    }
    /**
     * Writes currently defined client specification into perforce.
     * 
     * @throws BuildException
     *
     * (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void write()
        throws BuildException {
        
        if(this.clientValue == null || this.clientValue.trim().length() == 0) {
            log("Need to specified client spec to read!", Project.MSG_ERR);
            return;
        }
        
        String clientSpec = buildClientSpec();
        log("Submit modified client spec back to perforce", Project.MSG_INFO);
        log("Modified client spec begin:\n" + clientSpec, Project.MSG_INFO);
        log("Modified client spec end.", 2);
        P4HandlerAdapter handler = new P4HandlerAdapter() {

            public void processStdout(String line)
            {
                log(line, Project.MSG_INFO);
            }

            public void processStderr(String line)
            {
                log(line, Project.MSG_ERR);
            }

        };
        
        handler.setOutput(clientSpec);
        execP4Command("client -i", handler);
    }
    
    private String buildClientSpec() {
        StringBuffer buf = new StringBuffer();
        
        buf.append("Client:\t" + this.clientValue);
        buf.append("\n");
        if (this.ownerValue != null) buf.append("Owner:\t" + this.ownerValue + "\n");
        if (this.hostValue != null) buf.append("Host:\t" + this.hostValue + "\n");
        if (this.rootValue != null) buf.append("Root:\t" + this.rootValue + "\n");
        if (this.optionsValue != null) buf.append("Options:\t" + this.optionsValue + "\n");
        if (this.lineEndValue != null) buf.append("LineEnd:\t" + this.lineEndValue + "\n");
        
        buf.append("Description:\n");
        if (this.descValue.size() > 0) buf.append(getDescValue());
        buf.append("\n");

        buf.append("View:\n");
        if (this.viewValue.size() > 0) buf.append(getViewValue());
        buf.append("\n");
               
        return buf.toString();
    }
}