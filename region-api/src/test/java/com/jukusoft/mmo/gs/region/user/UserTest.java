package com.jukusoft.mmo.gs.region.user;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserTest {

    @Test
    public void testConstructor () {
        new User(1, "test", new ArrayList<>());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNegativeUserIDConstructor () {
        new User(-1, "test", new ArrayList<>());
    }

    @Test (expected = NullPointerException.class)
    public void testNullUsernameConstructor () {
        new User(1, null, new ArrayList<>());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEmptyUsernameConstructor () {
        new User(1, "", new ArrayList<>());
    }

    @Test (expected = NullPointerException.class)
    public void testNullGroupsListConstructor () {
        new User(1, "test", null);
    }

    @Test
    public void testGetterAndSetter () {
        User user = new User(10, "test", new ArrayList<>());
        assertEquals(10, user.getUserID());
        assertEquals("test", user.getUsername());
        assertEquals(false, user.hasGroup("gamemaster"));
        assertEquals(0, user.listGroups().size());

        List<String> groups = new ArrayList<>();
        groups.add("testgroup");
        User user1 = new User(10, "test", groups);
        assertEquals(false, user1.hasGroup("gamemaster"));
        assertEquals(true, user1.hasGroup("testgroup"));
        assertEquals(1, user1.listGroups().size());
    }

}
