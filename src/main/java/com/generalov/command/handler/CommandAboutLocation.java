package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Location;
import com.generalov.database.entity.Shelter;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

public class CommandAboutLocation extends Command{
    public CommandAboutLocation(CatBot catBot) {
        super(catBot);
    }

    //TODO вода, еда и т.д., сделать, чтобы не учитывала твоего кота и тех, кто не в игре
    @SneakyThrows
    private void aboutLocation(Long userId){
        Cat cat = database.getCatByUserIdAndCatStatus(userId, true);
        Integer locationId = cat.getLocationId();
        Location location = database.getLocationByLocationId(locationId);
        ArrayList<Cat> cats = database.getCatsByLocationId(locationId);
        Integer[] neighboringLocationsId = location.getNeighboringLocationsId();
        String foodTitle = database.getFoodTitleById(location.getFoodId());
        String waterTitle = database.getWaterTitleById(location.getWaterId());
        String healthTitle = database.getHealthTitleById(location.getHealthId());
        ArrayList<Shelter> shelters = database.getSheltersByLocationId(locationId);
        String message = "";
        message += locationTitleFormat(location.getTitle());
        if (hasCat(cats)){
            message += catsFormat(cats);
        }else {
            message += catsNoText();
        }
        message += neighboringLocationsFormat(neighboringLocationsId);
        if (hasShelter(shelters)){
            message += sheltersFormat(shelters);
        }else {
            message += sheltersNoText();
        }
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }

    @SneakyThrows
    public void aboutLocation(Update update){
        Long userId = update.getMessage().getChatId();
        Short condition = database.getUserById(userId).getCondition();
        if (condition != User.NOT_IN_GAME){
            aboutLocation(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }

    private Boolean hasCat(ArrayList<Cat> cats){
        return cats.size() > 0;
    }

    private Boolean hasShelter(ArrayList<Shelter> shelters){
        return shelters.size() > 0;
    }

    private String locationTitleFormat(String locationTitle){
        return "Вы находитесь в локации: " + locationTitle;
    }

    private String catsFormat(ArrayList<Cat> cats){
        String message = "\nКоты на локации:";
        for (int i = 0; i < cats.size(); i++) {
            message += "\n   " + cats.get(i).getGender() + " " + cats.get(i).getName() + ";";
        }
        return message;
    }

    private String sheltersFormat(ArrayList<Shelter> shelters){
        String message = "\nУкрытия на локации:";
        for (int i = 0; i < shelters.size(); i++) {
            message += "\n   " + shelters.get(i).getTitle() + ", всего мест в укрытии - " + shelters.get(i).getCapacity() + ";";
        }
        return message;
    }

    private String neighboringLocationsFormat(Integer[] neighboringLocationsId){
        String message = "\nСоседние локации:";
        for (int i = 0; i < neighboringLocationsId.length; i++) {
            Location location = database.getLocationByLocationId(neighboringLocationsId[i]);
            message += "\n   " + location.getTitle() + ";";
        }
        return message;
    }

    private String catsNoText(){
        return "\nНа локации нет котов, кроме Вас.";
    }

    private String sheltersNoText(){
        return "\nНа данной локации нет укрытий.";
    }

    @SneakyThrows
    private void wrongConditionMessage(Long userId){
        String message = "Войдите в игре, чтобы посмотреть информацию о локации, на которой вы будете находиться.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }
}
