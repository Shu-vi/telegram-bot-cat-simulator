package com.generalov.database.entity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemFood extends BaseEntity{
    private String title;

    public ItemFood(Integer id, String title) {
        super(id);
        this.title = title;
    }
}
