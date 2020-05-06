package com.nikvay.drnitingroup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.adapter.ExpandableListAdapter;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.modales.Question;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewPaperQuestions extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    AppSingleTone appSingleTone;
    public static NoticeData paperDta, apiData;
    ArrayList<Question> questions = new ArrayList<>();
    ImageView addQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_paper_questions);
        Constant.ADD_Q_IN_PAPER = false;
        Constant.DELETE_Q_IN_PAPER = false;
        expListView = findViewById(R.id.expandableList);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        appSingleTone = new AppSingleTone(this);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        addQ = findViewById(R.id.addQ);
        addQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectQuestionsActivity.selectedQ.clear();
                SelectQuestionsActivity.apiData = apiData;
                Constant.IS_PAPER = true;
                FinalisePaperActivity.IS_UPDATE = false;
                startActivity(new Intent(ViewPaperQuestions.this, FinalisePaperActivity.class));
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });
        // preparing list data
//        prepareListData();

        getPaperData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPaperData();
    }


    void parseTestQuestions(JSONObject jsonObject) {
        try {
            questions.clear();
            JSONArray jsonArray = jsonObject.getJSONArray("paper_data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Question question = new Question();
                question.setQuestionId(object.getString("id"));
                question.setQuestion(object.getString("question_heading"));
                question.setAnswerDescription(object.getString("marks"));
                question.paperID = jsonObject.getJSONObject("paper_information").getString("id");
                JSONArray options = object.getJSONArray("questions_list");
                for (int k = 0; k < options.length(); k++) {
                    JSONObject op = options.getJSONObject(k);
                    Question q = new Question();
                    q.setQuestionId(op.getString("id"));
                    q.setQuestion(op.getString("questions"));
                    q.setImageUrl(op.getString("image"));
                    question.sectionData.add(q);
                }

                questions.add(question);
            }
            listAdapter = new ExpandableListAdapter(this, questions, userSession.getBoolean("isAdmin"));

            // setting list adapter
            expListView.setAdapter(listAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPaperData() {
        try {

            String url = appSingleTone.paperData;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("paper_id", paperDta.getId());
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.paper_edit_menu, menu);
        menu.setHeaderTitle("Select The Action");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){

        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

        int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);

        int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
        Log.e("Pos", ": "+groupPos);
        switch (item.getItemId()){
            case R.id.editHeading:
                Log.e("Hed", "");
                FinalisePaperActivity.IS_UPDATE = true;
                FinalisePaperActivity.paperData = (Question) expListView.getItemAtPosition(groupPos);
                startActivity(new Intent(ViewPaperQuestions.this, FinalisePaperActivity.class));
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                break;
            case R.id.addQuestion:
                SelectQuestionsActivity.INDEX = 0;
                Constant.headQuestion = (Question) expListView.getItemAtPosition(groupPos);
                Constant.paperData = paperDta;
                Constant.IS_PAPER = true;
                Constant.ADD_Q_IN_PAPER = true;
                SelectQuestionsActivity.apiData = apiData;
                startActivityForResult(new Intent(ViewPaperQuestions.this, SelectQuestionsActivity.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);

                break;
            case R.id.deleteQ:
                Log.e("Del", "");
                SelectQuestionsActivity.INDEX = 1;
                Constant.headQuestion = (Question) expListView.getItemAtPosition(groupPos);
                Constant.paperData = paperDta;
                Constant.IS_PAPER = true;
                Constant.ADD_Q_IN_PAPER = false;
                Constant.DELETE_Q_IN_PAPER = true;
                SelectQuestionsActivity.apiData = apiData;
                startActivityForResult(new Intent(ViewPaperQuestions.this, SelectQuestionsActivity.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                break;
        }
        return true;
    }
}
