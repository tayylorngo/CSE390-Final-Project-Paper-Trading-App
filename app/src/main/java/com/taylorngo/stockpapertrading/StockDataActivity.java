package com.taylorngo.stockpapertrading;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;

public class StockDataActivity extends AppCompatActivity {
    private String stockName;
    private String stockTicker;
    private double stockPrice;
    private int sharesOwned;
    private double totalReturn;
    private double averageCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_data);
        Intent intent = getIntent();
        this.stockTicker = intent.getStringExtra("stockTicker");
        this.stockName = intent.getStringExtra("stockName");
        this.stockPrice = intent.getDoubleExtra("stockPrice", 0.0);
        getUserInfo();
        updateInfo();
    }

    public void getUserInfo(){
        TextView sharesOwnedLabel = findViewById(R.id.sharesOwnedLabel);
        sharesOwnedLabel.setText("Shares Owned: " + sharesOwned);
        TextView totalReturnLabel = findViewById(R.id.totalReturnLabel);
        totalReturnLabel.setText("Total Return: $" + totalReturn);
        TextView averageCostLabel = findViewById(R.id.averageCostLabel);
        averageCostLabel.setText("Average Cost: $" + averageCost);
    }

    public void updateInfo(){
        TextView stockNameLabel = findViewById(R.id.stockNameLabel);
        TextView stockPriceLabel = findViewById(R.id.stockPriceLabel);
        TextView stockTickerLabel = findViewById(R.id.stockTickerLabel);
        stockNameLabel.setText(stockName);
        stockTickerLabel.setText(stockTicker);
        stockPriceLabel.setText("$" + stockPrice);
    }


}