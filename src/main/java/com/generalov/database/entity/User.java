package com.generalov.database.entity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User{
    private Long id;
    private String name;
    public static final Short NOT_IN_GAME = 0;
    public static final Short IN_GAME = 1;
    public static final Short IN_SHELTER = 2;
    public static final Short EATING = 3;
    public static final Short SLEEPING = 4;
    public static final Short DRINKING = 5;
    public static final Short HEALING = 6;
    public static final Short MOVING = 7;
    /**
     * 0 - Пользователь не в игре
     * 1 - Пользователь в игре
     * 2 - Пользователь в укрытии
     * 3 - Пользователь ест
     * 4 - Пользователь спит
     * 5 - Пользователь пьёт
     * 6 - Пользователь исцеляется
     * 7 - Пользователь меняет локацию
     */
    private Short condition;

    public User(Long id, String name, Short condition) {
        this.id = id;
        this.name = name;
        this.condition = condition;
    }
}
