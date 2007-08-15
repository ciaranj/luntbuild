package com.luntsys.luntbuild.scrapers;

/**
 * MSVSProject is used as a data object for Velocity.  All the getters
 * will become Velocity properties (e.g. $project.Name).
 *
 * @author kevin.lin@smartbombinteractive.com
 */
public class MSVSProject
{
    /**
     * Get the project name.
     */
    public String getName() {return name;}

    /**
     * Number of errors in the project build.
     */
    public int getErrors() {return errors;}

    /**
     * Number of warnings in the project build.
     */
    public int getWarnings() {return warnings;}

    public void setName(String name) {
        this.name = name;
    }

    public void setResults(int errors, int warnings) {
        this.errors = errors;
        this.warnings = warnings;
    }

    private String name = null;
    private int errors = 0;
    private int warnings = 0;
}