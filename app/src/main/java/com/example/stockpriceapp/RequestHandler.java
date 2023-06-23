package com.example.stockpriceapp;


import android.util.Log;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestHandler {
    public static final int WEEKLY=7;
    public static final int MONTHLY=30;
    public static final int YEARLY=12;
    public void fetchDailyStockData(String stockSymbol, final DataCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String baseUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=";
        String IntervalKeyAPI = "&interval=60min&apikey=";
        String APIKey = "ZZBXG9G47JQ80SH1";

        Request request = new Request.Builder().url(baseUrl + stockSymbol + IntervalKeyAPI + APIKey).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure
                callback.onDataFetchFailure();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String jsonResponse = response.body().string();
                GraphDataHandler.getDailyStockData(jsonResponse);
                List<StockData> tmp = GraphDataHandler.getStockDataForGraphPlot();
                callback.onDataFetchSuccess(tmp); // Pass the data to the callback
            }
        });
    }
    public void fetchWeeklyOrMonthlyStockData(String stockSymbol,int option,final DataCallback callback){
        OkHttpClient client = new OkHttpClient();
        String baseUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=";
        String APIKey = "&apikey=ZZBXG9G47JQ80SH1";
        Request request = new Request.Builder().url(baseUrl + stockSymbol  + APIKey).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onDataFetchFailure();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String jsonResponse=response.body().string();
                if(option==WEEKLY) {
                    GraphDataHandler.getWeeklyOrMonthlyStockData(jsonResponse, WEEKLY);
                }
                else {
                    GraphDataHandler.getWeeklyOrMonthlyStockData(jsonResponse, MONTHLY);
                }
                List<StockData> tmp=GraphDataHandler.getStockDataForGraphPlot();
                callback.onDataFetchSuccess(tmp);
            }
        });
    }
    public void fetchYearlyStockData(String stockSymbol, final DataCallback callback){
        OkHttpClient client = new OkHttpClient();
        String baseUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=";
        String APIKey = "&apikey=ZZBXG9G47JQ80SH1";
        Request request = new Request.Builder().url(baseUrl + stockSymbol  + APIKey).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String jsonResponse=response.body().string();
                    GraphDataHandler.getYearlyStockData(jsonResponse, YEARLY);
                    List<StockData> tmp=GraphDataHandler.getStockDataForGraphPlot();
                    callback.onDataFetchSuccess(tmp);

            }
        });
    }
}
