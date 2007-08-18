/*
 * $Header: /cvsroot/webdav-servlet/webdav-servlet/src/main/java/net/sf/webdav/Attic/IWebdavStorage.java,v 1.3 2006/03/20 19:09:07 yavarin Exp $
 * $Revision: 1.3 $
 * $Date: 2006/03/20 19:09:07 $
 *
 * ====================================================================
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.webdavaccess;

import org.webdavaccess.exceptions.WebdavException;

import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for simple implementation of any store for the WebdavServlet
 * <p>
 * based on the BasicWebdavStore from Oliver Zeigermann, that was part
 * of the Webdav Construction Kit from slide
 * 
 */
public interface IWebdavStorage {

	public static final String WEBDAV_STORAGE_AUTHENTICATION_IMPLEMENTATION = "webdavAuthorizationImplementation";
	public static final String WEBDAV_RESOURCE_IMPLEMENTATION = "webdavStoreImplementation";
	public static final String DEBUG_PARAMETER = "servletDebug";

	
    /**
     * Indicates that a new request or transaction with this store involved has
     * been started. The request will be terminated by either {@link #commit()}
     * or {@link #rollback()}. If only non-read methods have been called, the
     * request will be terminated by a {@link #commit()}. This method will be
     * called by (@link WebdavStoreAdapter} at the beginning of each request.
     * 
     * 
     * @param req
     *            http request
     * @param parameters
     *            Hashtable containing the parameters' names and associated
     *            values configured in the <init-param> from web.xml
     * @param defaultStorageLocation
     *            Default storage location, can be null.
     * @param authorize
     *            Authorization implementation, can be null.
     * @throws WebdavException
     */
    void begin(HttpServletRequest req,  Hashtable parameters, String defaultStorageLocation, IWebdavAuthorization authorize)
            throws WebdavException;

    /**
     * Checks if authentication information passed in {@link #begin}
     * is valid. If not throws an exception.
     * 
     * @param req request to check authentication for
     * @throws UnauthenticatedException if authentication is not valid
     */
    void checkAuthentication(HttpServletRequest req) throws WebdavException;
    
    /**
     * Indicates that all changes done inside this request shall be made
     * permanent and any transactions, connections and other temporary resources
     * shall be terminated.
     * 
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    void commit() throws WebdavException;

    /**
     * Indicates that all changes done inside this request shall be undone and
     * any transactions, connections and other temporary resources shall be
     * terminated.
     * 
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    void rollback() throws WebdavException;

    /**
     * Checks if there is an object at the position specified by
     * <code>uri</code>.
     * 
     * @param uri
     *            URI of the object to check
     * @return <code>true</code> if the object at <code>uri</code> exists
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    boolean objectExists(String uri) throws WebdavException;

    /**
     * Checks if there is an object at the position specified by
     * <code>uri</code> and if so if it is a folder.
     * 
     * @param uri
     *            URI of the object to check
     * @return <code>true</code> if the object at <code>uri</code> exists
     *         and is a folder
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    boolean isFolder(String uri) throws WebdavException;

    /**
     * Checks if there is an object at the position specified by
     * <code>uri</code> and if so if it is a content resource.
     * 
     * @param uri
     *            URI of the object to check
     * @return <code>true</code> if the object at <code>uri</code> exists
     *         and is a content resource
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    boolean isResource(String uri) throws WebdavException;

    /**
     * Creates a folder at the position specified by <code>folderUri</code>.
     * 
     * @param folderUri
     *            URI of the folder
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    void createFolder(String folderUri) throws WebdavException;

    /**
     * Creates a content resource at the position specified by
     * <code>resourceUri</code>.
     * 
     * @param resourceUri
     *            URI of the content resource
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    void createResource(String resourceUri) throws WebdavException;

    /**
     * Sets / stores the content of the resource specified by
     * <code>resourceUri</code>.
     * 
     * @param resourceUri
     *            URI of the resource where the content will be stored
     * @param content
     *            input stream from which the content will be read from
     * @param contentType
     *            content type of the resource or <code>null</code> if unknown
     * @param characterEncoding
     *            character encoding of the resource or <code>null</code> if
     *            unknown or not applicable
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    void setResourceContent(String resourceUri, InputStream content, String contentType, String characterEncoding)
            throws WebdavException;

    /**
     * Gets the date of the last modiciation of the object specified by
     * <code>uri</code>.
     * 
     * @param uri
     *            URI of the object, i.e. content resource or folder
     * @return date of last modification, <code>null</code> declares this
     *         value as invalid and asks the adapter to try to set it from the
     *         properties if possible
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    Date getLastModified(String uri) throws WebdavException;

    /**
     * Gets the date of the creation of the object specified by <code>uri</code>.
     * 
     * @param uri
     *            URI of the object, i.e. content resource or folder
     * @return date of creation, <code>null</code> declares this value as
     *         invalid and asks the adapter to try to set it from the properties
     *         if possible
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    Date getCreationDate(String uri) throws WebdavException;

    /**
     * Gets the names of the children of the folder specified by
     * <code>folderUri</code>.
     * 
     * @param folderUri
     *            URI of the folder
     * @return array containing names of the children or null if it is no folder
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    String[] getChildrenNames(String folderUri) throws WebdavException;

    /**
     * Gets the content of the resource specified by <code>resourceUri</code>.
     * 
     * @param resourceUri
     *            URI of the content resource
     * @return input stream you can read the content of the resource from
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    InputStream getResourceContent(String resourceUri) throws WebdavException;

    /**
     * Gets the length of the content resource specified by
     * <code>resourceUri</code>.
     * 
     * @param resourceUri
     *            URI of the content resource
     * @return length of the resource in bytes,
     *         <code>-1</code> declares this value as invalid and asks the
     *         adapter to try to set it from the properties if possible
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    long getResourceLength(String resourceUri) throws WebdavException;

    /**
     * Removes the object specified by <code>uri</code>.
     * 
     * @param uri
     *            URI of the object, i.e. content resource or folder
     * @throws WebdavException
     * 				if something goes wrong on the store level
     */
    void removeObject(String uri) throws WebdavException;
}