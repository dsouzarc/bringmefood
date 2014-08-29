package com.ryan.bringmefood;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SQLiteOrdersDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "BringMeFoodDB";
    private static final String TABLE_NAME = "OrdersTable";

    private static final String KEY_ID = "id";
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






}
