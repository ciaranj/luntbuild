package com.luntsys.luntbuild.scrapers;

import java.util.Vector;

/**
 * MSVSSolution is used as a data object for Velocity.  All the getters
 * will become Velocity properties (e.g. $solution.Name).
 *
 * @author kevin.lin@smartbombinteractive.com
 */
public class MSVSSolution
{
    /**
     * The path to the solution file.
     */
    public String getPath() {return path;}

    /**
     * The name of the solution file (path minus the directory).
     */
    public String getName() {return name;}

    /**
     * The solution configuration used in the build.
     */
    public String getConfiguration() {return configuration;}

    /**
     * Number of projects successfully built for the solution.
     */
    public int getSucceeded() {return succeeded;}

    /**
     * Number of projects that failed.
     */
    public int getFailed() {return failed;}

    /**
     * Number of projects skipped.
     */
    public int getSkipped() {return skipped;}

    /**
     * List of projects built for the solution.
     * @see MSVSProject
     */
    public Vector getProjects() {return projects;}

    public void setPath(String path) {
        this.path = path;
        int slash = path.lastIndexOf('\\');

        if(slash == -1)
        {
            slash = path.lastIndexOf('/');
        }

        if(slash != -1)
        {
            name = path.substring(slash + 1, path.length());
        }
        else
        {
            name = path;
        }
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public void setResults(int succeeded, int failed, int skipped) {
        this.succeeded = succeeded;
        this.failed = failed;
        this.skipped = skipped;
    }

    private String path = null;
    private String name = null;
    private String configuration = null;
    private int succeeded = 0;
    private int failed = 0;
    private int skipped = 0;
    private Vector projects = new Vector();
}