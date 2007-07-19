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
 * Base class for all builder facades.
 * 
 * @author robin shine
 * @see com.luntsys.luntbuild.builders.Builder
 */
public abstract class BuilderFacade {
	private String name;
	private String environments;
	private String buildSuccessCondition;

    /**
     * Gets the corresponding builder class name.
     *
     * @return the builder class name
     */
	public abstract String getBuilderClassName();

    /**
     * Gets the build success condition for this builder.
     *
     * @return the build success condition
     */
	public String getBuildSuccessCondition() {
		return buildSuccessCondition;
	}

    /**
     * Sets the build success condition for this builder.
     *
     * @param buildSuccessCondition the build success condition
     */
	public void setBuildSuccessCondition(String buildSuccessCondition) {
		this.buildSuccessCondition = buildSuccessCondition;
	}

    /**
     * Gets the environment settings for this builder.
     * 
     * @return the environment settings
     */
	public String getEnvironments() {
		return environments;
	}

    /**
     * Sets the environment settings for this builder.
     * 
     * @param environments the environment settings
     */
	public void setEnvironments(String environments) {
		this.environments = environments;
	}

    /**
     * Gets the name of this builder.
     * 
     * @return the name
     */
	public String getName() {
		return name;
	}

    /**
     * Sets the name of this builder.
     * 
     * @param name the name
     */
	public void setName(String name) {
		this.name = name;
	}
}
