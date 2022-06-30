package com.generalov.database.entity;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Cat extends BaseEntity{
    private String name;
    private String gender;
    private Integer breedId;
    private Integer health;
    private Integer satiety;
    private Integer water;
    private Integer stamina;
    private Integer locationId;
    private Long userId;
    private Boolean isOnline;
    private Breed breed;

    public Cat(Integer id, String name, String gender, Integer breedId, Integer health, Integer satiety, Integer water, Integer stamina, Integer locationId, Long userId, Boolean isOnline) {
        super(id);
        this.name = name;
        this.gender = gender;
        this.breedId = breedId;
        this.health = health;
        this.satiety = satiety;
        this.water = water;
        this.stamina = stamina;
        this.locationId = locationId;
        this.userId = userId;
        this.isOnline = isOnline;
    }
}
