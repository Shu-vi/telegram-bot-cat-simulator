package com.generalov.command.handler;

import com.generalov.CatBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandHealing extends Command{
    public CommandHealing(CatBot catBot){
        super(catBot);
    }

    public void healing(Update update){
        /**
         * Проверка наличия лекарств на локации
         * если они есть, восстановить хп, и убрать растение
         * Если лекарств нет, то сообщить об этом.
         */
    }
}
