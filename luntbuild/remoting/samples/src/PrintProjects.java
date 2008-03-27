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

import java.util.Iterator;
import java.util.List;

/**
 * This class gives an example of using luntbuild web service API to edit properties
 * of a project
 *
 * @author robin shine
 */
public class PrintProjects {
	public static void main(String[] args) {
		String url = "http://localhost:8080/luntbuild/app.do?service=hessian";
		com.luntsys.luntbuild.facades.ILuntbuild luntbuild;
		HessianProxyFactory factory = new HessianProxyFactory();
        factory.setOverloadEnabled(true);
		try {
			/**
			 * The following code demonstrates how to project list
			 */

			/**
			 * Change user and password here to login to Luntbuild system
			 */
			factory.setUser("luntbuild");
			factory.setPassword("luntbuild");
			luntbuild = (com.luntsys.luntbuild.facades.ILuntbuild)
            factory.create(com.luntsys.luntbuild.facades.ILuntbuild.class, url);

            List projects = null;
            int count = 0;
            while (count++ < 1000) {
                try{
                    projects = luntbuild.getAllProjects();
                }catch(Exception e){
                    System.err.println("Unable to get projectd from Luntbuild!");
                    System.exit(1);
                }

                System.out.println("List of projects in Luntbuild:");
                com.luntsys.luntbuild.facades.lb12.ProjectFacade project = null;
                for (Iterator iter = projects.iterator(); iter.hasNext();) {
                    project = (com.luntsys.luntbuild.facades.lb12.ProjectFacade) iter.next();
                    if (project == null) {
                        System.err.println("Can not find project!");
                        System.exit(1);
                    }
                    System.out.println(project.getName() + " - " + project.getDescription());
                }
                Thread.sleep(10 * 1000);
            }
		} catch (Exception e) {
			e.printStackTrace();
            System.exit(1);
		}
	}
}
