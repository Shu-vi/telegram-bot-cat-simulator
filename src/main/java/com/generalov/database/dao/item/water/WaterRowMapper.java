package com.generalov.database.dao.item.water;

import com.generalov.database.entity.ItemWater;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WaterRowMapper implements RowMapper<ItemWater> {
    @Override
    public ItemWater mapRow(ResultSet rs, int rowNum) throws SQLException {
        ItemWater itemWater = new ItemWater(
                rs.getInt("id"),
                rs.getString("title")
        );
        return itemWater;
    }
}
