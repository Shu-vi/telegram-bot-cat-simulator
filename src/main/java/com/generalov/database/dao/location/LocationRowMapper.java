package com.generalov.database.dao.location;

import com.generalov.database.entity.Location;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationRowMapper implements RowMapper<Location> {
    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        Location location = new Location(
                rs.getInt("id"),
                (Integer[]) rs.getArray("neighboring_locations_id").getArray(),
                rs.getString("title"),
                rs.getInt("food_id"),
                rs.getInt("water_id"),
                rs.getInt("health_id")
        );
        return location;
    }
}
