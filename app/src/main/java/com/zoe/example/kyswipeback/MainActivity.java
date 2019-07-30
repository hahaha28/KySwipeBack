package com.zoe.example.kyswipeback;

import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        textView.setText("放一段很长很长很长很长很长很长很长很长很长" +
                "很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                "很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                "很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                "很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                "很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                "很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                "很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                "很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                "很长很长很长很长很长很长很长很长很长很长很长很长很长很长的文字");

        String[] data={"test","test","test","test","test","test","test","test","test","test","test","test","test","test","test"};
        ListView listView=findViewById(R.id.listView);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);

//        getSupportActionBar().setElevation(0);
    }
}
