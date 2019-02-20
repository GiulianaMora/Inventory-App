package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.android.inventoryapp.data.ProductDbHelper;
import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductItem;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class DetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailsActivity.class.getCanonicalName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private ProductDbHelper dbHelper;
    EditText nameEdit;
    EditText priceEdit;
    EditText quantityEdit;
    EditText supplierNameEdit;
    EditText supplierPhoneEdit;
    EditText supplierEmailEdit;
    long currentItemId;
    ImageButton decreaseQuantity;
    ImageButton increaseQuantity;
    Button imageBtn;
    ImageView imageView;
    Uri actualUri;
    private static final int PICK_IMAGE_REQUEST = 0;
    Boolean infoItemHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameEdit = (EditText) findViewById(R.id.product_name_edit);
        priceEdit = (EditText) findViewById(R.id.price_edit);
        quantityEdit = (EditText) findViewById(R.id.quantity_edit);
        supplierNameEdit = (EditText) findViewById(R.id.supplier_name_edit);
        supplierPhoneEdit = (EditText) findViewById(R.id.supplier_phone_edit);
        supplierEmailEdit = (EditText) findViewById(R.id.supplier_email_edit);
        decreaseQuantity = (ImageButton) findViewById(R.id.decrease_quantity);
        increaseQuantity = (ImageButton) findViewById(R.id.increase_quantity);
        imageBtn = (Button) findViewById(R.id.select_image);
        imageView = (ImageView) findViewById(R.id.image_view);

        dbHelper = new ProductDbHelper(this);
        currentItemId = getIntent().getLongExtra("itemId", 0);

        if(currentItemId == 0){
            setTitle(getString(R.string.editor_activity_add_new_item));
        } else {
            setTitle(getString(R.string.editor_activity_edit_item));
            addValuesToEditItem(currentItemId);
        }

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sumOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tryToOpenImageSelector();
                infoItemHasChanged = true;
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (!infoItemHasChanged){
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void subtractOneToQuantity(){
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()){
            return;
        } else if (previousValueString.equals("0")){
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            quantityEdit.setText(String.valueOf(previousValue - 1));
        }
    }

    private void sumOneToQuantity(){
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()){
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        quantityEdit.setText(String.valueOf(previousValue + 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        if(currentItemId == 0){
            MenuItem deleteOneItemMenuItem = menu.findItem(R.id.action_delete_item);
            MenuItem deleteAllMenuItem = menu.findItem(R.id.action_delete_all_data);
            MenuItem orderMenuItem = menu.findItem(R.id.action_order);
            deleteOneItemMenuItem.setVisible(false);
            deleteAllMenuItem.setVisible(false);
            orderMenuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_save:
                if (!addItemToDb()){
                    return true;
                }
                finish();
                return true;
            case android.R.id.home:
                if(!infoItemHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i){
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_order:
                showOrderConfirmationDialog();
                return true;
            case R.id.action_delete_item:
                showDeleteConfirmationDialog(currentItemId);
                return true;
            case R.id.action_delete_all_data:
                showDeleteConfirmationDialog(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addItemToDb(){
        boolean isAllOk = true;
        if (!checkIfValueSet(nameEdit, "name")){
            isAllOk = false;
        }

        if (!checkIfValueSet(priceEdit, "price")){
            isAllOk = false;
        }
        if (!checkIfValueSet(quantityEdit, "quantity")){
            isAllOk = false;
        }
        if (!checkIfValueSet(supplierNameEdit, "supplier name")){
            isAllOk = false;
        }

        if (!checkIfValueSet(supplierPhoneEdit, "supplier phone")){
            isAllOk = false;
        }
        if (!checkIfValueSet(supplierEmailEdit, "supplier email")){
            isAllOk = false;
        }

        if (actualUri == null && currentItemId == 0){
            isAllOk = false;
            imageBtn.setError("Missing image");
        }
        if (!isAllOk){
            return false;
        }

        if (currentItemId == 0){
            ProductItem item = new ProductItem(
                    nameEdit.getText().toString().trim(),
                    priceEdit.getText().toString().trim(),
                    Integer.parseInt(quantityEdit.getText().toString().trim()),
                    supplierNameEdit.getText().toString().trim(),
                    supplierPhoneEdit.getText().toString().trim(),
                    supplierEmailEdit.getText().toString().trim(),
                    actualUri.toString());
            dbHelper.insertItem(item);
        } else {
            int quantity = Integer.parseInt(quantityEdit.getText().toString().trim());
            dbHelper.updateItem(currentItemId, quantity);
        }
        return true;
    }

    private boolean checkIfValueSet(EditText text, String description){
        if (TextUtils.isEmpty(text.getText())){
            text.setError("Missing product " + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }
    private void addValuesToEditItem(long itemId){
        Cursor cursor = dbHelper.readItem(itemId);
        cursor.moveToFirst();
        nameEdit.setText(cursor.getString(cursor.getColumnIndex(ProductContract.StockEntry.COLUMN_NAME)));
        priceEdit.setText(cursor.getString(cursor.getColumnIndex(ProductContract.StockEntry.COLUMN_PRICE)));
        quantityEdit.setText(cursor.getString(cursor.getColumnIndex(ProductContract.StockEntry.COLUMN_QUANTITY)));
        supplierNameEdit.setText(cursor.getString(cursor.getColumnIndex(ProductContract.StockEntry.COLUMN_SUPPLIER_NAME)));
        supplierPhoneEdit.setText(cursor.getString(cursor.getColumnIndex(ProductContract.StockEntry.COLUMN_SUPPLIER_PHONE)));
        supplierEmailEdit.setText(cursor.getString(cursor.getColumnIndex(ProductContract.StockEntry.COLUMN_SUPPLIER_EMAIL)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(ProductContract.StockEntry.COLUMN_IMAGE))));
        nameEdit.setEnabled(false);
        priceEdit.setEnabled(false);
        supplierNameEdit.setEnabled(false);
        supplierPhoneEdit.setEnabled(false);
        supplierEmailEdit.setEnabled(false);
        imageBtn.setEnabled(false);
    }

    private void showOrderConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_message);
        builder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Intent intent = new Intent(Intent.ACTION_DIAL);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
                intent.setData(Uri.parse("PHONE:" + supplierPhoneEdit.getText().toString().trim()));
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private int deleteAllRowsFromTable() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(ProductContract.StockEntry.PATH_PRODUCTS, null, null);
    }

    private int deleteOneItemFromTable(long itemId){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = ProductContract.StockEntry._ID + "=?";
        String[] selectionArgs = { String.valueOf(itemId) };
        int rowsDeleted = database.delete(
                ProductContract.StockEntry.PATH_PRODUCTS, selection, selectionArgs);
        return rowsDeleted;
    }

    private void showDeleteConfirmationDialog(final long itemId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                if (itemId == 0){
                    deleteAllRowsFromTable();
                } else {
                    deleteOneItemFromTable(itemId);
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void tryToOpenImageSelector(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    public void openImageSelector(){
        Intent intent;
        if (Build.VERSION.SDK_INT < 19){
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openImageSelector();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData){
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK){

            if (resultData != null){
                actualUri = resultData.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }
}