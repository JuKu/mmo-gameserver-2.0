package com.jukusoft.mmo.gs.region.database;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class SimpleDBClientTest {

    @Test
    public void testConstructor () {
        new SimpleDBClient(Mockito.mock(Connection.class), "");
    }

    @Test
    public void testPrepareStatement () throws SQLException {
        DBClient dbClient = new SimpleDBClient(Mockito.mock(Connection.class), "");
        when(((SimpleDBClient) dbClient).connection.prepareStatement(anyString())).thenReturn(Mockito.mock(PreparedStatement.class));

        assertNotNull(dbClient.prepareStatement("test"));
    }

    @Test
    public void testClose () throws Exception {
        DBClient dbClient = new SimpleDBClient(Mockito.mock(Connection.class), "");
        dbClient.close();
    }

}
