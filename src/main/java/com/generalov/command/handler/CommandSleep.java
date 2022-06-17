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

    /**
     * Вызывает методы в зависимости от состояния пользователя. Если пользователь в укрытии,
     * то вызывает метод сна. Если пользователь в игре, но не в укрытии, то сообщает об этом.
     * Если пользователь не в игре, то сообщает об этом. Если пользователь имеет некое другое состояние,
     * то сообщает об этом ему.
     */
    @Override
    public void run() {
        Short userCondition = database.getUserById(update.getMessage().getChatId()).getCondition();
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

    /**
     * Метод отвечает за сон и отправляет пользователя в сон, если в этом есть необходимость, иначе сообщает,
     * что пользователю не нужен сон.
     */
    private void sleep(){
        Long userId = update.getMessage().getChatId();
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        Long waitingTimeMillis = calculateWaitingTimeMillis(cat.getStamina());
        if (isCanSleep(waitingTimeMillis)){
            sleeping(cat, waitingTimeMillis, userId);
        } else {
            catAwakeMessage();
        }
    }

    /**
     * Метод "усыпляет кота". Изменяет состояние пользователя, восстанавливает выносливость
     * коту, выводит сообщения о действиях, которые происходят, усыпляет поток на некоторое
     * время и возвращает состояние на те, что были ранее, выводя сообщение о завершении действий.
     * Здесь же происходит работа с БД.
     */
    @SneakyThrows
    private void sleeping(Cat cat, Long waitingTimeMillis, Long userId){
        database.setUserConditionByUserId(User.SLEEPING, userId);
        cat.setStamina(100);
        database.setCat(cat);
        catSleepingMessage(waitingTimeMillis);
        Thread.sleep(waitingTimeMillis);
        database.setUserConditionByUserId(User.IN_SHELTER, userId);
        catWakeupMessage();
    }

    /**
     * ------------------------------------------------------------------
     * Методы промежуточных вычислений
     */

    /**
     * Считает время сна в миллисекундах по формуле: (МаксимальнаяВыносливость - ТекущаяВыносливость) * 6000;
     */
    private Long calculateWaitingTimeMillis(Integer catStamina){
        return (100 - catStamina) * 6000L;
    }

    /**
     * ---------------------------------------------------------------------
     * Булевы методы, отвечающие за проверку условий
     */

    /**
     * Возвращает True, если у пользователя неполная выносливость и он может спать.
     */
    private Boolean isCanSleep(Long waitingTimeMillis){
        return waitingTimeMillis > 0;
    }

    /**
     * --------------------------------------------------------------------
     * Методы отправки сообщений
     */

    /**
     * Отправляет пользователю сообщение о том, что его кот лёг спать, указывая время в секундах, сколько кот будет спать.
     */
    @SneakyThrows
    private void catSleepingMessage(Long waitingTimeMillis){
        Long userId = update.getMessage().getChatId();
        String message = "Вы легли спать. Время сна - " + waitingTimeMillis/1000 + " секунд.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    /**
     * Отправляет пользователю сообщение о том, что его кот проснулся и выносливость восстановлена.
     */
    @SneakyThrows
    private void catWakeupMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Вы проснулись, вся выносливость восстановлена.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    /**
     * Отправляет пользователю сообщение о том, что у него полная выносливость
     * (Срабатывает, если пользователь пытается поспать
     * с полной выносливостью).
     */
    @SneakyThrows
    private void catAwakeMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "У вас полная выносливость.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    /**
     * Отправляет пользователю сообщение о том, что он не в игре.
     */
    @SneakyThrows
    private void notInGameMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Вы не в игре.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    /**
     * Отправляет пользователю сообщение, что он не может лечь спать, не зайдя в укрытие. Срабатывает,
     *  когда пользователь в игре, но не в укрытии.
     */
    @SneakyThrows
    private void inGameMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Сперва зайдите в укрытие, потом сможете лечь спать.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    /**
     * Срабатывает, если пользователь пытается поспать во всех других нестандартных случаях и говорит ему, что
     * он не может сделать это сейчас.
     */
    @SneakyThrows
    private void otherConditionsMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Дождитесь завершения всех действий, а затем зайдите в укрытие.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }
}
