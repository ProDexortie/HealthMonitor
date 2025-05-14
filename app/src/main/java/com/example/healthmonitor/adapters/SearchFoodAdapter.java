package com.example.healthmonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmonitor.R;
import com.example.healthmonitor.models.Food;

import java.util.List;

public class SearchFoodAdapter extends RecyclerView.Adapter<SearchFoodAdapter.ViewHolder> {

    private final Context context;
    private final List<Food> foodList;
    private final OnFoodClickListener listener;

    // Интерфейс слушателя для обработки нажатий
    public interface OnFoodClickListener {
        void onFoodClick(Food food);
    }

    public SearchFoodAdapter(Context context, List<Food> foodList, OnFoodClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.bindData(food);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateData(List<Food> newFoodList) {
        foodList.clear();
        foodList.addAll(newFoodList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFoodName;
        private final TextView tvFoodCalories;
        private final TextView tvFoodMacros;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodCalories = itemView.findViewById(R.id.tv_food_calories);
            tvFoodMacros = itemView.findViewById(R.id.tv_food_macros);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFoodClick(foodList.get(position));
                }
            });
        }

        public void bindData(Food food) {
            tvFoodName.setText(food.getFoodName());
            tvFoodCalories.setText(food.getCalories() + " ккал");

            // Форматируем макронутриенты в виде текста
            String macrosText = String.format("Б: %.1f г • Ж: %.1f г • У: %.1f г",
                    food.getProteins(), food.getFats(), food.getCarbs());
            tvFoodMacros.setText(macrosText);
        }
    }
}