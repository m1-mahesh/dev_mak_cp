package com.mak.classportal.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mak.classportal.NewHomeworkActivity;
import com.mak.classportal.R;
import com.mak.classportal.RootActivity;
import com.mak.classportal.adapter.HomeworkAd;
import com.mak.classportal.modales.NoticeData;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class HomeworkFragment extends Fragment {


    RecyclerView mRecyclerView;
    ArrayList<NoticeData> allClassData = new ArrayList<>();

    public static HomeworkFragment newInstance(String menuID) {
        HomeworkFragment contentFragment = new HomeworkFragment();
        return contentFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    void getTestList() {
        for (int i = 8; i < 12; i++) {
            NoticeData notice = new NoticeData();
            notice.setName(i + "st");
            allClassData.add(notice);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_homework_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.homeworkList);
        getTestList();
        HomeworkAd adapter1 = new HomeworkAd(getContext(), allClassData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter1);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        if (!RootActivity.hasPermissionToCreate)
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewHomeworkActivity.class));
                getActivity().overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });

        return rootView;
    }


}

