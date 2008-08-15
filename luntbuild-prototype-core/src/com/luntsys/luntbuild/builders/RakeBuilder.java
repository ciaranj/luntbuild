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
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.lb20.RakeBuilderFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Rake builder implementation.
 * @author lubosp
 */
public class RakeBuilder extends Builder {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization.
     */
    static final long serialVersionUID = 1L;

    public static final String COMMAND = "command";
    public static final String BUILDSCRIPTPATH = "buildscriptpath";
    public static final String TARGETS = "targets";
    public static final String BUILDPROPERTIES = "buildproperties";
    public static final String DISPLAYNAME = "displayname";

    private String type = "rakebuilder";

    /**
     * Extra properties transfered into the build script.
     */
    private static final String buildProperties =
            "buildVersion=\"${build.version}\"\n" +
            "artifactsDir=\"${build.artifactsDir}\"\n" +
            "buildDate=\"${build.startDate}\"\n" +
            "junitHtmlReportDir=\"${build.junitHtmlReportDir}\"";

    private Map properties;


    public RakeBuilder() {
        setBuildSuccessCondition("result==0 and !logContainsLine(\"Command failed with status\")");
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof RakeBuilder) {
            if (getName().equals(((RakeBuilder)obj).getName()))
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

    public String getBuildScriptPath() {
    	Map props = getProperties();
    	String buildScriptPath = (String)props.get(BUILDSCRIPTPATH);
    	return (buildScriptPath == null) ? "" : buildScriptPath;
    }

    public String getTargets() {
    	Map props = getProperties();
    	String targets = (String)props.get(TARGETS);
    	return (targets == null) ? "" : targets;
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

	@Override
	public Map getProperties() {
		if (this.properties == null) {
			this.properties = new HashMap();
	        this.properties.put(BUILDPROPERTIES, new StringProperty(BUILDPROPERTIES, buildProperties));
	        if (System.getProperty("os.name").startsWith("Windows")) {
	        	this.properties.put(COMMAND, "C:\\rake\\bin\\rake.bat ");
	        } else {
	        	this.properties.put(COMMAND, "/usr/local/bin/rake ");
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

    public void validate() {
        super.validate();
        try {
            Luntbuild.validateExpression(getCommand());
        } catch (ValidationException e) {
            throw new ValidationException("Invalid command to run Rake: " + e.getMessage());
        }
        if (!Luntbuild.isEmpty(getTargets())) {
            try {
                Luntbuild.validateExpression(getTargets());
            } catch (ValidationException e) {
                throw new ValidationException("Invalid targets: " + e.getMessage());
            }
        }
        if (!Luntbuild.isEmpty(getBuildProperties())) {
            BufferedReader reader = new BufferedReader(new StringReader(getBuildProperties()));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals(""))
                        continue;
                    String name = Luntbuild.getAssignmentName(line);
                    String value = Luntbuild.getAssignmentValue(line);
                    if (Luntbuild.isEmpty(name) || Luntbuild.isEmpty(value))
                        throw new ValidationException("Invalid build property definition: " + line);
                }
            } catch (IOException e) {
                // ignores
            }
        }
    }

    /**
     * Construct command to run Rake
     *
     * @return command to run Rake
     */
    public String constructBuildCmd(Build build) throws IOException {
        String rakeCmd = getCommand();
        rakeCmd = rakeCmd.replace('\n', ' ');
        rakeCmd = rakeCmd.replace('\r', ' ');

        if (!Luntbuild.isEmpty(getBuildProperties())) {
            BufferedReader reader = new BufferedReader(new StringReader(getBuildProperties()));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals(""))
                        continue;
                    String name = Luntbuild.getAssignmentName(line);
                    String value = Luntbuild.getAssignmentValue(line);
                    if (!Luntbuild.isEmpty(name) && !Luntbuild.isEmpty(value)) {
                        rakeCmd += " " + name + "=" + value;
                    }
                }
            } catch (IOException e) {
                // ignores
            }
        }

        // set rake log level based on project's log level if log level does not been explicitely
        // specified in rake command
        if (!rakeCmd.matches(".*\\s(-q|--quiet)($|\\s.*)") &&
                !rakeCmd.matches(".*\\s(-v|-verbose)($|\\s.*)")) {
            if (build.getSchedule().getProject().getLogLevel() == Constants.LOG_LEVEL_BRIEF)
                rakeCmd += " -q";
            else if (build.getSchedule().getProject().getLogLevel() == Constants.LOG_LEVEL_VERBOSE)
                rakeCmd += " -v";
        }

        if (getBuildScriptPath() != null && getBuildScriptPath().length() > 0) {
            String buildScriptAbsolutePath = build.getSchedule().resolveAbsolutePath(getBuildScriptPath());
            rakeCmd += "  --rakefile \"" + buildScriptAbsolutePath + "\"";
        }
        if (!Luntbuild.isEmpty(getTargets()))
            rakeCmd += " " + getTargets();

        return rakeCmd;
    }

    public String constructBuildCmdDir(Build build) {
        String buildScriptAbsolutePath = build.getSchedule().resolveAbsolutePath(getBuildScriptPath());
        return new File(buildScriptAbsolutePath).getParent();
    }

    public com.luntsys.luntbuild.facades.lb20.BuilderFacade constructFacade() {
        return new RakeBuilderFacade();
    }

    public void loadFromFacade(com.luntsys.luntbuild.facades.lb20.BuilderFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb20.RakeBuilderFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb20.RakeBuilderFacade rakeBuilderFacade = (com.luntsys.luntbuild.facades.lb20.RakeBuilderFacade) facade;
        copyProperties(rakeBuilderFacade.getProperties(), getProperties());
    }

    public void saveToFacade(com.luntsys.luntbuild.facades.lb20.BuilderFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb20.RakeBuilderFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb20.RakeBuilderFacade rakeBuilderFacade = (RakeBuilderFacade) facade;
        copyProperties(getProperties(), rakeBuilderFacade.getProperties());
    }
}
