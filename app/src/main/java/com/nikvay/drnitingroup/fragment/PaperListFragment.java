package com.nikvay.drnitingroup.fragment;

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
import com.nikvay.drnitingroup.BoardSelectActivity;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.RootActivity;
import com.nikvay.drnitingroup.adapter.PaperListAd;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.permission.PermissionsActivity;
import com.nikvay.drnitingroup.permission.PermissionsChecker;
import com.nikvay.drnitingroup.utilities.Constant;

import java.util.ArrayList;

import static com.nikvay.drnitingroup.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.nikvay.drnitingroup.permission.PermissionsChecker.REQUIRED_PERMISSION;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class PaperListFragment extends Fragment {


    PermissionsChecker checker;
    RecyclerView mRecyclerView;
    ArrayList<NoticeData> allClassData = new ArrayList<>();

    public static PaperListFragment newInstance(String menuID) {
        PaperListFragment contentFragment = new PaperListFragment();
        return contentFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checker = new PermissionsChecker(getContext());
        if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
            PermissionsActivity.startActivityForResult(getActivity(), PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_paper_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.paperList);
        PaperListAd adapter1 = new PaperListAd(getContext(), allClassData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter1);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        if (!RootActivity.hasPermissionToCreate)
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.IS_PAPER = true;
                startActivity(new Intent(getContext(), BoardSelectActivity.class));
                getActivity().overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });
        return rootView;
    }



}

