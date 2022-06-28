package com.generalov.command.handler;

import com.generalov.CatBot;
import com.generalov.database.Database;
import com.generalov.database.entity.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@Scope(value = "singleton")
public class CommandHelp extends Command{
    private InlineKeyboardMarkup outGameKeyboard;
    private InlineKeyboardMarkup inGameKeyboard;

    @Autowired
    public CommandHelp(CatBot catBot,
                       Database database,
                       @Qualifier(value = "outGameKeyboard") InlineKeyboardMarkup outGameKeyboard,
                       @Qualifier(value = "inGameKeyboard") InlineKeyboardMarkup inGameKeyboard) {
        super(catBot, database);
        this.outGameKeyboard = outGameKeyboard;
        this.inGameKeyboard = inGameKeyboard;
    }


    @Override
    public void useCommand(Update update) {
        help(update);
    }

    private void help(Update update){
        Long userId = update.getMessage().getChatId();
        Short condition = database.getUserById(userId).getCondition();
        if (condition == User.NOT_IN_GAME){
            helpOutGame(userId);
        } else{
            helpInGame(userId);
        }
    }

    @SneakyThrows
    private void helpInGame(Long userId){
        String message = "Список доступных команд в игре:" +
                "\n1) \"Пойти в НазваниеЛокации\" - переводит кота в другую локацию(можно переходить только в соседние локации)." +
                " В место НазваниеЛокации нужно ввести название желаемой локации." +
                "\n2) \"О локации\" - выводит информацию о локации, в которой вы находитесь." +
                "\n3) \"Съесть дичь\" - если на локации есть еда, то кот съест её и восстановит себе немного еды." +
                "\n4) \"Попить воды\" - если на локации есть вода, то кот будет пить воду до тех пор, пока" +
                " не напьётся." +
                "\n5) \"Излечиться травой\" - если на локации есть трава, то кот покушает её и излечится." +
                "\n6) \"Зайти в укрытие НазваниеУкрытия\" - заходит в укрытие на локации с названием НазваниеУкрытия." +
                "\n7) \"Спать\" - ложится спать в укрытии и восстанавливает выносливость во время сна." +
                "\n8) \"Выйти из укрытия\" - выходит из укрытия." +
                "\n9) \"Выйти из игры\" - выходит из игры.";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(message).replyMarkup(inGameKeyboard).build());
    }

    @SneakyThrows
    private void helpOutGame(Long userId){
        String text = "Список доступных команд вне игры:" +
                "\n1) \"Мои коты\" - выводит всех ваших котов и их параметры." +
                "\n2) \"Локации\" - выводит список локаций, доступных для спавна." +
                "\n3) " +
                "\n\"Создать кота: ИмяКота" +
                "\nПорода: НазваниеПороды" +
                "\nЛокация: НазваниеЛокацииДляСпавна" +
                "\nПол: ПолКота\"" +
                "\nГде ИмяКота - желаемое имя для вашего питомца. В дальнейшем его нельзя будет поменять(но это не точно)," +
                " НазваниеПороды - порода вашего кота, нужно выбрать одну из следующих трёх \"Длиннолапый\", \"Крепкий\", \"Мускулистый\"," +
                " НазваниеЛокацииДляСпавна - название локации, нужно выбрать одну из следующих двух \"Деревня\", \"Лес\"," +
                " ПолКота - пол вашего котика, нужно выбрать один из следующих двух \"Кот\", \"Кошка\"." +
                "\n4) \"Зайти на ИмяКота\" - зайдёт в игру на кота с именем ИмяКота";
        catBot.execute(SendMessage.builder().chatId(userId.toString()).text(text).replyMarkup(outGameKeyboard).build());
    }
}
