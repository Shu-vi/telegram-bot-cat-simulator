package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.dao.breed.BreedDao;
import com.generalov.database.dao.cat.CatDao;
import com.generalov.database.dao.location.LocationDao;
import com.generalov.database.dao.user.UserDao;
import com.generalov.database.entity.Breed;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = "singleton")
public class CommandMyCats extends Command{
    private UserDao userDao;
    private CatDao catDao;
    private BreedDao breedDao;
    private LocationDao locationDao;

    @Autowired
    public CommandMyCats(CatBot catBot, UserDao userDao, CatDao catDao, BreedDao breedDao, LocationDao locationDao) {
        super(catBot);
        this.userDao = userDao;
        this.catDao = catDao;
        this.breedDao = breedDao;
        this.locationDao = locationDao;
    }

    /**
     * Метод проверяет состояние пользователя, если он не в игре, то показывает пользователю его котов, иначе говорит
     * пользователю, что надо выйти из игры.
     */
    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        Short userCondition = userDao.read(userId).getCondition();
        if (userCondition == User.NOT_IN_GAME){
            getCats(userId);
        } else {
            wrongConditionMessage(userId);
        }
    }

    /**
     * Проверяет, есть ли у пользователя коты. Если нет, то печатает информацию об этом, иначе печатает информацию о котах.
     */
    @SneakyThrows
    private void getCats(Long userId){
        Cat cat = catDao.readCatByUserId(userId);
        String text = "";
        if (!isCatExist(cat)){
            text += catsNoText();
        } else {
                Breed breed = breedDao.read(cat.getBreedId());
                text += getCatInfo(cat, breed);
        }
        catBot.execute(SendMessage.builder().text(text).chatId(userId.toString()).build());
    }

    /**
     * ---------------------------------------------------
     * Методы форматирования строк
     */

    /**
     * Возвращает форматированную строку с информацией о коте пользователя.
     */
    private String getCatInfo(Cat cat, Breed breed){
        return cat.getGender() + " " + cat.getName() + " породы " +
                breed.getTitle() + //Скорее всего надо изменить
                " на локации " + locationDao.read(cat.getLocationId()).getTitle() + "." +
                "\n   Параметры:" +
                "\n      Здоровье: " + cat.getHealth() + " / " + (breed.getMaxHealth() == null? "Бесконечность" : breed.getMaxHealth()) +
                "\n      Жажда: " + cat.getWater() + " / " + (breed.getMaxWater() == null? "Бесконечность" : breed.getMaxWater()) +
                "\n      Голод: " + cat.getSatiety() + " / " + (breed.getMaxSatiety() == null? "Бесконечность" : breed.getMaxSatiety()) +
                "\n      Выносливость: " + cat.getStamina() + " / " + (breed.getMaxStamina() == null? "Бесконечность" : breed.getMaxStamina()) + "\n\n";
    }

    private String catsNoText(){
        return "У вас нет ни одного кота.";
    }

    /**
     * ------------------------------------------------------------
     * Булевые методы для проверки условий
     */

    /**
     * Возвращает True, если у пользователя есть коты.
     */
    private Boolean isCatExist(Cat cat){
        return cat != null;
    }

    /**
     * --------------------------------------------------------------
     * Методы отправки сообщений
     */

    /**
     * Сообщает пользователю о том, что данную команду можно использовать только не в игре.
     */
    @SneakyThrows
    private void wrongConditionMessage(Long userId){
        String text = "Данную команду невозможно выполнить, находясь в игре. Для начала выйдите из игры командой \"Выйти из игры\".";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(text).build());
    }
}
