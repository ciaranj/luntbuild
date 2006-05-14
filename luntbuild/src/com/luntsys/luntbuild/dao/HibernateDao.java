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
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.remoting.SearchCriteria;
import com.luntsys.luntbuild.remoting.BuildScheduleFacade;
import com.luntsys.luntbuild.remoting.BuildFacade;
import net.sf.hibernate.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

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
			ListIterator  itProperty = session.find("from Property").listIterator();
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

	public void saveProject(Project project) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.saveOrUpdate(project);
		} catch (HibernateException ex) {
			logger.error("Error in saveProject: ", ex);
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

	public Project loadProject(long projectId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Project project left join fetch " +
					"project.views where project.id = :projectId");
			query.setParameter("projectId", new Long(projectId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified project can not " +
						"be found in the database!");
			Project project = (Project) results.get(0);
			Hibernate.initialize(project.getVcsLogins());
			return project;
		} catch (HibernateException ex) {
			logger.error("Error in loadProject: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteProject(long projectId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Iterator it = session.find("select build.id from Build build where build." +
					"buildSchedule.view.project.id = ?",
					new Long(projectId), Hibernate.LONG).listIterator();
			Map properties = loadProperties();
			while (it.hasNext()) {
				long buildId = ((Long) it.next()).longValue();
				Build build = new Build();
				build.setId(buildId);
				Luntbuild.deleteDir(build.getPublishDir(properties));
			}
			session.delete("from Project project where project.id = ?",
					new Long(projectId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteProject: ", ex);
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
			User user = (User)results.get(0);
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

	public void saveView(View view) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createSQLQuery("delete from NOTIFICATION_CONFIG " +
					"where FK_VIEW_ID=:viewId", "", NotificationConfig.class);
			query.setLong("viewId", view.getId());
			query.list();
			query = session.createSQLQuery("delete from VIEW_COMPOSITION_CONFIG " +
					"where FK_COMPOSITE_VIEW_ID=:viewId", "", ViewCompositionConfig.class);
			query.setLong("viewId", view.getId());
			query.list();

			if (view.getNotificationConfigs() != null) {
				Iterator it = view.getNotificationConfigs().keySet().iterator();
				while (it.hasNext()) {
					User user = (User) it.next();
					NotificationConfig config = (NotificationConfig) view.getNotificationConfigs().get(user);
					config.setId(0);
				}
			}

			if (view.getBasicViewConfigs() != null) {
				Iterator it = view.getBasicViewConfigs().keySet().iterator();
				while (it.hasNext()) {
					View basicView = (View) it.next();
					ViewCompositionConfig config = (ViewCompositionConfig) view.getBasicViewConfigs().get(basicView);
					config.setId(0);
				}
			}
			session.saveOrUpdate(view);
  		} catch (HibernateException ex) {
			logger.error("Error in saveView: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public View loadView(long viewId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from View view inner join fetch " +
					"view.project where view.id = :viewId");
			query.setParameter("viewId", new Long(viewId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified view can not be " +
						"found in the database!");
			View view = (View) results.get(0);
			Hibernate.initialize(view.getBuildSchedules());
			Hibernate.initialize(view.getNotificationConfigs());
			Hibernate.initialize(view.getBasicViewConfigs());
			return view;
		} catch (HibernateException ex) {
			logger.error("Error in loadView: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteView(long viewId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Iterator it = session.find("select build.id from Build build where " +
					"build.buildSchedule.view.id = ?",
					new Long(viewId), Hibernate.LONG).listIterator();
			Map properties = loadProperties();
			while (it.hasNext()) {
				long buildId = ((Long) it.next()).longValue();
				Build build = new Build();
				build.setId(buildId);
				Luntbuild.deleteDir(build.getPublishDir(properties));
			}
			session.delete("from View view where view.id = ?",
					new Long(viewId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteView: ", ex);
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

	/**
	 * @return load all schedules
	 */
	public List loadSchedules() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			return session.find("from Schedule schedule order by schedule.name");
		} catch (HibernateException ex) {
			logger.error("Error in loadSchedules: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Schedule loadSchedule(long scheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Schedule schedule left join " +
					"fetch schedule.buildSchedules where schedule.id = :scheduleId");
			query.setParameter("scheduleId", new Long(scheduleId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified schedule can not " +
						"be found in the database!");
			return (Schedule) results.get(0);
		} catch (HibernateException ex) {
			logger.error("Error in loadSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteSchedule(long scheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Iterator it = session.find("select build.id from Build build where " +
					"build.buildSchedule.schedule.id = ?",
					new Long(scheduleId), Hibernate.LONG).listIterator();
			Map properties = loadProperties();
			while (it.hasNext()) {
				long buildId = ((Long) it.next()).longValue();
				Build build = new Build();
				build.setId(buildId);
				Luntbuild.deleteDir(build.getPublishDir(properties));
			}
			session.delete("from Schedule schedule where schedule.id = ?",
					new Long(scheduleId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List loadBuildSchedules() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			return session.find("from BuildSchedule buildSchedule " +
					"inner join fetch buildSchedule.view " +
					"inner join fetch buildSchedule.view.project " +
					"inner join fetch buildSchedule.schedule " +
					"order by buildSchedule.view.project.name, " +
					"buildSchedule.view.name, buildSchedule.schedule.name");
		} catch (HibernateException ex) {
			logger.error("Error in loadBuildSchedules: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List loadViews() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			return session.find("from View view inner join fetch view.project order by " +
					"view.project.name, view.name");
		} catch (HibernateException ex) {
			logger.error("Error in loadViews: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public BuildSchedule loadBuildSchedule(long buildScheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from BuildSchedule buildSchedule " +
					"inner join fetch buildSchedule.view " +
					"inner join fetch buildSchedule.view.project  " +
					"inner join fetch buildSchedule.schedule " +
					"where buildSchedule.id = :buildScheduleId");
			query.setParameter("buildScheduleId", new Long(buildScheduleId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified build schedule " +
						"can not be found in the database!");
			return (BuildSchedule) results.get(0);
		} catch (HibernateException ex) {
			logger.error("Error in loadBuildSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void saveBuildSchedule(BuildSchedule buildSchedule) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.saveOrUpdate(buildSchedule);
		} catch (HibernateException ex) {
			logger.error("Error in saveBuildSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteBuildSchedule(long buildScheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Iterator it = session.find("select build.id from Build build " +
					"where build.buildSchedule.id = ?",
					new Long(buildScheduleId), Hibernate.LONG).listIterator();
			Map properties = loadProperties();
			while (it.hasNext()) {
				long buildId = ((Long) it.next()).longValue();
				Build build = new Build();
				build.setId(buildId);
				Luntbuild.deleteDir(build.getPublishDir(properties));
			}
			session.delete("from BuildSchedule buildSchedule " +
					"where buildSchedule.id = ?",
					new Long(buildScheduleId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteBuildSchedule: ", ex);
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
			Query query = session.createQuery("from Build build inner join fetch build.buildSchedule "+
					"inner join fetch build.buildSchedule.view " +
					"inner join fetch build.buildSchedule.view.project " +
					"inner join fetch build.buildSchedule.schedule " +
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

	public void deleteBuild(long buildId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Build build = new Build();
			build.setId(buildId);
			Luntbuild.deleteDir(build.getPublishDir(loadProperties()));
			session.delete("from Build build where build.id = ?", new Long(buildId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build getLastBuild(BuildSchedule buildSchedule) {
		if (buildSchedule.getBuilds() == null)
			return null;
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.lock(buildSchedule, LockMode.NONE);
			Query query = session.createFilter(buildSchedule.getBuilds(), "order by this.id desc");
			query.setMaxResults(1);
			List results = query.list();
			if (results.size() != 0)
				return (Build) results.get(0);
			else
				return null;
		} catch (HibernateException ex) {
			logger.error("Error in getLastBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build getLastSuccessBuild(BuildSchedule buildSchedule) {
		if (buildSchedule.getBuilds() == null)
			return null;
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.lock(buildSchedule, LockMode.NONE);
			Query query = session.createFilter(buildSchedule.getBuilds(), "where this.status = " +
					":buildStatus order by this.id desc");
			query.setParameter("buildStatus", new Integer(BuildFacade.SUCCESS), Hibernate.INTEGER);
			query.setMaxResults(1);
			List results = query.list();
			if (results.size() != 0)
				return (Build) results.get(0);
			else
				return null;
		} catch (HibernateException ex) {
			logger.error("Error in getLastBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public int getTotalBuilds(BuildSchedule buildSchedule) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.lock(buildSchedule, LockMode.NONE);
			Query query = session.createFilter(buildSchedule.getBuilds(), "select count(*)");
			return ((Integer)query.list().get(0)).intValue();
		} catch (HibernateException ex) {
			logger.error("Error in getTotalBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public int getTotalBuilds(com.luntsys.luntbuild.remoting.SearchCriteria searchCriteria) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select count(*) from Build build " +
					buildHQLWhereClause(searchCriteria));
			fillQueryParams(query, searchCriteria);
			return ((Integer)query.list().get(0)).intValue();
		} catch (HibernateException ex) {
			logger.error("Error in getTotalBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List searchBuilds(com.luntsys.luntbuild.remoting.SearchCriteria searchCriteria, int start, int count) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select build from Build build " +
					"inner join fetch build.buildSchedule " +
					"inner join fetch build.buildSchedule.schedule " +
					"inner join fetch build.buildSchedule.view " +
					"inner join fetch build.buildSchedule.view.project " +
					buildHQLWhereClause(searchCriteria) + " order by build.id desc");
			fillQueryParams(query, searchCriteria);
			query.setFirstResult(start);
			if (count != 0)
				query.setMaxResults(count);
			return query.list();
		} catch (HibernateException ex) {
			logger.error("Error in getTotalBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteBuilds(SearchCriteria searchCriteria) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("select build.id from Build build " +
					buildHQLWhereClause(searchCriteria));
			fillQueryParams(query, searchCriteria);
			Iterator it = query.list().listIterator();
			Map properties = loadProperties();
			while (it.hasNext()) {
				long buildId = ((Long) it.next()).longValue();
				Build build = new Build();
				build.setId(buildId);
				Luntbuild.deleteDir(build.getPublishDir(properties));
				session.delete("from Build build where build.id = ?",
						new Long(buildId), Hibernate.LONG);
			}
		} catch (HibernateException ex) {
			logger.error("Error in deleteBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build getRunningBuild(BuildSchedule buildSchedule) {
		if (buildSchedule.getBuilds() == null)
			return null;
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.lock(buildSchedule, LockMode.NONE);
			Query query = session.createFilter(buildSchedule.getBuilds(), "where this.status = " +
					":buildStatus");
			query.setParameter("buildStatus", new Integer(BuildFacade.RUNNING), Hibernate.INTEGER);
			query.setMaxResults(1);
			List results = query.list();
			if (results.size() != 0)
				return (Build) results.get(0);
			else
				return null;
		} catch (HibernateException ex) {
			logger.error("Error in getRunningBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void moveBuilds(com.luntsys.luntbuild.remoting.SearchCriteria searchCriteria, long buildScheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createSQLQuery("update BUILD " +
					"set FK_BUILD_SCHEDULE_ID = :destBuildScheduleId " +
					buildSQLWhereClause(searchCriteria), "", Build.class);
			query.setLong("destBuildScheduleId", buildScheduleId);
			fillQueryParams(query, searchCriteria);
			query.list();
		} catch (HibernateException ex) {
			logger.error("Error in moveBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	/**
	 * Build HQL where clause based on specified search criteria
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
		if (searchCriteria.getStatus() != BuildFacade.UNKNOWN) {
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
		if (searchCriteria.getBuildScheduleId() != 0) {
			if (whereClause.equals(""))
				whereClause += "where build.buildSchedule.id = :buildScheduleId ";
			else
				whereClause += "and build.buildSchedule.id = :buildScheduleId ";
		}
		return whereClause;
	}

	/**
	 * Build SQL where clause based on specified search criteria
	 * @param searchCriteria
	 * @return
	 */
	private String buildSQLWhereClause(SearchCriteria searchCriteria) {
		String whereClause = "";
		if (!Luntbuild.isEmpty(searchCriteria.getVersion())) {
			if (searchCriteria.isExactMatch())
				whereClause += "where VERSION = :version ";
			else
				whereClause += "where VERSION like :version ";
		}
		if (searchCriteria.getStatus() != BuildFacade.UNKNOWN) {
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
		if (searchCriteria.getBuildScheduleId() != 0) {
			if (whereClause.equals(""))
				whereClause += "where FK_BUILD_SCHEDULE_ID = :buildScheduleId ";
			else
				whereClause += "and FK_BUILD_SCHEDULE_ID = :buildScheduleId ";
		}
		return whereClause;
	}

	/**
	 * Fill query parameters with specified search criteria
	 * @param query
	 * @param searchCriteria
	 */
	private void fillQueryParams(Query query, com.luntsys.luntbuild.remoting.SearchCriteria searchCriteria) {
		if (!Luntbuild.isEmpty(searchCriteria.getVersion())) {
			if (searchCriteria.isExactMatch())
				query.setString("version", searchCriteria.getVersion());
			else
				query.setString("version", searchCriteria.getVersion() + "%");
		}
		if (searchCriteria.getStatus() != BuildFacade.UNKNOWN)
			query.setInteger("status", searchCriteria.getStatus());
		if (searchCriteria.getFrom() != null)
			query.setDate("from", searchCriteria.getFrom());
		if (searchCriteria.getTo() != null)
			query.setDate("to", searchCriteria.getTo());
		if (searchCriteria.getBuildScheduleId() != 0)
			query.setLong("buildScheduleId", searchCriteria.getBuildScheduleId());
	}

	public void failUnfinishedBuilds() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createSQLQuery("update BUILD set STATUS = :failedStatus, " +
					"END_DATE = :currentDate where STATUS  = :unfinishedStatus",
					"", Build.class);
			query.setInteger("failedStatus", BuildFacade.FAILED);
			query.setInteger("unfinishedStatus", BuildFacade.RUNNING);
			query.setTimestamp("currentDate", new Date());
			query.list();
		} catch (HibernateException ex) {
			logger.error("Error in failUnfinishedBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void failUnfinishedSchedules() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createSQLQuery("update BUILD_SCHEDULE " +
					"set STATUS = :failedStatus, " +
					"STATUS_DATE = :currentDate where STATUS  = :unfinishedStatus",
					"", BuildSchedule.class);
			query.setInteger("failedStatus", BuildScheduleFacade.FAILED);
			query.setInteger("unfinishedStatus", BuildScheduleFacade.RUNNING);
			query.setTimestamp("currentDate", new Date());
			query.list();
		} catch (HibernateException ex) {
			logger.error("Error in failUnfinishedSchedules: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void createUserCheckin() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createSQLQuery("insert into USER " +
					"values(:userCheckinId, :userCheckinName, '', :contacts)",
					"", User.class);
			query.setLong("userCheckinId", User.USER_CHECKIN_ID);
			query.setString("userCheckinName", User.USER_CHECKIN_NAME);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(new HashMap());
			} catch (IOException e) {
				throw new HibernateException(e);
			}
			query.setBinary("contacts", baos.toByteArray());
			query.list();
		} catch (HibernateException ex) {
			logger.error("Error in createUserCheckin: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}
}