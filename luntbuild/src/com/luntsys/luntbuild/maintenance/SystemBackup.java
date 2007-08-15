/*
 * Copyright luntsys (c) 2007,
 * Date: 2007-4-26
 * Time: 22:40:17
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

package com.luntsys.luntbuild.maintenance;

import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.facades.lb12.DataCollection;
import com.luntsys.luntbuild.security.SecurityHelper;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.StatefulJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * System backup class to backup information or resources in Luntbuild.
 * @author Jason Archer
 */
public class SystemBackup implements StatefulJob {
	private static Log logger = LogFactory.getLog(SystemBackup.class);

	/** Job name for system backup. */
	public static final String JOB_NAME = "system backup";
	/** Job group for system backup. */
	public static final String JOB_GROUP = "system backup";
	/** Trigger name for system backup. */
	public static final String TRIGGER_NAME = "system backup";
	/** Trigger group for system backup. */
	public static final String TRIGGER_GROUP = "system backup";

	/**
	 * Executes system backup proceedures.
	 * <p>The individual proceedures are:</p>
	 * <ul>
	 *    <li>Database - Exports the database to XML using the configured or default file name</li>
	 * </ul>
	 * 
	 * @param jobExecutionContext the job context
	 * @throws JobExecutionException if a backup fails
	 */
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		SecurityHelper.runAsSiteAdmin();
		backupDatabase();
	}

	/**
	 * 
	 * 
	 * @throws JobExecutionException if backup fails
	 */
	private void backupDatabase() throws JobExecutionException {
		String fileName = (String) Luntbuild.getProperties().get(Constants.BACKUP_FILE_NAME);
		if (Luntbuild.isEmpty(fileName)) {
			fileName = "database.xml";
		}

		DataCollection dataCollection = Luntbuild.getDao().loadDataCollection12();
		OutputStream os = null;
		PrintWriter pw = null;
		if (!(new File(fileName).isAbsolute())) {
			fileName = Luntbuild.installDir + File.separator + "backup" + File.separator + fileName;
			try {
				(new File(Luntbuild.installDir + File.separator + "backup")).mkdir();
			} catch (Exception e) { /* ignore */ }
		}
		try {
			os = new FileOutputStream(fileName);
			pw = new PrintWriter(os);
			XStream xstream = new XStream();
			xstream.alias("DataCollection", DataCollection.class);
			pw.print(xstream.toXML(dataCollection));
			pw.close();
			pw = null;
			os.close();
			os = null;
		} catch (IOException e) {
			throw new JobExecutionException("Failed to backup database", e, false);
		} finally {
			try {
				if (pw != null)
					pw.close();
				if (os != null)
					os.close();
			} catch (IOException e) {
				// ignore
			}
		}

		logger.info("Backing up database to " + fileName + ".");
	}
}
