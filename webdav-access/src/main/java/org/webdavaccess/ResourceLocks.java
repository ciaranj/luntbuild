/*
 * Copyright 2005-2006 webdav-servlet group.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.webdavaccess;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * a very simple locking management for concurrent data access, NOT the
 * webdav locking.
 * <p>
 * The <code>ResourceLocks</code> are only used for a preliminary check, while
 * the <code>VirtualFileSystem</code> takes care of distributed locking.
 * </p>
 * <p>
 * The original class is <code>org.apache.catalina.servlets.WebdavServlet</code>
 * by the webdav-servlet project at Sourceforge (<a
 * href="http://webdav-servlet.sf.net">http://webdav-servlet.sf.net</a>).<br/>
 * The class was heavily refactored to meet the <em>pulse</em> coding
 * standards.
 * </p>
 * 
 * 
 * @author re
 * @author Thomas Weber
 * @version $Revision$
 */
public class ResourceLocks {

	private static Log log = LogFactory.getLog(ResourceLocks.class);

	/**
	 * after creating this much LockObjects, a cleanup deletes unused
	 * LockObjects.
	 */
	private final int fCleanupLimit = 100000;

	/**
	 * .
	 */
	private int fCleanupCounter = 0;

	/**
	 * keys: path value: LockObject from that path.
	 */
	private ConcurrentHashMap<String, LockObject> fLocks = null;

	// REMEMBER TO REMOVE UNUSED LOCKS FROM THE HASHTABLE AS WELL

	/**
	 * the root of the repository.
	 */
	private LockObject fRoot = null;

	/**
	 * constructor.
	 */
	public ResourceLocks() {
		this.fLocks = new ConcurrentHashMap<String, LockObject>();
		fRoot = new LockObject("/");
	}

	/**
	 * trys to lock the resource at "path".
	 * 
	 * 
	 * @param path
	 *            what resource to lock
	 * @param owner
	 *            the owner of the lock
	 * @param exclusive
	 *            if the lock should be exclusive (or shared)
	 * @param depth
	 *            the depth for lock checking
	 * @return true if the resource at path was successfully locked, false if an
	 *         existing lock prevented this
	 */
	public final synchronized boolean lock(final String path,
			final String owner, final boolean exclusive, final int depth) {

		LockObject lo = generateLockObjects(path);
		if (lo.checkLocks(exclusive, depth)) {
			if (lo.addLockObjectOwner(owner)) {
				lo.fExclusive = exclusive;
				return true;
			} else {
				log.warn("Cannot lock, lo.addLockObjectOwner() failed for owner " + owner + " and path:" + path);
				return false;
			}
		} else {
			// can not lock
			log.warn("Cannot lock, lo.checkLocks() failed for path: " + path);
			return false;
		}
	}

	/**
	 * unlocks all resources at "path" (and all sub folders if existing) that
	 * have the same owner.
	 * 
	 * @param path
	 *            what resource to unlock
	 * @param owner
	 *            who wants to unlock
	 */
	public final synchronized void unlock(final String path,
			final String owner) {
		if (this.fLocks.containsKey(path)) {
			LockObject lo = (LockObject) this.fLocks.get(path);
			lo.removeLockObjectOwner(owner);
			if (log.isDebugEnabled()) {
				log.debug("Number of LockObjects: " + fLocks.size());
			}
		} else {
			/*
			 * there is no lock at that path. someone tried to unlock it anyway.
			 * could point to a problem
			 */
			log.warn("no lock for path " + path);
		}

		if (fCleanupCounter > fCleanupLimit) {
			fCleanupCounter = 0;
			cleanLockObjects(fRoot);
		}
	}

	/**
	 * generates LockObjects for the resource at path and its parent folders.
	 * does not create new LockObjects if they already exist.
	 * 
	 * @param path
	 *            path to the (new) LockObject
	 * @return the LockObject for path.
	 */
	private LockObject generateLockObjects(final String path) {
		if (log.isDebugEnabled()) {
			log.debug("generating LockObject for path " + path);
		}
		if (!this.fLocks.containsKey(path)) {
			LockObject parentLockObject = generateLockObjects(getParentPath(path));
			LockObject returnObject = new LockObject(path);
			parentLockObject.addChild(returnObject);
			returnObject.fParent = parentLockObject;
			return returnObject;
		} else {
			return (LockObject) this.fLocks.get(path);
		}

	}

	/**
	 * deletes unused LockObjects and resets the counter.
	 * 
	 * @param lo
	 *            the <code>LockObject</code> to be cleaned
	 * @return <code>true</code>, if the operation was successful
	 */
	private boolean cleanLockObjects(final LockObject lo) {
		if (lo.fChildren == null) {
			if (lo.fOwner == null) {
				lo.removeLockObject();
				return true;
			} else {
				return false;
			}
		} else {
			boolean canDelete = true;
			int limit = lo.fChildren.length;
			for (int i = 0; i < limit; i++) {
				if (!cleanLockObjects(lo.fChildren[i])) {
					canDelete = false;
				} else {

					// because the deleting shifts the array
					i--;
					limit--;
				}
			}
			if (canDelete) {
				if (lo.fOwner == null) {
					lo.removeLockObject();
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	/**
	 * get the parent path of the given path.
	 * 
	 * @param path
	 *            the given path
	 * @return the parent path of the given path
	 */
	private String getParentPath(final String path) {
		int slash = path.lastIndexOf('/');
		if (slash == -1) {
			return null;
		} else {
			if (slash == 0) {
				// return "root" if parent path is empty string
				return "/";
			} else {
				return path.substring(0, slash);
			}
		}
	}

	// ----------------------------------------------------------------------------

	/**
	 * internal lock object.
	 */
	private class LockObject {

		/**
		 * the path for the <code>LockObject</code>.
		 */
		private String fPath;

		/**
		 * owner of the lock. shared locks can have multiple owners. is null if
		 * no owner is present
		 */
		private String[] fOwner = null;

		/**
		 * children of that lock.
		 */
		private LockObject[] fChildren = null;

		/**
		 * ???
		 */
		private LockObject fParent = null;

		/**
		 * weather the lock is exclusive or not. if owner=null the exclusive
		 * value doesn't matter
		 */
		private boolean fExclusive = false;

		/**
		 * constructs a <code>LockObject</code> for the given path.
		 * 
		 * @param path
		 *            the path to the locked object
		 */
		LockObject(final String path) {
			this.fPath = path;
			fLocks.put(path, this);

			fCleanupCounter++;
		}

		/**
		 * adds a new owner to a lock.
		 * 
		 * @param owner
		 *            string that represents the owner
		 * @return true if the owner was added, false otherwise
		 */
		boolean addLockObjectOwner(final String owner) {
			if (this.fOwner == null) {
				this.fOwner = new String[1];
			} else {

				int size = this.fOwner.length;
				String[] newLockObjectOwner = new String[size + 1];

				// check if the owner is already here (that should actually not
				// happen)
				for (int i = 0; i < size; i++) {
					if (this.fOwner[i].equals(owner)) {
						return !this.fExclusive;
					}
				}

				for (int i = 0; i < size; i++) {
					newLockObjectOwner[i] = this.fOwner[i];
				}
				this.fOwner = newLockObjectOwner;
			}

			this.fOwner[this.fOwner.length - 1] = owner;
			return true;
		}

		/**
		 * tries to remove the owner from the lock.
		 * 
		 * @param owner
		 *            string that represents the owner
		 */
		void removeLockObjectOwner(final String owner) {
			if (this.fOwner != null) {
				for (int i = 0; i < this.fOwner.length; i++) {
					// check every owner if it is the requested one
					if (this.fOwner[i].equals(owner)) {
						// remove the owner
						String[] newLockObjectOwner = new String[this.fOwner.length - 1];
						for (int i2 = 0; i2 < (this.fOwner.length - 1); i2++) {
							if (i2 < i) {
								newLockObjectOwner[i2] = this.fOwner[i2];
							} else {
								newLockObjectOwner[i2] = this.fOwner[i2 + 1];
							}
						}
						this.fOwner = newLockObjectOwner;

					}
				}
				if (this.fOwner.length == 0) {
					this.fOwner = null;
				}
			}
		}

		/**
		 * adds a child <code>LockObject</code>.
		 * 
		 * @param newChild
		 *            the <code>LockObject</code> to be added
		 */
		void addChild(final LockObject newChild) {
			if (this.fChildren == null) {
				this.fChildren = new LockObject[0];
			}
			int size = this.fChildren.length;
			LockObject[] newChildren = new LockObject[size + 1];
			for (int i = 0; i < size; i++) {
				newChildren[i] = this.fChildren[i];
			}
			newChildren[size] = newChild;
			this.fChildren = newChildren;
		}

		/**
		 * deletes this Lock object. assumes that it has no children and no
		 * owners (does not check this itself)
		 * 
		 */
		void removeLockObject() {
			if (this != fRoot) {
				// removing from tree
				int size = this.fParent.fChildren.length;
				for (int i = 0; i < size; i++) {
					if (this.fParent.fChildren[i].equals(this)) {
						LockObject[] newChildren = new LockObject[size - 1];
						for (int i2 = 0; i2 < (size - 1); i2++) {
							if (i2 < i) {
								newChildren[i2] = this.fParent.fChildren[i2];
							} else {
								newChildren[i2] = this.fParent.fChildren[i2 + 1];
							}
						}
						if (newChildren.length != 0) {
							this.fParent.fChildren = newChildren;
						} else {
							this.fParent.fChildren = null;
						}
						break;
					}
				}

				// removing from hashtable
				fLocks.remove(this.fPath);
				// now the garbage collector has some work to do
			}
		}

		/**
		 * 
		 * @param exclusive
		 *            wheather the new lock should be exclusive
		 * @param depth
		 *            the depth for child checks
		 * @return true if the lock can be placed
		 */
		boolean checkLocks(final boolean exclusive, final int depth) {
			return (checkParents(exclusive) && checkChildren(exclusive, depth));
		}

		/**
		 * 
		 * @param exclusive
		 *            wheather the new lock should be exclusive
		 * @return true if no locks at the parent path are forbidding a new lock
		 */
		private boolean checkParents(final boolean exclusive) {
			if (this.fPath.equals("/")) {
				return true;
			} else {

				if (this.fOwner == null) {
					// no owner, checking parents
					return this.fParent.checkParents(exclusive);
				} else {
					// there already is a owner
					if (this.fExclusive || exclusive) {
						// the new lock and/or the old lock are exclusive
						return false;
					} else {
						// new and old lock are shared
						return this.fParent.checkParents(exclusive);
					}
				}
			}
		}

		/**
		 * 
		 * @param exclusive
		 *            whether the new lock should be exclusive
		 * @param depth
		 *            the depth for the check
		 * @return true if no locks at the children paths are forbidding a new
		 *         lock
		 */
		private boolean checkChildren(final boolean exclusive, final int depth) {
			if (this.fChildren == null) {
				// a file
				if (this.fOwner == null) {
					// no owner
					return true;
				}
				return !(this.fExclusive || exclusive);
			}

			// a folder
			if (this.fOwner == null) {
				// no owner, checking children
				if (depth != 0) {
					boolean canLock = true;
					int limit = this.fChildren.length;
					for (int i = 0; i < limit; i++) {
						if (!this.fChildren[i].checkChildren(exclusive,
								depth - 1)) {
							canLock = false;
						}
					}
					return canLock;
				} else {
					// depth == 0 -> we don't care for children
					return true;
				}
			} else {
				return !(this.fExclusive || exclusive);
			}
		}
	}
}
