package com.example.experiment7;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    String clipboard = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
    }

    // Create Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    // Handle Menu Clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_cut) {
            clipboard = editText.getText().toString();
            editText.setText("");
            Toast.makeText(this, "Cut", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_copy) {
            clipboard = editText.getText().toString();
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_paste) {
            editText.setText(clipboard);
            Toast.makeText(this, "Pasted", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_delete) {
            editText.setText("");
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_select_all) {
            editText.selectAll();
            Toast.makeText(this, "All Selected", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_unselect_all) {
            editText.setSelection(editText.getText().length());
            Toast.makeText(this, "Unselected", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
