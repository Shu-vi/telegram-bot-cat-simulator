package com.generalov.database.entity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemHealth extends BaseEntity{
    private String title;

    public ItemHealth(Integer id, String title) {
        super(id);
        this.title = title;
    }
}
