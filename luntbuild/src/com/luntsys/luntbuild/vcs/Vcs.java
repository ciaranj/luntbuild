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

import com.luntsys.luntbuild.facades.lb12.ModuleFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.ValidationException;
import org.apache.tools.ant.Project;

import java.io.Serializable;
import java.util.*;

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
	 * Quiet period to wait before checkout
	 */
	private String quietPeriod;

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
	 * Get list of properties specific to different vcs. Item of this list is of type {@link DisplayProperty}
	 * @return list of vcs property
	 */
	public abstract List getVcsSpecificProperties();

	/**
	 * Cleanup this vcs object's checked out contents
	 * @param workingSchedule
	 * @param antProject
	 */
	public void cleanupCheckout(Schedule workingSchedule, Project antProject) {
		Luntbuild.cleanupDir(workingSchedule.getWorkingDir());
	}

	/**
	 * Checkout contents from version control system
	 * @param build
	 * @param antProject
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

	public abstract void checkoutActually(Build build, Project antProject);

	public boolean isVcsQuietSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		Revisions revisions = getRevisionsSince(sinceDate, workingSchedule, antProject);
		return !revisions.isFileModified();
	}

	/**
	 * Label contents in version control system
	 * @param build
	 * @param antProject
	 */
	public abstract void label(Build build, Project antProject);

	/**
	 * Unlabel contents in version control system
	 * @param build
	 * @param antProject
	 */
	public void unlabel(Build build, Project antProject){};

	/**
	 * Create a new module for current vcs
	 *
	 * @return null if modules are not applicable for current vcs
	 */
	public abstract Module createNewModule();

	/**
	 * Get a list of change logs covered by current vcs config since
	 * the specified date.
	 * @param sinceDate since date
	 * @param workingSchedule working schedule
	 * @param antProject
	 * @return list of file modification descriptions
	 */
	public abstract Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule, Project antProject);

	public com.luntsys.luntbuild.facades.lb12.VcsFacade getFacade() {
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

	public void setFacade(VcsFacade facade) {
		setQuietPeriod(facade.getQuietPeriod());
		loadFromFacade(facade);
		getModules().clear();
		Iterator it = facade.getModules().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb12.ModuleFacade moduleFacade = (ModuleFacade) it.next();
			Module module = createNewModule();
			module.setFacade(moduleFacade);
			getModules().add(module);
		}
	}

	public abstract void saveToFacade(VcsFacade facade);

	public abstract void loadFromFacade(VcsFacade facade);

	public abstract VcsFacade constructFacade();

	/**
	 * Check vcs properties against {@link com.luntsys.luntbuild.utility.DisplayProperty#isRequired()}
	 * sub-class who override this method should call this implementation first
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

	public String getQuietPeriod() {
		return quietPeriod;
	}

	public void setQuietPeriod(String quietPeriod) {
		this.quietPeriod = quietPeriod;
	}

	public String toString() {
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
	 * Derive vcs object at build time. This is meaningful for some vcs adaptors that
	 * utilize other vcs object to perform build, such as {@link UCMClearcaseAdaptor}
	 * @return
	 */
	public Vcs deriveBuildTimeVcs(Project antProject) {
		return this;
	}

	/**
	 * This interface represents a module definition
	 *
	 * @author robin shine
	 * @see com.luntsys.luntbuild.db.Project
	 */
	public abstract class Module implements Serializable, Cloneable {
        /**
         * Keep tracks of version of this class, used when do serialization-deserialization
         */
        static final long serialVersionUID = 1L;
		/**
		 * Get list of display properties for this module
		 * @return
		 */
		public abstract List getProperties();

		public abstract com.luntsys.luntbuild.facades.lb12.ModuleFacade getFacade();

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