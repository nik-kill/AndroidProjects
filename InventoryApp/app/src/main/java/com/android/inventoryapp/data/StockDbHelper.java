package com.android.inventoryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StockDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = StockDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "stock.db";
    private static final int DATABASE_VERSION = 1;

    public StockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StockContract.StockEntry.CREATE_TABLE_STOCK);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getAllStock() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                StockContract.StockEntry._ID,
                StockContract.StockEntry.COLUMN_NAME,
                StockContract.StockEntry.COLUMN_PRICE,
                StockContract.StockEntry.COLUMN_QUANTITY,
                StockContract.StockEntry.COLUMN_SUPPLIER_NAME,
                StockContract.StockEntry.COLUMN_SUPPLIER_PHONE,
                StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL,
                StockContract.StockEntry.COLUMN_IMAGE
        };
        return db.query(
                StockContract.StockEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }

}
