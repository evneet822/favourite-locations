package com.example.a759831andrioidassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "FavLocationDatabase";
    public static final  int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "FavLocations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LONG = "long";
    public static final String COLUMN_DATE = "sdate";
    public static final String COLUMN_ADRRESS = "saddress";
    public static final String COLUMN_VISITED = "isvisited";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table  " + TABLE_NAME + "(" +
                COLUMN_ID + " integer not null constraint favlocation_pk primary key autoincrement," +
                COLUMN_LAT + " double not null, " +
                COLUMN_LONG + " double not null, " +
                COLUMN_DATE + " varchar(200) not null, " +
                COLUMN_ADRRESS + " varchar(200) not null, " +
                COLUMN_VISITED + " integer not null);" ;
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql  = "drop table if exists " + TABLE_NAME + ";";
        db.execSQL(sql);
        onCreate(db);

    }

    boolean addLocation(double latitute, double longitude, String address, String date, int isVisited){

        //in order to insert items into database , we need a writeable dsatabase
        //this method returns a SQLiteDatabase instance

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        //we need to define a content values instance

        ContentValues cv = new ContentValues();

        //first argument is column name and the second is value

        cv.put(COLUMN_LAT,String.valueOf(latitute));
        cv.put(COLUMN_LONG,String.valueOf(longitude));
        cv.put(COLUMN_ADRRESS,date);
        cv.put(COLUMN_DATE,address);
        cv.put(COLUMN_VISITED,String.valueOf(isVisited));

        // insert method return row number if insertion is successful and -1 if un successful

        return sqLiteDatabase.insert(TABLE_NAME,null,cv) != -1;

//        return true;
    }

    Cursor getallLocations(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + TABLE_NAME,null);
    }

    boolean deleteLocation(int id){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        //the delete method returns the number of rows affected

        return sqLiteDatabase.delete(TABLE_NAME,COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    boolean updateLocation(int id, double latitute, double longitude, String address,String date, int isVisited){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues cv = new ContentValues();

//        cv.put(COLUMN_NAME,name);
//        cv.put(COLUMN_DEPT,dept);
//        cv.put(COLUMN_SALARY,String.valueOf(salary));

        cv.put(COLUMN_LAT,String.valueOf(latitute));
        cv.put(COLUMN_LONG,String.valueOf(longitude));
        cv.put(COLUMN_ADRRESS,address);
        cv.put(COLUMN_DATE,date);
        cv.put(COLUMN_VISITED,isVisited);

        // this method returns the number of rows affected

        return sqLiteDatabase.update(TABLE_NAME,cv,COLUMN_ID + "=?",new String[]{String.valueOf(id)}) > 0;
    }
}
