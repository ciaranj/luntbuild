package com.luntsys.luntbuild.db;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** 
 *        @hibernate.class
 *         table="ROLE"
 *         mutable="false"
 *     
*/
public class Role implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3616729387425675314L;

    /** identifier field */
    private long id;

    /** nullable persistent field */
    private String name;

    /** persistent field */
    private Set rolesMappings;

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
     *            @hibernate.property
     *             column="NAME"
     *             length="255"
     *         
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** 
     *            @hibernate.set
     *             lazy="true"
     *             inverse="true"
     *             cascade="delete"
     *            @hibernate.collection-key
     *             column="FK_ROLE_ID"
     *            @hibernate.collection-one-to-many
     *             class="com.luntsys.luntbuild.db.RolesMapping"
     *         
     */
    public Set getRolesMappings() {
        return this.rolesMappings;
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

}
