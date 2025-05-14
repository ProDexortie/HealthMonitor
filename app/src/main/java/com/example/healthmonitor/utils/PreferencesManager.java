package com.example.healthmonitor.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Класс для управления настройками приложения.
 * Использует SharedPreferences для хранения пользовательских настроек.
 */
public class PreferencesManager {

    private static final String PREF_NAME = "health_monitor_prefs";

    // Ключи для настроек
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_NIGHT_MODE = "night_mode";
    private static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    private static final String KEY_NOTIFICATION_TIME = "notification_time";
    private static final String KEY_SYNC_ENABLED = "sync_enabled";
    private static final String KEY_MEASUREMENT_SYSTEM = "measurement_system";
    private static final String KEY_USER_ID = "user_id";

    // Значения по умолчанию
    private static final boolean DEFAULT_FIRST_LAUNCH = true;
    private static final boolean DEFAULT_NIGHT_MODE = false;
    private static final boolean DEFAULT_NOTIFICATION_ENABLED = false;
    private static final String DEFAULT_NOTIFICATION_TIME = "09:00";
    private static final boolean DEFAULT_SYNC_ENABLED = false;
    private static final String DEFAULT_MEASUREMENT_SYSTEM = "metric"; // metric или imperial
    private static final long DEFAULT_USER_ID = -1;

    private final SharedPreferences preferences;
    private static PreferencesManager instance;

    // Получение единственного экземпляра
    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    // Конструктор
    private PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Методы для работы с настройками

    // Проверка первого запуска приложения
    public boolean isFirstLaunch() {
        return preferences.getBoolean(KEY_FIRST_LAUNCH, DEFAULT_FIRST_LAUNCH);
    }

    // Установка флага первого запуска
    public void setFirstLaunch(boolean isFirstLaunch) {
        preferences.edit().putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch).apply();
    }

    // Получение режима темы
    public boolean isNightMode() {
        return preferences.getBoolean(KEY_NIGHT_MODE, DEFAULT_NIGHT_MODE);
    }

    // Установка режима темы
    public void setNightMode(boolean isNightMode) {
        preferences.edit().putBoolean(KEY_NIGHT_MODE, isNightMode).apply();
    }

    // Проверка, включены ли уведомления
    public boolean isNotificationEnabled() {
        return preferences.getBoolean(KEY_NOTIFICATION_ENABLED, DEFAULT_NOTIFICATION_ENABLED);
    }

    // Включение/выключение уведомлений
    public void setNotificationEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply();
    }

    // Получение времени уведомления
    public String getNotificationTime() {
        return preferences.getString(KEY_NOTIFICATION_TIME, DEFAULT_NOTIFICATION_TIME);
    }

    // Установка времени уведомления
    public void setNotificationTime(String time) {
        preferences.edit().putString(KEY_NOTIFICATION_TIME, time).apply();
    }

    // Проверка, включена ли синхронизация
    public boolean isSyncEnabled() {
        return preferences.getBoolean(KEY_SYNC_ENABLED, DEFAULT_SYNC_ENABLED);
    }

    // Включение/выключение синхронизации
    public void setSyncEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_SYNC_ENABLED, enabled).apply();
    }

    // Получение системы измерения
    public String getMeasurementSystem() {
        return preferences.getString(KEY_MEASUREMENT_SYSTEM, DEFAULT_MEASUREMENT_SYSTEM);
    }

    // Установка системы измерения
    public void setMeasurementSystem(String system) {
        if (system.equals("metric") || system.equals("imperial")) {
            preferences.edit().putString(KEY_MEASUREMENT_SYSTEM, system).apply();
        }
    }

    // Получение ID пользователя
    public long getUserId() {
        return preferences.getLong(KEY_USER_ID, DEFAULT_USER_ID);
    }

    // Установка ID пользователя
    public void setUserId(long userId) {
        preferences.edit().putLong(KEY_USER_ID, userId).apply();
    }

    // Метод для сброса всех настроек
    public void resetAllSettings() {
        preferences.edit().clear().apply();
    }
}