/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-8
 * Time: 20:56
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
package com.luntsys.luntbuild.web.selectionmodels;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.form.IPropertySelectionModel;

import com.luntsys.luntbuild.db.User;

/**
 * The selection model for users configured in the system,
 *
 * @author robin shine
 */
public class UserSelectionModel implements IPropertySelectionModel {
	private List users;

	public UserSelectionModel(List users){
		this.users = users;
	}

	public int getOptionCount() {
		return users.size();
	}

	public Object getOption(int index) {
		return users.get(index);
	}

	public String getLabel(int index) {
		return ((User)getOption(index)).getName();
	}

	public String getValue(int index) {
		return String.valueOf(((User)getOption(index)).getId());
	}

	public Object translateValue(String value) {
		long userId = new Integer(value).longValue();
		Iterator itUser = users.iterator();
	 	while (itUser.hasNext()) {
			 User user = (User) itUser.next();
			 if (user.getId() == userId){
				 return user;
			 }
		 }
		throw new ApplicationRuntimeException("Selected user has been removed already, " +
				"please select another user!");
	}
}
