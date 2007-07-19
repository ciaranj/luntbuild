package com.luntsys.luntbuild.security;

import org.acegisecurity.acl.basic.AbstractBasicAclEntry;
import org.acegisecurity.acl.basic.AclObjectIdentity;

/**
 * Luntbuild specific ACL entry.
 * 
 * @author alvin shen
 */
public class LuntbuildAclEntry extends AbstractBasicAclEntry {
    // Base permissions we permit
	/** Permission, none */
    public static final int NOTHING = 0;
	/** Permission, project read access */
    public static final int PROJECT_READ = (int) Math.pow(2, 0);
	/** Permission, project build access */
    public static final int PROJECT_BUILD = (int) Math.pow(2, 1);
	/** Permission, project administration access */
    public static final int PROJECT_ADMIN = (int) Math.pow(2, 2);

    // Array required by the abstract superclass via getValidPermissions()
    private static final int[] validPermissions = {NOTHING, PROJECT_READ, PROJECT_BUILD, PROJECT_ADMIN};

    /**
     * Creates a blank Luntbuild ACL entry.
     */
    public LuntbuildAclEntry() {
        super();
    }

    /**
     * Creates a Luntbuild ACL entry.
     * 
     * @param recipient
     * @param aclObjectIdentity
     * @param aclObjectParentIdentity
     * @param mask
     */
    public LuntbuildAclEntry(Object recipient, AclObjectIdentity aclObjectIdentity,
        AclObjectIdentity aclObjectParentIdentity, int mask) {
        super(recipient, aclObjectIdentity, aclObjectParentIdentity, mask);
    }

	/**
	 * @inheritDoc
	 */
    public int[] getValidPermissions() {
        return validPermissions;
    }

	/**
	 * @inheritDoc
	 */
    public String printPermissionsBlock(int i) {
    	StringBuffer sb = new StringBuffer();
        if (isPermitted(i, PROJECT_READ)) {
        	sb.append('R');
        } else {
        	sb.append('-');
        }

        if (isPermitted(i, PROJECT_BUILD)) {
			 sb.append('B');
		} else {
			 sb.append('-');
		}

		if (isPermitted(i, PROJECT_ADMIN)) {
			sb.append('A');
		} else {
			sb.append('-');
		}
        return sb.toString();
    }
 }
