package com.example.my_weather;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import java.util.List;

public class ForecastActivity extends AppCompatActivity implements WeatherSearch.OnWeatherSearchListener {

    private RecyclerView recyclerView;
    private TextView tvCitySub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvCitySub = findViewById(R.id.tv_city_sub);

        // 获取上一个页面传来的城市名
        String city = getIntent().getStringExtra("city_name");
        if (city == null) city = "北京市"; // 默认值

        tvCitySub.setText(city);
        searchForecast(city);
    }

    private void searchForecast(String city) {
        WeatherSearchQuery query = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_FORECAST);
        try {
            WeatherSearch search = new WeatherSearch(this);
            search.setOnWeatherSearchListener(this);
            search.setQuery(query);
            search.searchWeatherAsyn();
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult result, int rCode) {
        if (rCode == 1000 && result != null && result.getForecastResult() != null) {
            List<LocalDayWeatherForecast> list = result.getForecastResult().getWeatherForecast();

            // 设置适配器
            ForecastAdapter adapter = new ForecastAdapter(list);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        // 不需要实现
    }
}