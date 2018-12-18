package com.jukusoft.mmo.gs.region.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DBClient extends AutoCloseable {

    public PreparedStatement prepareStatement (String sql) throws SQLException;

}
