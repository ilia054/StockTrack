package com.example.stockpriceapp;

public class StockData {
    private String dateAndTime;
    private String openValue;
    private String monthAndDay;


    public StockData(String dateAndTime, String openValue) {
        this.dateAndTime = dateAndTime;
        this.openValue = openValue;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public String getOpenValue() {
        return openValue;
    }
    @Override
    public String toString()
    {
        return dateAndTime+" "+openValue;
    }

    public void setMonthAndDay(String monthAndDay)
    {
        this.monthAndDay=monthAndDay;
    }

    public String getMonthAndDay()
    {
        return monthAndDay;
    }

}
