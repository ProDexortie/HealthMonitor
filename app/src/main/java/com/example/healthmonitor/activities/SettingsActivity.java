package com.example.healthmonitor.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.healthmonitor.R;
import com.example.healthmonitor.database.DataManager;
import com.example.healthmonitor.utils.PreferencesManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SwitchMaterial switchNightMode;
    private SwitchMaterial switchNotifications;
    private SwitchMaterial switchSync;
    private RadioGroup radioGroupMeasurement;
    private RadioButton radioMetric;
    private RadioButton radioImperial;
    private LinearLayout layoutNotificationTime;
    private Button btnNotificationTime;
    private Button btnSyncNow;
    private Button btnAbout;
    private Button btnReset;

    private PreferencesManager preferencesManager;
    private DataManager dataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferencesManager = PreferencesManager.getInstance(this);
        dataManager = DataManager.getInstance(this);

        initUI();
        loadSettings();
        setupListeners();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        switchNightMode = findViewById(R.id.switch_night_mode);
        switchNotifications = findViewById(R.id.switch_notifications);
        switchSync = findViewById(R.id.switch_sync);
        radioGroupMeasurement = findViewById(R.id.radio_group_measurement);
        radioMetric = findViewById(R.id.radio_metric);
        radioImperial = findViewById(R.id.radio_imperial);
        layoutNotificationTime = findViewById(R.id.layout_notification_time);
        btnNotificationTime = findViewById(R.id.btn_notification_time);
        btnSyncNow = findViewById(R.id.btn_sync_now);
        btnAbout = findViewById(R.id.btn_about);
        btnReset = findViewById(R.id.btn_reset);
    }

    private void loadSettings() {
        // Загрузка настроек из PreferencesManager
        switchNightMode.setChecked(preferencesManager.isNightMode());
        switchNotifications.setChecked(preferencesManager.isNotificationEnabled());
        switchSync.setChecked(preferencesManager.isSyncEnabled());

        // Настройка видимости времени уведомления
        layoutNotificationTime.setVisibility(preferencesManager.isNotificationEnabled() ? View.VISIBLE : View.GONE);
        btnNotificationTime.setText(preferencesManager.getNotificationTime());

        // Настройка системы измерения
        String measurementSystem = preferencesManager.getMeasurementSystem();
        if (measurementSystem.equals("metric")) {
            radioMetric.setChecked(true);
        } else {
            radioImperial.setChecked(true);
        }

        // Настройка кнопки синхронизации
        btnSyncNow.setEnabled(preferencesManager.isSyncEnabled());
    }

    private void setupListeners() {
        // Переключатель темной темы
        switchNightMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setNightMode(isChecked);
            // Применение темы
            AppCompatDelegate.setDefaultNightMode(isChecked ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Переключатель уведомлений
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setNotificationEnabled(isChecked);
            layoutNotificationTime.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Переключатель синхронизации
        switchSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setSyncEnabled(isChecked);
            btnSyncNow.setEnabled(isChecked);
            dataManager.setUseCloudDB(isChecked);
        });

        // Группа радиокнопок для системы измерения
        radioGroupMeasurement.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_metric) {
                preferencesManager.setMeasurementSystem("metric");
            } else {
                preferencesManager.setMeasurementSystem("imperial");
            }
        });

        // Кнопка выбора времени уведомления
        btnNotificationTime.setOnClickListener(v -> showTimePickerDialog());

        // Кнопка синхронизации
        btnSyncNow.setOnClickListener(v -> {
            if (dataManager.isUsingCloudDB()) {
                dataManager.performFullSync();
                showToast("Синхронизация выполнена");
            }
        });

        // Кнопка "О разработчике"
        btnAbout.setOnClickListener(v -> showAboutDialog());

        // Кнопка сброса настроек
        btnReset.setOnClickListener(v -> showResetConfirmDialog());
    }

    // Показ диалога выбора времени
    private void showTimePickerDialog() {
        String currentTime = preferencesManager.getNotificationTime();
        int hour = 9;
        int minute = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = sdf.parse(currentTime);
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    btnNotificationTime.setText(time);
                    preferencesManager.setNotificationTime(time);
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    // Показ диалога о разработчике
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("О разработчике")
                .setMessage("Приложение разработано студентом в рамках выпускной квалификационной работы.\n\n" +
                        "© 2025 HealthMonitor")
                .setPositiveButton("ОК", null)
                .show();
    }

    // Показ диалога подтверждения сброса настроек
    private void showResetConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Сбросить настройки")
                .setMessage("Вы уверены, что хотите сбросить все настройки приложения? Это действие нельзя отменить.")
                .setPositiveButton("Сбросить", (dialog, which) -> {
                    preferencesManager.resetAllSettings();
                    // Перезагрузка активности для применения настроек по умолчанию
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}