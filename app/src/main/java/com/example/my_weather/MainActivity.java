package com.example.my_weather;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;

import java.util.List;

public class MainActivity extends AppCompatActivity implements WeatherSearch.OnWeatherSearchListener {

    // UI 组件
    private EditText etSearch;
    private TextView tvCity, tvStatus, tvCurrentTemp, tvRange;
    private TextView tvDayWeather, tvDayWind, tvNightWeather, tvNightWind;

    // 定义城市快捷按钮，以便后续修改颜色
    private Button btnBj, btnSh, btnGz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 隐私合规 (只需调用一次)
        ServiceSettings.updatePrivacyShow(this, true, true);
        ServiceSettings.updatePrivacyAgree(this, true);

        // 2. 初始化控件
        initViews();

        // 3. 默认查广州，并设置按钮状态
        searchAllWeather("广州市");
        updateCityButtons(btnGz);
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search_city);
        tvCity = findViewById(R.id.tv_city_name);
        tvStatus = findViewById(R.id.tv_weather_status);
        tvCurrentTemp = findViewById(R.id.tv_current_temp);
        tvRange = findViewById(R.id.tv_temp_range);

        tvDayWeather = findViewById(R.id.tv_day_weather);
        tvDayWind = findViewById(R.id.tv_day_wind);
        tvNightWeather = findViewById(R.id.tv_night_weather);
        tvNightWind = findViewById(R.id.tv_night_wind);

        // 初始化城市按钮
        btnBj = findViewById(R.id.btn_bj);
        btnSh = findViewById(R.id.btn_sh);
        btnGz = findViewById(R.id.btn_gz);

        Button btnForecast = findViewById(R.id.btn_to_forecast);
        btnForecast.setOnClickListener(v -> {
            // 获取当前显示的城市名
            String currentCity = tvCity.getText().toString();

            // 跳转
            Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
            intent.putExtra("city_name", currentCity); // 传值
            startActivity(intent);
        });

        // 搜索按钮逻辑
        findViewById(R.id.btn_search).setOnClickListener(v -> {
            String city = etSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(city)) {
                searchAllWeather(city);
                // 手动搜索时，取消下方快捷按钮的选中状态
                updateCityButtons(null);
            }
        });

        // 软键盘回车搜索
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String city = etSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(city)) {
                    searchAllWeather(city);
                    updateCityButtons(null);
                }
                return true;
            }
            return false;
        });

        // 快捷按钮逻辑：点击搜索对应城市，并更新按钮颜色
        btnBj.setOnClickListener(v -> {
            searchAllWeather("北京市");
            updateCityButtons(btnBj);
        });

        btnSh.setOnClickListener(v -> {
            searchAllWeather("上海市");
            updateCityButtons(btnSh);
        });

        btnGz.setOnClickListener(v -> {
            searchAllWeather("广州市");
            updateCityButtons(btnGz);
        });
    }

    /**
     * 更新城市按钮的选中状态颜色
     * @param selectedBtn 当前被选中的按钮，若为 null 则全部置为未选中状态
     */
    private void updateCityButtons(Button selectedBtn) {
        // 定义颜色：选中(蓝色 #007AFF)，未选中(半透明白 #33FFFFFF)
        int selectedColor = Color.parseColor("#007AFF");
        int unselectedColor = Color.parseColor("#33FFFFFF");

        // 1. 重置所有按钮为未选中颜色
        btnBj.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
        btnSh.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
        btnGz.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));

        // 2. 如果有选中的按钮，将其设置为高亮颜色
        if (selectedBtn != null) {
            selectedBtn.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
        }
    }

    /**
     * 组合查询：既查实况，也查预报
     */
    private void searchAllWeather(String city) {
        // 1. 查实况 (Live)
        WeatherSearchQuery liveQuery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        try {
            WeatherSearch liveSearch = new WeatherSearch(this);
            liveSearch.setOnWeatherSearchListener(this);
            liveSearch.setQuery(liveQuery);
            liveSearch.searchWeatherAsyn();
        } catch (AMapException e) {
            e.printStackTrace();
        }

        // 2. 查预报 (Forecast) - 为了获取白天/夜间详情和最高最低温
        WeatherSearchQuery forecastQuery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_FORECAST);
        try {
            WeatherSearch forecastSearch = new WeatherSearch(this);
            forecastSearch.setOnWeatherSearchListener(this);
            forecastSearch.setQuery(forecastQuery);
            forecastSearch.searchWeatherAsyn();
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult result, int rCode) {
        if (rCode == 1000 && result != null && result.getLiveResult() != null) {
            LocalWeatherLive live = result.getLiveResult();

            // 更新 UI：实况部分
            tvCity.setText(live.getCity());
            tvStatus.setText(live.getWeather());
            tvCurrentTemp.setText(live.getTemperature() + "°");
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult result, int rCode) {
        if (rCode == 1000 && result != null && result.getForecastResult() != null) {
            List<LocalDayWeatherForecast> list = result.getForecastResult().getWeatherForecast();
            if (list != null && list.size() > 0) {
                // 获取今天（列表第0个）的预报
                LocalDayWeatherForecast today = list.get(0);

                // 更新 UI：最高/最低温
                tvRange.setText("最高: " + today.getDayTemp() + "°  最低: " + today.getNightTemp() + "°");

                // 更新 UI：白天卡片
                tvDayWeather.setText("天气: " + today.getDayWeather());
                tvDayWind.setText("风向: " + today.getDayWindDirection() + "风 " + today.getDayWindPower() + "级");

                // 更新 UI：夜间卡片
                tvNightWeather.setText("天气: " + today.getNightWeather());
                tvNightWind.setText("风向: " + today.getNightWindDirection() + "风 " + today.getNightWindPower() + "级");
            }
        }
    }
}