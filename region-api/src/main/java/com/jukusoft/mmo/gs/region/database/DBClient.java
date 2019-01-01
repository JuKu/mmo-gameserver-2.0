package com.jukusoft.mmo.gs.region.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBClient extends AutoCloseable {

    public PreparedStatement prepareStatement (String sql) throws SQLException;

    public ResultSet query (String sql) throws SQLException;

}
