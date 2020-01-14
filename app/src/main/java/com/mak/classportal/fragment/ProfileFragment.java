package com.mak.classportal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mak.classportal.R;
import com.mak.classportal.RootActivity;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ProfileFragment extends Fragment {


    LinearLayout personalinfo, experience, review;
    TextView personalinfobtn, experiencebtn, reviewbtn;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_main, container, false);
        TextView userType = rootView.findViewById(R.id.userType);
        LinearLayout scoreView = rootView.findViewById(R.id.scoreView);
        LinearLayout teacherView = rootView.findViewById(R.id.qualificationView);
        if (RootActivity.isTeacher) {
            scoreView.setVisibility(View.GONE);
            teacherView.setVisibility(View.VISIBLE);
            userType.setText("Teacher");
        }
        else if (RootActivity.isStudent) {
            teacherView.setVisibility(View.GONE);
            scoreView.setVisibility(View.VISIBLE);
            userType.setText("Student");
        }


        return rootView;
    }


}

