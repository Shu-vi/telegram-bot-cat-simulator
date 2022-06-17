package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Shelter;
import com.generalov.database.entity.User;
import com.generalov.string.handler.StringHandler;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandEnterToShelter extends Command{
    public CommandEnterToShelter(CatBot catBot) {
        super(catBot);
    }

    public void enterToShelter(Update update){
        Long userId = update.getMessage().getChatId();
        String message = StringHandler.deleteBotName(update.getMessage().getText());
        Short userCondition = database.getUserById(userId).getCondition();
        if (userCondition == User.IN_GAME){
            enterToShelter(userId, message);
            congratulationsMessage(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }

    private void enterToShelter(Long userId, String message){
        String shelterName = getShelterName(message);
        Shelter shelter = database.getShelterByShelterTitle(shelterName);
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        addCatToShelter(shelter, cat);
    }

    private void addCatToShelter(Shelter shelter, Cat newCat){
        Integer[] oldCatsId = shelter.getCats();
        Integer[] newCatsId = getNewCatsId(oldCatsId, newCat);
        shelter.setCats(newCatsId);
        database.setShelterByShelterId(shelter);
        database.setUserConditionByUserId(User.IN_SHELTER, newCat.getUserId());
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
