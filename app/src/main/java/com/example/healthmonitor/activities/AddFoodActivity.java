package com.example.healthmonitor.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.healthmonitor.R;
import com.example.healthmonitor.database.DataManager;
import com.example.healthmonitor.fragments.CustomFoodFragment;
import com.example.healthmonitor.fragments.SearchFoodFragment;
import com.example.healthmonitor.models.Food;
import com.example.healthmonitor.models.Meal;
import com.example.healthmonitor.models.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

public class AddFoodActivity extends AppCompatActivity implements SearchFoodFragment.OnFoodSelectedListener {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Toolbar toolbar;

    private String date;
    private String mealType;
    private DataManager dataManager;
    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        // Получаем параметры
        date = getIntent().getStringExtra("date");
        mealType = getIntent().getStringExtra("meal_type");

        // Инициализация DataManager
        dataManager = DataManager.getInstance(this);

        // Получаем текущего пользователя
        currentUser = dataManager.getUser();

        initUI();
        setupViewPager();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    private void setupViewPager() {
        // Создаем адаптер для ViewPager
        AddFoodPagerAdapter adapter = new AddFoodPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Связываем TabLayout с ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Поиск");
            } else {
                tab.setText("Свой продукт");
            }
        }).attach();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onFoodSelected(Food food) {
        showAddToDiaryDialog(food);
    }

    // Метод для отображения диалога добавления продукта в дневник
    private void showAddToDiaryDialog(Food food) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_to_diary);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // Инициализация элементов диалога
        AutoCompleteTextView spinnerMealType = dialog.findViewById(R.id.spinner_meal_type);
        TextInputEditText etServings = dialog.findViewById(R.id.et_servings);
        TextView tvCalculatedCalories = dialog.findViewById(R.id.tv_calculated_calories);
        TextView tvCalculatedPortion = dialog.findViewById(R.id.tv_calculated_portion);
        TextView tvCalculatedProteins = dialog.findViewById(R.id.tv_calculated_proteins);
        TextView tvCalculatedFats = dialog.findViewById(R.id.tv_calculated_fats);
        TextView tvCalculatedCarbs = dialog.findViewById(R.id.tv_calculated_carbs);
        Button btnAddFood = dialog.findViewById(R.id.btn_add_food);

        // Установка типа приема пищи
        String[] mealTypes = {
                getString(R.string.breakfast),
                getString(R.string.lunch),
                getString(R.string.dinner),
                getString(R.string.snack)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, mealTypes);
        spinnerMealType.setAdapter(adapter);

        // Если известен тип приема пищи, устанавливаем его
        if (mealType != null && !mealType.isEmpty()) {
            spinnerMealType.setText(mealType, false);
        } else {
            spinnerMealType.setText(mealTypes[0], false);
        }

        // Слушатель изменения количества порций
        etServings.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateNutritionValues(s.toString(), food, tvCalculatedCalories, tvCalculatedPortion,
                        tvCalculatedProteins, tvCalculatedFats, tvCalculatedCarbs);
            }
        });

        // Начальное обновление значений питательных веществ
        updateNutritionValues("1.0", food, tvCalculatedCalories, tvCalculatedPortion,
                tvCalculatedProteins, tvCalculatedFats, tvCalculatedCarbs);

        // Обработчик кнопки добавления
        btnAddFood.setOnClickListener(v -> {
            // Получаем значения из диалога
            String selectedMealType = spinnerMealType.getText().toString();
            String servingsText = etServings.getText().toString();

            if (servingsText.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, укажите количество порций", Toast.LENGTH_SHORT).show();
                return;
            }

            float servings = Float.parseFloat(servingsText);

            // Создаем объект Meal
            Meal meal = new Meal();
            meal.setUserId(currentUser.getId());
            meal.setFoodId(food.getId());
            meal.setMealType(selectedMealType);
            meal.setDate(date);
            meal.setServings(servings);

            // Сохраняем в базу данных через DataManager
            long mealId = dataManager.addMeal(meal);

            if (mealId > 0) {
                Toast.makeText(this, getString(R.string.food_added), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish(); // Закрываем активность, чтобы вернуться к дневнику
            } else {
                Toast.makeText(this, "Ошибка при добавлении продукта", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    // Обновление значений питательных веществ в диалоге
    private void updateNutritionValues(String servingsText, Food food,
                                       TextView tvCalories, TextView tvPortion,
                                       TextView tvProteins, TextView tvFats, TextView tvCarbs) {
        if (servingsText.isEmpty()) {
            return;
        }

        try {
            float servings = Float.parseFloat(servingsText);

            int portionSize = (int) (food.getPortionSize() * servings);
            int calories = food.calculateCaloriesForPortion(portionSize);
            float proteins = food.calculateProteinsForPortion(portionSize);
            float fats = food.calculateFatsForPortion(portionSize);
            float carbs = food.calculateCarbsForPortion(portionSize);

            tvCalories.setText(calories + " ккал");
            tvPortion.setText(portionSize + " г");
            tvProteins.setText(String.format("%.1f г", proteins));
            tvFats.setText(String.format("%.1f г", fats));
            tvCarbs.setText(String.format("%.1f г", carbs));

        } catch (NumberFormatException e) {
            // Игнорируем ошибку
        }
    }

    // Адаптер для ViewPager
    private class AddFoodPagerAdapter extends FragmentStateAdapter {

        public AddFoodPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new SearchFoodFragment();
            } else {
                return new CustomFoodFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}