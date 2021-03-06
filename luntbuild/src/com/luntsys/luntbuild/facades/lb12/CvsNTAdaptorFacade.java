/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-20
 * Time: 19:57:43
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

package com.luntsys.luntbuild.facades.lb12;

/**
 * CVS VCS adaptor facade.
 *
 * @author robin shine
 * @see com.luntsys.luntbuild.vcs.CvsAdaptor
 */
public class CvsNTAdaptorFacade extends CvsAdaptorFacade {
  private boolean cvsNTAudit = false;
  private String auditURL = "";
  private String auditPwd = "";
  private String auditusr = "";
  private String auditdrv = "";
    /**
     * Gets the corresponding VCS adaptor class name.
     *
     * @return the VCS adaptor class name
     */
  public String getVcsClassName() {
    return "com.luntsys.luntbuild.vcs.CvsNTAdaptor";
  }

  public String getAuditdrv()
  {
    return auditdrv;
  }

  public String getAuditPwd()
  {
    return auditPwd;
  }

  public String getAuditURL()
  {
    return auditURL;
  }

  public String getAuditusr()
  {
    return auditusr;
  }

  public boolean isCvsNTAudit()
  {
    return cvsNTAudit;
  }

  public void setAuditdrv(String auditdrv)
  {
    this.auditdrv = auditdrv;
  }

  public void setAuditPwd(String auditPwd)
  {
    this.auditPwd = auditPwd;
  }

  public void setAuditURL(String auditURL)
  {
    this.auditURL = auditURL;
  }

  public void setAuditusr(String auditusr)
  {
    this.auditusr = auditusr;
  }

  public void setCvsNTAudit(boolean cvsNTAudit)
  {
    this.cvsNTAudit = cvsNTAudit;
  }
}
