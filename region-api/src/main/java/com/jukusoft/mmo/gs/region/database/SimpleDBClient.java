package com.jukusoft.mmo.gs.region.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class SimpleDBClient implements DBClient {

    protected final Connection connection;
    protected final String prefix;

    public SimpleDBClient (Connection connection, String prefix) {
        Objects.requireNonNull(connection);
        Objects.requireNonNull(prefix);

        this.connection = connection;
        this.prefix = prefix;
    }

    @Override
    public PreparedStatement prepareStatement (String sql) throws SQLException {
        String query = sql.replace("{prefix}", this.prefix);
        return this.connection.prepareStatement(query);
    }

    @Override
    public ResultSet query(String sql) throws SQLException {
        try (PreparedStatement statement = prepareStatement(sql)) {
            return statement.executeQuery();
        }
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }

}
