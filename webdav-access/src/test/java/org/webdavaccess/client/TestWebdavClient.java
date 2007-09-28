/**
 * 
 */
package org.webdavaccess.client;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 * @author lubosp
 *
 */
public class TestWebdavClient extends TestCase {

	private static Log log = LogFactory.getLog(TestWebdavClient.class);
	
	private static final String webdavUrl = "http://localhost:8080/webdav-access/webdav";
	/**
	 * @param arg0
	 */
	public TestWebdavClient(String arg0) {
		super(arg0);
	}

	private static final WebdavClient webdavClient;

	static {
		webdavClient = new WebdavClient();
		File f = new File("src/test/uploaded.txt");
		if (!f.exists()) {
			try {
				PrintStream os = new PrintStream(f);
				os.println("This is uploaded file!");
				os.close();
			} catch (Exception e) {
				fail("Failed to create src/test/uploaded.txt: " + e.getMessage());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUploadFile() {
		try {
			boolean status = webdavClient.doUploadFile(webdavUrl + "/uploaded.txt", new File("src/test/uploaded.txt"));
			assertTrue("testUploadFile() - uploaded.txt", status);
		} catch (Exception e) {
			fail("Failed testUploadFile() - uploaded.txt: " + e.getMessage());
		}
	}

	public void testDownloadFile() {
		try {
			boolean status = webdavClient.doDownloadFile(webdavUrl + "/uploaded.txt", new File("src/test/downloaded.txt"));
			assertTrue("testDownloadFile() - downloaded.txt", status);
		} catch (Exception e) {
			fail("Failed testDownloadFile() - downloaded.txt: " + e.getMessage());
		}
		try {
			boolean status = webdavClient.doDownloadFile(webdavUrl + "/uploaded.txt", new File("src/test/uploaded.txt"));
			assertTrue("testDownloadFile() - uploaded.txt", status);
		} catch (Exception e) {
			fail("Failed testDownloadFile() - uploaded.txt: " + e.getMessage());
		}
	}

	public void testCreateFolder() {
		try {
			String dirname = new Date().toString().replaceAll(" ", "_").replaceAll(":", "_");
			boolean status = webdavClient.createFolder(webdavUrl + "/" + dirname);
			assertTrue("testCreateFolder() - " + dirname, status);
		} catch (Exception e) {
			fail("Failed testCreateFolder(): " + e.getMessage());
		}
	}

	public void testGetList() {
		try {
			Vector results = webdavClient.getList(webdavUrl);
			assertTrue("testGetList()", results.size() > 0);
			for (Iterator iter = results.iterator(); iter.hasNext();) {
				WebdavInfo info = (WebdavInfo) iter.next();
				System.out.println(info.print());
			}
		} catch (Exception e) {
			fail("Failed testGetList(): " + e.getMessage());
		}
	}

	public void testCopy() {
		try {
			boolean status = webdavClient.createFolder(webdavUrl + "/fromcopytest");
			status = webdavClient.createFolder(webdavUrl + "/tocopytest");
			String filename = new Date().toString().replaceAll(" ", "_").replaceAll(":", "_") + ".txt";
			status = webdavClient.doUploadFile(webdavUrl + "/fromcopytest/" + filename, new File("src/test/uploaded.txt"));
			status = webdavClient.doCopy(webdavUrl + "/fromcopytest/" + filename,
					webdavUrl + "/tocopytest/" + filename, true);
			assertTrue("testCopy() - " + filename, status);
		} catch (Exception e) {
			fail("Failed testCopy(): " + e.getMessage());
		}
	}

	public void testMove() {
		try {
			String filename = new Date().toString().replaceAll(" ", "_").replaceAll(":", "_") + ".txt";
			boolean status = webdavClient.doUploadFile(webdavUrl + "/fromcopytest/" + filename, new File("src/test/uploaded.txt"));
			Thread.sleep(2000);
			status = webdavClient.doMove(webdavUrl + "/fromcopytest/" + filename,
					webdavUrl + "/tocopytest/" + filename, true);
			if (!status)
				System.out.println("Move status: " + webdavClient.getCurrentStatusMessage());
		} catch (Exception e) {
			fail("Failed testMove(): " + e.getMessage());
		}
	}

	public void testDelete() {
		try {
			String dirname = new Date().toString().replaceAll(" ", "_").replaceAll(":", "_");
			String url = webdavUrl + "/" + dirname;
			boolean status = webdavClient.createFolder(url);
			assertTrue("testDelete() - exists: " + dirname, webdavClient.doExists(url));
			status = webdavClient.doDelete(url);
			assertTrue("testDelete() - " + dirname, status);
			assertTrue("testDelete() - deleted: " + dirname, !webdavClient.doExists(url));
		} catch (Exception e) {
			fail("Failed testDelete(): " + e.getMessage());
		}
	}

	public void testSetProperties() {
		String[] propNames = new String[] {"propname1", "propname2", "propname3"};
		String[] propValues = new String[] {"propvalue1", "propvalue2", "propvalue3"};
		try {
			Vector results = webdavClient.getList(webdavUrl + "/fromcopytest");
			for (Iterator iter = results.iterator(); iter.hasNext();) {
				WebdavInfo info = (WebdavInfo) iter.next();
				String url = info.getPath();
				boolean status = webdavClient.setProperties(url, propNames, propValues);
				System.out.println("Set properties status: " + webdavClient.getCurrentStatusMessage());
			}
		} catch (Exception e) {
			fail("Failed testSetProperties(): " + e.getMessage());
		}
	}

	public void testFindProperties() {
		String[] propNames = new String[] {"resourcetype"};
		try {
			Vector results = webdavClient.findProperties(webdavUrl + "/fromcopytest", propNames);
			assertTrue("testFindProperties()", results.size() > 0);
			results = webdavClient.findProperties(webdavUrl + "/fromcopytest", propNames, "infinity", "names");
			assertTrue("testFindProperties() names", results.size() > 0);
			results = webdavClient.findProperties(webdavUrl + "/fromcopytest", new String[] {"propname1", "propname2", "propname3"}, "infinity", "byname");
			boolean success = true;
			for (Iterator it = results.iterator(); it.hasNext();) {
				WebdavProperty wprop = (WebdavProperty) it.next();
				success &= wprop.getResource() != null;
				success &= propertyContains(wprop.getProperties(), new String[] {"propname1", "propname2", "propname3"});
			}
			assertTrue("testFindProperties() names", results.size() > 0 && success);
		} catch (Exception e) {
			fail("Failed testFindProperties(): " + e.getMessage());
		}
	}

	private boolean propertyContains(Properties props, String[] propNames) {
		if (props == null || props.size() == 0) return false;
		boolean success = true;
		Enumeration en = props.keys();
		while(en.hasMoreElements()) {
			String key = (String)en.nextElement();
			boolean hasKey = false;
			for(int i = 0; i < propNames.length; i++) {
				if (propNames[i].equals(key)) {
					hasKey = true;
					break;
				}
			}
			success &= hasKey;
		}
		return success;
	}
}
