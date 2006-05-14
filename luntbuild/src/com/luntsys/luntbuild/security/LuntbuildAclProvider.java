/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-1-27
 * Time: 15:26:26
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
package com.luntsys.luntbuild.security;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.VcsLogin;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.context.SecureContext;
import net.sf.acegisecurity.acl.AclEntry;
import net.sf.acegisecurity.acl.AclProvider;

/**
 * Luntbuild specific acl provider.
 *
 * @author alvin shen
 */
public class LuntbuildAclProvider implements AclProvider {
	public AclEntry[] getAcls(Object o) {
		return new AclEntry[0];
	}

	public AclEntry[] getAcls(Object o, Authentication authentication) {
		Project project;
		if (o instanceof Project)
			project = (Project) o;
		else if (o instanceof Schedule)
			project = ((Schedule) o).getProject();
		else if (o instanceof Build)
			project = ((Build) o).getSchedule().getProject();
		else
			project = ((VcsLogin) o).getProject();

		int permissionMask = 0;
		SecureContext context = (SecureContext) ContextHolder.getContext();
		Authentication oldAuthentication = context.getAuthentication();
		context.setAuthentication(authentication);
		if (SecurityHelper.isUserInRole(project.getId(), "ROLE_SITE_ADMIN,LUNTBUILD_PRJ_ADMIN"))
			permissionMask |= LuntbuildAclEntry.PROJECT_ADMIN;
		if (SecurityHelper.isUserInRole(project.getId(), "LUNTBUILD_PRJ_BUILDER"))
			permissionMask |= LuntbuildAclEntry.PROJECT_BUILD;
		if (SecurityHelper.isUserInRole(project.getId(), "LUNTBUILD_PRJ_VIEWER"))
			permissionMask |= LuntbuildAclEntry.PROJECT_READ;
		LuntbuildAclEntry aclEntry = new LuntbuildAclEntry(authentication.getPrincipal(), project, null, permissionMask);
		context.setAuthentication(oldAuthentication);
		return new AclEntry[]{aclEntry};
	}

	public boolean supports(Object o) {
		if (o == null)
			return false;
		if (o instanceof Project || o instanceof Schedule || o instanceof Build || o instanceof VcsLogin)
			return true;
		else
			return false;
	}
}
