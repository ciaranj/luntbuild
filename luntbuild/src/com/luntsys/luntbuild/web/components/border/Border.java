/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-4-29
 * Time: 6:46:18
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
package com.luntsys.luntbuild.web.components.border;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IRequestCycle;
import com.luntsys.luntbuild.web.HierarchyPage;
import com.luntsys.luntbuild.utility.LuntBuild;

import java.io.File;
import java.util.Properties;

/**
 * The common border for lunt applications, it uses the RenderBody component to achieve
 * templating-like page. This border will extract special hierarchical informations from pages include it,
 * So every page include this component should extends from the {@link com.luntsys.luntbuild.web.HierarchyPage}
 *
 * @author alvin shen
 */
public class Border extends BaseComponent {             

	/**
	 * Do some initializations before render.
	 */
	protected void finishLoad() {
		super.finishLoad();
	}

	public String getTitle(){
		return "luntbuild - make your software building managable";
	}

	public IAsset getLogoImage(){
		return getAsset("logo");
	}

	public String getLogoAlt(){
		return "luntbuild";
	}

	public IAsset getHelpImage(){
		return getAsset("help");
	}

	public String getHelpAlt(){
		return "help";
	}

	public IAsset getStylesheet(){
		return getAsset("stylesheet");
	}

	public String getProductVersion(){
		String version = LuntBuild.buildInfos.getProperty("buildVersion");
		if (version == null || version.trim().equals(""))
			return "luntbuild";
		else
			return version;
	}

	public String getProductDate(){
		String date = LuntBuild.buildInfos.getProperty("buildDate");
		if (date == null || date.trim().equals(""))
			return "2001~2004";
		else
			return date;
	}
}
