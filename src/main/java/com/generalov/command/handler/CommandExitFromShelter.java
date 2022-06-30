package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.dao.cat.CatDao;
import com.generalov.database.dao.shelter.ShelterDao;
import com.generalov.database.dao.user.UserDao;
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
import java.util.List;

@Component
@Scope(value = "singleton")
public class CommandExitFromShelter extends Command{
    private UserDao userDao;
    private CatDao catDao;
    private ShelterDao shelterDao;

    @Autowired
    public CommandExitFromShelter(CatBot catBot, UserDao userDao, CatDao catDao, ShelterDao shelterDao) {
        super(catBot);
        this.userDao = userDao;
        this.catDao = catDao;
        this.shelterDao = shelterDao;
    }

    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        Short userCondition = userDao.read(userId).getCondition();
        if (userCondition == User.IN_SHELTER){
            exitFromShelter(userId);
            congratulationMessage(userId);
        }
    }

    private void exitFromShelter(Long userId){
        User user = userDao.read(userId);
        Cat cat = catDao.readCatByUserId(userId);
        List<Shelter> shelters = shelterDao.readSheltersByLocationId(cat.getLocationId());
        Shelter shelter = getShelter(cat, shelters);
        catShelterOut(cat, shelter);
        user.setCondition(User.IN_GAME);
        userDao.update(user);
    }

    private Shelter getShelter(Cat cat, List<Shelter> shelters){
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
        shelterDao.update(shelter);
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
