/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-20
 * Time: 7:01:02
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

import com.caucho.hessian.client.HessianProxyFactory;
import com.luntsys.luntbuild.facades.lb12.CvsAdaptorFacade;

import java.net.MalformedURLException;

/**
 * This class gives an example of using luntbuild web service API to edit properties
 * of a project
 *
 * @author robin shine
 */
public class EditProperties {
	public static void main(String[] args) {
		String url = "http://localhost:8080/luntbuild/app.do?service=hessian";
		com.luntsys.luntbuild.facades.ILuntbuild luntbuild;
		HessianProxyFactory factory = new HessianProxyFactory();
        factory.setOverloadEnabled(true);
		try {
			/**
			 * The following code demonstrates how to create a new vcs object and assign it
			 * to the specified project
			 */

			/**
			 * Change user and password here to login to Luntbuild system
			 */
			factory.setUser("luntbuild");
			factory.setPassword("luntbuild");
			luntbuild = (com.luntsys.luntbuild.facades.ILuntbuild) factory.create(com.luntsys.luntbuild.facades.ILuntbuild.class, url);

			com.luntsys.luntbuild.facades.lb12.ProjectFacade project = luntbuild.getProjectByName("testcvs");
			if (project == null) {
				System.err.println("Can not find project \"testcvs\"!");
				System.exit(1);
			}

			// create a svn adaptor
			com.luntsys.luntbuild.facades.lb12.CvsAdaptorFacade cvsAdaptor = new com.luntsys.luntbuild.facades.lb12.CvsAdaptorFacade();
			cvsAdaptor.setCvsRoot(":pserver:build@localhost:/cvs_repository");
			cvsAdaptor.setCvsPassword("build");
			// create cvs modules
			com.luntsys.luntbuild.facades.lb12.CvsModuleFacade module = new com.luntsys.luntbuild.facades.lb12.CvsModuleFacade();
			module.setSrcPath("componentA");
			cvsAdaptor.getModules().add(module);

			project.getVcsList().clear();
			project.getVcsList().add(cvsAdaptor);
			luntbuild.saveProject(project);

			/**
			 * The following section demonstrates update VCS information property of a project
			 */
			project = luntbuild.getProjectByName("testcvs");
			if (project == null) {
				System.err.println("Can not find project \"testcvs\"!");
				System.exit(1);
			}
			com.luntsys.luntbuild.facades.lb12.CvsAdaptorFacade cvs = (CvsAdaptorFacade) project.getVcsList().get(0);
			cvs.setCvsPassword("build");
			luntbuild.saveProject(project);

			/**
			 * The following codes demonstrate update of a luntbuild system property
			 */
			String publishDir = luntbuild.getSystemProperty(com.luntsys.luntbuild.facades.Constants.PUBLISH_DIR);
			if (publishDir != null)
				System.out.println("Current publish directory: " + publishDir);
			publishDir = ""; // use default publish directory
			luntbuild.setSystemProperty(com.luntsys.luntbuild.facades.Constants.PUBLISH_DIR, publishDir);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
