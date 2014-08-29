package com.ryan.bringmefood;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class SQLiteOrdersDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "BringMeFoodDB";
    private static final String TABLE_NAME = "OrdersTable";

    private static final String KEY_ID = "ORDER_ID";
    private static final String KEY_ORDER = "orderAsJSON";

    private static final String[] COLUMNS = {KEY_ID, KEY_ORDER};

    public SQLiteOrdersDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase theDB) {
        final String CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ID + " INTEGER, "+
                KEY_ORDER + " STRING )";
        theDB.execSQL(CREATE_ORDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void addOrder(final Order theOrder) {
        final SQLiteDatabase theDB = this.getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(KEY_ID, Integer.parseInt(theOrder.getIdNumber()));
        values.put(KEY_ORDER, theOrder.toJSONObject().toString());

        theDB.insert(TABLE_NAME, null, values);
        theDB.close();
    }

    public ArrayList<Order> getAllOrders() {
        final LinkedList<Order> allOrders = new LinkedList<Order>();

        final String query = "SELECT * FROM " + TABLE_NAME;
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    allOrders.add(Order.getOrder(new JSONObject(cursor.getString(2))));
                } catch (Exception e) {
                    e.printStackTrace();
                    log("Adding order: " + cursor.getString(1) + ": " + cursor.getString(2));
                }
            } while (cursor.moveToNext());
        }

        db.close();

        return new ArrayList<Order>(allOrders);
    }

    public void deleteOrder(final String orderID) {
        final SQLiteDatabase theDB = this.getWritableDatabase();

        try {
            theDB.delete(TABLE_NAME, KEY_ID + " = ?", new String[]{orderID});
        }
        catch (Exception e) {
            log("Failed to delete: " + orderID);
        }
        theDB.close();
    }

    public void deleteAllOrders() {
        final SQLiteDatabase theDB = this.getWritableDatabase();
        theDB.delete(TABLE_NAME, null, null);
        theDB.close();
    }

    public void log(final String message) {
        Log.e("com.ryan.bringmefood", message);
    }
}
