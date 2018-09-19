package com.mobileappclass.lab8;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFS_FILE_NAME = "Contacts_SharedPrefs";
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button saveButton = findViewById(R.id.save_button);
        Button loadButton = findViewById(R.id.load_button);
        saveButton.setOnClickListener(v -> onSaveButtonPressed());
        loadButton.setOnClickListener(v -> onLoadButtonPressed());

        database = new ContactSqliteHelper(this).getWritableDatabase();

    }

    private void onSaveButtonPressed() {
//        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS_FILE_NAME,
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPrefs.edit();
        EditText name = ((EditText) findViewById(R.id.name_edit_text));
        EditText number = ((EditText) findViewById(R.id.number_edit_text));
        if (name.getText().toString().equals("") || number.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Empty string is not valid!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(number.getText().toString().length() == 10)) {
            Toast.makeText(MainActivity.this, "It has to be a 10 digit number!", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues content = new ContentValues();
        content.put(ContactDbSchema.Cols.NAME, name.getText().toString());
        content.put(ContactDbSchema.Cols.NUMBER, number.getText().toString());
//
//        editor.putString(name.getText().toString(),number.getText().toString()).commit();

        try (Cursor cursor = database.query(
                ContactDbSchema.TABLE_NAME,
                null,
                ContactDbSchema.Cols.NAME + " = ?",
                new String[]{name.getText().toString()},
                null, null, null)) {
            // Make the cursor point to the first row in the result set.
            cursor.moveToFirst();
            if (cursor.isAfterLast()) {
                // No records exist. Need to insert a new one.
                database.insert(ContactDbSchema.TABLE_NAME, null, content);
            } else {
                // A record already exists. Need to update it with the new number.
                database.update(ContactDbSchema.TABLE_NAME, content, ContactDbSchema.Cols.NAME + " = ? ", new String[]{name.getText().toString()});
            }
        }
    }

    private void onLoadButtonPressed() {
//        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS_FILE_NAME,
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPrefs.edit();
        EditText name = ((EditText) findViewById(R.id.name_edit_text));
        EditText number = ((EditText) findViewById(R.id.number_edit_text));
        if (name.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Empty string is not valid!", Toast.LENGTH_SHORT).show();
            return;
        }

//        String num = sharedPrefs.getString(name.getText().toString(), "Contact don't exist.");
//        number.setText(num);

        try (Cursor cursor = database.query(
                ContactDbSchema.TABLE_NAME,
                null,
                ContactDbSchema.Cols.NAME + " = ? ",
                new String[]{name.getText().toString()},
                null, null, null)) {
            cursor.moveToFirst();

            if (cursor.isAfterLast()) {
                Toast.makeText(MainActivity.this, "Error! The contact was not found.", Toast.LENGTH_SHORT).show();
                return;
            }
            int colIndex = cursor.getColumnIndex(ContactDbSchema.Cols.NUMBER);
            if (colIndex < 0) {
                Toast.makeText(MainActivity.this, "Error! The contact was not found in cursor.", Toast.LENGTH_SHORT).show();
                return;
            }
            String numberStr = cursor.getString(colIndex);
            number.setText(numberStr);

        }
    }
}
