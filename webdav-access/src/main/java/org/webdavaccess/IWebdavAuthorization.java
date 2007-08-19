package org.webdavaccess;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.auth.AuthenticationException;

public interface IWebdavAuthorization {

    /**
     * Checks if authentication information passed in {@link #begin}
     * is valid. If not throws an exception.
     * 
     * @param req request to check authentication for
     * @throws AuthenticationException if authentication is not valid
     */
	public void authorize(HttpServletRequest req) throws AuthenticationException;
}
