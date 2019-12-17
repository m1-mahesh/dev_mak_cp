package com.mak.classportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.mak.classportal.adapter.ClassListAdapter;
import com.mak.classportal.adapter.QuestionListAdapter;
import com.mak.classportal.adapter.SubjectListAdapter;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.utilities.Constant;

import java.util.ArrayList;

public class SelectQuestionsActivity extends AppCompatActivity {

    public static String SUBJECT_ID = "";
    public static String CLASS_ID = "";
    public static int INDEX = 0;
    RecyclerView mRecyclerView;
    ArrayList<HomeMenu> allClassData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareData();
        if (INDEX == 0) {
            setContentView(R.layout.fragment_chapters);
            mRecyclerView = findViewById(R.id.chapterList);
            ((TextView)findViewById(R.id.tvTitle)).setText(Constant.SELECT_CHAPTER);
            mRecyclerView.setHasFixedSize(true);
            SubjectListAdapter.menuId = Constant.SELECT_CHAPTER;
            SubjectListAdapter adapter1 = new SubjectListAdapter(this,allClassData);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerView.setAdapter(adapter1);
        }else{
            setContentView(R.layout.activity_select_questions);
            ((TextView)findViewById(R.id.tvTitle)).setText(Constant.SELECT_QUESTION);
            mRecyclerView = findViewById(R.id.questionList);
            ((TextView)findViewById(R.id.tvTitle)).setText(Constant.SELECT_QUESTION);
            mRecyclerView.setHasFixedSize(true);
            QuestionListAdapter adapter1 = new QuestionListAdapter(this,allClassData);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(adapter1);
        }

    }
    void prepareData(){
        allClassData = new ArrayList<>();
        HomeMenu aClass=new HomeMenu();
        aClass.setName("Marathi");

        HomeMenu aClass1=new HomeMenu();
        aClass1.setName("English");
        HomeMenu aClass2=new HomeMenu();
        aClass2.setName("Maths");
        HomeMenu aClass3=new HomeMenu();
        aClass3.setName("History");
        HomeMenu aClass4=new HomeMenu();
        aClass4.setName("Science");
        HomeMenu aClass5=new HomeMenu();
        aClass5.setName("Hindi");
        allClassData.add(aClass);
        allClassData.add(aClass1);
        allClassData.add(aClass2);
        allClassData.add(aClass3);
        allClassData.add(aClass4);
        allClassData.add(aClass5);
    }
}
