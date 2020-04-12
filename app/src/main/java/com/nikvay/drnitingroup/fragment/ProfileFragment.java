package com.nikvay.drnitingroup.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.RootActivity;
import com.nikvay.drnitingroup.utilities.UserSession;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ProfileFragment extends Fragment {


    LinearLayout personalinfo, experience, review;
    TextView nameTxt, roleTxt, specificationTxt, mobileTxt, emailTxt, addressTxt, marksTxt, testCountTxt;
    UserSession userSession;
    SharedPreferences sharedPreferences;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_main, container, false);
        TextView userType = rootView.findViewById(R.id.userType);
        LinearLayout scoreView = rootView.findViewById(R.id.scoreView);
        LinearLayout teacherView = rootView.findViewById(R.id.qualificationView);
        nameTxt = rootView.findViewById(R.id.nameTxt);
        roleTxt = rootView.findViewById(R.id.userType);
        specificationTxt = rootView.findViewById(R.id.specificationTxt);
        marksTxt = rootView.findViewById(R.id.marksTxt);
        testCountTxt = rootView.findViewById(R.id.testCountTxt);
        mobileTxt = rootView.findViewById(R.id.mobileTxt);
        emailTxt = rootView.findViewById(R.id.emailTxt);
        addressTxt = rootView.findViewById(R.id.addressTxt);
        if (RootActivity.isTeacher) {
            scoreView.setVisibility(View.GONE);
//            teacherView.setVisibility(View.VISIBLE);
            userType.setText("Teacher");
            nameTxt.setText(userSession.getAttribute("name"));
            emailTxt.setText(userSession.getAttribute("email"));
            mobileTxt.setText(userSession.getAttribute("mobile"));
        }
        else if (RootActivity.isStudent) {
            teacherView.setVisibility(View.GONE);
//            scoreView.setVisibility(View.VISIBLE);
            userType.setText("Student");
            nameTxt.setText(userSession.getAttribute("name"));
            emailTxt.setText(userSession.getAttribute("email"));
            mobileTxt.setText(userSession.getAttribute("mobile"));
        }


        return rootView;
    }


}

