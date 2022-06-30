package com.generalov.command.handler;

import com.generalov.CatBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class Command {
    protected CatBot catBot;

    public Command(CatBot catBot){
        this.catBot = catBot;
    }

    public abstract void useCommand(Update update);
}
