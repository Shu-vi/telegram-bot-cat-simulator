package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.Database;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class Command {
    protected Database database;
    protected CatBot catBot;

    public Command(CatBot catBot, Database database){
        this.catBot = catBot;
        this.database = database;
    }

    public abstract void useCommand(Update update);
}
