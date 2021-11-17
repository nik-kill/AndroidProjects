package com.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.inventoryapp.data.StockContract.StockEntry;
public class StockCursorAdapter extends CursorAdapter {
    private final StockListActivity stockListActivity;

    public StockCursorAdapter(StockListActivity context, Cursor c){
        super(context, c, 0);
        this.stockListActivity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.single_stock_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor){
        TextView stock_name_tv = (TextView) view.findViewById(R.id.stock_unit_name);
        TextView stock_quantity_tv = (TextView) view.findViewById(R.id.stock_unit_quantity);
        TextView stock_unit_price = (TextView) view.findViewById(R.id.stock_unit_price);
        ImageView stock_unit_image_view = (ImageView) view.findViewById(R.id.stock_image);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        int idColumnIndex = cursor.getColumnIndex(StockEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_IMAGE);

        final int stockId = cursor.getInt(idColumnIndex);
        String stockUnitName = cursor.getString(nameColumnIndex);
        float stockPrice = cursor.getFloat(priceColumnIndex);
        final int stockQuantity = cursor.getInt(quantityColumnIndex);
        String stockImageUri = cursor.getString(imageColumnIndex);

        stock_name_tv.setText(stockUnitName);
        stock_quantity_tv.setText(String.valueOf(stockQuantity));
        stock_unit_price.setText(String.valueOf(stockPrice));
        if(!TextUtils.equals(stockImageUri, stockListActivity.getString(R.string.no_image))){
            stock_unit_image_view.setImageURI(Uri.parse(stockImageUri));
        } else {
            stock_unit_image_view.setImageURI(Uri.parse(stockListActivity.getString(R.string.no_image_url)));
        }

        saleButton.setOnClickListener(new View.OnClickListener() {
            int updatedQuantity = stockQuantity - 1;

            @Override
            public void onClick(View v) {
                stockListActivity.clickOnSale(stockId,
                        updatedQuantity);
            }
        });
    }
}
