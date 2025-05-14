package com.example.healthmonitor.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmonitor.R;
import com.example.healthmonitor.adapters.SearchFoodAdapter;
import com.example.healthmonitor.database.DataManager;
import com.example.healthmonitor.models.Food;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchFoodFragment extends Fragment implements SearchFoodAdapter.OnFoodClickListener {

    private TextInputEditText etSearch;
    private RecyclerView recyclerSearchResults;
    private TextView tvNoResults;
    private ProgressBar progressBar;

    private DataManager dataManager;
    private SearchFoodAdapter adapter;
    private OnFoodSelectedListener mCallback;

    // Интерфейс для обратного вызова в активность
    public interface OnFoodSelectedListener {
        void onFoodSelected(Food food);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnFoodSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnFoodSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = DataManager.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_food, container, false);

        initUI(view);
        setupSearchListener();
        setupRecyclerView();

        return view;
    }

    private void initUI(View view) {
        etSearch = view.findViewById(R.id.et_search);
        recyclerSearchResults = view.findViewById(R.id.recycler_search_results);
        tvNoResults = view.findViewById(R.id.tv_no_results);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2) {
                    searchFoods(s.toString());
                } else if (s.length() == 0) {
                    showAllFoods();
                }
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchFoods(query);
                }
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SearchFoodAdapter(requireContext(), new ArrayList<>(), this);
        recyclerSearchResults.setAdapter(adapter);

        // Загружаем все продукты при первом открытии
        showAllFoods();
    }

    private void showAllFoods() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerSearchResults.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);

        // Получаем все продукты из базы данных через DataManager
        List<Food> allFoods = dataManager.getAllFoods();

        progressBar.setVisibility(View.GONE);

        if (allFoods.isEmpty()) {
            tvNoResults.setText("Нет доступных продуктов");
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            recyclerSearchResults.setVisibility(View.VISIBLE);
            adapter.updateData(allFoods);
        }
    }

    private void searchFoods(String query) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerSearchResults.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);

        // Поиск продуктов по запросу через DataManager
        List<Food> searchResults = dataManager.searchFoodsByName(query);

        progressBar.setVisibility(View.GONE);

        if (searchResults.isEmpty()) {
            tvNoResults.setText("Ничего не найдено");
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            recyclerSearchResults.setVisibility(View.VISIBLE);
            adapter.updateData(searchResults);
        }
    }

    @Override
    public void onFoodClick(Food food) {
        if (mCallback != null) {
            mCallback.onFoodSelected(food);
        }
    }
}