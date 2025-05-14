package com.example.healthmonitor.models;

public class Meal {
    private long id;
    private long userId;
    private long foodId;
    private String mealType;
    private String date;
    private float servings;

    // Дополнительные поля для отображения данных
    private String foodName;
    private int calories;
    private float proteins;
    private float fats;
    private float carbs;
    private int portionSize;

    // Конструктор по умолчанию
    public Meal() {
    }

    // Конструктор с основными параметрами
    public Meal(long id, long userId, long foodId, String mealType, String date, float servings) {
        this.id = id;
        this.userId = userId;
        this.foodId = foodId;
        this.mealType = mealType;
        this.date = date;
        this.servings = servings;
    }

    // Конструктор с расширенными параметрами
    public Meal(long id, long userId, long foodId, String mealType, String date, float servings,
                String foodName, int calories, float proteins, float fats, float carbs, int portionSize) {
        this.id = id;
        this.userId = userId;
        this.foodId = foodId;
        this.mealType = mealType;
        this.date = date;
        this.servings = servings;
        this.foodName = foodName;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.portionSize = portionSize;
    }

    // Геттеры и сеттеры
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getFoodId() {
        return foodId;
    }

    public void setFoodId(long foodId) {
        this.foodId = foodId;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getServings() {
        return servings;
    }

    public void setServings(float servings) {
        this.servings = servings;
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

    // Расчет калорий для приема пищи с учетом размера порции
    public int getTotalCalories() {
        return (int) (calories * servings);
    }

    // Расчет белков с учетом размера порции
    public float getTotalProteins() {
        return proteins * servings;
    }

    // Расчет жиров с учетом размера порции
    public float getTotalFats() {
        return fats * servings;
    }

    // Расчет углеводов с учетом размера порции
    public float getTotalCarbs() {
        return carbs * servings;
    }

    // Получение фактического размера порции
    public int getActualPortionSize() {
        return (int) (portionSize * servings);
    }
}