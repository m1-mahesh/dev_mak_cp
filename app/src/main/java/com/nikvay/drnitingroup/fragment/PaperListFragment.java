package com.nikvay.drnitingroup.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nikvay.drnitingroup.NewPaperActivity;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.RootActivity;
import com.nikvay.drnitingroup.adapter.PaperListAd;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.permission.PermissionsActivity;
import com.nikvay.drnitingroup.permission.PermissionsChecker;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.nikvay.drnitingroup.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.nikvay.drnitingroup.permission.PermissionsChecker.REQUIRED_PERMISSION;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class PaperListFragment extends Fragment {


    PermissionsChecker checker;
    RecyclerView mRecyclerView;
    ArrayList<NoticeData> allClassData = new ArrayList<>();
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    public static boolean IS_ADD = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (IS_ADD) {
            IS_ADD = false;
            getPaperList();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checker = new PermissionsChecker(getContext());
        if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
            PermissionsActivity.startActivityForResult(getActivity(), PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_paper_list, container, false);
        appSingleTone = new AppSingleTone(getContext());
        sharedPreferences = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        mRecyclerView = rootView.findViewById(R.id.paperList);
        Constant.ADD_Q_IN_PAPER = false;
        Constant.DELETE_Q_IN_PAPER = false;
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        if (!RootActivity.hasPermissionToCreate)
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.IS_PAPER = true;
                startActivity(new Intent(getContext(), NewPaperActivity.class));
                getActivity().overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });
        getPaperList();
        return rootView;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    void parseHomeworkList(JSONObject jsonObject){
        try {
            allClassData.clear();
            Log.e("", jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("paper_list");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                NoticeData notice = new NoticeData();
                notice.setId(object.getString("id"));
                notice.setSubjectName(object.getString("subject_name"));
                notice.setTitle(object.getString("title"));
                notice.totalMarks = object.getString("total_marks");
                notice.examTimeHr =object.getString("exam_time_hr");
                notice.examDate =object.getString("exam_date");
                notice.setCreatedBy(object.getString("added_by"));
                notice.setClassName(object.getString("class_name"));
                notice.classID = object.getString("class_id");
                notice.subjectId = object.getString("subject_id");
                allClassData.add(notice);
            }

            PaperListAd adapter1 = new PaperListAd(getContext(), allClassData);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(adapter1);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void getPaperList() {

        try {
            String url = null;
            if (userSession.isTeacher()&&!userSession.getBoolean("isAdmin"))
                url = appSingleTone.paperListForTeacher;
            else if(userSession.getBoolean("isAdmin"))
                url = appSingleTone.paperListForAdmin;
            ExecuteAPI executeAPI = new ExecuteAPI(getContext(), url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));

            if (userSession.isTeacher())
                executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
            else {
                executeAPI.addPostParam("student_id", userSession.getAttribute("user_id"));
                executeAPI.addPostParam("class_id", userSession.getAttribute("class_id"));
                executeAPI.addPostParam("division_id", userSession.getAttribute("division_id"));
            }
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    parseHomeworkList(result);
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

