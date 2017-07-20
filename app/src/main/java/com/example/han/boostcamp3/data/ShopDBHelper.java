package com.example.han.boostcamp3.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.han.boostcamp3.data.ShopContract;
/**
 * Created by Han on 2017-07-19.
 */

public class ShopDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shop.db";

    private static final int DATABASE_VERSION = 2;

    public ShopDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_SHOP_TABLE_CREATE = "CREATE TABLE " + ShopContract.ShopEntry.TABLE_NAME + " (" +

                ShopContract.ShopEntry._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                ShopContract.ShopEntry.SHOP_TITLE + " VARCHAR(255) NOT NULL, " +

                ShopContract.ShopEntry.SHOP_ADDRESS + " VARCHAR(255) NOT NULL, "  +

                ShopContract.ShopEntry.SHOP_LAT + " FLOAT NOT NULL, "+

                ShopContract.ShopEntry.SHOP_LNG + " FLOAT NOT NULL, "+

                ShopContract.ShopEntry.SHOP_PHONE + " VARCHAR(255) NOT NULL, " +
                ShopContract.ShopEntry.SHOP_CONTENT + " VARCHAR(300) NOT NULL);";


        db.execSQL(SQL_SHOP_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ShopContract.ShopEntry.TABLE_NAME);
        onCreate(db);
    }
}
