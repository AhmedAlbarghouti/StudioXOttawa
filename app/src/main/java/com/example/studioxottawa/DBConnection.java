package com.example.studioxottawa;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBConnection {
    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
//    private static final String DBURL = "jdbc:mysql://192.168.0.60:3306/testdb?useSSL=FALSE";
    private static final String DBURL = "jdbc:mysql://192.168.0.30:3306/studioxottawa";
    private static final String DBUSER = "admintest";
    private static final String DBPASSWORD = "admintest123";

    public static User selectMysql(String usern, String pass) {
        Log.i("gyc", "in DB");
        Connection conn=null;
        PreparedStatement stmt=null;
        User user = null;
        try {
//            Class.forName(DBDRIVER).newInstance();
            Class.forName(DBDRIVER);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        try{
            Log.i("gyc", "try connection");
            conn = DriverManager.getConnection(DBURL,DBUSER,DBPASSWORD);
            Log.i("gyc", "connection done");
            String sql = "SELECT * from userrecord where USER_NAME = '" + usern + "' and PASSWORD = '" + pass + "';";
            stmt= conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Log.i("gyc", "trying find next");
                String username = rs.getString("USER_NAME");
                String password = rs.getString("PASSWORD");
                String firstName = rs.getString("FIRST_NAME");
                String lastName = rs.getString("LAST_NAME");
                String phoneNumber = rs.getString("PHONE_NUM");
                String email = rs.getString("EMAIL");
                String permission = rs.getString("ADMIN_PERMISSION");
                user = new User(username, password, firstName, lastName, phoneNumber, email, permission);
                Log.i("gyc", username+" "+password+" "+permission);
            }
            rs.close();
            stmt.close();;
            conn.close();
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(conn!=null){
                try {
                    conn.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean insertMysql() {
        Log.i("gyc", "in DB");
        Connection conn=null;
        PreparedStatement stmt=null;
        try {
//            Class.forName(DBDRIVER).newInstance();
            Class.forName(DBDRIVER);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        try{
            Log.i("gyc", "try connection");
            conn = DriverManager.getConnection(DBURL,DBUSER,DBPASSWORD);
            Log.i("gyc", "connection done");
            String sql = "INSERT INTO `studioxottawa`.`userrecord` (`USER_NAME`,`PASSWORD`,`FIRST_NAME`,`LAST_NAME`,`PHONE_NUM`,`EMAIL`,`ADMIN_PERMISSION`) VALUES ('user1', 'password1', 'Adam', 'test', '6131112222', 'user1@gmail.com', 0);";
            stmt= conn.prepareStatement(sql);

            boolean rs = stmt.execute();
            if(rs){
                Log.i("gyc", "insert successfully");
            }else{
                Log.i("gyc", "insert failed");
            }
            stmt.close();;
            conn.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if(conn!=null){
                try {
                    conn.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
