package com.nikvay.drnitingroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nikvay.drnitingroup.adapter.BoardAd;
import com.nikvay.drnitingroup.modales.BoardData;

import java.util.ArrayList;

public class BoardSelectActivity extends AppCompatActivity {

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    ArrayList<BoardData> allClassData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_select_sctivity);
        mRecyclerView = findViewById(R.id.scheduledTest);
        ((TextView)findViewById(R.id.tvTitle)).setText("Select Board");
        getTestList();
        BoardAd adapter1 = new BoardAd(this, allClassData);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(adapter1);

    }
    void getTestList(){
        for (int i=0;i<4;i++){
            BoardData notice=new BoardData();
            if(i<2)
                notice.setName("SSC");
            else
                notice.setName("CBSC");

            allClassData.add(notice);
        }
    }
}
