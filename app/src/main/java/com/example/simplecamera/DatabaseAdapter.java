package com.example.simplecamera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.widget.Toast;

public class DatabaseAdapter {

    DatabaseHelper myhelper;

    // constructor
    public DatabaseAdapter(Context context) {
        myhelper = new DatabaseHelper(context);
    }

    /*
    public long insertData(String name) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, name);
        long id = dbb.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
        return id;
    }*/
    public long insertData(String name, byte[] image) throws SQLException {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.NAME, name);
        cv.put(DatabaseHelper.IMAGE, image);
        long id = dbb.insert(DatabaseHelper.TABLE_NAME, null, cv);
        return id;
    }

    public String getDataString() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {DatabaseHelper.UID, DatabaseHelper.NAME};
        Cursor cursor =db.query(DatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
            buffer.append(cid+" "+name+" \n");
        }
        cursor.close();
        return buffer.toString();
    }

    /*
    public byte[] getDataImage() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {DatabaseHelper.UID, DatabaseHelper.NAME};
        Cursor cursor =db.query(DatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        byte[] imageByteArray = new byte[0];
        while (cursor.moveToNext()) {
            imageByteArray = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.IMAGE));
        }
        return imageByteArray;
    }
    */

    public byte[] getDataImage(String nm) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        byte[] tempbyte = new byte[0];
        String[] columns = {DatabaseHelper.UID, DatabaseHelper.NAME, DatabaseHelper.IMAGE};
        //Cursor cursor =db.query(DatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        //Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, columns, "NAME like " + nm, null, null, null, null);
        Cursor cursor = null;
        cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME + " where " + DatabaseHelper.NAME + "='" + nm + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            tempbyte=cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.IMAGE));
        }
        cursor.close();
        return tempbyte;
    }

    public int delete (String uname) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs = {uname};

        int count = db.delete(myhelper.TABLE_NAME, myhelper.NAME+" =?", whereArgs);
        return count;
    }

    public int updateName (String oldName, String newName) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myhelper.NAME, newName);
        String[] whereArgs = {oldName};
        int count = db.update(myhelper.TABLE_NAME, contentValues, myhelper.NAME+" =?", whereArgs);
        return count;
    }

    static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "myDatabase";    // Database Name
        private static final String TABLE_NAME = "myTable";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version
        private static final String UID="_id";     // Column I (Primary Key)
        private static final String NAME = "Name";    //Column II
        private static final String IMAGE = "image_data";
        // private static final Bitmap BITMAP;    // Column III
        //private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
        //        " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+" VARCHAR(255));";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("+
                UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NAME + " TEXT," +
                IMAGE + " BLOB);";
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate (SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            }
            catch (Exception e){
                Toast.makeText(context, "could not create table", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Toast.makeText(context, "OnUpgrade", Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                Toast.makeText(context, "could not do upgrade", Toast.LENGTH_LONG).show();
            }
        }


    }

}