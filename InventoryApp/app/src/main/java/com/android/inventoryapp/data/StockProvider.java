package com.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.inventoryapp.data.StockContract.StockEntry;

public class StockProvider extends ContentProvider {

    public static final String LOG_TAG = StockProvider.class.getSimpleName();

    private static final int STOCK = 100;

    private static final int STOCK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_STOCK, STOCK);

        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_STOCK + "/#", STOCK_ID);
    }

    private StockDbHelper stockDbHelper;

    @Override
    public boolean onCreate() {
        stockDbHelper = new StockDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = stockDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case STOCK_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return StockEntry.CONTENT_LIST_TYPE;
            case STOCK_ID:
                return StockEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return insertStockEntry(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertStockEntry(Uri uri, ContentValues values) {
        String name = values.getAsString(StockEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Stock Unit requires a name");
        }
        float price = values.getAsFloat(StockEntry.COLUMN_PRICE);
        if (price < 0) {
            throw new IllegalArgumentException("Stock Unit requires a price");
        }
        int quantity = values.getAsInteger(StockEntry.COLUMN_QUANTITY);
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock Unit requires a quantity");
        }
        String supplierName = values.getAsString(StockEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Supplier Name is required");
        }
        String supplierEmail = values.getAsString(StockEntry.COLUMN_SUPPLIER_EMAIL);
        if (supplierEmail == null) {
            throw new IllegalArgumentException("Supplier Email is required");
        }
        String supplierPhone = values.getAsString(StockEntry.COLUMN_SUPPLIER_PHONE);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("Supplier Phone is required");
        }

        SQLiteDatabase database = stockDbHelper.getWritableDatabase();

        long id = database.insert(StockEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = stockDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return updateStock(uri, contentValues, selection, selectionArgs);
            case STOCK_ID:
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateStock(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateStock(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(StockEntry.COLUMN_NAME)) {
            String name = values.getAsString(StockEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Stock Unit requires a name");
            }
        }
        if (values.containsKey(StockEntry.COLUMN_PRICE)) {
            float price = values.getAsFloat(StockEntry.COLUMN_PRICE);
            if (price == 0) {
                throw new IllegalArgumentException("Stock Unit requires a price");
            }
        }
        if (values.containsKey(StockEntry.COLUMN_QUANTITY)) {
            int quantity = values.getAsInteger(StockEntry.COLUMN_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException("Stock Unit requires a quantity to be positive");
            }
        }
        if (values.containsKey(StockEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(StockEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Supplier Name is required");
            }
        }
        if (values.containsKey(StockEntry.COLUMN_SUPPLIER_EMAIL)) {
            String supplierEmail = values.getAsString(StockEntry.COLUMN_SUPPLIER_EMAIL);
            if (supplierEmail == null) {
                throw new IllegalArgumentException("Supplier Email is required");
            }
        }
        if (values.containsKey(StockEntry.COLUMN_SUPPLIER_PHONE)) {
            String supplierPhone = values.getAsString(StockEntry.COLUMN_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Supplier Phone is required");
            }
        }
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = stockDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(StockEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


}
