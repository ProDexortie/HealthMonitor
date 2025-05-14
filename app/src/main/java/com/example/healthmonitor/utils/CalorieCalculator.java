package com.example.healthmonitor.utils;

import java.text.DecimalFormat;

import com.example.healthmonitor.models.User;

public class CalorieCalculator {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    // Расчет ИМТ (индекс массы тела)
    public static float calculateBMI(float weight, float height) {
        float heightInMeters = height / 100f;
        return weight / (heightInMeters * heightInMeters);
    }

    // Получение категории ИМТ
    public static String getBMICategory(float bmi) {
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

    // Расчет базового обмена веществ (BMR) по формуле Миффлина-Сент Жеора
    public static int calculateBMR(User user) {
        if (user.getGender().equalsIgnoreCase("Мужской")) {
            return (int) (10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5);
        } else {
            return (int) (10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161);
        }
    }

    // Расчет общего расхода энергии (TDEE) с учетом уровня активности
    public static int calculateTDEE(User user) {
        int bmr = calculateBMR(user);
        float activityMultiplier = getActivityMultiplier(user.getActivityLevel());
        return (int) (bmr * activityMultiplier);
    }

    // Получение множителя активности
    public static float getActivityMultiplier(String activityLevel) {
        switch (activityLevel) {
            case "Малоподвижный":
                return 1.2f;
            case "Легкая активность":
                return 1.375f;
            case "Умеренная активность":
                return 1.55f;
            case "Активный":
                return 1.725f;
            case "Очень активный":
                return 1.9f;
            default:
                return 1.2f;
        }
    }

    // Расчет дневной нормы калорий для снижения веса (дефицит 500 ккал)
    public static int calculateWeightLossCalories(User user) {
        int tdee = calculateTDEE(user);
        return Math.max(1200, tdee - 500); // Не менее 1200 ккал
    }

    // Расчет дневной нормы калорий для набора веса (профицит 500 ккал)
    public static int calculateWeightGainCalories(User user) {
        int tdee = calculateTDEE(user);
        return tdee + 500;
    }

    // Расчет оптимального распределения макронутриентов (белки, жиры, углеводы)
    public static MacronutrientSplit calculateMacronutrientSplit(int calories, String goal) {
        MacronutrientSplit macros = new MacronutrientSplit();

        switch (goal) {
            case "Снижение веса":
                // Высокий белок, умеренные жиры, низкие углеводы
                macros.proteinPercentage = 40;
                macros.fatPercentage = 30;
                macros.carbPercentage = 30;
                break;
            case "Поддержание веса":
                // Умеренный белок, умеренные жиры, умеренные углеводы
                macros.proteinPercentage = 30;
                macros.fatPercentage = 30;
                macros.carbPercentage = 40;
                break;
            case "Набор массы":
                // Умеренный белок, умеренные жиры, высокие углеводы
                macros.proteinPercentage = 25;
                macros.fatPercentage = 25;
                macros.carbPercentage = 50;
                break;
            default:
                // По умолчанию - сбалансированное питание
                macros.proteinPercentage = 30;
                macros.fatPercentage = 30;
                macros.carbPercentage = 40;
        }

        // Расчет граммов белков, жиров и углеводов
        macros.proteinGrams = (int) ((calories * (macros.proteinPercentage / 100.0)) / 4);
        macros.fatGrams = (int) ((calories * (macros.fatPercentage / 100.0)) / 9);
        macros.carbGrams = (int) ((calories * (macros.carbPercentage / 100.0)) / 4);

        return macros;
    }

    // Класс для хранения расчетов макронутриентов
    public static class MacronutrientSplit {
        public int proteinPercentage;
        public int fatPercentage;
        public int carbPercentage;
        public int proteinGrams;
        public int fatGrams;
        public int carbGrams;

        // Форматированный вывод белков в граммах и процентах
        public String getFormattedProtein() {
            return proteinGrams + "г (" + proteinPercentage + "%)";
        }

        // Форматированный вывод жиров в граммах и процентах
        public String getFormattedFat() {
            return fatGrams + "г (" + fatPercentage + "%)";
        }

        // Форматированный вывод углеводов в граммах и процентах
        public String getFormattedCarbs() {
            return carbGrams + "г (" + carbPercentage + "%)";
        }
    }

    // Форматирование чисел с плавающей точкой
    public static String formatDecimal(float value) {
        return DECIMAL_FORMAT.format(value);
    }
}