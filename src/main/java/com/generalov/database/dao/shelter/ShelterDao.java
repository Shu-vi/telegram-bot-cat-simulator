package com.generalov.database.dao.shelter;

import com.generalov.database.entity.Shelter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(value = "singleton")
public class ShelterDao{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ShelterDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Shelter read(Integer id) {
        return jdbcTemplate.query("SELECT * FROM shelter WHERE id = ?", new ShelterRowMapper(), id)
                .stream().findAny().orElse(null);
    }

    public void update(Shelter obj) {
        jdbcTemplate.update("UPDATE shelter SET title = ?, capacity = ?, location_id = ?, cats = ? WHERE id = ?",
                obj.getTitle(), obj.getCapacity(), obj.getLocationId(), obj.getCats(), obj.getId());
    }

    public void create(Shelter obj) {
        jdbcTemplate.update("INSERT INTO shelter(capacity, location_id, title, cats) VALUES(?, ?, ?, ?)",
                obj.getCapacity(), obj.getLocationId(), obj.getTitle(), obj.getCats());
    }

    public void delete(Shelter obj) {
        jdbcTemplate.update("DELETE FROM shelter WHERE id = ?", obj.getId());
    }

    public List<Shelter> readSheltersByLocationId(Integer locationId){
        return jdbcTemplate.query("SELECT * FROM shelter WHERE location_id = ?", new ShelterRowMapper(), locationId);
    }

    public Shelter readShelterByShelterTitle(String shelterTitle){
        return jdbcTemplate.query("SELECT * FROM shelter WHERE title = ?", new ShelterRowMapper(), shelterTitle)
                .stream().findAny().orElse(null);
    }
}