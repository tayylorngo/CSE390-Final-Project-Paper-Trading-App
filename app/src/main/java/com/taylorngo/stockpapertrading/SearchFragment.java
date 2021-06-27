package com.taylorngo.stockpapertrading;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchFragment extends Fragment {

    private static final String SHARED_PREFS = "sharedPrefs";

    private String stockTicker;
    private String stockName;
    private double stockPrice;
    private RequestQueue mQueue;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mContext = view.getContext();
        EditText stockInput = view.findViewById(R.id.stockSearchForm);
        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockTicker = stockInput.getText().toString();
                getStockData();
                closeKeyboard();
            }
        });
        mQueue = Volley.newRequestQueue(getActivity());
        return view;
    }

    public void getStockData(){
        String API_KEY = "2329aa49fc077f763ccd0d3839e6e913";
        String urlString = "https://financialmodelingprep.com/api/v3/quote/" + stockTicker + "?apikey=" + API_KEY;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        double currBalance = Double.parseDouble(sharedPreferences.getString("balance", "0.0"));
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if(response.length() == 0){
                                Toast toast = Toast.makeText(mContext, "Asset not found.", Toast.LENGTH_SHORT);
                                toast.show();
                                return;
                            }
                            for(int i = 0; i < response.length(); i++){
                                JSONObject stock = response.getJSONObject(i);
                                stockName = stock.getString("name");
                                stockPrice = stock.getDouble("price");
                            }
                            Intent intent = new Intent(getActivity(), StockDataActivity.class);
                            intent.putExtra("stockTicker", stockTicker);
                            intent.putExtra("stockName", stockName);
                            intent.putExtra("stockPrice", stockPrice);
                            intent.putExtra("totalBalance", currBalance);
                            startActivity(intent);
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

    public void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
