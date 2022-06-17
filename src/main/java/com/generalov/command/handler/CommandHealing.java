package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Location;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandHealing extends Command implements Runnable{
    private Update update;

    public CommandHealing(CatBot catBot, Update update){
        super(catBot);
        this.update = update;
    }

    @Override
    public void run() {
        User user = database.getUserById(update.getMessage().getChatId());
        Short userCondition = user.getCondition();
        if (userCondition == User.IN_GAME) {
            healing();
        } else if (userCondition == User.NOT_IN_GAME) {
            notInGameMessage();
        } else {
            otherConditionsMessage();
        }
    }

    @SneakyThrows
    private void healing(){
        Long userId = update.getMessage().getChatId();
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        Location location = database.getLocationByLocationId(cat.getLocationId());
        Long waitingTimeMillis = Long.valueOf(calculateMillisOfHealing());
        if (isCanHealing(location)){
            doHealing(waitingTimeMillis, cat);
        } else {
            healingIsNotExistMessage();
        }
    }

    @SneakyThrows
    private void doHealing(Long waitingTimeMillis, Cat cat){
        Long userId = update.getMessage().getChatId();
        healingMessage(waitingTimeMillis);
        database.setUserConditionByUserId(User.HEALING, userId);
        Thread.sleep(waitingTimeMillis);
        cat.setHealth(Math.min(cat.getHealth() + 7, 100));
        database.setCat(cat);
        database.setUserConditionByUserId(User.IN_GAME, userId);
        congratulationMessage();
    }

    /**
     * @return количество миллисекунд ожидания
     */
    private Integer calculateMillisOfHealing(){
        return 20000;
    }

    private Boolean isCanHealing(Location location){
        return location.getHealthId() != null;
    }

    /**
     * ------------------------------------------------
     * Методы для отправки обратной связи пользователям
     */


    @SneakyThrows
    private void healingMessage(Long waitingTimeMillis){
        Long userId = update.getMessage().getChatId();
        String message = "Вы начали залечивать свои раны, подождите " + waitingTimeMillis/1000 + " секунд, другие действия недоступны.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void congratulationMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Вы закончили залечивать свои раны.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void healingIsNotExistMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "На этой локации нет трав для излечения ваших ран.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void notInGameMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Вы не в игре. Для начала зайдите в игру.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void otherConditionsMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Дождитесь завершения всех действий и выйдите из укрытия.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }
}
