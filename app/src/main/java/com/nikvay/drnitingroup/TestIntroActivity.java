package com.nikvay.drnitingroup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikvay.drnitingroup.adapter.InstructionsAd;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.modales.TestData;

import org.json.JSONObject;

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
    RecyclerView instructionList;
    String[] listItem;
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
        instructionList = findViewById(R.id.listView);

        getInstructions();

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
    void getInstructions(){
        try{
            ArrayList<NoticeData> instructions = new ArrayList<>();
            for(int i=0;i<testData.instructionArray.length();i++){
                NoticeData data = new NoticeData();
                JSONObject jsonObject = testData.instructionArray.getJSONObject(i);
                data.setId(jsonObject.getString("id"));
                data.setTitle(jsonObject.getString("title"));
                instructions.add(data);
            }

            InstructionsAd adapter1 = new InstructionsAd(this, instructions, true, true);
            instructionList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            instructionList.setAdapter(adapter1);
        }catch (Exception e){
            e.printStackTrace();
        }
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
