package com.zyong.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.zyong.library.widget.ItemView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ItemView itemView = (ItemView) findViewById(R.id.itemview);
        itemView.setDivideVisible(false);
        itemView.setOnItemViewClickListener(new ItemView.OnItemViewClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "click item view", Toast.LENGTH_SHORT).show();
            }
        });
        itemView.setOnCheckedChangedListener(new ItemView.OnCheckedChangedListener() {
            @Override
            public void onCheckedChanged(boolean checked) {
                Toast.makeText(MainActivity.this, "click item checked " + checked, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
