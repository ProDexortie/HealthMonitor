package com.example.healthmonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmonitor.R;
import com.example.healthmonitor.database.DataManager;
import com.example.healthmonitor.models.Meal;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private final Context context;
    private final List<Meal> mealList;
    private final OnFoodItemClickListener listener;
    private final DataManager dataManager;

    // Интерфейс слушателя для обработки нажатий
    public interface OnFoodItemClickListener {
        void onFoodItemClick(Meal meal);
        void onFoodItemDeleted(Meal meal);
    }

    public FoodAdapter(Context context, List<Meal> mealList, OnFoodItemClickListener listener) {
        this.context = context;
        this.mealList = mealList;
        this.listener = listener;
        this.dataManager = DataManager.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.bindData(meal);
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFoodName;
        private final TextView tvFoodPortion;
        private final TextView tvFoodMacros;
        private final TextView tvFoodCalories;
        private final ImageButton btnDeleteFood;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodPortion = itemView.findViewById(R.id.tv_food_portion);
            tvFoodMacros = itemView.findViewById(R.id.tv_food_macros);
            tvFoodCalories = itemView.findViewById(R.id.tv_food_calories);
            btnDeleteFood = itemView.findViewById(R.id.btn_delete_food);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFoodItemClick(mealList.get(position));
                }
            });

            btnDeleteFood.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Meal meal = mealList.get(position);
                    if (dataManager.deleteMeal(meal.getId())) {
                        mealList.remove(position);
                        notifyItemRemoved(position);
                        listener.onFoodItemDeleted(meal);
                        Toast.makeText(context, R.string.food_deleted, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Ошибка при удалении блюда", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void bindData(Meal meal) {
            tvFoodName.setText(meal.getFoodName());

            // Форматируем информацию о порции
            String portionText = String.format("%d г", meal.getActualPortionSize());
            if (meal.getServings() != 1.0f) {
                portionText += String.format(" (%.1f порции)", meal.getServings());
            }
            tvFoodPortion.setText(portionText);

            // Форматируем информацию о макронутриентах
            String macrosText = String.format("Б: %.1f г • Ж: %.1f г • У: %.1f г",
                    meal.getTotalProteins(), meal.getTotalFats(), meal.getTotalCarbs());
            tvFoodMacros.setText(macrosText);

            // Отображаем калории
            tvFoodCalories.setText(String.format("%d ккал", meal.getTotalCalories()));
        }
    }
}