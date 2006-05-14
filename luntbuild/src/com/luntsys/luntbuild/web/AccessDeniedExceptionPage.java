/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-30
 * Time: 8:36:29
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
package com.luntsys.luntbuild.web;

import org.apache.tapestry.IRequestCycle;

/**
 * this page is used to show aspects of a specific project.
 *
 * @author robin shine
 */
public class AccessDeniedExceptionPage extends HierarchyPage {
	public String getPageDataDescription() {
			return "information";
	}

	public PageInfo[] getHierarchyPageInfos() {
		PageInfo[] pageInfos = new PageInfo[1];
		pageInfos[0] = new PageInfo();
		pageInfos[0].setPageName("Home");

		// data description and id are non-sense for home page, we are just make some
		// initialization of them
		pageInfos[0].setPageDataDesciption("Home");
		pageInfos[0].setPageDataId(Home.SERVICE_PARAMETER_HOME);

		return pageInfos;
	}

	public int getRefreshInterval(){
		return 0;
	}

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		// does nothing
	}
}
