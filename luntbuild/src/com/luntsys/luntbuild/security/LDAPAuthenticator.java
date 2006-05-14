package com.luntsys.luntbuild.security;

import javax.naming.*;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
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

    private static Log logger = LogFactory.getLog(LDAPAuthenticator.class);

    private String host = null;
    private int port = 0;
    private String userDN = null;
    private String ldapAuth = null;
    private String ldapUid = null;

    /**
     *
     */
    public LDAPAuthenticator () {
        this (null, 389, null, "simple", "uid=");
    }

    /**
     * @param host
     * @param userDN
     */
    public LDAPAuthenticator (String host, String userDN){
        this(host, 389, userDN, "simple", "uid=");
    }

    /**
     * @param host
     * @param port
     * @param userDN
     * @param ldapAuth
     * @param ldapUid
     */
    public LDAPAuthenticator (String host, int port, String userDN, String ldapAuth, String ldapUid){
        setHost(host);
        setUserDN(userDN);
        setPort(port);
        setLdapAuth(ldapAuth);
        setLdapUid(ldapUid);
    }

    private Hashtable createContextEnv(String user, String password) {
        Hashtable env = new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);

        env.put(Context.SECURITY_AUTHENTICATION, this.ldapAuth);
        env.put(Context.SECURITY_PRINCIPAL, this.ldapUid + "=" + user + "," + this.userDN);
        env.put(Context.SECURITY_CREDENTIALS, password);

        return env;
    }

    private Hashtable CreateDirContextEnv() {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);

        return env;
    }

    /**
     * @param user
     * @param password
     * @return true if authenticated
     */
    public boolean authenticate (String user, String password) {
        Hashtable env = createContextEnv(user, password);

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
        Hashtable env = CreateDirContextEnv();

        /* Connect to LDAP server, if connection is made, user is authenticated, get email */
        String email = null;
        try {
            DirContext ctx = new InitialDirContext(env);
            SearchControls ctls = new SearchControls();
            ctls. setReturningObjFlag (true);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            Attributes matchAttrs = new BasicAttributes(true);
            matchAttrs.put(new BasicAttribute(this.ldapUid, user));
            matchAttrs.put(new BasicAttribute(emailAttrName));
            //Search for objects with these matching attributes
            NamingEnumeration answer = ctx.search(this.userDN, matchAttrs);
            if (answer == null) return null;
            while (answer.hasMore()) {
                SearchResult sr = (SearchResult)answer.next();
                Attributes attrs = sr.getAttributes();
                if (attrs == null) continue;
                try {
                    for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {
                        Attribute attrib = (Attribute)ae.next();
                        if (attrib.getID().equals(emailAttrName)) {
                            // Return first email
                            for (NamingEnumeration e = attrib.getAll();e.hasMore();) {
                                return (String)e.next();
                            }
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

}
