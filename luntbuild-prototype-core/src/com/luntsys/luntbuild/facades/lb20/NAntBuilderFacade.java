package com.luntsys.luntbuild.facades.lb20;

import java.util.HashMap;
import java.util.Map;

public class NAntBuilderFacade extends BuilderFacade {

    private Map properties;

	public Map getProperties() {
		if (this.properties == null) this.properties = new HashMap();
		return this.properties;
	}

	public void setProperties(Map m) {
		this.properties = m;
	}

	public String getBuilderClassName() {
		return "com.luntsys.luntbuild.builders.NAntBuilder";
	}
}
