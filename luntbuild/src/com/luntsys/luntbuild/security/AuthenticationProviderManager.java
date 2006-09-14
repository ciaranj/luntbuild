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

import org.acegisecurity.AbstractAuthenticationManager;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.ProviderNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Iterator;
import java.util.List;


/**
 * This class integrates into the acegi security framework 
 * 
 * A AuthenticationProviderManager which has to be defined through the springframework
 * configiguration handles incoming authentication requests and
 * tries to find a matching AuthenticationProvider.
 * 
 * A 'matching' Provider is one that accepts the specific Athentication objects
 * and is able to authenticate.
 * 
 * This implementation tries ALL available Providers in case one cannot authenticate 
 * although registered as capable.
 * 
 * This gives us the oportunity to either define users & and their roles declarative
 * through springframework configiguration
 * and
 * at runtime through or db backed application
 *  
 * @author johannes plachy
 */

public class AuthenticationProviderManager extends AbstractAuthenticationManager implements InitializingBean
{

    private static transient final Log logger = LogFactory.getLog(AuthenticationProviderManager.class);

    // ~ Instance fields
    // ========================================================

    private List providers;

    /**
     * Attempts to authenticate the passed {@link Authentication}object.
     * 
     * <p>
     * The list of {@link AuthenticationProvider}s will be successively tried
     * if an <code>AuthenticationProvider</code> indicates it is capable of
     * authenticating the type of <code>Authentication</code> object passed.
     * Authentication will then be attempted with that
     * <code>AuthenticationProvider</code>.
     * </p>
     * 
     * <p>
     * If more than one <code>AuthenticationProvider</code> supports the
     * passed <code>Authentication</code> object, all
     * <code>AuthenticationProvider</code> will be tried, as long as no valid
     * <code>Authentication</code> object is returned
     * </p>
     * 
     * @param authentication the authentication request object.
     * 
     * @return a fully authenticated object including credentials.
     * 
     * @throws AuthenticationException if authentication fails.
     * @throws ProviderNotFoundException is thrown if no
     *             <code>AuthenticationProvider</code> is found that supoprts
     *             the given <code>Authentication</code> object
     * 
     */
    protected Authentication doAuthentication(Authentication authentication) throws AuthenticationException
    {
        Authentication result = null;
        boolean foundProvider = false;

        Iterator iter = getProviders().iterator();

        Class toTest = authentication.getClass();

        while ((iter.hasNext()) && (result == null))
        {
            AuthenticationProvider provider = (AuthenticationProvider) iter.next();

            if (provider.supports(toTest))
            {
                foundProvider = true;

                try
                {
                    result = provider.authenticate(authentication);
                }
                catch ( AuthenticationException ae)
                {
                }                
            }
        }

        if (result != null)
        {
            // return initialized authenticationTokens
            return result;
        }
        else
        {
            // throw exeption if authentication fails
            if (foundProvider == false)
            {
                throw new ProviderNotFoundException("No authentication provider for " + toTest.getName());
            }
            else
            {
                throw new ApplicationAuthenticationException("no provider was able to authenticate");
            }
        }
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception
    {
        checkIfValidList(this.providers);

    }

    private void checkIfValidList(List listToCheck)
    {
        if ((listToCheck == null) || (listToCheck.size() == 0)) { throw new IllegalArgumentException(
                "A list of AuthenticationManagers is required"); }
    }

    public List getProviders()
    {
        return this.providers;
    }

    /**
     * Sets the {@link AuthenticationProvider}objects to be used for
     * authentication.
     * 
     * @param newList
     * 
     * @throws IllegalArgumentException if Provider doesnt implement
     *             <code>AuthenticationProvider</code>
     */
    public void setProviders(List newList)
    {
        checkIfValidList(newList);

        Iterator iter = newList.iterator();

        while (iter.hasNext())
        {
            Object currentObject = null;

            try
            {
                currentObject = iter.next();

                AuthenticationProvider attemptToCast = (AuthenticationProvider) currentObject;
            }
            catch (ClassCastException cce)
            {
                throw new IllegalArgumentException("AuthenticationProvider " + currentObject.getClass().getName()
                        + " must implement AuthenticationProvider");
            }
        }

        this.providers = newList;
    }
}
