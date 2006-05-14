/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-30
 * Time: 7:03:17
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

import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.IExternalPage;

/**
 * This abstract class should be extended by every page who wants to include the Border component.
 *
 * @author alvin shen
 * @see com.luntsys.luntbuild.web.components.border.Border
 */
public abstract class HierarchyPage extends BasePage implements IExternalPage{
	/**
	 * @return a list of hierarchy page information from most ancestor to parent
	 */
	public abstract PageInfo[] getHierarchyPageInfos();

	/**
	 * Get description of the data contained in current page
	 * @return
	 */ 
	public abstract String getPageDataDescription();

	/**
	 * Get number of seconds to automatically refresh the page. This page will not
	 * automatically be refreshed if this value is 0
	 * @return
	 */
	public abstract int getRefreshInterval();
}