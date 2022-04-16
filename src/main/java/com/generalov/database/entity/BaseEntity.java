package com.generalov.database.entity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class BaseEntity {
    protected Integer id;

    public BaseEntity(Integer id){
        this.id = id;
    }
}
