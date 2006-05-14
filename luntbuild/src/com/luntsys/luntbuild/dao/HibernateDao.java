/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-10
 * Time: 9:01:31
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
package com.luntsys.luntbuild.dao;

import com.luntsys.luntbuild.db.*;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.SearchCriteria;
import com.luntsys.luntbuild.facades.lb12.DataCollection;
import com.luntsys.luntbuild.migration.MigrationManager;
import com.luntsys.luntbuild.security.SecurityHelper;
import com.luntsys.luntbuild.utility.Luntbuild;
import net.sf.acegisecurity.AccessDeniedException;
import net.sf.hibernate.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Implementor of data access interface using hibernate as persistent layer
 *
 * @author robin shine
 */
public class HibernateDao extends HibernateDaoSupport implements Dao {
	private static Log logger = LogFactory.getLog(HibernateDao.class);

	public void saveProperties(Map properties) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.delete("from Property property");
			Iterator it = properties.keySet().iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				Property property = new Property();
				property.setName(name);
				property.setValue((String) properties.get(name));
				session.save(property);
			}
		} catch (HibernateException ex) {
			logger.error("Error in saveProperties: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Map loadProperties() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			ListIterator itProperty = session.find("from Property").listIterator();
			Map properties = new HashMap();
			while (itProperty.hasNext()) {
				Property property = (Property) itProperty.next();
				properties.put(property.getName(), property.getValue());
			}
			return properties;
		} catch (HibernateException ex) {
			logger.error("Error in getProperties: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List loadProjects() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			List projects = session.find("from Project project order by project.name");
			return projects;
		} catch (HibernateException ex) {
			logger.error("Error in loadProjects: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void saveUser(User user) {
		if (!SecurityHelper.isSiteAdmin() && !user.getName().equals(SecurityHelper.getPricipal().toString())) {
			throw new AccessDeniedException("Access denied!");
		}
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.saveOrUpdate(user);
		} catch (HibernateException ex) {
			logger.error("Error in saveUser: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List loadUsers() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			List users = session.find("from User user order by user.name");
			return users;
		} catch (HibernateException ex) {
			logger.error("Error in loadUsers: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public User loadUser(long userId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from User user where user.id = :userId");
			query.setParameter("userId", new Long(userId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified user can not " +
						"be found in the database!");
			User user = (User) results.get(0);
			Hibernate.initialize(user.getNotifyMappings());
			Hibernate.initialize(user.getRolesMappings());
			Hibernate.initialize(user.getVcsLogins());
			return user;
		} catch (HibernateException ex) {
			logger.error("Error in loadUser: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteUser(long userId) {
		if (!SecurityHelper.isSiteAdmin())
			throw new AccessDeniedException("Access denied!");
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.delete("from User user where user.id = ?",
					new Long(userId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteUser: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void saveProject(Project project) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			String oldName = null;
			if (project.getId() != 0) {
				Query query = session.createQuery("select project.name from Project project " +
						"where project.id = :projectId");
				query.setParameter("projectId", new Long(project.getId()));
				List results = query.list();
				if (results.size() == 0 || results.get(0) == null)
					throw new DataRetrievalFailureException("Specified project can not " +
							"be found in the database!");
				oldName = (String) results.get(0);
			}
			session.saveOrUpdate(project);
			if (oldName != null) {
				String newName = project.getName();
				project.setName(oldName);
				String oldPublishDir = project.getPublishDir();
				project.setName(newName);
				String newPublishDir = project.getPublishDir();
				Luntbuild.renameDir(oldPublishDir, newPublishDir);
			}
		} catch (HibernateException ex) {
			logger.error("Error in saveProject: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Project loadProject(long projectId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Project project left join fetch " +
					"project.schedules where project.id = :projectId");
			query.setParameter("projectId", new Long(projectId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified project can not " +
						"be found in the database!");
			Project project = (Project) results.get(0);
			Hibernate.initialize(project.getVcsLogins());
			Hibernate.initialize(project.getNotifyMappings());
			//JPL
			Hibernate.initialize(project.getRolesMappings());
			return project;
		} catch (HibernateException ex) {
			logger.error("Error in loadProject: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Project loadProject(String projectName) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Project project left join fetch " +
					"project.schedules where project.name = :projectName");
			query.setString("projectName", projectName);
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified project can not " +
						"be found in the database!");
			Project project = (Project) results.get(0);
			Hibernate.initialize(project.getVcsLogins());
			Hibernate.initialize(project.getNotifyMappings());
			//JPL
			Hibernate.initialize(project.getRolesMappings());
			return project;
		} catch (HibernateException ex) {
			logger.error("Error in loadProject: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteProject(Project project) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Iterator it = session.find("from Build build where build.schedule.project.id = ?",
					new Long(project.getId()), Hibernate.LONG).listIterator();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				Luntbuild.deleteDir(build.getPublishDir());
			}
			session.delete("from Project project where project.id = ?",
					new Long(project.getId()), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteProject: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List loadSchedules() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			List schedules = session.find("from Schedule schedule " +
					"inner join fetch schedule.project " +
					"order by schedule.project.name, schedule.name");
			return schedules;
		} catch (HibernateException ex) {
			logger.error("Error in loadSchedules: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Schedule loadSchedule(long scheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Schedule schedule " +
					"inner join fetch schedule.project " +
					"where schedule.id = :scheduleId");
			query.setParameter("scheduleId", new Long(scheduleId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified schedule " +
						"can not be found in the database!");
			return (Schedule) results.get(0);
		} catch (HibernateException ex) {
			logger.error("Error in loadSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Schedule loadSchedule(String projectName, String scheduleName) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Schedule schedule " +
					"inner join fetch schedule.project " +
					"where schedule.name = :scheduleName and " +
					"schedule.project.name = :projectName");
			query.setString("scheduleName", scheduleName);
			query.setString("projectName", projectName);
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified schedule " +
						"can not be found in the database!");
			return (Schedule) results.get(0);
		} catch (HibernateException ex) {
			logger.error("Error in loadSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void saveSchedule(Schedule schedule) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			String oldName = null;
			if (schedule.getId() != 0) {
				Query query = session.createQuery("select schedule.name from Schedule schedule " +
						"where schedule.id = :scheduleId");
				query.setParameter("scheduleId", new Long(schedule.getId()));
				List results = query.list();
				if (results.size() == 0 || results.get(0) == null)
					throw new DataRetrievalFailureException("Specified schedule can not " +
							"be found in the database!");
				oldName = (String) results.get(0);
			}
			session.saveOrUpdate(schedule);
			if (oldName != null) {
				String newName = schedule.getName();
				schedule.setName(oldName);
				String oldPublishDir = schedule.getPublishDir();
				schedule.setName(newName);
				String newPublishDir = schedule.getPublishDir();
				Luntbuild.renameDir(oldPublishDir, newPublishDir);
			}
		} catch (HibernateException ex) {
			logger.error("Error in saveSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteSchedule(Schedule schedule) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Iterator it = session.find("from Build build where build.schedule.id = ?",
					new Long(schedule.getId()), Hibernate.LONG).listIterator();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				Luntbuild.deleteDir(build.getPublishDir());
			}
			session.delete("from Schedule schedule where schedule.id = ?",
					new Long(schedule.getId()), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public VcsLogin loadVcsLogin(long vcsLoginId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from VcsLogin login " +
					"inner join fetch login.project " +
					"inner join fetch login.user " +
					"where login.id = :vcsLoginId");
			query.setParameter("vcsLoginId", new Long(vcsLoginId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified vcs login " +
						"can not be found in the database!");
			return (VcsLogin) results.get(0);
		} catch (HibernateException ex) {
			logger.error("Error in loadVcsLogin: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void saveVcsLogin(VcsLogin vcsLogin) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.saveOrUpdate(vcsLogin);
		} catch (HibernateException ex) {
			logger.error("Error in saveVcsLogin: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteVcsLogin(VcsLogin vcsLogin) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.delete("from VcsLogin vcsLogin " +
					"where vcsLogin.id = ?",
					new Long(vcsLogin.getId()), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteVcsLogin: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void saveBuild(Build build) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.saveOrUpdate(build);
		} catch (HibernateException ex) {
			logger.error("Error in saveBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build loadBuild(long buildId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Build build inner join fetch build.schedule " +
					"inner join fetch build.schedule.project " +
					"where build.id = :buildId");
			query.setParameter("buildId", new Long(buildId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified build can not be found in the database!");
			return (Build) results.get(0);
		} catch (HibernateException ex) {
			logger.error("Error in loadBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build loadBuild(String projectName, String scheduleName, String buildVersion) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Build build inner join fetch build.schedule " +
					"inner join fetch build.schedule.project " +
					"where build.version = :buildVersion and build.schedule.name = :scheduleName and " +
					"build.schedule.project.name = :projectName");
			query.setString("buildVersion", buildVersion);
			query.setString("scheduleName", scheduleName);
			query.setString("projectName", projectName);
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified build can not be found in the database!");
			return (Build) results.get(0);
		} catch (HibernateException ex) {
			logger.error("Error in loadBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteBuild(Build build) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Luntbuild.deleteDir(build.getPublishDir());
			session.delete("from Build build where build.id = ?", new Long(build.getId()), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build loadLastBuild(Schedule schedule) {
		if (schedule.getBuilds() == null)
			return null;
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.lock(schedule, LockMode.NONE);
			Query query = session.createFilter(schedule.getBuilds(), "order by this.id desc");
			query.setMaxResults(1);
			List results = query.list();
			if (results.size() != 0)
				return (Build) results.get(0);
			else
				return null;
		} catch (HibernateException ex) {
			logger.error("Error in loadLastBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build loadLastBuild(String projectName, String scheduleName) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select build from Build build inner join fetch build.schedule " +
					"inner join fetch build.schedule.project where " +
					"build.schedule.name = :scheduleName and " +
					"build.schedule.project.name = :projectName " +
					"order by build.id desc");
			query.setString("scheduleName", scheduleName);
			query.setString("projectName", projectName);
			query.setMaxResults(1);
			List results = query.list();
			if (results.size() != 0)
				return (Build) results.get(0);
			else
				return null;
		} catch (HibernateException ex) {
			logger.error("Error in loadLastBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build loadLastSuccessBuild(String projectName, String scheduleName) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select build from Build build inner join fetch build.schedule " +
					"inner join fetch build.schedule.project where " +
					"build.schedule.name = :scheduleName and " +
					"build.schedule.project.name = :projectName and " +
					"build.status = :buildStatus " +
					"order by build.id desc");
			query.setString("scheduleName", scheduleName);
			query.setString("projectName", projectName);
			query.setInteger("buildStatus", Constants.BUILD_STATUS_SUCCESS);
			query.setMaxResults(1);
			List results = query.list();
			if (results.size() != 0)
				return (Build) results.get(0);
			else
				return null;
		} catch (HibernateException ex) {
			logger.error("Error in loadLastBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build loadLastSuccessBuild(Schedule schedule) {
		if (schedule.getBuilds() == null)
			return null;
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.lock(schedule, LockMode.NONE);
			Query query = session.createFilter(schedule.getBuilds(), "where this.status = " +
					":buildStatus order by this.id desc");
			query.setParameter("buildStatus", new Integer(com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_SUCCESS), Hibernate.INTEGER);
			query.setMaxResults(1);
			List results = query.list();
			if (results.size() != 0)
				return (Build) results.get(0);
			else
				return null;
		} catch (HibernateException ex) {
			logger.error("Error in loadLastSuccessBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public int countBuilds(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select count(*) from Build build " +
					buildHQLWhereClause(searchCriteria));
			fillQueryParams(query, searchCriteria);
			return ((Integer) query.list().get(0)).intValue();
		} catch (HibernateException ex) {
			logger.error("Error in countBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List searchBuilds(SearchCriteria searchCriteria, int start, int count) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select build from Build build " +
					"inner join fetch build.schedule " +
					"inner join fetch build.schedule.project " +
					buildHQLWhereClause(searchCriteria) + " order by build.id desc");
			fillQueryParams(query, searchCriteria);
			query.setFirstResult(start);
			if (count != 0)
				query.setMaxResults(count);
			return query.list();
		} catch (HibernateException ex) {
			logger.error("Error in countBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteBuilds(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select build from Build build " + buildHQLWhereClause(searchCriteria));
			fillQueryParams(query, searchCriteria);
			Iterator it = query.list().listIterator();
			List builds = new ArrayList();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				if (!SecurityHelper.isPrjAdministrable(build.getSchedule().getProject().getId()))
					throw new AccessDeniedException("Access denied!");
				builds.add(build);
			}
			it = builds.iterator();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				Luntbuild.deleteDir(build.getPublishDir());
				session.delete("from Build build where build.id = ?", new Long(build.getId()), Hibernate.LONG);
			}
		} catch (HibernateException ex) {
			logger.error("Error in deleteBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build loadRunningBuild(Schedule schedule) {
		if (schedule.getBuilds() == null)
			return null;
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.lock(schedule, LockMode.NONE);
			Query query = session.createFilter(schedule.getBuilds(), "where this.status = " +
					":buildStatus");
			query.setParameter("buildStatus", new Integer(com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_RUNNING), Hibernate.INTEGER);
			query.setMaxResults(1);
			List results = query.list();
			if (results.size() != 0)
				return (Build) results.get(0);
			else
				return null;
		} catch (HibernateException ex) {
			logger.error("Error in loadRunningBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void moveBuilds(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria, long scheduleId) {
		// make sure destination schedule have write access
		if (!SecurityHelper.isPrjAdministrable(loadSchedule(scheduleId).getProject().getId()))
			throw new AccessDeniedException("Access denied!");
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			// first move all matching build's publish directory to destination schedule's publish directory
			Schedule schedule = loadSchedule(scheduleId);
			Query query = session.createQuery("select build from Build build " + buildHQLWhereClause(searchCriteria));
			fillQueryParams(query, searchCriteria);
			Iterator it = query.list().listIterator();
			List builds = new ArrayList();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				// make sure write permission available for source projects
				if (!SecurityHelper.isPrjAdministrable(build.getSchedule().getProject().getId()))
					throw new AccessDeniedException("Access denied!");
				builds.add(build);
			}
			Connection connection = session.connection();
			PreparedStatement pstmt = connection.prepareStatement("update LB_BUILD set FK_SCHEDULE_ID = ? where ID = ?");
			it = builds.iterator();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				if (new File(build.getPublishDir()).exists())
					Luntbuild.renameDir(build.getPublishDir(), schedule.getPublishDir() + File.separator + build.getVersion());
				pstmt.setLong(1, scheduleId);
				pstmt.setLong(2, build.getId());
				pstmt.executeUpdate();
			}
		} catch (HibernateException ex) {
			logger.error("Error in moveBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		} catch (SQLException e) {
			logger.error("Error in moveBuilds: ", e);
			throw SessionFactoryUtils.convertHibernateAccessException(new HibernateException(e));
		}
	}

	/**
	 * Build HQL where clause based on specified search criteria
	 *
	 * @param searchCriteria
	 * @return
	 */
	private String buildHQLWhereClause(com.luntsys.luntbuild.facades.SearchCriteria searchCriteria) {
		String whereClause = "";
		if (!Luntbuild.isEmpty(searchCriteria.getVersion())) {
			if (searchCriteria.isExactMatch())
				whereClause += "where build.version = :version ";
			else
				whereClause += "where build.version like :version ";
		}
		if (searchCriteria.getStatus() != com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_ALL) {
			if (whereClause.equals(""))
				whereClause += "where build.status = :status ";
			else
				whereClause += "and build.status = :status ";
		}
		if (searchCriteria.getFrom() != null) {
			if (whereClause.equals(""))
				whereClause += "where build.startDate >= :from ";
			else
				whereClause += "and build.startDate >= :from ";
		}
		if (searchCriteria.getTo() != null) {
			if (whereClause.equals(""))
				whereClause += "where build.startDate <= :to ";
			else
				whereClause += "and build.startDate <= :to ";
		}
		if (whereClause.equals(""))
			whereClause += "where build.schedule.id in (0";
		else
			whereClause += "and build.schedule.id in (0";
		for (int i = 0; i < searchCriteria.getScheduleIds().length; i++) {
			long scheduleId = searchCriteria.getScheduleIds()[i];
			whereClause += ",";
			whereClause += String.valueOf(scheduleId);
		}
		whereClause += ")";
		return whereClause;
	}

	/**
	 * Fill query parameters with specified search criteria
	 *
	 * @param query
	 * @param searchCriteria
	 */
	private void fillQueryParams(Query query, com.luntsys.luntbuild.facades.SearchCriteria searchCriteria) {
		if (!Luntbuild.isEmpty(searchCriteria.getVersion())) {
			if (searchCriteria.isExactMatch())
				query.setString("version", searchCriteria.getVersion());
			else
				query.setString("version", searchCriteria.getVersion() + "%");
		}
		if (searchCriteria.getStatus() != com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_ALL)
			query.setInteger("status", searchCriteria.getStatus());
		if (searchCriteria.getFrom() != null)
			query.setDate("from", searchCriteria.getFrom());
		if (searchCriteria.getTo() != null)
			query.setDate("to", searchCriteria.getTo());
	}

	public void processUnfinishedBuilds() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Connection connection = session.connection();
			PreparedStatement pstmt = connection.prepareStatement("update LB_BUILD set STATUS = ?, END_DATE = ? " +
					"where STATUS  = ?");
			pstmt.setInt(1, com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_FAILED);
			pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
			pstmt.setInt(3, com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_RUNNING);
			pstmt.executeUpdate();
		} catch (HibernateException ex) {
			logger.error("Error in processUnfinishedBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		} catch (SQLException e) {
			logger.error("Error in processUnfinishedBuilds: ", e);
			throw SessionFactoryUtils.convertHibernateAccessException(new HibernateException(e));
		}
	}

	public void processUnfinishedSchedules() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Connection connection = session.connection();
			PreparedStatement pstmt = connection.prepareStatement("update LB_SCHEDULE set STATUS = ?, STATUS_DATE = ? " +
					"where STATUS  = ?");
			pstmt.setInt(1, com.luntsys.luntbuild.facades.Constants.SCHEDULE_STATUS_FAILED);
			pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
			pstmt.setInt(3, com.luntsys.luntbuild.facades.Constants.SCHEDULE_STATUS_RUNNING);
			pstmt.executeUpdate();
		} catch (HibernateException ex) {
			logger.error("Error in processUnfinishedSchedules: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		} catch (SQLException e) {
			logger.error("Error in processUnfinishedSchedules: ", e);
			throw SessionFactoryUtils.convertHibernateAccessException(new HibernateException(e));
		}
	}

	public void moveBuild(long buildId, long scheduleId) {
		// check permissions
		if (!SecurityHelper.isPrjAdministrable(loadBuild(buildId).getSchedule().getProject().getId()))
			throw new AccessDeniedException("Access denied!");
		if (!SecurityHelper.isPrjAdministrable(loadSchedule(scheduleId).getProject().getId()))
			throw new AccessDeniedException("Access denied!");
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			// first move build's publish directory to destination schedule's publish directory
			Schedule schedule = loadSchedule(scheduleId);
			Build build = loadBuild(buildId);
			if (new File(build.getPublishDir()).exists())
				Luntbuild.renameDir(build.getPublishDir(), schedule.getPublishDir() + File.separator + build.getVersion());

			Connection connection = session.connection();
			PreparedStatement pstmt = connection.prepareStatement("update LB_BUILD set FK_SCHEDULE_ID = ? where ID = ?");
			pstmt.setLong(1, scheduleId);
			pstmt.setLong(2, buildId);
			pstmt.executeUpdate();
		} catch (HibernateException ex) {
			logger.error("Error in moveBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		} catch (SQLException e) {
			logger.error("Error in moveBuild: ", e);
			throw SessionFactoryUtils.convertHibernateAccessException(new HibernateException(e));
		}
	}

	// for authentication
	public User loadUser(String userName) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from User user where user.name = :name");
			query.setParameter("name", userName);
			List results = query.list();

			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified user can not " +
						"be found in the database!");

			User user = (User) results.get(0);
			Hibernate.initialize(user.getNotifyMappings());
			Hibernate.initialize(user.getRolesMappings());
			Hibernate.initialize(user.getVcsLogins());
			
			return user;
		} catch (HibernateException ex) {
			logger.error("Error in loadUser: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List loadRoles() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			return session.find("from Role role order by role.id");
		} catch (HibernateException ex) {
			logger.error("Error in loadRoles: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public boolean isUserNameUsed(User user) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from User user where user.name = :name");
			query.setParameter("name", user.getName());
			List results = query.list();

			if (results.size() == 0 || results.get(0) == null)
				return false;
			else {
				User userInDB = (User) results.get(0);
				if (userInDB.getId() != user.getId())
					return true;
				else
					return false;
			}
		} catch (HibernateException ex) {
			logger.error("Error in isUserNameUsed: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public boolean isUserExist(String userName) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from User user where user.name = :name");
			query.setParameter("name", userName);
			List results = query.list();

			if (results.size() == 0 || results.get(0) == null)
				return false;
			else
				return true;
		} catch (HibernateException ex) {
			logger.error("Error in isUserExist: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void initialize() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			User checkinUser = new User();
			checkinUser.setName(User.CHECKIN_USER_NAME);
			session.saveOrUpdate(checkinUser);
			Role role = new Role();
			role.setName(Role.ROLE_AUTHENTICATED);
			session.saveOrUpdate(role);
			role = new Role();
			role.setName(Role.ROLE_SITE_ADMIN);
			session.saveOrUpdate(role);
			role = new Role();
			role.setName(Role.LUNTBUILD_PRJ_ADMIN);
			session.saveOrUpdate(role);
			role = new Role();
			role.setName(Role.LUNTBUILD_PRJ_BUILDER);
			session.saveOrUpdate(role);
			role = new Role();
			role.setName(Role.LUNTBUILD_PRJ_VIEWER);
			session.saveOrUpdate(role);
		} catch (HibernateException ex) {
			logger.error("Error in initialize: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public boolean isProjectNameUsed(Project project) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Project project where project.name = :projectName");
			query.setString("projectName", project.getName());
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				return false;
			else {
				Project projectInDB = (Project)results.get(0);
				if (projectInDB.getId() != project.getId())
					return true;
				else
					return false;
			}
		} catch (HibernateException ex) {
			logger.error("Error in isProjectNameUsed: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public boolean isScheduleNameUsed(Schedule schedule) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Schedule schedule where schedule.project.id = :projectId " +
					"and schedule.name = :scheduleName");
			query.setLong("projectId", schedule.getProject().getId());
			query.setString("scheduleName", schedule.getName());
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				return false;
			else {
				Schedule scheduleInDB = (Schedule) results.get(0);
				if (scheduleInDB.getId() != schedule.getId())
					return true;
				else
					return false;
			}
		} catch (HibernateException ex) {
			logger.error("Error in isScheduleNameUsed: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public boolean isVcsLoginNameUsed(VcsLogin vcsLogin) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from VcsLogin vcsLogin where vcsLogin.project.id = :projectId " +
					"and vcsLogin.login = :loginName");
			query.setLong("projectId", vcsLogin.getProject().getId());
			query.setString("loginName", vcsLogin.getLogin());
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				return false;
			else {
				VcsLogin vcsLoginInDB = (VcsLogin) results.get(0);
				if (vcsLoginInDB.getId() != vcsLogin.getId())
					return true;
				else
					return false;
			}
		} catch (HibernateException ex) {
			logger.error("Error in isVcsLoginNameUsed: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void reserveBuildsByCount(Schedule schedule, int reserveCount) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select build from Build build where " +
					"build.schedule.id=:scheduleId order by build.id desc");
			query.setLong("scheduleId", schedule.getId());
			Iterator it = query.list().listIterator();
			List builds = new ArrayList();
			int index = 0;
			while (it.hasNext()) {
				Build build = (Build) it.next();
				if (++index > reserveCount)
					builds.add(build);
			}
			it = builds.iterator();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				Luntbuild.deleteDir(build.getPublishDir());
				session.delete("from Build build where build.id = ?", new Long(build.getId()), Hibernate.LONG);
			}
		} catch (HibernateException ex) {
			logger.error("Error in reserveBuildsByCount: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void removeBuildsBefore(Schedule schedule, Date date) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select build from Build build where " +
					"build.schedule.id=:scheduleId and build.startDate < :startDate");
			query.setLong("scheduleId", schedule.getId());
			query.setDate("startDate", date);
			Iterator it = query.list().listIterator();
			List builds = new ArrayList();
			while (it.hasNext()) {
				builds.add(it.next());
			}
			it = builds.iterator();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				Luntbuild.deleteDir(build.getPublishDir());
				session.delete("from Build build where build.id = ?", new Long(build.getId()), Hibernate.LONG);
			}
		} catch (HibernateException ex) {
			logger.error("Error in removeBuildsBefore: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public DataCollection loadDataCollection12() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			DataCollection data12 = new DataCollection();
			Iterator it = session.find("from Property").iterator();
			while (it.hasNext()) {
				Property property = (Property) it.next();
				data12.getProperties().add(property.getFacade());
			}
			it = session.find("from Project project").iterator();
			while (it.hasNext()) {
				Project project = (Project) it.next();
				data12.getProjects().add(project.getFacade());
			}
			it = session.find("from Schedule schedule inner join fetch schedule.project").iterator();
			while (it.hasNext()) {
				Schedule schedule = (Schedule) it.next();
				data12.getSchedules().add(schedule.getFacade());
			}
			it = session.find("from Build build inner join fetch build.schedule").iterator();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				data12.getBuilds().add(build.getFacade());
			}
			it = session.find("from User user").iterator();
			while (it.hasNext()) {
				User user = (User) it.next();
				data12.getUsers().add(user.getFacade());
			}
			it = session.find("from Role role").iterator();
			while (it.hasNext()) {
				Role role = (Role) it.next();
				data12.getRoles().add(role.getFacade());
			}
			it  = session.find("from VcsLogin vcsLogin inner join fetch vcsLogin.project " +
					"inner join fetch vcsLogin.user").iterator();
			while (it.hasNext()) {
				VcsLogin vcsLogin = (VcsLogin) it.next();
				data12.getVcsLoginMapping().add(vcsLogin.getFacade());
			}
			it = session.find("from RolesMapping rolesMapping inner join fetch rolesMapping.project " +
					"inner join fetch rolesMapping.user inner join fetch rolesMapping.role").iterator();
			while (it.hasNext()) {
				RolesMapping rolesMapping = (RolesMapping) it.next();
				data12.getRolesMapping().add(rolesMapping.getFacade());
			}
			it = session.find("from NotifyMapping notifyMapping inner join fetch notifyMapping.project " +
					"inner join fetch notifyMapping.user").iterator();
			while (it.hasNext()) {
				NotifyMapping notifyMapping = (NotifyMapping) it.next();
				data12.getNotifyMapping().add(notifyMapping.getFacade());
			}
			return data12;
		} catch (HibernateException ex) {
			logger.error("Error in loadDataCollection: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void saveDataCollection12(DataCollection data12) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			com.luntsys.luntbuild.db.DataCollection data = MigrationManager.deFacade(data12);

			logger.info("Saving system settings...");
			Iterator it = data.getProperties().iterator();
			while (it.hasNext()) {
				Property property = (Property) it.next();
				session.save(property);
			}

			it = data.getRoles().iterator();
			while (it.hasNext()) {
				Role role = (Role) it.next();
				logger.info("Saving role: " + role.getName());
				session.save(role);
			}
			it = data.getUsers().iterator();
			while (it.hasNext()) {
				User user = (User) it.next();
				logger.info("Saving user: " + user.getName());
				session.save(user);
			}
			it = data.getProjects().iterator();
			while (it.hasNext()) {
				Project project = (Project) it.next();
				logger.info("Saving project: " + project.getName());
				session.save(project);
			}
			it = data.getSchedules().iterator();
			while (it.hasNext()) {
				Schedule schedule = (Schedule) it.next();
				logger.info("Saving schedule: " + schedule.getProject().getName() + "/" + schedule.getName());
				session.save(schedule);
			}
			it = data.getSchedules().iterator();
			while (it.hasNext()) {
				Schedule schedule = (Schedule) it.next();
				schedule.getDependentScheduleIds().clear();
				Iterator itDependent = schedule.getDependentSchedules().iterator();
				while (itDependent.hasNext()) {
					Schedule dependentSchedule = (Schedule) itDependent.next();
					schedule.getDependentScheduleIds().add(new Long(dependentSchedule.getId()));
				}
				logger.info("Updating dependent schedule information for schedule: " + schedule.getProject().getName() + "/" +
						schedule.getName());
				session.update(schedule);
			}
			it = data.getBuilds().iterator();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				logger.info("Saving build: " + build.getSchedule().getProject().getName() + "/" + build.getSchedule().getName() +
						"/" + build.getVersion());
				session.save(build);
			}

			logger.info("Saving vcs logins...");
			it = data.getVcsLoginMapping().iterator();
			while (it.hasNext()) {
				VcsLogin vcsLogin = (VcsLogin) it.next();
				session.save(vcsLogin);
			}
		} catch (HibernateException ex) {
			logger.error("Error in saveDataCollection: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void eraseExistingData() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			logger.info("Deleting existing data from current system...");
			session.delete("from User user");
			session.delete("from Role role");
			session.delete("from Property property");
			session.delete("from Project project");
		} catch (HibernateException ex) {
			logger.error("Error in eraseExistingData: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}
}