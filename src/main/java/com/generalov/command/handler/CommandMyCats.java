package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

public class CommandMyCats extends Command{
    public CommandMyCats(CatBot catBot){
        super(catBot);
    }


    @SneakyThrows
    private void getCats(Long userId){
        ArrayList<Cat> cats = database.getCatsListByUserId(userId);
        String text = "";
        if (cats.size()==0){
            text += "У вас нет ни одного кота.";
        } else {
            for (int i = 0; i < cats.size(); i++) {
                text += (i+1) + ") " + cats.get(i).getGender() + " " + cats.get(i).getName() + " породы " +
                        database.getBreedByBreedId(cats.get(i).getBreedId()) +
                        " на локации " + database.getLocationByLocationId(cats.get(i).getLocationId()).getTitle() + "." +
                        "\n   Параметры:" +
                        "\n      Здоровье: " + cats.get(i).getHealth() +
                        "\n      Жажда: " + cats.get(i).getWater() +
                        "\n      Голод: " + cats.get(i).getSatiety() +
                        "\n      Выносливость: " + cats.get(i).getStamina() + "\n\n";
            }
        }
        catBot.execute(SendMessage.builder().text(text).chatId(userId.toString()).build());
    }

    @SneakyThrows
    private void wrongConditionMessage(Long userId){
        String text = "Данную команду невозможно выполнить, находясь в игре. Для начала выйдите из игры командой \"Выйти из игры\".";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(text).build());
    }


    public void myCats(Update update){
        Long userId = update.getMessage().getChatId();
        if (database.getUserById(userId).getCondition() == User.NOT_IN_GAME){
            getCats(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }
}
