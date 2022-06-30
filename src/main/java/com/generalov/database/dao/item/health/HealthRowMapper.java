package com.generalov.database.dao.item.health;



import com.generalov.database.entity.ItemHealth;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HealthRowMapper implements RowMapper<ItemHealth> {
    @Override
    public ItemHealth mapRow(ResultSet rs, int rowNum) throws SQLException {
        ItemHealth itemHealth = new ItemHealth(
                rs.getInt("id"),
                rs.getString("title")
        );
        return itemHealth;
    }
}
