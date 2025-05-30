package com.example.healthmonitor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmonitor.R;
import com.example.healthmonitor.adapters.MealCategoryAdapter;
import com.example.healthmonitor.database.DataManager;
import com.example.healthmonitor.database.MealDAO;
import com.example.healthmonitor.models.User;
import com.example.healthmonitor.utils.DateUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodDiaryFragment extends Fragment {

    private TextView tvCurrentDate, tvCaloriesConsumed, tvCaloriesRemaining;
    private TextView tvProteins, tvFats, tvCarbs;
    private ImageButton btnPrevDay, btnNextDay;
    private RecyclerView recyclerMeals;
    private ProgressBar progressCalories;
    private FloatingActionButton fabAddFood;

    private DataManager dataManager;
    private User currentUser;
    private String currentDate;
    private MealCategoryAdapter mealCategoryAdapter;

    private List<String> mealCategories;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = DataManager.getInstance(requireContext());
        currentDate = DateUtils.getCurrentDate();
        mealCategories = Arrays.asList(
                getString(R.string.breakfast),
                getString(R.string.lunch),
                getString(R.string.dinner),
                getString(R.string.snack)
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_diary, container, false);

        initUI(view);
        setupListeners();
        loadUserData();
        setupRecyclerView();
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void initUI(View view) {
        tvCurrentDate = view.findViewById(R.id.tv_current_date);
        tvCaloriesConsumed = view.findViewById(R.id.tv_calories_consumed);
        tvCaloriesRemaining = view.findViewById(R.id.tv_calories_remaining);
        tvProteins = view.findViewById(R.id.tv_proteins);
        tvFats = view.findViewById(R.id.tv_fats);
        tvCarbs = view.findViewById(R.id.tv_carbs);
        btnPrevDay = view.findViewById(R.id.btn_prev_day);
        btnNextDay = view.findViewById(R.id.btn_next_day);
        recyclerMeals = view.findViewById(R.id.recycler_meals);
        progressCalories = view.findViewById(R.id.progress_calories);
        fabAddFood = view.findViewById(R.id.fab_add_food);
    }

    private void setupListeners() {
        btnPrevDay.setOnClickListener(v -> {
            currentDate = DateUtils.getDateWithOffset(currentDate, -1); // Правильно!
            updateUI();
        });

        btnNextDay.setOnClickListener(v -> {
            String nextDate = DateUtils.getDateWithOffset(currentDate, 1);
            if (DateUtils.compareDates(nextDate, DateUtils.getCurrentDate()) <= 0) {
                currentDate = nextDate;
                updateUI();
            }
        });

        fabAddFood.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddFoodActivity.class);
            intent.putExtra("date", currentDate);
            startActivity(intent);
        });
    }

    private void loadUserData() {
        currentUser = dataManager.getUser();
        if (currentUser == null) {
            // Перенаправляем на экран профиля, если пользователь не настроен
            ((MainActivity) requireActivity()).onNavigationItemSelected(
                    requireActivity().findViewById(R.id.nav_profile).findViewById(R.id.nav_profile));
        }
    }

    private void setupRecyclerView() {
        recyclerMeals.setLayoutManager(new LinearLayoutManager(requireContext()));
        mealCategoryAdapter = new MealCategoryAdapter(requireContext(), new ArrayList<>(), currentDate);
        recyclerMeals.setAdapter(mealCategoryAdapter);
    }

    private void updateUI() {
        if (currentUser == null) {
            return;
        }

        // Обновляем дату
        tvCurrentDate.setText(DateUtils.formatDateFull(currentDate));

        // Получаем данные о питании за день
        MealDAO.DailySummary summary = dataManager.getDailySummary(currentUser.getId(), currentDate);

        // Обновляем информацию о калориях
        int caloriesConsumed = summary.totalCalories;
        int dailyCalories = currentUser.getDailyCalories();
        int caloriesRemaining = Math.max(0, dailyCalories - caloriesConsumed);

        tvCaloriesConsumed.setText(String.valueOf(caloriesConsumed));
        tvCaloriesRemaining.setText(String.valueOf(caloriesRemaining));

        // Обновляем информацию о макронутриентах
        tvProteins.setText(String.format("%.1f г", summary.totalProteins));
        tvFats.setText(String.format("%.1f г", summary.totalFats));
        tvCarbs.setText(String.format("%.1f г", summary.totalCarbs));

        // Обновляем прогресс-бар
        int progress = (int) (((float) caloriesConsumed / dailyCalories) * 100);
        progressCalories.setProgress(Math.min(progress, 100));

        // Обновляем список приемов пищи
        mealCategoryAdapter.updateData(mealCategories, currentUser.getId(), currentDate);
    }
}