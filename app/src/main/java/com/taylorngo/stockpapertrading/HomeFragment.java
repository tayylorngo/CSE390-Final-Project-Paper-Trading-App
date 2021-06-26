package com.taylorngo.stockpapertrading;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private SQLiteDatabase mDatabase;
    static StocksAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        StocksDBHelper dbHelper = new StocksDBHelper(view.getContext());
        mDatabase = dbHelper.getWritableDatabase();
        RecyclerView recyclerView = view.findViewById(R.id.assetHoldingsView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAdapter = new StocksAdapter(view.getContext(), getAllItems());
        recyclerView.setAdapter(mAdapter);
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
