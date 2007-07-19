/*
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
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import java.util.Iterator;

/**
 * Used to retrieve application internal user credentials, in our case from HSQL DB.
 * 
 * <p>This class integrates into the acegi security framework.</p>
 * 
 * @author johannes plachy
 */
public class ApplicationInternalDAO implements UserDetailsService {
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
    private String ldapUrl;
    private String ldapPrefix;
    private String ldapSuffix;

    private static transient final Log logger = LogFactory.getLog(ApplicationInternalDAO.class);

    /**
     * @inheritDoc
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
     * Gets the LDAP host.
     * 
     * @return the LDAP host
     */
    public final String getLdapHost() {
        return this.ldapHost;
    }

    /**
     * Sets the LDAP host.
     * 
     * @param ldapHost the LDAP host
     */
    public final void setLdapHost(String ldapHost) {
        this.ldapHost = ldapHost;
    }

    /**
     * Gets the LDAP port.
     * 
     * @return the LDAP port
     */
    public final String getLdapPort() {
        return this.ldapPort;
    }

    /**
     * Sets the LDAP port.
     * 
     * @param ldapPort the LDAP port
     */
    public final void setLdapPort(String ldapPort) {
        this.ldapPort = ldapPort;
    }

    /**
     * Gets the use Luntbuild user on fail setting.
     * 
     * @return the setting
     */
    public final String getLdapUseLuntbuildOnFail() {
        return this.ldapUseLuntbuildOnFail;
    }

    /**
     * Sets the use Luntbuild user on fail setting.
     * 
     * @param ldapUseLuntbuildOnFail the setting
     */
    public final void setLdapUseLuntbuildOnFail(String ldapUseLuntbuildOnFail) {
        this.ldapUseLuntbuildOnFail = ldapUseLuntbuildOnFail;
    }

    /**
     * Gets the LDAP user domain name.
     * 
     * @return the LDAP user domain name
     */
    public final String getLdapUserDn() {
        return this.ldapUserDn;
    }

    /**
     * Sets the LDAP user domain name.
     * 
     * @param ldapUserDn the LDAP user domain name
     */
    public final void setLdapUserDn(String ldapUserDn) {
        this.ldapUserDn = ldapUserDn;
    }

    /**
     * Gets the can create project setting.
     * 
     * @return the can create project setting
     */
    public final String getLdapCanCreateProject() {
        return this.ldapCanCreateProject;
    }

    /**
     * Sets the can create project setting.
     * 
     * @param ldapCanCreateProject the can create project setting
     */
    public final void setLdapCanCreateProject(String ldapCanCreateProject) {
        this.ldapCanCreateProject = ldapCanCreateProject;
    }

    /**
     * Gets the create Luntbuild user setting.
     * 
     * @return the create Luntbuild user setting
     */
    public final String getLdapCreateLuntbuildUser() {
        return this.ldapCreateLuntbuildUser;
    }

    /**
     * Sets the create Luntbuild user setting.
     * 
     * @param ldapCreateLuntbuildUser the create Luntbuild user setting
     */
    public final void setLdapCreateLuntbuildUser(String ldapCreateLuntbuildUser) {
        this.ldapCreateLuntbuildUser = ldapCreateLuntbuildUser;
    }

    /**
     * Gets the LDAP authentication type.
     * 
     * @return the LDAP authentication type
     */
    public final String getLdapAuthentication() {
        return this.ldapAuthentication;
    }

    /**
     * Sets the LDAP authentication type.
     * 
     * @param ldapAuthentication the LDAP authentication type
     */
    public final void setLdapAuthentication(String ldapAuthentication) {
        this.ldapAuthentication = ldapAuthentication;
    }

    /**
     * Gets the LDAP user id.
     * 
     * @return the LDAP user id
     */
    public final String getLdapUserId() {
        return this.ldapUserId;
    }

    /**
     * Sets the LDAP user id.
     * 
     * @param ldapUserId the LDAP user id
     */
    public final void setLdapUserId(String ldapUserId) {
        this.ldapUserId = ldapUserId;
    }

    /**
     * Gets the LDAP E-mail attribute name.
     * 
     * @return the LDAP E-mail attribute name
     */
    public final String getLdapEmailAttrName() {
        return this.ldapEmailAttrName;
    }

    /**
     * Sets the LDAP E-mail attribute name.
     * 
     * @param ldapEmailAttrName the LDAP E-mail attribute name
     */
    public final void setLdapEmailAttrName(String ldapEmailAttrName) {
        this.ldapEmailAttrName = ldapEmailAttrName;
    }

    /**
     * Gets the LDAP URL.
     * 
     * @return the LDAP URL
     */
    public final String getLdapUrl() {
        return this.ldapUrl;
    }

    /**
     * Sets the LDAP URL.
     * 
     * @param ldapUrl the LDAP URL
     */
    public final void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    /**
     * Gets the LDAP prefix.
     * 
     * @return the LDAP URL
     */
    public final String getLdapPrefix() {
        return this.ldapPrefix;
    }

    /**
     * Sets the LDAP prefix.
     * 
     * @param ldapPrefix the LDAP URL
     */
    public final void setLdapPrefix(String ldapPrefix) {
        this.ldapPrefix = ldapPrefix;
    }

    /**
     * Gets the LDAP suffix.
     * 
     * @return the LDAP URL
     */
    public final String getLdapSuffix() {
        return this.ldapSuffix;
    }

    /**
     * Sets the LDAP suffix.
     * 
     * @param ldapSuffix the LDAP URL
     */
    public final void setLdapSuffix(String ldapSuffix) {
        this.ldapSuffix = ldapSuffix;
    }

    /**
     * Gets the can build project setting.
     * 
     * @return the can build project setting
     */
    public final String getLdapCanBuildProject() {
        return this.ldapCanBuildProject;
    }

    /**
     * Sets the can build project setting.
     * 
     * @param ldapCanBuildProject the can build project setting
     */
    public final void setLdapCanBuildProject(String ldapCanBuildProject) {
        this.ldapCanBuildProject = ldapCanBuildProject;
    }

    /**
     * Gets the can view project setting.
     * 
     * @return the can view project setting
     */
    public final String getLdapCanViewProject() {
        return this.ldapCanViewProject;
    }

    /**
     * Sets the can view project setting.
     * 
     * @param ldapCanViewProject the can view project setting
     */
    public final void setLdapCanViewProject(String ldapCanViewProject) {
        this.ldapCanViewProject = ldapCanViewProject;
    }
}
