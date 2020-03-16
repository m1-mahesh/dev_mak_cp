package com.nikvay.drnitingroup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nikvay.drnitingroup.R;

public class NoticeStepOneFragment extends Fragment implements View.OnClickListener  {

    ImageButton allClass, oneClass;
    public static int selectedOption = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_new_notice2, container,
                false);
        allClass = rootView.findViewById(R.id.allC);
        oneClass = rootView.findViewById(R.id.singleC);

        oneClass.setOnClickListener(this);
        allClass.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.allC:
                allClass.setBackgroundResource(R.drawable.selcted_border);
                oneClass.setBackgroundResource(R.drawable.layout_border);
                selectedOption = 0;
                break;
            case R.id.singleC:
                allClass.setBackgroundResource(R.drawable.layout_border);
                oneClass.setBackgroundResource(R.drawable.selcted_border);
                selectedOption = 1;
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
