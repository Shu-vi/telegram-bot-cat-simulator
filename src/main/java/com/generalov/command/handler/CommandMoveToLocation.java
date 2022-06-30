package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.dao.cat.CatDao;
import com.generalov.database.dao.location.LocationDao;
import com.generalov.database.dao.user.UserDao;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Location;
import com.generalov.database.entity.User;
import com.generalov.string.handler.StringHandler;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
@Scope(value = "prototype")
public class CommandMoveToLocation extends Command implements Runnable{
    private Update update;
    private Command commandAboutLocation;
    //todo нужен рефакторинг
    private UserDao userDao;
    private CatDao catDao;
    private LocationDao locationDao;

    @Autowired
    public CommandMoveToLocation(CatBot catBot,
                                 @Qualifier(value = "commandAboutLocation") Command commandAboutLocation,
                                 UserDao userDao,
                                 CatDao catDao,
                                 LocationDao locationDao) {
        super(catBot);
        this.commandAboutLocation = commandAboutLocation;
        this.catDao = catDao;
        this.userDao = userDao;
        this.locationDao = locationDao;
    }

    @Override
    public void useCommand(Update update) {
        this.update = update;
        new Thread(this).start();
    }

    @Override
    public void run() {
        Long userId = update.getMessage().getChatId();
        String message = StringHandler.deleteBotName(update.getMessage().getText());
        Short userCondition = userDao.read(userId).getCondition();
        if (userCondition == User.IN_GAME){
            moveToLocation(userId, message);
            commandAboutLocation.useCommand(update);
        } else if (userCondition == User.NOT_IN_GAME) {
            notInGameMessage(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }

    private void moveToLocation(Long userId, String message){
        String locationTitle = StringHandler.toUpperCaseFirstChar(getLocationTitle(message));
        Cat cat = catDao.readCatByUserId(userId);
        Location currentLocation = locationDao.read(cat.getLocationId());
        Integer[] neighboringLocationId = currentLocation.getNeighboringLocationsId();
        Location wishesLocation = locationDao.readLocationByLocationTitle(locationTitle);
        if (isExistLocation(wishesLocation)){
            if (isContains(wishesLocation, neighboringLocationId)){
                if (!isLowStats(cat))
                    move(userId, cat, wishesLocation);
            }else {
                notNeighboringLocationMessage(userId);
            }
        }else {
            notExistLocationMessage(userId);
        }
    }

    @SneakyThrows
    private void move(Long userId, Cat cat, Location wishesLocation){
        User user = userDao.read(userId);
        user.setCondition(User.MOVING);
        userDao.update(user);
        movingMessage(userId);
        Thread.sleep(30000);
        editCatParametersDuringMoving(cat, wishesLocation);
        user.setCondition(User.IN_GAME);
        userDao.update(user);
        congratulationMessage(userId, wishesLocation.getTitle());
    }

    private void editCatParametersDuringMoving(Cat cat, Location location){
        cat.setLocationId(location.getId());
        cat.setStamina(cat.getStamina() - 3);
        cat.setHealth(cat.getHealth() - 1);
        cat.setSatiety(cat.getSatiety() - 10);
        cat.setWater(cat.getWater() - 7);
        catDao.update(cat);
    }

    /**
     * ----------------------------------------------------------
     * Методы форматирования строк
     */

    private String getLocationTitle(String message){
        return message.substring(8);
    }

    /**
     * ----------------------------------------------------------
     * Булевые методы проверки условий
     */

    private Boolean isExistLocation(Location location){
        return location != null;
    }

    private Boolean isContains(Location wishesLocation, Integer[] checkingLocation){
        boolean flag = false;
        for (Integer integer : checkingLocation) {
            flag = flag || integer == wishesLocation.getId();
        }
        return flag;
    }

    private Boolean isLowStats(Cat cat){
        return cat.getStamina() <= 3 || cat.getHealth() <= 1 || cat.getWater() <= 7 || cat.getSatiety() <= 10;
    }

    /**
     * ------------------------------------------------------
     * Методы отправки сообщений
     */

    @SneakyThrows
    private void notExistLocationMessage(Long userId){
        String message = "Такой локации не существует.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void notNeighboringLocationMessage(Long userId){
        String message = "Вы не можете пойти в данную локацию, поскольку она не граничит с той локацией, в которой находитесь вы.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void notInGameMessage(Long userId){
        String  message = "Для этого действия необходимо зайти в игру.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void wrongConditionMessage(Long userId){
        String message = "Выйдите из укрытия и дождитесь завершения всех действий.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void congratulationMessage(Long userId, String locationTitle){
        String message = "Вы успешно перешли в локацию " + locationTitle + ".";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void movingMessage(Long userId){
        String message = "Начался переход в другую локацию, это займёт 30 секунд.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }
}
