package com.mak.classportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mak.classportal.adapter.ClassListAdapter;
import com.mak.classportal.adapter.QuestionListAdapter;
import com.mak.classportal.adapter.SubjectListAdapter;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.modales.Question;
import com.mak.classportal.utilities.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectQuestionsActivity extends AppCompatActivity {

    public static String SUBJECT_ID = "";
    public static String CLASS_ID = "";
    public static int INDEX = 0;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    ArrayList<HomeMenu> allClassData = new ArrayList<>();
    public static ArrayList<Question> questions = new ArrayList<>();
    public static HashMap<String, ArrayList<Question>> chapterQuestions = new HashMap<>();
    TextView totalQText, viewText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (INDEX == 0) {
            prepareData();
            setContentView(R.layout.fragment_chapters);
            mRecyclerView = findViewById(R.id.chapterList);
            totalQText = findViewById(R.id.questionText);
            viewText = findViewById(R.id.viewText);
            ((TextView)findViewById(R.id.tvTitle)).setText(Constant.SELECT_CHAPTER);
            mRecyclerView.setHasFixedSize(true);
            SubjectListAdapter.menuId = Constant.SELECT_CHAPTER;
            SubjectListAdapter adapter1 = new SubjectListAdapter(this,allClassData);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerView.setAdapter(adapter1);
        }else{
            setContentView(R.layout.activity_select_questions);
            prepareQData();
            ((TextView)findViewById(R.id.tvTitle)).setText(Constant.SELECT_QUESTION);
            mRecyclerView = findViewById(R.id.questionList);
            ((TextView)findViewById(R.id.tvTitle)).setText(Constant.SELECT_QUESTION);
            mRecyclerView.setHasFixedSize(true);
            QuestionListAdapter adapter1 = new QuestionListAdapter(this,questions, false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(adapter1);
        }

    }
    void showToast(String toastText){
        inflater = getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(getApplicationContext());
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(this, R.font.opensansregular));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(tostLayout);
        toast.show();
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
    void prepareQData(){
        Question aClass=new Question();
        aClass.setQuestion("Question 1");
        Question aClass1=new Question();
        aClass1.setQuestion("Question 2");
        Question aClass2=new Question();
        aClass2.setQuestion("Question 3");
        Question aClass3=new Question();
        aClass3.setQuestion("Question 4");
        Question aClass4=new Question();
        aClass4.setQuestion("Question 5");
        Question aClass5=new Question();
        aClass5.setQuestion("Question 6");
        questions.add(aClass);
        questions.add(aClass1);
        questions.add(aClass2);
        questions.add(aClass3);
        questions.add(aClass4);
        questions.add(aClass5);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (totalQText!=null)
            totalQText.setText("Total Questions: "+ selectedQ.size());

    }
    static ArrayList<Question> selectedQ = new ArrayList<>();
    public void saveQuestion(View view){
        showToast("Data Saved Successfully");
        selectedQ.clear();
        chapterQuestions.put(SUBJECT_ID, questions);
        for (Map.Entry<String,ArrayList<Question>> entry : chapterQuestions.entrySet()){
            for (int j=0;j<entry.getValue().size(); j++){
                Question q=entry.getValue().get(j);
                if(q.isChecked)
                    selectedQ.add(q);
            }
        }
        finish();
    }
    public void viewQuestion(View view){
        if (selectedQ.size()>0) {
            isShow = true;
            setContentView(R.layout.activity_select_questions);
            ((TextView) findViewById(R.id.tvTitle)).setText(R.string.final_q);
            mRecyclerView = findViewById(R.id.questionList);
            ((TextView) findViewById(R.id.tvTitle)).setText(Constant.SELECT_QUESTION);
            mRecyclerView.setHasFixedSize(true);
            QuestionListAdapter adapter1 = new QuestionListAdapter(this, selectedQ, true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            mRecyclerView.setAdapter(adapter1);
            ((Button) findViewById(R.id.saveButton)).setText("Proceed");
        }
    }
    boolean isShow = false;
    @Override
    public void onBackPressed() {
        if (isShow){
            SelectQuestionsActivity.INDEX = 0;
            startActivity(new Intent(this, SelectQuestionsActivity.class));
            isShow = false;
        }
    }
}
