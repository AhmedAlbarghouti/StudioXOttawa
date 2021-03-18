package com.example.studioxottawa.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studioxottawa.welcome.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//public class DBHelper extends SQLiteOpenHelper {
    public class DBHelper  {
    ArrayList<String> array_list = new ArrayList<String>();
        public String username2 = "";
//    public static final String DATABASE_NAME = "MyDBnotif.db";
//    public static final String NOTIFTAB_TABLE_NAME = "notiftab2";
//    public static final String NOTIFTAB_COLUMN_ID = "id";
//    public static final String NOTIFTAB_COLUMN_UNAME = "uname";
//    public static final String NOTIFTAB_COLUMN_LESSON = "lesson";
//    public static final String NOTIFTAB_COLUMN_APTMENT = "aptment";
//    public static final String NOTIFTAB_COLUMN_DATE = "date";
//    public static final String NOTIFTAB_COLUMN_TIME = "time";
//    private HashMap hp;

//    public DBHelper(Context context) {
//
//        super(context, DATABASE_NAME , null, 2);
//    }
public DBHelper () {

}

//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        // TODO Auto-generated method stub
//        db.execSQL(
//                "create table notiftab2 " +
//                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, uname text, lesson text, aptment text, date text, time text)"
//        );
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // TODO Auto-generated method stub
//        db.execSQL("DROP TABLE IF EXISTS notiftab2");
//        onCreate(db);
//    }
//
//   public void droptable() {
//       SQLiteDatabase sdb;
//       sdb= this.getWritableDatabase();
//       sdb.execSQL("DROP TABLE IF EXISTS notiftab2");
//   }
//
//    public boolean insertNotif (String uname, String lesson, String aptment, String date,String time) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("uname", uname);
//        contentValues.put("lesson", lesson);
//        contentValues.put("aptment", aptment);
//        contentValues.put("date", date);
//        contentValues.put("time", time);
//        db.insert("notiftab2", null, contentValues);
//        return true;
//    }
//
//        public Integer deleteNotif (String uname) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete("notiftab2",
//                "uname = ?" ,
//                new String[] { uname });
//    }
//
//
//    public ArrayList<String> getAllnotifs() {
//        ArrayList<String> array_list = new ArrayList<String>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from notiftab2", null );
//        res.moveToFirst();
//       String str = "";
//        while(res.isAfterLast() == false){
//
//            str = //res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_UNAME));
//            res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_LESSON)) + " " +
//            res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_APTMENT)) + " " +
//            res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_DATE)) + " " +
//            res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_TIME));
//
//            array_list.add(str);
//            res.moveToNext();
//        }
//        return array_list;
//    }
//
//
//    //public Cursor getData(String uname) {
//     public ArrayList<String> getData(String uname) {
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from notiftab2 where uname="+ "'" +uname+"'"+"", null );
//
//        ArrayList<String> array_list = new ArrayList<String>();
//            res.moveToFirst();
//            String str = "";
//            String str_lesson = "";
//            String str_apt = "";
//            String str_date = "";
//            String str_time = "";
//
//
//            while(res.isAfterLast() == false){
//                str_lesson = "";
//                str_apt = "";
//
//                str_lesson = res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_LESSON));
//                str_apt = res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_APTMENT));
//                str_date = res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_DATE));
//                str_time = res.getString(res.getColumnIndex(NOTIFTAB_COLUMN_TIME));
//
//               if  (str_lesson.equals("")) {
//                   str = str_apt + "   " + str_date + "   " + str_time;
//                } else {
//                    str = str_lesson + "        " + str_date + "   " + str_time;
//                }
//
//                array_list.add(str);
//
//                res.moveToNext();
//            }
//        //return res;
//            return array_list;
//    }
//
//      //return the cursor is pointing to the uname rows
//      public Cursor getcursor(String uname) {
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from notiftab2 where uname="+ "'" +uname+"'"+"", null );
//
//        return res;
//    }

    public String get_username() {



        //FirebaseUser user;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User signInUser = snapshot.getValue(User.class);
                String username = signInUser.fullName;
                username2 = username;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        });


        return username2;


    }

    public ArrayList<String> get_firebase_data() {




        FirebaseUser user;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        //String o = user.getDisplayName();


        userRef.child(uid).child("Events Purchased").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String name = String.valueOf(ds.child("name").getValue());
                    String date = String.valueOf(ds.child("date").getValue());
                    String time = String.valueOf(ds.child("time").getValue());
                    String staff = String.valueOf(ds.child("staff").getValue());
                    String uid = String.valueOf(ds.child("uid").getValue());

                    //mydb.insertNotif(username,name,"Yes",date,time);
                   String str = "";
                    str = name + "," + date+ "," + time;
                    array_list.add(str);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return array_list;
    }





}
