package com.taylorngo.stockpapertrading;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText stockInput = view.findViewById(R.id.stockSearchForm);
        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchStock(stockInput.getText().toString());
            }
        });
        return view;
    }

    public void searchStock(String stock){
        Intent intent = new Intent(getActivity(), StockDataActivity.class);
        intent.putExtra("stockName", stock);
        startActivity(intent);
    }
}
