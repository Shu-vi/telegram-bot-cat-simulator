package com.generalov.database.entity;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Breed extends BaseEntity{
    private String title;
    private Integer maxHealth;
    private Integer maxStamina;
    private Integer maxWater;
    private Integer maxSatiety;

    public Breed(Integer id, String title, Integer maxHealth, Integer maxStamina, Integer maxWater, Integer maxSatiety) {
        super(id);
        this.title = title;
        this.maxHealth = maxHealth;
        this.maxStamina = maxStamina;
        this.maxWater = maxWater;
        this.maxSatiety = maxSatiety;
    }
}
