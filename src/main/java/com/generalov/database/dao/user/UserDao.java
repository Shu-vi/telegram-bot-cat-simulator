package com.generalov.database.dao.user;

import com.generalov.database.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
@Scope(value = "singleton")
public class UserDao{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User read(Long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", new UserRowMapper(), id)
                .stream().findAny().orElse(null);
    }

    public void update(User obj) {
        jdbcTemplate.update("UPDATE users SET name = ?, condition = ? WHERE id = ?", obj.getName(), obj.getCondition(), obj.getId());
    }

    public void create(User obj) {
        jdbcTemplate.update("INSERT INTO users(id, name, condition) VALUES (?, ?, ?)", obj.getId(), obj.getName(), obj.getCondition());
    }

    public void delete(User obj) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", obj.getId());
    }
}
