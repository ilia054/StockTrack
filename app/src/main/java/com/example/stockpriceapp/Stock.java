package com.example.stockpriceapp;

public class Stock {
    String stockSymbol;
    double currentPrice;
    String dailyChange;

    public Stock(String stockSymbol, double currentPrice, String dailyChange) {
        this.stockSymbol = stockSymbol;
        this.currentPrice = currentPrice;
        this.dailyChange = dailyChange;
    }
    public String getStockSymbol() {
        return stockSymbol;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getDailyChange() {
        return dailyChange;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "stockSymbol='" + stockSymbol + '\'' +
                ", currentPrice=" + currentPrice +
                ", dailyChange='" + dailyChange + '\'' +
                '}';
    }
}
