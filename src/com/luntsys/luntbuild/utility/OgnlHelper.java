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
import java.util.Locale;
import java.text.DateFormatSymbols;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ognl.OgnlException;

/**
 * Helper class for OGNL evaluation.
 *
 * @author alvin shen
 */
public class OgnlHelper {

    private static Log logger = LogFactory.getLog(OgnlHelper.class);

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

	/**
	 * Gets the revisions object for this thread.
	 * 
	 * @return the revisions object
	 */
	public static Revisions getRevisions() {
		return (Revisions) revisions.get();
	}

	/**
	 * Sets the revisions object for this thread.
	 * 
	 * @param revisions the revisions object
	 */
	public static void setRevisions(Revisions revisions) {
		OgnlHelper.revisions.set(revisions);
	}

	/**
	 * Gets the ant project for this thread.
	 * 
	 * @return the ant project
	 */
	public static org.apache.tools.ant.Project getAntProject() {
		return (org.apache.tools.ant.Project) antProject.get();
	}

	/**
	 * Sets the ant project for this thread.
	 * 
	 * @param antProject the ant project
	 */
	public static void setAntProject(org.apache.tools.ant.Project antProject) {
		OgnlHelper.antProject.set(antProject);
	}

	/**
	 * Checks if test mode is on for this thread when evaluating expressions.
	 * 
	 * @return <code>true</code> if test mode is on
	 */
	public static boolean isTestMode() {
		return ((Boolean) OgnlHelper.testMode.get()).booleanValue();
	}

	/**
	 * Sets test mode for this thread when evaluating expressions.
	 * 
	 * @param testMode set <code>true</code> for test mode
	 */
	public static void setTestMode(boolean testMode) {
		OgnlHelper.testMode.set(new Boolean(testMode));
	}

	/**
	 * Gets the currently running schedule for this tread.
	 * 
	 * @return the currently running schedule
	 */
	public static Schedule getWorkingSchedule() {
		return (Schedule) workingSchedule.get();
	}

	/**
	 * Sets the currently running schedule for this tread.
	 * 
	 * @param workingSchedule the currently running schedule
	 */
	public static void setWorkingSchedule(Schedule workingSchedule) {
		OgnlHelper.workingSchedule.set(workingSchedule);
	}

	/**
	 * Evaluates the value of an OGNL expression for the currently working schedule.
	 * 
	 * @param val the OGNL expression
	 * @return the evaluated string
	 */
	public static String evaluateScheduleValue(String val) {
		if (val == null || val.trim().length() == 0) return "";
		Schedule workingSchedule = OgnlHelper.getWorkingSchedule();
		if (workingSchedule != null) {
			try {
				return Luntbuild.evaluateExpression(workingSchedule, val);
			}  catch (OgnlException e) {
				throw new RuntimeException("Unable to evaluate expression \"" + val +
						"\".", e);
			}
		} else
			return val;
	}

	/**
	 * Gets the project with specified name.
	 * 
	 * @param projectName the project name
	 * @return the project
	 */
	public Project getProject(String projectName) {
		return Luntbuild.getDao().loadProject(projectName);
	}

	/**
	 * Empty method, only want to conform with OGNL indexed property requirement.
	 * 
	 * @param projectName the project name
	 * @param project the project
	 */
	public void setProject(String projectName, Project project) {
		// empty methods, only want to conform to ognl indexed property
	}

	/**
	 * Gets the name of this object.
	 * 
	 * @return the name
	 */
	public String getName() {
		return "system";
	}

	/**
	 * Executes the specified command. This method requires the following thread local variables
	 * in <code>OgnlHelper</code>:
	 * <ul>
	 * <li>antProject</li>
	 * </ul>
	 *
	 * @param command the command to execute
	 * @return the result of the command
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
	 * Gets the day of month, possible values are 01 to 31.
	 * 
	 * @return the day of month
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
	 * Gets the day of week, for example: Mon, Sat, etc.
	 * 
	 * @return the day of week
	 */
	public String getDayOfWeek() {
		return new DateFormatSymbols(Locale.US).
				getShortWeekdays()[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)];
	}

	/**
	 * Gets the numeric day of week, possible values are 0 to 6.
	 * 
	 * @return the numeric day of week
	 */
	public String getNumericDayOfWeek() {
		return String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
	}

	/**
	 * Gets the day of year, possible values are 1 to 365.
	 * 
	 * @return the day of year
	 */
	public String getDayOfYear() {
		return String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * Gets the hour, possible values are 00 to 23.
	 * 
	 * @return the hour
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
	 * Gets the minute, possible values are 00 to 59.
	 * 
	 * @return the minute
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
	 * Gets the month, for example: Jan, Feb, etc.
	 * 
	 * @return the month
	 */
	public String getMonth() {
		return new DateFormatSymbols(Locale.US).
				getShortMonths()[Calendar.getInstance().get(Calendar.MONTH)];
	}

	/**
	 * Gets the numeric month, possible values are 01 to 12.
	 * 
	 * @return the numeric month
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
	 * Gets the week of year, for example: 1, 2, etc.
	 * 
	 * @return the week of year
	 */
	public String getWeekOfYear() {
		return String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
	}

	/**
	 * Gets the year, for example: 2005, 2006, etc.
	 * 
	 * @return the year
	 */
	public String getYear() {
		return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	}

	/**
	 * Gets the short representation of year, for example: 05, 06, etc.
	 * 
	 * @return the short representation of year
	 */
	public String getShortYear() {
		return String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2);
	}

    /**
     * Gets the extension class instance with the specified name.
     * 
     * @param name the name
     * @return the extension class instance
     * @throws RuntimeException if no extension with that name was found
     */
    public Object getExtension(String name) {
        Object result = Luntbuild.extensions.get(name);
        if (result == null)
            throw new RuntimeException("Extension named \"" + name + "\" not found.");

        return result;
    }
}
