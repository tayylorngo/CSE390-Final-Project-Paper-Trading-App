// Taylor Ngo
// 112626118
package com.taylorngo.stockpapertrading;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * The HomeFragment class implements the HomeScreen of the application
 * that shows the user account value and list of the user's assets.
 *
 * @author Taylor Ngo
 */
public class HomeFragment extends Fragment {
    public static final String SHARED_PREFS = "sharedPrefs";
    private SQLiteDatabase mDatabase;
    static StocksAdapter mAdapter;

    private String sortBy;
    private String sortOrder;

    /**
     * This method creates the View for the HomeFragment which includes
     * the TextViews for account value, profit/loss, buying power, and
     * the RecyclerView for the user's assets.
     *
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView balanceLabel = view.findViewById(R.id.totalLabel);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        TextView buyingPowerLabel = view.findViewById(R.id.buyingPowerLabel);
        buyingPowerLabel.setText("Buying Power: $" + MainActivity.priceify(Double.parseDouble(sharedPreferences.getString("balance", "0.0"))));
        double totalBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        double totalStocksPrice = Double.parseDouble(sharedPreferences.getString("totalCost", "0.0"));
        double actualBalance = totalBalance + totalStocksPrice;
        balanceLabel.setText("$" + MainActivity.priceify(actualBalance));

        double totalCost = 0.0;
        StocksDBHelper dbHelper = new StocksDBHelper(view.getContext());
        String selectQuery = "SELECT * FROM " + StocksContract.StockEntry.TABLE_NAME;
        SQLiteDatabase rDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = rDatabase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            totalCost += cursor.getDouble(3);
            cursor.moveToNext();
        }
        cursor.close();
        rDatabase.close();
        mDatabase = dbHelper.getWritableDatabase();
        RecyclerView recyclerView = view.findViewById(R.id.assetHoldingsView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext().getApplicationContext()));
        mAdapter = new StocksAdapter(view.getContext(), getAllItems());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        sortBy = sharedPreferences.getString("sortBy", "");
        sortOrder = sharedPreferences.getString("sortOrder", "");
        sortList(sortBy, sortOrder);

        totalBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        if(totalCost == 0){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("totalCost", "0.0");
            editor.apply();
        }
        totalStocksPrice = Double.parseDouble(sharedPreferences.getString("totalCost", "0.0"));
        actualBalance = totalBalance + totalStocksPrice;
        actualBalance = MainActivity.round(actualBalance);
        balanceLabel.setText("$" + MainActivity.priceify(actualBalance));
        TextView profitLabel = view.findViewById(R.id.profitLossLabel);
        double profit = (totalStocksPrice - totalCost);
        profit += Double.parseDouble(sharedPreferences.getString("profit", "0.0"));
        profit = MainActivity.round(profit);
        if(profit < 0){
            profitLabel.setText("Total Profit/Loss: -$" + MainActivity.priceify(Math.abs(profit)));
            profitLabel.setTextColor(Color.RED);
        }
        else if(profit == 0){
            profitLabel.setText("Total Profit/Loss: $" + MainActivity.priceify(Math.abs(profit)));
        }
        else{
            profitLabel.setText("Total Profit/Loss: $" + MainActivity.priceify(profit));
            profitLabel.setTextColor(Color.GREEN);
        }
        FloatingActionButton refreshBtn = view.findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
            }
        });
        return view;
    }

    /**
     * This method sorts the list onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getView().getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        sortBy = sharedPreferences.getString("sortBy", "");
        sortOrder = sharedPreferences.getString("sortOrder", "");
        sortList(sortBy, sortOrder);
    }

    /**
     * This method sorts the RecyclerView based on
     * data from the SettingsFragment
     *
     * @param sortBy Sorting criteria
     * @param sortOrder Sort order(ascending or descending)
     */
    public void sortList(String sortBy, String sortOrder){
        if(sortOrder.equals("Ascending")){
            sortOrder = " ASC";
        }
        else{
            sortOrder = " DESC";
        }
        if(sortBy.equals("Name")){
            sortBy = StocksContract.StockEntry.COLUMN_NAME;
        }
        else if(sortBy.equals("Number of Shares")){
            sortBy = StocksContract.StockEntry.COLUMN_AMOUNT;
        }
        else if(sortBy.equals("Total Cost")){
            sortBy = StocksContract.StockEntry.COLUMN_COST;
        }
        else{
            sortBy = StocksContract.StockEntry.COLUMN_TIMESTAMP;
        }
        Cursor temp = mDatabase.query(StocksContract.StockEntry.TABLE_NAME,
                null, null, null, null, null,
                sortBy +  sortOrder);
        mAdapter.swapCursor(temp);
    }

    /**
     * This method queries all the items in the database.
     *
     * @return A Cursor object of all the items in the database.
     */
    private Cursor getAllItems(){
        return mDatabase.query(
                StocksContract.StockEntry.TABLE_NAME,
                null, null, null, null, null,
                StocksContract.StockEntry.COLUMN_TIMESTAMP + " DESC"
        );
    }
}