package com.nikvay.drnitingroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.adapter.QuestionListAdapter;
import com.nikvay.drnitingroup.adapter.SubjectListAdapter;
import com.nikvay.drnitingroup.modales.HomeMenu;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.modales.Question;
import com.nikvay.drnitingroup.modales.SubjectData;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectQuestionsActivity extends AppCompatActivity {

    public static String SUBJECT_ID = "";
    public static String CHAPTER_ID = "";
    public static String CLASS_ID = "";
    public static int INDEX = 0;
    public static boolean ADD_Q_IN_PAPER = false;

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView, mRecyclerView1;
    ArrayList<HomeMenu> allClassData = new ArrayList<>();
    public static HashMap<String, ArrayList<Question>> chapterQuestions = new HashMap<>();
    TextView totalQText, viewText;
    public static SubjectData subjectData = null;
    public static Map<String, String> divisions = new HashMap<>();
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        if (INDEX == 0) {
            prepareData();
            setContentView(R.layout.fragment_chapters);
            mRecyclerView = findViewById(R.id.chapterList);
            totalQText = findViewById(R.id.questionText);
            viewText = findViewById(R.id.viewText);
            ((TextView)findViewById(R.id.tvTitle)).setText(Constant.SELECT_CHAPTER);
            mRecyclerView.setHasFixedSize(true);
            SubjectListAdapter adapter1 = new SubjectListAdapter(this,allClassData);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerView.setAdapter(adapter1);
        }else{
            setContentView(R.layout.activity_select_questions);
            mRecyclerView = findViewById(R.id.questionList);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            ((TextView)findViewById(R.id.tvTitle)).setText(Constant.SELECT_QUESTION);
            mRecyclerView.setHasFixedSize(true);
            if (Constant.DELETE_Q_IN_PAPER){
                ((Button)findViewById(R.id.saveButton)).setText("Delete Questions");
                QuestionListAdapter adapter1 = new QuestionListAdapter(this, Constant.headQuestion.sectionData, false);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                mRecyclerView.setAdapter(adapter1);
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter1.notifyDataSetChanged();
                    }
                });
            }else if(chapterQuestions.containsKey(CHAPTER_ID)) {
                QuestionListAdapter.CHAPTER_ID = CHAPTER_ID;
                QuestionListAdapter adapter1 = new QuestionListAdapter(this, chapterQuestions.get(CHAPTER_ID), false);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                mRecyclerView.setAdapter(adapter1);
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter1.notifyDataSetChanged();
                    }
                });
            }else {
                levels.put("1", true);
                levels.put("2", true);
                levels.put("3", true);
                if (Constant.IS_PAPER)
                    getQuestionsForPaper();
                else
                    getChapterQuestions();
            }

            findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveQuestion();
                }
            });
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
        if (subjectData!=null) {
            for (Map.Entry<String, String> entry : subjectData.getChapters().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                HomeMenu aClass = new HomeMenu();
                aClass.setName(value);
                aClass.setId(key);
                allClassData.add(aClass);
            }
        }else if (Constant.IS_PAPER){
            getSubjectChapters();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (totalQText!=null)
            totalQText.setText("Total Questions: "+ selectedQ.size());

    }
    public static ArrayList<Question> selectedQ = new ArrayList<>();
    public void saveQuestion(){

        selectedQ.clear();


        if(Constant.DELETE_Q_IN_PAPER) {
            for(int i=0; i<Constant.headQuestion.sectionData.size();i++){
                Question q = Constant.headQuestion.sectionData.get(i);
                if(q.isChecked)
                    selectedQ.add(q);
            }
            editDeleteQuestion();
        }else{
            showToast("Questions Added");
            for (Map.Entry<String,ArrayList<Question>> entry : chapterQuestions.entrySet()){
                for (int j=0;j<entry.getValue().size(); j++){
                    Question q=entry.getValue().get(j);
                    if(q.isChecked)
                        selectedQ.add(q);
                }
            }
            finish();
        }

    }
    public void editDeleteQuestion() {
        try {
            String url = appSingleTone.editPaperDeleteQuestions;
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<selectedQ.size();i++){
                jsonArray.put(selectedQ.get(i).getQuestionId());
            }
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("paper_id", apiData.getId());
            executeAPI.addPostParam("heading_id", Constant.headQuestion.getQuestionId());
            executeAPI.addPostParam("question_id", jsonArray.toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            SelectQuestionsActivity.chapterQuestions.clear();
                            SelectQuestionsActivity.subjectData = null;
                            ViewTestQuestions.selectedQ.clear();
                            setResult(Constant.ACTIVITY_FINISH_REQUEST_CODE);
                            finish();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void viewQuestion(View view){
        if (selectedQ.size()>0) {
            ViewTestQuestions.selectedQ = selectedQ;
            ViewTestQuestions.IS_VIEW_CREATED_TEST = false;
            ViewTestQuestions.apiData = apiData;
            startActivityForResult(new Intent(SelectQuestionsActivity.this, ViewTestQuestions.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
            overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_FINISH_REQUEST_CODE && resultCode == Constant.ACTIVITY_FINISH_REQUEST_CODE){
            SelectQuestionsActivity.chapterQuestions.clear();
            SelectQuestionsActivity.subjectData = null;
            finish();
        }
    }

    public static ArrayList<Question> mData = new ArrayList<>();


    void parseTestQuestions(JSONObject jsonObject) {
        if (!chapterQuestions.containsKey(CHAPTER_ID)) {
            chapterQuestions.put(CHAPTER_ID, new ArrayList<>());
        }
        try {
            JSONArray jsonArray = Constant.IS_PAPER?jsonObject.getJSONArray("paper_genration_question_list"):jsonObject.getJSONArray("online_test_question_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Question question = new Question();
                question.setQuestionId(object.getString("id"));
                question.setQuestion(object.getString("questions"));
                JSONArray options = object.getJSONArray("options");
                for(int k=0;k<options.length();k++){
                    JSONObject op = options.getJSONObject(k);
                    question.options.put(op.getString("option_id"),op.getString("option_value"));
                }
                if (object.has("answer_id"))
                    question.setCorrectAns(object.getString("answer_id"));
                question.setMarks(object.getInt("questions_marks"));
                question.setStatus(object.getString("status"));
                question.setImageUrl(object.getString("image"));
                chapterQuestions.get(CHAPTER_ID).add(question);
            }
            QuestionListAdapter.CHAPTER_ID = CHAPTER_ID;
            QuestionListAdapter adapter1 = new QuestionListAdapter(this,chapterQuestions.get(CHAPTER_ID), false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(adapter1);
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    adapter1.notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getChapterQuestions() {
            if(Constant.IS_PAPER) {
                getQuestionsForPaper();
                return;
            }
        try {
            JSONArray jsonArray = new JSONArray();
            for (Map.Entry<String, String> entry : subjectData.divisions.entrySet()) {
                String key = entry.getKey();
                jsonArray.put(key);
            }
            JSONArray levelJsonArray = new JSONArray();
            for (Map.Entry<String, Boolean> entry : levels.entrySet()) {
                if(entry.getValue())
                    levelJsonArray.put(entry.getKey());
            }
            Log.e("Levels ", levelJsonArray.toString());
            String url = appSingleTone.questionList;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", subjectData.getClassId());
            executeAPI.addPostParam("division_id", jsonArray.toString());
            executeAPI.addPostParam("chapter_id", CHAPTER_ID);
            executeAPI.addPostParam("subject_id", subjectData.getId());
            executeAPI.addPostParam("level_of_question_array", levelJsonArray.toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    parseTestQuestions(result);
                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getQuestionsForPaper() {

        try {
            JSONArray levelJsonArray = new JSONArray();
            for (Map.Entry<String, Boolean> entry : levels.entrySet()) {
                if(entry.getValue())
                    levelJsonArray.put(entry.getKey());
            }
            Log.e("Levels ", levelJsonArray.toString());
            String url = appSingleTone.questionsListForPaper;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", apiData.classID);
//            executeAPI.addPostParam("division_id", jsonArray.toString());
            executeAPI.addPostParam("chapter_id", CHAPTER_ID);
            executeAPI.addPostParam("subject_id", apiData.subjectId);
            executeAPI.addPostParam("level_of_question_array", levelJsonArray.toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    parseTestQuestions(result);
                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        if(!Constant.DELETE_Q_IN_PAPER)
            getMenuInflater().inflate(R.menu.level_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.isChecked())
            item.setChecked(false);
        else item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.easy:
                levels.put("1", item.isChecked());
                getChapterQuestions();
                return true;
            case R.id.medium:
                levels.put("2", item.isChecked());
                getChapterQuestions();
                return true;
            case R.id.hard:
                levels.put("3", item.isChecked());
                getChapterQuestions();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    Map<String, Boolean> levels = new HashMap<>();
    ArrayList<SubjectData> chaptersForPaper = new ArrayList<>();
    public static NoticeData apiData = null;
    void prepareSubjectChapter(JSONArray apiResponse){
        try {
            chaptersForPaper.clear();
            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                SubjectData aClass = new SubjectData();
                aClass.setId(classObj.getString("id"));
                aClass.setName(classObj.getString("subject_name"));
                if(aClass.getId().equals(apiData.subjectId)) {
                    for (int j = 0; j < classObj.getJSONArray("chapter_list").length(); j++) {
                        JSONObject obj = classObj.getJSONArray("chapter_list").getJSONObject(j);
                        aClass.addChapter(obj.getString("id"), obj.getString("chapter_name"));
                    }

                    Log.e("", aClass.id);
                    chaptersForPaper.add(aClass);
                    break;
                }
            }
            if (chaptersForPaper.size()>0) {
                for (Map.Entry<String, String> entry : chaptersForPaper.get(0).getChapters().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    HomeMenu aClass = new HomeMenu();
                    aClass.setName(value);
                    aClass.setId(key);
                    allClassData.add(aClass);
                }
                SubjectListAdapter adapter1 = new SubjectListAdapter(this,allClassData);
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                mRecyclerView.setAdapter(adapter1);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getSubjectChapters() {

        try {
            String url = appSingleTone.subjectChapterList;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", apiData.classID);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.has("subject_list")) {
                            JSONArray object = result.getJSONArray("subject_list");
                            prepareSubjectChapter(object);
                        } else {
                            showToast("Something went wrong, Please try again later");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
