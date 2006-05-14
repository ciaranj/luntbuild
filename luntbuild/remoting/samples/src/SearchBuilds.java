/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-22
 * Time: 16:09:54
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

import com.luntsys.luntbuild.remoting.*;
import com.caucho.hessian.client.HessianProxyFactory;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Iterator;

/**
 * This class demonstrates search builds in the system, and access build information.
 *
 * @author robin shine
 */
public class SearchBuilds {
	public static void main(String[] args) {
		ILuntbuild luntbuild;
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			luntbuild = (ILuntbuild) factory.create(com.luntsys.luntbuild.remoting.ILuntbuild.class,
					"http://localhost:8081/luntbuild/app?service=luntbuild");

			/**
			 * In the first step, get the build schedule "release" for view "development" of project
			 * "testcvs"
			 */
			ProjectFacade project = luntbuild.getProjectByName("testcvs");
			if (project == null) {
				System.err.println("Can not find project \"testcvs\"!");
				System.exit(1);
			}
			ViewFacade view = luntbuild.getViewByName(project, "development");
			if (view == null) {
				System.err.println("Can not find view \"development\" for project " +
						"\"testcvs\"!");
				System.exit(1);
			}
			BuildScheduleFacade buildSchedule = luntbuild.getBuildScheduleByName(view,
					"release");
			if (buildSchedule == null) {
				System.err.println("Can not find build schedule \"release\" for view " +
						"\"development\" of project \"testcvs\"!");
				System.exit(1);
			}

			/**
			 * Secondly, we search for all successful builds after 2004-08-11 for the build
			 * schedule retrieved in the first step
			 */
			SearchCriteria condition = new SearchCriteria();
			condition.setBuildScheduleId(buildSchedule.getId());
			condition.setStatus(BuildFacade.SUCCESS);
			condition.setFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2004-08-11"));
			List builds = luntbuild.searchBuilds(condition, 0, 0);

			/**
			 * List version and url for all found builds
			 */
			System.out.println("Total " + builds.size() + " builds found\n");
			Iterator it = builds.iterator();
			while (it.hasNext()) {
				BuildFacade buildFacade = (BuildFacade) it.next();
				System.out.println("----------------------------------------------------------");
				System.out.println("version: " + buildFacade.getVersion());
				System.out.println("url: " + buildFacade.getUrl());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
