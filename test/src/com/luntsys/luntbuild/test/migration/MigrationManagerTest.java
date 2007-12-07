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

package com.luntsys.luntbuild.test.migration;

import com.luntsys.luntbuild.migration.MigrationManager;
import junit.framework.TestCase;

import com.thoughtworks.xstream.XStream;
import java.io.*;

/**
 * Test class for <code>MigrationManager</code>.
 *
 * @author Jason Archer
 * @see MigrationManager
 */
public class MigrationManagerTest extends TestCase {

	/**
	 * Sets up objects used in test cases.
	 * 
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
	}

	/**
	 * Tears down objects used in test cases.
	 * 
	 * @throws Exception
	 */
	protected void tearDown() throws Exception {
		if (new File("test/resources/export.xml").exists()) {
			try {
				new File("test/resources/export.xml").delete();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/**
	 * Tests the import of XML data using plain version 1.3 file.
	 * 
	 * @throws Exception from {@link MigrationManager.importAsDataCollection12(File)}
	 * @see MigrationManager#importAsDataCollection12(File)
	 */
	public void testImportAsDataCollection12_v1_3_0() throws Exception {
		File xmlFile = new File("test/resources/luntbuild.1.3.0.xml");
		try {
			com.luntsys.luntbuild.facades.lb12.DataCollection datacollection =
				MigrationManager.importAsDataCollection12(xmlFile);
			assertTrue(datacollection != null);
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * Tests the import of XML data using version 1.3 build 32 file (host objects).
	 * 
	 * @throws Exception from {@link MigrationManager.importAsDataCollection12(File)}
	 * @see MigrationManager#importAsDataCollection12(File)
	 */
	public void testImportAsDataCollection12_v1_3_0_32() throws Exception {
		File xmlFile = new File("test/resources/luntbuild.1.3.0.32.xml");
		try {
			com.luntsys.luntbuild.facades.lb12.DataCollection datacollection =
				MigrationManager.importAsDataCollection12(xmlFile);
			assertTrue(datacollection != null);
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * Tests that <code>MigrationManager</code> can import data exported from <code>XStream</code>.
	 * 
	 * @throws Exception
	 * @see MigrationManager#importAsDataCollection12(File)
	 */
	public void testImportFromExport() throws Exception {
		export(com.luntsys.luntbuild.test.facades.lb12.DataCollectionHelper.createDataCollection12());

		File xmlFile = new File("test/resources/export.xml");
		try {
			com.luntsys.luntbuild.facades.lb12.DataCollection datacollection =
				MigrationManager.importAsDataCollection12(xmlFile);
			assertTrue(datacollection != null);
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	private void export(com.luntsys.luntbuild.facades.lb12.DataCollection dataCollection) throws IOException {
		String fileName = "test/resources/export.xml";
		OutputStream os = null;
		PrintWriter pw = null;
		try {
			os = new FileOutputStream(fileName);
			pw = new PrintWriter(os);
			XStream xstream = new XStream();
			xstream.alias("DataCollection", com.luntsys.luntbuild.facades.lb12.DataCollection.class);
			pw.print(xstream.toXML(dataCollection));
			pw.close();
			pw = null;
			os.close();
			os = null;
		} catch (IOException e) {
			throw e;
		} finally {
			if (pw != null)
				pw.close();
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					// ignores
				}
		}
	}
}
