package com.generalov.database.dao.shelter;

import com.generalov.database.entity.Shelter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShelterRowMapper implements RowMapper<Shelter> {
    @Override
    public Shelter mapRow(ResultSet rs, int rowNum) throws SQLException {
        Shelter shelter = new Shelter(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getInt("capacity"),
                rs.getInt("location_id"),
                rs.getArray("cats") != null? (Integer[]) rs.getArray("cats").getArray() : null
        );
        return shelter;
    }
}
