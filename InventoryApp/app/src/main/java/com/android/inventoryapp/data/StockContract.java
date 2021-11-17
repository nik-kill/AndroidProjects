package com.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class StockContract implements BaseColumns {
    public static final String CONTENT_AUTHORITY = "com.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_STOCK = "stock";

    public StockContract() {}

    public static final class StockEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STOCK);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;

        public static final String TABLE_NAME = "stock";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_IMAGE = "image";

        public static final String CREATE_TABLE_STOCK = "CREATE TABLE " +
                StockContract.StockEntry.TABLE_NAME + "(" +
                StockContract.StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StockContract.StockEntry.COLUMN_NAME + " TEXT NOT NULL," +
                StockContract.StockEntry.COLUMN_PRICE + " TEXT NOT NULL," +
                StockContract.StockEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                StockContract.StockEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                StockContract.StockEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL," +
                StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL," +
                StockEntry.COLUMN_IMAGE + " TEXT NOT NULL" + ");";

    }

}
