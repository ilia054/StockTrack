package com.example.stockpriceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> implements OnStockClickListener {
    private ArrayList<Stock> stockList;
    private Context context;

    public StockAdapter(Context context) {
        this.context = context;
        this.stockList = dataHandler.getStockInstance(context);
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.bind(stock);
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public void deleteItem(int position) {
        Stock deletedStock = stockList.get(position);
        dataHandler.removeStockFromJson(context, deletedStock);
        notifyItemRemoved(position);
    }

    @Override
    public void onStockClick(int position) {
        Stock clickedStock = stockList.get(position);
        Intent intent = new Intent(context, StockDetailsActivity.class);
        intent.putExtra("stockSymbol", clickedStock.getStockSymbol());
        context.startActivity(intent);
    }

    public static class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dailyChangeTextView;
        TextView priceTextView;
        TextView stockSymbolTextView;
        private OnStockClickListener stockClickListener;

        public StockViewHolder(@NonNull View itemView, OnStockClickListener stockClickListener) {
            super(itemView);
            this.stockClickListener = stockClickListener;
            dailyChangeTextView = itemView.findViewById(R.id.dailyChangeTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            stockSymbolTextView = itemView.findViewById(R.id.stockSymbolTextView);
            itemView.setOnClickListener(this);
        }

        public void bind(Stock stock) {
            dailyChangeTextView.setText(stock.getDailyChange());
            priceTextView.setText(String.valueOf(stock.getCurrentPrice()));
            stockSymbolTextView.setText(stock.getStockSymbol());

            String dailyChange = stock.getDailyChange().replace("%", "");
            double change = Double.parseDouble(dailyChange);
            if (change > 0) {
                dailyChangeTextView.setText("+" + dailyChangeTextView.getText());
                dailyChangeTextView.setTextColor(Color.GREEN);
            } else if (change < 0) {
                dailyChangeTextView.setTextColor(Color.RED);
            } else {
                dailyChangeTextView.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                stockClickListener.onStockClick(position);
            }
        }

    }

}