package com.mak.classportal;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.mak.classportal.fragment.ActiveTestsTabFragment;
import com.mak.classportal.modales.TestData;

import java.util.ArrayList;
import java.util.List;

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
        marksText.setText("120");
        questionText.setText("50");
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
