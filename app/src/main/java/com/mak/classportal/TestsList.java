package com.mak.classportal;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mak.classportal.adapter.ClassListAdapter;
import com.mak.classportal.adapter.ScheduledTestsAdapter;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.modales.TestData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class TestsList extends AppCompatActivity {

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    ArrayList<TestData> allClassData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_list);
        mRecyclerView = findViewById(R.id.scheduledTest);
        ((TextView)findViewById(R.id.tvTitle)).setText("Scheduled Tests");
        getTestList();
        ScheduledTestsAdapter adapter1 = new ScheduledTestsAdapter(this, allClassData, false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter1);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(TestsList.this, NewTestActivity.class));
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });
    }

    void getTestList(){
        for (int i=0;i<5;i++){
            TestData testData =new TestData();
            testData.setTestClass(i+" std");
            testData.setTestDate("12-Dec-2019");
            testData.setTestStatus("Pending");
            allClassData.add(testData);
        }
    }
}
