package com.luntsys.luntbuild.luntclipse;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors.AccuRevAdaptorGroup;
import com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors.BaseClearcaseAdaptorGroup;
import com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors.ClearcaseUCMAdaptorGroup;
import com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors.CvsAdaptorGroup;
import com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors.FilesystemAdaptorGroup;
import com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors.PerforceAdaptorGroup;
import com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors.StarTeamAdaptorGroup;
import com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors.SubversionAdaptorGroup;
import com.luntsys.luntbuild.luntclipse.wizards.vcsadaptors.VisualSourcesafeAdaptorGroup;
/**
 * Luntclipse constants and strings
 *
 * @author Lubos Pochman
 *
 */
public class LuntclipseConstants {

    public static final String gettingData = "No builds yet";

    public static final String usersCheckedRecently = "<users who checked in code recently>";

    public static final String noBuildsYet = "no builds yet";

    /* Luntbuild version constants */
    public static final String LUNTBUILD_VERSION_12 = "1.2";
    public static final String LUNTBUILD_VERSION_121 = "1.2.1";
    public static final String LUNTBUILD_VERSION_122 = "1.2.2";
    public static final String LUNTBUILD_VERSION_13 = "1.3";

    private static final int[] versionEncodeArr = { 1<< 12, 1 << 8, 1 << 4, 1 << 0};

    /* ImageRegistry constants */
    public static final String BUILD_IMG = "BUILD_IMG";
    public static final String SUCCESS_IMG = "SUCCESS_IMG";
    public static final String FAILED_IMG = "FAILED_IMG";
    public static final String RUNNING_IMG = "RUNNING_IMG";
    public static final String PROJECT_IMG = "PROJECT_IMG";
    public static final String CREATE_IMG = "CREATE_IMG";
    public static final String MODIFY_IMG = "MODIFY_IMG";
    public static final String DELETE_IMG = "DELETE_IMG";
    public static final String DESELECT_IMG = "DESELECT_IMG";

    public static final String[] buildType = {"Clean", "Increment"};

    public static final String[] buildStatus = {"all", "success", "failed", "running"};
    public static final int BUILD_ALL = 0;
    public static final int BUILD_SUCCESS = 1;
    public static final int BUILD_FAILED = 2;
    public static final int BUILD_RUNNING = 3;

    public static final String[] postBuildStrategy = {
        "Do not post-build",
        "Post-build when success",
        "Post-build when failed",
        "Post-build always"
    };

    public static final String[] labelStrategy = {
        "Label successful builds",
        "Label always",
        "Do not label"
    };

    public static final String[] notifyStrategy = {
        "Notify when status changed",
        "Notify when success",
        "Notify when failed",
        "Notify always",
        "Do not notify"
    };

    public static final String[] dependencyStrategy = {
        "Trigger schedules this depends on",
        "Trigger schedules depends on this",
        "Trigger all dependent schedules",
        "Do not trigger any dependent schedules"
    };

    public static final String[] notifyWith = {
        "Email Notifier",
        "Msn Notifier",
        "Jabber Notifier",
        "Sametime Notifier",
        "Blog Notifier"
    };

    public static final String[] notifyWithClass = {
        "com.luntsys.luntbuild.notifiers.EmailNotifier",
        "com.luntsys.luntbuild.notifiers.MsnNotifier",
        "com.luntsys.luntbuild.notifiers.JabberNotifier",
        "com.luntsys.luntbuild.notifiers.SametimeNotifier",
        "com.luntsys.luntbuild.notifiers.BlogNotifier"
    };

    public static final String getNotifierClassName(String name) {
        for (int i = 0; i < notifyWith.length; i++) {
            String n = notifyWith[i];
            if (n.equals(name)) return notifyWithClass[i];
        }
        return "";
    }

    public static final String getNotifierName(String className) {
        for (int i = 0; i < notifyWithClass.length; i++) {
            if (notifyWithClass[i].equals(className)) return notifyWith[i];
        }
        return "";
    }

    public static final String[] logLevel = {
        "Brief",
        "Normal",
        "Verbose"
    };

    public static final String[] buildTriggerTypes = {
        "Manual",
        "Simple",
        "Cron"
    };
    public static final int TRIGGER_TYPE_MANUAL = 0;
    public static final int TRIGGER_TYPE_SIMPLE = 1;
    public static final int TRIGGER_TYPE_CRON = 2;

    public static final String[] buildCleanupStrategy = {
        "No cleanup",
        "Keep",
        "Keep"
    };

    public static final String[] builderType = {
        "Ant Builder",
        "Maven Builder",
        "Maven2 Builder",
        "Command Builder",
        "Rake Builder"
    };

    public static final String[] builderPathLabels = {
        "Build script path",
        "Directory to run Maven in",
        "Directory to run Maven in",
        "Run command in directory",
        "Build script path"
    };

    public static final String[] builderTargetLabels = {
        "Build targets",
        "Goals to build",
        "Goals to build",
        null,
        "Build targets"
    };

    public static final ArrayList builderProperties = new ArrayList();
    static {
        String[] props = {
            "buildVersion=\"${build.version}\"",
            "artifactsDir=\"${build.artifactsDir}\"",
            "buildDate=\"${build.startDate}\"",
            "junitHtmlReportDir=\"${build.junitHtmlReportDir}\""
        };
        builderProperties.add(props);
        builderProperties.add(props);
        builderProperties.add(props);
        builderProperties.add(null);
        builderProperties.add(props);
    }

    public static final String[] builderCommands;
    static {
        if (System.getProperty("os.name").startsWith("Windows")) {
            String[] cmds = {
                "C:\\apache-ant-1.6.2\\bin\\ant.bat",
                "\"C:\\Program Files\\Apache Software Foundation\\Maven 1.0.2\\bin\\maven.bat\"",
                "\"C:\\maven-2.0.1\\bin\\mvn.bat\"",
                "\"${build.schedule.workingDir}\\build\\build.bat\" \"${build.version}\" \"${build.artifactsDir}\" \"${build.startDate}\"",
                "C:\\rake\\bin\\rake.bat"
            };
            builderCommands = cmds;
        } else {
            String[] cmds = {
                    "/usr/local/bin/ant",
                    "/usr/local/bin/maven",
                    "/usr/local/bin/mvn",
                    "\"${build.schedule.workingDir}/build/build\" \"${build.version}\" \"${build.artifactsDir}\" \"${build.startDate}\"",
                    "/usr/local/bin/rake"
                };
            builderCommands = cmds;
        }
    }

    public static final String[] builderSuccessConditions = {
        "result==0 and logContainsLine(\"BUILD SUCCESSFUL\")",
        "result==0 and logContainsLine(\"BUILD SUCCESSFUL\")",
        "result==0 and logContainsLine(\"\\\\[INFO\\\\].*BUILD SUCCESSFUL.*\")",
        "result==0",
        "result==0 and !logContainsLine(\"Command failed with status\")"
    };

    public static final int ANT_BUILDER = 0;
    public static final int MAVEN_BUILDER = 1;
    public static final int MAVEN2_BUILDER = 2;
    public static final int COMMAND_BUILDER = 3;
    public static final int RAKE_BUILDER = 4;

    public static final String[] vcsAdaptorType = {
        "AccRev",
        "Base Clearcase",
        "Cvs",
        "File system",
        "Perforce",
        "StarTeam",
        "Subversion",
        "Clearcase UCM",
        "Visual Sourcesafe"
    };

    public static final Class[] vcsAdaptorClass = {
        AccuRevAdaptorGroup.class,
        BaseClearcaseAdaptorGroup.class,
        CvsAdaptorGroup.class,
        FilesystemAdaptorGroup.class,
        PerforceAdaptorGroup.class,
        StarTeamAdaptorGroup.class,
        SubversionAdaptorGroup.class,
        ClearcaseUCMAdaptorGroup.class,
        VisualSourcesafeAdaptorGroup.class
    };

    public static final int ACCUREV_ADAPTOR = 0;
    public static final int BASE_CLEARCASE_ADAPTOR = 1;
    public static final int CVS_ADAPTOR = 2;
    public static final int FILESYSTEM_ADAPTOR = 3;
    public static final int PERFORCE_ADAPTOR = 4;
    public static final int STARTEAM_ADAPTOR = 5;
    public static final int SUBVERSION_ADAPTOR = 6;
    public static final int UCM_CLEARCASE_ADAPTOR = 7;
    public static final int VSS_ADAPTOR = 8;

    public static final String[] perforceLineEnd = {
        "local",
        "unix",
        "mac",
        "win",
        "share"
    };
    public static int perforceLineEndIndex(String lineEnd) {
        for (int i = 0; i < perforceLineEnd.length; i++) {
            String val = perforceLineEnd[i];
            if (val.equalsIgnoreCase(lineEnd)) return i;
        }
        return -1;
    }

    /**
     * @param version as string
     * @return encoded version as number
     */
    public static int getVersion(String version) {
        StringTokenizer tok = new StringTokenizer(version, ".");
        int total = 0;
        int i = 0;
        while (tok.hasMoreTokens()) {
            int val = new Integer(tok.nextToken()).intValue();
            total += val * versionEncodeArr[i++];
        }
        return total;
    }
}
