package com.mak.classportal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.UserSession;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class TestResultFragment extends Fragment {


    RecyclerView mRecyclerView;
    ArrayList<TestData> allClassData = new ArrayList<>();

    SharedPreferences sharedPreferences;
    UserSession userSession;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());


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
        ScheduledTestsAdapter adapter1 = new ScheduledTestsAdapter(getContext(), allClassData, true, userSession, Constant.TAB_INDEX_2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(adapter1);

        return rootView;
    }


}

