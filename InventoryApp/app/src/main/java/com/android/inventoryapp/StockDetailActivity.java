package com.android.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;


import java.io.File;
import com.android.inventoryapp.data.StockContract.StockEntry;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int PICK_PHOTO_REQUEST = 20;
    public static final int EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 21;
    private static final int STOCK_LOADER = 1;

    TextView stockUnitNameEditText;
    TextView stockUnitQuantityEditText;
    TextView stockUnitPriceEditText;
    TextView supplierNameEditText;
    TextView supplierPhoneEditText;
    TextView supplierEmailEditText;
    ImageView productImageView;
    Button addQuantityButton;
    Button addImageButton;
    Button subtractQuantityButton;
    private Uri mCurrentStockUri;
    private boolean mStockHasChanged = false;
    private String mCurrentPhotoUri = "no images";

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent){
            mStockHasChanged = true;
            return false;
        }
    };

    public final static boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        mCurrentStockUri = intent.getData();

        if(mCurrentStockUri == null){
            setTitle(getString(R.string.detail_activity_title_new_stock_unit));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.detail_activity_title_edit_stock_unit));

            getLoaderManager().initLoader(STOCK_LOADER,null, this);
        }

        stockUnitNameEditText = (EditText) findViewById(R.id.stock_unit_name);
        stockUnitQuantityEditText = (EditText) findViewById(R.id.stock_unit_quantity);
        stockUnitPriceEditText = (EditText) findViewById(R.id.stock_unit_price);
        supplierNameEditText = (EditText) findViewById(R.id.supplier_name);
        supplierPhoneEditText = (EditText) findViewById(R.id.supplier_phone_number);
        supplierEmailEditText = (EditText) findViewById(R.id.supplier_email);
        productImageView = (ImageView) findViewById(R.id.product_image_view);
        addQuantityButton = (Button) findViewById(R.id.add_quantity_button);
        subtractQuantityButton = (Button) findViewById(R.id.subtract_quantity_button);
        addImageButton = (Button) findViewById(R.id.add_image_button);

        stockUnitNameEditText.setOnTouchListener(mTouchListener);
        stockUnitQuantityEditText.setOnTouchListener(mTouchListener);
        stockUnitPriceEditText.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierPhoneEditText.setOnTouchListener(mTouchListener);
        supplierEmailEditText.setOnTouchListener(mTouchListener);

        addQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int currentValue = Integer.parseInt(stockUnitQuantityEditText.getText().toString());
                int increasedValue = currentValue + 1;
                stockUnitQuantityEditText.setText(String.valueOf(increasedValue));
            }
        });

        subtractQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue = Integer.parseInt(stockUnitQuantityEditText.getText().toString());
                if (currentValue > 0) {
                    int decreasedValue = currentValue - 1;
                    stockUnitQuantityEditText.setText(String.valueOf(decreasedValue));
                }
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhotoProductUpdate(v);
            }
        });
    }

    private void saveStockUnit(){
        String name = stockUnitNameEditText.getText().toString().trim();
        int quantity = Integer.parseInt(stockUnitQuantityEditText.getText().toString().trim());
        float price = Float.parseFloat(stockUnitPriceEditText.getText().toString().trim());
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();
        String supplierEmail = supplierEmailEditText.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            stockUnitNameEditText.setError("The product name cannot be blank");
        } else if (price < 0.00) {
            stockUnitPriceEditText.setError("The Stock Unit Price Cannot be less than Zero");
        } else if (TextUtils.isEmpty(supplierName)) {
            supplierNameEditText.setError("The Supplier Name cannot be blank");
        } else if (TextUtils.isEmpty(supplierPhone)) {
            supplierPhoneEditText.setError("The Supplier Phone cannot be blank");
        } else if (TextUtils.isEmpty(supplierEmail)) {
            supplierEmailEditText.setError("Please Enter a Supplier's Email Id");
        } else if (!isValidEmail(supplierEmail)) {
            supplierEmailEditText.setError("Please Enter a Valid Email Id");
        } else {
            ContentValues values = new ContentValues();
            values.put(StockEntry.COLUMN_NAME, name);
            values.put(StockEntry.COLUMN_QUANTITY, quantity);
            values.put(StockEntry.COLUMN_PRICE, price);
            values.put(StockEntry.COLUMN_SUPPLIER_NAME, supplierName);
            values.put(StockEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
            values.put(StockEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);
            values.put(StockEntry.COLUMN_IMAGE, mCurrentPhotoUri);

            if(mCurrentStockUri == null){
                Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);

                if(newUri == null){
                    Toast.makeText(this, getString(R.string.editor_insert_stock_unit_failed),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_stock_unit_successful), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentStockUri, values, null,null);
                if(rowsAffected==0){
                    Toast.makeText(this, getString(R.string.editor_update_stock_unit_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_stock_unit_successful), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_stock_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_save:
                saveStockUnit();
                break;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                break;
            case R.id.action_order_more:
                orderMore();
                break;
            case android.R.id.home:
                if(!mStockHasChanged){
                    NavUtils.navigateUpFromSameTask(StockDetailActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(StockDetailActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPhotoProductUpdate(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //We are on M or above so we need to ask for runtime permissions
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                invokeGetPhoto();
            } else {
                // we are here if we do not all ready have permissions
                String[] permisionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permisionRequest, EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
            }
        } else {
            //We are on an older devices so we dont have to ask for runtime permissions
            invokeGetPhoto();
        }
    }
    private void invokeGetPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);

        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            invokeGetPhoto();
        } else {
            Toast.makeText(this, R.string.err_external_storage_permissions, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri mProductPhotoUri = data.getData();
                mCurrentPhotoUri = mProductPhotoUri.toString();
                //Log.d(TAG, "Selected images " + mProductPhotoUri);

                productImageView.setImageURI(Uri.parse(mCurrentPhotoUri));
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentStockUri == null) {
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
            deleteMenuItem.setVisible(false);
            MenuItem orderMoreMenuItem = menu.findItem(R.id.action_order_more);
            orderMoreMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_NAME,
                StockEntry.COLUMN_PRICE,
                StockEntry.COLUMN_QUANTITY,
                StockEntry.COLUMN_SUPPLIER_NAME,
                StockEntry.COLUMN_SUPPLIER_PHONE,
                StockEntry.COLUMN_SUPPLIER_EMAIL,
                StockEntry.COLUMN_IMAGE};

        return new CursorLoader(this,
                mCurrentStockUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_PHONE);
            int supplierEmailColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_EMAIL);
            int productImageColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_IMAGE);

            String stock_name = cursor.getString(nameColumnIndex);
            float stock_price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier_name = cursor.getString(supplierNameColumnIndex);
            String supplier_phone = cursor.getString(supplierPhoneColumnIndex);
            String supplier_email = cursor.getString(supplierEmailColumnIndex);
            String stock_image = cursor.getString(productImageColumnIndex);
            mCurrentPhotoUri = stock_image;

            stockUnitNameEditText.setText(stock_name);
            stockUnitQuantityEditText.setText(String.valueOf(quantity));
            stockUnitPriceEditText.setText(String.valueOf(stock_price));
            supplierNameEditText.setText(supplier_name);
            supplierPhoneEditText.setText(supplier_phone);
            supplierEmailEditText.setText(supplier_email);

            if (TextUtils.equals(stock_image, getString(R.string.no_image))) {
                productImageView.setImageURI(Uri.parse(getString(R.string.no_image_url)));
            } else {
                productImageView.setImageURI(Uri.parse(stock_image));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        stockUnitNameEditText.setText("");
        stockUnitQuantityEditText.setText("");
        stockUnitPriceEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
        supplierEmailEditText.setText("");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteStockUnit();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mStockHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void deleteStockUnit() {
        if (mCurrentStockUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentStockUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_stock_unit_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_stock_unit_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void orderMore() {
        String stockUnitName = stockUnitNameEditText.getText().toString();
        String supplierEmail = supplierEmailEditText.getText().toString();
        String mailText = getString(R.string.mailText);
        composeEmail(new String[]{supplierEmail}, "Order for more" + stockUnitName, mailText);
    }
    public void composeEmail(String[] addresses, String subject, String mailText) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, mailText);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }




}
