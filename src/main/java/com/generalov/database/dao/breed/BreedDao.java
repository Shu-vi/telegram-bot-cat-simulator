package com.generalov.database.dao.breed;

import com.generalov.database.entity.Breed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class BreedDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public BreedDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Breed read(Integer id){
        return jdbcTemplate.query("SELECT * FROM breed WHERE id = ?", new BreedRowMapper(), id)
                .stream().findAny().orElse(null);
    }

    public void create(Breed obj){
        jdbcTemplate.update("INSERT INTO breed(title, max_health, max_stamina, max_water, max_satiety) VALUES(?, ?, ?, ?, ?)",
                obj.getTitle(), obj.getMaxHealth(), obj.getMaxStamina(), obj.getMaxWater(), obj.getMaxSatiety());
    }

    public void update(Breed obj){
        jdbcTemplate.update("UPDATE breed SET title = ?, max_health = ?, max_stamina = ?, max_water = ?, max_satiety = ? WHERE id = ?",
                obj.getTitle(), obj.getMaxHealth(), obj.getMaxStamina(), obj.getMaxWater(), obj.getMaxSatiety(), obj.getId());
    }

    public void delete(Breed obj){
        jdbcTemplate.update("DELETE FROM breed WHERE id = ?", obj.getId());
    }

    public Breed readBreedByBreedTitle(String breedTitle){
        return jdbcTemplate.query("SELECT * FROM breed WHERE title = ?", new BreedRowMapper(), breedTitle)
                .stream().findAny().orElse(null);
    }
}
