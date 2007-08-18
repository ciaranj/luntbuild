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

package com.luntsys.luntbuild.test.utility;

import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.ValidationException;
import junit.framework.TestCase;
import ognl.OgnlException;

/**
 * Test class for <code>Luntbuild</code>.
 * 
 * @see Luntbuild
 */
public class LuntbuildTest extends TestCase {

	/**
	 * Tests that <code>Luntbuild</code> properly validates file paths.
	 * 
	 * @see Luntbuild#validatePathElement(String)
	 */
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

	/**
	 * Tests creation of labels based on build versions.
	 * 
	 * @see Luntbuild#getLabelByVersion(String)
	 */
	public void testGetLabelByVersion() {
		assertEquals("luntbuild-1_0-build-1", Luntbuild.getLabelByVersion("luntbuild 1.0 build 1"));
		assertEquals("v1_0", Luntbuild.getLabelByVersion("1.0"));
	}

	/**
	 * Tests that build versions are properly incremented.
	 * 
	 * @see Luntbuild#increaseBuildVersion(String)
	 */
	public void testIncreaseBuildVersion() {
		assertEquals("1.1", Luntbuild.increaseBuildVersion("1.0"));
		assertEquals("v1.1", Luntbuild.increaseBuildVersion("v1.0"));
		assertEquals("luntbuild 1.0 (build 101)", Luntbuild.increaseBuildVersion("luntbuild 1.0 (build 100)"));
	}

	/**
	 * Tests evaluation of OGNL expressions.
	 * 
	 * @see Luntbuild#evaluateExpression(Object, String)
	 */
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

	/**
	 * Tests detection of OGNL variables in an expression.
	 * 
	 * @see Luntbuild#isVariablesContained(String)
	 */
	public void testIsVariablesContained() {
		assertEquals(false, Luntbuild.isVariablesContained("lb-1.0"));
	}
}
