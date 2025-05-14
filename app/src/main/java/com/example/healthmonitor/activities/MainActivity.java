package com.example.healthmonitor.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.healthmonitor.R;
import com.example.healthmonitor.database.DataManager;
import com.example.healthmonitor.models.User;
import com.example.healthmonitor.utils.PreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private DataManager dataManager;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация менеджера настроек и применение темы
        preferencesManager = PreferencesManager.getInstance(this);
        if (preferencesManager.isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        // Инициализация менеджера данных
        dataManager = DataManager.getInstance(this);

        // Синхронизация, если включена
        if (preferencesManager.isSyncEnabled()) {
            dataManager.setUseCloudDB(true);
        }

        // Настройка Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Настройка BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Проверка первого запуска
        if (preferencesManager.isFirstLaunch()) {
            showWelcomeDialog();
            preferencesManager.setFirstLaunch(false);
        }

        // Проверка наличия пользователя
        User currentUser = dataManager.getUser();
        boolean hasUser = currentUser != null;

        // По умолчанию открываем экран дневника питания или профиля, если пользователь не настроен
        if (savedInstanceState == null) {
            if (hasUser) {
                bottomNavigationView.setSelectedItemId(R.id.nav_diary);
            } else {
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = "";

        int itemId = item.getItemId();
        if (itemId == R.id.nav_diary) {
            selectedFragment = new FoodDiaryFragment();
            title = getString(R.string.food_diary);
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
            title = getString(R.string.profile);
        } else if (itemId == R.id.nav_statistics) {
            selectedFragment = new StatisticsFragment();
            title = getString(R.string.statistics);
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            // Обновляем заголовок
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showWelcomeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Добро пожаловать в HealthMonitor!")
                .setMessage("Это приложение поможет вам следить за питанием и здоровьем. " +
                        "Начните с заполнения профиля, а затем ведите дневник питания и отслеживайте прогресс.")
                .setPositiveButton("Начать", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("О приложении")
                .setMessage("HealthMonitor - приложение для мониторинга здоровья, питания и калорийности блюд.\n\n" +
                        "Версия: 1.0\n" +
                        "© 2025 HealthMonitor")
                .setPositiveButton("ОК", null)
                .show();
    }
}