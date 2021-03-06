package com.example.damiangarcia.tareasettings.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.damiangarcia.tareasettings.Beans.Category;
import com.example.damiangarcia.tareasettings.Beans.ItemProduct;
import com.example.damiangarcia.tareasettings.Beans.Store;

import java.util.ArrayList;

public class ItemProductControl {
    public long addItemProduct(ItemProduct product, DataBaseHandler dh){
        long inserted = 0;
        SQLiteDatabase db = dh.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put(DataBaseHandler.KEY_PRODUCT_ID, product.getCode());
        values.put(DataBaseHandler.KEY_PRODUCT_CATEGORY, product.getCategory().getIdCategory());
        values.put(DataBaseHandler.KEY_PRODUCT_DESCRIPTION, product.getDescription());
        values.put(DataBaseHandler.KEY_PRODUCT_IMAGE, product.getImage());
        values.put(DataBaseHandler.KEY_PRODUCT_STORE, product.getStore().getId());
        values.put(DataBaseHandler.KEY_PRODUCT_TITLE, product.getTitle());

        // Inserting Row
        inserted = db.insert(DataBaseHandler.TABLE_PRODUCT, null, values);
        // Closing database connection
        try {db.close();} catch (Exception e) {}
        db = null; values = null;
        return inserted;
    }

    public int updateProduct(ItemProduct product, DataBaseHandler dh){
        SQLiteDatabase db = dh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataBaseHandler.KEY_PRODUCT_ID, product.getCode());
        values.put(DataBaseHandler.KEY_PRODUCT_CATEGORY, product.getCategory().getIdCategory());
        values.put(DataBaseHandler.KEY_PRODUCT_DESCRIPTION, product.getDescription());
        values.put(DataBaseHandler.KEY_PRODUCT_IMAGE, product.getImage());
        values.put(DataBaseHandler.KEY_PRODUCT_STORE, product.getStore().getId());
        values.put(DataBaseHandler.KEY_PRODUCT_TITLE, product.getTitle());
        // Updating row
        int count = db.update(DataBaseHandler.TABLE_PRODUCT, values,
                DataBaseHandler.KEY_PRODUCT_ID + " = ?",
                new String[] { String.valueOf(product.getCode()) });

        try { db.close();} catch (Exception e) {}
        db = null;
        return count;

    }

    public void deleteProduct(int idProduct, DataBaseHandler dh){
        SQLiteDatabase db = dh.getWritableDatabase();
        db.delete(DataBaseHandler.TABLE_PRODUCT, DataBaseHandler.KEY_PRODUCT_ID
                + " = ?", new String[] { String.valueOf(idProduct) });
        try {
            db.close();
        } catch (Exception e) {
        }
        db = null;
    }

    public ItemProduct getProductById(int idProduct, DataBaseHandler dh){
        ItemProduct itemProduct =  null;
        String selectQuery = "SELECT S."+DataBaseHandler.KEY_PRODUCT_ID+","
                + "S."+DataBaseHandler.KEY_PRODUCT_CATEGORY + ","
                + "S."+DataBaseHandler.KEY_PRODUCT_DESCRIPTION + ","
                + "S."+DataBaseHandler.KEY_PRODUCT_TITLE + ","
                + "S."+DataBaseHandler.KEY_PRODUCT_IMAGE + ", "
                + "C."+DataBaseHandler.KEY_CATEGORY_NAME + ", "
                + "S."+DataBaseHandler.KEY_PRODUCT_STORE + " "
                + " FROM "
                + DataBaseHandler.TABLE_PRODUCT + " S, "
                + DataBaseHandler.TABLE_CATEGORY + " C "
                + "WHERE S."+DataBaseHandler.KEY_PRODUCT_ID
                + " = " + idProduct + " AND "
                + "C."+ DataBaseHandler.KEY_CATEGORY_ID + " = " + "S."+ DataBaseHandler.KEY_PRODUCT_CATEGORY;

        SQLiteDatabase db = dh.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Category category = new Category();

        int idStore = 0 ;

        if(cursor.moveToFirst()){
            itemProduct = new ItemProduct();
            itemProduct.setCode(cursor.getInt(0));
            category.setIdCategory(cursor.getInt(1));
            itemProduct.setDescription(cursor.getString(2));
            itemProduct.setTitle(cursor.getString(3));
            itemProduct.setImage(cursor.getInt(4));
            category.setName(cursor.getString(5));
            idStore = cursor.getInt(6);

            itemProduct.setCategory(category);
        }

        if (itemProduct == null)
            return null;

        try {cursor.close();;
        } catch (Exception e) {
            Log.e("BASE", e.toString());}

        //Category c =
        StoreControl sc = new StoreControl();
        Store store = sc.getStoreById(idStore,dh);
        itemProduct.setStore(store);

        return itemProduct;
    }

    public ArrayList<ItemProduct> getProductsWhere(
            String strWhere, String strOrderBy, DataBaseHandler dh){

        ArrayList<ItemProduct> items = new ArrayList<>();
        ArrayList<Integer> stores = new ArrayList<>();

        String selectQuery = "SELECT S."+DataBaseHandler.KEY_PRODUCT_ID+","
                + "S."+DataBaseHandler.KEY_PRODUCT_CATEGORY + ","
                + "S."+DataBaseHandler.KEY_PRODUCT_DESCRIPTION + ","
                + "S."+DataBaseHandler.KEY_PRODUCT_TITLE + ","
                + "S."+DataBaseHandler.KEY_PRODUCT_IMAGE + ", "
                + "C."+DataBaseHandler.KEY_CATEGORY_NAME + ", "
                + "S."+DataBaseHandler.KEY_PRODUCT_STORE + " "
                + " FROM "
                + DataBaseHandler.TABLE_PRODUCT + " S, "
                + DataBaseHandler.TABLE_CATEGORY + " C "
                + " WHERE "
                + "C."+ DataBaseHandler.KEY_CATEGORY_ID + " = " + "S."+ DataBaseHandler.KEY_PRODUCT_CATEGORY
                + ((strWhere != null)? ( " AND " + strWhere ) : "");

        SQLiteDatabase db = dh.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);
        }catch (Exception e){
            Log.e("SQL", e.toString());
        }

        Category category = new Category();

        int idStore = 0;

        while(cursor.moveToNext()){
            ItemProduct itemProduct =  new ItemProduct();

            itemProduct.setCode(cursor.getInt(0));
            category.setIdCategory(cursor.getInt(1));
            itemProduct.setDescription(cursor.getString(2));
            itemProduct.setTitle(cursor.getString(3));
            itemProduct.setImage(cursor.getInt(4));
            category.setName(cursor.getString(5));
            idStore = cursor.getInt(6);

            itemProduct.setCategory(category);

            items.add(itemProduct);
            stores.add(idStore);
        }

        try {cursor.close();;
        } catch (Exception e) {
            Log.e("BASE", e.toString());}

        StoreControl sc = new StoreControl();

        for(int i = 0 ; i< stores.size(); i++ ){
            Store s = sc.getStoreById(stores.get(i),dh);
            items.get(i).setStore(s);
        }

        return items;
    }
}
