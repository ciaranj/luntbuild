/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */

package com.luntsys.luntbuild.vcs.accurev;

import java.util.Map;
import java.util.HashMap;

/**
 * Stream type object.
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class StreamType {
    private static Map INSTANCES = new HashMap();
    /** Stream type, normal */
    public static final StreamType normal = new StreamType("normal");
    /** Stream type, workspace */
    public static final StreamType workspace = new StreamType("workspace");
    /** Stream type, snapshot */
    public static final StreamType snapshot = new StreamType("snapshot");

    /**
     * Gets the stream type object with the specified name.
     * 
     * @param name the name
     * @return the stream type object, or <code>null</code> if no stream type object exists with that name
     */
    static StreamType fromString(String name) {
        return (StreamType) INSTANCES.get(name);
    }

    private final String myName; // for debug only

    /**
     * Creates a new stream type object.
     * 
     * @param name the stream type
     */
    private StreamType(String name) {
        myName = name;
        INSTANCES.put(name, this);
    }

    /**
     * Gets the name of this object, used for debugging.
     * 
     * @return the name
     */
    public String toString() {
        return myName;
    }
}
