package com.nikvay.drnitingroup.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.adapter.MenuAdapter;
import com.nikvay.drnitingroup.adapter.SliderAdapter;
import com.nikvay.drnitingroup.modales.HomeMenu;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class DashboardFragment extends Fragment {

    ArrayList<HomeMenu> allClassData;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    RecyclerView mRecyclerView;
    StaggeredGridLayoutManager mStaggeredLayoutManager;
    ArrayList<NoticeData> sliderUrls;

    ViewPager viewPager;
    TabLayout indicator;
    AppSingleTone appSingleTone;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSingleTone = new AppSingleTone(getContext());
        sharedPreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        allClassData = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        viewPager = rootView.findViewById(R.id.viewPager);
        indicator = rootView.findViewById(R.id.indicator);
        mRecyclerView = rootView.findViewById(R.id.details_list);
        mRecyclerView.setHasFixedSize(true);
        getBanners();
        parseMenuList();
        return rootView;
    }

    public void parseMenuList() {

        HomeMenu menu = new HomeMenu();
        if (userSession.isStudent())
            menu.setName("Attendance");
        else
            menu.setName("Take Attendance");
        menu.setResourceId(R.drawable.attendace);
        menu.setId("attendance");
        menu.setMenuId(2);
        allClassData.add(menu);

        HomeMenu menu1 = new HomeMenu();
        menu1.setName("Notices");
        menu1.setMenuId(3);
        menu1.setResourceId(R.drawable.notice);
        menu1.setId("notice");
        allClassData.add(menu1);


        HomeMenu menu5 = new HomeMenu();
        menu5.setName("Online Test");
        menu5.setResourceId(R.drawable.checklist);
        menu5.setMenuId(4);
        menu5.setId("online_test");
        allClassData.add(menu5);


        if (userSession.isStudent()){
            HomeMenu menu6 = new HomeMenu();
            menu6.setName("Video Lecture");
            menu6.setMenuId(5);
            menu6.setId("videos");
            menu6.setResourceId(R.drawable.menu_video);
            allClassData.add(menu6);

            HomeMenu menu2 = new HomeMenu();
            menu2.setName("Homework");
            menu2.setMenuId(6);
            menu2.setResourceId(R.drawable.menu_homework);
            menu2.setId("homework");
            allClassData.add(menu2);
            HomeMenu menu4 = new HomeMenu();
            menu4.setName("Timetable");
            menu4.setId("timetable");
            menu4.setMenuId(7);
            menu4.setResourceId(R.drawable.menu_timetable);
            allClassData.add(menu4);
        }else {
            HomeMenu menu2 = new HomeMenu();
            menu2.setName("Homework");
            menu2.setMenuId(5);
            menu2.setResourceId(R.drawable.menu_homework);
            menu2.setId("homework");
            allClassData.add(menu2);
            HomeMenu menu4 = new HomeMenu();
            menu4.setName("Timetable");
            menu4.setId("timetable");
            menu4.setMenuId(6);
            menu4.setResourceId(R.drawable.menu_timetable);
            allClassData.add(menu4);
        }



        if (userSession.isTeacher()){
            HomeMenu menu3 = new HomeMenu();
            menu3.setMenuId(7);
            menu3.setName("Generate Paper");
            menu3.setResourceId(R.drawable.menu_paper);
            menu3.setId("paper");
            allClassData.add(menu3);
        }


        mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        MenuAdapter mAdapter = new MenuAdapter(getContext(), allClassData, userSession);
        mRecyclerView.setAdapter(mAdapter);
    }
    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            if(getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() < sliderUrls.size() - 1) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        }
    }

    void parseBanners(JSONObject jsonObject){
        try {
            sliderUrls = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("slider_list");
            if(jsonArray.length() == 0){
                viewPager.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                indicator.setVisibility(View.GONE);
            }
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                NoticeData notice = new NoticeData();
                notice.setId(object.getString("id"));
                notice.setDescription(object.getString("image"));
                notice.setMediaUrl(object.getString("redirect_url"));
                sliderUrls.add(notice);
            }
            viewPager.setAdapter(new SliderAdapter(getContext(), sliderUrls));
            indicator.setupWithViewPager(viewPager, true);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void  getBanners() {

        try {
            String url = null;
            url = appSingleTone.getBanners;
            ExecuteAPI executeAPI = new ExecuteAPI(getContext(), url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    parseBanners(result);
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

