package com.snehpandya.crd;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by sneh.pandya on 20/09/17.
 */

public class UserCursorAdapter extends CursorAdapter {

    public UserCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView genderTextView = (TextView) view.findViewById(R.id.gender);
        TextView weightTextView = (TextView) view.findViewById(R.id.weight);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USER_NAME);
        int genderColumnIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USER_GENDER);
        int weightColumnIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_USER_WEIGHT);

        // Read the pet attributes from the Cursor for the current pet
        String userName = cursor.getString(nameColumnIndex);
        String userGender = cursor.getString(genderColumnIndex);
        String userWeight = cursor.getString(weightColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(userName);
        genderTextView.setText(userGender);
        weightTextView.setText(userWeight);
    }
}
