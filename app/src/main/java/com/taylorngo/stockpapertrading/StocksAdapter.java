package com.taylorngo.stockpapertrading;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.StocksViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private RequestQueue mQueue;

    private static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences;

    public StocksAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public StocksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_item, parent, false);
        sharedPreferences = itemView.getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("totalCost", String.valueOf(0.0));
        editor.apply();
        mQueue = Volley.newRequestQueue(itemView.getContext());
        return new StocksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StocksAdapter.StocksViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }
        String ticker = mCursor.getString(mCursor.getColumnIndex(StocksContract.StockEntry.COLUMN_NAME));
        holder.stockItemNameLabel.setText(ticker);
        double shares = mCursor.getDouble(mCursor.getColumnIndex(StocksContract.StockEntry.COLUMN_AMOUNT));
        holder.stockItemSharesLabel.setText(shares + " shares");

        String API_KEY = "0344862ce6e643d4a1bb3bca12776a36";
        String urlString = "https://financialmodelingprep.com/api/v3/quote/" + ticker + "?apikey=" + API_KEY;
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
                                String currTotalCostString = sharedPreferences.getString("totalCost", "0.0");
                                double currTotalCost = Double.parseDouble(currTotalCostString);
                                currTotalCost += stock.getDouble("price") * shares;
                                currTotalCost = Math.round(currTotalCost * 100.0) / 100.0;
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("totalCost", String.valueOf(currTotalCost));
                                editor.apply();
                                double total = stock.getDouble("price") * shares;
                                total = Math.round(total * 100.0) / 100.0;
                                holder.stockItemPriceLabel.setText("$" + total);
                            }
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

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;
        if(newCursor != null){
            notifyDataSetChanged();
        }
    }

    public class StocksViewHolder extends RecyclerView.ViewHolder {
        private TextView stockItemNameLabel;
        private TextView stockItemSharesLabel;
        private TextView stockItemPriceLabel;

        public StocksViewHolder(View itemView) {
            super(itemView);
            stockItemNameLabel = itemView.findViewById(R.id.stockItemNameLabel);
            stockItemPriceLabel = itemView.findViewById(R.id.stockItemPriceLabel);
            stockItemSharesLabel = itemView.findViewById(R.id.stockItemSharesLabel);
        }
    }
}
