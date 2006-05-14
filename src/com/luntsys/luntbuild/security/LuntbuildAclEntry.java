package com.luntsys.luntbuild.security;

import net.sf.acegisecurity.acl.basic.AbstractBasicAclEntry;
import net.sf.acegisecurity.acl.basic.AclObjectIdentity;

/**
 * luntbuild specific acl entry
 * @author alvin shen
 */
public class LuntbuildAclEntry extends AbstractBasicAclEntry {
     // Base permissions we permit
     public static final int NOTHING = 0;
     public static final int PROJECT_READ = (int) Math.pow(2, 0);
     public static final int PROJECT_BUILD = (int) Math.pow(2, 1);
     public static final int PROJECT_ADMIN = (int) Math.pow(2, 2);

     // Array required by the abstract superclass via getValidPermissions()
     private static final int[] validPermissions = {NOTHING, PROJECT_READ, PROJECT_BUILD, PROJECT_ADMIN};

     public LuntbuildAclEntry() {
         super();
     }

     public LuntbuildAclEntry(Object recipient,
         AclObjectIdentity aclObjectIdentity,
         AclObjectIdentity aclObjectParentIdentity, int mask) {
         super(recipient, aclObjectIdentity, aclObjectParentIdentity, mask);
     }

     public int[] getValidPermissions() {
         return validPermissions;
     }

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
