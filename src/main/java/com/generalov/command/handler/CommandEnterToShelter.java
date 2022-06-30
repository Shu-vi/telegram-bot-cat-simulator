package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.dao.cat.CatDao;
import com.generalov.database.dao.shelter.ShelterDao;
import com.generalov.database.dao.user.UserDao;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Shelter;
import com.generalov.database.entity.User;
import com.generalov.string.handler.StringHandler;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Scope(value = "singleton")
public class CommandEnterToShelter extends Command{
    private UserDao userDao;
    private ShelterDao shelterDao;
    private CatDao catDao;

    @Autowired
    public CommandEnterToShelter(CatBot catBot, UserDao userDao, ShelterDao shelterDao, CatDao catDao) {
        super(catBot);
        this.userDao = userDao;
        this.shelterDao = shelterDao;
        this.catDao = catDao;
    }

    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        String message = StringHandler.deleteBotName(update.getMessage().getText());
        Short userCondition = userDao.read(userId).getCondition();
        if (userCondition == User.IN_GAME){
            enterToShelter(userId, message);
            congratulationsMessage(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }

    private void enterToShelter(Long userId, String message){
        String shelterName = getShelterName(message);
        Shelter shelter = shelterDao.readShelterByShelterTitle(shelterName);
        Cat cat = catDao.readCatByUserId(userId);
        addCatToShelter(shelter, cat);
    }

    private void addCatToShelter(Shelter shelter, Cat newCat){
        Integer[] oldCatsId = shelter.getCats();
        Integer[] newCatsId = getNewCatsId(oldCatsId, newCat);
        shelter.setCats(newCatsId);
        shelterDao.update(shelter);
        User user = userDao.read(newCat.getUserId());
        user.setCondition(User.IN_SHELTER);
        userDao.update(user);
    }

    private Integer[] getNewCatsId(Integer[] oldCatsId, Cat newCat){
        Integer[] newCatsId;
        if (oldCatsId != null) {
            newCatsId = new Integer[oldCatsId.length + 1];
            for (int i = 0; i < oldCatsId.length; i++) {
                newCatsId[i] = oldCatsId[i];
            }
            newCatsId[oldCatsId.length] = newCat.getId();
        }
        else {
            newCatsId = new Integer[1];
            newCatsId[0] = newCat.getId();
        }
        return newCatsId;
    }

    private String getShelterName(String message){
        return message.substring(16);
    }

    @SneakyThrows
    private void wrongConditionMessage(Long userId){
        String message = "Чтобы войти в укрытие, вы должны дождаться завершения действия и быть в игре";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }

    @SneakyThrows
    private void congratulationsMessage(Long userId){
        String message = "Вы успешно вошли в укрытие.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }
}
