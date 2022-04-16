package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.Database;

public abstract class Command {
    protected Database database;
    protected CatBot catBot;

    public Command(CatBot catBot){
        this.catBot = catBot;
        database = Database.getObjectDatabaseControl();
    }
}
