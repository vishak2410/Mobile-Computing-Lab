package com.example.addition;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editText1, editText2;
    Button buttonAdd, buttonReset;
    TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonReset = findViewById(R.id.buttonReset);
        textResult = findViewById(R.id.textResult);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num1 = editText1.getText().toString();
                String num2 = editText2.getText().toString();

                if (!num1.isEmpty() && !num2.isEmpty()) {
                    double sum = Double.parseDouble(num1) + Double.parseDouble(num2);
                    textResult.setText("Result: " + sum);
                } else {
                    textResult.setText("Enter valid numbers");
                }
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText1.setText("");
                editText2.setText("");
                textResult.setText("");
            }
        });
    }
}