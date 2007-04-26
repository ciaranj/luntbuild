package com.luntsys.luntbuild.facades.lb20;

import java.util.Map;

public abstract class BuilderFacade {
	private String name;
	private String environments;
	private String buildSuccessCondition;

	/**
	 * Get corresponding builders class name for this facade
	 * @return corresponding builders class name for this facade
	 */
	public abstract String getBuilderClassName();

    public abstract void setProperties(Map m);

    public abstract Map getProperties();

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
