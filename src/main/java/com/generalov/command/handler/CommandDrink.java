package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.dao.breed.BreedDao;
import com.generalov.database.dao.cat.CatDao;
import com.generalov.database.dao.location.LocationDao;
import com.generalov.database.dao.user.UserDao;
import com.generalov.database.entity.Breed;
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
public class CommandDrink extends Command implements Runnable{
    private Update update;
    private UserDao userDao;
    private CatDao catDao;
    private LocationDao locationDao;
    private BreedDao breedDao;

    @Autowired
    public CommandDrink(CatBot catBot, UserDao userDao, CatDao catDao, LocationDao locationDao, BreedDao breedDao) {
        super(catBot);
        this.userDao = userDao;
        this.catDao = catDao;
        this.locationDao = locationDao;
        this.breedDao = breedDao;
    }

    @Override
    public void useCommand(Update update) {
        this.update = update;
        new Thread(this).start();
    }

    @Override
    public void run() {
        Short userCondition = userDao.read(update.getMessage().getChatId()).getCondition();
        if (userCondition == User.IN_GAME) {
            drink();
        } else if (userCondition == User.NOT_IN_GAME) {
            notInGameMessage();
        } else {
            otherConditionsMessage();
        }
    }

    private void drink(){
        Long userId = update.getMessage().getChatId();
        Cat cat = catDao.readCatByUserId(userId);
        cat.setBreed(breedDao.read(cat.getBreedId()));
        Location location = locationDao.read(cat.getLocationId());
        Long waitingTimeMillis = Long.valueOf(calculateMillisOfDrinking(cat));
        if (isExistWater(location)){
            doDrinking(waitingTimeMillis, cat);
            congratulationMessage();
        } else {
            waterIsNotExistMessage();
        }
    }

    @SneakyThrows
    private void doDrinking(Long waitingTimeMillis, Cat cat){
        drinkingMessage(waitingTimeMillis);
        User user = userDao.read(update.getMessage().getChatId());
        user.setCondition(User.DRINKING);
        userDao.update(user);
        Thread.sleep(waitingTimeMillis);
        cat.setWater(cat.getBreed().getMaxWater());
        catDao.update(cat);
        user.setCondition(User.IN_GAME);
        userDao.update(user);
    }

    private Boolean isExistWater(Location location){
        return location.getWaterId() != null;
    }

    @SneakyThrows
    private void notInGameMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Вы сейчас не в игре. Зайдите в игру, чтобы попить.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());

    }

    @SneakyThrows
    private void drinkingMessage(Long waitingTimeMillis){
        Long userId = update.getMessage().getChatId();
        String message = "Вы пьёте. Действие завершится через " + waitingTimeMillis/1000 + " секунд";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void congratulationMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Действие завершено. Вы успешно попили.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void otherConditionsMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "Выйдите из укрытия и завершите все действия..";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void waterIsNotExistMessage(){
        Long userId = update.getMessage().getChatId();
        String message = "На этой локации нет воды.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }

    private Integer calculateMillisOfDrinking(Cat cat){
        return (cat.getBreed().getMaxWater() - cat.getWater()) * 6 * 1000;
    }
}
