package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.dao.cat.CatDao;
import com.generalov.database.dao.location.LocationDao;
import com.generalov.database.dao.user.UserDao;
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
public class CommandHealing extends Command implements Runnable{
    private Update update;
    private UserDao userDao;
    private CatDao catDao;
    private LocationDao locationDao;

    @Autowired
    public CommandHealing(CatBot catBot, UserDao userDao, CatDao catDao, LocationDao locationDao) {
        super(catBot);
        this.userDao = userDao;
        this.catDao = catDao;
        this.locationDao = locationDao;
    }

    @Override
    public void useCommand(Update update) {
        this.update = update;
        new Thread(this).start();
    }

    @Override
    public void run() {
        User user = userDao.read(update.getMessage().getChatId());
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
        Cat cat = catDao.readCatByUserId(userId);
        Location location = locationDao.read(cat.getLocationId());
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
        User user = userDao.read(userId);
        user.setCondition(User.HEALING);
        userDao.update(user);
        Thread.sleep(waitingTimeMillis);
        cat.setHealth(Math.min(cat.getHealth() + 7, 100));
        catDao.update(cat);
        user.setCondition(User.IN_GAME);
        userDao.update(user);
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
