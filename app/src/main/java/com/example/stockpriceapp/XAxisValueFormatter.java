package com.example.stockpriceapp;

import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class XAxisValueFormatter extends ValueFormatter {
    private final String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };
    private final String[] days = {
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };

    private final int changePeriod;
    private List<StockData> stockList;

    public XAxisValueFormatter(int changePeriod, List<StockData>stockList) {
        this.changePeriod = changePeriod;
        this.stockList=stockList;
    }

    @Override
    public String getFormattedValue(float value) {
        try {
            stockList = GraphDataHandler.getStockDataForGraphPlot();
            if (changePeriod == 0) { // Daily
                int hour = (int) value;
                return String.format(Locale.getDefault(), "%02d:00", hour);
            } else if (changePeriod == 1) { // Weekly
                int dayOfMonth = (int) value;
                String dayOfWeek = getDayOfWeek(dayOfMonth);
                return dayOfWeek;
            } else if (changePeriod == 2) { // Monthly
                try {
                    int index = (int) value;
                    String monthAndDay = stockList.get(stockList.size() - index).getMonthAndDay();
                    int month = Integer.parseInt(monthAndDay.substring(0, 2));
                    String day = monthAndDay.substring(3, 5);
                    return String.format(Locale.getDefault(), "%s %s", months[month - 1], day);
                } catch (Exception e) {
                    return "1";
                }
            } else if (changePeriod == 3) { // Yearly
                int month = (int) value;
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                return months[(month + currentMonth - 1) % 12];
            }
        }
        catch (Exception e)
        {
            return "1";
        }

        return String.valueOf(value); // Default return value
    }

    private String getDayOfWeek(int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return days[dayOfWeek - 1];
    }

    private String getMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        return months[month];
    }

}