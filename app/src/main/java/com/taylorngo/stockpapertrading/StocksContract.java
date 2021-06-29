// Taylor Ngo
// 112626118
package com.taylorngo.stockpapertrading;
import android.provider.BaseColumns;

/**
 * This class implements the column names in the database.
 *
 * @author Taylor Ngo
 */
public class StocksContract {

    /**
     * Private constructor so this class cannot be instantiated.
     */
    private StocksContract(){}

    public static final class StockEntry implements BaseColumns {
        public static final String TABLE_NAME = "stocksList";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_COST = "cost";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
