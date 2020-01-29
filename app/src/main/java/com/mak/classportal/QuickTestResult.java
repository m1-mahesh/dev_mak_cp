package com.mak.classportal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.adapter.BoardAd;
import com.mak.classportal.adapter.TestResults;
import com.mak.classportal.modales.BoardData;
import com.mak.classportal.modales.Question;
import com.mak.classportal.modales.TestData;
import com.mak.classportal.utilities.UserSession;

import java.util.ArrayList;

public class QuickTestResult extends AppCompatActivity {

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    public static ArrayList<Question> mData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_result);
        mRecyclerView = findViewById(R.id.scheduledTest);
        ((TextView)findViewById(R.id.tvTitle)).setText("Result");
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        TestResults adapter1 = new TestResults(this, mData, userSession);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter1);
        ((ImageButton) findViewById(R.id.closeBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.clear();
                finish();
            }
        });

    }

}
