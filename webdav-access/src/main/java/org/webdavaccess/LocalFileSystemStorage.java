/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.webdavaccess;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.webdavaccess.exceptions.WebdavException;

/**
 * Reference Implementation of IWebdavStorage
 * 
 * @author joa
 * @author re
 * @author lubosp
 */
public class LocalFileSystemStorage implements IWebdavStorage {

	private static Log log = LogFactory.getLog(LocalFileSystemStorage.class);
    
	private static final String ROOTPATH_PARAMETER = "rootpath";
	private static final String DEBUG_PARAMETER = "storeDebug";
	private static final int MAX_PROPERTIES_CACHE_SIZE = 5000;
	private static final String CUSTOM_PROPERTIES_STORAGE = ".properties";
	private static final String PROPERTIES_STORAGE_PATH_SEPARATOR = "|";

	private static final String SYSTEM_PROPERTY_PREFIX = "__";
	private static final String CREATION_DATE_PROPERTY = SYSTEM_PROPERTY_PREFIX + "CreationDate" + SYSTEM_PROPERTY_PREFIX;
	private static final String CREATION_DATE_FORMAT = "yyyyMMddHHmmss";
	
	private static int BUF_SIZE = 50000;
	private static File root = null;
	private static int debug = -1;
	
	private String servletName;

	private MRUCache mPropertiesCache;
	
	public void begin(HttpServletRequest req, Hashtable parameters, String defaultStorageLocation)
			throws WebdavException {
		if (debug == -1) {
			String debugString = (String) parameters.get(DEBUG_PARAMETER);
			if (debugString == null) {
				debug = 0;
			}else{
				debug = Integer.parseInt(debugString);
			}
		}
		if (debug == 1) log.debug("LocalFileSystemStore.begin()");
		
		if (root == null) {

			String rootPath = (String) parameters.get(ROOTPATH_PARAMETER);
			if (rootPath != null) {
				try {
					InitialContext context = new InitialContext();
					Context environment = (Context) context.lookup("java:comp/env");
					rootPath = (String) environment.lookup(rootPath);
				} catch (Exception ex) {
					// Not an context env. use the original as root
				}
			}
			if (rootPath == null && defaultStorageLocation != null) {
				rootPath = defaultStorageLocation;
			}
			
			if (rootPath == null) {
				throw new WebdavException("missing parameter: "
						+ ROOTPATH_PARAMETER);
			}
			
			root = new File(rootPath);
			if (!root.exists()) {
				if (!root.mkdirs()) {
					throw new WebdavException(ROOTPATH_PARAMETER + ": " + root
							+ " does not exist and could not be created");
				} 
			}
			
			servletName = req.getServletPath();
		}
		
		mPropertiesCache = new MRUCache(MAX_PROPERTIES_CACHE_SIZE);
		
	}

	public void commit() throws WebdavException {
		// do nothing
		if (debug == 1)
			log.debug("LocalFileSystemStore.commit() - do nothing");
	}

	public void rollback() throws WebdavException {
		// do nothing
		if (debug == 1)
			log.debug("LocalFileSystemStore.rollback() - do nothing");

	}

	public boolean objectExists(String uri) throws WebdavException {
		uri = normalize(uri);
		File file = new File(root, uri);
		if (debug == 1)
			log.debug("LocalFileSystemStore.objectExists(" + uri + ")=" + file.exists());
		return file.exists();
	}

	public boolean isFolder(String uri) throws WebdavException {
		uri = normalize(uri);
		File file = new File(root, uri);
		if (debug == 1)
			log.debug("LocalFileSystemStore.isFolder(" + uri + ")=" + file.isDirectory());
		return file.isDirectory();
	}

	public boolean isResource(String uri) throws WebdavException {
		uri = normalize(uri);
		File file = new File(root, uri);
		if (debug == 1)
			log.debug("LocalFileSystemStore.isResource(" + uri + ") " + file.isFile());
		return file.isFile();
	}

	/**
	 * @throws IOException
	 *             if the folder cannot be created
	 */
	public void createFolder(String uri) throws WebdavException {
		if (debug == 1)
			log.debug("LocalFileSystemStore.createFolder(" + uri + ")");
		uri = normalize(uri);
		File file = new File(root, uri);
		if (!file.mkdir())
			throw new WebdavException("cannot create folder: " + uri);
	}

	/**
	 * @throws IOException
	 *             if the resource cannot be created
	 */
	public void createResource(String uri) throws WebdavException {
		if (debug == 1)
			log.debug("LocalFileSystemStore.createResource(" + uri + ")");
		uri = normalize(uri);
		File file = new File(root, uri);
		try {
			if (!file.createNewFile())
				throw new WebdavException("cannot create file: " + uri);
			Properties props = new Properties();
			props.setProperty(CREATION_DATE_PROPERTY, new SimpleDateFormat(CREATION_DATE_FORMAT).format(new Date()));
			setCustomProperties(uri, props);
		} catch (IOException e) {
			throw new WebdavException(e);
		}
	}

	/**
	 * @throws IOException
	 *             if the resource cannot be created
	 */
	public void copyResource(String src, String dest) throws WebdavException {
		if (debug == 1)
			log.debug("LocalFileSystemStore.createResource(" + dest + ")");
		dest = normalize(dest);
		File file = new File(root, dest);
		try {
			if (!file.createNewFile())
				throw new WebdavException("cannot create file: " + dest);
		} catch (IOException e) {
			throw new WebdavException(e);
		}
		src = normalize(src);
		setResourceContent(dest, getResourceContent(src), null, null);

		// copy properties from src to dest
		copyCustomProperties(src, dest);
	}

	/**
	 * tries to save the given InputStream to the file at path "uri". content
	 * type and character encoding are ignored
	 */
	public void setResourceContent(String uri, InputStream is,
			String contentType, String characterEncoding)
			throws WebdavException {

		if (debug == 1)
			log.debug("LocalFileSystemStore.setResourceContent(" + uri + ")");
		uri = normalize(uri);
		File file = new File(root, uri);
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					file));
			try {
				int read = -1;
				byte[] copyBuffer = new byte[BUF_SIZE];

				while ((read = is.read(copyBuffer, 0, copyBuffer.length)) != -1) {
					os.write(copyBuffer, 0, read);
				}
			} finally {
				try {
					is.close();
				} finally {
					os.close();
				}
			}
		} catch (IOException e) {
			throw new WebdavException(e);
		}
	}

	/**
	 * @return the lastModified Date
	 */
	public Date getLastModified(String uri) throws WebdavException {
		if (debug == 1)
			log.debug("LocalFileSystemStore.getLastModified(" + uri + ")");
		uri = normalize(uri);
		File file = new File(root, uri);
		return new Date(file.lastModified());
	}

	/**
	 * @return the lastModified date of the file, java.io.file does not support
	 *         a creation date
	 */
	public Date getCreationDate(String uri) throws WebdavException {
		if (debug == 1)
			log.debug("LocalFileSystemStore.getCreationDate(" + uri + ")");
		// return creation date if available else last modified
		uri = normalize(uri);
		Properties props = getCustomProperties(uri, true);
		if (props.containsKey(CREATION_DATE_PROPERTY)) {
			try {
				return new SimpleDateFormat(CREATION_DATE_FORMAT).parse(
						props.getProperty(CREATION_DATE_PROPERTY));
			} catch (Exception e) {
				// get modified date instead
			}
		}
		File file = new File(root, uri);
		return new Date(file.lastModified());
	}

	/**
	 * @return a (possibly empty) list of children, or <code>null</code> if
	 *         the uri points to a file
	 */
	public String[] getChildrenNames(String uri) throws WebdavException {
		if (debug == 1)
			log.debug("LocalFileSystemStore.getChildrenNames(" + uri + ")");
		uri = normalize(uri);
		File file = new File(root, uri);
		if (file.isDirectory()) {

			File[] children = file.listFiles();
			List childList = new ArrayList();
			for (int i = 0; i < children.length; i++) {
				String name = children[i].getName();
				if (!CUSTOM_PROPERTIES_STORAGE.equals(name))
					childList.add(name);
			}
			String[] childrenNames = new String[childList.size()];
			childrenNames = (String[]) childList.toArray(childrenNames);
			return childrenNames;
		} else {
			return null;
		}

	}

	/**
	 * @return an input stream to the specified resource
	 */
	public InputStream getResourceContent(String uri) throws WebdavException {
		if (debug == 1)
			log.debug("LocalFileSystemStore.getResourceContent(" + uri + ")");
		uri = normalize(uri);
		File file = new File(root, uri);

		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
		} catch (IOException e) {
			throw new WebdavException(e);
		}
		return in;
	}

	/**
	 * @return the size of the file
	 */
	public long getResourceLength(String uri) throws WebdavException {
		if (debug == 1)
			log.debug("LocalFileSystemStore.getResourceLength(" + uri + ")");
		uri = normalize(uri);
		File file = new File(root, uri);
		return file.length();
	}

	/**
	 * @throws IOException
	 *             if the deletion failed
	 * 
	 */
	public void removeObject(String uri) throws WebdavException {
		uri = normalize(uri);
		File file = new File(root, uri);
		boolean success = file.delete();
		// delete properties
		deleteProperties(uri);
		if (debug == 1)
			log.debug("LocalFileSystemStore.removeObject(" + uri + ")=" + success);
		if (!success) {
			throw new WebdavException("cannot delete object: " + uri);
		}

	}

	// Normalize uri
	private String normalize(String uri) {
		if (uri.startsWith(servletName))
			return uri.substring(servletName.length());
		else
			return uri;
	}

	// Get properties file for resourceUri
	private File getPropertiesFile(String resourceUri) {
		File file = new File(root, resourceUri);
		file = file.getParentFile();
		if (file == null) return null;
		return new File(file.getAbsoluteFile() + File.separator + CUSTOM_PROPERTIES_STORAGE);
	}
	
	// Save custom properties
	private void saveCustomProperties(Properties newProperties, String resourceUri) {
		if (newProperties == null || newProperties.size() == 0) return;
		
		resourceUri = normalize(resourceUri);
		File file = getPropertiesFile(resourceUri);
		if (file == null) return;
		Properties persisted = new Properties();
		// Properties file exists, load it so we can add new properties
		if (file.exists()) {
			InputStream in = null;
			try {
				in = new FileInputStream(file);
				persisted.loadFromXML(in);
			} catch (Exception e) {
				log.warn("Failed to get properties from cache for " + resourceUri);
				return;
			} finally {
				if (in != null) try {in.close();} catch (Exception e) {}
			}
		}
		// Add new properties with key format of: resourceUri + "|" + key
		Enumeration en = newProperties.keys();
		while(en.hasMoreElements()) {
			String key = (String)en.nextElement();
			persisted.setProperty(resourceUri + PROPERTIES_STORAGE_PATH_SEPARATOR + key, newProperties.getProperty(key));
		}
		// Store the updates properties
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			persisted.storeToXML(os, "");
		} catch (Exception e) {
			log.warn("Failed to store properties for " + resourceUri);
		} finally {
			if (os != null) try {os.close();} catch (Exception e) {}
		}
	}
	
	// Get properties for given uri
	private Properties getPropertiesFor(String uri) {
		uri = normalize(uri);
		File file = getPropertiesFile(uri);
		if (file == null || !file.exists()) {
			return null;
		}
		InputStream in = null;
		Properties persisted = new Properties();
		try {
			in = new FileInputStream(file);
			persisted.loadFromXML(in);
		} catch (Exception e) {
			log.warn("Failed to get properties from cache for " + uri);
			return null;
		} finally {
			if (in != null) try {in.close();} catch (Exception e) {}
		}
		Enumeration en = persisted.keys();
		Properties props = new Properties();
		while(en.hasMoreElements()) {
			String key = (String)en.nextElement();
			if (key.startsWith(uri + PROPERTIES_STORAGE_PATH_SEPARATOR)) {
				String newKey = key.substring(uri.length() + PROPERTIES_STORAGE_PATH_SEPARATOR.length());
				props.setProperty(newKey, persisted.getProperty(key));
			}
		}
		return (props.size() == 0) ? null : props;
	}
	
    /**
     * Set custom properties for given resource
     * 
     * @param resourceUri URI of the resource
     * @param properties to set
     */
	public void setCustomProperties(String resourceUri, Properties newProperties) {
		if (newProperties == null || newProperties.size() == 0) return;
		
		resourceUri = normalize(resourceUri);
		if (resourceUri.endsWith("/" + CUSTOM_PROPERTIES_STORAGE)) return;
		
		Properties props = (Properties)mPropertiesCache.get(resourceUri);
		if (props == null) {
			// Get them from persistent storage
			props = getPropertiesFor(resourceUri);
			if (props == null) props = new Properties();
		}
		
		props.putAll(newProperties);
		// Save in cache
		try {
			mPropertiesCache.put(resourceUri, props);
		} catch (Exception e) {
			log.warn("Failed to put properties into cache for " + resourceUri);
		}
		// Write them to the persistent storage
		saveCustomProperties(props, resourceUri);
	}

    /**
     * Get custom properties for given resource
     * 
     * @param resourceUri URI of the resource
     */
	private Properties getCustomProperties(String resourceUri, boolean doIncludeSystem) {
		resourceUri = normalize(resourceUri);
		// Try cache first
		Properties props = (Properties)mPropertiesCache.get(resourceUri);
		if (props != null) {
			if (!doIncludeSystem) props = removeSystemProperties(props);
			return props;
		}
		// Get them from persistent storage
		try {
			props = getPropertiesFor(resourceUri);
			if (props == null) return new Properties();
			if (!doIncludeSystem) props = removeSystemProperties(props);
			// Put them to the cache
			mPropertiesCache.put(resourceUri, props);
		} catch (Exception e) {
			log.warn("Failed to put properties into cache for " + resourceUri);
		}
		
		return props;
	}

	private Properties removeSystemProperties(Properties props) {
		// remove system props
		Enumeration en = props.keys();
		Properties newProps = new Properties();
		while(en.hasMoreElements()) {
			String key = (String)en.nextElement();
			if (!(key.startsWith(SYSTEM_PROPERTY_PREFIX) && key.endsWith(SYSTEM_PROPERTY_PREFIX))) {
				newProps.setProperty(key, props.getProperty(key));
			}
		}
		return newProps;
	}
	
    /**
     * Get custom properties for given resource
     * 
     * @param resourceUri URI of the resource
     */
	public Properties getCustomProperties(String resourceUri) {
		return getCustomProperties(resourceUri, false);
	}
	
	/**
	 * Delete properties for given resource
	 * 
	 * @param resourceUri for which to delete properties
	 */
	public void deleteProperties(String resourceUri) {
		resourceUri = normalize(resourceUri);
		// Try cache first
		Properties props = (Properties)mPropertiesCache.get(resourceUri);
		if (props != null) mPropertiesCache.remove(resourceUri);
		File file = getPropertiesFile(resourceUri);
		if (file == null || !file.exists()) {
			return;
		}
		InputStream in = null;
		Properties persisted = new Properties();
		try {
			in = new FileInputStream(file);
			persisted.loadFromXML(in);
		} catch (Exception e) {
			log.warn("Failed to get properties from cache for " + resourceUri);
			return;
		} finally {
			if (in != null) try {in.close();} catch (Exception e) {}
		}
		if (persisted.remove(resourceUri) != null) {
			// Store the updates properties
			OutputStream os = null;
			try {
				os = new FileOutputStream(file);
				persisted.storeToXML(os, "");
			} catch (Exception e) {
				log.warn("Failed to store properties for " + resourceUri);
			} finally {
				if (os != null) try {os.close();} catch (Exception e) {}
			}
		}
	}
	
	/**
	 * Copy properties from src to dest
	 * 
	 * @param srcUri to copy from
	 * @param destUri to copy to
	 */
	public void copyCustomProperties(String srcUri, String destUri) {
		Properties props = getCustomProperties(srcUri);
		setCustomProperties(destUri, props);
	}

}
