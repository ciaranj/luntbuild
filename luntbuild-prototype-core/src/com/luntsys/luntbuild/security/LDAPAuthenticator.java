package com.luntsys.luntbuild.security;

import javax.naming.*;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Hashtable;

/**
 * LDAP Authenticator
 *
 * @author lubosp, based on Kira (kec161) contribution
 *
 */
public class LDAPAuthenticator {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 389;
    private static final String DEFAULT_LDAP_AUTH = "simple";
    private static final String DEFAULT_LDAP_UID = "uid";

    private static Log logger = LogFactory.getLog(LDAPAuthenticator.class);

    private String host = null;
    private int port = 0;
    private String userDN = null;
    private String ldapAuth = null;
    private String ldapUid = null;
    private String ldapUrl = null;
    private String ldapPrefix = null;
    private String ldapSuffix = null;
    private String referral = null;

    /**
     *
     */
    public LDAPAuthenticator () {
        this (DEFAULT_HOST, DEFAULT_PORT, "", DEFAULT_LDAP_AUTH, DEFAULT_LDAP_UID);
    }

    /**
     * @param host
     * @param userDN
     */
    public LDAPAuthenticator (String host, String userDN){
        this(host, DEFAULT_PORT, userDN, DEFAULT_LDAP_AUTH, DEFAULT_LDAP_UID);
    }

    /**
     * @param host
     * @param port
     * @param userDN
     * @param ldapAuth
     * @param ldapUid
     */
    public LDAPAuthenticator (String host, int port, String userDN, String ldapAuth, String ldapUid){
        this(host, port, userDN, ldapAuth, ldapUid, null, ldapUid + '=', ',' + userDN);
    }

    /**
     * @param host
     * @param port
     * @param userDN
     * @param ldapAuth
     * @param ldapUid
     * @param ldapUrl
     * @param ldapPrefix
     * @param ldapSuffix
     */
    public LDAPAuthenticator (String host, int port, String userDN, String ldapAuth, String ldapUid, String ldapUrl, String ldapPrefix, String ldapSuffix){
        setHost(host);
        setUserDN(userDN);
        setPort(port);
        setLdapAuth(ldapAuth);
        setLdapUid(ldapUid);
        setLdapUrl(ldapUrl);
        setLdapPrefix(ldapPrefix);
        setLdapSuffix(ldapSuffix);
    }

    private Hashtable createContextEnv(String user, String password) {
        Hashtable env = new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, getLdapUrl());

        env.put(Context.SECURITY_AUTHENTICATION, this.ldapAuth);
        env.put(Context.SECURITY_PRINCIPAL, this.ldapPrefix + user + this.ldapSuffix);
        env.put(Context.SECURITY_CREDENTIALS, password);

        if (this.referral != null) {
            env.put(Context.REFERRAL, this.referral);
        }
        return env;
    }

    /**
     * @param user
     * @param password
     * @return true if authenticated
     */
    public boolean authenticate (String user, String password) {
        Hashtable env = createContextEnv(user, password);

        if (logger.isDebugEnabled()) {
            Hashtable newEnv = new Hashtable(env);
            newEnv.put(Context.SECURITY_CREDENTIALS, "*****");
            logger.debug("Logging in with env: " + newEnv);
        }

        /* Connect to LDAP server, if connection is made, user is authenticated */
        try {
            Context ct = new InitialContext(env);
            ct.close();
        } catch (AuthenticationException ae){
            if (logger.isDebugEnabled())
                logger.warn("Error: Cannot authenticate user!", ae);
            else
                logger.warn("Error: Cannot authenticate user!");
            return false;
        } catch (NamingException nm){
            if (logger.isDebugEnabled())
                logger.warn("Error: Cannot authenticate user!", nm);
            else
                logger.warn("Error: Cannot authenticate user!");
            return false;
        } catch (Exception ce){
            logger.warn("Error: Cannot connect!", ce);
            return false;
        }
        return true;
    }

    /**
     * @param user
     * @param password
     * @param emailAttrName
     * @return email or null
     */
    public String lookupEmail(String user, String password, String emailAttrName) {
        Hashtable env = createContextEnv(user, password);

        /* Connect to LDAP server, if connection is made, user is authenticated, get email */
        String email = null;
        try {
            DirContext ctx = new InitialDirContext(env);
            SearchControls ctls = new SearchControls();
            ctls. setReturningObjFlag (true);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            //Search for objects with these matching attributes
            NamingEnumeration answer =
                ctx.search(this.userDN, "(&(objectclass=user)("+this.ldapUid+"="+user+"))", ctls);
            if (answer == null) return null;
            while (answer.hasMore()) {
                SearchResult sr = (SearchResult)answer.next();
                Attributes attrs = sr.getAttributes();
                if (attrs == null) continue;
                try {
                    Attribute attrib = attrs.get(emailAttrName);
                    if (attrib.getID().equals(emailAttrName)) {
                        // Return first email
                        for (NamingEnumeration e = attrib.getAll();e.hasMore();) {
                            return (String)e.next();
                        }
                    }
                } catch (NamingException e) {
                    if (logger.isDebugEnabled())
                        logger.warn("Cannot retrieve email!", e);
                    else
                        logger.warn("Cannot retrieve email!");
                }
            }

            ctx.close();
        } catch (AuthenticationException ae){
            if (logger.isDebugEnabled())
                logger.warn("Cannot authenticate user!", ae);
            else
                logger.warn("Cannot authenticate user!");
            return null;
        } catch (NamingException nm){
            if (logger.isDebugEnabled())
                logger.warn("Cannot authenticate user!", nm);
            else
                logger.warn("Cannot authenticate user!");
            return null;
        } catch (Exception ce){
            if (logger.isDebugEnabled())
                logger.warn("Cannot connect!", ce);
            else
                logger.warn("Cannot connect!");
            return null;
        }
        return email;
    }


    /**
     * @param host
     */
    public void setHost (String host){
        this.host = host;
    }

    /**
     * @param port
     */
    public void setPort (int port){
        this.port = port;
    }

    /**
     * @param userDN
     */
    public void setUserDN (String userDN){
        this.userDN = userDN;
    }

    /**
     * @return Returns the ldapAuth.
     */
    public final String getLdapAuth() {
        return this.ldapAuth;
    }

    /**
     * @param ldapAuth The ldapAuth to set.
     */
    public final void setLdapAuth(String ldapAuth) {
        this.ldapAuth = ldapAuth;
    }

    /**
     * @return Returns the ldapUid.
     */
    public final String getLdapUid() {
        return this.ldapUid;
    }

    /**
     * @param ldapUid The ldapUid to set.
     */
    public final void setLdapUid(String ldapUid) {
        this.ldapUid = ldapUid;
    }

        /**
     * @return Returns the ldapUrl.
     */
    public final String getLdapUrl() {
        if (this.ldapUrl != null) {
            return this.ldapUrl;
        } else {
            return "ldap://" + this.host + ":" + this.port;
        }
    }

    /**
     * @param ldapUrl The ldapUrl to set.
     */
    public final void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    /**
     * @return Returns the ldapPrefix.
     */
    public final String getLdapPrefix() {
        return this.ldapPrefix;
    }

    /**
     * @param ldapPrefix The ldapPrefix to set.
     */
    public final void setLdapPrefix(String ldapPrefix) {
        this.ldapPrefix = ldapPrefix;
    }

    /**
     * @return Returns the ldapSuffix.
     */
    public final String getLdapSuffix() {
        return this.ldapSuffix;
    }

    /**
     * @param ldapSuffix The ldapSuffix to set.
     */
    public final void setLdapSuffix(String ldapSuffix) {
        this.ldapSuffix = ldapSuffix;
    }

    /**
     * @return Returns the referral.
     */
    public final String getReferral() {
        return this.referral;
    }

    /**
     * @param referral The referral to set.
     */
    public final void setReferral(String referral) {
        this.referral = referral;
    }
}
