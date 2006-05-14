/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-4-26
 * Time: 11:28:17
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
package com.luntsys.luntbuild.vcs;

import com.luntsys.luntbuild.db.Snapshot;

import java.io.Serializable;

/**
 * version control system common interface. Different type of version control system such as cvs, vss should implement
 * this interface and will be serialized by hibernate.
 *
 * @author alvin shen
 */
public interface Vcs extends Serializable{
	/**
	 * @return a string value describes type of the version control system
	 */
	String getVcsDisplayName();

	/**
	 * @return list of property names of the version control system
	 */
	String[] getProperties();

	/**
	 * @param propertyName name of the property
	 * @return display name of the property
	 */
	String getPropertyDisplayName(String propertyName);

	/**
	 * To see if specified property should be treat as secret information.
	 * Secret property will be treated as password inputs at user interface
	 * @param propertyName
	 * @return
	 */
	boolean isPropertySecret(String propertyName);

	/**
	 * Is this property a optional property? Optional property means the input
	 * value for this property can be empty
	 *
	 * @param propertyName
	 * @return
	 */
	boolean isPropertyOptional(String propertyName);

	/**
	 * @param propertyName name of the property
	 * @return description of the property
	 */
	String getPropertyDescription(String propertyName);

	/**
	 * To login to the version control system
	 *
	 * @param logFile name of the file this method should APPEND its warning or error messages to.
	 */
	void login(String logFile);

	/**
	 * To logout from the version control system
	 *
	 * @param logFile name of the file this method should APPEND its warning or error messages to.
	 */
	void logout(String logFile);

	/**
	 * Retrieve an module with specified branch or label into specified blank directory.
	 * Sub class can decide to use branch or label if both of them are not empty
	 *
	 * @param workingDir the directory this module should be retrieved into
	 * @param module the module to be retrieved
	 * @param snapshot the snapshot of the module to be retrieved
	 * @param logFile name of the file this method should APPEND its warning or error messages to
	 */
	void retrieveModule(String workingDir, String module, Snapshot snapshot, String logFile);

	/**
	 * Labels source codes in specified directory with specified label
	 *
	 * @param workingDir the directory where source codes to be labeled resides
	 * @param module the module to label
	 * @param snapshot the snapshot for the module to label
	 * @param label the label to apply to this module
	 * @param logFile name of the file this method should APPEND its warning or error messages to.
	 */
	void labelModule(String workingDir, String module, Snapshot snapshot, String label, String logFile);

	/**
	 * Update specified module with specified snapshot in specified directory.
	 * Sub class can choose to use branch or label if both of them are not empty
	 *
	 * @param workingDir the directory this update should occurs in
	 * @param module the module to update
	 * @param snapshot the snapshot for the module to update
	 * @param logFile name of the file this method should APPEND its warning and error messages to
	 */
	void updateModule(String workingDir, String module, Snapshot snapshot, String logFile);
}
