package com.generalov;

import com.generalov.command.handler.*;
import com.generalov.properties.GetProperties;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class CatBot extends TelegramLongPollingBot {
    private MessageType messageType;

    public CatBot(){
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
                || word.matches("^Пойти в ([А-я]+)$");
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

    //TODO доделать весь список команд
    /**
     * Метод обрабатывает введённые текстовые команды.
     */
    private void sendMessage(Update update) {
        String message = update.getMessage().getText();
        switch (message) {
            case "/help":
                new CommandHelp(this).help(update);
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
                new CommandEat(this).eat(update);
                return;
            case "Попить воды":
                new CommandDrink(this).drink(update);
                return;
            case "Излечиться травой":
                new CommandHealing(this).healing(update);
                return;
            case "Спать":
                new CommandSleep(this).sleep(update);
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
