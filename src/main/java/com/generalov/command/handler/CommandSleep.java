package com.generalov.command.handler;

import com.generalov.CatBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandSleep extends Command{
    public CommandSleep(CatBot catBot){
        super(catBot);
    }

    public void sleep(Update update){
        /**
         * Проверить, где находится кот. Если в укрытии, то лечь спать.
         * Проснуться по истечение времени N.
         * Сообщить чё-то пользователю
         */
    }
}
