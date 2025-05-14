package com.example.healthmonitor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Основная информация о базе данных
    private static final String DATABASE_NAME = "health_monitor.db";
    private static final int DATABASE_VERSION = 1;

    // Имена таблиц
    public static final String TABLE_USER = "users";
    public static final String TABLE_FOOD = "foods";
    public static final String TABLE_MEAL = "meals";
    public static final String TABLE_WEIGHT_LOG = "weight_log";

    // Общие колонки
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";

    // Колонки таблицы User
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_TARGET_WEIGHT = "target_weight";
    public static final String COLUMN_ACTIVITY_LEVEL = "activity_level";
    public static final String COLUMN_DAILY_CALORIES = "daily_calories";

    // Колонки таблицы Food
    public static final String COLUMN_FOOD_NAME = "food_name";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_PROTEINS = "proteins";
    public static final String COLUMN_FATS = "fats";
    public static final String COLUMN_CARBS = "carbs";
    public static final String COLUMN_PORTION_SIZE = "portion_size";
    public static final String COLUMN_IS_CUSTOM = "is_custom";

    // Колонки таблицы Meal
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_FOOD_ID = "food_id";
    public static final String COLUMN_MEAL_TYPE = "meal_type";
    public static final String COLUMN_SERVINGS = "servings";

    // Колонки таблицы WeightLog
    public static final String COLUMN_WEIGHT_VALUE = "weight_value";

    // Создание таблицы User
    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_AGE + " INTEGER, " +
                    COLUMN_GENDER + " TEXT, " +
                    COLUMN_HEIGHT + " REAL, " +
                    COLUMN_WEIGHT + " REAL, " +
                    COLUMN_TARGET_WEIGHT + " REAL, " +
                    COLUMN_ACTIVITY_LEVEL + " TEXT, " +
                    COLUMN_DAILY_CALORIES + " INTEGER" +
                    ")";

    // Создание таблицы Food
    private static final String SQL_CREATE_FOOD_TABLE =
            "CREATE TABLE " + TABLE_FOOD + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FOOD_NAME + " TEXT, " +
                    COLUMN_CALORIES + " INTEGER, " +
                    COLUMN_PROTEINS + " REAL, " +
                    COLUMN_FATS + " REAL, " +
                    COLUMN_CARBS + " REAL, " +
                    COLUMN_PORTION_SIZE + " INTEGER, " +
                    COLUMN_IS_CUSTOM + " INTEGER" +
                    ")";

    // Создание таблицы Meal
    private static final String SQL_CREATE_MEAL_TABLE =
            "CREATE TABLE " + TABLE_MEAL + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_FOOD_ID + " INTEGER, " +
                    COLUMN_MEAL_TYPE + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_SERVINGS + " REAL, " +
                    "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_FOOD_ID + ") REFERENCES " + TABLE_FOOD + "(" + COLUMN_ID + ")" +
                    ")";

    // Создание таблицы WeightLog
    private static final String SQL_CREATE_WEIGHT_LOG_TABLE =
            "CREATE TABLE " + TABLE_WEIGHT_LOG + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_WEIGHT_VALUE + " REAL, " +
                    "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + ")" +
                    ")";

    // Одиночный экземпляр базы данных
    private static DatabaseHelper instance;

    // Получение одиночного экземпляра
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Конструктор
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблиц
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_FOOD_TABLE);
        db.execSQL(SQL_CREATE_MEAL_TABLE);
        db.execSQL(SQL_CREATE_WEIGHT_LOG_TABLE);

        // Добавление демонстрационных данных
        insertDemoData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // В реальном приложении здесь должна быть логика миграции базы данных
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // Метод для добавления демонстрационных данных
    private void insertDemoData(SQLiteDatabase db) {
        // Добавление демо-пользователя
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_NAME, "Иван Иванов");
        userValues.put(COLUMN_AGE, 25);
        userValues.put(COLUMN_GENDER, "Мужской");
        userValues.put(COLUMN_HEIGHT, 180);
        userValues.put(COLUMN_WEIGHT, 75);
        userValues.put(COLUMN_TARGET_WEIGHT, 70);
        userValues.put(COLUMN_ACTIVITY_LEVEL, "Умеренная активность");
        userValues.put(COLUMN_DAILY_CALORIES, 2200);
        long userId = db.insert(TABLE_USER, null, userValues);

        // Добавление демо-продуктов
        insertDemoFood(db, "Овсянка на воде", 68, 2.4, 1.2, 12.0, 100, 0);
        insertDemoFood(db, "Яблоко", 52, 0.3, 0.2, 13.8, 100, 0);
        insertDemoFood(db, "Куриная грудка (отварная)", 165, 31.0, 3.6, 0.0, 100, 0);
        insertDemoFood(db, "Гречка отварная", 132, 4.5, 0.8, 28.0, 100, 0);
        insertDemoFood(db, "Салат из огурцов и помидоров", 20, 0.9, 0.2, 4.2, 100, 0);
        insertDemoFood(db, "Творог 5%", 121, 18.0, 5.0, 3.0, 100, 0);
        insertDemoFood(db, "Банан", 96, 1.5, 0.2, 21.8, 100, 0);
        insertDemoFood(db, "Сэндвич с курицей", 350, 20.0, 12.0, 40.0, 200, 0);
        insertDemoFood(db, "Суп с фрикадельками", 110, 7.0, 5.0, 8.5, 250, 0);
        insertDemoFood(db, "Рис отварной", 130, 2.7, 0.3, 28.2, 100, 0);

        // Добавление демо-приемов пищи (на текущую дату)
        String currentDate = java.text.DateFormat.getDateInstance().format(new java.util.Date());

        // Завтрак
        insertDemoMeal(db, userId, 1, "Завтрак", currentDate, 1.5); // Овсянка
        insertDemoMeal(db, userId, 6, "Завтрак", currentDate, 1.0); // Творог
        insertDemoMeal(db, userId, 2, "Завтрак", currentDate, 1.0); // Яблоко

        // Обед
        insertDemoMeal(db, userId, 3, "Обед", currentDate, 1.5); // Куриная грудка
        insertDemoMeal(db, userId, 4, "Обед", currentDate, 1.0); // Гречка
        insertDemoMeal(db, userId, 5, "Обед", currentDate, 1.0); // Салат

        // Ужин
        insertDemoMeal(db, userId, 3, "Ужин", currentDate, 1.0); // Куриная грудка
        insertDemoMeal(db, userId, 10, "Ужин", currentDate, 1.0); // Рис

        // Демо-запись веса
        insertDemoWeightLog(db, userId, currentDate, 75.0);

        // Демо-записи веса за предыдущие дни
        for (int i = 1; i <= 7; i++) {
            String date = getDateBefore(i);
            double weight = 75.0 + (Math.random() * 1.0 - 0.5); // ±0.5 кг от 75
            insertDemoWeightLog(db, userId, date, weight);
        }
    }

    // Вспомогательный метод для добавления демо-продуктов
    private void insertDemoFood(SQLiteDatabase db, String name, int calories,
                                double proteins, double fats, double carbs,
                                int portionSize, int isCustom) {
        ContentValues foodValues = new ContentValues();
        foodValues.put(COLUMN_FOOD_NAME, name);
        foodValues.put(COLUMN_CALORIES, calories);
        foodValues.put(COLUMN_PROTEINS, proteins);
        foodValues.put(COLUMN_FATS, fats);
        foodValues.put(COLUMN_CARBS, carbs);
        foodValues.put(COLUMN_PORTION_SIZE, portionSize);
        foodValues.put(COLUMN_IS_CUSTOM, isCustom);
        db.insert(TABLE_FOOD, null, foodValues);
    }

    // Вспомогательный метод для добавления демо-приемов пищи
    private void insertDemoMeal(SQLiteDatabase db, long userId, long foodId,
                                String mealType, String date, double servings) {
        ContentValues mealValues = new ContentValues();
        mealValues.put(COLUMN_USER_ID, userId);
        mealValues.put(COLUMN_FOOD_ID, foodId);
        mealValues.put(COLUMN_MEAL_TYPE, mealType);
        mealValues.put(COLUMN_DATE, date);
        mealValues.put(COLUMN_SERVINGS, servings);
        db.insert(TABLE_MEAL, null, mealValues);
    }

    // Вспомогательный метод для добавления демо-записей веса
    private void insertDemoWeightLog(SQLiteDatabase db, long userId, String date, double weight) {
        ContentValues weightValues = new ContentValues();
        weightValues.put(COLUMN_USER_ID, userId);
        weightValues.put(COLUMN_DATE, date);
        weightValues.put(COLUMN_WEIGHT_VALUE, weight);
        db.insert(TABLE_WEIGHT_LOG, null, weightValues);
    }

    // Вспомогательный метод для получения даты n дней назад
    private String getDateBefore(int days) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DATE, -days);
        return java.text.DateFormat.getDateInstance().format(calendar.getTime());
    }
}