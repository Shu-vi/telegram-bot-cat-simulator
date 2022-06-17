package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandStart extends Command{
    public CommandStart(CatBot catBot){
        super(catBot);
    }

    /**
     * Действия, которые будут выполняться при команде /start.
     * Если юзера нет в БД, то добавляет его в бд и отправляет приветствие.
     */
    @SneakyThrows
    public void start(Update update) {
        Long userId = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getFirstName();
        if (!isUserExist(userId)){
            User user = new User(userId, userName, (short) 0);
            database.addUser(user);
            sendGreeting(userName, userId);
        }
    }

    /**
     * @param id нужен для запроса в бд, чтобы проверить, нет ли юзера с данным id уже в базе данных.
     * @return возвращает true, если пользователь уже есть в базе данных.
     */
    private Boolean isUserExist(Long id) {
        return database.getUserById(id) != null;
    }

    /**
     *Метод отправляет пользователю приветствие и сообщает инфу о боте.
     */
    @SneakyThrows
    private void sendGreeting(String userFirstName, Long userId) {
        String messageText = "Здравствуйте, " + userFirstName + ".\nЭтот бот позволит вам" +
                " играть в симулятор кота, игра является многопользовательской";
        this.catBot.execute(SendMessage.builder().chatId(userId.toString()).text(messageText).build());
    }
}
