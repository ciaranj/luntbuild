/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2004-12-17
 * Time: 12:40:22
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
 * Base facade class for builders
 * @author robin shine
 */
public abstract class BuilderFacade {
	private String name;
	private String environments;
	private String buildSuccessCondition;

	/**
	 * Get corresponding builders class name for this facade
	 * @return corresponding builders class name for this facade
	 */
	public abstract String getBuilderClassName();

	/**
	 * Get the build success condition for this builder
	 * @return build success condition for current builder
	 */
	public String getBuildSuccessCondition() {
		return buildSuccessCondition;
	}

	/**
	 * Set build success condition for this builder
	 * @param buildSuccessCondition
	 */
	public void setBuildSuccessCondition(String buildSuccessCondition) {
		this.buildSuccessCondition = buildSuccessCondition;
	}

	/**
	 * Get environments for this builder.
	 * @return environments for this builder
	 */
	public String getEnvironments() {
		return environments;
	}

	/**
	 * Set environments for this builder
	 * @param environments
	 */
	public void setEnvironments(String environments) {
		this.environments = environments;
	}

	/**
	 * Get name of the builder
	 * @return name of the builder
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name of the builder
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
