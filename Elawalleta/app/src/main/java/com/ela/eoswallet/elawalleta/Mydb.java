package com.ela.eoswallet.elawalleta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class Mydb extends SQLiteOpenHelper {
    public static final String CREATE_MESSAGELIST="create table messagelist ("
            + "id integer primary key autoincrement,"
            + "sender text, "
            + "curtime datetime, "
            + "content text, "
            + "yn integer,"
            + "reciver text)";

    public static final String CREATE_FIRENDLIST="create table firendlist("
            + "id integer primary key autoincrement, "
            + "userid text, "
            + "nickname text)";

    public static final String CREATE_NEWFIRENDLIST="create table newfirendlist("
            + "id integer primary key autoincrement,"
            + "userid text,"
            + "yn integer,"
            + "hello text,"
            + "nickname text)";

    private Context context;

    public Mydb(Context context, String name,
                            CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_MESSAGELIST);
        db.execSQL(CREATE_FIRENDLIST);
        db.execSQL(CREATE_NEWFIRENDLIST);
       Toast.makeText(context, "create succeeded",
               Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("drop table if exists messagelist");
        db.execSQL("drop table if exists firendlist");
        db.execSQL("drop table if exists newfirendlist");
        onCreate(db);
    }

}
