package com.example.stockpriceapp;
import android.content.Context;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;


public class dataHandler {
    private static StockAdapter myStockAdapter;
    private static ArrayList<Stock> myStocks;
    public static void setStockAdapter (StockAdapter stockAdapter)
    {
        myStockAdapter=stockAdapter;
    }

    public static StockAdapter getStockAdapter ()
    {
        return myStockAdapter;
    }
    public static ArrayList<Stock> getStockInstance (Context context)
    {
        if(myStocks==null)
            myStocks=readStocksFromJson(context);
        return myStocks;
    }
    public static void addStockToJson(Context context, Stock stock) {
        try {
            // Read the existing JSON file into a JSONObject
            JSONObject json;
            String jsonContent = readJsonFile(context);
            if (jsonContent != null) {
                json = new JSONObject(jsonContent);
            } else {
                json = new JSONObject();
            }

            // Get the "stocks" array from the JSONObject
            JSONArray stocksArray;
            if (json.has("Stocks")) {
                stocksArray = json.getJSONArray("Stocks");
            } else {
                stocksArray = new JSONArray();
                json.put("Stocks", stocksArray);
            }

            // Create a JSONObject for the new stock

            JSONObject stockJson = new JSONObject();
            stockJson.put("stockSymbol", stock.getStockSymbol());
            stockJson.put("currentPrice", stock.getCurrentPrice());
            stockJson.put("dailyChange", stock.getDailyChange());

            // Add the new stock JSONObject to the "stocks" array
            stocksArray.put(stockJson);

            // Write the updated JSONObject back to the file
            writeJsonObjectToFile(context, json);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "Stock added to file successfully", Toast.LENGTH_SHORT).show();
        myStocks.add(stock);
        myStockAdapter.notifyDataSetChanged();
    }
    public static void removeStockFromJson(Context context, Stock stock) {
        try {
            // Read the existing JSON file into a JSONObject
            JSONObject json = new JSONObject(readJsonFile(context));

            // Check if the "Stocks" array exists in the JSON
            if (json.has("Stocks")) {
                // Get the "Stocks" array from the JSON
                JSONArray stocksArray = json.getJSONArray("Stocks");

                // Iterate over each stock object in the array
                for (int i = 0; i < stocksArray.length(); i++) {
                    // Get the stock JSON object at the current index
                    JSONObject stockJson = stocksArray.getJSONObject(i);

                    // Extract the stock details from the JSON object
                    String stockSymbol = stockJson.getString("stockSymbol");

                    // Check if the stock symbol matches the one to be removed
                    if (stockSymbol.equalsIgnoreCase(stock.getStockSymbol())) {
                        // Remove the stock object from the array
                        stocksArray.remove(i);
                        break;
                    }
                }

                // Update the JSON object with the modified "Stocks" array
                json.put("Stocks", stocksArray);

                // Write the updated JSON object back to the file
                writeJsonObjectToFile(context, json);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        myStocks.remove(stock);
        myStockAdapter.notifyDataSetChanged();
    }
    public static void writeJsonObjectToFile(Context context,JSONObject jsonObject) throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput("savedStocks.json", Context.MODE_PRIVATE);
            outputStream.write(jsonObject.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static String readJsonFile(Context context) {
        try {
            FileInputStream inputStream = context.openFileInput("savedStocks.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            inputStream.close();
            return jsonString.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static ArrayList<Stock> readStocksFromJson(Context context) {
        ArrayList<Stock> stocks = new ArrayList<>();

        try {
            // Read the JSON file into a JSONObject
            JSONObject json = new JSONObject(readJsonFile(context));

            // Check if the "stocks" array exists in the JSON
            if (json.has("Stocks")) {
                // Get the "stocks" array from the JSON
                JSONArray stocksArray = json.getJSONArray("Stocks");

                // Iterate over each stock object in the array
                for (int i = 0; i < stocksArray.length(); i++) {
                    // Get the stock JSON object at the current index
                    JSONObject stockJson = stocksArray.getJSONObject(i);

                    // Extract the stock details from the JSON object
                    String stockSymbol = stockJson.getString("stockSymbol");
                    double currentPrice = stockJson.getDouble("currentPrice");
                    String dailyChange = stockJson.getString("dailyChange");

                    // Create a Stock object and add it to the ArrayList
                    Stock stock = StockFactory.createStock(stockSymbol, currentPrice, dailyChange);
                    stocks.add(stock);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stocks;
    }
    public static boolean doesContainSymbol(String symbol)
    {
        for(Stock stock:myStocks)
            if(stock.getStockSymbol().equals(symbol.toUpperCase()))
                return true;
        return false;
    }
}