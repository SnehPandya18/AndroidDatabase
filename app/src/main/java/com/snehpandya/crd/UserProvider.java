package com.snehpandya.crd;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by sneh.pandya on 20/09/17.
 */

public class UserProvider extends ContentProvider {

    private static final int USERS = 100;
    private static final int USERS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(UserContract.CONTENT_AUTHORITY, UserContract.PATH_USERS, USERS);
        sUriMatcher.addURI(UserContract.CONTENT_AUTHORITY, UserContract.PATH_USERS + "/#", USERS_ID);
    }

    private UserDBHelper mUserDBHelper;

    @Override
    public boolean onCreate() {
        mUserDBHelper = new UserDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqLiteDatabase = mUserDBHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                cursor = sqLiteDatabase.query(UserContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case USERS_ID:
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(UserContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return insertUsers(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertUsers(Uri uri, ContentValues values) {
        String name = values.getAsString(UserContract.UserEntry.COLUMN_USER_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer gender = values.getAsInteger(UserContract.UserEntry.COLUMN_USER_GENDER);
        if (gender == null || !UserContract.UserEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        Integer weight = values.getAsInteger(UserContract.UserEntry.COLUMN_USER_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        SQLiteDatabase database = mUserDBHelper.getWritableDatabase();

        long id = database.insert(UserContract.UserEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e("LOG", "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return updateUsers(uri, contentValues, selection, selectionArgs);
            case USERS_ID:
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateUsers(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    private int updateUsers(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(UserContract.UserEntry.COLUMN_USER_NAME)) {
            String name = contentValues.getAsString(UserContract.UserEntry.COLUMN_USER_NAME);
            if (name == null) {
                throw new IllegalArgumentException("User requires a name");
            }
        }

        if (contentValues.containsKey(UserContract.UserEntry.COLUMN_USER_GENDER)) {
            Integer gender = contentValues.getAsInteger(UserContract.UserEntry.COLUMN_USER_GENDER);
            if (gender == null || !UserContract.UserEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("User requires valid gender");
            }
        }

        if (contentValues.containsKey(UserContract.UserEntry.COLUMN_USER_WEIGHT)) {
            Integer weight = contentValues.getAsInteger(UserContract.UserEntry.COLUMN_USER_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("User requires valid weight");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase sqLiteDatabase = mUserDBHelper.getWritableDatabase();

        int rowsUpdated = sqLiteDatabase.update(UserContract.UserEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mUserDBHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                rowsDeleted = sqLiteDatabase.delete(UserContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case USERS_ID:
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = sqLiteDatabase.delete(UserContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return UserContract.UserEntry.CONTENT_LIST_TYPE;
            case USERS_ID:
                return UserContract.UserEntry.CONENT_ITEM_TPYE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}