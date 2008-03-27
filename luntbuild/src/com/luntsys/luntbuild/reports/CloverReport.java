/*
 * Copyright luntsys (c) 2004-2007,
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

package com.luntsys.luntbuild.reports;

import java.io.File;

/**
 * Clover report.
 * 
 * <p><a href="http://www.atlassian.com/software/clover/">http://www.atlassian.com/software/clover/</a>.</p>
 * 
 * @author Jason Archer
 */
public class CloverReport implements Report {

    private String report_dir = "clover_html_report";

    /**
     * @inheritDoc
     */
    public String getReportDescription() {
        return "Code coverage analysis for Java.";
    }

    /**
     * @inheritDoc
     */
    public String getReportDir() {
        return report_dir;
    }

    /**
     * Gets the URL to the report.
     * 
     * The no frames index page will be used if the frames index page does not exist.  <code>null</code>
     * is returned if neither index page exists.
     * 
     * @param publishDir the publish directory for the build
     * @return the URL to the report, or <code>null</code>
     */
    public String getReportUrl(String publishDir) {
        String htmlReportsDir = publishDir + File.separator + report_dir;
        if (new File(htmlReportsDir + File.separator + "index.html").exists())
            return report_dir + "/index.html";
        if (new File(htmlReportsDir + File.separator + "pkgs-summary.html").exists())
            return report_dir + "/pkgs-summary.html";
        return null;
    }

    /**
     * @inheritDoc
     */
    public String getIcon() {
        return "clover.gif";
    }
}
