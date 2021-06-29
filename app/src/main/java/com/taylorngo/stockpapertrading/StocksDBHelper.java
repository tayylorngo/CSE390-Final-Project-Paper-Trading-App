// Taylor Ngo
// 112626118
package com.taylorngo.stockpapertrading;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.taylorngo.stockpapertrading.StocksContract.*;

/**
 * The StocksDBHelper class implements the database outline
 * to store asset information such as name, amount, cost, and etc.
 */
public class StocksDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "stockslist.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * Constructor to create the StocksDBHelper
     * @param context Context
     */
    public StocksDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method creates the database table to store asset information.
     * @param db The database to create the table in.
     */
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

    /**
     * Method used to update the database when needed.
     * @param db Database to be updated
     * @param oldVersion old version number of the database
     * @param newVersion new version number of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StockEntry.TABLE_NAME);
        onCreate(db);
    }
}
