package com.example.healthmonitor.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.healthmonitor.models.Food;

import java.util.ArrayList;
import java.util.List;

public class FoodDAO {
    private static final String TAG = "FoodDAO";
    private DatabaseHelper dbHelper;

    public FoodDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // Добавление нового продукта
    public long addFood(Food food) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long foodId = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_FOOD_NAME, food.getFoodName());
            values.put(DatabaseHelper.COLUMN_CALORIES, food.getCalories());
            values.put(DatabaseHelper.COLUMN_PROTEINS, food.getProteins());
            values.put(DatabaseHelper.COLUMN_FATS, food.getFats());
            values.put(DatabaseHelper.COLUMN_CARBS, food.getCarbs());
            values.put(DatabaseHelper.COLUMN_PORTION_SIZE, food.getPortionSize());
            values.put(DatabaseHelper.COLUMN_IS_CUSTOM, food.isCustom() ? 1 : 0);

            foodId = db.insert(DatabaseHelper.TABLE_FOOD, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error while adding food: " + e.getMessage());
        } finally {
            db.close();
        }

        return foodId;
    }

    // Обновление данных продукта
    public boolean updateFood(Food food) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_FOOD_NAME, food.getFoodName());
            values.put(DatabaseHelper.COLUMN_CALORIES, food.getCalories());
            values.put(DatabaseHelper.COLUMN_PROTEINS, food.getProteins());
            values.put(DatabaseHelper.COLUMN_FATS, food.getFats());
            values.put(DatabaseHelper.COLUMN_CARBS, food.getCarbs());
            values.put(DatabaseHelper.COLUMN_PORTION_SIZE, food.getPortionSize());
            values.put(DatabaseHelper.COLUMN_IS_CUSTOM, food.isCustom() ? 1 : 0);

            int rowsAffected = db.update(
                    DatabaseHelper.TABLE_FOOD,
                    values,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(food.getId())}
            );

            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error while updating food: " + e.getMessage());
        } finally {
            db.close();
        }

        return success;
    }

    // Удаление продукта
    public boolean deleteFood(long foodId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            int rowsAffected = db.delete(
                    DatabaseHelper.TABLE_FOOD,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(foodId)}
            );

            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error while deleting food: " + e.getMessage());
        } finally {
            db.close();
        }

        return success;
    }

    // Получение продукта по ID
    public Food getFoodById(long foodId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Food food = null;

        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_FOOD,
                    null,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(foodId)},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                food = cursorToFood(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting food by ID: " + e.getMessage());
        } finally {
            db.close();
        }

        return food;
    }

    // Получение всех продуктов
    public List<Food> getAllFoods() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Food> foodList = new ArrayList<>();

        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_FOOD,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_FOOD_NAME + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Food food = cursorToFood(cursor);
                    foodList.add(food);
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting all foods: " + e.getMessage());
        } finally {
            db.close();
        }

        return foodList;
    }

    // Поиск продуктов по названию
    public List<Food> searchFoodsByName(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Food> foodList = new ArrayList<>();

        try {
            String searchQuery = "%" + query + "%";
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_FOOD,
                    null,
                    DatabaseHelper.COLUMN_FOOD_NAME + " LIKE ?",
                    new String[]{searchQuery},
                    null,
                    null,
                    DatabaseHelper.COLUMN_FOOD_NAME + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Food food = cursorToFood(cursor);
                    foodList.add(food);
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while searching foods: " + e.getMessage());
        } finally {
            db.close();
        }

        return foodList;
    }

    // Получение пользовательских продуктов
    public List<Food> getCustomFoods() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Food> foodList = new ArrayList<>();

        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_FOOD,
                    null,
                    DatabaseHelper.COLUMN_IS_CUSTOM + " = ?",
                    new String[]{"1"},
                    null,
                    null,
                    DatabaseHelper.COLUMN_FOOD_NAME + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Food food = cursorToFood(cursor);
                    foodList.add(food);
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting custom foods: " + e.getMessage());
        } finally {
            db.close();
        }

        return foodList;
    }

    // Преобразование курсора в объект Food
    private Food cursorToFood(Cursor cursor) {
        Food food = new Food();

        food.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        food.setFoodName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FOOD_NAME)));
        food.setCalories(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CALORIES)));
        food.setProteins(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROTEINS)));
        food.setFats(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FATS)));
        food.setCarbs(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARBS)));
        food.setPortionSize(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PORTION_SIZE)));
        food.setCustom(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_CUSTOM)) == 1);

        return food;
    }
}