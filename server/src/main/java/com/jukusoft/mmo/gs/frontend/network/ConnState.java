package com.jukusoft.mmo.gs.frontend.network;

import java.util.List;
import java.util.Objects;

public class ConnState {

    //user & character information
    protected int userID = -1;
    protected String username = "";
    protected int cid = -1;
    protected List<String> groups = null;

    //region information
    protected long regionID = -1;
    protected int instanceID = -1;
    protected int shardID = -1;

    public ConnState() {
        //
    }

    public void setAuthorized(int userID, String username, int cid, List<String> groups) {
        Objects.requireNonNull(groups);

        this.userID = userID;
        this.username = username;
        this.cid = cid;
        this.groups = groups;
    }

    public void setRegion(long regionID, int instanceID, int shardID) {
        this.regionID = regionID;
        this.instanceID = instanceID;
        this.shardID = shardID;
    }

    public int getUserID() {
        return this.userID;
    }

    public String getUsername() {
        return this.username;
    }

    public int getCID() {
        return this.cid;
    }

    public List<String> listGroups() {
        return this.groups;
    }

    public boolean hasGroup (String groupName) {
        return this.groups.contains(groupName);
    }

    public long getRegionID() {
        return this.regionID;
    }

    public int getInstanceID() {
        return this.instanceID;
    }

    public int getShardID() {
        return this.shardID;
    }

}
