package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandSleep extends Command implements Runnable{
    private Update update;
    public CommandSleep(CatBot catBot, Update update){
        super(catBot);
        this.update = update;
    }

    @SneakyThrows
    private void sleep(){
        Long userId = update.getMessage().getChatId();
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        Long waitingTimeMillis = calculateWaitingTimeMillis(cat.getStamina());
        if (waitingTimeMillis > 0){
            database.setUserConditionByUserId(User.SLEEPING, userId);
            cat.setStamina(100);
            database.setCat(cat);
            catSleepingMessage(waitingTimeMillis);
            Thread.sleep(waitingTimeMillis);
            database.setUserConditionByUserId(User.IN_SHELTER, userId);
            catWakeupMessage();
        } else {
            catAwakeMessage();
        }
        /**
         * Проверить, где находится кот. Если в укрытии, то лечь спать.
         * Проснуться по истечение времени N.
         * Сообщить чё-то пользователю
         */
    }

    @Override
    public void run() {
        User user = database.getUserById(update.getMessage().getChatId());
        Short userCondition = user.getCondition();
        if (userCondition == User.IN_SHELTER) {
            sleep();
        } else if (userCondition == User.IN_GAME) {
            inGameMessage();
        } else if(userCondition == User.NOT_IN_GAME) {
            notInGameMessage();
        } else {
            otherConditionsMessage();
        }
    }

    private Long calculateWaitingTimeMillis(Integer catStamina){
        return (100 - catStamina) * 6000L;
    }

    @SneakyThrows
    private void catSleepingMessage(Long waitingTimeMillis){
        Long userId = update.getMessage().getChatId();
        String message = "Вы легли спать. Время сна - " + waitingTimeMillis/1000 + " секунд.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void catWakeupMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Вы проснулись, вся выносливость восстановлена.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void catAwakeMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "У вас полная выносливость.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void notInGameMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Вы не в игре.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void inGameMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Сперва зайдите в укрытие, потом сможете лечь спать.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void otherConditionsMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Дождитесь завершения всех действий, а затем зайдите в укрытие.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }
}
