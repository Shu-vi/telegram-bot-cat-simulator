package com.generalov;

import com.generalov.command.handler.Command;
import com.generalov.properties.SpringConfig;
import com.generalov.string.handler.StringHandler;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Scope("singleton")
public class CatBot extends TelegramLongPollingBot {
    private MessageType messageType;
    @Value("${botUsername}")
    private String botUsername;
    @Value("${botToken}")
    private String botToken;
    public final static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
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
                context.getBean("commandHelp", Command.class).useCommand(update);
                return;
            case "Мои коты":
                context.getBean("commandMyCats", Command.class).useCommand(update);
                return;
            case "/start":
                context.getBean("commandStart", Command.class).useCommand(update);
                return;
            case "Локации":
                context.getBean("commandLocations", Command.class).useCommand(update);
                return;
            case"О локации":
                context.getBean("commandAboutLocation", Command.class).useCommand(update);
                return;
            case "Съесть дичь":
                context.getBean("commandEat", Command.class).useCommand(update);
                return;
            case "Попить воды":
                context.getBean("commandDrink", Command.class).useCommand(update);
                return;
            case "Излечиться травой":
                context.getBean("commandHealing", Command.class).useCommand(update);
                return;
            case "Спать":
                context.getBean("commandSleep", Command.class).useCommand(update);
                return;
            case "Выйти из укрытия":
                context.getBean("commandExitFromShelter", Command.class).useCommand(update);
                return;
            case "Выйти из игры":
                context.getBean("commandExitFromGame", Command.class).useCommand(update);
                return;
            default:
                if (message.matches("^(С|с)оздать кота: ([А-яA-z]+)\\nПорода: ((Д|д)линнолапый|(К|к)репкий|(М|м)ускулистый)\\nЛокация: ([А-я]+)\\nПол: (((К|к)от)|((К|к)ошка))$")){
                    context.getBean("commandCreateCat", Command.class).useCommand(update);
                } else if (message.matches("^Зайти на ([А-яA-z]+)$")) {
                    context.getBean("commandEnterOn", Command.class).useCommand(update);
                } else if (message.matches("^Пойти в ([А-я]+)$")) {
                    context.getBean("commandMoveToLocation", Command.class).useCommand(update);
                } else if (message.matches("^Зайти в укрытие ([А-я ]+)$")) {
                    context.getBean("commandEnterToShelter", Command.class).useCommand(update);
                }
        }
    }


    /**
     * Метод отправляет пользователю сообщение о том, что действие нераспознанное.
     */
    @SneakyThrows
    private void sendOther(Update update) {
        execute(SendMessage.builder().chatId(update.getMessage().getChatId().toString()).text("Действие нераспознано. Пожалуйста, вызовите помощь командой /help\nЕсли никакие команды не работают, попробуйте написать /start").build());
    }


    @SneakyThrows
    public static void main(String[] args){
        CatBot catBot = CatBot.context.getBean("catBot", CatBot.class);
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(catBot);
    }

    private enum MessageType {
        MESSAGE, OTHER
    }
}
