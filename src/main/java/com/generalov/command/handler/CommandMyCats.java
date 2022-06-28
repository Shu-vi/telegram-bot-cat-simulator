package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.Database;
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

@Component
@Scope(value = "singleton")
public class CommandMyCats extends Command{
    @Autowired
    public CommandMyCats(CatBot catBot, Database database) {
        super(catBot, database);
    }

    /**
     * Метод проверяет состояние пользователя, если он не в игре, то показывает пользователю его котов, иначе говорит
     * пользователю, что надо выйти из игры.
     */
    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        Short userCondition = database.getUserById(userId).getCondition();
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
        ArrayList<Cat> cats = database.getCatsListByUserId(userId);
        String text = "";
        if (!isCatExist(cats)){
            text += catsNoText();
        } else {
            for (int i = 0; i < cats.size(); i++) {
                String breedTitle = database.getBreedByBreedId(cats.get(i).getBreedId());
                Breed breed = database.getBreed(breedTitle);
                text += getCatInfo(i+1, cats.get(i), breed);
            }
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
    private String getCatInfo(Integer catNumber, Cat cat, Breed breed){
        return catNumber + ") " + cat.getGender() + " " + cat.getName() + " породы " +
                breed.getTitle() + //Скорее всего надо изменить
                " на локации " + database.getLocationByLocationId(cat.getLocationId()).getTitle() + "." +
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
    private Boolean isCatExist(ArrayList<Cat> cats){
        return cats.size() > 0;
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
