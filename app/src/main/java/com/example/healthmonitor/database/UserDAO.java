package com.example.healthmonitor.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.healthmonitor.models.User;

public class UserDAO {
    private static final String TAG = "UserDAO";
    private DatabaseHelper dbHelper;

    public UserDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // Добавление нового пользователя
    public long addUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long userId = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, user.getName());
            values.put(DatabaseHelper.COLUMN_AGE, user.getAge());
            values.put(DatabaseHelper.COLUMN_GENDER, user.getGender());
            values.put(DatabaseHelper.COLUMN_HEIGHT, user.getHeight());
            values.put(DatabaseHelper.COLUMN_WEIGHT, user.getWeight());
            values.put(DatabaseHelper.COLUMN_TARGET_WEIGHT, user.getTargetWeight());
            values.put(DatabaseHelper.COLUMN_ACTIVITY_LEVEL, user.getActivityLevel());
            values.put(DatabaseHelper.COLUMN_DAILY_CALORIES, user.getDailyCalories());

            userId = db.insert(DatabaseHelper.TABLE_USER, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error while adding user: " + e.getMessage());
        } finally {
            db.close();
        }

        return userId;
    }

    // Обновление данных пользователя
    public boolean updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, user.getName());
            values.put(DatabaseHelper.COLUMN_AGE, user.getAge());
            values.put(DatabaseHelper.COLUMN_GENDER, user.getGender());
            values.put(DatabaseHelper.COLUMN_HEIGHT, user.getHeight());
            values.put(DatabaseHelper.COLUMN_WEIGHT, user.getWeight());
            values.put(DatabaseHelper.COLUMN_TARGET_WEIGHT, user.getTargetWeight());
            values.put(DatabaseHelper.COLUMN_ACTIVITY_LEVEL, user.getActivityLevel());
            values.put(DatabaseHelper.COLUMN_DAILY_CALORIES, user.getDailyCalories());

            int rowsAffected = db.update(
                    DatabaseHelper.TABLE_USER,
                    values,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(user.getId())}
            );

            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error while updating user: " + e.getMessage());
        } finally {
            db.close();
        }

        return success;
    }

    // Получение пользователя по ID
    public User getUserById(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_USER,
                    null,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                user = cursorToUser(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting user by ID: " + e.getMessage());
        } finally {
            db.close();
        }

        return user;
    }

    // Получение первого пользователя (для простоты, в реальном приложении может быть иная логика)
    public User getFirstUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        try {
            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_USER,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_ID + " ASC",
                    "1"
            );

            if (cursor != null && cursor.moveToFirst()) {
                user = cursorToUser(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting first user: " + e.getMessage());
        } finally {
            db.close();
        }

        return user;
    }

    // Проверка наличия пользователей в базе
    public boolean hasUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean hasUsers = false;

        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USER, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                hasUsers = count > 0;
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking if has users: " + e.getMessage());
        } finally {
            db.close();
        }

        return hasUsers;
    }

    // Обновление веса пользователя и запись в журнал
    public boolean updateUserWeight(long userId, float newWeight, String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            db.beginTransaction();

            // Обновляем вес пользователя
            ContentValues userValues = new ContentValues();
            userValues.put(DatabaseHelper.COLUMN_WEIGHT, newWeight);

            db.update(
                    DatabaseHelper.TABLE_USER,
                    userValues,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );

            // Добавляем запись в журнал веса
            ContentValues logValues = new ContentValues();
            logValues.put(DatabaseHelper.COLUMN_USER_ID, userId);
            logValues.put(DatabaseHelper.COLUMN_DATE, date);
            logValues.put(DatabaseHelper.COLUMN_WEIGHT_VALUE, newWeight);

            db.insert(DatabaseHelper.TABLE_WEIGHT_LOG, null, logValues);

            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            Log.e(TAG, "Error updating user weight: " + e.getMessage());
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
            db.close();
        }

        return success;
    }

    // Преобразование курсора в объект User
    private User cursorToUser(Cursor cursor) {
        User user = new User();

        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
        user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AGE)));
        user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER)));
        user.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEIGHT)));
        user.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT)));
        user.setTargetWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_WEIGHT)));
        user.setActivityLevel(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_LEVEL)));
        user.setDailyCalories(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DAILY_CALORIES)));

        return user;
    }
}