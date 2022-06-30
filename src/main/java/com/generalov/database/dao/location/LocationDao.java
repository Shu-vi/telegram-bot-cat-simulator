package com.generalov.database.dao.location;

import com.generalov.database.dao.shelter.ShelterRowMapper;
import com.generalov.database.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class LocationDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LocationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Location read(Integer id) {
        return jdbcTemplate.query("SELECT * FROM location WHERE id = ?", new LocationRowMapper(), id)
                .stream().findAny().orElse(null);
    }

    public void update(Location obj) {
        jdbcTemplate.update("UPDATE location SET neighboring_locations_id = ?, title = ?, food_id = ?, water_id = ?, health_id WHERE id = ?",
                obj.getNeighboringLocationsId(), obj.getTitle(), obj.getFoodId(), obj.getWaterId(), obj.getHealthId(), obj.getId());
    }

    public void create(Location obj) {
        jdbcTemplate.update("INSERT INTO location(neighboring_locations_id, title, food_id, water_id, health_id) VALUES(?, ?, ?, ?, ?)",
                obj.getNeighboringLocationsId(), obj.getTitle(), obj.getFoodId(), obj.getWaterId(), obj.getHealthId());
    }

    public void delete(Location obj) {
        jdbcTemplate.update("DELETE FROM location WHERE id = ?", obj.getId());
    }

    public Location readLocationByLocationTitle(String locationTitle){
        return jdbcTemplate.query("SELECT * FROM location WHERE title = ?", new LocationRowMapper(), locationTitle)
                .stream().findAny().orElse(null);
    }
}
