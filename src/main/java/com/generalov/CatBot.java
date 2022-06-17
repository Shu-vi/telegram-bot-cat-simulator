package com.generalov;

import com.generalov.command.handler.*;
import com.generalov.properties.GetProperties;
import com.generalov.string.handler.StringHandler;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.ArrayList;
import java.util.List;

public class CatBot extends TelegramLongPollingBot {
    private MessageType messageType;
    /**
     * Объект клавиатуры для команды /help
     */
    private InlineKeyboardMarkup outGameKeyboard;
    /**
     * Объект клавиатуры для команды /help
     */
    private InlineKeyboardMarkup inGameKeyboard;


    public CatBot(){
        initOutGameKeyboard();
        initInGameKeyboard();
    }

    @Override
    public String getBotUsername() {
        return GetProperties.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return GetProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        typeUpdate(update);
        sendAnswer(update);
    }

    /**
     * Метод изменяет состояние поля messageType, в зависимости от того, какой update поступает.
     */
    private synchronized void typeUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && commandWord(update.getMessage().getText()))
            this.messageType = MessageType.MESSAGE;
        else
            this.messageType = MessageType.OTHER;
    }

    //TODO дописать регулярки на некоторые команды
    /**
     * @param word поступающее слово, которое нужно проверить, является ли оно командным.
     * @return возвращает true, если слово является командой бля бота.
     */
    private Boolean commandWord(String word) {
        return word.equals("/start")
                || word.equals("/help")
                || word.equals("Мои коты")
                || word.equals("Локации")
                || word.equals("О локации")
                || word.equals("Съесть дичь")
                || word.equals("Попить воды")
                || word.equals("Излечиться травой")
                || word.equals("Спать")
                || word.equals("Выйти из укрытия")
                || word.equals("Выйти из игры")
                || word.matches("^(С|с)оздать кота: ([А-яA-z]+)\\nПорода: ((Д|д)линнолапый|(К|к)репкий|(М|м)ускулистый)\\nЛокация: ([А-я]+)\\nПол: (((К|к)от)|((К|к)ошка))$")
                || word.matches("^Зайти на ([А-яA-z]+)$")
                || word.matches("^Пойти в ([А-я]+)$")
                || word.matches("^Зайти в укрытие ([А-я ]+)$")
                || word.contains("@cat_1_simulator_bot ");
    }

    /**
     * Метод, в зависимости от типа Update, вызывает метод его обработки.
     */
    private synchronized void sendAnswer(Update update) {
        switch (this.messageType) {
            case MESSAGE -> sendMessage(update);
            case OTHER -> sendOther(update);
        }
    }

    /**
     * Метод обрабатывает введённые текстовые команды.
     */
    private void sendMessage(Update update) {
        String message = update.getMessage().getText();
        message = StringHandler.deleteBotName(message);
        switch (message) {
            case "/help":
                new CommandHelp(this, outGameKeyboard, inGameKeyboard).help(update);
                return;
            case "Мои коты":
                new CommandMyCats(this).myCats(update);
                return;
            case "/start":
                new CommandStart(this).start(update);
                return;
            case "Локации":
                new CommandLocations(this).locations(update);
                return;
            case"О локации":
                new CommandAboutLocation(this).aboutLocation(update);
                return;
            case "Съесть дичь":
                new Thread(new CommandEat(this, update)).start();
                return;
            case "Попить воды":
                new Thread(new CommandDrink(this, update)).start();
                return;
            case "Излечиться травой":
                new Thread(new CommandHealing(this, update)).start();
                return;
            case "Спать":
                new Thread(new CommandSleep(this, update)).start();
                return;
            case "Выйти из укрытия":
                new CommandExitFromShelter(this).exitFromShelter(update);
                return;
            case "Выйти из игры":
                new CommandExitFromGame(this).exitFromGame(update);
                return;
            default:
                if (message.matches("^(С|с)оздать кота: ([А-яA-z]+)\\nПорода: ((Д|д)линнолапый|(К|к)репкий|(М|м)ускулистый)\\nЛокация: ([А-я]+)\\nПол: (((К|к)от)|((К|к)ошка))$")){
                    new CommandCreateCat(this).createCat(update);
                } else if (message.matches("^Зайти на ([А-яA-z]+)$")) {
                    new CommandEnterOn(this).enterOn(update);
                    new CommandAboutLocation(this).aboutLocation(update);
                } else if (message.matches("^Пойти в ([А-я]+)$")) {
                    new Thread(new CommandMoveToLocation(this, update, new CommandAboutLocation(this))).start();
                } else if (message.matches("^Зайти в укрытие ([А-я ]+)$")) {
                    new CommandEnterToShelter(this).enterToShelter(update);
                }
                return;
        }
    }


    /**
     * Метод отправляет пользователю сообщение о том, что действие нераспознанное.
     */
    @SneakyThrows
    private void sendOther(Update update) {
        execute(SendMessage.builder().chatId(update.getMessage().getChatId().toString()).text("Действие нераспознано. Пожалуйста, вызовите помощь командой /help").build());
    }

    /**
     * Клавиатура для команды помощи вне игры.
     */
    private void initOutGameKeyboard(){
        outGameKeyboard = new InlineKeyboardMarkup();
        /**
         * Первый ряд кнопок
         */
        List<InlineKeyboardButton> buttonsRow1 = new ArrayList<>();
        buttonsRow1.add(InlineKeyboardButton.builder().text("Мои коты").switchInlineQueryCurrentChat("Мои коты").build());
        buttonsRow1.add(InlineKeyboardButton.builder().text("Локации для спавна").switchInlineQueryCurrentChat("Локации").build());
        /**
         * Второй ряд кнопок
         */
        List<InlineKeyboardButton> buttonsRow2 = new ArrayList<>();
        buttonsRow2.add(InlineKeyboardButton.builder().text("Создать кота").switchInlineQueryCurrentChat("Создать кота: ИмяКота\nПорода: длиннолапый\nЛокация: Деревня\nПол: кот").build());
        buttonsRow2.add(InlineKeyboardButton.builder().text("Зайти в игру").switchInlineQueryCurrentChat("Зайти на ИмяКота").build());
        /**
         * Объединение рядов
         */
        List<List<InlineKeyboardButton>> rowArrayList = new ArrayList<>();
        rowArrayList.add(buttonsRow1);
        rowArrayList.add(buttonsRow2);
        /**
         * Добавление рядов в объект клавиатуры
         */
        outGameKeyboard.setKeyboard(rowArrayList);
    }

    /**
     * Клавиатура для команды помощи в игре.
     */
    private void initInGameKeyboard(){
        inGameKeyboard = new InlineKeyboardMarkup();
        /**
         * Первый ряд кнопок
         */
        List<InlineKeyboardButton> buttonsRow1 = new ArrayList<>();
        buttonsRow1.add(InlineKeyboardButton.builder().text("О текущей локации").switchInlineQueryCurrentChat("О локации").build());
        buttonsRow1.add(InlineKeyboardButton.builder().text("Покушать").switchInlineQueryCurrentChat("Съесть дичь").build());
        buttonsRow1.add(InlineKeyboardButton.builder().text("Попить").switchInlineQueryCurrentChat("Попить воды").build());
        /**
         * Второй ряд кнопок
         */
        List<InlineKeyboardButton> buttonsRow2 = new ArrayList<>();
        buttonsRow2.add(InlineKeyboardButton.builder().text("Восстановить здоровье").switchInlineQueryCurrentChat("Излечиться травой").build());
        buttonsRow2.add(InlineKeyboardButton.builder().text("Смена локации").switchInlineQueryCurrentChat("Пойти в НазваниеЛокации").build());
        buttonsRow2.add(InlineKeyboardButton.builder().text("Зайти в укрытие").switchInlineQueryCurrentChat("Зайти в укрытие НазваниеУкрытия").build());
        /**
         * Третий ряд кнопок
         */
        List<InlineKeyboardButton> buttonsRow3 = new ArrayList<>();
        buttonsRow3.add(InlineKeyboardButton.builder().text("Выйти из укрытия").switchInlineQueryCurrentChat("Выйти из укрытия").build());
        buttonsRow3.add(InlineKeyboardButton.builder().text("Спать").switchInlineQueryCurrentChat("Спать").build());
        buttonsRow3.add(InlineKeyboardButton.builder().text("Выйти из игры").switchInlineQueryCurrentChat("Выйти из игры").build());
        /**
         * Объединение рядов
         */
        List<List<InlineKeyboardButton>> rowArrayList = new ArrayList<>();
        rowArrayList.add(buttonsRow1);
        rowArrayList.add(buttonsRow2);
        rowArrayList.add(buttonsRow3);
        /**
         * Добавление рядов в объект клавиатуры
         */
        inGameKeyboard.setKeyboard(rowArrayList);
    }

    @SneakyThrows
    public static void main(String[] args){
        CatBot catBot = new CatBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(catBot);
    }

    private enum MessageType {
        MESSAGE, OTHER
    }
}
