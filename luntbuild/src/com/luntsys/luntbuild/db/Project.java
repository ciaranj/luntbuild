/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-20
 * Time: 15:18
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

package com.luntsys.luntbuild.db;

import com.luntsys.luntbuild.builders.Builder;
import com.luntsys.luntbuild.remoting.Constants;
import com.luntsys.luntbuild.remoting.facade.BuilderFacade;
import com.luntsys.luntbuild.remoting.facade.ProjectFacade;
import com.luntsys.luntbuild.remoting.facade.VcsFacade;
import com.luntsys.luntbuild.security.InternalRoles;
import com.luntsys.luntbuild.utility.BuildNecessaryConditionRoot;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.ValidationException;
import com.luntsys.luntbuild.vcs.Vcs;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The class represents a luntbuild project.
 *
 * @author robin shine
 */
public class Project {
	private static Log logger = LogFactory.getLog(Project.class);

	private long id;
	private String name;
	private String description;

	/**
	 * List of version control systems configured for this project
	 */
	private List vcsList = new ArrayList();

	/**
	 * List of builders configured for this project
	 */
	private List builderList = new ArrayList();

	/**
	 * List of postbuilders configured for this project
	 */
	private List postbuilderList = new ArrayList();

	/**
	 * Set of schedules configured for this project
	 */
	private Set schedules = new HashSet();

	/**
	 * Set of vcs logins configured for this project
	 */
	private Set vcsLogins = new HashSet();

	/**
	 * persistent field
	 */
	private Set rolesMappings;

	private String buildNecessaryCondition;

	private String nextVersion;

	/**
	 * List of notifier class names applicable for sending build notification of this
	 * project
	 */
	private List notifiers = new ArrayList();

	/**
	 * Map of user to {@link NotificationConfig}. Used when determine who will get notified, and how
	 * to notify
	 */
	private Map notificationConfigs = new HashMap();

	/**
	 * This map keeps current value of all possible variables used by the nextVersion property
	 */
	private Map versionVariableValues = new HashMap();

	private int logLevel = Constants.LOG_LEVEL_NORMAL;

	/**
	 * set the unique identity of this project, will be called by hibernate
	 *
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	/**
	 * set the name of this project
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * set the description of this project
	 *
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public List getVcsList() {
		return vcsList;
	}

	public void setVcsList(List vcsList) {
		this.vcsList = vcsList;
	}

	public List getBuilderList() {
		return builderList;
	}

	public void setBuilderList(List builderList) {
		this.builderList = builderList;
	}

	public List getPostbuilderList() {
		return postbuilderList;
	}

	public void setPostbuilderList(List postbuilderList) {
		this.postbuilderList = postbuilderList;
	}

	public Set getSchedules() {
		return schedules;
	}

	public void setSchedules(Set schedules) {
		this.schedules = schedules;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Project) {
			if (getId() == ((Project) obj).getId())
				return true;
		}
		return false;
	}

	public int hashCode() {
		return (int) getId();
	}

	public Set getVcsLogins() {
		return vcsLogins;
	}

	public void setVcsLogins(Set vcsLogins) {
		this.vcsLogins = vcsLogins;
	}

	/**
	 * This function determines the luntbuild user by the vcs login string
	 *
	 * @param login version control system login name
	 * @param users luntbuild users list
	 * @return maybe null if no user found to match this vcs login
	 */
	public User getUserByVcsLogin(String login, List users) {
		VcsLogin vcsLogin = VcsLogin.findVcsLogin(getVcsLogins(), login);
		if (vcsLogin != null)
			return vcsLogin.getUser();

		// continue to find based on global users
		Iterator it = users.iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			if (user.getId() == User.USER_CHECKIN_ID)
				continue;
			if (login.equalsIgnoreCase(user.getName()))
				return user;
		}
		return null;
	}

	/**
	 * Set the build version number for the next build. This property is updated automatically
	 * by the building process, And can also be adjusted manually from user interface
	 *
	 * @param nextVersion
	 */
	public void setNextVersion(String nextVersion) {
		this.nextVersion = nextVersion;
	}

	public String getNextVersion() {
		return nextVersion;
	}

	/**
	 * Validate all properties of this project
	 */
	public void validate() {
		validateBasic();
		Iterator it = getVcsList().iterator();
		while (it.hasNext()) {
			Vcs vcs = (Vcs) it.next();
			vcs.validate();
		}
		it = getBuilderList().iterator();
		while (it.hasNext()) {
			Builder builder = (Builder) it.next();
			builder.validate();
		}
		it = getPostbuilderList().iterator();
		while (it.hasNext()) {
			Builder builder = (Builder) it.next();
			builder.validate();
		}
	}

	/**
	 * Validate all properties of this project at build time. It is different from validate()
	 * method in the way that it enforces vcsList and builderList not empty
	 */
	public void validateAtBuildTime() {
		validate();
		if (getVcsList().size() == 0)
			throw new ValidationException("No version control system defined for project \"" + getName() + "\"!");
		if (getBuilderList().size() == 0)
			throw new ValidationException("No builders defined for this project \"" + getName() + "\"!");
	}

	/**
	 * Validate project basic properties. Complicate properties such as {@link this#vcsList}, {@link this#builderList},
	 * {@link this#postbuilderList} will not get validated
	 */
	public void validateBasic() {
		try {
			Luntbuild.validatePathElement(getName());
		} catch (ValidationException e) {
			throw new ValidationException("Invalid name: " + e.getMessage());
		}
		setName(getName().trim());

		Luntbuild.validateBuildVersion(getNextVersion());
		setNextVersion(getNextVersion().trim());

		if (!Luntbuild.isEmpty(getBuildNecessaryCondition())) {
			try {
				Ognl.parseExpression(getBuildNecessaryCondition());
			} catch (OgnlException e) {
				throw new ValidationException("Invalid build necessary condition: " + e.getMessage());
			}
			setBuildNecessaryCondition(getBuildNecessaryCondition().trim());
		}

		if (logLevel != Constants.LOG_LEVEL_BRIEF && logLevel != Constants.LOG_LEVEL_NORMAL &&
				logLevel != Constants.LOG_LEVEL_VERBOSE)
			throw new ValidationException("Invalid log level!");
	}

	public ProjectFacade getFacade() {
		ProjectFacade facade = new ProjectFacade();
		facade.setId(getId());
		facade.setName(getName());
		facade.setDescription(getDescription());
		facade.setBuildNecessaryCondition(getBuildNecessaryCondition());
		facade.setNextVersion(getNextVersion());
		facade.setLogLevel(getLogLevel());
		Iterator it = vcsList.iterator();
		while (it.hasNext()) {
			Vcs vcs = (Vcs) it.next();
			facade.getVcsList().add(vcs.getFacade());
		}
		it = builderList.iterator();
		while (it.hasNext()) {
			Builder builder = (Builder) it.next();
			facade.getBuilderList().add(builder.getFacade());
		}
		it = postbuilderList.iterator();
		while (it.hasNext()) {
			Builder builder = (Builder) it.next();
			facade.getBuilderList().add(builder.getFacade());
		}
		return facade;
	}

	public void setFacade(ProjectFacade facade) {
		setName(facade.getName());
		setDescription(facade.getDescription());
		setBuildNecessaryCondition(getBuildNecessaryCondition());
		setNextVersion(facade.getNextVersion());
		setLogLevel(facade.getLogLevel());
		try {
			vcsList.clear();
			Iterator it = facade.getVcsList().iterator();
			while (it.hasNext()) {
				VcsFacade vcsFacade = (VcsFacade) it.next();
				Vcs vcs = (Vcs) Class.forName(vcsFacade.getVcsClassName()).newInstance();
				vcs.setFacade(vcsFacade);
				vcsList.add(vcs);
			}
			builderList.clear();
			it = facade.getBuilderList().iterator();
			while (it.hasNext()) {
				BuilderFacade builderFacade = (BuilderFacade) it.next();
				Builder builder = (Builder) Class.forName(builderFacade.getBuilderClassName()).newInstance();
				builder.setFacade(builderFacade);
				builderList.add(builder);
			}
			it = facade.getPostbuilderList().iterator();
			while (it.hasNext()) {
				BuilderFacade builderFacade = (BuilderFacade) it.next();
				Builder builder = (Builder) Class.forName(builderFacade.getBuilderClassName()).newInstance();
				builder.setFacade(builderFacade);
				postbuilderList.add(builder);
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		validate();
	}

	public List getNotifiers() {
		return notifiers;
	}

	public void setNotifiers(List notifiers) {
		this.notifiers = notifiers;
	}

	/**
	 * Get the working directory of current project
	 *
	 * @param properties the luntbuild system level properties
	 * @return
	 */
	public String getWorkingDir(Map properties) {
		String globalWorkingDir = (String) properties.get(Constants.WORKING_DIR);
		if (Luntbuild.isEmpty(globalWorkingDir))
			globalWorkingDir = new File(Luntbuild.installDir + File.separator + "work").getAbsolutePath();
		return globalWorkingDir + File.separator + getName();
	}

	public String getWorkingDir() throws IOException {
		String workingDir = new File(getWorkingDir(Luntbuild.getDao().loadProperties())).getCanonicalPath();
		return workingDir.replaceAll("\\\\", "\\\\\\\\"); // in order to keep back slash for ognl expression evaluation
	}

	public String getBuildNecessaryCondition() {
		return buildNecessaryCondition;
	}

	public void setBuildNecessaryCondition(String buildNecessaryCondition) {
		this.buildNecessaryCondition = buildNecessaryCondition;
	}

	/**
	 * Resolves the absolute path for specified file path, if this file path already denotes a
	 * absolute file path, it will just return this path. Otherwise it will prefix the file path with
	 * this project's working directory and return that.
	 *
	 * @param properties
	 * @param filePath
	 * @return
	 */
	public String resolveAbsolutePath(Map properties, String filePath) {
		if (Luntbuild.isEmpty(filePath))
			return getWorkingDir(properties);
		File file = new File(filePath);
		if (file.isAbsolute())
			return filePath;
		else
			return getWorkingDir(properties) + File.separator + filePath;
	}

	/**
	 * Determines if vcs contents of this project has changed. This function will use value
	 * of the following thread local variables in  {@link com.luntsys.luntbuild.utility.BuildNecessaryConditionRoot}
	 * <i> activeProject, this variable denotes the project which this method is initiated by
	 * <i> initiateProject, this variable denotes the logging ant project this method should use
	 * <i> baseBuild, this variable denotes the base build the modification is determined since
	 * <i> revisions, this variable denotes the revisions for initiative project
	 *
	 * @return
	 */
	public boolean isModified() {
		org.apache.tools.ant.Project antProject = BuildNecessaryConditionRoot.getAntProject();
		Build baseBuild = BuildNecessaryConditionRoot.getBaseBuild();
		Project initiateProject = BuildNecessaryConditionRoot.getInitiateProject();
		Revisions revisions = new Revisions();
		if (baseBuild == null) {
			if (BuildNecessaryConditionRoot.getRevisions() == null && initiateProject == this) {
				revisions.getChangeLogs().add("========== Change log ignored: base build does not exist ==========");
				BuildNecessaryConditionRoot.setRevisions(revisions);
			}
			return true;
		}

		antProject.log("Getting revisions for project \"" + getName() + "\"...");
		Map properties = Luntbuild.getDao().loadProperties();
		Revisions allRevisions = new Revisions();
		Iterator it = vcsList.iterator();
		while (it.hasNext()) {
			Vcs vcs = (Vcs) it.next();
			revisions = vcs.deriveBuildVcs(antProject).getRevisionsSince(properties, baseBuild, antProject);
			allRevisions.setFileModified(allRevisions.isFileModified() || revisions.isFileModified());
			allRevisions.getChangeLogs().add("*************************************************************");
			allRevisions.getChangeLogs().add(vcs.summarize());
			allRevisions.getChangeLogs().add("");
			allRevisions.getChangeLogs().addAll(revisions.getChangeLogs());
			allRevisions.getChangeLogins().addAll(revisions.getChangeLogins());
		}
		if (BuildNecessaryConditionRoot.getRevisions() == null && initiateProject == this)
			BuildNecessaryConditionRoot.setRevisions(allRevisions);
		return allRevisions.isFileModified();
	}

	public Map getVersionVariableValues() {
		return versionVariableValues;
	}

	public void setVersionVariableValues(Map versionVariableValues) {
		this.versionVariableValues = versionVariableValues;
	}

	public Map getNotificationConfigs() {
		return notificationConfigs;
	}

	public void setNotificationConfigs(Map notificationConfigs) {
		this.notificationConfigs = notificationConfigs;
	}

	/**
	 * Get log level of this project
	 *
	 * @return
	 */
	public int getLogLevel() {
		return logLevel;
	}

	/**
	 * Set log level of this project
	 *
	 * @param logLevel refer to {@link this#getLogLevel()}
	 */
	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * @hibernate.set lazy="true"
	 * inverse="true"
	 * cascade="delete"
	 * @hibernate.collection-key column="FK_PROJECT_ID"
	 * @hibernate.collection-one-to-many class="com.luntsys.luntbuild.db.RolesMapping"
	 */
	public Set getRolesMappings() {
		return this.rolesMappings;
	}

	public void setRolesMappings(Set rolesMappings) {
		this.rolesMappings = rolesMappings;
	}

	/**
	 *  split project related rolemapping to
	 *  separate list for handling by model
	 */
	public List getMappedRolesUserList(String roleName)
	{
		List usersWithAssignedRoles = new ArrayList();

		Set rml = getRolesMappings();

		if ( rml != null)
		{
			Iterator iter = rml.iterator();

			while (iter.hasNext())
			{
				RolesMapping rm = (RolesMapping) iter.next();

				User user = rm.getUser();
				Role role = rm.getRole();

				if (role.getName().equals(roleName))
				{
					usersWithAssignedRoles.add(user);
				}
			}
		}

		return usersWithAssignedRoles;
	}

	/**
	 * save all new mapped roles to project
	 *
	 * @param userlist
	 * @param roleName
	 */
	public void putMappedRolesUserList(List userlist, String roleName)
	{
		// remove all existing roles
		Set rolemappings = getRolesMappings();

		if ( rolemappings != null)
		{
			Iterator iter = rolemappings.iterator();

			while ( iter.hasNext())
			{
				RolesMapping rolemapping = (RolesMapping)iter.next();

				if (rolemapping.getRole().getName().equals(roleName))
				{
					if (logger.isDebugEnabled())
					{
						logger.debug(roleName+" : removed rolemapping : "+rolemapping.getRole().getName()+" from list for user : "+rolemapping.getUser().getName());
					}

					iter.remove();
				}
			}
		}
		else
		{
			rolemappings = new HashSet();
			setRolesMappings(rolemappings);
		}

		if ( userlist != null)
		{
			// find dbbased matching role
			Role role = getMatchingRole(roleName);

			Iterator iter = userlist.iterator();

			while ( iter.hasNext())
			{
				User user = (User)iter.next();

				RolesMapping rm = new RolesMapping();

				rm.setUser(user);
				rm.setProject(this);
				rm.setRole(role);

				if (logger.isDebugEnabled())
				{
					logger.debug(roleName+" : adding rolemapping : "+rm.getRole().getName()+" from list for user : "+rm.getUser().getName());
				}

				rolemappings.add(rm);
			}
		}
	}

	private Role getMatchingRole(String roleName)
	{
		List internalRoles = InternalRoles.getRoles();

		Iterator iter = internalRoles.iterator();
		boolean found = false;
		Role role = null;

		while ( iter.hasNext() && (found == false))
		{
			role = (Role)iter.next();

			found = role.getName().equals(roleName);
		}

		return role;
	}
}