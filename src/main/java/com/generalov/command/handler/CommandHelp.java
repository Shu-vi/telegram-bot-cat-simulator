package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandHelp extends Command{
    public CommandHelp(CatBot catBot){
        super(catBot);
    }


    public void help(Update update){
        Long userId = update.getMessage().getChatId();
        Short condition = database.getUserById(userId).getCondition();
        if (condition == User.NOT_IN_GAME){
            helpOutGame(userId);
        } else {
            helpInGame(userId);
        }
    }

    @SneakyThrows
    private void helpInGame(Long userId){

    }

    @SneakyThrows
    private void helpOutGame(Long userId){
        String text = "Список доступных команд вне игры:" +
                "\n1) \"Мои коты\" - выводит всех ваших котов и их параметры." +
                "\n2) \"Локации\" - выводит список локаций, доступных для спавна." +
                "\n3)" +
                "\n\"Создать кота: ИмяКота" +
                "\nПорода: НазваниеПороды" +
                "\nЛокация: НазваниеЛокацииДляСпавна" +
                "\nПол: ПолКота\"" +
                "\nГде ИмяКота - желаемое имя для вашего петомца. В дальнйшем его нельзя будет поменять(но это не точно)," +
                " НазваниеПороды - порода вашего кота, нужно выбрать одну из следующих трёх \"Длиннолапый\", \"Крепкий\", \"Мускулистый\"," +
                " НазваниеЛокацииДляСпавна - название локации, нужно выбрать одну из следующих двух \"Деревня\", \"Лес\"," +
                " ПолКота - пол вашего котика, нужно выбрать один из следующих двух \"Кот\", \"Кошка\".";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(text).build());
    }
}
