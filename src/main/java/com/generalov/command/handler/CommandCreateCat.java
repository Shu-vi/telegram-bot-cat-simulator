package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.Database;
import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Location;
import com.generalov.database.entity.User;
import com.generalov.string.handler.StringHandler;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Scope(value = "singleton")
public class CommandCreateCat extends Command{
    @Autowired
    public CommandCreateCat(CatBot catBot, Database database) {
        super(catBot, database);
    }

    /**
     * Передаёт создание кота в другой метод, но делает это если пользователь сейчас не в игре и у пользователя меньше 3 котов.
     * Если условие не выполняется, то сообщает пользователю об ошибке.
     */
    @Override
    public void useCommand(Update update) {
        Long userId = update.getMessage().getChatId();
        String message = StringHandler.deleteBotName(update.getMessage().getText());
        if (isCanCreateCat(userId)){
            addCat(message, userId);
        }else {
            catLimitOrInGameMessage(userId);
        }
    }

    /**
     * Метод создаёт кота. Поступают имя кота, порода, локация, пол, выстаскиваем эти данные из одного сообщения.
     * Породу, пол, локацию нужно немного подкорректировать на случай, если пользователю ввёл их с нижнего регистра.
     * Локацию нужно проверять на корректность.
     * Нужно проверить на корректность имя кота, у одного пользователя не должно быть повторений по имени.
     * Порода и пол приходят уже корректными.
     */
    private void addCat(String message, Long chatId){
        String catName = getCatName(message);
        String catBreed = StringHandler.toUpperCaseFirstChar(getCatBreed(message));
        Integer breedId = database.getBreedIdByBreed(catBreed);
        String locationTitle = StringHandler.toUpperCaseFirstChar(getLocationTitle(message));
        Location location = database.getLocationByLocationTitle(locationTitle);
        String catGender = StringHandler.toUpperCaseFirstChar(getCatGender(message));
        if (isWrongLocation(location.getId())){
            notExistLocationMessage(chatId);
        } else if (isExistCatName(catName, chatId)){
            nameIsNotFreeMessage(chatId);
        } else{
            Cat cat = new Cat(0, catName, catGender, breedId, 100, 100, 100, 100, location.getId(), chatId, false);
            database.addCat(cat);
            congratulationsMessage(chatId);
        }
    }

    /**
     * @return true, если у пользователя < 1 кота И состояние NOT_IN_GAME
     */
    private Boolean isCanCreateCat(Long userId){
        return database.getUserById(userId).getCondition() == User.NOT_IN_GAME && database.getCatsCountByUserId(userId)<1;
    }

    /**
     * Получает строку, достаёт от туда имя.
     * @param message "Создать кота: ИмяКота\nПорода: ПородаКота\nЛокация: ЛокацияСпавна\nПол: Кот/Кошка"
     * @return ИмяКота
     */
    private String getCatName(String message){
        message = message.substring(14);
        int i = 0;
        String catName = "";
        while (message.charAt(i) != '\n'){
            catName += message.charAt(i);
            i++;
        }
        return catName;
    }

    /**
     * Получает строку, достаёт от туда породу.
     * @param message "Создать кота: ИмяКота\nПорода: ПородаКота\nЛокация: ЛокацияСпавна\nПол: Кот/Кошка"
     * @return ПородаКота
     */
    private String getCatBreed(String message){
        message = message.substring(14);
        int i = 0;
        String catBreed = "";
        while (message.charAt(i) != '\n')
            i++;
        i += 9;
        while (message.charAt(i) != '\n'){
            catBreed += message.charAt(i);
            i++;
        }
        return catBreed;
    }

    /**
     * Получает строку, достаёт от туда локацию спавна.
     * @param message "Создать кота: ИмяКота\nПорода: ПородаКота\nЛокация: ЛокацияСпавна\nПол: Кот/Кошка"
     * @return ЛокацияСпавна
     */
    private String getLocationTitle(String message){
        message = message.substring(14);
        int i = 0;
        String location = "";
        while (message.charAt(i) != '\n')
            i++;
        i += 9;
        while (message.charAt(i) != '\n')
            i++;
        i += 10;
        while (message.charAt(i) != '\n'){
            location += message.charAt(i);
            i++;
        }
        return location;
    }

    /**
     * Получает строку, достаёт от туда пол персонажа.
     * @param message "Создать кота: ИмяКота\nПорода: ПородаКота\nЛокация: ЛокацияСпавна\nПол: Кот/Кошка"
     * @return Кот/Кошка
     */
    private String getCatGender(String message){
        message = message.substring(14);
        int i = 0;
        String gender = "";
        while (message.charAt(i) != '\n')
            i++;
        i += 9;
        while (message.charAt(i) != '\n')
            i++;
        i += 10;
        while (message.charAt(i) != '\n'){
            i++;
        }
        i += 6;
        while (i < message.length()){
            gender += message.charAt(i);
            i++;
        }
        return gender;
    }

    /**
     * @param locationId айди локации из БД
     * @return true, если локации не существует
     */
    private Boolean isWrongLocation(Integer locationId){
        return locationId == null;
    }

    private Boolean isExistCatName(String name, Long userId){
        return database.getCatByCatNameAndUserId(name, userId) != null;
    }

    /**
     * @param chatId - айди чата, куда отправлять сообщение
     * Метод вызывается, если все данные были корректно введены и персонаж был успешно создан, об этом сообщается пользователю.
     */
    @SneakyThrows
    private void congratulationsMessage(Long chatId){
        String message = "Персонаж успешно создан";
        catBot.execute(SendMessage.builder().chatId(chatId.toString()).text(message).build());
    }

    /**
     * Отправляет пользователю сообщение о том, что такое локации не существует. Вызывается, соответственно, после проверки
     * локации на некорректный ввод.
     */
    @SneakyThrows
    private void notExistLocationMessage(Long chatId){
        String message = "Введённой локации не существует. Проверьте локации, доступные для спавна командой \"Локации\".";
        catBot.execute(SendMessage.builder().chatId(chatId.toString()).text(message).build());
    }

    /**
     * Отправляет пользователю сообщение о том, что превышен лимит котов или он сейчас в игре.
     */
    @SneakyThrows
    private void catLimitOrInGameMessage(Long userId){
        String text = "Превышен лимит котов(1 максимум) или вы сейчас в игре.";
        catBot.execute(SendMessage.builder().text(text).chatId(userId.toString()).build());
    }

    /**
     * Отправляет пользователю сообщение о том, что у него уже есть кот с таким именем.
     */
    @SneakyThrows
    private void nameIsNotFreeMessage(Long userId){
        String text = "У вас уже есть кот с данным именем.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(text).build());
    }
}
