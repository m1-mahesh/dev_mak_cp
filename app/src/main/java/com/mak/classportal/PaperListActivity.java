package com.mak.classportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mak.classportal.adapter.NoticeListAd;
import com.mak.classportal.adapter.PaperListAd;
import com.mak.classportal.modales.NoticeData;

import java.util.ArrayList;

public class PaperListActivity extends AppCompatActivity {

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    ArrayList<NoticeData> allClassData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_list);
        mRecyclerView = findViewById(R.id.scheduledTest);
        ((TextView)findViewById(R.id.tvTitle)).setText("Board Papers");
        getTestList();
        PaperListAd adapter1 = new PaperListAd(this, allClassData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter1);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(PaperListActivity.this, BoardSelectActivity.class));
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });
    }

    void getTestList(){
        for (int i=8;i<12;i++){
            NoticeData notice=new NoticeData();
            notice.setName(i+"st");
            allClassData.add(notice);
        }
    }
}
