package com.taylorngo.stockpapertrading;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StockDataActivity extends AppCompatActivity implements BuyStockDialog.BuyStockDialogListener, SellStockDialog.SellStockDialogListener {

    private static final String SHARED_PREFS = "sharedPrefs";

    private String stockName;
    private String stockTicker;
    private double stockPrice;
    private double totalBalance;
    private double sharesOwned;
    private double totalCost;
    private double totalReturn;
    private double averageCost;

    private SQLiteDatabase mDatabase;
    private BuyStockDialog buyStockDialog;
    private SellStockDialog sellStockDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_data);

        StocksDBHelper dbHelper = new StocksDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        this.stockTicker = intent.getStringExtra("stockTicker");
        this.stockName = intent.getStringExtra("stockName");
        this.stockPrice = intent.getDoubleExtra("stockPrice", 0.0);
        this.totalBalance = intent.getDoubleExtra("totalBalance", 0.0);
        this.sharesOwned = intent.getDoubleExtra("sharesOwned", 0.0);
        this.totalCost = intent.getDoubleExtra("totalCost", 0.0);
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
        sellStockDialog = new SellStockDialog(totalBalance, stockPrice, sharesOwned, stockTicker);
        sellStockDialog.show(getSupportFragmentManager(), "Sell " + stockTicker);
    }

    @Override
    public void applyTexts(double amount) {
        // maybe refresh stock price here
        if(amount * stockPrice > totalBalance){
            Toast toast = Toast.makeText(this, "Not enough funds.", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            buyStock(amount);
            getUserInfo();
            buyStockDialog.dismiss();
        }
    }

    public void buyStock(double amount){
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        double currBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        String name = this.stockTicker;
        double cost = amount * stockPrice;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        double newBalance = currBalance - cost;
        newBalance = Math.round(newBalance * 100.0) / 100.0;
        totalBalance = newBalance;
        editor.putString("balance", String.valueOf(newBalance));
        double currTotalCost = Double.parseDouble(sharedPreferences.getString("totalCost", "0.0"));
        currTotalCost += cost;
        editor.putString("totalCost", String.valueOf(currTotalCost));
        editor.apply();

        sharesOwned -= amount;
        sharesOwned = Math.round(sharesOwned * 100.0) / 100.0;

        ContentValues cv = new ContentValues();
        cv.put(StocksContract.StockEntry.COLUMN_NAME, name);
        cv.put(StocksContract.StockEntry.COLUMN_AMOUNT, amount);
        cv.put(StocksContract.StockEntry.COLUMN_COST, cost);

        mDatabase.insert(StocksContract.StockEntry.TABLE_NAME, null, cv);

        HomeFragment.mAdapter.swapCursor(mDatabase.query(
                StocksContract.StockEntry.TABLE_NAME,
                null, null, null, null, null,
                StocksContract.StockEntry.COLUMN_TIMESTAMP + " DESC"
        ));

        Toast toast = Toast.makeText(this, "Purchased successfully", Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void applyTexts(double amount, String stockName) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        double currBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        String name = this.stockTicker;
        double totalSellValue = amount * stockPrice;
        totalSellValue = Math.round(totalSellValue * 100.0) / 100.0;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        double newBalance = currBalance + totalSellValue;
        newBalance = Math.round(newBalance * 100.0) / 100.0;
        totalBalance = newBalance;
        editor.putString("balance", String.valueOf(newBalance));
        double currTotalCost = Double.parseDouble(sharedPreferences.getString("totalCost", "0.0"));
        System.out.println(currTotalCost);
        currTotalCost -= totalSellValue;
        System.out.println(currTotalCost);
        editor.putString("totalCost", String.valueOf(currTotalCost));
        editor.apply();

        sharesOwned += amount;
        sharesOwned = Math.round(sharesOwned * 100.0) / 100.0;

        if(sharesOwned == 0){
            mDatabase.delete(StocksContract.StockEntry.TABLE_NAME,
                    "name=?", new String[]{stockTicker});
        }
        else{
            ContentValues cv = new ContentValues();
            cv.put(StocksContract.StockEntry.COLUMN_NAME, name);
            cv.put(StocksContract.StockEntry.COLUMN_AMOUNT, sharesOwned);
            cv.put(StocksContract.StockEntry.COLUMN_COST, (totalCost -  totalSellValue));
            mDatabase.update(StocksContract.StockEntry.TABLE_NAME, cv, "name=?", new String[]{stockTicker});
        }

        HomeFragment.mAdapter.swapCursor(mDatabase.query(
                StocksContract.StockEntry.TABLE_NAME,
                null, null, null, null, null,
                StocksContract.StockEntry.COLUMN_TIMESTAMP + " DESC"
        ));
        Toast toast = Toast.makeText(this, "Sold " + amount + " shares of " + stockName + " successfully", Toast.LENGTH_SHORT);
        toast.show();
        getUserInfo();
        sellStockDialog.dismiss();
    }
}