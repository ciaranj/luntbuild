package com.luntsys.luntbuild.builders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Builder;
import com.luntsys.luntbuild.db.StringProperty;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.lb20.BuilderFacade;
import com.luntsys.luntbuild.facades.lb20.NAntBuilderFacade;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;

public class NAntBuilder extends Builder {

    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1L;

    public static final String COMMAND = "command";
    public static final String BUILDSCRIPTPATH = "buildscriptpath";
    public static final String TARGETS = "targets";
    public static final String BUILDPROPERTIES = "buildproperties";
    public static final String DISPLAYNAME = "displayname";

    private String type = "nantbuilder";

    /**
     * Extra properties transfered into the build script
     */
    private static final String buildProperties =
            "buildVersion=\"${build.version}\"\n" +
            "artifactsDir=\"${build.artifactsDir}\"\n" +
            "buildDate=\"${build.startDate}\"\n" +
            "junitHtmlReportDir=\"${build.junitHtmlReportDir}\"";

    private Map properties;


    public NAntBuilder() {
        setBuildSuccessCondition("result==0 and logContainsLine(\"BUILD SUCCESSFUL\")");
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NAntBuilder) {
            if (getName().equals(((NAntBuilder)obj).getName()))
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
	        	this.properties.put(COMMAND, "C:\\Program Files\\NAnt\\bin\\NAnt.exe");
	        } else {
	        	this.properties.put(COMMAND, "/usr/local/bin/nant");
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

	@Override
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

	@Override
	public String constructBuildCmdDir(Build build) {
        String buildScriptAbsolutePath = build.getSchedule().resolveAbsolutePath(getBuildScriptPath());
        return new File(buildScriptAbsolutePath).getParent();
	}

	@Override
	public BuilderFacade constructFacade() {
        return new NAntBuilderFacade();
	}

	@Override
	public void loadFromFacade(BuilderFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb20.NAntBuilderFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb20.NAntBuilderFacade antBuilderFacade =
        	(com.luntsys.luntbuild.facades.lb20.NAntBuilderFacade) facade;
        copyProperties(antBuilderFacade.getProperties(), getProperties());
	}

	@Override
	public void saveToFacade(BuilderFacade facade) {
        if (!(facade instanceof com.luntsys.luntbuild.facades.lb20.NAntBuilderFacade))
            throw new RuntimeException("Invalid facade class: " + facade.getClass().getName());
        com.luntsys.luntbuild.facades.lb20.NAntBuilderFacade antBuilderFacade =
        	(com.luntsys.luntbuild.facades.lb20.NAntBuilderFacade) facade;
        copyProperties(getProperties(), antBuilderFacade.getProperties());
	}

}
