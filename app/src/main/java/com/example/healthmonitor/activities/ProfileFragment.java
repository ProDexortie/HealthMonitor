package com.example.healthmonitor.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.healthmonitor.R;
import com.example.healthmonitor.database.DataManager;
import com.example.healthmonitor.models.User;
import com.example.healthmonitor.utils.CalorieCalculator;
import com.example.healthmonitor.utils.DateUtils;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private TextInputEditText etName, etAge, etHeight, etWeight, etTargetWeight;
    private AutoCompleteTextView spinnerGender, spinnerActivityLevel;
    private Button btnSaveProfile;
    private TextView tvBmiValue, tvBmiCategory, tvBmr, tvMaintenance, tvWeightLoss, tvWeightGain;

    private DataManager dataManager;
    private User currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = DataManager.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(view);
        setupActivityLevelSpinner();
        setupGenderSpinner();
        loadUserData();
        setupSaveButton();

        return view;
    }

    private void initUI(View view) {
        etName = view.findViewById(R.id.et_name);
        etAge = view.findViewById(R.id.et_age);
        etHeight = view.findViewById(R.id.et_height);
        etWeight = view.findViewById(R.id.et_weight);
        etTargetWeight = view.findViewById(R.id.et_target_weight);
        spinnerGender = view.findViewById(R.id.spinner_gender);
        spinnerActivityLevel = view.findViewById(R.id.spinner_activity_level);
        btnSaveProfile = view.findViewById(R.id.btn_save_profile);
        tvBmiValue = view.findViewById(R.id.tv_bmi_value);
        tvBmiCategory = view.findViewById(R.id.tv_bmi_category);
        tvBmr = view.findViewById(R.id.tv_bmr);
        tvMaintenance = view.findViewById(R.id.tv_maintenance);
        tvWeightLoss = view.findViewById(R.id.tv_weight_loss);
        tvWeightGain = view.findViewById(R.id.tv_weight_gain);
    }

    private void setupGenderSpinner() {
        String[] genders = {getString(R.string.male), getString(R.string.female)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, genders);
        spinnerGender.setAdapter(adapter);
    }

    private void setupActivityLevelSpinner() {
        String[] activityLevels = {
                getString(R.string.activity_level_sedentary),
                getString(R.string.activity_level_light),
                getString(R.string.activity_level_moderate),
                getString(R.string.activity_level_active),
                getString(R.string.activity_level_very_active)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, activityLevels);
        spinnerActivityLevel.setAdapter(adapter);
    }

    private void loadUserData() {
        currentUser = dataManager.getUser();

        if (currentUser != null) {
            etName.setText(currentUser.getName());
            etAge.setText(String.valueOf(currentUser.getAge()));
            etHeight.setText(String.valueOf(currentUser.getHeight()));
            etWeight.setText(String.valueOf(currentUser.getWeight()));
            etTargetWeight.setText(String.valueOf(currentUser.getTargetWeight()));
            spinnerGender.setText(currentUser.getGender(), false);
            spinnerActivityLevel.setText(currentUser.getActivityLevel(), false);

            updateStatsUI();
        } else {
            // Создаем дефолтного пользователя
            currentUser = new User();
            currentUser.setName("");
            currentUser.setAge(25);
            currentUser.setGender(getString(R.string.male));
            currentUser.setHeight(170);
            currentUser.setWeight(70);
            currentUser.setTargetWeight(70);
            currentUser.setActivityLevel(getString(R.string.activity_level_moderate));
            currentUser.setDailyCalories(2000);

            etAge.setText(String.valueOf(currentUser.getAge()));
            etHeight.setText(String.valueOf(currentUser.getHeight()));
            etWeight.setText(String.valueOf(currentUser.getWeight()));
            etTargetWeight.setText(String.valueOf(currentUser.getTargetWeight()));
            spinnerGender.setText(currentUser.getGender(), false);
            spinnerActivityLevel.setText(currentUser.getActivityLevel(), false);
        }
    }

    private void updateStatsUI() {
        if (currentUser != null) {
            float bmi = currentUser.calculateBMI();
            String bmiCategory = currentUser.getBMICategory();
            int bmr = currentUser.calculateBMR();
            int tdee = currentUser.calculateDailyCalories();
            int weightLossCalories = Math.max(1200, tdee - 500);
            int weightGainCalories = tdee + 500;

            tvBmiValue.setText(CalorieCalculator.formatDecimal(bmi));
            tvBmiCategory.setText(bmiCategory);
            tvBmr.setText(bmr + " ккал");
            tvMaintenance.setText(tdee + " ккал");
            tvWeightLoss.setText(weightLossCalories + " ккал");
            tvWeightGain.setText(weightGainCalories + " ккал");
        }
    }

    private void setupSaveButton() {
        btnSaveProfile.setOnClickListener(v -> {
            if (validateInputs()) {
                saveUserData();
            }
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etName.getText())) {
            Toast.makeText(requireContext(), "Пожалуйста, введите имя", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(etAge.getText())) {
            Toast.makeText(requireContext(), "Пожалуйста, введите возраст", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(etHeight.getText())) {
            Toast.makeText(requireContext(), "Пожалуйста, введите рост", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(etWeight.getText())) {
            Toast.makeText(requireContext(), "Пожалуйста, введите вес", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(etTargetWeight.getText())) {
            Toast.makeText(requireContext(), "Пожалуйста, введите целевой вес", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(spinnerGender.getText())) {
            Toast.makeText(requireContext(), "Пожалуйста, выберите пол", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(spinnerActivityLevel.getText())) {
            Toast.makeText(requireContext(), "Пожалуйста, выберите уровень активности", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserData() {
        // Обновляем данные пользователя
        currentUser.setName(etName.getText().toString());
        currentUser.setAge(Integer.parseInt(etAge.getText().toString()));
        currentUser.setGender(spinnerGender.getText().toString());
        currentUser.setHeight(Float.parseFloat(etHeight.getText().toString()));

        // Получаем текущий вес
        float newWeight = Float.parseFloat(etWeight.getText().toString());
        float oldWeight = currentUser.getWeight();

        // Обновляем вес в модели
        currentUser.setWeight(newWeight);
        currentUser.setTargetWeight(Float.parseFloat(etTargetWeight.getText().toString()));
        currentUser.setActivityLevel(spinnerActivityLevel.getText().toString());

        // Расчет дневной нормы калорий
        int dailyCalories = currentUser.calculateDailyCalories();
        currentUser.setDailyCalories(dailyCalories);

        // Сохраняем пользователя
        long userId = dataManager.saveUser(currentUser);
        boolean success = userId > 0;

        // Если вес изменился, записываем это в журнал
        if (success && newWeight != oldWeight) {
            dataManager.updateUserWeight(userId, newWeight, DateUtils.getCurrentDate());
        }

        if (success) {
            updateStatsUI();
            Toast.makeText(requireContext(), getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Ошибка при сохранении профиля", Toast.LENGTH_SHORT).show();
        }
    }
}