package org.webdavaccess;

public class WebdavAuthorizationFactory {

	private Class fImplementation;

	public WebdavAuthorizationFactory(Class class1) {
		fImplementation = class1;
	}

	public IWebdavAuthorization getAuthorization() throws InstantiationException, IllegalAccessException {
		return (IWebdavAuthorization) fImplementation.newInstance();
	}

}
