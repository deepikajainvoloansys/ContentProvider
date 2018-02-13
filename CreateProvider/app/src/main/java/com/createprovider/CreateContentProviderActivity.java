package com.createprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateContentProviderActivity extends AppCompatActivity implements View.OnClickListener {

    public static String URL = "content://com.createprovider.studentProvider";

    private TextView mTextViewTitle;
    private EditText mEditTextName;
    private EditText mEditTextGrade;
    private Button mButtonAdd;
    private Button mButtonRetrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_content_provider);
        findViews();
    }

    private void findViews() {
        mTextViewTitle = (TextView) findViewById(R.id.text_view_title);
        mEditTextName = (EditText) findViewById(R.id.edit_text_name);
        mEditTextGrade = (EditText) findViewById(R.id.edit_text_grade);
        mButtonAdd = (Button) findViewById(R.id.button_add);
        mButtonRetrive = (Button) findViewById(R.id.button_retrive);

        mButtonAdd.setOnClickListener(this);
        mButtonRetrive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonAdd) {
            ContentValues values = new ContentValues();
            values.put(StundentContentProvider.STUDENT_NAME,
                    mEditTextName.getText().toString());

            values.put(StundentContentProvider.GRADE,
                    mEditTextGrade.getText().toString());

            Uri uri = getContentResolver().insert(
                    StundentContentProvider.CONTENT_URI, values);

            Toast.makeText(getBaseContext(),
                    uri.toString(), Toast.LENGTH_LONG).show();

        } else if (v == mButtonRetrive) {

            Uri studentsUri = Uri.parse(URL);

            Cursor studentCursor = managedQuery(studentsUri, null, null, null, "");

            if (studentCursor.moveToFirst()) {
                do {
                    Toast.makeText(this,
                            studentCursor.getString(studentCursor.getColumnIndex(StundentContentProvider.ID)) +
                                    ", " + studentCursor.getString(studentCursor.getColumnIndex(StundentContentProvider.STUDENT_NAME)) +
                                    ", " + studentCursor.getString(studentCursor.getColumnIndex(StundentContentProvider.GRADE)),
                            Toast.LENGTH_SHORT).show();
                } while (studentCursor.moveToNext());
            }
        }

    }

}
