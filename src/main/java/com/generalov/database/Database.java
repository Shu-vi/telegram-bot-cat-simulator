package com.generalov.database;

import com.generalov.database.entity.Cat;
import com.generalov.database.entity.Location;
import com.generalov.database.entity.Shelter;
import com.generalov.database.entity.User;
import com.generalov.properties.GetProperties;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    private static final String db_username = GetProperties.getDBUsername();
    private static final String db_url = GetProperties.getDBUrl();
    private static final String db_password = GetProperties.getDBPassword();
    private Connection connection;
    /**
     * Singleton класс
     */
    private volatile static Database database;

    private Database(){}

    /**
     * @return возвращает единственный экзэмпляр класса.
     */
    public static Database getObjectDatabaseControl(){
        if (database == null){
            synchronized (Database.class){
                if (database == null){
                    database = new Database();
                }
            }
        }
        return database;
    }

    /**
     * Подключение к БД
     */
    @SneakyThrows
    private void connectToDB() {
        this.connection = DriverManager.getConnection(db_url, db_username, db_password);
    }

    /**
     * Отключение БД
     */
    @SneakyThrows
    private void disconnectBD() {
        this.connection.close();
    }

    /**
     * @param user пользователь, которого нужно добавить.
     * @return true, если пользователь был успешно добавлен.
     * Добавляет пользователя в бд при старте бота.
     */
    @SneakyThrows
    public synchronized void addUser(User user){
        connectToDB();
        PreparedStatement statement = connection.prepareStatement("insert into users(id, name, condition) VALUES (?, ?, ?)");
        statement.setLong(1, user.getId());
        statement.setString(2, user.getName());
        statement.setShort(3, user.getCondition());
        statement.execute();
        disconnectBD();
    }


    @SneakyThrows
    public Shelter getShelterByShelterTitle(String shelterTitle){
        Shelter shelter = null;
        connectToDB();
        PreparedStatement statement = connection.prepareStatement("select * from shelter where title = ?");
        statement.setString(1, shelterTitle);
        ResultSet result = statement.executeQuery();
        while (result.next()){
            shelter = new Shelter(
                    result.getInt("id"),
                    result.getString("title"),
                    result.getInt("capacity"),
                    result.getInt("location_id"),
                    result.getArray("cats") != null? (Integer[]) result.getArray("cats").getArray() : null
            );
        }
        disconnectBD();
        return shelter;
    }

    @SneakyThrows
    public void setShelterByShelterId(Shelter shelter){
        connectToDB();
        PreparedStatement statement = connection.prepareStatement("update shelter" +
                " set title = ?, capacity = ?, location_id = ?, cats = ?" +
                " where id = ?");
        statement.setString(1, shelter.getTitle());
        statement.setInt(2, shelter.getCapacity());
        statement.setInt(3, shelter.getLocationId());
        statement.setArray(4, connection.createArrayOf("integer", shelter.getCats()));
        statement.setInt(5, shelter.getId());
        statement.execute();
        disconnectBD();
    }

    /**
     * @param id айди пользователя.
     * @return возвращает пользователя по его айди.
     */
    @SneakyThrows
    public synchronized User getUserById(Long id) {
        User user = null;
        connectToDB();
        PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");
        statement.setLong(1, id);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            user = new User(
                    result.getLong("id"),
                    result.getString("name"),
                    result.getShort("condition")
            );
        }
        disconnectBD();
        return user;
    }

    /**
     * @param id айди породы.
     * @return возвращает породу.
     */
    @SneakyThrows
    public synchronized String getBreedByBreedId(Integer id){
        String breed = null;
        connectToDB();
        PreparedStatement statement = connection.prepareStatement("select title from breed where id = ?");
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        while (result.next())
            breed = result.getString("title");
        disconnectBD();
        return breed;
    }

    /**
     * @return true, если кот создан.
     * Добавляет кота в БД.
     */
    @SneakyThrows
    public synchronized void addCat(Cat cat){
        connectToDB();
        PreparedStatement statement = connection.prepareStatement("insert into " +
                "cat(name, gender, breed_id, health, satiety, water, stamina, location_id, user_id, is_online) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, cat.getName());
        statement.setString(2, cat.getGender());
        statement.setInt(3, cat.getBreedId());
        statement.setInt(4, cat.getHealth());
        statement.setInt(5, cat.getSatiety());
        statement.setInt(6, cat.getWater());
        statement.setInt(7, cat.getStamina());
        statement.setInt(8, cat.getLocationId());
        statement.setLong(9, cat.getUserId());
        statement.setBoolean(10, cat.getIsOnline());
        statement.execute();
        disconnectBD();
    }

    /**
     * Возвращает айди породы по названию породы
     */
    @SneakyThrows
    public synchronized Integer getBreedIdByBreed(String catBreed){
        connectToDB();
        Integer breedId = null;
        PreparedStatement statement = connection.prepareStatement("select id from breed where title = ?");
        statement.setString(1, catBreed);
        ResultSet result = statement.executeQuery();
        while (result.next()){
            breedId = result.getInt("id");
        }
        disconnectBD();
        return breedId;
    }

    /**
     * Возвращает айди локации по её названию.
     */
    @SneakyThrows
    public synchronized Location getLocationByLocationTitle(String locationTitle){
        connectToDB();
        Location location = null;
        PreparedStatement statement = connection.prepareStatement("select * from location where title = ?");
        statement.setString(1, locationTitle);
        ResultSet result = statement.executeQuery();
        while (result.next())
            location = new Location(
                    result.getInt("id"),
                    (Integer[]) result.getArray("neighboring_locations_id").getArray(),
                    result.getString("title"),
                    result.getInt("food_id"),
                    result.getInt("water_id"),
                    result.getInt("health_id")
            );
        disconnectBD();
        return location;
    }

    /**
     * Возвращает список всех котов пользователя по его айди.
     */
    @SneakyThrows
    public synchronized ArrayList<Cat> getCatsListByUserId(Long userId){
        connectToDB();
        Cat cat;
        ArrayList<Cat> cats = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("select * from cat where user_id = ?");
        statement.setLong(1, userId);
        ResultSet result = statement.executeQuery();
        while (result.next()){
            cat = new Cat(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("gender"),
                    result.getInt("breed_id"),
                    result.getInt("health"),
                    result.getInt("satiety"),
                    result.getInt("water"),
                    result.getInt("stamina"),
                    result.getInt("location_id"),
                    result.getLong("user_id"),
                    result.getBoolean("is_online")
            );
            cats.add(cat);
        }
        disconnectBD();
        return cats;
    }

    /**
     * Возвращает название локации по её айди.
     */
    @SneakyThrows
    public synchronized Location getLocationByLocationId(Integer locationId){
        connectToDB();
        Location location = null;
        PreparedStatement statement = connection.prepareStatement("select * from location where id = ?");
        statement.setInt(1, locationId);
        ResultSet result = statement.executeQuery();
        while (result.next())
            location = new Location(
                    result.getInt("id"),
                    (Integer[]) result.getArray("neighboring_locations_id").getArray(),
                    result.getString("title"),
                    result.getInt("food_id"),
                    result.getInt("water_id"),
                    result.getInt("health_id")
            );
        disconnectBD();
        return location;
    }

    @SneakyThrows
    public synchronized ArrayList<Cat> getCatsByLocationId(Integer locationId){
        connectToDB();
        ArrayList<Cat> cats = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("select * from cat where location_id = ?");
        statement.setInt(1, locationId);
        ResultSet result = statement.executeQuery();
        while (result.next())
            cats.add(new Cat(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("gender"),
                    result.getInt("breed_id"),
                    result.getInt("health"),
                    result.getInt("satiety"),
                    result.getInt("water"),
                    result.getInt("stamina"),
                    result.getInt("location_id"),
                    result.getLong("user_id"),
                    result.getBoolean("is_online")
            ));
        disconnectBD();
        return cats;
    }

    /**
     * Получение количества котов у пользователя по айди.
     */
    @SneakyThrows
    public synchronized Integer getCatsCountByUserId(Long userId){
        connectToDB();
        Integer catsCount = 0;
        PreparedStatement statement = connection.prepareStatement("select count(*) as count from cat where user_id = ?");
        statement.setLong(1, userId);
        ResultSet result = statement.executeQuery();
        while (result.next())
            catsCount = result.getInt("count");
        disconnectBD();
        return catsCount;
    }

    @SneakyThrows
    public synchronized Cat getCatByCatNameAndUserId(String name, Long userId){
        Cat cat = null;
        connectToDB();
        PreparedStatement statement = connection.prepareStatement("select * from cat where name = ? and user_id = ?");
        statement.setString(1, name);
        statement.setLong(2, userId);
        ResultSet result = statement.executeQuery();
        while (result.next())
            cat = new Cat(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("gender"),
                    result.getInt("breed_id"),
                    result.getInt("health"),
                    result.getInt("satiety"),
                    result.getInt("water"),
                    result.getInt("stamina"),
                    result.getInt("location_id"),
                    result.getLong("user_id"),
                    result.getBoolean("is_online")
            );
        disconnectBD();
        return cat;
    }

    @SneakyThrows
    public synchronized void setUserConditionByUserId(Short userCondition, Long userId){
        connectToDB();
        PreparedStatement statement = connection.prepareStatement("update users set condition = ? where id = ?");
        statement.setShort(1, userCondition);
        statement.setLong(2, userId);
        statement.execute();
        disconnectBD();
    }

    /**
     * Возвращает кота из БД по айди юзера и статусу кота(онлайн или нет).
     */
    @SneakyThrows
    public synchronized Cat getCatByUserIdAndCatStatus(Long userId, Boolean catStatus){
        connectToDB();
        Cat cat = null;
        PreparedStatement statement = connection.prepareStatement("select * from cat where is_online = ? and user_id = ?");
        statement.setBoolean(1, catStatus);
        statement.setLong(2, userId);
        ResultSet result = statement.executeQuery();
        while (result.next())
            cat = new Cat(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("gender"),
                    result.getInt("breed_id"),
                    result.getInt("health"),
                    result.getInt("satiety"),
                    result.getInt("water"),
                    result.getInt("stamina"),
                    result.getInt("location_id"),
                    result.getLong("user_id"),
                    result.getBoolean("is_online")
            );
        disconnectBD();
        return cat;
    }

    @SneakyThrows
    public synchronized void setCatOnlineStatus(Boolean isOnline, Integer catId){
        connectToDB();
        PreparedStatement statement = connection.prepareStatement("update cat set is_online = ? where id = ?");
        statement.setBoolean(1, isOnline);
        statement.setInt(2, catId);
        statement.execute();
        disconnectBD();
    }

    @SneakyThrows
    public synchronized String getFoodTitleById(Integer foodId){
        connectToDB();
        String foodTitle = null;
        PreparedStatement statement = connection.prepareStatement("select title from item_food where id = ?");
        statement.setInt(1, foodId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next())
            foodTitle = resultSet.getString("title");
        disconnectBD();
        return foodTitle;
    }

    @SneakyThrows
    public synchronized String getWaterTitleById(Integer waterId){
        connectToDB();
        String waterTitle = null;
        PreparedStatement statement = connection.prepareStatement("select title from item_water where id = ?");
        statement.setInt(1, waterId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next())
            waterTitle = resultSet.getString("title");
        disconnectBD();
        return waterTitle;
    }

    @SneakyThrows
    public synchronized String getHealthTitleById(Integer healthId){
        connectToDB();
        String healthTitle = null;
        PreparedStatement statement = connection.prepareStatement("select title from item_health where id = ?");
        statement.setInt(1, healthId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next())
            healthTitle = resultSet.getString("title");
        disconnectBD();
        return healthTitle;
    }

    @SneakyThrows
    public synchronized ArrayList<Shelter> getSheltersByLocationId(Integer locationId){
        connectToDB();
        ArrayList<Shelter> shelters = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("select * from shelter where location_id = ?");
        statement.setInt(1, locationId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next())
            shelters.add(new Shelter(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getInt("capacity"),
                    resultSet.getInt("location_id"),
                    resultSet.getArray("cats") != null ? (Integer[]) resultSet.getArray("cats").getArray() : null
            ));
        disconnectBD();
        return shelters;
    }

    @SneakyThrows
    public synchronized void setCat(Cat cat){
        connectToDB();
        PreparedStatement statement = connection.prepareStatement("update cat " +
                "set name = ?, gender = ?, breed_id = ?, health = ?, satiety = ?, water = ?, stamina = ?, location_id = ?, is_online = ? " +
                "where id = ?");
        statement.setString(1, cat.getName());
        statement.setString(2, cat.getGender());
        statement.setInt(3, cat.getBreedId());
        statement.setInt(4, cat.getHealth());
        statement.setInt(5, cat.getSatiety());
        statement.setInt(6, cat.getWater());
        statement.setInt(7, cat.getStamina());
        statement.setInt(8, cat.getLocationId());
        statement.setBoolean(9, cat.getIsOnline());
        statement.setInt(10, cat.getId());
        statement.execute();
        disconnectBD();
    }

    public static void main(String[] args) {
        Database database = Database.getObjectDatabaseControl();
//        Integer[] cats = {2, 4};
//        Shelter shelter = new Shelter(5, "Пещера", 10, 2, null);
//        database.setShelterByShelterId(shelter);
        Shelter shelter = database.getShelterByShelterTitle("Пещера");
    }
}
