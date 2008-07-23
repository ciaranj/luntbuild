/*
 * Copyright  2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.luntsys.luntbuild.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.providers.cas.CasAuthoritiesPopulator;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Role;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.utility.Luntbuild;

/**
 * Luntbuild authorities populator that works with JA-SIG CAS single sign on.
 * This populador is called after the CAS authentication, in order to populate
 * the user credencials. If the user does not exist in the database, it is
 * created with a random password, so that the login has always to be made thru
 * CAS
 * 
 * @author gustavonalle@gmail.com
 * 
 */
public class LuntbuildCasAuthoritiesPopulator implements CasAuthoritiesPopulator {

	private static Log logger = LogFactory.getLog(LuntbuildCasAuthoritiesPopulator.class);

	private UserDetailsService authenticationDao;

	public UserDetailsService getAuthenticationDao() {
		return authenticationDao;
	}

	public void setAuthenticationDao(UserDetailsService authenticationDao) {
		this.authenticationDao = authenticationDao;
	}

	public UserDetails getUserDetails(String casUserId) throws AuthenticationException {
		if (!Luntbuild.getDao().isUserExist(casUserId)) {
			logger.info("User does not exist in backend. Creating...");
			User user = new User();
			user.setName(casUserId.toLowerCase());
			user.setCanCreateProject(true);
			// Generate a random 8 digit password.
			user.setDecryptedPassword(RandomStringUtils.randomAlphabetic(8));
			Luntbuild.getDao().saveUserInternal(user);
			setProjectsPrivileges(user, Role.LUNTBUILD_PRJ_ADMIN);
			setProjectsPrivileges(user, Role.LUNTBUILD_PRJ_BUILDER);
			setProjectsPrivileges(user, Role.LUNTBUILD_PRJ_VIEWER);
			return this.authenticationDao.loadUserByUsername(casUserId.toLowerCase());
		} else {
			logger.info("User is already on database. Loading...");
			User user = Luntbuild.getDao().loadUser(casUserId.toLowerCase());
			return authorizeUser(casUserId, user.getDecryptedPassword(), user.isCanCreateProject(), true, true);

		}

	}

	private void setProjectsPrivileges(User user, String role) {
		Iterator it = Luntbuild.getDao().loadProjectsInternal().iterator();
		while (it.hasNext()) {
			Project project = (Project) it.next();
			List origUsers = project.getMappedRolesUserList(role);
			ArrayList users = new ArrayList();
			users.addAll(origUsers);
			users.add(user);
			project.putMappedRolesUserList(users, role);
			Luntbuild.getDao().saveProjectInternal(project);
		}
	}

	private UserDetails authorizeUser(String name, String password, boolean canCreateProject, boolean canBuildProject, boolean canViewProject) {
		UserDetails userdetails = null;
		GrantedAuthority authorities[] = null;

		int authSize = 1;
		if (canCreateProject)
			authSize++;
		if (canViewProject)
			authSize += 2;
		if (canBuildProject)
			authSize++;

		authorities = new GrantedAuthorityImpl[authSize];

		int ix = 0;
		authorities[ix++] = new GrantedAuthorityImpl(Role.ROLE_AUTHENTICATED);
		if (canCreateProject)
			authorities[ix++] = new GrantedAuthorityImpl("LUNTBUILD_PRJ_ADMIN_0");
		if (canViewProject) {
			authorities[ix++] = new GrantedAuthorityImpl(Role.ROLE_ANONYMOUS);
			authorities[ix++] = new GrantedAuthorityImpl("LUNTBUILD_PRJ_VIEWER");
		}
		if (canBuildProject)
			authorities[ix++] = new GrantedAuthorityImpl("LUNTBUILD_PRJ_BUILDER");

		userdetails = new org.acegisecurity.userdetails.User(name, password, true, true, true, true, authorities);

		return userdetails;
	}

}
