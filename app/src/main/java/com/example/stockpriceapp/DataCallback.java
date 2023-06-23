package com.example.stockpriceapp;

import java.util.List;

public interface DataCallback {
    void onDataFetchSuccess(List<StockData> stockDataList);
    void onDataFetchFailure();
}