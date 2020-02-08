package com.mak.classportal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.mak.classportal.R;
import com.mak.classportal.adapter.MenuAdapter;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.utilities.UserSession;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class DashboardFragment extends Fragment {

    ArrayList<HomeMenu> allClassData;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    RecyclerView mRecyclerView;
    StaggeredGridLayoutManager mStaggeredLayoutManager;
    private View containerView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
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
        mRecyclerView = rootView.findViewById(R.id.details_list);

        mRecyclerView.setHasFixedSize(true);
        parseMenuList();
        return rootView;
    }

    void parseMenuList() {

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


}

