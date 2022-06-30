package com.generalov.database.dao.item.water;

import com.generalov.database.entity.ItemWater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class WaterDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public WaterDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public ItemWater read(Integer id){
        return jdbcTemplate.query("SELECT * FROM item_water WHERE id = ?", new WaterRowMapper(), id)
                .stream().findAny().orElse(null);
    }

    public void update(ItemWater obj){
        jdbcTemplate.update("UPDATE item_water SET title = ? WHERE id = ?",
                obj.getTitle(), obj.getId());
    }

    public void create(ItemWater obj){
        jdbcTemplate.update("INSERT INTO item_water(title) VALUES (?)",
                obj.getTitle());
    }

    public void delete(ItemWater obj){
        jdbcTemplate.update("DELETE FROM item_water WHERE id = ?");
    }
}
