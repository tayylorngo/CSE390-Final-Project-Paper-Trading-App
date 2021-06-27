package com.taylorngo.stockpapertrading;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.taylorngo.stockpapertrading.StocksContract.*;

import androidx.annotation.Nullable;

public class StocksDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "stockslist.db";
    public static final int DATABASE_VERSION = 1;

    public StocksDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQLITE_CREATE_STOCKSLIST_TABLE = "CREATE TABLE " +
                StockEntry.TABLE_NAME + " (" +
                StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StockEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                StockEntry.COLUMN_AMOUNT + " DOUBLE NOT NULL, " +
                StockEntry.COLUMN_COST + " DOUBLE NOT NULL, " +
                StockEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQLITE_CREATE_STOCKSLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StockEntry.TABLE_NAME);
        onCreate(db);
    }
}
