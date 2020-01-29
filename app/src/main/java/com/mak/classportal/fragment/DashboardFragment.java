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

        for (int i = 0; i < 5; i++) {
            HomeMenu menu = new HomeMenu();
            menu.setName("Menu" + i);
            allClassData.add(menu);
        }

        mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        MenuAdapter mAdapter = new MenuAdapter(getContext(), allClassData);
        mRecyclerView.setAdapter(mAdapter);
    }


}

