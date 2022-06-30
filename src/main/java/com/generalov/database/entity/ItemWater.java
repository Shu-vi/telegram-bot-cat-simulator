package com.generalov.database.entity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemWater extends BaseEntity{
    private String title;

    public ItemWater(Integer id, String title) {
        super(id);
        this.title = title;
    }
}
