package com.example.homeword5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "photoApp.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_PHOTOS = "photos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE = "image";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_PHOTOS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_IMAGE + " BLOB" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) { db.execSQL(TABLE_CREATE); }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

    public long addPhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, photo.getTitle());
        values.put(COLUMN_DESCRIPTION, photo.getDescription());
        values.put(COLUMN_IMAGE, photo.getImage());
        long id = db.insert(TABLE_PHOTOS, null, values);
        db.close();
        return id;
    }

    public Photo getPhotoById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTOS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Photo photo = null;
        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
            photo = new Photo(id, title, description, image);
            cursor.close();
        }
        db.close();
        return photo;
    }

    public List<Photo> getAllPhotos() {
        List<Photo> photoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTOS, null, null, null, null, null, COLUMN_ID + " DESC");
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
                photoList.add(new Photo(id, title, description, image));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return photoList;
    }

    public int updatePhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, photo.getTitle());
        values.put(COLUMN_DESCRIPTION, photo.getDescription());
        values.put(COLUMN_IMAGE, photo.getImage());
        int rowsAffected = db.update(TABLE_PHOTOS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(photo.getId())});
        db.close();
        return rowsAffected;
    }

    public void deletePhoto(long photoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTOS, COLUMN_ID + " = ?", new String[]{String.valueOf(photoId)});
        db.close();
    }
}