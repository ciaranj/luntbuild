/**
 * 
 */
package com.luntsys.luntbuild.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.ldap.DefaultInitialDirContextFactory;
import org.acegisecurity.providers.ldap.LdapAuthenticationProvider;
import org.acegisecurity.providers.ldap.LdapAuthenticator;
import org.acegisecurity.providers.ldap.LdapAuthoritiesPopulator;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.luntsys.luntbuild.db.Project;
import com.luntsys.luntbuild.db.Role;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.notifiers.EmailNotifier;
import com.luntsys.luntbuild.notifiers.TemplatedNotifier;
import com.luntsys.luntbuild.utility.Luntbuild;


/**
 * @author lubosp
 *
 */
public class LuntbuildLdapAuthenticationProvider extends LdapAuthenticationProvider {
	
    private static Log logger = LogFactory.getLog(LuntbuildLdapAuthenticationProvider.class);

    private UserDetailsService authenticationDao;
    private DefaultInitialDirContextFactory dirContextFactory = null;
        
    private String ldapCanCreateProject;
    private String ldapCanViewProject;
    private String ldapCanBuildProject;
    private String ldapCreateLuntbuildUser;
    private String ldapEmailAttrName;
    private String ldapAccountNameAttr;
    private String ldapSearchBase;
    private String ldapFullNameAttr;

    public LuntbuildLdapAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
        super(authenticator, authoritiesPopulator);
    }

    /**
     * Creates a <code>UserDetails</code> instance ({@link SplusServerUserDetails}) that provides the additional properties
     * <code>email</code> and <code>displayname</code>.
     */
    protected UserDetails createUserDetails(LdapUserDetails ldapUser, String username, String password) {
    	if (Luntbuild.isEmpty(username)) throw new UsernameNotFoundException("");

    	boolean canCreateProject = new Boolean(getLdapCanCreateProject()).booleanValue();
    	boolean canViewProject = new Boolean(getLdapCanViewProject()).booleanValue();
    	boolean canBuildProject = new Boolean(getLdapCanBuildProject()).booleanValue();
    	boolean doCreateLuntbuildUser = new Boolean(getLdapCreateLuntbuildUser()).booleanValue();

    	// Load Luntbuild user if exist, or create new user
    	if (doCreateLuntbuildUser) {
    		if (!Luntbuild.getDao().isUserExist(username.toLowerCase())) {
    			// create user
    			User user = new User();
    			user.setName(username.toLowerCase());
    			user.setCanCreateProject(canCreateProject);
    			user.setDecryptedPassword(password);
    			HashMap attrs = lookupUserAttrs(username);
    			String fullName = (String)attrs.get("fullname");
    			if (fullName != null) user.setFullname(fullName);
    			
    			// set email
    			if (ldapEmailAttrName != null && ldapEmailAttrName.trim().length() > 0) {
    				String email = (String)attrs.get("mail");
    				if (email != null) {
	    				Map contacts = user.getContacts();
	    				List notifiers = Luntbuild.getNotifierInstances(Luntbuild.notifiers);
	    				EmailNotifier emailNotifier = null;
	    				for (Iterator iter = notifiers.iterator(); iter.hasNext();) {
	    					TemplatedNotifier cz = (TemplatedNotifier) iter.next();
	    					if (cz instanceof EmailNotifier) {
	    						emailNotifier = (EmailNotifier)cz;
	    						break;
	    					}
	    				}
	    				if (emailNotifier != null) {
	    					contacts.put(emailNotifier.getKey(), email);
	    					user.setContacts(contacts);
	    				}
    				}
    			}

    			Luntbuild.getDao().saveUserInternal(user);

    			if (canCreateProject) setProjectsPrivileges(user, Role.LUNTBUILD_PRJ_ADMIN);
    			if (canBuildProject) setProjectsPrivileges(user, Role.LUNTBUILD_PRJ_BUILDER);
    			if (canViewProject) setProjectsPrivileges(user, Role.LUNTBUILD_PRJ_VIEWER);

    			logger.info("LDAP User Authentication (user: " + username + ") SUCCESS\n");
    			return this.authenticationDao.loadUserByUsername(username.toLowerCase());

    		} else {
    			// Update password
    			User user = Luntbuild.getDao().loadUser(username.toLowerCase());
    			user.setDecryptedPassword(password);
    			Luntbuild.getDao().saveUserInternal(user);
    			return authorizeUser(username, password,
    					user.isCanCreateProject(), canBuildProject, canViewProject);
    		}

    	} else {
    		logger.info("LDAP User Authentication (user: " + username + ") SUCCESS\n");
    		return authorizeUser(username, password, canCreateProject, canBuildProject, canViewProject);
    	}
    }
    
    /**
     * Sets the authentication DAO.
     * 
     * @param authenticationDao the authentication DAO
     */
    public void setAuthenticationDao(UserDetailsService authenticationDao) {
        this.authenticationDao = authenticationDao;
    }

    /**
     * Gets the authentication DAO.
     * 
     * @return the authentication DAO
     */
    public UserDetailsService getAuthenticationDao() {
        return this.authenticationDao;
    }

	/**
	 * @return the dirContextFactory
	 */
	public DefaultInitialDirContextFactory getDirContextFactory() {
		return dirContextFactory;
	}

	/**
	 * @param dirContextFactory the dirContextFactory to set
	 */
	public void setDirContextFactory(
			DefaultInitialDirContextFactory dirContextFactory) {
		this.dirContextFactory = dirContextFactory;
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

    private UserDetails authorizeUser(String name, String password,
            boolean canCreateProject, boolean canBuildProject, boolean canViewProject) {
        UserDetails userdetails = null;
        GrantedAuthority authorities[] = null;

        int authSize = 1;
        if (canCreateProject) authSize++;
        if (canViewProject) authSize += 2;
        if (canBuildProject) authSize++;

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

        userdetails =
            new org.acegisecurity.userdetails.User(name, password, true, true, true, true, authorities);

        return userdetails;
    }

    /**
     * Gets the E-mail address of a user.
     * 
     * @param user the user
     * @return the user's E-mail address, or <code>null</code> if the address could not be retrieved
     */
    public HashMap lookupUserAttrs(String user) {
    	HashMap retAttrs = new HashMap();
        
        /* Connect to LDAP server, if connection is made, user is authenticated, get email */
        DirContext ctx = null;
        try {
            ctx = dirContextFactory.newInitialDirContext();
            SearchControls ctls = new SearchControls();
            ctls.setReturningObjFlag(true);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            //Search for objects with these matching attributes
            NamingEnumeration answer =
            	ctx.search(getLdapSearchBase(), "(" + getLdapAccountNameAttr() + "=" + user + ")", ctls);
            if (answer == null) return null;
            
            while (answer.hasMore()) {
                SearchResult sr = (SearchResult)answer.next();
                Attributes attrs = sr.getAttributes();
                if (attrs == null) continue;
                if (retAttrs.get("mail") == null) {
	                try {
	                    Attribute attrib = attrs.get(getLdapEmailAttrName());
	                    if (attrib.getID().equals(getLdapEmailAttrName())) {
	                        // Return first email
	                        for (NamingEnumeration e = attrib.getAll(); e.hasMore(); ) {
	                        	retAttrs.put("mail", (String)e.next());
	                        	break;
	                        }
	                    }
	                } catch (NamingException e) {
	                    if (logger.isDebugEnabled())
	                        logger.warn("Cannot retrieve email!", e);
	                    else
	                        logger.warn("Cannot retrieve email!");
	                }
                }
                if (retAttrs.get("fullname") == null) {
	                try {
	                    Attribute attrib = attrs.get(getLdapFullNameAttr());
	                    if (attrib.getID().equals(getLdapFullNameAttr())) {
	                        // Return first email
	                        for (NamingEnumeration e = attrib.getAll(); e.hasMore(); ) {
	                        	retAttrs.put("fullname", (String)e.next());
	                        	break;
	                        }
	                    }
	                } catch (NamingException e) {
	                    if (logger.isDebugEnabled())
	                        logger.warn("Cannot retrieve email!", e);
	                    else
	                        logger.warn("Cannot retrieve email!");
	                }
                }
                if (retAttrs.get("mail") != null && retAttrs.get("fullname") != null)
                	break;
            }

        } catch (AuthenticationException ae){
            if (logger.isDebugEnabled())
                logger.warn("Cannot access directory when retrieving email!", ae);
            else
                logger.warn("Cannot access directory when retrieving email!");
        } catch (NamingException nm){
            if (logger.isDebugEnabled())
                logger.warn("Cannot access directory when retrieving email!", nm);
            else
                logger.warn("Cannot access directory when retrieving email!");
        } catch (Exception ce){
            if (logger.isDebugEnabled())
                logger.warn("Cannot access directory!", ce);
            else
                logger.warn("Cannot access directory!");
        } finally {
            if (ctx != null) try {ctx.close();} catch (Exception e) {}
        }
        return retAttrs;
    }

	/**
	 * @return the ldapAccountNameAttr
	 */
	public String getLdapAccountNameAttr() {
		return ldapAccountNameAttr;
	}

	/**
	 * @param ldapAccountNameAttr the ldapAccountNameAttr to set
	 */
	public void setLdapAccountNameAttr(String ldapAccountNameAttr) {
		this.ldapAccountNameAttr = ldapAccountNameAttr;
	}

	/**
	 * @return the ldapSearchBase
	 */
	public String getLdapSearchBase() {
		return ldapSearchBase;
	}

	/**
	 * @param ldapSearchBase the ldapSearchBase to set
	 */
	public void setLdapSearchBase(String ldapSearchBase) {
		this.ldapSearchBase = ldapSearchBase;
	}

	/**
	 * @return the ldapFullNameAttr
	 */
	public String getLdapFullNameAttr() {
		return ldapFullNameAttr;
	}

	/**
	 * @param ldapFullNameAttr the ldapFullNameAttr to set
	 */
	public void setLdapFullNameAttr(String ldapFullNameAttr) {
		this.ldapFullNameAttr = ldapFullNameAttr;
	}

}
