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
import com.luntsys.luntbuild.utility.IStringProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Maven builder implementation
 * @author robin shine
 */
public class MavenBuilder extends Builder {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1L;

    /**
     * The command to run maven
     */
    private String command;

    /**
     * Directory to run maven in
     */
    private String dirToRunMaven;

    /**
     * Goals to build
     */
    private String goals;

    /**
     * Extra properties transfered into the build script
     */
    private String buildProperties =
            "buildVersion=\"${build.version}\"\n" +
            "artifactsDir=\"${build.artifactsDir}\"\n" +
            "buildDate=\"${build.startDate}\"\n" +
            "junitHtmlReportDir=\"${build.junitHtmlReportDir}\"";


    public MavenBuilder() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            this.command = "\"C:\\Program Files\\Apache Software Foundation\\Maven 1.0.2\\bin\\maven.bat\"";
        } else {
            this.command = "/usr/local/bin/maven";
        }
        setBuildSuccessCondition("result==0 and logContainsLine(\"BUILD SUCCESSFUL\")");
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDirToRunMaven() {
        return dirToRunMaven;
    }

    public void setDirToRunMaven(String dirToRunMaven) {
        this.dirToRunMaven = dirToRunMaven;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getBuildProperties() {
        return buildProperties;
    }

    public void setBuildProperties(String buildProperties) {
        this.buildProperties = buildProperties;
    }

    public String getDisplayName() {
        return "Maven builder";
    }

    public String getIconName() {
        return "maven.png";
    }

    public List getBuilderSpecificProperties() {
        List properties = getMavenProperties();
        return properties;
    }

    public void validate() {
        super.validate();
        try {
            Luntbuild.validateExpression(getCommand());
        } catch (ValidationException e) {
            throw new ValidationException("Invalid command to run Maven: " + e.getMessage());
        }
        if (!Luntbuild.isEmpty(getGoals())) {
            try {
                Luntbuild.validateExpression(getGoals());
            } catch (ValidationException e) {
                throw new ValidationException("Invalid goals: " + e.getMessage());
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
     * Construct command to run maven
     *
     * @return command to run maven
     */
    public String constructBuildCmd(Build build) throws IOException {
        String mavenCmd = getCommand();
        mavenCmd = mavenCmd.replace('\n', ' ');
        mavenCmd = mavenCmd.replace('\r', ' ');

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
                        mavenCmd += " -D" + name + "=" + value;
                    }
                }
            } catch (IOException e) {
                // ignores
            }
        }

        // set maven log level based on project's log level if log level does not been explicitely specified in maven command
        if (!mavenCmd.matches(".*\\s(-X|--debug)($|\\s.*)") && !mavenCmd.matches(".*\\s(-q|--quiet)($|\\s.*)")) {
            if (build.getSchedule().getProject().getLogLevel() == com.luntsys.luntbuild.facades.Constants.LOG_LEVEL_BRIEF)
                mavenCmd += " -q";
            else if (build.getSchedule().getProject().getLogLevel() == com.luntsys.luntbuild.facades.Constants.LOG_LEVEL_VERBOSE)
                mavenCmd += " -X";
        }

        mavenCmd += " -d \"" + build.getSchedule().resolveAbsolutePath(getDirToRunMaven()) + "\"";
        if (!Luntbuild.isEmpty(getGoals()))
            mavenCmd += " " + getGoals();

        return mavenCmd;
    }

    public String constructBuildCmdDir(Build build) {
        return build.getSchedule().resolveAbsolutePath(getDirToRunMaven());
    }

    public com.luntsys.luntbuild.facades.lb12.BuilderFacade constructFacade() {
        return new com.luntsys.luntbuild.facades.lb12.MavenBuilderFacade();
    }

    public void loadFromFacade(com.luntsys.luntbuild.facades.lb12.BuilderFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.MavenBuilderFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb12.MavenBuilderFacade mavenBuilderFacade = (com.luntsys.luntbuild.facades.lb12.MavenBuilderFacade) facade;
        setCommand(mavenBuilderFacade.getCommand());
        setDirToRunMaven(mavenBuilderFacade.getDirToRunMaven());
        setGoals(mavenBuilderFacade.getGoals());
        setBuildProperties(mavenBuilderFacade.getBuildProperties());
    }

    public void saveToFacade(com.luntsys.luntbuild.facades.lb12.BuilderFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb12.MavenBuilderFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb12.MavenBuilderFacade mavenBuilderFacade = (com.luntsys.luntbuild.facades.lb12.MavenBuilderFacade) facade;
        mavenBuilderFacade.setCommand(getCommand());
        mavenBuilderFacade.setDirToRunMaven(getDirToRunMaven());
        mavenBuilderFacade.setGoals(getGoals());
        mavenBuilderFacade.setBuildProperties(getBuildProperties());
    }
}