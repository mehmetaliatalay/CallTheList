package myapplication.callthelist.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CallListProvider extends ContentProvider {

    //Database ve tablolar ile ilgili kısım
    private static final String DATABASE_NAME = "list.db";
    private static final int DATABASE_VERSION = 1;
    private static final String LIST_TABLE_NAME = "list";

    private static final String CREATE_LIST_TABLE = "CREATE TABLE " + LIST_TABLE_NAME
            + "(id INTEGER PRIMARY KEY ,"
            + "name TEXT NOT NULL,"
            + "number TEXT NOT NULL);";
    //

    //ContentProvider Kısmı
    private static final String CONTENT_AUTHORITY = "myapplication.callthelist.calllistprovider";
    private static final String PATH_LIST = "list";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LIST);
    private static final UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(CONTENT_AUTHORITY, PATH_LIST, 1);
    }
    //

    SQLiteDatabase database;
    DatabaseHelper databaseHelper;


    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        database = databaseHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (matcher.match(uri)) {

            case 1:
                cursor = database.query(LIST_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }

        return null;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (matcher.match(uri)) {

            case 1:
                Long insertedRowId = database.insert(LIST_TABLE_NAME, null, values);
                if (insertedRowId > 0) {

                    return ContentUris.withAppendedId(uri, insertedRowId);
                }
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int effectedRows = 0;
        switch (matcher.match(uri)) {

            case 1:
                effectedRows = database.delete(LIST_TABLE_NAME, selection, selectionArgs);
                break;
        }

        return effectedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    private class DatabaseHelper extends SQLiteOpenHelper{


        DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_LIST_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +LIST_TABLE_NAME);
            onCreate(db);
        }
    }

}
