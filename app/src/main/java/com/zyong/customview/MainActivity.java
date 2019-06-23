package com.zyong.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.zyong.library.widget.AutoPollRecyclerView;
import com.zyong.library.widget.BaseRecyclerView;
import com.zyong.library.widget.ItemView;
import com.zyong.library.widget.TitleView;

public class MainActivity extends AppCompatActivity {

    private AutoPollRecyclerView recyclerView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ItemView itemView = (ItemView) findViewById(R.id.itemview);
        itemView.setDivideVisible(false);
        itemView.setListener(new ItemView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "click item view", Toast.LENGTH_SHORT).show();
            }
        });

        TitleView titleView = (TitleView) findViewById(R.id.title_view);
        titleView.setListener(new TitleView.OnClickListener() {
            @Override
            public void onClickLeft() {
                Toast.makeText(MainActivity.this, "click left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickRight() {
                Toast.makeText(MainActivity.this, "click right", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.recyclerview);
        mAdapter = new MyAdapter();
        recyclerView.setAdapter(mAdapter);
        AutoPollRecyclerView.LayoutManager layoutManager = new AutoPollRecyclerView.LayoutManager(this);
        layoutManager.setPollEnable(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.start();
        recyclerView.setOnItemClickListener(new BaseRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View item, int adapterPosition, RecyclerView.Adapter adapter) {
                Toast.makeText(MainActivity.this, "item = " +adapterPosition, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
