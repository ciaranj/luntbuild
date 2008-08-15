/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-3-24
 * Time: 12:46:42
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

import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.OgnlHelper;
import com.luntsys.luntbuild.utility.ValidationException;
import junit.framework.TestCase;

/**
 * Test against {@link com.luntsys.luntbuild.db.Schedule} class
 *
 * @author robin shine
 */
public class TestSchedule extends TestCase {
	OgnlHelper system;
	Schedule schedule;

	protected void setUp() throws Exception {
		OgnlHelper.setAntProject(Luntbuild.createAntProject());
		OgnlHelper.setTestMode(true);
		schedule = new Schedule();
		schedule.setProject(new Project());
		schedule.getProject().setVariables("releaseIteration=1\n");
		system = new OgnlHelper();
	}

	public void testBuildVersionEvaluation() throws Exception {
		assertEquals("luntbuild-" + system.getShortYear() + "-" + system.getNumericMonth() +
				"-" + system.getDayOfMonth(), Luntbuild.evaluateExpression(schedule,
						"luntbuild-${system.shortYear}-${system.numericMonth}-${system.dayOfMonth}"));

		assertEquals("luntbuild-1.1.1", Luntbuild.evaluateExpression(schedule, "luntbuild-1.1.${project.var['releaseIteration']}"));
	}

	public void testValidateBuildVersion() {
		try {
			schedule.validateBuildVersion(null);
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion(" ");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("/1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("\\1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion(":1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("*1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("?1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("\"1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("<1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion(">1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("|1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("$1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion(",1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion(";1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("@1");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("luntbuild");
			fail();
		} catch (ValidationException e) {
		}
		try {
			schedule.validateBuildVersion("1.0");
		} catch (ValidationException e) {
			fail();
		}
	}
}
