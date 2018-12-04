package com.jukusoft.mmo.gs.region.user;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User {

    //data from database
    protected final int userID;
    protected final String username;

    //ldap groups for permissions
    protected final List<String> groups;

    public User(int userID, String username, List<String> groups) {
        Objects.requireNonNull(userID);
        Objects.requireNonNull(username);
        Objects.requireNonNull(groups);

        if (username.isEmpty()) {
            throw new IllegalArgumentException("username cannot be empty.");
        }

        if (userID <= 0) {
            throw new IllegalArgumentException("userID has to be > 0.");
        }

        this.userID = userID;
        this.username = username;
        this.groups = groups;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public List<String> listGroups() {
        return Collections.unmodifiableList(this.groups);
    }

    public boolean hasGroup (String groupName) {
        return this.groups.contains(groupName);
    }

}
