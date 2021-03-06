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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.Project;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.ModuleFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.ValidationException;

/**
 * Base class for all version control adaptors. Different types of version control systems (VCS)
 * such as cvs or vss should implement this abstract class.
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
	 * Quiet period to wait before checkout
	 */
	private String quietPeriod;

    /**
     * Gets the display name for this VCS.
     *
     * @return the display name for this VCS
     */
	public abstract String getDisplayName();

    /**
     * Gets the the name of the icon for this VCS.
     * 
     * @return the name of the icon for this VCS. Icon should be put into
     *         the images directory of the web application.
     */
	public abstract String getIconName();

    /**
     * Gets the common VCS properties. These properites will be shown to user and expect
     * input from user.
     *
     * @return the list of properties can be configured by user
     * @see DisplayProperty
     */
	public List getProperties() {
		List properties = getVcsSpecificProperties();
		properties.add(new DisplayProperty(){
			public String getDisplayName() {
				return "Quiet period";
			}

			public String getDescription() {
				return "Number of seconds current VCS should be quiet (without checkins) before Luntbuild decides to " +
						"check out the code of this VCS for a build. This is used to avoid checking out code in the middle of " +
						"some other checkins. This property is optional. When left empty, quiet period will not be used " +
						"before checking out code to build.";
			}

			public String getValue() {
				return getQuietPeriod();
			}

			public void setValue(String value) {
				setQuietPeriod(value);
			}

			public boolean isRequired() {
				return false;
			}
		});
		return properties;
	}

    /**
     * Gets the properties of this implementation of <code>Vcs</code>. These properites will be shown to user and expect
     * input from user.
     *
     * @return the list of properties can be configured by user
     * @see DisplayProperty
     */
	public abstract List getVcsSpecificProperties();

	/**
	 * Cleans up this VCS object's checked out contents.
	 * 
	 * @param workingSchedule the currently running schedule
	 * @param antProject the ant project used for logging
	 */
	public void cleanupCheckout(Schedule workingSchedule, Project antProject) {
		Luntbuild.cleanupDir(workingSchedule.getWorkDirRaw());
	}

	/**
	 * Checks out the contents from the VCS after waiting for the designated quiet period.
	 * 
	 * @param build the build
	 * @param antProject the ant project used for logging
	 */
	public void checkout(Build build, Project antProject) {
		if (!Luntbuild.isEmpty(getQuietPeriod())) {
			int quietPeriodValue = new Integer(getQuietPeriod()).intValue();
			antProject.log("Checking quiet period...");
			while(!isVcsQuietSince(new Date(System.currentTimeMillis() - quietPeriodValue*1000),
					build.getSchedule(), antProject)) {
				try {
					antProject.log("VCS not quiet in recent " + getQuietPeriod() + " seconds, sleeping a while before check again...");
					Thread.sleep(quietPeriodValue * 1000);
					antProject.log("Checking quiet period...");
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			antProject.log("VCS quiet in recent " + getQuietPeriod() + " seconds, continue to check out code...");
		}
		checkoutActually(build, antProject);
	}

	/**
	 * Checks out the contents from the VCS without waiting.
	 * 
	 * @param build the build
	 * @param antProject the ant project used for logging
	 */
	public abstract void checkoutActually(Build build, Project antProject);

	/**
	 * Checks if the VCS has had any changes since the specified date.
	 * 
	 * @param sinceDate the date to check from
	 * @param workingSchedule the currently running schedule
	 * @param antProject the ant project used for logging
	 * @return <code>true</code> if there have been changes since the specified date
	 */
	public boolean isVcsQuietSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		Revisions revisions = getRevisionsSince(sinceDate, workingSchedule, antProject);
		return !revisions.isFileModified();
	}

	/**
	 * Labels the contents in the VCS.
	 * 
	 * @param build the build
	 * @param antProject the ant project used for logging
	 */
	public abstract void label(Build build, Project antProject);

	/**
	 * Unlabels the contents in the VCS.
	 * 
	 * @param build the build
	 * @param antProject the ant project used for logging
	 */
	public void unlabel(Build build, Project antProject){};

	/**
	 * Creates a new module for this VCS.
	 *
	 * @return the new module, or <code>null<code> if modules are not applicable for this VCS
	 */
	public abstract Module createNewModule();

	/**
	 * Creates a new module for this VCS from the specified module.
	 * 
	 * @param module the module to create from
	 * @return the new module, or <code>null<code> if modules are not applicable for this VCS
     */
    public abstract Module createNewModule(Module module);

	/**
	 * Gets a list of change logs covered by this VCS config since the specified date.
	 * 
	 * @param sinceDate the date to check from
	 * @param workingSchedule the currently running schedule
	 * @param antProject the ant project used for logging
	 * @return list of file modification descriptions
	 */
	public abstract Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject);

    /**
     * Gets the facade object of this VCS.
     *
     * @return facade object of this VCS
     */
	public VcsFacade getFacade() {
		VcsFacade facade = constructFacade();
		facade.setQuietPeriod(getQuietPeriod());
		saveToFacade(facade);
		List modules = getModules();
		for (Iterator iterator = modules.iterator(); iterator.hasNext();) {
			Module module = (Module) iterator.next();
			facade.getModules().add(module.getFacade());
		}
		return facade;
	}

    /**
     * Sets the facade object of this VCS.
     *
     * @param facade the VCS facade
     */
	public void setFacade(VcsFacade facade) {
		setQuietPeriod(facade.getQuietPeriod());
		loadFromFacade(facade);
		getModules().clear();
		Iterator it = facade.getModules().iterator();
		while (it.hasNext()) {
			ModuleFacade moduleFacade = (ModuleFacade) it.next();
			Module module = createNewModule();
			module.setFacade(moduleFacade);
			getModules().add(module);
		}
	}

    /**
     * Saves this VCS to a VCS facade.
     *
     * @param facade the VCS facade
     */
	public abstract void saveToFacade(VcsFacade facade);

    /**
     * Loads this VCS from a VCS facade.
     *
     * @param facade the VCS facade
     */
	public abstract void loadFromFacade(VcsFacade facade);

    /**
     * Constructs a blank VCS facade object.
     *
     * @return the VCS facade object
     */
	public abstract VcsFacade constructFacade();

    /**
     * Validates the properties of this VCS.  Checks that property values are valid and required properties have a value.
     * <p>Sub-classes who override this method should call this implementation first.</p>
     *
     * @throws ValidationException if a property has an invalid value
     * @see DisplayProperty#isRequired()
     */
	public void validateProperties() {
		Iterator it = getVcsSpecificProperties().iterator();
		while (it.hasNext()) {
			DisplayProperty property = (DisplayProperty) it.next();
			if (property.isRequired() && (property.getValue() == null
					|| property.getValue().trim().equals("")))
				throw new ValidationException("Property \"" + property.getDisplayName() + "\" can not be empty!");
			if (!property.isMultiLine() && !property.isSecret() && property.getValue() != null)
				property.setValue(property.getValue().trim());
		}
		if (getQuietPeriod() != null)
			setQuietPeriod(getQuietPeriod().trim());
		if (!Luntbuild.isEmpty(getQuietPeriod())) {
			try {
				int quietPeriodValue = new Integer(getQuietPeriod()).intValue();
				if (quietPeriodValue <= 0)
					throw new ValidationException("Property \"Quiet period\" should be greater than 0, or left empty!");
			} catch (NumberFormatException e) {
				throw new ValidationException("Property \"Quiet period\" is not a valid number!");
			}
		}
	}

    /**
     * Validates the modules of this VCS.
     * <p>Sub-classes who override this method should call this implementation first.</p>
     *
     * @throws ValidationException if a module is not invalid
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
	 * Validates this VCS.
	 * 
	 * <p>Sub-classes can override this method to provide extra checking over overrall properties, but call this implementation as well.</p>
	 */
	public void validate() {
		validateProperties();
		validateModules();
	}

	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @return a clone of this instance
	 * @throws CloneNotSupportedException if cloning is not supported
	 * @throws RuntimeException if a clone can not be done
	 */
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
                Module newModule = createNewModule(module);
				copy.getModules().add(newModule);
			}
			return copy;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * Gets the quiet period for this VCS.
     * 
     * @return the quiet period for this VCS
     */
	public String getQuietPeriod() {
		return quietPeriod;
	}

    /**
     * Sets the quiet period for this VCS.
     * 
     * @param quietPeriod the quiet period for this VCS
     */
	public void setQuietPeriod(String quietPeriod) {
		this.quietPeriod = quietPeriod;
	}

	/**
	 * Converts this VCS to a string.
	 * 
	 * @return the string representation of this VCS
	 */
	public String toString() {
		String summary = "Vcs name: " + getDisplayName() + "\n";
		Iterator it = getProperties().iterator();
		while (it.hasNext()) {
			DisplayProperty property = (DisplayProperty) it.next();
			if (!property.isSecret())
				summary += "    " + property.getDisplayName() + ": " + property.getActualValue() + "\n";
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
						summary += property.getDisplayName() + ": " + property.getActualValue();
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
	 * Gets the modules of this VCS.
	 *
	 * @return the modules of this VCS
	 * @see Vcs.Module
	 */
	public final List getModules() {
		if (modules == null)
			modules = new ArrayList();
		return modules;
	}

	/**
	 * Derives VCS object at build time. This is meaningful for some VCS adaptors that
	 * utilize other VCS objects to perform builds, such as {@link UCMClearcaseAdaptor}.
	 * 
	 * @param antProject the ant project used for logging
	 * @return the build time VCS object
	 */
	public Vcs deriveBuildTimeVcs(Project antProject) {
		return this;
	}

	/**
	 * A VCS module definition.
	 *
	 * @author robin shine
	 */
	public abstract class Module implements Serializable, Cloneable {
        /**
         * Keep tracks of version of this class, used when do serialization-deserialization
         */
        static final long serialVersionUID = 1L;

		/**
		 * Gets the list of display properties for this module.
		 * 
		 * @return the list of display properties
		 * @see DisplayProperty
		 */
		public abstract List getProperties();

	    /**
	     * Gets the facade object of this module.
	     *
	     * @return facade object of this module
	     */
		public abstract ModuleFacade getFacade();

	    /**
	     * Sets the facade object of this module.
	     *
	     * @param facade the module facade
	     */
		public abstract void setFacade(ModuleFacade facade);

		/**
		 * Validates the properties of this module.
		 * 
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

		/**
		 * Indicates whether some other object is "equal to" this one.
		 * 
		 * @param obj the reference object with which to compare
		 * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise
		 */
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof Module){
				Module module = (Module) obj;
				return (getProperties() == null? module.getProperties()== null:
					getProperties().equals(module.getProperties()));
			}
			return false;
		}

		public int hashCode() {
			return super.hashCode();
		}

		/**
		 * Creates and returns a copy of this object.
		 * 
		 * @return a clone of this instance
		 * @throws CloneNotSupportedException if cloning is not supported
		 * @throws RuntimeException if a clone can not be done
		 */
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
