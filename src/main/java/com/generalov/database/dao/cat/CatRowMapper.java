package com.generalov.database.dao.cat;

import com.generalov.database.entity.Cat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CatRowMapper implements RowMapper<Cat> {
    @Override
    public Cat mapRow(ResultSet rs, int rowNum) throws SQLException {
        Cat cat = new Cat(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("gender"),
                rs.getInt("breed_id"),
                rs.getInt("health"),
                rs.getInt("satiety"),
                rs.getInt("water"),
                rs.getInt("stamina"),
                rs.getInt("location_id"),
                rs.getLong("user_id"),
                rs.getBoolean("is_online")
        );
        return cat;
    }
}
