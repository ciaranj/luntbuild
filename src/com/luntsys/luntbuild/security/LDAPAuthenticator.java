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
 * LDAP authenticator.
 *
 * @author lubosp, based on Kira (kec161) contribution
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
     * Creates a blank LDAP authenticator with default settings.
     */
    public LDAPAuthenticator () {
        this (DEFAULT_HOST, DEFAULT_PORT, "", DEFAULT_LDAP_AUTH, DEFAULT_LDAP_UID);
    }

    /**
     * Creates a LDAP authenticator with default settings.
     * 
     * @param host the LDAP host
     * @param userDN the LDAP user domain name
     */
    public LDAPAuthenticator (String host, String userDN){
        this(host, DEFAULT_PORT, userDN, DEFAULT_LDAP_AUTH, DEFAULT_LDAP_UID);
    }

    /**
     * Creates a LDAP authenticator.
     * 
     * @param host the LDAP host
     * @param port the LDAP port
     * @param userDN the LDAP user domain name
     * @param ldapAuth the LDAP authentication type
     * @param ldapUid the LDAP user id
     */
    public LDAPAuthenticator (String host, int port, String userDN, String ldapAuth, String ldapUid){
        this(host, port, userDN, ldapAuth, ldapUid, null, ldapUid + '=', ',' + userDN);
    }

    /**
     * Creates a LDAP authenticator.
     * 
     * @param host the LDAP host
     * @param port the LDAP port
     * @param userDN the LDAP user domain name
     * @param ldapAuth the LDAP authentication type
     * @param ldapUid the LDAP user id
     * @param ldapUrl the LDAP URL
     * @param ldapPrefix the LDAP prefix
     * @param ldapSuffix the LDAP suffix
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
     * Authenticates a user.
     * 
     * @param user the user
     * @param password the user's password
     * @return <code>true<code> if authenticated
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
     * Gets the E-mail address of a user.
     * 
     * @param user the user
     * @param password the user's password
     * @param emailAttrName the E-mail attribute name
     * @return the user's E-mail address, or <code>null</code> if the address could not be retrieved
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
                logger.warn("Cannot access directory when retrieving email!", ae);
            else
                logger.warn("Cannot access directory when retrieving email!");
            return null;
        } catch (NamingException nm){
            if (logger.isDebugEnabled())
                logger.warn("Cannot access directory when retrieving email!", nm);
            else
                logger.warn("Cannot access directory when retrieving email!");
            return null;
        } catch (Exception ce){
            if (logger.isDebugEnabled())
                logger.warn("Cannot access directory!", ce);
            else
                logger.warn("Cannot access directory!");
            return null;
        }
        return email;
    }


    /**
     * Sets the LDAP host.
     * 
     * @param host the LDAP host
     */
    public void setHost(String host){
        this.host = host;
    }

    /**
     * Sets the LDAP port.
     * 
     * @param port the LDAP port
     */
    public void setPort(int port){
        this.port = port;
    }

    /**
     * Sets the LDAP user domain name.
     * 
     * @param userDN the LDAP user domain name
     */
    public void setUserDN(String userDN){
        this.userDN = userDN;
    }

    /**
     * Gets the LDAP authentication type.
     * 
     * @return the LDAP authentication type
     */
    public final String getLdapAuth() {
        return this.ldapAuth;
    }

    /**
     * Sets the LDAP authentication type.
     * 
     * @param ldapAuth the LDAP authentication type
     */
    public final void setLdapAuth(String ldapAuth) {
        this.ldapAuth = ldapAuth;
    }

    /**
     * Gets the LDAP user id.
     * 
     * @return the LDAP user id
     */
    public final String getLdapUid() {
        return this.ldapUid;
    }

    /**
     * Sets the LDAP user id.
     * 
     * @param ldapUid the LDAP user id
     */
    public final void setLdapUid(String ldapUid) {
        this.ldapUid = ldapUid;
    }

    /**
     * Gets the LDAP URL.
     * 
     * @return the LDAP URL
     */
    public final String getLdapUrl() {
        if (this.ldapUrl != null) {
            return this.ldapUrl;
        } else {
            return "ldap://" + this.host + ":" + this.port;
        }
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
     * @return the LDAP prefix
     */
    public final String getLdapPrefix() {
        return this.ldapPrefix;
    }

    /**
     * Sets the LDAP prefix.
     * 
     * @param ldapPrefix the LDAP prefix
     */
    public final void setLdapPrefix(String ldapPrefix) {
        this.ldapPrefix = ldapPrefix;
    }

    /**
     * Gets the LDAP suffix.
     * 
     * @return the LDAP suffix
     */
    public final String getLdapSuffix() {
        return this.ldapSuffix;
    }

    /**
     * Sets the LDAP suffix.
     * 
     * @param ldapSuffix the LDAP suffix
     */
    public final void setLdapSuffix(String ldapSuffix) {
        this.ldapSuffix = ldapSuffix;
    }

    /**
     * Gets the referral.
     * 
     * @return the referral
     */
    public final String getReferral() {
        return this.referral;
    }

    /**
     * Sets the referral.
     * 
     * @param referral the referral
     */
    public final void setReferral(String referral) {
        this.referral = referral;
    }
}
