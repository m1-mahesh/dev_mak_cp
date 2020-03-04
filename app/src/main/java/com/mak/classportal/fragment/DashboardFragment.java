package com.mak.classportal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mak.classportal.R;
import com.mak.classportal.adapter.MenuAdapter;
import com.mak.classportal.adapter.SliderAdapter;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.utilities.UserSession;

import java.util.ArrayList;
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
    List<String> sliderUrls;

    ViewPager viewPager;
    TabLayout indicator;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        initializeSlider();
        parseMenuList();
        return rootView;
    }
    public void initializeSlider(){
        sliderUrls = new ArrayList<>();
        sliderUrls.add("https://thumbs.dreamstime.com/z/back-to-school-sale-banner-offer-back-to-school-sale-leaves-colored-hand-drawn-doodle-icons-education-symbols-business-banners-123250117.jpg");
        sliderUrls.add("https://c8.alamy.com/comp/PJDXPN/big-sale-banner-vector-school-children-pupil-template-for-advertising-discount-tag-special-offer-banner-isolated-illustration-PJDXPN.jpg");
        sliderUrls.add("https://st4.depositphotos.com/1001941/19880/i/1600/depositphotos_198809040-stock-photo-back-school-super-sale-header.jpg");
        viewPager.setAdapter(new SliderAdapter(getContext(), sliderUrls));
        indicator.setupWithViewPager(viewPager, true);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
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

        HomeMenu menu2 = new HomeMenu();
        menu2.setName("Homework");
        menu2.setMenuId(7);
        menu2.setResourceId(R.drawable.menu_homework);
        menu2.setId("homework");
        allClassData.add(menu2);

        HomeMenu menu4 = new HomeMenu();
        menu4.setName("Timetable");
        menu4.setId("timetable");
        menu4.setMenuId(8);
        menu4.setResourceId(R.drawable.menu_timetable);
        allClassData.add(menu4);

        HomeMenu menu5 = new HomeMenu();
        menu5.setName("Online Test");
        menu5.setResourceId(R.drawable.checklist);
        menu5.setMenuId(5);
        menu5.setId("online_test");
        allClassData.add(menu5);

        HomeMenu menu6 = new HomeMenu();
        menu6.setName("Video Lecture");
        menu6.setMenuId(4);
        menu6.setId("videos");
        menu6.setResourceId(R.drawable.menu_video);
        if (userSession.isStudent())
            allClassData.add(menu6);

        HomeMenu menu3 = new HomeMenu();
        menu3.setMenuId(9);
        menu3.setName("Generate Paper");
        menu3.setResourceId(R.drawable.menu_paper);
        menu3.setId("paper");
        if (userSession.isTeacher())
            allClassData.add(menu3);

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

}

