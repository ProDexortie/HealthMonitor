package com.example.healthmonitor.models;

public class User {
    private long id;
    private String name;
    private int age;
    private String gender;
    private float height;
    private float weight;
    private float targetWeight;
    private String activityLevel;
    private int dailyCalories;

    // Конструктор по умолчанию
    public User() {
    }

    // Конструктор с параметрами
    public User(long id, String name, int age, String gender, float height,
                float weight, float targetWeight, String activityLevel, int dailyCalories) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.targetWeight = targetWeight;
        this.activityLevel = activityLevel;
        this.dailyCalories = dailyCalories;
    }

    // Геттеры и сеттеры
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public int getDailyCalories() {
        return dailyCalories;
    }

    public void setDailyCalories(int dailyCalories) {
        this.dailyCalories = dailyCalories;
    }

    // Расчет индекса массы тела (ИМТ)
    public float calculateBMI() {
        // ИМТ = вес (кг) / (рост (м) * рост (м))
        float heightInMeters = height / 100f;
        return weight / (heightInMeters * heightInMeters);
    }

    // Интерпретация ИМТ
    public String getBMICategory() {
        float bmi = calculateBMI();

        if (bmi < 18.5f) {
            return "Недостаточный вес";
        } else if (bmi < 25f) {
            return "Нормальный вес";
        } else if (bmi < 30f) {
            return "Избыточный вес";
        } else {
            return "Ожирение";
        }
    }

    // Расчет базового метаболизма (BMR) по формуле Миффлина-Сент Жеора
    public int calculateBMR() {
        if (gender.equalsIgnoreCase("Мужской")) {
            return (int) (10 * weight + 6.25 * height - 5 * age + 5);
        } else {
            return (int) (10 * weight + 6.25 * height - 5 * age - 161);
        }
    }

    // Расчет дневной нормы калорий в зависимости от уровня активности
    public int calculateDailyCalories() {
        int bmr = calculateBMR();
        float activityMultiplier;

        switch (activityLevel) {
            case "Малоподвижный":
                activityMultiplier = 1.2f;
                break;
            case "Легкая активность":
                activityMultiplier = 1.375f;
                break;
            case "Умеренная активность":
                activityMultiplier = 1.55f;
                break;
            case "Активный":
                activityMultiplier = 1.725f;
                break;
            case "Очень активный":
                activityMultiplier = 1.9f;
                break;
            default:
                activityMultiplier = 1.2f;
        }

        return (int) (bmr * activityMultiplier);
    }
}