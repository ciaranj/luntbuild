/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-4-26
 * Time: 11:28:17
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.luntsys.luntbuild.vcs;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.remoting.facade.ModuleFacade;
import com.luntsys.luntbuild.remoting.facade.VcsFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.Luntbuild;
import org.apache.tools.ant.Project;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Base class for all version control adaptors. Different type of version control system
 * such as cvs, vss should implement this abstract class
 *
 * @author robin shine
 */
public abstract class Vcs implements Serializable, Cloneable {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1L;

	/**
	 * Modules setting for current vcs
	 */
	private List modules;

	/**
	 * @return a string value describes type of the version control system
	 */
	public abstract String getDisplayName();

	/**
	 * @return name of the icon for this version control system. Icon should be put into
	 * the images directory of the web application.
	 */
	public abstract String getIconName();

	/**
	 * Get list of properties specific to this vcs. Item of this list is of type {@link DisplayProperty}
	 * @return list of vcs property
	 */
	public abstract List getProperties();

	/**
	 * Cleanup this vcs object's checked out contents
	 * @param properties
	 * @param build
	 * @param antProject
	 */
	public void cleanupCheckout(Map properties, Build build, Project antProject) {
		Luntbuild.cleanupDir(build.getSchedule().getProject().getWorkingDir(properties));
	}

	/**
	 * Checkout contents from version control system
	 * @param properties
	 * @param build
	 * @param antProject
	 */
	public abstract void checkout(Map properties, Build build, Project antProject);

	/**
	 * Label contents in version control system
	 * @param properties
	 * @param build
	 * @param antProject
	 */
	public abstract void label(Map properties, Build build, Project antProject);

	/**
	 * Unlabel contents in version control system
	 * @param properties
	 * @param build
	 * @param antProject
	 */
	public void unlabel(Map properties, Build build, Project antProject){};

	/**
	 * Create a new module for current vcs
	 *
	 * @return null if modules are not applicable for current vcs
	 */
	public abstract Module createNewModule();

	/**
	 * Get a list of change logs covered by current vcs config since
	 * the specified date.
	 * @param properties luntbuild system properties
	 * @param build
	 * @param antProject
	 * @return list of file modification descriptions
	 */
	public abstract Revisions getRevisionsSince(Map properties, Build build, Project antProject);

	public abstract VcsFacade getFacade();

	public abstract void setFacade(VcsFacade facade);

	/**
	 * Check vcs properties against {@link com.luntsys.luntbuild.utility.DisplayProperty#isRequired()}
	 * sub-class who override this method should call this implementation first
	 */
	public void validateProperties() {
		Iterator it = getProperties().iterator();
		while (it.hasNext()) {
			DisplayProperty property = (DisplayProperty) it.next();
			if (property.isRequired() && (property.getValue() == null
					|| property.getValue().trim().equals("")))
				throw new ValidationException("Property \"" + property.getDisplayName() + "\" can not be empty!");
			if (!property.isMultiLine() && !property.isSecret() && property.getValue() != null)
				property.setValue(property.getValue().trim());
		}
	}

	/**
	 * Check module properties against {@link DisplayProperty#isRequired()}
	 * sub-class who override this method should call this implementation first
	 */
	public void validateModules() {
		if (createNewModule() == null) // modules are not applicable for current vcs
			return;
		if (getModules().size() == 0)
			throw new ValidationException("No modules defined!");
		Iterator it = getModules().iterator();
		Module newModule = createNewModule();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj.getClass() != newModule.getClass())
				throw new ValidationException("Invalid module class detected!");
			Module module = (Vcs.Module) obj;
			module.validate();
		}
	}

	/**
	 * Sub class will override this method if wants to provide extra checking over overral
	 * properties
	 */
	public void validate() {
		validateProperties();
		validateModules();
	}

	public Object clone() throws CloneNotSupportedException {
		try {
			Vcs copy = (Vcs) getClass().newInstance();
			for (int i=0; i<getProperties().size(); i++) {
				DisplayProperty property = (DisplayProperty) getProperties().get(i);
				DisplayProperty propertyCopy = (DisplayProperty) copy.getProperties().get(i);
				propertyCopy.setValue(property.getValue());
			}
			for (int i=0; i<getModules().size(); i++) {
				Module module = (Module) getModules().get(i);
				copy.getModules().add(module.clone());
			}
			return copy;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public String summarize() {
		String summary = "Vcs name: " + getDisplayName() + "\n";
		Iterator it = getProperties().iterator();
		while (it.hasNext()) {
			DisplayProperty property = (DisplayProperty) it.next();
			if (!property.isSecret())
				summary += "    " + property.getDisplayName() + ": " + property.getValue() + "\n";
			else
				summary += "    " + property.getDisplayName() + ":*****\n";
		}
		if (createNewModule() != null){
			summary += "    modules:\n";
			it = getModules().iterator();
			while (it.hasNext()) {
				Module module = (Vcs.Module) it.next();
				Iterator itProperty = module.getProperties().iterator();
				summary += "        ";
				while (itProperty.hasNext()) {
					DisplayProperty property = (DisplayProperty) itProperty.next();
					if (!property.isSecret())
						summary += property.getDisplayName() + ": " + property.getValue();
					else
						summary += property.getDisplayName() + ":*****";
					if (itProperty.hasNext())
						summary += ", ";
				}
				summary += "\n";
			}
		}
		return summary;
	}

	/**
	 * Make this method final to ensure the returned value will
	 * never be null
	 *
	 * @return
	 */
	public final List getModules() {
		if (modules == null)
			modules = new ArrayList();
		return modules;
	}

	/**
	 * Derive vcs object suitable for build. This is meaningful for some vcs adaptors that
	 * utilize other vcs object to perform build, such as {@link UCMClearcaseAdaptor}
	 * @return
	 */
	public Vcs deriveBuildVcs(Project antProject) {
		return this;
	}

/*
	public boolean isConfigModifiedComparedTo(Vcs vcs, Project antProject) {
		// compare the class
		if (vcs.getClass() != getClass())
			return true;

		// compare the project level properties
		if (!getProperties().equals(vcs.getProperties()))
			return true;

		// compare the module level properties
		if (createNewModule() != null) {
			if (!getModules().equals(vcs.getModules()))
				return true;
		}

		return false;
	}
*/

	/**
	 * This interface represents a module definition
	 *
	 * @author robin shine
	 * @see com.luntsys.luntbuild.db.Project
	 */
	public abstract class Module implements Serializable, Cloneable {
		/**
		 * Get list of display properties for this module
		 * @return
		 */
		public abstract List getProperties();

		public abstract ModuleFacade getFacade();

		public abstract void setFacade(ModuleFacade facade);

		/**
		 * Validates properties of this module
		 * @throws ValidationException
		 */
		public void validate() {
			Iterator it = getProperties().iterator();
			while (it.hasNext()) {
				DisplayProperty property = (DisplayProperty) it.next();
				if (property.isRequired() && (property.getValue() == null
						|| property.getValue().trim().equals("")))
					throw new ValidationException("Module property \"" + property.getDisplayName() + "\" can not be empty!");
				if (!property.isMultiLine() && !property.isSecret() && property.getValue() != null)
					property.setValue(property.getValue().trim());
			}
		}

		public boolean equals(Object obj) {
			if (obj != null && obj instanceof Module){
				Module module = (Module) obj;
				return (getProperties() == null? module.getProperties()== null: 
					getProperties().equals(module.getProperties()));
			}
			return false;
		}

		public Object clone() throws CloneNotSupportedException {
			try {
				Module copy = (Module) getClass().newInstance();
				for (int i=0; i<getProperties().size(); i++) {
					DisplayProperty property = (DisplayProperty) getProperties().get(i);
					DisplayProperty propertyCopy = (DisplayProperty) copy.getProperties().get(i);
					propertyCopy.setValue(property.getValue());
				}
				return copy;
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}