package com.example.stockpriceapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Stock> myStocks;
    private RecyclerView stockRecyclerView;
    private StockAdapter stockAdapter;
    private OkHttpClient client;
    private EditText stockSymbolEditText;
    private Button goButton;

    private Handler refreshHandler;
    private Runnable refreshRunnable;
    private static final long REFRESH_INTERVAL = 35000; // Refresh interval in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockRecyclerView = findViewById(R.id.stockRecyclerView);
        stockAdapter = new StockAdapter(MainActivity.this);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockRecyclerView.setAdapter(stockAdapter);

        stockSymbolEditText = findViewById(R.id.stockSymbolEditText);
        goButton = findViewById(R.id.goButton);

        myStocks = dataHandler.getStockInstance(MainActivity.this);

        client = new OkHttpClient();
        dataHandler.setStockAdapter(stockAdapter);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stockSymbol = stockSymbolEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(stockSymbol)) {
                    get(stockSymbol);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a stock symbol", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                stockAdapter.deleteItem(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(stockRecyclerView);

        stockAdapter.notifyDataSetChanged();

        // Start the refresh task
        startRefreshTask();
    }

    private void startRefreshTask() {
        refreshHandler = new Handler(Looper.getMainLooper());
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                // Refresh stock data
                refreshStockData();
                // Schedule the next refresh after the specified interval
                refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
            }
        };
        // Schedule the first refresh after the specified interval
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    private void refreshStockData() {
        if(myStocks.size()==0)
            return;
        try {
            int length = myStocks.size() > 2 ? 2 : myStocks.size();
            for (int i = 0; i < length; i++) {
                String stockSymbol = myStocks.get(i).getStockSymbol();
                stockAdapter.deleteItem(i);
                get(stockSymbol);
            }
        }
        catch (Exception e)
        {
            Log.d("ERROR",e.getMessage());
        }
    }

    public void get(String stockSymbol) {
        if (dataHandler.doesContainSymbol(stockSymbol)) {
            Toast.makeText(MainActivity.this, "This stock already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        String baseUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=";
        String keyAPI = "&apikey=ZZBXG9G47JQ80SH1";

        Request request = new Request.Builder().url("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + stockSymbol + keyAPI).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(MainActivity.this, "Stock search has failed!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String jsonResponse = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!jsonResponse.contains("01. symbol")) {
                                Toast.makeText(MainActivity.this, "There is no such stock+\n Or Too many API Requests!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONObject json = new JSONObject(jsonResponse);
                            JSONObject globalQuote = json.getJSONObject("Global Quote");
                            String symbol = globalQuote.getString("01. symbol");
                            double price = globalQuote.getDouble("05. price");
                            String changePercent = globalQuote.getString("10. change percent");
                            Stock stock = StockFactory.createStock(symbol.toUpperCase(), price, changePercent);
                            dataHandler.addStockToJson(MainActivity.this, stock);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel the refresh task
        if (refreshHandler != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
            refreshHandler = null;
        }
    }
}