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

package com.luntsys.luntbuild;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.luntsys.luntbuild.utility.Luntbuild;

/**
 * Application specific servlet lifecycle listener needed for acegi security framework integration.
 * Used to handle application initialization and finalization.
 * 
 * @author johannes plachy
 */
public class LuntbuildContextListener implements ServletContextListener
{

	/**
     * Used to initialize Luntbuild application.
     * 
     * @param contextEvent the servlet initialization event
     */
    public void contextInitialized(ServletContextEvent contextEvent)
    {
        // logging might not me initialized...
        System.out.println(contextEvent.getServletContext().getServletContextName() + " : --> context initialization started");

        Luntbuild.initApplication(contextEvent.getServletContext());
        
        System.out.println(contextEvent.getServletContext().getServletContextName() + " : --> context initialization finished");
    }

    /**
     * Used to clean webtier-shutdown.
     * 
     * @param contextEvent the servlet shutdown event
     */
    public void contextDestroyed(ServletContextEvent contextEvent)
    {
        System.out.println(contextEvent.getServletContext().getServletContextName() + " : --> context shutdown started");

        Luntbuild.destroyApplication(contextEvent.getServletContext());
        
        System.out.println(contextEvent.getServletContext().getServletContextName() + " : --> context shutdown finished");
    }
}
