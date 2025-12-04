package com.example.my_weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private List<LocalDayWeatherForecast> dataList;

    public ForecastAdapter(List<LocalDayWeatherForecast> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalDayWeatherForecast day = dataList.get(position);

        holder.tvWeek.setText("周" + day.getWeek());
        holder.tvDate.setText(day.getDate()); // 格式如 2025-12-04
        holder.tvWeatherText.setText(day.getDayWeather());
        holder.tvHigh.setText(day.getDayTemp() + "°");
        holder.tvLow.setText(day.getNightTemp() + "°");

        // 简单的图标逻辑
        if (day.getDayWeather().contains("晴")) holder.tvIcon.setText("☀");
        else if (day.getDayWeather().contains("雨")) holder.tvIcon.setText("🌧");
        else if (day.getDayWeather().contains("云")) holder.tvIcon.setText("⛅");
        else holder.tvIcon.setText("☁");
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWeek, tvDate, tvIcon, tvWeatherText, tvHigh, tvLow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeek = itemView.findViewById(R.id.tv_week);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvIcon = itemView.findViewById(R.id.tv_weather_icon);
            tvWeatherText = itemView.findViewById(R.id.tv_weather_text);
            tvHigh = itemView.findViewById(R.id.tv_high_temp);
            tvLow = itemView.findViewById(R.id.tv_low_temp);
        }
    }
}