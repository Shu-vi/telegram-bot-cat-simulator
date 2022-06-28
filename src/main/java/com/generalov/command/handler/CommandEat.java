package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.Database;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Location;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Scope(value = "prototype")
public class CommandEat extends Command implements Runnable{
    private Update update;

    @Autowired
    public CommandEat(CatBot catBot, Database database) {
        super(catBot, database);
    }

    @Override
    public void useCommand(Update update) {
        this.update = update;
        new Thread(this).start();
    }

    @Override
    public void run() {
        User user = database.getUserById(update.getMessage().getChatId());
        Short userCondition = user.getCondition();
        if (userCondition == User.IN_GAME) {
            eat();
        } else if (userCondition == User.NOT_IN_GAME) {
            notInGameMessage();
        } else {
            otherConditionsMessage();
        }
    }

    private void eat(){
        Long userId = update.getMessage().getChatId();
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        Location location = database.getLocationByLocationId(cat.getLocationId());
        Long waitingTimeMillis = Long.valueOf(calculateMillisOfEating());
        if (isExistFood(location)){
            doEat(waitingTimeMillis, userId, cat);
            congratulationMessage();
        } else {
            foodIsNotExistMessage();
        }
    }

    @SneakyThrows
    private void doEat(Long waitingTimeMillis, Long userId, Cat cat){
        eatingMessage(waitingTimeMillis);
        database.setUserConditionByUserId(User.EATING, userId);
        Thread.sleep(waitingTimeMillis);
        cat.setSatiety(Math.min(cat.getSatiety() + 10, 100));
        database.setCat(cat);
        database.setUserConditionByUserId(User.IN_GAME, userId);
    }

    private Boolean isExistFood(Location location){
        return location.getFoodId() != null;
    }


    private Integer calculateMillisOfEating(){
        return 20 * 1000;
    }

    @SneakyThrows
    private void otherConditionsMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Выйдите из укрытия и завершите все действия..";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void notInGameMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Вы сейчас не в игре. Зайдите в игру, чтобы попить.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());

    }

    @SneakyThrows
    private void eatingMessage(Long waitingTimeMillis) {
        Long userId = update.getMessage().getChatId();
        String message = "Вы кушаете. Действие завершится через " + waitingTimeMillis/1000 + " секунд";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void foodIsNotExistMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "На этой локации нет еды.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }

    @SneakyThrows
    private void congratulationMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Действие завершено. Вы успешно поели.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }
}
