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
    private RequestQueue mQueue;
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
        this.stockTicker = intent.getStringExtra("stockName");
        mQueue = Volley.newRequestQueue(this);
        getUserInfo();
        getStockData();
        TextView stockNameLabel = findViewById(R.id.stockTickerLabel);
        stockNameLabel.setText(stockTicker);
    }

    public void getUserInfo(){
        TextView sharesOwnedLabel = findViewById(R.id.sharesOwnedLabel);
        sharesOwnedLabel.setText("Shares Owned: " + sharesOwned);
        TextView totalReturnLabel = findViewById(R.id.totalReturnLabel);
        totalReturnLabel.setText("Total Return: $" + totalReturn);
        TextView averageCostLabel = findViewById(R.id.averageCostLabel);
        averageCostLabel.setText("Average Cost: $" + averageCost);
    }

    public void getStockData(){
        String API_KEY = "2329aa49fc077f763ccd0d3839e6e913";
        String urlString = "https://financialmodelingprep.com/api/v3/quote/" + stockTicker + "?apikey=" + API_KEY;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i = 0; i < response.length(); i++){
                                JSONObject stock = response.getJSONObject(i);
                                stockName = stock.getString("name");
                                stockPrice = stock.getDouble("price");
                            }
                            updateInfo();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    public void updateInfo(){
        TextView stockNameLabel = findViewById(R.id.stockNameLabel);
        TextView stockPriceLabel = findViewById(R.id.stockPriceLabel);
        stockNameLabel.setText(stockName);
        stockPriceLabel.setText("$" + stockPrice);
    }


}