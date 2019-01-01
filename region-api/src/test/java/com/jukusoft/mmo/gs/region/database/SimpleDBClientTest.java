package com.jukusoft.mmo.gs.region.database;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

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
    public void testQuery () throws SQLException {
        Connection conn = Mockito.mock(Connection.class);
        DBClient dbClient = new SimpleDBClient(conn, "");

        when(((SimpleDBClient) dbClient).connection.prepareStatement(anyString())).thenReturn(new PreparedStatementAdapter() {
            @Override
            public ResultSet executeQuery() {
                return Mockito.mock(ResultSet.class);
            }
        });

        assertNotNull(dbClient.query("test"));
    }

    @Test
    public void testClose () throws Exception {
        DBClient dbClient = new SimpleDBClient(Mockito.mock(Connection.class), "");
        dbClient.close();
    }

}
