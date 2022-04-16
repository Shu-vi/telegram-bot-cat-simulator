package com.generalov.database.entity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Location extends BaseEntity{
    private Integer[] neighboringLocationsId;
    private String title;
    private Integer foodId;
    private Integer waterId;
    private Integer healthId;

    public Location(Integer id, Integer[] neighboringLocationsId, String title, Integer foodId, Integer waterId, Integer healthId) {
        super(id);
        this.neighboringLocationsId = neighboringLocationsId;
        this.title = title;
        this.foodId = foodId;
        this.waterId = waterId;
        this.healthId = healthId;
    }
}