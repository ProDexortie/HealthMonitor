package com.example.healthmonitor.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthmonitor.R;
import com.example.healthmonitor.database.DatabaseHelper;
import com.example.healthmonitor.database.MealDAO;
import com.example.healthmonitor.database.UserDAO;
import com.example.healthmonitor.models.User;
import com.example.healthmonitor.utils.DateUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private StatisticsPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        initUI(view);
        setupViewPager();

        return view;
    }

    private void initUI(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
    }

    private void setupViewPager() {
        adapter = new StatisticsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Связываем TabLayout с ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.daily_summary);
                    break;
                case 1:
                    tab.setText(R.string.weekly_summary);
                    break;
                case 2:
                    tab.setText(R.string.monthly_summary);
                    break;
            }
        }).attach();
    }

    // Внутренний класс для адаптера ViewPager
    public static class StatisticsPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

        public StatisticsPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Создаем соответствующий фрагмент в зависимости от позиции
            StatisticsPageFragment fragment = new StatisticsPageFragment();
            Bundle args = new Bundle();
            args.putInt("period", position); // 0 - день, 1 - неделя, 2 - месяц
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3; // Дневная, недельная и месячная статистика
        }
    }

    // Внутренний класс для страницы статистики
    public static class StatisticsPageFragment extends Fragment {

        private int period; // 0 - день, 1 - неделя, 2 - месяц
        private MealDAO mealDAO;
        private UserDAO userDAO;
        private User currentUser;

        private BarChart chartCalories;
        private PieChart chartMacros;
        private LineChart chartWeight;
        private PieChart chartMealComposition;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                period = getArguments().getInt("period", 0);
            }

            DatabaseHelper dbHelper = DatabaseHelper.getInstance(requireContext());
            mealDAO = new MealDAO(dbHelper);
            userDAO = new UserDAO(dbHelper);
            currentUser = userDAO.getFirstUser();
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_statistics_daily, container, false);

            // Инициализация графиков
            chartCalories = view.findViewById(R.id.chart_calories);
            chartMacros = view.findViewById(R.id.chart_macros);
            chartWeight = view.findViewById(R.id.chart_weight);
            chartMealComposition = view.findViewById(R.id.chart_meal_composition);

            // Настройка и заполнение графиков в зависимости от периода
            setupCharts();
            loadData();

            return view;
        }

        private void setupCharts() {
            // Настройка графика калорий
            chartCalories.getDescription().setEnabled(false);
            chartCalories.setDrawGridBackground(false);
            chartCalories.setDrawBarShadow(false);
            chartCalories.setHighlightFullBarEnabled(false);

            XAxis xAxis = chartCalories.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            chartCalories.getAxisLeft().setAxisMinimum(0f);
            chartCalories.getAxisRight().setEnabled(false);

            // Настройка графика макронутриентов
            chartMacros.getDescription().setEnabled(false);
            chartMacros.setUsePercentValues(true);
            chartMacros.setDrawHoleEnabled(true);
            chartMacros.setHoleColor(Color.WHITE);
            chartMacros.setHoleRadius(35f);
            chartMacros.setTransparentCircleRadius(40f);
            chartMacros.setRotationEnabled(true);
            chartMacros.setHighlightPerTapEnabled(true);
            chartMacros.setEntryLabelColor(Color.BLACK);
            chartMacros.setEntryLabelTextSize(12f);

            // Настройка графика веса
            chartWeight.getDescription().setEnabled(false);
            chartWeight.setDrawGridBackground(false);

            XAxis weightXAxis = chartWeight.getXAxis();
            weightXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            weightXAxis.setDrawGridLines(false);

            YAxis leftAxis = chartWeight.getAxisLeft();
            leftAxis.setDrawZeroLine(true);

            chartWeight.getAxisRight().setEnabled(false);

            // Настройка графика состава приемов пищи
            chartMealComposition.getDescription().setEnabled(false);
            chartMealComposition.setUsePercentValues(true);
            chartMealComposition.setDrawHoleEnabled(true);
            chartMealComposition.setHoleColor(Color.WHITE);
            chartMealComposition.setHoleRadius(35f);
            chartMealComposition.setTransparentCircleRadius(40f);
        }

        private void loadData() {
            if (currentUser == null) {
                return;
            }

            switch (period) {
                case 0:
                    loadDailyData();
                    break;
                case 1:
                    loadWeeklyData();
                    break;
                case 2:
                    loadMonthlyData();
                    break;
            }
        }

        private void loadDailyData() {
            // 1. График потребления калорий по часам дня
            loadDailyCaloriesChart();

            // 2. График макронутриентов за день
            loadDailyMacrosChart();

            // 3. Данные веса
            loadWeightChart();

            // 4. Состав приемов пищи
            loadMealCompositionChart();
        }

        private void loadWeeklyData() {
            // 1. График потребления калорий по дням недели
            loadWeeklyCaloriesChart();

            // 2. График макронутриентов за неделю
            loadWeeklyMacrosChart();

            // 3. Данные веса за неделю
            loadWeightChart();

            // 4. Состав приемов пищи за неделю
            loadWeeklyMealCompositionChart();
        }

        private void loadMonthlyData() {
            // 1. График потребления калорий по неделям месяца
            loadMonthlyCaloriesChart();

            // 2. График макронутриентов за месяц
            loadMonthlyMacrosChart();

            // 3. Данные веса за месяц
            loadWeightChart();

            // 4. Состав приемов пищи за месяц
            loadMonthlyMealCompositionChart();
        }

        // Загрузка данных о калориях за день
        private void loadDailyCaloriesChart() {
            ArrayList<BarEntry> entries = new ArrayList<>();
            String[] timeLabels = {"Утро", "День", "Вечер", "Ночь"};

            // Демо-данные о потреблении калорий в разное время дня
            entries.add(new BarEntry(0, 450)); // Утро
            entries.add(new BarEntry(1, 680)); // День
            entries.add(new BarEntry(2, 520)); // Вечер
            entries.add(new BarEntry(3, 150)); // Ночь

            BarDataSet dataSet = new BarDataSet(entries, "Калории");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

            BarData data = new BarData(dataSet);
            data.setBarWidth(0.7f);

            chartCalories.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeLabels));
            chartCalories.setData(data);
            chartCalories.animateY(1000);
            chartCalories.invalidate();
        }

        // Загрузка данных о калориях за неделю
        private void loadWeeklyCaloriesChart() {
            ArrayList<BarEntry> entries = new ArrayList<>();
            String[] dayLabels = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

            // Демо-данные о потреблении калорий по дням недели
            entries.add(new BarEntry(0, 1850)); // Пн
            entries.add(new BarEntry(1, 1720)); // Вт
            entries.add(new BarEntry(2, 1950)); // Ср
            entries.add(new BarEntry(3, 1630)); // Чт
            entries.add(new BarEntry(4, 2100)); // Пт
            entries.add(new BarEntry(5, 2350)); // Сб
            entries.add(new BarEntry(6, 1980)); // Вс

            BarDataSet dataSet = new BarDataSet(entries, "Калории");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

            BarData data = new BarData(dataSet);
            data.setBarWidth(0.7f);

            chartCalories.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dayLabels));
            chartCalories.setData(data);
            chartCalories.animateY(1000);
            chartCalories.invalidate();
        }

        // Загрузка данных о калориях за месяц
        private void loadMonthlyCaloriesChart() {
            ArrayList<BarEntry> entries = new ArrayList<>();
            String[] weekLabels = {"Неделя 1", "Неделя 2", "Неделя 3", "Неделя 4"};

            // Демо-данные о потреблении калорий по неделям месяца
            entries.add(new BarEntry(0, 13850)); // Неделя 1
            entries.add(new BarEntry(1, 14320)); // Неделя 2
            entries.add(new BarEntry(2, 12950)); // Неделя 3
            entries.add(new BarEntry(3, 13630)); // Неделя 4

            BarDataSet dataSet = new BarDataSet(entries, "Калории");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

            BarData data = new BarData(dataSet);
            data.setBarWidth(0.7f);

            chartCalories.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekLabels));
            chartCalories.setData(data);
            chartCalories.animateY(1000);
            chartCalories.invalidate();
        }

        // Загрузка данных о макронутриентах за день
        private void loadDailyMacrosChart() {
            ArrayList<PieEntry> entries = new ArrayList<>();

            // Демо-данные о макронутриентах (белки, жиры, углеводы)
            entries.add(new PieEntry(60, "Белки"));
            entries.add(new PieEntry(45, "Жиры"));
            entries.add(new PieEntry(180, "Углеводы"));

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(chartMacros));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            chartMacros.setData(data);
            chartMacros.animateY(1000);
            chartMacros.invalidate();
        }

        // Загрузка данных о макронутриентах за неделю
        private void loadWeeklyMacrosChart() {
            ArrayList<PieEntry> entries = new ArrayList<>();

            // Демо-данные о макронутриентах за неделю (средние значения)
            entries.add(new PieEntry(65, "Белки"));
            entries.add(new PieEntry(50, "Жиры"));
            entries.add(new PieEntry(195, "Углеводы"));

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(chartMacros));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            chartMacros.setData(data);
            chartMacros.animateY(1000);
            chartMacros.invalidate();
        }

        // Загрузка данных о макронутриентах за месяц
        private void loadMonthlyMacrosChart() {
            ArrayList<PieEntry> entries = new ArrayList<>();

            // Демо-данные о макронутриентах за месяц (средние значения)
            entries.add(new PieEntry(63, "Белки"));
            entries.add(new PieEntry(52, "Жиры"));
            entries.add(new PieEntry(190, "Углеводы"));

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(chartMacros));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            chartMacros.setData(data);
            chartMacros.animateY(1000);
            chartMacros.invalidate();
        }

        // Загрузка данных о весе
        private void loadWeightChart() {
            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> dateLabels = new ArrayList<>();

            // Демо-данные о весе за последние дни/недели/месяцы в зависимости от периода
            if (period == 0) { // День
                // За последние 7 дней
                for (int i = 6; i >= 0; i--) {
                    String date = DateUtils.getDateWithOffset(-i);
                    // Генерируем случайный вес в диапазоне ±1 кг от текущего веса
                    float weight = (float) (currentUser.getWeight() + (Math.random() * 2 - 1) * 0.5);
                    entries.add(new Entry(6 - i, weight));
                    dateLabels.add(DateUtils.formatDateWithDay(date));
                }
            } else if (period == 1) { // Неделя
                // За последние 4 недели
                for (int i = 0; i < 4; i++) {
                    // Генерируем случайный вес в диапазоне ±2 кг от текущего веса
                    float weight = (float) (currentUser.getWeight() + (Math.random() * 4 - 2) * 0.5);
                    entries.add(new Entry(i, weight));
                    dateLabels.add("Неделя " + (i + 1));
                }
            } else { // Месяц
                // За последние 6 месяцев
                for (int i = 0; i < 6; i++) {
                    // Генерируем случайный вес с трендом к целевому весу
                    float progress = i / 5f; // От 0 до 1
                    float weight = currentUser.getWeight() * (1 - progress) + currentUser.getTargetWeight() * progress;
                    // Добавляем небольшое случайное отклонение
                    weight += (Math.random() * 2 - 1) * 0.3;
                    entries.add(new Entry(i, weight));
                    dateLabels.add("Месяц " + (i + 1));
                }
            }

            LineDataSet dataSet = new LineDataSet(entries, "Вес (кг)");
            dataSet.setColor(Color.rgb(76, 175, 80)); // Зеленый
            dataSet.setLineWidth(2f);
            dataSet.setCircleColor(Color.rgb(76, 175, 80));
            dataSet.setCircleRadius(3f);
            dataSet.setDrawCircleHole(false);
            dataSet.setValueTextSize(9f);
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(Color.rgb(76, 175, 80));
            dataSet.setFillAlpha(65);
            dataSet.setDrawValues(true);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            LineData data = new LineData(dataSet);

            // Настройка оси X для отображения дат
            XAxis xAxis = chartWeight.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
            xAxis.setLabelRotationAngle(45f);

            chartWeight.setData(data);
            chartWeight.animateX(1000);
            chartWeight.invalidate();
        }

        // Загрузка данных о составе приемов пищи
        private void loadMealCompositionChart() {
            ArrayList<PieEntry> entries = new ArrayList<>();

            // Демо-данные о калориях по типам приемов пищи
            entries.add(new PieEntry(450, "Завтрак"));
            entries.add(new PieEntry(680, "Обед"));
            entries.add(new PieEntry(520, "Ужин"));
            entries.add(new PieEntry(150, "Перекус"));

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.PASTEL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(chartMealComposition));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            chartMealComposition.setData(data);
            chartMealComposition.animateY(1000);
            chartMealComposition.invalidate();
        }

        // Загрузка данных о составе приемов пищи за неделю
        private void loadWeeklyMealCompositionChart() {
            ArrayList<PieEntry> entries = new ArrayList<>();

            // Демо-данные о калориях по типам приемов пищи за неделю
            entries.add(new PieEntry(3150, "Завтрак"));
            entries.add(new PieEntry(4760, "Обед"));
            entries.add(new PieEntry(3640, "Ужин"));
            entries.add(new PieEntry(1050, "Перекус"));

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.PASTEL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(chartMealComposition));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            chartMealComposition.setData(data);
            chartMealComposition.animateY(1000);
            chartMealComposition.invalidate();
        }

        // Загрузка данных о составе приемов пищи за месяц
        private void loadMonthlyMealCompositionChart() {
            ArrayList<PieEntry> entries = new ArrayList<>();

            // Демо-данные о калориях по типам приемов пищи за месяц
            entries.add(new PieEntry(13500, "Завтрак"));
            entries.add(new PieEntry(20400, "Обед"));
            entries.add(new PieEntry(15600, "Ужин"));
            entries.add(new PieEntry(4500, "Перекус"));

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.PASTEL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(chartMealComposition));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            chartMealComposition.setData(data);
            chartMealComposition.animateY(1000);
            chartMealComposition.invalidate();
        }
    }
}