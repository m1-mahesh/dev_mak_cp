package com.mak.classportal.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mak.classportal.NewTestActivity;
import com.mak.classportal.R;
import com.mak.classportal.RootActivity;
import com.mak.classportal.TestsList;
import com.mak.classportal.adapter.ScheduledTestsAdapter;
import com.mak.classportal.modales.TestData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by @vitovalov on 30/9/15.
 */
public class ActiveTestsTabFragment extends Fragment {

    private ListAdapter mAdapter;
    ArrayList<TestData> allClassData = new ArrayList<>();
    private String mItemData = "Lorem Ipsum is simply dummy text of the printing and "
            + "typesetting industry Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_test_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(
                R.id.activeTests);

        getTestList();
        ScheduledTestsAdapter adapter1 = new ScheduledTestsAdapter(getContext(), allClassData, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter1);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        if (!RootActivity.hasPermissionToCreate)
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                NewTestActivity.isTest = true;
                getActivity().startActivity(new Intent(getContext(), NewTestActivity.class));
                getActivity().overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });

        return view;
    }
    void getTestList(){
        for (int i=0;i<5;i++){
            TestData testData =new TestData();
            testData.setTestClass(i+" std");
            testData.setTestDate("12-Dec-2019");
            testData.setTestStatus("Pending");
            allClassData.add(testData);
        }
    }
}
