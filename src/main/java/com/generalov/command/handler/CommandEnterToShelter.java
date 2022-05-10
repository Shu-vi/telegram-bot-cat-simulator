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
        } else {
            /*
            Здесь происходит обработка всякой хрени
             */
            wrongConditionMessage(userId);
        }
    }

    private void enterToShelter(Long userId, String message){
        String shelterName = getShelterName(message);
        Shelter shelter = database.getShelterByShelterTitle(shelterName);
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        addCatToShelter(shelter, cat);
        congratulationsMessage(userId);
        /**
         * Получаем название укрытия.
         * Достаём укрытие по его названию.
         * Добавляем в укрытие кота.
         * Изменяем укрытие.
         * Сообщаем, что чел в укрытии.
         */
    }

    @SneakyThrows
    private void congratulationsMessage(Long userId){
        String message = "Вы успешно вошли в укрытие.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }

    private void addCatToShelter(Shelter shelter, Cat newCat){
        Integer[] oldCats = shelter.getCats();
        Integer[] newCats;

        if (oldCats != null) {
            newCats = new Integer[oldCats.length + 1];
            for (int i = 0; i < oldCats.length; i++) {
                newCats[i] = oldCats[i];
            }
            newCats[oldCats.length] = newCat.getId();
        }
        else {
            newCats = new Integer[1];
            newCats[0] = newCat.getId();
        }
        shelter.setCats(newCats);
        database.setShelterByShelterId(shelter);
        database.setUserConditionByUserId(User.IN_SHELTER, newCat.getUserId());
    }

    private String getShelterName(String message){
        return message.substring(16);
    }

    @SneakyThrows
    private void wrongConditionMessage(Long userId){
        String message = "Чтобы войти в укрытие, вы должны дождаться завершения действия и быть в игре";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }
}
