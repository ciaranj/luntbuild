/**
 * 
 */
package org.webdavaccess;

/**
 * @author lubosp
 *
 */
public class WebdavAliasFactory {

	private Class fImplementation;

	public WebdavAliasFactory(Class class1) {
		fImplementation = class1;
	}

	public IWebdavAlias getAliasManager() throws InstantiationException, IllegalAccessException {
		return (IWebdavAlias) fImplementation.newInstance();
	}


}
