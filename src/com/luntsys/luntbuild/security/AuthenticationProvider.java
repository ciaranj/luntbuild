package com.luntsys.luntbuild.security;

import org.acegisecurity.providers.dao.AbstractUserDetailsAuthenticationProvider;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;

import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationServiceException;

import org.springframework.dao.DataAccessException;
import org.acegisecurity.DisabledException;
import org.acegisecurity.AccountExpiredException;
import org.acegisecurity.CredentialsExpiredException;

import org.acegisecurity.Authentication;
import org.springframework.util.Assert;

import org.acegisecurity.providers.dao.UserCache;
import org.acegisecurity.providers.dao.cache.NullUserCache;

/**
 * DAO authentication provider.
 *
 * @author lubosp
 */
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider  {

    private UserDetailsService authenticationDao;
    private boolean hideUserNotFoundExceptions = true;
    private UserCache userCache = new NullUserCache();
    private boolean forcePrincipalAsString = false;
    private UserDetails userAuth = null;

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
     * Sets suppression of <code>BadCredentialsException</code> for unfound
     * usernames or incorrect passwords.
     * 
     * <p>By default the <code>DaoAuthenticationProvider</code> throws a
     * <code>BadCredentialsException</code> if a username is not found or the
     * password is incorrect. Setting this property to <code>false</code> will
     * cause <code>UsernameNotFoundException</code>s to be thrown instead for
     * the former. Note this is considered less secure than throwing
     * <code>BadCredentialsException</code> for both exceptions.</p>
     *
     * @param hideUserNotFoundExceptions set to <code>false</code> if you wish
     *        <code>UsernameNotFoundException</code>s to be thrown instead of
     *        the non-specific <code>BadCredentialsException</code> (defaults
     *        to <code>true</code>)
     */
    public void setHideUserNotFoundExceptions(
        boolean hideUserNotFoundExceptions) {
        this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
    }

    /**
     * Checks if suppression of <code>BadCredentialsException</code> for unfound
     * usernames or incorrect passwords is on.
     * 
     * @return <code>true</code> if <code>BadCredentialsException</code> should be thrown,
     * <code>false</code> if <code>UsernameNotFoundException</code> should be thrown
     */
    public boolean isHideUserNotFoundExceptions() {
        return this.hideUserNotFoundExceptions;
    }

    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication)
    		throws AuthenticationException {
//        Object salt = null;
//        this.userAuth = userDetails;
//
//        String name = authentication.getPrincipal().toString().trim();
//        String password = authentication.getCredentials().toString().trim();
//
//        // Handle sys admin
//        if (name.equals("luntbuild") || name.equals("anonymous")) {
//            if (userDetails == null)
//                throw new AuthenticationServiceException(
//                "AuthenticationDao returned null, which is an interface contract violation");
//
//            if (this.saltSource != null) {
//                salt = this.saltSource.getSalt(userDetails);
//            }
//
//            if (!this.passwordEncoder.isPasswordValid(userDetails.getPassword(),
//                    authentication.getCredentials().toString(), salt)) {
//                throw new BadCredentialsException("Bad credentials: "
//                        + userDetails.toString());
//            }
//            return;
//        }
//
//        // Get Luntbuild authenticator
//        if (!(this.authenticationDao instanceof ApplicationInternalDAO)) return;
//
//        ApplicationInternalDAO luntbuildAuthenticator = (ApplicationInternalDAO)this.authenticationDao;
//
//        int port = 389;
//        try {
//            port = Integer.parseInt(luntbuildAuthenticator.getLdapPort());
//        } catch (NumberFormatException e) {
//            port = 389;
//        }
//
//        // Get LDAP host and userDN
//        String host = luntbuildAuthenticator.getLdapHost();
//        String userDN = luntbuildAuthenticator.getLdapUserDn();
//        boolean useLuntbuildOnFail =
//            new Boolean(luntbuildAuthenticator.getLdapUseLuntbuildOnFail()).booleanValue();
//        boolean canCreateProject =
//            new Boolean(luntbuildAuthenticator.getLdapCanCreateProject()).booleanValue();
//        boolean canViewProject =
//            new Boolean(luntbuildAuthenticator.getLdapCanViewProject()).booleanValue();
//        boolean canBuildProject =
//            new Boolean(luntbuildAuthenticator.getLdapCanBuildProject()).booleanValue();
//        boolean doCreateLuntbuildUser =
//            new Boolean(luntbuildAuthenticator.getLdapCreateLuntbuildUser()).booleanValue();
//        String emailAttr = luntbuildAuthenticator.getLdapEmailAttrName();
//
//        // LDAP not specified use Luntbuild authentication
//        if (host.length() == 0 || userDN.length() == 0 || host.startsWith("${") || userDN.startsWith("${")) {
//            if (useLuntbuildOnFail &&
//                    userDetails != null && userDetails.getPassword().equals(password)) {
//                logger.info("Luntbuild User Authentication (user: " + name + ") SUCCESS\n");
//                return;
//            }
//            authentication.setAuthenticated(false);
//            logger.warn("User Authentication (user: " + name + ") FAILURE\n");
//            throw new AuthenticationCredentialsNotFoundException(
//                    "Cannot authenticate user " + name);
//        }
//        String ldapAuthentication = luntbuildAuthenticator.getLdapAuthentication();
//        String ldapUserId = luntbuildAuthenticator.getLdapUserId();
//        String ldapUrl = luntbuildAuthenticator.getLdapUrl();
//        String ldapPrefix = luntbuildAuthenticator.getLdapPrefix();
//        String ldapSuffix = luntbuildAuthenticator.getLdapSuffix();
//
//        // LDAP specified, use it
//        LDAPAuthenticator authenticator =
//            new LDAPAuthenticator(host, port, userDN, ldapAuthentication, ldapUserId);
//        if (ldapUrl != null) {
//            authenticator.setLdapUrl(ldapUrl);
//        }
//        if (ldapPrefix != null) {
//            authenticator.setLdapPrefix(ldapPrefix);
//        }
//        if (ldapSuffix != null) {
//            authenticator.setLdapSuffix(ldapSuffix);
//        }
//        if (authenticator.authenticate(name, password)){
//            // Load Luntbuild user if exist, or create new user
//            if (doCreateLuntbuildUser) {
//                if (!Luntbuild.getDao().isUserExist(name.toLowerCase())) {
//                    // create user
//                    User user = new User();
//                    user.setName(name.toLowerCase());
//                    user.setCanCreateProject(canCreateProject);
//                    user.setDecryptedPassword(password);
//                    // set email
//                    if (emailAttr != null && emailAttr.trim().length() > 0) {
//                        String email = authenticator.lookupEmail(name, password, emailAttr.trim());
//                        Map contacts = user.getContacts();
//                        List notifiers = Luntbuild.getNotifierInstances(Luntbuild.notifiers);
//                        EmailNotifier emailNotifier = null;
//                        for (Iterator iter = notifiers.iterator(); iter.hasNext();) {
//                            TemplatedNotifier cz = (TemplatedNotifier) iter.next();
//                            if (cz instanceof EmailNotifier) {
//                                emailNotifier = (EmailNotifier)cz;
//                                break;
//                            }
//                        }
//                        if (emailNotifier != null) {
//                            contacts.put(emailNotifier.getKey(), email);
//                            user.setContacts(contacts);
//                        }
//                    }
//
//                    Luntbuild.getDao().saveUserInternal(user);
//
//                    if (canCreateProject) setProjectsPrivileges(user, Role.LUNTBUILD_PRJ_ADMIN);
//                    if (canBuildProject) setProjectsPrivileges(user, Role.LUNTBUILD_PRJ_BUILDER);
//                    if (canViewProject) setProjectsPrivileges(user, Role.LUNTBUILD_PRJ_VIEWER);
//
//                    userDetails = this.authenticationDao.loadUserByUsername(name.toLowerCase());
//
//                    this.userAuth = userDetails;
//
//                } else {
//                    // Update password
//                    User user = Luntbuild.getDao().loadUser(name.toLowerCase());
//                    user.setDecryptedPassword(password);
//                }
//
//            } else {
//                this.userAuth =
//                    authorizeUser(name, password, canCreateProject, canBuildProject, canViewProject);
//            }
//            logger.info("LDAP User Authentication (user: " + name + ") SUCCESS\n");
//            return;
//        } else {
//            if (useLuntbuildOnFail &&
//                    userDetails != null && userDetails.getPassword().equals(password)) {
//                logger.info("Luntbuild User Authentication (user: " + name + ") SUCCESS\n");
//                return;
//            }
//            authentication.setAuthenticated(false);
//            logger.warn("User Authentication (user: " + name + ") FAILURE\n");
//            throw new AuthenticationCredentialsNotFoundException(
//                    "Cannot authenticate user " + name + " against LDAP");
//        }
    }

    protected final UserDetails retrieveUser(String username,
            UsernamePasswordAuthenticationToken authentication)
    		throws AuthenticationException {
        UserDetails loadedUser;

        try {
            loadedUser = this.authenticationDao.loadUserByUsername(username);
            if (loadedUser == null) {
                throw new AuthenticationServiceException(
                "AuthenticationDao returned null, which is an interface contract violation");
            }
            String password = loadedUser.getPassword();
            String credentials = (String)authentication.getCredentials();
            if (password == null && credentials == null) return loadedUser;
            if ((password == null && credentials != null) ||
            		(password != null && credentials == null))
            	return null;
            if (password.equals(credentials))
            	return loadedUser;
            else
            	return null;
        } catch (UsernameNotFoundException notFound) {
            return null;
        } catch (DataAccessException repositoryProblem) {
            throw new AuthenticationServiceException(repositoryProblem
                    .getMessage(), repositoryProblem);
        }
    }

    /**
     * @inheritDoc
     */
    public Authentication authenticate(Authentication authentication)
    		throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class,
                authentication,
        "Only UsernamePasswordAuthenticationToken is supported");

        // Determine username
        String username = (authentication.getPrincipal() == null)
        ? "NONE_PROVIDED" : authentication.getName();

        boolean cacheWasUsed = true;
        UserDetails user = this.userCache.getUserFromCache(username);

        if (user == null) {
            cacheWasUsed = false;
            user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);
        }

        // This check must come here, as we don't want to tell users
        // about account status unless they presented the correct credentials
//        try {
//            additionalAuthenticationChecks(user,
//                    (UsernamePasswordAuthenticationToken) authentication);
//        } catch (AuthenticationException exception) {
//            // There was a problem, so try again after checking we're using latest data
//            cacheWasUsed = false;
//            this.userAuth = null;
//            user = retrieveUser(username,
//                    (UsernamePasswordAuthenticationToken) authentication);
//            additionalAuthenticationChecks(user,
//                    (UsernamePasswordAuthenticationToken) authentication);
//        }
        if (user == null) user = this.userAuth;
        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("User account has expired");
        }

        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("User credentials have expired");
        }

        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }

        Object principalToReturn = user;

        if (this.forcePrincipalAsString) {
            principalToReturn = user.getUsername();
        }

        return createSuccessAuthentication(principalToReturn, authentication, user);
    }
}
