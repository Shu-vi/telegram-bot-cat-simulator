package com.generalov.database.dao.breed;

import com.generalov.database.entity.Breed;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BreedRowMapper implements RowMapper<Breed> {
    @Override
    public Breed mapRow(ResultSet rs, int rowNum) throws SQLException {
        Breed breed = new Breed(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getInt("max_health"),
                rs.getInt("max_stamina"),
                rs.getInt("max_water"),
                rs.getInt("max_satiety")
        );
        return breed;
    }
}
