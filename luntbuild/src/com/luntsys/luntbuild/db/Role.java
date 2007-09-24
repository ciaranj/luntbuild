/*
 *
 */

package com.luntsys.luntbuild.db;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.facades.lb12.RoleFacade;

/**
 * A security role for <code>User</code>s in Luntbuild.
 * 
 * <p>This is a hibernate mapping class.</p>
 */
public class Role implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    /** identifier field */
    private long id;

    /** nullable persistent field */
    private String name;

    /** persistent field */
    private Set rolesMappings;

	/** Global role for authenticated users. */
	public final static String ROLE_AUTHENTICATED = "ROLE_AUTHENTICATED";
	/** Global role for site administrators. */
	public final static String ROLE_SITE_ADMIN = "ROLE_SITE_ADMIN";
	/** Global role for anonymous users. */
    public final static String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
	// per project roles
	/** Project role for users allowed to change the configuration of a project. */
    public final static String LUNTBUILD_PRJ_ADMIN = "LUNTBUILD_PRJ_ADMIN";
	/** Project role for users allowed to build a project. */
	public final static String LUNTBUILD_PRJ_BUILDER = "LUNTBUILD_PRJ_BUILDER";
	/** Project role for users allowed to view a project. */
	public final static String LUNTBUILD_PRJ_VIEWER = "LUNTBUILD_PRJ_VIEWER";
	private static List roles = null;

	/**
	 * Creates a role.
	 * 
	 * @param name the name of the role
	 * @param rolesMappings the list of roles mappings for the role
	 */
    public Role(String name, Set rolesMappings) {
        this.name = name;
        this.rolesMappings = rolesMappings;
    }

    /**
     * Creates a blank role.
     */
    public Role() {
    }

    /**
     * Creates a role.
     * 
     * @param rolesMappings the list of roles mappings for this role
     */
    public Role(Set rolesMappings) {
        this.rolesMappings = rolesMappings;
    }

	/**
	 * Gets the identifer of this role.
	 * 
	 * @return the identifer of this role
	 */
    public long getId() {
        return this.id;
    }

	/**
	 * Sets the identifier of this role, will be called by hibernate.
	 *
	 * @param id the identifier of this role
	 */
    public void setId(long id) {
        this.id = id;
    }

	/**
	 * Gets the name of this role.
	 * 
	 * @return the name of this role
	 */
    public String getName() {
        return this.name;
    }

	/**
	 * Sets the name of this role.
	 * 
	 * @param name the name of this role
	 */
    public void setName(String name) {
        this.name = name;
    }

	/**
	 * Gets the list of roles mappings of this role.
	 * 
	 * @return the list of roles mappings of this role
	 */
    public Set getRolesMappings() {
		if (rolesMappings == null)
			rolesMappings = new HashSet();
        return rolesMappings;
    }

	/**
	 * Sets the list of roles mappings of this role.
	 * 
	 * @param rolesMappings the list of roles mappings of this role
	 */
    public void setRolesMappings(Set rolesMappings) {
        this.rolesMappings = rolesMappings;
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
        if ( !(obj instanceof Role) ) return false;
        Role castOther = (Role) obj;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
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
            .append(getId())
            .toHashCode();
    }

	/**
     * Helper method to easily retrieve all existing roles from database.
     * 
     * @return the existing roles from database
     */
    public static List getRoles()
    {
        if ( roles == null)
        {
            roles = Luntbuild.getDao().loadRoles();
        }
        return roles;
    }

	/**
	 * Gets the facade of this role.
	 * 
	 * @return the facade of this role
	 */
	public RoleFacade getFacade() {
		RoleFacade facade = new RoleFacade();
		facade.setId(getId());
		facade.setName(getName());
		return facade;
	}

	/**
	 * Sets the facade of this role.
	 * 
	 * @param facade the role facade
	 */
	public void setFacade(RoleFacade facade) {
	}
}
