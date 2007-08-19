/**
 * 
 */
package org.webdavaccess;

/**
 * @author lubosp
 *
 */
public interface IWebdavAlias {

	/**
	 * Returns resource destination based on path. Implementation is responsible for persisting and mapping the resource destination.
	 * 
	 * @param path to be aliased
	 * @return resource destination based on path
	 */
	public String getResourceDestination(String path);
	
	/**
	 * Webdav servlet notifies the Alias implementation that resource has been moved.
	 * 
	 * @param sourcePath resource moved from
	 * @param destinationPath resource moved to
	 */
	public void resourceMovedNotification(String sourcePath, String destinationPath);
}
