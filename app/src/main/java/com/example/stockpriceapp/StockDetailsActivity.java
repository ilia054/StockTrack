package com.example.stockpriceapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StockDetailsActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private int changePeriod = 0; // 0-> daily , 1-> weekly , 2-> monthly , 3-> yearly
    private List<StockData> stockDataList = new ArrayList<>(); // Replace with your actual list of StockData objects
    private String stockSymbol;
    private LineChart stockChart;
    private TextView stockSymbolTextView;
    private TextView graphTextView;
    private TextView clickedCoordinatesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);

        stockSymbolTextView = findViewById(R.id.stockSymbolTextView);
        graphTextView = findViewById(R.id.graphTextView);
        stockChart = findViewById(R.id.stockChart);
        clickedCoordinatesTextView = findViewById((R.id.clickedCoordinatesTextView));
        Button buttonDaily = findViewById(R.id.buttonDaily);
        Button buttonWeekly = findViewById(R.id.buttonWeekly);
        Button buttonMonthly = findViewById(R.id.buttonMonthly);
        Button buttonYearly = findViewById(R.id.buttonYearly);

        // Retrieve the selected stock symbol from the intent
        stockSymbol = getIntent().getStringExtra("stockSymbol");

        // Set the stock symbol in the TextView
        stockSymbolTextView.setText(stockSymbol);

        // Set the text for graphTextView
        setTimeFrameText();

        // Button click listeners
        buttonDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePeriod = 0;
                setTimeFrameText();
                dailyGraph();
                // TODO: Implement the desired functionality
            }
        });

        buttonWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePeriod = 1;
                setTimeFrameText();
                WeeklyGraph();
                // TODO: Implement the desired functionality
            }
        });
        buttonMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePeriod = 2;
                setTimeFrameText();
                monthlyGraph();
                // TODO: Implement the desired functionality
            }
        });

        buttonYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePeriod = 3;
                setTimeFrameText();
                yearlyGraph();
                // TODO: Implement the desired functionality
            }
        });

        // Set the OnChartValueSelectedListener
        stockChart.setOnChartValueSelectedListener(this);

    }

    private void plotStockGraph(LineChart stockChart) {
        List<Entry> entries = new ArrayList<>();
        for (int i = stockDataList.size() - 1; i >= 0; i--) {
            StockData stockData = stockDataList.get(i);
            float x = Float.parseFloat(stockData.getDateAndTime().substring(0, 2)); // Use the time in milliseconds as the x-axis value
            float y = Float.parseFloat(stockData.getOpenValue()); // Parse the open value as float
            entries.add(new Entry(x, y));
        }
        // Create a LineDataSet from the entries
        LineDataSet dataSet = new LineDataSet(entries, "Stock Data");
        // Customize the line appearance
        dataSet.setLineWidth(2f);
        dataSet.setColor(ContextCompat.getColor(this, R.color.black));
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.black));
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);

        dataSet.setHighlightEnabled(true);
        dataSet.setHighLightColor(Color.parseColor("#6750A4"));
        dataSet.setHighlightLineWidth(2.5f);
        dataSet.setDrawHorizontalHighlightIndicator(false);

        // Set the fill color for the area below the curve
        Drawable fillDrawable = didStockMakeProfit();
        dataSet.setFillDrawable(fillDrawable);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(255); // Set the alpha value to 255 for solid fill

        // Create a LineData object with the dataset
        LineData lineData = new LineData(dataSet);

        // Set the LineData to the chart
        stockChart.setData(lineData);

        // Configure the chart appearance
        stockChart.getDescription().setEnabled(false);
        stockChart.setTouchEnabled(true);
        stockChart.setDragEnabled(true);
        stockChart.setScaleEnabled(true);
        stockChart.setPinchZoom(true);
        stockChart.setDrawGridBackground(false);
        stockChart.getLegend().setEnabled(false);
        stockChart.setExtraOffsets(8f, 16f, 8f, 16f);

        // Configure the X axis
        XAxis xAxis = stockChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new XAxisValueFormatter(changePeriod, stockDataList));
        xAxis.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // Configure the Y axis
        YAxis yAxisLeft = stockChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawZeroLine(true);
        yAxisLeft.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        YAxis yAxisRight = stockChart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Animate the chart drawing on the UI thread
        stockChart.post(new Runnable() {
            @Override
            public void run() {
                stockChart.animateX(1000, Easing.EaseInOutQuart);
                stockChart.invalidate();
            }
        });
    }

    private void resetGraph() {
        stockChart.clear();
        stockDataList.clear();

    }

    private void dailyGraph() {
        resetGraph();
        RequestHandler handler = new RequestHandler();
        handler.fetchDailyStockData(stockSymbol, new DataCallback() {
            @Override
            public void onDataFetchSuccess(List<StockData> fetchedStockDataList) {
                // Plot the graph with the fetched data
                stockDataList = fetchedStockDataList;
                plotStockGraph(stockChart);
            }

            @Override
            public void onDataFetchFailure() {
                // Handle failure if needed
            }
        });
    }

    private void WeeklyGraph() {
        resetGraph();
        RequestHandler handler = new RequestHandler();
        handler.fetchWeeklyOrMonthlyStockData(stockSymbol, RequestHandler.WEEKLY, new DataCallback() {
            @Override
            public void onDataFetchSuccess(List<StockData> fetchedStockDataList) {
                // Plot the graph with the fetched data
                stockDataList = fetchedStockDataList;
                plotStockGraph(stockChart);
            }

            @Override
            public void onDataFetchFailure() {
                // Handle failure if needed
            }
        });
    }

    private void monthlyGraph() {
        resetGraph();
        RequestHandler handler = new RequestHandler();
        handler.fetchWeeklyOrMonthlyStockData(stockSymbol, RequestHandler.MONTHLY, new DataCallback() {
            @Override
            public void onDataFetchSuccess(List<StockData> fetchedStockDataList) {
                // Plot the graph with the fetched data
                stockDataList = fetchedStockDataList;
                plotStockGraph(stockChart);
            }

            @Override
            public void onDataFetchFailure() {
                // Handle failure if needed
            }
        });
    }

    private void yearlyGraph() {
        resetGraph();
        RequestHandler handler = new RequestHandler();
        handler.fetchYearlyStockData(stockSymbol, new DataCallback() {
            @Override
            public void onDataFetchSuccess(List<StockData> fetchedStockDataList) {
                // Plot the graph with the fetched data
                stockDataList = fetchedStockDataList;
                plotStockGraph(stockChart);
            }

            @Override
            public void onDataFetchFailure() {
                // Handle failure if needed
            }
        });
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        XAxisValueFormatter formatHelper = new XAxisValueFormatter(changePeriod, stockDataList);
        float y = e.getY();
        String value = y + " USD " + formatHelper.getFormattedValue(e.getX());
        clickedCoordinatesTextView.setText(value);
    }

    @Override
    public void onNothingSelected() {
        stockChart.highlightValue(null);
    }

    private Drawable didStockMakeProfit() {
        try {
            float closingPrice = Float.parseFloat(stockDataList.get(stockDataList.size() - 1).getOpenValue());
            float startingPrice = Float.parseFloat(stockDataList.get(0).getOpenValue());
            if (startingPrice - closingPrice >= 0) {
                return ContextCompat.getDrawable(this, R.drawable.fade_green);
            } else {
                return ContextCompat.getDrawable(this, R.drawable.fade_red);
            }
        }
        catch (Exception e)
        {
            return ContextCompat.getDrawable(this, R.drawable.fade_green);
        }
    }

    private void setTimeFrameText() {
        String timeFrameText = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        Calendar currentDate = Calendar.getInstance();
        String currentDateStr = dateFormat.format(currentDate.getTime());
        Calendar startDate;

        switch (changePeriod) {
            case 0: // Daily
                timeFrameText = "Daily Performance (" + currentDateStr + ")";
                break;
            case 1: // Weekly
                startDate = Calendar.getInstance();
                startDate.add(Calendar.DAY_OF_YEAR, -7);
                timeFrameText = "Weekly Trend (" + dateFormat.format(startDate.getTime()) + "-" + currentDateStr + ")";
                break;
            case 2: // Monthly
                startDate = Calendar.getInstance();
                startDate.add(Calendar.MONTH, -1);
                timeFrameText = "Monthly Analysis (" + dateFormat.format(startDate.getTime()) + "-" + currentDateStr + ")";
                break;
            case 3: // Yearly
                startDate = Calendar.getInstance();
                startDate.add(Calendar.YEAR, -1);
                timeFrameText = "Yearly Change (" + dateFormat.format(startDate.getTime()) + "-" + currentDateStr + ")";
                break;
            default:
                timeFrameText = "Invalid time frame";
                break;
        }

        graphTextView.setText(timeFrameText);
    }
}