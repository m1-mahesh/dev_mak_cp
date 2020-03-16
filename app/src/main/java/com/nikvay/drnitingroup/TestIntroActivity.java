package com.nikvay.drnitingroup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nikvay.drnitingroup.modales.TestData;

import java.util.ArrayList;

public class TestIntroActivity extends AppCompatActivity {

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    ArrayList<TestData> allClassData = new ArrayList<>();
    Toolbar toolbar;
    public static String CLASS_ID = "";
    public static String DIVISION = "";
    public static TestData testData;
    TextView questionText, durationText, marksText, instructionText, descriptionText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_intro);
        ((TextView) findViewById(R.id.tvTitle)).setText(testData.getTestTitle());
        questionText = findViewById(R.id.questionText);
        durationText = findViewById(R.id.durationText);
        marksText = findViewById(R.id.totalMarks);
        instructionText = findViewById(R.id.instructionText);
        descriptionText = findViewById(R.id.descriptionText);

        instructionText.setText(Html.fromHtml(testData.getInstruction()));
        String totalMarks = "", noOfQ = "";
        if (testData.getTotalMarks()<10)
            totalMarks="0"+testData.getTotalMarks();
        else totalMarks = ""+testData.getTotalMarks();
        if (testData.totalQuestions<10)
            noOfQ="0"+testData.totalQuestions;
        else noOfQ = ""+testData.totalQuestions;
        marksText.setText(totalMarks);
        questionText.setText(noOfQ);
        durationText.setText(testData.getDuration()+" Mins");
        descriptionText.setText(testData.getDescription());
        ((Button) findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunTest.testData = testData;
                RunTest.step = 0;
                startActivity(new Intent(TestIntroActivity.this, RunTest.class));
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                finish();

            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);


    }
}
