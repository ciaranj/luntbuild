/**
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-21
 * Time: 16:03:58
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

import com.luntsys.luntbuild.db.RolesMapping;
import com.luntsys.luntbuild.db.Role;
import com.luntsys.luntbuild.utility.Luntbuild;
import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.GrantedAuthorityImpl;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.providers.dao.AuthenticationDao;
import net.sf.acegisecurity.providers.dao.User;
import net.sf.acegisecurity.providers.dao.UsernameNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import java.util.Iterator;


/**
 * This class integrates into the acegi security framework
 *
 * It is used to retrieve application internal user credentials
 * in our case from hsql db
 *  *
 * @author johannes plachy
 */
public class ApplicationInternalDAO implements AuthenticationDao {
    private String ldapHost;
    private String ldapPort;
    private String ldapUserDn;
    private String ldapAuthentication;
    private String ldapUserId;
    private String ldapUseLuntbuildOnFail;
    private String ldapCanCreateProject;
    private String ldapCanViewProject;
    private String ldapCanBuildProject;
    private String ldapCreateLuntbuildUser;
    private String ldapEmailAttrName;

    private static transient final Log logger = LogFactory.getLog(ApplicationInternalDAO.class);

    /**
     * @see net.sf.acegisecurity.providers.dao.AuthenticationDao#loadUserByUsername(java.lang.String)
     */
    public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException, DataAccessException {
		if (Luntbuild.isEmpty(username))
			throw new UsernameNotFoundException("");
        UserDetails userdetails = null;
        GrantedAuthority authorities[] = null;
        String password = null;

        try
        {
            // try to retrieve user credentials from local db
            com.luntsys.luntbuild.db.User luntUser = Luntbuild.getDao().loadUser(username);

            password = luntUser.getDecryptedPassword();

            int authSize = 1;
			if (luntUser.isCanCreateProject())
				authSize++;

            if ( luntUser.getRolesMappings() != null)
            {
                authSize += luntUser.getRolesMappings().size();
            }

            authorities = new GrantedAuthorityImpl[authSize];

            int ix = 0;
			authorities[ix++] = new GrantedAuthorityImpl(Role.ROLE_AUTHENTICATED);
			if (luntUser.isCanCreateProject())
				authorities[ix++] = new GrantedAuthorityImpl("LUNTBUILD_PRJ_ADMIN_0");

            if ( luntUser.getRolesMappings() != null)
            {
                Iterator iterator = luntUser.getRolesMappings().iterator();

                while (iterator.hasNext())
                {
                    RolesMapping mappedRole = (RolesMapping)iterator.next();

                    String role = mappedRole.getRole().getName();

                    if ( role.indexOf("PRJ") > 0)
                    {
                        role += "_"+mappedRole.getProject().getId();
                    }

                    authorities[ix] =  new GrantedAuthorityImpl(role);
                    ix++;
                }
            }
        }
        catch (Exception x)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("user with loginname ["+username+"] could not be found in db");
            }

            throw new UsernameNotFoundException("username ["+username+"] could not be found in db");
        }

        userdetails = new User(username, password, true, true, true, authorities);

        return userdetails;
    }

    /**
     * @return Returns the ldapHost.
     */
    public final String getLdapHost() {
        return this.ldapHost;
    }

    /**
     * @param ldapHost The ldapHost to set.
     */
    public final void setLdapHost(String ldapHost) {
        this.ldapHost = ldapHost;
    }

    /**
     * @return Returns the ldapPort.
     */
    public final String getLdapPort() {
        return this.ldapPort;
    }

    /**
     * @param ldapPort The ldapPort to set.
     */
    public final void setLdapPort(String ldapPort) {
        this.ldapPort = ldapPort;
    }

    /**
     * @return Returns the ldapUseLuntbuildOnFail.
     */
    public final String getLdapUseLuntbuildOnFail() {
        return this.ldapUseLuntbuildOnFail;
    }

    /**
     * @param ldapUseLuntbuildOnFail The ldapUseLuntbuildOnFail to set.
     */
    public final void setLdapUseLuntbuildOnFail(String ldapUseLuntbuildOnFail) {
        this.ldapUseLuntbuildOnFail = ldapUseLuntbuildOnFail;
    }

    /**
     * @return Returns the ldapUserDn.
     */
    public final String getLdapUserDn() {
        return this.ldapUserDn;
    }

    /**
     * @param ldapUserDn The ldapUserDn to set.
     */
    public final void setLdapUserDn(String ldapUserDn) {
        this.ldapUserDn = ldapUserDn;
    }

    /**
     * @return Returns the ldapCanCreateProject.
     */
    public final String getLdapCanCreateProject() {
        return this.ldapCanCreateProject;
    }

    /**
     * @param ldapCanCreateProject The ldapCanCreateProject to set.
     */
    public final void setLdapCanCreateProject(String ldapCanCreateProject) {
        this.ldapCanCreateProject = ldapCanCreateProject;
    }

    /**
     * @return Returns the ldapCreateLuntbuildUser.
     */
    public final String getLdapCreateLuntbuildUser() {
        return this.ldapCreateLuntbuildUser;
    }

    /**
     * @param ldapCreateLuntbuildUser The ldapCreateLuntbuildUser to set.
     */
    public final void setLdapCreateLuntbuildUser(String ldapCreateLuntbuildUser) {
        this.ldapCreateLuntbuildUser = ldapCreateLuntbuildUser;
    }

    /**
     * @return Returns the ldapAuthentication.
     */
    public final String getLdapAuthentication() {
        return this.ldapAuthentication;
    }

    /**
     * @param ldapAuthentication The ldapAuthentication to set.
     */
    public final void setLdapAuthentication(String ldapAuthentication) {
        this.ldapAuthentication = ldapAuthentication;
    }

    /**
     * @return Returns the ldapUserId.
     */
    public final String getLdapUserId() {
        return this.ldapUserId;
    }

    /**
     * @param ldapUserId The ldapUserId to set.
     */
    public final void setLdapUserId(String ldapUserId) {
        this.ldapUserId = ldapUserId;
    }

    /**
     * @return Returns the ldapEmailAttrName.
     */
    public final String getLdapEmailAttrName() {
        return this.ldapEmailAttrName;
    }

    /**
     * @param ldapEmailAttrName The ldapEmailAttrName to set.
     */
    public final void setLdapEmailAttrName(String ldapEmailAttrName) {
        this.ldapEmailAttrName = ldapEmailAttrName;
    }

    /**
     * @return Returns the ldapCanBuildProject.
     */
    public final String getLdapCanBuildProject() {
        return this.ldapCanBuildProject;
    }

    /**
     * @param ldapCanBuildProject The ldapCanBuildProject to set.
     */
    public final void setLdapCanBuildProject(String ldapCanBuildProject) {
        this.ldapCanBuildProject = ldapCanBuildProject;
    }

    /**
     * @return Returns the ldapCanViewProject.
     */
    public final String getLdapCanViewProject() {
        return this.ldapCanViewProject;
    }

    /**
     * @param ldapCanViewProject The ldapCanViewProject to set.
     */
    public final void setLdapCanViewProject(String ldapCanViewProject) {
        this.ldapCanViewProject = ldapCanViewProject;
    }

}
