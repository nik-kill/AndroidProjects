package com.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.android.inventoryapp.data.StockContract.StockEntry;

public class StockListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STOCK_LOADER = 0;
    StockCursorAdapter stockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(StockListActivity.this, StockDetailActivity.class);
                startActivity(intent);
            }
        });

        final ListView stockListView = (ListView) findViewById(R.id.stock_list_view);
        View emptyView = findViewById(R.id.stock_empty_view);
        stockListView.setEmptyView(emptyView);

        getLoaderManager().initLoader(STOCK_LOADER, null, this);

        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StockListActivity.this, StockDetailActivity.class);
                Uri currentStockUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);
                intent.setData(currentStockUri);

                startActivity(intent);
            }
        });

        stockAdapter = new StockCursorAdapter(this, null);
        stockListView.setAdapter(stockAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_NAME,
                StockEntry.COLUMN_PRICE,
                StockEntry.COLUMN_QUANTITY,
                StockEntry.COLUMN_IMAGE
        };

        return new CursorLoader(this,
                StockEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        stockAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        stockAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_stock_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_save:
                insertStock();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertStock(){
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_NAME, "Pen");
        values.put(StockEntry.COLUMN_PRICE, 150.00);
        values.put(StockEntry.COLUMN_QUANTITY, 10);
        values.put(StockEntry.COLUMN_SUPPLIER_NAME, "Camlin");
        values.put(StockEntry.COLUMN_SUPPLIER_PHONE, "+9999999999");
        values.put(StockEntry.COLUMN_SUPPLIER_EMAIL, "john@supplier.com");
        values.put(StockEntry.COLUMN_IMAGE, "android.resource://com.android.inventoryapp/drawable/pen");

        Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
    }

    public void clickOnSale(long id, int quantity){
        ContentValues values = new ContentValues();
        values.put(StockEntry._ID, id);
        values.put(StockEntry.COLUMN_QUANTITY, quantity);
        String selection = StockEntry._ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(id)};

        if (quantity >= 0) {
            int rowsAffected = getContentResolver().update(StockEntry.CONTENT_URI, values, selection, selectionArgs);
        }
    }

}
