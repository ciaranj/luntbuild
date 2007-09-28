/**
 * 
 */
package org.webdavaccess;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.webdavaccess.exceptions.CacheException;

/**
 * @author lubosp
 *
 */
public class MRUCache implements ICache {

	/** Cached object hashtable */
	Hashtable mHash = new Hashtable();

	/**
	 * Maximum number of objects in the cache.
	 */
	int mMaxCapacity;

	/**
	 * Oldest element.
	 */
	CacheEntry mFirstElement;

	/**
	 * Most recently used element.
	 */
	CacheEntry mLastElement;


	/**
	 * Construct an MRU with a given maximum number of objects.
	 */
	public MRUCache(int max) {
		if (max <= 0) {
			throw new IllegalArgumentException("MRU cache must contain at least one entry");
		}
		mMaxCapacity = max;
	}


	/* (non-Javadoc)
	 * @see org.webdavaccess.Cache#put(java.lang.Object, java.lang.Object)
	 */
	public void put(Object key, Object value) throws CacheException {
		CacheEntry entry = (CacheEntry)mHash.get(key);
		if (entry != null) {
			entry.setValue(value);
			touchEntry(entry);
		} else {

			if (mHash.size() == mMaxCapacity) {
				// purge and recycle entry
				entry = purgeEntry();
				entry.setKey(key);
				entry.setValue(value);
			} else {
				entry = new CacheEntry(key, value);
			}
			addEntry(entry);
			mHash.put(entry.getKey(), entry);
		}
	}


	/* (non-Javadoc)
	 * @see org.webdavaccess.Cache#get(java.lang.Object)
	 */
	public Object get(Object key) {
		CacheEntry entry = (CacheEntry)mHash.get(key);
		if (entry != null) {
			touchEntry(entry);
			return entry.getValue();
		} else {
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see org.webdavaccess.Cache#remove(java.lang.Object)
	 */
	public void remove(Object key) {
		CacheEntry entry = (CacheEntry)mHash.get(key);
		if (entry != null) {
			removeEntry(entry);
			mHash.remove(entry.getKey());
		}
	}


	/* (non-Javadoc)
	 * @see org.webdavaccess.Cache#removeAll()
	 */
	public void removeAll() {
		mHash = new Hashtable();
		mFirstElement = null;
		mLastElement = null;
	}


	/* (non-Javadoc)
	 * @see org.webdavaccess.Cache#elements()
	 */
	public Enumeration elements() {
		return new MRUEnumeration(mHash.elements());
	}

	/**
	 * Add a CacheEntry.  Entry goes at the end of the list.
	 */
	protected void addEntry(CacheEntry entry) {
		if (mFirstElement == null) {
			mFirstElement = entry;
			mLastElement = entry;
		} else {
			mLastElement.setNext(entry);
			entry.setPrevious(mLastElement);
			mLastElement = entry;
		}
	}


	/**
	 * Remove a CacheEntry from linked list
	 */
	protected void removeEntry(CacheEntry entry) {
		if (entry == mFirstElement) {
			mFirstElement = entry.getNext();
		}
		if (mLastElement == entry) {
			mLastElement = entry.getPrevious();
		}
		CacheEntry previous = entry.getPrevious();
		CacheEntry next = entry.getNext();
		if (previous != null) {
			previous.setNext(next);
		}
		if (next != null) {
			next.setPrevious(previous);
		}
		entry.setPrevious(null);
		entry.setNext(null);
	}

	/**
	 * Place entry at the end of linked list -- Most Recently Used
	 */
	protected void touchEntry(CacheEntry entry) {
		if (mLastElement == entry) {
			return;
		}
		removeEntry(entry);
		addEntry(entry);
	}

	/**
	 * Purge least recently used object from the cache
	 *
	 * @return recyclable CacheEntry
	 */
	protected CacheEntry purgeEntry() throws CacheException {
		CacheEntry entry = mFirstElement;

		removeEntry(entry);
		mHash.remove(entry.getKey());

		entry.setValue(null);
		return entry;
	}

    /**
     * Returns true if cache contains key.
     * @param key Key Object
     * @return true if cache contains key
     */
	public boolean containsKey(Object key) {
		CacheEntry entry = (CacheEntry)mHash.get(key);
		return entry != null;
	}

	/**
	 * State information for cache entries.
	 */
	class CacheEntry {
		private Object mKey;
		private Object mValue;
	
		private CacheEntry mPrevious;
		private CacheEntry mNext;
	
		CacheEntry(Object key, Object value) {
			mKey = key;
			mValue = value;
		}
	
		Object getKey() {
			return mKey;
		}
	
		void setKey(Object obj) {
			mKey = obj;
		}
	
		Object getValue() {
			return mValue;
		}
	
		void setValue(Object obj) {
			mValue = obj;
		}
	
		CacheEntry getPrevious() {
			return mPrevious;
		}
	
		void setPrevious(CacheEntry entry) {
			mPrevious = entry;
		}
	
		CacheEntry getNext() {
			return mNext;
		}
	
		void setNext(CacheEntry entry) {
			mNext = entry;
		}
	}

	/**
	 * Enumeration wrapper to return actual user objects instead of
	 * CacheEntries.
	 */
	class MRUEnumeration implements Enumeration {
		Enumeration mEnumaration;
	
		MRUEnumeration(Enumeration en) {
			mEnumaration = en;
		}
	
		public boolean hasMoreElements() {
			return mEnumaration.hasMoreElements();
		}
	
		public Object nextElement() {
			CacheEntry entry = (CacheEntry)mEnumaration.nextElement();
			return entry.getValue();
		}
	}

}
