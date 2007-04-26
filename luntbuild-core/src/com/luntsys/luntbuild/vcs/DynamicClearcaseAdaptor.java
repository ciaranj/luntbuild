/*
 * Copyright luntsys (c) 2004-2005, Date: 2004-7-23 Time: 10:06
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
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
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.luntsys.luntbuild.vcs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.types.FileSet;

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.IStringProperty;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb20.DynamicClearcaseAdaptorFacade;
import com.luntsys.luntbuild.facades.lb20.VcsFacade;
import com.luntsys.luntbuild.utility.MyExecTask;

public class DynamicClearcaseAdaptor extends AbstractClearcaseAdaptor {

    static final long serialVersionUID = -5874620844092691665L;

    private String mvfsPath;

    private String projectPath;

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    /**
     * After creating a view, we need to setup its config spec.
     */
    protected void postCreateCcView(Schedule schedule, Project antProject) {
        setCcViewCfgSpec(schedule, getViewCfgSpec(), Project.MSG_INFO,
                antProject);
    }

    public void checkoutActually(Build build, Project antProject) {
        ensureViewPresent(build.getSchedule(), antProject);
        if (build.isCleanBuild()) {
            cleanViewPrivateFiles(build, antProject);
        }
        setCcViewCfgSpec(build.getSchedule(), getViewCfgSpec(),
                Project.MSG_INFO, antProject);
    }

    private void cleanViewPrivateFiles(Build build, Project antProject) {
        Commandline cmd = buildCleartoolExecutable();
        final String ctCmd = "lsprivate";
        cmd.createArgument().setValue(ctCmd);
        cmd.createArgument().setLine("-short -other");
        final ArrayList lines = new ArrayList();
        new MyExecTask(ctCmd, antProject, getClearcaseWorkDirRaw(build
                .getSchedule()), cmd, null, null, Project.MSG_INFO) {
            public void handleStdout(String line) {
                lines.add(line);
            }
        }.execute();

        if (lines.isEmpty()) {
            return;
        }

        // I don't know if this is strictly necessary given the relatively smart
        // mechanism used for deleting files below, but when I did this type of
        // thing via command line scripts, I always had to reverse sort the list
        // to ensure that directory elements were removed before the directories
        // themselves.
        Collections.sort(lines, new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = (String) o1;
                String s2 = (String) o2;
                return s2.compareTo(s1);
            }
        });

        Delete del = new Delete();
        del.setProject(antProject);

        del.setTaskType("delete");
        del.setTaskName("delete");
        del.setIncludeEmptyDirs(true);

        // Let's be a little smart here and group together all directory
        // elements that share a common parent. This should be less verbose.
        HashMap dirFileSets = new HashMap();
        for (Iterator iter = lines.iterator(); iter.hasNext();) {
            final File file = new File(((String) iter.next()));
            if (null == dirFileSets.get(file.getParent())) {
                FileSet fs = new FileSet();
                fs.setDir(file.getParentFile());
                del.addFileset(fs);
                dirFileSets.put(file.getParent(), fs);
            }
            FileSet fs = (FileSet) dirFileSets.get(file.getParent());
            fs.createInclude().setName(file.getName());
        }

        antProject.log("Removing view private files ...", Project.MSG_INFO);
        del.execute();
    }

    public VcsFacade constructFacade() {
        return new DynamicClearcaseAdaptorFacade();
    }

    public String getDisplayName() {
        return "Dynamic Clearcase";
    }

    public void label(Build build, Project antProject) {
        // TODO Auto-generated method stub
    }

    protected boolean isSnapshot() {
        return false;
    }

    protected void loadAdditionalStuffFromFacade(VcsFacade facade) {
        setMvfsPath(((DynamicClearcaseAdaptorFacade) facade).getMvfsPath());
        setProjectPath(((DynamicClearcaseAdaptorFacade) facade)
                .getProjectPath());
    }

    protected void postSetCs(Project antProject, String workingDir) {
        // EMPTY
    }

    protected void prepForHistory(Schedule workingSchedule, Project antProject,
            String workingDir) {
        // EMPTY
    }

    protected void saveAdditionalStuffToFacade(VcsFacade facade) {
        DynamicClearcaseAdaptorFacade dynFacade =
                (DynamicClearcaseAdaptorFacade) facade;
        dynFacade.setMvfsPath(getMvfsPath());
        dynFacade.setProjectPath(getProjectPath());
    }

    protected void validateClearcaseAdaptorProperties() {
        // EMPTY
    }

    protected String getClearcaseWorkDirRaw(Schedule schedule) {
        String sep = File.separator; // TODO:handle unset mvfs path
        StringBuffer result = new StringBuffer();
        String mvfs = getMvfsPath();
        if (null == mvfs || 0 == mvfs.length()) {
            if (System.getProperty("os.name").startsWith("Windows")) {
                mvfs = "M:";
            }
        }
        result.append(mvfs);
        if (!mvfs.endsWith(File.separator)) {
            result.append(sep);
        }
        result.append(getViewName(schedule));
        if (!(null == getProjectPath() || 0 == getProjectPath().length())) {
            result.append(sep).append(getProjectPath());
        }
        return result.toString();
    }

    public String getMvfsPath() {
        return mvfsPath;
    }

    public void setMvfsPath(String mvfsPath) {
        this.mvfsPath = mvfsPath;
    }
}
