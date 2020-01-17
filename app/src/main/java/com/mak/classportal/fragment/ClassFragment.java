package com.mak.classportal.fragment;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.LoginActivity;
import com.mak.classportal.R;
import com.mak.classportal.RootActivity;
import com.mak.classportal.adapter.ClassListAdapter;
import com.mak.classportal.modales.StudentClass;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

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
        getClassList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_classes, container, false);
        classList = rootView.findViewById(R.id.classList);

        classList.setHasFixedSize(true);

        return rootView;
    }
    void parseClassList(JSONObject jsonObject){
        try {
            allClassData.clear();
            Log.e("", jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("class_list");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                StudentClass aClass = new StudentClass();
                aClass.setName(object.getString("class_name"));
                aClass.setId(object.getString("class_id"));
                allClassData.add(aClass);
            }
            ClassListAdapter adapter1 = new ClassListAdapter(getContext(), allClassData);
            if (menuId.equals(Constant.TAKE_TEST) || menuId.equals(Constant.CASE)) {
                ClassListAdapter.menuId = menuId;
                classList.setLayoutManager(new GridLayoutManager(getContext(), 2));

            } else {
                ClassListAdapter.menuId = menuId;
                classList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            }
            classList.setAdapter(adapter1);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getClassList() {


        try {
            String url = appSingleTone.classList;
            ExecuteAPI executeAPI = new ExecuteAPI(getContext(), url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            Log.e("Org id", ""+userSession.getInt("org_id"));
            executeAPI.addPostParam("org_id", ""+userSession.getInt("org_id"));
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    parseClassList(result);

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

