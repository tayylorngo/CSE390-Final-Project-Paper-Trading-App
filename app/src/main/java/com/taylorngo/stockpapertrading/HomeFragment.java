package com.taylorngo.stockpapertrading;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    public static final String SHARED_PREFS = "sharedPrefs";
    private SQLiteDatabase mDatabase;
    private SQLiteDatabase rDatabase;

    static StocksAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView balanceLabel = view.findViewById(R.id.totalLabel);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        TextView buyingPowerLabel = view.findViewById(R.id.buyingPowerLabel);
        buyingPowerLabel.setText("Buying Power: $" + sharedPreferences.getString("balance", "0.0"));
        double totalBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        double totalStocksPrice = Double.parseDouble(sharedPreferences.getString("totalCost", "0.0"));
        double actualBalance = totalBalance + totalStocksPrice;
        balanceLabel.setText("$" + (actualBalance));

        double totalCost = 0.0;
        StocksDBHelper dbHelper = new StocksDBHelper(view.getContext());
        String selectQuery = "SELECT * FROM " + StocksContract.StockEntry.TABLE_NAME;
        rDatabase = dbHelper.getReadableDatabase();
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

        totalBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        totalStocksPrice = Double.parseDouble(sharedPreferences.getString("totalCost", "0.0"));
        actualBalance = totalBalance + totalStocksPrice;
        actualBalance = Math.round(actualBalance * 100.0) / 100.0;
        balanceLabel.setText("$" + (actualBalance));
        TextView profitLabel = view.findViewById(R.id.profitLossLabel);
        double profit = (totalStocksPrice - totalCost);
        profit = Math.round(profit * 100.0) / 100.0;
        profitLabel.setText("Total Profit/Loss: $" + profit);
        return view;
    }

    private Cursor getAllItems(){
        return mDatabase.query(
                StocksContract.StockEntry.TABLE_NAME,
                null, null, null, null, null,
                StocksContract.StockEntry.COLUMN_TIMESTAMP + " DESC"
        );
    }
}
