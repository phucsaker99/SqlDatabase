package com.example.sqldatabase.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.sqldatabase.model.Pet;
import com.example.sqldatabase.util.Constants;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLite extends SQLiteOpenHelper {
    private Context context;

    //contrustor khi khởi tạo cơ sở dữ liệu
    public SQLite(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.context = context;
    }

    //Hàm đc gọi đầu tiên khi mới khỏi tạo
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PET_TABLE = "Create table " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY,"
                + Constants.KEY_NAME + " TEXT,"
                + Constants.KEY_WEIGHT + " FLOAT,"
                + Constants.KEY_PRICE + " TEXT,"
                + Constants.KEY_DESCRIPTION + " INTEGER,"
                + Constants.KEY_DATE + " LONG);";

        db.execSQL(CREATE_PET_TABLE);
    }

    //Kiểm tra phiên bản SQLiteDatabase
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        onCreate(db);
    }

    //Các lệnh sql

    // TODO: 11/21/2020 Thêm item
    public void addItem(Pet item) {
        SQLiteDatabase db = this.getWritableDatabase(); // lệnh chó phép ghi csdl

        ContentValues values = new ContentValues(); //Lớp giúp đặt giá trị dưới dạng thuộc tính và giá trị
        values.put(Constants.KEY_NAME, item.getName());
        values.put(Constants.KEY_PRICE, item.getPrice());
        values.put(Constants.KEY_WEIGHT, item.getWeight());
        values.put(Constants.KEY_DESCRIPTION, item.getDescription());
        values.put(Constants.KEY_DATE, java.lang.System.currentTimeMillis());//lấy thòi gian của hệ thống

        //thêm dòng vào csdl
        db.insert(Constants.TABLE_NAME, null, values);
        db.close(); //Khi ghi dữ liệu vào csdl xong cần đóng db để tránh rò rỉ dữ liệu
    }

    // TODO: 11/21/2020 Lấy ra item hiện tại theo id
    public Pet getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase(); //lệnh cho phép đọc dữ liệu

        //Biến cursor(con trỏ) cho phép chỏ đến đầu hàng các dòng để đọc giá trị từng cột tại hàng đó
        Cursor cursor = db.query(Constants.TABLE_NAME,
                new String[]{Constants.KEY_ID,
                        Constants.KEY_NAME,
                        Constants.KEY_WEIGHT,
                        Constants.KEY_PRICE,
                        Constants.KEY_DESCRIPTION,
                        Constants.KEY_DATE},
                Constants.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Pet item = new Pet();
        if (cursor != null) {
            item.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID)))); //đọc dữ liệu theo tên cột vào đối tượng Pet
            item.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));
            item.setWeight(cursor.getFloat(cursor.getColumnIndex(Constants.KEY_WEIGHT)));
            item.setPrice(cursor.getDouble(cursor.getColumnIndex(Constants.KEY_PRICE)));
            item.setDescription(cursor.getString(cursor.getColumnIndex(Constants.KEY_DESCRIPTION)));

            //convert Timestamp to something readable
            DateFormat dateFormat = DateFormat.getDateInstance();
            String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE)))
                    .getTime()); // Feb 23, 2020

            item.setDate(formattedDate);
        }

        return item;
    }

    // TODO: 11/21/2020  lấy danh sách tất cả các item có trong cơ sở dữ liệu
    public ArrayList<Pet> getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Pet> itemList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME,
                new String[]{Constants.KEY_ID,
                        Constants.KEY_NAME,
                        Constants.KEY_WEIGHT,
                        Constants.KEY_PRICE,
                        Constants.KEY_DESCRIPTION,
                        Constants.KEY_DATE},
                null, null, null, null,
                Constants.KEY_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Pet item = new Pet();
                item.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                item.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));
                item.setWeight(cursor.getFloat(cursor.getColumnIndex(Constants.KEY_WEIGHT)));
                item.setPrice(cursor.getDouble(cursor.getColumnIndex(Constants.KEY_PRICE)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(Constants.KEY_DESCRIPTION)));

                //convert Timestamp to something readable
                DateFormat dateFormat = DateFormat.getDateInstance();
                String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE)))
                        .getTime()); // Feb 23, 2020
                item.setDate(formattedDate);

                //Add to arraylist
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        return itemList;
    }

    // TODO: 11/21/2020 Cập nhật item
    public int updateItem(Pet item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NAME, item.getName());
        values.put(Constants.KEY_PRICE, item.getPrice());
        values.put(Constants.KEY_WEIGHT, item.getWeight());
        values.put(Constants.KEY_DESCRIPTION, item.getDescription());
        values.put(Constants.KEY_DATE, java.lang.System.currentTimeMillis());//lấy thời gian của hệ thống

        //update row
        return db.update(Constants.TABLE_NAME, values,
                Constants.KEY_ID + "=?",
                new String[]{String.valueOf(item.getId())});

    }

    // TODO: 11/21/2020 Xóa item
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME,
                Constants.KEY_ID + "=?",
                new String[]{String.valueOf(id)});

        //close
        db.close();

    }

    // TODO: 11/21/2020 Lấy ra tổng số lượng item hiện có
    public int getItemsCount() {
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();

    }
}
