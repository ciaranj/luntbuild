/*
 *
 */

package com.luntsys.luntbuild.db;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.luntsys.luntbuild.facades.lb12.RolesMappingFacade;

/**
 * Represents the <code>User</code> to <code>Project</code> to <code>Role</code> mapping.
 * 
 * <p>This is a hibernate mapping class.</p>
 * 
 * @see User
 * @see Project
 * @see Role
 */
public class RolesMapping {
    /** identifier field */
    private long id;

    /** persistent field */
    private User user;

    /** persistent field */
    private Project project;

    /** persistent field */
    private Role role;

    /**
     * Creates a new roles mapping.
     * 
     * @param user the user
     * @param project the project
     * @param role the role
     */
    public RolesMapping(User user, Project project, Role role) {
        this.user = user;
        this.project = project;
        this.role = role;
        this.id = 0;
    }

    /**
     * Creates a blank roles mapping.
     */
    public RolesMapping() {
        this.id = 0;
    }

	/**
	 * Gets the identifer of this roles mapping.
	 * 
	 * @return the identifer of this roles mapping
	 */
    public long getId() {
        return this.id;
    }

	/**
	 * Sets the identifier of this roles mapping, will be called by hibernate.
	 *
	 * @param id the identifier of this roles mapping
	 */
    public void setId(long id) {
        this.id = id;
    }

	/**
	 * Gets the user of this roles mapping.
	 * 
	 * @return the user of this roles mapping
	 */
    public User getUser() {
        return this.user;
    }

	/**
	 * Sets the user of this roles mapping.
	 * 
	 * @param user the user of this roles mapping
	 */
    public void setUser(User user) {
        this.user = user;
    }

	/**
	 * Gets the project of this roles mapping.
	 * 
	 * @return the project of this roles mapping
	 */
    public Project getProject() {
        return this.project;
    }

	/**
	 * Sets the project of this roles mapping.
	 * 
	 * @param project the project of this roles mapping
	 */
    public void setProject(Project project) {
        this.project = project;
    }

	/**
	 * Gets the role of this roles mapping.
	 * 
	 * @return the role of this roles mapping
	 */
    public Role getRole() {
        return this.role;
    }

	/**
	 * Sets the role of this roles mapping.
	 * 
	 * @param role the role of this roles mapping
	 */
    public void setRole(Role role) {
        this.role = role;
    }

	/**
	 * Returns a string representation of this object.
	 * 
	 * @return a string representation of this object
	 */
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .toString();
    }

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise
	 */
    public boolean equals(Object obj) {
        if ( !(obj instanceof RolesMapping) ) return false;
        RolesMapping castOther = (RolesMapping) obj;
        return new EqualsBuilder()
            .append(getProject().getId(), castOther.getProject().getId())
			.append(getUser().getId(), castOther.getUser().getId())
			.append(getRole().getId(), castOther.getRole().getId())
            .isEquals();
    }

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return a hash code value for this object
	 * @see #equals(Object)
	 */
    public int hashCode() {
        return new HashCodeBuilder()
            .append(getProject().getId())
			.append(getUser().getId())
			.append(getRole().getId())
			.toHashCode();
    }

	/**
	 * Gets the facade of this roles mapping.
	 * 
	 * @return the facade of this roles mapping
	 */
	public RolesMappingFacade getFacade() {
		RolesMappingFacade facade = new RolesMappingFacade();
		facade.setId(getId());
		facade.setProjectId(getProject().getId());
		facade.setRoleId(getRole().getId());
		facade.setUserId(getUser().getId());
		return facade;
	}
}
