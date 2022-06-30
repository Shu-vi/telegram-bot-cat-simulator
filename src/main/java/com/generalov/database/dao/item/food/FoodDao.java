package com.generalov.database.dao.item.food;


import com.generalov.database.entity.ItemFood;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class FoodDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public FoodDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public ItemFood read(Integer id){
        return jdbcTemplate.query("SELECT * FROM item_food WHERE id = ?", new FoodRowMapper(), id)
                .stream().findAny().orElse(null);
    }

    public void update(ItemFood obj){
        jdbcTemplate.update("UPDATE item_food SET title = ? WHERE id = ?",
                obj.getTitle(), obj.getId());
    }

    public void create(ItemFood obj){
        jdbcTemplate.update("INSERT INTO item_food(title) VALUES (?)",
                obj.getTitle());
    }

    public void delete(ItemFood obj){
        jdbcTemplate.update("DELETE FROM item_food WHERE id = ?");
    }
}
