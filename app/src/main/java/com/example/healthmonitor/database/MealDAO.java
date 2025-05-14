package com.example.healthmonitor.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.healthmonitor.models.Meal;

import java.util.ArrayList;
import java.util.List;

public class MealDAO {
    private static final String TAG = "MealDAO";
    private DatabaseHelper dbHelper;

    public MealDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // Добавление нового приема пищи
    public long addMeal(Meal meal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long mealId = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_ID, meal.getUserId());
            values.put(DatabaseHelper.COLUMN_FOOD_ID, meal.getFoodId());
            values.put(DatabaseHelper.COLUMN_MEAL_TYPE, meal.getMealType());
            values.put(DatabaseHelper.COLUMN_DATE, meal.getDate());
            values.put(DatabaseHelper.COLUMN_SERVINGS, meal.getServings());

            mealId = db.insert(DatabaseHelper.TABLE_MEAL, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error while adding meal: " + e.getMessage());
        } finally {
            db.close();
        }

        return mealId;
    }

    // Обновление данных приема пищи
    public boolean updateMeal(Meal meal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_ID, meal.getUserId());
            values.put(DatabaseHelper.COLUMN_FOOD_ID, meal.getFoodId());
            values.put(DatabaseHelper.COLUMN_MEAL_TYPE, meal.getMealType());
            values.put(DatabaseHelper.COLUMN_DATE, meal.getDate());
            values.put(DatabaseHelper.COLUMN_SERVINGS, meal.getServings());

            int rowsAffected = db.update(
                    DatabaseHelper.TABLE_MEAL,
                    values,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(meal.getId())}
            );

            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error while updating meal: " + e.getMessage());
        } finally {
            db.close();
        }

        return success;
    }

    // Удаление приема пищи
    public boolean deleteMeal(long mealId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            int rowsAffected = db.delete(
                    DatabaseHelper.TABLE_MEAL,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(mealId)}
            );

            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error while deleting meal: " + e.getMessage());
        } finally {
            db.close();
        }

        return success;
    }

    // Получение приема пищи по ID
    public Meal getMealById(long mealId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Meal meal = null;

        try {
            String query = "SELECT m.*, f.* FROM " + DatabaseHelper.TABLE_MEAL + " m " +
                    "JOIN " + DatabaseHelper.TABLE_FOOD + " f ON m." + DatabaseHelper.COLUMN_FOOD_ID +
                    " = f." + DatabaseHelper.COLUMN_ID +
                    " WHERE m." + DatabaseHelper.COLUMN_ID + " = ?";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(mealId)});

            if (cursor != null && cursor.moveToFirst()) {
                meal = cursorToMeal(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting meal by ID: " + e.getMessage());
        } finally {
            db.close();
        }

        return meal;
    }

    // Получение приемов пищи для конкретного пользователя и даты
    public List<Meal> getMealsByUserAndDate(long userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Meal> mealList = new ArrayList<>();

        try {
            String query = "SELECT m.*, f.* FROM " + DatabaseHelper.TABLE_MEAL + " m " +
                    "JOIN " + DatabaseHelper.TABLE_FOOD + " f ON m." + DatabaseHelper.COLUMN_FOOD_ID +
                    " = f." + DatabaseHelper.COLUMN_ID +
                    " WHERE m." + DatabaseHelper.COLUMN_USER_ID + " = ? AND m." +
                    DatabaseHelper.COLUMN_DATE + " = ? " +
                    "ORDER BY CASE m." + DatabaseHelper.COLUMN_MEAL_TYPE +
                    " WHEN 'Завтрак' THEN 1 " +
                    " WHEN 'Обед' THEN 2 " +
                    " WHEN 'Ужин' THEN 3 " +
                    " WHEN 'Перекус' THEN 4 " +
                    " ELSE 5 END";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), date});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Meal meal = cursorToMeal(cursor);
                    mealList.add(meal);
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting meals by user and date: " + e.getMessage());
        } finally {
            db.close();
        }

        return mealList;
    }

    // Получение приемов пищи по типу для конкретного пользователя и даты
    public List<Meal> getMealsByUserDateAndType(long userId, String date, String mealType) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Meal> mealList = new ArrayList<>();

        try {
            String query = "SELECT m.*, f.* FROM " + DatabaseHelper.TABLE_MEAL + " m " +
                    "JOIN " + DatabaseHelper.TABLE_FOOD + " f ON m." + DatabaseHelper.COLUMN_FOOD_ID +
                    " = f." + DatabaseHelper.COLUMN_ID +
                    " WHERE m." + DatabaseHelper.COLUMN_USER_ID + " = ? AND m." +
                    DatabaseHelper.COLUMN_DATE + " = ? AND m." + DatabaseHelper.COLUMN_MEAL_TYPE + " = ?";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), date, mealType});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Meal meal = cursorToMeal(cursor);
                    mealList.add(meal);
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting meals by user, date and type: " + e.getMessage());
        } finally {
            db.close();
        }

        return mealList;
    }

    // Получение суммарных калорий и нутриентов за день
    public DailySummary getDailySummary(long userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DailySummary summary = new DailySummary();

        try {
            String query = "SELECT SUM(f." + DatabaseHelper.COLUMN_CALORIES + " * m." +
                    DatabaseHelper.COLUMN_SERVINGS + ") as total_calories, " +
                    "SUM(f." + DatabaseHelper.COLUMN_PROTEINS + " * m." + DatabaseHelper.COLUMN_SERVINGS +
                    ") as total_proteins, " +
                    "SUM(f." + DatabaseHelper.COLUMN_FATS + " * m." + DatabaseHelper.COLUMN_SERVINGS +
                    ") as total_fats, " +
                    "SUM(f." + DatabaseHelper.COLUMN_CARBS + " * m." + DatabaseHelper.COLUMN_SERVINGS +
                    ") as total_carbs " +
                    "FROM " + DatabaseHelper.TABLE_MEAL + " m " +
                    "JOIN " + DatabaseHelper.TABLE_FOOD + " f ON m." + DatabaseHelper.COLUMN_FOOD_ID +
                    " = f." + DatabaseHelper.COLUMN_ID +
                    " WHERE m." + DatabaseHelper.COLUMN_USER_ID + " = ? AND m." +
                    DatabaseHelper.COLUMN_DATE + " = ?";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), date});

            if (cursor != null && cursor.moveToFirst()) {
                summary.totalCalories = cursor.getInt(cursor.getColumnIndexOrThrow("total_calories"));
                summary.totalProteins = cursor.getFloat(cursor.getColumnIndexOrThrow("total_proteins"));
                summary.totalFats = cursor.getFloat(cursor.getColumnIndexOrThrow("total_fats"));
                summary.totalCarbs = cursor.getFloat(cursor.getColumnIndexOrThrow("total_carbs"));
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting daily summary: " + e.getMessage());
        } finally {
            db.close();
        }

        return summary;
    }

    // Класс для хранения суммарных данных за день
    public static class DailySummary {
        public int totalCalories;
        public float totalProteins;
        public float totalFats;
        public float totalCarbs;
    }

    // Преобразование курсора в объект Meal с информацией о продукте
    private Meal cursorToMeal(Cursor cursor) {
        Meal meal = new Meal();

        int mealIdIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID);
        int userIdIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID);
        int foodIdIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_ID);
        int mealTypeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEAL_TYPE);
        int dateIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE);
        int servingsIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVINGS);

        meal.setId(cursor.getLong(mealIdIndex));
        meal.setUserId(cursor.getLong(userIdIndex));
        meal.setFoodId(cursor.getLong(foodIdIndex));
        meal.setMealType(cursor.getString(mealTypeIndex));
        meal.setDate(cursor.getString(dateIndex));
        meal.setServings(cursor.getFloat(servingsIndex));

        // Информация о продукте
        int foodNameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_NAME);
        int caloriesIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CALORIES);
        int proteinsIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROTEINS);
        int fatsIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FATS);
        int carbsIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARBS);
        int portionSizeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PORTION_SIZE);

        meal.setFoodName(cursor.getString(foodNameIndex));
        meal.setCalories(cursor.getInt(caloriesIndex));
        meal.setProteins(cursor.getFloat(proteinsIndex));
        meal.setFats(cursor.getFloat(fatsIndex));
        meal.setCarbs(cursor.getFloat(carbsIndex));
        meal.setPortionSize(cursor.getInt(portionSizeIndex));

        return meal;
    }
}