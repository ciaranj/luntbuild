package com.luntsys.luntbuild.db;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** 
 *        @hibernate.class
 *         table="ROLES_MAPPING"
 *     
*/
public class RolesMapping implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 4049637910313842738L;

    /** identifier field */
    private long id;

    /** persistent field */
    private com.luntsys.luntbuild.db.User user;

    /** persistent field */
    private com.luntsys.luntbuild.db.Project project;

    /** persistent field */
    private com.luntsys.luntbuild.db.Role role;

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

    /** 
     *            @hibernate.id
     *             generator-class="increment"
     *             type="long"
     *             column="ID"
     *         
     */
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /** 
     *            @hibernate.many-to-one
     *             not-null="true"
     *            @hibernate.column name="FK_USER_ID"         
     *         
     */
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /** 
     *            @hibernate.many-to-one
     *             not-null="true"
     *            @hibernate.column name="FK_PROJECT_ID"         
     *         
     */
    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    /** 
     *            @hibernate.many-to-one
     *             not-null="true"
     *            @hibernate.column name="FK_ROLE_ID"         
     *         
     */
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
            .append(this.getId(), castOther.getId()).append(this.getRole().getId(), castOther.getRole().getId()).append(this.getProject().getId(), castOther.getProject().getId()).append(this.getUser().getId(), castOther.getUser().getId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId()).append(getRole().getId()).append(getProject().getId()).append(getUser().getId())
            .toHashCode();
    }

}
