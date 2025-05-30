package com.example.healthmonitor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmonitor.R;
import com.example.healthmonitor.activities.AddFoodActivity;
import com.example.healthmonitor.database.DataManager;
import com.example.healthmonitor.models.Meal;

import java.util.List;

public class MealCategoryAdapter extends RecyclerView.Adapter<MealCategoryAdapter.ViewHolder> implements FoodAdapter.OnFoodItemClickListener {

    private final Context context;
    private List<String> mealCategories;
    private String currentDate; // ИЗМЕНЕНО: убрали final
    private final long userId;
    private final DataManager dataManager;

    public MealCategoryAdapter(Context context, List<String> mealCategories, String currentDate) {
        this.context = context;
        this.mealCategories = mealCategories;
        this.currentDate = currentDate;
        this.dataManager = DataManager.getInstance(context);

        // Получаем ID пользователя
        this.userId = dataManager.getUser().getId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String mealCategory = mealCategories.get(position);
        holder.bindData(mealCategory);
    }

    @Override
    public int getItemCount() {
        return mealCategories.size();
    }

    @Override
    public void onFoodItemClick(Meal meal) {
        // Обработка нажатия на элемент еды - можно реализовать редактирование
    }

    @Override
    public void onFoodItemDeleted(Meal meal) {
        // Обновляем информацию после удаления
        notifyDataSetChanged();
    }

    public void updateData(List<String> mealCategories, long userId, String currentDate) {
        this.mealCategories = mealCategories;
        this.currentDate = currentDate; // ДОБАВЛЕНО: обновляем currentDate
        notifyDataSetChanged();
    }

    // НОВЫЙ МЕТОД: для обновления только даты
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMealType;
        private final TextView tvMealCalories;
        private final RecyclerView recyclerFoods;
        private final TextView tvNoFoods;
        private final ImageButton btnAddMeal;
        private FoodAdapter foodAdapter;
        private List<Meal> mealList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMealType = itemView.findViewById(R.id.tv_meal_type);
            tvMealCalories = itemView.findViewById(R.id.tv_meal_calories);
            recyclerFoods = itemView.findViewById(R.id.recycler_foods);
            tvNoFoods = itemView.findViewById(R.id.tv_no_foods);
            btnAddMeal = itemView.findViewById(R.id.btn_add_meal);

            recyclerFoods.setLayoutManager(new LinearLayoutManager(context));

            btnAddMeal.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String mealType = mealCategories.get(position);

                    // Запуск активности добавления продукта с типом приема пищи
                    Intent intent = new Intent(context, AddFoodActivity.class);
                    intent.putExtra("meal_type", mealType);
                    intent.putExtra("date", currentDate); // ИСПОЛЬЗУЕМ актуальную дату
                    context.startActivity(intent);
                }
            });
        }

        public void bindData(String mealCategory) {
            tvMealType.setText(mealCategory);

            // Получаем список блюд для данного типа приема пищи через DataManager
            // ИСПОЛЬЗУЕМ актуальную дату
            mealList = dataManager.getMealsByUserDateAndType(userId, currentDate, mealCategory);

            // Если список пуст, показываем сообщение
            if (mealList.isEmpty()) {
                tvNoFoods.setVisibility(View.VISIBLE);
                recyclerFoods.setVisibility(View.GONE);
                tvMealCalories.setText("0 ккал");
            } else {
                tvNoFoods.setVisibility(View.GONE);
                recyclerFoods.setVisibility(View.VISIBLE);

                // Создаем и устанавливаем адаптер
                foodAdapter = new FoodAdapter(context, mealList, MealCategoryAdapter.this);
                recyclerFoods.setAdapter(foodAdapter);

                // Подсчитываем общее количество калорий
                int totalCalories = 0;
                for (Meal meal : mealList) {
                    totalCalories += meal.getTotalCalories();
                }
                tvMealCalories.setText(totalCalories + " ккал");
            }
        }
    }
}