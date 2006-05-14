/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.luntsys.luntbuild.vcs.accurev;

/**
 * ReferenceTreeInfo
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class ReferenceTreeInfo {
    private String name;
    private StreamInfo basisStream;
    private long transaction;
    private boolean hidden;

    ReferenceTreeInfo(String name, StreamInfo basisStream, long transaction, boolean hidden) {
        if (basisStream == null) {
            throw new IllegalArgumentException("Backing stream for reference tree cannot be null.");
        }
        this.name = name;
        this.basisStream = basisStream;
        this.transaction = transaction;
        this.hidden = hidden;
    }

    public String getName() {
        return name;
    }

    public StreamInfo getBasisStream() {
        return basisStream;
    }

    public long getTransaction() {
        return transaction;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
