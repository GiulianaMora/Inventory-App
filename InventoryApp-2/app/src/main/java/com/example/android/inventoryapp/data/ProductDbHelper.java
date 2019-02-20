package com.example.android.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDbHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "inventory.db";
    public final static int DB_VERSION = 1;
    public final static String LOG_TAG = ProductDbHelper.class.getCanonicalName();

    public ProductDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(ProductContract.StockEntry.CREATE_TABLE_STOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    public void insertItem(ProductItem item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductContract.StockEntry.COLUMN_NAME, item.getProductName());
        values.put(ProductContract.StockEntry.COLUMN_PRICE, item.getPrice());
        values.put(ProductContract.StockEntry.COLUMN_QUANTITY, item.getQuantity());
        values.put(ProductContract.StockEntry.COLUMN_SUPPLIER_NAME, item.getSupplierName());
        values.put(ProductContract.StockEntry.COLUMN_SUPPLIER_PHONE, item.getSupplierPhone());
        values.put(ProductContract.StockEntry.COLUMN_SUPPLIER_EMAIL, item.getSupplierEmail());
        values.put(ProductContract.StockEntry.COLUMN_IMAGE, item.getImage());
        long id = db.insert(ProductContract.StockEntry.PATH_PRODUCTS, null, values);
    }

    public Cursor readStock(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ProductContract.StockEntry._ID,
                ProductContract.StockEntry.COLUMN_NAME,
                ProductContract.StockEntry.COLUMN_PRICE,
                ProductContract.StockEntry.COLUMN_QUANTITY,
                ProductContract.StockEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.StockEntry.COLUMN_SUPPLIER_PHONE,
                ProductContract.StockEntry.COLUMN_SUPPLIER_EMAIL,
                ProductContract.StockEntry.COLUMN_IMAGE
        };

        Cursor cursor = db.query(
                ProductContract.StockEntry.PATH_PRODUCTS,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readItem(long itemId){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ProductContract.StockEntry._ID,
                ProductContract.StockEntry.COLUMN_NAME,
                ProductContract.StockEntry.COLUMN_PRICE,
                ProductContract.StockEntry.COLUMN_QUANTITY,
                ProductContract.StockEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.StockEntry.COLUMN_SUPPLIER_PHONE,
                ProductContract.StockEntry.COLUMN_SUPPLIER_EMAIL,
                ProductContract.StockEntry.COLUMN_IMAGE
        };
        String selection = ProductContract.StockEntry._ID + "=?";
        String [] selectionArgs = new String[] { String.valueOf(itemId)};

        Cursor cursor = db.query(
                ProductContract.StockEntry.PATH_PRODUCTS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public void updateItem(long currentItemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductContract.StockEntry.COLUMN_QUANTITY, quantity);
        String selection = ProductContract.StockEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(currentItemId) };
        db.update(ProductContract.StockEntry.PATH_PRODUCTS,
                values, selection, selectionArgs);
    }
    public void sellOneItem(long itemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        int newQuantity = 0;
        if (quantity > 0) {
            newQuantity = quantity -1;
        }
        ContentValues values = new ContentValues();
        values.put(ProductContract.StockEntry.COLUMN_QUANTITY, newQuantity);
        String selection = ProductContract.StockEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(itemId) };
        db.update(ProductContract.StockEntry.PATH_PRODUCTS,
                values, selection, selectionArgs);
    }
}