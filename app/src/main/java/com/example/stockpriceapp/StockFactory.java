package com.example.stockpriceapp;

public class StockFactory {
    public static Stock createStock(String stockSymbol, double currentPrice, String dailyChange) {
        return new Stock(stockSymbol, currentPrice, dailyChange);
    }

    public static StockData createStockData(String dateAndTime, String openValue)
    {
        return new StockData(dateAndTime,openValue);
    }
}