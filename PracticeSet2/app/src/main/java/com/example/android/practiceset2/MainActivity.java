package com.example.android.practiceset2;

import androidx.appcompat.app.AppCompatActivity;
// instead of android.support.v7.app.AppCompactActivity

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int raspberryPrice = 5;
        display1("1 box: $" + raspberryPrice);
        raspberryPrice = 10;
        display2("2 boxes: $" + (raspberryPrice));
        display3("3 boxes: $" + (raspberryPrice * 3));

    }

    public void display(String text) {
        TextView t = (TextView) findViewById(R.id.display_text_view);
        t.setText(text);
    }
    public void display(int text) {
        TextView t = (TextView) findViewById(R.id.display_text_view);
        t.setText(text + "");
    }
    public void display1(String text) {
        display(text);
    }
    public void display2(String text) {
        TextView t = (TextView) findViewById(R.id.display_text_view_2);
        t.setText(text);
    }
    public void display3(String text) {
        TextView t = (TextView) findViewById(R.id.display_text_view_3);
        t.setText(text);
    }


}