/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-8-10
 * Time: 20:07:55
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
package com.luntsys.luntbuild.test;

import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.vcs.SvnAdaptor;
import junit.framework.TestCase;
import ognl.OgnlException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import org.apache.tools.ant.Project;

/**
 * Test class {@link com.luntsys.luntbuild.utility.Luntbuild}
 */
public class TestLuntbuild extends TestCase {
    private File moduleDestDir;

    public static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static {
        INPUT_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public void testValidatePathElement() {
        try {
            Luntbuild.validatePathElement(null);
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement(" ");
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement("/");
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement("\\");
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement(":");
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement("*");
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement("?");
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement("\"");
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement("<");
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement(">");
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement("|");
            fail();
        } catch (ValidationException e) {
        }
        try {
            Luntbuild.validatePathElement("testcvs-development_hello.abc");
        } catch (ValidationException e) {
            fail();
        }
    }

    public void testGetLabelByVersion() {
        assertEquals("luntbuild-1_0-build-1", Luntbuild.getLabelByVersion("luntbuild 1.0 build 1"));
        assertEquals("v1_0", Luntbuild.getLabelByVersion("1.0"));
    }

    public void testIncreaseBuildVersion() {
        assertEquals("1.1", Luntbuild.increaseBuildVersion("1.0"));
        assertEquals("v1.1", Luntbuild.increaseBuildVersion("v1.0"));
        assertEquals("luntbuild 1.0 (build 101)", Luntbuild.increaseBuildVersion("luntbuild 1.0 (build 100)"));
    }

    public void testEvaluateExpression() {
        Object ognlRoot = new Object() {
            public String getYear() {
                return "2004";
            }

            public String getMonth() {
                return "12";
            }

            public String getDayOfMonth() {
                return "25";
            }

            public String getBuildVersion() {
                return "testcvs 1.1";
            }
        };
        try {
            assertEquals("testcvs-2004-12-25", Luntbuild.evaluateExpression(ognlRoot, "testcvs-${year}-${month}-${dayOfMonth}"));
            assertEquals("ant -Dluntbuild.buildVersion=\"testcvs 1.1\"",
                    Luntbuild.evaluateExpression(ognlRoot, "ant -Dluntbuild.buildVersion=\"${buildVersion}\""));
        } catch (OgnlException e) {
            fail();
        }
    }

    public void testIsVariablesContained() {
        assertEquals(false, Luntbuild.isVariablesContained("lb-1.0"));
    }

    public void testSvnRetrieve() {
        SvnAdaptor svnAdaptor = createSvnAdaptor();
        SvnAdaptor.SvnModule module = createModule(svnAdaptor);

        svnAdaptor.retrieveModule(moduleDestDir.getAbsolutePath(), module, crateAntProject());
    }

    private Project crateAntProject() {
        return new Project();
    }

    public void testSvnUpdate() {
        testSvnRetrieve();

        SvnAdaptor svnAdaptor = createSvnAdaptor();
        SvnAdaptor.SvnModule module = createModule(svnAdaptor);

        svnAdaptor.updateModule(moduleDestDir.getAbsolutePath(), module, crateAntProject());
    }

    public void testSvnLabel() {
        SvnAdaptor svnAdaptor = createSvnAdaptor();
        SvnAdaptor.SvnModule module = createModule(svnAdaptor);

        svnAdaptor.labelModule(null, module, "test" + (System.currentTimeMillis() / 1000), crateAntProject());
    }

    public void testSvnGetRevisionSince() throws Exception {
        SvnAdaptor svnAdaptor = createSvnAdaptor();
        createModule(svnAdaptor);

        Date sinceDate = INPUT_DATE_FORMAT.parse("2006-01-28T04:07:11Z");
        Revisions revisions = svnAdaptor.getRevisionsSince(sinceDate, null, crateAntProject());

        assertNotNull(revisions);
        assertEquals("Number of Logins", 1, revisions.getChangeLogins().size());
        assertTrue("Number of changed paths should be > 0", revisions.getChangeLogs().size() > 0);
    }

    private SvnAdaptor.SvnModule createModule(SvnAdaptor svnAdaptor) {
        SvnAdaptor.SvnModule module = (SvnAdaptor.SvnModule) svnAdaptor.createNewModule();
        module.setSrcPath("");
        svnAdaptor.getModules().add(module);
        return module;
    }

    private SvnAdaptor createSvnAdaptor() {
        SvnAdaptor svnAdaptor = new SvnAdaptor();
        svnAdaptor.setUrlBase("http://svn1.cvsdude.com/luntbuild-svn/luntbuild-demo");
        svnAdaptor.setUser("luntbuild-svn");
        svnAdaptor.setPassword("demo");
        svnAdaptor.setTrunk("trunk");
        svnAdaptor.setTags("tags");
        svnAdaptor.setBranches("branches");
        return svnAdaptor;
    }

    private File createTempDirectory() {
        try {
            File dir = File.createTempFile("luntbuild", "");
            dir.delete();
            dir.mkdir();
            dir.deleteOnExit();
            return dir;
        } catch (IOException e) {
            throw new RuntimeException("Error creating temp directory", e);
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        moduleDestDir = createTempDirectory();
    }

    protected void tearDown() throws Exception {
        if (moduleDestDir != null && moduleDestDir.exists()) {
            recurseDeleteDirectory(moduleDestDir);
        }
        super.tearDown();
    }

    private void recurseDeleteDirectory(File dir) {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                recurseDeleteDirectory(file);
            } else if (file.isFile()) {
                file.delete();
            }
        }
        dir.delete();
    }
}




