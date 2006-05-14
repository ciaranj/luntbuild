package com.luntsys.luntbuild.db;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.luntsys.luntbuild.facades.lb12.RolesMappingFacade;

public class RolesMapping {
    /** identifier field */
    private long id;

    /** persistent field */
    private User user;

    /** persistent field */
    private Project project;

    /** persistent field */
    private Role role;

    /** full constructor */
    public RolesMapping(User user, Project project, Role role) {
        this.user = user;
        this.project = project;
        this.role = role;
        this.id = 0;
    }

    /** default constructor */
    public RolesMapping() {
        this.id = 0;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof RolesMapping) ) return false;
        RolesMapping castOther = (RolesMapping) other;
        return new EqualsBuilder()
            .append(getProject().getId(), castOther.getProject().getId())
			.append(getUser().getId(), castOther.getUser().getId())
			.append(getRole().getId(), castOther.getRole().getId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getProject().getId())
			.append(getUser().getId())
			.append(getRole().getId())
			.toHashCode();
    }

	public RolesMappingFacade getFacade() {
		RolesMappingFacade facade = new com.luntsys.luntbuild.facades.lb12.RolesMappingFacade();
		facade.setId(getId());
		facade.setProjectId(getProject().getId());
		facade.setRoleId(getRole().getId());
		facade.setUserId(getUser().getId());
		return facade;
	}
}
