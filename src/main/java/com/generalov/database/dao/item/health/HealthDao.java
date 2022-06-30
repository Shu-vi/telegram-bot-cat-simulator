package com.generalov.database.dao.item.health;

import com.generalov.database.entity.ItemHealth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class HealthDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public HealthDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public ItemHealth read(Integer id){
        return jdbcTemplate.query("SELECT * FROM item_health WHERE id = ?", new HealthRowMapper(), id)
                .stream().findAny().orElse(null);
    }

    public void update(ItemHealth obj){
        jdbcTemplate.update("UPDATE item_health SET title = ? WHERE id = ?",
                obj.getTitle(), obj.getId());
    }

    public void create(ItemHealth obj){
        jdbcTemplate.update("INSERT INTO item_health(title) VALUES (?)",
                obj.getTitle());
    }

    public void delete(ItemHealth obj){
        jdbcTemplate.update("DELETE FROM item_health WHERE id = ?");
    }
}
