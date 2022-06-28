package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.Database;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Shelter;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Component
@Scope(value = "singleton")
public class CommandExitFromShelter extends Command{
    @Autowired
    public CommandExitFromShelter(CatBot catBot, Database database) {
        super(catBot, database);
    }

    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        Short userCondition = database.getUserById(userId).getCondition();
        if (userCondition == User.IN_SHELTER){
            exitFromShelter(userId);
            congratulationMessage(userId);
        }
    }

    private void exitFromShelter(Long userId){
        User user = database.getUserById(userId);
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        ArrayList<Shelter> shelters = database.getSheltersByLocationId(cat.getLocationId());
        Shelter shelter = getShelter(cat, shelters);
        catShelterOut(cat, shelter);
        database.setUserConditionByUserId(User.IN_GAME, user.getId());
    }

    private Shelter getShelter(Cat cat, ArrayList<Shelter> shelters){
        Integer catId = cat.getId();
        for (int i = 0; i < shelters.size(); i++) {
            Integer catsSize = shelters.get(i).getCats().length;
            Shelter shelter = shelters.get(i);
            for (int j = 0; j < catsSize; j++) {
                if (isCatsEquals(catId, shelter.getCats()[j])) {
                    return shelter;
                }
            }
        }
        return null;
    }

    private void catShelterOut(Cat cat, Shelter shelter){
        Integer[] oldCatsId = shelter.getCats();
        Integer[] newCatsId = new Integer[oldCatsId.length-1];
        Integer catId = cat.getId();
        if (oldCatsId.length>1) {
            for (int i = 0; i < oldCatsId.length; i++) {
                if (catId != oldCatsId[i]) {
                    newCatsId[i] = oldCatsId[i];
                }
            }
        }
        shelter.setCats(newCatsId);
        database.setShelterByShelterId(shelter);
    }

    private Boolean isCatsEquals(Integer catIdFirst, Integer catIdSecond){
        return catIdFirst == catIdSecond;
    }

    @SneakyThrows
    private void congratulationMessage(Long userId){
        String message = "Вы покинули укрытие.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }
}
