package org.webdavaccess;

import javax.servlet.http.HttpServletRequest;

public interface IWebdavAuthorization {

	public boolean authorize(HttpServletRequest req);
}
