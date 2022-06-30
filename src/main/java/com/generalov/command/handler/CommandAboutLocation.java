package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.dao.cat.CatDao;
import com.generalov.database.dao.item.food.FoodDao;
import com.generalov.database.dao.item.health.HealthDao;
import com.generalov.database.dao.item.water.WaterDao;
import com.generalov.database.dao.location.LocationDao;
import com.generalov.database.dao.shelter.ShelterDao;
import com.generalov.database.dao.user.UserDao;
import com.generalov.database.entity.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.List;

@Component
@Scope(value = "singleton")
public class CommandAboutLocation extends Command{
    private UserDao userDao;
    private CatDao catDao;
    private LocationDao locationDao;
    private FoodDao foodDao;
    private WaterDao waterDao;
    private HealthDao healthDao;
    private ShelterDao shelterDao;
    @Autowired
    public CommandAboutLocation(CatBot catBot,
                                UserDao userDao,
                                CatDao catDao,
                                LocationDao locationDao,
                                FoodDao foodDao,
                                WaterDao waterDao,
                                HealthDao healthDao,
                                ShelterDao shelterDao) {
        super(catBot);
        this.userDao = userDao;
        this.catDao = catDao;
        this.locationDao = locationDao;
        this.foodDao = foodDao;
        this.waterDao = waterDao;
        this.healthDao = healthDao;
        this.shelterDao = shelterDao;
    }

    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        User user = userDao.read(userId);
        Short condition = user.getCondition();
        if (condition != User.NOT_IN_GAME){
            aboutLocation(user);
        } else {
            wrongConditionMessage(userId);
        }
    }

    @SneakyThrows
    private void aboutLocation(User user){
        Cat cat = catDao.readCatByUserId(user.getId());
        Location location = locationDao.read(cat.getLocationId());
        List<Cat> cats = catDao.readCatsByLocationId(location.getId());
        ItemFood itemFood = foodDao.read(location.getFoodId());
        ItemWater itemWater = waterDao.read(location.getWaterId());
        ItemHealth itemHealth = healthDao.read(location.getHealthId());
        List<Shelter> shelters = shelterDao.readSheltersByLocationId(location.getId());
        String message = getFormatMessage(location.getTitle(), cats, location.getNeighboringLocationsId(), shelters, itemWater.getTitle(), itemFood.getTitle(), itemHealth.getTitle());
        catBot.execute(SendMessage.builder().text(message).chatId(user.getId().toString()).build());
    }

    private String getFormatMessage(String titleLocation,
                                    List<Cat> cats,
                                    Integer[] neighboringLocationsId,
                                    List<Shelter> shelters,
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

    private String getCats(List<Cat> cats){
        String message = hasCat(cats)? catsFormat(cats) : catsNoText();
        return message;
    }

    private String getNeighboringLocations(Integer[] neighboringLocationsId){
        String message = neighboringLocationsFormat(neighboringLocationsId);
        return message;
    }

    private String getShelters(List<Shelter> shelters){
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
    private Boolean hasCat(List<Cat> cats){
        return cats.size() > 0;
    }

    private Boolean hasShelter(List<Shelter> shelters){
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

    private String catsFormat(List<Cat> cats){
        String message = "\nКоты на локации:";
        for (int i = 0; i < cats.size(); i++) {
            message += "\n   " + cats.get(i).getGender() + " " + cats.get(i).getName() + ";";
        }
        return message;
    }

    private String sheltersFormat(List<Shelter> shelters){
        String message = "\nУкрытия на локации:";
        for (int i = 0; i < shelters.size(); i++) {
            message += "\n   " + shelters.get(i).getTitle() + ", всего мест в укрытии - " + shelters.get(i).getCapacity() + ";";
        }
        return message;
    }

    private String neighboringLocationsFormat(Integer[] neighboringLocationsId){
        String message = "\nСоседние локации:";
        for (int i = 0; i < neighboringLocationsId.length; i++) {
            Location location = locationDao.read(neighboringLocationsId[i]);
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