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
import com.mak.classportal.adapter.VideoListAd;
import com.mak.classportal.modales.NoticeData;

import java.util.ArrayList;

public class VideosActivity extends AppCompatActivity {

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    ArrayList<NoticeData> allClassData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        mRecyclerView = findViewById(R.id.scheduledTest);
        ((TextView)findViewById(R.id.tvTitle)).setText("Videos List");
        getTestList();
        VideoListAd adapter1 = new VideoListAd(this, allClassData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter1);

    }

    void getTestList(){
        for (int i=8;i<12;i++){
            NoticeData notice=new NoticeData();
            notice.setName(i+"st");
            allClassData.add(notice);
        }
    }
}
