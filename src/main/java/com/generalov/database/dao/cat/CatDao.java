package com.generalov.database.dao.cat;

import com.generalov.database.entity.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(value = "singleton")
public class CatDao{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CatDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Cat read(Integer id) {
        return jdbcTemplate.query("SELECT * FROM cat WHERE id = ?", new CatRowMapper(), id)
                .stream().findAny().orElse(null);
    }

    public void update(Cat obj) {
        jdbcTemplate.update("UPDATE cat SET name = ?, gender = ?, breed_id = ?, health = ?, satiety = ?, water = ?, stamina = ?, location_id = ?, is_online = ? WHERE id = ?",
                obj.getName(), obj.getGender(), obj.getBreedId(), obj.getHealth(), obj.getSatiety(), obj.getWater(), obj.getStamina(), obj.getLocationId(), obj.getIsOnline(), obj.getId());
    }

    public void create(Cat obj) {
        jdbcTemplate.update("INSERT INTO cat(name, gender, breed_id, health, satiety, water, stamina, location_id, user_id, is_online) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                obj.getName(), obj.getGender(), obj.getBreedId(), obj.getHealth(), obj.getSatiety(), obj.getWater(), obj.getStamina(), obj.getLocationId(), obj.getUserId(), obj.getIsOnline());
    }

    public void delete(Cat obj) {
        jdbcTemplate.update("DELETE FROM cat WHERE id = ?", obj.getId());
    }

    public Cat readCatByUserId(Long userId){
        return jdbcTemplate.query("SELECT * FROM cat WHERE user_id = ?", new CatRowMapper(), userId)
                .stream().findAny().orElse(null);
    }

    public List<Cat> readCatsByLocationId(Integer locationId){
        return jdbcTemplate.query("SELECT * FROM cat WHERE location_id = ?", new CatRowMapper(), locationId);
    }

    public Cat readCatByCatName(String catName){
        return jdbcTemplate.query("SELECT * FROM cat WHERE name = ?", new CatRowMapper(), catName)
                .stream().findAny().orElse(null);
    }
}

