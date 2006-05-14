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
import com.luntsys.luntbuild.remoting.*;

import java.net.MalformedURLException;

/**
 * This class gives an example of using luntbuild web service API to edit properties
 * of a view
 *
 * @author robin shine
 */
public class EditProperties {
	public static void main(String[] args) {
		String url = "http://localhost:8081/luntbuild/app?service=luntbuild";
		ILuntbuild luntbuild;
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			/**
			 * The following codes demonstrate create a new vcs object and assigned
			 * to specified project and view
			 */

			// first get the luntbuild web service stub
			luntbuild = (ILuntbuild) factory.create(ILuntbuild.class, url);

			ProjectFacade project = luntbuild.getProjectByName("testproject");
			if (project == null) {
				System.err.println("Can not find project \"testproject\"!");
				System.exit(1);
			}
			ViewFacade view = luntbuild.getViewByName(project, "development");
			if (view == null) {
				System.err.println("Can not find view \"development\" for project \"testproject\"!");
				System.exit(1);
			}

			// create a svn adaptor
			SvnAdaptorFacade svn = new SvnAdaptorFacade();
			svn.setUrlBase("svn://localhost");
			// create svn modules
			svn.getModules().clear();
			SvnModuleFacade module = new SvnModuleFacade();
			module.setSrcPath("testsvn");
			svn.getModules().add(module);

			/**
			 * Save created svn object into both project, and view. This is necessary
			 * because properties of a vcs object is divided into two parts, project level
			 * and view level. A luntbuild project responsible for data storage for vcs
			 * project level properties, while a luntbuild view responsible for data storage
			 * for vcs view level properties. In this case, urlBase, branches, tags, user
			 * and password are project level properties. Modules are view level properties.
			 */
			project.setVcs(svn);
			luntbuild.saveProject(project);
			view.setVcs(svn);
			luntbuild.saveView(view);

			/**
			 * The following section demonstrates update cvs property of a project
			 */
			project = luntbuild.getProjectByName("testcvs");
			if (project == null) {
				System.err.println("Can not find project \"testcvs\"!");
				System.exit(1);
			}
			CvsAdaptorFacade cvs = (CvsAdaptorFacade) project.getVcs();
			cvs.setCvsRoot(":pserver:alvin@localhost:c:\\cvs_repository");
			luntbuild.saveProject(project);

			/**
			 * The following codes demonstrate update of a luntbuild system property
			 */
			String antCommand = luntbuild.getSystemProperty(SystemPropertyNames.ANT_COMMAND);
			if (antCommand != null)
				System.out.println("Current ant command: " + antCommand);
			antCommand = "\"C:\\j2sdk1.4.2_04\\bin\\java\" -classpath " +
					"\"D:\\lunt\\cvs\\lunt\\luntbuild\\ant\\lib\\ant-launcher.jar\" " +
					"\"-Dant.home=D:\\lunt\\cvs\\lunt\\luntbuild\\ant\" " +
					"org.apache.tools.ant.launch.Launcher";
			luntbuild.setSystemProperty(SystemPropertyNames.ANT_COMMAND,
					antCommand);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
