package com.generalov.command.handler;

import com.generalov.CatBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandDrink extends Command{

    public CommandDrink(CatBot catBot) {
        super(catBot);
    }

    public void drink(Update update){
        /**
         * Проверить наличие воды.
         * Если вода есть, то начать пить.
         * Если воды нет, то сообщить об этом.
         */
    }
}
