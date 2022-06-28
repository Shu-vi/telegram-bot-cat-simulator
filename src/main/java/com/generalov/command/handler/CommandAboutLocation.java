package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.Database;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Location;
import com.generalov.database.entity.Shelter;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.ArrayList;

@Component
@Scope(value = "singleton")
public class CommandAboutLocation extends Command{
    @Autowired
    public CommandAboutLocation(CatBot catBot, Database database) {
        super(catBot, database);
    }

    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        Short condition = database.getUserById(userId).getCondition();
        if (condition != User.NOT_IN_GAME){
            aboutLocation(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }

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
        String message = getFormatMessage(location.getTitle(), cats, neighboringLocationsId, shelters, waterTitle, foodTitle, healthTitle);

        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }

    private String getFormatMessage(String titleLocation,
                                    ArrayList<Cat> cats,
                                    Integer[] neighboringLocationsId,
                                    ArrayList<Shelter> shelters,
                                    String waterTitle,
                                    String foodTitle,
                                    String healthTitle){
        String message = "";
        message += getTitleLocation(titleLocation);
        message += getCats(cats);
        message += getNeighboringLocations(neighboringLocationsId);
        message += getShelters(shelters);
        message += getWater(waterTitle);
        message += getFood(foodTitle);
        message += getHealth(healthTitle);
        return message;
    }

    /**
     * -----------------------------------------------------------------------
     * Методы для получения сообщений
     */
    private String getTitleLocation(String titleLocation){
        String message = locationTitleFormat(titleLocation);
        return message;
    }

    private String getCats(ArrayList<Cat> cats){
        String message = hasCat(cats)? catsFormat(cats) : catsNoText();
        return message;
    }

    private String getNeighboringLocations(Integer[] neighboringLocationsId){
        String message = neighboringLocationsFormat(neighboringLocationsId);
        return message;
    }

    private String getShelters(ArrayList<Shelter> shelters){
        String message = hasShelter(shelters)? sheltersFormat(shelters) : sheltersNoText();
        return  message;
    }

    private String getWater(String waterTitle){
        String message = hasWater(waterTitle)? waterFormat(waterTitle) : waterNoText();
        return message;
    }

    private String getFood(String foodTitle){
        String message = hasFood(foodTitle)? foodFormat(foodTitle) : foodNoText();
        return message;
    }

    private String getHealth(String healthTitle){
        String message = hasHealth(healthTitle)? healthFormat(healthTitle) : healthNoText();
        return message;
    }

    /**
     * ------------------------------------------------------------------------
     * Булевые функции проверки некоторых условий
     */
    private Boolean hasCat(ArrayList<Cat> cats){
        return cats.size() > 0;
    }

    private Boolean hasShelter(ArrayList<Shelter> shelters){
        return shelters.size() > 0;
    }

    private Boolean hasWater(String waterTitle){
        return waterTitle != null && !waterTitle.equals("");
    }

    private Boolean hasFood(String foodTitle){
        return foodTitle != null && !foodTitle.equals("");
    }

    private Boolean hasHealth(String healthTitle){
        return healthTitle != null & !healthTitle.equals("");
    }

    /**
     * ---------------------------------------------------------------------------
     * Форматирование строк
     */
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

    private String waterFormat(String waterTitle){
        return "\n\"" + waterTitle + "\" - водоём локации.";
    }

    private String foodFormat(String foodTitle){
        return "\n\"" + foodTitle + "\" - еда на локации.";
    }

    private String healthFormat(String healthTitle){
        return "\n\"" + healthTitle + "\" - исцеляющая трава на локации.";
    }

    private String waterNoText(){
        return "Водоёма на локации нет.";
    }

    private String foodNoText(){
        return "Еды на локации нет.";
    }

    private String healthNoText(){
        return "Лечебных растений на локации нет.";
    }

    /**
     *-------------------------------------------------------------------------------------------
     * Вывод сообщений пользователю
     */
    @SneakyThrows
    private void wrongConditionMessage(Long userId){
        String message = "Войдите в игре, чтобы посмотреть информацию о локации, на которой вы будете находиться.";
        catBot.execute(SendMessage.builder().text(message).chatId(userId.toString()).build());
    }
}