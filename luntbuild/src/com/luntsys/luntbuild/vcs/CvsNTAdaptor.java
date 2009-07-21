/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-4-26
 * Time: 11:38:40
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

package com.luntsys.luntbuild.vcs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import com.luntsys.luntbuild.ant.Commandline;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.facades.lb12.CvsNTAdaptorFacade;
import com.luntsys.luntbuild.facades.lb12.VcsFacade;
import com.luntsys.luntbuild.utility.DisplayProperty;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.MyExecTask;
import com.luntsys.luntbuild.utility.Revisions;
import com.luntsys.luntbuild.utility.ValidationException;

/**
 * CVSNT VCS adaptor implementation.
 *
 * <p>This adaptor is safe for remote hosts.</p>
 *
 * @author joe enfield
 */
public class CvsNTAdaptor extends CvsAdaptor {
	/**
	 * Keep tracks of version of this class, used when do serialization-deserialization
	 */
	static final long serialVersionUID = 1;

	/**
	 * cvsNTAudit - constrain to "Yes" or "No"
	 * This field is used to determine if the
	 * audit database should be used.
	 */
	private String cvsNTAudit = "";
	/**
	 * auditURL The JDBC URL String to use for access to the CVSNT database
	 * Example (using MS SqlServer and oss jtds jdbc driver):
	 * jdbc:jtds:sqlserver://my.server.com:1433/cvsnthistory
	 */
	private String auditURL = "";
	/**
	 * auditPwd Plain String password associated with database user login name.
	 */
	private String auditPwd = "";
	/**
	 * auditusr Database user login name
	 */
	private String auditusr = "";
	/**
	 * auditdrv JDBC Driver Package and Class.
	 * Example (using oss jtds JDBC driver for MS SqlServer):
	 * net.sourceforge.jtds.jdbc.Driver
	 */
	private String auditdrv = "";

	/**
	 * Constructor, creates a CVS adaptor with default settings.
	 */
	public CvsNTAdaptor() {
		super();
	}

	public String getCvsNTAudit()
	{
		return cvsNTAudit;
	}

	public String getAuditURL()
	{
		return auditURL;
	}

	public String getAuditPwd()
	{
		return auditPwd;
	}

	public String getAuditusr()
	{
		return auditusr;
	}

	public String getAuditdrv()
	{
		return auditdrv;
	}

	public void setCvsNTAudit(String audit)
	{
		cvsNTAudit = audit;
	}

	public void setAuditURL(String url)
	{
		auditURL = url;
	}

	public void setAuditPwd(String pwd)
	{
		auditPwd = pwd;
	}

	public void setAuditusr(String usr)
	{
		auditusr = usr;
	}

	public void setAuditdrv(String drv)
	{
		auditdrv = drv;
	}

	public boolean isCvsNTAudit() {
		return !Luntbuild.isEmpty(getCvsNTAudit()) && getCvsNTAudit().equalsIgnoreCase("yes");
	}

	/**
	 * @inheritDoc
	 */
	public String getDisplayName() {
		return "CvsNT";
	}

	/**
	 * @inheritDoc
	 */
	public String getIconName() {
		return "cvs.jpg";
	}

	private static final transient int CVSROOT_PROTOCOL=0;
	private static final transient int CVSROOT_USERNAME=1;
	private static final transient int CVSROOT_USERPWD=2;
	private static final transient int CVSROOT_HOST=3;
	private static final transient int CVSROOT_PORT=4;
	private static final transient int CVSROOT_REPOSITORY=5;

	// :sspi:[[username][:password]@]023app44.guardian.com:[2401]/mytoproot/mysubroot
	/**
	 * parseCvsRoot
	 * method to parse a CVSRoot string into its respective components.
	 *
	 * @param cvsRoot String
	 * @return String[]
	 */
	public String[] parseCvsRoot(String cvsRoot)
	{
		if (cvsRoot == null || cvsRoot.length() < 1) {
			throw new IllegalArgumentException("empty or null cvsRoot parameter is illegal.");
		}
		String[] cvsRootParts = new String[6];
		Arrays.fill(cvsRootParts, "");

		int piece = cvsRoot.indexOf(':', 1);
		cvsRootParts[CVSROOT_PROTOCOL] = cvsRoot.substring(0, piece + 1);
		cvsRoot = cvsRoot.substring(piece + 1);

		piece = cvsRoot.indexOf('@');
		if (piece > 0) {
			int atPiece = piece;
			cvsRootParts[CVSROOT_USERNAME] = cvsRoot.substring(0, piece);
			cvsRoot = cvsRoot.substring(piece + 1);

			piece = cvsRootParts[CVSROOT_USERNAME].indexOf(':');
			if (piece > 0) {
				cvsRootParts[CVSROOT_USERPWD] = cvsRootParts[CVSROOT_USERNAME].substring(
						piece + 1,atPiece);
				cvsRootParts[CVSROOT_USERNAME] = cvsRootParts[CVSROOT_USERNAME].substring(0, piece);
			}
		}

		piece = cvsRoot.indexOf(':');
		cvsRootParts[CVSROOT_HOST] = cvsRoot.substring(0, piece);
		cvsRoot = cvsRoot.substring(piece + 1);

		piece = cvsRoot.indexOf('/');
		if (piece > 0) {
			cvsRootParts[CVSROOT_PORT] = cvsRoot.substring(0, piece);
		}
		cvsRootParts[CVSROOT_REPOSITORY] = cvsRoot.substring(piece);

		return cvsRootParts;
	}

	/**
	 * login
	 * Method used to specifically login to a CVS Module
	 *
	 * @param antProject Project
	 */
	public void login(Project antProject)
	{
		antProject.log("Login to cvs...", Project.MSG_INFO);
		if (getCvsRoot().indexOf("@") == -1 || Luntbuild.isEmpty(getCvsPassword())) {
			// this must mean no login is necessary
			return;
		}
		if (System.getProperty("os.name").startsWith("Windows")) {
			Commandline cmdLine = new Commandline();
			cmdLine.setExecutable("cmd.exe");
			Commandline.Argument arg = cmdLine.createArgument();
			arg.setValue("/c");

			arg = cmdLine.createArgument();
			arg.setValue("ECHO");

			arg = cmdLine.createArgument();
			arg.setValue(getCvsPassword() + "|");
			arg.setDescriptiveLine("***|");

			arg = cmdLine.createArgument();
			arg.setValue("cvs.exe");

			arg = cmdLine.createArgument();
			arg.setValue("-d");

			arg = cmdLine.createArgument();
			arg.setValue(getCvsRoot());

			arg = cmdLine.createArgument();
			arg.setValue("login");

			MyExecTask exec = null;
			try {
				exec = new MyExecTask("cvsnt_passwd", antProject, cmdLine, Project.MSG_INFO);
				exec.execute();
			} catch (BuildException ex) {
				antProject.log(exec, "ERROR: Failed to find password for CVSROOT \"" +
						getCvsRoot() + "\" in password file!", Project.MSG_ERR);
			}
		}
	}

	/**
	 * logout
	 * method used to specifically log out of CVS
	 *
	 * @param antProject Project
	 */
	public void logout(Project antProject)
	{
		antProject.log("logout of cvs...", Project.MSG_INFO);
		if (System.getProperty("os.name").startsWith("Windows")) {
			Commandline cmdLine = new Commandline();
			cmdLine.setExecutable("cvs.exe");
			Commandline.Argument arg = cmdLine.createArgument();
			arg = cmdLine.createArgument();
			arg.setValue("-d");

			arg = cmdLine.createArgument();
			arg.setValue(getCvsRoot());

			arg = cmdLine.createArgument();
			arg.setValue("logout");

			MyExecTask exec = null;
			try {
				exec = new MyExecTask("cvsnt_passwd", antProject, cmdLine, Project.MSG_INFO);
				exec.execute();
			} catch (BuildException ex) {
				// we should not go this far
				antProject.log(exec,"ERROR: Failed to find password for CVSROOT \"" +
						getCvsRoot() + "\" in password file!", Project.MSG_ERR);
			}
			return ;
		}
	}

	/**
	 * Validates the properties of this VCS.
	 *
	 * @throws ValidationException if a property has an invalid value
	 */
	public void validateProperties() {
		super.validateProperties();
		if (!Luntbuild.isEmpty(getCvsNTAudit())) {
			if (!getCvsNTAudit().equalsIgnoreCase("yes") && !getCvsNTAudit().equalsIgnoreCase("no"))
				throw new ValidationException("Only \"yes\" or \"no\" value can be specified " +
				"for \"is cvsnt Audit\" property!");
		}
	}

	/**
	 * Checks if the VCS has had any changes since the specified date.
	 * Overrides the default implementation in order to speed up quiet detection for the CVS adaptor.
	 *
	 * @inheritDoc
	 */
	public boolean isVcsQuietSince(Date sinceDate, Schedule workingSchedule, Project antProject) {
		if (isCvsNTAudit()) {
			Revisions revisions = getRevisionsSince(sinceDate, workingSchedule, antProject);
			return !revisions.isFileModified();
		} else {
			return super.isVcsQuietSince(sinceDate,workingSchedule,antProject);
		}
	}

	private static final String HEAD = "HEAD";
	/**
	 * generateRevisionSinceSql
	 * Creates the base SQLStatement necessary to retrieve
	 * revision information from the CvsNT Audit database
	 *
	 * @param sinceDate Date
	 * @return String
	 */
	public String generateRevisionSinceSql(Date sinceDate) {
		String[] rootParts = parseCvsRoot(getCvsRoot());
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		StringBuffer sb = new StringBuffer("SELECT SessionLog.startTime, SessionLog.username, ");
		sb.append("CommitLog.oldRev, CommitLog.newRev, CommitLog.fileName, ");
		sb.append("CommitLog.directory, CommitLog.message ");
		sb.append("FROM SessionLog JOIN ");
		sb.append("CommitLog ON CommitLog.sessionID = SessionLog.id ");
		sb.append("WHERE SessionLog.startTime > '"+sdf.format(sinceDate)+"' ");
		sb.append("AND SessionLog.virtRepos = '"+rootParts[CVSROOT_REPOSITORY]+"' ");

		Iterator it = getModules().iterator();

		boolean isFirst = true;
		while (it.hasNext()) {
			CvsModule module = (CvsModule)it.next();

			// Don't check modules with Labels - no new commmit is available.
			if (module.getLabel() != null && module.getLabel().length() > 0) {
				continue;
			}
			if (isFirst) {
				sb.append("AND ( ");
				isFirst = false;
			} else {
				sb.append("OR ");
			}
			String branch = module.getBranch() == null ? null : module.getBranch().toUpperCase();
			String srcModule = module.getSrcPath();
			if (srcModule.startsWith("/") || srcModule.startsWith("\\")) {
				srcModule = srcModule.substring(1);
			}
			boolean isHead = HEAD.equals(branch);
			sb.append("(CommitLog.directory LIKE ");
			sb.append("'" + srcModule + "%' ");
			if (branch != null && branch.length() > 0) {
				if (isHead) {
					sb.append("AND ( ");
				} else {
					sb.append("AND ");
				}
				sb.append("CommitLog.Tag = '"+branch+"' ");
				if(isHead){
					sb.append("OR CommitLog.Tag = '' ) ");
				}
			}
			sb.append(") ");
		}
		if (!isFirst) {
			sb.append(" ) ");
		} else {
			// if all source directories are labeled, then none will be included, so get nothing. */
			sb.append("AND 1=0 ");
		}
		return sb.toString();
	}

	/**
	 * @inheritDoc
	 */
	public Revisions getRevisionsSince(Date sinceDate, Schedule workingSchedule,
			Project antProject)
	{
		if (isCvsNTAudit()) {
			return getRevisionsSinceAudit(sinceDate, workingSchedule, antProject);
		} else {
			return super.getRevisionsSince(sinceDate, workingSchedule, antProject);
		}
	}

	/**
	 * getRevisionsSinceAudit
	 * Connects to a CvsNT Audit database
	 * and retrieves all revisions since the
	 * 'sinceDate' parameter and restricted to
	 * the vcs module srcPath and Branch/Tag information.
	 *
	 * Currently Tested with MS SqlServer ONLY.
	 *
	 * @param sinceDate Date
	 * @param workingSchedule Schedule
	 * @param antProject Project
	 * @return Revisions
	 */
	public Revisions getRevisionsSinceAudit(Date sinceDate, Schedule workingSchedule,
			Project antProject)
	{
		String sqlStatement = generateRevisionSinceSql(sinceDate);
		SimpleDateFormat sdf = new SimpleDateFormat(LOG_DATE_FORMAT);

		final Revisions revisions = new Revisions();
		try {
			Class.forName(getAuditdrv());
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}

		String url = getAuditURL();
		Connection con = null;
		Statement statement = null;
		ResultSet results = null;
		try {
			con = DriverManager.getConnection(url, getAuditusr(), getAuditPwd());
			statement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			results = statement.executeQuery(sqlStatement);
			boolean isFirst = true;
			while (results.next()) {
				if (isFirst) {
					revisions.setFileModified(true);
					isFirst = false;
				}
				revisions.getChangeLogins().add(results.getString("username"));
				revisions.getChangeLogs().add("Date: "+sdf.format(results.getTimestamp("startTime")) + " " +
						"user: "+results.getString("username") + " " +
						"old: "+results.getString("oldRev") + " " +
						"new: "+results.getString("newRev") + "\n" +
						"file: "+results.getString("directory") + "/" +
						results.getString("fileName") + "\n" +
						"message: "+results.getString("message"));

			}

		} catch (SQLException ex1) {
			throw new RuntimeException(ex1);
		} finally {
			SQLException sqle = null;
			resultsClose(results, sqle);
			statementClose(statement, sqle);
			conClose(con, sqle);
			if (sqle != null) {
				throw new RuntimeException(sqle);
			}
		}

		return revisions;
	}

	/**
	 * resultsClose
	 * Capture SQLException and Continue.
	 * Will be thrown after all Connections are closed.
	 *
	 * @param rs ResultSet
	 * @param sqle SQLException
	 */
	private void resultsClose(ResultSet rs,SQLException sqle) {
		if (rs == null) {
			return;
		}
		try {
			rs.close();
		} catch (SQLException ex) {
			exceptionOccured(ex, sqle);
		}
	}

	/**
	 * statementClose
	 * Capture SQLException and Continue.
	 * Will be thrown after all Connections are closed.
	 *
	 * @param rs Statement
	 * @param sqle SQLException
	 */
	private void statementClose(Statement rs, SQLException sqle) {
		if (rs == null) {
			return;
		}
		try {
			rs.close();
		} catch (SQLException ex) {
			exceptionOccured(ex, sqle);
		}
	}

	/**
	 * conClose
	 * Capture SQLException and Continue.
	 * Will be thrown after all Connections are closed.
	 *
	 * @param rs Connection
	 * @param sqle SQLException
	 */
	private void conClose(Connection rs, SQLException sqle) {
		if (rs == null) {
			return;
		}
		try {
			rs.close();
		} catch (SQLException ex) {
			exceptionOccured(ex, sqle);
		}
	}

	/**
	 * exceptionOccured
	 * Bundle all 'Close' related SQLExceptions so they can be thrown as one.
	 *
	 * @param ex SQLException
	 * @param sqle SQLException
	 */
	private void exceptionOccured(SQLException ex, SQLException sqle) {
		if (sqle == null) {
			sqle = ex;
		} else {
			SQLException nexte = sqle;
			while (nexte.getNextException() != null) {
				nexte = nexte.getNextException();
			}
			nexte.setNextException(ex);
		}
	}

	/**
	 * @inheritDoc
	 */
	public List getVcsSpecificProperties() {
		List properties = super.getVcsSpecificProperties();


		DisplayProperty p = null;
		IPropertySelectionModel model = null;

		p = new DisplayProperty() {
			public String getDisplayName() {
				return "Use CvsNT Audit database?";
			}

			public String getDescription() {
				return "This property indicates whether or not the CVSNT Audit Database should be used instead " +
				"of log / history. This can speed up modification detection, however " +
				"some earlier versions of Cvs do not support this option. In this case you should disable it. ";
			}

			public boolean isRequired() {
				return true;
			}

			public boolean isSelect() {
				return true;
			}

			public String getValue() {
				return getCvsNTAudit();
			}

			public void setValue(String value) {
				setCvsNTAudit(value);
			}
		};
		// Create selection model
		model = new CvsYesNoSelectionModel();
		// Set selection model
		p.setSelectionModel(model);
		// Add property to properties list
		properties.add(p);

		p = new DisplayProperty() {
			public String getDisplayName() {
				return "JDBC Driver";
			}

			public String getDescription() {
				return "Enter the driver class";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getAuditdrv();
			}

			public void setValue(String value) {
				setAuditdrv(value);
			}
		};
		// Add property to properties list
		properties.add(p);

		p = new DisplayProperty() {
			public String getDisplayName() {
				return "Enter the Database URL";
			}

			public String getDescription() {
				return "This property is the database connection string. ";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getAuditURL();
			}

			public void setValue(String value) {
				setAuditURL(value);
			}
		};
		// Add property to properties list
		properties.add(p);

		p = new DisplayProperty() {
			public String getDisplayName() {
				return "Enter Database User";
			}

			public String getDescription() {
				return "User Name for database access. ";
			}

			public boolean isRequired() {
				return false;
			}

			public String getValue() {
				return getAuditusr();
			}

			public void setValue(String value) {
				setAuditusr(value);
			}
		};
		// Add property to properties list
		properties.add(p);

		p = new DisplayProperty() {
			public String getDisplayName() {
				return "Enter Database User Password";
			}

			public String getDescription() {
				return "Password for database access. ";
			}

			public boolean isRequired() {
				return false;
			}

			public boolean isSecret(){
				return true;
			}

			public String getValue() {
				return getAuditPwd();
			}

			public void setValue(String value) {
				setAuditPwd(value);
			}
		};
		// Add property to properties list
		properties.add(p);

		return properties;
	}

	/**
	 * @inheritDoc
	 * @see CvsAdaptorFacade
	 */
	public void saveToFacade(VcsFacade facade) {
		// TODO throw RuntimeException if the facade is not the right class
		super.saveToFacade(facade);
		CvsNTAdaptorFacade cvsFacade = (CvsNTAdaptorFacade) facade;
		cvsFacade.setAuditdrv(getAuditdrv());
		cvsFacade.setAuditPwd(getAuditPwd());
		cvsFacade.setAuditURL(getAuditURL());
		cvsFacade.setAuditusr(getAuditusr());
		cvsFacade.setCvsNTAudit(getCvsNTAudit().equalsIgnoreCase("yes") ||
				getCvsNTAudit().equalsIgnoreCase("true"));
	}

	/**
	 * @inheritDoc
	 * @throws RuntimeException if the facade is not an <code>CvsAdaptorFacade</code>
	 * @see CvsAdaptorFacade
	 */
	public void loadFromFacade(VcsFacade facade) {
		super.loadFromFacade(facade);
		CvsNTAdaptorFacade cvsFacade = (CvsNTAdaptorFacade) facade;
		setAuditdrv(cvsFacade.getAuditdrv());
		setAuditPwd(cvsFacade.getAuditPwd());
		setAuditURL(cvsFacade.getAuditURL());
		setAuditusr(cvsFacade.getAuditusr());
		setCvsNTAudit(cvsFacade.isCvsNTAudit() ? "yes" : "no");
	}

	/**
	 * @inheritDoc
	 * @see CvsAdaptorFacade
	 */
	public VcsFacade constructFacade()
	{
		return new CvsNTAdaptorFacade();
	}

	/**
	 * loginRequired
	 * Method to determine if a cvs login is required.
	 *
	 * @return boolean
	 */
	protected boolean loginRequired()
	{
		/** @todo
		 * need to enhance loginRequired so that it can determine
		 * specifically when a login is required.
		 * Perhaps an ognl expression supplied by the user?
		 *
		 * For Example
		 * the CVSROOT :sspi:my.server.com:/module
		 * suggests that a login is not necessary.
		 *
		 * the CVSROOT :sspi:myname@my.server.com:/module
		 * suggests that a login is necessary.
		 *  */
		return true;
	}
}
