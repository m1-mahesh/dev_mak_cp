package com.mak.classportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mak.classportal.modales.HomeMenu;

import java.util.ArrayList;

public class NewTestActivity extends AppCompatActivity {

    public static boolean isTest = false;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    ArrayList<HomeMenu> allClassData = new ArrayList<>();
    AppCompatSpinner selectClass, selectSubject, selectDiv, selectLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tests);
        if (isTest)
            ((TextView) findViewById(R.id.tvTitle)).setText(R.string.new_test);
        else
            ((TextView) findViewById(R.id.tvTitle)).setText(R.string.new_paper);
        ((Button) findViewById(R.id.saveButton)).setText("Next");

        selectClass = findViewById(R.id.selectClass);
        selectDiv = findViewById(R.id.selectDiv);
        selectSubject = findViewById(R.id.selectSubject);
        selectLevel = findViewById(R.id.selectLevel);
        if (isTest){
            selectLevel.setVisibility(View.GONE);
        }else {
            selectDiv.setVisibility(View.GONE);
        }
    }
    void showToast(String toastText){
        inflater = getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(getApplicationContext());
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(this, R.font.opensansregular));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(tostLayout);
        toast.show();
    }
    public void next(View view){
        SelectQuestionsActivity.CLASS_ID = "";
        SelectQuestionsActivity.SUBJECT_ID = "";
        SelectQuestionsActivity.INDEX = 0;
        startActivity(new Intent(this, SelectQuestionsActivity.class));
        overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
        finish();
    }
}
