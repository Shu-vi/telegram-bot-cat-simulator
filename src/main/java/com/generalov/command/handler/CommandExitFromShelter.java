package com.generalov.command.handler;

import com.generalov.CatBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandExitFromShelter extends Command{
    public CommandExitFromShelter(CatBot catBot){
        super(catBot);
    }

    public void exitFromShelter(Update update){
        /**
         * Проверить, находится ли кот в укрытии
         * Если находится, то выйти из него
         * Если не находится, то сообщить об этом
         */
    }
}
