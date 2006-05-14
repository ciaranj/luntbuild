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
import com.luntsys.luntbuild.remoting.Constants;
import com.luntsys.luntbuild.remoting.SearchCriteria;
import com.luntsys.luntbuild.utility.Luntbuild;
import net.sf.hibernate.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
			session.delete("from Property");
			Iterator itProperty = properties.keySet().iterator();
			while (itProperty.hasNext()) {
				String name = (String) itProperty.next();
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
			return session.find("from Project project order by project.name");
		} catch (HibernateException ex) {
			logger.error("Error in loadProjects: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void saveUser(User user) {
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
			return session.find("from User user order by user.name");
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
			return user;
		} catch (HibernateException ex) {
			logger.error("Error in loadUser: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteUser(long userId) {
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
/*
			Query query = session.createSQLQuery("delete from NOTIFICATION_CONFIG " +
					"where FK_PROJECT_ID=:projectId", "", NotificationConfig.class);
			query.setLong("projectId", project.getId());
			query.list();
*/
			Connection connection = session.connection();
			PreparedStatement pstmt = connection.prepareStatement("delete from NOTIFICATION_CONFIG where FK_PROJECT_ID = ?");
			pstmt.setLong(1, project.getId());
			pstmt.execute();

			Iterator it = project.getNotificationConfigs().keySet().iterator();
			while (it.hasNext()) {
				User user = (User) it.next();
				NotificationConfig config = (NotificationConfig) project.getNotificationConfigs().get(user);
				config.setId(0);
			}
			session.saveOrUpdate(project);
		} catch (HibernateException ex) {
			logger.error("Error in saveProject: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		} catch (SQLException e) {
			logger.error("Error in saveProject: ", e);
			throw SessionFactoryUtils.convertHibernateAccessException(new HibernateException(e));
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
			Hibernate.initialize(project.getNotificationConfigs());
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
			Hibernate.initialize(project.getNotificationConfigs());
			return project;
		} catch (HibernateException ex) {
			logger.error("Error in loadProject: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteProject(long projectId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Iterator it = session.find("from Build build where build.schedule.project.id = ?",
					new Long(projectId), Hibernate.LONG).listIterator();
			Map properties = loadProperties();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				Luntbuild.deleteDir(build.getPublishDir(properties));
			}
			session.delete("from Project project where project.id = ?",
					new Long(projectId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteProject: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List loadSchedules() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			return session.find("from Schedule schedule " +
					"inner join fetch schedule.project " +
					"order by schedule.project.name, schedule.name");
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
			session.saveOrUpdate(schedule);
		} catch (HibernateException ex) {
			logger.error("Error in saveSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteSchedule(long scheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Iterator it = session.find("from Build build where build.schedule.id = ?",
					new Long(scheduleId), Hibernate.LONG).listIterator();
			Map properties = loadProperties();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				Luntbuild.deleteDir(build.getPublishDir(properties));
			}
			session.delete("from Schedule schedule " +
					"where schedule.id = ?",
					new Long(scheduleId), Hibernate.LONG);
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

	public void deleteVcsLogin(long vcsLoginId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.delete("from VcsLogin vcsLogin " +
					"where vcsLogin.id = ?",
					new Long(vcsLoginId), Hibernate.LONG);
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

	public void deleteBuild(long buildId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Build build = Luntbuild.getDao().loadBuild(buildId);
			Luntbuild.deleteDir(build.getPublishDir(loadProperties()));
			session.delete("from Build build where build.id = ?", new Long(buildId), Hibernate.LONG);
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
			query.setParameter("buildStatus", new Integer(Constants.BUILD_STATUS_SUCCESS), Hibernate.INTEGER);
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

	public int countBuilds(Schedule schedule) {
		if (schedule.getBuilds() == null)
			return 0;
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.lock(schedule, LockMode.NONE);
			Query query = session.createFilter(schedule.getBuilds(), "select count(*)");
			return ((Integer) query.list().get(0)).intValue();
		} catch (HibernateException ex) {
			logger.error("Error in countBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public int countBuilds(SearchCriteria searchCriteria) {
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

	public void deleteBuilds(SearchCriteria searchCriteria) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select build from Build build " + buildHQLWhereClause(searchCriteria));
			fillQueryParams(query, searchCriteria);
			Iterator it = query.list().listIterator();
			Map properties = loadProperties();
			while (it.hasNext()) {
				Build build = (Build) it.next();
				Luntbuild.deleteDir(build.getPublishDir(properties));
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
			query.setParameter("buildStatus", new Integer(Constants.BUILD_STATUS_RUNNING), Hibernate.INTEGER);
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

	public void moveBuilds(SearchCriteria searchCriteria, long scheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			// first move all matching build's publish directory to destination schedule's publish directory
			Schedule schedule = loadSchedule(scheduleId);
			Query query = session.createQuery("select build from Build build " + buildHQLWhereClause(searchCriteria));
			fillQueryParams(query, searchCriteria);
			Iterator it = query.list().listIterator();
			Map properties = loadProperties();
			Connection connection = session.connection();
			PreparedStatement pstmt = connection.prepareStatement("update BUILD set FK_SCHEDULE_ID = ? where ID = ?");
			while (it.hasNext()) {
				Build build = (Build) it.next();
				if (new File(build.getPublishDir(properties)).exists())
					Luntbuild.moveDir(build.getPublishDir(properties), schedule.getPublishDir(properties));
/*
				Query updateQuery = session.createSQLQuery("update BUILD set FK_SCHEDULE_ID = :scheduleId " +
						"where ID = :buildId", "", Build.class);
				updateQuery.setLong("scheduleId", scheduleId);
				updateQuery.setLong("buildId", build.getId());
				updateQuery.list();
*/
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
	private String buildHQLWhereClause(SearchCriteria searchCriteria) {
		String whereClause = "";
		if (!Luntbuild.isEmpty(searchCriteria.getVersion())) {
			if (searchCriteria.isExactMatch())
				whereClause += "where build.version = :version ";
			else
				whereClause += "where build.version like :version ";
		}
		if (searchCriteria.getStatus() != Constants.BUILD_STATUS_ALL) {
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
		if (searchCriteria.getScheduleId() != 0) {
			if (whereClause.equals(""))
				whereClause += "where build.schedule.id = :scheduleId";
			else
				whereClause += "and build.schedule.id = :scheduleId ";
		}
		return whereClause;
	}

	/**
	 * Build SQL where clause based on specified search criteria
	 *
	 * @param searchCriteria
	 * @return
	 */
/*
	private String buildSQLWhereClause(SearchCriteria searchCriteria) {
		String whereClause = "";
		if (!Luntbuild.isEmpty(searchCriteria.getVersion())) {
			if (searchCriteria.isExactMatch())
				whereClause += "where VERSION = :version ";
			else
				whereClause += "where VERSION like :version ";
		}
		if (searchCriteria.getStatus() != Constants.BUILD_STATUS_ALL) {
			if (whereClause.equals(""))
				whereClause += "where STATUS = :status ";
			else
				whereClause += "and STATUS = :status ";
		}
		if (searchCriteria.getFrom() != null) {
			if (whereClause.equals(""))
				whereClause += "where START_DATE >= :from ";
			else
				whereClause += "and START_DATE >= :from ";
		}
		if (searchCriteria.getTo() != null) {
			if (whereClause.equals(""))
				whereClause += "where START_DATE <= :to ";
			else
				whereClause += "and START_DATE <= :to ";
		}
		if (searchCriteria.getScheduleId() != 0) {
			if (whereClause.equals(""))
				whereClause += "where FK_SCHEDULE_ID = :scheduleId ";
			else
				whereClause += "and FK_SCHEDULE_ID = :scheduleId ";
		}
		return whereClause;
	}
*/

	/**
	 * Fill query parameters with specified search criteria
	 *
	 * @param query
	 * @param searchCriteria
	 */
	private void fillQueryParams(Query query, SearchCriteria searchCriteria) {
		if (!Luntbuild.isEmpty(searchCriteria.getVersion())) {
			if (searchCriteria.isExactMatch())
				query.setString("version", searchCriteria.getVersion());
			else
				query.setString("version", searchCriteria.getVersion() + "%");
		}
		if (searchCriteria.getStatus() != Constants.BUILD_STATUS_ALL)
			query.setInteger("status", searchCriteria.getStatus());
		if (searchCriteria.getFrom() != null)
			query.setDate("from", searchCriteria.getFrom());
		if (searchCriteria.getTo() != null)
			query.setDate("to", searchCriteria.getTo());
		if (searchCriteria.getScheduleId() != 0)
			query.setLong("scheduleId", searchCriteria.getScheduleId());
	}

	public void failUnfinishedBuilds() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Connection connection = session.connection();
			PreparedStatement pstmt = connection.prepareStatement("update BUILD set STATUS = ?, END_DATE = ? " +
					"where STATUS  = ?");
			pstmt.setInt(1, Constants.BUILD_STATUS_FAILED);
			pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
			pstmt.setInt(3, Constants.BUILD_STATUS_RUNNING);
			pstmt.executeUpdate();
/*
			Query query = session.createSQLQuery("update BUILD set STATUS = :failedStatus, " +
					"END_DATE = :currentDate where STATUS  = :unfinishedStatus",
					"", Build.class);
			query.setInteger("failedStatus", Constants.BUILD_STATUS_FAILED);
			query.setInteger("unfinishedStatus", Constants.BUILD_STATUS_RUNNING);
			query.setTimestamp("currentDate", new Date());
			query.list();
*/
		} catch (HibernateException ex) {
			logger.error("Error in failUnfinishedBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		} catch (SQLException e) {
			logger.error("Error in failUnfinishedBuilds: ", e);
			throw SessionFactoryUtils.convertHibernateAccessException(new HibernateException(e));
		}
	}

	public void failUnfinishedSchedules() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Connection connection = session.connection();
			PreparedStatement pstmt = connection.prepareStatement("update SCHEDULE set STATUS = ?, STATUS_DATE = ? " +
					"where STATUS  = ?");
			pstmt.setInt(1, Constants.SCHEDULE_STATUS_FAILED);
			pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
			pstmt.setInt(3, Constants.SCHEDULE_STATUS_RUNNING);
			pstmt.executeUpdate();
/*
			Query query = session.createSQLQuery("update SCHEDULE " +
					"set STATUS = :failedStatus, STATUS_DATE = :currentDate " +
					"where STATUS  = :unfinishedStatus",
					"", Schedule.class);
			query.setInteger("failedStatus", Constants.SCHEDULE_STATUS_FAILED);
			query.setInteger("unfinishedStatus", Constants.SCHEDULE_STATUS_RUNNING);
			query.setTimestamp("currentDate", new Date());
			query.list();
*/
		} catch (HibernateException ex) {
			logger.error("Error in failUnfinishedSchedules: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		} catch (SQLException e) {
			logger.error("Error in failUnfinishedSchedules: ", e);
			throw SessionFactoryUtils.convertHibernateAccessException(new HibernateException(e));
		}
	}

	public void createUserCheckin() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Connection connection = session.connection();
			PreparedStatement pstmt = connection.prepareStatement("insert into USER values(?, ?, '', ?, '', false)");
			pstmt.setLong(1, User.USER_CHECKIN_ID);
			pstmt.setString(2, User.USER_CHECKIN_NAME);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(new HashMap());
			} catch (IOException e) {
				throw new HibernateException(e);
			}
			pstmt.setBytes(3, baos.toByteArray());
			pstmt.executeUpdate();
/*
			Query query = session.createSQLQuery("insert into USER " +
					"values(:userCheckinId, :userCheckinName, '', :contacts)",
					"", User.class);
			query.setLong("userCheckinId", User.USER_CHECKIN_ID);
			query.setString("userCheckinName", User.USER_CHECKIN_NAME);
			query.setBinary("contacts", baos.toByteArray());
			query.list();
*/
		} catch (HibernateException ex) {
			logger.error("Error in createUserCheckin: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		} catch (SQLException e) {
			logger.error("Error in createUserCheckin: ", e);
			throw SessionFactoryUtils.convertHibernateAccessException(new HibernateException(e));
		}
	}

	public void moveBuild(long buildId, long scheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			// first move build's publish directory to destination schedule's publish directory
			Schedule schedule = loadSchedule(scheduleId);
			Build build = loadBuild(buildId);
			Map properties = loadProperties();
			if (new File(build.getPublishDir(properties)).exists())
				Luntbuild.moveDir(build.getPublishDir(properties), schedule.getPublishDir(properties));

			Connection connection = session.connection();
			PreparedStatement pstmt = connection.prepareStatement("update BUILD set FK_SCHEDULE_ID = ? where ID = ?");
			pstmt.setLong(1, scheduleId);
			pstmt.setLong(2, buildId);
			pstmt.executeUpdate();
/*
			Query updateQuery = session.createSQLQuery("update BUILD set FK_SCHEDULE_ID = :scheduleId " +
					"where ID = :buildId", "", Build.class);
			updateQuery.setLong("scheduleId", scheduleId);
			updateQuery.setLong("buildId", build.getId());
			updateQuery.list();
*/
		} catch (HibernateException ex) {
			logger.error("Error in moveBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		} catch (SQLException e) {
			logger.error("Error in moveBuild: ", e);
			throw SessionFactoryUtils.convertHibernateAccessException(new HibernateException(e));
		}
	}

	public List loadUsersWithoutVirtual() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from User user where user.id != :userId");
			query.setParameter("userId", new Long(User.USER_CHECKIN_ID));
			List results = query.list();

			return results;

		} catch (HibernateException ex) {
			logger.error("Error in loadUsersWithoutVirtual: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	// for authentication

	public User loadUser(String userLoginName) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from User user where user.name = :name");
			query.setParameter("name", userLoginName);
			List results = query.list();

			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified user can not " +
						"be found in the database!");

			User user = (User)results.get(0);
			return user;
		} catch (HibernateException ex) {
			logger.error("Error in loadUser: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List loadRoles()
	{
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			return session.find("from Role role order by role.id");
		} catch (HibernateException ex) {
			logger.error("Error in loadRoles: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteRoleMapping(long roleId)
	{
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.delete("from RolesMapping rm where rm.id = ?", new Long(roleId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteRoleMapping: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}
}