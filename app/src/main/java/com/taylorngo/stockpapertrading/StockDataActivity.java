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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;

public class StockDataActivity extends AppCompatActivity implements BuyStockDialog.BuyStockDialogListener {
    private String stockName;
    private String stockTicker;
    private double stockPrice;
    private double totalBalance;
    private int sharesOwned;
    private double totalReturn;
    private double averageCost;

    private BuyStockDialog buyStockDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_data);
        Intent intent = getIntent();
        this.stockTicker = intent.getStringExtra("stockTicker");
        this.stockName = intent.getStringExtra("stockName");
        this.stockPrice = intent.getDoubleExtra("stockPrice", 0.0);
        this.totalBalance = intent.getDoubleExtra("totalBalance", 0.0);
        getUserInfo();
        updateInfo();
        Button buyButton = findViewById(R.id.buyBtn);
        Button sellButton = findViewById(R.id.sellBtn);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBuySharesDialog();
            }
        });
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSellSharesDialog();
            }
        });
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

    public void openBuySharesDialog(){
        buyStockDialog = new BuyStockDialog(totalBalance, stockPrice, stockTicker);
        buyStockDialog.show(getSupportFragmentManager(), "Buy " + stockTicker);
    }

    public void openSellSharesDialog(){


    }

    @Override
    public void applyTexts(double amount) {
        // maybe refresh stock price here
        if(amount * stockPrice > totalBalance){
            Toast toast = Toast.makeText(this, "Not enough funds.", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            System.out.println("YOU COPPED " + amount);
            buyStockDialog.dismiss();
        }
    }
}