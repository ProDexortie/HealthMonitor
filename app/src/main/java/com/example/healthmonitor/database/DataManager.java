package com.example.healthmonitor.database;

import android.content.Context;

import com.example.healthmonitor.models.Food;
import com.example.healthmonitor.models.Meal;
import com.example.healthmonitor.models.User;

/**
 * Этот класс служит фасадом для работы с данными.
 * Он позволяет легко переключаться между локальной базой данных SQLite
 * и облачным хранилищем данных (например, Firebase).
 */
public class DataManager {

    private static DataManager instance;

    private UserDAO userDAO;
    private FoodDAO foodDAO;
    private MealDAO mealDAO;

    // Флаг, указывающий на использование облачной базы данных
    private boolean useCloudDB = false;

    // Конструктор
    private DataManager(Context context) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        userDAO = new UserDAO(dbHelper);
        foodDAO = new FoodDAO(dbHelper);
        mealDAO = new MealDAO(dbHelper);
    }

    // Получение единственного экземпляра DataManager
    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context.getApplicationContext());
        }
        return instance;
    }

    // Методы для работы с пользователями

    public User getUser() {
        // В будущем здесь будет проверка: если useCloudDB == true,
        // то получаем данные из облачной базы, иначе - из локальной
        return userDAO.getFirstUser();
    }

    public long saveUser(User user) {
        if (user.getId() > 0) {
            userDAO.updateUser(user);
            return user.getId();
        } else {
            return userDAO.addUser(user);
        }
    }

    public boolean updateUserWeight(long userId, float weight, String date) {
        return userDAO.updateUserWeight(userId, weight, date);
    }

    // Методы для работы с продуктами

    public Food getFoodById(long foodId) {
        return foodDAO.getFoodById(foodId);
    }

    public java.util.List<Food> getAllFoods() {
        return foodDAO.getAllFoods();
    }

    public java.util.List<Food> searchFoodsByName(String query) {
        return foodDAO.searchFoodsByName(query);
    }

    public long addFood(Food food) {
        return foodDAO.addFood(food);
    }

    public boolean updateFood(Food food) {
        return foodDAO.updateFood(food);
    }

    public boolean deleteFood(long foodId) {
        return foodDAO.deleteFood(foodId);
    }

    // Методы для работы с приемами пищи

    public long addMeal(Meal meal) {
        return mealDAO.addMeal(meal);
    }

    public boolean updateMeal(Meal meal) {
        return mealDAO.updateMeal(meal);
    }

    public boolean deleteMeal(long mealId) {
        return mealDAO.deleteMeal(mealId);
    }

    public java.util.List<Meal> getMealsByUserAndDate(long userId, String date) {
        return mealDAO.getMealsByUserAndDate(userId, date);
    }

    public java.util.List<Meal> getMealsByUserDateAndType(long userId, String date, String mealType) {
        return mealDAO.getMealsByUserDateAndType(userId, date, mealType);
    }

    public MealDAO.DailySummary getDailySummary(long userId, String date) {
        return mealDAO.getDailySummary(userId, date);
    }

    // Методы для синхронизации с облачной базой данных

    /**
     * Включает/выключает использование облачной базы данных
     * @param useCloud true - использовать облачную БД, false - использовать локальную
     */
    public void setUseCloudDB(boolean useCloud) {
        this.useCloudDB = useCloud;
    }

    /**
     * Проверяет, включено ли использование облачной базы данных
     * @return true, если используется облачная БД
     */
    public boolean isUsingCloudDB() {
        return useCloudDB;
    }

    /**
     * Синхронизирует данные между локальной и облачной базами данных
     * @return true, если синхронизация прошла успешно
     */
    public boolean syncData() {
        // Код для синхронизации данных
        // В реальном приложении здесь была бы логика синхронизации

        // Пример:
        // 1. Получить данные из облачной БД
        // 2. Сравнить с локальными данными
        // 3. Решить конфликты
        // 4. Обновить локальную и облачную БД

        return true; // Заглушка
    }

    public boolean performFullSync() {
        // Код для синхронизации данных
        // В реальном приложении здесь была бы логика синхронизации

        // Пример:
        // 1. Получить данные из облачной БД
        // 2. Сравнить с локальными данными
        // 3. Решить конфликты
        // 4. Обновить локальную и облачную БД

        return true; // Заглушка
    }
}