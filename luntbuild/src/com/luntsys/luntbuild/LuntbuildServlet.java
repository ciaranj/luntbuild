/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-18
 * Time: 10:31:54
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
package com.luntsys.luntbuild;

import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.listeners.Listener;
import com.luntsys.luntbuild.notifiers.Notifier;
import com.luntsys.luntbuild.utility.CompositeVcsAdaptor;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.vcs.VcsAdaptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.ApplicationServlet;
import org.apache.tapestry.multipart.DefaultMultipartDecoder;
import org.sadun.util.ClassPackageExplorer;
import org.sadun.util.SimpleClassPackageExplorer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.dao.DataAccessException;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Properties;

/**
 * This servlet overrides the tapestry {@link org.apache.tapestry.ApplicationServlet} to provides
 * some extra behaviors during servlet initialization and destroy
 *
 * @author robin shine
 */
public class LuntbuildServlet extends ApplicationServlet {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(LuntbuildServlet.class);
	private static final String VCS_PACKAGE_NAME = "com.luntsys.luntbuild.vcs";
	private static final String NOTIFIER_PACKAGE_NAME = "com.luntsys.luntbuild.notifiers";
	private static final String LISTENER_PACKAGE_NAME = "com.luntsys.luntbuild.listeners";

	/**
	 * Do some initialization works
	 *
	 * @throws ServletException
	 */
	public void init() throws ServletException {
		try {
			Luntbuild.installDir = getServletConfig().getInitParameter("installDir");

			if (Luntbuild.installDir == null || Luntbuild.installDir.trim().equals(""))
				throw new RuntimeException("Missing parameter \"installDir\" for lunt build servlet");

			// load build informations
			Luntbuild.buildInfos = new Properties();
			Luntbuild.buildInfos.load(new FileInputStream(Luntbuild.installDir + "/buildInfo.properties"));
			// load and configure log4j properties to specify the log file
			Properties log4jProperties = new Properties();
			InputStream is = getServletContext().getResourceAsStream("/WEB-INF/log4j.properties");
			if (is == null)
				throw new RuntimeException("Failed to find /WEB-INF/log4j.properties");
			log4jProperties.load(is);
			log4jProperties.setProperty("log4j.appender.logfile.File",
					new File(Luntbuild.installDir + "/logs/" + Luntbuild.log4jFileName).getAbsolutePath());
			PropertyConfigurator.configure(log4jProperties);

			logger.debug("Enter servlet initialization");

			super.init();

			XmlWebApplicationContext xwac = new XmlWebApplicationContext();
			xwac.setServletContext(getServletContext());
			xwac.setParent(null);
			xwac.setConfigLocations(new String[]{"/WEB-INF/applicationContext.xml"});
			Properties props = new Properties();

			String hsqlDataset = new File(Luntbuild.installDir + "/db/luntbuild").getAbsolutePath();
			props.setProperty("hsqlUrl", "jdbc:hsqldb:" + hsqlDataset);

			PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
			cfg.setProperties(props);
			xwac.addBeanFactoryPostProcessor(cfg);
			xwac.refresh();

			Luntbuild.appContext = xwac;

			loadVcsAdaptors();
			loadNotifiers();
			loadListeners();

			// mark unfinished builds as failed
			Luntbuild.getDao().failUnfinishedBuilds();
			// mark unfinished schedule executions as failed
			Luntbuild.getDao().failUnfinishedSchedules();
			// check pre-defined users
			try {
				Luntbuild.getDao().loadUser(User.USER_CHECKIN_ID);
			} catch (DataAccessException ex) {
				Luntbuild.getDao().createUserCheckin();
			}

			// cleanup temp directory
			Luntbuild.cleanupDir(Luntbuild.installDir + "/tmp");

			// setup upload parameters
			DefaultMultipartDecoder.getSharedInstance().setRepositoryPath(Luntbuild.installDir + "/tmp");
			DefaultMultipartDecoder.getSharedInstance().setMaxSize(-1); // set no limit on max upload size
			DefaultMultipartDecoder.getSharedInstance().setThresholdSize(Luntbuild.FILE_BLOCK_SIZE);

			Luntbuild.getSchedService().startup();
			Luntbuild.getSchedService().rescheduleBuilds();
			logger.debug("Leave servlet initialization");
		} catch (Throwable throwable) {
			logger.error("Exception catched in LuntbuildServlet.init()", throwable);
		}
	}

	private void loadVcsAdaptors() {
		ClassPackageExplorer pkgExplorer = new SimpleClassPackageExplorer(getServletContext().getRealPath("/") + "WEB-INF/classes");
		String[] classNames = pkgExplorer.listPackage(VCS_PACKAGE_NAME);
		Luntbuild.vcsAdaptors = new ArrayList();
		for (int i = 0; i < classNames.length; i++) {
			String aClassName = classNames[i];
			try {
				Class aClass = Class.forName(aClassName);
				if (VcsAdaptor.class.isAssignableFrom(aClass) && !Modifier.isAbstract(aClass.getModifiers())) {
					Luntbuild.vcsAdaptors.add(aClass);
					logger.info("Vcs adaptor \"" + aClassName + "\" found");
				}
			} catch (Exception e) {
				logger.fatal("Failed to load class: " + aClassName, e);
				throw new ApplicationRuntimeException(e);
			}
		}
		if (Luntbuild.vcsAdaptors.size() == 0) {
			logger.fatal("No version control adaptor found in package: \"" + VCS_PACKAGE_NAME + "\"");
			throw new ApplicationRuntimeException("No version control adaptor found!");
		} else { // add the composite vcs adaptor
			Luntbuild.vcsAdaptors.add(CompositeVcsAdaptor.class);
		}
	}

	private void loadNotifiers() {
		ClassPackageExplorer pkgExplorer = new SimpleClassPackageExplorer(getServletContext().getRealPath("/") + "WEB-INF/classes");
		String[] classNames = pkgExplorer.listPackage(NOTIFIER_PACKAGE_NAME);
		Luntbuild.notifiers = new ArrayList();
		for (int i = 0; i < classNames.length; i++) {
			String aClassName = classNames[i];
			try {
				Class aClass = Class.forName(aClassName);
				if (Notifier.class.isAssignableFrom(aClass) && !Modifier.isAbstract(aClass.getModifiers())) {
					Luntbuild.notifiers.add(aClass);
					logger.info("Notifier \"" + aClassName + "\" found");
				}
			} catch (Exception e) {
				logger.fatal("Failed to load class: " + aClassName, e);
				throw new ApplicationRuntimeException(e);
			}
		}
	}

	private void loadListeners() {
		ClassPackageExplorer pkgExplorer = new SimpleClassPackageExplorer(getServletContext().getRealPath("/") + "WEB-INF/classes");
		String[] classNames = pkgExplorer.listPackage(LISTENER_PACKAGE_NAME);
		Luntbuild.listeners = new ArrayList();
		for (int i = 0; i < classNames.length; i++) {
			String aClassName = classNames[i];
			try {
				Class aClass = Class.forName(aClassName);
				if (Listener.class.isAssignableFrom(aClass) && !Modifier.isAbstract(aClass.getModifiers())) {
					Luntbuild.listeners.add(aClass);
					logger.info("Listener \"" + aClassName + "\" found");
				}
			} catch (Exception e) {
				logger.fatal("Failed to load class: " + aClassName, e);
				throw new ApplicationRuntimeException(e);
			}
		}
	}

	/**
	 * Do some cleanup works, such as cleanup the schedule thread, etc.
	 */
	public void destroy() {
		logger.debug("Enter servlet destroy");
		super.destroy();
		Luntbuild.getSchedService().shutdown();
		Luntbuild.appContext.close();
		logger.debug("Leave servlet destroy");
	}
}
