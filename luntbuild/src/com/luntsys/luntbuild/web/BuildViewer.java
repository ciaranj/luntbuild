/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-27
 * Time: 6:41:35
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
package com.luntsys.luntbuild.web;

import com.luntsys.luntbuild.BuildGenerator;
import com.luntsys.luntbuild.builders.Builder;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.security.SecurityHelper;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.LuntbuildLogger;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.vcs.*;
import com.luntsys.luntbuild.web.components.SecuritySupportComponent;

import org.acegisecurity.AccessDeniedException;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tools.ant.BuildException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This component renders build viewer page
 *
 * @author robin shine
 * @author Jason Archer
 */
public abstract class BuildViewer extends SecuritySupportComponent implements PageDetachListener {
    private Build build;

    public String getBuildStatusGif() {
        if (getBuild().getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_SUCCESS)
            return "images/success.gif";
        else if (getBuild().getStatus() == com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_FAILED)
            return "images/failed.gif";
        else
            return "images/running.gif";
    }

    public String getBuildCostTime() {
        if (getBuild().getStartDate() == null)
            return "";
        if (getBuild().getEndDate() == null)
            return String.valueOf((System.currentTimeMillis() -
                    getBuild().getStartDate().getTime()) / 60000);
        return String.valueOf((getBuild().getEndDate().getTime() -
                getBuild().getStartDate().getTime()) / 60000);
    }

    public void getBuildLog(IRequestCycle cycle) {
        getBuildsTab().ensureCurrentTab();
        String publishDir = getBuild().getPublishDir();
        String buildXmlPath = publishDir + File.separator + BuildGenerator.BUILD_XML_LOG;
        String buildPath = publishDir + File.separator + BuildGenerator.BUILD_HTML_LOG;
        String buildTextPath = publishDir + File.separator + BuildGenerator.BUILD_LOG;

        LuntbuildLogger buildLogger = getBuild().getLogger();
        if (buildLogger != null)
            buildLogger.logHtml(buildXmlPath, Luntbuild.installDir + "/log.xsl", buildPath, buildTextPath);

        File f = new File(buildPath);
        if (!(f.exists() && f.canRead())) {
            buildPath = publishDir + "/" + BuildGenerator.BUILD_LOG;
            f = new File(buildPath);
            if (f.exists() && f.canRead())
                Luntbuild.sendFile(cycle, buildPath);
        } else
            Luntbuild.sendFile(cycle, buildPath);
    }

    public BuildsTab getBuildsTab() {
        return (BuildsTab) getContainer();
    }

    public void getRevisionLog(IRequestCycle cycle) {
        getBuildsTab().ensureCurrentTab();
        String publishDir = getBuild().getPublishDir();
        String revisionLogFile = publishDir + "/" + BuildGenerator.REVISION_HTML_LOG;
        File f = new File(revisionLogFile);
        if (!(f.exists() && f.canRead())) {
            revisionLogFile = publishDir + "/" + BuildGenerator.REVISION_LOG;
            f = new File(revisionLogFile);
            if (f.exists() && f.canRead())
                Luntbuild.sendFile(cycle, revisionLogFile);
        } else
            Luntbuild.sendFile(cycle, revisionLogFile);
    }

    public abstract void setBuildId(long buildId);

    public abstract long getBuildId();

    /**
     * Request a path into the publish directory
     */
    public void requestFile(IRequestCycle cycle) {
        getBuildsTab().ensureCurrentTab();
        String pathToRequest = (String) cycle.getServiceParameters()[0];
        String publishDir = getBuild().getPublishDir();
        File fileToRequest = new File(publishDir + pathToRequest);

        String pathRelativeToPublishDir = Luntbuild.parseRelativePath(new File(publishDir), fileToRequest);
        // requested file equals to publish directory or is not under publish directory
        if (pathRelativeToPublishDir == null || pathRelativeToPublishDir.equals(""))
            throw new ApplicationRuntimeException("Invalid file requested: " +
                    fileToRequest.getAbsolutePath());
        if (fileToRequest.isFile())
            Luntbuild.sendFile(cycle, fileToRequest.getAbsolutePath());
        else
            throw new ApplicationRuntimeException("Invalid file requested: " +
                    fileToRequest.getAbsolutePath());
    }

    /**
     * List directory contents
     * @param cycle
     */
    public void listDir(IRequestCycle cycle) {
        getBuildsTab().ensureCurrentTab();
        // refresh the build
        build = Luntbuild.getDao().loadBuild(getBuildId());
        String relativePathRequested = (String) cycle.getServiceParameters()[0];
        String publishDir = getBuild().getPublishDir();
        File dirToBeList = new File(publishDir + relativePathRequested);

        String pathRelativeToPublishDir = Luntbuild.parseRelativePath(new File(publishDir), dirToBeList);
        if (pathRelativeToPublishDir == null || pathRelativeToPublishDir.equals(""))
            throw new ApplicationRuntimeException("Invalid directory requested: " + dirToBeList.getAbsolutePath());
        if (!dirToBeList.isDirectory())
            throw new ApplicationRuntimeException("Invalid directory requested: " + dirToBeList.getAbsolutePath());
        setRelativePath(pathRelativeToPublishDir.replace('\\', '/'));
    }

    public abstract File getCurrentFile();

    public String getCurrentFileLastModified() {
        return Luntbuild.DATE_DISPLAY_FORMAT.format(new Date(getCurrentFile().lastModified()));
    }

    public String getCurrentFileLength() {
        long length = getCurrentFile().length();
        return "" + length + " bytes";
    }

    public abstract void setAction(String action);

    public abstract String getAction();

    public abstract void setDetail(String detail);

    public abstract String getDetail();

	public void changeDetail(IRequestCycle cycle) {
		setDetail(cycle.getServiceParameters()[0].toString());
	}

    public void rebuild(IRequestCycle cycle) {
        getBuildsTab().ensureCurrentTab();
        RebuildEditor rebuildEditor = (RebuildEditor) getComponent("rebuildEditorComponent");
        rebuildEditor.setBuild(getBuild());
        setAction("rebuild");
    }

    /**
     * Set the relative path to current build's publishing directory.
     * @param relativePath
     */
    public abstract void setRelativePath(String relativePath);

    public abstract String getRelativePath();

    /**
     * Get list of files under current relative path
     * @return
     */
    public List getFiles() {
        String publishDir = getBuild().getPublishDir();
        File currentDir = new File(publishDir + getRelativePath());
        List files = new ArrayList();
        String pathRelativeToArtifacts = Luntbuild.parseRelativePath(new File(publishDir + "/artifacts"), currentDir);
        if (pathRelativeToArtifacts != null && !pathRelativeToArtifacts.equals("")) {
            files.add(new File(currentDir, ".."));
        }
        File fileArray[] = currentDir.listFiles();
        if (fileArray != null)
            files.addAll(Arrays.asList(fileArray));
        return files;
    }

    public Build getBuild() {
        if (build == null)
            build = Luntbuild.getDao().loadBuild(getBuildId());
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
        if (build != null)
            setBuildId(build.getId());
    }

    public void finishLoad(IRequestCycle cycle, IPageLoader loader, IComponentSpecification specification) {
        super.finishLoad(cycle, loader, specification);
        build = null;
    }

    public void pageDetached(PageEvent event) {
        build = null;
    }

    public boolean isRebuildable() {
        return getBuild().isHaveLabelOnHead();
    }

    public String getBuildStartDate() {
        if (getBuild().getStartDate() != null)
            return Luntbuild.DATE_DISPLAY_FORMAT.format(getBuild().getStartDate());
        else
            return "";
    }

    public String getBuildEndDate() {
        if (getBuild().getEndDate() != null)
            return Luntbuild.DATE_DISPLAY_FORMAT.format(getBuild().getEndDate());
        else
            return "";
    }

    public abstract IUploadFile getFileToBeUpload();

    /**
     * file upload listener method
     *
     * @param cycle
     */
    public void upload(IRequestCycle cycle) {
        getBuildsTab().ensureCurrentTab();
        if (getFileToBeUpload() == null || Luntbuild.isEmpty(getFileToBeUpload().getFileName()))
            return;
        // check permissions
        if (!SecurityHelper.isPrjAdministrable(getBuild().getSchedule().getProject().getId()))
            throw new AccessDeniedException("Access denied!");
        InputStream fis = getFileToBeUpload().getStream();
        FileOutputStream fos = null;
        try {
            String publishDir = getBuild().getPublishDir();
            File dir = new File(publishDir + getRelativePath());
            Luntbuild.createDir(dir.getAbsolutePath());
            fos = new FileOutputStream(new File(dir + "/" + getFileToBeUpload().getFileName()));
            byte[] buffer = new byte[Luntbuild.FILE_BLOCK_SIZE];
            while (true) {
                int length = fis.read(buffer);
                if (length < 0)
                    break;
                fos.write(buffer, 0, length);
            }
            fis.close();
            fos.close();
        } catch (IOException e) {
            throw new ApplicationRuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // ignores
                }
            }
        }
    }

    public abstract void setFileToBeDelete(File fileToBeDelete);

    public abstract File getFileToBeDelete();

    /**
     * Delete specified file in artifacts directory
     *
     * @param cycle
     */
    public void deleteFile(IRequestCycle cycle) {
        getBuildsTab().ensureCurrentTab();
        setFileToBeDelete((File) (cycle.getServiceParameters()[0]));
        setAction("deleteFile");
    }

    public void confirmDeleteFile(IRequestCycle cycle) {
        getBuildsTab().ensureCurrentTab();
        if (getFileToBeDelete() == null)
            return;
        // check permissions
        if (!SecurityHelper.isPrjAdministrable(getBuild().getSchedule().getProject().getId()))
            throw new AccessDeniedException("Access denied!");
        String publishDir = getBuild().getPublishDir();
        File artifactsRoot = new File(publishDir + "/artifacts");
        String pathRelativeToArtifactsDir = Luntbuild.parseRelativePath(artifactsRoot, getFileToBeDelete());
        if (pathRelativeToArtifactsDir == null || pathRelativeToArtifactsDir.equals(""))
            throw new ApplicationRuntimeException("Can not delete file: " +
                    getFileToBeDelete().getAbsolutePath());

        if (getFileToBeDelete().isFile()) {
            if (!getFileToBeDelete().delete())
                throw new ApplicationRuntimeException("Can not delete file: " +
                        getFileToBeDelete().getAbsolutePath());
        } else {
            try {
                Luntbuild.deleteDir(getFileToBeDelete().getAbsolutePath());
            } catch (BuildException e) {
                throw new ApplicationRuntimeException(e);
            }
        }
        setFileToBeDelete(null);
        setAction(null);
    }

    public void cancelDeleteFile(IRequestCycle cycle) {
        getBuildsTab().ensureCurrentTab();
        setFileToBeDelete(null);
        setAction(null);
    }

    public abstract String getDirToBeCreate();

    /**
     * Listener to create the directory
     * @param cycle
     */
    public void createDir(IRequestCycle cycle) {
        getBuildsTab().ensureCurrentTab();
        if (Luntbuild.isEmpty(getDirToBeCreate()))
            return;
        // check permissions
        if (!SecurityHelper.isPrjAdministrable(getBuild().getSchedule().getProject().getId()))
            throw new AccessDeniedException("Access denied!");
        if (getDirToBeCreate().indexOf("..") != -1)
            throw new ApplicationRuntimeException("Invalid directory name specified: " +
                    getDirToBeCreate());
        String publishDir = getBuild().getPublishDir();
        File absoluteDir = new File(publishDir + getRelativePath() +
                "/" + getDirToBeCreate());
        try {
            Luntbuild.createDir(absoluteDir.getAbsolutePath());
        } catch (BuildException e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    public abstract void setCssIndex(int index);

    public abstract int getCssIndex();

    public String getFileRowCssClass() {
        if (getCssIndex() % 2 == 0)
			return "dataTableRow dataTableRow2";
		else
			return "dataTableRow dataTableRow1";
    }

    public String getArtifactsTailCssClass() {
        if (getCssIndex() % 2 == 0)
            return "artifactsTail2";
        else
            return "artifactsTail1";
    }

    public String getJunitHtmlReport() {
        String junitHtmlReportsDir = getBuild().getJunitHtmlReportDir();
        if (new File(junitHtmlReportsDir + File.separator + "index.html").exists())
            return Builder.JUNIT_HTML_REPORT_DIR + "/index.html";
        if (new File(junitHtmlReportsDir + File.separator + "junit-noframes.html").exists())
            return Builder.JUNIT_HTML_REPORT_DIR + "/junit-noframes.html";
        return null;
    }

    public abstract int getVcsIndex();

    public Iterator getRevisionLogs() {
    	List log_list = new ArrayList();
    	NodeList logs = Revisions.readLogs(build);
    	if (logs != null) {
	    	for (int i = 0; i < logs.getLength(); i++) {
	    		log_list.add(logs.item(i));
	    	}
    	}

    	return log_list.iterator();
    }

    public String getRevisionLogGraphical(Node log) {
        String html = "";
        String classname = log.getAttributes().item(0).getNodeValue();
        Vcs vcs = (Vcs) getBuild().getVcsList().get(getVcsIndex());
        if (classname.equals("com.luntsys.luntbuild.vcs.CvsAdaptor")) {
            CvsAdaptor cvs = (CvsAdaptor) vcs;
            NodeList entries = log.getChildNodes();
            String path = "";
            for (int i = 0; i < entries.getLength(); i++) {
                if (entries.item(i).getNodeName().equals("vcs")) {
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"10\">" + Luntbuild.getTextContent(entries.item(i)) + "</td>";
                    html += "</tr>";
                } else if (entries.item(i).getNodeName().equals("logentry")) {
                    String author = "";
                    String date = "";
                    String msg = "";
                    NodeList members = entries.item(i).getChildNodes();
                    for (int m = 0; m < members.getLength(); m++) {
                        Node member = members.item(m);
                        if (member.getNodeName().equals("author")) {
                            author = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("date")) {
                            date = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("msg")) {
                            msg = Luntbuild.getTextContent(member);
                            html += "<tr class=\"" + getFileRowCssClass() + "\">";
                            html += "<td height=\"25\" colspan=\"2\">&nbsp;</td>";
                            html += "<td height=\"25\" colspan=\"8\">" + msg + "</td>";
                            html += "</tr>";
                        } else if (member.getNodeName().equals("paths")) {
                            NodeList paths = member.getChildNodes();
                            for (int p = 0; p < paths.getLength(); p++) {
                                if (paths.item(p).getNodeName().equals("path")) {
                                    String version = paths.item(p).getAttributes().item(1).getNodeValue();
                                    String temp_path = Luntbuild.getTextContent(paths.item(p));
                                    if (!path.equals(temp_path)) {
                                        path = temp_path;
                                        setCssIndex(getCssIndex() + 1);
                                        html += "<tr class=\"" + getFileRowCssClass() + "\">";
                                        html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                                        html += "<td height=\"25\" colspan=\"9\">"
                                            + "<img src=\"images/file.gif\" width=\"16\" height=\"16\"> " + cvs.createLinkForFile(path, version) + "</td>";
                                        html += "</tr>";
                                    }
                                    // Try to create diff link
                                    String diff = " " + cvs.createLinkForDiff(path, version);
                                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                                    html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                                    html += "<td height=\"25\" colspan=\"3\">" + version + diff + "</td>";
                                    html += "<td height=\"25\" colspan=\"3\">"
                                        + "<img src=\"images/user.gif\" width=\"16\" height=\"16\" title=\"author\"> " + author + "</td>";
                                    html += "<td height=\"25\" colspan=\"3\">"
                                        + "<img src=\"images/date.gif\" width=\"16\" height=\"14\"> " + date.replaceAll("T"," ") + "</td>";
                                    html += "</tr>";
                                }
                            }
                        }
                    }
                }
            }
            setCssIndex(getCssIndex() + 1);
        } else if (classname.equals("com.luntsys.luntbuild.vcs.DynamicClearcaseAdaptor") ||
                classname.equals("com.luntsys.luntbuild.vcs.UCMClearcaseAdaptor")) {
            AbstractClearcaseAdaptor clearcase = (AbstractClearcaseAdaptor) vcs;
            NodeList entries = log.getChildNodes();
            for (int i = 0; i < entries.getLength(); i++) {
                if (entries.item(i).getNodeName().equals("vcs")) {
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"10\">" + Luntbuild.getTextContent(entries.item(i)) + "</td>";
                    html += "</tr>";
                    setCssIndex(getCssIndex() + 1);
                } else if (entries.item(i).getNodeName().equals("logentry")) {
                    String branch = entries.item(i).getAttributes().item(0).getNodeValue();
                    String author = "";
                    String date = "";
                    String msg = "";
                    String paths_html = "";
                    NodeList members = entries.item(i).getChildNodes();
                    for (int m = 0; m < members.getLength(); m++) {
                        Node member = members.item(m);
                        if (member.getNodeName().equals("author")) {
                            author = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("date")) {
                            date = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("msg")) {
                            msg = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("paths")) {
                            NodeList paths = member.getChildNodes();
                            for (int p = 0; p < paths.getLength(); p++) {
                                if (paths.item(p).getNodeName().equals("path")) {
                                    String action = paths.item(p).getAttributes().item(0).getNodeValue();
                                    String version = paths.item(p).getAttributes().item(1).getNodeValue();
                                    String path = Luntbuild.getTextContent(paths.item(p));
                                    // Try to create diff link
                                    path = clearcase.createLinkForFile(path, branch, version) + " " +
                                        clearcase.createLinkForDiff(path, branch, version);
                                    paths_html += "<tr class=\"" + getFileRowCssClass() + "\">";
                                    paths_html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                                    paths_html += "<td height=\"25\" colspan=\"1\">" + action + "</td>";
                                    paths_html += "<td height=\"25\" colspan=\"8\">" + path + "</td>";
                                    paths_html += "</tr>";
                                }
                            }
                        }
                    }
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                    html += "<td height=\"25\" colspan=\"5\">" + author + "</td>";
                    html += "<td height=\"25\" colspan=\"4\">"
                        + "<img src=\"images/date.gif\" width=\"16\" height=\"14\"> " + date.replaceAll("T"," ") + "</td>";
                    html += "</tr>";
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                    html += "<td height=\"25\" colspan=\"9\">" + msg + "</td>";
                    html += "</tr>";
                    html += paths_html;
                    setCssIndex(getCssIndex() + 1);
                }
            }
        } else if (classname.equals("com.luntsys.luntbuild.vcs.FileSystemAdaptor")) {
            FileSystemAdaptor filesystem = (FileSystemAdaptor) vcs;
            NodeList entries = log.getChildNodes();
            for (int i = 0; i < entries.getLength(); i++) {
                if (entries.item(i).getNodeName().equals("vcs")) {
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"10\">" + Luntbuild.getTextContent(entries.item(i)) + "</td>";
                    html += "</tr>";
                    setCssIndex(getCssIndex() + 1);
                } else if (entries.item(i).getNodeName().equals("logentry")) {
                    String date = "";
                    NodeList members = entries.item(i).getChildNodes();
                    for (int m = 0; m < members.getLength(); m++) {
                        Node member = members.item(m);
                        if (member.getNodeName().equals("date")) {
                            date = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("paths")) {
                            NodeList paths = member.getChildNodes();
                            for (int p = 0; p < paths.getLength(); p++) {
                                if (paths.item(p).getNodeName().equals("path")) {
                                    String action = paths.item(p).getAttributes().item(0).getNodeValue();
                                    String path = filesystem.createLinkForFile(Luntbuild.getTextContent(paths.item(p)));
                                    if (action.equals("directory")) {
                                        action = "<img src=\"images/folder.gif\" width=\"16\" height=\"16\" title=\"directory\"> ";
                                    } else if (action.equals("file")) {
                                        action = "<img src=\"images/file.gif\" width=\"16\" height=\"16\" title=\"file\"> ";
                                    } else {
                                        action = "";
                                    }
                                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                                    html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                                    html += "<td height=\"25\" colspan=\"7\">" + action + path + "</td>";
                                    html += "<td height=\"25\" colspan=\"2\">"
                                        + "<img src=\"images/date.gif\" width=\"16\" height=\"14\"> " + date.replaceAll("T"," ") + "</td>";
                                    html += "</tr>";
                                    setCssIndex(getCssIndex() + 1);
                                }
                            }
                        }
                    }
                }
            }
        } else if (classname.equals("com.luntsys.luntbuild.vcs.PerforceAdaptor")) {
            PerforceAdaptor p4 = (PerforceAdaptor) vcs;
            NodeList entries = log.getChildNodes();
            for (int i = 0; i < entries.getLength(); i++) {
                if (entries.item(i).getNodeName().equals("vcs")) {
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"10\">" + Luntbuild.getTextContent(entries.item(i)) + "</td>";
                    html += "</tr>";
                    setCssIndex(getCssIndex() + 1);
                } else if (entries.item(i).getNodeName().equals("logentry")) {
                    String changelist = p4.createLinkForChangelist(entries.item(i).getAttributes().item(0).getNodeValue());
                    String author = "";
                    String date = "";
                    String msg = "";
                    String tasks_html = "";
                    String paths_html = "";
                    NodeList members = entries.item(i).getChildNodes();
                    for (int m = 0; m < members.getLength(); m++) {
                        Node member = members.item(m);
                        if (member.getNodeName().equals("author")) {
                            author = p4.createLinkForUser(Luntbuild.getTextContent(member));
                        } else if (member.getNodeName().equals("date")) {
                            date = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("msg")) {
                            msg = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("tasks")) {
                            NodeList tasks = member.getChildNodes();
                            for (int t = 0; t < tasks.getLength(); t++) {
                                if (tasks.item(t).getNodeName().equals("task")) {
                                    String job = "";
                                    String user = p4.createLinkForUser(tasks.item(t).getAttributes().item(1).getNodeValue());
                                    String status = tasks.item(t).getAttributes().item(0).getNodeValue();
                                    String description = "";
                                    NodeList task_members = tasks.item(t).getChildNodes();
                                    for (int tm = 0; tm < task_members.getLength(); tm++) {
                                        if (task_members.item(tm).getNodeName().equals("name")) {
                                            job = p4.createLinkForJob(Luntbuild.getTextContent(task_members.item(tm)));
                                        } else if (task_members.item(tm).getNodeName().equals("description")) {
                                            description = Luntbuild.getTextContent(task_members.item(tm));
                                        }
                                    }
                                    tasks_html += "<tr class=\"" + getFileRowCssClass() + "\">";
                                    tasks_html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                                    tasks_html += "<td height=\"25\" colspan=\"9\"><img src=\"images/p4_job.gif\" width=\"16\" height=\"16\" title=\"job\"> "
                                        + job + " on " + date.replaceAll("T.*","") + " by " + user + " " + status + "</td>";
                                    tasks_html += "</tr>";
                                    tasks_html += "<tr class=\"" + getFileRowCssClass() + "\">";
                                    tasks_html += "<td height=\"25\" colspan=\"2\">&nbsp;</td>";
                                    tasks_html += "<td height=\"25\" colspan=\"8\">" + description + "</td>";
                                    tasks_html += "</tr>";
                                }
                            }
                        } else if (member.getNodeName().equals("paths")) {
                            NodeList paths = member.getChildNodes();
                            for (int p = 0; p < paths.getLength(); p++) {
                                if (paths.item(p).getNodeName().equals("path")) {
                                    String action = paths.item(p).getAttributes().item(0).getNodeValue();
                                    String revision = paths.item(p).getAttributes().item(1).getNodeValue();
                                    String path = p4.createLinkForFile(Luntbuild.getTextContent(paths.item(p)), revision);
                                    // Try to create diff link
                                    path = path + " " + p4.createLinkForDiff(Luntbuild.getTextContent(paths.item(p)), revision);
                                    if (action.equals("add")) {
                                        action = "<img src=\"images/p4_file_add.gif\" width=\"16\" height=\"16\" title=\"add\"> ";
                                    } else if (action.equals("branch")) {
                                        action = "<img src=\"images/p4_file_branch.gif\" width=\"16\" height=\"16\" title=\"branch\"> ";
                                    } else if (action.equals("delete")) {
                                        action = "<img src=\"images/p4_file_delete.gif\" width=\"16\" height=\"16\" title=\"delete\"> ";
                                    } else if (action.equals("edit")) {
                                        action = "<img src=\"images/p4_file_edit.gif\" width=\"16\" height=\"16\" title=\"edit\"> ";
                                    } else if (action.equals("integrate")) {
                                        action = "<img src=\"images/p4_file_integrate.gif\" width=\"16\" height=\"16\" title=\"integrate\"> ";
                                    } else {
                                        action = "";
                                    }
                                    paths_html += "<tr class=\"" + getFileRowCssClass() + "\">";
                                    paths_html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                                    paths_html += "<td height=\"25\" colspan=\"9\">" + action + path + "</td>";
                                    paths_html += "</tr>";
                                }
                            }
                        }
                    }
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                    html += "<td height=\"25\" colspan=\"2\">"
                        + "<img src=\"images/p4_changelist.gif\" width=\"16\" height=\"16\" title=\"changelist\"> " + changelist + "</td>";
                    html += "<td height=\"25\" colspan=\"3\">"
                        + "<img src=\"images/p4_user.gif\" width=\"16\" height=\"16\" title=\"user\"> " + author + "</td>";
                    html += "<td height=\"25\" colspan=\"4\">"
                        + "<img src=\"images/date.gif\" width=\"16\" height=\"14\"> " + date.replaceAll("T"," ") + "</td>";
                    html += "</tr>";
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"2\">&nbsp;</td>";
                    html += "<td height=\"25\" colspan=\"8\">" + msg + "</td>";
                    html += "</tr>";
                    html += tasks_html;
                    html += paths_html;
                    setCssIndex(getCssIndex() + 1);
                }
            }
        } else if (classname.equals("com.luntsys.luntbuild.vcs.SvnAdaptor") ||
                classname.equals("com.luntsys.luntbuild.vcs.SvnExeAdaptor")) {
            NodeList entries = log.getChildNodes();
            for (int i = 0; i < entries.getLength(); i++) {
                if (entries.item(i).getNodeName().equals("vcs")) {
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"10\">" + Luntbuild.getTextContent(entries.item(i)) + "</td>";
                    html += "</tr>";
                    setCssIndex(getCssIndex() + 1);
                } else if (entries.item(i).getNodeName().equals("logentry")) {
                    String revision = entries.item(i).getAttributes().item(0).getNodeValue();
                    String author = "";
                    String date = "";
                    String msg = "";
                    String paths_html = "";
                    NodeList members = entries.item(i).getChildNodes();
                    for (int m = 0; m < members.getLength(); m++) {
                        Node member = members.item(m);
                        if (member.getNodeName().equals("author")) {
                            author = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("date")) {
                            date = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("msg")) {
                            msg = Luntbuild.getTextContent(member);
                        } else if (member.getNodeName().equals("paths")) {
                            NodeList paths = member.getChildNodes();
                            for (int p = 0; p < paths.getLength(); p++) {
                                if (paths.item(p).getNodeName().equals("path")) {
                                    String action = paths.item(p).getAttributes().item(0).getNodeValue();
                                    String path = Luntbuild.getTextContent(paths.item(p));
                                    String path_comment = "";
                                    // Try to create diff link
                                    if (classname.equals("com.luntsys.luntbuild.vcs.SvnAdaptor"))
                                        path = ((SvnAdaptor) vcs).createLinkForFile(path, revision) + " " +
                                            ((SvnAdaptor) vcs).createLinkForDiff(path, revision);
                                    else if  (classname.equals("com.luntsys.luntbuild.vcs.SvnExeAdaptor"))
                                        path = ((SvnExeAdaptor) vcs).createLinkForFile(path, revision) + " " +
                                            ((SvnExeAdaptor) vcs).createLinkForDiff(path, revision);
                                    if (action.equals("A")) {
                                        if (paths.item(p).getAttributes().item(2) == null) {
                                            action = "<img src=\"images/svn_file_add.gif\" width=\"16\" height=\"16\" title=\"add\"> ";
                                        } else {
                                            action = "<img src=\"images/svn_file_copy.gif\" width=\"16\" height=\"16\" title=\"copy\"> ";
                                            String copy_path = paths.item(p).getAttributes().item(2).getNodeValue();
                                            String copy_revision = paths.item(p).getAttributes().item(3).getNodeValue();
                                            if (classname.equals("com.luntsys.luntbuild.vcs.SvnAdaptor"))
                                                copy_path = ((SvnAdaptor) vcs).createLinkForFile(copy_path, copy_revision);
                                            else if  (classname.equals("com.luntsys.luntbuild.vcs.SvnExeAdaptor"))
                                                copy_path = ((SvnExeAdaptor) vcs).createLinkForFile(copy_path, copy_revision);
                                            //path_comment += "<tr class=\"" + getFileRowCssClass() + "\">";
                                            //path_comment += "<td height=\"25\" colspan=\"2\">&nbsp;</td>";
                                            //path_comment += "<td height=\"25\" colspan=\"8\">(<i>copied from " + copy_path + "</i>)</td>";
                                            //path_comment += "</tr>";
                                            path += "<br/>(<i>copied from " + copy_path + ", r" + copy_revision + "</i>)";
                                        }
                                    } else if (action.equals("D")) {
                                        action = "<img src=\"images/svn_file_delete.gif\" width=\"16\" height=\"16\" title=\"delete\"> ";
                                    } else if (action.equals("M")) {
                                        action = "<img src=\"images/svn_file_modify.gif\" width=\"16\" height=\"16\" title=\"modify\"> ";
                                    } else if (action.equals("R")) {
                                        if (paths.item(p).getAttributes().item(2) == null) {
                                            action = "<img src=\"images/svn_file_move.gif\" width=\"16\" height=\"16\" title=\"replace\"> ";
                                        } else {
                                            action = "<img src=\"images/svn_file_move.gif\" width=\"16\" height=\"16\" title=\"move\"> ";
                                            String copy_path = paths.item(p).getAttributes().item(2).getNodeValue();
                                            String copy_revision = paths.item(p).getAttributes().item(3).getNodeValue();
                                            if (classname.equals("com.luntsys.luntbuild.vcs.SvnAdaptor"))
                                                copy_path = ((SvnAdaptor) vcs).createLinkForFile(copy_path, copy_revision);
                                            else if  (classname.equals("com.luntsys.luntbuild.vcs.SvnExeAdaptor"))
                                                copy_path = ((SvnExeAdaptor) vcs).createLinkForFile(copy_path, copy_revision);
                                            path_comment += "<tr class=\"" + getFileRowCssClass() + "\">";
                                            path_comment += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                                            path_comment += "<td height=\"25\" colspan=\"9\">(<i>moved from " + copy_path + "</i>)</td>";
                                            path_comment += "</tr>";
                                        }
                                    } else {
                                        action = "";
                                    }
                                    paths_html += "<tr class=\"" + getFileRowCssClass() + "\">";
                                    paths_html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                                    paths_html += "<td height=\"25\" colspan=\"9\">" + action + path + "</td>";
                                    paths_html += "</tr>";
                                    paths_html += path_comment;
                                }
                            }
                        }
                    }
                    if (classname.equals("com.luntsys.luntbuild.vcs.SvnAdaptor"))
                        revision = ((SvnAdaptor) vcs).createLinkForRevision(revision);
                    else if  (classname.equals("com.luntsys.luntbuild.vcs.SvnExeAdaptor"))
                        revision = ((SvnExeAdaptor) vcs).createLinkForRevision(revision);
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                    html += "<td height=\"25\" colspan=\"2\">"
                        + "<img src=\"images/svn_revision.gif\" width=\"16\" height=\"16\" title=\"revision\"> " + revision + "</td>";
                    html += "<td height=\"25\" colspan=\"3\">" + author + "</td>";
                    html += "<td height=\"25\" colspan=\"4\">"
                        + "<img src=\"images/date.gif\" width=\"16\" height=\"14\"> " + date.replaceAll("T"," ") + "</td>";
                    html += "</tr>";
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
                    html += "<td height=\"25\" colspan=\"9\">" + msg + "</td>";
                    html += "</tr>";
                    html += paths_html;
                    setCssIndex(getCssIndex() + 1);
                }
            }
        } else {
            NodeList entries = log.getChildNodes();
            for (int i = 0; i < entries.getLength(); i++) {
                if (entries.item(i).getNodeName().equals("vcs")) {
                    html += "<tr class=\"" + getFileRowCssClass() + "\">";
                    html += "<td height=\"25\" colspan=\"10\">" + Luntbuild.getTextContent(entries.item(i)) + "</td>";
                    html += "</tr>";
                    setCssIndex(getCssIndex() + 1);
                    break;
                }
            }
            html += "<tr class=\"" + getFileRowCssClass() + "\">";
            html += "<td height=\"25\" colspan=\"1\">&nbsp;</td>";
            html += "<td height=\"25\" colspan=\"9\"><span class=\"errorMsg\">"
                + "Unable to create revision log for \"" + classname + "\".</span></td>";
            html += "</tr>";
            setCssIndex(getCssIndex() + 1);
        }
        
        return html;
    }

    /** Test if file exists either as specified or in artifacts or publish directory
     * @param fname to test
     * @return the absolute path to the file or null
     */
    public String getArtifactsFile(String fname) {
        File f = new File(fname);
        if (f.exists() && f.isFile()) return f.getAbsolutePath();
        if (f.isAbsolute()) return null;
        String dir = getBuild().getArtifactsDir();
        f = new File(dir + File.separator + fname);
        if (f.exists() && f.isFile()) return f.getAbsolutePath();
        dir = getBuild().getPublishDir();
        f = new File(dir + File.separator + fname);
        if (f.exists() && f.isFile()) return f.getAbsolutePath();
        
        return null;
    }
}
