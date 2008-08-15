/*
 * Copyright  2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
/*
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */

package com.luntsys.luntbuild.ant.perforce;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 *  Creates a new perforce client if the specified client name does not
 *  exists. Otherwise, edit the existing client
 *
 * Example Usage:
 * <pre>
 *   &lt;P4Client clientValue="MyClient" rootValue="d:\myworkspace" view="//depot1/... //MyClient/depot1/...://depot2/... //MyClient/depot2/..."/&gt;
 * </pre>
 * For more information, pls. refer to perforce client specification.
 *
 * @author <A HREF="mailto:yjshen@yahoo.com">Alvin Shen</A>
 *
 * @ant.task category="scm"
 */
public class P4Client extends P4Base {

    protected String clientValue;
	protected String ownerValue;
	protected String hostValue;
    protected String descValue;
    protected String rootValue;
	protected String optionsValue;
	protected String lineEndValue;
	protected String viewValue;

	public String getClientValue() {
		return clientValue;
	}

	/**
	 * Set the client name for this client, optional,
	 * If this value is not set, A new view will be create using
	 * host name of this machine. Otherwise view will be created
	 * or edited based on if name of this client exists or not.
	 * @param clientValue
	 */
	public void setClientValue(String clientValue) {
		this.clientValue = clientValue;
	}

	public String getOwnerValue() {
		return ownerValue;
	}

	/**
	 * Set the owner for this client, optional.
	 * If this value is not set, the owner will be:
	 * <i> unchanged when editing existing client
	 * <i> current perforce user when creating new client.
	 * @param ownerValue
	 */
	public void setOwnerValue(String ownerValue) {
		this.ownerValue = ownerValue;
	}

	public String getHostValue() {
		return hostValue;
	}

	/**
	 * Set the host for this client, optional.
	 * If this value is not set, the host will be:
	 * <i> unchanged when editing existing client
	 * <i> current host name when creating new client
	 * @param hostValue
	 */
	public void setHostValue(String hostValue) {
		this.hostValue = hostValue;
	}

	public String getDescValue() {
		return descValue;
	}

	/**
	 * Set the description for this client, optional.
	 * If this value is not set, the description will be:
	 * <i> unchanged when editing existing client
	 * <i> "Created by XXX" when creating new client, where XXX stands for current user
	 * @param descValue
	 */
	public void setDescValue(String descValue) {
		this.descValue = descValue;
	}

	public String getRootValue() {
		return rootValue;
	}

	/**
	 * Set the root directory for this client, optional.
	 * If this value is not set, the root directory will be:
	 * <i> unchanged when editing existing client
	 * <i> current working directory when creating new client.
	 * @param rootValue
	 */
	public void setRootValue(String rootValue) {
		this.rootValue = rootValue;
	}

	public String getOptionsValue() {
		return optionsValue;
	}

	/**
	 * Set the options for this client, optional.
	 * If this value is not set, the options will be:
	 * <i> unchanged when editing existing client
	 * <i> default value set by perforce when creating new client
	 * @param optionsValue
	 */
	public void setOptionsValue(String optionsValue) {
		this.optionsValue = optionsValue;
	}

	public String getLineEndValue() {
		return lineEndValue;
	}

	/**
	 * Set the line end for this client, optional:
	 * If this value is not set, the options will be:
	 * <i> unchanged when editing existing client
	 * <i> default value set by perforce when creating new client
	 * @param lineEndValue
	 */
	public void setLineEndValue(String lineEndValue) {
		this.lineEndValue = lineEndValue;
	}

	public String getViewValue() {
		return viewValue;
	}

	/**
	 * Set the view for this client, optional:
	 * If this value is not set, the options will be:
	 * <i> unchanged when editing existing client
	 * <i> default value set by perforce when creating new client
	 * Please use "\n" to seperate multiple mappings for this view
	 * @param viewValue
	 */
	public void setViewValue(String viewValue) {
		this.viewValue = viewValue;
	}

    /**
     * do the work
     * @throws BuildException if failonerror has been set to true and Perforce fails
     */
    public void execute() throws BuildException {
        log("P4Client exec:", Project.MSG_INFO);

		final StringBuffer clientSpec = new StringBuffer();
		final boolean[] descBlock = new boolean[] {false};
		final boolean[] viewBlock = new boolean[] {false};
		P4Handler handler = new P4HandlerAdapter() {
			public void processStdout(String line) {
				log(line, Project.MSG_INFO);
				boolean endBlock = !util.match("/^\\s/", line);
				boolean endDescBlock = false;
				boolean endViewBlock = false;
				if (descBlock[0] && endBlock){
					descBlock[0] = false;
					endDescBlock = true;
				}
				if (viewBlock[0] && endBlock){
					viewBlock[0] = false;
					endViewBlock = true;
				}
				if (util.match("/^Owner:/", line)) {
					if (ownerValue != null)
						line = "Owner:	" + ownerValue;
				} else if (util.match("/^Host:/", line)) {
					if (hostValue != null)
						line = "Host:	" + hostValue;
				} else if (util.match("/^Root:/", line)) {
					if (rootValue != null)
						line = "Root:	" + rootValue;
				} else if (util.match("/^Options:/", line)){
					if (optionsValue != null)
						line = "Options:	" + optionsValue;
				} else if (util.match("/^LineEnd:/", line)){
					if (lineEndValue != null)
						line = "LineEnd:	" + lineEndValue;
				} else if (util.match("/^Description:/", line)){
					descBlock[0] = true;
				} else if (util.match("/^View:/", line)){
					viewBlock[0] = true;
				}

				if (endDescBlock){
					if (descValue != null)
						clientSpec.append("Description:	" + descValue.replaceAll("\n", "\n\t") + "\n");
				} else if (endViewBlock){
					if (viewValue != null)
						clientSpec.append("View: " + viewValue.replaceAll("\n", "\n\t") + "\n");
				}
				if (descBlock[0]){
					if (descValue == null)
						clientSpec.append(line + "\n");
				} else if (viewBlock[0]){
					if (viewValue == null)
						clientSpec.append(line + "\n");
				} else if (!util.match("/^End/", line))
					clientSpec.append(line + "\n");
			}

			public void processStderr(String line){
				log(line, Project.MSG_ERR);
			}
		};

		log("Read client spec output from perforce", Project.MSG_INFO);
		if (clientValue != null){
			execP4Command("client -o " + clientValue, handler);
		} else {
			execP4Command("client -o", handler);
		}
		// feed an explicit end
		handler.processStdout("End.");

		log("Submit modified client spec back to perforce", Project.MSG_INFO);
        log("Modified client spec begin:\n" + clientSpec.toString(), Project.MSG_VERBOSE);
		log("Modified client spec end.", Project.MSG_VERBOSE);

        handler = new P4HandlerAdapter() {
			public void processStdout(String line) {
				log(line, Project.MSG_INFO);
			}

			public void processStderr(String line) {
				log(line, Project.MSG_ERR);
			}
		};

        handler.setOutput(clientSpec.toString());
        execP4Command("client -i", handler);
    }
}
