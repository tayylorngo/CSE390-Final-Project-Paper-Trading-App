package com.taylorngo.stockpapertrading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class StockDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_data);
        TextView stockNameLabel = findViewById(R.id.stockTickerLabel);
        Intent intent = getIntent();
        stockNameLabel.setText(intent.getStringExtra("stockName"));
    }


}