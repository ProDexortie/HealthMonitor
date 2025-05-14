package com.example.healthmonitor.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthmonitor.R;
import com.example.healthmonitor.activities.AddFoodActivity;
import com.example.healthmonitor.database.DataManager;
import com.example.healthmonitor.models.Food;
import com.google.android.material.textfield.TextInputEditText;

public class CustomFoodFragment extends Fragment {

    private TextInputEditText etFoodName;
    private TextInputEditText etPortionSize;
    private TextInputEditText etCalories;
    private TextInputEditText etProteins;
    private TextInputEditText etFats;
    private TextInputEditText etCarbs;
    private Button btnSaveFood;

    private DataManager dataManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = DataManager.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_food, container, false);

        initUI(view);
        setupSaveButton();

        return view;
    }

    private void initUI(View view) {
        etFoodName = view.findViewById(R.id.et_food_name);
        etPortionSize = view.findViewById(R.id.et_portion_size);
        etCalories = view.findViewById(R.id.et_calories);
        etProteins = view.findViewById(R.id.et_proteins);
        etFats = view.findViewById(R.id.et_fats);
        etCarbs = view.findViewById(R.id.et_carbs);
        btnSaveFood = view.findViewById(R.id.btn_save_food);
    }

    private void setupSaveButton() {
        btnSaveFood.setOnClickListener(v -> {
            if (validateInputs()) {
                saveCustomFood();
            }
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etFoodName.getText())) {
            Toast.makeText(requireContext(), "Введите название продукта", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(etPortionSize.getText())) {
            Toast.makeText(requireContext(), "Укажите размер порции", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(etCalories.getText())) {
            Toast.makeText(requireContext(), "Укажите калорийность", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveCustomFood() {
        // Создаем объект Food
        Food food = new Food();
        food.setFoodName(etFoodName.getText().toString());
        food.setPortionSize(Integer.parseInt(etPortionSize.getText().toString()));
        food.setCalories(Integer.parseInt(etCalories.getText().toString()));

        // Устанавливаем макронутриенты, если указаны
        food.setProteins(TextUtils.isEmpty(etProteins.getText()) ? 0 :
                Float.parseFloat(etProteins.getText().toString()));
        food.setFats(TextUtils.isEmpty(etFats.getText()) ? 0 :
                Float.parseFloat(etFats.getText().toString()));
        food.setCarbs(TextUtils.isEmpty(etCarbs.getText()) ? 0 :
                Float.parseFloat(etCarbs.getText().toString()));

        // Отмечаем как пользовательский продукт
        food.setCustom(true);

        // Сохраняем в базу данных через DataManager
        long foodId = dataManager.addFood(food);

        if (foodId > 0) {
            food.setId(foodId);
            Toast.makeText(requireContext(), "Продукт добавлен", Toast.LENGTH_SHORT).show();

            // Вызываем метод обработки выбора продукта в родительской активности
            if (getActivity() instanceof AddFoodActivity) {
                ((AddFoodActivity) getActivity()).onFoodSelected(food);
            }

            // Очищаем поля ввода
            clearInputs();
        } else {
            Toast.makeText(requireContext(), "Ошибка при добавлении продукта", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        etFoodName.setText("");
        etPortionSize.setText("100");
        etCalories.setText("");
        etProteins.setText("");
        etFats.setText("");
        etCarbs.setText("");
        etFoodName.requestFocus();
    }
}