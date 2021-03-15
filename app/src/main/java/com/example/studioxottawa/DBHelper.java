package com.example.studioxottawa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBnotif.db";
    public static final String NOTIFTAB_TABLE_NAME = "notiftab";
    public static final String NOTIFTAB_COLUMN_ID = "id";
    public static final String NOTIFTAB_COLUMN_UNAME = "uname";
    public static final String NOTIFTAB_COLUMN_LESSON = "lesson";
    public static final String NOTIFTAB_COLUMN_APTMENT = "aptment";
    public static final String NOTIFTAB_COLUMN_DATE = "date";
    public static final String NOTIFTAB_COLUMN_TIME = "time";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table notiftab " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, uname text, lesson text, aptment text, date text, time text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS notiftab");
        onCreate(db);
    }

    public boolean insertNotif (String uname, String lesson, String aptment, String date,String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uname", uname);
        contentValues.put("lesson", lesson);
        contentValues.put("aptment", aptment);
        contentValues.put("date", date);
        contentValues.put("time", time);
        db.insert("notiftab", null, contentValues);
        return true;
    }

    //public Cursor getData(String uname) {
     public ArrayList<String> getData(String uname) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notiftab where uname="+ "'" +uname+"'"+"", null );

        ArrayList<String> array_list = new ArrayList<String>();
            res.moveToFirst();
            String str = "";
            String str_lesson = "";
            String str_apt = "";
            String str_date = "";
            String str_time = "";


            while(res.isAfterLast() == false){
                str_lesson = "";
                str_apt = "";

                str_lesson = res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_LESSON));
                str_apt = res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_APTMENT));
                str_date = res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_DATE));
                str_time = res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_TIME));

               if  (str_lesson.equals("")) {
                   str = str_apt + "   " + str_date + "   " + str_time;
                } else {
                    str = str_lesson + "        " + str_date + "   " + str_time;
                }

                array_list.add(str);

                res.moveToNext();
            }
        //return res;
            return array_list;
    }

      //return the cursor is pointing to the uname rows
      public Cursor getcursor(String uname) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notiftab where uname="+ "'" +uname+"'"+"", null );

        return res;
    }

}
