package com.san.numberaddsubview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public   NumberAddSubView num_add_sub_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        num_add_sub_view= (NumberAddSubView) findViewById(R.id.num_add_sub_view);
        num_add_sub_view.seOnNumberClickListener(new NumberAddSubView.OnNumberClickListener() {
            @Override
            public void onNumberAdd(View view, int values) {
                Toast.makeText(MainActivity.this, "+1="+values, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNumberSub(View view, int values) {
                Toast.makeText(MainActivity.this, "-1="+values, Toast.LENGTH_SHORT).show();

            }
        });
//        num_add_sub_view.setValues(5);
//        num_add_sub_view.setMaxValues(17);
//        num_add_sub_view.setMinValues(-5);
    }
}
