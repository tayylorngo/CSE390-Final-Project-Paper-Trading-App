// Taylor Ngo
// 112626118
package com.taylorngo.stockpapertrading;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The StockDataActivity class implements the Activity page where the
 * user can view an asset's data including price, their ownership, and
 * the returns they have on the asset.
 *
 * @author Taylor Ngo
 */
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
    private SQLiteDatabase rDatabase;
    private BuyStockDialog buyStockDialog;
    private SellStockDialog sellStockDialog;

    /**
     * This method creates the activity and initalizes
     * all TextViews and button functionality.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_data);

        StocksDBHelper dbHelper = new StocksDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();
        rDatabase = dbHelper.getReadableDatabase();

        Intent intent = getIntent();
        this.stockTicker = intent.getStringExtra("stockTicker");
        this.stockName = intent.getStringExtra("stockName");
        this.stockPrice = intent.getDoubleExtra("stockPrice", 0.0);
        this.totalBalance = intent.getDoubleExtra("totalBalance", 0.0);
        this.sharesOwned = intent.getDoubleExtra("sharesOwned", 0.0);
        this.totalCost = intent.getDoubleExtra("totalCost", 0.0);

        this.averageCost = totalCost / sharesOwned;
        this.averageCost = MainActivity.round(this.averageCost);

        this.totalReturn = (stockPrice * sharesOwned) - totalCost;
        this.totalReturn = MainActivity.round(this.totalReturn);

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

    /**
     * This method sets the TextViews to the user's information
     * such as asset ownership and total returns.
     */
    public void getUserInfo(){
        TextView sharesOwnedLabel = findViewById(R.id.sharesOwnedLabel);
        sharesOwnedLabel.setText("Shares Owned: " + MainActivity.priceify(sharesOwned));
        TextView totalReturnLabel = findViewById(R.id.totalReturnLabel);
        if(totalReturn < 0){
            totalReturnLabel.setText("Total Return: -$" + MainActivity.priceify(Math.abs(totalReturn)));
        }
        else{
            totalReturnLabel.setText("Total Return: $" + MainActivity.priceify(totalReturn));
        }
        TextView averageCostLabel = findViewById(R.id.averageCostLabel);
        averageCostLabel.setText("Average Cost: $" + MainActivity.priceify(averageCost));
    }

    /**
     * This method updates the TextViews of the asset name, ticker, and price.
     */
    public void updateInfo(){
        TextView stockNameLabel = findViewById(R.id.stockNameLabel);
        TextView stockPriceLabel = findViewById(R.id.stockPriceLabel);
        TextView stockTickerLabel = findViewById(R.id.stockTickerLabel);
        stockNameLabel.setText(stockName);
        stockTickerLabel.setText(stockTicker);
        stockPriceLabel.setText("$" + MainActivity.priceify(stockPrice));
    }

    /**
     * This method creates and shows the BuyStockDialog
     */
    public void openBuySharesDialog(){
        buyStockDialog = new BuyStockDialog(totalBalance, stockPrice, stockTicker);
        buyStockDialog.show(getSupportFragmentManager(), "Buy " + stockTicker);
    }

    /**
     * This method creates and shows the SellStockDialog
     */
    public void openSellSharesDialog(){
        sellStockDialog = new SellStockDialog(totalBalance, stockPrice, sharesOwned, stockTicker);
        sellStockDialog.show(getSupportFragmentManager(), "Sell " + stockTicker);
    }

    /**
     * This method performs the action of buying a stock
     *
     * @param amount Amount to purchase
     */
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

    /**
     * This method updates and calculates all the values of buying a stock
     * and updates them accordingly.
     *
     * @param amount Amount to purchase.
     */
    public void buyStock(double amount){
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        double currBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        String name = this.stockTicker;
        double cost = amount * stockPrice;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        double newBalance = currBalance - cost;
        newBalance = MainActivity.round(newBalance);
        totalBalance = newBalance;
        editor.putString("balance", String.valueOf(newBalance));
        double currTotalCost = Double.parseDouble(sharedPreferences.getString("totalCost", "0.0"));
        currTotalCost += cost;
        editor.putString("totalCost", String.valueOf(currTotalCost));
        editor.apply();

        sharesOwned += amount;
        sharesOwned = MainActivity.round(sharesOwned);

        this.totalCost += cost;
        this.averageCost = totalCost / sharesOwned;
        this.averageCost = MainActivity.round(this.averageCost);

        this.totalReturn = (stockPrice * sharesOwned) - totalCost;
        this.totalReturn = MainActivity.round(this.totalReturn);

        boolean ownedStock = false;
        double totalCostOfStock = 0;
        String selectQuery = "SELECT * FROM " + StocksContract.StockEntry.TABLE_NAME;
        Cursor cursor = rDatabase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String temp = cursor.getString(1);
            if(temp.equals(stockTicker)){
                ownedStock = true;
                totalCostOfStock = cursor.getDouble(3);
            }
            cursor.moveToNext();
        }

        ContentValues cv = new ContentValues();
        cv.put(StocksContract.StockEntry.COLUMN_NAME, name);
        if(!ownedStock){
            cv.put(StocksContract.StockEntry.COLUMN_AMOUNT, amount);
            cv.put(StocksContract.StockEntry.COLUMN_COST, cost);
            mDatabase.insert(StocksContract.StockEntry.TABLE_NAME, null, cv);
        }
        else{
            cv.put(StocksContract.StockEntry.COLUMN_AMOUNT, sharesOwned);
            cv.put(StocksContract.StockEntry.COLUMN_COST, totalCostOfStock + cost);
            mDatabase.update(StocksContract.StockEntry.TABLE_NAME, cv, "name=?", new String[]{stockTicker});
        }

        HomeFragment.mAdapter.swapCursor(mDatabase.query(
                StocksContract.StockEntry.TABLE_NAME,
                null, null, null, null, null,
                StocksContract.StockEntry.COLUMN_TIMESTAMP + " DESC"
        ));
        Toast toast = Toast.makeText(this, "Purchased successfully", Toast.LENGTH_SHORT);
        toast.show();
        mDatabase.close();
    }

    /**
     * This method performs the action of selling a stock.
     *
     * @param amount Amount to sell
     * @param stockName Name of the stock.
     */
    @Override
    public void applyTexts(double amount, String stockName) {
        sellStock(amount);
        getUserInfo();
        sellStockDialog.dismiss();
    }

    /**
     * This method updates and calculates all the values after selling
     * an asset.
     *
     * @param amount Amount to sell.
     */
    public void sellStock(double amount){
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        double currBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        String name = this.stockTicker;
        double totalSellValue = amount * stockPrice;
        totalSellValue = MainActivity.round(totalSellValue);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        double newBalance = currBalance + totalSellValue;
        newBalance = MainActivity.round(newBalance);
        totalBalance = newBalance;
        editor.putString("balance", String.valueOf(newBalance));
        double currTotalCost = Double.parseDouble(sharedPreferences.getString("totalCost", "0.0"));
        currTotalCost -= (averageCost * amount);
        currTotalCost = MainActivity.round(currTotalCost);
        editor.putString("totalCost", String.valueOf(currTotalCost));

        sharesOwned -= amount;
        sharesOwned = MainActivity.round(sharesOwned);

        if(sharesOwned == 0){
            this.totalReturn = 0.0;
            this.totalCost = 0.0;
        }
        else{
//            this.totalReturn = (stockPrice * sharesOwned) - totalCost;
//            this.totalReturn = MainActivity.round(this.totalReturn);
            this.totalCost -= (averageCost * amount);
//            this.averageCost = totalCost / sharesOwned;
//            this.averageCost = MainActivity.round(this.averageCost);
        }

        if(sharesOwned == 0){
            mDatabase.delete(StocksContract.StockEntry.TABLE_NAME,
                    "name=?", new String[]{stockTicker});
        }
        else{
            ContentValues cv = new ContentValues();
            cv.put(StocksContract.StockEntry.COLUMN_NAME, name);
            cv.put(StocksContract.StockEntry.COLUMN_AMOUNT, sharesOwned);
            cv.put(StocksContract.StockEntry.COLUMN_COST, totalCost);
            mDatabase.update(StocksContract.StockEntry.TABLE_NAME, cv, "name=?", new String[]{stockTicker});
        }

        double oldProfit = Double.parseDouble(sharedPreferences.getString("profit", "0.0"));
        double profit = (this.stockPrice - this.averageCost) * amount;
        profit += oldProfit;
        profit = MainActivity.round(profit);
        editor.putString("profit", String.valueOf(profit));
        editor.apply();

        if(sharesOwned == 0){
            this.averageCost = 0.0;
        }

        HomeFragment.mAdapter.swapCursor(mDatabase.query(
                StocksContract.StockEntry.TABLE_NAME,
                null, null, null, null, null,
                StocksContract.StockEntry.COLUMN_TIMESTAMP + " DESC"
        ));
        Toast toast = Toast.makeText(this, "Sold " + amount + " shares of " + stockName + " successfully", Toast.LENGTH_SHORT);
        toast.show();
        mDatabase.close();
    }
}