package com.example.healthmonitor.models;

public class Food {
    private long id;
    private String foodName;
    private int calories;
    private float proteins;
    private float fats;
    private float carbs;
    private int portionSize;
    private boolean isCustom;

    // Конструктор по умолчанию
    public Food() {
    }

    // Конструктор с параметрами
    public Food(long id, String foodName, int calories, float proteins,
                float fats, float carbs, int portionSize, boolean isCustom) {
        this.id = id;
        this.foodName = foodName;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.portionSize = portionSize;
        this.isCustom = isCustom;
    }

    // Геттеры и сеттеры
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getProteins() {
        return proteins;
    }

    public void setProteins(float proteins) {
        this.proteins = proteins;
    }

    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public int getPortionSize() {
        return portionSize;
    }

    public void setPortionSize(int portionSize) {
        this.portionSize = portionSize;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    // Расчет питательных веществ для указанного размера порции
    public int calculateCaloriesForPortion(int newPortionSize) {
        return (int) (calories * ((float) newPortionSize / portionSize));
    }

    public float calculateProteinsForPortion(int newPortionSize) {
        return proteins * ((float) newPortionSize / portionSize);
    }

    public float calculateFatsForPortion(int newPortionSize) {
        return fats * ((float) newPortionSize / portionSize);
    }

    public float calculateCarbsForPortion(int newPortionSize) {
        return carbs * ((float) newPortionSize / portionSize);
    }
}