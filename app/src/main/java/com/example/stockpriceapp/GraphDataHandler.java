package com.example.stockpriceapp;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GraphDataHandler {
    private static GraphDataHandler instance; // Singleton instance

    private GraphDataHandler() {
        // Private constructor to prevent instantiation
    }

    public static GraphDataHandler getInstance() {
        if (instance == null) {
            synchronized (GraphDataHandler.class) {
                if (instance == null) {
                    instance = new GraphDataHandler();
                }
            }
        }
        return instance;
    }
    private static final int WEEKLY=7;
    private static final int MONTHLY=30;
    private static final int YEARLY=12;
    private static List<StockData> StockDataForGraphPlot=new ArrayList<>();
    public static List<StockData> getStockDataForGraphPlot() {
        return StockDataForGraphPlot;
    }
    public static void getDailyStockData(String response) {
        List<StockData> latestData = new ArrayList<>();

        try {
            JSONObject data = new JSONObject(response);
            JSONObject timeSeries = data.getJSONObject("Time Series (60min)");
            List<JSONObject> latestObjects = new ArrayList<>();

            //Iterate over all of the returned objects that have the latest date
            String latestDate= timeSeries.keys().next().substring(0, 10);


            Iterator<String> keys = timeSeries.keys();
            while(keys.hasNext())
            {
                String key = keys.next();
                String currentStockDate=key.substring(0, 10);
                if(currentStockDate.equals(latestDate)) {
                    String time=key.substring(11,16);
                    String price=timeSeries.getJSONObject(key).getString("1. open");
                    latestData.add(StockFactory.createStockData(time,price));
                }
                else //We have found a response that is already in the past date.
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        StockDataForGraphPlot.clear();
        for(StockData tmp:latestData)
            StockDataForGraphPlot.add(tmp);

    }
    public static void getWeeklyOrMonthlyStockData(String response, int option){
        List<StockData> latestData = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(response);
            JSONObject timeSeries = data.getJSONObject("Time Series (Daily)");
            List<JSONObject> latestObjects = new ArrayList<>();

            //Iterate over all of the returned objects that have the latest date
            Iterator<String> keys = timeSeries.keys();
            if(option==WEEKLY)
            latestData = processDataBySize(keys,timeSeries,WEEKLY);
            else
                latestData = processDataBySize(keys,timeSeries,MONTHLY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        StockDataForGraphPlot.clear();
        for(StockData tmp:latestData)
            StockDataForGraphPlot.add(tmp);
    }
    public static void getYearlyStockData(String response,int option){
        List<StockData> latestData = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(response);
            JSONObject timeSeries = data.getJSONObject("Monthly Time Series");
            List<JSONObject> latestObjects = new ArrayList<>();

            //Iterate over all of the returned objects that have the latest date
            Iterator<String> keys = timeSeries.keys();
            latestData=processDataBySize(keys,timeSeries,YEARLY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        StockDataForGraphPlot.clear();
        for(StockData tmp:latestData)
            StockDataForGraphPlot.add(tmp);
    }
    private static List<StockData> processDataBySize(Iterator<String> keys,JSONObject timeSeries,int length) throws JSONException {
        List<StockData> processedData = new ArrayList<>();
        for(int i=0;i<length;i++)
        {
            String key=keys.next();
            String price=timeSeries.getJSONObject(key).getString("1. open");
            if(length==WEEKLY )
            processedData.add(StockFactory.createStockData(key.substring(8,10),price));
            else if(length==MONTHLY)
            {
                processedData.add(StockFactory.createStockData(String.valueOf(length-i)+"    ",price));
                processedData.get(i).setMonthAndDay(key.substring(5,10));
            }
            else
                  processedData.add(StockFactory.createStockData(String.valueOf(12-i)+"  ",price));
        }
        return processedData;
    }

}