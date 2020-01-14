package com.mak.classportal.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mak.classportal.R;
import com.mak.classportal.RootActivity;
import com.mak.classportal.modales.NoticeData;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class TimeTableFragment extends Fragment {


    ArrayList<NoticeData> allClassData = new ArrayList<>();

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
        View rootView = inflater.inflate(R.layout.activity_timetable, container, false);


        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        if (!RootActivity.hasPermissionToCreate)
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return rootView;
    }


}

