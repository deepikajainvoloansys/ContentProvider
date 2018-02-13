package com.createprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by deepika on 12/2/18.
 */

public class StundentContentProvider extends ContentProvider {

    private static final String PROVIDER_NAME = "com.createprovider.studentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/students");
    public static final String ID = "_id";
    public static final String STUDENT_NAME = "name";
    public static final String GRADE = "grade";

    private static final int STUDENTS = 1;
    private static final int STUDENT_ID = 2;
    private static final UriMatcher uriMatcher;

    private static HashMap<String, String> sSTUDENTS_PROJECTION_MAP;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "students", STUDENTS);
        uriMatcher.addURI(PROVIDER_NAME, "students/#", STUDENT_ID);
    }

    private static final String DATABASE_NAME = "College";
    private static final String STUDENTS_TABLE_NAME = "students";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_DB_TABLE =
            " CREATE TABLE " + STUDENTS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " grade TEXT NOT NULL);";

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        db = databaseHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long rowId = db.insert(STUDENTS_TABLE_NAME, null, contentValues);
        if (rowId > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        sqb.setTables(STUDENTS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                sqb.setProjectionMap(sSTUDENTS_PROJECTION_MAP);
                break;

            case STUDENT_ID:
                sqb.appendWhere(ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder == "") {
            /**
             * By default sort on student names
             */
            sortOrder = STUDENT_NAME;
        }


        Cursor c = sqb.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                count = db.update(STUDENTS_TABLE_NAME, values, selection, selectionArgs);
                break;

            case STUDENT_ID:
                count = db.update(STUDENTS_TABLE_NAME, values,
                        ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                count = db.delete(STUDENTS_TABLE_NAME, selection, selectionArgs);
                break;

            case STUDENT_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(STUDENTS_TABLE_NAME, ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            /**
             * Get all student records
             */
            case STUDENTS:
                return "vnd.android.cursor.dir/vnd.contentProvider.students";
            /**
             * Get a particular student
             */
            case STUDENT_ID:
                return "vnd.android.cursor.item/vnd.contentProvider.students";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

}
