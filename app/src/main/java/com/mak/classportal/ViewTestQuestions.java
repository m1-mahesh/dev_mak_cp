package com.mak.classportal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mak.classportal.adapter.QuestionListAdapter;
import com.mak.classportal.adapter.ViewQuestionListAd;
import com.mak.classportal.modales.Question;
import com.mak.classportal.utilities.Constant;

import java.util.ArrayList;

public class ViewTestQuestions extends AppCompatActivity {

    RecyclerView mRecyclerView1;
    public static ArrayList<Question> selectedQ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_questions);
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.IS_PAPER)
                    startActivity(new Intent(ViewTestQuestions.this, FinalisePaperActivity.class));
                else
                    startActivityForResult(new Intent(ViewTestQuestions.this, FinaliseTest.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
//                finish();
            }
        });
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.final_q);
        mRecyclerView1 = findViewById(R.id.questionList);
        mRecyclerView1.setHasFixedSize(true);
        ViewQuestionListAd adapter1 = new ViewQuestionListAd(this, selectedQ, true);
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mRecyclerView1.setAdapter(adapter1);
        ((Button) findViewById(R.id.saveButton)).setText("Proceed");
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_FINISH_REQUEST_CODE && resultCode == Constant.ACTIVITY_FINISH_REQUEST_CODE) {
            setResult(Constant.ACTIVITY_FINISH_REQUEST_CODE);
            finish();
        }
    }

}
