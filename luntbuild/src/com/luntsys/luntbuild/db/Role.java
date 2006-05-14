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
 *     
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

	//  global roles
	public final static String ROLE_AUTHENTICATED = "ROLE_AUTHENTICATED";
	public final static String ROLE_SITE_ADMIN = "ROLE_SITE_ADMIN";
	// per project roles
    public final static String LUNTBUILD_PRJ_ADMIN = "LUNTBUILD_PRJ_ADMIN";
	public final static String LUNTBUILD_PRJ_BUILDER = "LUNTBUILD_PRJ_BUILDER";
	public final static String LUNTBUILD_PRJ_VIEWER = "LUNTBUILD_PRJ_VIEWER";
	private static List roles = null;

	/** full constructor */
    public Role(String name, Set rolesMappings) {
        this.name = name;
        this.rolesMappings = rolesMappings;
    }

    /** default constructor */
    public Role() {
    }

    /** minimal constructor */
    public Role(Set rolesMappings) {
        this.rolesMappings = rolesMappings;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set getRolesMappings() {
		if (rolesMappings == null)
			rolesMappings = new HashSet();
        return rolesMappings;
    }

    public void setRolesMappings(Set rolesMappings) {
        this.rolesMappings = rolesMappings;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof Role) ) return false;
        Role castOther = (Role) other;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId())
            .toHashCode();
    }

	/**
     * Helper method to easily retrieve all existing roles from db
     * @return
     */
    public static List getRoles()
    {
        if ( roles == null)
        {
            roles = Luntbuild.getDao().loadRoles();
        }
        return roles;
    }

	public RoleFacade getFacade() {
		RoleFacade facade = new RoleFacade();
		facade.setId(getId());
		facade.setName(getName());
		return facade;
	}

	public void setFacade(RoleFacade facade) {
	}
}
