/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-12-16
 * Time: 21:44:26
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
package com.luntsys.luntbuild.builders;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Builder;
import com.luntsys.luntbuild.db.StringProperty;
import com.luntsys.luntbuild.facades.lb20.BuilderFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implements for a command line builder
 */
public class CommandBuilder extends Builder {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1L;

    public static final String COMMAND = "command";
    public static final String DIRTORUN = "dirtorun";
    public static final String WAITFORFINISH = "waitforfinish";
    public static final String BUILDPROPERTIES = "buildproperties";
    public static final String DISPLAYNAME = "displayname";

    private String type = "cmdbuilder";

	private static final String buildProperties =
        " \"${build.version}\" \"${build.artifactsDir}\" \"${build.startDate}\" \"${build.junitHtmlReportDir}\"";

    private Map properties;


    public CommandBuilder() {
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CommandBuilder) {
            if (getName().equals(((CommandBuilder)obj).getName()))
                return true;
        }
        return false;
    }

    public int hashCode() {
        return getName().hashCode();
    }

	public String getCommand() {
    	Map props = getProperties();
    	String command = (String)props.get(COMMAND);
    	return (command == null) ? "" : command;
	}

	public String getDirToRunCmd() {
    	Map props = getProperties();
    	String dirToRunCmd = (String)props.get(DIRTORUN);
    	return (dirToRunCmd == null) ? "" : dirToRunCmd;
	}

	public String getWaitForFinish() {
    	Map props = getProperties();
    	String waitForFinish = (String)props.get(WAITFORFINISH);
    	return (waitForFinish == null) ? "" : waitForFinish;
	}

    public String getBuildProperties() {
    	Map props = getProperties();
    	String buildprops = (String)props.get(BUILDPROPERTIES);
    	return (buildprops == null) ? "" : buildprops;
    }

	public String getDisplayName() {
    	Map props = getProperties();
    	String displayName = (String)props.get(DISPLAYNAME);
    	return (displayName == null) ? "" : displayName;
	}

	public void validate() {
		super.validate();
		try {
			Luntbuild.validateExpression(getCommand());
		} catch (ValidationException e) {
			throw new ValidationException("Invalid command to run: " + e.getMessage());
		}
	}

	/**
	 * Construct command to run cmd
	 *
	 * @return command to run cmd
	 */
	public String constructBuildCmd(Build build) throws IOException {
		String buildCmd = getCommand() + " " + getBuildProperties();
		buildCmd = buildCmd.replace('\n', ' ');
		buildCmd = buildCmd.replace('\r', ' ');

		return buildCmd;
	}

	public String constructBuildCmdDir(Build build) {
		if (Luntbuild.isEmpty(getDirToRunCmd()))
			return build.getSchedule().getWorkDirRaw();
		else
			return build.getSchedule().resolveAbsolutePath(getDirToRunCmd());
	}

	public BuilderFacade constructFacade() {
		return new com.luntsys.luntbuild.facades.lb20.CommandBuilderFacade();
	}

	public void loadFromFacade(BuilderFacade facade) {
		if (!(facade instanceof com.luntsys.luntbuild.facades.lb20.CommandBuilderFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		com.luntsys.luntbuild.facades.lb20.CommandBuilderFacade cmdBuilderFacade =
			(com.luntsys.luntbuild.facades.lb20.CommandBuilderFacade) facade;
        copyProperties(cmdBuilderFacade.getProperties(), getProperties());
	}

	public void saveToFacade(BuilderFacade facade) {
		if (!(facade instanceof com.luntsys.luntbuild.facades.lb20.CommandBuilderFacade))
			throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
		com.luntsys.luntbuild.facades.lb20.CommandBuilderFacade cmdBuilderFacade =
			(com.luntsys.luntbuild.facades.lb20.CommandBuilderFacade) facade;
        copyProperties(getProperties(), cmdBuilderFacade.getProperties());
	}

	@Override
	public Map getProperties() {
		if (this.properties == null) {
			this.properties = new HashMap();
	        this.properties.put(BUILDPROPERTIES, new StringProperty(BUILDPROPERTIES, buildProperties));
	        if (System.getProperty("os.name").startsWith("Windows")) {
	        	this.properties.put(COMMAND, "\"${build.schedule.workingDir}\\build\\build.bat\"");
	        } else {
	        	this.properties.put(COMMAND, "\"${build.schedule.workingDir}/build/build\"");
	        }
		}
		return this.properties;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setProperties(Map m) {
		this.properties = m;
	}

}
