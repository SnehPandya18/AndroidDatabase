package com.snehpandya.crd;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int USER_LOADER = 0;
    UserCursorAdapter mUserCursorAdapter;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertUser();
            }
        });

        mListView = (ListView) findViewById(R.id.list);

        mUserCursorAdapter = new UserCursorAdapter(this, null);
        mListView.setAdapter(mUserCursorAdapter);

        getLoaderManager().initLoader(USER_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                UserContract.UserEntry._ID,
                UserContract.UserEntry.COLUMN_USER_NAME,
                UserContract.UserEntry.COLUMN_USER_GENDER,
                UserContract.UserEntry.COLUMN_USER_WEIGHT
        };

        return new CursorLoader(this,
                UserContract.UserEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor o) {
        mUserCursorAdapter.swapCursor(o);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mUserCursorAdapter.swapCursor(null);
    }

    private void insertUser() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserContract.UserEntry.COLUMN_USER_NAME, "Jhon");
        contentValues.put(UserContract.UserEntry.COLUMN_USER_GENDER, UserContract.UserEntry.GENDER_MALE);
        contentValues.put(UserContract.UserEntry.COLUMN_USER_WEIGHT, 70);

        Uri newURI = getContentResolver().insert(UserContract.UserEntry.CONTENT_URI, contentValues);
    }

    private void deleteAllUsers() {
        int rowsDeleted = getContentResolver().delete(UserContract.UserEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }
}
