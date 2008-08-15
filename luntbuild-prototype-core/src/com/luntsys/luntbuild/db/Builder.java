/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-12-16
 * Time: 21:16:37
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
package com.luntsys.luntbuild.db;

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.builders.CommandBuilder;
import com.luntsys.luntbuild.facades.lb20.BuilderFacade;
import com.luntsys.luntbuild.utility.*;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Environment;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

/**
 * The base class for all builders.
 *
 * @author robin shine
 */
public abstract class Builder implements Serializable {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1L;

    public static final String ARTIFACTS_DIR = "artifacts";

    public static final String JUNIT_HTML_REPORT_DIR = "junit_html_report";

	private long id;
    /**
     * Name of the builder
     */
    private String name;

    /**
     * Currently running build
     */
    private transient Build build;

    /**
     * Result of builder execution
     */
    private transient int result;

    /**
     * File path logging builder execution
     */
    private transient String logPath;

    private String buildSuccessCondition;
    private String environments;

	/**
	 * set the unique identity of this builder, will be called by hibernate
	 *
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get identifer of this builder
	 * @return identifer of this builder
	 */
	public long getId() {
		return id;
	}

    /**
     * Get name of this builder
     * @return name of this builder
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of this builder
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get display name for current builders
     *
     * @return display name for current builders
     */
    public abstract String getDisplayName();

    public abstract void setProperties(Map m);

    public abstract Map getProperties();

    public abstract String getType();

    /**
     * Validates properties of this builders
     *
     * @throws com.luntsys.luntbuild.utility.ValidationException
     *
     */
    public void validate() {
        if (Luntbuild.isEmpty(getName())) {
            throw new ValidationException("Builder name should not be empty!");
        }
        setName(getName().trim());
        if (!Luntbuild.isEmpty(getEnvironments())) {
            BufferedReader reader = new BufferedReader(new StringReader(getEnvironments()));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals(""))
                        continue;
                    String name = Luntbuild.getAssignmentName(line);
                    String value = Luntbuild.getAssignmentValue(line);
                    if (Luntbuild.isEmpty(name) || Luntbuild.isEmpty(value))
                        throw new ValidationException("Invalid environment variable definition: " + line);
                }
            } catch (IOException e) {
                // ignores
            }
        }
        if (!Luntbuild.isEmpty(getBuildSuccessCondition())) {
            try {
                Ognl.parseExpression(getBuildSuccessCondition());
            } catch (OgnlException e) {
                throw new ValidationException("Invalid build success condition: " + getBuildSuccessCondition() +
                        ", reason: " + e.getMessage());
            }
        }
        Map props = getProperties();
        Iterator it = props.keySet().iterator();
        while(it.hasNext()) {
        	String key = (String)it.next();
        	String value = (String)props.get(key);
        	if (!Luntbuild.isEmpty(value)) {
                try {
                    Ognl.parseExpression(value);
                } catch (OgnlException e) {
                    throw new ValidationException("Invalid property \"" + key + "\": " + value +
                            ", reason: " + e.getMessage());
                }
        	}
        }
    }

    /**
     * Get facade object of this builders
     *
     * @return facade object of this builders
     */
    public com.luntsys.luntbuild.facades.lb20.BuilderFacade getFacade() {
        com.luntsys.luntbuild.facades.lb20.BuilderFacade facade = constructFacade();
        facade.setName(getName());
        facade.setEnvironments(getEnvironments());
        facade.setBuildSuccessCondition(getBuildSuccessCondition());
        facade.setProperties(getProperties());
        saveToFacade(facade);
        return facade;
    }

    /**
     * Construct builders facade object
     *
     * @return builders facade object
     */
    public abstract com.luntsys.luntbuild.facades.lb20.BuilderFacade constructFacade();

    /**
     * Load value from builders facade
     *
     * @param facade
     */
    public abstract void loadFromFacade(BuilderFacade facade);

    /**
     * Save value to builders facade
     *
     * @param facade
     */
    public abstract void saveToFacade(com.luntsys.luntbuild.facades.lb20.BuilderFacade facade);

    /**
     * Set facade object of this builders
     *
     * @param facade
     */
    public void setFacade(com.luntsys.luntbuild.facades.lb20.BuilderFacade facade) {
        setName(facade.getName());
        setEnvironments(facade.getEnvironments());
        setBuildSuccessCondition(facade.getBuildSuccessCondition());
        setProperties(facade.getProperties());
        loadFromFacade(facade);
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            Builder copy = (Builder) getClass().newInstance();
            copy.setName(getName());
            copy.setEnvironments(getEnvironments());
            copy.setBuildSuccessCondition(getBuildSuccessCondition());
            copy.setProperties(getProperties());
            return copy;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void resolveEmbeddedOgnlVariables(Build build, Project antProject) throws Throwable {
        this.build = build;
        OgnlHelper.setAntProject(antProject);
        OgnlHelper.setTestMode(false);
        Map props = getProperties();
        Iterator it = props.keySet().iterator();
        while(it.hasNext()) {
        	String key = (String)it.next();
        	String value = (String)props.get(key);
        	if (!Luntbuild.isEmpty(value))
        		props.put(key, Luntbuild.evaluateExpression(this, value));
        	else
        		props.put(key, "");
        }
    }

    /**
     * Perform build for specified build object
     *
     * @throws Throwable
     */
    public void build(Build build, LuntbuildLogger buildLogger) throws Throwable {
        this.build = build;
        this.logPath = buildLogger.getOutputPath();

        // create a ant project to receive log
        Project antProject = Luntbuild.createAntProject();
        // log will be written without any filter or decoration
        buildLogger.setDirectMode(true);
        antProject.addBuildListener(buildLogger);

        String buildCmd = constructBuildCmd(build);

        OgnlHelper.setAntProject(antProject);
        OgnlHelper.setTestMode(false);
        Commandline cmdLine = Luntbuild.parseCmdLine(buildCmd);

        Environment env = new Environment();
        if (!Luntbuild.isEmpty(getEnvironments())) {
            BufferedReader reader = new BufferedReader(new StringReader(getEnvironments()));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals(""))
                        continue;
                    String assname = Luntbuild.getAssignmentName(line);
                    String value = Luntbuild.getAssignmentValue(line);
                    if (!Luntbuild.isEmpty(assname) && !Luntbuild.isEmpty(value)) {
                        Environment.Variable var = new Environment.Variable();
                        var.setKey(assname);
                        var.setValue(value);
                        env.addVariable(var);
                    }
                }
            } catch (IOException e) {
                // ignores
            }
        }

        MyExecTask exec =
            new MyExecTask(getDisplayName(), antProject, constructBuildCmdDir(build), cmdLine, env,
                null, Project.MSG_INFO);

        boolean waitForFinish =
        	!(this instanceof CommandBuilder &&
        			((CommandBuilder)this).getWaitForFinish() != null &&
        			((CommandBuilder)this).getWaitForFinish().equalsIgnoreCase("no"));
        result = exec.executeAndGetResult(waitForFinish);

        buildLogger.setDirectMode(false);
        if (!isBuildSuccess())
            throw new BuildException(getDisplayName() + " failed: build success condition not met!");
    }

    /**
     * Constructs the command to run build
     *
     * @return the command to run build, should not be null
     */
    public abstract String constructBuildCmd(Build build) throws IOException;

    /**
     * Constructs the directory to run build command in
     *
     * @return the directory to run build command in. Null if do not care where to run build command
     */
    public abstract String constructBuildCmdDir(Build build);

    /**
     * Get build success condition for this builders
     *
     * @return build success condition for this builders, Null if not exist
     */
    public String getBuildSuccessCondition() {
        return buildSuccessCondition;
    }

    /**
     * Set build success condition for this builders
     *
     * @param buildSuccessCondition
     */
    public void setBuildSuccessCondition(String buildSuccessCondition) {
        this.buildSuccessCondition = buildSuccessCondition;
    }

    /**
     * Get environment settings for this builder
     * @return environment settings for this builder
     */
    public String getEnvironments() {
        return environments;
    }

    /**
     * Set environment settings for this builder
     * @param environments
     */
    public void setEnvironments(String environments) {
        this.environments = environments;
    }

    private boolean isBuildSuccess() {
        try {
            Boolean buildSuccessValue;
            if (!Luntbuild.isEmpty(buildSuccessCondition))
                buildSuccessValue = (Boolean) Ognl.getValue(Ognl.parseExpression(buildSuccessCondition),
                    Ognl.createDefaultContext(this), this, Boolean.class);
            else
                buildSuccessValue = (Boolean) Ognl.getValue(Ognl.parseExpression("result==0"),
                    Ognl.createDefaultContext(this), this, Boolean.class);
            if (buildSuccessValue == null)
                return false;
            else
                return buildSuccessValue.booleanValue();
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get return code of execution of this builder
     * @return return code of execution of this builder
     */
    public int getResult() {
        return result;
    }

    /**
     * Whether or not the builder log contains specified line pattern
     * @param linePattern
     * @return Whether or not the builder log contains specified line pattern
     */
    public boolean logContainsLine(String linePattern) {
        File logFile = new File(logPath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(logFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches(linePattern))
                    return true;
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get build object using this builder
     * @return build object using this builder
     */
    public Build getBuild() {
        return build;
    }

    /**
     * Get system object, this is mainly used for Ognl expression evaluation
     * @return system object
     */
    public OgnlHelper getSystem() {
        return new OgnlHelper();
    }

    public String toString() {
        String summary = "Builder name: " + getName() + "\n";
        summary += "Builder type: " + getDisplayName() + "\n";
        Map props = getProperties();
        Iterator it = props.keySet().iterator();
        while(it.hasNext()) {
        	String key = (String)it.next();
        	String value = (String)props.get(key);
            summary += "    " + key + ": " + value + "\n";
        }
        return summary;
    }

    public void copyProperties(Map srcProps, Map destProps) {
        Iterator it = srcProps.keySet().iterator();
        while(it.hasNext()) {
        	String key = (String)it.next();
        	destProps.put(key, srcProps.get(key));
        }
    }
}
