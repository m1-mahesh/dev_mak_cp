package com.nikvay.drnitingroup.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.adapter.ClassListAdapter;
import com.nikvay.drnitingroup.modales.StudentClass;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ClassFragment extends Fragment {

    public static String menuId = "";
    protected ImageView mImageView;
    protected int res;
    AppSingleTone appSingleTone;
    ArrayList<StudentClass> allClassData;
    private View containerView;
    private Bitmap bitmap;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    RecyclerView classList;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSingleTone = new AppSingleTone(getContext());
        sharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        allClassData = new ArrayList<>();
        getClassDivision();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_classes, container, false);
        classList = rootView.findViewById(R.id.classList);

        classList.setHasFixedSize(true);

        return rootView;
    }
    void prepareClassData(JSONArray apiResponse){
        try {
            allClassData.clear();

            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                StudentClass aClass = new StudentClass();
                aClass.setId(classObj.getString("class_id"));
                aClass.setName(classObj.getString("class_name"));
                for (int j =0; j< classObj.getJSONArray("division_list").length(); j++) {
                    JSONObject obj = classObj.getJSONArray("division_list").getJSONObject(j);
                    aClass.addDivision(obj.getString("division_id"), obj.getString("division_name"));
                }
                allClassData.add(aClass);
            }
            ClassListAdapter adapter1 = new ClassListAdapter(getContext(), allClassData);
            ClassListAdapter.menuId = menuId;
            classList.setLayoutManager(new GridLayoutManager(getContext(), 2));
            classList.setAdapter(adapter1);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getClassDivision() {

        try {
            String url = appSingleTone.classDivisionList;

            ExecuteAPI executeAPI = new ExecuteAPI(getContext(), url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.has("class_list")) {
                            JSONArray object = result.getJSONArray("class_list");
                            prepareClassData(object);
                        } else {
                            //showToast("Something went wrong, Please try again later");
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

