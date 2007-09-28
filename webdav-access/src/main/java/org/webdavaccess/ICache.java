package org.webdavaccess;

import java.util.Enumeration;

import org.webdavaccess.exceptions.CacheException;

public interface ICache {

	/**
	 * Place an object in the cache.
	 */
	public abstract void put(Object key, Object value) throws CacheException;

	/**
	 * Obtain an object in the cache
	 */
	public abstract Object get(Object key);

	/**
	 * Remove an object from the cache
	 */
	public abstract void remove(Object key);

	/**
	 * Remove all objects from the cache
	 */
	public abstract void removeAll();

	/**
	 * Enumerate elements' values in the cache
	 */
	public abstract Enumeration elements();

    /**
     * Returns true if cache contains key.
     * @param key Key Object
     * @return true if cache contains key
     */
    public abstract boolean containsKey(Object key);
}