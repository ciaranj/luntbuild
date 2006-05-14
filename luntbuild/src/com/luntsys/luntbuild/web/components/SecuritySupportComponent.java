/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-2-24
 * Time: 9:58:42
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
package com.luntsys.luntbuild.web.components;

import com.luntsys.luntbuild.security.SecurityHelper;
import org.apache.tapestry.BaseComponent;

/**
 * @author johannes plachy
 */
public  class SecuritySupportComponent extends BaseComponent {
	/**
	 * Get name of current logged in user
	 * @return
	 */
	public String getPrincipal() {
        return SecurityHelper.getPrincipalAsString();
	}

	public boolean isPrjReadable(long prjId) {
		return SecurityHelper.isPrjReadable(prjId);
	}

	public boolean isPrjBuildable(long prjId) {
		return SecurityHelper.isPrjBuildable(prjId);
	}

	public boolean isPrjAdministrable(long prjId) {
		return SecurityHelper.isPrjAdministrable(prjId);
	}

	public boolean isSiteAdmin() {
		return SecurityHelper.isSiteAdmin();
	}
}
