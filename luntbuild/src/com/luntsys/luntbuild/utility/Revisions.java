/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-8-12
 * Time: 11:17:45
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
package com.luntsys.luntbuild.utility;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * This class manages revisions(or change log) for various vcs
 *
 * @author robin shine
 */
public class Revisions {
	/**
	 * Defines maximum change log entries suggest for retrieve
	 */
	public static final long MAX_ENTRIES = 1000;
	/**
	 * list of raw change log lines retrieved from various vcs
	 */
	private List changeLogs = new ArrayList();

	/**
	 * set of vcs logins who has changed the vcs content since last build
	 */
	private Set changeLogins = new HashSet();

	/**
	 * Is there any file or directory modification, add or delete action inside this revisions?
	 */
	private boolean fileModified = false;

	public List getChangeLogs() {
		return changeLogs;
	}

	public Set getChangeLogins() {
		return changeLogins;
	}

	public boolean isFileModified() {
		return fileModified;
	}

	public void setFileModified(boolean fileModified) {
		this.fileModified = fileModified;
	}

	public void initialize() {
		changeLogs.clear();
		changeLogins.clear();
		fileModified = false;
	}
}
