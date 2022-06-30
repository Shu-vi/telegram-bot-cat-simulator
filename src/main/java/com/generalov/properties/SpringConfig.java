package com.generalov.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan("com.generalov")
@PropertySource("classpath:app.properties")
public class SpringConfig {
    @Value("${databaseUsername}")
    private String db_username;
    @Value("${databaseUrl}")
    private String db_url;
    @Value("${databasePassword}")
    private String db_password;

    /**
     * @return бин клавиатуры для состояний во время игры.
     */
    @Bean
    @Scope(value = "singleton")
    public InlineKeyboardMarkup inGameKeyboard(){
        InlineKeyboardMarkup inGameKeyboard = new InlineKeyboardMarkup();
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
        return inGameKeyboard;
    }

    /**
     * @return бин клавиатуры для состояния вне игры
     */
    @Bean
    @Scope(value = "singleton")
    public InlineKeyboardMarkup outGameKeyboard(){
        InlineKeyboardMarkup outGameKeyboard = new InlineKeyboardMarkup();

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
        return outGameKeyboard;
    }

    @Bean
    @Scope(value = "singleton")
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(db_url);
        dataSource.setUsername(db_username);
        dataSource.setPassword(db_password);
        return dataSource;
    }

    @Bean
    @Scope(value = "singleton")
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource());
    }
}
