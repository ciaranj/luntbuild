/*
 * Copyright TRX Inc(c) 2006,
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.types.selectors.FilenameSelector;

import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.Revisions;
import com.mks.api.CmdRunner;
import com.mks.api.Command;
import com.mks.api.IntegrationPoint;
import com.mks.api.IntegrationPointFactory;
import com.mks.api.Option;
import com.mks.api.Session;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.mks.api.si.SIModelTypeName;
import com.mks.api.util.ResponseUtil;

/**
 * A collection of MKS services, requried by the MksAdaptor.
 * 
 * @author Stefan Baramov (TRX Inc.)
 */
public class MksServiceProvider {

	/**
	 * Internal logger.
	 */
	private static final Log logger = LogFactory.getLog(MksServiceProvider.class);

	/**
	 * Default password used to initiliaze the command runner.
	 */
	private final String defaultPassword;

	/**
	 * Default username used to initialize the command runner. All commands will be run on the behalf
	 * of this user.
	 */
	private final String defaultUsername;
	
	/**
	 * Default hostname of the MKS repo server used to initialize the command runner.
	 */
	private final String defaultHostname;

	/**
	 * Default port of the MKS repo server used to initialize the command runner.
	 */
	private final int defaultPort;
	
	/**
	 * The command runner.
	 */
	private CmdRunner runner;

	/**
	 * Current MKS session object.
	 */
	private Session session;

	/**
	 * Initialize a new object with its default settings and constructs a session with the MKS client.
	 * 
	 * @param defaultHostname
	 *        default hostname.
	 * @param defaultPort
	 *        default port number.
	 * @param defaultUsername
	 *        default username.
	 * @param defaultPassword
	 *        default password.
	 * 
	 * @throws BuildException
	 *         in case it fails to construct a session or a command runner.
	 */
	public MksServiceProvider(String defaultHostname, int defaultPort, String defaultUsername, String defaultPassword) {

		super();

		this.defaultUsername = defaultUsername;
		this.defaultPassword = defaultPassword;
		this.defaultHostname = "";
		this.defaultPort = 0;

		initClientSession();
		initCommandRunner();
	}

	/**
	 * Updates the given revisions object with all revisions of after the given date for the given
	 * project.
	 * 
	 * @param revisions
	 *        the revisions data holder to be updated.
	 * @param sinceDate
	 *        the since date.
	 * @param project
	 *        the MKS project.
	 * @param devPath
	 *        the development path of the given project. Optional.
	 */
	public void getRevisionsSince(Revisions revisions, Date sinceDate, String project, String devPath) {

		// view project - listing all members and subproject.
		Command vproject = new Command(Command.SI, "viewproject");
		vproject.addOption(new Option("project", project));
		vproject.addOption(new Option("recurse"));
		addDevelopmentPath(vproject, devPath);

		Response response = null;
		try {
			response = runner.execute(vproject);
		}
		catch (APIException ex) {
			logger.error(
					"Failed to retrieve project revisions. Cannot view project: " + exceptionString(ex), ex);

			throw new BuildException(getExceptionMessage(ex), ex);
		}

		try {
			// go through all work items and check the one with type: memeber
			WorkItemIterator projectItemIter = response.getWorkItems();
			while (projectItemIter.hasNext()) {
				WorkItem projectItem = projectItemIter.next();
				
				if (projectItem.getModelType().equals(SIModelTypeName.MEMBER)) {
					// found a member lets go and view the memeber history.
					String member = projectItem.getId();
					String parentProject = projectItem.getField("parent").getValueAsString();
					
					Command vhistory = new Command(Command.SI, "viewhistory");
					vhistory.addOption(new Option("project", parentProject));
					vhistory.addSelection(member);
					addDevelopmentPath(vhistory, devPath);
					
					Response memberRevHistory = runner.execute(vhistory);
					
					Field revisionData = memberRevHistory.getWorkItem(member).getField("revisions");
					// each item in the feild is a individual revision
					for (Iterator iterator = revisionData.getList().iterator(); iterator.hasNext();) {
						
						Item revisionItem = (Item) iterator.next();
						Date revisionDate = (Date) revisionItem.getField("date").getValue();
						
						if (sinceDate.before(revisionDate)) {
							// newer revisions - record info
							addRevision(revisions, member, revisionItem);
						}
					}
				}
				// not iterested in other items then memebers
			}
			// project revision history complete
		}
		catch (APIException ex) {
			logger.error(
					"Failed to retrieve project revisions. Cannot view member history. Error:"
					+ exceptionString(ex), ex);
			
			throw new BuildException(getExceptionMessage(ex), ex);
		}

		revisions.setFileModified(revisions.getChangeLogins().size() != 0);
	}

	/**
	 * Returns the corresponding project revision to the given checkpoint description.
	 * 
	 * @param project
	 *        the project full name.
	 * @param checkpointDescription
	 *        the checkpoint description.
	 * @param devPath
	 *        the development of the given project. Optional.
	 * @return the revision or null if none is found
	 */
	public String getProjectRevision(String project, String checkpointDescription, String devPath) {

		try {
			/*
			 * si viewprojecthistory [--fields=field1[:width1],field2[:width2]...] [--height=value]
			 * [--width=value] [-x value] [-y value] [(-R|--[no|confirm]recurse)] [(-P
			 * project|--project=project)] [(-S sandbox|--sandbox=sandbox)] [--devpath=path]
			 * [--projectRevision=rev] [--hostname=server] [--port=number] [--password=password]
			 * [--user=name] [(-?|--usage)] [(-F file|--selectionFile=file)] [(-N|--no)] [(-Y|--yes)]
			 * [--[no]batch] [--cwd =directory] [--forceConfirm=[yes|no]] [(-g|--gui)] [--[no]persist]
			 * [--quiet] [--settingsUI=[gui|default]] [--status=[none|gui|default]] [--xmlapi]
			 */
			// view project - listing all members and subproject.
			Command vproject = new Command(Command.SI, "viewprojecthistory");
			vproject.addOption(new Option("project", project));
			addDevelopmentPath(vproject, devPath);
			Response response = runner.execute(vproject);

			// the response has only one work item. So get the first and read the revisions field
			Field revisions = response.getWorkItems().next().getField("revisions");
			for (Iterator iter = revisions.getList().iterator(); iter.hasNext();) {
				Item revItem = (Item) iter.next();

				String revDesc = revItem.getField("description").getValueAsString();
				if (checkpointDescription.equalsIgnoreCase(revDesc)) {
					// we found our revision
					return revItem.getField("revision").getValueAsString();
				}
			}

			// could not find an appropriate revision
			return null;

		}
		catch (APIException ex) {
			logger.error("Failed to retrieve project revision for checkpoint " + checkpointDescription
					+ ". Error: "
					 + exceptionString(ex), ex);

			throw new BuildException(getExceptionMessage(ex), ex);
		}
	}

	/**
	 * Returns true if the sandbox already exist.
	 * 
	 * @param sandbox
	 *        the sandbox name.
	 *        
	 * @return <code>true</code> if the sandbox exists.
	 * 
	 * @throws BuildException
	 *         if it fails to retrieve the list of sandboxes.
	 */
	public boolean isSandboxExist(String sandbox) {

		try {
			final boolean windowsOs = isWindowsOs();
			final Response sboxes = runner.execute(new Command(Command.SI, "sandboxes"));
			
			// log the list of checkboxes 
			try {
				logger.debug("[isSandboxExist]The following sandboxes were found on the build machine:"+ responseString(sboxes));
			}
			catch (Throwable ex) {
				// ignore me.
				logger.error("Failed to log the list of checkboxes");
			}

			// look for the given checkbox.
			WorkItemIterator iter = sboxes.getWorkItems();
			while (iter.hasNext()) {
				final String existingSandbox = iter.next().getId();
				if (windowsOs) {
					if (sandbox.equalsIgnoreCase(existingSandbox)) {
						return true;
					}
				}
				else {
					if (sandbox.equals(existingSandbox)) {
						return true;
					}
				}
			}
			
			// at this point the sandbox was not found to be registered with the SI client.
			// clean up the file if it exists
			cleanupSandbox(sandbox);

			return false;

		}
		catch (APIException ex) {
			logger.error("Failed to retrieve the list of  sandboxes: " + exceptionString(ex), ex);

			throw new BuildException(getExceptionMessage(ex), ex);
		}
	}

	/**
	 * Deletes the content of a old sandbox. The content might be of an unregistered sandbox.
	 * 
	 * @param sandbox
	 *        the sandbox to be cleaned up.
	 */
	private void cleanupSandbox(String sandbox) {

		File sandboxFile = new File(sandbox);
		if (sandboxFile.exists()) {
			
			logger.info("Cleaning up a phantom sandbox : " + sandbox);
			
			try {
				
				deleteFileMask(sandboxFile.getParentFile(), "**/*.*");
				
			}
			catch (Throwable ex) {
				logger.warn("Failed to clean up sandbox " + sandbox, ex);
			}
		}
	}
	
	/**
	 * Returns true if the given directory is a part of a sandbox.
	 * 
	 * @param dir
	 *        the directory to check.
	 *        
	 * @return <code>true</code> if the directory is contains a valid sandbox.
	 * 
	 * @throws BuildException
	 *         if it fails to retrieve the list of sandboxes.
	 */
	public boolean isPartOfSandbox(String dir) {

		try {
			final boolean windowsOs = isWindowsOs();
			final Response sboxes = runner.execute(new Command(Command.SI, "sandboxes"));
			
			// log the list of checkboxes 
			try {
				logger.debug("[isPartOfSandbox]The following sandboxes were found on the build machine:"+ responseString(sboxes));
			}
			catch (Throwable ex) {
				// ignore me.
				logger.error("Failed to log the list of checkboxes");
			}

			WorkItemIterator iter = sboxes.getWorkItems();
			while (iter.hasNext()) {
				String existingSandbox = iter.next().getId();
				
				if (windowsOs) {
					if (existingSandbox.toLowerCase().startsWith(dir.toLowerCase())) {
						return true;
					}
				}
				else {
					if (existingSandbox.startsWith(dir)) {
						return true;
					}
				}
			}

			return false;

		}
		catch (APIException ex) {
			logger.error("Failed to retrieve the list of  sandboxes: "  + exceptionString(ex), ex);

			throw new BuildException(getExceptionMessage(ex), ex);
		}
	}

	/**
	 * Creates a sand box for the given project in the given directory.
	 * 
	 * @param project
	 *        the MKS project.
	 * @param targetDir
	 *        the sandbox target directory.
	 * @param revision
	 *        the project revision to be used. If it is null, then the top of the trunk is retrieved.
	 * @param devPath
	 *        the development path of the given project. Optional parameter.
	 *        
	 * @return the newly created sandbox name.
	 * 
	 * @throws BuildException
	 *         if it fails to create the sandbox.
	 */
	public String createSandbox(String project, String targetDir, String revision, String devPath) {
		
		/*
		 * si createsandbox [(-R|--[no|confirm]recurse)] [--lineTerminator=[lf|crlf|native]]
		 * [--[no]populate] [--[no]sparse] [--[no]openView] [--[no]shared]
		 * [(-Pproject|--project=project)] [--devpath=path] [--projectRevision=rev] [--hostname=server]
		 * [--port=number] [--password=password] [--user=name] [(-?|--usage)] [(-F
		 * file|--selectionFile=file)] [(-N|--no)] [(-Y|--yes)] [--[no]batch] [--cwd=directory]
		 * [--forceConfirm=[yes|no]] [(-g|--gui)] [--quiet] [--settingsUI=[gui|default]]
		 * [--status=[none|gui|default]] [--xmlapi] directory
		 */
		
		// --- log info ---
		logger.info(MessageFormat.format(
				"Creating a sandbox for project {0} into {1} directory, project revision is {2}.",
				new Object[]{project, targetDir, revision}));
		
		// CREATE SANDBOX
		
		Command cmd = new Command(Command.SI, "createsandbox");
		cmd.addOption(new Option("project", project));
		cmd.addOption(new Option("recurse"));
		cmd.addOption(new Option("populate"));
		cmd.addOption(new Option("forceConfirm", "yes"));
		addDevelopmentPath(cmd, devPath);
		addProjectRevision(cmd, revision);
		cmd.addSelection(targetDir);

		try {
			
			Response response = runner.execute(cmd);
			
			// return the sandbox name.
			return response.getResult().getField("resultant").getItem().getId();
		}
		catch (APIException ex) {
			
			logger.warn("Failed to create a sandbox for project: " + project
					+ ". Error:\n" + exceptionString(ex));
			
			throw new BuildException(getExceptionMessage(ex), ex);
		}
	}

	/**
	 * Drops a particular sandbox.
	 * 
	 * @param sandbox
	 *        the sandbox to be dropped.
	 * @throws BuildException
	 *         if it fails to drop the sand box.
	 */
	public void dropSandbox(String sandbox) {

		// [Apr 21, 2006] drop sandbox is uncertified API command. we just hope it will work. 
		
		try {

			doDropSandbox(sandbox, "all");

		}
		catch (APIException ex) {

			logger.error("Failed to drop sandbox: " + sandbox
					+ ". Error:\n"
					+ exceptionString(ex), ex);

			// exception name -- see if it is known exception. 
			String exceptionName = ex.getField("exception-name").getValueAsString();
			
			if ("common.CommandFailed".equals(exceptionName)) {
				// try to recover  if possible 

				// try to drop it without deleting the sandbox memebers
				try {

					doDropSandbox(sandbox, "none");

					cleanupSandbox(sandbox);
				}
				catch (APIException ex2) {
					logger.error("Failed to recover from drop sandbox operation: " + sandbox
							+ ". Error:\n"
							+ exceptionString(ex), ex);

					throw new BuildException(getExceptionMessage(ex), ex);
				}
			}
			else {

				throw new BuildException(getExceptionMessage(ex), ex);
			}
		}
	}
	
	
	private void doDropSandbox(String sandbox, String memeberOp) throws APIException {

		/*
		 * si dropsandbox [--[no]confirm] [--delete=[none|members|all]] [-f] [(-?|--usage)] [(-F
		 * file|--selectionFile=file)] [(-N|--no)] [(-Y|--yes)] [--[no]batch] [--cwd=directory]
		 * [--forceConfirm=[yes|no]] [(-g|--gui)] [--quiet] [--settingsUI=[gui|default]]
		 * [--status=[none|gui|default]] sandbox location...
		 */

		Command cmd = new Command(Command.SI, "dropsandbox");
		cmd.addOption(new Option("delete", memeberOp));
		cmd.addOption(new Option("forceConfirm", "yes"));
		cmd.addSelection(sandbox);
		

		runner.execute(cmd);
	}

	/**
	 * Resynchronize existing sandbox.
	 * 
	 * @param sandbox
	 *        the sandbox to resynchronize
	 * @param recurse
	 *        recurse in subprojects.
	 */
	public void resyncSandbox(String sandbox, boolean recurse) {

		/*
		 * si resync [--mergeType=[confirm|cancel|automatic|manual]]
		 * [--onMergeConflict=[confirm|cancel|mark|launchtool|highlight|error]] [--[no]byCP]
		 * [--[no]confirm] [--[no]confirmPopulateSparse] [--[no]includeDropped] [--[no|confirm]merge]
		 * [--[no|un]expand] [-f] [--[no|confirm]overwriteChanged] [--[no|confirm]overwriteIfPending]
		 * [--[no|confirm]overwriteDeferred] [--[no]overwriteUnchanged] [--[no]populate]
		 * [--[no]restoreTimestamp] [(-R|--[no|confirm]recurse)] [--filter=filteroptions] [(-S
		 * sandbox|--sandbox=sandbox)] [--hostname=server] [--port=number] [--password=password]
		 * [--user=name] [(-?|--usage)] [(-F file|--selectionFile=file)] [(-N|--no)] [(-Y|--yes)]
		 * [--[no]batch] [--cwd=directory] [--forceConfirm=[yes|no]] [(-g|--gui)] [--quiet]
		 * [--settingsUI=[gui|default]] [--status=[none|gui|default]] current or dropped
		 * member/subproject...
		 */

		Command cmd = new Command(Command.SI, "resync");
		cmd.addOption(new Option("sandbox", sandbox));
		cmd.addOption(new Option("populate"));
		cmd.addOption(new Option("forceConfirm", "yes"));
		if (recurse) {
			cmd.addOption(new Option("recurse"));
		}
		
		try {

			runner.execute(cmd);

		}
		catch (APIException ex) {
			logger.error("Failed to resynchronize sandbox: " + sandbox
					+ ". Error:\n"
					+ exceptionString(ex), ex);
			
			throw new BuildException(getExceptionMessage(ex), ex);
		}
	}

	/**
	 * Creates a checkpoint for the given project on the given development path. The development path
	 * is option. If omitted, then it is assumed it is the main trunck project.
	 * 
	 * <p>
	 * The method does not create a label for the particular checkpoint. According to MKS consultant,
	 * the label only "polute" the database and does not bring any additional information. Checkpoint
	 * description should be suffiant.
	 * </p>
	 * 
	 * @param project
	 *        the project full name. required.
	 * @param description
	 *        the checkpoint description. required.
	 * @param devPath
	 *        the development path. optional.
	 */
	public void checkpointProject(String project, String description, String devPath) {
		
		/*
		 * si checkpoint [--author=name] [(-d desc|--description=desc)] [--descriptionFile=file]
		 * [--[no]labelMembers] [--[no]notify] [(-L label| --label=label)] [(-s state|--state =state)]
		 * [--[no]stateMembers] [(-P project|--project=project)] [(-S sandbox|--sandbox=sandbox)]
		 * [--devpath=path] [--hostname=server] [--port=number] [--password=password] [--user=name]
		 * [(-?|--usage)] [(-N|--no)] [(-Y|--yes)] [--[no]batch] [--cwd=directory]
		 * [--forceConfirm=[yes|no]] [(-g|--gui)] [--quiet] [--settingsUI=[gui|default]]
		 * [--status=[none|gui|default]]
		 */
		
		Command cmd = new Command(Command.SI, "checkpoint");
		cmd.addOption(new Option("project", project));
		cmd.addOption(new Option("description", description));
		cmd.addOption(new Option("forceConfirm", "yes"));
		addDevelopmentPath(cmd, devPath);
		
		try {
			
			runner.execute(cmd);
			
		}
		catch (APIException ex) {
			logger.error("Failed to checkpoint project: " + project + ". Error:\n" + exceptionString(ex),
					ex);
			
			throw new BuildException(getExceptionMessage(ex), ex);
		}
		
	}

	/**
	 * The method should always be used before the service provider is disposed. Otherwise memory and
	 * resource leaking will occure.
	 */
	public void release() {

		// Release the MKS client runner.
		if (runner != null) {
			// release and terminate any command possibly running
			try {
				runner.release();
			}
			catch (APIException ex) {
				logger.warn("Failed to clearly release the runner.", ex);
			}
			catch (Throwable ex) {
				logger.warn("Failed to clearly release the runner because of unexpected error.", ex);
			}
			finally {
				runner = null;
			}
		}

		// Release the MKS client session.
		if (session != null) {
			// release and terminate any command possibly running
			try {
				session.release(true);
			}
			catch (IOException ex) {
				logger.warn("Failed to clearly release the session to the MKS Client because of IO error.",
						ex);
			}
			catch (APIException ex) {
				logger.warn("Failed to clearly release the session to the MKS Client.", ex);
			}
			catch (Throwable ex) {
				logger.warn(
						"Failed to clearly release the session to the MKS Client because of unexpected error.",
						ex);
			}
			finally {
				session = null;
			}
		}
	}

	/**
	 * Adds a revision information.
	 * 
	 * @param revisions
	 *        the revisions data.
	 * @param member
	 *        the member name.
	 * @param revisionItem
	 *        the revision time.
	 */
	private void addRevision(Revisions revisions, String member, Item revisionItem) {

		// CHANGE LOG
		StringBuffer changeLog = new StringBuffer();

		// member name
		changeLog.append(member);

		// revision
		changeLog.append(" - ");
		changeLog.append(revisionItem.getId());

		// description
		if (! Luntbuild.isEmpty(revisionItem.getField("description").getValueAsString())) {
			changeLog.append(" - ");
			changeLog.append(revisionItem.getField("description").getValueAsString());
		}

		// author
		changeLog.append(" [");
		changeLog.append(revisionItem
				.getField("author")
				.getItem()
				.getField("fullname")
				.getValueAsString());
		changeLog.append("]");

		revisions.getChangeLogs().add(changeLog.toString());

		// CHANGE LOGIN
		revisions.getChangeLogins().add(revisionItem.getField("author").getItem().getId());

	}

	/**
	 * Adds a development path option to the given command.
	 * 
	 * @param cmd
	 *        the command to be modified.
	 * @param devPath
	 *        the development path.
	 */
	private void addDevelopmentPath(Command cmd, String devPath) {

		if ( ! Luntbuild.isEmpty(devPath)) {
			cmd.addOption(new Option("devpath", devPath));
		}
	}

	/**
	 * Adds a project revision option to the given command.
	 * 
	 * @param cmd
	 *        the command to be modified.
	 * @param revision
	 *        the revision text.
	 */
	private void addProjectRevision(Command cmd, String revision) {

		if (!Luntbuild.isEmpty(revision)) {
			cmd.addOption(new Option("projectRevision", revision));
		}
	}

	/**
	 * Returns the exception message or exception ID if the message is missing.
	 * 
	 * @param ex
	 *        the MKS exception.
	 * @return the error message.
	 */
	private String getExceptionMessage(APIException ex) {

		String msg = ex.getMessage();

		if (Luntbuild.isEmpty(msg)) {
			// don't have a message - produce one
			msg = exceptionString(ex);
			if (ex.getResponse() != null) {
				msg = msg + "\n" + responseString(ex.getResponse());
			}
		}

		return msg;
	}

	/**
	 * Returns a session with the MKS client app. It will automatically start the client if it is not
	 * available.
	 * 
	 * @return the session object.
	 */
	private Session initClientSession() {

		try {
			/*
			 * Create local client integration point. In order to autostart the client the c:\program
			 * files\mks\sourceintegrity\bin should be in the system path. Need to test this to see how it
			 * will work in service environment. We can always fall back to -Djava.library.path option if
			 * we need to.
			 * 
			 * A server integration point will not requires a client but it will not be able to work with
			 * local paths.
			 */
			IntegrationPointFactory factory = IntegrationPointFactory.getInstance();
			IntegrationPoint p = factory.createLocalIntegrationPoint();
			p.setAutoStartIntegrityClient(true);

			session = p.getCommonSession();
			session.setAutoReconnect(true);

			return session;
		}
		catch (APIException ex) {
			String err = getExceptionMessage(ex);

			logger.error("Failed to connect to Source Integrity Client.", ex);

			throw new BuildException(err, ex);
		}
	}

	/**
	 * Returns a runner based on the current session.
	 * 
	 * @return a new command runner.
	 */
	private CmdRunner initCommandRunner() {

		try {
			runner = session.createCmdRunner();
			
			if (!Luntbuild.isEmpty(defaultUsername)) {
				runner.setDefaultUsername(defaultUsername);
			}
			if (!Luntbuild.isEmpty(defaultPassword)) {
				runner.setDefaultPassword(defaultPassword);
			}
			if (!Luntbuild.isEmpty(defaultHostname)) {
				runner.setDefaultHostname(defaultHostname);
			}
			if (defaultPort > 0) {
				runner.setDefaultPort(defaultPort);
			}

			return runner;
		}
		catch (APIException ex) {
			String err = getExceptionMessage(ex);

			logger.error("Failed to construct command runner", ex);

			throw new BuildException(err, ex);
		}
	}

	private boolean isWindowsOs() {

		// os.name

		try {
			String osName = System.getProperty("os.name");
			return osName.toLowerCase().indexOf("windows") != -1;
		}
		catch (Throwable ex) {
			return false;
		}
	}
	
	/**
	 * Deletes a file masked with ANT wildcard mask from the given base directory. This method should
	 * be in the Luntbuild class.
	 * 
	 * @param baseDir
	 *        the base directory
	 * @param mask
	 *        the file mask. e.g. ** / *.pj
	 */
	private void deleteFileMask(File baseDir, String mask) {
		
		Delete deleteTask = new Delete();
		deleteTask.setProject(new Project());
		deleteTask.getProject().init();
		deleteTask.setDir(baseDir);
		
		FilenameSelector selector = new FilenameSelector();
		selector.setName(mask);
		deleteTask.add(selector);
		
		deleteTask.execute();
	}
	

	/**
	 * Debug method, useful to analyse the MKS resposne object.
	 * 
	 * @param response
	 *        the response to analayse
	 * @return the string version.
	 */
	protected String responseString(Response response) {

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		ResponseUtil.printResponse(response, 0, new PrintStream(output));

		try {
			output.flush();
			return output.toString();
		}
		catch (IOException ex) {
			return "";
		}

	}

	/**
	 * Debug method used to analyse the exception.
	 * 
	 * @param ex
	 *        the exception.
	 * @return the complete string representation.
	 */
	protected String exceptionString(APIException ex) {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream print = new PrintStream(output);

		ResponseUtil.printAPIException(ex, 0, print);
		
		print.println("\nStack Trace:");
		ex.printStackTrace(print);

		try {
			print.flush();
			output.flush();
			return output.toString();
		}
		catch (IOException io) {
			return "";
		}
		
	}
}

