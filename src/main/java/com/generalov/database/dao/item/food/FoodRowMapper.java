package com.generalov.database.dao.item.food;

import com.generalov.database.entity.ItemFood;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FoodRowMapper implements RowMapper <ItemFood>{
    @Override
    public ItemFood mapRow(ResultSet rs, int rowNum) throws SQLException {
        ItemFood itemFood = new ItemFood(
                rs.getInt("id"),
                rs.getString("title")
        );
        return itemFood;
    }
}
