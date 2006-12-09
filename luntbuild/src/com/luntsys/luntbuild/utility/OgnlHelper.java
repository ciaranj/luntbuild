/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-3-9
 * Time: 14:10:58
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

import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.ant.Commandline;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormatSymbols;

/**
 * This class helps ognl evaluation
 *
 * @author alvin shen
 */
public class OgnlHelper {

	/**
	 * Contains instances of extension classes keyed with extension names.
	 */
	private static Hashtable extensions;

	/**
	 * The schedule this build necessary condition evaluation trigged on behalf of
	 */
	private static ThreadLocal workingSchedule = new ThreadLocal();

	/**
	 * Revisions for project of the working schedule
	 */
	private static ThreadLocal revisions = new ThreadLocal();

	/**
	 * The ant project for logging purpose
	 */
	private static ThreadLocal antProject = new ThreadLocal();

	/**
	 * Is test mode when try to evaluate expression?
	 */
	private static ThreadLocal testMode = new ThreadLocal();

	public static Revisions getRevisions() {
		return (Revisions) revisions.get();
	}

	public static void setRevisions(Revisions revisions) {
		OgnlHelper.revisions.set(revisions);
	}

	public static org.apache.tools.ant.Project getAntProject() {
		return (org.apache.tools.ant.Project) antProject.get();
	}

	public static void setAntProject(org.apache.tools.ant.Project antProject) {
		OgnlHelper.antProject.set(antProject);
	}

	public static boolean isTestMode() {
		return ((Boolean) OgnlHelper.testMode.get()).booleanValue();
	}

	public static void setTestMode(boolean testMode) {
		OgnlHelper.testMode.set(new Boolean(testMode));
	}

	public static Schedule getWorkingSchedule() {
		return (Schedule) workingSchedule.get();
	}

	public static void setWorkingSchedule(Schedule workingSchedule) {
		OgnlHelper.workingSchedule.set(workingSchedule);
	}

	/**
	 * Get project object with specified name
	 * @param projectName
	 * @return project object with specified name
	 */
	public Project getProject(String projectName) {
		return Luntbuild.getDao().loadProject(projectName);
	}

	/**
	 * Empty method, only want to conform with ognl indexed property requirement
	 * @param projectName
	 * @param project
	 */
	public void setProject(String projectName, Project project) {
		// empty methods, only want to conform to ognl indexed property
	}

	public String getName() {
		return "system";
	}

	/**
	 * Execute the specified command. This method requires the following thread local variables
	 * in {@link com.luntsys.luntbuild.utility.OgnlHelper}
	 * <i> antProject
	 *
	 * @param command
	 * @return result
	 */
	public int execute(String command) {
		String message = "Execute command during ognl expression evaluation: " + command;
		getAntProject().log(message);

		Commandline cmdLine = Luntbuild.parseCmdLine(command);
		MyExecTask exec = new MyExecTask("execute", getAntProject(), null, cmdLine, null,
				null, org.apache.tools.ant.Project.MSG_VERBOSE);
		return exec.executeAndGetResult();
	}

	/**
	 * Get day of month, possible values are 01 to 31
	 * @return day of month
	 */
	public String getDayOfMonth() {
		String dayOfMonth;
		int value = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		if (value < 10)
			dayOfMonth = "0" + value;
		else
			dayOfMonth = String.valueOf(value);
		return dayOfMonth;
	}

	/**
	 * Get day of week, for example: Mon, Sat, etc.
	 * @return day of week
	 */
	public String getDayOfWeek() {
		return new DateFormatSymbols(Locale.US).
				getShortWeekdays()[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)];
	}

	/**
	 * Get numeric day of week, possible values are 1 to 7
	 * @return numeric day of week
	 */
	public String getNumericDayOfWeek() {
		return String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
	}

	/**
	 * Get day of year, possible values are 1 to 365
	 * @return day of year
	 */
	public String getDayOfYear() {
		return String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * Get hour, possible values are 00 to 23
	 * @return hour
	 */
	public String getHour() {
		String hour;
		int value = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (value < 10)
			hour = "0" + value;
		else
			hour = String.valueOf(value);
		return hour;
	}

	/**
	 * Get minute, possible values are 00 to 59
	 * @return minute
	 */
	public String getMinute() {
		String minute;
		int value = Calendar.getInstance().get(Calendar.MINUTE);
		if (value < 10)
			minute = "0" + value;
		else
			minute = String.valueOf(value);
		return minute;
	}

	/**
	 * Get month, for example: Jan, Feb, ...
	 * @return month
	 */
	public String getMonth() {
		return new DateFormatSymbols(Locale.US).
				getShortMonths()[Calendar.getInstance().get(Calendar.MONTH)];
	}

	/**
	 * Get numeric month, possible values are 01 to 12
	 * @return numeric month
	 */
	public String getNumericMonth() {
		String numericMonth;
		int value = Calendar.getInstance().get(Calendar.MONTH) + 1;
		if (value < 10)
			numericMonth = "0" + value;
		else
			numericMonth = String.valueOf(value);
		return numericMonth;
	}

	/**
	 * Get week of year, for example: 1, 2, ...
	 * @return week of year
	 */
	public String getWeekOfYear() {
		return String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
	}

	/**
	 * Get year, for example: 2005, 2006, ...
	 * @return year
	 */
	public String getYear() {
		return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	}

	/**
	 * Get short representation of year, for example: 05, 06, ...
	 * @return short representation of year
	 */
	public String getShortYear() {
		return String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2);
	}

	/*
	 * Get a extension class instance.
	 */
	public Object getExtension(String name) {

		Object result = null;

		loadExtensions();

		result = extensions.get(name);
		if (result == null) throw new RuntimeException("Extension " + name + " doesn't exist");

		return result;

	}

	/*
	 * First time this method is run,
	 * 1) it searches classpath for files named luntbuild_extension.properties
	 * 2) it instantiates the classes and stores the instances to Hashtable extensions
	 *
	 * After the first time the method does nothing
	 */
	private synchronized void loadExtensions() {

		if (extensions == null) {

			extensions = new Hashtable();

			Enumeration resourceEnum = null;
			try {
				 resourceEnum = getClass().getClassLoader().getResources("luntbuild_extension.properties");
			} catch (IOException e) {
				throw new RuntimeException("Exception occured while searching for Luntbuild extensions", e);
			}

			while (resourceEnum.hasMoreElements()) {

				URL resourceURL = (URL) resourceEnum.nextElement();

				Properties extensionProperties = new Properties();

				try {
					extensionProperties.load(resourceURL.openStream());
				} catch (IOException e) {
					throw new RuntimeException("Loading of extension properties from " + resourceURL.toExternalForm() + " failed", e);
				}

				String extensionName = (String) extensionProperties.get("luntbuild.extension.name");
				String className = (String) extensionProperties.get("luntbuild.extension.class");

				if (extensionName == null || extensionName.trim().equals("")) throw new RuntimeException("Property luntbuild.extension.name is missing or empty in property file " + resourceURL.toExternalForm());
				if (className == null || className.trim().equals("")) throw new RuntimeException("Property luntbuild.extension.class is missing or empty in property file " + resourceURL.toExternalForm());

				Object extension = null;

				try {
					Class extensionClass = Class.forName(className);
					extension = extensionClass.newInstance();
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("Extension class " + className + " for luntbuild extension " + extensionName + " not found", e);
				} catch (InstantiationException e) {
					throw new RuntimeException("Exception occured while instantiating extension class " + className  + " for luntbuild extension " + extensionName, e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Extension class for " + className + " luntbuild extension " + extensionName + " doesn't have a public constructor", e);
				}

				extensions.put(extensionName, extension);

			}
		}
	}

}
