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

import com.caucho.hessian.client.HessianProxyFactory;
import com.luntsys.luntbuild.facades.ILuntbuild;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

/**
 * This class demonstrates search builds in the system, and how to access build information.
 *
 * @author robin shine
 */
public class SearchBuilds {
	public static void main(String[] args) {
		ILuntbuild luntbuild = null;
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			/**
			 * Change user and password here to login to Luntbuild system
			 */
			factory.setUser("luntbuild");
			factory.setPassword("luntbuild");
			luntbuild = (ILuntbuild) factory.create(com.luntsys.luntbuild.facades.ILuntbuild.class,
					"http://localhost:8080/luntbuild/app.do?service=hessian");

			com.luntsys.luntbuild.facades.lb12.ScheduleFacade schedule = luntbuild.getScheduleByName("testcvs", "nightly");
			if (schedule == null) {
				System.err.println("Can not find schedule \"nightly\" inside project \"testcvs\"!");
				System.exit(1);
			}

			/**
			 * Secondly, we search for all successful builds after 2004-08-11 for the schedule retrieved in the first step
			 */
			com.luntsys.luntbuild.facades.SearchCriteria condition = new com.luntsys.luntbuild.facades.SearchCriteria();
			condition.setScheduleIds(new long[]{schedule.getId()});
			condition.setStatus(com.luntsys.luntbuild.facades.Constants.BUILD_STATUS_SUCCESS);
			condition.setFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2004-08-11"));
			List builds = luntbuild.searchBuilds(condition, 0, 0);

			/**
			 * List version and url for all found builds
			 */
			System.out.println("Total " + builds.size() + " builds found\n");
			Iterator it = builds.iterator();
			while (it.hasNext()) {
				com.luntsys.luntbuild.facades.lb12.BuildFacade buildFacade = (com.luntsys.luntbuild.facades.lb12.BuildFacade) it.next();
				System.out.println("----------------------------------------------------------");
				System.out.println("version: " + buildFacade.getVersion());
				System.out.println("url: " + buildFacade.getUrl());
				System.out.println("build log url: " + buildFacade.getBuildLogUrl());
				System.out.println("revision log url: " + buildFacade.getRevisionLogUrl());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} 
	}
}
