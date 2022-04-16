package com.generalov.command.handler;

import com.generalov.CatBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandEat extends Command{
    public CommandEat(CatBot catBot) {
        super(catBot);
    }

    public void eat(Update update){
        /**
         * Проверить наличие еды на локации.
         * Если еда есть, то восстановить еду на 10 и поставить таймер.
         * Если еды нет, то написать об этом.
         */
    }
}
