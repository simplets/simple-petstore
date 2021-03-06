package org.testinfected.petstore.db.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Record<T> {

    T hydrate(ResultSet rs) throws SQLException;

    void dehydrate(PreparedStatement st, T entity) throws SQLException;

    void handleKeys(ResultSet keys, T entity) throws SQLException;
}
