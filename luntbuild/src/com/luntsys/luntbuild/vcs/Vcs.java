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
import com.luntsys.luntbuild.remoting.ModuleFacade;
import com.luntsys.luntbuild.remoting.VcsFacade;
import org.apache.tools.ant.Project;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * version control system common interface. A vcs will include project level properties
 * which will defined at luntbuild project basic setting page, view level properties which
 * will be defined at luntbuild view basic page, and modules which will be defined at
 * luntbuild view modules page. Different type of version control system
 * such as cvs, vss should implement this interface and will be serialized by hibernate
 * as a property in the {@link com.luntsys.luntbuild.db.Project} (for project level settings}
 * and {@link com.luntsys.luntbuild.db.View} (for view level settings}
 *
 * @author robin shine
 */
public interface Vcs extends Serializable {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = -4018122444301153743L;

	/**
	 * @return a string value describes type of the version control system
	 */
	String getVcsDisplayName();

	/**
	 * The project level properties will be shown at project basic information page
	 * @return list of project level display properties of the version control system.
	 * Should not be empty
	 */
	List getProjectLevelProperties();

	/**
	 * The view level properties will be shown at the view basic information page
	 * @return list of view level display properties of this vcs. Should not be null.
	 */
	List getViewLevelProperties();

	/**
	 * Checkout contents from version control system
	 * @param properties
	 * @param build
	 * @param antProject
	 */
	void checkout(Map properties, Build build, Project antProject);

	/**
	 * Label contents in version control system
	 * @param properties
	 * @param build
	 * @param antProject
	 */
	void label(Map properties, Build build, Project antProject);

	/**
	 * Unlabel contents in version control system
	 * @param properties
	 * @param build
	 * @param antProject
	 */
	void unlabel(Map properties, Build build, Project antProject);

	/**
	 * Validates project level properties of this vcs
	 *
	 * @throws com.luntsys.luntbuild.utility.ValidationException
	 *
	 */
	void validateProjectLevelProperties();

	/**
	 * Validates view level properties of this vcs
	 *
	 * @throws com.luntsys.luntbuild.utility.ValidationException
	 *
	 */
	void validateViewLevelProperties();

	/**
	 * Create a new module for current vcs
	 *
	 * @return null if modules are not applicable for current vcs
	 */
	Module createNewModule();

	/**
	 * Get list of modules for current vcs
	 *
	 * @return should not be null, even modules are not applicable for current vcs
	 */
	List getModules();

	/**
	 * Get list of vcs adaptors for current vcs
	 * @return
	 */
	List getVcsAdaptors();

	/**
	 * Validates modules setting for current vcs
	 *
	 * @throws com.luntsys.luntbuild.utility.ValidationException
	 *
	 */
	void validateModules();

	/**
	 * Validates project level properties, view level properties, and modules properties
	 * as a whole
	 * @throws com.luntsys.luntbuild.utility.ValidationException
	 */
	void validateAll();

	/**
	 * Get a summary string for all settings in current vcs
	 * @return
	 */
	String summarize();

	/**
	 * Determines if config of current vcs modified compared to specified
	 * vcs
	 * @param vcs
	 * @param antProject
	 * @return
	 */
	boolean isConfigModifiedComparedTo(Vcs vcs, Project antProject);

	/**
	 * Get a list of change logs covered by current vcs config since
	 * the specified date.
	 * @param properties luntbuild system properties
	 * @param build
	 * @param antProject
	 * @return list of file modification descriptions
	 */
	com.luntsys.luntbuild.utility.Revisions getRevisionsSince(Map properties, Build build, Project antProject);

	VcsFacade getFacade();

	void setFacade(VcsFacade facade);

	/**
	 * Is this vcs an composite vcs which will be comprised of multiple vcs objects
	 * @return
	 */
	boolean isComposite();

	/**
	 * This interface represents a module definition
	 *
	 * @author robin shine
	 * @see com.luntsys.luntbuild.db.View
	 */
	public abstract class Module implements Serializable {
		/**
		 * Get list of display properties for this module
		 * @return
		 */
		public abstract List getProperties();

		public abstract ModuleFacade getFacade();

		public abstract void setFacade(ModuleFacade facade);
		
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof Module){
				Module module = (Module) obj;
				return (getProperties() == null? module.getProperties()== null: 
					getProperties().equals(module.getProperties()));
			}
			return false;
		}	
	}
}