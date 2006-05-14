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
import com.luntsys.luntbuild.remoting.ILuntbuild;
import com.luntsys.luntbuild.remoting.facade.ProjectFacade;
import com.luntsys.luntbuild.remoting.facade.ScheduleFacade;

import java.net.MalformedURLException;

/**
 * This class gives an example of using luntbuild web service API to trigger a
 * build
 */
public class TriggerBuild {                
	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("Parameters not correct, it should be of the format " +
					"\"<luntbuild service url> <project name> <schedule name>\". " +
					"luntbuild service url should be \"http://<ip>:<port>/luntbuild/app?service=hessian\", " +
					"where <ip> and <port> should be replaced with actual ip address and port number you " +
					"access luntbuild from web. Project name, and schedule name represents " +
					"the actual project/schedule you want to trigger the build in.");
			System.exit(1);
		}
		String url = args[0];
		String projectName = args[1];
		String scheduleName = args[2];

		ILuntbuild luntbuild;
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			luntbuild = (ILuntbuild) factory.create(com.luntsys.luntbuild.remoting.ILuntbuild.class, url);
			ProjectFacade project = luntbuild.getProjectByName(projectName);
			if (project == null) {
				System.err.println("Can not find project \"" + projectName + "\"!");
				System.exit(1);
			}
			ScheduleFacade schedule = luntbuild.getScheduleByName(project, scheduleName);
			if (schedule == null) {
				System.err.println("Can not find schedule \"" + scheduleName + " inside project \"" + projectName + "\"!");
				System.exit(1);
			}
			luntbuild.triggerBuild(schedule);
			System.out.println("Build triggered successfully!");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}