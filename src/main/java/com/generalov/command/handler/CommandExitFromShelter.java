package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Shelter;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

public class CommandExitFromShelter extends Command{
    public CommandExitFromShelter(CatBot catBot){
        super(catBot);
    }

    public void exitFromShelter(Update update){
        Long userId = update.getMessage().getChatId();
        Short userCondition = database.getUserById(userId).getCondition();
        if (userCondition == User.IN_SHELTER){
            exitFromShelter(userId);
            congratulationMessage(userId);
        }
    }

    @SneakyThrows
    private void congratulationMessage(Long userId){
        String message = "Вы покинули укрытие.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }


    private void exitFromShelter(Long userId){
        User user = database.getUserById(userId);
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        ArrayList<Shelter> shelters = database.getSheltersByLocationId(cat.getLocationId());
        /**
         * Находим нужное нам укрытие с котом.
         * Выводим кота из укрытия.
         * Изменяем состояние юзера.
         */
        Shelter shelter = getShelter(cat, shelters);
        catShelterOut(cat, shelter);
        changeUserCondition(user);
    }

    private void changeUserCondition(User user){
        database.setUserConditionByUserId(User.IN_GAME, user.getId());
    }


    private void catShelterOut(Cat cat, Shelter shelter){
        Integer[] newCats = new Integer[shelter.getCats().length-1];
        if (shelter.getCats().length>1) {
            for (int i = 0; i < shelter.getCats().length; i++) {
                if (cat.getId() != shelter.getCats()[i]) {
                    newCats[i] = shelter.getCats()[i];
                }
            }
        }
        shelter.setCats(newCats);
        database.setShelterByShelterId(shelter);
    }

    private Shelter getShelter(Cat cat, ArrayList<Shelter> shelters){
        Integer catId = cat.getId();
        for (int i = 0; i < shelters.size(); i++) {
                for (int j = 0; j < shelters.get(i).getCats().length; j++) {
                    if (catId == shelters.get(i).getCats()[j]) {
                        return shelters.get(i);
                    }
                }
        }
        return null;
    }
}
