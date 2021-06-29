// Taylor Ngo
// 112626118
package com.taylorngo.stockpapertrading;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The StocksAdapter class acts as an Adapter for the RecyclerView to show
 * the list of the user's assets.
 *
 * @author Taylor Ngo
 */
public class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.StocksViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private RequestQueue mQueue;
    private double sharesOwned;
    private double totalCost;

    private SQLiteDatabase rDatabase;
    private static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences;

    /**
     * Constructor to create the StocksAdapter
     *
     * @param context Context
     * @param cursor Cursor
     */
    public StocksAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    /**
     * This method creates the view for the RecyclerView.
     *
     * @param parent Parent
     * @param viewType ViewType
     *
     * @return A view representing the RecyclerView
     */
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

    /**
     * This method creates the view and updates the values for
     * each individual asset in the RecyclerView.
     *
     * @param holder holder
     * @param position position in the list.
     */
    @Override
    public void onBindViewHolder(StocksAdapter.StocksViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }
        String ticker = mCursor.getString(mCursor.getColumnIndex(StocksContract.StockEntry.COLUMN_NAME));
        holder.stockItemNameLabel.setText(ticker);
        double shares = mCursor.getDouble(mCursor.getColumnIndex(StocksContract.StockEntry.COLUMN_AMOUNT));
        holder.stockItemSharesLabel.setText(shares + " shares");

        String API_KEY = mContext.getString(R.string.api_key);
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
                                currTotalCost = MainActivity.round(currTotalCost);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("totalCost", String.valueOf(currTotalCost));
                                editor.apply();
                                double total = stock.getDouble("price") * shares;
                                total = MainActivity.round(total);
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

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            /**
             * This method opens an intent showing
             * the stock data selected in the list.
             * @param v view
             */
            @Override
            public void onClick(View v) {
                StocksDBHelper dbHelper = new StocksDBHelper(v.getContext());
                String selectQuery = "SELECT * FROM " + StocksContract.StockEntry.TABLE_NAME;
                rDatabase = dbHelper.getReadableDatabase();
                Cursor cursor = rDatabase.rawQuery(selectQuery, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String temp = cursor.getString(1);
                    if(temp.equals(ticker)){
                        sharesOwned = cursor.getDouble(2);
                        totalCost = cursor.getDouble(3);
                    }
                    cursor.moveToNext();
                }
                rDatabase.close();
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                String API_KEY = mContext.getString(R.string.api_key);
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
                                        Intent intent = new Intent(mContext, StockDataActivity.class);
                                        intent.putExtra("stockTicker", ticker);
                                        intent.putExtra("stockName", stock.getString("name"));
                                        intent.putExtra("stockPrice", stock.getDouble("price"));
                                        intent.putExtra("totalBalance", Double.parseDouble(sharedPreferences.getString("balance", "0.0")));
                                        intent.putExtra("sharesOwned", sharesOwned);
                                        intent.putExtra("totalCost", totalCost);
                                        mContext.startActivity(intent);
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
        });
    }

    /**
     * This method gets the item count of
     * the items in the database.
     *
     * @return The amount of items in the database.
     */
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * This method updates the RecyclerView
     * when data is altered in the database.
     *
     * @param newCursor newCursor
     */
    public void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;
        if(newCursor != null){
            notifyDataSetChanged();
        }
    }

    /**
     * This class acts the ViewHolder for the RecyclerView.
     *
     * @author Taylor Ngo
     */
    public class StocksViewHolder extends RecyclerView.ViewHolder {
        private TextView stockItemNameLabel;
        private TextView stockItemSharesLabel;
        private TextView stockItemPriceLabel;
        private ConstraintLayout mainLayout;

        /**
         * Constructor to create the StocksViewHolder
         *
         * @param itemView itemView
         */
        public StocksViewHolder(View itemView) {
            super(itemView);
            stockItemNameLabel = itemView.findViewById(R.id.stockItemNameLabel);
            stockItemPriceLabel = itemView.findViewById(R.id.stockItemPriceLabel);
            stockItemSharesLabel = itemView.findViewById(R.id.stockItemSharesLabel);
            mainLayout = itemView.findViewById(R.id.stock_item);
        }
    }
}
