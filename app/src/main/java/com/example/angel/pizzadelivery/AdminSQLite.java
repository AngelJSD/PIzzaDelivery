package com.example.angel.pizzadelivery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLite extends SQLiteOpenHelper {



    public AdminSQLite (Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL("create table 'order'(dni integer, name text, pizza text, number integer, lat text, lng text)");
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int version1, int version2){

        String tableName = "order";
        db.execSQL("DROP TABLE IF EXISTS 'order'");
        onCreate(db);
    }
}
