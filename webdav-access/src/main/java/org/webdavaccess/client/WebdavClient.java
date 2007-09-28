package org.webdavaccess.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.methods.CopyMethod;
import org.apache.webdav.lib.methods.DepthSupport;
import org.apache.webdav.lib.methods.MkcolMethod;
import org.apache.webdav.lib.methods.MoveMethod;
import org.apache.webdav.lib.methods.PropFindMethod;
import org.apache.webdav.lib.methods.PropPatchMethod;
import org.apache.webdav.lib.methods.XMLResponseMethodBase;
import org.apache.webdav.lib.util.WebdavStatus;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.webdavaccess.exceptions.WebdavException;


/** 
 * Provides Webdav client.
 * 
 * Uses the Jakarta Commons HTTP Client classes.  See:
 * 
 *     http://jakarta.apache.org/commons/httpclient/features.html
 * 
 * Also uses the Jakarta Slide WebDAV client classes.  See:
 * 
 *      http://jakarta.apache.org/slide/
 */

public class WebdavClient {
	
	/** 
	 * Reuse a singleton HttpClient(). 
	 */
	
	private final HttpClient httpClient;

	private String userName;
	private String password;
	
	// Setup logging
	private static final String logging = "org.apache.commons.logging";
	
	static {
		System.setProperty(logging + ".Log", logging + ".impl.Log4JLogger");
		System.setProperty("log4j.logger.org.apache.commons.httpclient", "error");
		System.setProperty(logging + ".simplelog.showdatetime", "true");
		System.setProperty(logging + ".simplelog.log.httpclient.wire", "error");
		System.setProperty(logging + ".simplelog.log.org.apache.commons.httpclient", "error");
	}

	
	private int currentStatusCode = HttpStatus.SC_OK;
	
	/**
	 * Default constructor, no credentials are set
	 */
	public WebdavClient() {
		httpClient = new HttpClient();
	}
	
	/**
	 * Constuctor with user credentials
	 * @param user
	 * @param passwd
	 */
	public WebdavClient(String user, String passwd) {
		httpClient = new HttpClient();
		setCredentials(user, passwd);
	}
	
	/**
	 * Set user credentials
	 * @param user
	 * @param passwd
	 */
	public void setCredentials(String user, String passwd) {
		userName = user;
		password = passwd;
		if (userName != null && password != null) {
			httpClient.getParams().setAuthenticationPreemptive(true);
			HttpState state = httpClient.getState();
			Credentials credentials = new UsernamePasswordCredentials(userName, password);
			state.setCredentials(AuthScope.ANY, credentials);
		}
	}
	
	private Credentials setCredentials() {
		if (userName != null && password != null) {
			return new UsernamePasswordCredentials(userName, password);
		}
		return null;
	}
	
	/**
	 * @return current operation status code
	 */
	public int getCurrentStatusCode() {
		return currentStatusCode;
	}
	
	/**
	 * @return current operation status message
	 */
	public String getCurrentStatusMessage() {
		return WebdavStatus.getStatusText(currentStatusCode);
	}
	
	/**
	 * Download (GET) the file to the current directory.
	 * 
	 * @param url of the file in the storage
	 * @param file to download
	 * @return true if download success
	 * @throws StorageException
	 */
	public boolean doDownloadFile(String url, File file) throws WebdavException {
		try {
			HttpURL hrl = new HttpURL(url);
			WebdavResource wdr = new WebdavResource(hrl.getEscapedURIReference(), setCredentials(), true);
			boolean status = wdr.getMethod(file);
			currentStatusCode = wdr.getStatusCode();
			return status;
		} catch (Exception e) {
			currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			throw new WebdavException(e);
		}
	}

	/**
	 * Upload (PUT) the file to the current directory.
	 * 
	 * @param url of the file in the storage (folders have to exist)
	 * @param file to upload
	 * @return true if download success
	 * @throws StorageException
	 */
	public boolean doUploadFile(String url, File file) throws WebdavException {
		try {
			int idx = url.lastIndexOf('/');
			if (idx < 0) return false;
			
			String filename = url.substring(idx + 1);
			url = url.substring(0, idx);
			HttpURL hrl = new HttpURL(url);
			WebdavResource wdr = new WebdavResource(hrl.getEscapedURIReference(), setCredentials(), true);
			boolean status = wdr.putMethod(wdr.getPath() + "/" + filename, new FileInputStream(file));
			currentStatusCode = wdr.getStatusCode();
			return status;
		} catch (Exception e) {
			currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			throw new WebdavException(e);
		}
	}

	/**
	 * List the resources in the current collection (folder).
	 * 
	 * @param url of the collection (folder)
	 * @return vector of WebdavInfo resources in the collection (folder)
	 * @throws StorageException
	 */
	public Vector getList(String url) throws WebdavException {
		try {
			HttpURL hrl = new HttpURL(url);
			WebdavResource wdr = new WebdavResource(hrl.getEscapedURIReference(), setCredentials(), true);
			Vector results = new Vector();
	        if(!wdr.isCollection()) return results;
	        WebdavResource[] flist = wdr.listWebdavResources();
	        for (int i = 0; i < flist.length; i++) {
	        	WebdavResource fwdr = flist[i];
	        	WebdavInfo info = createWebdavInfo(fwdr);
				results.add(info);
			}
			currentStatusCode = HttpStatus.SC_OK;
	        return results;
		} catch (Exception e) {
			currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			throw new WebdavException(e);
		}
	}
	
	/**
	 * Return a WebdavInfo resource for the specified url.
	 * 
	 * @param url
	 * @return WebdavInfo resource for the url
	 * @throws StorageException
	 */
	public WebdavInfo getInfo(String url) throws WebdavException {
		try {
			HttpURL hrl = new HttpURL(url);
			WebdavResource wdr = new WebdavResource(hrl.getEscapedURIReference(), setCredentials(), true);
			WebdavInfo info = createWebdavInfo(wdr);
			currentStatusCode = HttpStatus.SC_OK;
	        return info;
		} catch (Exception e) {
			currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			throw new WebdavException(e);
		}
	}
	
	private WebdavInfo createWebdavInfo(WebdavResource fwdr) {
		WebdavInfo info = new WebdavInfo();
		info.setName(fwdr.getName());
		info.setDisplayName(fwdr.getDisplayName());
		long time = fwdr.getCreationDate();
		info.setCreationDate(new Date(time));
		time = fwdr.getGetLastModified();
		info.setLastModifiedDate(new Date(time));
		info.setLength(fwdr.getGetContentLength());
		info.setType(fwdr.getGetContentType());
		info.setPath(fwdr.toString());
		info.setOwner(fwdr.getOwner());
		info.setFolder(fwdr.isCollection());
		return info;
	}
	
	/** 
	  * COPY: Additional "destination", "overwrite", and "depth" parameters.
	  */
	
	public boolean doCopy(String uri, String responseBodyFileName, 
			String [] requestHeaderNames, String [] requestHeaderValues,
			String destination, boolean overwrite, String depth)
		throws WebdavException {
		Vector results = null;

		try {
			CopyMethod methodObj = new CopyMethod(
					uri, destination, overwrite);
	
			if (depth != null && !depth.equals("")){
				methodObj.setDepth(getDepthCode(depth));
			}
			
			results = executeMethodObject(methodObj, responseBodyFileName,
					requestHeaderNames, requestHeaderValues);
			
			currentStatusCode = methodObj.getStatusCode();
			return methodObj.getStatusCode() == HttpStatus.SC_OK || methodObj.getStatusCode() == HttpStatus.SC_CREATED;
		} catch (Exception e) {
			currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			throw new WebdavException(e);
		}
	}
		
	public boolean doCopy(String uri, String destination, boolean overwrite, String depth) throws WebdavException {
		return doCopy(uri, null, null, null, destination, overwrite, depth);
	}
	
	public boolean doCopy(String uri, String destination, boolean overwrite) throws WebdavException {
		return doCopy(uri, null, null, null, destination, overwrite, "infinity");
	}
	
	/**
	 * Locks the given resource
	 * 
	 * @param url resource to lock
	 * @param user to own the lock
	 * @param duration in sec
	 * @return status
	 * @throws StorageException
	 */
	public boolean doLock(String url, String user, int duration) throws WebdavException {
		try {
			HttpURL hrl = new HttpURL(url);
			WebdavResource wdr = new WebdavResource(hrl.getEscapedURIReference(), setCredentials(), true);
			boolean status = wdr.lockMethod(user, duration);
			currentStatusCode = wdr.getStatusCode();
			return status;
		} catch (Exception e) {
			currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			throw new WebdavException(e);
		}
	}

	/**
	 * Unlock given resource
	 * 
	 * @param url resource to unlock
	 * @return status
	 * @throws StorageException
	 */
	public boolean doUnock(String url) throws WebdavException {
		try {
			HttpURL hrl = new HttpURL(url);
			WebdavResource wdr = new WebdavResource(hrl.getEscapedURIReference(), setCredentials(), true);
			boolean status = wdr.unlockMethod();
			currentStatusCode = wdr.getStatusCode();
			return status;
		} catch (Exception e) {
			currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			throw new WebdavException(e);
		}
	}

	/**
	 * Make new collection (folder) MKCOL
	 * 
	 * @param uri
	 * @param responseBodyFileName
	 * @param requestHeaderNames
	 * @param requestHeaderValues
	 * @return true if make new collection (folder)
	 * @throws WebdavException
	 */
	public boolean doMkcol(String uri, String responseBodyFileName, 
			String [] requestHeaderNames, String [] requestHeaderValues)
	 throws WebdavException {
		Vector results = null;

		try {
			HttpMethod methodObj = new MkcolMethod(uri);
	
			results = executeMethodObject(methodObj, responseBodyFileName,
					requestHeaderNames, requestHeaderValues);
			
			currentStatusCode = methodObj.getStatusCode();
			return methodObj.getStatusCode() == HttpStatus.SC_CREATED || methodObj.getStatusCode() == HttpStatus.SC_OK;
		} catch (Exception e) {
			throw new WebdavException(e);
		}
	}
	
	/**
	 * Make new collection (folder) MKCOL
	 * 
	 * @param uri
	 * @return true if folder (collection) created
	 * @throws WebdavException
	 */
	public boolean createFolder(String uri) throws WebdavException {
		return doMkcol(uri, null, null, null);
	}
	
	 /** 
	  * MOVE: Additional "destination" and "overwrite" parameters.
	  */
	
	public boolean doMove(String uri, String responseBodyFileName, 
			String [] requestHeaderNames, String [] requestHeaderValues,
			String destination, boolean overwrite)
		throws WebdavException {
		Vector results = null;

		try {
			HttpMethod methodObj = new MoveMethod(uri, destination, overwrite);
	
			results = executeMethodObject(methodObj, responseBodyFileName,
					requestHeaderNames, requestHeaderValues);
			
			currentStatusCode = methodObj.getStatusCode();
			return methodObj.getStatusCode() == HttpStatus.SC_OK ||
			methodObj.getStatusCode() == HttpStatus.SC_CREATED ||
			methodObj.getStatusCode() == HttpStatus.SC_NO_CONTENT;
		} catch (Exception e) {
			throw new WebdavException(e);
		}
	}
	
	 /** 
	  * MOVE: Additional "destination" and "overwrite" parameters.
	  */
	
	public boolean doMove(String uri, String destination, boolean overwrite)
		throws WebdavException {
		return doMove(uri, null, null, null, destination, overwrite);
	}
	
	/**
	 * Delete given resource
	 * 
	 * @param url resource to delete
	 * @return status
	 * @throws WebdavException
	 */
	public boolean doDelete(String url) throws WebdavException {
		try {
			HttpURL hrl = new HttpURL(url);
			WebdavResource wdr = new WebdavResource(hrl.getEscapedURIReference(), setCredentials(), true);
			boolean status = wdr.deleteMethod();
			currentStatusCode = wdr.getStatusCode();
			return status;
		} catch (Exception e) {
			currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			throw new WebdavException(e);
		}
	}

	/**
	 * Check if given resource exists
	 * 
	 * @param url resource to check
	 * @return true if exists
	 * @throws WebdavException
	 */
	public boolean doExists(String url) throws WebdavException {
		WebdavResource wdr = null;
		try {
			HttpURL hrl = new HttpURL(url);
			wdr = new WebdavResource(hrl.getEscapedURIReference(), setCredentials(), true);
			wdr.setProperties(DepthSupport.DEPTH_0);
			boolean status = wdr.exists();
			currentStatusCode = wdr.getStatusCode();
			return status;
		} catch (Exception e) {
			if (wdr != null)
				currentStatusCode = wdr.getStatusCode();
			else
				currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			if (currentStatusCode == HttpStatus.SC_NOT_FOUND)
				return false;
			throw new WebdavException(e);
		}
	}

	/**
	 * Check if given resource exists
	 * 
	 * @param url resource to check
	 * @return true if exists
	 * @throws WebdavException
	 */
	public boolean doIsCollection(String url) throws WebdavException {
		WebdavResource wdr = null;
		try {
			HttpURL hrl = new HttpURL(url);
			wdr = new WebdavResource(hrl.getEscapedURIReference(), setCredentials(), true);
			wdr.setProperties(DepthSupport.DEPTH_0);
			boolean status = wdr.isCollection();
			currentStatusCode = wdr.getStatusCode();
			return status;
		} catch (Exception e) {
			if (wdr != null)
				currentStatusCode = wdr.getStatusCode();
			else
				currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			if (currentStatusCode == HttpStatus.SC_NOT_FOUND)
				return false;
			throw new WebdavException(e);
		}
	}

	 /** 
	  * PROPFIND:  Additional property name, depth, and type parameters.
	  */
	
	public Vector doPropFind(String uri, String responseBodyFileName, 
			String [] requestHeaderNames, String [] requestHeaderValues,
			String [] propNames, String depth, String type)
		throws WebdavException {
		Vector results = null;

		try {
			PropFindMethod methodObj = new PropFindMethod(uri) {
				public void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException, HttpException {
					super.addRequestHeaders(state, conn);
					// force content length to be generated
					super.setRequestHeader("Content-Length", Integer.toString(generateRequestBody().length()));
				}
			};
		
			if (depth != null && !depth.equals("")){
				methodObj.setDepth(getDepthCode(depth));
			}
			
			if (type != null && !type.equals("")){
				methodObj.setType(getPropFindTypeCode(type));
			}
			
			if (propNames != null && propNames.length > 0){
				// Go through some contortions to get an Enumeration 
				Enumeration en = (new Vector(Arrays.asList(propNames))).elements();
				methodObj.setPropertyNames(en);
			}
			
			results = executeMethodObject(methodObj, responseBodyFileName,
					requestHeaderNames, requestHeaderValues);
	
			// get property info from response document
			if (methodObj.getResponseDocument()!= null && 
					methodObj.getResponseDocument().getDocumentElement() != null) {
				// convert the response document into a list of items
				results.set(results.size()-1, storeResponseDocument(methodObj));
			}
	
			return results;
		} catch (Exception e) {
			currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			throw new WebdavException(e);
		}
	}
	
	/**
	 * Returns vector of matching WebdavProperty values.<br/><br/>
	 * 
	 * If <b>all</b> <i>type</i> is specified all properties for matching resources are returned.<br/>
	 * If <b>names</b> <i>type</i> is specified only property names are returned, property values in WebdavProperty Properties are empty.<br/>
	 * If <b>byname</b> <i>type</i> is specified only properties with names specified in <i>propNames</i> for matching resources are returned.<br/>
	 * 
	 * @param uri resource uri
	 * @param propNames property names, only used for "byname" type
	 * @param depth ("0", "1" or "infinity")
	 * @param type ("all", "names", "byname")
	 * @return vector of matching WebdavProperty values
	 * 
	 * @throws WebdavException
	 * 
	 * @see #com.insightful.webdavaccess.client.WebdavProperty
	 */
	public Vector findProperties(String uri, String [] propNames, String depth, String type)
		throws WebdavException {
		Vector results = doPropFind(uri, null, null, null, propNames, depth, type);
		return getMatchingResources(results, propNames);
	}
	
	public Vector findProperties(String uri, String [] propNames)
	throws WebdavException {
		return findProperties(uri, propNames, "infinity", "all");
	}

	private static Vector storeResponseDocument(XMLResponseMethodBase methodObj) {
		Element multistatus = methodObj.getResponseDocument().getDocumentElement();
		Vector names = new Vector(), values = new Vector();
		Vector properties = new Vector();
		properties.add(new String [] {"Files", "Properties"});
		properties.add(names);
		properties.add(values);
		
		NodeList responses = multistatus.getChildNodes();
		for (int r=0; r<responses.getLength(); r++) {
			Node response = responses.item(r);
			
			NodeList responseChildren = response.getChildNodes();
			Vector propertyList = new Vector(), propNames = new Vector(), propInfo = new Vector();
			propertyList.add(propNames);
			propertyList.add(propInfo);
			
			for (int rc=0; rc<responseChildren.getLength(); rc++) {
				Node propStat = responseChildren.item(rc);
				String propStatName = propStat.getLocalName();
				if (propStatName==null) continue;
				
				if (propStatName.equals("href")) {
					names.add(propStat.getFirstChild().getNodeValue());
					values.add(propertyList);
				} else if (propStatName.equals("propstat")) {
					NodeList propsOrStatii = propStat.getChildNodes();
					String statusString = "";
					for (int p=0; p<propsOrStatii.getLength(); p++) {
						Node propOrStatus = propsOrStatii.item(p);
						String name = propOrStatus.getLocalName();
						if (name == null) continue;
						
						if (name.equals("status")) {
							statusString = propOrStatus.getFirstChild().getNodeValue();
							break;
						}
					}

					for (int p=0; p<propsOrStatii.getLength(); p++) {
						Node propOrStatus = propsOrStatii.item(p);
						String name = propOrStatus.getLocalName();
						if (name == null) continue;
						
						if (name.equals("prop")) {
							storeProperties(propOrStatus, statusString, propNames, propInfo);
						}
					}
				}
			}
		}
		return properties;
	}
	
	private static Vector getMatchingResources(Vector results, String [] propNames) {
		Vector empty = new Vector();
		if (results == null || results.size() != 6) return empty;
		Vector tmp = (Vector)results.get(5);
		if (tmp.size() != 3) return empty;
		
		Vector paths = (Vector)tmp.get(1);		
		Vector propVect = (Vector)tmp.get(2);
		Vector webdavProps = new Vector();
		for (int i = 0; i < propVect.size(); i++) {
			WebdavProperty wpr = new WebdavProperty((String)paths.get(i), (Vector)propVect.get(i));
			if (wpr != null && wpr.getProperties().size() > 0)
				webdavProps.add(wpr);
		}
		return webdavProps;
	}
	
	private static Vector storeLockDiscovery(Node lockDiscovery) {
		Vector lockDiscoveryList = new Vector(), names = new Vector(), values = new Vector();
		lockDiscoveryList.add(names);
		lockDiscoveryList.add(values);
		boolean returnEmpty = true;
		
		NodeList locks = lockDiscovery.getChildNodes();
		for (int l=0, i=0; l<locks.getLength(); l++) {
			Node lock = locks.item(l);
			String lName = lock.getLocalName();
			if (lName == null) continue;

			Vector entryProps = new Vector(), entrynames = new Vector(), entryvalues = new Vector();
			entryProps.add(entrynames);
			entryProps.add(entryvalues);

			names.add(Integer.toString(i++));
			values.add(entryProps);

			NodeList attributes = lock.getChildNodes();
			for (int a=0; a<attributes.getLength(); a++) {
				Node attribute = attributes.item(a);
				String aName = attribute.getLocalName();
				if (aName == null) continue;

				returnEmpty = false;
				entrynames.add(lName);
				entryvalues.add(getUnknownChildValue(attribute));				
			}
		}
		
		if (returnEmpty) return new Vector();
		return lockDiscoveryList;
	}
	
	private static Vector storeSupportedLock(Node supportedLock) {
		Vector supportedLockList = new Vector(), names = new Vector(), values = new Vector();
		supportedLockList.add(names);
		supportedLockList.add(values);
		boolean returnEmpty = true;
		
		NodeList lockEntries = supportedLock.getChildNodes();
		for (int l=0, i=0; l<lockEntries.getLength(); l++) {
			Node entry = lockEntries.item(l);
			String eName = entry.getLocalName();
			if (eName == null) continue;
			
			Vector entryProps = new Vector(), entrynames = new Vector(), entryvalues = new Vector();
			entryProps.add(entrynames);
			entryProps.add(entryvalues);
			
			names.add(new Integer(i++));
			values.add(entryProps);

			NodeList attributes = entry.getChildNodes();
			for (int a=0; a<attributes.getLength(); a++) {
				Node attribute = attributes.item(a);
				String aName = attribute.getLocalName();
				if (aName == null) continue;
				
				returnEmpty = false;
				entrynames.add(aName);
				entryvalues.add(getUnknownChildValue(attribute));
			}
		}
		
		if (returnEmpty) return new Vector();
		return supportedLockList;
	}
	
	private static void storeProperties(Node prop, String status, Vector names, Vector info) {
		NodeList attributes = prop.getChildNodes();
		for (int a=0; a<attributes.getLength(); a++) {
			Node attribute = attributes.item(a);
			String name = attribute.getLocalName();
			if (name == null) continue;
			
			names.add(name);
			
			Vector forEachName = new Vector(), values = new Vector();
			forEachName.add(new String [] {"Value", "Status"});
			forEachName.add(values);
			info.add(forEachName);
			
			if (name.equals("lockdiscovery")) {
				values.add(storeLockDiscovery(attribute));
			} else if (name.equals("supportedlock")) {
				values.add(storeSupportedLock(attribute));
			} else {
				values.add(getUnknownChildValue(attribute));
			}
			values.add(status);
		}
	}
	
	private static String getUnknownChildValue(Node node) {
		String propValue = "";
		
		Node valueNode = node.getFirstChild();
		while (propValue.length()==0 && valueNode != null) {
			if (valueNode instanceof Text) propValue = valueNode.getNodeValue().trim();
			else {
				if (!valueNode.hasChildNodes()) propValue = valueNode.getLocalName();
				else propValue = valueNode.getFirstChild().getNodeValue();
			}
			valueNode = valueNode.getNextSibling();
		}

		return propValue;
	}
	
	 /** 
	  * PROPPATCH: Additional property name and value parameters.
	  */
	
	public boolean doPropPatch(String uri, String responseBodyFileName, 
			String [] requestHeaderNames, String [] requestHeaderValues,
			String [] propNames, String [] propValues)
		throws WebdavException {
		Vector results = null;

		try {
			PropPatchMethod methodObj = new PropPatchMethod(uri) {
				public void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException, HttpException {
					super.addRequestHeaders(state, conn);
					// force content length to be generated
					super.setRequestHeader("Content-Length", Integer.toString(generateRequestBody().length()));
				}
			};
			addPropertiesToSet(methodObj, propNames, propValues);
			
			results = executeMethodObject(methodObj, responseBodyFileName,
					requestHeaderNames, requestHeaderValues);
			
			// get property info from response document
			if (methodObj.getResponseDocument()!= null && 
					methodObj.getResponseDocument().getDocumentElement() != null) {
				// convert the response document into a list of items
				results.set(results.size()-1, storeResponseDocument(methodObj));
			}
	
			currentStatusCode = methodObj.getStatusCode();
			return currentStatusCode == HttpStatus.SC_OK;
		} catch (Exception e) {
			currentStatusCode = HttpStatus.SC_METHOD_FAILURE;
			throw new WebdavException(e);
		}
	}
	
	public boolean setProperties(String uri, String [] propNames, String [] propValues) throws WebdavException {
		return doPropPatch(uri, null, null, null, propNames, propValues);
	}
	
	/**
	 * Execute an HttpMethod object.  This routine is responsible for
	 * setting the additional request header values, executing the
	 * method, and packaging up the results for S-PLUS.
	 */
	
	private Vector executeMethodObject(HttpMethod methodObj, 
			String responseBodyFileName, 
			String [] requestHeaderNames, String [] requestHeaderValues)
		throws FileNotFoundException, IOException {

		Vector results = null;
	
		// Add request headers if specified
		setRequestHeaders(methodObj, requestHeaderNames, requestHeaderValues);

		if (userName != null && password != null) methodObj.setDoAuthentication(true);
		try {
			// Execute the method
			httpClient.executeMethod(methodObj);

			// Gather the results up into an Object [] array
			results = packageResults(methodObj, responseBodyFileName);
		}
		finally {
			methodObj.releaseConnection();
		}
		
		return results;
	}
		
	/**
	 *  Set request headers if specified.
	 */
	
	private static void setRequestHeaders(HttpMethod methodObj, 
			String [] requestHeaderNames, String [] requestHeaderValues){
		if (requestHeaderNames != null && requestHeaderValues != null) {
			int numNames = requestHeaderNames.length;
			if (numNames > 0 && numNames == requestHeaderValues.length){
				for (int i = 0; i < numNames; i++){
					methodObj.setRequestHeader(requestHeaderNames[i],
							requestHeaderValues[i]);
				}
			}
		}
	}
	
	/**
	 *  Add properties to set if specified.
	 */
	
	private static void addPropertiesToSet(PropPatchMethod methodObj, 
			String [] propNames, String [] propValues){
		if (propNames != null && propValues != null) {
			int numNames = propNames.length;
			if (numNames > 0 && numNames == propValues.length){
				for (int i = 0; i < numNames; i++){
					methodObj.addPropertyToSet(propNames[i], propValues[i]);
				}
			}
		}
	}
	
	/**
	 * Convert string value of "0", "1", or "infinity" to the appropriate
	 * depth int flag.
	 */
	
	private static int getDepthCode(String depth){
		int result;
		
		depth = depth.toLowerCase();		
		if (depth.equals("0")){
			result = DepthSupport.DEPTH_0;
		} else if (depth.equals("1")){
			result = DepthSupport.DEPTH_1;				
		} else if (depth.equals("infinity")){
			result = DepthSupport.DEPTH_INFINITY;				
		} else {
			throw new RuntimeException("Depth parameter must be '0', '1', or 'infinity'.");
		}
		return result;
	}
	
	/**
	 * Convert string value of "shared" or "exclusive" to the appropriate
	 * scope short flag.
	 */
	
	private static int getPropFindTypeCode(String type){
		int result;
		
		type = type.toLowerCase();		
		if (type.equals("all")){
			result = PropFindMethod.ALL;
		} else if (type.equals("names")){
			result = PropFindMethod.NAMES;				
		} else if (type.equals("byname")){
			result = PropFindMethod.BY_NAME;				
		} else {
			throw new RuntimeException("Scope parameter must be 'all', 'names', or 'byname'.");
		}
		return result;
	}
	
	/**
	 * Gather up various information from the executed HttpMethod object
	 * and return it to S-PLUS as an Object array. 
	 */
	
	private static String [] RESULT_ELEMENTS = new String [] {
		"Status", "RequestHeaders", "ResponseHeaders", 
		"ResponseFooters", "Body"
	};
	
	private static Vector packageResults(HttpMethod methodObj,
			String responseBodyFileName) 
		throws IOException {
		
		Vector results = new Vector(RESULT_ELEMENTS.length + 1);
		results.add(RESULT_ELEMENTS);
		
		// Add the status info: status code, status line, status text
		Object [] statusInfo = new Object [] {
				new Integer(methodObj.getStatusCode()),
				methodObj.getStatusText()
		};
		
		results.add(statusInfo);

		// Header array temp var
		Header [] headers = null;
		String [] headerStrings = null;
		int headerCount = 0;
		
		// Add String array containing the request headers
		headers = methodObj.getRequestHeaders();
		headerCount = headers.length;
		headerStrings = new String[headerCount];

		for (int i = 0; i < headerCount; i++){
			headerStrings[i] = headers[i].toString();
		}
		results.add(headerStrings);
		
		// Add the response headers
		headers = methodObj.getResponseHeaders();
		headerCount = headers.length;
		headerStrings = new String[headerCount];

		for (int i = 0; i < headerCount; i++){
			headerStrings[i] = headers[i].toString();
		}
		results.add(headerStrings);
		
		// Add the response footers
		headers = methodObj.getResponseFooters();
		headerCount = headers.length;
		headerStrings = new String[headerCount];

		for (int i = 0; i < headerCount; i++){
			headerStrings[i] = headers[i].toString();
		}
		results.add(headerStrings);
		
		// Add the body as a string or write it to a file, depending
		// on whether the responseBodyFileName is specified.
		String bodyStr = null;
		
		if(responseBodyFileName==null || responseBodyFileName.length() == 0){
			bodyStr = methodObj.getResponseBodyAsString();			
		} else {
			InputStream in = methodObj.getResponseBodyAsStream();
			OutputStream out = new FileOutputStream(responseBodyFileName);

			try {
				transferBytes(in, out);
			}
			finally {
				in.close();
				out.close();
			}
			
			bodyStr = responseBodyFileName;
		}
		results.add(bodyStr);	

		return results;
	}

    /**
     * Transfer a file from a FileInputStream to a FileOutputStream.  This
     * is a straight transfer of the bytes.  It reads from the input stream
     * as long as there is more to read, and writes the bytes to the
     * output stream.  This method does not open or close the file streams,
     * it just performs the byte transfer.
     */

    private static void transferBytes(InputStream sourceStream,
                              OutputStream targetStream)
        throws IOException {

        int blockSize = 4096;
        byte [] buf = new byte[blockSize];
        int numRead = 0;

        while(numRead != -1){
            numRead = sourceStream.read(buf, 0, buf.length);

            if (numRead != -1){
                targetStream.write(buf, 0, numRead);
            }
        }
    }
    
}
