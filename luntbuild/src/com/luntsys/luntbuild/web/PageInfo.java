/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-5-16
 * Time: 18:25:42
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

/**
 * This class provides information about a page, in order to be shown at the navigation area
 * of the user interface
 *
 * @author robin shine
 */
public class PageInfo {
	private String pageName;
	private String pageDataDescription;
	private long pageDataId;

	/**
	 * Set the page name
	 * @param pageName
	 */
	public void setPageName(String pageName){
		this.pageName = pageName;
	}

	public String getPageName(){
		return pageName;
	}

	/**
	 * Set the description of the data related to this page
	 * @param pageDataDescription
	 */
	public void setPageDataDesciption(String pageDataDescription){
		this.pageDataDescription= pageDataDescription;
	}

	public String getPageDataDescription(){
		return pageDataDescription;
	}

	/**
	 * Set the id of the data related to this page. The page will load data based
	 * on this id when activated
	 * @param pageDataId
	 */
	public void setPageDataId(long pageDataId){
		this.pageDataId = pageDataId;
	}

	public long getPageDataId(){
		return pageDataId;
	}
}
