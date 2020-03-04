package com.faresa.qrcodebuku.QRCode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.faresa.qrcodebuku.QRCode.adapter.Code;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "scan_barcode.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    static{
        // register our models
        cupboard().register(Code.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //ensure that all tables are created
        cupboard().withDatabase(db).createTables();
        //add index and other database tweaks in this method if you want
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // upgrade tables, adding columns and new tables.
        // -> that existing columns will not converted
        cupboard().withDatabase(db).upgradeTables();
        // do migration work if you have an alternation to make to your schema here

    }
}
