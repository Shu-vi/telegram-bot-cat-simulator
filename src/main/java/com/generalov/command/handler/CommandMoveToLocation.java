package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Location;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandMoveToLocation extends Command implements Runnable{
    private Update update;
    private CommandAboutLocation commandAboutLocation;
    public CommandMoveToLocation(CatBot catBot, Update update, CommandAboutLocation commandAboutLocation) {
        super(catBot);
        this.update = update;
        this.commandAboutLocation = commandAboutLocation;
    }

    private void moveToLocation(Long userId, String message){
        /**
         * достаём название локации.
         * Проверяем, есть ли эта локация в соседних.
         * Переводим в соседнюю локацию
         */
        String locationTitle = normalizeData(getLocationTitle(message));
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        Location currentLocation = database.getLocationByLocationId(cat.getLocationId());
        Integer[] neighboringLocationId = currentLocation.getNeighboringLocationsId();
        Location wishesLocation = database.getLocationByLocationTitle(locationTitle);
        if (isExistLocation(wishesLocation)){
            if (isContains(wishesLocation, neighboringLocationId)){
                cat.setLocationId(wishesLocation.getId());
                database.setCat(cat);
                congratulationMessage(userId, wishesLocation.getTitle());
            }else {
                notNeighboringLocationMessage(userId);
            }
        }else {
            notExistLocation(userId);
        }
    }

    @SneakyThrows
    private void congratulationMessage(Long userId, String locationTitle){
        String message = "Вы успешно перешли в локацию " + locationTitle + ".";
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

    private String normalizeData(String message){
        return Character.toUpperCase(message.charAt(0)) + message.substring(1).toLowerCase();
    }


    private String getLocationTitle(String message){
        return message.substring(8);
    }

    private Boolean isExistLocation(Location location){
        return location != null;
    }

    private Boolean isContains(Location wishesLocation, Integer[] checkingLocation){
        Boolean flag = false;
        for (int i = 0; i < checkingLocation.length; i++) {
            flag = flag || checkingLocation[i] == wishesLocation.getId();
        }
        return flag;
    }

    @SneakyThrows
    private void notExistLocation(Long userId){
        String message = "Такой локации не существует.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @SneakyThrows
    private void notNeighboringLocationMessage(Long userId){
        String message = "Вы не можете пойти в данную локацию, поскольку она не граничит с той локацией, в которой находитесь вы.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).build());
    }

    @Override @SneakyThrows
    public void run() {
        Long userId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        Short userCondition = database.getUserById(userId).getCondition();
        if (userCondition == User.IN_GAME){
            database.setUserConditionByUserId(User.MOVING, userId);
            Thread.sleep(30000);
            moveToLocation(userId, message);
            commandAboutLocation.aboutLocation(update);
            database.setUserConditionByUserId(User.IN_GAME, userId);
        } else if (userCondition == User.NOT_IN_GAME) {
            notInGameMessage(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }
}
