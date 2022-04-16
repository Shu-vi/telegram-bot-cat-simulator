package com.generalov.database.entity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Shelter extends BaseEntity{
    private String title;
    private Integer capacity;
    private Integer locationId;
    private Integer[] cats;

    public Shelter(Integer id, String title, Integer capacity, Integer locationId, Integer[] cats) {
        super(id);
        this.title = title;
        this.capacity = capacity;
        this.locationId = locationId;
        this.cats = cats;
    }
}
