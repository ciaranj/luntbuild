/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-4-4
 * Time: 10:00:40
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
package com.luntsys.luntbuild.migration;

import com.luntsys.luntbuild.db.*;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * The class to control migration
 *
 * @author robin shine
 */
public class MigrationManager {
	private static Log logger = LogFactory.getLog(MigrationManager.class);

	public static com.luntsys.luntbuild.facades.lb20.DataCollection importAsDataCollection20(File xmlDataFile) {
		FileReader fileReader = null;
		try {
			logger.info("Detect data version...");
			fileReader = new FileReader(xmlDataFile);
			XmlPullParser xpp = new MXParser();
			xpp.setInput(fileReader);
			xpp.nextTag();
			if (!xpp.getName().equals("DataCollection"))
				throw new MigrationException("Invalid data file: Root element should be <DataCollection>");
			xpp.nextTag();
			if (!xpp.getName().equals("version"))
				throw new MigrationException("Invalid data file: First element of <DataCollection> should be <version>");
			String version = xpp.nextText().trim();
			XStream xstream = new XStream();
			fileReader.close();
			fileReader = null;
			fileReader = new FileReader(xmlDataFile);
			logger.info("Data version of importing file is: " + version);
			if (version.equals("1.2")) {
				xstream.alias("DataCollection", com.luntsys.luntbuild.facades.lb20.DataCollection.class);
				return (com.luntsys.luntbuild.facades.lb20.DataCollection) xstream.fromXML(fileReader);
			}
			throw new MigrationException("This version of data file is not supported!");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (XmlPullParserException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fileReader != null)
				try {
					fileReader.close();
				} catch (IOException e) {
					// ignore
				}
		}
	}

	public static com.luntsys.luntbuild.db.DataCollection deFacade(com.luntsys.luntbuild.facades.lb20.DataCollection data20) {
		logger.info("Defacading 2.0 data collection...");
		com.luntsys.luntbuild.db.DataCollection data = new com.luntsys.luntbuild.db.DataCollection();
		logger.info("Defacading system settings...");
		Iterator it = data20.getProperties().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb20.PropertyFacade propertyFacade =
					(com.luntsys.luntbuild.facades.lb20.PropertyFacade) it.next();
			Property property = new Property();
			property.setName(propertyFacade.getName());
			property.setFacade(propertyFacade);
			data.getProperties().add(property);
		}

		it = data20.getProjects().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb20.ProjectFacade projectFacade =
					(com.luntsys.luntbuild.facades.lb20.ProjectFacade) it.next();
			logger.info("Defacading project: " + projectFacade.getName());
			Project project = new Project();
			project.setId(projectFacade.getId());
			project.setName(projectFacade.getName());
			project.setFacade(projectFacade);
			data.getProjects().add(project);
		}

		it = data20.getSchedules().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb20.ScheduleFacade scheduleFacade =
					(com.luntsys.luntbuild.facades.lb20.ScheduleFacade) it.next();
			logger.info("Defacading schedule: " + data20.getProject(scheduleFacade.getProjectId()).getName() +
					"/" + scheduleFacade.getName());
			Schedule schedule = new Schedule();
			schedule.setId(scheduleFacade.getId());
			schedule.setName(scheduleFacade.getName());
			Project project = new Project();
			project.setId(scheduleFacade.getProjectId());
			schedule.setProject((Project) data.getProjects().get(data.getProjects().indexOf(project)));
			schedule.setFacade(scheduleFacade);
			data.getSchedules().add(schedule);
		}
		it = data.getSchedules().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			logger.info("Remapping dependent schedules for schedule: " + schedule.getProject().getName() +
					"/" + schedule.getName());
			Iterator itDependent = schedule.getDependentScheduleIds().iterator();
			while (itDependent.hasNext()) {
				long dependentScheduleId = ((Long) itDependent.next()).longValue();
				Schedule dependentSchedule = new Schedule();
				dependentSchedule.setId(dependentScheduleId);
				int index = data.getSchedules().indexOf(dependentSchedule);
				if (index != -1)
					schedule.getDependentSchedules().add(data.getSchedules().get(index));
			}
		}

		it = data20.getBuilds().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb20.BuildFacade buildFacade = (com.luntsys.luntbuild.facades.lb20.BuildFacade) it.next();
			logger.info("Defacading build: " + data.getSchedule(buildFacade.getScheduleId()).getProject().getName() +
					"/" + data.getSchedule(buildFacade.getScheduleId()) + "/" + buildFacade.getVersion());
			Build build = new Build();
			build.setId(buildFacade.getId());
			Schedule schedule = new Schedule();
			schedule.setId(buildFacade.getScheduleId());
			build.setSchedule((Schedule) data.getSchedules().get(data.getSchedules().indexOf(schedule)));
			build.setFacade(buildFacade);
			data.getBuilds().add(build);
		}

		it = data20.getUsers().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb20.UserFacade userFacade =
					(com.luntsys.luntbuild.facades.lb20.UserFacade) it.next();
			logger.info("Defacading user: " + userFacade.getName());
			User user = new User();
			user.setId(userFacade.getId());
			user.setName(userFacade.getName());
			user.setFacade(userFacade);
			data.getUsers().add(user);
		}

		it = data20.getRoles().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb20.RoleFacade roleFacade =
					(com.luntsys.luntbuild.facades.lb20.RoleFacade) it.next();
			logger.info("Defacading role: " + roleFacade.getName());
			Role role = new Role();
			role.setId(roleFacade.getId());
			role.setName(roleFacade.getName());
			role.setFacade(roleFacade);
			data.getRoles().add(role);
		}

		logger.info("Defacading vcs logins...");
		it = data20.getVcsLoginMapping().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb20.VcsLoginFacade vcsLoginFacade =
					(com.luntsys.luntbuild.facades.lb20.VcsLoginFacade) it.next();
			VcsLogin vcsLogin = new VcsLogin();
			vcsLogin.setLogin(vcsLoginFacade.getLogin());
			Project project = new Project();
			project.setId(vcsLoginFacade.getProjectId());
			User user = new User();
			user.setId(vcsLoginFacade.getUserId());
			vcsLogin.setProject((Project) data.getProjects().get(data.getProjects().indexOf(project)));
			vcsLogin.setUser((User) data.getUsers().get(data.getUsers().indexOf(user)));
			data.getVcsLoginMapping().add(vcsLogin);
		}

		logger.info("Defacading roles mappings...");
		it = data20.getRolesMapping().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb20.RolesMappingFacade rolesMappingFacade =
					(com.luntsys.luntbuild.facades.lb20.RolesMappingFacade) it.next();
			RolesMapping rolesMapping = new RolesMapping();
			Project project = new Project();
			project.setId(rolesMappingFacade.getProjectId());
			project = (Project) data.getProjects().get(data.getProjects().indexOf(project));
			User user = new User();
			user.setId(rolesMappingFacade.getUserId());
			Role role = new Role();
			role.setId(rolesMappingFacade.getRoleId());
			rolesMapping.setProject(project);
			rolesMapping.setUser((User) data.getUsers().get(data.getUsers().indexOf(user)));
			rolesMapping.setRole((Role) data.getRoles().get(data.getRoles().indexOf(role)));
			project.getRolesMappings().add(rolesMapping);
		}

		logger.info("Defacading notify mappings...");
		it = data20.getNotifyMapping().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb20.NotifyMappingFacade notifyMappingFacade =
					(com.luntsys.luntbuild.facades.lb20.NotifyMappingFacade) it.next();
			NotifyMapping notifyMapping = new NotifyMapping();
			Project project = new Project();
			project.setId(notifyMappingFacade.getProjectId());
			project = (Project) data.getProjects().get(data.getProjects().indexOf(project));
			User user = new User();
			user.setId(notifyMappingFacade.getUserId());
			user = (User) data.getUsers().get(data.getUsers().indexOf(user));
			notifyMapping.setProject(project);
			notifyMapping.setUser(user);
			project.getNotifyMappings().add(notifyMapping);
		}

		it = data.getProjects().iterator();
		while (it.hasNext()) {
			Project project = (Project) it.next();
			project.setId(0);
		}
		it = data.getSchedules().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			schedule.setId(0);
		}
		it = data.getBuilds().iterator();
		while (it.hasNext()) {
			Build build = (Build) it.next();
			build.setId(0);
		}
		it = data.getUsers().iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			user.setId(0);
		}
		it = data.getRoles().iterator();
		while (it.hasNext()) {
			Role role = (Role) it.next();
			role.setId(0);
		}
		return data;
	}
}
