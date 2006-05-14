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
import com.luntsys.luntbuild.web.ManualBuild;
import net.sf.hibernate.*;
import net.sf.hibernate.sql.Update;
import net.sf.hibernate.type.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.*;

/**
 * Implementor of data access interface using hibernate as persistent layer
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
			Query query = session.createQuery("from Project project left join fetch project.views where project.id = :projectId");
			query.setParameter("projectId", new Long(projectId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified project can not be found in the database!");
			return (Project) results.get(0);
		} catch (HibernateException ex) {
			logger.error("Error in loadProject: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteProject(long projectId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.delete("from Project project where project.id = ?", new Long(projectId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteProject: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void saveView(View view) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.saveOrUpdate(view);
		} catch (HibernateException ex) {
			logger.error("Error in saveView: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public View loadView(long viewId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from View view inner join fetch view.project left join fetch view.modules where view.id = :viewId");
			query.setParameter("viewId", new Long(viewId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified view can not be found in the database!");
			View view = (View) results.get(0);
			Hibernate.initialize(view.getBuildSchedules());
			return view;
		} catch (HibernateException ex) {
			logger.error("Error in loadView: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteView(long viewId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.delete("from View view where view.id = ?", new Long(viewId), Hibernate.LONG);
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
	 * @return all schedules whose id is not 1. The schedule with id 1 is reserved for
	 * manually builds
	 */
	public List loadSchedules() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			return session.find("from Schedule schedule order by schedule.name where schedule.id != ?",
					new Long(ManualBuild.SCHEDULE_ID), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in loadSchedules: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Schedule loadSchedule(long scheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Schedule schedule left join fetch schedule.buildSchedules where schedule.id = :scheduleId");
			query.setParameter("scheduleId", new Long(scheduleId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified schedule can not be found in the database!");
			return (Schedule) results.get(0);
		} catch (HibernateException ex) {
			logger.error("Error in loadSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void deleteSchedule(long scheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.delete("from Schedule schedule where schedule.id = ?", new Long(scheduleId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteSchedule: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public List loadBuildSchedules() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			return session.find("from BuildSchedule buildSchedule inner join fetch buildSchedule.view " +
					"inner join fetch buildSchedule.view.project " +
					"inner join fetch buildSchedule.schedule where buildSchedule.schedule.id != ?",
					new Long(ManualBuild.SCHEDULE_ID), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in loadBuildSchedules: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public BuildSchedule loadBuildSchedule(long buildScheduleId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from BuildSchedule buildSchedule inner join fetch buildSchedule.view " +
					"inner join fetch buildSchedule.view.project  inner join fetch buildSchedule.schedule " +
					"left join fetch buildSchedule.view.modules where buildSchedule.id = :buildScheduleId");
			query.setParameter("buildScheduleId", new Long(buildScheduleId));
			List results = query.list();
			if (results.size() == 0 || results.get(0) == null)
				throw new DataRetrievalFailureException("Specified build schedule can not be found in the database!");
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
			session.delete("from BuildSchedule buildSchedule where buildSchedule.id = ?", new Long(buildScheduleId), Hibernate.LONG);
		} catch (HibernateException ex) {
			logger.error("Error in deleteBuildSchedule: ", ex);
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

	public Build loadBuild(BuildSchedule buildSchedule, long buildId) {
		if (buildSchedule.getBuilds() == null)
			return null;
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.lock(buildSchedule, LockMode.NONE);
			Query query = session.createFilter(buildSchedule.getBuilds(), "where this.id = :buildId");
			query.setLong("buildId", buildId);
			List results = query.list();
			if (results.size() != 0)
				return (Build) results.get(0);
			else
				return null;
		} catch (HibernateException ex) {
			logger.error("Error in loadBuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build loadBuild(long buildId) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Build build inner join fetch build.buildSchedule "+
					"inner join fetch build.buildSchedule.view " +
					"inner join fetch build.buildSchedule.view.project  inner join fetch build.buildSchedule.schedule " +
					"left join fetch build.buildSchedule.view.modules where build.id = :buildId");
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
			Query query = session.createFilter(buildSchedule.getBuilds(), "where this.status = :buildStatus order by this.id desc");
			query.setParameter("buildStatus", new Integer(Build.SUCCESS), Hibernate.INTEGER);
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

	public List loadBuilds(BuildSchedule buildSchedule, int start,  int count) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			session.lock(buildSchedule, LockMode.NONE);
			Query query = session.createFilter(buildSchedule.getBuilds(), "order by this.id desc");
			query.setFirstResult(start - 1);
			query.setMaxResults(count);
			return query.list();
		} catch (HibernateException ex) {
			logger.error("Error in loadBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public void failUnfinishedBuilds() {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createSQLQuery("update BUILD set STATUS = :failedStatus, END_DATE = :currentDate " +
					"where STATUS  = :unfinishedStatus", "", Build.class);
			query.setInteger("failedStatus", Build.FAILED);
			query.setInteger("unfinishedStatus", Build.NOT_FINISHED);
			query.setTimestamp("currentDate", new Date(System.currentTimeMillis()));
			query.list();
		} catch (HibernateException ex) {
			logger.error("Error in failUnfinishedBuilds: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	public Build getLastRebuild(View view) {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), false);
		try {
			Query query = session.createQuery("from Build build inner join fetch build.buildSchedule buildSchedule " +
					"where buildSchedule.view.id = :viewId and build.rebuild = true order by build.startDate desc");
			query.setParameter("viewId", new Long(view.getId()), Hibernate.LONG);
			query.setMaxResults(1);
			List results = query.list();
			if (results.size() != 0)
				return (Build) results.get(0);
			else
				return null;
		} catch (HibernateException ex) {
			logger.error("Error in getLastRebuild: ", ex);
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}
}