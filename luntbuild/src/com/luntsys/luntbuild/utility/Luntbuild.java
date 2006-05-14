/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-5-10
 * Time: 9:54:34
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
package com.luntsys.luntbuild.utility;

import com.luntsys.luntbuild.build.LuntbuildLogger;
import com.luntsys.luntbuild.dao.Dao;
import com.luntsys.luntbuild.remoting.ILuntbuild;
import com.luntsys.luntbuild.services.SchedService;
import com.luntsys.luntbuild.vcs.Vcs;
import com.luntsys.luntbuild.web.Home;
import ognl.OgnlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.Touch;
import org.apache.tools.ant.types.Commandline;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is a utility class to provides easy access some commonly used
 * objects.
 * 
 * @author robin shine
 */
public class Luntbuild {
	public static final String TRIGGER_NAME_SEPERATOR = "$";
	private static Log logger = LogFactory.getLog(Luntbuild.class);
	public static final int PAGE_REFRESH_INTERVAL = 15;
	public static final int DEFAULT_MAIL_PORT = 25;
	/**
	 * The block size when operating files
	 */
	public static final int FILE_BLOCK_SIZE = 25000;

	/**
	 * The date display format at luntbuild web interface
	 */
	public static final SimpleDateFormat DATE_DISPLAY_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public static final String log4jFileName = "luntbuild_log.txt";

	/**
	 * The application wide context for use in spring framework
	 */
	public static XmlWebApplicationContext appContext;

	/**
	 * The installation directory for luntbuild
	 */
	public static String installDir;

	public static Properties buildInfos;

	/**
	 * List of vcs adaptor classes found in the system
	 */
	public static List vcsAdaptors;

	/**
	 * List of notifier classes found in the system
	 */
	public static List notifiers;

	/**
	 * List of listener classes found in the system
	 */
	public static List listeners;

	/**
	 * Provides easy access to data access object
	 * 
	 * @return 
	 */
	public static Dao getDao() {
		String message;
		if (appContext == null) {
			message = "Application context not initialized!";
			logger.error(message);
			throw new RuntimeException(message);
		}
		Dao dao = (Dao) appContext.getBean("dao");
		if (dao == null) {
			message = "Failed to find bean \"dao\" in application context!";
			logger.error(message);
			throw new RuntimeException(message);
		}
		return dao;
	}

	/**
	 * Provides easy access to sched service object
	 *
	 * @return
	 * @throws RuntimeException
	 */
	public static SchedService getSchedService() {
		String message;
		if (appContext == null) {
			message = "Application context not initialized!";
			logger.error(message);
			throw new RuntimeException(message);
		}
		SchedService schedService = (SchedService) appContext.getBean("schedService");
		if (schedService == null) {
			message = "Failed to find bean \"schedService\" in application context!";
			logger.error(message);
			throw new RuntimeException(message);
		}
		return schedService;
	}

	/**
	 * Provides easy access to luntbuild service object
	 *
	 * @return
	 * @throws RuntimeException
	 */
	public static ILuntbuild getLuntbuildService() {
		String message;
		if (appContext == null) {
			message = "Application context not initialized!";
			logger.error(message);
			throw new RuntimeException(message);
		}
		ILuntbuild luntbuildService = (ILuntbuild) appContext.getBean("luntbuildService");
		if (luntbuildService == null) {
			message = "Failed to find bean \"luntbuildService\" in application context!";
			logger.error(message);
			throw new RuntimeException(message);
		}
		return luntbuildService;
	}

	/**
	 * This class will dig all messages for a throwable object
	 *
	 * @param throwable
	 * @return
	 */
	public static String getExceptionMessage(Throwable throwable) {
		String message;
		if (throwable instanceof OgnlException) {
			OgnlException ognlException = (OgnlException)throwable;
			message = ognlException.getClass().getName();
			if (ognlException.getMessage() != null)
				message += "(" + ognlException.getMessage() + ")";
			if (ognlException.getReason() != null) {
				message += "\nReason: " + ognlException.getReason().getClass().getName();
				if (ognlException.getReason().getMessage() != null)
					message += "(" + ognlException.getReason().getMessage() + ")";
			}
			return message;
		} else {
			message = throwable.getClass().getName();
			if (throwable.getMessage() != null)
				message += "(" + throwable.getMessage() + ")";
			Throwable cause = throwable.getCause();
			while (cause != null) {
				message += "\nCause: " + cause.getClass().getName();
				if (cause.getMessage() != null)
					message += "(" + cause.getMessage() + ")";
				cause = cause.getCause();
			}
			return message;			
		}
	}

	/**
	 * This method use an ant task to delete a directory and all the contents inside of it
	 * 
	 * @param directory 
	 * @throws org.apache.tools.ant.BuildException
	 *          when delete fails
	 */
	public static void deleteDir(String directory) {
		Delete deleteTask = new Delete();
		deleteTask.setProject(new org.apache.tools.ant.Project());
		deleteTask.getProject().init();
		deleteTask.setDir(new File(directory));
		deleteTask.execute();
	}

	/**
	 * Delete all contents inside a directory
	 * 
	 * @param directory 
	 * @throws org.apache.tools.ant.BuildException
	 *          when cleanup fails
	 */
	public static void cleanupDir(String directory) {
		deleteDir(directory);
		createDir(directory);
	}

	/**
	 * This method use ant task to delete a file
	 * 
	 * @param file 
	 * @throws org.apache.tools.ant.BuildException
	 *          
	 */
	public static void deleteFile(String file) {
		Delete deleteTask = new Delete();
		deleteTask.setProject(new org.apache.tools.ant.Project());
		deleteTask.getProject().init();
		deleteTask.setFile(new File(file));
		deleteTask.execute();
	}

	/**
	 * This method use ant task to creates a new directory
	 * 
	 * @param directory 
	 * @throws org.apache.tools.ant.BuildException
	 *          
	 */
	public static void createDir(String directory) {
		Mkdir mkdirTask = new Mkdir();
		mkdirTask.setProject(new org.apache.tools.ant.Project());
		mkdirTask.getProject().init();
		mkdirTask.setDir(new File(directory));
		mkdirTask.execute();
	}

	/**
	 * Touch a file to update its modification time, if this file is not exist, it will be
	 * created
	 *
	 * @param file
	 * @throws org.apache.tools.ant.BuildException
	 *
	 */
	public static void touchFile(String file) {
		Touch touchTask = new Touch();
		touchTask.setProject(new org.apache.tools.ant.Project());
		touchTask.getProject().init();
		touchTask.setFile(new File(file));
		touchTask.execute();
	}

	/**
	 * Sends the content of the specified file to the web browser
	 * 
	 * @param cycle    the request cycle for current http request
	 * @param filePath using the header user-agent
	 */
	public static void sendFile(IRequestCycle cycle, String filePath) {
		BufferedReader reader = null;
		FileInputStream in = null;
		File file = new File(filePath);
		try {
			HttpServletResponse response = cycle.getRequestContext().getResponse();
			if (filePath.endsWith(".txt")) {
				// convert EOL flag of log text file to the EOL flag of requesting host
				response.setHeader("Content-disposition", "inline;filename=" + file.getName());
				response.setContentType("text/plain");
				reader = new BufferedReader(new FileReader(file));
				ServletOutputStream out = response.getOutputStream();
				String line;
				int length = 0;
				String eol = getEol(cycle.getRequestContext().getRequest());
				List lines = new ArrayList();
				while ((line = reader.readLine()) != null) {
					length += line.getBytes().length;
					length += eol.getBytes().length;
					lines.add(line + eol);
				}
				response.setContentLength(length);

				Iterator it = lines.iterator();
				while (it.hasNext()) {
					String s = (String) it.next();
					out.write(s.getBytes());
				}
			} else {
				response.setHeader("Content-disposition", "filename=" + file.getName());
				String contentType = cycle.getRequestContext().getRequest().getSession().
						getServletContext().getMimeType(filePath);
				if (Luntbuild.isEmpty(contentType))
					response.setContentType("application/octet-stream");
				else
					response.setContentType(contentType);
				in = new FileInputStream(filePath);
				byte[] data = new byte[FILE_BLOCK_SIZE];
				response.setContentLength(new Long(file.length()).intValue());
				ServletOutputStream out = response.getOutputStream();
				while (in.read(data) > -1) {
					out.write(data);
				}
			}
			response.flushBuffer();
		} catch (IOException e) {
			logger.error("IOException during sendFile: ", e);
			throw new ApplicationRuntimeException(e);
		} finally {
			try {
				if (in != null)
					in.close();
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				logger.error("Failed to close file: " + filePath, e);
			}
		}
	}

	public static String getEol(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		if (userAgent == null)
			return "\n";
		if (userAgent.matches(".*Windows.*"))
			return "\r\n";
		return "\n";
	}

	/**
	 * Calculate the actual date time by specifying a time of the format hh:mm of today
	 * 
	 * @param hhmm 
	 * @return 
	 */
	public static Date getDateByHHMM(String hhmm) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
			Date timeDate = sdf.parse(hhmm);
			Calendar timeCal = Calendar.getInstance(); // only time part of this calendar will be used
			timeCal.setTime(timeDate);
			Calendar dateCal = Calendar.getInstance(); // only date part of this calendar will be used
			dateCal.setTimeInMillis(System.currentTimeMillis());
			dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
			dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
			dateCal.set(Calendar.SECOND, 0);
			dateCal.set(Calendar.MILLISECOND, 0);
			return dateCal.getTime();
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Convert the version to a label that propers to applied to various version control system.
	 * 
	 * @param version 
	 * @return 
	 */
	public static String getLabelByVersion(String version) {
		return version.trim().replace('.', '_').replaceAll("[\\s]", "-");
	}

	/**
	 * Concatenate two path into one path, the main functionality is to detect the
	 * trailing "/" of path1, and leading "/" of path2, and forms only one "/" in the joined
	 * path
	 * 
	 * @param path1 
	 * @param path2 
	 * @return 
	 */
	public static String concatPath(String path1, String path2) {
		if (isEmpty(path1)) {
			if (path2 == null)
				return "";
			else
				return path2.trim();
		}
		if (isEmpty(path2))
			return path1.trim();

		String trimmedPath1 = path1.trim();
		String trimmedPath2 = path2.trim();
		String path;
		if (trimmedPath1.charAt(trimmedPath1.length() - 1) == '/' ||
				trimmedPath1.charAt(trimmedPath1.length() - 1) == '\\') {
			if (trimmedPath2.charAt(0) == '/' || trimmedPath2.charAt(0) == '\\') {
				if (trimmedPath1.length() == 1)
					path = trimmedPath2;
				else {
					path = trimmedPath1.substring(0, trimmedPath1.length() - 2);
					path += trimmedPath2;
				}
			} else
				path = trimmedPath1 + trimmedPath2;
		} else {
			if (trimmedPath2.charAt(0) == '/' || trimmedPath2.charAt(0) == '\\')
				path = trimmedPath1 + trimmedPath2;
			else
				path = trimmedPath1 + '/' + trimmedPath2;
		}
		return path;
	}

	/**
	 * Removes the leading '/' or '\' character from the path
	 * 
	 * @param path 
	 * @return 
	 */
	public static String removeLeadingSlash(String path) {
		if (path == null || path.trim().equals(""))
			return "";
		String trimmedPath = path.trim();
		if (trimmedPath.charAt(0) == '/' || trimmedPath.charAt(0) == '\\') {
			if (trimmedPath.length() == 1)
				return "";
			else
				return trimmedPath.substring(1);
		} else
			return trimmedPath;
	}

	/**
	 * Removes the trailing '/' or '\' character from the path
	 * 
	 * @param path 
	 * @return 
	 */
	public static String removeTrailingSlash(String path) {
		if (isEmpty(path))
			return "";
		String trimmedPath = path.trim();
		if (trimmedPath.charAt(trimmedPath.length() - 1) == '/' ||
				trimmedPath.charAt(trimmedPath.length() - 1) == '\\') {
			if (trimmedPath.length() == 1)
				return "";
			else
				return trimmedPath.substring(0, trimmedPath.length() - 2);
		} else
			return trimmedPath;
	}

	/**
	 * Return the lunt build logger for specified antProject, or return null if
	 * it does not contain a lunt build logger
	 * 
	 * @param antProject
	 * @return 
	 */
	public static LuntbuildLogger getLuntBuildLogger(Project antProject) {
		Iterator itListener = antProject.getBuildListeners().iterator();
		while (itListener.hasNext()) {
			Object o = itListener.next();
			if (o instanceof LuntbuildLogger)
				return (LuntbuildLogger) o;
		}
		return null;
	}

	/**
	 * Determines if the specified string is empty
	 * 
	 * @param aString 
	 * @return 
	 */
	public static boolean isEmpty(String aString) {
		if (aString == null || aString.trim().equals(""))
			return true;
		else
			return false;
	}

	/**
	 * Create a cloned copy of specified module
	 * 
	 * @param module 
	 * @return 
	 */
	public static Vcs.Module cloneModule(Vcs vcs, Vcs.Module module) {
		Vcs.Module clone = vcs.createNewModule();
		if (clone == null)
			throw new RuntimeException("Module clone operation failed because " +
					"supplied vcs object does not support module!");

		List srcProperties = module.getProperties();
		List dstProperties = clone.getProperties();
		for (int i = 0; i < srcProperties.size(); i++) {
			DisplayProperty srcProperty = (DisplayProperty) srcProperties.get(i);
			DisplayProperty dstProperty = (DisplayProperty) dstProperties.get(i);
			dstProperty.setValue(new String(srcProperty.getValue()));
		}

		return clone;
	}

	public static String getServletUrl(Map properties) {
		String servletUrl = (String) properties.get("servletUrl");
		if (isEmpty(servletUrl))
			return "http://" + getIpAddress() + ":8080/luntbuild/app";
		else
			return servletUrl;
	}

	/**
	 * Get the host name of the build server
	 * 
	 * @return 
	 */
	public static String getHostName() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostName();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the ip address of the build server
	 * 
	 * @return 
	 */
	public static String getIpAddress() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Determines if specified shorter file recursively contains specified longer file.
	 * Dir will recursively contains longer file when the following conditions meet at same time:
	 * <i> specified shorter is a directory
	 * <i> specified longer is a sub-directory or longer recursively under shorter
	 *
	 * @param shorter
	 * @param longer
	 * @return path of longer relative to shorter, null if longer is not relative to shorter, or "" if
	 * shorter is a directory and is the same as longer
	 * @throws RuntimeException
	 */
	public static String parseRelativePath(File shorter, File longer) {
		try {
			if (!shorter.isDirectory())
				return null;

			String shorterPath = removeTrailingSlash(shorter.getCanonicalFile().getAbsolutePath());
			String longerPath = removeTrailingSlash(longer.getCanonicalFile().getAbsolutePath());

            if (longerPath.startsWith(shorterPath))
				return longerPath.substring(shorterPath.length());
			else
				return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Filter given list of notifier class names to get a list of existing notifier classes
	 *
	 * @param notifierClassNames list of notifier class names, should not be null
	 * @return list of existing notifier classes
	 */
	public static List getNotifierClasses(List notifierClassNames) {
		List notifierClasses = new ArrayList();
		Iterator itClassName = notifierClassNames.iterator();
		while (itClassName.hasNext()) {
			String notifierClassName = (String) itClassName.next();
			Iterator itClass = notifiers.iterator();
			while (itClass.hasNext()) {
				Class notifierClass = (Class) itClass.next();
				if (notifierClass.getName().equals(notifierClassName)) {
					notifierClasses.add(notifierClass);
					break;
				}
			}
		}
		return notifierClasses;
	}

	/**
	 * Convert list of notifier classes to list of notifier instances
	 * @param notifierClasses should not be null
	 * @return
	 */
	public static List getNotifierInstances(List notifierClasses) {
		List notifierInstances = new ArrayList();
		Iterator it = notifierClasses.iterator();
		while (it.hasNext()) {
			Class notifierClass = (Class) it.next();
			try {
				notifierInstances.add(notifierClass.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return notifierInstances;
	}

	/**
	 * Convert list of listener classes to list of listener instances
	 * @param listenerClasses should not be null
	 * @return
	 */
	public static List getListenerInstances(List listenerClasses) {
		List listenerInstances = new ArrayList();
		Iterator it = listenerClasses.iterator();
		while (it.hasNext()) {
			Class listenerClass = (Class) it.next();
			try {
				listenerInstances.add(listenerClass.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return listenerInstances;
	}

	public static String getSystemLogUrl(Map properties) {
		return getServletUrl(properties) + "?service=external/Home&sp=l" +
				Home.SERVICE_PARAMETER_SYSTEMLOG;
	}

	/**
	 * This function parses the input command line string to get rid of special characters such as
	 * quotation, end of line, tab, etc. It also extracts characters inside a pair of quotation to form
	 * a single argument
	 *
	 * @param input
	 * @return
	 */
	public static Commandline parseCmdLine(String input) {
		Commandline cmdLine = new Commandline();
		char inputChars[] = input.toCharArray();
		boolean quoted = false;
		String field = new String("");
		for (int i = 0; i < inputChars.length; i++) {
			char inputChar = inputChars[i];
			switch (inputChar) {
				case '"':
					if (!quoted) {
						quoted = true;
					} else {
						quoted = false;
					}
					break;
				case '\n':
				case '\r':
				case ' ':
				case '\t':
					if (quoted) {
						field += ' ';
					} else {
						field = field.trim();
						if (!field.equals("")) {
							if (cmdLine.getExecutable() == null)
								cmdLine.setExecutable(field);
							else
								cmdLine.createArgument().setValue(field);
							field = new String("");
						}
					}
					break;
				default:
					field += inputChar;
					break;
			}
		}
		field = field.trim();
		if (!field.equals("")) {
			if (cmdLine.getExecutable() == null)
				cmdLine.setExecutable(field);
			else
				cmdLine.createArgument().setValue(field);
		}
		return cmdLine;
	}
}