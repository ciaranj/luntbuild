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

/**
 * Report interface for build reports.
 */
public interface Report {
    /**
     * Gets the description of the report.
     * 
     * @return the description of the report
     */
    String getReportDescription();

    /**
     * Gets the folder to store the report.
     * 
     * This should be a relative path.
     * 
     * @return the folder to store the report
     */
    String getReportDir();

    /**
     * Gets the URL to the report.
     * 
     * This should be a relative and properly encoded path.  The build publish dir is provided
     * for reports that need to check for the existance of certain files in their folder.
     * 
     * @param publishDir the publish directory for the build
     * @return the URL to the report
     */
    String getReportUrl(String publishDir);

    /**
     * Gets the name of the icon to use for this report.
     * 
     * @return the name of the icon
     */
    String getIcon();
}
