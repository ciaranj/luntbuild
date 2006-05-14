/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.vcs.accurev;



/**
 * StreamInfo
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class StreamInfo {
    private String name;
    private String backingStream;
    private String depot;
    private StreamType type;

    public StreamInfo(String name, String backingStream, String depot, StreamType type) {
        this.name = name;
        this.backingStream = backingStream;
        this.depot = depot;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getBackingStream() {
        return backingStream;
    }

    public String getDepot() {
        return depot;
    }

    public StreamType getType() {
        return type;
    }
}
