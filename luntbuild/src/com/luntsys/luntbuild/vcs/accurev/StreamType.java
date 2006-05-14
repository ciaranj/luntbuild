/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.vcs.accurev;

import java.util.Map;
import java.util.HashMap;

public class StreamType {
    private static Map INSTANCES = new HashMap();
    public static final StreamType normal = new StreamType("normal");
    public static final StreamType workspace = new StreamType("workspace");
    public static final StreamType snapshot = new StreamType("snapshot");


    static StreamType fromString(String name) {
        return (StreamType) INSTANCES.get(name);
    }

    private final String myName; // for debug only

    private StreamType(String name) {
        myName = name;
        INSTANCES.put(name, this);
    }

    public String toString() {
        return myName;
    }
}
