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
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.lb12.AntBuilderFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Ant builder implementation
 * @author robin shine
 */
public class AntBuilder extends Builder {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1L;

    /**
     * The command to run ant
     */
    private String command;

    /**
     * Path to ant build script
     */
    private String buildScriptPath;

    /**
     * Targets to build
     */
    private String targets;

    /**
     * Extra properties transfered into the build script
     */
    private String buildProperties =
            "buildVersion=\"${build.version}\"\n" +
            "artifactsDir=\"${build.artifactsDir}\"\n" +
            "buildDate=\"${build.startDate}\"\n" +
            "junitHtmlReportDir=\"${build.junitHtmlReportDir}\"";

    public AntBuilder() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            this.command = "C:\\apache-ant-1.6.2\\bin\\ant.bat";
        } else {
            this.command = "/usr/local/bin/ant";
        }
        setBuildSuccessCondition("result==0 and logContainsLine(\"BUILD SUCCESSFUL\")");
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getBuildScriptPath() {
        return buildScriptPath;
    }

    public void setBuildScriptPath(String buildScriptPath) {
        this.buildScriptPath = buildScriptPath;
    }

    public String getTargets() {
        return targets;
    }

    public void setTargets(String targets) {
        this.targets = targets;
    }

    public String getBuildProperties() {
        return buildProperties;
    }

    public void setBuildProperties(String buildProperties) {
        this.buildProperties = buildProperties;
    }

    public String getDisplayName() {
        return "Ant builder";
    }

    public String getIconName() {
        return "ant.gif";
    }

    public List getBuilderSpecificProperties() {
        List properties = getAntProperties();
        return properties;
    }

    public void validate() {
        super.validate();
        try {
            Luntbuild.validateExpression(getCommand());
        } catch (ValidationException e) {
            throw new ValidationException("Invalid command to run Ant: " + e.getMessage());
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
     * Construct command to run ant
     *
     * @return command to run ant
     */
    public String constructBuildCmd(Build build) throws IOException {
        String antCmd = getCommand();
        antCmd = antCmd.replace('\n', ' ');
        antCmd = antCmd.replace('\r', ' ');

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
                        antCmd += " -D" + name + "=" + value;
                    }
                }
            } catch (IOException e) {
                // ignores
            }
        }

        // set ant log level based on project's log level if log level does not been explicitely
        // specified in ant command
        if (!antCmd.matches(".*\\s(-q|-quiet)($|\\s.*)") && !antCmd.matches(".*\\s(-v|-verbose)($|\\s.*)") &&
                !antCmd.matches(".*\\s(-d|-debug)($|\\s.*)")) {
            if (build.getSchedule().getProject().getLogLevel() == Constants.LOG_LEVEL_BRIEF)
                antCmd += " -q";
            else if (build.getSchedule().getProject().getLogLevel() == Constants.LOG_LEVEL_VERBOSE)
                antCmd += " -v";
        }

        String buildScriptAbsolutePath = build.getSchedule().resolveAbsolutePath(getBuildScriptPath());
        antCmd += "  -buildfile \"" + buildScriptAbsolutePath + "\"";
        if (!Luntbuild.isEmpty(getTargets()))
            antCmd += " " + getTargets();

        return antCmd;
    }

    public String constructBuildCmdDir(Build build) {
        String buildScriptAbsolutePath = build.getSchedule().resolveAbsolutePath(getBuildScriptPath());
        return new File(buildScriptAbsolutePath).getParent();
    }

    public com.luntsys.luntbuild.facades.lb12.BuilderFacade constructFacade() {
        return new AntBuilderFacade();
    }

    public void loadFromFacade(com.luntsys.luntbuild.facades.lb12.BuilderFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.AntBuilderFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb12.AntBuilderFacade antBuilderFacade = (com.luntsys.luntbuild.facades.lb12.AntBuilderFacade) facade;
        setCommand(antBuilderFacade.getCommand());
        setBuildScriptPath(antBuilderFacade.getBuildScriptPath());
        setTargets(antBuilderFacade.getBuildTargets());
        setBuildProperties(antBuilderFacade.getBuildProperties());
    }

    public void saveToFacade(com.luntsys.luntbuild.facades.lb12.BuilderFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.AntBuilderFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb12.AntBuilderFacade antBuilderFacade = (AntBuilderFacade) facade;
        antBuilderFacade.setCommand(getCommand());
        antBuilderFacade.setBuildScriptPath(getBuildScriptPath());
        antBuilderFacade.setBuildTargets(getTargets());
        antBuilderFacade.setBuildProperties(getBuildProperties());
    }
}