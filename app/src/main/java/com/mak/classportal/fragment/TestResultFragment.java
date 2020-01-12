package com.mak.classportal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.adapter.ScheduledTestsAdapter;
import com.mak.classportal.modales.TestData;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class TestResultFragment extends Fragment {


    RecyclerView mRecyclerView;
    ArrayList<TestData> allClassData = new ArrayList<>();

    public static TestResultFragment newInstance(String menuID) {
        TestResultFragment contentFragment = new TestResultFragment();
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

    void getTestList(){
        for (int i=0;i<5;i++){
            TestData testData =new TestData();
            testData.setTestClass(i+" std");
            testData.setTestDate("12-Dec-2019");
            testData.setTestStatus("Pending");
            allClassData.add(testData);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.result_one_step, container, false);

        mRecyclerView = rootView.findViewById(R.id.scheduledTest);
        getTestList();
        ScheduledTestsAdapter adapter1 = new ScheduledTestsAdapter(getContext(), allClassData, true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter1);

        return rootView;
    }


}

