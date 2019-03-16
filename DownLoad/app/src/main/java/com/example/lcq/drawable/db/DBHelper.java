package com.example.lcq.drawable.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


/**
 * 创建数据库帮助类

 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "download.db";
    public  static final int VERSION = 1;
    private static DBHelper helper = null; //静态对象引用
    public static final String SQL_CREATE = "create table thread_info(_id integer primary key autoincrement," +
            "thread_id integer,url text,start integer,end integer,finished integer)";
    public static final String SQL_DROP = "drop table if exists thread_info";

    private DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /*
    获取类的对象
     */

    public static DBHelper getInstance(Context context) {
        if(helper == null) {
            helper = new DBHelper((context));
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}
