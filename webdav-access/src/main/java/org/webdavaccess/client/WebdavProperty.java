/**
 * 
 */
package org.webdavaccess.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/**
 * WebdavProperty contains information about properties associated with given resource.<br/><br/>
 * 
 * Contains <i>resource</i> path with which the properties are associated,<br/>
 * And <i>properties</i> associated with the <i>resource</i>.<br/>
 * 
 * @author lubosp
 *
 * @see #java.util.Properties
 */
public class WebdavProperty {

	private String resource;
	private Properties properties;
	
	/**
	 * Create webdav property that contains information about properties associated with given resource
	 * 
	 * @param path resource path
	 * @param propVect
	 */
	public WebdavProperty(String path, Vector propVect) {
		this.resource = path;
		this.properties = new Properties();
		Vector propNamesVect = (Vector)propVect.get(0);
		Vector propValues = (Vector)propVect.get(1);
		for (int j = 0; j < propValues.size(); j++) {
			Vector propValue = (Vector)propValues.get(j);
			Vector value = (Vector)propValue.get(1);
			if (value.size() == 2) {
				String status = (String)value.elementAt(1);
				if (status.contains("200")) {
					if (value.get(0) instanceof String) {
						this.properties.setProperty((String)propNamesVect.elementAt(j), (String)value.elementAt(0));
					} else if (value.get(0) instanceof Collection) {
						this.properties.setProperty((String)propNamesVect.elementAt(j), commaList((Collection)value.get(0)));
					} else {
						this.properties.setProperty((String)propNamesVect.elementAt(j), null);
					}
				}
			}
		}
	}

	private String commaList(Collection col) {
		StringBuffer result = new StringBuffer();
		for (Iterator it = col.iterator(); it.hasNext();) {
			if (result.length() > 0) result.append(",");
			String value = (String) it.next();
			result.append(value);
		}
		return result.toString();
	}
	
	/**
	 * @return the resource
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

}
